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

import static java.util.Objects.nonNull;

import java.util.logging.Logger;

import org.eclipse.emf.ecore.EOperation.Internal.InvocationDelegate.Factory;
import org.eclipse.emf.ecore.EOperation.Internal.InvocationDelegate.Factory.Descriptor;
import org.eclipse.emf.ecore.EOperation.Internal.InvocationDelegate.Factory.Registry;
import org.eclipse.fennec.emf.osgi.annotation.provide.EMFConfigurator;
import org.osgi.annotation.versioning.ProviderType;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.annotations.ReferenceCardinality;
import org.osgi.service.component.annotations.ReferencePolicy;

/**
 * An implementation of a package registry that can delegate failed lookup to
 * another registry. This implementation is derived from the default
 * {@link DefaultEOperationInvocationDelegateRegistryComponent} to be enabled as OSGi component
 */
@Component(name = DefaultEOperationInvocationDelegateRegistryComponent.NAME)
@ProviderType
public class DefaultEOperationInvocationDelegateRegistryComponent {
	
	private static final Logger LOG = Logger.getLogger(DefaultEOperationInvocationDelegateRegistryComponent.class.getName());
	/** DEFAULT_E_PACKAGE_REGISTRY */
	public static final String NAME = "DefaultEOperationInvocationDelegateRegistry";
	public static final String TARGET = "(configuratorType=OPERATION_INVOCATION_FACTORY)";

	private final Registry registry = Registry.INSTANCE;
	
	/**
	 * Creates a non-delegating instance.
	 */
	public DefaultEOperationInvocationDelegateRegistryComponent() {
		super();
	}

	@Reference(policy = ReferencePolicy.DYNAMIC, cardinality = ReferenceCardinality.MULTIPLE, target = TARGET)
	public void addOperationInvocationDelegateFactory(Factory factory, final EMFConfigurator properties) {
		Object recent = registry.put(properties.configuratorName(), factory);
		if (nonNull(recent)) {
			LOG.info(()->String.format("An operation invocation delegate factory '%s' for '%s' was already registered and is now replace by a new one", recent.toString(), properties.configuratorName()));
			return;
		}
	}
	
	public void removeOperationInvocationDelegateFactory(Factory factory, EMFConfigurator properties) {
		registry.remove(properties.configuratorName());
	}
	
	@Reference(policy = ReferencePolicy.DYNAMIC, cardinality = ReferenceCardinality.MULTIPLE, target = TARGET)
	public void addOperationInvocationDelegateDescriptor(Descriptor factory, final EMFConfigurator properties) {
		Object recent = registry.put(properties.configuratorName(), factory);
		if (nonNull(recent)) {
			LOG.info(()->String.format("An operation invocation delegate factory descriptor '%s' for '%s' was already registered and is now replace by a new one", recent.toString(), properties.configuratorName()));
			return;
		}
	}
	
	public void removeOperationInvocationDelegateDescriptor(Descriptor factory, EMFConfigurator properties) {
		registry.remove(properties.configuratorName());
	}

}
