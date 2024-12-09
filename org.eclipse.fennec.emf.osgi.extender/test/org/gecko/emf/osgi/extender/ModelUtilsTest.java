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
 *       Data In Motion - initial API and implementation
 */
package org.gecko.emf.osgi.extender;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.fennec.emf.osgi.extender.ModelHelper;
import org.junit.jupiter.api.Test;

/**
 * 
 * @author mark
 * @since 17.10.2022
 */
@java.lang.SuppressWarnings({"java:S5786"})
class ModelUtilsTest {
	
	@Test
	void testExtractPropertiesNoProperties() {
		assertNull(ModelHelper.extractProperties(null, null));
		assertEquals("/test", ModelHelper.extractProperties("/test", null));
		assertEquals("/test", ModelHelper.extractProperties("/test;blub", null));
	}
	
	@Test
	void testExtractPropertiesPath() {
		Map<String, String> props = new HashMap<String, String>();
		assertEquals("/test", ModelHelper.extractProperties("/test", props));
		assertTrue(props.isEmpty());
		props.clear();
		assertEquals("", ModelHelper.extractProperties("", props));
		assertTrue(props.isEmpty());
		props.clear();
		assertEquals("/test", ModelHelper.extractProperties("/test;", props));
		assertTrue(props.isEmpty());
		props.clear();
		
	}
	
	@Test
	void testExtractPropertiesWorking() {
		Map<String, String> props = new HashMap<String, String>();
		assertEquals("/test", ModelHelper.extractProperties("/test;blub", props));
		assertFalse(props.isEmpty());
		assertTrue(props.containsKey("blub"));
		assertNull(props.get("blub"));
		props.clear();
		assertEquals("/test", ModelHelper.extractProperties("/test;blub=bla", props));
		assertFalse(props.isEmpty());
		assertTrue(props.containsKey("blub"));
		assertEquals("bla", props.get("blub"));
		props.clear();
		assertEquals("/test", ModelHelper.extractProperties("/test;blub=bla;blub=blubber", props));
		assertFalse(props.isEmpty());
		assertEquals(1, props.size());
		assertTrue(props.containsKey("blub"));
		assertEquals("blubber", props.get("blub"));
		props.clear();
		assertEquals("/test", ModelHelper.extractProperties("/test;blub=bla;foo=bar", props));
		assertFalse(props.isEmpty());
		assertEquals(2, props.size());
		assertTrue(props.containsKey("blub"));
		assertEquals("bla", props.get("blub"));
		assertTrue(props.containsKey("foo"));
		assertEquals("bar", props.get("foo"));
		props.clear();
		assertEquals("/test", ModelHelper.extractProperties("/test;blub;foo=bar", props));
		assertFalse(props.isEmpty());
		assertEquals(2, props.size());
		assertTrue(props.containsKey("blub"));
		assertNull(props.get("blub"));
		assertTrue(props.containsKey("foo"));
		assertEquals("bar", props.get("foo"));
		props.clear();
		assertEquals("", ModelHelper.extractProperties(";blub=bla", props));
		assertFalse(props.isEmpty());
		assertTrue(props.containsKey("blub"));
		assertEquals("bla", props.get("blub"));
		props.clear();
		
	}

}
