/**
 * Copyright (c) 2012 - 2023 Data In Motion and others.
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
package org.eclipse.fennec.emf.osgi.components.dynamic;

import static java.util.Objects.requireNonNull;

import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EPackage.Registry;
import org.eclipse.fennec.emf.osgi.ResourceSetFactory;
import org.eclipse.fennec.emf.osgi.configurator.EPackageConfigurator;

/**
 * Can be registered as a Service and the given Package will be configured to any {@link ResourceSetFactory} that picks it up.
 * 
 * @author Juergen Albert
 * @since 17 Mar 2022
 */
public class DynamicPackageConfiguratorImpl implements EPackageConfigurator {

		private final EPackage ePackage;

		/**
		 * Creates a new instance.
		 */
		DynamicPackageConfiguratorImpl(EPackage ePackage) {
			requireNonNull(ePackage);
			this.ePackage = ePackage;
		}
		
		/* 
		 * (non-Javadoc)
		 * @see org.gecko.emf.osgi.EPackageConfigurator#configureEPackage(org.eclipse.emf.ecore.EPackage.Registry)
		 */
		@Override
		public void configureEPackage(Registry registry) {
			registry.put(ePackage.getNsURI(),ePackage);
		}

		/* 
		 * (non-Javadoc)
		 * @see org.gecko.emf.osgi.EPackageConfigurator#unconfigureEPackage(org.eclipse.emf.ecore.EPackage.Registry)
		 */
		@Override
		public void unconfigureEPackage(Registry registry) {
			registry.remove(ePackage.getNsURI());
		}
	}