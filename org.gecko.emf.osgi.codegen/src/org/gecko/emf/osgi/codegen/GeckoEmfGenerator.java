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
package org.gecko.emf.osgi.codegen;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicBoolean;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.emf.codegen.ecore.genmodel.GenModel;
import org.eclipse.emf.codegen.ecore.genmodel.GenModelPackage;
import org.eclipse.emf.codegen.ecore.genmodel.generator.GenBaseGeneratorAdapter;
import org.eclipse.emf.codegen.ecore.genmodel.impl.GenModelFactoryImpl;
import org.eclipse.emf.codegen.ecore.genmodel.impl.GenModelImpl;
import org.eclipse.emf.codegen.ecore.genmodel.impl.GenModelPackageImpl;
import org.eclipse.emf.codegen.util.CodeGenUtil;
import org.eclipse.emf.common.util.Diagnostic;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.eclipse.emf.ecore.xmi.impl.XMIResourceFactoryImpl;
import org.gecko.emf.osgi.annotation.provide.EPackage;
import org.gecko.emf.osgi.codegen.GeckoEmfGenerator.GeneratorOptions;
import org.gecko.emf.osgi.codegen.adapter.BNDGeneratorAdapterFactory;
import org.gecko.emf.osgi.constants.VersionConstant;
import org.osgi.resource.Capability;

import aQute.bnd.build.Container;
import aQute.bnd.build.Project;
import aQute.bnd.header.Attrs;
import aQute.bnd.osgi.Domain;
import aQute.bnd.osgi.Jar;
import aQute.bnd.osgi.resource.CapabilityBuilder;
import aQute.bnd.service.externalplugin.ExternalPlugin;
import aQute.bnd.service.generate.BuildContext;
import aQute.bnd.service.generate.Generator;
import aQute.bnd.service.generate.Options;
import aQute.lib.io.IO;

@ExternalPlugin(name = "geckoEMF", objectClass = Generator.class, version = VersionConstant.GECKOPROJECTS_EMF_VERSION)
public class GeckoEmfGenerator implements Generator<GeneratorOptions> {

	public static final String ORIGINAL_GEN_MODEL_PATH = "originalGenModelPath";
	public static final String ORIGINAL_GEN_MODEL_PATHS_EXTRA = "originalGenModelPathsExtra";
	public static final String INCLUDE_GEN_MODEL_FOLDER = "includeGenModelFolder";
	
	/** The output folders default */
	private static final String OUTPUT_DEFAULT = "src-gen"; //$NON-NLS-1$
	/** which genmodel to generate */
	private static final String PROP_GENMODEL = "genmodel"; //$NON-NLS-1$
	/** where the genmodel will end up in the build model, should be used when not in the defaults model folder */
	private static final String PROP_GENMODEL_INLCLUDE_LOCATION = "genmodelIncludeLocation"; //$NON-NLS-1$
	/** PROP_OUTPUT */
	private static final String PROP_OUTPUT = "output"; //$NON-NLS-1$
	/** PROP_LOGFILE */
	private static final String PROP_LOGFILE = "logfile"; //$NON-NLS-1$

	private static PrintStream logWriter;

	public static void info(String message) {
		if(logWriter != null) {
			logWriter.println("[INFO] " + message); //$NON-NLS-1$
			logWriter.flush();
		} else {
			System.out.println("[INFO] " + message); //$NON-NLS-1$
		}
	}

	public static void error(String message) {
		if(logWriter != null) {
			logWriter.println("[ERROR] " + message); //$NON-NLS-1$
			logWriter.flush();
		} else {
			System.err.println("[ERROR] " + message); //$NON-NLS-1$
		}
	}

	public static void error(String message, Throwable t) {
		error(message);
		if (logWriter != null) {
			t.printStackTrace(logWriter);
			logWriter.flush();
		} else {
			t.printStackTrace(System.err);
		}
	}

	private static void initializeLog(File base, String file) throws IOException {
		File logFile = new File(base, file);
		IO.delete(logFile);
		if (logFile.createNewFile()) {
			logWriter = new PrintStream(logFile);
		}
	}

	// We don't really use it.
	public interface GeneratorOptions extends Options {
		Optional<File> output();
	}

	public Optional<String> generate(
			BuildContext context, 
			GeneratorOptions options) throws Exception {
		try {
			if(context.get(PROP_LOGFILE) != null) {
				initializeLog(context.getBase(), context.get(PROP_LOGFILE));
			}
			
			info("Running Gecko EMF Codegen Version " + VersionConstant.GECKOPROJECTS_EMF_VERSION);
			
			String genFolder = context.get(PROP_OUTPUT);
			File output = null;
			if(genFolder != null) {
				output = context.getFile(genFolder);
			} else {
				output = context.getFile(OUTPUT_DEFAULT);
			}
			info("Output configured: " + genFolder);
			info("Output result: " + output);
			output.mkdirs();

			String genmodel = context.get(PROP_GENMODEL); 
			if(genmodel == null) {
				return Optional.of("genmodel attribute not set");
			}

			String genmodelLocation = context.get(PROP_GENMODEL_INLCLUDE_LOCATION); 
			if(genmodelLocation == null) {
				info("genmodelLocation: null");
			} else {
				info("genmodelLocation: [" + genmodelLocation + "]");
			}

			
			File genmodelFile = new File(context.getBase(), genmodel);

			if(!genmodelFile.exists()) {
				return Optional.of("No genmodel found at " + genmodelFile.getPath());
			}

			Map<Container, Map<String, String>> refModels = extractedLocationsWithCap(context.getProject().getBuildpath());
			Project project = (Project) context.getParent();
			Iterator<String> iterator = project.getBsns().iterator();
			String bsn = iterator.hasNext() ? iterator.next() : context.getParent()
					.toString();
			return doGenerate(genFolder, genmodel, 
					refModels, 
					context.getBase(), 
					bsn, genmodelLocation);
		} catch (Exception e) {
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			PrintWriter print = new PrintWriter(baos);
			print.println("Something went wrong: " + e.getMessage());
			e.printStackTrace(print);
			print.close();
			String error = new String(baos.toByteArray());
			baos.close();
			error(error);
			return Optional.of(error);
		} finally {
			closeLog();
		}
	}

	/**
	 * org.eclipse.emf.ecore.generated_package;class="org.gecko.emf.osgi.example.model.basic.BasicPackage";uri="http://gecko.org/example/model/basic";genModel="/model/basic.genmodel";sourceLocations="other/main/resources/model/basic.genmodel,org.gecko.emf.osgi.example.model.basic/other/main/resources/model/basic.genmodel"
	 * @param context
	 * @return
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	private Map<Container, Map<String,String>> extractedLocationsWithCap(Collection<Container> buildpath)
			throws Exception {
		Map<Container, Map<String, String>> refModels = new HashMap<>(); 
		for(Container c : buildpath) {
			File f = c.getFile();
			Attrs attrs = null;
			if(!f.isDirectory()) {
				Domain domain = Domain.domain(c.getManifest());
				attrs = domain.getProvideCapability().get(EPackage.NAMESPACE);
			}
			if(attrs != null) {
				Map<String, String> result = new HashMap<>();
				CapabilityBuilder builder = new CapabilityBuilder(EPackage.NAMESPACE);
				builder.addAttributesOrDirectives(attrs);
				Capability capability = builder.synthetic();
				String genModelLocation = (String) capability.getAttributes().get("genModel");
				List<String> sourceLocations = (List<String>) capability.getAttributes().get("genModelSourceLocations");
				if(sourceLocations != null) {
					sourceLocations.forEach(l -> result.put(l, genModelLocation));
				}
				result.put(genModelLocation, genModelLocation);
				String ecoreLocation = (String) capability.getAttributes().get("ecore");
				AtomicBoolean checkForEcore = new AtomicBoolean(true);
				if(ecoreLocation != null) {
					checkForEcore.set(false);
					List<String> ecoreSourceLocations = (List<String>) capability.getAttributes().get("ecoreSourceLocations");
					if(ecoreSourceLocations != null) {
						ecoreSourceLocations.forEach(l -> result.put(l, ecoreLocation));
					}
					result.put(ecoreLocation, ecoreLocation);
					String uri = (String) capability.getAttributes().get("uri");
					if(uri != null) {
						result.put(uri, ecoreLocation);
					}
				} 
				refModels.put(c, result);
				try (Jar jar = new Jar(f)){
					jar.getResourceNames(s -> (checkForEcore.get() && s.endsWith(".ecore")) || s.endsWith(".genmodel") ||  s.endsWith(".uml"))
						.forEach(s -> result.put(s, s));
					refModels.put(c, result);
				}
			} else {
				try (Jar jar = new Jar(f)){
					Map<String, String> result = new HashMap<>();
					jar.getResourceNames(s -> s.endsWith(".ecore") || s.endsWith(".genmodel") || s.endsWith(".uml"))
							.forEach(s -> result.put(s, s));
					refModels.put(c, result);
				}
			}
			
		}
		return refModels;
	}

	/**
	 * 
	 */
	private static void closeLog() {
		if(logWriter != null) {
			logWriter.close();
			logWriter = null;
		}
	}

	private void configureEMFGenerator(org.eclipse.emf.codegen.ecore.generator.Generator gen) {
		info("Configuring Jet");
		gen.getAdapterFactoryDescriptorRegistry().addDescriptor(GenModelPackage.eNS_URI, BNDGeneratorAdapterFactory.DESCRIPTOR);
	}

	private void configureEMF(ResourceSet resourceSet, Map<Container, Map<String, String>> refModels, String bsn, File base) {

		GenModelPackageImpl.init();
		GenModelFactoryImpl.init();

		resourceSet.getURIConverter().getURIHandlers().add(0, new ResourceUriHandler(refModels, bsn, base));
		resourceSet.getResourceFactoryRegistry().getContentTypeToFactoryMap().put(GenModelPackage.eCONTENT_TYPE, new XMIResourceFactoryImpl());
		resourceSet.getResourceFactoryRegistry().getContentTypeToFactoryMap().put("application/xmi", new XMIResourceFactoryImpl());
	}

	/**
	 * @param output
	 * @param genmodelPath
	 * @param genmodelFile
	 * @param refModels 
	 * @param genmodelLocation 
	 * @param string 
	 * @param file 
	 * @return
	 * @throws IOException
	 */
	protected Optional<String> doGenerate(String output, String genmodelPath, Map<Container, Map<String, String>> refModels, File base, String bsn, String genmodelLocation) {
		info("Running for genmodel " + genmodelPath + " in " + base.getAbsolutePath()); 
		ResourceSet resourceSet = new ResourceSetImpl();
		try {
			configureEMF(resourceSet, refModels, bsn, base);
			URI genModelUri = URI.createURI("resource://" + bsn + "/" + genmodelPath);
			
			info("Loading " + genModelUri.toString());
			
			Resource resource = resourceSet.getResource(genModelUri, true);
			
			if(!resource.getErrors().isEmpty()) {
				return Optional.of(resource.getErrors().get(0).toString());
			}
			
			GenModel genModel = (GenModel) resource.getContents().get(0);
			org.eclipse.emf.codegen.ecore.generator.Generator gen = new org.eclipse.emf.codegen.ecore.generator.Generator();
			configureEMFGenerator(gen);
			
			String modelDirectory = "/" + bsn + (output.startsWith("/") ? "" : "/") + output;
			
			info("Setting modelDirectory" + modelDirectory);
			
			genModel.setModelDirectory(modelDirectory);
			gen.setInput(genModel);
			
			
			Map<String, Object> props = new HashMap<>();
			props.put(GeckoEmfGenerator.ORIGINAL_GEN_MODEL_PATH, genmodelPath);
			props.put(GeckoEmfGenerator.ORIGINAL_GEN_MODEL_PATHS_EXTRA, Arrays.asList(base.getName() + "/" + genmodelPath));
			props.put(GeckoEmfGenerator.INCLUDE_GEN_MODEL_FOLDER, genmodelLocation);
			gen.getOptions().data = new Object[] {props};
			
			genModel.setCanGenerate(true);
			genModel.setUpdateClasspath(false);
			
			info("Starting generator run");
			try {
				Diagnostic diagnostic = gen.generate(genModel, GenBaseGeneratorAdapter.MODEL_PROJECT_TYPE, CodeGenUtil.EclipseUtil.createMonitor(new LoggingProgressMonitor(), 1));
				printResult(diagnostic);
				if(diagnostic.getSeverity() != Diagnostic.OK) {
					return Optional.of(diagnostic.toString());
				} 
			} catch (Exception e) {
				String message = "An error appeared while generating: " + e.getMessage();
				error(message, e);
				return Optional.of(message);
			}
		} finally {
			resourceSet.getResources().forEach(Resource::unload);
			resourceSet.getResources().clear();
		}
		return Optional.empty();
	}

	/**
	 * @param diagnostic
	 */
	private void printResult(Diagnostic diagnostic) {
		printResult(diagnostic, ""); //$NON-NLS-1$
	}
	private void printResult(Diagnostic diagnostic, String prefix) {
		if(diagnostic.getSeverity() == Diagnostic.OK) {
			info(prefix + diagnostic.getMessage() + " - " + diagnostic.getSource()); //$NON-NLS-1$
		} else {
			error(prefix + diagnostic.getMessage() + " - " + diagnostic.getSource()); //$NON-NLS-1$
			if(diagnostic.getException() != null) {
				error(prefix, diagnostic.getException());
				if(diagnostic.getException() instanceof NullPointerException) {
					Throwable t = diagnostic.getException(); 
					StackTraceElement stackTraceElement = t.getStackTrace()[0];
					if(stackTraceElement.getClassName().equals(GenModelImpl.class.getName()) && stackTraceElement.getMethodName().equals("setImportManager")) {
						String message = prefix + "|-> Nullpointer Exception while setting Import Manager on the Genmodel indicates that the genmodel may need to be reloaded. This usually happens when a referenced Genmodel can't be loaded.";
						error(message);
					}
				}
				error("");
			}
		}
		diagnostic.getChildren().forEach(c -> printResult(c, prefix + "  "));
	}

	public static class LoggingProgressMonitor implements IProgressMonitor{

		private String name;


		@Override
		public void beginTask(String name, int totalWork) {
			info("beginTask " + name);
		}


		@Override
		public void done() {
			info("done");
		}


		@Override
		public void internalWorked(double work) {
			info("internaly worked " + work + " on " + name);
		}


		@Override
		public boolean isCanceled() {
			return false;
		}


		@Override
		public void setCanceled(boolean value) {
			info(name + " cancled");
		}


		@Override
		public void setTaskName(String name) {
			this.name = name;

		}


		@Override
		public void subTask(String name) {
			info("subtask " + name);
		}


		@Override
		public void worked(int work) {
			info(" worked " + work + " on " + name);
		}

	}

}