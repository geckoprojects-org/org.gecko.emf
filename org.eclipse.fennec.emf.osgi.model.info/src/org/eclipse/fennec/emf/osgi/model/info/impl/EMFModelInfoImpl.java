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
package org.eclipse.fennec.emf.osgi.model.info.impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EClassifier;
import org.eclipse.emf.ecore.EFactory;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EcorePackage;
import org.eclipse.emf.ecore.impl.DynamicEObjectImpl;
import org.eclipse.fennec.emf.osgi.configurator.EPackageConfigurator;
import org.eclipse.fennec.emf.osgi.model.info.EMFModelInfo;
import org.osgi.annotation.bundle.Capability;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.annotations.ReferenceCardinality;
import org.osgi.service.component.annotations.ReferencePolicy;
import org.osgi.service.component.annotations.ReferencePolicyOption;

/**
 * An implementation for the {@link EMFModelInfo} service
 * 
 * @author Juergen Albert
 * @since 8 Nov 2018
 */
@Capability(namespace = EMFModelInfo.NAMESPACE, name = EMFModelInfo.NAME)
@Component(service = EMFModelInfo.class)
public class EMFModelInfoImpl extends HashMap<String, Object> implements EMFModelInfo, EPackage.Registry {

	/** serialVersionUID */
	private static final long serialVersionUID = 7749336016374647599L;

	private transient Map<Class<?>, EClassifier> classes = new ConcurrentHashMap<>();
	private transient Map<EClass, List<EClass>> upperHierarchy = new ConcurrentHashMap<>();
	private transient Map<EClass, List<EClass>> needsRevisiting = new ConcurrentHashMap<>();
	private transient List<EPackageConfigurator> list = new ArrayList<>();
	
	private final ReadWriteLock lock = new ReentrantReadWriteLock();

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.gecko.emf.osgi.model.info.EMFModelInfo#getEClassifierForClass(java.
	 * lang.Class)
	 */
	@Override
	public Optional<EClassifier> getEClassifierForClass(Class<?> clazz) {
		return classes.entrySet().stream().filter(e -> e.getKey().equals(clazz)).map(Entry::getValue).findFirst();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.gecko.emf.osgi.model.info.EMFModelInfo#getEClassifierForClass(java.
	 * lang.String)
	 */
	@Override
	public Optional<EClassifier> getEClassifierForClass(String fullQualifiedClassName) {
		lock.readLock().lock();
		try {
			return classes.entrySet().stream().filter(e -> e.getKey().getName().equals(fullQualifiedClassName))
					.map(Entry::getValue).findFirst();
		} finally {
			lock.readLock().unlock();
		}
	}

	@Reference(cardinality = ReferenceCardinality.MULTIPLE, policy = ReferencePolicy.DYNAMIC, policyOption = ReferencePolicyOption.GREEDY)
	public void bindEPackageConfigurator(EPackageConfigurator configurator) {
		lock.writeLock().lock();
		try {
			list.add(configurator);
			refresh();
		} finally {
			lock.writeLock().unlock();
		}
	}

	private synchronized void refresh() {
		classes = new ConcurrentHashMap<>();
		upperHierarchy = new ConcurrentHashMap<>();
		needsRevisiting = new ConcurrentHashMap<>();

		list.forEach(c -> c.configureEPackage(this));

	}

	public void unbindEPackageConfigurator(EPackageConfigurator configurator) {
		lock.writeLock().lock();
		try {
			list.remove(configurator);
			configurator.unconfigureEPackage(this);
			refresh();
		} finally {
			lock.writeLock().unlock();
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.util.HashMap#put(java.lang.Object, java.lang.Object)
	 */
	@Override
	public Object put(String uri, Object value) {
		if (value instanceof EPackage) {
			EPackage ePackage = (EPackage) value;
			addEClassesOfEPackage(ePackage);
		}
		return null;
	}

	private synchronized void addEClassesOfEPackage(EPackage ePackage) {
		ePackage.getEClassifiers().stream().filter(ec -> ec.getInstanceClass() != null).forEach(ec -> {
			analyseHirachy(ec);
			Class<?> instanceClass = ec.getInstanceClass();
			if (instanceClass != DynamicEObjectImpl.class) {
				classes.put(instanceClass, ec);
			}
		});
	}

	/**
	 * @param ec EClass to analyze the Hierarchy for
	 */
	private void analyseHirachy(EClassifier ec) {
		if (!(ec instanceof EClass) || ec.getEPackage().equals(EcorePackage.eINSTANCE)) {
			return;
		}
		EClass eClass = (EClass) ec;
		List<EClass> thisHirachy = needsRevisiting.remove(eClass);
		if (thisHirachy == null) {
			thisHirachy = Collections.synchronizedList(new LinkedList<EClass>());
		}
		upperHierarchy.put(eClass, thisHirachy);
		eClass.getEAllSuperTypes().forEach(superEClass -> {
			if (superEClass.equals(EcorePackage.Literals.ECLASS)) {
				return;
			}
			if (upperHierarchy.containsKey(superEClass)) {
				List<EClass> hierarchy = upperHierarchy.get(superEClass);
				if (!hierarchy.contains(superEClass)) {
					hierarchy.add(eClass);
				}
			} else {
				List<EClass> otherHierachy = needsRevisiting.getOrDefault(superEClass,
						Collections.synchronizedList(new LinkedList<EClass>()));
				otherHierachy.add(eClass);
				needsRevisiting.put(superEClass, otherHierachy);
			}
		});

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.emf.ecore.EPackage.Registry#getEFactory(java.lang.String)
	 */
	@Override
	public EFactory getEFactory(String uri) {
		throw new UnsupportedOperationException("This method must not be called");
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.emf.ecore.EPackage.Registry#getEPackage(java.lang.String)
	 */
	@Override
	public EPackage getEPackage(String nsUri) {
		throw new UnsupportedOperationException("This method must not be called");

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.gecko.emf.osgi.model.info.EMFModelInfo#getUpperTypeHierarchyForEClass(
	 * org.eclipse.emf.ecore.EClass)
	 */
	@Override
	public List<EClass> getUpperTypeHierarchyForEClass(EClass eClass) {
		lock.readLock().lock();
		try {
			if (!upperHierarchy.containsKey(eClass)) {
				return Collections.emptyList();
			}
			
			return upperHierarchy.get(eClass);
		} finally {
			lock.readLock().unlock();
		}
	}
}