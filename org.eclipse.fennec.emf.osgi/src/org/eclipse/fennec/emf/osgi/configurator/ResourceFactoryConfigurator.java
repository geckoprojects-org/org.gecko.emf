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
 *     Data In Motion - initial API and implementation
 */
package org.eclipse.fennec.emf.osgi.configurator;

import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.Resource.Factory.Registry;
import org.osgi.annotation.versioning.ProviderType;

/**
 * Configurator for the {@link Registry} registry
 * @author Mark Hoffmann
 * @since 25.07.2017
 */
@ProviderType
public interface ResourceFactoryConfigurator {
	
	/**
	 * Configures the {@link Registry} registry
	 * @param registry the registry to be configured
	 */
	public void configureResourceFactory(Resource.Factory.Registry registry);
	
	/**
	 * Unconfigure {@link Registry} registry
	 * @param registry the registry to unconfigure
	 */
	public void unconfigureResourceFactory(Resource.Factory.Registry registry);

}
