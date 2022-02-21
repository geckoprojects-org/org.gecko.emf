/**
 * Copyright (c) 2012 - 2022 Data In Motion and others.
 * All rights reserved. 
 * 
 * This program and the accompanying materials are made available under the terms of the 
 * Eclipse Public License v2.0 which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v20.html
 * 
 * Contributors:
 *      Data In Motion - initial API and implementation
 */
package org.gecko.emf.codegen;

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.eclipse.emf.common.util.URI;
import org.gecko.emf.osgi.codegen.GeckoEmfGenerator;
import org.junit.jupiter.api.Test;


/**
 * 
 * @author ungei
 * @since 14 Jan 2021
 */
public class GeckoEmfGeneratorTest {

	/**
	 * Test method for {@link org.gecko.emf.osgi.codegen.GeckoEmfGenerator#doGenerate(java.io.File, java.lang.String, java.io.File)}.
	 * @throws IOException 
	 */
	@Test
	public void testDoGenerate() throws IOException {

		URI toSanatize = URI.createURI("resource://org.gecko.emf.osgi.codegen.test/../org.eclipse.emf.ecore/model/Ecore.genmodel");
//		URI toResolve = URI.createURI("resource://org.gecko.emf.osgi.codegen.test/model/test.genmodel");
//		URI theOther = URI.createURI("resource://org.gecko.emf.osgi.codegen.test/");
		
//		System.out.println(toResolve.deresolve(theOther));
//		System.out.println(toResolve.resolve(toResolve));
//		System.out.println(theOther.deresolve(toResolve));
		System.out.println(toSanatize);
		
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
		
//		String genmodelPath = "testdata/model/test.genmodel";
//		
//		File genmodel = new File(genmodelPath);
//		
//		assertTrue(genmodel.exists());
//		
//		GeckoEmfGenerator geckoEmfGenerator = new GeckoEmfGenerator();
//		File out = new File("out");
//		out.mkdir();
//		
//		Optional<String> result = geckoEmfGenerator.doGenerate(out, genmodelPath, genmodel);
//		assertTrue(result.isEmpty());
	}

}
