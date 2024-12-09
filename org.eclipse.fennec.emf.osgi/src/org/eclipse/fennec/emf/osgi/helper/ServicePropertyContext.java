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
package org.eclipse.fennec.emf.osgi.helper;

import static java.util.Objects.requireNonNull;

import java.util.Dictionary;
import java.util.Map;

/**
 * Class to handle the different service properties. It allows to have  
 * @author Mark Hoffmann
 * @since 09.11.2023
 */
public interface ServicePropertyContext {

	/**
	 * Updates the service properties for a resource factory, configurator or service. The service properties
	 * are expected to contain a service.id entry with a long value.
	 * @param serviceProperties the service properties
	 * @throws IllegalStateException thrown, if the serviceProperties do not contain a service.id entry
	 */
	void updateServiceProperties(Map<String, Object> serviceProperties);

	/**
	 * Adds a sub-context for a given service id contained in the sub context properties.
	 * The service properties are expected to contain a service.id entry with a long value. If this is not the case 
	 * the method throws an {@link IllegalArgumentException}
	 * @param subContextProperties the service property map of the sub-context
	 * @return the {@link ServicePropertyContext} instance
	 * @throws IllegalStateException thrown, if the serviceProperties do not contain a service.id entry
	 */
	ServicePropertyContext addSubContext(Map<String, Object> subContextProperties);
	
	/**
	 * Removes the sub-context for the given service properties and returns the removed value or <code>null</code>. 
	 * The service properties are expected to contain a service.id entry with a long value. 
	 * If this is not the case the method throws an {@link IllegalArgumentException}
	 * @param subContextProperties the service properties of the sub-context to be removed
	 * @return the {@link ServicePropertyContext} instance, that has been removed or <code>null</code>
	 * @throws IllegalStateException thrown, if the serviceProperties do not contain a service.id entry
	 */
	ServicePropertyContext removeSubContext(Map<String, Object> subContextProperties);
	
	/**
	 * Creates a {@link Map} for the stored properties
	 * @param merged, set to <code>true</code>, to get the merged properties of this context and all registered sub-contexts
	 * @return a {@link Map} for the stored properties
	 */
	Map<String, Object> getProperties(boolean merged);

	/**
	 * Creates a {@link Dictionary} for the stored properties
	 * @param merged, set to <code>true</code>, to get the merged properties of this context and all registered sub-contexts
	 * @return a {@link Dictionary} for the stored properties
	 */
	Dictionary<String, Object> getDictionary(boolean merged);
	
	/**
	 * Creates a {@link ServicePropertyContext} instance. 
	 * @return {@link ServicePropertyContext} instance
	 */
	static ServicePropertyContext create() {
		return new ServicePropertyContextImpl();
	}

	/**
	 * Creates a {@link ServicePropertyContext} out of a property map. The properties must contain a entry for service.id with value of type Long.
	 * @param contextProperties the property map, must not be <code>null</code>
	 * @return {@link ServicePropertyContext} instance
	 * @throws IllegalStateException thrown, if the serviceProperties do not contain a service.id entry
	 */
	static ServicePropertyContext create(Map<String, Object> contextProperties) {
		requireNonNull(contextProperties);
		ServicePropertyContext ctx = new ServicePropertyContextImpl();
		ctx.updateServiceProperties(contextProperties);
		return ctx;
	}
	
	/**
	 * Merge the given {@link ServicePropertyContext} with this instance
	 * @param toMerge the contexts to be merged
	 * @return a new merged instance
	 */
	ServicePropertyContext merge(ServicePropertyContext ...toMerge);

}