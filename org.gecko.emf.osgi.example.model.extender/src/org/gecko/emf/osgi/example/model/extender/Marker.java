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
package org.gecko.emf.osgi.example.model.extender;

import org.gecko.emf.osgi.annotation.provide.DynamicEPackage;

@DynamicEPackage(nsUri="http://foo.bar/toast", location="toast/toast.ecore;")
public class Marker {

}
