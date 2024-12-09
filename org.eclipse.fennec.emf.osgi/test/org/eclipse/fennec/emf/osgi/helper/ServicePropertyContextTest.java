/**
 * Copyright (c) 2012 - 2023 Data In Motion and others.
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
package org.eclipse.fennec.emf.osgi.helper;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eclipse.fennec.emf.osgi.constants.EMFNamespaces;
import org.eclipse.fennec.emf.osgi.helper.ServicePropertyContext;
import org.junit.jupiter.api.Test;
import org.osgi.framework.Constants;

/**
 * Suppress warning makes sonar ignoring test classes and methods that are public 
 * @author Mark Hoffmann
 * @since 06.11.2023
 */
@java.lang.SuppressWarnings({"java:S5786"})
public class ServicePropertyContextTest {

	@Test
	public void testUpdatePropertiesNull() {
		ServicePropertyContext spc = ServicePropertyContext.create();
		
		assertThrows(NullPointerException.class, ()->spc.updateServiceProperties(null));
		assertThrows(NullPointerException.class, ()->ServicePropertyContext.create(null));
		
		assertTrue(spc.getDictionary(false).isEmpty());
		assertTrue(spc.getProperties(false).isEmpty());
	}
	
	@Test
	public void testUpdatePropertiesNoServiceId() {
		ServicePropertyContext spc = ServicePropertyContext.create();
		
		Map<String, Object> map = Collections.emptyMap();
		assertThrows(IllegalStateException.class, ()->spc.updateServiceProperties(map));
		
		assertTrue(spc.getDictionary(false).isEmpty());
		assertTrue(spc.getProperties(false).isEmpty());
		
		final Map<String, Object> map2 = new HashMap<>();
		map2.put(EMFNamespaces.EMF_CONFIGURATOR_NAME, Collections.singleton("test"));
		assertThrows(IllegalStateException.class, ()->spc.updateServiceProperties(map2));
		
		assertTrue(spc.getDictionary(false).isEmpty());
		assertTrue(spc.getProperties(false).isEmpty());
		
		assertThrows(IllegalStateException.class, ()->ServicePropertyContext.create(map2));
	}
	
	@Test
	public void testUpdateProperties() {
		
		Map<String, Object> map = new HashMap<>();
		map.put(Constants.SERVICE_ID, Long.valueOf(42));
		ServicePropertyContext spc = ServicePropertyContext.create();
		spc.updateServiceProperties(map);
		
		assertTrue(spc.getDictionary(false).isEmpty());
		assertTrue(spc.getProperties(false).isEmpty());
		
		ServicePropertyContext spc2 = ServicePropertyContext.create(map);
		assertTrue(spc2.getDictionary(false).isEmpty());
		assertTrue(spc2.getProperties(false).isEmpty());
		
		map.put(EMFNamespaces.EMF_CONFIGURATOR_NAME, Collections.singleton("test"));
		spc.updateServiceProperties(map);
		assertFalse(spc.getDictionary(false).isEmpty());
		assertFalse(spc.getProperties(false).isEmpty());
		assertEquals(1, spc.getProperties(false).size());
		verifyExistingKeys(spc.getProperties(false), EMFNamespaces.EMF_CONFIGURATOR_NAME, "test");
		
		spc2 = ServicePropertyContext.create(map);
		assertFalse(spc2.getDictionary(false).isEmpty());
		assertFalse(spc2.getProperties(false).isEmpty());
		assertEquals(1, spc2.getProperties(false).size());
		
		verifyExistingKeys(spc2.getProperties(false), EMFNamespaces.EMF_CONFIGURATOR_NAME, "test");
		
		Set<String> modelNames = new HashSet<>();
		modelNames.add("one");
		modelNames.add("two");
		map.put(EMFNamespaces.EMF_MODEL_NAME, modelNames);
		
		spc.updateServiceProperties(map);
		assertEquals(2, spc.getProperties(false).size());
		verifyExistingKeys(spc.getProperties(false), EMFNamespaces.EMF_CONFIGURATOR_NAME, "test");
		verifyExistingKeys(spc.getProperties(false), EMFNamespaces.EMF_MODEL_NAME, "one", "two");
		
		spc2 = ServicePropertyContext.create(map);
		assertEquals(2, spc2.getProperties(false).size());
		verifyExistingKeys(spc2.getProperties(false), EMFNamespaces.EMF_CONFIGURATOR_NAME, "test");
		verifyExistingKeys(spc2.getProperties(false), EMFNamespaces.EMF_MODEL_NAME, "one", "two");
	}
	
	@Test
	public void testUpdateFeatureProperties() {
		
		Map<String, Object> map = new HashMap<>();
		map.put(Constants.SERVICE_ID, Long.valueOf(42));
		ServicePropertyContext spc = ServicePropertyContext.create(map);
		spc.updateServiceProperties(map);
		
		assertTrue(spc.getDictionary(false).isEmpty());
		assertTrue(spc.getProperties(false).isEmpty());
		
		map.put(EMFNamespaces.EMF_CONFIGURATOR_NAME, Collections.singleton("test"));
		spc.updateServiceProperties(map);
		assertFalse(spc.getDictionary(false).isEmpty());
		assertFalse(spc.getProperties(false).isEmpty());
		assertEquals(1, spc.getProperties(false).size());
		verifyExistingKeys(spc.getProperties(false), EMFNamespaces.EMF_CONFIGURATOR_NAME, "test");
		
		map.put(EMFNamespaces.EMF_MODEL_FEATURE + ".foo", "bar");
		spc = ServicePropertyContext.create(map);
		assertFalse(spc.getDictionary(false).isEmpty());
		assertFalse(spc.getProperties(false).isEmpty());
		assertEquals(2, spc.getProperties(false).size());
		verifyExistingKeys(spc.getProperties(false), EMFNamespaces.EMF_CONFIGURATOR_NAME, "test");
		verifyExistingObjectKeys(spc.getProperties(false), EMFNamespaces.EMF_MODEL_FEATURE + ".foo", "bar");
		
		Set<String> modelNames = new HashSet<>();
		modelNames.add("one");
		modelNames.add("two");
		map.put(EMFNamespaces.EMF_MODEL_FEATURE + ".foo", modelNames);
		
		spc.updateServiceProperties(map);
		assertEquals(2, spc.getProperties(false).size());
		verifyExistingKeys(spc.getProperties(false), EMFNamespaces.EMF_CONFIGURATOR_NAME, "test");
		verifyExistingObjectKeys(spc.getProperties(false), EMFNamespaces.EMF_MODEL_FEATURE + ".foo", "one", "two");
		map.put(EMFNamespaces.EMF_MODEL_FEATURE + ".foo", new Object[] {42, 13});
		spc.updateServiceProperties(map);
		assertEquals(2, spc.getProperties(false).size());
		verifyExistingKeys(spc.getProperties(false), EMFNamespaces.EMF_CONFIGURATOR_NAME, "test");
		verifyExistingObjectKeys(spc.getProperties(false), EMFNamespaces.EMF_MODEL_FEATURE + ".foo", 42, 13);
	}
	
	@Test
	public void testSubContext() {
		Map<String, Object> map = new HashMap<>();
		map.put(Constants.SERVICE_ID, Long.valueOf(42));
		Set<String> modelNames = new HashSet<>();
		modelNames.add("one");
		modelNames.add("two");
		map.put(EMFNamespaces.EMF_MODEL_NAME, modelNames);
		map.put(EMFNamespaces.EMF_CONFIGURATOR_NAME, Collections.singleton("test"));
		
		ServicePropertyContext ctx = ServicePropertyContext.create(map);
		assertEquals(2, ctx.getProperties(false).size());
		verifyExistingKeys(ctx.getProperties(false), EMFNamespaces.EMF_CONFIGURATOR_NAME, "test");
		verifyExistingKeys(ctx.getProperties(false), EMFNamespaces.EMF_MODEL_NAME, "one", "two");
		
		assertThrows(NullPointerException.class, ()->ctx.addSubContext(null));
		assertThrows(IllegalStateException.class, ()->ctx.addSubContext(Collections.emptyMap()));
		
		Map<String, Object> subMap = new HashMap<>();
		subMap.put(Constants.SERVICE_ID, Long.valueOf(2));
		Set<String> subModelNames = new HashSet<>();
		subModelNames.add("one");
		subModelNames.add("three");
		subMap.put(EMFNamespaces.EMF_MODEL_NAME, subModelNames);
		subMap.put(EMFNamespaces.EMF_CONFIGURATOR_NAME, Collections.singleton("toast"));
		subMap.put(EMFNamespaces.EMF_MODEL_CONTENT_TYPE, Collections.singleton("application/toast"));
		
		ServicePropertyContext subCtx = ctx.addSubContext(subMap);
		assertNotNull(subCtx);
		assertEquals(3, subCtx.getProperties(false).size());
		verifyExistingKeys(subCtx.getProperties(false), EMFNamespaces.EMF_CONFIGURATOR_NAME, "toast");
		verifyExistingKeys(subCtx.getProperties(false), EMFNamespaces.EMF_MODEL_NAME, "one", "three");
		verifyExistingKeys(subCtx.getProperties(false), EMFNamespaces.EMF_MODEL_CONTENT_TYPE, "application/toast");
		
		assertEquals(2, ctx.getProperties(false).size());
		verifyExistingKeys(ctx.getProperties(false), EMFNamespaces.EMF_CONFIGURATOR_NAME, "test");
		verifyExistingKeys(ctx.getProperties(false), EMFNamespaces.EMF_MODEL_NAME, "one", "two");
		
		assertThrows(NullPointerException.class, ()->ctx.removeSubContext(null));
		assertThrows(IllegalStateException.class, ()->ctx.removeSubContext(Collections.emptyMap()));
		
		// remove unknown sub-context
		subMap = new HashMap<>();
		subMap.put(Constants.SERVICE_ID, Long.valueOf(3));
		subCtx = ctx.removeSubContext(subMap);
		assertNull(subCtx);
		
		subMap.put(Constants.SERVICE_ID, Long.valueOf(2));
		subCtx = ctx.removeSubContext(subMap);
		assertNotNull(subCtx);
		assertEquals(3, subCtx.getProperties(false).size());
		verifyExistingKeys(subCtx.getProperties(false), EMFNamespaces.EMF_CONFIGURATOR_NAME, "toast");
		verifyExistingKeys(subCtx.getProperties(false), EMFNamespaces.EMF_MODEL_NAME, "one", "three");
		verifyExistingKeys(subCtx.getProperties(false), EMFNamespaces.EMF_MODEL_CONTENT_TYPE, "application/toast");

		assertEquals(2, ctx.getProperties(false).size());
		verifyExistingKeys(ctx.getProperties(false), EMFNamespaces.EMF_CONFIGURATOR_NAME, "test");
		verifyExistingKeys(ctx.getProperties(false), EMFNamespaces.EMF_MODEL_NAME, "one", "two");

		subMap.put(Constants.SERVICE_ID, Long.valueOf(2));
		subCtx = ctx.removeSubContext(subMap);
		assertNull(subCtx);
		
		assertEquals(2, ctx.getProperties(false).size());
		verifyExistingKeys(ctx.getProperties(false), EMFNamespaces.EMF_CONFIGURATOR_NAME, "test");
		verifyExistingKeys(ctx.getProperties(false), EMFNamespaces.EMF_MODEL_NAME, "one", "two");
	}
	
	@Test
	public void testSubContextEmptyParent() {
		Map<String, Object> map = new HashMap<>();
		map.put(Constants.SERVICE_ID, Long.valueOf(42));
		Set<String> modelNames = new HashSet<>();
		modelNames.add("one");
		modelNames.add("two");
		map.put(EMFNamespaces.EMF_MODEL_NAME, modelNames);
		map.put(EMFNamespaces.EMF_CONFIGURATOR_NAME, Collections.singleton("test"));
		
		ServicePropertyContext ctx = ServicePropertyContext.create();
		assertTrue(ctx.getProperties(false).isEmpty());
		
		Map<String, Object> subMap = new HashMap<>();
		subMap.put(Constants.SERVICE_ID, Long.valueOf(2));
		Set<String> subModelNames = new HashSet<>();
		subModelNames.add("one");
		subModelNames.add("three");
		subMap.put(EMFNamespaces.EMF_MODEL_NAME, subModelNames);
		subMap.put(EMFNamespaces.EMF_CONFIGURATOR_NAME, Collections.singleton("toast"));
		subMap.put(EMFNamespaces.EMF_MODEL_CONTENT_TYPE, Collections.singleton("application/toast"));
		
		ServicePropertyContext subCtx = ctx.addSubContext(subMap);
		assertNotNull(subCtx);
		assertEquals(3, subCtx.getProperties(false).size());
		verifyExistingKeys(subCtx.getProperties(false), EMFNamespaces.EMF_CONFIGURATOR_NAME, "toast");
		verifyExistingKeys(subCtx.getProperties(false), EMFNamespaces.EMF_MODEL_NAME, "one", "three");
		verifyExistingKeys(subCtx.getProperties(false), EMFNamespaces.EMF_MODEL_CONTENT_TYPE, "application/toast");
		
		assertTrue(ctx.getProperties(false).isEmpty());
		
		assertEquals(3, ctx.getProperties(true).size());
		verifyExistingKeys(ctx.getProperties(true), EMFNamespaces.EMF_CONFIGURATOR_NAME, "toast");
		verifyExistingKeys(ctx.getProperties(true), EMFNamespaces.EMF_MODEL_NAME, "one", "three");
		verifyExistingKeys(ctx.getProperties(true), EMFNamespaces.EMF_MODEL_CONTENT_TYPE, "application/toast");
		
		subMap.put(Constants.SERVICE_ID, Long.valueOf(2));
		subCtx = ctx.removeSubContext(subMap);
		assertNotNull(subCtx);
		assertEquals(3, subCtx.getProperties(false).size());
		verifyExistingKeys(subCtx.getProperties(false), EMFNamespaces.EMF_CONFIGURATOR_NAME, "toast");
		verifyExistingKeys(subCtx.getProperties(false), EMFNamespaces.EMF_MODEL_NAME, "one", "three");
		verifyExistingKeys(subCtx.getProperties(false), EMFNamespaces.EMF_MODEL_CONTENT_TYPE, "application/toast");
		
		assertTrue(ctx.getProperties(false).isEmpty());
		assertTrue(ctx.getProperties(true).isEmpty());
		
	}
	
	@Test
	public void testMerge() {
		Map<String, Object> map = new HashMap<>();
		map.put(Constants.SERVICE_ID, Long.valueOf(42));
		Set<String> modelNames = new HashSet<>();
		modelNames.add("one");
		modelNames.add("two");
		map.put(EMFNamespaces.EMF_MODEL_NAME, modelNames);
		map.put(EMFNamespaces.EMF_CONFIGURATOR_NAME, Collections.singleton("test"));
		
		ServicePropertyContext ctx = ServicePropertyContext.create(map);
		Map<String, Object> mergedResult = ctx.getProperties(true);
		assertEquals(2, mergedResult.size());
		verifyExistingKeys(mergedResult, EMFNamespaces.EMF_CONFIGURATOR_NAME, "test");
		verifyExistingKeys(mergedResult, EMFNamespaces.EMF_MODEL_NAME, "one", "two");
		
		Map<String, Object> subMap = new HashMap<>();
		subMap.put(Constants.SERVICE_ID, Long.valueOf(2));
		Set<String> subModelNames = new HashSet<>();
		subModelNames.add("one");
		subModelNames.add("three");
		subMap.put(EMFNamespaces.EMF_MODEL_NAME, subModelNames);
		subMap.put(EMFNamespaces.EMF_CONFIGURATOR_NAME, Collections.singleton("toast"));
		subMap.put(EMFNamespaces.EMF_MODEL_CONTENT_TYPE, Collections.singleton("application/toast"));
		
		ServicePropertyContext subCtx = ctx.addSubContext(subMap);
		assertNotNull(subCtx);
		assertEquals(3, subCtx.getProperties(false).size());
		verifyExistingKeys(subCtx.getProperties(false), EMFNamespaces.EMF_CONFIGURATOR_NAME, "toast");
		verifyExistingKeys(subCtx.getProperties(false), EMFNamespaces.EMF_MODEL_NAME, "one", "three");
		verifyExistingKeys(subCtx.getProperties(false), EMFNamespaces.EMF_MODEL_CONTENT_TYPE, "application/toast");
		
		mergedResult = ctx.getProperties(true);
		assertEquals(3, mergedResult.size());
		verifyExistingKeys(mergedResult, EMFNamespaces.EMF_CONFIGURATOR_NAME, "toast", "test");
		verifyExistingKeys(mergedResult, EMFNamespaces.EMF_MODEL_NAME, "one", "two", "three");
		verifyExistingKeys(mergedResult, EMFNamespaces.EMF_MODEL_CONTENT_TYPE, "application/toast");
		
		subMap = new HashMap<>();
		subMap.put(Constants.SERVICE_ID, Long.valueOf(3));
		subCtx = ctx.removeSubContext(subMap);
		assertNull(subCtx);
		
		mergedResult = ctx.getProperties(true);
		assertEquals(3, mergedResult.size());
		verifyExistingKeys(mergedResult, EMFNamespaces.EMF_CONFIGURATOR_NAME, "toast", "test");
		verifyExistingKeys(mergedResult, EMFNamespaces.EMF_MODEL_NAME, "one", "two", "three");
		verifyExistingKeys(mergedResult, EMFNamespaces.EMF_MODEL_CONTENT_TYPE, "application/toast");
		
		subMap = new HashMap<>();
		subMap.put(Constants.SERVICE_ID, Long.valueOf(2));
		subCtx = ctx.removeSubContext(subMap);
		assertEquals(3, subCtx.getProperties(false).size());
		verifyExistingKeys(subCtx.getProperties(false), EMFNamespaces.EMF_CONFIGURATOR_NAME, "toast");
		verifyExistingKeys(subCtx.getProperties(false), EMFNamespaces.EMF_MODEL_NAME, "one", "three");
		verifyExistingKeys(subCtx.getProperties(false), EMFNamespaces.EMF_MODEL_CONTENT_TYPE, "application/toast");
		
		mergedResult = ctx.getProperties(true);
		assertEquals(2, mergedResult.size());
		verifyExistingKeys(mergedResult, EMFNamespaces.EMF_CONFIGURATOR_NAME, "test");
		verifyExistingKeys(mergedResult, EMFNamespaces.EMF_MODEL_NAME, "one", "two");
	}
	
	@Test
	public void testMergeFeatureProperties() {
		Map<String, Object> map = new HashMap<>();
		map.put(Constants.SERVICE_ID, Long.valueOf(42));
		Set<String> modelNames = new HashSet<>();
		modelNames.add("one");
		modelNames.add("two");
		map.put(EMFNamespaces.EMF_MODEL_FEATURE + ".foo", modelNames);
		
		ServicePropertyContext ctx = ServicePropertyContext.create(map);
		Map<String, Object> mergedResult = ctx.getProperties(true);
		assertEquals(1, mergedResult.size());
		verifyExistingObjectKeys(mergedResult, EMFNamespaces.EMF_MODEL_FEATURE + ".foo", "one", "two");
		
		Map<String, Object> subMap = new HashMap<>();
		subMap.put(Constants.SERVICE_ID, Long.valueOf(2));
		Set<String> subModelNames = new HashSet<>();
		subModelNames.add("one");
		subModelNames.add("three");
		subMap.put(EMFNamespaces.EMF_MODEL_FEATURE + ".foo", subModelNames);
		subMap.put(EMFNamespaces.EMF_MODEL_FEATURE + ".bar", Collections.singleton("toast"));
		
		ServicePropertyContext subCtx = ctx.addSubContext(subMap);
		assertNotNull(subCtx);
		assertEquals(2, subCtx.getProperties(false).size());
		verifyExistingObjectKeys(subCtx.getProperties(false), EMFNamespaces.EMF_MODEL_FEATURE + ".foo", "one", "three");
		verifyExistingObjectKeys(subCtx.getProperties(false), EMFNamespaces.EMF_MODEL_FEATURE + ".bar", "toast");
		
		mergedResult = ctx.getProperties(true);
		assertEquals(2, mergedResult.size());
		verifyExistingObjectKeys(mergedResult, EMFNamespaces.EMF_MODEL_FEATURE + ".foo", "one", "two", "three");
		verifyExistingObjectKeys(mergedResult, EMFNamespaces.EMF_MODEL_FEATURE + ".bar", "toast");
		
		subMap = new HashMap<>();
		subMap.put(Constants.SERVICE_ID, Long.valueOf(2));
		subCtx = ctx.removeSubContext(subMap);
		assertEquals(2, subCtx.getProperties(false).size());
		verifyExistingObjectKeys(subCtx.getProperties(false), EMFNamespaces.EMF_MODEL_FEATURE + ".foo", "one", "three");
		verifyExistingObjectKeys(subCtx.getProperties(false), EMFNamespaces.EMF_MODEL_FEATURE + ".bar", "toast");
		
		mergedResult = ctx.getProperties(true);
		assertEquals(1, mergedResult.size());
		verifyExistingObjectKeys(ctx.getProperties(false), EMFNamespaces.EMF_MODEL_FEATURE + ".foo", "one", "two");
	}
	
	private void verifyExistingKeys(Map<String, Object> source, String key, String...expectedValues) {
		assertNotNull(source);
		assertNotNull(key);
		assertNotNull(expectedValues);
		
		Object value = source.get(key);
		assertNotNull(value);
		assertInstanceOf(String[].class, value);
		List<String> stringValues = Arrays.asList((String[]) value);
		for (String ev : expectedValues) {
			assertTrue(stringValues.contains(ev));
		}
	}
	
	private void verifyExistingObjectKeys(Map<String, Object> source, String key, Object...expectedValues) {
		assertNotNull(source);
		assertNotNull(key);
		assertNotNull(expectedValues);
		
		Object value = source.get(key);
		assertNotNull(value);
		assertInstanceOf(Object[].class, value);
		List<Object> values = Arrays.asList((Object[]) value);
		for (Object ev : expectedValues) {
			assertTrue(values.contains(ev));
		}
	}
	
	
}
