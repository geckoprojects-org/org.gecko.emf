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
package org.eclipse.fennec.emf.osgi.constants;

import java.util.Map;

import org.eclipse.emf.ecore.resource.Resource;

/**
 * A few Constants to enable customized UriHandler behavior
 * @author Juergen Albert
 * @since 27.07.2017
 */
public interface EMFUriHandlerConstants {

	/**
	 * The Request Methode to be used. Only applies for Outputstream to determin if out or post is used
	 */
	String OPTION_HTTP_METHOD = "method";
	
	/**
	 * Must provide a {@link Map}p of additional Headers that will be set to the request. They will be added last and thus may overwrite existing headers
	 */
	String OPTION_HTTP_HEADERS = "headers";
	
	/**
	 * If a complex Object in response is expected the Reponse will be feed as input stream to the given {@link Resource}
	 */
	String OPTIONS_EXPECTED_RESPONSE_RESOURCE = "expected.response.resource";
	
	/**
	 * A {@link Map} of Resource options that will be feed to the given response {@link Resource} as well
	 */
	String OPTIONS_EXPECTED_RESPONSE_RESOURCE_OPTIONS = "expected.response.options";
	
	/**
	 * if this option is set to <code>true</code> the response will be logged by the Urihandlers logger
	 */
	String OPTIONS_LOG_RESPONSE = "log.response";
	
	/**
	 * They was used for basic authentication, which is not recommended anymore. 
	 * If you need basic Auth, please handle it manually and set the Header via the 
	 * {@link EMFUriHandlerConstants#OPTION_HTTP_HEADERS}
	 * @deprecated will not be replaced
	 */
	@Deprecated
	String OPTIONS_AUTH_USER = "user";
	
	/**
	 * They was used for basic authentication, which is not recommended anymore. 
	 * If you need basic Auth, please handle it manually and set the Header via the 
	 * {@link EMFUriHandlerConstants#OPTION_HTTP_HEADERS}
	 * @deprecated will not be replaced
	 */
	@Deprecated
	String OPTIONS_AUTH_MANDANT = "mandant";
	
	/**
	 * They was used for basic authentication, which is not recommended anymore. 
	 * If you need basic Auth, please handle it manually and set the Header via the 
	 * {@link EMFUriHandlerConstants#OPTION_HTTP_HEADERS}
	 * @deprecated will not be replaced
	 */
	@Deprecated
	String OPTIONS_AUTH_PASSWORD = "password";

}
