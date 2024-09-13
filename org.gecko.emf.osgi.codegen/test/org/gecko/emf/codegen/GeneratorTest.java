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
package org.gecko.emf.codegen;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.File;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.gecko.emf.osgi.codegen.GeckoEmfGenerator;
import org.gecko.emf.osgi.codegen.GeckoEmfGenerator.GeneratorOptions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInfo;

import aQute.bnd.build.Project;
import aQute.bnd.build.Workspace;
import aQute.bnd.service.generate.BuildContext;
import aQute.lib.io.IO;


/**
 * 
 * @author Jürgen Albert
 * @since 14 Jan 2021
 */
class GeneratorTest {
	
	File tmp;
	
	@BeforeEach
	public void beforeEach(TestInfo testInfo) {
		tmp = new File("generated/test/" + testInfo.getDisplayName());
		tmp.delete();
		tmp.mkdirs();
	}
	
	@Test
	void testGenerator() throws Exception {
		try (Workspace workspace = getWorkspace("test-resources/ws-1")) {
			Project project = workspace.getProject("org.w3.rdf.model");
			assertThat(project).isNotNull();
			project.verifyDependencies(false);
			assertThat(project.getErrors()).isEmpty();
			Map<String, String> attrs = Map.of(
					"generate", "geckoEMF",
					"genmodel", "model/rdf.genmodel",
					"output", "src");
			BuildContext bc = new BuildContext(project, attrs, List.of(), System.in, System.out, System.err);
			GeckoEmfGenerator generator = new GeckoEmfGenerator();
			generator.generate(bc, new GeneratorOptions() {
				
				@Override
				public Map<String, String> _properties() {
					// TODO Auto-generated method stub
					return null;
				}
				
				@Override
				public List<String> _arguments() {
					// TODO Auto-generated method stub
					return null;
				}
				
				@Override
				public Optional<File> output() {
					// TODO Auto-generated method stub
					return Optional.empty();
				}
			});
		};
	}
	
	private Workspace getWorkspace(File file) throws Exception {
		IO.copy(file, tmp);
		return new Workspace(tmp);
	}

	private Workspace getWorkspace(String dir) throws Exception {
		return getWorkspace(new File(dir));
	}
}
