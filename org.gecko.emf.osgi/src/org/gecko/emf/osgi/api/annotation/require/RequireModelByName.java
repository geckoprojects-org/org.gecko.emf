/**
 * Copyright (c) 2012 - 2018 Data In Motion and others.
 * All rights reserved. 
 * 
 * This program and the accompanying materials are made available under the terms of the 
 * Eclipse Public License v1.0 which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     Data In Motion - initial API and implementation
 */
package org.gecko.emf.osgi.api.annotation.require;


import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.gecko.emf.osgi.api.EMFNamespaces;
import org.gecko.emf.osgi.api.EPackageConfigurator;
import org.osgi.annotation.bundle.Attribute;
import org.osgi.annotation.bundle.Requirement;

/**
 * Marks the requirement of a specific EMF Model 
 * @author Juergen Albert
 * @since 9 Feb 2018
 */
@Documented
@Retention(RetentionPolicy.CLASS)
@Target({
		ElementType.TYPE, ElementType.PACKAGE
})
@Requirement(namespace = EMFNamespaces.EMF_CONFIGURATOR_NAMESPACE, //
		name = EPackageConfigurator.EMF_CONFIGURATOR_NAME)
public @interface RequireModelByName {
	
	/**
	 * The name of the {@link EPackageConfigurator} reqired
	 */
	@Attribute()
	String name(); 
	
	/**
	 * The Version of the {@link EPackageConfigurator}
	 */
	@Attribute()
	String version() default "1.0.0";

}