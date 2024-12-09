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
 *       Data In Motion - initial API and implementation
 */
package org.eclipse.fennec.emf.osgi.itest;

import static org.assertj.core.api.Assertions.assertThat;

import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.fennec.emf.osgi.ResourceSetFactory;
import org.eclipse.fennec.emf.osgi.constants.EMFNamespaces;
import org.eclipse.fennec.emf.osgi.example.model.basic.BasicPackage;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.osgi.framework.ServiceReference;
import org.osgi.service.condition.Condition;
import org.osgi.test.assertj.dictionary.DictionaryAssert;
import org.osgi.test.common.annotation.InjectService;
import org.osgi.test.common.service.ServiceAware;
import org.osgi.test.junit5.context.BundleContextExtension;
import org.osgi.test.junit5.service.ServiceExtension;

/**
 * Integration test for the {@link ResourceSetFactory}
 * 
 * @author Mark Hoffmann
 * @since 25.07.2017
 */
@ExtendWith(BundleContextExtension.class)
@ExtendWith(ServiceExtension.class)
public class ResourceSetFactoryIntegrationTest {

	/**
	 * Tests, if the service was set up correctly
	 */
	@Test
	public void testResourceSetFactoryExists(@InjectService ResourceSetFactory factory) {
		Assertions.assertNotNull(factory);
		ResourceSet rs1 = factory.createResourceSet();
		Assertions.assertNotNull(rs1);
		ResourceSet rs2 = factory.createResourceSet();
		Assertions.assertNotNull(rs2);
		Assertions.assertNotEquals(rs1, rs2);
	}

	/**
	 * Tests, if the service was set up correctly
	 */
	@Test
	public void testResourceSetFactoryExists(
			@InjectService(filter = "(" + Condition.CONDITION_ID + "=" + ResourceSetFactory.CONDITION_ID + ")") ServiceAware<Condition> condition,
			@InjectService BasicPackage basicPackage
			) {
		
		assertThat(basicPackage).isNotNull();
		ServiceReference<Condition> reference = condition.getServiceReference();
		assertThat(reference).isNotNull();

		DictionaryAssert.assertThat(reference.getProperties()).containsKey(EMFNamespaces.EMF_MODEL_NAME)
				.extractingByKey(EMFNamespaces.EMF_MODEL_NAME).isNotNull()
				.isInstanceOfSatisfying(String[].class, arr -> {
					assertThat(arr).contains(BasicPackage.eNAME);
				});

	}

}