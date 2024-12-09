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
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.concurrent.Executors;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.eclipse.fennec.emf.osgi.HughDataResourceSet;
import org.eclipse.fennec.emf.osgi.ResourceSetFactory;
import org.eclipse.fennec.emf.osgi.example.model.basic.BasicFactory;
import org.eclipse.fennec.emf.osgi.example.model.basic.BasicPackage;
import org.eclipse.fennec.emf.osgi.example.model.basic.Person;
import org.eclipse.fennec.emf.osgi.example.model.basic.util.BasicResourceFactoryImpl;
import org.eclipse.fennec.emf.osgi.resourceset.HughDataResourceSetImpl;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.osgi.framework.ServiceReference;
import org.osgi.test.common.annotation.InjectService;
import org.osgi.test.common.service.ServiceAware;
import org.osgi.test.junit5.context.BundleContextExtension;
import org.osgi.test.junit5.service.ServiceExtension;
import org.osgi.util.promise.Promise;
import org.osgi.util.promise.PromiseFactory;
import org.osgi.util.promise.TimeoutException;

/**
 * Integration test for the {@link ResourceSetFactory}
 * 
 * @author Mark Hoffmann
 * @since 25.07.2017
 */
@ExtendWith(BundleContextExtension.class)
@ExtendWith(ServiceExtension.class)
@Disabled
public class HughDataResourceSetIntegrationTest {

	@Test
	public void testHughDataResourceSet(
			@InjectService(cardinality = 1) BasicPackage basicPackage
			) throws InterruptedException {
		HughDataResourceSet hdr = new HughDataResourceSetImpl();
		hdr.getPackageRegistry().put(BasicPackage.eNS_URI, basicPackage);
		hdr.getResourceFactoryRegistry().getProtocolToFactoryMap().put("file", new BasicResourceFactoryImpl());

		URI lastUri = null;
		URI firstUri = null;
		// create
		System.out.println("Creating 500000 Persons");
		for (int i = 1; i <= 500000; i++) {
			lastUri = URI.createURI("file://test/" + i);
			Resource resource = hdr.createResource(lastUri);
			Person p = basicPackage.getBasicFactory().createPerson();
			p.setId(Integer.toString(i));
			resource.getContents().add(p);
			lastUri = lastUri.appendFragment(Integer.toString(i));
			if (firstUri == null) {
				firstUri = lastUri;
			}
		}

		long start = System.currentTimeMillis();
		Person result = (Person) hdr.getEObject(firstUri, false);
		assertNotNull(result);
		System.out.println("Checking 1 first uri took: " + (System.currentTimeMillis() - start));

		start = System.currentTimeMillis();
		for (int i = 0; i < 100; i++) {
			result = (Person) hdr.getEObject(lastUri, false);
			assertNotNull(result);
		}
		System.out.println("Checking 1 last uri 100 times took: " + (System.currentTimeMillis() - start));

		assertEquals(500000, hdr.getResources().size());

		start = System.currentTimeMillis();
		hdr.getResources().clear();
		long fastRun = System.currentTimeMillis() - start;
		System.out.println("Fastrun: " + fastRun);
		result = (Person) hdr.getEObject(firstUri, false);
		assertNull(result);
		result = (Person) hdr.getEObject(lastUri, false);
		assertNull(result);

		System.out.println("Clearing resource set with 500.000 entries took " + (System.currentTimeMillis() - start));
		hdr.setUseResourceLocator(false);
		System.out.println("Creating 500000 Persons (second try use locator = false)");

		firstUri = null;
		for (int i = 1; i <= 500000; i++) {
			lastUri = URI.createURI("file://test/" + i);
			Resource resource = hdr.createResource(lastUri);
			Person p = basicPackage.getBasicFactory().createPerson();
			p.setId(Integer.toString(i));
			resource.getContents().add(p);
			lastUri = lastUri.appendFragment(Integer.toString(i));
			if (firstUri == null) {
				firstUri = lastUri;
			}
		}

		start = System.currentTimeMillis();
		result = (Person) hdr.getEObject(firstUri, false);
		assertNotNull(result);
		System.out.println("Checking first uri (second try) took: " + (System.currentTimeMillis() - start));

		start = System.currentTimeMillis();
		for (int i = 0; i < 100; i++) {
			result = (Person) hdr.getEObject(lastUri, false);
		}
		System.out.println("Checking last uri (second try) 100 times took: " + (System.currentTimeMillis() - start));

		start = System.currentTimeMillis();
		PromiseFactory pf = new PromiseFactory(Executors.newCachedThreadPool());
		Promise<Void> promise = pf.submit(() -> {
			hdr.getResources().clear();
			return null;
		});

		System.out.println(promise.timeout(fastRun * 10).getFailure());
		assertTrue(promise.timeout(fastRun * 10).getFailure() instanceof TimeoutException);
		System.out.println(
				"Clearing resource set with 500.000 entries (second try) took " + (System.currentTimeMillis() - start));
	}

	@Test
	public void testWrappedHughDataResourceSet(
			@InjectService(cardinality = 0) ServiceAware<ResourceSetFactory> sa,
			@InjectService(cardinality = 1) BasicPackage basicPackage)
			throws InterruptedException {

		ServiceReference<ResourceSetFactory> reference = sa.getServiceReference();
		assertNotNull(reference);
		ResourceSetFactory factory = sa.getService();
		assertNotNull(factory);
		ResourceSet rs1 = factory.createResourceSet();
		assertNotNull(rs1);
		assertTrue(rs1 instanceof ResourceSetImpl);

		rs1.getPackageRegistry().put(BasicPackage.eNS_URI, basicPackage);

		URI lastUri = null;
		URI firstUri = null;
		// create
		System.out.println("Creating 500000 Persons");
		for (int i = 1; i <= 500000; i++) {
			lastUri = URI.createURI("file://test/" + i);
			Resource resource = rs1.createResource(lastUri);
			Person p = basicPackage.getBasicFactory().createPerson();
			p.setId(Integer.toString(i));
			resource.getContents().add(p);
			lastUri = lastUri.appendFragment(Integer.toString(i));
			if (firstUri == null) {
				firstUri = lastUri;
			}
		}

		long start = System.currentTimeMillis();
		Person result = (Person) rs1.getEObject(firstUri, false);
		assertNotNull(result);
		System.out.println("Checking first uri took: " + (System.currentTimeMillis() - start));

		start = System.currentTimeMillis();
		for (int i = 0; i < 100; i++) {
			result = (Person) rs1.getEObject(lastUri, false);
			assertNotNull(result);
		}
		System.out.println("Checking last uri 100 times took: " + (System.currentTimeMillis() - start));

		assertEquals(500000, rs1.getResources().size());

		start = System.currentTimeMillis();
		rs1.getResources().clear();

		result = (Person) rs1.getEObject(firstUri, false);
		assertNull(result);
		result = (Person) rs1.getEObject(lastUri, false);
		assertNull(result);

		System.out.println("Clearing resource set with 500.000 entries took " + (System.currentTimeMillis() - start));

		System.out.println("Creating 500000 Persons (second try)");
		HughDataResourceSet hdr = new HughDataResourceSetImpl(rs1);
		firstUri = null;
		for (int i = 1; i <= 500000; i++) {
			lastUri = URI.createURI("file://test/" + i);
			Resource resource = hdr.createResource(lastUri);
			Person p = basicPackage.getBasicFactory().createPerson();
			p.setId(Integer.toString(i));
			resource.getContents().add(p);
			lastUri = lastUri.appendFragment(Integer.toString(i));
			if (firstUri == null) {
				firstUri = lastUri;
			}
		}

		start = System.currentTimeMillis();
		result = (Person) hdr.getEObject(firstUri, false);
		assertNotNull(result);
		System.out.println("Checking first uri (second try) took: " + (System.currentTimeMillis() - start));

		start = System.currentTimeMillis();
		for (int i = 0; i < 100; i++) {
			result = (Person) hdr.getEObject(lastUri, false);
		}
		System.out.println("Checking last uri (second try) 100 times took: " + (System.currentTimeMillis() - start));

		start = System.currentTimeMillis();
		hdr.getResources().clear();

		result = (Person) hdr.getEObject(firstUri, false);
		assertNull(result);
		result = (Person) hdr.getEObject(lastUri, false);
		assertNull(result);

		System.out.println(
				"Clearing resource set with 500.000 entries (second try) took " + (System.currentTimeMillis() - start));

	}

	@Test
	public void testDeactivateHughdataResourceSet(
			@InjectService(cardinality = 0) ServiceAware<ResourceSetFactory> sa,
			@InjectService(cardinality = 1) BasicPackage basicPackage,
			@InjectService(cardinality = 1) BasicFactory basicFactory
			)
			throws InterruptedException {

		ServiceReference<ResourceSetFactory> reference = sa.getServiceReference();
		assertNotNull(reference);
		ResourceSetFactory factory = sa.getService();
		assertNotNull(factory);
		ResourceSet rs1 = factory.createResourceSet();
		assertNotNull(rs1);
		assertTrue(rs1 instanceof ResourceSetImpl);

		rs1.getPackageRegistry().put(BasicPackage.eNS_URI, basicPackage);

		System.out.println("Creating 500000 Persons");
		HughDataResourceSet hdr = new HughDataResourceSetImpl(rs1);
		URI lastUri = null;
		URI firstUri = null;
		for (int i = 1; i <= 500000; i++) {
			lastUri = URI.createURI("file://test/" + i);
			Resource resource = hdr.createResource(lastUri);
			Person p = basicFactory.createPerson();
			p.setId(Integer.toString(i));
			resource.getContents().add(p);
			lastUri = lastUri.appendFragment(Integer.toString(i));
			if (firstUri == null) {
				firstUri = lastUri;
			}
		}

		long start = System.currentTimeMillis();
		Person result = (Person) hdr.getEObject(firstUri, false);
		assertNotNull(result);
		System.out.println("Checking first uri took: " + (System.currentTimeMillis() - start));

		start = System.currentTimeMillis();
		for (int i = 0; i < 100; i++) {
			result = (Person) hdr.getEObject(lastUri, false);
		}
		System.out.println("Checking last uri 100 times took: " + (System.currentTimeMillis() - start));

		start = System.currentTimeMillis();
		hdr.getResources().clear();
		long fastRun = System.currentTimeMillis() - start;

		result = (Person) hdr.getEObject(firstUri, false);
		assertNull(result);
		result = (Person) hdr.getEObject(lastUri, false);
		assertNull(result);

		System.out.println(
				"Clearing resource set with 500.000 entries (second try) took " + (System.currentTimeMillis() - start));

		hdr.setUseResourceLocator(false);
		System.out.println("Creating 500000 Persons (second try use locator = false)");
		firstUri = null;
		for (int i = 1; i <= 500000; i++) {
			lastUri = URI.createURI("file://test/" + i);
			Resource resource = hdr.createResource(lastUri);
			Person p = basicFactory.createPerson();
			p.setId(Integer.toString(i));
			resource.getContents().add(p);
			lastUri = lastUri.appendFragment(Integer.toString(i));
			if (firstUri == null) {
				firstUri = lastUri;
			}
		}

		start = System.currentTimeMillis();
		result = (Person) hdr.getEObject(firstUri, false);
		assertNotNull(result);
		System.out.println("Checking first uri (second try) took: " + (System.currentTimeMillis() - start));

		start = System.currentTimeMillis();
		for (int i = 0; i < 100; i++) {
			result = (Person) hdr.getEObject(lastUri, false);
		}
		System.out.println("Checking last uri (second try) 100 times took: " + (System.currentTimeMillis() - start));

		PromiseFactory pf = new PromiseFactory(Executors.newCachedThreadPool());
		Promise<Void> promise = pf.submit(() -> {
			hdr.getResources().clear();
			return null;
		});
		assertTrue(promise.timeout(fastRun * 10).getFailure() instanceof TimeoutException);

		System.out.println(
				"Clearing resource set with 500.000 entries (second try) took " + (System.currentTimeMillis() - start));
	}

}