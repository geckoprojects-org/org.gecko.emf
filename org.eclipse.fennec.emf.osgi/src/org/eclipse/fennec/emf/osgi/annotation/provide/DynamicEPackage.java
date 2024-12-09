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
 * dynamic model and allows the ecore editor to find this in a repository.
 * 
 * @author Juergen Albert
 * @since 21 Feb 2018
 */
@Documented
@Retention(RetentionPolicy.CLASS)
@Target({ ElementType.TYPE, ElementType.PACKAGE })
@Capability(namespace = "org.eclipse.emf.ecore.dynamic_package",
	attribute = {
		"uri=${#nsUri}",
		"location=${#location}"
	})
public @interface DynamicEPackage {

	String nsUri();
	String location();
}
