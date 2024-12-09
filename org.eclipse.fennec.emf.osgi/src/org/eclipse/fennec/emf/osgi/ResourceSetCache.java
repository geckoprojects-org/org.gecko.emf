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
package org.eclipse.fennec.emf.osgi;

import org.eclipse.emf.ecore.resource.ResourceSet;
import org.osgi.annotation.versioning.ProviderType;

/**
 * The ResourceSet from this service is not thread safe. This service
 * is intended to be used on the UI thread.
 * 
 * @author bhunt
 * @author Mark Hoffmann
 */
@ProviderType
public interface ResourceSetCache {
	
	/** Service property name to define a name for the instance */
	public static final String RESOURCE_SET_CACHE_NAME = "rs.cache.name"; 
	
	/**
	 * Returns the cached instance. If no instance exists, it will be created initially
	 * @return the {@link ResourceSet} instance
	 */
	public ResourceSet getResourceSet();
}