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
 *       Data In Motion - initial API and implementation
 */
package org.eclipse.fennec.emf.osgi.model.info;

import java.util.List;
import java.util.Optional;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EClassifier;
import org.osgi.annotation.bundle.Requirement;

/**
 * A Model repository, with some searching capability to find Models by there Classname
 * @author Jürgen Albert
 * @since 8 Nov 2018
 */
@Requirement(
		namespace = EMFModelInfo.NAMESPACE,
		name=EMFModelInfo.NAME
		)
public interface EMFModelInfo {

	public static final String NAMESPACE = "org.gecko.emf.osgi";
	public static final String NAME = "model.info";
	
	/**
	 * Looks through all known Packages as returns  and {@link Optional} with the  {@link EClass} for the given Class
	 * @param clazz the {@link Class} to look for
	 * @return an {@link Optional} for the {@link EClass}
	 */
	public Optional<EClassifier> getEClassifierForClass(Class<?> clazz);

	/**
	 * Looks through all known Packages as returns  and {@link Optional} with the  {@link EClass} for the given Class
	 * @param fullQualifiedClassName the full qualified class name to look for
	 * @return an {@link Optional} for the {@link EClass}
	 */
	public Optional<EClassifier> getEClassifierForClass(String fullQualifiedClassName);
	
	/**
	 * It might be necessary to know, that class can inherit from the given class. This method will return a List of all known upper {@link EClass}es.
	 * @param eClass the EClass to get the hirachy for
	 * @return The {@link List} of upper elements. 
	 */
	public List<EClass> getUpperTypeHierarchyForEClass(EClass eClass);	
}
