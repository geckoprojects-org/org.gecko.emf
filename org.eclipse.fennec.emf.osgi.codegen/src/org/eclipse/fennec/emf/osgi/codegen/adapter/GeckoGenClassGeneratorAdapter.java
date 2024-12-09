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

import org.eclipse.emf.codegen.ecore.CodeGenEcorePlugin;
import org.eclipse.emf.codegen.ecore.generator.GeneratorAdapterFactory;
import org.eclipse.emf.codegen.ecore.genmodel.GenClass;
import org.eclipse.emf.codegen.ecore.genmodel.GenModel;
import org.eclipse.emf.codegen.ecore.genmodel.GenPackage;
import org.eclipse.emf.codegen.ecore.genmodel.generator.GenClassGeneratorAdapter;
import org.eclipse.emf.common.util.Monitor;
import org.eclipse.emf.common.util.URI;

/**
 * Used to avoid the PDE specific Files
 * 
 * @author Juergen Albert
 * @since 13 May 2022
 */
public class GeckoGenClassGeneratorAdapter extends GenClassGeneratorAdapter {

	protected static final int CLASS_ID = 0;

	private static final JETEmitterDescriptor[] JET_EMITTER_DESCRIPTORS = {
			new JETEmitterDescriptor("model/Class.javajet", "org.eclipse.fennec.emf.osgi.codegen.templates.model.Class"), };

	/**
	 * Returns the set of <code>JETEmitterDescriptor</code>s used by the adapter.
	 * The contents of the returned array should never be changed. Rather,
	 * subclasses may override this method to return a different array altogether.
	 */
	@Override
	protected JETEmitterDescriptor[] getJETEmitterDescriptors() {
		return JET_EMITTER_DESCRIPTORS;
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
	
	/**
	 * Creates a new instance.
	 * 
	 * @param generatorAdapterFactory
	 */
	public GeckoGenClassGeneratorAdapter(GeneratorAdapterFactory generatorAdapterFactory) {
		super(generatorAdapterFactory);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.emf.codegen.ecore.genmodel.generator.GenClassGeneratorAdapter#
	 * generateInterface(org.eclipse.emf.codegen.ecore.genmodel.GenClass,
	 * org.eclipse.emf.common.util.Monitor)
	 */
	@Override
	protected void generateInterface(GenClass genClass, Monitor monitor) {
		GenModel genModel = genClass.getGenModel();
		GenPackage genPackage = genClass.getGenPackage();

		if (!genClass.isExternalInterface() && (!genModel.isSuppressInterfaces() || genClass.isInterface())) {
			message = CodeGenEcorePlugin.INSTANCE.getString("_UI_GeneratingJavaInterface_message",
					new Object[] { genPackage.getInterfacePackageName() + "." + genClass.getInterfaceName() });
			monitor.setTaskName(message);
			monitor.subTask(message);
			generateJava(genModel.getModelDirectory(), genPackage.getInterfacePackageName(),
					genClass.getInterfaceName(), getJETEmitter(getJETEmitterDescriptors(), CLASS_ID),
					new Object[] { new Object[] { genClass, Boolean.TRUE, Boolean.FALSE } },
					createMonitor(monitor, 1));
		} else {
			monitor.worked(1);
		}
	}
}
