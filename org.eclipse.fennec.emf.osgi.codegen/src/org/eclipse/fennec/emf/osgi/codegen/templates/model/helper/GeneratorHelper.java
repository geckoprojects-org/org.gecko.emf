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
package org.eclipse.fennec.emf.osgi.codegen.templates.model.helper;

import org.eclipse.emf.codegen.ecore.genmodel.GenPackage;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EAnnotation;

/**
 * A helper for some special OSGi specific dependency deduction
 * 
 * @author Juergen Albert
 * @since 29 Aug 2022
 */
public class GeneratorHelper {
	
	private GeneratorHelper() {
	}
	
	public static String getVersion(GenPackage genPackage) {
		EAnnotation versionAnnotation = genPackage.getEcorePackage().getEAnnotation("Version");
		if(versionAnnotation == null) {
			return "1.0";
		}
		String value = versionAnnotation.getDetails().get("value");
		if(value == null) {
			return "1.0";
		}
		return value; 
	}

	/**
	 * This method assumes that the relative paths between the genmodel and the ecore will be similar in the resulting bundle. 
	 * It thus determines a the ecore path relative to the bundleGenmodelPath.
	 * 
	 * @param bundleGenModelPath
	 * @param originalEcoreUri
	 * @param originalGenModelUri
	 * @return the ecore URI in the resulting bundle
	 */
	public static URI convertToBundleEcoreURI(URI bundleGenModelPath, URI originalEcoreUri, URI originalGenModelUri) {
    	URI dummy = URI.createURI("resources://bla/");
    	URI genModelPathResolved = bundleGenModelPath.resolve(dummy);
    	URI finalEcore = originalEcoreUri.
    			deresolve(originalGenModelUri).
    			resolve(genModelPathResolved).
    			deresolve(dummy);
		return finalEcore;
	}

	
}
