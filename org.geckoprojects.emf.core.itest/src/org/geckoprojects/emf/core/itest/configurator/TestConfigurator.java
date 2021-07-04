/**
 * Copyright (c) 2012 - 2018 Data In Motion and others.
 * All rights reserved. 
 * 
 * This program and the accompanying materials are made available under the terms of the 
 * Eclipse Public License v1.0 which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     Data In Motion - initial API and implementation
 */
package org.geckoprojects.emf.core.itest.configurator;

import org.eclipse.emf.ecore.resource.Resource.Factory.Registry;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.geckoprojects.emf.core.ResourceFactoryConfigurator;
import org.geckoprojects.emf.core.ResourceSetConfigurator;

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
