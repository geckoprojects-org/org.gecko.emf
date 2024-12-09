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

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * A {@link Map} implementation that holds an internal {@link HashMap} to store its data together with a delegate.
 * All reading operations will delegate on the delegate instance if no results are found internally.
 * 
 * @author Juergen Albert
 * @since 25 Nov 2022
 */
public class DelegatingHashMap<K, V> implements Map<K, V> {

	private Map<K, V> delegate;
	private Map<K, V> main;

	/**
	 * Creates a new instance.
	 */
	public DelegatingHashMap(Map<K,V> delegate) {
		this.main = new HashMap<>();
		this.delegate = delegate;
	}
	
	/* 
	 * (non-Javadoc)
	 * @see java.util.HashMap#size()
	 */
	
	@Override
	public int size() {
		return main.size() + delegate.size();
	}
	
	/* 
	 * (non-Javadoc)
	 * @see java.util.HashMap#isEmpty()
	 */
	@Override
	public boolean isEmpty() {
		return main.isEmpty() && delegate.isEmpty();
	}
	
	/* 
	 * (non-Javadoc)
	 * @see java.util.HashMap#containsKey(java.lang.Object)
	 */
	@Override
	public boolean containsKey(Object key) {
		return main.containsKey(key) || delegate.containsKey(key) ;
	}
	
	/* 
	 * (non-Javadoc)
	 * @see java.util.HashMap#containsValue(java.lang.Object)
	 */
	@Override
	public boolean containsValue(Object value) {
		return main.containsValue(value) || delegate.containsValue(value);
	}
	
	/* 
	 * (non-Javadoc)
	 * @see java.util.HashMap#entrySet()
	 */
	@Override
	public Set<Entry<K, V>> entrySet() {
		Set<Entry<K, V>> set = new HashSet<>(delegate.entrySet());
		set.addAll(main.entrySet());
		return set;
	}
	
	/* 
	 * (non-Javadoc)
	 * @see java.util.HashMap#get(java.lang.Object)
	 */
	@Override
	public V get(Object key) {
		V result = main.get(key);
		if(result == null) {
			return delegate.get(key);
		}
		return result;
	}
	
	/* 
	 * (non-Javadoc)
	 * @see java.util.HashMap#values()
	 */
	@Override
	public Collection<V> values() {
		List<V> values = new ArrayList<>(delegate.values());
		values.addAll(main.values());
		return values;
	}
	
	/* 
	 * (non-Javadoc)
	 * @see java.util.HashMap#keySet()
	 */
	@Override
	public Set<K> keySet() {
		Set<K> keys = new HashSet<>(delegate.keySet());
		keys.addAll(main.keySet());
		return keys;
	}

	/* 
	 * (non-Javadoc)
	 * @see java.util.Map#put(java.lang.Object, java.lang.Object)
	 */
	@Override
	public V put(K key, V value) {
		return main.put(key, value);
	}

	/* 
	 * (non-Javadoc)
	 * @see java.util.Map#remove(java.lang.Object)
	 */
	@Override
	public V remove(Object key) {
		return main.remove(key);
	}

	/* 
	 * (non-Javadoc)
	 * @see java.util.Map#putAll(java.util.Map)
	 */
	@Override
	public void putAll(Map<? extends K, ? extends V> m) {
		main.putAll(m);
	}

	/* 
	 * (non-Javadoc)
	 * @see java.util.Map#clear()
	 */
	@Override
	public void clear() {
		main.clear();
	}
}
