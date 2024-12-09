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
package org.eclipse.fennec.emf.osgi.example.model.manual.configuration;

import org.eclipse.emf.ecore.resource.Resource.Factory.Registry;
import org.eclipse.fennec.emf.osgi.configurator.EPackageConfigurator;
import org.eclipse.fennec.emf.osgi.configurator.ResourceFactoryConfigurator;
import org.eclipse.fennec.emf.osgi.example.model.manual.ManualPackage;
import org.eclipse.fennec.emf.osgi.example.model.manual.util.ManualResourceFactoryImpl;

public class ManualPackageConfigurator implements EPackageConfigurator, ResourceFactoryConfigurator {

	/* 
	 * (non-Javadoc)
	 * @see org.gecko.emf.osgi.osgi.ResourceFactoryConfigurator#configureResourceFactory(org.eclipse.emf.ecore.resource.Resource.Factory.Registry)
	 */
	@Override
	public void configureResourceFactory(Registry registry) {
		registry.getExtensionToFactoryMap().put("manual", new ManualResourceFactoryImpl());
		registry.getContentTypeToFactoryMap().put("manual#1.0", new ManualResourceFactoryImpl());
	}

	/* 
	 * (non-Javadoc)
	 * @see org.gecko.emf.osgi.osgi.ResourceFactoryConfigurator#unconfigureResourceFactory(org.eclipse.emf.ecore.resource.Resource.Factory.Registry)
	 */
	@Override
	public void unconfigureResourceFactory(Registry registry) {
		registry.getExtensionToFactoryMap().remove("manual");
		registry.getContentTypeToFactoryMap().remove("manual#1.0");
	}

	/* 
	 * (non-Javadoc)
	 * @see org.gecko.emf.osgi.osgi.EPackageRegistryConfigurator#configureEPackage(org.eclipse.emf.ecore.EPackage.Registry)
	 */
	@Override
	public void configureEPackage(org.eclipse.emf.ecore.EPackage.Registry registry) {
		registry.put(ManualPackage.eNS_URI, ManualPackage.eINSTANCE);
	}

	/* 
	 * (non-Javadoc)
	 * @see org.gecko.emf.osgi.osgi.EPackageRegistryConfigurator#unconfigureEPackage(org.eclipse.emf.ecore.EPackage.Registry)
	 */
	@Override
	public void unconfigureEPackage(org.eclipse.emf.ecore.EPackage.Registry registry) {
		registry.remove(ManualPackage.eNS_URI);
	}

}
