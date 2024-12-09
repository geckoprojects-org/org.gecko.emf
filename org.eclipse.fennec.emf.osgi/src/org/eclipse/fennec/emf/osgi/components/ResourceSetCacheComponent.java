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

import java.util.concurrent.atomic.AtomicReference;

import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.fennec.emf.osgi.ResourceSetCache;
import org.eclipse.fennec.emf.osgi.ResourceSetFactory;
import org.osgi.annotation.bundle.Requirement;
import org.osgi.annotation.versioning.ProviderType;
import org.osgi.service.cm.ConfigurationConstants;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.ConfigurationPolicy;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.annotations.ReferenceCardinality;
import org.osgi.service.component.annotations.ReferencePolicy;
import org.osgi.namespace.implementation.ImplementationNamespace;
import org.osgi.resource.Namespace;

/**
 * A cache for a {@link ResourceSet}. Whereas the {@link ResourceSetFactory} only creates new instances of
 * {@link ResourceSet}'s, this component enables a cache for a certain resource set.
 * @author bhunt
 * @author Mark Hoffmann
 */
@Component(name="ResourceSetCache", service=ResourceSetCache.class, configurationPolicy=ConfigurationPolicy.REQUIRE)
@Requirement(namespace = ImplementationNamespace.IMPLEMENTATION_NAMESPACE, //
	name = ConfigurationConstants.CONFIGURATION_ADMIN_IMPLEMENTATION, //
	version = ConfigurationConstants.CONFIGURATION_ADMIN_SPECIFICATION_VERSION,
	resolution = Namespace.RESOLUTION_OPTIONAL)
@ProviderType
public class ResourceSetCacheComponent implements ResourceSetCache {
	
	private AtomicReference<ResourceSetFactory> resourceSetFactoryReference = new AtomicReference<>();
	private AtomicReference<ResourceSet> resourceSet = new AtomicReference<>();


	@Override
	public synchronized ResourceSet getResourceSet() {
		if (resourceSet.get() == null) {
			ResourceSetFactory resourceSetFactory = resourceSetFactoryReference.get();

			if (resourceSetFactory != null) {
				resourceSet.compareAndSet(null, resourceSetFactory.createResourceSet());
			}
		}
		return resourceSet.get();
	}

	/**
	 * Binds the {@link ResourceSetFactory} to this component
	 * @param resourceSetFactory
	 */
	@Reference(cardinality=ReferenceCardinality.MANDATORY, policy=ReferencePolicy.STATIC)
	public void bindResourceSetFactory(ResourceSetFactory resourceSetFactory) {
		resourceSetFactoryReference.set(resourceSetFactory);
	}

	/**
	 * Unbind the resource set factory instance on shutdown
	 * @param resourceSetFactory the resource set factory to be removed
	 */
	public void unbindResourceSetFactory(ResourceSetFactory resourceSetFactory) {
		resourceSetFactoryReference.compareAndSet(resourceSetFactory, null);
	}
}
