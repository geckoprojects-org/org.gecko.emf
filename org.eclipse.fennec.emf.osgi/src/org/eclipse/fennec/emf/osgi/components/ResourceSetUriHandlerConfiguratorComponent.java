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
package org.eclipse.fennec.emf.osgi.components;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;

import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.URIConverter;
import org.eclipse.emf.ecore.resource.URIHandler;
import org.eclipse.fennec.emf.osgi.UriHandlerProvider;
import org.eclipse.fennec.emf.osgi.UriMapProvider;
import org.eclipse.fennec.emf.osgi.configurator.ResourceSetConfigurator;
import org.osgi.annotation.versioning.ProviderType;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.annotations.ReferenceCardinality;
import org.osgi.service.component.annotations.ReferencePolicy;

/**
 * This implementation of the ResourceSetConfigurator service will attach
 * all currently bound URI handlers to the ResourceSet. This service is
 * intended to be used with the IResourceSetFactory service.
 * 
 * @author bhunt
 * 
 */
@Component(name="UriHandlerConfigurator", immediate=true, service=ResourceSetConfigurator.class)
@ProviderType
public class ResourceSetUriHandlerConfiguratorComponent implements ResourceSetConfigurator
{
	private Set<UriHandlerProvider> handlerProviders = new CopyOnWriteArraySet<>();
	private Set<UriMapProvider> mapProviders = new CopyOnWriteArraySet<>();

	public ResourceSetUriHandlerConfiguratorComponent() {
		handlerProviders.add(new RestUriHandlerProvider());
	}

	@Override
	public void configureResourceSet(ResourceSet resourceSet) {
		URIConverter uriConverter = resourceSet.getURIConverter();
		EList<URIHandler> uriHandlers = uriConverter.getURIHandlers();
		Map<URI, URI> uriMap = uriConverter.getURIMap();

		for (UriHandlerProvider handlerProvider : handlerProviders) {
			uriHandlers.add(0, handlerProvider.getURIHandler());
		}

		for (UriMapProvider mapProvider : mapProviders) {
			uriMap.putAll(mapProvider.getUriMap());
		}
	}

	/**
	 * Binds {@link UriHandlerProvider} via OSGi DS
	 * @param handlerProvider the provider to add
	 */
	@Reference(name="UriHandlerProvider", cardinality=ReferenceCardinality.MULTIPLE, policy=ReferencePolicy.DYNAMIC, unbind="removeUriHandlerProvider")
	public void addUriHandlerProvider(UriHandlerProvider handlerProvider) {
		handlerProviders.add(handlerProvider);
	}

	/**
	 * Removed {@link UriHandlerProvider} via OSGi DS
	 * @param handlerProvider the provider to be removed
	 */
	public void removeUriHandlerProvider(UriHandlerProvider handlerProvider) {
		handlerProviders.remove(handlerProvider);
	}

	/**
	 * Binds {@link UriMapProvider} via OSGi DS
	 * @param mapProvider the provider to set
	 */
	@Reference(name="UriMapProvider", cardinality=ReferenceCardinality.MULTIPLE, policy=ReferencePolicy.DYNAMIC, unbind="removeUriMapProvider")
	public void addUriMapProvider(UriMapProvider mapProvider) {
		mapProviders.add(mapProvider);
	}

	/**
	 * Unbinds the {@link UriMapProvider} via OSGi DS
	 * @param mapProvider the provider to be removed
	 */
	public void removeUriMapProvider(UriMapProvider mapProvider) 	{
		mapProviders.remove(mapProvider);
	}
	
}
