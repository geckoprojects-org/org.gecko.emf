/**
 * Copyright (c) 2012 - 2017 Data In Motion and others.
 * All rights reserved. 
 * 
 * This program and the accompanying materials are made available under the terms of the 
 * Eclipse Public License v1.0 which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     Data In Motion - initial API and implementation
 */
package org.geckoprojects.emf.core.api;

import org.eclipse.emf.ecore.EPackage;

/**
 * Configurator for the {@link EPackage} registry
 * @author Mark Hoffmann
 * @since 25.07.2017
 */
public interface EPackageConfigurator {
	
	public static final String EMF_CONFIGURATOR_NAME = "epackage";
	
	/**
	 * Configures the {@link EPackage} registry
	 * @param registry the registry to be configured
	 */
	public void configureEPackage(EPackage.Registry registry);
	
	/**
	 * Un-configure {@link EPackage} registry
	 * @param registry the registry to un-configure
	 */
	public void unconfigureEPackage(EPackage.Registry registry);

}