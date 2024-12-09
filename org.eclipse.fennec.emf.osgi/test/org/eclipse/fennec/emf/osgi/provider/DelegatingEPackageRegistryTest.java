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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.eclipse.emf.ecore.EcorePackage;
import org.eclipse.emf.ecore.impl.EPackageRegistryImpl;
import org.eclipse.fennec.emf.osgi.provider.DelegatingEPackageRegistry;
import org.eclipse.fennec.emf.osgi.provider.DelegatingHashMap;
import org.junit.jupiter.api.Test;

/**
 * Tests {@link DelegatingHashMap}
 * @author Juergen Albert
 * @since 2 Dec 2022
 */
class DelegatingEPackageRegistryTest {

	/**
	 * Test method for {@link org.eclipse.fennec.emf.osgi.provider.DelegatingHashMap#size()}.
	 */
	@Test
	void testSize() {
		EPackageRegistryImpl d = new EPackageRegistryImpl();
		DelegatingEPackageRegistry r = new DelegatingEPackageRegistry(d);
		assertEquals(0, r.size());
		
		d.put("test", EcorePackage.eINSTANCE);
		assertEquals(1, d.size());
		assertEquals(1, r.size());
		
		r.put("test2", EcorePackage.eINSTANCE);
		assertEquals(2, r.size());
		d.clear();
		assertEquals(1, r.size());
		assertEquals(0, d.size());
	}

	/**
	 * Test method for {@link org.eclipse.fennec.emf.osgi.provider.DelegatingHashMap#isEmpty()}.
	 */
	@Test
	void testIsEmpty() {
		EPackageRegistryImpl d = new EPackageRegistryImpl();
		DelegatingEPackageRegistry r = new DelegatingEPackageRegistry(d);
		
		assertTrue(r.isEmpty());
		
		d.put("test", EcorePackage.eINSTANCE);
		assertFalse(r.isEmpty());
		
		r.put("test2", EcorePackage.eINSTANCE);
		assertFalse(d.isEmpty());

		d.clear();
		assertFalse(r.isEmpty());

		r.clear();
		assertTrue(r.isEmpty());
		
	}

	/**
	 * Test method for {@link org.eclipse.fennec.emf.osgi.provider.DelegatingHashMap#containsKey(java.lang.Object)}.
	 */
	@Test
	void testContainsKey() {
		EPackageRegistryImpl d = new EPackageRegistryImpl();
		DelegatingEPackageRegistry r = new DelegatingEPackageRegistry(d);
		
		d.put("1", "test");
		
		assertEquals("test", r.get("1"));
		
		r.put("2", "test2");
		r.put("3", "test2");
		
		assertTrue(r.containsKey("1"));
		assertTrue(r.containsKey("2"));
		assertTrue(r.containsKey("3"));

		assertTrue(d.containsKey("1"));
		assertFalse(d.containsKey("2"));
		assertFalse(d.containsKey("3"));
	}

	/**
	 * Test method for {@link org.eclipse.fennec.emf.osgi.provider.DelegatingHashMap#containsValue(java.lang.Object)}.
	 */
	@Test
	void testContainsValue() {
		EPackageRegistryImpl d = new EPackageRegistryImpl();
		DelegatingEPackageRegistry r = new DelegatingEPackageRegistry(d);
		
		d.put("1", "test");
		
		assertEquals("test", r.get("1"));
		
		r.put("2", "test2");
		r.put("3", "test3");
		
		assertTrue(r.containsValue("test"));
		assertTrue(r.containsValue("test2"));
		assertTrue(r.containsValue("test3"));

		assertTrue(d.containsValue("test"));
		assertFalse(d.containsValue("test2"));
		assertFalse(d.containsValue("test3"));
	}

	/**
	 * Test method for {@link org.eclipse.fennec.emf.osgi.provider.DelegatingHashMap#entrySet()}.
	 */
	@Test
	void testEntrySet() {
		EPackageRegistryImpl d = new EPackageRegistryImpl();
		DelegatingEPackageRegistry r = new DelegatingEPackageRegistry(d);
		
		d.put("1", "test");
		
		assertEquals("test", r.get("1"));
		
		r.put("1", "test2");
		r.put("2", "test3");
		
		assertEquals("test2", r.get("1"));
		assertEquals("test3", r.get("2"));
		assertEquals("test", d.get("1"));
		
		Set<Entry<String,Object>> entrySet = r.entrySet();
		
		assertEquals(3, entrySet.size());
	}

	/**
	 * Test method for {@link org.eclipse.fennec.emf.osgi.provider.DelegatingHashMap#get(java.lang.Object)}.
	 */
	@Test
	void testGet() {
		EPackageRegistryImpl d = new EPackageRegistryImpl();
		DelegatingEPackageRegistry r = new DelegatingEPackageRegistry(d);
		
		d.put("1", "test");
		
		assertEquals("test", r.get("1"));
		
		r.put("1", "test2");

		assertEquals("test2", r.get("1"));
		assertEquals("test", d.get("1"));	}

	/**
	 * Test method for {@link org.eclipse.fennec.emf.osgi.provider.DelegatingHashMap#values()}.
	 */
	@Test
	void testValues() {
		EPackageRegistryImpl d = new EPackageRegistryImpl();
		DelegatingEPackageRegistry r = new DelegatingEPackageRegistry(d);
		
		d.put("1", "test");
		
		assertEquals("test", r.get("1"));
		
		r.put("1", "test2");
		r.put("2", "test3");
		
		assertEquals("test2", r.get("1"));
		assertEquals("test3", r.get("2"));
		assertEquals("test", d.get("1"));
		
		Collection<Object> values = r.values();
		
		assertEquals(3, values.size());
	}

	/**
	 * Test method for {@link org.eclipse.fennec.emf.osgi.provider.DelegatingHashMap#keySet()}.
	 */
	@Test
	void testKeySet() {
		EPackageRegistryImpl d = new EPackageRegistryImpl();
		DelegatingEPackageRegistry r = new DelegatingEPackageRegistry(d);
		
		d.put("1", "test");
		
		assertEquals("test", r.get("1"));
		
		r.put("1", "test2");
		r.put("2", "test3");
		
		assertEquals("test2", r.get("1"));
		assertEquals("test3", r.get("2"));
		assertEquals("test", d.get("1"));
		
		Set<String> keySet = r.keySet();
		
		assertEquals(2, keySet.size());
	}

	/**
	 * Test method for {@link org.eclipse.fennec.emf.osgi.provider.DelegatingHashMap#put(java.lang.Object, java.lang.Object)}.
	 */
	@Test
	void testPut() {
		EPackageRegistryImpl d = new EPackageRegistryImpl();
		DelegatingEPackageRegistry r = new DelegatingEPackageRegistry(d);
		
		d.put("1", "test");
		
		assertEquals("test", r.get("1"));
		
		r.put("1", "test2");

		assertEquals("test2", r.get("1"));
		assertEquals("test", d.get("1"));
	}

	/**
	 * Test method for {@link org.eclipse.fennec.emf.osgi.provider.DelegatingHashMap#remove(java.lang.Object)}.
	 */
	@Test
	void testRemove() {
		EPackageRegistryImpl d = new EPackageRegistryImpl();
		DelegatingEPackageRegistry r = new DelegatingEPackageRegistry(d);
		
		d.put("1", "test");
		
		assertEquals("test", r.get("1"));
		
		r.put("1", "test2");
		
		assertEquals("test2", r.get("1"));
		assertEquals("test", d.get("1"));
		
		d.remove("1");
		
		assertEquals("test2", r.get("1"));
		assertNull(d.get("1"));
	}

	/**
	 * Test method for {@link org.eclipse.fennec.emf.osgi.provider.DelegatingHashMap#putAll(java.util.Map)}.
	 */
	@Test
	void testPutAll() {
		EPackageRegistryImpl d = new EPackageRegistryImpl();
		DelegatingEPackageRegistry r = new DelegatingEPackageRegistry(d);
		
		d.put("1", "test");
		
		assertEquals(1, d.size());
		assertEquals(1, r.size());
		
		Map<String, String> toAdd = new HashMap<String, String>();
		toAdd.put("2", "test2");
		toAdd.put("3", "test2");
		
		r.putAll(toAdd);
		assertEquals(1, d.size());
		assertEquals(3, r.size());
	}

	/**
	 * Test method for {@link org.eclipse.fennec.emf.osgi.provider.DelegatingHashMap#clear()}.
	 */
	@Test
	void testClear() {
		EPackageRegistryImpl d = new EPackageRegistryImpl();
		DelegatingEPackageRegistry r = new DelegatingEPackageRegistry(d);
		
		d.put("1", "test");
		
		assertEquals("test", r.get("1"));
		
		r.put("2", "test2");
		r.put("3", "test2");
		
		assertEquals(3, r.size());
		assertEquals(1, d.size());
		
		d.clear();
		
		assertEquals(2, r.size());
		assertEquals(0, d.size());
	}

}
