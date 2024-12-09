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
package org.eclipse.fennec.emf.osgi.codegen.adapter;

import org.eclipse.emf.codegen.ecore.generator.GeneratorAdapterFactory;
import org.eclipse.emf.codegen.ecore.genmodel.generator.GenModelGeneratorAdapter;
import org.eclipse.emf.common.util.Diagnostic;
import org.eclipse.emf.common.util.Monitor;
import org.eclipse.emf.common.util.URI;

/**
 * Used to avoid the PDE specific Files
 * 
 * @author Juergen Albert
 * @since 13 May 2022
 */
public class GeckoGenModelGeneratorAdapter extends GenModelGeneratorAdapter {

	/**
	 * Creates a new instance.
	 * @param generatorAdapterFactory
	 */
	public GeckoGenModelGeneratorAdapter(GeneratorAdapterFactory generatorAdapterFactory) {
		super(generatorAdapterFactory);
	}
	
	/* 
	 * (non-Javadoc)
	 * @see org.eclipse.emf.codegen.ecore.generator.AbstractGeneratorAdapter#getEncoding(org.eclipse.emf.common.util.URI)
	 */
	@Override
	protected String getEncoding(URI workspacePath) {
		String encoding = super.getEncoding(workspacePath);
		if(encoding == null) {
			return "UTF8";
		}
		return encoding;
	}
	
	/* 
	 * (non-Javadoc)
	 * @see org.eclipse.emf.codegen.ecore.genmodel.generator.GenModelGeneratorAdapter#generateModel(java.lang.Object, org.eclipse.emf.common.util.Monitor)
	 */
	@Override
	protected Diagnostic generateModel(Object object, Monitor monitor) {
		monitor.worked(1);
		return Diagnostic.OK_INSTANCE;
	}
}
