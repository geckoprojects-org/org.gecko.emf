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

import java.io.IOException;

import org.assertj.core.api.Assertions;
import org.eclipse.emf.common.util.EMap;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.fennec.emf.osgi.example.model.basic.BasicPackage;
import org.eclipse.fennec.emf.osgi.example.model.extended.ExtendedFactory;
import org.eclipse.fennec.emf.osgi.example.model.extended.ExtendedPerson;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.osgi.framework.BundleContext;
import org.osgi.test.common.annotation.InjectBundleContext;
import org.osgi.test.common.annotation.InjectService;
import org.osgi.test.junit5.context.BundleContextExtension;
import org.osgi.test.junit5.service.ServiceExtension;

/**
 * Tests the cases, where the generated logic actually provides metadata as it should
 * 
 * @author Juergen Albert
 * @since 13.05.2022
 */
@ExtendWith(BundleContextExtension.class)
@ExtendWith(ServiceExtension.class)
public class EMFOSGiGeneratedCodeTest {

	@InjectBundleContext
	BundleContext bc;

	/**
	 * Trying to load an instance with a non registered {@link EPackage}
	 * 
	 * @throws IOException
	 */
	@Test
	public void testPropertiesMapTypeIsCorrect(@InjectService ExtendedFactory factory,
			@InjectService BasicPackage basicPackage) {

		ExtendedPerson extendedPerson = factory.createExtendedPerson();
		
		EMap<String,String> properties = extendedPerson.getProperties();
		Assertions.assertThat(properties).isNotNull();
	}

}