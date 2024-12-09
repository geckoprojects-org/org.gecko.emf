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
package org.eclipse.fennec.emf.osgi.components.dynamic;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.eclipse.fennec.emf.osgi.configurator.ResourceSetConfigurator;
import org.eclipse.fennec.emf.osgi.constants.EMFNamespaces;
import org.osgi.service.component.annotations.ComponentPropertyType;
import org.osgi.service.metatype.annotations.AttributeDefinition;
import org.osgi.service.metatype.annotations.ObjectClassDefinition;

/**
 * A meta annotation for the {@link EMFNamespaces#EMF_CONFIGURATOR_FEATURE} property of the {@link ResourceSetConfigurator} 
 * @author Juergen Albert
 * @since 12 Feb 2018
 */
@ComponentPropertyType
@Retention(RetentionPolicy.CLASS)
@Target(ElementType.TYPE)
@ObjectClassDefinition(
		description = "A URL can be configured, where a ecore is expected. The EPackage will then be loaded and registered for further use. Only the first EPackage is registered."
		)
public @interface DynamicEMFModel {
	static String PREFIX_ = EMFNamespaces.EMF_PREFIX;
	String dynamicEcoreUri();
	@AttributeDefinition(description = "The model custom feature", required = false)
	String[] feature() default "" ;
	@AttributeDefinition(description = "The model version", required = false)
	String version() default "" ;
	
}