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
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.util.Arrays;
import java.util.Dictionary;
import java.util.List;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.Resource.Factory;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ResourceFactoryImpl;
import org.eclipse.fennec.emf.osgi.ResourceSetFactory;
import org.eclipse.fennec.emf.osgi.constants.EMFNamespaces;
import org.eclipse.fennec.emf.osgi.itest.configurator.TestResource;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.osgi.framework.ServiceRegistration;
import org.osgi.test.common.annotation.InjectBundleContext;
import org.osgi.test.common.annotation.InjectService;
import org.osgi.test.common.dictionary.Dictionaries;
import org.osgi.test.common.service.ServiceAware;
import org.osgi.test.junit5.context.BundleContextExtension;
import org.osgi.test.junit5.service.ServiceExtension;

/**
 * Tests the EMF Resource Factory Whiteboard integration
 * 
 * @author Juergen Albert
 * @since 22.02.2022
 */
@ExtendWith(BundleContextExtension.class)
@ExtendWith(ServiceExtension.class)
@ExtendWith(MockitoExtension.class)
public class EMFResourceFactoryWhiteboardTest {

	@InjectBundleContext
	BundleContext bc;
	
	@Mock
	Factory factoryOneMock;
	@Mock
	Factory factoryTwoMock;

	@Test
	public void testDelegatingResourceFactoryRegistry(@InjectService ServiceAware<ResourceSetFactory> sa
			)
			throws IOException {

		ServiceReference<ResourceSetFactory> reference = sa.getServiceReference();
		assertNotNull(reference);

		ResourceSet resourceSet = sa.getService().createResourceSet();
		
		resourceSet.getResourceFactoryRegistry().getContentTypeToFactoryMap().put("foo/bar", new ResourceFactoryImpl() {
			/* 
			 * (non-Javadoc)
			 * @see org.eclipse.emf.ecore.resource.impl.ResourceFactoryImpl#createResource(org.eclipse.emf.common.util.URI)
			 */
			@Override
			public Resource createResource(URI uri) {
				
				return new TestResource(uri);
			}
		});
		
		Resource resource = resourceSet.createResource(URI.createURI("http://test/foo"), "foo/bar");
		assertTrue(resource instanceof TestResource);
		
	}

	
	//TODO More tests for dynamic registration and unregistration.
	
	/**
	 * Trying to load an instance with a non registered {@link EPackage}
	 * 
	 * @throws IOException
	 */
	@Test
	public void testGeckoXMLResourceFactoryRegistered(@InjectService ServiceAware<ResourceSetFactory> sa)
			throws IOException {

		ServiceReference<ResourceSetFactory> reference = sa.getServiceReference();
		assertNotNull(reference);

		Object configurators = reference.getProperty(EMFNamespaces.EMF_CONFIGURATOR_NAME);
		assertNotNull(configurators);
		assertTrue(configurators instanceof String[]);
		List<String> configuratorNameList = Arrays.asList((String[]) configurators);
		assertTrue(configuratorNameList.contains("GeckoXMLResourceFactory"));
	}

	/**
	 * Trying to load an instance with a non registered {@link EPackage}
	 * 
	 * @throws IOException
	 */
	@Test
	public void testGeckoXMLResourceFactoryRegisteredInRegistry(@InjectService ServiceAware<ResourceSetFactory> sa)
			throws IOException {
		
		
		ServiceReference<ResourceSetFactory> rsfReference = sa.getServiceReference();
		assertNotNull(rsfReference);
		
		ResourceSetFactory rsf = bc.getService(rsfReference);
		assertNotNull(rsf);
		ResourceSet rs = rsf.createResourceSet();
		assertNotNull(rs);
		
		Object xmlFactory = rs.getResourceFactoryRegistry().getContentTypeToFactoryMap().get("application/xml");
		assertNotNull(xmlFactory);
		assertEquals("GeckoXMLResourceFactory", xmlFactory.getClass().getSimpleName());
	}
	
	/**
	 * Trying to register a mock registry and lookup in the resulting {@link ResourceSet}
	 * 
	 * @throws IOException
	 * @throws InterruptedException 
	 */
	@Test
	public void testRegisterResourceFactoryInRegistry(@InjectService ServiceAware<ResourceSetFactory> rsfAware, 
			@InjectService(filter = "(%s=%s)", filterArguments = {EMFNamespaces.EMF_CONFIGURATOR_NAME, "foo-bar"}, cardinality = 0) ServiceAware<Factory> factoryAware)
					throws IOException, InterruptedException {
		
		assertNull(factoryAware.getService());
		ServiceReference<ResourceSetFactory> rsfReference = rsfAware.getServiceReference();
		assertNotNull(rsfReference);
		
		Dictionary<String, Object> properties = Dictionaries.dictionaryOf(EMFNamespaces.EMF_MODEL_CONTENT_TYPE, new String[]{"foo/bar"}, EMFNamespaces.EMF_CONFIGURATOR_NAME, "foo-bar");
		ServiceRegistration<Factory> factoryRegistration = bc.registerService(Factory.class, factoryOneMock, properties);
		
		Factory factory = factoryAware.waitForService(1000l);
		assertNotNull(factory);
		ServiceReference<Factory> factoryReference = factoryAware.getServiceReference();
		assertNotNull(factoryReference);
		
		Object contentType = factoryReference.getProperty(EMFNamespaces.EMF_MODEL_CONTENT_TYPE);
		Object configuratorName = factoryReference.getProperty(EMFNamespaces.EMF_CONFIGURATOR_NAME);
		assertNotNull(configuratorName);
		assertInstanceOf(String.class, configuratorName);
		assertEquals("foo-bar", configuratorName);
		assertNotNull(contentType);
		assertInstanceOf(String[].class, contentType);
		List<String> contentTypeNameList = Arrays.asList((String[]) contentType);
		assertTrue(contentTypeNameList.contains("foo/bar"));
		
		rsfReference = rsfAware.getServiceReference();
		assertNotNull(rsfReference);
		
		ResourceSetFactory rsf = bc.getService(rsfReference);
		assertNotNull(rsf);
		ResourceSet rs = rsf.createResourceSet();
		assertNotNull(rs);
		
		Object fooBarFactory = rs.getResourceFactoryRegistry().getContentTypeToFactoryMap().get("foo/bar");
		assertNotNull(fooBarFactory);
		assertEquals(factoryOneMock, fooBarFactory);
		
		factoryRegistration.unregister();
		
		factory = factoryAware.waitForService(1000l);
		assertNull(factory);
		fooBarFactory = rs.getResourceFactoryRegistry().getContentTypeToFactoryMap().get("foo/bar");
		assertNull(fooBarFactory);
		
	}
	
	/**
	 * Trying to register a mock registry and lookup in the resulting {@link ResourceSet} properties, if the resource factory properties are delegated to the resource set factory
	 * 
	 * @throws IOException
	 * @throws InterruptedException 
	 */
	@Test
	public void testRegisterResourceFactoryInResourceSetFactory(@InjectService ServiceAware<ResourceSetFactory> rsfAware, 
			@InjectService(filter = "(%s=%s)", filterArguments = {EMFNamespaces.EMF_CONFIGURATOR_NAME, "foo-bar"}, cardinality = 0) ServiceAware<Factory> factoryAware)
					throws IOException, InterruptedException {
		
		assertFalse(rsfAware.isEmpty());
		assertTrue(factoryAware.isEmpty());
		
		ServiceReference<ResourceSetFactory> rsfReference = rsfAware.getServiceReference();
		assertNotNull(rsfReference);
		
		Object configuratorName = rsfReference.getProperty(EMFNamespaces.EMF_CONFIGURATOR_NAME);
		assertNotNull(configuratorName);
		assertInstanceOf(String[].class, configuratorName);
		List<String> configuratorNameList = Arrays.asList((String[]) configuratorName);
		assertFalse(configuratorNameList.contains("foo-bar"));
		
		Object contentType = rsfReference.getProperty(EMFNamespaces.EMF_MODEL_CONTENT_TYPE);
		assertNotNull(contentType);
		assertInstanceOf(String[].class, contentType);
		List<String> contentTypeNameList = Arrays.asList((String[]) contentType);
		assertFalse(contentTypeNameList.contains("foo/bar"));
		
		Dictionary<String, Object> properties = Dictionaries.dictionaryOf(EMFNamespaces.EMF_MODEL_CONTENT_TYPE, new String[]{"foo/bar"}, EMFNamespaces.EMF_CONFIGURATOR_NAME, "foo-bar");
		ServiceRegistration<Factory> registration = bc.registerService(Factory.class, factoryOneMock, properties);
		
		assertNotNull(factoryAware.waitForService(1000l));
		
		configuratorName = rsfReference.getProperty(EMFNamespaces.EMF_CONFIGURATOR_NAME);
		assertNotNull(configuratorName);
		assertInstanceOf(String[].class, configuratorName);
		configuratorNameList = Arrays.asList((String[]) configuratorName);
		assertTrue(configuratorNameList.contains("foo-bar"));
		
		contentType = rsfReference.getProperty(EMFNamespaces.EMF_MODEL_CONTENT_TYPE);
		assertNotNull(contentType);
		assertInstanceOf(String[].class, contentType);
		contentTypeNameList = Arrays.asList((String[]) contentType);
		assertTrue(contentTypeNameList.contains("foo/bar"));
		
		ResourceSetFactory rsf = rsfAware.getService();
		assertNotNull(rsf);
		ResourceSet rs = rsf.createResourceSet();
		assertNotNull(rs);
		
		Object fooBarFactory = rs.getResourceFactoryRegistry().getContentTypeToFactoryMap().get("foo/bar");
		assertNotNull(fooBarFactory);
		assertEquals(factoryOneMock, fooBarFactory);
		
		registration.unregister();
		
		fooBarFactory = rs.getResourceFactoryRegistry().getContentTypeToFactoryMap().get("foo/bar");
		assertNull(fooBarFactory);
		
		configuratorName = rsfReference.getProperty(EMFNamespaces.EMF_CONFIGURATOR_NAME);
		assertNotNull(configuratorName);
		assertInstanceOf(String[].class, configuratorName);
		configuratorNameList = Arrays.asList((String[]) configuratorName);
		assertFalse(configuratorNameList.contains("foo-bar"));
		
		contentType = rsfReference.getProperty(EMFNamespaces.EMF_MODEL_CONTENT_TYPE);
		assertNotNull(contentType);
		assertInstanceOf(String[].class, contentType);
		contentTypeNameList = Arrays.asList((String[]) contentType);
		assertFalse(contentTypeNameList.contains("foo/bar"));
		
		
	}
	
	/**
	 * Trying to register a mock registry and lookup in the resulting {@link ResourceSet} for two resource factories of same content type.
	 * 
	 * @throws IOException
	 * @throws InterruptedException 
	 */
	@Test
	public void testRegisterSecondResourceFactoryInRegistry(@InjectService ServiceAware<ResourceSetFactory> rsfAware, 
			@InjectService(filter = "(%s=%s)", filterArguments = {EMFNamespaces.EMF_CONFIGURATOR_NAME, "foo-bar"}, cardinality = 0) ServiceAware<Factory> factoryOneAware,
			@InjectService(filter = "(%s=%s)", filterArguments = {EMFNamespaces.EMF_CONFIGURATOR_NAME, "foo-bar2"}, cardinality = 0) ServiceAware<Factory> factoryTwoAware)
					throws IOException, InterruptedException {
		
		assertFalse(rsfAware.isEmpty());
		assertTrue(factoryOneAware.isEmpty());
		assertTrue(factoryTwoAware.isEmpty());
		
		Dictionary<String, Object> properties = Dictionaries.dictionaryOf(EMFNamespaces.EMF_MODEL_CONTENT_TYPE, new String[]{"foo/bar"}, EMFNamespaces.EMF_CONFIGURATOR_NAME, "foo-bar");
		ServiceRegistration<Factory> registrationOne = bc.registerService(Factory.class, factoryOneMock, properties);
		
		assertNotNull(factoryOneAware.waitForService(1000l));
		
		ServiceReference<ResourceSetFactory> rsfReference = rsfAware.getServiceReference();
		assertNotNull(rsfReference);
		
		Object configuratorName = rsfReference.getProperty(EMFNamespaces.EMF_CONFIGURATOR_NAME);
		List<String> configuratorNameList = Arrays.asList((String[]) configuratorName);
		assertTrue(configuratorNameList.contains("foo-bar"));
		assertFalse(configuratorNameList.contains("foo-bar2"));
//		
		Object contentType = rsfReference.getProperty(EMFNamespaces.EMF_MODEL_CONTENT_TYPE);
		List<String> contentTypeNameList = Arrays.asList((String[]) contentType);
		assertTrue(contentTypeNameList.contains("foo/bar"));
		
		ResourceSetFactory rsf = rsfAware.getService();
		assertNotNull(rsf);
		ResourceSet rs = rsf.createResourceSet();
		assertNotNull(rs);
		
		Object fooBarFactory = rs.getResourceFactoryRegistry().getContentTypeToFactoryMap().get("foo/bar");
		assertNotNull(fooBarFactory);
		assertEquals(factoryOneMock, fooBarFactory);
		
		properties = Dictionaries.dictionaryOf(EMFNamespaces.EMF_MODEL_CONTENT_TYPE, new String[]{"foo/bar"}, EMFNamespaces.EMF_CONFIGURATOR_NAME, "foo-bar2");
		ServiceRegistration<Factory> registrationTwo = bc.registerService(Factory.class, factoryTwoMock, properties);
		
		assertNotNull(factoryTwoAware.waitForService(1000l));
		
		fooBarFactory = rs.getResourceFactoryRegistry().getContentTypeToFactoryMap().get("foo/bar");
		assertNotNull(fooBarFactory);
		assertEquals(factoryTwoMock, fooBarFactory);
		
		configuratorName = rsfReference.getProperty(EMFNamespaces.EMF_CONFIGURATOR_NAME);
		assertNotNull(configuratorName);
		assertInstanceOf(String[].class, configuratorName);
		configuratorNameList = Arrays.asList((String[]) configuratorName);
		assertTrue(configuratorNameList.contains("foo-bar"));
		assertTrue(configuratorNameList.contains("foo-bar2"));
		
		contentType = rsfReference.getProperty(EMFNamespaces.EMF_MODEL_CONTENT_TYPE);
		assertNotNull(contentType);
		assertInstanceOf(String[].class, contentType);
		contentTypeNameList = Arrays.asList((String[]) contentType);
		assertTrue(contentTypeNameList.contains("foo/bar"));
		
		registrationOne.unregister();
		
		configuratorName = rsfReference.getProperty(EMFNamespaces.EMF_CONFIGURATOR_NAME);
		assertNotNull(configuratorName);
		assertInstanceOf(String[].class, configuratorName);
		configuratorNameList = Arrays.asList((String[]) configuratorName);
		assertFalse(configuratorNameList.contains("foo-bar"));
		assertTrue(configuratorNameList.contains("foo-bar2"));
		
		contentType = rsfReference.getProperty(EMFNamespaces.EMF_MODEL_CONTENT_TYPE);
		assertNotNull(contentType);
		assertInstanceOf(String[].class, contentType);
		contentTypeNameList = Arrays.asList((String[]) contentType);
		assertTrue(contentTypeNameList.contains("foo/bar"));
		
		fooBarFactory = rs.getResourceFactoryRegistry().getContentTypeToFactoryMap().get("foo/bar");
		assertNotNull(fooBarFactory);
		assertEquals(factoryTwoMock, fooBarFactory);
		
		registrationTwo.unregister();
		fooBarFactory = rs.getResourceFactoryRegistry().getContentTypeToFactoryMap().get("foo/bar");
		assertNull(fooBarFactory);
		
		configuratorName = rsfReference.getProperty(EMFNamespaces.EMF_CONFIGURATOR_NAME);
		assertNotNull(configuratorName);
		assertInstanceOf(String[].class, configuratorName);
		configuratorNameList = Arrays.asList((String[]) configuratorName);
		assertFalse(configuratorNameList.contains("foo-bar"));
		assertFalse(configuratorNameList.contains("foo-bar2"));
		
		contentType = rsfReference.getProperty(EMFNamespaces.EMF_MODEL_CONTENT_TYPE);
		assertNotNull(contentType);
		assertInstanceOf(String[].class, contentType);
		contentTypeNameList = Arrays.asList((String[]) contentType);
		assertFalse(contentTypeNameList.contains("foo/bar"));
		
		
	}
	
}