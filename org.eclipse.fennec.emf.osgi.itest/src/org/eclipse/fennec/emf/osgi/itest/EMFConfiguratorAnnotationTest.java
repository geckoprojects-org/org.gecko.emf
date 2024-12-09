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

import static org.assertj.core.api.Assertions.fail;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.fennec.emf.osgi.ResourceSetFactory;
import org.eclipse.fennec.emf.osgi.constants.EMFNamespaces;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.osgi.framework.dto.BundleDTO;
import org.osgi.service.component.runtime.ServiceComponentRuntime;
import org.osgi.service.component.runtime.dto.ComponentDescriptionDTO;
import org.osgi.test.common.annotation.InjectBundleContext;
import org.osgi.test.common.annotation.InjectService;
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
public class EMFConfiguratorAnnotationTest {

	@InjectBundleContext
	BundleContext bc;
	
	@Test
	public void testResourceFactoryAnnotation(@InjectService ServiceAware<ResourceSetFactory> sa, @InjectService ServiceAware<ServiceComponentRuntime> scrAware
			)
			throws IOException {

		ServiceReference<ResourceSetFactory> reference = sa.getServiceReference();
		assertNotNull(reference);
		
		assertFalse(scrAware.isEmpty());
		ServiceComponentRuntime scr = scrAware.getService();
		ComponentDescriptionDTO cddto = new ComponentDescriptionDTO();
		cddto.name = "TestResourceFactoryConfigurator";
		cddto.bundle = bc.getBundle().adapt(BundleDTO.class);
		assertFalse(scr.isComponentEnabled(cddto));
		
		ResourceSet resourceSet = sa.getService().createResourceSet();
		
		assertNull(resourceSet.getResourceFactoryRegistry().getContentTypeToFactoryMap().get("test/123"));
		assertNull(resourceSet.getResourceFactoryRegistry().getContentTypeToFactoryMap().get("test234"));
		
		Object contentType = reference.getProperty(EMFNamespaces.EMF_MODEL_CONTENT_TYPE);
		Object configuratorName = reference.getProperty(EMFNamespaces.EMF_CONFIGURATOR_NAME);
		Object fileExt = reference.getProperty(EMFNamespaces.EMF_MODEL_FILE_EXT);
		
		assertNotNull(configuratorName);
		assertInstanceOf(String[].class, configuratorName);
		List<String> configuratorNameList = Arrays.asList((String[]) configuratorName);
		assertFalse(configuratorNameList.contains("testResourceFactory"));
		
		assertNotNull(contentType);
		assertInstanceOf(String[].class, contentType);
		List<String> contentTypeNameList = Arrays.asList((String[]) contentType);
		assertFalse(contentTypeNameList.contains("test/123"));
		assertFalse(contentTypeNameList.contains("test234"));
		
		Object protocol = reference.getProperty(EMFNamespaces.EMF_MODEL_PROTOCOL);
		assertNull(protocol);
		
		Object feature = reference.getProperty(EMFNamespaces.EMF_MODEL_FEATURE);
		assertNull(feature);
		
		assertNotNull(fileExt);
		assertInstanceOf(String[].class, fileExt);
		List<String> fileExtList = Arrays.asList((String[]) fileExt);
		assertFalse(fileExtList.contains("fe123"));
		assertFalse(fileExtList.contains("fe234"));
		
		scr.enableComponent(cddto);
		
		try {
			Thread.sleep(100);
		} catch (InterruptedException e) {
			fail("unexpected interruption");
		}
		assertNotNull(resourceSet.getResourceFactoryRegistry().getContentTypeToFactoryMap().get("test/123"));
		assertNotNull(resourceSet.getResourceFactoryRegistry().getContentTypeToFactoryMap().get("test234"));
		
		contentType = reference.getProperty(EMFNamespaces.EMF_MODEL_CONTENT_TYPE);
		configuratorName = reference.getProperty(EMFNamespaces.EMF_CONFIGURATOR_NAME);
		assertNotNull(configuratorName);
		assertInstanceOf(String[].class, configuratorName);
		configuratorNameList = Arrays.asList((String[]) configuratorName);
		assertTrue(configuratorNameList.contains("testResourceFactory"));
		
		assertNotNull(contentType);
		assertInstanceOf(String[].class, contentType);
		contentTypeNameList = Arrays.asList((String[]) contentType);
		assertTrue(contentTypeNameList.contains("test/123"));
		assertTrue(contentTypeNameList.contains("test234"));
		
		protocol = reference.getProperty(EMFNamespaces.EMF_MODEL_PROTOCOL);
		assertNotNull(protocol);
		assertInstanceOf(String[].class, protocol);
		List<String> protocolList = Arrays.asList((String[]) protocol);
		assertTrue(protocolList.contains("p123"));
		assertTrue(protocolList.contains("p234"));
		
		feature = reference.getProperty(EMFNamespaces.EMF_MODEL_FEATURE);
		assertNotNull(feature);
		assertInstanceOf(String[].class, feature);
		List<String> featureList = Arrays.asList((String[]) feature);
		assertTrue(featureList.contains("test123"));
		assertTrue(featureList.contains("test234"));
		
		fileExt = reference.getProperty(EMFNamespaces.EMF_MODEL_FILE_EXT);
		assertNotNull(fileExt);
		assertInstanceOf(String[].class, fileExt);
		fileExtList = Arrays.asList((String[]) fileExt);
		assertTrue(fileExtList.contains("fe123"));
		assertTrue(fileExtList.contains("fe234"));
		
		scr.disableComponent(cddto);
		assertFalse(scr.isComponentEnabled(cddto));
		
	}
	
	@Test
	public void testEPackageAnnotation(@InjectService ServiceAware<ResourceSetFactory> sa, @InjectService ServiceAware<ServiceComponentRuntime> scrAware
			)
					throws IOException {
		
		ServiceReference<ResourceSetFactory> reference = sa.getServiceReference();
		assertNotNull(reference);
		
		assertFalse(scrAware.isEmpty());
		ServiceComponentRuntime scr = scrAware.getService();
		ComponentDescriptionDTO cddto = new ComponentDescriptionDTO();
		cddto.name = "TestEPackageConfigurator";
		cddto.bundle = bc.getBundle().adapt(BundleDTO.class);
		assertFalse(scr.isComponentEnabled(cddto));
		
		Object contentType = reference.getProperty(EMFNamespaces.EMF_MODEL_CONTENT_TYPE);
		Object configuratorName = reference.getProperty(EMFNamespaces.EMF_CONFIGURATOR_NAME);
		Object fileExt = reference.getProperty(EMFNamespaces.EMF_MODEL_FILE_EXT);
		
		assertNotNull(configuratorName);
		assertInstanceOf(String[].class, configuratorName);
		List<String> configuratorNameList = Arrays.asList((String[]) configuratorName);
		assertFalse(configuratorNameList.contains("testPackage"));
		
		assertNotNull(contentType);
		assertInstanceOf(String[].class, contentType);
		List<String> contentTypeNameList = Arrays.asList((String[]) contentType);
		assertFalse(contentTypeNameList.contains("testPackage"));
		assertFalse(contentTypeNameList.contains("testPackage2"));
		
		Object protocol = reference.getProperty(EMFNamespaces.EMF_MODEL_PROTOCOL);
		assertNull(protocol);
		
		Object feature = reference.getProperty(EMFNamespaces.EMF_MODEL_FEATURE);
		assertNull(feature);
		
		assertNotNull(fileExt);
		assertInstanceOf(String[].class, fileExt);
		List<String> fileExtList = Arrays.asList((String[]) fileExt);
		assertFalse(fileExtList.contains("fe123"));
		assertFalse(fileExtList.contains("fe234"));
		
		scr.enableComponent(cddto);
		
		try {
			Thread.sleep(100);
		} catch (InterruptedException e) {
			fail("unexpected interruption");
		}
		
		contentType = reference.getProperty(EMFNamespaces.EMF_MODEL_CONTENT_TYPE);
		configuratorName = reference.getProperty(EMFNamespaces.EMF_CONFIGURATOR_NAME);
		assertNotNull(configuratorName);
		assertInstanceOf(String[].class, configuratorName);
		configuratorNameList = Arrays.asList((String[]) configuratorName);
		assertTrue(configuratorNameList.contains("testPackage"));
		
		assertNotNull(contentType);
		assertInstanceOf(String[].class, contentType);
		contentTypeNameList = Arrays.asList((String[]) contentType);
		assertTrue(contentTypeNameList.contains("testPackage"));
		assertTrue(contentTypeNameList.contains("testPackage2"));
		
		protocol = reference.getProperty(EMFNamespaces.EMF_MODEL_PROTOCOL);
		assertNotNull(protocol);
		assertInstanceOf(String[].class, protocol);
		List<String> protocolList = Arrays.asList((String[]) protocol);
		assertTrue(protocolList.contains("tp1"));
		assertTrue(protocolList.contains("tp2"));
		
		feature = reference.getProperty(EMFNamespaces.EMF_MODEL_FEATURE);
		assertNotNull(feature);
		assertInstanceOf(String[].class, feature);
		List<String> featureList = Arrays.asList((String[]) feature);
		assertTrue(featureList.contains("testPackageFeature"));
		assertTrue(featureList.contains("testPF"));
		
		fileExt = reference.getProperty(EMFNamespaces.EMF_MODEL_FILE_EXT);
		assertNotNull(fileExt);
		assertInstanceOf(String[].class, fileExt);
		fileExtList = Arrays.asList((String[]) fileExt);
		assertTrue(fileExtList.contains("fetp1"));
		assertTrue(fileExtList.contains("fetp2"));
		
		scr.disableComponent(cddto);
		assertFalse(scr.isComponentEnabled(cddto));
	}
	
	@Test
	public void testResourceSetAnnotation(@InjectService ServiceAware<ResourceSetFactory> sa, @InjectService ServiceAware<ServiceComponentRuntime> scrAware
			)
					throws IOException {
		
		ServiceReference<ResourceSetFactory> reference = sa.getServiceReference();
		assertNotNull(reference);
		
		assertFalse(scrAware.isEmpty());
		ServiceComponentRuntime scr = scrAware.getService();
		ComponentDescriptionDTO cddto = new ComponentDescriptionDTO();
		cddto.name = "TestResourceSetConfigurator";
		cddto.bundle = bc.getBundle().adapt(BundleDTO.class);
		assertFalse(scr.isComponentEnabled(cddto));
		
		Object contentType = reference.getProperty(EMFNamespaces.EMF_MODEL_CONTENT_TYPE);
		Object configuratorName = reference.getProperty(EMFNamespaces.EMF_CONFIGURATOR_NAME);
		Object fileExt = reference.getProperty(EMFNamespaces.EMF_MODEL_FILE_EXT);
		
		assertNotNull(configuratorName);
		assertInstanceOf(String[].class, configuratorName);
		List<String> configuratorNameList = Arrays.asList((String[]) configuratorName);
		assertFalse(configuratorNameList.contains("testResourceSet"));
		
		assertNotNull(contentType);
		assertInstanceOf(String[].class, contentType);
		List<String> contentTypeNameList = Arrays.asList((String[]) contentType);
		assertFalse(contentTypeNameList.contains("testRS"));
		assertFalse(contentTypeNameList.contains("testRS2"));
		
		Object protocol = reference.getProperty(EMFNamespaces.EMF_MODEL_PROTOCOL);
		assertNull(protocol);
		
		Object feature = reference.getProperty(EMFNamespaces.EMF_MODEL_FEATURE);
		assertNull(feature);
		
		assertNotNull(fileExt);
		assertInstanceOf(String[].class, fileExt);
		List<String> fileExtList = Arrays.asList((String[]) fileExt);
		assertFalse(fileExtList.contains("fers1"));
		assertFalse(fileExtList.contains("fers2"));
		
		scr.enableComponent(cddto);
		
		try {
			Thread.sleep(100);
		} catch (InterruptedException e) {
			fail("unexpected interruption");
		}
		contentType = reference.getProperty(EMFNamespaces.EMF_MODEL_CONTENT_TYPE);
		configuratorName = reference.getProperty(EMFNamespaces.EMF_CONFIGURATOR_NAME);
		assertNotNull(configuratorName);
		assertInstanceOf(String[].class, configuratorName);
		configuratorNameList = Arrays.asList((String[]) configuratorName);
		assertTrue(configuratorNameList.contains("testResourceSet"));
		
		assertNotNull(contentType);
		assertInstanceOf(String[].class, contentType);
		contentTypeNameList = Arrays.asList((String[]) contentType);
		assertTrue(contentTypeNameList.contains("testRS"));
		assertTrue(contentTypeNameList.contains("testRS2"));
		
		protocol = reference.getProperty(EMFNamespaces.EMF_MODEL_PROTOCOL);
		assertNotNull(protocol);
		assertInstanceOf(String[].class, protocol);
		List<String> protocolList = Arrays.asList((String[]) protocol);
		assertTrue(protocolList.contains("rsp1"));
		assertTrue(protocolList.contains("rsp2"));
		
		feature = reference.getProperty(EMFNamespaces.EMF_MODEL_FEATURE);
		assertNotNull(feature);
		assertInstanceOf(String[].class, feature);
		List<String> featureList = Arrays.asList((String[]) feature);
		assertTrue(featureList.contains("testRSFeature"));
		assertTrue(featureList.contains("testRSPF"));
		
		fileExt = reference.getProperty(EMFNamespaces.EMF_MODEL_FILE_EXT);
		assertNotNull(fileExt);
		assertInstanceOf(String[].class, fileExt);
		fileExtList = Arrays.asList((String[]) fileExt);
		assertTrue(fileExtList.contains("fers1"));
		assertTrue(fileExtList.contains("fers2"));
		
		scr.disableComponent(cddto);
		assertFalse(scr.isComponentEnabled(cddto));
	}

	
}