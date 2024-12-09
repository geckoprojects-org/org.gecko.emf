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
package org.eclipse.fennec.emf.osgi.helper;

import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;
import static java.util.Objects.requireNonNull;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Dictionary;
import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.osgi.framework.Constants;

/**
 * A Helper to update the given Service Properties
 * @author Juergen Albert
 * @since 22 Feb 2022
 */
public class ServicePropertiesHelper {
	
	private ServicePropertiesHelper() {
	}
	
	/**
	 * Merges the source map into the target map and appends the values
	 * @param source the source map, must not be <code>null</code>
	 * @param target the map to merge into, must not be <code>null</code>
	 */
	public static void mergeNameMap(Map<Long, Set<String>> source, Map<Long, Set<String>> target) {
		requireNonNull(source);
		requireNonNull(target);
		
		source.forEach((key, value)->appendNameMap(target, value, key, true));
	}
	
	/**
	 * Updates the name set for a given service id and appends the new names
	 * @param nameMap the map to update
	 * @param newNames the new properties
	 * @param serviceId the service id the name set belongs to
	 * @param append set to <code>true</code> to append the values instead of replacing them
	 */
	public static void appendNameMap(Map<Long, Set<String>> nameMap, Set<String> newNames, Long serviceId, boolean append) {
		requireNonNull(nameMap);
		requireNonNull(newNames);
		requireNonNull(serviceId);
		
		Set<String> nameSet = nameMap.get(serviceId);
		if (isNull(nameSet)) {
			nameSet = new HashSet<>();
			nameMap.put(serviceId, nameSet);
		}
		if (!append) {
			nameSet.clear();
		}
		nameSet.addAll(newNames);
	}

	/**
	 * Updates the name set for a given service id
	 * @param nameMap the map to update
	 * @param newNames the new properties
	 * @param serviceId the service id the name set belongs to
	 */
	public static void updateNameMap(Map<Long, Set<String>> nameMap, Set<String> newNames, Long serviceId) {
		appendNameMap(nameMap, newNames, serviceId, false);
	}

	/**
	 * Return all values of all map entries as array
	 * @param nameMap the map to return values from
	 * @return a string array
	 */
	public static String[] getNamesArray(Map<Long, Set<String>> nameMap) {
		return nameMap.entrySet()
				.stream()
				.filter(e->Objects.nonNull(e.getValue()))
				.map(Entry::getValue)
				.flatMap(Collection::stream)
				.collect(Collectors.toSet())
				.toArray(new String[0]);
	}
	
	/**
	 * returns a String+ value and recognizes Object, Object[] and Collection<Object>
	 * @param options the property map
	 * @param key the key to get
	 * @return a {@link Set} of {@link Object} or <code>null</code>, if the key is not available
	 */
	public static Set<String> getStringPlusValue(Map<String, Object> options, String key) {
		if (isNull(options) || isNull(key)) {
			return null;
		}
		Object value = options.get(key);
		if (isNull(value)) {
			return null;
		}
		Collection<?> values;
		if (value instanceof String) {
			values = Collections.singletonList((String) value);
		} else if(value.getClass().isArray()){
			values = Arrays.asList((Object[])value);
		} else if (value instanceof Collection<?>) {
			values = (Collection<?>)value;
		} else {
			values =  Collections.singletonList(value.toString());
		}
		return values.
				stream().
				filter(Objects::nonNull).
				map(Object::toString).
				collect(Collectors.toSet());
	}
	
	/**
	 * returns a Object+ value and recognizes Object, Object[] and Collection<Object>
	 * @param options the property map
	 * @param key the key to get
	 * @return a {@link Set} of {@link Object} or <code>null</code>, if the key is not available
	 */
	public static Object[] createObjectPlusValue(Object value) {
		if (isNull(value)) {
			return null;
		}
		Collection<?> values;
		if(value.getClass().isArray()){
			values = Arrays.asList((Object[])value);
		} else if (value instanceof Collection<?>) {
			values = (Collection<?>)value;
		} else {
			values = Collections.singletonList(value);
		}
		return values.
				stream().
				filter(Objects::nonNull).
				collect(Collectors.toSet()).
				toArray();
	}
	
	/**
	 * Appends the values for the key-property from the sourceMap to the resulting {@link Dictionary}
	 * @param key the property key to get from the source map
	 * @param sourceMap the source properties map
	 * @param dictionary the resulting dictionary
	 * @return the modified resulting dictionary
	 */
	public static Dictionary<String, Object> appendToDictionary(String key, Map<Long, Set<String>> sourceMap, Dictionary<String, Object> dictionary) {
		requireNonNull(dictionary);
		requireNonNull(sourceMap);
		requireNonNull(key);
		String[] stringArrayValues = ServicePropertiesHelper.getNamesArray(sourceMap);
		if (nonNull(stringArrayValues) && stringArrayValues.length > 0) {
			dictionary.put(key, stringArrayValues);
		}
		return dictionary;
	}
	
	/**
	 * Returns an {@link Optional} of the service id extracted out of the given service properties
	 * @param properties the service properties, must not be <code>null</code>
	 * @return an {@link Optional} of the service id
	 */
	public static Optional<Long> getServiceId(Map<String, Object> properties) {
		if (isNull(properties)) {
			return Optional.empty();
		}
		Long serviceId = (Long) properties.get(Constants.SERVICE_ID);
		return Optional.ofNullable(serviceId);
	}
	
	/**
	 * Takes the given properties and filters of all entries that start with the given prefix. As a result the prefix will be removed from the key.
	 * An entry: test.foo=bar with prefix "test." will end up in foo=bar.
	 * The key is only accepted, if it not equal to the prefix, to avoid blank space keys 
	 * If the prefix or properties is <code>null</code>, an empty map will be returned.
	 * @param properties the source properties
	 * @return the normalized properties or an empty property map
	 */
	public static Map<String, Object> filterProperties(String prefix, Map<String, Object> properties) {
		if (isNull(prefix) || isNull(properties)) {
			return Collections.emptyMap();
		}
		return properties
				.entrySet()
				.stream()
				.filter(e -> e.getKey().startsWith(prefix) && !e.getKey().equals(prefix))
				.collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
	}
	
	/**
	 * Takes the given properties and filters of all entries that start with the given prefix. As a result the prefix will be removed from the key.
	 * An entry: test.foo=bar with prefix "test." will end up in foo=bar.
	 * The key is only accepted, if it not equal to the prefix, to avoid blank space keys 
	 * If the prefix or properties is <code>null</code>, an empty map will be returned.
	 * @param properties the source properties
	 * @return the normalized properties or an empty property map
	 */
	public static Map<String, Object> normalizeProperties(String prefix, Map<String, Object> properties) {
		if (isNull(prefix) || isNull(properties)) {
			return Collections.emptyMap();
		}
		return filterProperties(prefix, properties)
				.entrySet()
				.stream()
				.collect(Collectors.toMap(e->e.getKey().substring(prefix.length()), e->{
					Object value = e.getValue();
					if (value.getClass().isArray()) {
						Object[] values = (Object[]) value;
						if (values.length == 1) {
							return values[0];
						}
					}
					return value;
				}));
	}
	
}
