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
package org.eclipse.fennec.emf.osgi.codegen.adapter;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.eclipse.emf.codegen.ecore.CodeGenEcorePlugin;
import org.eclipse.emf.codegen.ecore.generator.GeneratorAdapterFactory;
import org.eclipse.emf.codegen.ecore.genmodel.GenModel;
import org.eclipse.emf.codegen.ecore.genmodel.GenPackage;
import org.eclipse.emf.codegen.ecore.genmodel.GenResourceKind;
import org.eclipse.emf.codegen.ecore.genmodel.generator.GenPackageGeneratorAdapter;
import org.eclipse.emf.common.util.Diagnostic;
import org.eclipse.emf.common.util.Monitor;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EAnnotation;
import org.eclipse.emf.ecore.EModelElement;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.emf.ecore.EcorePackage;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.BinaryResourceImpl.BinaryIO.Version;
import org.eclipse.emf.ecore.resource.impl.BinaryResourceImpl.EObjectOutputStream;
import org.eclipse.emf.ecore.resource.impl.ResourceFactoryImpl;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.emf.ecore.xmi.XMLResource;
import org.eclipse.emf.ecore.xmi.impl.EcoreResourceFactoryImpl;
import org.eclipse.emf.ecore.xmi.impl.URIHandlerImpl;
import org.eclipse.emf.ecore.xmi.impl.XMIResourceImpl;
import org.eclipse.fennec.emf.osgi.codegen.GeckoEmfGenerator;

/**
 * EMF codegen generator adapter that is responsible to generate the OSGi
 * service component, that registers the {@link EPackage} and
 * {@link ResourceFactoryImpl} instances to the corresponding registry
 * 
 * @author Mark Hoffmann
 * @since 25.07.2017
 */
public class GeckoGenPackageGeneratorAdapter extends GenPackageGeneratorAdapter {

	/** PACKAGE_INFO_NAME */
	private static final String PACKAGE_INFO_NAME = "package-info";
	protected static final int PACKAGE_OSGI_CONFIGURATION = 14;
	protected static final int PACKAGE_INFO = 15;
	protected static final int PACKAGE_INFO_IMPL = 16;
	protected static final int PACKAGE_INFO_UTIL = 17;
	protected static final int RESOURCE_FACOTRY = 18;
	protected static final int EPACKAGE_CONFIGURATOR_CLASS = 19;
	protected static final int PACKAGE_CLASS = 20;
	protected static final int FACTORY_CLASS = 21;

	private static final JETEmitterDescriptor[] JET_EMITTER_DESCRIPTORS = {
			new JETEmitterDescriptor("model/ConfigComponent.javajet",
					"org.eclipse.fennec.emf.osgi.codegen.templates.model.ConfigurationComponentClass"),
			new JETEmitterDescriptor("model/PackageInfo.javajet",
					"org.eclipse.fennec.emf.osgi.codegen.templates.model.PackageInfo"),
			new JETEmitterDescriptor("model/PackageInfoImpl.javajet",
					"org.eclipse.fennec.emf.osgi.codegen.templates.model.PackageInfoImpl"),
			new JETEmitterDescriptor("model/PackageInfoUtil.javajet",
					"org.eclipse.fennec.emf.osgi.codegen.templates.model.PackageInfoUtil"),
			new JETEmitterDescriptor("model/ResourceFactoryClass.javajet",
					"org.eclipse.fennec.emf.osgi.codegen.templates.model.ResourceFactoryClass"),
			new JETEmitterDescriptor("model/EPackageConfigurator.javajet",
					"org.eclipse.fennec.emf.osgi.codegen.templates.model.EPackageConfiguratorClass"),
			new JETEmitterDescriptor("model/PackageClass.javajet",
					"org.eclipse.fennec.emf.osgi.codegen.templates.model.PackageClass"),
			new JETEmitterDescriptor("model/FactoryClass.javajet",
					"org.eclipse.fennec.emf.osgi.codegen.templates.model.FactoryClass") };

	public GeckoGenPackageGeneratorAdapter(GeneratorAdapterFactory generatorAdapterFactory) {
		super(generatorAdapterFactory);
	}

	/* 
	 * (non-Javadoc)
	 * @see org.eclipse.emf.codegen.ecore.generator.AbstractGeneratorAdapter#getEncoding(org.eclipse.emf.common.util.URI)
	 */
	@Override
	protected String getEncoding(URI workspacePath) {
		String encoding = super.getEncoding(workspacePath);
		if(encoding == null) {
			return "UTF8";
		}
		return encoding;
	}
	
	/**
	 * Returns the set of <code>JETEmitterDescriptor</code>s used by the adapter.
	 * The contents of the returned array should never be changed. Rather,
	 * subclasses may override this method to return a different array altogether.
	 */
	@Override
	protected JETEmitterDescriptor[] getJETEmitterDescriptors() {
		List<JETEmitterDescriptor> descs = new LinkedList<>();
		descs.addAll(Arrays.asList(super.getJETEmitterDescriptors()));
		descs.addAll(Arrays.asList(JET_EMITTER_DESCRIPTORS));
		return descs.toArray(new JETEmitterDescriptor[descs.size()]);
	}

	@Override
	protected Diagnostic generateModel(Object object, Monitor monitor) {
		monitor.beginTask("Generating model", 15);

		GenPackage genPackage = (GenPackage) object;
		message = "Generating Code for " + genPackage.getBasicPackageName();
		monitor.subTask(message);

		GenModel genModel = genPackage.getGenModel();

		ensureProjectExists(genModel.getModelDirectory(), genPackage, MODEL_PROJECT_TYPE, genModel.isUpdateClasspath(),
				createMonitor(monitor, 1));

		super.generateSchema(genPackage, monitor);
		super.generatePackagePublication(genPackage, monitor);
		generatePackageSerialization(genPackage, monitor);
		super.generateXMLProcessorClass(genPackage, monitor);
		super.generateResourceClass(genPackage, monitor);
		generatePackageInterface(genPackage, monitor);
		super.generatePackageClass(genPackage, monitor);
		generateFactoryInterface(genPackage, monitor);
		super.generateFactoryClass(genPackage, monitor);
		super.generateSwitchClass(genPackage, monitor);
		super.generateAdapterFactoryClass(genPackage, monitor);
		super.generateValidatorClass(genPackage, monitor);

		if (genModel.isOSGiCompatible()) {
			generateEPackageConfigurator(genPackage, monitor);
			generateResourceFactoryClass(genPackage, monitor);
			generateConfigurationComponent(genPackage, monitor);
			generatePackageInfo(genPackage, monitor);

		} else {
			super.generateResourceFactoryClass(genPackage, monitor);
		}

		return Diagnostic.OK_INSTANCE;
	}

	private static class ExtendedEcoreResourceFactoryImpl extends EcoreResourceFactoryImpl {
		@Override
		public Resource createResource(URI uri) {
			XMLResource result = new XMIResourceImpl(uri) {
				@Override
				protected boolean useIDs() {
					return eObjectToIDMap != null || idToEObjectMap != null;
				}

				@Override
				protected EObjectOutputStream createEObjectOutputStream(OutputStream outputStream, Map<?, ?> options,
						Version version, final URIHandler uriHandler) throws IOException {
					return new EObjectOutputStream(outputStream, options, version) {
						@Override
						public void writeURI(URI uri, String fragment) throws IOException {
							// We need to ensure that the callback being used for redirecting to the nsURI
							// is invoked also for binary serialization, which relies on the fragment being
							// present.
							URI deresolvedURI = deresolve(uri.appendFragment(fragment));
							super.writeURI(deresolvedURI, deresolvedURI.fragment());
						}

						@Override
						protected URI deresolve(URI uri) {
							return uriHandler == null ? super.deresolve(uri) : uriHandler.deresolve(uri);
						}
					};
				}
			};
			result.setEncoding("UTF-8");

			if ("genmodel".equals(uri.fileExtension())) {
				result.getDefaultLoadOptions().put(XMLResource.OPTION_RECORD_UNKNOWN_FEATURE, Boolean.TRUE);
			}

			result.getDefaultSaveOptions().put(XMLResource.OPTION_USE_ENCODED_ATTRIBUTE_STYLE, Boolean.TRUE);
			result.getDefaultSaveOptions().put(XMLResource.OPTION_LINE_WIDTH, 80);
			result.getDefaultSaveOptions().put(XMLResource.OPTION_URI_HANDLER,
					new URIHandlerImpl.PlatformSchemeAware());
			return result;
		}
	}

	protected void generatePackageSerialization(GenPackage genPackage, Monitor monitor) {
		if (genPackage.hasClassifiers() && genPackage.isLoadingInitialization()) {
			monitor = createMonitor(monitor, 1);

			try {
				monitor.beginTask("Serializing Ecore because initialize by loading is set to true", 2);

				final GenModel genModel = genPackage.getGenModel();
				String targetPathName = genModel.getModelDirectory() + "/"
						+ genPackage.getClassPackageName().replace('.', '/') + "/"
						+ genPackage.getSerializedPackageFilename();
				message = CodeGenEcorePlugin.INSTANCE.getString("_UI_GeneratingPackageSerialization_message",
						new Object[] { targetPathName });
				monitor.subTask(message);

				URI targetFile = toURI(targetPathName);
				ensureContainerExists(targetFile.trimSegments(1), createMonitor(monitor, 1));

				final ResourceSet originalSet = genModel.eResource().getResourceSet();
				EPackage originalPackage = genPackage.getEcorePackage();
				final String originalPackageNsURI = originalPackage.getNsURI();

				ResourceSet outputSet = new ResourceSetImpl();
				GeckoEmfGenerator.info("Setting Uri Handler: " + originalSet.getURIConverter().getURIHandlers().get(0));
				outputSet.getURIConverter().getURIHandlers().add(0,
						originalSet.getURIConverter().getURIHandlers().get(0));
				outputSet.getResourceFactoryRegistry().getExtensionToFactoryMap()
						.put(Resource.Factory.Registry.DEFAULT_EXTENSION, new ExtendedEcoreResourceFactoryImpl());
				URI targetURI = toPlatformResourceURI(targetFile);
				Resource outputResource = outputSet.createResource(targetURI);

				// Copy the package, excluding unwanted annotations.
				//
				EcoreUtil.Copier copier = new EcoreUtil.Copier() {
					private static final long serialVersionUID = 1L;

					@Override
					protected void copyContainment(EReference reference, EObject object, EObject copyEObject) {
						if (reference == EcorePackage.Literals.EMODEL_ELEMENT__EANNOTATIONS) {
							List<EAnnotation> result = ((EModelElement) copyEObject).getEAnnotations();
							result.clear();

							for (EAnnotation eAnnotation : ((EModelElement) object).getEAnnotations()) {
								if (!genModel.isSuppressedAnnotation(eAnnotation.getSource())) {
									result.add((EAnnotation) copy(eAnnotation));
								}
							}
							return;
						}
						super.copyContainment(reference, object, copyEObject);
					}
				};
				EPackage outputPackage = (EPackage) copier.copy(originalPackage);
				copier.copyReferences();
				outputResource.getContents().add(outputPackage);
				collapseEmptyPackages(outputPackage);

				// This URI handler redirects cross-document references to correct
				// namespace-based values.
				//
				XMLResource.URIHandler uriHandler = new URIHandlerImpl.PlatformSchemeAware() {
					private EPackage getContainingPackage(EObject object) {
						while (object != null) {
							if (object instanceof EPackage) {
								return (EPackage) object;
							}
							object = object.eContainer();
						}
						return null;
					}

					private String getRelativeFragmentPath(Resource resource, EObject base, String path) {
						String basePath = resource.getURIFragment(base);
						if (basePath != null && path.startsWith(basePath)) {
							int i = basePath.length();
							if (path.length() == i) {
								return "";
							} else if (path.charAt(i) == '/') {
								return path.substring(i);
							}
						}
						return null;
					}

					private EPackage getNonEmptySuperPackage(EPackage ePackage) {
						EPackage result = ePackage.getESuperPackage();
						while (result != null && result.getEClassifiers().isEmpty()) {
							result = result.getESuperPackage();
						}
						return result;
					}

					private URI redirect(URI uri) {
						if (uri != null && !uri.isCurrentDocumentReference() && uri.hasFragment()) {
							URI base = uri.trimFragment();
							String fragment = uri.fragment();
							Resource resource = originalSet.getResource(base, false);
							if (resource != null) {
								EObject object = resource.getEObject(fragment);
								if (object != null) {
									EPackage ePackage = getContainingPackage(object);
									if (ePackage != null) {
										String relativePath = getRelativeFragmentPath(resource, ePackage, fragment);
										if (relativePath != null) {
											StringBuilder path = new StringBuilder();
											EPackage superPackage = getNonEmptySuperPackage(ePackage);
											while (superPackage != null) {
												path.insert(0, '/');
												path.insert(1, ePackage.getName());
												ePackage = superPackage;
												superPackage = getNonEmptySuperPackage(ePackage);
											}
											path.insert(0, '/');
											path.append(relativePath);

											// With Xcore generation, the previously serialized resource might have been
											// loaded into the resource set.
											// So if the package nsURI is the same as the nsURI of the package we're
											// generating,
											// and if the resource URI is the one to which we are serializing,
											// redirect to the base URI so that it's subsequently deresolved to a
											// document relative URI.
											String nsURI = ePackage.getNsURI();
											URI redirectedURI = originalPackageNsURI.equals(nsURI)
													&& resource.getURI().equals(baseURI) ? baseURI
															: URI.createURI(nsURI);
											return redirectedURI.appendFragment(path.toString());
										}
									}
								}
							}
						}
						return uri;
					}

					@Override
					public URI deresolve(URI uri) {
						return super.deresolve(redirect(uri));
					}

					@Override
					public URI resolve(URI uri) {
						return super.resolve(redirect(uri));
					}

					@Override
					public void setBaseURI(URI uri) {
						super.setBaseURI(redirect(uri));
					}
				};
				Map<Object, Object> options = new HashMap<Object, Object>();
				options.put(XMLResource.OPTION_URI_HANDLER, uriHandler);
				options.put(Resource.OPTION_SAVE_ONLY_IF_CHANGED, Resource.OPTION_SAVE_ONLY_IF_CHANGED_MEMORY_BUFFER);
				if ("ebin".equals(outputResource.getURI().fileExtension())) {
					options.put(XMLResource.OPTION_BINARY, Boolean.TRUE);
				} else {
					options.put(Resource.OPTION_LINE_DELIMITER, Resource.OPTION_LINE_DELIMITER_UNSPECIFIED);
				}

				try {
					GeckoEmfGenerator.info("saving ecore to " + outputResource.getURI().toString());
					outputResource.save(options);
				} catch (IOException exception) {
					// DMS handle this well.
					GeckoEmfGenerator.error("Could not serialize ecore to " + outputResource.getURI().toString(), exception);
				}
			} finally {
				GeckoEmfGenerator.info("Finished Serialization of ecore");
				monitor.done();
			}
		} else {
			monitor.worked(1);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.emf.codegen.ecore.generator.AbstractGeneratorAdapter#
	 * toPlatformResourceURI(org.eclipse.emf.common.util.URI)
	 */
	@Override
	protected URI toPlatformResourceURI(URI uri) {
		GeckoEmfGenerator.info("toPlatformResourceURI: " + uri.toString());
		URI result = super.toPlatformResourceURI(uri);
		GeckoEmfGenerator.info("toPlatformResourceURI result: " + uri.toString());
		return result;
	}

	@Override
	protected void generateResourceFactoryClass(GenPackage genPackage, Monitor monitor) {
		if (genPackage.getResource() != GenResourceKind.NONE_LITERAL) {
			message = CodeGenEcorePlugin.INSTANCE.getString("_UI_GeneratingJavaClass_message",
					new Object[] { genPackage.getQualifiedResourceFactoryClassName() });
			monitor.subTask(message);
			generateJava(genPackage.getGenModel().getModelDirectory(), genPackage.getUtilitiesPackageName(),
					genPackage.getResourceFactoryClassName(),
					getJETEmitter(getJETEmitterDescriptors(), RESOURCE_FACOTRY), null, createMonitor(monitor, 1));
		} else {
			monitor.worked(1);
		}
	}

	/**
	 * Generates the EPackageConfigurator for the {@link GenPackage}
	 * 
	 * @param genPackage the package to create the configuration component for
	 * @param monitor    the progress monitor
	 */
	protected void generateEPackageConfigurator(GenPackage genPackage, Monitor monitor) {
		GenModel genModel = genPackage.getGenModel();
		if (genPackage.hasClassifiers()) {
			message = String.format("Generating EPackageConfigurator for package '%s'", genPackage.getPackageName());
			monitor.subTask(message);
			generateJava(genModel.getModelDirectory(), getConfigurationPackageName(genPackage),
					getBasicConfiguratorClassName(genPackage),
					getJETEmitter(getJETEmitterDescriptors(), EPACKAGE_CONFIGURATOR_CLASS),
					new Object[] { new Object[] { genPackage, Boolean.TRUE, Boolean.FALSE } },
					createMonitor(monitor, 1));
		} else {
			monitor.worked(1);
		}
	}

	/**
	 * Generates the configuration service component for the {@link GenPackage}
	 * 
	 * @param genPackage the package to create the configuration component for
	 * @param monitor    the progress monitor
	 */
	protected void generateConfigurationComponent(GenPackage genPackage, Monitor monitor) {
		GenModel genModel = genPackage.getGenModel();
		if (genPackage.hasClassifiers()) {
			message = String.format("Generating OSGi configuration component for package '%s'",
					genPackage.getPackageName());
			monitor.subTask(message);
			generateJava(genModel.getModelDirectory(), getConfigurationPackageName(genPackage),
					getBasicConfigurationComponentClassName(genPackage),
					getJETEmitter(getJETEmitterDescriptors(), PACKAGE_OSGI_CONFIGURATION),
					new Object[] { new Object[] { genPackage, Boolean.TRUE, Boolean.FALSE } },
					createMonitor(monitor, 1));
		} else {
			monitor.worked(1);
		}
	}

	/**
	 * Generates the package info for the {@link GenPackage}
	 * 
	 * @param genPackage the package to create the configuration component for
	 * @param monitor    the progress monitor
	 */
	protected void generatePackageInfo(GenPackage genPackage, Monitor monitor) {
		GenModel genModel = genPackage.getGenModel();
		if (genPackage.hasClassifiers()) {
			message = String.format("Generating package info for package '%s'", genPackage.getPackageName());
			monitor.subTask(message);
			generateJava(genModel.getModelDirectory(), genPackage.getInterfacePackageName(), PACKAGE_INFO_NAME,
					getJETEmitter(getJETEmitterDescriptors(), PACKAGE_INFO),
					new Object[] { new Object[] { genPackage, Boolean.TRUE, Boolean.FALSE } },
					createMonitor(monitor, 1));
			generateJava(genModel.getModelDirectory(), getClassPackageName(genPackage), PACKAGE_INFO_NAME,
					getJETEmitter(getJETEmitterDescriptors(), PACKAGE_INFO_IMPL),
					new Object[] { new Object[] { genPackage, Boolean.TRUE, Boolean.FALSE } },
					createMonitor(monitor, 1));
			if ((genPackage.hasClassifiers() && genPackage.getResource().getValue() == GenResourceKind.XML)
					|| (genPackage.getResource() != GenResourceKind.NONE_LITERAL)
					|| (genPackage.hasClassifiers() && genPackage.isAdapterFactory()
							&& !genPackage.getGenClasses().isEmpty())
					|| (genPackage.hasClassifiers() && genPackage.hasConstraints())) {
				generateJava(genModel.getModelDirectory(), genPackage.getUtilitiesPackageName(), PACKAGE_INFO_NAME,
						getJETEmitter(getJETEmitterDescriptors(), PACKAGE_INFO_UTIL),
						new Object[] { new Object[] { genPackage, Boolean.TRUE, Boolean.FALSE } },
						createMonitor(monitor, 1));
			}
		} else {
			monitor.worked(1);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.emf.codegen.ecore.genmodel.generator.GenPackageGeneratorAdapter#
	 * generatePackageInterface(org.eclipse.emf.codegen.ecore.genmodel.GenPackage,
	 * org.eclipse.emf.common.util.Monitor)
	 */
	@Override
	protected void generatePackageInterface(GenPackage genPackage, Monitor monitor) {
		GenModel genModel = genPackage.getGenModel();

		if (genPackage.hasClassifiers() && !genModel.isSuppressEMFMetaData() && !genModel.isSuppressInterfaces()) {
			message = String.format("Generating package interface info for package '%s'",
					genPackage.getQualifiedPackageInterfaceName());
			monitor.subTask(message);

			@SuppressWarnings("unchecked")
			Map<String, Object> props = (Map<String, Object>) getAdapterFactory().getGenerator().getOptions().data[0];

			generateJava(genModel.getModelDirectory(), genPackage.getReflectionPackageName(),
					genPackage.getPackageInterfaceName(), getJETEmitter(getJETEmitterDescriptors(), PACKAGE_CLASS),
					new Object[] { new Object[] { genPackage, Boolean.TRUE, Boolean.FALSE, props } },
					createMonitor(monitor, 1));
		} else {
			monitor.worked(1);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.emf.codegen.ecore.genmodel.generator.GenPackageGeneratorAdapter#
	 * generateFactoryInterface(org.eclipse.emf.codegen.ecore.genmodel.GenPackage,
	 * org.eclipse.emf.common.util.Monitor)
	 */
	@Override
	protected void generateFactoryInterface(GenPackage genPackage, Monitor monitor) {
		GenModel genModel = genPackage.getGenModel();

		if (genPackage.hasClassifiers() && !genModel.isSuppressInterfaces()) {
			message = String.format("Generating factory interface info for package '%s'",
					genPackage.getQualifiedFactoryInterfaceName());
			monitor.subTask(message);
			generateJava(genModel.getModelDirectory(), genPackage.getReflectionPackageName(),
					genPackage.getFactoryInterfaceName(), getJETEmitter(getJETEmitterDescriptors(), FACTORY_CLASS),
					new Object[] { new Object[] { genPackage, Boolean.TRUE, Boolean.FALSE } },
					createMonitor(monitor, 1));
		} else {
			monitor.worked(1);
		}
	}

	/**
	 * Returns the configuration component package name
	 * 
	 * @param genPackage the generator package
	 * @return the package name
	 */
	private String getConfigurationPackageName(GenPackage genPackage) {
		String result = genPackage.getInterfacePackageName();
		return result + ".configuration";
	}

	/**
	 * Returns the Package impl names
	 * 
	 * @param genPackage the generator package
	 * @return the package name
	 */
	private String getClassPackageName(GenPackage genPackage) {
		String result = genPackage.getInterfacePackageName();
		return result + "." + genPackage.getClassPackageSuffix();
	}

	/**
	 * Returns the class name of the configuration component
	 * 
	 * @param genPackage the generator package
	 * @return the class name
	 */
	private String getBasicConfigurationComponentClassName(GenPackage genPackage) {
		return genPackage.getPrefix() + "ConfigurationComponent";
	}

	/**
	 * Returns the class name of the EPackageConfigurator
	 * 
	 * @param genPackage the generator package
	 * @return the class name
	 */
	private String getBasicConfiguratorClassName(GenPackage genPackage) {
		return genPackage.getPrefix() + "EPackageConfigurator";
	}

}
