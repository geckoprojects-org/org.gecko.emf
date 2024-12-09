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

import static org.eclipse.fennec.emf.osgi.constants.EMFNamespaces.EMF_CONFIGURATOR_NAME;
import static org.eclipse.fennec.emf.osgi.constants.EMFNamespaces.EMF_MODEL_NAME;
import static org.eclipse.fennec.emf.osgi.constants.EMFNamespaces.EMF_MODEL_NSURI;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.Dictionary;
import java.util.Hashtable;
import java.util.List;

import org.assertj.core.api.Assertions;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EcorePackage;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.Resource.IOWrappedException;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.fennec.emf.osgi.ResourceSetFactory;
import org.eclipse.fennec.emf.osgi.configurator.EPackageConfigurator;
import org.eclipse.fennec.emf.osgi.configurator.ResourceFactoryConfigurator;
import org.eclipse.fennec.emf.osgi.configurator.ResourceSetConfigurator;
import org.eclipse.fennec.emf.osgi.constants.EMFNamespaces;
import org.eclipse.fennec.emf.osgi.example.model.manual.Foo;
import org.eclipse.fennec.emf.osgi.example.model.manual.ManualFactory;
import org.eclipse.fennec.emf.osgi.example.model.manual.ManualPackage;
import org.eclipse.fennec.emf.osgi.example.model.manual.configuration.ManualPackageConfigurator;
import org.eclipse.fennec.emf.osgi.itest.configurator.TestConfigurator;
import org.eclipse.fennec.emf.osgi.itest.configurator.TestResourceSetConfiguration;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.osgi.framework.BundleContext;
import org.osgi.framework.InvalidSyntaxException;
import org.osgi.framework.ServiceReference;
import org.osgi.framework.ServiceRegistration;
import org.osgi.test.assertj.monitoring.MonitoringAssertion;
import org.osgi.test.common.annotation.InjectBundleContext;
import org.osgi.test.common.annotation.InjectService;
import org.osgi.test.common.service.ServiceAware;
import org.osgi.test.junit5.context.BundleContextExtension;
import org.osgi.test.junit5.service.ServiceExtension;

/**
 * Tests the EMF OSGi integration
 * 
 * @author Mark Hoffmann
 * @since 25.07.2017
 */
@ExtendWith(BundleContextExtension.class)
@ExtendWith(ServiceExtension.class)
public class EMFOSGiIntegrationTest {

	@InjectBundleContext
	BundleContext bc;

	/**
	 * Trying to load an instance with a non registered {@link EPackage}
	 * 
	 * @throws IOException
	 */
	@Test
	public void testLoadResourceFailNoEPackage_Factory(@InjectService ServiceAware<ResourceSetFactory> sa)
			throws IOException {

		ServiceReference<ResourceSetFactory> reference = sa.getServiceReference();
		assertNotNull(reference);

		Object modelNames = reference.getProperty(EMF_MODEL_NAME);
		assertNotNull(modelNames);
		assertTrue(modelNames instanceof String[]);
		List<String> modelNameList = Arrays.asList((String[]) modelNames);
		assertTrue(modelNameList.contains("ecore"));
		assertFalse(modelNameList.contains("manual"));

		ResourceSetFactory factory = sa.getService();
		assertNotNull(factory);
		ResourceSet rs = factory.createResourceSet();
		assertNotNull(rs);
		URI uri = URI.createURI("foo.manual");
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		Resource testSaveResource = rs.createResource(uri);
		assertNotNull(testSaveResource);
		Foo p = ManualFactory.eINSTANCE.createFoo();
		p.setValue("Emil");
		testSaveResource.getContents().add(p);
		testSaveResource.save(baos, null);

		byte[] content = baos.toByteArray();
		ByteArrayInputStream bais = new ByteArrayInputStream(content);

		Resource testLoadResource = rs.createResource(uri);
		Assertions.assertThatThrownBy(() -> {

			testLoadResource.load(bais, null);
		}).isInstanceOf(IOWrappedException.class);
	}

	/**
	 * Trying to load an instance with a non registered {@link EPackage}
	 * 
	 * @throws IOException
	 */
	@Test
	public void testLoadResourceFailNoEPackage(@InjectService ServiceAware<ResourceSet> sa) throws IOException {

		ServiceReference<ResourceSet> reference = sa.getServiceReference();
		assertNotNull(reference);
		Object modelNames = reference.getProperty(EMF_MODEL_NAME);
		assertNotNull(modelNames);
		assertTrue(modelNames instanceof String[]);
		List<String> modelNameList = Arrays.asList((String[]) modelNames);
		assertTrue(modelNameList.contains("ecore"));
		assertFalse(modelNameList.contains("manual"));
		Object configNames = reference.getProperty(EMF_CONFIGURATOR_NAME);
		assertNotNull(configNames);
		assertEquals(4, ((String[]) configNames).length);

		ResourceSet rs = sa.getService();
		assertNotNull(rs);
		URI uri = URI.createURI("foox.manual");
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		Resource testSaveResource = rs.createResource(uri);
		assertNotNull(testSaveResource);
		Foo p = ManualFactory.eINSTANCE.createFoo();
		p.setValue("Emil");
		testSaveResource.getContents().add(p);
		testSaveResource.save(baos, null);

		byte[] content = baos.toByteArray();
		ByteArrayInputStream bais = new ByteArrayInputStream(content);

		Resource testLoadResource = rs.createResource(uri);

		Assertions.assertThatThrownBy(() -> {

			testLoadResource.load(bais, null);
		}).isInstanceOf(IOWrappedException.class);

	}

	/**
	 * Trying to load an instance with a registered {@link EPackage}
	 * 
	 * @throws IOException
	 * @throws InvalidSyntaxException
	 */
	@Test
	public void testLoadResourceRegisteredEPackage_Factory(@InjectService ServiceAware<ResourceSetFactory> sa)
			throws IOException, InvalidSyntaxException {

		bc.registerService(
				new String[] { EPackageConfigurator.class.getName(), ResourceFactoryConfigurator.class.getName() },
				new ManualPackageConfigurator(), new Hashtable<String, Object>());

		ServiceReference<ResourceSetFactory> reference = sa.getServiceReference();
		Object modelNames = reference.getProperty(EMF_MODEL_NAME);
		assertNotNull(modelNames);
		assertTrue(modelNames instanceof String[]);
		List<String> modelNameList = Arrays.asList((String[]) modelNames);
		assertTrue(modelNameList.contains("ecore"));
		assertFalse(modelNameList.contains("manual"));
		Object configNames = reference.getProperty(EMF_CONFIGURATOR_NAME);
		assertNotNull(configNames);
		assertEquals(4, ((String[]) configNames).length);

		assertNotNull(reference);
		ResourceSetFactory factory = sa.getService();
		assertNotNull(factory);
		ResourceSet rs = factory.createResourceSet();
		assertNotNull(rs);
		URI uri = URI.createURI("fooxx.manual");
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		Resource testSaveResource = rs.createResource(uri);
		assertNotNull(testSaveResource);
		Foo p = ManualFactory.eINSTANCE.createFoo();
		p.setValue("Emil");
		testSaveResource.getContents().add(p);
		testSaveResource.save(baos, null);

		byte[] content = baos.toByteArray();
		ByteArrayInputStream bais = new ByteArrayInputStream(content);

		Resource testLoadResource = rs.createResource(uri);
		assertEquals(0, testLoadResource.getContents().size());
		testLoadResource.load(bais, null);
		assertEquals(1, testLoadResource.getContents().size());
		Foo result = (Foo) testLoadResource.getContents().get(0);
		assertNotNull(result);
		assertEquals("Emil", result.getValue());
	}

	/**
	 * Trying to load an instance with a registered {@link EPackage}
	 * 
	 * @throws IOException
	 * @throws InvalidSyntaxException
	 */
	@Test
	public void testLoadResourceRegisteredEPackage_FactoryWithModelName(
			@InjectService(filter = "(" + EMF_MODEL_NAME + "=manual)", cardinality = 0) ServiceAware<ResourceSetFactory> serviceAwareRSF)
			throws IOException, InvalidSyntaxException {

		Dictionary<String, Object> properties = new Hashtable<String, Object>();
		properties.put(EMF_MODEL_NAME, "manual");

		MonitoringAssertion.executeAndObserve(() -> {
			bc.registerService(
					new String[] { EPackageConfigurator.class.getName(), ResourceFactoryConfigurator.class.getName() },
					new ManualPackageConfigurator(), properties);

		}).untilNoMoreServiceEventWithin(100).assertWithTimeoutThat(1000)
				.hasExactlyOneServiceEventRegisteredWith(EPackageConfigurator.class)
				.hasExactlyOneServiceEventRegisteredWith(ResourceFactoryConfigurator.class);

		ServiceReference<ResourceSetFactory> reference = serviceAwareRSF.getServiceReference();
		Object modelNames = reference.getProperty(EMF_MODEL_NAME);
		assertNotNull(modelNames);
		assertTrue(modelNames instanceof String[]);
		List<String> modelNameList = Arrays.asList((String[]) modelNames);
		assertTrue(modelNameList.contains("ecore"));
		assertTrue(modelNameList.contains("manual"));
		Object configNames = reference.getProperty(EMF_CONFIGURATOR_NAME);
		assertNotNull(configNames);
		assertEquals(4, ((String[]) configNames).length);

		assertNotNull(reference);
		ResourceSetFactory factory = serviceAwareRSF.getService();
		assertNotNull(factory);
		ResourceSet rs = factory.createResourceSet();
		assertNotNull(rs);
		URI uri = URI.createURI("fooo.manual");
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		Resource testSaveResource = rs.createResource(uri);
		assertNotNull(testSaveResource);
		Foo p = ManualFactory.eINSTANCE.createFoo();
		p.setValue("Emil");
		testSaveResource.getContents().add(p);
		testSaveResource.save(baos, null);

		byte[] content = baos.toByteArray();
		ByteArrayInputStream bais = new ByteArrayInputStream(content);

		Resource testLoadResource = rs.createResource(uri);
		assertEquals(0, testLoadResource.getContents().size());
		testLoadResource.load(bais, null);
		assertEquals(1, testLoadResource.getContents().size());
		Foo result = (Foo) testLoadResource.getContents().get(0);
		assertNotNull(result);
		assertEquals("Emil", result.getValue());
	}

	/**
	 * Trying to load an instance with a registered {@link EPackage}
	 * 
	 * @throws IOException
	 * @throws InvalidSyntaxException
	 */
	@Test
	public void testLoadResourceRegisteredConfigurator_FactoryWithModelName(
			@InjectService(filter = "(" + EMF_CONFIGURATOR_NAME + "=myConfig)", cardinality = 0) ServiceAware<ResourceSetFactory> serviceAwareRSF)
			throws IOException, InvalidSyntaxException {

		MonitoringAssertion.executeAndObserve(() -> {
			Dictionary<String, Object> properties = new Hashtable<String, Object>();
			properties.put(EMF_MODEL_NAME, "manual");
			bc.registerService(
					new String[] { EPackageConfigurator.class.getName(), ResourceFactoryConfigurator.class.getName() },
					new ManualPackageConfigurator(), properties);

		}).untilNoMoreServiceEventWithin(100).assertWithTimeoutThat(1000)
				.hasExactlyOneServiceEventRegisteredWith(EPackageConfigurator.class)
				.hasExactlyOneServiceEventRegisteredWith(ResourceFactoryConfigurator.class);

		Dictionary<String, Object> properties = new Hashtable<String, Object>();
		properties.put(EMF_CONFIGURATOR_NAME, "myConfig");
		bc.registerService(new String[] { ResourceSetConfigurator.class.getName() }, new TestResourceSetConfiguration(),
				properties);

		ServiceReference<ResourceSetFactory> reference = serviceAwareRSF.getServiceReference();
		Object modelNames = reference.getProperty(EMF_MODEL_NAME);
		assertNotNull(modelNames);
		assertTrue(modelNames instanceof String[]);
		List<String> modelNameList = Arrays.asList((String[]) modelNames);
		assertTrue(modelNameList.contains("ecore"));
		assertTrue(modelNameList.contains("manual"));
		Object configNames = reference.getProperty(EMF_CONFIGURATOR_NAME);
		assertNotNull(configNames);
		List<String> configNameList = Arrays.asList((String[]) configNames);
		assertTrue(configNameList.contains("myConfig"));

		assertNotNull(reference);
		ResourceSetFactory factory = serviceAwareRSF.getService();
		assertNotNull(factory);
		ResourceSet rs = factory.createResourceSet();
		assertNotNull(rs);
		URI uri = URI.createURI("fff.manual");
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		Resource testSaveResource = rs.createResource(uri);
		assertNotNull(testSaveResource);
		Foo p = ManualFactory.eINSTANCE.createFoo();
		p.setValue("Emil");
		testSaveResource.getContents().add(p);
		testSaveResource.save(baos, null);

		byte[] content = baos.toByteArray();
		ByteArrayInputStream bais = new ByteArrayInputStream(content);

		Resource testLoadResource = rs.createResource(uri);
		assertEquals(0, testLoadResource.getContents().size());
		testLoadResource.load(bais, null);
		assertEquals(1, testLoadResource.getContents().size());
		Foo result = (Foo) testLoadResource.getContents().get(0);
		assertNotNull(result);
		assertEquals("Emil", result.getValue());
	}
	
	/**
	 * Trying to load an instance with a registered {@link EPackage}
	 * 
	 * @throws IOException
	 * @throws InvalidSyntaxException
	 */
	@Test
	public void testLoadResourceRegisteredConfigurator_FactoryWithFeatures(
			@InjectService(filter = "(" + EMF_CONFIGURATOR_NAME + "=myConfig)", cardinality = 0) ServiceAware<ResourceSetFactory> serviceAwareRSF)
					throws IOException, InvalidSyntaxException {
		
		MonitoringAssertion.executeAndObserve(() -> {
			Dictionary<String, Object> properties = new Hashtable<String, Object>();
			properties.put(EMF_MODEL_NAME, "manual");
			properties.put(EMFNamespaces.EMF_MODEL_FEATURE, "myManualFeature");
			properties.put(EMFNamespaces.EMF_MODEL_FEATURE + ".foo", "bar");
			properties.put(EMFNamespaces.EMF_MODEL_FEATURE + ".fizz", "manualBuzz");
			bc.registerService(
					new String[] { EPackageConfigurator.class.getName(), ResourceFactoryConfigurator.class.getName() },
					new ManualPackageConfigurator(), properties);
			
		}).untilNoMoreServiceEventWithin(100).assertWithTimeoutThat(1000)
		.hasExactlyOneServiceEventRegisteredWith(EPackageConfigurator.class)
		.hasExactlyOneServiceEventRegisteredWith(ResourceFactoryConfigurator.class);
		
		Dictionary<String, Object> properties = new Hashtable<String, Object>();
		properties.put(EMF_CONFIGURATOR_NAME, "myConfig");
		properties.put(EMFNamespaces.EMF_MODEL_FEATURE, "myTestFeature");
		properties.put(EMFNamespaces.EMF_MODEL_FEATURE + ".fizz", "testBuzz");
		bc.registerService(new String[] { ResourceSetConfigurator.class.getName() }, new TestResourceSetConfiguration(),
				properties);
		
		ServiceReference<ResourceSetFactory> reference = serviceAwareRSF.getServiceReference();
		assertNotNull(reference);
		ResourceSetFactory factory = serviceAwareRSF.getService();
		assertNotNull(factory);
		
		Object modelNames = reference.getProperty(EMF_MODEL_NAME);
		assertNotNull(modelNames);
		assertTrue(modelNames instanceof String[]);
		List<String> modelNameList = Arrays.asList((String[]) modelNames);
		assertTrue(modelNameList.contains("ecore"));
		assertTrue(modelNameList.contains("manual"));
		Object configNames = reference.getProperty(EMF_CONFIGURATOR_NAME);
		assertNotNull(configNames);
		List<String> configNameList = Arrays.asList((String[]) configNames);
		assertTrue(configNameList.contains("myConfig"));
		Object featureNames = reference.getProperty(EMFNamespaces.EMF_MODEL_FEATURE);
		List<String> featureList = Arrays.asList((String[]) featureNames);
		assertEquals(2, featureList.size());
		assertTrue(featureList.contains("myTestFeature"));
		assertTrue(featureList.contains("myManualFeature"));
		
		assertEquals("bar", reference.getProperty("foo"));
		Object fizzes = reference.getProperty("fizz");
		assertNotNull(fizzes);
		assertTrue(fizzes.getClass().isArray());
		List<Object> fizzList = Arrays.asList((Object[]) fizzes);
		assertTrue(fizzList.contains("testBuzz"));
		assertTrue(fizzList.contains("manualBuzz"));
		
	}

	/**
	 * Trying to load an instance with a registered {@link EPackage}
	 * 
	 * @throws IOException
	 * @throws InvalidSyntaxException
	 */
	@Test
	public void testLoadResourceRegisteredConfiguratorManyConfigName_FactoryWithModelName(
			@InjectService(filter = "(&(" + EMF_CONFIGURATOR_NAME + "=myConfig)(" + EMF_CONFIGURATOR_NAME + "=mySecConfig)(" + EMF_CONFIGURATOR_NAME + "=myThirdConfig))", cardinality = 0) ServiceAware<ResourceSetFactory> serviceAwareRSF)
			throws IOException, InvalidSyntaxException {

		Dictionary<String, Object> properties = new Hashtable<String, Object>();
		properties.put(EMF_MODEL_NAME, "manual");

		bc.registerService(
				new String[] { EPackageConfigurator.class.getName(), ResourceFactoryConfigurator.class.getName() },
				new ManualPackageConfigurator(), properties);

		properties = new Hashtable<String, Object>();
		String[] configs = new String[] { "myConfig", "mySecConfig" };
		properties.put(EMF_CONFIGURATOR_NAME, configs);

		bc.registerService(new String[] { ResourceSetConfigurator.class.getName() }, new TestResourceSetConfiguration(),
				properties);

		properties = new Hashtable<String, Object>();
		properties.put(EMF_CONFIGURATOR_NAME, "myThirdConfig");
		bc.registerService(new String[] { ResourceSetConfigurator.class.getName() }, new TestResourceSetConfiguration(),
				properties);

		ServiceReference<ResourceSetFactory> reference = serviceAwareRSF.getServiceReference();
		Object modelNames = reference.getProperty(EMF_MODEL_NAME);
		assertNotNull(modelNames);
		assertTrue(modelNames instanceof String[]);
		List<String> modelNameList = Arrays.asList((String[]) modelNames);
		assertTrue(modelNameList.contains("ecore"));
		assertTrue(modelNameList.contains("manual"));
		Object configNames = reference.getProperty(EMF_CONFIGURATOR_NAME);
		assertNotNull(configNames);
		List<String> configNameList = Arrays.asList((String[]) configNames);
		assertTrue(configNameList.contains("myConfig"));
		assertTrue(configNameList.contains("mySecConfig"));
		assertTrue(configNameList.contains("myThirdConfig"));

		assertNotNull(reference);
		ResourceSetFactory factory = serviceAwareRSF.getService();
		assertNotNull(factory);
		ResourceSet rs = factory.createResourceSet();
		assertNotNull(rs);
		URI uri = URI.createURI("fffg.manual");
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		Resource testSaveResource = rs.createResource(uri);
		assertNotNull(testSaveResource);
		Foo p = ManualFactory.eINSTANCE.createFoo();
		p.setValue("Emil");
		testSaveResource.getContents().add(p);
		testSaveResource.save(baos, null);

		byte[] content = baos.toByteArray();
		ByteArrayInputStream bais = new ByteArrayInputStream(content);

		Resource testLoadResource = rs.createResource(uri);
		assertEquals(0, testLoadResource.getContents().size());
		testLoadResource.load(bais, null);
		assertEquals(1, testLoadResource.getContents().size());
		Foo result = (Foo) testLoadResource.getContents().get(0);
		assertNotNull(result);
		assertEquals("Emil", result.getValue());
	}

	/**
	 * Trying to load an instance with a registered {@link EPackage}
	 * 
	 * @throws IOException
	 * @throws InvalidSyntaxException
	 */
	@Test
	public void testLoadResourceRegisteredManyChangeConfigName_FactoryWithModelName(
			@InjectService(filter = "(&(" + EMF_CONFIGURATOR_NAME + "=myConfig)(" + EMF_CONFIGURATOR_NAME + "=mySecConfig)(" + EMF_CONFIGURATOR_NAME + "=myThirdConfig))", cardinality = 0) ServiceAware<ResourceSetFactory> serviceAwareRSF)
			throws IOException, InvalidSyntaxException {

		Dictionary<String, Object> properties = new Hashtable<String, Object>();
		properties.put(EMF_MODEL_NAME, "manual");
		EPackageConfigurator testConfig = new ManualPackageConfigurator();
		ServiceRegistration<?> reg1 = bc.registerService(
				new String[] { EPackageConfigurator.class.getName(), ResourceFactoryConfigurator.class.getName() },
				testConfig, properties);

		properties = new Hashtable<String, Object>();
		String[] configs = new String[] { "myConfig", "mySecConfig" };
		properties.put(EMF_CONFIGURATOR_NAME, configs);
		ResourceSetConfigurator rsConfig = new TestResourceSetConfiguration();
		ServiceRegistration<?> reg2 = bc.registerService(new String[] { ResourceSetConfigurator.class.getName() },
				rsConfig, properties);

		properties = new Hashtable<String, Object>();
		properties.put(EMF_CONFIGURATOR_NAME, "myThirdConfig");
		bc.registerService(new String[] { ResourceSetConfigurator.class.getName() }, new TestResourceSetConfiguration(),
				properties);

		ServiceReference<ResourceSetFactory> reference = serviceAwareRSF.getServiceReference();
		Object modelNames = reference.getProperty(EMF_MODEL_NAME);
		assertNotNull(modelNames);
		assertTrue(modelNames instanceof String[]);
		List<String> modelNameList = Arrays.asList((String[]) modelNames);
		assertTrue(modelNameList.contains("ecore"));
		assertTrue(modelNameList.contains("manual"));
		Object configNames = reference.getProperty(EMF_CONFIGURATOR_NAME);
		assertNotNull(configNames);
		List<String> configNameList = Arrays.asList((String[]) configNames);
		assertTrue(configNameList.contains("myConfig"));
		assertTrue(configNameList.contains("mySecConfig"));
		assertTrue(configNameList.contains("myThirdConfig"));

		assertNotNull(reference);
		ResourceSetFactory factory = serviceAwareRSF.getService();
		assertNotNull(factory);
		ResourceSet rs = factory.createResourceSet();
		assertNotNull(rs);
		URI uri = URI.createURI("fofo.manual");
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		Resource testSaveResource = rs.createResource(uri);
		assertNotNull(testSaveResource);
		Foo p = ManualFactory.eINSTANCE.createFoo();
		p.setValue("Emil");
		testSaveResource.getContents().add(p);
		testSaveResource.save(baos, null);

		byte[] content = baos.toByteArray();
		ByteArrayInputStream bais = new ByteArrayInputStream(content);

		Resource testLoadResource = rs.createResource(uri);
		assertEquals(0, testLoadResource.getContents().size());
		testLoadResource.load(bais, null);
		assertEquals(1, testLoadResource.getContents().size());
		Foo result = (Foo) testLoadResource.getContents().get(0);
		assertNotNull(result);
		assertEquals("Emil", result.getValue());

		properties = new Hashtable<String, Object>();
		properties.put(EMF_MODEL_NAME, "manual2");
		reg1.setProperties(properties);

		reference = serviceAwareRSF.getServiceReference();
		modelNames = reference.getProperty(EMF_MODEL_NAME);
		assertNotNull(modelNames);
		assertTrue(modelNames instanceof String[]);
		modelNameList = Arrays.asList((String[]) modelNames);
		assertTrue(modelNameList.contains("ecore"));
		assertTrue(modelNameList.contains("manual2"));
		configNames = reference.getProperty(EMF_CONFIGURATOR_NAME);
		assertNotNull(configNames);
		configNameList = Arrays.asList((String[]) configNames);
		assertTrue(configNameList.contains("myConfig"));
		assertTrue(configNameList.contains("mySecConfig"));
		assertTrue(configNameList.contains("myThirdConfig"));

		configs = new String[] { "myConfig", "mySecConfig", "hallo" };
		properties.put(EMF_CONFIGURATOR_NAME, configs);
		reg2.setProperties(properties);

		reference = serviceAwareRSF.getServiceReference();
		modelNames = reference.getProperty(EMF_MODEL_NAME);
		assertNotNull(modelNames);
		assertTrue(modelNames instanceof String[]);
		modelNameList = Arrays.asList((String[]) modelNames);
		assertTrue(modelNameList.contains("ecore"));
		assertTrue(modelNameList.contains("manual2"));
		configNames = reference.getProperty(EMF_CONFIGURATOR_NAME);
		assertNotNull(configNames);
		configNameList = Arrays.asList((String[]) configNames);
		assertTrue(configNameList.contains("myConfig"));
		assertTrue(configNameList.contains("mySecConfig"));
		assertTrue(configNameList.contains("myThirdConfig"));
		assertTrue(configNameList.contains("hallo"));
	}

	/**
	 * Trying to load an instance with a registered {@link EPackage}
	 * 
	 * @throws IOException
	 */
	@Test
	public void testLoadResourceRegisteredEPackage(
			@InjectService(cardinality = 0) ServiceAware<ResourceSet> serviceAwareRS) throws IOException {

		bc.registerService(
				new String[] { EPackageConfigurator.class.getName(), ResourceFactoryConfigurator.class.getName() },
				new ManualPackageConfigurator(), new Hashtable<String, Object>());

		ServiceReference<ResourceSet> reference = serviceAwareRS.getServiceReference();
		assertNotNull(reference);
		ResourceSet rs = serviceAwareRS.getService();
		assertNotNull(rs);
		URI uri = URI.createURI("person.manual");
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		Resource testSaveResource = rs.createResource(uri);
		assertNotNull(testSaveResource);
		Foo p = ManualFactory.eINSTANCE.createFoo();
		p.setValue("Emil");
		testSaveResource.getContents().add(p);
		testSaveResource.save(baos, null);

		byte[] content = baos.toByteArray();
		ByteArrayInputStream bais = new ByteArrayInputStream(content);

		Resource testLoadResource = rs.createResource(uri);
		assertEquals(0, testLoadResource.getContents().size());
		testLoadResource.load(bais, null);
		assertEquals(1, testLoadResource.getContents().size());
		Foo result = (Foo) testLoadResource.getContents().get(0);
		assertNotNull(result);
		assertEquals("Emil", result.getValue());

	}

	/**
	 * Trying to load an instance with a registered {@link EPackage}
	 * 
	 * @throws IOException
	 */
	@Test
	public void testLoadResourceRegisteredEPackageResourceSet_Factory(
			@InjectService(cardinality = 0) ServiceAware<ResourceSetFactory> serviceAwareRSF) throws IOException {
		bc.registerService(
				new String[] { EPackageConfigurator.class.getName(), ResourceFactoryConfigurator.class.getName() },
				new ManualPackageConfigurator(), new Hashtable<String, Object>());

		ServiceReference<ResourceSetFactory> reference = serviceAwareRSF.getServiceReference();
		assertNotNull(reference);
		ResourceSetFactory factory = serviceAwareRSF.getService();
		assertNotNull(factory);
		ResourceSet rs = factory.createResourceSet();
		URI uri = URI.createURI("person.manual");
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		Resource testSaveResource = rs.createResource(uri);
		assertNotNull(testSaveResource);
		Foo p = ManualFactory.eINSTANCE.createFoo();

		p.setValue("Emil");
		testSaveResource.getContents().add(p);
		testSaveResource.save(baos, null);

		byte[] content = baos.toByteArray();
		ByteArrayInputStream bais = new ByteArrayInputStream(content);

		Resource testLoadResource = rs.createResource(uri);
		assertEquals(0, testLoadResource.getContents().size());
		testLoadResource.load(bais, null);
		assertEquals(1, testLoadResource.getContents().size());
		Foo result = (Foo) testLoadResource.getContents().get(0);
		assertNotNull(result);
		assertEquals("Emil", result.getValue());

	}

	/**
	 * Trying to load an instance with a registered {@link EPackage}
	 * 
	 * @throws IOException
	 */
	@Test
	public void testLoadResourceRegisteredEPackageResourceSet(
			@InjectService(cardinality = 0) ServiceAware<ResourceSet> serviceAwareRS) throws IOException {

		bc.registerService(
				new String[] { EPackageConfigurator.class.getName(), ResourceFactoryConfigurator.class.getName() },
				new ManualPackageConfigurator(), new Hashtable<String, Object>());

		ServiceReference<ResourceSet> reference = serviceAwareRS.getServiceReference();
		assertNotNull(reference);
		ResourceSet rs = serviceAwareRS.getService();
		assertNotNull(rs);
		URI uri = URI.createURI("foo.manual");
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		Resource testSaveResource = rs.createResource(uri);
		assertNotNull(testSaveResource);
		Foo p = ManualFactory.eINSTANCE.createFoo();
		p.setValue("Emil");
		testSaveResource.getContents().add(p);
		testSaveResource.save(baos, null);

		byte[] content = baos.toByteArray();
		ByteArrayInputStream bais = new ByteArrayInputStream(content);

		Resource testLoadResource = rs.createResource(uri);
		assertEquals(0, testLoadResource.getContents().size());
		testLoadResource.load(bais, null);
		assertEquals(1, testLoadResource.getContents().size());
		Foo result = (Foo) testLoadResource.getContents().get(0);
		assertNotNull(result);
		assertEquals("Emil", result.getValue());

	}

	/**
	 * Trying to load an instance with a registered {@link EPackage} than unload the
	 * EPackage and have an exception
	 * 
	 * @throws IOException
	 * @throws InterruptedException
	 */
	@Test
	public void testLoadResourceRegisteredEPackageAndUnregister_Factory(
			@InjectService(cardinality = 0) ServiceAware<ResourceSetFactory> serviceAwareRSF)
			throws IOException, InterruptedException {

		ManualPackageConfigurator configurator = new ManualPackageConfigurator();

		ServiceRegistration<?> reg = bc.registerService(
				new String[] { EPackageConfigurator.class.getName(), ResourceFactoryConfigurator.class.getName() },
				configurator, new Hashtable<String, Object>());

		ServiceReference<ResourceSetFactory> reference = serviceAwareRSF.getServiceReference();
		assertNotNull(reference);
		ResourceSetFactory factory = serviceAwareRSF.getService();
		assertNotNull(factory);
		ResourceSet rs = factory.createResourceSet();
		assertNotNull(rs);
		URI uri = URI.createURI("foo.manual");
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		Resource testSaveResource = rs.createResource(uri);
		assertNotNull(testSaveResource);
		Foo p = ManualFactory.eINSTANCE.createFoo();
		p.setValue("Emil");

		testSaveResource.getContents().add(p);
		testSaveResource.save(baos, null);

		byte[] content = baos.toByteArray();
		ByteArrayInputStream bais = new ByteArrayInputStream(content);

		Resource testLoadResource = rs.createResource(uri);
		assertEquals(0, testLoadResource.getContents().size());
		testLoadResource.load(bais, null);
		assertEquals(1, testLoadResource.getContents().size());
		Foo result = (Foo) testLoadResource.getContents().get(0);
		assertNotNull(result);
		assertEquals("Emil", result.getValue());

		reg.unregister();

		Resource testLoadResource2 = rs.createResource(uri);
		assertNotEquals(testLoadResource, testLoadResource2);
		assertEquals(0, testLoadResource2.getContents().size());

		Assertions.assertThatThrownBy(() -> {

			testLoadResource2.load(bais, null);
		}).isInstanceOf(IOWrappedException.class);
	}

	/**
	 * Trying to load an instance with a registered {@link EPackage} than unload the
	 * EPackage and have an exception
	 * 
	 * @throws IOException
	 * @throws InterruptedException
	 */
	@Test
	public void testLoadResourceRegisteredEPackageAndUnregister(
			@InjectService(cardinality = 0) ServiceAware<ResourceSet> serviceAwareRS)
			throws IOException, InterruptedException {
		ManualPackageConfigurator configurator = new ManualPackageConfigurator();
		Dictionary<String, Object> props = new Hashtable<String, Object>();
		props.put(EMF_MODEL_NAME, ManualPackage.eNAME);
		props.put(EMF_MODEL_NSURI, ManualPackage.eNS_URI);

		ServiceRegistration<?> reg = bc.registerService(
				new String[] { EPackageConfigurator.class.getName(), ResourceFactoryConfigurator.class.getName() },
				configurator, props);

		ServiceReference<ResourceSet> reference = serviceAwareRS.getServiceReference();
		assertNotNull(reference);
		ResourceSet rs = serviceAwareRS.getService();
		assertNotNull(rs);

		URI uri = URI.createURI("foo.manual");
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		Resource testSaveResource = rs.createResource(uri);
		assertNotNull(testSaveResource);
		Foo p = ManualFactory.eINSTANCE.createFoo();
		p.setValue("Emil");

		testSaveResource.getContents().add(p);
		testSaveResource.save(baos, null);

		byte[] content = baos.toByteArray();
		ByteArrayInputStream bais = new ByteArrayInputStream(content);

		Resource testLoadResource = rs.createResource(uri);
		assertEquals(0, testLoadResource.getContents().size());
		testLoadResource.load(bais, null);
		assertEquals(1, testLoadResource.getContents().size());
		Foo result = (Foo) testLoadResource.getContents().get(0);
		assertNotNull(result);
		assertEquals("Emil", result.getValue());

		reg.unregister();
		Thread.sleep(1000l);
		Resource testLoadResource2 = rs.createResource(uri);
		assertNotEquals(testLoadResource, testLoadResource2);
		assertEquals(0, testLoadResource2.getContents().size());

		Assertions.assertThatThrownBy(() -> {
			testLoadResource2.load(bais, null);
		}).isInstanceOf(IOWrappedException.class);
	}

	/**
	 * Trying to load an instance with a registered {@link EPackage} than unload the
	 * EPackage and have an exception
	 * 
	 * @throws IOException
	 */
	@Test
	public void testLoadResourceRegisteredEPackageAndUnregisterProperties_Factory(
			@InjectService(cardinality = 0) ServiceAware<ResourceSetFactory> serviceAwareRSF) throws IOException {
		Dictionary<String, Object> properties = new Hashtable<String, Object>();
		properties.put(EMF_MODEL_NAME, ManualPackage.eNAME);
		ManualPackageConfigurator configurator = new ManualPackageConfigurator();

		ServiceRegistration<?> reg = bc.registerService(
				new String[] { EPackageConfigurator.class.getName(), ResourceFactoryConfigurator.class.getName() },
				configurator, properties);

		ServiceReference<ResourceSetFactory> reference = serviceAwareRSF.getServiceReference();
		assertNotNull(reference);
		Object modelNames = reference.getProperty(EMF_MODEL_NAME);
		assertNotNull(modelNames);
		assertTrue(modelNames instanceof String[]);
		List<String> modelNameList = Arrays.asList((String[]) modelNames);
		assertTrue(modelNameList.contains("ecore"));
		assertTrue(modelNameList.contains("manual"));

		ResourceSetFactory factory = serviceAwareRSF.getService();
		assertNotNull(factory);
		ResourceSet rs = factory.createResourceSet();
		assertNotNull(rs);
		URI uri = URI.createURI("foo.manual");
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		Resource testSaveResource = rs.createResource(uri);
		assertNotNull(testSaveResource);
		Foo p = ManualFactory.eINSTANCE.createFoo();
		p.setValue("Emil");

		testSaveResource.getContents().add(p);
		testSaveResource.save(baos, null);

		byte[] content = baos.toByteArray();
		ByteArrayInputStream bais = new ByteArrayInputStream(content);

		Resource testLoadResource = rs.createResource(uri);
		assertEquals(0, testLoadResource.getContents().size());
		testLoadResource.load(bais, null);
		assertEquals(1, testLoadResource.getContents().size());
		Foo result = (Foo) testLoadResource.getContents().get(0);
		assertNotNull(result);
		assertEquals("Emil", result.getValue());

		reg.unregister();

		Resource testLoadResource2 = rs.createResource(uri);
		assertNotEquals(testLoadResource, testLoadResource2);
		assertEquals(0, testLoadResource2.getContents().size());

		Assertions.assertThatThrownBy(() -> {
			testLoadResource2.load(bais, null);
		}).isInstanceOf(IOWrappedException.class);

		modelNames = reference.getProperty(EMF_MODEL_NAME);
		assertNotNull(modelNames);
		assertTrue(modelNames instanceof String[]);
		modelNameList = Arrays.asList((String[]) modelNames);
		assertTrue(modelNameList.contains("ecore"));
		assertFalse(modelNameList.contains("manual"));
	}

	/**
	 * Trying to load an instance with a registered {@link EPackage} than unload the
	 * EPackage and have an exception
	 * 
	 * @throws IOException
	 */
	@Test
	public void testLoadResourceRegisteredEPackageAndUnregisterProperties(
			@InjectService(cardinality = 0) ServiceAware<ResourceSet> serviceAwareRS) throws IOException {
		Dictionary<String, Object> properties = new Hashtable<String, Object>();
		properties.put(EMF_MODEL_NAME, ManualPackage.eNAME);
		ManualPackageConfigurator configurator = new ManualPackageConfigurator();

		ServiceRegistration<?> reg = bc.registerService(
				new String[] { EPackageConfigurator.class.getName(), ResourceFactoryConfigurator.class.getName() },
				configurator, properties);

		ServiceReference<ResourceSet> reference = serviceAwareRS.getServiceReference();
		assertNotNull(reference);
		Object modelNames = reference.getProperty(EMF_MODEL_NAME);
		assertNotNull(modelNames);
		assertTrue(modelNames instanceof String[]);
		List<String> modelNameList = Arrays.asList((String[]) modelNames);
		assertTrue(modelNameList.contains("ecore"));
		assertTrue(modelNameList.contains("manual"));

		ResourceSet rs = serviceAwareRS.getService();
		assertNotNull(rs);
		URI uri = URI.createURI("foo.manual");
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		Resource testSaveResource = rs.createResource(uri);
		assertNotNull(testSaveResource);
		Foo p = ManualFactory.eINSTANCE.createFoo();
		p.setValue("Emil");
		testSaveResource.getContents().add(p);
		testSaveResource.save(baos, null);

		byte[] content = baos.toByteArray();
		ByteArrayInputStream bais = new ByteArrayInputStream(content);

		Resource testLoadResource = rs.createResource(uri);
		assertEquals(0, testLoadResource.getContents().size());
		testLoadResource.load(bais, null);
		assertEquals(1, testLoadResource.getContents().size());
		Foo result = (Foo) testLoadResource.getContents().get(0);
		assertNotNull(result);
		assertEquals("Emil", result.getValue());

		reg.unregister();

		Resource testLoadResource2 = rs.createResource(uri);
		assertNotEquals(testLoadResource, testLoadResource2);
		assertEquals(0, testLoadResource2.getContents().size());

		Assertions.assertThatThrownBy(() -> {
			testLoadResource2.load(bais, null);
		}).isInstanceOf(IOWrappedException.class);

		modelNames = reference.getProperty(EMF_MODEL_NAME);
		assertNotNull(modelNames);
		assertTrue(modelNames instanceof String[]);
		modelNameList = Arrays.asList((String[]) modelNames);
		assertTrue(modelNameList.contains("ecore"));
		assertFalse(modelNameList.contains("manual"));
	}

	/**
	 * Trying to load an instance with a registered {@link EPackage} than unload the
	 * EPackage and have an exception
	 * 
	 * @throws IOException
	 * @throws InterruptedException
	 */
	@Test
	public void testEPackageAndConfiguratorProperties_Factory(
			@InjectService(cardinality = 0) ServiceAware<ResourceSetFactory> serviceAwareRSF)
			throws IOException, InterruptedException {
		Dictionary<String, Object> epackageProperties = new Hashtable<String, Object>();
		epackageProperties.put(EMF_MODEL_NAME, ManualPackage.eNAME);
		epackageProperties.put(EMF_MODEL_NSURI, ManualPackage.eNS_URI);
		ManualPackageConfigurator configurator = new ManualPackageConfigurator();

		ServiceRegistration<?> sreg = bc.registerService(
				new String[] { EPackageConfigurator.class.getName(), ResourceFactoryConfigurator.class.getName() },
				configurator, epackageProperties);

		ServiceReference<ResourceSetFactory> reference = serviceAwareRSF.getServiceReference();
		assertNotNull(reference);
		Object modelNames = reference.getProperty(EMF_MODEL_NAME);
		assertNotNull(modelNames);
		assertTrue(modelNames instanceof String[]);
		List<String> modelNameList = Arrays.asList((String[]) modelNames);
		assertTrue(modelNameList.contains("ecore"));
		assertTrue(modelNameList.contains("manual"));

		Object modelUris = reference.getProperty(EMF_MODEL_NSURI);
		assertNotNull(modelUris);
		assertTrue(modelUris instanceof String[]);
		List<String> modelUrisList = Arrays.asList((String[]) modelUris);
		assertTrue(modelUrisList.contains(EcorePackage.eNS_URI));
		assertTrue(modelUrisList.contains(ManualPackage.eNS_URI));
		Object configNames = reference.getProperty(EMF_CONFIGURATOR_NAME);
		assertNotNull(configNames);
		assertTrue(configNames instanceof String[]);
		List<String> configNameList = Arrays.asList((String[]) configNames);
		assertEquals(4, configNameList.size());

		Dictionary<String, Object> configProperties = new Hashtable<String, Object>();
		configProperties.put(EMF_CONFIGURATOR_NAME, new String[]{"testConfigurator", "testResourceConfigurator"});

		TestConfigurator configurator2 = new TestConfigurator();

		ServiceRegistration<?> sreg2 = bc.registerService(
				new String[] { ResourceSetConfigurator.class.getName(), ResourceFactoryConfigurator.class.getName() },
				configurator2, configProperties);
		reference = serviceAwareRSF.getServiceReference();
		assertNotNull(reference);

		configNames = reference.getProperty(EMF_CONFIGURATOR_NAME);
		assertNotNull(configNames);
		assertTrue(configNames instanceof String[]);
		configNameList = Arrays.asList((String[]) configNames);
		assertEquals(6, configNameList.size());
		assertTrue(configNameList.contains("testConfigurator"));
		assertTrue(configNameList.contains("testResourceConfigurator"));

		ResourceSetFactory factory = serviceAwareRSF.getService();
		assertNotNull(factory);
		ResourceSet rs = factory.createResourceSet();
		assertNotNull(rs);
		URI uri = URI.createURI("foo.manual");
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		Resource testSaveResource = rs.createResource(uri);
		assertNotNull(testSaveResource);
		Foo p = ManualFactory.eINSTANCE.createFoo();
		p.setValue("Emil");
		testSaveResource.getContents().add(p);
		testSaveResource.save(baos, null);

		byte[] content = baos.toByteArray();
		ByteArrayInputStream bais = new ByteArrayInputStream(content);

		Resource testLoadResource = rs.createResource(uri);
		assertEquals(0, testLoadResource.getContents().size());
		testLoadResource.load(bais, null);
		assertEquals(1, testLoadResource.getContents().size());
		Foo result = (Foo) testLoadResource.getContents().get(0);
		assertNotNull(result);
		assertEquals("Emil", result.getValue());

		sreg.unregister();
		modelUris = reference.getProperty(EMF_MODEL_NAME);
		assertNotNull(modelUris);
		assertTrue(modelUris instanceof String[]);
		modelUrisList = Arrays.asList((String[]) modelUris);
		assertTrue(modelUrisList.contains("ecore"));
		assertFalse(modelUrisList.contains("manual"));

		sreg2.unregister();

		configNames = reference.getProperty(EMF_CONFIGURATOR_NAME);
		assertNotNull(configNames);
		assertTrue(configNames instanceof String[]);
		configNameList = Arrays.asList((String[]) configNames);
		assertEquals(4, configNameList.size());
		assertFalse(configNameList.contains("testConfigurator"));
		assertFalse(configNameList.contains("testResourceConfigurator"));
	}

	/**
	 * Trying to load an instance with a registered {@link EPackage} than unload the
	 * EPackage and have an exception
	 * 
	 * @throws IOException
	 * @throws InterruptedException
	 */
	@Test
	public void testEPackageAndConfiguratorProperties(
			@InjectService(cardinality = 0) ServiceAware<ResourceSet> serviceAwareRS)
			throws IOException, InterruptedException {
		Dictionary<String, Object> epackageProperties = new Hashtable<String, Object>();
		epackageProperties.put(EMF_MODEL_NAME, ManualPackage.eNAME);
		ManualPackageConfigurator configurator = new ManualPackageConfigurator();

		ServiceRegistration<?> reg = bc.registerService(
				new String[] { EPackageConfigurator.class.getName(), ResourceFactoryConfigurator.class.getName() },
				configurator, epackageProperties);

		ServiceReference<ResourceSet> reference = serviceAwareRS.getServiceReference();
		assertNotNull(reference);
		Object modelNames = reference.getProperty(EMF_MODEL_NAME);
		assertNotNull(modelNames);
		assertTrue(modelNames instanceof String[]);
		List<String> modelNameList = Arrays.asList((String[]) modelNames);
		assertTrue(modelNameList.contains("ecore"));
		assertTrue(modelNameList.contains("manual"));
		Object configNames = reference.getProperty(EMF_CONFIGURATOR_NAME);
		assertNotNull(configNames);
		assertTrue(configNames instanceof String[]);
		List<String> configNameList = Arrays.asList((String[]) configNames);
		assertEquals(4, configNameList.size());

		Dictionary<String, Object> configProperties = new Hashtable<String, Object>();
		configProperties.put(EMF_CONFIGURATOR_NAME, new String[]{"testConfigurator", "testResourceConfigurator"});

		TestConfigurator configurator2 = new TestConfigurator();

		ServiceRegistration<?> reg2 = bc.registerService(
				new String[] { ResourceSetConfigurator.class.getName(), ResourceFactoryConfigurator.class.getName() },
				configurator2, configProperties);
		reference = serviceAwareRS.getServiceReference();
		assertNotNull(reference);

		configNames = reference.getProperty(EMF_CONFIGURATOR_NAME);
		assertNotNull(configNames);
		assertTrue(configNames instanceof String[]);
		configNameList = Arrays.asList((String[]) configNames);
		assertEquals(6, configNameList.size());
		assertTrue(configNameList.contains("testConfigurator"));
		assertTrue(configNameList.contains("testResourceConfigurator"));

		ResourceSet rs = serviceAwareRS.getService();
		assertNotNull(rs);
		URI uri = URI.createURI("foo.manual");
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		Resource testSaveResource = rs.createResource(uri);
		assertNotNull(testSaveResource);
		Foo p = ManualFactory.eINSTANCE.createFoo();
		p.setValue("Emil");
		testSaveResource.getContents().add(p);
		testSaveResource.save(baos, null);

		byte[] content = baos.toByteArray();
		ByteArrayInputStream bais = new ByteArrayInputStream(content);

		Resource testLoadResource = rs.createResource(uri);
		assertEquals(0, testLoadResource.getContents().size());
		testLoadResource.load(bais, null);
		assertEquals(1, testLoadResource.getContents().size());
		Foo result = (Foo) testLoadResource.getContents().get(0);
		assertNotNull(result);
		assertEquals("Emil", result.getValue());

		reg.unregister();

		modelNames = reference.getProperty(EMF_MODEL_NAME);
		assertNotNull(modelNames);
		assertTrue(modelNames instanceof String[]);
		modelNameList = Arrays.asList((String[]) modelNames);
		assertTrue(modelNameList.contains("ecore"));
		assertFalse(modelNameList.contains("manual"));

		reg2.unregister();

		configNames = reference.getProperty(EMF_CONFIGURATOR_NAME);
		assertNotNull(configNames);
		assertTrue(configNames instanceof String[]);
		configNameList = Arrays.asList((String[]) configNames);
		assertEquals(4, configNameList.size());
		assertFalse(configNameList.contains("testConfigurator"));
		assertFalse(configNameList.contains("testResourceConfigurator"));
	}

}