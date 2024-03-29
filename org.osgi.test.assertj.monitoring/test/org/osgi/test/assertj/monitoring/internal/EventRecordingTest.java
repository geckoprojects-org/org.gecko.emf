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
package org.osgi.test.assertj.monitoring.internal;

import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

import org.assertj.core.api.ThrowableAssert.ThrowingCallable;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.BundleEvent;
import org.osgi.framework.BundleListener;
import org.osgi.framework.FrameworkEvent;
import org.osgi.framework.FrameworkListener;
import org.osgi.framework.ServiceEvent;
import org.osgi.framework.ServiceListener;
import org.osgi.framework.ServiceReference;
import org.osgi.test.assertj.monitoring.TimedEvent;

import static org.assertj.core.api.Assertions.assertThat;

@Disabled
public class EventRecordingTest {

	static List<FrameworkListener> frameworkListeners = new ArrayList<>();
	static List<ServiceListener> serviceListeners = new ArrayList<>();
	static List<BundleListener> bundleListeners = new ArrayList<>();
	static BundleContext bundleContext;

	static Bundle bundle;

	@BeforeAll
	public static void beforeAll() {
		bundle = mock(Bundle.class);

		when(bundle.getSymbolicName()).thenReturn("test");

		bundleContext = mock(BundleContext.class);

		doAnswer(invocation -> {
			Object listener = invocation.getArgument(0);
			bundleListeners.add((BundleListener) listener);
			return null;
		}).when(bundleContext).addBundleListener(Mockito.any(BundleListener.class));

		doAnswer(invocation -> {
			Object listener = invocation.getArgument(0);
			bundleListeners.remove(listener);
			return null;
		}).when(bundleContext).removeBundleListener(Mockito.any(BundleListener.class));

		doAnswer(invocation -> {
			Object listener = invocation.getArgument(0);
			frameworkListeners.add((FrameworkListener) listener);
			return null;
		}).when(bundleContext).addFrameworkListener(Mockito.any(FrameworkListener.class));

		doAnswer(invocation -> {
			Object listener = invocation.getArgument(0);
			frameworkListeners.remove(listener);
			return null;
		}).when(bundleContext).removeFrameworkListener(Mockito.any(FrameworkListener.class));

		doAnswer(invocation -> {
			Object listener = invocation.getArgument(0);
			serviceListeners.add((ServiceListener) listener);
			return null;
		}).when(bundleContext).addServiceListener(Mockito.any(ServiceListener.class));
		doAnswer(invocation -> {
			Object listener = invocation.getArgument(0);
			serviceListeners.remove(listener);
			return null;
		}).when(bundleContext).removeServiceListener(Mockito.any(ServiceListener.class));

	}

	@Test
	public void test_takeSnapsot() throws Exception {

		// Test any Match
		Predicate<Object> matches = (event) -> {
			if (event instanceof ServiceEvent) {
				return ((ServiceEvent) event).getType() == ServiceEvent.REGISTERED;
			}
			return false;
		};

		ThrowingCallable execute = () -> {

			assertThat(frameworkListeners).hasSize(1);

			assertThat(bundleListeners).hasSize(1);

			assertThat(serviceListeners).hasSize(1);

			frameworkListeners.forEach(
					(l) -> l.frameworkEvent(new FrameworkEvent(FrameworkEvent.INFO, mock(Bundle.class), null)));

			bundleListeners.forEach((l) -> l
					.bundleChanged(new BundleEvent(BundleEvent.INSTALLED, mock(Bundle.class), mock(Bundle.class))));

			serviceListeners.forEach(
					(l) -> l.serviceChanged(new ServiceEvent(ServiceEvent.REGISTERED, mock(ServiceReference.class))));

		};
		// Create an Obervator with the given count and Predicate-Matcher
		EventRecording result = EventRecording.record(bundleContext, execute, matches, 0);

		// Check that the event happened

		assertThat(result).isNotNull();
		assertThat(result.isTimedOut()).isFalse();
		assertThat(result.timedEvents()).isNotNull();
		assertThat(result.timedEvents()).hasSize(3);

		assertThat(result.timedEvents()).element(0).extracting(TimedEvent::getEvent).isInstanceOf(FrameworkEvent.class);

		assertThat(result.timedEvents()).element(1).extracting(TimedEvent::getEvent).isInstanceOf(BundleEvent.class);

		assertThat(result.timedEvents()).element(2).extracting(TimedEvent::getEvent).isInstanceOf(ServiceEvent.class);

		assertThat(bundleListeners).isEmpty();
		assertThat(frameworkListeners).isEmpty();
		assertThat(serviceListeners).isEmpty();

	}

	class A {
	}
}