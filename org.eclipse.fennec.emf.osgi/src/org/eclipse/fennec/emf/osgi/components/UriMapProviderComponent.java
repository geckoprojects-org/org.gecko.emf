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
package org.eclipse.fennec.emf.osgi.components;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.emf.common.util.URI;
import org.eclipse.fennec.emf.osgi.UriMapProvider;
import org.osgi.annotation.versioning.ProviderType;
import org.osgi.service.cm.ConfigurationException;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.ConfigurationPolicy;

/**
 * Implementation of an {@link UriMapProvider}
 * @author bhunt
 * @author Mark Hoffmann
 * @since 25.07.2017
 */
@Component(name="DefaultUriMapProvider", configurationPolicy=ConfigurationPolicy.REQUIRE)
@ProviderType
public class UriMapProviderComponent implements UriMapProvider {
	
	private Map<URI, URI> uriMap = new HashMap<>();
	

	@Override
	public Map<URI, URI> getUriMap() {
		return uriMap;
	}

	/**
	 * Called on component activation
	 * @param properties the configured properties
	 * @throws ConfigurationException throw non errors during the activation
	 */
	@Activate
	public void activate(Map<String, Object> properties) throws ConfigurationException {
		String src = (String) properties.get(URI_MAP_SOURE);
		if (src == null) {
			throw new ConfigurationException(URI_MAP_SOURE, "The property was not given, but is required");
		}
		String dest = (String) properties.get(URI_MAP_DESTINATION);
		if (dest == null) {
			throw new ConfigurationException(URI_MAP_DESTINATION, "The property was not given, but is required");
		}
		String[] sources = src.split(",");
		String[] destinations = dest.split(",");
		if (sources.length != destinations.length) {
			throw new ConfigurationException(URI_MAP_SOURE, "source an destination strings contain not the same number of elements");
		}
		for (int i = 0; i < sources.length; i++) {
			URI srcUri = null;
			URI destUri = null;
			try {
				srcUri = URI.createURI(sources[i]);
				destUri = URI.createURI(destinations[i]);
				uriMap.put(srcUri, destUri);
			} catch (Exception e) {
				String property = srcUri == null ? sources[i] : destinations[i];
				throw new ConfigurationException(property, "The given value cannot be converted into an URI", e);
			}
			
		}
	}
	
}
