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
package org.eclipse.fennec.emf.osgi.annotation.extender;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.eclipse.fennec.emf.osgi.constants.EMFNamespaces;
import org.osgi.annotation.bundle.Attribute;
import org.osgi.annotation.bundle.Requirement;
import org.osgi.namespace.extender.ExtenderNamespace;

/**
 * This annotation can be used to require the EMF model extender. It can be
 * used directly, or as a meta-annotation.
 * <p>
 * This annotation allows users to define custom locations that should be
 * searched for EMF ecore files using {@link ProvideExtenderModel#value()}
 * 
 * @author Mark Hoffmann
 * @since 13.10.2022
 */
@Documented
@Retention(RetentionPolicy.CLASS)
@Target({
		ElementType.TYPE, ElementType.PACKAGE
})
@Requirement(namespace = ExtenderNamespace.EXTENDER_NAMESPACE, //
		name = EMFNamespaces.EMF_MODEL_EXTENDER_NAME, //
		version = "1.0.0")
public @interface ProvideExtenderModel {

	/**
	 * This attribute can be used to define one or more locations that the
	 * EMF model extender must search, in order, for EMF ecore files.
	 * <p>
	 * If no locations are defined then the Extender default of
	 * <code>/model</code> will be used.
	 * 
	 * @return A list of bundle locations containing ecore files
	 */
	@Attribute("models")
	String[] value() default {};

}
