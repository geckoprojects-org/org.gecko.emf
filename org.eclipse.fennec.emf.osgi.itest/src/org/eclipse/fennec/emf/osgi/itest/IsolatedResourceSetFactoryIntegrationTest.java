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

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.util.Arrays;
import java.util.Dictionary;
import java.util.Hashtable;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.fennec.emf.osgi.ResourceSetFactory;
import org.eclipse.fennec.emf.osgi.configurator.EPackageConfigurator;
import org.eclipse.fennec.emf.osgi.configurator.ResourceFactoryConfigurator;
import org.eclipse.fennec.emf.osgi.constants.EMFNamespaces;
import org.eclipse.fennec.emf.osgi.example.model.basic.BasicPackage;
import org.eclipse.fennec.emf.osgi.example.model.manual.ManualPackage;
import org.eclipse.fennec.emf.osgi.example.model.manual.configuration.ManualPackageConfigurator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.osgi.framework.BundleContext;
import org.osgi.framework.InvalidSyntaxException;
import org.osgi.framework.ServiceReference;
import org.osgi.framework.ServiceRegistration;
import org.osgi.service.cm.Configuration;
import org.osgi.service.cm.ConfigurationAdmin;
import org.osgi.service.cm.annotations.RequireConfigurationAdmin;
import org.osgi.test.assertj.monitoring.MonitoringAssertion;
import org.osgi.test.common.annotation.InjectBundleContext;
import org.osgi.test.common.annotation.InjectService;
import org.osgi.test.common.annotation.InjectService.AnyService;
import org.osgi.test.common.service.ServiceAware;
import org.osgi.test.junit5.context.BundleContextExtension;
import org.osgi.test.junit5.service.ServiceExtension;

/**
 * Integration test for the {@link ResourceSetFactory}
 * 
 * @author Mark Hoffmann
 * @since 25.07.2017
 */
@ExtendWith(BundleContextExtension.class)
@ExtendWith(ServiceExtension.class)
@RequireConfigurationAdmin
public class IsolatedResourceSetFactoryIntegrationTest {

	@InjectBundleContext
	BundleContext bc;

	@InjectService
	ConfigurationAdmin ca;

	/**
	 * Tests, if the service was set up correctly
	 * 
	 * @throws IOException
	 * @throws InterruptedException
	 * @throws InvalidSyntaxException
	 */
	@Test
	public void testResourceSetFactoryExists(
			@InjectService(filter = "(rsf.name=test)", cardinality = 0) ServiceAware<AnyService> serviceAware)
			throws IOException, InterruptedException, InvalidSyntaxException {

		Dictionary<String, Object> properties = new Hashtable<>();
		properties.put("rsf.name", "test");
		properties.put("rsf.model.target.filter", "(" + EMFNamespaces.EMF_MODEL_NAME + "=*)");
		
		AtomicReference<Configuration> cr = new AtomicReference<>();
		MonitoringAssertion.executeAndObserve(() -> {
			Configuration configuration = ca.getConfiguration(EMFNamespaces.ISOLATED_RESOURCE_SET_FACTORY_CONFIG_NAME,
					"?");
			configuration.update(properties);

			cr.set(configuration);
		}).untilNoMoreServiceEventWithin(1000).assertWithTimeoutThat(1000)
				.hasExactlyOneServiceEventRegisteredWith(ResourceSetFactory.class)
				.hasExactlyOneServiceEventRegisteredWith(Resource.Factory.Registry.class)
				.hasExactlyOneServiceEventRegisteredWith(EPackage.Registry.class);

		cr.get().delete();
	}

	/**
	 * Tests, if the service was set up correctly
	 * 
	 * @throws IOException
	 * @throws InterruptedException
	 * @throws InvalidSyntaxException
	 */
	@Test
	public void testResourceSetExists(
			@InjectService(filter = "(rsf.name=test)", cardinality = 0) ServiceAware<AnyService> serviceAware)
			throws IOException, InterruptedException, InvalidSyntaxException {

		Dictionary<String, Object> properties = new Hashtable<>();
		properties.put("rsf.name", "test");
		properties.put("rsf.model.target.filter", "(" + EMFNamespaces.EMF_MODEL_NAME + "=*)");

		AtomicReference<Configuration> cr = new AtomicReference<>();
		MonitoringAssertion.executeAndObserve(() -> {
			Configuration configuration = ca.getConfiguration(EMFNamespaces.ISOLATED_RESOURCE_SET_FACTORY_CONFIG_NAME,
					"?");
			configuration.update(properties);

			cr.set(configuration);
		}).untilNoMoreServiceEventWithin(1000).assertWithTimeoutThat(1000)
				.hasExactlyOneServiceEventRegisteredWith(ResourceSet.class)
				.hasExactlyOneServiceEventRegisteredWith(Resource.Factory.Registry.class)
				.hasExactlyOneServiceEventRegisteredWith(EPackage.Registry.class);

		cr.get().delete();

	}

	/**
	 * Tests, if the service was set up correctly
	 * 
	 * @throws IOException
	 * @throws InterruptedException
	 * @throws InvalidSyntaxException
	 */
	@Test
	public void testResourceSetFactoryRegister(
			@InjectService(filter = "(rsf.name=manual)", cardinality = 0) ServiceAware<AnyService> anyAware,
			@InjectService(filter = "(rsf.name=manual)", cardinality = 0) ServiceAware<ResourceSet> rsAware,
			@InjectService(filter = "(rsf.name=manual)", cardinality = 0) ServiceAware<ResourceSetFactory> rsfAware)
			throws IOException, InterruptedException, InvalidSyntaxException {

		Dictionary<String, Object> properties = new Hashtable<>();
		properties.put(EMFNamespaces.PROP_RESOURCE_SET_FACTORY_NAME, "manual");
		properties.put(EMFNamespaces.PROP_MODEL_TARGET_FILTER, "(" + EMFNamespaces.EMF_MODEL_NAME + "=manual)");

		AtomicReference<Configuration> cr = new AtomicReference<>();
		MonitoringAssertion.executeAndObserve(() -> {
			Configuration configuration = ca.getConfiguration(EMFNamespaces.ISOLATED_RESOURCE_SET_FACTORY_CONFIG_NAME,
					"?");
			configuration.update(properties);

			cr.set(configuration);
		}).untilNoMoreServiceEventWithin(1000).assertWithTimeoutThat(1000)
				.hasExactlyOneServiceEventRegisteredWith(ResourceSet.class)
				.hasExactlyOneServiceEventRegisteredWith(ResourceSetFactory.class)
				.hasExactlyOneServiceEventRegisteredWith(Resource.Factory.Registry.class)
				.hasExactlyOneServiceEventRegisteredWith(EPackage.Registry.class);


		ServiceReference<?> rsfRef = rsfAware.getServiceReference();
		Object modelNames = rsfRef.getProperty(EMFNamespaces.EMF_MODEL_NAME);
		assertNotNull(modelNames);
		assertTrue(modelNames instanceof String[]);
		List<String> modelNameList = Arrays.asList((String[]) modelNames);
		assertTrue(modelNameList.contains("ecore"));
		assertFalse(modelNameList.contains("manual"));
		assertFalse(modelNameList.contains("manual2"));

		ServiceReference<?> rsRef = rsAware.getServiceReference();
		modelNames = rsRef.getProperty(EMFNamespaces.EMF_MODEL_NAME);
		assertNotNull(modelNames);
		assertTrue(modelNames instanceof String[]);
		modelNameList = Arrays.asList((String[]) modelNames);
		assertTrue(modelNameList.contains("ecore"));
		assertFalse(modelNameList.contains("manual"));
		assertFalse(modelNameList.contains("manual2"));

		Dictionary<String, Object> manualProperties = new Hashtable<String, Object>();
		manualProperties.put(EMFNamespaces.EMF_MODEL_NAME, ManualPackage.eNAME);
		ManualPackageConfigurator manualPackageConfigurator = new ManualPackageConfigurator();

		ServiceRegistration<?> manualRegistration = bc.registerService(
				new String[] { EPackageConfigurator.class.getName(), ResourceFactoryConfigurator.class.getName() },
				manualPackageConfigurator, manualProperties);

		Thread.sleep(4000);
		
		rsfRef = rsAware.getServiceReference();
		modelNames = rsfRef.getProperty(EMFNamespaces.EMF_MODEL_NAME);
		assertNotNull(modelNames);
		assertTrue(modelNames instanceof String[]);
		modelNameList = Arrays.asList((String[]) modelNames);
		assertTrue(modelNameList.contains("ecore"));
		assertTrue(modelNameList.contains("manual"));
		assertFalse(modelNameList.contains("manual2"));

		rsRef = rsfAware.getServiceReference();
		modelNames = rsRef.getProperty(EMFNamespaces.EMF_MODEL_NAME);
		assertNotNull(modelNames);
		assertTrue(modelNames instanceof String[]);
		modelNameList = Arrays.asList((String[]) modelNames);
		assertTrue(modelNameList.contains("ecore"));
		assertTrue(modelNameList.contains("manual"));
		assertFalse(modelNameList.contains("manual2"));

		manualProperties = new Hashtable<String, Object>();
		manualProperties.put(EMFNamespaces.EMF_MODEL_NAME, BasicPackage.eNAME + "2");
		/*
		 * No change expected because target filter does not match
		 */

		ManualPackageConfigurator configurator2 = new ManualPackageConfigurator();
		ServiceRegistration<?> reg2 = bc.registerService(
				new String[] { EPackageConfigurator.class.getName(), ResourceFactoryConfigurator.class.getName() },
				configurator2, manualProperties);

		rsfRef = rsfAware.getServiceReference();
		modelNames = rsfRef.getProperty(EMFNamespaces.EMF_MODEL_NAME);
		assertNotNull(modelNames);
		assertTrue(modelNames instanceof String[]);
		modelNameList = Arrays.asList((String[]) modelNames);
		assertTrue(modelNameList.contains("ecore"));
		assertTrue(modelNameList.contains("manual"));
		assertFalse(modelNameList.contains("manual2"));

		rsRef = rsAware.getServiceReference();
		;
		modelNames = rsRef.getProperty(EMFNamespaces.EMF_MODEL_NAME);
		assertNotNull(modelNames);
		assertTrue(modelNames instanceof String[]);
		modelNameList = Arrays.asList((String[]) modelNames);
		assertTrue(modelNameList.contains("ecore"));
		assertTrue(modelNameList.contains("manual"));
		assertFalse(modelNameList.contains("manual2"));

		manualRegistration.unregister();
		reg2.unregister();

		MonitoringAssertion.executeAndObserve(() -> {
			cr.get().delete();
		}).untilNoMoreServiceEventWithin(1000).assertWithTimeoutThat(1000)
				.hasExactlyOneServiceEventUnregisteringWith(ResourceSet.class)
				.hasExactlyOneServiceEventUnregisteringWith(ResourceSetFactory.class)
				.hasExactlyOneServiceEventUnregisteringWith(Resource.Factory.Registry.class)
				.hasExactlyOneServiceEventUnregisteringWith(EPackage.Registry.class);

	}

}