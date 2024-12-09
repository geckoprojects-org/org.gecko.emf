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
package org.eclipse.fennec.emf.osgi.components;

import static org.eclipse.fennec.emf.osgi.constants.EMFNamespaces.EMF_MODEL_NAME;

import java.util.Map;

import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.Resource.Factory;
import org.eclipse.emf.ecore.resource.Resource.Factory.Registry;
import org.eclipse.fennec.emf.osgi.ResourceSetFactory;
import org.eclipse.fennec.emf.osgi.configurator.EPackageConfigurator;
import org.eclipse.fennec.emf.osgi.configurator.ResourceFactoryConfigurator;
import org.eclipse.fennec.emf.osgi.configurator.ResourceSetConfigurator;
import org.eclipse.fennec.emf.osgi.constants.EMFNamespaces;
import org.eclipse.fennec.emf.osgi.constants.VersionConstant;
import org.eclipse.fennec.emf.osgi.ecore.EcoreConfigurator;
import org.eclipse.fennec.emf.osgi.ecore.EcorePackagesRegistrator;
import org.eclipse.fennec.emf.osgi.provider.DefaultResourceSetFactory;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.osgi.annotation.bundle.Capability;
import org.osgi.framework.BundleContext;
import org.osgi.framework.FrameworkUtil;
import org.osgi.framework.ServiceReference;
import org.osgi.service.component.ComponentContext;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.annotations.ReferenceCardinality;
import org.osgi.service.component.annotations.ReferencePolicy;

import aQute.bnd.annotation.service.ServiceCapability;

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
@Component(name="DefaultResourcesetFactory", enabled=true, immediate=true, service= {})
@Capability(
		namespace = EMFNamespaces.EMF_NAMESPACE,
		name = ResourceSetFactory.EMF_CAPABILITY_NAME,
		version = VersionConstant.GECKOPROJECTS_EMF_VERSION
		)
@ServiceCapability(value = ResourceSet.class)
@ServiceCapability(value = ResourceSetFactory.class)
public class DefaultResourceSetFactoryComponent extends DefaultResourceSetFactory {

	
	private ServiceReference<Registry> resourceFactoryRegistryReference;
	private BundleContext cxt;

	/**
	 * Called before component activation
	 * @param ctx the component context
	 */
	@Activate
	public DefaultResourceSetFactoryComponent(BundleContext ctx,
			@Reference(name="ePackageRegistry", unbind = "unsetRegistry")
			EPackage.Registry registry,
			@Reference(name="resourceFactoryRegistry")
			ServiceReference<Resource.Factory.Registry> resourceFactoryRegistryReference
			) {
		this.cxt = ctx;
		this.resourceFactoryRegistryReference = resourceFactoryRegistryReference;
		super.setEPackageRegistry(registry);
		super.setResourceFactoryRegistry(ctx.getService(resourceFactoryRegistryReference), FrameworkUtil.asMap(resourceFactoryRegistryReference.getProperties()));
		EcoreConfigurator ecoreConfigurator = new EcoreConfigurator();
		addEPackageConfigurator(ecoreConfigurator, EcoreConfigurator.PROPERTIES);
		addResourceFactoryConfigurator(ecoreConfigurator, EcoreConfigurator.PROPERTIES);
		EcorePackagesRegistrator.start();
	}
	
	/*
	 * We have a two step activation for historic reasons. We use the constructor injection to 
	 * make sure everything mandatory is available before activation has there had been cases, 
	 * where mandatory fields have been null at activation for some reason. Felix SCR will also
	 * look for a method named activate which is present through the DefaultResourceSetFactory
	 * and will call it after the Constructor, regardless of the fact that it is already activated 
	 * through the Constructor. So to avoid activating this twice by accident, we don't call the 
	 * super activate method in the constructor but let SCR run its course. The Activate 
	 * annotation is on here, to act as a marker.
	 *
	 * (non-Javadoc)
	 * @see org.gecko.emf.osgi.provider.DefaultResourceSetFactory#activate(org.osgi.service.component.ComponentContext)
	 */
	@Activate
	@Override
	public void activate(ComponentContext ctx) {
		super.activate(ctx);
	}
	
	/**
	 * Called on component deactivation
	 */
	@Deactivate
	@Override
	public void deactivate() {
		super.deactivate();
		cxt.ungetService(resourceFactoryRegistryReference);
		EcorePackagesRegistrator.stop();
	}

	protected void unsetRegistry(org.eclipse.emf.ecore.EPackage.Registry registry) {
		super.unsetEPackageRegistry(registry);
	}

	/**
	 * This is a bit of a hack. To make sure that we have the Registry as early as possible, we use constructor Injection. 
	 * We want to know about the property updates as well, which is not possible for constructor injected references. 
	 * We know that our Default registry is a singleton. Thus, we create a second Reference, that does nothing on set, 
	 * but reacts to changes of the properties. Not nice, but it works.   
	 * @param resourceFactoryRegistry the resource factory to be injected
	 */
	@Override
	@Reference(policy=ReferencePolicy.STATIC, unbind="unsetResourceFactoryRegistry", updated = "modifiedResourceFactoryRegistry")
	public void setResourceFactoryRegistry(Resource.Factory.Registry resourceFactoryRegistry, Map<String, Object> properties) {
//		Do nothing here
	}
	
	@Override
	public void modifiedResourceFactoryRegistry(Resource.Factory.Registry resourceFactoryRegistry, Map<String, Object> properties) {
		super.modifiedResourceFactoryRegistry(resourceFactoryRegistry, properties);
	}

	/**
	 * Removed the registry on shutdown
	 * @param resourceFactoryRegistry the registry to be removed
	 */
	@Override
	public void unsetResourceFactoryRegistry(Resource.Factory.Registry resourceFactoryRegistry, Map<String, Object> properties) {
		super.unsetResourceFactoryRegistry(resourceFactoryRegistry, properties);
		cxt.ungetService(resourceFactoryRegistryReference);
	}

	/**
	 * Injects {@link EPackageConfigurator}, to register a new {@link EPackage}
	 * @param configurator the {@link EPackageConfigurator} to be registered
	 * @param properties the service properties
	 */
	@Override
	@Reference(policy=ReferencePolicy.DYNAMIC, cardinality=ReferenceCardinality.MULTIPLE, target="(!(" + EMF_MODEL_NAME + "=ecore))", updated = "modifyEPackageConfigurator", unbind = "removeEPackageConfigurator")
	public void addEPackageConfigurator(EPackageConfigurator configurator, Map<String, Object> properties) {
		super.addEPackageConfigurator(configurator, properties);
	}
	
	/**
	 * Injects {@link EPackageConfigurator}, to update a new {@link EPackage}
	 * @param configurator the {@link EPackageConfigurator} to be updated
	 * @param properties the service properties
	 */
	public void modifyEPackageConfigurator(EPackageConfigurator configurator, Map<String, Object> properties) {
		super.addEPackageConfigurator(configurator, properties);
	}

	/**
	 * Removes a {@link EPackageConfigurator} from the registry and un-configures it
	 * @param configurator the configurator to be removed
	 * @param properties the service properties
	 */
	@Override
	public void removeEPackageConfigurator(EPackageConfigurator configurator, Map<String, Object> properties) {
		super.removeEPackageConfigurator(configurator, properties);
	}

	/**
	 * Adds a resource factory configurator to the registry 
	 * @param configurator the resource factory configurator to be registered
	 * @param properties the service properties
	 */
	@Override
	@Reference(policy=ReferencePolicy.DYNAMIC, cardinality=ReferenceCardinality.MULTIPLE, target="(!(" + EMF_MODEL_NAME + "=ecore))", updated = "modifyResourceFactoryConfigurator")
	public void addResourceFactoryConfigurator(ResourceFactoryConfigurator configurator, Map<String, Object> properties) {
		super.addResourceFactoryConfigurator(configurator, properties);
	}
	
	/**
	 * Modifies a resource factory configurator to the registry 
	 * @param configurator the resource factory configurator to be updated
	 * @param properties the service properties
	 */
	public void modifyResourceFactoryConfigurator(ResourceFactoryConfigurator configurator, Map<String, Object> properties) {
		super.addResourceFactoryConfigurator(configurator, properties);
	}

	/**
	 * Removes a resource factory configurator from the registry
	 * @param configurator the resource factory configurator to be removed
	 * @param properties the service properties
	 */
	@Override
	public void removeResourceFactoryConfigurator(ResourceFactoryConfigurator configurator, Map<String, Object> properties) {
		super.removeResourceFactoryConfigurator(configurator, properties);
	}

	/**
	 * Adds new {@link ResourceSetConfigurator} to this factory
	 * @param resourceSetConfigurator the new configurator to be added
	 * @param properties the service properties
	 */
	@Override
	@Reference(policy=ReferencePolicy.DYNAMIC, cardinality=ReferenceCardinality.MULTIPLE, updated = "modifyResourceSetConfigurator")
	public void addResourceSetConfigurator(ResourceSetConfigurator resourceSetConfigurator, Map<String, Object> properties) {
		super.addResourceSetConfigurator(resourceSetConfigurator, properties);
	}
	
	/**
	 * Modifies new {@link ResourceSetConfigurator} to this factory
	 * @param resourceSetConfigurator the new configurator to be modified
	 * @param properties the service properties
	 */
	public void modifyResourceSetConfigurator(ResourceSetConfigurator resourceSetConfigurator, Map<String, Object> properties) {
		super.addResourceSetConfigurator(resourceSetConfigurator, properties);
	}

	/**
	 * Removes a {@link ResourceSetConfigurator} from the list for this factory
	 * @param resourceSetConfigurator
	 * @param properties the service properties
	 */
	@Override
	public void removeResourceSetConfigurator(ResourceSetConfigurator resourceSetConfigurator, Map<String, Object> properties) {
		super.removeResourceSetConfigurator(resourceSetConfigurator, properties);
	}
}
