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
package org.eclipse.fennec.emf.osgi.annotation;

/**
 * Type of Gecko EMF OSGi configurators 
 * @author Mark Hoffmann
 * @since 15.12.2023
 */
public enum ConfiguratorType {

	EPACKAGE,
	RESOURCE_SET,
	RESOURCE_FACTORY,
	OPERATION_INVOCATION_FACTORY
	
}
