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
package org.gecko.emf.osgi.ecore;

import java.security.SecureRandom;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EcorePackage;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.Resource.Factory.Registry;
import org.eclipse.emf.ecore.resource.impl.BinaryResourceImpl;
import org.eclipse.emf.ecore.resource.impl.ResourceFactoryImpl;
import org.eclipse.emf.ecore.xmi.impl.EMOFResourceFactoryImpl;
import org.eclipse.emf.ecore.xmi.impl.EcoreResourceFactoryImpl;
import org.eclipse.emf.ecore.xmi.impl.XMIResourceFactoryImpl;
import org.eclipse.emf.ecore.xml.namespace.XMLNamespacePackage;
import org.eclipse.emf.ecore.xml.type.XMLTypePackage;
import org.gecko.emf.osgi.configurator.EPackageConfigurator;
import org.gecko.emf.osgi.configurator.ResourceFactoryConfigurator;
import org.gecko.emf.osgi.constants.EMFNamespaces;
import org.osgi.framework.Constants;

/**
 * Configurator for for the {@link EcorePackage}, which is a {@link EPackageConfigurator} and {@link ResourceFactoryConfigurator}
 * As default it registers resource factories for:
 * <li>XMI</li> 
 * <li>EMOF</li>
 * <li>ecore</li>
 * <li>binary</li>
 * 
 * </br>Also the following mime-types are registered as content type identifiers:
 * <li>application/xmi XMI</li>
 * <li>application/xml XML</li>
 * <li>application/octet-stream Binary</li>
 * s
 * @author Mark Hoffmann
 * @since 25.07.2017
 */
public class EcoreConfigurator implements EPackageConfigurator, ResourceFactoryConfigurator {

	/** OCTET_STREAM */
	protected static final String OCTET_STREAM = "application/octet-stream";
	/** APPLICATION_XMI */
	protected static final String APPLICATION_XMI = "application/xmi";
	/** EMF_EMOF */
	protected static final String EMF_EMOF = "org.eclipse.emf.emof";
	/** EMF_ECORE */
	protected static final String EMF_ECORE = EcorePackage.eCONTENT_TYPE;
	/** ECORE */
	private static final String ECORE = "ecore";
	public static final Map<String, Object> PROPERTIES = EcoreConfigurator.getProperties();
	private static SecureRandom random = null;
	
	private static Map<String, Object> getProperties(){
		HashMap<String, Object> result = new HashMap<>();
		result.put(EMFNamespaces.EMF_CONFIGURATOR_NAME,ECORE); 
		result.put(EMFNamespaces.EMF_MODEL_CONTENT_TYPE, Arrays.asList( 
				EMF_ECORE, 
				EMF_EMOF, 
				APPLICATION_XMI, 
				OCTET_STREAM)); 
		result.put(EMFNamespaces.EMF_MODEL_FILE_EXT, Arrays.asList( 
				"*", 
				"xmi", 
				ECORE, 
				"emof", 
				"bin")); 
		result.put(EMFNamespaces.EMF_MODEL_VERSION, "1.0.0");
		result.put(EMFNamespaces.EMF_MODEL_NAME, ECORE);
		result.put(EMFNamespaces.EMF_MODEL_REGISTRATION, EMFNamespaces.MODEL_REGISTRATION_INTERNAL);
		result.put(EMFNamespaces.EMF_MODEL_NSURI, Arrays.asList( 
				XMLTypePackage.eNS_URI,
				XMLNamespacePackage.eNS_URI,
				EcorePackage.eNS_URI));
		if (random == null) {
			random = new SecureRandom();
		}
		result.put(Constants.SERVICE_ID, random.nextLong());
		return result;
	}
			
	
	protected static Resource.Factory binaryFactory = new ResourceFactoryImpl(){
		@Override
		public Resource createResource(URI uri) {
			return new BinaryResourceImpl(uri);
		}
	};
	
	@Override
	public void configureResourceFactory(Registry registry) {
		registry.getExtensionToFactoryMap().put("*", new XMIResourceFactoryImpl());
		registry.getExtensionToFactoryMap().put("xmi", new XMIResourceFactoryImpl());
		registry.getExtensionToFactoryMap().put(ECORE, new EcoreResourceFactoryImpl());
		registry.getExtensionToFactoryMap().put("emof", new EMOFResourceFactoryImpl());
		registry.getExtensionToFactoryMap().put("bin", binaryFactory);
		registry.getContentTypeToFactoryMap().put(EMF_ECORE, new EcoreResourceFactoryImpl());
		registry.getContentTypeToFactoryMap().put(EMF_EMOF, new EcoreResourceFactoryImpl());
		registry.getContentTypeToFactoryMap().put(APPLICATION_XMI, new XMIResourceFactoryImpl());
		registry.getContentTypeToFactoryMap().put(OCTET_STREAM, binaryFactory);
	}

	@Override
	public void unconfigureResourceFactory(Registry registry) {
		registry.getExtensionToFactoryMap().remove("*");
		registry.getExtensionToFactoryMap().remove("xmi");
		registry.getExtensionToFactoryMap().remove(ECORE);
		registry.getExtensionToFactoryMap().remove("emof");
		registry.getContentTypeToFactoryMap().remove(EMF_ECORE);
		registry.getContentTypeToFactoryMap().remove(EMF_EMOF);
		registry.getContentTypeToFactoryMap().remove(APPLICATION_XMI);
		registry.getContentTypeToFactoryMap().remove(OCTET_STREAM);
	}

	@Override
	public void configureEPackage(org.eclipse.emf.ecore.EPackage.Registry registry) {
		registry.put(XMLTypePackage.eNS_URI, XMLTypePackage.eINSTANCE);
		registry.put(XMLNamespacePackage.eNS_URI, XMLNamespacePackage.eINSTANCE);
		registry.put(EcorePackage.eNS_URI, EcorePackage.eINSTANCE);
	}

	@Override
	public void unconfigureEPackage(org.eclipse.emf.ecore.EPackage.Registry registry) {
		registry.remove(XMLTypePackage.eNS_URI);
		registry.remove(XMLNamespacePackage.eNS_URI);
		registry.remove(EcorePackage.eNS_URI);
	}

}
