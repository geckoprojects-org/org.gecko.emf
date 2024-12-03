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
package org.gecko.emf.osgi.test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.eclipse.emf.ecore.EPackage;
import org.gecko.emf.osgi.annotation.require.RequireEMF;
import org.gecko.emf.osgi.constants.EMFNamespaces;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.osgi.test.assertj.dictionary.DictionaryAssert;
import org.osgi.test.common.annotation.InjectService;
import org.osgi.test.common.service.ServiceAware;
import org.osgi.test.junit5.context.BundleContextExtension;
import org.osgi.test.junit5.service.ServiceExtension;

@RequireEMF
@ExtendWith(ServiceExtension.class)
@ExtendWith(BundleContextExtension.class)
public class BasicModelPropertiesTest {

	
	@Test
	public void simpleTestEPackageRegistrationProperty(@InjectService(filter = "(" + EMFNamespaces.EMF_MODEL_NAME + "=basic)") ServiceAware<EPackage> epackageAware) {
		assertNotNull(epackageAware);
		assertThat(epackageAware.isEmpty()).isFalse();
		DictionaryAssert.assertThat(epackageAware.getServiceReference().getProperties()).containsKey(EMFNamespaces.EMF_MODEL_REGISTRATION)	
		.extractingByKey(EMFNamespaces.EMF_MODEL_REGISTRATION).isEqualTo(EMFNamespaces.MODEL_REGISTRATION_PROVIDED);
	}
	
}
