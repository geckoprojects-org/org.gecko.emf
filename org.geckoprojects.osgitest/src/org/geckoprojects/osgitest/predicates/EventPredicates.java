package org.geckoprojects.osgitest.predicates;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Dictionary;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.osgi.framework.BundleEvent;
import org.osgi.framework.Constants;
import org.osgi.framework.FrameworkEvent;
import org.osgi.framework.ServiceEvent;
import org.osgi.framework.ServiceReference;
import org.osgi.test.common.dictionary.Dictionaries;

public interface EventPredicates {

	static <E> Predicate<? super E> any() {
		return element -> true;
	}

	static <E> Predicate<Optional<E>> isPresentAnd(final Predicate<? super E> predicate) {
		return optional -> optional.isPresent() && predicate.test(optional.get());
	}

	static <E> Predicate<List<E>> element(int index, final Predicate<? super E> predicate) {
		return elements -> elements.size() > 0 && elements.size() >= index && predicate.test(elements.get(index));
	}

	static <E> Predicate<List<E>> first(final Predicate<? super E> predicate) {
		return elements -> elements.size() > 0 && predicate.test(elements.get(0));
	}

	static <E> Predicate<List<E>> last(final Predicate<? super E> predicate) {
		return elements -> elements.size() > 0 && predicate.test(elements.get(elements.size() - 1));
	}

	static <E> Predicate<List<E>> all(final Predicate<? super E> predicate) {
		return elements -> elements.stream().allMatch(predicate);
	}

	public static <E> Predicate<List<E>> any(final Predicate<? super E> predicate) {
		return elements -> elements.stream().anyMatch(predicate);
	}

	public static <E> Predicate<List<E>> none(final Predicate<? super E> predicate) {
		return elements -> elements.stream().noneMatch(predicate);
	}

	@SafeVarargs
	public static <E> Predicate<List<E>> ordered(final Predicate<? super E> predicate,
			final Predicate<? super E>... predicatesNext) {
		return elements -> {

			List<Predicate<? super E>> predicates = new ArrayList<>();
			predicates.add(predicate);
			Collections.addAll(predicates, predicatesNext);

			long skip = 0;
			for (int i = 0; i <= predicatesNext.length - 1; i++) {
				Predicate<? super E> predicateBefore = predicates.get(i);
				Predicate<? super E> predicateAfter = predicates.get(i + 1);

				Optional<E> before = elements.stream().skip(skip).filter(predicateBefore).findFirst();
				Optional<E> after = elements.stream().skip(skip).filter(predicateAfter).findFirst();

				if (before.isPresent() && after.isPresent()) {

					int indexBefore = elements.indexOf(before.get());
					int indexAfter = elements.indexOf(after.get());
					if (indexBefore >= indexAfter) {
						return false;
					}
					skip = indexBefore;
				} else {
					return false;
				}
			}
			return true;
		};
	}

	public static <E> Predicate<List<E>> hasSize(long size, final Predicate<? super E> predicate) {
		return elements -> elements.stream().filter(predicate).count() == size;
	}

	public static <E> Predicate<List<E>> hasMoreThan(long size, final Predicate<? super E> predicate) {
		return elements -> elements.stream().filter(predicate).count() > size;
	}

	static <E> Predicate<List<E>> hasLessThan(long size, final Predicate<? super E> predicate) {
		return elements -> elements.stream().filter(predicate).count() < size;
	}

	class FrameworkEvents {

		// FramworkEvents

		public static Predicate<Object> isFrameworkEvent() {
			return e -> e instanceof FrameworkEvent;
		}

		public static Predicate<Object> isFrameworkEventAnd(Predicate<FrameworkEvent> predicate) {
			return e -> isFrameworkEvent().test(e) && predicate.test((FrameworkEvent) e);
		}

		public static Predicate<FrameworkEvent> isType(final int eventTypeMask) {
			return e -> (e.getType() & eventTypeMask) != 0;
		}

		public static Predicate<FrameworkEvent> isTypeError() {
			return e -> isType(FrameworkEvent.ERROR).test(e);
		}

		public static Predicate<FrameworkEvent> isTypeInfo() {
			return e -> isType(FrameworkEvent.INFO).test(e);
		}

		public static Predicate<FrameworkEvent> isTypePackagesRefreshed() {
			return e -> isType(FrameworkEvent.PACKAGES_REFRESHED).test(e);
		}

		public static Predicate<FrameworkEvent> isTypeStarted() {
			return e -> isType(FrameworkEvent.STARTED).test(e);
		}

		public static Predicate<FrameworkEvent> isTypeStartlevelChanged() {
			return e -> isType(FrameworkEvent.STARTLEVEL_CHANGED).test(e);
		}

		public static Predicate<FrameworkEvent> isTypeStopped() {
			return e -> isType(FrameworkEvent.STOPPED).test(e);
		}

		public static Predicate<FrameworkEvent> isTypeStoppedUpdate() {
			return e -> isType(FrameworkEvent.STOPPED_UPDATE).test(e);
		}

		public static Predicate<FrameworkEvent> isTypeWaitTimeout() {
			return e -> isType(FrameworkEvent.WAIT_TIMEDOUT).test(e);
		}

		public static Predicate<FrameworkEvent> isTypeWarning() {
			return e -> isType(FrameworkEvent.WARNING).test(e);
		}
	}

	class ServiceEvents {

		public static Predicate<Object> isServiceEvent() {
			return e -> e instanceof ServiceEvent;
		}

		public static Predicate<Object> isServiceEventAnd(Predicate<ServiceEvent> predicate) {
			return e -> isServiceEvent().test(e) && predicate.test((ServiceEvent) e);
		}

		// ServiceEvents
		public static Predicate<ServiceEvent> isType(final int eventTypeMask) {
			return e -> (e.getType() & eventTypeMask) != 0;
		}

		public static Predicate<ServiceEvent> isTypeRegistered() {
			return e -> isType(ServiceEvent.REGISTERED).test(e);
		}

		public static Predicate<ServiceEvent> isTypeModified() {
			return e -> isType(ServiceEvent.MODIFIED).test(e);
		}

		public static Predicate<ServiceEvent> isTypeModifiedEndmatch() {
			return e -> isType(ServiceEvent.MODIFIED_ENDMATCH).test(e);
		}

		public static Predicate<ServiceEvent> isTypeUnregistering() {
			return e -> isType(ServiceEvent.UNREGISTERING).test(e);
		}

		public static Predicate<ServiceEvent> hasObjectClass(final Class<?> objectClass) {

			return e -> {
				Object classes = e.getServiceReference().getProperty(Constants.OBJECTCLASS);

				if (classes != null && classes instanceof String[]) {
					return Stream.of((String[]) classes).filter(Objects::nonNull)
							.anyMatch(objectClass.getName()::equals);
				}
				return false;
			};

		}

		public static Predicate<ServiceEvent> containServiceProperty(final String key, Object value) {
			return e -> containsServiceProperties(Dictionaries.dictionaryOf(key, value)).test(e);
		}

		public static Predicate<ServiceEvent> containsServiceProperties(Map<String, ?> map) {
			return e -> {
				ServiceReference<?> sr = e.getServiceReference();
				List<String> keys = Stream.of(sr.getPropertyKeys()).collect(Collectors.toList());
				for (Entry<String, ?> entry : map.entrySet()) {
					if (!keys.contains(entry.getKey())) {
						return false;
					}
					if (!Objects.equals(sr.getProperty(entry.getKey()), entry.getValue())) {
						return false;
					}
				}
				return true;
			};
		}

		public static Predicate<ServiceEvent> containsServiceProperties(Dictionary<String, ?> dictionary) {
			return e -> containsServiceProperties(Dictionaries.asMap(dictionary)).test(e);
		}

		public static Predicate<ServiceEvent> matches(int eventTypeMask, final Class<?> objectClass) {
			return e -> isType(eventTypeMask).test(e) && (hasObjectClass(objectClass).test(e));
		}

		public static Predicate<ServiceEvent> matches(int eventTypeMask, final Class<?> objectClass,
				Map<String, ?> map) {
			return e -> isType(eventTypeMask).test(e) && (hasObjectClass(objectClass).test(e))
					&& containsServiceProperties(map).test(e);
		}

		public static Predicate<ServiceEvent> matches(int eventTypeMask, final Class<?> objectClass,
				Dictionary<String, ?> dictionary) {
			return e -> matches(eventTypeMask, objectClass, Dictionaries.asMap(dictionary)).test(e);
		}

		public static Predicate<ServiceEvent> isTypeRegistered(final Class<?> objectClass) {
			return matches(ServiceEvent.REGISTERED, objectClass);
		}

		public static Predicate<ServiceEvent> isTypeRegisteredWith(final Class<?> objectClass, Map<String, ?> map) {
			return matches(ServiceEvent.REGISTERED, objectClass, map);
		}

		public static Predicate<ServiceEvent> isTypeRegisteredWith(final Class<?> objectClass,
				Dictionary<String, ?> dictionary) {
			return matches(ServiceEvent.REGISTERED, objectClass, dictionary);
		}

		public static Predicate<ServiceEvent> isTypeUnregistering(final Class<?> objectClass) {
			return matches(ServiceEvent.UNREGISTERING, objectClass);
		}

		public static Predicate<ServiceEvent> isTypeModified(final Class<?> objectClass) {
			return matches(ServiceEvent.MODIFIED, objectClass);
		}

		public static Predicate<ServiceEvent> isTypeModifiedEndmatch(final Class<?> objectClass) {
			return matches(ServiceEvent.MODIFIED_ENDMATCH, objectClass);
		}

		public static Predicate<ServiceEvent>  matches(Class<?> objectClass, Map<String, Object> map) {
			return e ->  (hasObjectClass(objectClass).test(e))
					&& containsServiceProperties(map).test(e);
		}
	}

	interface BundleEvents {

		// BundleEvents

		public static Predicate<Object> isBundleEvent() {
			return e -> e instanceof BundleEvent;
		}

		public static Predicate<Object> isBundleEventAnd(Predicate<BundleEvent> predicate) {
			return e -> isBundleEvent().test(e) && predicate.test((BundleEvent) e);
		}

		public static Predicate<BundleEvent> isType(final int eventTypeMask) {
			return e -> (e.getType() & eventTypeMask) != 0;
		}

		// BundleEvents - by type
		public static Predicate<BundleEvent> isTypeInstalled() {
			return e -> isType(BundleEvent.INSTALLED).test(e);
		}

		public static Predicate<BundleEvent> isTypeLazyActivation() {
			return e -> isType(BundleEvent.LAZY_ACTIVATION).test(e);
		}

		public static Predicate<BundleEvent> isTypeResolved() {
			return e -> isType(BundleEvent.RESOLVED).test(e);
		}

		public static Predicate<BundleEvent> isTypeStarted() {
			return e -> isType(BundleEvent.STARTED).test(e);
		}

		public static Predicate<BundleEvent> isTypeStarting() {
			return e -> isType(BundleEvent.STARTING).test(e);
		}

		public static Predicate<BundleEvent> isTypeStopped() {
			return e -> isType(BundleEvent.STOPPED).test(e);
		}

		public static Predicate<BundleEvent> isTypeStopping() {
			return e -> isType(BundleEvent.STOPPING).test(e);
		}

		public static Predicate<BundleEvent> isTypeUninstalled() {
			return e -> isType(BundleEvent.UNINSTALLED).test(e);
		}

		public static Predicate<BundleEvent> isTypeUnresolved() {
			return e -> isType(BundleEvent.UNRESOLVED).test(e);
		}

		public static Predicate<BundleEvent> isTypeUpdated() {
			return e -> isType(BundleEvent.UPDATED).test(e);
		}
	}

}
