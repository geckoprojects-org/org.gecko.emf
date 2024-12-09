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
import org.eclipse.emf.codegen.ecore.genmodel.generator.GenModelGeneratorAdapterFactory;
import org.eclipse.emf.common.notify.Adapter;
import org.eclipse.fennec.emf.osgi.codegen.GeckoEmfGenerator;

/**
 * EMF codegen generator adapter factory that is responsible to create the Bnd adapter
 * @author Mark Hoffmann
 * @since 30.03.2018
 */
public class BNDGeneratorAdapterFactory extends GenModelGeneratorAdapterFactory {

	public static final GeneratorAdapterFactory.Descriptor DESCRIPTOR = ()->{
			GeckoEmfGenerator.info("Creating BNDGeneratorAdapterFactory");
			return new BNDGeneratorAdapterFactory();
		};

		
		
	@Override
	public Adapter createGenPackageAdapter() {
		if (genPackageGeneratorAdapter == null)
		{
			GeckoEmfGenerator.info("Creating GeckoGenPackageGeneratorAdapter");
			genPackageGeneratorAdapter = new GeckoGenPackageGeneratorAdapter(this);
		}
		return genPackageGeneratorAdapter;
	}
	
	/* 
	 * (non-Javadoc)
	 * @see org.eclipse.emf.codegen.ecore.genmodel.generator.GenModelGeneratorAdapterFactory#createGenModelAdapter()
	 */
	@Override
	public Adapter createGenModelAdapter() {
		if (genModelGeneratorAdapter == null)
		{
			GeckoEmfGenerator.info("Creating GeckoGenModelGeneratorAdapter");
			genModelGeneratorAdapter = new GeckoGenModelGeneratorAdapter(this);
		} 
		return genModelGeneratorAdapter;
	}
	
	/* 
	 * (non-Javadoc)
	 * @see org.eclipse.emf.codegen.ecore.genmodel.generator.GenModelGeneratorAdapterFactory#createGenClassAdapter()
	 */
	@Override
	public Adapter createGenClassAdapter() {
		if (genClassGeneratorAdapter == null)
		{
			GeckoEmfGenerator.info("Creating GeckoGenClassGeneratorAdapter");
			genClassGeneratorAdapter = new GeckoGenClassGeneratorAdapter(this);
		} 
		return genClassGeneratorAdapter;
	}
	
	/* 
	 * (non-Javadoc)
	 * @see org.eclipse.emf.codegen.ecore.genmodel.generator.GenModelGeneratorAdapterFactory#createGenEnumAdapter()
	 */
	@Override
	public Adapter createGenEnumAdapter() {
		if (genEnumGeneratorAdapter == null)
		{
			GeckoEmfGenerator.info("Creating GeckoGenEnumGeneratorAdapter");
			genEnumGeneratorAdapter = new GeckoGenEnumGeneratorAdapter(this);
		} 
		return genEnumGeneratorAdapter;
	}
}
