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
package org.eclipse.fennec.emf.osgi.components.config;

import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.impl.ResourceFactoryRegistryImpl;
import org.eclipse.fennec.emf.osgi.constants.EMFNamespaces;
import org.osgi.annotation.versioning.ProviderType;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.ConfigurationPolicy;

/**
 * Component for the {@link ResourceFactoryRegistryImpl}
 * @author Mark Hoffmann
 * @since 25.07.2017
 */
@Component(configurationPid=EMFNamespaces.RESOURCE_FACTORY_CONFIG_NAME, service=Resource.Factory.Registry.class, configurationPolicy=ConfigurationPolicy.REQUIRE)
@ProviderType
public class ConfigurationResourceFactoryRegistryComponent extends ResourceFactoryRegistryImpl {

}
