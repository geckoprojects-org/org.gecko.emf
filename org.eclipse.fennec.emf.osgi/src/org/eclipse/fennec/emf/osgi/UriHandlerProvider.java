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

import org.eclipse.emf.ecore.resource.URIHandler;
import org.osgi.annotation.versioning.ProviderType;

/**
 * This OSGi service provides a URI handler. It is intended to be used with the
 * IResourceSetConfigurator for configuring a ResourceSet.
 * @author bhunt
 * 
 */
@ProviderType
public interface UriHandlerProvider {
	
	/**
	 * Returns the URI handler instance
	 * @return the URI handler instance
	 */
	URIHandler getURIHandler();
}
