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
package org.eclipse.fennec.emf.osgi;

import org.eclipse.emf.ecore.EObject;
import org.osgi.annotation.versioning.ProviderType;

/**
 * Interface to detach EObjects from a resource or/and resource sets
 * @author Mark Hoffmann
 * @since 23.11.2017
 */
@ProviderType
public interface Detachable {
	
	/**
	 * Detaches the object just from the resource and leaves the resource in the resource set
	 * @param object the object to detach
	 */
	public void detachFromResource(EObject object);
	
	/**
	 * Detaches the object from the resource and detaches the resource from the resource set as well
	 * @param object the object to detach 
	 */
	public void detachFromAll(EObject object);

}
