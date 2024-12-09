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
package org.eclipse.fennec.emf.osgi.codegen;

import java.util.Optional;

import org.eclipse.emf.common.util.URI;

/**
 * 
 * @author ungei
 * @since 23 Feb 2022
 */
public class UriSanatizer {

	/** RESOURCE_SCHEMA_NAME */
	public static final String RESOURCE_SCHEMA_NAME = "resource"; //$NON-NLS-1$
	/** APPLICATION_XMI */
	public static final String APPLICATION_XMI = "application/xmi"; //$NON-NLS-1$
	/** SLASH */
	public static final String SLASH = "/"; //$NON-NLS-1$
	/** SCHEMA_RESOURCE */
	public static final String SCHEMA_RESOURCE = RESOURCE_SCHEMA_NAME + "://"; //$NON-NLS-1$
	/** PLATFORM_PLUGIN */
	public static final String PLATFORM_PLUGIN = "platform:/plugin/"; //$NON-NLS-1$
	/** PLATFORM_RESOURCE */
	public static final String PLATFORM_RESOURCE = "platform:/resource/"; //$NON-NLS-1$
	
	private UriSanatizer() {
	}
	
	public static Optional<URI> sanitize(URI toSanatize) {
		if (toSanatize == null) {
			return Optional.empty();
		}
		if(toSanatize.toString().startsWith(PLATFORM_RESOURCE)) {
			toSanatize = URI.createURI(SCHEMA_RESOURCE + toSanatize.toString().substring(PLATFORM_RESOURCE.length()));
		} else if(toSanatize.toString().startsWith(PLATFORM_PLUGIN)) {
			toSanatize = URI.createURI(SCHEMA_RESOURCE + toSanatize.toString().substring(PLATFORM_PLUGIN.length()));
		}
		return doSanitize(toSanatize);
	}

	public static Optional<URI> doSanitize(URI toSanitize) {
		StringBuilder uri = new StringBuilder();
		for(int i = toSanitize.segmentCount() -1; i >= 0;  i--) {
			String segment = toSanitize.segment(i);
			if("..".equals(segment)) { //$NON-NLS-1$
				i--;
			} else {
				if(toSanitize.segmentCount() - 1 == i) {
					uri.append(segment);
				} else {
					uri.insert(0, segment + SLASH);
				}
			}
			if(i <= 0 ) {
				String host = toSanitize.host();
				if("..".equals(segment)) { //$NON-NLS-1$
					return Optional.of(URI.createURI(toSanitize.scheme() + "://" + uri.toString())); //$NON-NLS-1$
				}
				return Optional.of(URI.createURI(toSanitize.scheme() + "://"+ host + SLASH + uri.toString())); //$NON-NLS-1$
			}
		}
		return Optional.empty();
	}

	public static URI trimmedSanitize(URI toSanitize) {
		Optional<URI> sanitized = sanitize(toSanitize);
		return sanitized.map(URI::trimFragment).map(URI::trimQuery).orElse(null); 
	}
	
}
