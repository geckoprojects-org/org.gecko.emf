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
package org.eclipse.fennec.emf.osgi.itest.configurator;

import java.util.concurrent.atomic.AtomicInteger;

import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.fennec.emf.osgi.configurator.ResourceSetConfigurator;

/**
 * 
 * @author mark
 * @since 07.03.2020
 */
public class TestResourceSetConfiguration implements ResourceSetConfigurator {

	private AtomicInteger cnt = new AtomicInteger();

	@Override
	public void configureResourceSet(ResourceSet resourceSet) {
		cnt.incrementAndGet();
	}

	public int getCount() {
		return cnt.get();
	}

}
