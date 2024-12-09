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
package org.eclipse.fennec.emf.osgi.provider;

import java.util.Map;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.Resource.Factory;
import org.eclipse.emf.ecore.resource.Resource.Factory.Registry;
import org.eclipse.emf.ecore.resource.impl.ResourceFactoryRegistryImpl;

/**
 * A {@link Registry} that delegates to a parent {@link Registry} if nothing can
 * be found internally.
 * 
 * @author Juergen Albert
 * @since 25 Nov 2022
 */
public class DelegatingResourceFactoryRegistry extends ResourceFactoryRegistryImpl {

	private Registry delegate;

	/**
	 * Creates a new instance.
	 */
	public DelegatingResourceFactoryRegistry(Resource.Factory.Registry delegate) {
		this.delegate = delegate;
		protocolToFactoryMap = new DelegatingHashMap<String, Object>(delegate.getProtocolToFactoryMap());
		extensionToFactoryMap = new DelegatingHashMap<String, Object>(delegate.getExtensionToFactoryMap());
		contentTypeIdentifierToFactoryMap = new DelegatingHashMap<String, Object>(
				delegate.getContentTypeToFactoryMap());
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.emf.ecore.resource.impl.ResourceFactoryRegistryImpl#
	 * delegatedGetFactory(org.eclipse.emf.common.util.URI, java.lang.String)
	 */
	@Override
	protected Factory delegatedGetFactory(URI uri, String contentTypeIdentifier) {
		return delegate.getFactory(uri, contentTypeIdentifier);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.emf.ecore.resource.impl.ResourceFactoryRegistryImpl#
	 * getExtensionToFactoryMap()
	 */
	@Override
	public Map<String, Object> getExtensionToFactoryMap() {
		return extensionToFactoryMap;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.emf.ecore.resource.impl.ResourceFactoryRegistryImpl#
	 * getProtocolToFactoryMap()
	 */
	@Override
	public Map<String, Object> getProtocolToFactoryMap() {
		return protocolToFactoryMap;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.emf.ecore.resource.impl.ResourceFactoryRegistryImpl#
	 * getContentTypeToFactoryMap()
	 */
	@Override
	public Map<String, Object> getContentTypeToFactoryMap() {
		return contentTypeIdentifierToFactoryMap;
	}

	@Override
	public Resource.Factory getFactory(URI uri, String contentType) {
		return convert(getFactory(uri, protocolToFactoryMap, extensionToFactoryMap, contentTypeIdentifierToFactoryMap,
				contentType, true));
	}
}
