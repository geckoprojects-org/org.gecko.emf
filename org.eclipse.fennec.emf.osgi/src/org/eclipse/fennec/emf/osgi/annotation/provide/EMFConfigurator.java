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
package org.eclipse.fennec.emf.osgi.annotation.provide;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.fennec.emf.osgi.annotation.ConfiguratorType;
import org.eclipse.fennec.emf.osgi.annotation.require.RequireEMF;
import org.eclipse.fennec.emf.osgi.configurator.ResourceFactoryConfigurator;
import org.eclipse.fennec.emf.osgi.constants.EMFNamespaces;
import org.osgi.annotation.bundle.Capability;
import org.osgi.service.component.annotations.ComponentPropertyType;

/**
 * A meta annotation for the {@link EMFNamespaces#EMF_RESOURCE_CONFIGURATOR_FEATURE} property of the {@link ResourceFactoryConfigurator} and EMF Whiteboard.
 * It can annotate a {@link ResourceFactoryConfigurator} or {@link Resource.Factory} 
 * @author Juergen Albert
 * @since 12 Feb 2018
 */
@ComponentPropertyType
@Retention(RetentionPolicy.CLASS)
@Target(ElementType.TYPE)
@Capability(
		namespace = EMFNamespaces.EMF_CONFIGURATOR_NAMESPACE, 
		name = "${#configuratorType}", 
		attribute = {
				EMFNamespaces.EMF_CONFIGURATOR_NAME + "=${#configuratorName}", 
				EMFNamespaces.EMF_MODEL_FEATURE + ":List<String>=\"${#feature}\"", 
				EMFNamespaces.EMF_MODEL_PROTOCOL + ":List<String>=\"${#protocol}\"", 
				EMFNamespaces.EMF_MODEL_FILE_EXT + ":List<String>=\"${#fileExtension}\"", 
				EMFNamespaces.EMF_MODEL_CONTENT_TYPE + ":List<String>=\"${#contentType}\""},
		version = "${#version}")
@RequireEMF
public @interface EMFConfigurator {
	String PREFIX_ = EMFNamespaces.EMF_PREFIX;

	ConfiguratorType configuratorType() default ConfiguratorType.EPACKAGE;
	String configuratorName();
	String[] feature() default "";
	String[] protocol() default "";
	String[] contentType() default "";
	String[] fileExtension() default "";
	String version() default "0.0.0";
	
}
