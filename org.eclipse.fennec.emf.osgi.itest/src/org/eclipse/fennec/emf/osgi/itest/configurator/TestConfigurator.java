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
package org.eclipse.fennec.emf.osgi.itest.configurator;

import org.eclipse.emf.ecore.resource.Resource.Factory.Registry;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.fennec.emf.osgi.configurator.ResourceFactoryConfigurator;
import org.eclipse.fennec.emf.osgi.configurator.ResourceSetConfigurator;

/**
 * 
 * @author mark
 * @since 03.03.2018
 */
public class TestConfigurator implements ResourceSetConfigurator, ResourceFactoryConfigurator {

	@Override
	public void configureResourceSet(ResourceSet resourceSet) {
		// TODO Auto-generated method stub

	}

	@Override
	public void configureResourceFactory(Registry registry) {
		// TODO Auto-generated method stub

	}

	@Override
	public void unconfigureResourceFactory(Registry registry) {
		// TODO Auto-generated method stub

	}

}
