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
package org.osgi.test.assertj.monitoring;

import java.util.Map;
import java.util.Objects;
import java.util.function.Predicate;

import org.assertj.core.api.ThrowableAssert.ThrowingCallable;
import org.osgi.framework.BundleEvent;
import org.osgi.framework.FrameworkEvent;
import org.osgi.framework.ServiceEvent;
import org.osgi.test.assertj.monitoring.internal.MonitoringAssertionImpl;


/**
 * The Interface MonitoringAssert.
 */
public interface MonitoringAssertion {

	/**
	 * Creates a MonitoringAssert.
	 *
	 * @param execute - the action that will be invoked.
	 * @return the runtime monitoring assert
	 */
	public static MonitoringAssertion executeAndObserve(ThrowingCallable execute) {
		Objects.requireNonNull(execute, "Runnable must exist");
		return new MonitoringAssertionImpl(execute);
	}

	/**
	 * Until any service event is fired.
	 *
	 * @return MonitoringAssertTimeoutStep - to define the
	 *         observation-timeout.
	 */
	public MonitoringAssertionTimeoutStep untilAnyServiceEvent();

	/**
	 * Until a service event that matches the given predicate is fired.
	 *
	 * @param predicate the predicate that must match
	 * @return MonitoringAssertTimeoutStep - to define the
	 *         observation-timeout.
	 */
	public MonitoringAssertionTimeoutStep untilServiceEvent(Predicate<ServiceEvent> predicate);

	/**
	 * Until a service event registered with the given class is fired.
	 *
	 * @param objectClass the objectClass
	 * @return MonitoringAssertTimeoutStep - to define the
	 *         observation-timeout.
	 */
	public MonitoringAssertionTimeoutStep untilServiceEventRegistered(Class<?> objectClass);

	/**
	 * Until a service event registered with the given class and properties is
	 * fired.
	 *
	 * @param objectClass the objectClass
	 * @param map         the properties
	 * @return MonitoringAssertTimeoutStep - to define the
	 *         observation-timeout.
	 */
	public MonitoringAssertionTimeoutStep untilServiceEventRegistered(Class<?> objectClass,
			Map<String, Object> properties);

	/**
	 * Until a service event unregistered with the given class is fired.
	 *
	 * @param objectClass the objectClass
	 * @return MonitoringAssertTimeoutStep - to define the
	 *         observation-timeout.
	 */
	public MonitoringAssertionTimeoutStep untilServiceEventUnregistered(Class<?> objectClass);

	/**
	 * /** Until a service event unregistered with the given class and properties is
	 * fired.
	 *
	 * @param objectClass the objectClass
	 * @param map         the properties
	 * @return MonitoringAssertTimeoutStep - to define the
	 *         observation-timeout.
	 */
	public MonitoringAssertionTimeoutStep untilServiceEventUnregistered(Class<?> objectClass,
			Map<String, Object> properties);

	/**
	 * Until a service event modified with the given class is fired.
	 *
	 * @param objectClass the objectClass
	 * @return MonitoringAssertTimeoutStep - to define the
	 *         observation-timeout.
	 */
	public MonitoringAssertionTimeoutStep untilServiceEventModified(Class<?> objectClass);

	/**
	 * Until a service event modified with the given class and properties is fired.
	 *
	 * @param objectClass the objectClass
	 * @param map         the properties
	 * @return MonitoringAssertTimeoutStep - to define the
	 *         observation-timeout.
	 */
	public MonitoringAssertionTimeoutStep untilServiceEventModified(Class<?> objectClass,
			Map<String, Object> properties);

	/**
	 * Until a service event modified-endmatch with the given class is fired.
	 *
	 * @param clazz the objectClass
	 * @return MonitoringAssertTimeoutStep - to define the
	 *         observation-timeout.
	 */
	public MonitoringAssertionTimeoutStep untilServiceEventModifiedEndmatch(Class<?> objectClass);

	/**
	 * Until a service modified-endmatch with the given class and properties is
	 * fired.
	 *
	 * @param objectClass the objectClass
	 * @param properties  the properties
	 * @return MonitoringAssertTimeoutStep - to define the
	 *         observation-timeout.
	 */
	public MonitoringAssertionTimeoutStep untilServiceEventModifiedEndmatch(Class<?> objectClass,
			Map<String, Object> map);

	/**
	 * Until no more service event within given time is fired.
	 *
	 * @param timeMs the time ms
	 * @return MonitoringAssertTimeoutStep - to define the
	 *         observation-timeout.
	 */
	public MonitoringAssertionTimeoutStep untilNoMoreServiceEventWithin(long timeMs);

	/**
	 * Until no more service event within given time is fired.
	 *
	 * @param timeMs    the time ms
	 * @param predicate the predicate
	 * @return MonitoringAssertTimeoutStep - to define the
	 *         observation-timeout.
	 */
	public MonitoringAssertionTimeoutStep untilNoMoreServiceEventWithin(long timeMs,
			Predicate<ServiceEvent> predicate);

	/**
	 * Until no more service event within given time is fired.
	 *
	 * @param millis      the time in milliseconds
	 * @param objectClass the objectClass
	 * @return MonitoringAssertTimeoutStep - to define the
	 *         observation-timeout.
	 */
	public MonitoringAssertionTimeoutStep untilNoMoreServiceEventOfClassWithin(long millis, Class<?> objectClass);

	/**
	 * Until no more service event within given time is fired.
	 *
	 * @param millis      the time in milliseconds
	 * @param objectClass the objectClass
	 * @param properties  the properties
	 * @return MonitoringAssertTimeoutStep - to define the
	 *         observation-timeout.
	 */
	public MonitoringAssertionTimeoutStep untilNoMoreServiceEventOfClassWithin(long millis, Class<?> objectClass,
			Map<String, Object> properties);

	/**
	 * Until no more service event within given time is fired.
	 *
	 * @param millis      the time in milliseconds
	 * @param objectClass the objectClass
	 * @return MonitoringAssertTimeoutStep - to define the
	 *         observation-timeout.
	 */
	public MonitoringAssertionTimeoutStep untilNoMoreServiceEventRegisteredWithin(long millis,
			Class<?> objectClass);

	/**
	 * Until no more service event within given time is fired. *
	 * 
	 * @param millis      the time in milliseconds
	 * @param objectClass the objectClass
	 * @param properties  the properties
	 * @return MonitoringAssertTimeoutStep - to define the
	 *         observation-timeout.
	 */
	public MonitoringAssertionTimeoutStep untilNoMoreServiceEventRegistersWithin(long millis, Class<?> objectClass,
			Map<String, Object> properties);

	/**
	 * Until no more service event within given time is fired.
	 *
	 * @param millis      the time in milliseconds
	 * @param objectClass the objectClass
	 * @return MonitoringAssertTimeoutStep - to define the
	 *         observation-timeout.
	 */
	public MonitoringAssertionTimeoutStep untilNoMoreServiceEventUnregistersWithin(long millis,
			Class<?> objectClass);

	/**
	 * Until no more service event within given time is fired.
	 *
	 * @param millis      the time in milliseconds
	 * @param objectClass the objectClass
	 * @param properties  the properties
	 * @return MonitoringAssertTimeoutStep - to define the
	 *         observation-timeout.
	 */
	public MonitoringAssertionTimeoutStep untilNoMoreServiceEventUnregistersWithin(long millis,
			Class<?> objectClass, Map<String, Object> properties);

	/**
	 * Until no more service event within given time is fired.
	 *
	 * @param millis      the time in milliseconds
	 * @param objectClass the objectClass
	 * @return MonitoringAssertTimeoutStep - to define the
	 *         observation-timeout.
	 */
	public MonitoringAssertionTimeoutStep untilNoMoreServiceEventModifiedWithin(long millis, Class<?> objectClass);

	/**
	 * Until no more service event within given time is fired.
	 *
	 * @param millis      the time in milliseconds
	 * @param objectClass the objectClass
	 * @param properties  the properties
	 * @return MonitoringAssertTimeoutStep - to define the
	 *         observation-timeout.
	 */
	public MonitoringAssertionTimeoutStep untilNoMoreServiceEventModifiedWithin(long millis, Class<?> objectClass,
			Map<String, Object> properties);

	/**
	 * Until no more service event within given time is fired.
	 *
	 * @param millis      the time in milliseconds
	 * @param objectClass the objectClass
	 * @return MonitoringAssertTimeoutStep - to define the
	 *         observation-timeout.
	 */
	public MonitoringAssertionTimeoutStep untilNoMoreServiceEventModifiedEndmatchWithin(long millis,
			Class<?> objectClass);

	/**
	 * Until no more service event within given time is fired.
	 *
	 * @param millis      the time in milliseconds
	 * @param objectClass the objectClass
	 * @param properties  the properties
	 * @return MonitoringAssertTimeoutStep - to define the
	 *         observation-timeout.
	 */
	public MonitoringAssertionTimeoutStep untilNoMoreServiceEventModifiedEndmatchWithin(long millis,
			Class<?> objectClass, Map<String, Object> map);

	/**
	 * Until a bundle event matching the given predicate is fired.
	 *
	 * @param predicate the predicate
	 * @return MonitoringAssertTimeoutStep - to define the
	 *         observation-timeout.
	 */
	public MonitoringAssertionTimeoutStep untilBundleEvent(Predicate<BundleEvent> predicate);

	/**
	 * Until a framework event matching the given predicate is fired.
	 *
	 * @param predicate the predicate
	 * @return MonitoringAssertTimeoutStep - to define the
	 *         observation-timeout.
	 */
	public MonitoringAssertionTimeoutStep untilFrameworkEvent(Predicate<FrameworkEvent> predicate);

	/**
	 * will stop observation of the events direct after executing the action.
	 *
	 * @return the runtime monitoring result assert
	 */
	public MonitoringAssertionResult assertThat();

	/**
	 * will stop observation of the events at least after the given timeout.
	 *
	 * @param timeout - the maximal time in that actions will be observed. Could be
	 *                lower if an other `until`-Condition is set.
	 * @return the runtime monitoring result assert
	 */
	public MonitoringAssertionResult assertWithTimeoutThat(int timeout);

}
