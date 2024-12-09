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

import org.eclipse.emf.codegen.ecore.generator.GeneratorAdapter;
import org.eclipse.emf.codegen.ecore.generator.GeneratorAdapterFactory;
import org.eclipse.emf.codegen.ecore.genmodel.GenEnum;
import org.eclipse.emf.codegen.ecore.genmodel.generator.GenEnumGeneratorAdapter;
import org.eclipse.emf.common.util.Monitor;
import org.eclipse.emf.common.util.URI;

/**
 * A {@link GeneratorAdapter} for instances of {@link GenEnum}. This contributes
 * the artifacts for every enum to EMF's default code generation.
 * 
 * <p>
 * This implementation should not be extended merely to augment the default code
 * generation for enums. The recommended approach is to implement a new adapter
 * and register the {@link GeneratorAdapterFactory adapter factory} that creates
 * it, so that it is contributed to code generation. Such registration is
 * usually done through the
 * <code>org.eclipse.emf.codegen.ecore.generatorAdapters</code> extension point.
 * 
 * <p>
 * This implementation may be extended, however, in order to remove from or
 * change the default code generation.
 * 
 * @since 4.4.0
 */
public class GeckoGenEnumGeneratorAdapter extends GenEnumGeneratorAdapter {

	protected static final int ENUM_CLASS_ID = 0;

	private static final JETEmitterDescriptor[] JET_EMITTER_DESCRIPTORS = { new JETEmitterDescriptor(
			"model/EnumClass.javajet", "org.eclipse.fennec.emf.osgi.codegen.templates.model.EnumClass") };

	/**
	 * Creates a new instance.
	 * 
	 * @param generatorAdapterFactory
	 */
	public GeckoGenEnumGeneratorAdapter(GeneratorAdapterFactory generatorAdapterFactory) {
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
	
	/**
	 * Returns the set of <code>JETEmitterDescriptor</code>s used by the adapter.
	 * The contents of the returned array should never be changed. Rather,
	 * subclasses may override this method to return a different array altogether.
	 */
	@Override
	protected JETEmitterDescriptor[] getJETEmitterDescriptors() {
		return JET_EMITTER_DESCRIPTORS;
	}

	@Override
	protected void generateEnumClass(GenEnum genEnum, Monitor monitor) {
		monitor.subTask(message);
		monitor.setTaskName(message);
		generateJava(genEnum.getGenModel().getModelDirectory(), genEnum.getGenPackage().getInterfacePackageName(),
				genEnum.getName(), getJETEmitter(getJETEmitterDescriptors(), ENUM_CLASS_ID), null,
				createMonitor(monitor, 1));
	}
}
