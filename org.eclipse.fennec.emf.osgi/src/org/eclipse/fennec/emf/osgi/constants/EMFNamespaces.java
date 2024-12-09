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
package org.eclipse.fennec.emf.osgi.constants;

import org.eclipse.fennec.emf.osgi.annotation.EMFModel;

/**
 * 
 * @author Juergen Albert
 * @since 9 Feb 2018
 */
public class EMFNamespaces {
	
	private EMFNamespaces() {
	}

	public static final String EMF_NAMESPACE = "emf.core"; //$NON-NLS-1$
	// Attribute PREFIX for the different configurators and capability namespace
	public static final String EMF_CONFIGURATOR_NAMESPACE = "emf.configurator"; //$NON-NLS-1$
	
	/**
	 * Attribute name constants for {@link EMFConfigurator} annotation
	 */

//	public static final String EMF_CONFIGURATOR_PREFIX = EMF_CONFIGURATOR_NAMESPACE + "."; //$NON-NLS-1$
//	public static final String EMF_RESOURCE_FACTORY_CONFIGURATOR_NAME = EMF_CONFIGURATOR_PREFIX + "resourceFactory"; //$NON-NLS-1$
	
	/**
	 * Attribute name constants for {@link EMFModel} annotation.
	 * They provide additional information about a model, through EPackages our also ResourceFactories or configurators
	 */
	// Attribute PREFIX for the EMF models and confiogurators
	public static final String EMF_PREFIX = "emf.";
	// Attribute name constants for {@link EMFConfigurator} annotation
	public static final String EMF_CONFIGURATOR_NAME = EMF_PREFIX + "configuratorName"; //$NON-NLS-1$
	// Attribute PREFIX for the EMF model
//	public static final String EMF_MODEL_PREFIX = "emf.model.";
	// Attribute name for the EMF model name
	public static final String EMF_MODEL_NAME = EMF_PREFIX + "name";
	// Attribute name for the EMF model name
	public static final String EMF_MODEL_REGISTRATION = EMF_PREFIX + "registration";
	// Attribute name for the EMF model namespace
	public static final String EMF_MODEL_NSURI = EMF_PREFIX + "nsURI";
	// Attribute name for the EMF model content type
	public static final String EMF_MODEL_CONTENT_TYPE = EMF_PREFIX + "contentType";
	// Attribute name for the EMF model protocol
	public static final String EMF_MODEL_PROTOCOL = EMF_PREFIX + "protocol";
	// Attribute name for the EMF model file extension
	public static final String EMF_MODEL_FILE_EXT = EMF_PREFIX + "fileExtension";
	// Attribute name for the EMF model version
	public static final String EMF_MODEL_VERSION = EMF_PREFIX + "version";
	// Attribute name for the EMF model feature
	public static final String EMF_MODEL_FEATURE = EMF_PREFIX + "feature";
	// Attribute name for the EMF dynamic model configurator ecore path '<bsn>:(<version>)/(<path>)/<file>.ecore'
	public static final String EMF_MODEL_DYNAMIC_URI = EMF_PREFIX + "dynamicEcoreUri";
	
	/**
	 * Constants for the isolated configurable resource set factory
	 */
	// property name for the resource set factory name
	public static final String PROP_RESOURCE_SET_FACTORY_NAME = "rsf.name";
	// property name for the resource set factory model target filter
	public static final String PROP_MODEL_TARGET_FILTER = "rsf.model.target.filter";
	
	// Configuration pid for the EPackage registry 
	public static final String EPACKAGE_REGISTRY_CONFIG_NAME = "EPackageRegistry";
	// Configuration pid for the ResourceFactory registry 
	public static final String RESOURCE_FACTORY_CONFIG_NAME = "ResourceFactoryRegistry";
	// Configuration pid for the ResourceSetFactory registry 
	public static final String RESOURCE_SET_FACTORY_CONFIG_NAME = "ResourceSetFactory";
	// Configuration pid for the isolated ResourceSetFactrory configuration component 
	public static final String ISOLATED_RESOURCE_SET_FACTORY_CONFIG_NAME = "IsolatedResourceSetFactory";
	// Configuration pid for the dynamic EPackage and ResourceFactory configurator registry 
	public static final String DYNAMIC_MODEL_CONFIGURATOR_CONFIG_NAME = "DynamicModelConfigurator";
	// Configuration target filter property for the EPackage registry 
	public static final String EPACKAGE_REGISTRY_TARGET = "ePackageRegistry.target";
	// Configuration target filter property for the ResourceFactory registry 
	public static final String RESOURCE_FACTORY_REGISTRY_TARGET = "resourceFactoryRegistry.target";
	// Configuration target filter property for the EPackage configurators 
	public static final String EPACKAGE_TARGET = "ePackageConfigurator.target";
	// Configuration target filter property for the ResourceFactory configurators 
	public static final String RESOURCE_FACTORY_TARGET = "resourceFactoryConfigurator.target";
	
	// Name for the EMF ecore model extender
	public static final String EMF_MODEL_EXTENDER_NAME = "emf.model";
	// Requirement property for model paths
	public static final String EMF_MODEL_EXTENDER_PROP_MODELS_NAME = "models";
	// Default model path to look into
	public static final String EMF_MODEL_EXTENDER_DEFAULT_PATH = "model";
	
	// Types for the EMF model registration information
	// The registration happened with generated code / a provided bundle
	public static final String MODEL_REGISTRATION_PROVIDED = "provided";
	// The registration happened for internal purposes
	public static final String MODEL_REGISTRATION_INTERNAL = "internal";
	// The dynamic package loading was used to register the EPackage
	public static final String MODEL_REGISTRATION_DYNAMIC = "dynamic";
	// The model extender was used to register the EPackage
	public static final String MODEL_REGISTRATION_EXTENDER = "extender";
	
}
