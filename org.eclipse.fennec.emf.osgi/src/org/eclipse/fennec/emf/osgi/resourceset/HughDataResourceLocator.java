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
package org.eclipse.fennec.emf.osgi.resourceset;

import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl.MappedResourceLocator;

/**
 * An implementation of the {@link MappedResourceLocator}, with some methods,
 * to manually clear the cache.
 * @author Mark Hoffmann
 * @since 27.09.2019
 */
public class HughDataResourceLocator extends MappedResourceLocator {

	/**
	 * Creates a new instance.
	 * @param resourceSet
	 */
	public HughDataResourceLocator(ResourceSetImpl resourceSet) {
		super(resourceSet);
	}
	
	/**
	 * Clears the {@link Resource} cache
	 */
	public void clear() {
		normalizationMap.clear();
		resourceMap.clear();
	}
	
	/* 
	 * (non-Javadoc)
	 * @see org.eclipse.emf.ecore.resource.impl.ResourceSetImpl.ResourceLocator#dispose()
	 */
	@Override
	public void dispose() {
		super.dispose();
		clear();
	}

}
