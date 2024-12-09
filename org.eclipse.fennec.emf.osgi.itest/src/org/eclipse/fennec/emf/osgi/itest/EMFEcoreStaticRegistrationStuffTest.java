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

import static org.assertj.core.api.Assertions.assertThat;

import java.io.IOException;

import org.eclipse.emf.ecore.EcorePackage;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.xmi.impl.EMOFResourceFactoryImpl;
import org.eclipse.emf.ecore.xmi.impl.EcoreResourceFactoryImpl;
import org.eclipse.emf.ecore.xmi.impl.XMIResourceFactoryImpl;
import org.eclipse.emf.ecore.xml.namespace.XMLNamespacePackage;
import org.eclipse.emf.ecore.xml.type.XMLTypePackage;
import org.eclipse.fennec.emf.osgi.constants.EMFNamespaces;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.osgi.test.common.annotation.InjectService;
import org.osgi.test.common.service.ServiceAware;
import org.osgi.test.junit5.context.BundleContextExtension;
import org.osgi.test.junit5.service.ServiceExtension;

/**
 * Tests the registration of the different EMF static Packages and factories
 * 
 * @author Juergen Albert
 * @since 09.09.2024
 */
@ExtendWith(BundleContextExtension.class)
@ExtendWith(ServiceExtension.class)
public class EMFEcoreStaticRegistrationStuffTest {

	@Test
	public void testEPackage_XMIResourceFactory(@InjectService ServiceAware<XMIResourceFactoryImpl> sa)
			throws IOException {
		assertThat(sa).isNotNull();
	}
	
	@Test
	public void testEPackage_EcoreResourceFactoryImpl(@InjectService ServiceAware<EcoreResourceFactoryImpl> sa)
			throws IOException {
		assertThat(sa).isNotNull();
	}
	
	@Test
	public void testEPackage_EMOFResourceFactoryImpl(@InjectService ServiceAware<EMOFResourceFactoryImpl> sa)
			throws IOException {
		assertThat(sa).isNotNull();
	}
	
	@Test
	public void testEPackage_BinaryFactory(@InjectService(filter = "(" + EMFNamespaces.EMF_MODEL_FILE_EXT + "=bin)") ServiceAware<Resource.Factory> sa)
			throws IOException {
		assertThat(sa).isNotNull();
	}
	
	@Test
	public void testEPackage_EcorePackage(@InjectService ServiceAware<EcorePackage> sa)
			throws IOException {
		assertThat(sa).isNotNull();
	}
	
	@Test
	public void testEPackage_XMLTypePackage(@InjectService ServiceAware<XMLTypePackage> sa)
			throws IOException {
		assertThat(sa).isNotNull();
	}
	
	@Test
	public void testEPackage_XMLNamespacePackage(@InjectService ServiceAware<XMLNamespacePackage> sa)
			throws IOException {
		assertThat(sa).isNotNull();
	}
}