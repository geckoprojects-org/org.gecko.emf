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
package org.eclipse.fennec.emf.osgi.components.config;

import static java.util.Objects.isNull;
import static org.eclipse.fennec.emf.osgi.constants.EMFNamespaces.EMF_MODEL_NAME;
import static org.eclipse.fennec.emf.osgi.constants.EMFNamespaces.EPACKAGE_REGISTRY_CONFIG_NAME;
import static org.eclipse.fennec.emf.osgi.constants.EMFNamespaces.EPACKAGE_REGISTRY_TARGET;
import static org.eclipse.fennec.emf.osgi.constants.EMFNamespaces.EPACKAGE_TARGET;
import static org.eclipse.fennec.emf.osgi.constants.EMFNamespaces.ISOLATED_RESOURCE_SET_FACTORY_CONFIG_NAME;
import static org.eclipse.fennec.emf.osgi.constants.EMFNamespaces.PROP_MODEL_TARGET_FILTER;
import static org.eclipse.fennec.emf.osgi.constants.EMFNamespaces.PROP_RESOURCE_SET_FACTORY_NAME;
import static org.eclipse.fennec.emf.osgi.constants.EMFNamespaces.RESOURCE_FACTORY_CONFIG_NAME;
import static org.eclipse.fennec.emf.osgi.constants.EMFNamespaces.RESOURCE_FACTORY_REGISTRY_TARGET;
import static org.eclipse.fennec.emf.osgi.constants.EMFNamespaces.RESOURCE_FACTORY_TARGET;
import static org.eclipse.fennec.emf.osgi.constants.EMFNamespaces.RESOURCE_SET_FACTORY_CONFIG_NAME;

import java.io.IOException;
import java.util.Dictionary;
import java.util.Hashtable;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.osgi.annotation.versioning.ProviderType;
import org.osgi.framework.FrameworkUtil;
import org.osgi.framework.InvalidSyntaxException;
import org.osgi.service.cm.Configuration;
import org.osgi.service.cm.ConfigurationAdmin;
import org.osgi.service.cm.ConfigurationException;
import org.osgi.service.cm.annotations.RequireConfigurationAdmin;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.ConfigurationPolicy;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.component.annotations.Modified;
import org.osgi.service.component.annotations.Reference;

/**
 * COnfiguration component for a Isolated
 * @author mark
 * @since 14.10.2018
 */
@Component(configurationPid=ISOLATED_RESOURCE_SET_FACTORY_CONFIG_NAME, configurationPolicy = ConfigurationPolicy.REQUIRE)
@RequireConfigurationAdmin
@ProviderType
public class IsolatedResourceFactoryConfiguration {
	
	private static final Logger logger = Logger.getLogger(IsolatedResourceFactoryConfiguration.class.getName());
	@Reference
	private ConfigurationAdmin configAdmin;
	private Configuration eprConfig = null;
	private Configuration rfConfig = null;
	private Configuration rsfConfig = null;
	
	@Activate
	public void activate(Map<String, Object> properties) throws ConfigurationException {
		String name = (String) properties.get(PROP_RESOURCE_SET_FACTORY_NAME);
		if (name == null) {
			throw new ConfigurationException(PROP_RESOURCE_SET_FACTORY_NAME, "Missing resource set factory name");
		}
		String filter = getFilter(properties);
		try {
			eprConfig = configAdmin.getFactoryConfiguration(EPACKAGE_REGISTRY_CONFIG_NAME, name, "?");
			eprConfig.update(getRegistryProperties(name));
			rfConfig = configAdmin.getFactoryConfiguration(RESOURCE_FACTORY_CONFIG_NAME, name, "?");
			rfConfig.update(getRegistryProperties(name));
			rsfConfig = configAdmin.getFactoryConfiguration(RESOURCE_SET_FACTORY_CONFIG_NAME, name, "?");
			rsfConfig.update(getResourceSetFactoryProperties(name, filter));
		} catch (IOException e) {
			logger.log(Level.SEVERE, String.format("[%s] Error creating an isolated resource set factory", name), e);
		}
	}
	
	@Modified
	public void modified(Map<String, Object> properties) throws ConfigurationException {
		String name = (String) properties.get(PROP_RESOURCE_SET_FACTORY_NAME);
		if (name == null) {
			throw new ConfigurationException(PROP_RESOURCE_SET_FACTORY_NAME, "Missing resource set factory name");
		}
		String filter = getFilter(properties);
		try {
			if (eprConfig == null) {
				eprConfig = configAdmin.getFactoryConfiguration(EPACKAGE_REGISTRY_CONFIG_NAME, name, "?");
			}
			eprConfig.updateIfDifferent(getRegistryProperties(name));
			if (rfConfig == null) {
				rfConfig = configAdmin.getFactoryConfiguration(RESOURCE_FACTORY_CONFIG_NAME, name, "?");
			}
			rfConfig.updateIfDifferent(getRegistryProperties(name));
			if (rsfConfig == null) {
				rsfConfig = configAdmin.getFactoryConfiguration(RESOURCE_SET_FACTORY_CONFIG_NAME, name, "?");
			}
			rsfConfig.updateIfDifferent(getResourceSetFactoryProperties(name, filter));
		} catch (IOException e) {
			logger.log(Level.SEVERE, String.format("[%s] Error updating an isolated resource set factory", name), e);
		}
	}
	
	@Deactivate
	public void deactivate() {
		if (rsfConfig != null) {
			try {
				rsfConfig.delete();
			} catch (IOException e) {
				logger.log(Level.SEVERE, "Error removing an ResourceSetFactory configuration", e);
			}
		}
		if (eprConfig != null) {
			try {
				eprConfig.delete();
			} catch (IOException e) {
				logger.log(Level.SEVERE, "Error removing an EPackage registry configuration", e);
			}
		}
		if (rfConfig != null) {
			try {
				rfConfig.delete();
			} catch (IOException e) {
				logger.log(Level.SEVERE, "Error removing a ResourceFactory registry configuration", e);
			}
		}
	}
	
	/**
	 * Returns the calculated model target filter 
	 * @param properties the component properties
	 * @return the filter {@link String}
	 * @throws ConfigurationException
	 */
	private String getFilter(Map<String, Object> properties) throws ConfigurationException {
		String filterString = (String) properties.get(PROP_MODEL_TARGET_FILTER);
		if (isNull(filterString)) {
			filterString = "(" + EMF_MODEL_NAME + "=*)";
		}
		filterString = "(&(!(" + EMF_MODEL_NAME + "=ecore))" + filterString + ")";
		try {
			FrameworkUtil.createFilter(filterString);
		} catch (InvalidSyntaxException e1) {
			throw new ConfigurationException(PROP_MODEL_TARGET_FILTER, "Invalid target filter for models");
		}
		return filterString;
	}
	
	/**
	 * Returns the registry properties
	 * @param name the name of the new registry
	 * @return the properties
	 */
	private Dictionary<String, Object> getRegistryProperties(String name) {
		Dictionary<String, Object> properties = new Hashtable<>();
		properties.put(PROP_RESOURCE_SET_FACTORY_NAME, name);
		return properties;
	}
	
	/**
	 * Returns the resource set factory properties
	 * @param name the name of the new registry
	 * @param filter the target select filter
	 * @return the properties
	 */
	private Dictionary<String, Object> getResourceSetFactoryProperties(String name, String filter) {
		Dictionary<String, Object> properties = getRegistryProperties(name);
		properties.put(EPACKAGE_REGISTRY_TARGET, "(" + PROP_RESOURCE_SET_FACTORY_NAME + "=" + name + ")");
		properties.put(RESOURCE_FACTORY_REGISTRY_TARGET, "(" + PROP_RESOURCE_SET_FACTORY_NAME + "=" + name + ")");
		properties.put(EPACKAGE_TARGET, filter);
		properties.put(RESOURCE_FACTORY_TARGET, filter);
		return properties;
	}

}
