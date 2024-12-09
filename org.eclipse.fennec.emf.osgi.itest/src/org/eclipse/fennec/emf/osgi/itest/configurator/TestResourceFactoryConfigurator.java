/**
 * Copyright (c) 2012 - 2023 Data In Motion and others.
 * All rights reserved. 
 * 
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 * 
 * Contributors:
 *     Data In Motion - initial API and implementation
 */
package org.eclipse.fennec.emf.osgi.itest.configurator;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.Resource.Factory.Registry;
import org.eclipse.emf.ecore.resource.impl.ResourceFactoryImpl;
import org.eclipse.fennec.emf.osgi.annotation.ConfiguratorType;
import org.eclipse.fennec.emf.osgi.annotation.provide.EMFConfigurator;
import org.eclipse.fennec.emf.osgi.configurator.ResourceFactoryConfigurator;
import org.osgi.service.component.annotations.Component;

/**
 * 
 * @author mark
 * @since 17.12.2023
 */
@EMFConfigurator(
		configuratorName = "testResourceFactory", 
		configuratorType = ConfiguratorType.RESOURCE_FACTORY, 
		contentType = {"test/123", "test234"}, 
		feature = {"test123", "test234"},
		protocol = {"p123", "p234"},
		fileExtension = {"fe123", "fe234"})
@Component(name = "TestResourceFactoryConfigurator", enabled = false)
public class TestResourceFactoryConfigurator implements ResourceFactoryConfigurator {

	private final ResourceFactoryImpl resourceFactory = new ResourceFactoryImpl() {
		/* 
		 * (non-Javadoc)
		 * @see org.eclipse.emf.ecore.resource.impl.ResourceFactoryImpl#createResource(org.eclipse.emf.common.util.URI)
		 */
		@Override
		public Resource createResource(URI uri) {
			
			return new TestResource(uri);
		}
	};
	
	
	/* 
	 * (non-Javadoc)
	 * @see org.eclipse.fennec.emf.osgi.configurator.ResourceFactoryConfigurator#configureResourceFactory(org.eclipse.emf.ecore.resource.Resource.Factory.Registry)
	 */
	@Override
	public void configureResourceFactory(Registry registry) {
		registry.getContentTypeToFactoryMap().put("test/123", resourceFactory);
		registry.getContentTypeToFactoryMap().put("test234", resourceFactory);
	}


	/* 
	 * (non-Javadoc)
	 * @see org.eclipse.fennec.emf.osgi.configurator.ResourceFactoryConfigurator#unconfigureResourceFactory(org.eclipse.emf.ecore.resource.Resource.Factory.Registry)
	 */
	@Override
	public void unconfigureResourceFactory(Registry registry) {
		registry.getContentTypeToFactoryMap().remove("test/123");
		registry.getContentTypeToFactoryMap().remove("test234");
	}

}
