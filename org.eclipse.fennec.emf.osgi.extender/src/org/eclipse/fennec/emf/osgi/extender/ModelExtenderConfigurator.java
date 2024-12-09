/**
 * Copyright (c) 2012 - 2022 Data In Motion and others.
 * All rights reserved. 
 * 
 * This program and the accompanying materials are made available under the terms of the 
 * Eclipse Public License v2.0 which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v20.html
 * 
 * Contributors:
 *     Data In Motion - initial API and implementation
 */
package org.eclipse.fennec.emf.osgi.extender;

import java.util.logging.Level;
import java.util.logging.Logger;

import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.resource.Resource.Factory.Registry;
import org.eclipse.emf.ecore.resource.impl.ResourceFactoryImpl;
import org.eclipse.emf.ecore.xmi.impl.XMIResourceFactoryImpl;
import org.eclipse.emf.ecore.xmi.impl.XMLResourceFactoryImpl;
import org.eclipse.fennec.emf.osgi.configurator.EPackageConfigurator;
import org.eclipse.fennec.emf.osgi.configurator.ResourceFactoryConfigurator;

/**
 * Implementation for Gecko EMF configurators, used by the extender.
 * @author Mark Hoffmann
 * @since 13.10.2022
 */
public class ModelExtenderConfigurator implements EPackageConfigurator, ResourceFactoryConfigurator {
	
	private static Logger logger = Logger.getLogger(ModelExtenderConfigurator.class.getName());
	private final EPackage ePackage;
	private final String contentTypeIdentifier;
	private final String fileExtension;

	/**
	 * Creates a new instance.
	 */
	public ModelExtenderConfigurator(EPackage ePackage, String contentTypeIdentifier, String fileExtension) {
		this.ePackage = ePackage;
		this.contentTypeIdentifier = contentTypeIdentifier;
		this.fileExtension = fileExtension;
	}

	/* 
	 * (non-Javadoc)
	 * @see org.gecko.emf.osgi.ResourceFactoryConfigurator#configureResourceFactory(org.eclipse.emf.ecore.resource.Resource.Factory.Registry)
	 */
	@Override
	public void configureResourceFactory(Registry registry) {
		ResourceFactoryImpl factory = new XMIResourceFactoryImpl();
		if (contentTypeIdentifier != null) {
			if (contentTypeIdentifier.equalsIgnoreCase("XML")) {
				factory = new XMLResourceFactoryImpl();
			}
			registry.getContentTypeToFactoryMap().put(contentTypeIdentifier, factory);
		}
		if (fileExtension != null) {
			registry.getExtensionToFactoryMap().put(fileExtension, factory);
		}
	}

	/* 
	 * (non-Javadoc)
	 * @see org.gecko.emf.osgi.ResourceFactoryConfigurator#unconfigureResourceFactory(org.eclipse.emf.ecore.resource.Resource.Factory.Registry)
	 */
	@Override
	public void unconfigureResourceFactory(Registry registry) {
		if (contentTypeIdentifier != null) {
			registry.getExtensionToFactoryMap().remove(contentTypeIdentifier);
		}
		if (fileExtension != null) {
			registry.getExtensionToFactoryMap().remove(fileExtension);
		}
	}

	/* 
	 * (non-Javadoc)
	 * @see org.gecko.emf.osgi.EPackageConfigurator#configureEPackage(org.eclipse.emf.ecore.EPackage.Registry)
	 */
	@Override
	public void configureEPackage(org.eclipse.emf.ecore.EPackage.Registry registry) {
		if (ePackage != null) {
			registry.put(ePackage.getNsURI(), ePackage);
		} else {
			logger.log(Level.SEVERE, ()->"Error registering a NULL package, that should never happen");
		}
	}

	/* 
	 * (non-Javadoc)
	 * @see org.gecko.emf.osgi.EPackageConfigurator#unconfigureEPackage(org.eclipse.emf.ecore.EPackage.Registry)
	 */
	@Override
	public void unconfigureEPackage(org.eclipse.emf.ecore.EPackage.Registry registry) {
		if (ePackage != null) {
			registry.remove(ePackage.getNsURI());
		} else {
			logger.log(Level.SEVERE, ()->"Error un-registering a NULL package, that should never happen");
		}
	}

}
