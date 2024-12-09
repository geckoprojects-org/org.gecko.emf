/**
 * Copyright (c) 2012 - 2022 Data In Motion and others.
 * All rights reserved. 
 * 
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * Contributors:
 *     Data In Motion - initial API and implementation
 */
package org.eclipse.fennec.emf.osgi.configurator;

import org.eclipse.emf.ecore.EPackage;
import org.osgi.annotation.versioning.ProviderType;

/**
 * Configurator for the {@link EPackage} registry
 * @author Mark Hoffmann
 * @since 25.07.2017
 */
@ProviderType
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
