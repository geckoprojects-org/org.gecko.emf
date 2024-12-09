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
package org.eclipse.fennec.emf.osgi.extender.itest;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

import java.util.Collection;
import java.util.Collections;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EFactory;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.fennec.emf.osgi.configurator.EPackageConfigurator;
import org.eclipse.fennec.emf.osgi.constants.EMFNamespaces;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.BundleException;
import org.osgi.framework.InvalidSyntaxException;
import org.osgi.framework.ServiceReference;
import org.osgi.test.common.annotation.InjectBundleContext;
import org.osgi.test.common.annotation.InjectService;
import org.osgi.test.common.service.ServiceAware;
import org.osgi.test.junit5.context.BundleContextExtension;
import org.osgi.test.junit5.service.ServiceExtension;

/**
 * 
 * @author mark
 * @since 14.10.2022
 */
@ExtendWith(ServiceExtension.class)
@ExtendWith(BundleContextExtension.class)
public class EMFModelExtenderRestartTest {
	
	/** EXTENDER_TEST_MODEL_BSN */
	private static final String EXTENDER_TEST_MODEL_BSN = "org.eclipse.fennec.emf.osgi.example.model.extender";
	private BundleContext ctx;

	@BeforeEach
	public void before(@InjectBundleContext BundleContext ctx) {
		this.ctx = ctx;
	}
	
	@AfterEach
	public void after() {
		for (Bundle b : ctx.getBundles()) {
			if (EXTENDER_TEST_MODEL_BSN.equals(b.getSymbolicName())) {
				if (b.getState() != Bundle.ACTIVE) {
					try {
						b.start();
					} catch (BundleException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					try {
						Thread.sleep(1000l);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
						Thread.currentThread().interrupt();
					}
				}
			}
		}
	}
	
	@Test
	public void stopBundleTest(@InjectService(filter = "(" + EMFNamespaces.EMF_MODEL_NAME + "=manual)") ServiceAware<ResourceSet> rsAware, @InjectService(filter = "(" + EMFNamespaces.EMF_MODEL_NAME + "=manual)") ServiceAware<EPackage> ePackageAware) {
		ResourceSet rs = rsAware.getService();
		assertNotNull(rs);
		EPackage ePackageService = ePackageAware.getService();
		assertNotNull(ePackageService);
		EFactory eFactory = rs.getPackageRegistry().getEFactory("http://fennec.eclipse.org/example/model/manual/1.0");
		assertNotNull(eFactory);
		EPackage ePackage = eFactory.getEPackage();
		assertNotNull(ePackage);
		assertEquals(ePackage, ePackageService);
		// Foo class exists
		EClass foo = (EClass) ePackage.getEClassifier("Foo");
		assertNotNull(foo);
		EClass bar = (EClass) ePackage.getEClassifier("Bar");
		assertNull(bar);
		
		Collection<ServiceReference<EPackageConfigurator>> configurators = Collections.emptyList();
		try {
			configurators = ctx.getServiceReferences(EPackageConfigurator.class, "(" + EMFNamespaces.EMF_MODEL_NAME + "=manual)");
		} catch (InvalidSyntaxException e1) {
			fail("Invalid filter");
		}
		assertFalse(configurators.isEmpty());
		assertEquals(1, configurators.size());
		ServiceReference<EPackageConfigurator> manualConfigurator = configurators.iterator().next();
		Bundle origin = manualConfigurator.getBundle();
		assertNotNull(origin);
		assertEquals(EXTENDER_TEST_MODEL_BSN, origin.getSymbolicName());
		try {
			origin.stop();
		} catch (BundleException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		try {
			Thread.sleep(1000l);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			Thread.currentThread().interrupt();
		}
		assertTrue(rsAware.isEmpty());
		assertTrue(ePackageAware.isEmpty());
	}
	
	@Test
	public void restartBundleTest(@InjectService(filter = "(" + EMFNamespaces.EMF_MODEL_NAME + "=manual)") ServiceAware<ResourceSet> rsAware, @InjectService(filter = "(" + EMFNamespaces.EMF_MODEL_NAME + "=manual)") ServiceAware<EPackage> ePackageAware) {
		ResourceSet rs = rsAware.getService();
		assertNotNull(rs);
		EPackage ePackageService = ePackageAware.getService();
		assertNotNull(ePackageService);
		EFactory eFactory = rs.getPackageRegistry().getEFactory("http://fennec.eclipse.org/example/model/manual/1.0");
		assertNotNull(eFactory);
		EPackage ePackage = eFactory.getEPackage();
		assertNotNull(ePackage);
		assertEquals(ePackage, ePackageService);
		// Foo class exists
		EClass foo = (EClass) ePackage.getEClassifier("Foo");
		assertNotNull(foo);
		EClass bar = (EClass) ePackage.getEClassifier("Bar");
		assertNull(bar);
		
		Collection<ServiceReference<EPackageConfigurator>> configurators = Collections.emptyList();
		try {
			configurators = ctx.getServiceReferences(EPackageConfigurator.class, "(" + EMFNamespaces.EMF_MODEL_NAME + "=manual)");
		} catch (InvalidSyntaxException e1) {
			fail("Invalid filter");
		}
		assertEquals(1, configurators.size());
		ServiceReference<EPackageConfigurator> manualConfigurator = configurators.iterator().next();
		Bundle origin = manualConfigurator.getBundle();
		assertNotNull(origin);
		assertEquals(EXTENDER_TEST_MODEL_BSN, origin.getSymbolicName());
		try {
			origin.stop();
		} catch (BundleException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		try {
			Thread.sleep(1000l);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			Thread.currentThread().interrupt();
		}
		assertTrue(rsAware.isEmpty());
		assertTrue(ePackageAware.isEmpty());
		try {
			origin.start();
		} catch (BundleException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		try {
			Thread.sleep(1000l);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			Thread.currentThread().interrupt();
		}
		assertFalse(rsAware.isEmpty());
		assertFalse(ePackageAware.isEmpty());
		try {
			configurators = ctx.getServiceReferences(EPackageConfigurator.class, "(" + EMFNamespaces.EMF_MODEL_NAME + "=manual)");
		} catch (InvalidSyntaxException e1) {
			fail("Invalid filter");
		}
		assertEquals(1, configurators.size());
		manualConfigurator = configurators.iterator().next();
		origin = manualConfigurator.getBundle();
		assertNotNull(origin);
		assertEquals(EXTENDER_TEST_MODEL_BSN, origin.getSymbolicName());
	}
	
}
