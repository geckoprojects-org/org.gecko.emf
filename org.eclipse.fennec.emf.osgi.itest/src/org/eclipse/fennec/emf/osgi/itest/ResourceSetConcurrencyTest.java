/**
 * Copyright (c) 2012 - 2022 Data In Motion and others.
 * All rights reserved. 
 *  
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 *  
 * Contributors:
 *       Data In Motion - initial API and implementation
 */
package org.eclipse.fennec.emf.osgi.itest;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicBoolean;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.fennec.emf.osgi.Detachable;
import org.eclipse.fennec.emf.osgi.ResourceSetCache;
import org.eclipse.fennec.emf.osgi.example.model.basic.BasicPackage;
import org.eclipse.fennec.emf.osgi.example.model.basic.GenderType;
import org.eclipse.fennec.emf.osgi.example.model.basic.Person;
import org.eclipse.fennec.emf.osgi.example.model.basic.util.BasicResourceFactoryImpl;
import org.eclipse.fennec.emf.osgi.resourceset.SynchronizedResourceSetImpl;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.osgi.framework.BundleContext;
import org.osgi.framework.FrameworkUtil;
import org.osgi.framework.InvalidSyntaxException;
import org.osgi.test.common.annotation.InjectService;
import org.osgi.test.junit5.context.BundleContextExtension;
import org.osgi.test.junit5.service.ServiceExtension;

/**
 * Integration test for the {@link ResourceSetCache}
 * 
 * @author Mark Hoffmann
 * @since 25.07.2017
 */
@ExtendWith(BundleContextExtension.class)
@ExtendWith(ServiceExtension.class)
public class ResourceSetConcurrencyTest {

	private final BundleContext context = FrameworkUtil.getBundle(ResourceSetConcurrencyTest.class).getBundleContext();

	/**
	 * Tests how concurrency fails with the default implementation
	 * 
	 * TODO This does not seem to fail every time, which makes the tests a bit flacky
	 * 
	 * @throws IOException
	 * @throws URISyntaxException
	 * @throws InvalidSyntaxException
	 * @throws InterruptedException
	 */
	@Test
	@Disabled
	public void testResourceSetConcurrencyOld(
			@InjectService(cardinality = 1) BasicPackage basicPackage
			) throws IOException, URISyntaxException, InterruptedException {
		// default resource set implementation
		ResourceSet rs = new ResourceSetImpl();

		rs.getPackageRegistry().put(BasicPackage.eNS_URI, basicPackage);
		rs.getResourceFactoryRegistry().getExtensionToFactoryMap().put("basic", new BasicResourceFactoryImpl());
		URI personUri = createUri("mark.basic");
		Resource r = rs.createResource(personUri);
		r.load(null);
		assertFalse(r.getContents().isEmpty());
		Person p = (Person) r.getContents().get(0);
		assertEquals("Mark", p.getFirstName());
		assertEquals("Hoffmann", p.getLastName());
		assertEquals(GenderType.MALE, p.getGender());
		// make the given transactions per thread, in one thread reading in the other
		// writing nearly simultaneously
		int COUNT = 500;
		// wait the given milliseconds between the transactions
		long TIMEOUT = 3l;
		// run the test set the given times
		int TEST_SET_COUNT = 5;
		// count the returned test sets with error
		int errorCount = 0;
		for (int i = 0; i < TEST_SET_COUNT; i++) {
			boolean error = doConcurrencyTestOld(p, COUNT, TIMEOUT, rs);
			if (error) {
				errorCount++;
			}
			System.out.println("Test number: " + i + " with " + COUNT + " transactions and timeout of " + TIMEOUT
					+ "ms results in an error: " + error);
		}
		System.out.println("Error count was " + errorCount + " / " + TEST_SET_COUNT);
		assertTrue(errorCount > 0);
	}

	/**
	 * Tests, how concurrency works with the new implementation
	 * 
	 * @throws IOException
	 * @throws URISyntaxException
	 * @throws InvalidSyntaxException
	 * @throws InterruptedException
	 */
	@Test
	public void testResourceSetConcurrencyNew(
			@InjectService(cardinality = 1) BasicPackage basicPackage
			) throws IOException, URISyntaxException, InterruptedException {
		// user our resource set
		ResourceSet rs = new SynchronizedResourceSetImpl();

		rs.getPackageRegistry().put(BasicPackage.eNS_URI, basicPackage);
		rs.getResourceFactoryRegistry().getExtensionToFactoryMap().put("basic", new BasicResourceFactoryImpl());
		URI personUri = createUri("mark.basic");
		Resource r = rs.createResource(personUri);
		r.load(null);
		assertFalse(r.getContents().isEmpty());
		Person p = (Person) r.getContents().get(0);
		assertEquals("Mark", p.getFirstName());
		assertEquals("Hoffmann", p.getLastName());
		assertEquals(GenderType.MALE, p.getGender());
		// make the given transactions per thread, in one thread reading in the other
		// writing nearly simultaneously
		int COUNT = 500;
		// wait the given milliseconds between the transactions
		long TIMEOUT = 3l;
		// run the test set the given times
		int TEST_SET_COUNT = 5;
		// count the returned test sets with error
		int errorCount = 0;
		for (int i = 0; i < TEST_SET_COUNT; i++) {
			boolean error = doConcurrencyTestNew(p, COUNT, TIMEOUT, rs);
			if (error) {
				errorCount++;
			}
			System.out.println("Test number: " + i + " with " + COUNT + " transactions and timeout of " + TIMEOUT
					+ "ms results in an error: " + error);
		}
		System.out.println("Error count was " + errorCount + " / " + TEST_SET_COUNT);
		assertEquals(0, errorCount);
	}

	/**
	 * @param person
	 * @param COUNT
	 * @param TIMEOUT
	 * @param resourceSet
	 * @throws InterruptedException
	 */
	private boolean doConcurrencyTestOld(Person person, int COUNT, long TIMEOUT, ResourceSet resourceSet)
			throws InterruptedException {
		AtomicBoolean error = new AtomicBoolean(false);
		CountDownLatch latch = new CountDownLatch(1);
		Thread t1 = new Thread(new Runnable() {
			public void run() {
				try {
					latch.await();
					URI configUri = createUri("mark.basic");
					for (int i = 0; i < COUNT; i++) {
						assertNotNull(configUri);
						Resource r = resourceSet.createResource(configUri);
						r.load(null);
						assertFalse(r.getContents().isEmpty());
						EObject eo = r.getContents().get(0);
						assertNotNull(eo);
						resourceSet.getResources().remove(r);
						r.getContents().clear();
						Thread.sleep(TIMEOUT);
					}
				} catch (Exception ex) {
					error.set(true);
					Thread.currentThread().interrupt();
					System.out.println("Exception in thread 1:");
				}
			}
		}, "Loader Thread");
		Thread t2 = new Thread(new Runnable() {
			public void run() {
				try {
					latch.await();
					for (int i = 0; i < COUNT; i++) {

						URI saveUri = URI.createURI("test-" + 1 + ".basic");
						assertNotNull(saveUri);
						Resource r = resourceSet.getResource(saveUri, false);
						if (r == null) {
							r = resourceSet.createResource(saveUri);
						}
						EObject eo = EcoreUtil.copy(person);
						r.getContents().add(eo);
						ByteArrayOutputStream baos = new ByteArrayOutputStream();
						r.save(baos, null);
						baos.close();
						baos.reset();
						resourceSet.getResources().remove(r);
						r.getContents().clear();
						Thread.sleep(TIMEOUT);
					}
				} catch (Exception e) {
					System.out.println("Exception in thread 2:");
					Thread.currentThread().interrupt();
					error.set(true);
				}
			}
		}, "Saver Thread");
		long start = System.nanoTime();
		t1.start();
		t2.start();
		latch.countDown();
		t1.join();
		t2.join();
		long duration = System.nanoTime() - start;
		System.out.println("Executing pure the thread-unsafe EMF way " + COUNT
				+ " transactions in each of two thread with a wait timeout of " + TIMEOUT + "ms took "
				+ (duration / 1000000) + "ms");
		System.out.println("That means an average processing time of "
				+ (((duration / COUNT) - TIMEOUT * 1000000) / 1000) + " microsecond / transaction");
		return error.get();
	}

	/**
	 * @param person
	 * @param COUNT
	 * @param TIMEOUT
	 * @param resourceSet
	 * @throws InterruptedException
	 */
	private boolean doConcurrencyTestNew(Person person, int COUNT, long TIMEOUT, ResourceSet resourceSet)
			throws InterruptedException {
		AtomicBoolean error = new AtomicBoolean(false);
		CountDownLatch latch = new CountDownLatch(1);
		Thread t1 = new Thread(new Runnable() {
			public void run() {
				try {
					latch.await();
					URI configUri = createUri("mark.basic");
					for (int i = 0; i < COUNT; i++) {
						assertNotNull(configUri);
						Resource r = resourceSet.createResource(configUri);
						r.load(null);
						assertFalse(r.getContents().isEmpty());
						EObject eo = r.getContents().get(0);
						assertNotNull(eo);
						// use the thread safe way of detaching EObjects
						((Detachable) resourceSet).detachFromAll(eo);
						Thread.sleep(TIMEOUT);
					}
				} catch (Exception ex) {
					error.set(true);
					Thread.currentThread().interrupt();
					System.out.println("Exception in thread 1:");
				}
			}
		}, "New Loader Thread");
		Thread t2 = new Thread(new Runnable() {
			public void run() {
				try {
					latch.await();
					for (int i = 0; i < COUNT; i++) {

						URI saveUri = URI.createURI("test-" + 1 + ".basic");
						assertNotNull(saveUri);
						Resource r = resourceSet.getResource(saveUri, false);
						if (r == null) {
							r = resourceSet.createResource(saveUri);
						}
						EObject eo = EcoreUtil.copy(person);
						r.getContents().add(eo);
						ByteArrayOutputStream baos = new ByteArrayOutputStream();
						r.save(baos, null);
						baos.close();
						baos.reset();
						((Detachable) resourceSet).detachFromAll(eo);
						Thread.sleep(TIMEOUT);
					}
				} catch (Exception e) {
					System.out.println("Exception in thread 2:");
					Thread.currentThread().interrupt();
					error.set(true);
				}
			}
		}, "New Saver Thread");
		long start = System.nanoTime();
		t1.start();
		t2.start();
		latch.countDown();
		t1.join();
		t2.join();
		long duration = System.nanoTime() - start;
		System.out.println("Executing pure EMF thread-safe way " + COUNT
				+ " transactions in each of two thread with a wait timeout of " + TIMEOUT + "ms took "
				+ (duration / 1000000) + "ms");
		System.out.println("That means an average processing time of "
				+ (((duration / COUNT) - TIMEOUT * 1000000) / 1000) + " microsecond / transaction");
		return error.get();
	}

	/**
	 * Creates a uri for the given file name, as long the file is in the class path
	 * 
	 * @param fileName the name of the file
	 * @return the uri
	 * @throws URISyntaxException
	 */
	private URI createUri(String fileName) throws URISyntaxException {
		URL url = context.getBundle().getResource("data/");
		java.net.URI uri = url.toURI();
		String segmentsPath = uri.getPath();
		if (segmentsPath.startsWith("/")) {
			segmentsPath = segmentsPath.substring(1);
		}
		segmentsPath += fileName;
		String[] segments = segmentsPath.split("/");
		return URI.createHierarchicalURI(uri.getScheme(), uri.getAuthority(), null, segments, null, null);
	}

}
