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

import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.fennec.emf.osgi.annotation.provide.EMFConfigurator;
import org.eclipse.fennec.emf.osgi.configurator.ResourceSetConfigurator;
import org.osgi.service.component.annotations.Component;

/**
 * 
 * @author mark
 * @since 17.12.2023
 */
@EMFConfigurator(
		configuratorName = "testResourceSet", 
		contentType = {"testRS", "testRS2"}, 
		feature = {"testRSFeature", "testRSPF"},
		protocol = {"rsp1", "rsp2"},
		fileExtension = {"fers1", "fers2"})
@Component(name = "TestResourceSetConfigurator", enabled = false)
public class TestResourceSetConfigurator implements ResourceSetConfigurator {

	
	/* 
	 * (non-Javadoc)
	 * @see org.eclipse.fennec.emf.osgi.configurator.ResourceSetConfigurator#configureResourceSet(org.eclipse.emf.ecore.resource.ResourceSet)
	 */
	@Override
	public void configureResourceSet(ResourceSet resourceSet) {
		// TODO Auto-generated method stub
		
	}

}
