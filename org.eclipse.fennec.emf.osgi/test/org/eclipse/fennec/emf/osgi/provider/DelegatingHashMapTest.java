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

import org.eclipse.fennec.emf.osgi.provider.DelegatingHashMap;
import org.junit.jupiter.api.Test;

/**
 * Tests {@link DelegatingHashMap}
 * @author Juergen Albert
 * @since 2 Dec 2022
 */
class DelegatingHashMapTest {

	/**
	 * Test method for {@link org.eclipse.fennec.emf.osgi.provider.DelegatingHashMap#size()}.
	 */
	@Test
	void testSize() {
		Map<String, String> delegate = new HashMap<>();
		Map<String, String> toTest = new DelegatingHashMap<String, String>(delegate);
		
		delegate.put("1", "test");
		
		assertEquals("test", toTest.get("1"));
		
		toTest.put("1", "test2");
		
		assertEquals("test2", toTest.get("1"));
		assertEquals("test", delegate.get("1"));
		
		assertEquals(2, toTest.size());
	}

	/**
	 * Test method for {@link org.eclipse.fennec.emf.osgi.provider.DelegatingHashMap#isEmpty()}.
	 */
	@Test
	void testIsEmpty() {
		Map<String, String> delegate = new HashMap<>();
		Map<String, String> toTest = new DelegatingHashMap<String, String>(delegate);
		
		assertTrue(toTest.isEmpty());
		
		delegate.put("1", "test");
		assertFalse(toTest.isEmpty());
		
		toTest.put("1", "test2");
		assertFalse(toTest.isEmpty());

		delegate.clear();
		assertFalse(toTest.isEmpty());

		toTest.clear();
		assertTrue(toTest.isEmpty());
		
	}

	/**
	 * Test method for {@link org.eclipse.fennec.emf.osgi.provider.DelegatingHashMap#containsKey(java.lang.Object)}.
	 */
	@Test
	void testContainsKey() {
		Map<String, String> delegate = new HashMap<>();
		Map<String, String> toTest = new DelegatingHashMap<String, String>(delegate);
		
		delegate.put("1", "test");
		
		assertEquals("test", toTest.get("1"));
		
		toTest.put("2", "test2");
		toTest.put("3", "test2");
		
		assertTrue(toTest.containsKey("1"));
		assertTrue(toTest.containsKey("2"));
		assertTrue(toTest.containsKey("3"));

		assertTrue(delegate.containsKey("1"));
		assertFalse(delegate.containsKey("2"));
		assertFalse(delegate.containsKey("3"));
	}

	/**
	 * Test method for {@link org.eclipse.fennec.emf.osgi.provider.DelegatingHashMap#containsValue(java.lang.Object)}.
	 */
	@Test
	void testContainsValue() {
		Map<String, String> delegate = new HashMap<>();
		Map<String, String> toTest = new DelegatingHashMap<String, String>(delegate);
		
		delegate.put("1", "test");
		
		assertEquals("test", toTest.get("1"));
		
		toTest.put("2", "test2");
		toTest.put("3", "test3");
		
		assertTrue(toTest.containsValue("test"));
		assertTrue(toTest.containsValue("test2"));
		assertTrue(toTest.containsValue("test3"));

		assertTrue(delegate.containsValue("test"));
		assertFalse(delegate.containsValue("test2"));
		assertFalse(delegate.containsValue("test3"));
	}

	/**
	 * Test method for {@link org.eclipse.fennec.emf.osgi.provider.DelegatingHashMap#entrySet()}.
	 */
	@Test
	void testEntrySet() {
		Map<String, String> delegate = new HashMap<>();
		Map<String, String> toTest = new DelegatingHashMap<String, String>(delegate);
		
		delegate.put("1", "test");
		
		assertEquals("test", toTest.get("1"));
		
		toTest.put("1", "test2");
		toTest.put("2", "test3");
		
		assertEquals("test2", toTest.get("1"));
		assertEquals("test3", toTest.get("2"));
		assertEquals("test", delegate.get("1"));
		
		Set<Entry<String,String>> entrySet = toTest.entrySet();
		
		assertEquals(3, entrySet.size());
	}

	/**
	 * Test method for {@link org.eclipse.fennec.emf.osgi.provider.DelegatingHashMap#get(java.lang.Object)}.
	 */
	@Test
	void testGet() {
		Map<String, String> delegate = new HashMap<>();
		Map<String, String> toTest = new DelegatingHashMap<String, String>(delegate);
		
		delegate.put("1", "test");
		
		assertEquals("test", toTest.get("1"));
		
		toTest.put("1", "test2");

		assertEquals("test2", toTest.get("1"));
		assertEquals("test", delegate.get("1"));	}

	/**
	 * Test method for {@link org.eclipse.fennec.emf.osgi.provider.DelegatingHashMap#values()}.
	 */
	@Test
	void testValues() {
		Map<String, String> delegate = new HashMap<>();
		Map<String, String> toTest = new DelegatingHashMap<String, String>(delegate);
		
		delegate.put("1", "test");
		
		assertEquals("test", toTest.get("1"));
		
		toTest.put("1", "test2");
		toTest.put("2", "test3");
		
		assertEquals("test2", toTest.get("1"));
		assertEquals("test3", toTest.get("2"));
		assertEquals("test", delegate.get("1"));
		
		Collection<String> values = toTest.values();
		
		assertEquals(3, values.size());
	}

	/**
	 * Test method for {@link org.eclipse.fennec.emf.osgi.provider.DelegatingHashMap#keySet()}.
	 */
	@Test
	void testKeySet() {
		Map<String, String> delegate = new HashMap<>();
		Map<String, String> toTest = new DelegatingHashMap<String, String>(delegate);
		
		delegate.put("1", "test");
		
		assertEquals("test", toTest.get("1"));
		
		toTest.put("1", "test2");
		toTest.put("2", "test3");
		
		assertEquals("test2", toTest.get("1"));
		assertEquals("test3", toTest.get("2"));
		assertEquals("test", delegate.get("1"));
		
		Set<String> keySet = toTest.keySet();
		
		assertEquals(2, keySet.size());
	}

	/**
	 * Test method for {@link org.eclipse.fennec.emf.osgi.provider.DelegatingHashMap#put(java.lang.Object, java.lang.Object)}.
	 */
	@Test
	void testPut() {
		Map<String, String> delegate = new HashMap<>();
		Map<String, String> toTest = new DelegatingHashMap<String, String>(delegate);
		
		delegate.put("1", "test");
		
		assertEquals("test", toTest.get("1"));
		
		toTest.put("1", "test2");

		assertEquals("test2", toTest.get("1"));
		assertEquals("test", delegate.get("1"));
	}

	/**
	 * Test method for {@link org.eclipse.fennec.emf.osgi.provider.DelegatingHashMap#remove(java.lang.Object)}.
	 */
	@Test
	void testRemove() {
		Map<String, String> delegate = new HashMap<>();
		Map<String, String> toTest = new DelegatingHashMap<String, String>(delegate);
		
		delegate.put("1", "test");
		
		assertEquals("test", toTest.get("1"));
		
		toTest.put("1", "test2");
		
		assertEquals("test2", toTest.get("1"));
		assertEquals("test", delegate.get("1"));
		
		delegate.remove("1");
		
		assertEquals("test2", toTest.get("1"));
		assertNull(delegate.get("1"));
	}

	/**
	 * Test method for {@link org.eclipse.fennec.emf.osgi.provider.DelegatingHashMap#putAll(java.util.Map)}.
	 */
	@Test
	void testPutAll() {
		Map<String, String> delegate = new HashMap<>();
		Map<String, String> toTest = new DelegatingHashMap<String, String>(delegate);
		
		delegate.put("1", "test");
		
		assertEquals(1, delegate.size());
		assertEquals(1, toTest.size());
		
		Map<String, String> toAdd = new HashMap<String, String>();
		toAdd.put("2", "test2");
		toAdd.put("3", "test2");
		
		toTest.putAll(toAdd);
		assertEquals(1, delegate.size());
		assertEquals(3, toTest.size());
	}

	/**
	 * Test method for {@link org.eclipse.fennec.emf.osgi.provider.DelegatingHashMap#clear()}.
	 */
	@Test
	void testClear() {
		Map<String, String> delegate = new HashMap<>();
		Map<String, String> toTest = new DelegatingHashMap<String, String>(delegate);
		
		delegate.put("1", "test");
		
		assertEquals("test", toTest.get("1"));
		
		toTest.put("2", "test2");
		toTest.put("3", "test2");
		
		assertEquals(3, toTest.size());
		assertEquals(1, delegate.size());
		
		delegate.clear();
		
		assertEquals(2, toTest.size());
		assertEquals(0, delegate.size());
	}

}
