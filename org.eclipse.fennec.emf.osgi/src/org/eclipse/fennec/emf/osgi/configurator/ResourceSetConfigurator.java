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

import org.eclipse.emf.ecore.resource.ResourceSet;
import org.osgi.annotation.versioning.ProviderType;

/**
 * This OSGi service configures a resource set during construction. It is
 * expected that a system may define more than one configurator and that the
 * ResourceSet factory will apply all configurators.
 * 
 * @author bhunt, Mark Hoffmann
 */
@ProviderType
public interface ResourceSetConfigurator {
	
	public static final String EMF_CONFIGURATOR_NAME = "resourceSet";
	
	/**
	 * Configure a newly constructed {@link ResourceSet}. 
	 * @param resourceSet the {@link ResourceSet} to configure
	 */
	void configureResourceSet(ResourceSet resourceSet);
}
