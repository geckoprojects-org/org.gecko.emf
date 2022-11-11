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
package org.gecko.emf.osgi.codegen.adapter;

import org.eclipse.emf.codegen.ecore.generator.GeneratorAdapter;
import org.eclipse.emf.codegen.ecore.generator.GeneratorAdapterFactory;
import org.eclipse.emf.codegen.ecore.genmodel.GenEnum;
import org.eclipse.emf.codegen.ecore.genmodel.generator.GenEnumGeneratorAdapter;
import org.eclipse.emf.common.util.Monitor;

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
			"model/EnumClass.javajet", "org.gecko.emf.osgi.codegen.templates.model.EnumClass") };

	/**
	 * Creates a new instance.
	 * 
	 * @param generatorAdapterFactory
	 */
	public GeckoGenEnumGeneratorAdapter(GeneratorAdapterFactory generatorAdapterFactory) {
		super(generatorAdapterFactory);
		// TODO Auto-generated constructor stub
	}

	/**
	 * Returns the set of <code>JETEmitterDescriptor</code>s used by the adapter.
	 * The contents of the returned array should never be changed. Rather,
	 * subclasses may override this method to return a different array altogether.
	 */
	protected JETEmitterDescriptor[] getJETEmitterDescriptors() {
		return JET_EMITTER_DESCRIPTORS;
	}

	protected void generateEnumClass(GenEnum genEnum, Monitor monitor) {
		monitor.subTask(message);
		generateJava(genEnum.getGenModel().getModelDirectory(), genEnum.getGenPackage().getInterfacePackageName(),
				genEnum.getName(), getJETEmitter(getJETEmitterDescriptors(), ENUM_CLASS_ID), null,
				createMonitor(monitor, 1));
	}
}
