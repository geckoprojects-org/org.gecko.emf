/**
 * Copyright (c) 2012 - 2024 Data In Motion and others.
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

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.function.BiConsumer;

import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.impl.EPackageRegistryImpl;

/**
 * Fully {@link Map} implementing {@link EPackageRegistryImpl}
 * @author Mark Hoffmann
 * @since 09.04.2024
 */
public class DelegatingEPackageRegistry extends EPackageRegistryImpl {
	
	/** serialVersionUID */
	private static final long serialVersionUID = 1L;
	
	/**
	 * Creates a new instance.
	 */
	public DelegatingEPackageRegistry() {
		super(new EPackageRegistryImpl());
	}
	
	/**
	 * Creates a new instance.
	 */
	public DelegatingEPackageRegistry(EPackage.Registry delegate) {
		super(delegate);
	}
	
	/* 
	 * (non-Javadoc)
	 * @see java.util.HashMap#values()
	 */
	@Override
	public Collection<Object> values() {
		Set<Object> objects = new HashSet<>(delegateRegistry.values());
		objects.addAll(super.values());
		return Collections.unmodifiableCollection(objects);
	}
	
	/* 
	 * (non-Javadoc)
	 * @see java.util.HashMap#keySet()
	 */
	@Override
	public Set<String> keySet() {
		Set<String> objects = new HashSet<>(delegateRegistry.keySet());
		objects.addAll(super.keySet());
		return Collections.unmodifiableSet(objects);
	}
	
	/* 
	 * (non-Javadoc)
	 * @see java.util.HashMap#size()
	 */
	@Override
	public int size() {
		return super.size() + delegateRegistry.size();
	}
	
	/* 
	 * (non-Javadoc)
	 * @see java.util.HashMap#get(java.lang.Object)
	 */
	@Override
	public Object get(Object key) {
		Object object = super.get(key);
		if (object != null) {
			return object;
		}
		return delegateRegistry.get(key);
	}
	
	/* 
	 * (non-Javadoc)
	 * @see java.util.HashMap#isEmpty()
	 */
	@Override
	public boolean isEmpty() {
		return super.isEmpty() && delegateRegistry.isEmpty();
	}
	
	/* 
	 * (non-Javadoc)
	 * @see java.util.HashMap#containsValue(java.lang.Object)
	 */
	@Override
	public boolean containsValue(Object value) {
		return super.containsValue(value) || delegateRegistry.containsValue(value);
	}
	
	/* 
	 * (non-Javadoc)
	 * @see org.eclipse.emf.ecore.impl.EPackageRegistryImpl#containsKey(java.lang.Object)
	 */
	@Override
	public boolean containsKey(Object key) {
		return super.containsKey(key) || delegateRegistry.containsKey(key);
	}
	
	/* 
	 * (non-Javadoc)
	 * @see java.util.HashMap#entrySet()
	 */
	@Override
	public Set<Entry<String, Object>> entrySet() {
		Set<Entry<String,Object>> entrySet = new HashSet<>(delegateRegistry.entrySet());
		entrySet.addAll(super.entrySet());
		return Collections.unmodifiableSet(entrySet);
	}
	
	/* 
	 * (non-Javadoc)
	 * @see java.util.HashMap#forEach(java.util.function.BiConsumer)
	 */
	@Override
	public void forEach(BiConsumer<? super String, ? super Object> action) {
		super.forEach(action);
		delegateRegistry.forEach(action);
	}
	
}
