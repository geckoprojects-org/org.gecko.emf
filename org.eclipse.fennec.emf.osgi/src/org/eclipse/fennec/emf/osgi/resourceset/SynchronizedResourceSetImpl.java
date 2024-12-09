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

import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import org.eclipse.emf.common.notify.NotificationChain;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.URIConverter;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.eclipse.emf.ecore.util.DelegatingNotifyingInternalEListImpl;
import org.eclipse.emf.ecore.util.InternalEList;
import org.eclipse.fennec.emf.osgi.Detachable;

/**
 * Resource set with synchronized access especially for resources. It has a read write lock for
 * securing write and read access to the resources list
 * @author Mark Hoffmann
 * @since 10.04.2017
 */
public class SynchronizedResourceSetImpl extends ResourceSetImpl implements Detachable {

	protected ReentrantReadWriteLock rwLock = new ReentrantReadWriteLock();

	/**
	 * Creates a new instance.
	 */
	public SynchronizedResourceSetImpl() {
		new ResourceSetImpl.MappedResourceLocator(this);
	}
	
	/**
	 * The contained resources.
	 * @see #getResources
	 */
	protected EList<Resource> syncResources;

	@Override
	public EList<Resource> getResources() {
		if (syncResources == null) 
		{
			syncResources = new CopyOnWriteResourcesEList<>();
		}
		return syncResources;
	}

	@Override
	public Resource getResource(URI uri, boolean loadOnDemand) {
		if (resourceLocator != null)
		{
			synchronized (resourceLocator) {
				return resourceLocator.getResource(uri, loadOnDemand);
			}
		}

		Map<URI, Resource> map = getURIResourceMap();
		if (map != null)
		{
			Resource resource = null;
			synchronized (map) {
				resource = map.get(uri);
			}
			if (resource != null)
			{
				if (loadOnDemand && !resource.isLoaded())
				{
					demandLoadHelper(resource);
				}
				return resource;
			}
		}

		List<Resource> resourcesList = internalGetResources();
		URIConverter theURIConverter = getURIConverter();
		URI normalizedURI = null;
		synchronized (theURIConverter) {
			normalizedURI = theURIConverter.normalize(uri);
		}
		synchronized (resourcesList) {
			for (Resource resource : resourcesList)
			{
				if (resource == null) {
					continue;
				}
				URI resourceUri = null;
				synchronized (resource) {
					resourceUri = resource.getURI();
				}
				boolean normalized = false;
				if (resourceUri != null) {
					synchronized (theURIConverter) {
						normalized = theURIConverter.normalize(resourceUri).equals(normalizedURI);
					}
				}
				if (normalized)
				{
					if (loadOnDemand && !resource.isLoaded())
					{
						demandLoadHelper(resource);
					}

					if (map != null)
					{
						synchronized (map) {
							map.put(uri, resource);
						}
					}
					return resource;
				}
			}
		}

		Resource delegatedResource = delegatedGetResource(uri, loadOnDemand);
		if (delegatedResource != null)
		{
			if (map != null)
			{
				map.put(uri, delegatedResource);
			}
			return delegatedResource;
		}

		if (loadOnDemand)
		{
			Resource resource = demandCreateResource(uri);
			if (resource == null)
			{
				throw new RuntimeException("Cannot create a resource for '" + uri + "'; a registered resource factory is needed");
			}

			demandLoadHelper(resource);

			if (map != null)
			{
				map.put(uri, resource);
			}
			return resource;
		}

		return null;
	}

	@Override
	public Resource createResource(URI uri, String contentType) {
		Resource.Factory resourceFactory = getResourceFactoryRegistry().getFactory(uri, contentType);
		if (resourceFactory != null)
		{
			Resource result = null;
			synchronized (resourceFactory) {
				result = resourceFactory.createResource(uri);
			}
			rwLock.writeLock().lock();
			try {
				EList<Resource> resources = getResources();
				synchronized (resources) {
					resources.add(result);
				}
			} finally {
				rwLock.writeLock().unlock();
			}
			return result;
		}
		else
		{
			return null;
		}
	}

	@Override
	public void detachFromResource(EObject object) {
		Resource resource = object.eResource();
		if (resource == null) {
			return;
		}
		synchronized (resource) {
			resource.getContents().remove(object);
		}
	}


	@Override
	public void detachFromAll(EObject object) {
		detachFromResource(object);
		Resource resource = object.eResource();
		if (resource == null) {
			return;
		}
		rwLock.writeLock().lock();
		try {
			EList<Resource> resources = getResources();
			synchronized (resources) {
				resources.remove(resource);
			}
		} finally {
			rwLock.writeLock().unlock();
		}
	}

	/**
	 * Helper method to enable a thread safe way to return a list of resources
	 * @return the list of resources or an empty list
	 */
	private List<Resource> internalGetResources() {
		rwLock.readLock().lock();
		try {
			EList<Resource> r = getResources();
			synchronized (r) {
				int size = r.size();
				Resource[] ra = r.toArray(new Resource[size]); 
				return new CopyOnWriteArrayList<>(ra);
			}
		} finally {
			rwLock.readLock().unlock();
		}
	}

	/**
	 * A notifying list implementation for supporting {@link ResourceSet#getResources}.
	 * Contains a CopyOnWriteList as delegates
	 * Code is copied from ResourceSetImpl
	 */
	protected class CopyOnWriteResourcesEList<E extends Object & Resource> extends DelegatingNotifyingInternalEListImpl<E> implements InternalEList<E>
	{
		private static final long serialVersionUID = 1L;
		private final List<E> delegate = new CopyOnWriteArrayList<>();

		public CopyOnWriteResourcesEList()
		{
			super();
		}

		public CopyOnWriteResourcesEList(Collection<? extends E> collection)
		{
			super(collection);
		}

		@Override
		protected boolean isNotificationRequired()
		{
			return SynchronizedResourceSetImpl.this.eNotificationRequired();
		}

		@Override
		public Object getNotifier()
		{
			return SynchronizedResourceSetImpl.this;
		}

		@Override
		public int getFeatureID()
		{
			return RESOURCE_SET__RESOURCES;
		}

		@Override
		protected boolean useEquals()
		{
			return false;
		}

		@Override
		protected boolean hasInverse()
		{
			return true;
		}

		@Override
		protected boolean isUnique()
		{
			return true;
		}

		@Override
		protected NotificationChain inverseAdd(E object, NotificationChain notifications)
		{
			Resource.Internal resource = (Resource.Internal)object;
			return resource.basicSetResourceSet(SynchronizedResourceSetImpl.this, notifications);
		}

		@Override
		protected NotificationChain inverseRemove(E object, NotificationChain notifications)
		{
			Resource.Internal resource = (Resource.Internal)object;
			Map<URI, Resource> map = getURIResourceMap();
			if (map != null)
			{
				for (Iterator<Resource> i = map.values().iterator(); i.hasNext();)
				{
					if (resource == i.next())
					{
						i.remove();
					}
				}
			}
			return resource.basicSetResourceSet(null, notifications);
		}

		@Override
		public boolean contains(Object object)
		{
			return object instanceof Resource && ((Resource)object).getResourceSet() == SynchronizedResourceSetImpl.this;
		}


		@Override
		protected List<E> delegateList() {
			return delegate;
		}
	}

}
