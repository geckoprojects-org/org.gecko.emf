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
package org.gecko.emf.osgi.components.dynamic;

import static java.util.Objects.requireNonNull;
import static org.gecko.emf.osgi.constants.EMFNamespaces.DYNAMIC_MODEL_CONFIGURATOR_CONFIG_NAME;

import java.io.IOException;
import java.util.Dictionary;
import java.util.Hashtable;
import java.util.Map;
import java.util.Objects;
import java.util.logging.Logger;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.gecko.emf.osgi.configurator.EPackageConfigurator;
import org.gecko.emf.osgi.constants.EMFNamespaces;
import org.gecko.emf.osgi.helper.ServicePropertiesHelper;
import org.osgi.annotation.bundle.Requirement;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceRegistration;
import org.osgi.namespace.implementation.ImplementationNamespace;
import org.osgi.resource.Namespace;
import org.osgi.service.cm.ConfigurationConstants;
import org.osgi.service.cm.ConfigurationException;
import org.osgi.service.cm.annotations.RequireConfigurationAdmin;
import org.osgi.service.component.ComponentServiceObjects;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.ConfigurationPolicy;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.component.annotations.Modified;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.metatype.annotations.Designate;

import aQute.bnd.annotation.service.ServiceCapability;

/**
 * A URL can be configured, where a ecore is expected. The {@link EPackage} will then be loaded and together with a {@link EPackageConfigurator} registered for further use. 
 * Only the first EPackage is registered.
 * 
 * The {@link EPackage} and {@link EPackageConfigurator} will be registered with the properties {@link EMFNamespaces#EMF_MODEL_NAME} and {@link EMFNamespaces#EMF_MODEL_NSURI}. 
 * Additional properties can be defined with the prefix "additional.*". The prefix will be cut before used as registering properties.   
 * 
 * @author Juergen Albert
 * @since 16.03.2022
 */
@Component(name=DYNAMIC_MODEL_CONFIGURATOR_CONFIG_NAME, configurationPolicy = ConfigurationPolicy.REQUIRE)
@Designate(ocd = org.gecko.emf.osgi.components.dynamic.DynamicEMFModel.class, factory = true)
@RequireConfigurationAdmin
@Requirement(namespace = ImplementationNamespace.IMPLEMENTATION_NAMESPACE, //
	name = ConfigurationConstants.CONFIGURATION_ADMIN_IMPLEMENTATION, //
	version = ConfigurationConstants.CONFIGURATION_ADMIN_SPECIFICATION_VERSION,
	resolution = Namespace.RESOLUTION_OPTIONAL)
@ServiceCapability(EPackage.class)
@ServiceCapability(EPackageConfigurator.class)
public class DynamicPackageLoader{
	
	@Reference
	private ComponentServiceObjects<ResourceSet> resourceSetServiceObjects;
	
	private static final Logger logger = Logger.getLogger(DynamicPackageLoader.class.getName());
	
	private EPackage dynamicPackage = null;
	private BundleContext ctx;
	private DynamicEMFModel modelConfig;

	private ServiceRegistration<EPackage> packageRegistration;
	private ServiceRegistration<EPackageConfigurator> configuratorRegistration;


	/**
	 * Called on components activation
	 * @param ctx the bundle context
	 * @param modelConfig the config object
	 * @param properties the properties map
	 * @throws ConfigurationException
	 */
	@Activate
	public void activate(BundleContext ctx, DynamicEMFModel modelConfig, Map<String, Object> properties) throws ConfigurationException {
		logger.info("Trying to load Package for " + modelConfig.dynamicEcoreUri());
		this.ctx = ctx;
		this.modelConfig = modelConfig;
		registerModel(modelConfig, properties);
	}

	/**
	 * Called on components modification
	 * @param modelConfig the config object
	 * @param properties the properties map
	 * @throws ConfigurationException
	 */
	@Modified
	public void modified(DynamicEMFModel modelConfig, Map<String, Object> properties) throws ConfigurationException {
		logger.info("Trying to update Package for " + modelConfig.dynamicEcoreUri());
		if (!modelConfig.dynamicEcoreUri().equalsIgnoreCase(this.modelConfig.dynamicEcoreUri())) {
			unregisterModel();
			registerModel(modelConfig, properties);
		} else {
			updateModelProperties(modelConfig, properties);
		}
	}
	
	/**
	 * Called on de-activation
	 */
	@Deactivate
	public void deactivate() {
		unregisterModel();
	}

	/**
	 * Loads the model from the given URL
	 * @param config the model config
	 */
	private void loadModel(DynamicEMFModel config) {
		ResourceSet resourceSet = resourceSetServiceObjects.getService();
		URI ecoreURI = URI.createURI(config.dynamicEcoreUri());
		try {
			Resource resource = resourceSet.createResource(ecoreURI);
			resource.load(null);
			
			if (resource.getContents().isEmpty()) {
				throw new IllegalStateException("Loaded ecore with no content '" + ecoreURI + "'");
			}
			dynamicPackage = (EPackage) resource.getContents().get(0);
			EcoreUtil.resolveAll(dynamicPackage);
			resource.setURI(URI.createURI(dynamicPackage.getNsURI()));
			resourceSet.getResources().clear();
		} catch (IOException e) {
			throw new IllegalStateException("Error loading ecore file at '" + ecoreURI + "'", e);
		} finally {
			resourceSetServiceObjects.ungetService(resourceSet);
		}
	}

	/**
	 * Returns the current service properties
	 * @param config the model config
	 * @param properties all provided properties
	 * @return the service properties
	 */
	private Dictionary<String, Object> getServiceProperties(DynamicEMFModel config, Map<String, Object> properties) {
		requireNonNull(config);
		requireNonNull(properties);
		
		Dictionary<String, Object> props = new Hashtable<>();
		props.put(EMFNamespaces.EMF_MODEL_NAME, dynamicPackage.getName());
		props.put(EMFNamespaces.EMF_MODEL_NSURI, dynamicPackage.getNsURI());
		props.put(EMFNamespaces.EMF_MODEL_REGISTRATION, EMFNamespaces.MODEL_REGISTRATION_DYNAMIC);
		if (config.feature().length > 0) {
			props.put(EMFNamespaces.EMF_MODEL_FEATURE, config.feature());
		}
		if (!config.version().isEmpty()) {
			props.put(EMFNamespaces.EMF_MODEL_VERSION, config.version());
		}
		// normalize properties with the prefix and remove the prefix from the properties keys
		Map<String, Object> featureProperties = ServicePropertiesHelper.filterProperties(EMFNamespaces.EMF_MODEL_FEATURE + ".", properties);
		requireNonNull(featureProperties);
		featureProperties.forEach(props::put);
		return props;
	}
	
	/**
	 * Registers a dynamic model 
	 * @param modelConfig the model config
	 * @param properties all service properties
	 * @throws ConfigurationException
	 */
	private void registerModel(DynamicEMFModel modelConfig, Map<String, Object> properties) throws ConfigurationException {
		try {
			loadModel(modelConfig);
		} catch (Exception e) {
			throw new ConfigurationException("url", "The EMF model at " + modelConfig.dynamicEcoreUri() + " could not be loaded.", e);
		}
		Dictionary<String, Object> serviceProps = getServiceProperties(modelConfig, properties);
		EPackage.Registry.INSTANCE.put(dynamicPackage.getNsURI(),dynamicPackage);
		configuratorRegistration = ctx.registerService(EPackageConfigurator.class, new DynamicPackageConfiguratorImpl(dynamicPackage), serviceProps);
		packageRegistration =  ctx.registerService(EPackage.class, dynamicPackage, serviceProps);
	}

	/**
	 * Updates the current registration properties
	 * @param modelConfig the model config
	 * @param properties all service properties
	 */
	private void updateModelProperties(DynamicEMFModel modelConfig, Map<String, Object> properties) {
		requireNonNull(packageRegistration);
		requireNonNull(configuratorRegistration);
		Dictionary<String, Object> serviceProps = getServiceProperties(modelConfig, properties);
		packageRegistration.setProperties(serviceProps);
		configuratorRegistration.setProperties(serviceProps);
	}

	/**
	 * Un-registers the dynamic model and cleans up the resources
	 */
	private void unregisterModel() {
		packageRegistration.unregister();
		configuratorRegistration.unregister();
		EPackage.Registry.INSTANCE.remove(dynamicPackage.getNsURI());
		if (Objects.nonNull(dynamicPackage.eResource())) {
			dynamicPackage.eResource().unload();
			dynamicPackage = null;
		}
	}
}
