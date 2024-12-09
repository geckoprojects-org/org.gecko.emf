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

import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;
import static java.util.Objects.requireNonNull;
import static org.eclipse.fennec.emf.osgi.helper.ServicePropertiesHelper.appendToDictionary;
import static org.eclipse.fennec.emf.osgi.helper.ServicePropertiesHelper.filterProperties;
import static org.eclipse.fennec.emf.osgi.helper.ServicePropertiesHelper.getStringPlusValue;

import java.util.Arrays;
import java.util.Dictionary;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import org.eclipse.fennec.emf.osgi.constants.EMFNamespaces;
import org.osgi.framework.FrameworkUtil;

/**
 * {@link ServicePropertyContext} implementation. Hold a set of property maps for Gecko EMF
 * @author Mark Hoffmann
 * @since 04.11.2023
 */
public class ServicePropertyContextImpl implements ServicePropertyContext {
	
	// Map with property key as key and the map of service.id and value-set as value
	final Map<String, Map<Long, Set<String>>> keyMapPairs = new ConcurrentHashMap<>();
	// Map with service.id and corresponding sub-context instance as value
	private final Map<Long, ServicePropertyContext> subContextMap = new ConcurrentHashMap<>();
	// Set of all available properties in EMF OSGi
	private static Set<String> validKeys = new HashSet<>();
	private final Map<String, Object> customFeatureProperties = new ConcurrentHashMap<>();
	
	static {
		validKeys.add(EMFNamespaces.EMF_CONFIGURATOR_NAME);
		validKeys.add(EMFNamespaces.EMF_MODEL_NAME);
		validKeys.add(EMFNamespaces.EMF_MODEL_NSURI);
		validKeys.add(EMFNamespaces.EMF_MODEL_FEATURE);
		validKeys.add(EMFNamespaces.EMF_MODEL_VERSION);
		validKeys.add(EMFNamespaces.EMF_MODEL_CONTENT_TYPE);
		validKeys.add(EMFNamespaces.EMF_MODEL_FILE_EXT);
		validKeys.add(EMFNamespaces.EMF_MODEL_PROTOCOL);
	}
	
	/**
	 * Creates a new instance.
	 */
	ServicePropertyContextImpl() {
		init();
	}

	/* 
	 * (non-Javadoc)
	 * @see org.gecko.emf.osgi.helper.ServicePropertyContext#updateServiceProperties(java.util.Map)
	 */
	@Override
	public void updateServiceProperties(Map<String, Object> serviceProperties) {
		requireNonNull(serviceProperties);
		Long serviceId = validateServiceId(serviceProperties);
		updateFeatureProperties(serviceProperties);
		keyMapPairs.keySet().forEach(key->updateProperties(key, serviceProperties, serviceId));
	}

	/* 
	 * (non-Javadoc)
	 * @see org.gecko.emf.osgi.helper.ServicePropertyContext#getDictionary(boolean)
	 */
	@Override
	public Dictionary<String, Object> getDictionary(boolean merged) {
		Dictionary<String, Object> properties = new Hashtable<>();
		keyMapPairs.forEach((key, map)->appendToDictionary(key, map, properties));
		customFeatureProperties.forEach(properties::put);
		if (!merged) {
			return properties;
		} else {
			ServicePropertyContext[] subContexts;
			synchronized (subContextMap) {
				subContexts = subContextMap.values().toArray(new ServicePropertyContext[subContextMap.size()]);
			}
			return merge(subContexts).getDictionary(false);
		}
	}

	/* 
	 * (non-Javadoc)
	 * @see org.gecko.emf.osgi.helper.ServicePropertyContext#getProperties(boolean)
	 */
	@Override
	public Map<String, Object> getProperties(boolean merged) {
		return FrameworkUtil.asMap(getDictionary(merged));
	}
	
	/* 
	 * (non-Javadoc)
	 * @see org.gecko.emf.osgi.helper.ServicePropertyContext#addSubContext(java.util.Map)
	 */
	@Override
	public ServicePropertyContext addSubContext(Map<String, Object> subContextProperties) {
		requireNonNull(subContextProperties);
		return ServicePropertiesHelper.
				getServiceId(subContextProperties).
				map(id->addSubContext(id, subContextProperties)).
				orElseThrow(()->new IllegalStateException("The sub-context properties must contains a service.id entry with a long value"));
	}

	/* 
	 * (non-Javadoc)
	 * @see org.gecko.emf.osgi.helper.ServicePropertyContext#removeSubContext(java.util.Map)
	 */
	@Override
	public ServicePropertyContext removeSubContext(Map<String, Object> subContextProperties) {
		requireNonNull(subContextProperties);
		validateServiceId(subContextProperties);
		return ServicePropertiesHelper.
				getServiceId(subContextProperties).
				map(this::removeSubContext).
				orElse(null);
	}

	/* 
	 * (non-Javadoc)
	 * @see org.gecko.emf.osgi.helper.ServicePropertyContext#merge(org.gecko.emf.osgi.helper.ServicePropertyContext)
	 */
	@Override
	public ServicePropertyContext merge(ServicePropertyContext ...toMerge) {
		if (isNull(toMerge)) {
			return this;
		}
		ServicePropertyContextImpl merged = new ServicePropertyContextImpl();
		doMerge(this, merged);
		for (ServicePropertyContext mergeCtx : toMerge) {
			if (mergeCtx instanceof ServicePropertyContextImpl) {
				doMerge((ServicePropertyContextImpl) mergeCtx, merged);
			}
		}
		return merged;
	}
	
	/**
	 * Extract all properties that start  with the given feature prefix 
	 * @param serviceProperties the service properties
	 */
	protected void updateFeatureProperties(Map<String, Object> serviceProperties) {
		requireNonNull(serviceProperties);
		
		Map<String, Object> featureProperties = filterProperties(EMFNamespaces.EMF_MODEL_FEATURE + ".", serviceProperties);
		featureProperties.forEach((key, value)->{
			// append to customFeatureMap
			customFeatureProperties.computeIfPresent(key, (customKey, customValue) -> {
				Set<Object> result = new HashSet<>();
				result.addAll(Arrays.asList((Object[])customValue));
				Object[] newValue = ServicePropertiesHelper.createObjectPlusValue(value);
				if (nonNull(newValue)) {
					result.addAll(Arrays.asList(newValue));
				}
				return result.toArray();
			});
			customFeatureProperties.computeIfAbsent(key, customKey ->ServicePropertiesHelper.createObjectPlusValue(value));
		});
	}
	
	/**
	 * Updates all the needed service properties 
	 * @param key the property key
	 * @param serviceProperties the service properties to look into
	 */
	protected void updateProperties(String key, Map<String, Object> serviceProperties, Long serviceId) {
		requireNonNull(key);
		requireNonNull(serviceProperties);
		requireNonNull(serviceId);
		
		Set<String> nameSet = getStringPlusValue(serviceProperties, key);
		if (nonNull(nameSet)) {
			nameSet = new HashSet<>(nameSet);
			Map<Long, Set<String>> keyValueMap = keyMapPairs.get(key);
			if (nonNull(keyValueMap)) {
				ServicePropertiesHelper.updateNameMap(keyValueMap, nameSet, serviceId);
			}
		}
	}

	/**
	 * Add the given service properties as sub-context for the given service id and returns the corresponding instance
	 * @param serviceId the service id to register the sub-context
	 * @param subContextProperties the service properties from the sub-context
	 * @return the {@link ServicePropertyContext} instance
	 */
	protected ServicePropertyContext addSubContext(long serviceId, Map<String, Object> subContextProperties) {
		ServicePropertyContext subContext = nonNull(subContextProperties) ? 
				ServicePropertyContext.create(subContextProperties) : 
					new ServicePropertyContextImpl() ;
		synchronized (subContextMap) {
			subContextMap.put(serviceId, subContext);
		}
		return subContext;
	}

	/**
	 * Removes the {@link ServicePropertyContext} for the given service id and returns it or <code>null</code> 
	 * @param serviceId the service id to remove the {@link ServicePropertyContext} for
	 * @return the {@link ServicePropertyContext} to be removed or <code>null</code> 
	 */
	protected ServicePropertyContext removeSubContext(long serviceId) {
		ServicePropertyContext subContext = null;
		synchronized (subContextMap) {
			subContext = subContextMap.remove(serviceId);
		}
		return isNull(subContext) ? null : subContext;
	}

	/**
	 * Validates, if the provided service properties contains a service.id entry with a Long value.
	 * It returns this value otherwise a {@link IllegalStateException} is thrown 
	 * @param serviceProperties the properties to check
	 * @return the service id value
	 */
	protected Long validateServiceId(Map<String, Object> serviceProperties) {
		Optional<Long> serviceIdOpt = ServicePropertiesHelper.getServiceId(serviceProperties);
		if (!serviceIdOpt.isPresent()) {
			throw new IllegalStateException("Service Properties must contains an entry for the service.id");
		}
		return serviceIdOpt.get();
	}

	/**
	 * We assign each property key to its own map instance
	 */
	private void init() {
		validKeys.forEach(key->keyMapPairs.put(key, new ConcurrentHashMap<>()));
	}

	/**
	 * Merges the source context into the target context
	 * @param source the source context
	 * @param target the target context
	 */
	private void doMerge(ServicePropertyContextImpl source, ServicePropertyContextImpl target) {
		requireNonNull(source);
		requireNonNull(target);
		target.updateFeatureProperties(source.customFeatureProperties);
		source.keyMapPairs.forEach((key, map)->ServicePropertiesHelper.mergeNameMap(map, target.keyMapPairs.get(key)));
	}
	
}
