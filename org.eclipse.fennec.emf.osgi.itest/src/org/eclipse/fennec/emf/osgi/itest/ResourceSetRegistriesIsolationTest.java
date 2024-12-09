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
import static org.junit.jupiter.api.Assertions.assertThrowsExactly;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Hashtable;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.Resource.IOWrappedException;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.xmi.impl.XMIResourceFactoryImpl;
import org.eclipse.fennec.emf.osgi.ResourceSetFactory;
import org.eclipse.fennec.emf.osgi.configurator.EPackageConfigurator;
import org.eclipse.fennec.emf.osgi.configurator.ResourceFactoryConfigurator;
import org.eclipse.fennec.emf.osgi.example.model.basic.BasicPackage;
import org.eclipse.fennec.emf.osgi.example.model.manual.Foo;
import org.eclipse.fennec.emf.osgi.example.model.manual.ManualFactory;
import org.eclipse.fennec.emf.osgi.example.model.manual.ManualPackage;
import org.eclipse.fennec.emf.osgi.example.model.manual.configuration.ManualPackageConfigurator;
import org.eclipse.fennec.emf.osgi.example.model.manual.util.ManualResourceFactoryImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.osgi.framework.BundleContext;
import org.osgi.framework.InvalidSyntaxException;
import org.osgi.framework.ServiceRegistration;
import org.osgi.test.common.annotation.InjectBundleContext;
import org.osgi.test.common.annotation.InjectService;
import org.osgi.test.junit5.context.BundleContextExtension;
import org.osgi.test.junit5.service.ServiceExtension;

/**
 * Tests the Scoping of EPackageRegistry and and ResourceFactory
 * 
 * @author Mark Hoffmann
 * @since 25.07.2017
 */
@ExtendWith(BundleContextExtension.class)
@ExtendWith(ServiceExtension.class)
public class ResourceSetRegistriesIsolationTest {

	@InjectBundleContext
	BundleContext bc;

	/**
	 * Registers Packages in as Service and Manually in the {@link ResourceSet} registry and checks if they are properly scoped.
	 */
	@Test
	public void testEPackageScoping(@InjectService ResourceSetFactory factory)
			throws IOException, InvalidSyntaxException {

		ResourceSet rs1 = factory.createResourceSet();
		assertNotNull(rs1);

		ResourceSet rs2 = factory.createResourceSet();
		assertNotNull(rs2);
		
		assertThrowsExactly(IOWrappedException.class, () -> tryLoadAndSave(rs1));
		assertThrowsExactly(IOWrappedException.class, () -> tryLoadAndSave(rs2));
		
		rs1.getPackageRegistry().put(ManualPackage.eNS_URI, ManualPackage.eINSTANCE);

		assertTrue(rs1.getPackageRegistry().containsKey(ManualPackage.eNS_URI));
		assertFalse(rs2.getPackageRegistry().containsKey(ManualPackage.eNS_URI));

		assertThrowsExactly(IOWrappedException.class, () -> tryLoadAndSave(rs2));
		tryLoadAndSave(rs1);
		
		ServiceRegistration<?> registration = bc.registerService(
				new String[] { EPackageConfigurator.class.getName(), ResourceFactoryConfigurator.class.getName() },
				new ManualPackageConfigurator(), new Hashtable<String, Object>());
		
		assertTrue(rs1.getPackageRegistry().containsKey(ManualPackage.eNS_URI));
		assertTrue(rs2.getPackageRegistry().containsKey(ManualPackage.eNS_URI));
		
		tryLoadAndSave(rs1);
		tryLoadAndSave(rs2);
		
		registration.unregister();
		
		assertTrue(rs1.getPackageRegistry().containsKey(ManualPackage.eNS_URI));
		assertFalse(rs2.getPackageRegistry().containsKey(ManualPackage.eNS_URI));

		assertThrowsExactly(IOWrappedException.class, () -> tryLoadAndSave(rs2));
		tryLoadAndSave(rs1);
	}

	/**
	 * Registers Packages in as Service and Manually in the {@link ResourceSet} registry and checks if they are properly scoped.
	 */
	@Test
	public void testEPackageScoping_Overwrite(@InjectService ResourceSetFactory factory)
			throws IOException, InvalidSyntaxException {
		
		ResourceSet rs1 = factory.createResourceSet();
		assertNotNull(rs1);
		
		ResourceSet rs2 = factory.createResourceSet();
		assertNotNull(rs2);
		
		assertFalse(rs1.getPackageRegistry().containsKey(ManualPackage.eNS_URI));
		assertFalse(rs2.getPackageRegistry().containsKey(ManualPackage.eNS_URI));
		
		ServiceRegistration<?> registration = bc.registerService(
				new String[] { EPackageConfigurator.class.getName()},
				new ManualPackageConfigurator(), new Hashtable<String, Object>());
		
		assertTrue(rs1.getPackageRegistry().containsKey(ManualPackage.eNS_URI));
		assertTrue(rs2.getPackageRegistry().containsKey(ManualPackage.eNS_URI));
		
		assertInstanceOf(ManualPackage.class, rs1.getPackageRegistry().getEPackage(ManualPackage.eNS_URI));
		assertInstanceOf(ManualPackage.class, rs2.getPackageRegistry().getEPackage(ManualPackage.eNS_URI));

		rs1.getPackageRegistry().put(ManualPackage.eNS_URI, BasicPackage.eINSTANCE);
		
		assertInstanceOf(BasicPackage.class, rs1.getPackageRegistry().getEPackage(ManualPackage.eNS_URI));
		assertInstanceOf(ManualPackage.class, rs2.getPackageRegistry().getEPackage(ManualPackage.eNS_URI));

		rs1.getPackageRegistry().remove(ManualPackage.eNS_URI);
		
		assertInstanceOf(ManualPackage.class, rs1.getPackageRegistry().getEPackage(ManualPackage.eNS_URI));
		assertInstanceOf(ManualPackage.class, rs2.getPackageRegistry().getEPackage(ManualPackage.eNS_URI));

		rs1.getPackageRegistry().put(ManualPackage.eNS_URI, BasicPackage.eINSTANCE);
		rs2.getPackageRegistry().put(ManualPackage.eNS_URI, ManualPackage.eINSTANCE);
		
		registration.unregister();
		
		assertInstanceOf(BasicPackage.class, rs1.getPackageRegistry().getEPackage(ManualPackage.eNS_URI));
		assertInstanceOf(ManualPackage.class, rs2.getPackageRegistry().getEPackage(ManualPackage.eNS_URI));
	}

	
	/**
	 * Registers Packages in as Service and Manually in the {@link ResourceSet} registry and checks if they are properly scoped.
	 */
	@Test
	public void testFactoryScoping(@InjectService ResourceSetFactory factory)
			throws IOException, InvalidSyntaxException {
		
		
		ResourceSet rs1 = factory.createResourceSet();
		assertNotNull(rs1);
		
		ResourceSet rs2 = factory.createResourceSet();
		assertNotNull(rs2);
		
		//Add something manual
		rs1.getResourceFactoryRegistry().getProtocolToFactoryMap().put("xxx", new ManualResourceFactoryImpl());
		rs1.getResourceFactoryRegistry().getContentTypeToFactoryMap().put("xxx", new ManualResourceFactoryImpl());
		rs1.getResourceFactoryRegistry().getExtensionToFactoryMap().put("xxx", new ManualResourceFactoryImpl());
		
		assertTrue(rs1.getResourceFactoryRegistry().getProtocolToFactoryMap().containsKey("xxx"));
		assertTrue(rs1.getResourceFactoryRegistry().getContentTypeToFactoryMap().containsKey("xxx"));
		assertTrue(rs1.getResourceFactoryRegistry().getExtensionToFactoryMap().containsKey("xxx"));
		assertFalse(rs2.getResourceFactoryRegistry().getExtensionToFactoryMap().containsKey("manual"));

		assertFalse(rs2.getResourceFactoryRegistry().getProtocolToFactoryMap().containsKey("xxx"));
		assertFalse(rs2.getResourceFactoryRegistry().getContentTypeToFactoryMap().containsKey("xxx"));
		assertFalse(rs2.getResourceFactoryRegistry().getExtensionToFactoryMap().containsKey("xxx"));
		assertFalse(rs2.getResourceFactoryRegistry().getExtensionToFactoryMap().containsKey("manual"));
		
		
		//Now register the service and look if it adds
		ServiceRegistration<?> registration = bc.registerService(
				new String[] { EPackageConfigurator.class.getName(), ResourceFactoryConfigurator.class.getName() },
				new ManualPackageConfigurator(), new Hashtable<String, Object>());
		
		assertInstanceOf(ManualResourceFactoryImpl.class, rs1.getResourceFactoryRegistry().getProtocolToFactoryMap().get("xxx"));
		assertInstanceOf(ManualResourceFactoryImpl.class, rs1.getResourceFactoryRegistry().getContentTypeToFactoryMap().get("xxx"));
		assertInstanceOf(ManualResourceFactoryImpl.class, rs1.getResourceFactoryRegistry().getExtensionToFactoryMap().get("xxx"));
		assertInstanceOf(ManualResourceFactoryImpl.class, rs1.getResourceFactoryRegistry().getExtensionToFactoryMap().get("manual"));

		assertFalse(rs2.getResourceFactoryRegistry().getProtocolToFactoryMap().containsKey("xxx"));
		assertFalse(rs2.getResourceFactoryRegistry().getContentTypeToFactoryMap().containsKey("xxx"));
		assertFalse(rs2.getResourceFactoryRegistry().getExtensionToFactoryMap().containsKey("xxx"));
		assertInstanceOf(ManualResourceFactoryImpl.class, rs2.getResourceFactoryRegistry().getExtensionToFactoryMap().get("manual"));
		
		//Now we overwrite
		rs2.getResourceFactoryRegistry().getExtensionToFactoryMap().put("manual", new XMIResourceFactoryImpl());
		
		assertInstanceOf(ManualResourceFactoryImpl.class, rs1.getResourceFactoryRegistry().getProtocolToFactoryMap().get("xxx"));
		assertInstanceOf(ManualResourceFactoryImpl.class, rs1.getResourceFactoryRegistry().getContentTypeToFactoryMap().get("xxx"));
		assertInstanceOf(ManualResourceFactoryImpl.class, rs1.getResourceFactoryRegistry().getExtensionToFactoryMap().get("xxx"));
		assertInstanceOf(ManualResourceFactoryImpl.class, rs1.getResourceFactoryRegistry().getExtensionToFactoryMap().get("manual"));

		assertFalse(rs2.getResourceFactoryRegistry().getProtocolToFactoryMap().containsKey("xxx"));
		assertFalse(rs2.getResourceFactoryRegistry().getContentTypeToFactoryMap().containsKey("xxx"));
		assertFalse(rs2.getResourceFactoryRegistry().getExtensionToFactoryMap().containsKey("xxx"));
		assertInstanceOf(XMIResourceFactoryImpl.class, rs2.getResourceFactoryRegistry().getExtensionToFactoryMap().get("manual"));

		registration.unregister();
		
		assertInstanceOf(ManualResourceFactoryImpl.class, rs1.getResourceFactoryRegistry().getProtocolToFactoryMap().get("xxx"));
		assertInstanceOf(ManualResourceFactoryImpl.class, rs1.getResourceFactoryRegistry().getContentTypeToFactoryMap().get("xxx"));
		assertInstanceOf(ManualResourceFactoryImpl.class, rs1.getResourceFactoryRegistry().getExtensionToFactoryMap().get("xxx"));
		assertFalse(rs1.getResourceFactoryRegistry().getExtensionToFactoryMap().containsKey("manual"));

		assertFalse(rs2.getResourceFactoryRegistry().getProtocolToFactoryMap().containsKey("xxx"));
		assertFalse(rs2.getResourceFactoryRegistry().getContentTypeToFactoryMap().containsKey("xxx"));
		assertFalse(rs2.getResourceFactoryRegistry().getExtensionToFactoryMap().containsKey("xxx"));
		assertInstanceOf(XMIResourceFactoryImpl.class, rs2.getResourceFactoryRegistry().getExtensionToFactoryMap().get("manual"));
	}

	/**
	 * @param resourceSet
	 * @throws IOException
	 */
	private void tryLoadAndSave(ResourceSet resourceSet) throws IOException {
		URI uri = URI.createURI("fooxx.manual");
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		Resource testSaveResource = resourceSet.createResource(uri);
		assertNotNull(testSaveResource);
		Foo p = ManualFactory.eINSTANCE.createFoo();
		p.setValue("Emil");
		testSaveResource.getContents().add(p);
		testSaveResource.save(baos, null);

		byte[] content = baos.toByteArray();
		ByteArrayInputStream bais = new ByteArrayInputStream(content);

		Resource testLoadResource = resourceSet.createResource(uri);
		assertEquals(0, testLoadResource.getContents().size());
		testLoadResource.load(bais, null);
		assertEquals(1, testLoadResource.getContents().size());
		Foo result = (Foo) testLoadResource.getContents().get(0);
		assertNotNull(result);
		assertEquals("Emil", result.getValue());
	}
}