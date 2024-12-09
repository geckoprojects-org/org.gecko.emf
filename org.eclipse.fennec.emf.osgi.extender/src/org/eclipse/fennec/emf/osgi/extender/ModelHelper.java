/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.eclipse.fennec.emf.osgi.extender;

import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;
import static java.util.Objects.requireNonNull;
import static org.eclipse.fennec.emf.osgi.constants.EMFNamespaces.EMF_MODEL_EXTENDER_DEFAULT_PATH;
import static org.eclipse.fennec.emf.osgi.constants.EMFNamespaces.EMF_MODEL_EXTENDER_PROP_MODELS_NAME;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Dictionary;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.fennec.emf.osgi.constants.EMFNamespaces;
import org.eclipse.fennec.emf.osgi.extender.model.Model;
import org.osgi.annotation.bundle.Requirement;
import org.osgi.framework.Bundle;
import org.osgi.framework.wiring.BundleRequirement;
import org.osgi.framework.wiring.BundleWire;
import org.osgi.framework.wiring.BundleWiring;
import org.osgi.namespace.extender.ExtenderNamespace;

/**
 * Helper to handle model loading
 * @author mark
 * @since 17.10.2022
 */
public class ModelHelper {
	
	private static Logger logger = Logger.getLogger(ModelHelper.class.getName());

	public static final class Diagnostic {

		public final List<String> warnings = new ArrayList<>();
		public final List<String> errors = new ArrayList<>();
	}
	
	private ModelHelper() {
	}

	/**
	 * Read all models from a bundle
	 * @param bundle the bundle to be read
	 * @param resourceSet the resourceSet to load the models with
	 * @param paths The paths to read the models from
	 * @param diagnostic The report for errors and warnings
	 * @return the list of loaded models
	 */
	public static List<Model> readModelsFromBundle(final Bundle bundle,
			final ResourceSet resourceSet,
			final Set<String> paths,
			final Diagnostic diagnostic) {
		if (paths == null) {
			return Collections.emptyList();
		} else {
			return paths.stream().
					map(path->readModel(bundle, resourceSet, path, diagnostic)).
					flatMap(List::stream).
					collect(Collectors.toList());
		}
	}

	/**
	 * Read all model files from a given path in the bundle
	 * @param bundle the bundle to load the model from
	 * @param resourceSet the {@link ResourceSet} to load the model with
	 * @param path the model path in the JAR
	 * @param diagnostic 
	 * @return List of loaded models
	 */
	public static List<Model> readModel(final Bundle bundle,
			final ResourceSet resourceSet,
			final String path,
			final Diagnostic diagnostic) {
		final List<Model> models = new ArrayList<>();
		Map<String, String> properties = new HashMap<>();
		String plainPath = extractProperties(path, properties);
		final Enumeration<URL> urls;
		if (plainPath != null && plainPath.endsWith(".ecore")) {
			URL url = bundle.getEntry(plainPath);
			if (url == null) {
				urls = bundle.findEntries(plainPath, "*.ecore", false);
			} else {
				urls = Collections.enumeration(Collections.singleton(url));
			}
		} else {
			urls = bundle.findEntries(plainPath, "*.ecore", false);
		}
		readModelInstance(bundle, resourceSet, path, diagnostic, models, properties, plainPath, urls);
		return models;
	}

	/**
	 * @param bundle
	 * @param resourceSet
	 * @param path
	 * @param diagnostic
	 * @param models
	 * @param properties
	 * @param plainPath
	 * @param urls
	 */
	private static void readModelInstance(final Bundle bundle, final ResourceSet resourceSet, final String path,
			final Diagnostic diagnostic, final List<Model> models, Map<String, String> properties, String plainPath,
			final Enumeration<URL> urls) {
		if ( nonNull(urls) ) {
			while ( urls.hasMoreElements() ) {
				final URL url = urls.nextElement();
				try {
					final Model model = loadModelInstance(bundle.getBundleId(), resourceSet, url, properties, diagnostic);
					if ( nonNull(model) ) {
						models.add(model);
					}
				} catch ( final IOException ioe ) {
					diagnostic.errors.add("Unable to load ecore " + plainPath + " : " + ioe.getMessage());
				}
			}
		} else {
			diagnostic.errors.add("No ecore models found at path " + path);
		}
	}

	/**
	 * Extracts properties from the path String. It expects the first segment to be the path itself,
	 * followed by a semicolon separate list of key value pairs that are connect via '='
	 * e.g. /testpath;mykey:String=myval return '/testpath' and in the properties map Entry.key = mykey:String, Entry.value = myval 
	 * @param path
	 * @param properties
	 * @return
	 */
	public static String extractProperties(String path, Map<String, String> properties) {
		if (isNull(path) || path.isEmpty()) {
			return path;
		}
		String[] parts = path.split(";");
		if (parts.length > 1) {
			for (int i = 1; i < parts.length; i++) {
				String[] kvArray = parts[i].split("=");
				if (kvArray.length > 0 && properties != null) {
					String value = kvArray.length > 1 ? kvArray[1] : null;
					properties.put(kvArray[0], value);
				}
			}
			return parts[0];
			
		} else {
			return parts.length == 1 ? parts[0] : path;
		}
	}

	/**
	 * @param bundleId The bundle id
	 * @param resourceSet The configured {@link ResourceSet}
	 * @param path the ecore bundle file path
	 * @param properties the path properties
	 * @param url the file url
	 * @param diagnostic the error report
	 * @return the loaded model
	 * @throws IOException
	 */
	public static Model loadModelInstance(final long bundleId,
			final ResourceSet resourceSet,
			final URL url,
			final Map<String, String> properties,
			final Diagnostic diagnostic) throws IOException{
		Resource r = resourceSet.createResource(URI.createURI(url.toExternalForm())); 
		try {
			r.load(url.openStream(), null);
			if (!r.getContents().isEmpty()) {
				EPackage ePackage = (EPackage) r.getContents().get(0);
				Dictionary<String, Object> serviceProperties = new Hashtable<>();
				if (properties != null) {
					properties.forEach(serviceProperties::put);
				}
				String nsUri = ePackage.getNsURI();
				serviceProperties.put(EMFNamespaces.EMF_MODEL_NAME, ePackage.getName());
				serviceProperties.put(EMFNamespaces.EMF_MODEL_NSURI, nsUri);
				serviceProperties.put(EMFNamespaces.EMF_MODEL_REGISTRATION, EMFNamespaces.MODEL_REGISTRATION_EXTENDER);
				return new Model(ePackage, url, serviceProperties, bundleId);
			}
			return null;
		} finally {
			for (org.eclipse.emf.ecore.resource.Resource.Diagnostic de : r.getErrors()) {
				diagnostic.errors.add(de.getMessage() + " [" + de.getLine() + ":" + de.getColumn() + "]");
			}
			for (org.eclipse.emf.ecore.resource.Resource.Diagnostic dw : r.getWarnings()) {
				diagnostic.errors.add(dw.getMessage() + " [" + dw.getLine() + ":" + dw.getColumn() + "]");
			}
		}

	}
	
    /**
     * Check if the bundle contains EMF models depending on the OSGi requirements
     * @param bundle The bundle
     * @param modelBundleId The bundle id of the model bundle to check the wiring
     * @return Set of locations or {@code null}
     */
    public static Set<String> isModelBundle(final Bundle bundle, final long modelBundleId) {
        // check for bundle wiring
        final BundleWiring bundleWiring = bundle.adapt(BundleWiring.class);
        if ( isNull(bundleWiring) ) {
            return Collections.emptySet();
        }

        // check for bundle requirement to extender namespace
        final List<BundleRequirement> requirements = bundleWiring.getRequirements(ExtenderNamespace.EXTENDER_NAMESPACE);
        if ( isNull(requirements) || requirements.isEmpty() ) {
            return Collections.emptySet();
        }
        // get all wires for the implementation namespace
        final List<BundleWire> wires = bundleWiring.getRequiredWires(ExtenderNamespace.EXTENDER_NAMESPACE);
        for(final BundleWire wire : wires) {
            // if the wire is to this bundle (EMF extender), it must be the correct
            // requirement (no need to do additional checks like version etc.)
            if ( nonNull(wire.getProviderWiring())
                 && wire.getProviderWiring().getBundle().getBundleId() == modelBundleId ) {
                return extractModelPath(wire);
            }
        }
        return Collections.emptySet();
    }

	/**
	 * Etracts the path attribute from a {@link Bundle} 
	 * @param wire the {@link BundleWire}
	 * @return the {@link Set} of path, extracted from the {@link Requirement}
	 */
	@SuppressWarnings("unchecked")
	private static Set<String> extractModelPath(final BundleWire wire) {
		requireNonNull(wire);
		final Object val = wire.getRequirement().getAttributes().get(EMF_MODEL_EXTENDER_PROP_MODELS_NAME);
		if ( nonNull(val) ) {
		    if ( val instanceof String ) {
		        return Collections.singleton((String)val);
		    }
		    if ( val instanceof List ) {
		        final List<String> paths = (List<String>)val;
		        final Set<String> result = new HashSet<>();
		        for(final String p : paths) {
		            result.add(p);
		        }
		        return result;
		    }
		    logger.severe(()->"Attribute " + EMF_MODEL_EXTENDER_PROP_MODELS_NAME + " for EMF models requirement has an invalid type: " + val +
		                       ". Using default model path.");
		}
		return Collections.singleton(EMF_MODEL_EXTENDER_DEFAULT_PATH);
	}
}