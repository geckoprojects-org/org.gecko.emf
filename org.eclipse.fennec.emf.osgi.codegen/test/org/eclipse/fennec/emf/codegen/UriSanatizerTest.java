/**
 * Copyright (c) 2012 - 2023 Data In Motion and others.
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
package org.eclipse.fennec.emf.codegen;

import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.assertj.core.api.Assertions;
import org.eclipse.emf.common.util.URI;
import org.eclipse.fennec.emf.osgi.codegen.UriSanatizer;
import org.eclipse.fennec.emf.osgi.codegen.templates.model.helper.GeneratorHelper;
import org.junit.jupiter.api.Test;


/**
 * 
 * @author Jürgen Albert
 * @since 14 Jan 2021
 */
class UriSanatizerTest {
	
	@Test
	void testURIHandlerDefault() {
		URI toTest = URI.createURI("resource://org.eclipse.fennec.emf.osgi.codegen/../org.eclipse.emf.ecore/model/Ecore.genmodel");
		System.out.println(new File("").getAbsolutePath());
		URI basePath = URI.createFileURI(new File("").getAbsolutePath());
		System.out.println(basePath.lastSegment());
		URI result = UriSanatizer.trimmedSanitize(toTest);
		Assertions.assertThat(result).isSameAs(URI.createURI("resource://org.eclipse.emf.ecore/model/Ecore.genmodel"));
	}

	@Test
	void test() {
		URI genModelURI = URI.createURI("resource://metadata/src/main/resources/model/metadata.genmodel");
		System.out.println(genModelURI.toString());
		System.out.println(genModelURI.segmentCount());
		System.out.println(genModelURI.trimSegments(genModelURI.segmentCount() - 3).toString());
		System.out.println(genModelURI.deresolve(genModelURI.trimSegments(genModelURI.segmentCount() - 3).appendSegment("")).toString());
		
		genModelURI = URI.createURI("resource://org.eclipse.fennec.emf.osgi.example.model.basic/model/basic.genmodel");
		System.out.println(genModelURI.toString());
		System.out.println(genModelURI.segmentCount());
		System.out.println(genModelURI.trimSegments(genModelURI.segmentCount() - 3).toString());
		System.out.println(genModelURI.deresolve(genModelURI.trimSegments(genModelURI.segmentCount() - 3).appendSegment("")).toString());
	}
//	@Test
	void testURIResolve() {
		URI toTest = URI.createURI("../org.eclipse.emf.ecore/model/Ecore.genmodel");
		URI basePath = URI.createURI("resource://org.eclipse.fennec.emf.osgi.codegen");
		System.out.println(toTest);
		System.out.println(basePath);
		URI result = toTest.deresolve(basePath);
		System.out.println(result);
		Assertions.assertThat(result).isSameAs(URI.createURI("resource://org.eclipse.emf.ecore/model/Ecore.genmodel"));
	}

	@Test
	void testConvertToBundleEcoreURI() {
		URI testResult = URI.createURI("/ecore/model/test.ecore");
		
		URI genModelPath = URI.createURI("/genmodel/model/test.genmodel");
		URI ePackageUri = URI.createURI("resource://org.eclipse.fennec.emf.osgi.example.model.extended/src/main/resources/ecore/model/test.ecore");
    	URI genModelUri = URI.createURI("resource://org.eclipse.fennec.emf.osgi.example.model.extended/src/main/resources/genmodel/model/test.genmodel");
    	URI finalEcore = GeneratorHelper.convertToBundleEcoreURI(genModelPath, ePackageUri, genModelUri);
    	Assertions.assertThat(finalEcore).isSameAs(testResult);
	}

	@Test
	void testConvertToSourceEcoreURI() {
		URI testResult = URI.createURI("/src/main/resources/ecore/model/test.ecore");
		
		URI ePackageUri = URI.createURI("resource://org.eclipse.fennec.emf.osgi.example.model.extended/src/main/resources/ecore/model/test.ecore");
		URI genModelUri = URI.createURI("resource://org.eclipse.fennec.emf.osgi.example.model.extended/src/main/resources/genmodel/model/test.genmodel");
		URI finalEcore = ePackageUri.deresolve(genModelUri, true, false, false);
		Assertions.assertThat(finalEcore).isSameAs(testResult);
	}
	
	/**
	 * Test method for {@link org.eclipse.fennec.emf.osgi.codegen.GeckoEmfGenerator#doGenerate(java.io.File, java.lang.String, java.io.File)}.
	 * @throws IOException 
	 */
	@Test
	void testDoGenerate() throws IOException {

		URI toSanatize = URI.createURI("resource://org.eclipse.fennec.emf.osgi.codegen.test/../org.eclipse.emf.ecore/model/Ecore.genmodel");
		
		String uri = "";
		for(int i = toSanatize.segmentCount() -1; i >= 0;  i--) {
			String segment = toSanatize.segment(i);
			if("..".equals(segment)) {
				i--;
			} else {
				if(toSanatize.segmentCount() - 1 == i) {
					uri = segment;
				} else {
					uri = segment + "/" + uri;
				}
			}
			if(i <= 0 ) {
				String host = toSanatize.host();
				if("..".equals(segment)) {
					System.out.println(toSanatize.scheme() + "://" +uri);
				} else {
					System.out.println(toSanatize.scheme() + "://" + host + "/"+uri);
				}
			}
		};
		List<String> uris = Arrays.asList(toSanatize.segments());
		String segments = uris
				.stream()
				.filter(s->!s.equals(".."))
				.collect(Collectors.joining("/"));
		boolean noHost = uris.stream().filter(s->s.equals("..")).findFirst().isPresent();
		StringBuilder sb = new StringBuilder();
		sb.append(toSanatize.scheme());
		sb.append("://");
		if (!noHost) {
			sb.append(toSanatize.host());
			sb.append("/");
		}
		sb.append(segments);
		System.out.println(sb.toString());
		assertNotNull(uri);
	}
}
