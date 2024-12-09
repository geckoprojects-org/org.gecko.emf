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

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.osgi.annotation.bundle.Capability;

/**
 * Marker annotation that the bundle has the capability to provide a certain
 * model and allows the ecore editor to find this in a repository.
 * 
 * @author Juergen Albert
 * @since 9 Feb 2018
 */
@Documented
@Retention(RetentionPolicy.CLASS)
@Target({ ElementType.TYPE, ElementType.PACKAGE })
@Capability(namespace = EPackage.NAMESPACE,
	attribute = {
		"class=\"${#value}\"", //
		"uri=${#uri}",
		"genModel=${#genModel}",
		"genModelSourceLocations:List<String>=\"${#genModelSourceLocations}\"",
		"ecore=${#ecore}",
		"ecoreSourceLocations:List<String>=\"${#ecoreSourceLocations}\""
	})
public @interface EPackage {

	/** ORG_ECLIPSE_EMF_ECORE_GENERATED_PACKAGE */
	public static final String NAMESPACE = "org.eclipse.emf.ecore.generated_package";

	String uri();

	Class<?> value() default Target.class;

	String genModel();

	String[] genModelSourceLocations() default "";

	String ecore() default "";
	
	String[] ecoreSourceLocations() default "";
}
