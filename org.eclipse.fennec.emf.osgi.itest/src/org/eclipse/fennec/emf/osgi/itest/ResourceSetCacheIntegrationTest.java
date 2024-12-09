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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.io.IOException;
import java.util.Dictionary;
import java.util.Hashtable;

import org.assertj.core.api.Assertions;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.fennec.emf.osgi.ResourceSetCache;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.osgi.framework.InvalidSyntaxException;
import org.osgi.framework.ServiceReference;
import org.osgi.service.cm.Configuration;
import org.osgi.service.cm.ConfigurationAdmin;
import org.osgi.test.assertj.monitoring.MonitoringAssertion;
import org.osgi.test.common.annotation.InjectService;
import org.osgi.test.common.service.ServiceAware;
import org.osgi.test.junit5.context.BundleContextExtension;
import org.osgi.test.junit5.service.ServiceExtension;

/**
 * Integration test for the {@link ResourceSetCache}
 * 
 * @author Mark Hoffmann * @since 25.07.2017
 */
@ExtendWith(BundleContextExtension.class)
@ExtendWith(ServiceExtension.class)
public class ResourceSetCacheIntegrationTest {

	@InjectService
	ConfigurationAdmin ca;

	/**
	 * Tests, if the service was set up correctly
	 * 
	 * @throws IOException
	 * @throws InvalidSyntaxException
	 * @throws InterruptedException
	 */
	@Test
	public void testResourceSetCacheExists(@InjectService(cardinality = 0) ServiceAware<ResourceSetCache> sa1,
			@InjectService(cardinality = 0, filter = "(" + ResourceSetCache.RESOURCE_SET_CACHE_NAME
					+ "=test2)") ServiceAware<ResourceSetCache> sa2)
			throws IOException, InvalidSyntaxException, InterruptedException {

		Assertions.assertThat(sa1.getServices()).isEmpty();
		Dictionary<String, Object> configProperties01 = new Hashtable<>();
		configProperties01.put(ResourceSetCache.RESOURCE_SET_CACHE_NAME, "test1");

		MonitoringAssertion.executeAndObserve(() -> {

			Configuration c1 = ca.getFactoryConfiguration("ResourceSetCache", "one", "?");
			c1.update(configProperties01);

		}).untilNoMoreServiceEventWithin(100).assertWithTimeoutThat(1000)
				.hasExactlyOneServiceEventRegisteredWith(ResourceSetCache.class);

		Assertions.assertThat(sa1.getServices()).hasSize(1);
		ServiceReference<ResourceSetCache> cacheRef = sa1.getServiceReference();
		assertNotNull(cacheRef);

		assertEquals("test1", cacheRef.getProperty(ResourceSetCache.RESOURCE_SET_CACHE_NAME));
		ResourceSetCache cache1 = sa1.getService();
		assertNotNull(cache1);
		ResourceSet rs11 = cache1.getResourceSet();
		assertNotNull(rs11);
		ResourceSet rs21 = cache1.getResourceSet();
		assertNotNull(rs21);
		assertEquals(rs11, rs21);

		Dictionary<String, Object> configProperties02 = new Hashtable<>();
		configProperties02.put(ResourceSetCache.RESOURCE_SET_CACHE_NAME, "test2");

		MonitoringAssertion.executeAndObserve(() -> {

			Configuration c2 = ca.getFactoryConfiguration("ResourceSetCache", "two", "?");
			c2.update(configProperties02);

		}).untilNoMoreServiceEventWithin(100).assertWithTimeoutThat(1000)
				.hasExactlyOneServiceEventRegisteredWith(ResourceSetCache.class);

		Assertions.assertThat(sa1.getServices()).hasSize(2);

		Assertions.assertThat(sa2.getServices()).hasSize(1);

		ServiceReference<?> cacheRef2 = sa2.getServiceReference();
		assertEquals("test2", cacheRef2.getProperty(ResourceSetCache.RESOURCE_SET_CACHE_NAME));
		ResourceSetCache cache2 = (ResourceSetCache) sa2.getService();

		assertNotEquals(cache1, cache2);

		assertNotNull(cache2);
		ResourceSet rs12 = cache2.getResourceSet();
		assertNotNull(rs12);
		ResourceSet rs22 = cache2.getResourceSet();
		assertNotNull(rs22);
		assertEquals(rs12, rs22);

		assertNotEquals(rs11, rs12);
	}

}
