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

import java.util.Map;

import org.eclipse.emf.common.util.URI;
import org.osgi.annotation.versioning.ProviderType;

/**
 * Provider to convert Uri's
 * @author bhunt
 * @author Mark Hoffmann
 * @since 25.07.2017
 */
@ProviderType
public interface UriMapProvider {
	
	public static final String URI_MAP_SOURE = "uri.map.src"; 
	public static final String URI_MAP_DESTINATION = "uri.map.dest"; 
	
	/**
	 * Return the Uri map
	 * @return the map instance
	 */
	Map<URI, URI> getUriMap();
}
