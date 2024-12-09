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

import org.eclipse.emf.ecore.resource.URIHandler;
import org.eclipse.fennec.emf.osgi.UriHandlerProvider;
import org.eclipse.fennec.emf.osgi.urihandler.RestfulURIHandlerImpl;
import org.osgi.annotation.versioning.ProviderType;

/**
 * Provider for the rest-ful URI Handler, that can use BasicAuthentication
 * @author Mark Hoffmann
 * @since 27.07.2017
 */
@ProviderType
public class RestUriHandlerProvider implements UriHandlerProvider {


	@Override
	public URIHandler getURIHandler() {
		return new RestfulURIHandlerImpl();
	}

}
