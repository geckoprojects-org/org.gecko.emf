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

import static java.util.Objects.isNull;
import static java.util.Objects.requireNonNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Dictionary;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.concurrent.atomic.AtomicReference;

import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.Resource.Factory;
import org.eclipse.emf.ecore.resource.Resource.Factory.Registry;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.eclipse.fennec.emf.osgi.ResourceSetFactory;
import org.eclipse.fennec.emf.osgi.configurator.EPackageConfigurator;
import org.eclipse.fennec.emf.osgi.configurator.ResourceFactoryConfigurator;
import org.eclipse.fennec.emf.osgi.configurator.ResourceSetConfigurator;
import org.eclipse.fennec.emf.osgi.constants.EMFNamespaces;
import org.eclipse.fennec.emf.osgi.factory.ResourceSetPrototypeFactory;
import org.eclipse.fennec.emf.osgi.helper.ServicePropertiesHelper;
import org.eclipse.fennec.emf.osgi.helper.ServicePropertyContext;
import org.osgi.framework.Constants;
import org.osgi.framework.FrameworkUtil;
import org.osgi.framework.PrototypeServiceFactory;
import org.osgi.framework.ServiceReference;
import org.osgi.framework.ServiceRegistration;
import org.osgi.service.component.ComponentConstants;
import org.osgi.service.component.ComponentContext;
import org.osgi.service.condition.Condition;

/**
 * Implementation of a {@link ResourceSetFactory}. It hold the {@link EPackage} registry as well as the {@link Factory} registry.
 * {@link EPackage} are dynamically injected as {@link EPackageProvider} instance. 
 * {@link Factory} instance are injected dynamically as {@link ServiceReference} instance. So they can be registered using
 * their properties for contentTyp or fileExtension.
 * Third additional {@link ResourceSetConfigurator} instance can be injected to customize the {@link ResourceSet} for
 * further extension like custom serialization. 
 * @author Mark Hoffmann
 * @since 28.06.2017
 */
public class DefaultResourceSetFactory implements ResourceSetFactory {

	private final Set<ResourceSetConfigurator> resourceSetConfigurators = new CopyOnWriteArraySet<>();
	private final Set<EPackageConfigurator> ePackageConfigurators = new CopyOnWriteArraySet<>();
	private final Set<ResourceFactoryConfigurator> resourceFactoryConfigurators = new CopyOnWriteArraySet<>();
	private final ServicePropertyContext propertyContext = ServicePropertyContext.create();
	private final AtomicReference<Resource.Factory.Registry> resourceFactoryRegistry = new AtomicReference<>();
	protected EPackage.Registry packageRegistry;
	protected ServiceRegistration<ResourceSetFactory> rsfRegistration = null;
	protected ServiceRegistration<ResourceSet> rsRegistration = null;
	protected ServiceRegistration<Condition> conditionRegistration = null;
	private long serviceChangeCount = 0;
	
	/**
	 * Returns the propertyContext.
	 * @return the propertyContext
	 */
	public ServicePropertyContext getPropertyContext() {
		return propertyContext;
	}
	
	/**
	 * Returns the resource factory registry
	 * @return the resource factory registry
	 */
	public AtomicReference<Resource.Factory.Registry> getResourceFactoryRegistry() {
		return resourceFactoryRegistry;
	}
	
	/**
	 * Set the {@link EPackage.Registry}
	 * @param registry the {@link EPackage} registry to set
	 */
	protected void setEPackageRegistry(EPackage.Registry registry) {
		this.packageRegistry = registry;
		updatePackageRegistry();
	}
	
	/**
	 * Un-set the {@link EPackage} registry
	 * @param registry the {@link EPackage} registry to be removed
	 */
	protected void unsetEPackageRegistry(EPackage.Registry registry) {
		this.packageRegistry.clear();
		this.packageRegistry = null;
	}

	/**
	 * Set a {@link Registry} for resource factories. It atomically calls all configurators with the new registry and 
	 * updates the service properties using the {@link ServicePropertyContext}
	 * @param registry the resource factory registry to be injected
	 * @param properties the service properties of this registry service
	 */
	protected void setResourceFactoryRegistry(Resource.Factory.Registry registry, Map<String, Object> properties) {
		this.resourceFactoryRegistry.updateAndGet(rfr-> {
			updateResourceFactoryRegistry(registry);
			ServicePropertyContext ctx = getPropertyContext();
			synchronized (ctx) {
				ctx.addSubContext(properties);
			}
			return registry;
		});
		updateRegistrationProperties();
	}

	/**
	 * Modifies a {@link Registry} for resource factories. It atomically updates the service properties.
	 * @param registry the resource factory to be injected
	 * @param properties the service properties of this registry service
	 */
	protected void modifiedResourceFactoryRegistry(Resource.Factory.Registry registry, Map<String, Object> properties) {
		this.resourceFactoryRegistry.getAndUpdate(rfr->{
			ServicePropertyContext ctx = getPropertyContext();
			synchronized (ctx) {
				ctx.addSubContext(properties);
			}
			return rfr;
		});
		updateRegistrationProperties();
	}
	
	/**
	 * Removes the {@link Resource.Factory.Registry} for resource factories. It atomically removed the service properties using the {@link ServicePropertyContext},
	 * calls the un-configure callbacks of the configurators and removed the instance.
	 * @param registry the registry to be removed
	 * @param properties the service properties of this registry service
	 */
	protected void unsetResourceFactoryRegistry(Resource.Factory.Registry registry, Map<String, Object> properties) {
		this.resourceFactoryRegistry.getAndUpdate(rfr->{
			ServicePropertyContext ctx = getPropertyContext();
			synchronized (ctx) {
				ctx.removeSubContext(properties);
			}
			disposeResourceFactoryRegistry(rfr);
			return null;
		});
		updateRegistrationProperties();
	}

	/**
	 * Adds {@link EPackageConfigurator}, to register a new {@link EPackage}
	 * @param configurator the {@link EPackageConfigurator} to be registered
	 * @param properties the service properties
	 */
	protected void addEPackageConfigurator(EPackageConfigurator configurator, Map<String, Object> properties) {
		synchronized (ePackageConfigurators) {
			ePackageConfigurators.add(configurator);
		}
		if (packageRegistry != null) {
			configurator.configureEPackage(packageRegistry);
		}
		getPropertyContext().addSubContext(properties);
		updateRegistrationProperties();
	}

	/**
	 * Removes a {@link EPackageConfigurator} from the registry and unconfigures it
	 * @param configurator the configurator to be removed
	 * @param modelInfo the model information
	 * @param properties the service properties
	 */
	protected void removeEPackageConfigurator(EPackageConfigurator configurator, Map<String, Object> properties) {
		getPropertyContext().removeSubContext(properties);
		updateRegistrationProperties();
		synchronized (ePackageConfigurators) {
			ePackageConfigurators.remove(configurator);
		}
		Object nsUris = properties.get(EMFNamespaces.EMF_MODEL_NSURI);
		if (packageRegistry != null) {
			configurator.unconfigureEPackage(packageRegistry);
			if (nsUris != null && (nsUris instanceof String || nsUris instanceof String[])) {
				List<String> nsUriList = nsUris instanceof String ? 
						Collections.singletonList((String)nsUris) : Arrays.asList((String[])nsUris);
				for (String nsUri : nsUriList) {
					EPackage.Registry.INSTANCE.remove(nsUri);
				}
			}
		}
	}

	/**
	 * Adds a resource factory configurator to the registry 
	 * @param configurator the resource factory configurator to be registered
	 * @param properties the service properties
	 */
	protected void addResourceFactoryConfigurator(ResourceFactoryConfigurator configurator, Map<String, Object> properties) {
		synchronized (resourceFactoryConfigurators) {
			resourceFactoryConfigurators.add(configurator);
		}
		Factory.Registry rfr = resourceFactoryRegistry.get();
		if (rfr != null) {
			configurator.configureResourceFactory(rfr);
		}
		getPropertyContext().addSubContext(properties);
		updateRegistrationProperties();
	}

	/**
	 * Removes a resource factory configurator from the registry
	 * @param configurator the resource factory configurator to be removed
	 * @param properties the service properties
	 */
	protected void removeResourceFactoryConfigurator(ResourceFactoryConfigurator configurator, Map<String, Object> properties) {
		getPropertyContext().removeSubContext(properties);
		updateRegistrationProperties();
		resourceFactoryConfigurators.remove(configurator);
		Factory.Registry rfr = resourceFactoryRegistry.get();
		if (rfr != null) {
			configurator.unconfigureResourceFactory(rfr);
		}
	}

	/**
	 * Adds new {@link ResourceSetConfigurator} to this factory
	 * @param resourceSetConfigurator the new configurator to be added
	 * @param properties the service properties
	 */
	protected void addResourceSetConfigurator(ResourceSetConfigurator resourceSetConfigurator, Map<String, Object> properties) {
		resourceSetConfigurators.add(resourceSetConfigurator);
		getPropertyContext().addSubContext(properties);
		updateRegistrationProperties();
	}
	
	/**
	 * Removes a {@link ResourceSetConfigurator} from the list for this factory
	 * @param resourceSetConfigurator
	 * @param properties the service properties
	 */
	protected void removeResourceSetConfigurator(ResourceSetConfigurator resourceSetConfigurator, Map<String, Object> properties) {
		getPropertyContext().removeSubContext(properties);
		updateRegistrationProperties();
		resourceSetConfigurators.remove(resourceSetConfigurator);
	}

	/**
	 * Called on component activation
	 * @param ctx the component context
	 */
	protected void activate(ComponentContext ctx) {
		packageRegistry.putAll(EPackage.Registry.INSTANCE);
		Factory.Registry rfr = resourceFactoryRegistry.get();
		Objects.requireNonNull(rfr);
		rfr.getExtensionToFactoryMap().putAll(Resource.Factory.Registry.INSTANCE.getExtensionToFactoryMap());
		rfr.getContentTypeToFactoryMap().putAll(Resource.Factory.Registry.INSTANCE.getContentTypeToFactoryMap());
		rfr.getProtocolToFactoryMap().putAll(Resource.Factory.Registry.INSTANCE.getProtocolToFactoryMap());
		registerServices(ctx);
	}

	/**
	 * Registers the {@link ResourceSet} and {@link ResourceSetFactory}
	 * @param ctx the component context
	 */
	protected void registerServices(ComponentContext ctx) {
		Dictionary<String, Object> props = getDictionary();
		rsfRegistration = ctx.getBundleContext().registerService(ResourceSetFactory.class, this, copyDictionary(props));
		PrototypeServiceFactory<ResourceSet> protoFactory = new ResourceSetPrototypeFactory(this);
		rsRegistration = ctx.getBundleContext().registerService(ResourceSet.class, protoFactory, copyDictionary(props));
		conditionRegistration = ctx.getBundleContext().registerService(Condition.class, Condition.INSTANCE, copyDictionaryForCondition(props));
	}
	
	/**
	 * De-registers the {@link ResourceSet} and {@link ResourceSetFactory}
	 */
	protected void unregisterServices() {
		if (rsfRegistration != null) {
			rsfRegistration.unregister();
			rsfRegistration = null;
		}
		if (rsRegistration != null) {
			rsRegistration.unregister();
			rsRegistration = null;
		}
		if (conditionRegistration != null) {
			conditionRegistration.unregister();
			conditionRegistration = null;
		}
	}
	
	/**
	 * Called on component deactivation
	 */
	protected void deactivate() {
		unregisterServices();
	}
	
	/**
	 * Internally creates a {@link ResourceSet} instance
	 * @return the {@link ResourceSet} instance
	 */
	protected ResourceSet internalCreateResourceSet() {
		return new ResourceSetImpl();
	}

	@Override
	public ResourceSet createResourceSet() {
		Factory.Registry rfr = resourceFactoryRegistry.get();
		if (isNull(rfr)) {
			throw new IllegalStateException("There is no Resource Factory Registry available. This should not happen");
		}
		ResourceSet resourceSet = internalCreateResourceSet();
		resourceSet.setPackageRegistry(new DelegatingEPackageRegistry(packageRegistry));
		resourceSet.setResourceFactoryRegistry(new DelegatingResourceFactoryRegistry(rfr));
		resourceSetConfigurators.forEach(c->c.configureResourceSet(resourceSet));
		return resourceSet;
	}

	@Override
	public Collection<ResourceSetConfigurator> getResourceSetConfigurators() {
		return Collections.unmodifiableCollection(resourceSetConfigurators);
	}

	/**
	 * Updates the package registry
	 */
	protected void updatePackageRegistry() {
		List<EPackageConfigurator> providers = new ArrayList<>(ePackageConfigurators);
		providers.forEach(p->p.configureEPackage(packageRegistry));
	}
	
	/**
	 * Updates the resource factory registry
	 * Register the XML resource factory to handle XML files 
	 */
	protected void updateResourceFactoryRegistry(final Factory.Registry rfRegistry) {
		requireNonNull(rfRegistry);
		List<ResourceFactoryConfigurator> providers = new ArrayList<>(resourceFactoryConfigurators);
		providers.forEach(p->p.configureResourceFactory(rfRegistry));
	}
	
	/**
	 * Updates the resource factory registry
	 * Register the XML resource factory to handle XML files 
	 */
	protected void disposeResourceFactoryRegistry(final Factory.Registry rfRegistry) {
		requireNonNull(rfRegistry);
		List<ResourceFactoryConfigurator> providers = new ArrayList<>(resourceFactoryConfigurators);
		providers.forEach(p->p.unconfigureResourceFactory(rfRegistry));
	}
	
	/**
	 * Updates the service registration properties
	 */
	protected void updateRegistrationProperties() {
		if(rsfRegistration != null || rsRegistration != null || conditionRegistration != null) {
			Dictionary<String, Object> dictionary = getDictionary();
			if (rsfRegistration != null) {
				rsfRegistration.setProperties(copyDictionary(dictionary));
			}
			if (rsRegistration != null) {
				rsRegistration.setProperties(copyDictionary(dictionary));
			}
			if (conditionRegistration != null) {
				conditionRegistration.setProperties(copyDictionaryForCondition(dictionary));
			}
		}
	}

	/**
	 * @param dictionary the dictionary to copy
	 * @return a copy of the original Dictionary
	 */
	private Dictionary<String, Object> copyDictionaryForCondition(Dictionary<String, Object> dictionary) {
		Dictionary<String, Object> copy = copyDictionary(dictionary);
		copy.put(Condition.CONDITION_ID, CONDITION_ID);
		return copy;
	}
	
	/**
	 * @param dictionary the dictionary to copy
	 * @return a copy of the original Dictionary
	 */
	private Dictionary<String, Object> copyDictionary(Dictionary<String, Object> dictionary) {
		Dictionary<String, Object> props = new Hashtable<>();
		Enumeration<String> enumeration = dictionary.keys();
		while (enumeration.hasMoreElements()) {
			String string = enumeration.nextElement();
			props.put(string, dictionary.get(string));
		}
		return props;
	}

	/**
	 * Creates a dictionary for the stored properties
	 * @return a dictionary for the stored properties
	 */
	protected Dictionary<String, Object> getDictionary() {
		Dictionary<String, Object> properties = propertyContext.getDictionary(true);
		Map<String, Object> features = ServicePropertiesHelper.normalizeProperties(EMFNamespaces.EMF_MODEL_FEATURE + ".", FrameworkUtil.asMap(properties));
		features.forEach(properties::put);
		properties.put(ComponentConstants.COMPONENT_NAME, "DefaultResourcesetFactory");
		properties.put(Constants.SERVICE_CHANGECOUNT, serviceChangeCount++);
		return properties;
	}

}
