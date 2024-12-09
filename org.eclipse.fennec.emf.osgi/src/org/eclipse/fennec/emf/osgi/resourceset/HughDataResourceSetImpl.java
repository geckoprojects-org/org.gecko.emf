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

import java.util.concurrent.atomic.AtomicBoolean;

import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.eclipse.fennec.emf.osgi.HughDataResourceSet;

/**
 * An extended {@link ResourceSet}, that works with large data sets as well, without blocking
 * for along time, when {@link ResourceSet#getResources#clear} is called.
 * @author Mark Hoffmann
 * @since 27.09.2019
 */
public class HughDataResourceSetImpl extends ResourceSetImpl implements HughDataResourceSet{
	
	private final AtomicBoolean suppressNotification = new AtomicBoolean(false);
	private final AtomicBoolean internalSuppressNotification = new AtomicBoolean(false);
	private final ResourceSet resourceSet;
	private boolean useLocator = false;
	
	/**
	 * Creates a new instance.
	 */
	public HughDataResourceSetImpl(ResourceSet resourceSet) {
		this.resourceSet = resourceSet;
		eAdapters().addAll(resourceSet.eAdapters());
		getAdapterFactories().addAll(resourceSet.getAdapterFactories());
		getLoadOptions().putAll(resourceSet.getLoadOptions());
		getPackageRegistry().putAll(resourceSet.getPackageRegistry());
		resourceFactoryRegistry = resourceSet.getResourceFactoryRegistry();
		uriConverter = resourceSet.getURIConverter();
		setUseResourceLocator(true);
	}
	
	/**
	 * Creates a new instance.
	 */
	public HughDataResourceSetImpl() {
		resourceSet = null;
		setUseResourceLocator(true);
	}
	

	@Override
	public boolean eNotificationRequired() {
		if (isSuppressNotification() || internalSuppressNotification.get()) {
			return false;
		}
		return super.eNotificationRequired();
	}
	

	@Override
	public EList<Resource> getResources() {
	    if (resources == null)
	    {
	      resources = new GeckoResourceEList<Resource>();
	      if (resourceSet != null) {
	    	  resources.addAll(resourceSet.getResources());
	      }
	    }
	    return resources;
	}

	class GeckoResourceEList <T> extends ResourceSetImpl.ResourcesEList<Resource> {
		/** serialVersionUID */
		private static final long serialVersionUID = 1L;


		@Override
		protected boolean isNotificationRequired() {
			return HughDataResourceSetImpl.this.eNotificationRequired();
		}
		

		@Override
		protected boolean hasInverse() {
			if (isSuppressNotification() || internalSuppressNotification.get()) {
				return false;
			}
			return super.hasInverse();
		}
		
		/* 
		 * (non-Javadoc)
		 * @see org.eclipse.emf.common.notify.impl.NotifyingListImpl#clear()
		 */
		@Override
		public void clear() {
			if (isUseResourceLocator()) {
				internalSuppressNotification.set(true);
				super.clear();
				((HughDataResourceLocator)resourceLocator).clear();
				internalSuppressNotification.set(false);
			} else {
				super.clear();
			}
		}
	}


	@Override
	public void setUseResourceLocator(boolean useLocator) {
		this.useLocator = useLocator;
		updateResourceLocator();
	}


	@Override
	public boolean isUseResourceLocator() {
		return useLocator;
	}


	@Override
	public void setSuppressNotification(boolean suppressNotification) {
		this.suppressNotification.set(suppressNotification);
	}


	@Override
	public boolean isSuppressNotification() {
		return this.suppressNotification.get();
	}

	/**
	 * Updates the current state of the resource locator
	 */
	private void updateResourceLocator() {
		if (isUseResourceLocator()) {
			if (resourceLocator == null) {
				new HughDataResourceLocator(this);
			} else {
				if (!(resourceLocator instanceof HughDataResourceLocator)) {
					throw new IllegalStateException("There is already a ResourceLocator registered. Handling within this ResourceSet cannot work with this");
				}
			}
		} else {
			if (resourceLocator instanceof HughDataResourceLocator) {
				((HughDataResourceLocator)resourceLocator).dispose();
				resourceLocator = null;
			}
		}
	}
	
}
