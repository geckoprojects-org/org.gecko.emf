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
package org.eclipse.fennec.emf.osgi.codegen;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Collections;
import java.util.Map;
import java.util.Map.Entry;
import java.util.StringJoiner;
import java.util.jar.Manifest;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.resource.ContentHandler;
import org.eclipse.emf.ecore.resource.URIHandler;

import aQute.bnd.build.Container;
import aQute.bnd.exceptions.Exceptions;
import aQute.bnd.osgi.Constants;
import aQute.bnd.osgi.Jar;
import aQute.bnd.osgi.Resource;
import aQute.lib.io.IO;

/**
 * An URI Handler to translate between EMF and BND Dependencies
 * @author Juergen Albert
 * @since 18 Jan 2021
 */
public class ResourceUriHandler implements URIHandler {

	
	private final Map<Container, Map<String, String>> buildPathModels;
	private String bsn;
	private File base;
	private URI basePath;
	private String projectDirName;
	
	
	/**
	 * Creates a new instance.
	 */
	public ResourceUriHandler(Map<Container, Map<String, String>> refModels, String bsn, File base) {
		this.buildPathModels = refModels;
		this.bsn = bsn;
		this.base = base;
		URI fileURI = URI.createFileURI(base.getAbsolutePath());
		this.projectDirName = fileURI.lastSegment();
		basePath = bsn.equals(projectDirName) ? null: basePath;
		
		GeckoEmfGenerator.info("Setting up ResourceUriHandler:");
		GeckoEmfGenerator.info("    bsn: " + bsn);
		GeckoEmfGenerator.info("    base: " + base.toString());
		GeckoEmfGenerator.info("    projectDirName: " + projectDirName);
		GeckoEmfGenerator.info("    basePath: " + basePath);
		GeckoEmfGenerator.info("    buildPath:");
		refModels.forEach((c, r) -> {
			GeckoEmfGenerator.info("      Container: " + c);
			r.forEach((k,v) -> GeckoEmfGenerator.info("        |->: " + k + " - " + v));
		});
		
	}
	
	@Override
	public boolean canHandle(URI uri) {
		GeckoEmfGenerator.info("Asked to handle " + uri); //$NON-NLS-1$
		return uri.scheme().equals(UriSanatizer.RESOURCE_SCHEMA_NAME) || uri.toString().startsWith(UriSanatizer.PLATFORM_RESOURCE) || uri.toString().startsWith(UriSanatizer.PLATFORM_PLUGIN); 
	}

	
	
	@Override
	public InputStream createInputStream(URI uri, Map<?, ?> options) throws IOException {
		GeckoEmfGenerator.info("Asked to open InputStream for " + uri); //$NON-NLS-1$
		
		URI theUri = UriSanatizer.trimmedSanitize(uri);
		if (theUri == null) {
			GeckoEmfGenerator.error("URI is null, InputStream cannot be created"); //$NON-NLS-1$
			return null;
		}
		GeckoEmfGenerator.info("sanatized uri " + theUri); //$NON-NLS-1$
		String uriBSN = theUri.host();
		GeckoEmfGenerator.info("bsn according to URI " + uriBSN); //$NON-NLS-1$
		if(bsn.equals(uriBSN)) {
			GeckoEmfGenerator.info("The bsn segment part fits to: " + bsn); //$NON-NLS-1$
			StringJoiner joiner = new StringJoiner(UriSanatizer.SLASH);
			uri.segmentsList().stream().forEach(joiner::add);
			return IO.stream( new File(base, joiner.toString() ));
		} 
		
		for (Entry<Container, Map<String, String>> entry : buildPathModels.entrySet()) {
			Container c = entry.getKey();
			String containerBSN = getBSN(c);
			GeckoEmfGenerator.info("Comparing " + uriBSN + " with container using getBSN() " + containerBSN);  //$NON-NLS-1$//$NON-NLS-2$
			if(containerBSN != null && containerBSN.equals(uriBSN)) {
				GeckoEmfGenerator.info("Match in " + c); //$NON-NLS-1$
				for(Map.Entry<String, String> paths : entry.getValue().entrySet()) {
					String path = paths.getKey();
					if(path.startsWith(UriSanatizer.SLASH)) {
						path = path.substring(1);
					}
					String testUri = UriSanatizer.SCHEMA_RESOURCE + containerBSN + UriSanatizer.SLASH + path;
					GeckoEmfGenerator.info("comparing URIs " + testUri + " with the requested " + theUri); //$NON-NLS-1$ //$NON-NLS-2$
					if(testUri.equals(theUri.toString())) {
						try(Jar jar = new Jar(c.getFile())){
							Resource resource = jar.getResource(paths.getValue());
							if(resource != null) {
								try {
									byte[] data = IO.read(resource.openInputStream());
									return new ByteArrayInputStream(data);
								} catch (Exception e) {
									Exceptions.duck(e);
								}
							}
						}
					}
				}
			}
		}

		GeckoEmfGenerator.info("Nothing worked. Trying a relative path as a last ditch effort. ");  //$NON-NLS-1$//$NON-NLS-2$

		URI projectUri = URI.createURI(uri.scheme() + "://"+ projectDirName + "/");
		URI relative = uri.deresolve(projectUri);
		GeckoEmfGenerator.info("Deresolving " + projectUri + " against " + uri + " result: " + relative);  //$NON-NLS-1$//$NON-NLS-2$
		if(relative.isRelative()) {
			File file = new File(base, relative.toString());
			if(file.exists()) {
				GeckoEmfGenerator.info("Found it in a relative Path. We hope that there is a corresponding project in your buildpath.");  //$NON-NLS-1$//$NON-NLS-2$
				return new FileInputStream(file);
			} else {
				GeckoEmfGenerator.info("The File does not seem to exist: " + file.getAbsolutePath());  //$NON-NLS-1$//$NON-NLS-2$
			}
		} else {
			GeckoEmfGenerator.info("This URI does not seem to be relative: " + relative);  //$NON-NLS-1$//$NON-NLS-2$
		}
		GeckoEmfGenerator.error("Nothing Helped. We have been unable to find anything for " + uri);  //$NON-NLS-1$//$NON-NLS-2$
		
		return null;
	}

	private String getBSN(Container c) {
		try {
			Manifest manifest = null;
			if(c.getFile().getName().endsWith(".jar")) {
				manifest = c.getManifest();
			} else {
				//maybe we look at the generated class folder with a Manifest
				File manifestFile = new File(c.getFile(), "META-INF/MANIFEST.MF");
				if(manifestFile.exists()) {
					manifest = new Manifest(new FileInputStream(manifestFile));
				} 
			}
				
			String symbolicName = null;
			if(manifest != null) {
				symbolicName = manifest.getMainAttributes().getValue(Constants.BUNDLE_SYMBOLICNAME);
				if(symbolicName != null && symbolicName.contains(";")) { //$NON-NLS-1$
					symbolicName = symbolicName.substring(0, symbolicName.indexOf(';'));
				}
				return symbolicName;
			}
			return c.getBundleSymbolicName();
		} catch (Exception e) {
			GeckoEmfGenerator.info(String.format("Could not parse BSN from %s. Error was %s. Returning %s", c, e.getMessage(), c.getBundleSymbolicName())); //$NON-NLS-1$
			return c.getBundleSymbolicName();
		}
	}

	/* 
	 * (non-Javadoc)
	 * @see org.eclipse.emf.ecore.resource.URIHandler#createOutputStream(org.eclipse.emf.common.util.URI, java.util.Map)
	 */
	@Override
	public OutputStream createOutputStream(URI uri, Map<?, ?> options) throws IOException {
		GeckoEmfGenerator.info("Asked to open OutputStream for " + uri); //$NON-NLS-1$
		URI theUri = UriSanatizer.trimmedSanitize(uri);
		if (theUri == null) {
			GeckoEmfGenerator.error("URI is null, OutputStream cannot be created"); //$NON-NLS-1$
			return null;
		}
		GeckoEmfGenerator.info("Sanatized " + theUri); //$NON-NLS-1$
		String uriBSN = theUri.host();
		if(bsn.equals(uriBSN)) {
			StringJoiner joiner = new StringJoiner(UriSanatizer.SLASH);
			theUri.segmentsList().stream().forEach(joiner::add);
			File theFile = new File(base, joiner.toString());
			theFile.getParentFile().mkdirs();
			GeckoEmfGenerator.info("Opening file " + theFile); //$NON-NLS-1$
			return new FileOutputStream(theFile);
		} 
		return null;
	}

	/* 
	 * (non-Javadoc)
	 * @see org.eclipse.emf.ecore.resource.URIHandler#delete(org.eclipse.emf.common.util.URI, java.util.Map)
	 */
	@Override
	public void delete(URI uri, Map<?, ?> options) throws IOException {
//		GeckoEmfGenerator.info("Asked to delete " + uri); //$NON-NLS-1$
		URI theUri = UriSanatizer.trimmedSanitize(uri);
		if (theUri == null) {
			GeckoEmfGenerator.error("URI is null, Delete cannot be executed"); //$NON-NLS-1$
			return;
		}
//		GeckoEmfGenerator.info("Sanatized " + theUri); //$NON-NLS-1$
		String uriBSN = theUri.segment(0);
		if(bsn.equals(uriBSN)) {
			StringJoiner joiner = new StringJoiner(UriSanatizer.SLASH);
			uri.segmentsList().stream().skip(1).forEach(joiner::add);
			IO.delete(new File(base, joiner.toString() + uri.fileExtension()));
		} 
	}

	/* 
	 * (non-Javadoc)
	 * @see org.eclipse.emf.ecore.resource.URIHandler#contentDescription(org.eclipse.emf.common.util.URI, java.util.Map)
	 */
	@Override
	public Map<String, ?> contentDescription(URI uri, Map<?, ?> options) throws IOException {
//		GeckoEmfGenerator.info("Asked for content Descriptor " + uri); //$NON-NLS-1$
		return Collections.singletonMap(ContentHandler.CONTENT_TYPE_PROPERTY, UriSanatizer.APPLICATION_XMI);
	}

	/* 
	 * (non-Javadoc)
	 * @see org.eclipse.emf.ecore.resource.URIHandler#exists(org.eclipse.emf.common.util.URI, java.util.Map)
	 */
	@Override
	public boolean exists(URI uri, Map<?, ?> options) {
		GeckoEmfGenerator.info("Asked if exists " + uri); //$NON-NLS-1$
		URI theUri = UriSanatizer.trimmedSanitize(uri);
		if (theUri == null) {
			GeckoEmfGenerator.error("URI is null, existence cannot be checked"); //$NON-NLS-1$
			return false;
		}
//		GeckoEmfGenerator.info("Sanatized " + uri); //$NON-NLS-1$
		String uriBSN = theUri.segment(0);
		if(bsn.equals(uriBSN)) {
			StringJoiner joiner = new StringJoiner(UriSanatizer.SLASH);
			uri.segmentsList().stream().skip(1).forEach(joiner::add);
			return new File(base, joiner.toString() + uri.fileExtension()).exists();
		}
		

		URI projectUri = URI.createURI(uri.scheme() + "://"+ projectDirName + "/");
		URI relative = uri.deresolve(projectUri);
		if(relative.isRelative()) {
			File file = new File(base, relative.toString());
			return file.exists();
		}
		
		return false;
	}

	/* 
	 * (non-Javadoc)
	 * @see org.eclipse.emf.ecore.resource.URIHandler#getAttributes(org.eclipse.emf.common.util.URI, java.util.Map)
	 */
	@Override
	public Map<String, ?> getAttributes(URI uri, Map<?, ?> options) {
		return Collections.emptyMap();
	}


	/* 
	 * (non-Javadoc)
	 * @see org.eclipse.emf.ecore.resource.URIHandler#setAttributes(org.eclipse.emf.common.util.URI, java.util.Map, java.util.Map)
	 */
	@Override
	public void setAttributes(URI uri, Map<String, ?> attributes, Map<?, ?> options) throws IOException {
		// Nothing to set here
	}

}
