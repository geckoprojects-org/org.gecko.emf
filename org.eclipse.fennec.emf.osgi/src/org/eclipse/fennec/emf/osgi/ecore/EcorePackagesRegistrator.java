/**
 * Copyright (c) 2012 - 2024 Data In Motion and others.
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
package org.eclipse.fennec.emf.osgi.ecore;

import java.util.ArrayList;
import java.util.Dictionary;
import java.util.Hashtable;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EcorePackage;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.xmi.impl.EMOFResourceFactoryImpl;
import org.eclipse.emf.ecore.xmi.impl.EcoreResourceFactoryImpl;
import org.eclipse.emf.ecore.xmi.impl.XMIResourceFactoryImpl;
import org.eclipse.emf.ecore.xml.namespace.XMLNamespacePackage;
import org.eclipse.emf.ecore.xml.type.XMLTypePackage;
import org.eclipse.fennec.emf.osgi.constants.EMFNamespaces;
import org.osgi.framework.BundleContext;
import org.osgi.framework.FrameworkUtil;
import org.osgi.framework.ServiceRegistration;
import org.osgi.framework.Version;

/**
 * A Helper to register and unregister the EcorePackages (and related XML Packages)
 * 
 * @author Juergen Albert
 * @since 9 Sep 2024
 */
public class EcorePackagesRegistrator {

	private static AtomicInteger activateCount = new AtomicInteger(0);
	
	private static BundleContext bctx = FrameworkUtil.getBundle(EcorePackagesRegistrator.class).getBundleContext();

	private static List<ServiceRegistration<?>> registrations = new ArrayList<>();
	
	public static void start() {
		synchronized (activateCount) {
			if(activateCount.incrementAndGet() == 1) {
				registrations.add(bctx.registerService(new String[] {EPackage.class.getName(), EcorePackage.class.getName()}, EcorePackage.eINSTANCE, getServiceProperties(EcorePackage.eINSTANCE, null, EcorePackage.eCONTENT_TYPE, new Version("2002"))));
				registrations.add(bctx.registerService(new String[] {EPackage.class.getName(), XMLTypePackage.class.getName()}, XMLTypePackage.eINSTANCE, getServiceProperties(XMLTypePackage.eINSTANCE, null, null, new Version("2003"))));
				registrations.add(bctx.registerService(new String[] {EPackage.class.getName(), XMLNamespacePackage.class.getName()}, XMLNamespacePackage.eINSTANCE, getServiceProperties(XMLNamespacePackage.eINSTANCE, null, null, new Version("1998"))));
				
				registrations.add(bctx.registerService(new String[] {Resource.Factory.class.getName(), XMIResourceFactoryImpl.class.getName()}, new XMIResourceFactoryImpl(), getServiceProperties(EcorePackage.eINSTANCE, new String[]{"*", "xmi"}, "application/xmi", new Version("2002"))));
				registrations.add(bctx.registerService(new String[] {Resource.Factory.class.getName(), EcoreResourceFactoryImpl.class.getName()}, new EcoreResourceFactoryImpl(), getServiceProperties(EcorePackage.eINSTANCE, new String[]{"ecore"}, EcorePackage.eCONTENT_TYPE, new Version("2002"))));
				registrations.add(bctx.registerService(new String[] {Resource.Factory.class.getName(), EMOFResourceFactoryImpl.class.getName()}, new EMOFResourceFactoryImpl(), getServiceProperties(EcorePackage.eINSTANCE, new String[]{"emof"}, "org.eclipse.emf.emof", new Version("2002"))));
				registrations.add(bctx.registerService(new String[] {Resource.Factory.class.getName()}, EcoreConfigurator.binaryFactory, getServiceProperties(EcorePackage.eINSTANCE, new String[]{"bin"}, EcoreConfigurator.OCTET_STREAM, new Version("2002"))));
			}
		}
	}
	
	/**
	 * A method providing the Properties the services around this Model should be registered with.
	 * @generated
	 */
	public static Dictionary<String, Object> getServiceProperties(EPackage ePackage, String[] fileExt, String contentType, Version version) {
		Dictionary<String, Object> properties = new Hashtable<String, Object>();
		properties.put(EMFNamespaces.EMF_MODEL_NAME, ePackage.getName());
		properties.put(EMFNamespaces.EMF_MODEL_NSURI, ePackage.getNsURI());
		properties.put(EMFNamespaces.EMF_MODEL_REGISTRATION, EMFNamespaces.MODEL_REGISTRATION_PROVIDED);
		if(fileExt != null) {
			properties.put(EMFNamespaces.EMF_MODEL_FILE_EXT, fileExt);
		}
		if(contentType != null) {
			properties.put(EMFNamespaces.EMF_MODEL_CONTENT_TYPE, contentType);
		}
		properties.put(EMFNamespaces.EMF_MODEL_VERSION, version);
		return properties;
	}
	
	public static void stop() {
		synchronized (activateCount) {
			if(activateCount.decrementAndGet() == 0) {
				for (int i = registrations.size(); i >= 0 ; --i) {
					ServiceRegistration<?> serviceRegistration = registrations.get(i);
					serviceRegistration.unregister();
				}
				registrations.clear();
			}
		}
	}
}
