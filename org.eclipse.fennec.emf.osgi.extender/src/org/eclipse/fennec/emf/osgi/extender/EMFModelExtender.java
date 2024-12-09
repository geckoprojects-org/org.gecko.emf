
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

import java.io.IOException;
import java.util.Collections;
import java.util.Dictionary;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EcorePackage;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.eclipse.emf.ecore.xmi.impl.EcoreResourceFactoryImpl;
import org.eclipse.fennec.emf.osgi.configurator.EPackageConfigurator;
import org.eclipse.fennec.emf.osgi.configurator.ResourceFactoryConfigurator;
import org.eclipse.fennec.emf.osgi.constants.EMFNamespaces;
import org.eclipse.fennec.emf.osgi.extender.model.Model;
import org.eclipse.fennec.emf.osgi.extender.model.ModelNamespace;
import org.eclipse.fennec.emf.osgi.extender.model.ModelState;
import org.eclipse.fennec.emf.osgi.extender.model.State;
import org.osgi.annotation.bundle.Capability;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.BundleEvent;
import org.osgi.framework.Constants;
import org.osgi.framework.ServiceRegistration;
import org.osgi.namespace.extender.ExtenderNamespace;
import org.osgi.util.tracker.BundleTracker;
import org.osgi.util.tracker.BundleTrackerCustomizer;

/**
 * The main class of the EMF ecore model extender.
 *
 */
@Capability(namespace = ExtenderNamespace.EXTENDER_NAMESPACE, name = EMFNamespaces.EMF_MODEL_EXTENDER_NAME, version = "1.0.0")
public class EMFModelExtender {

	private static Logger logger = Logger.getLogger(EMFModelExtender.class.getName());

	private final BundleContext bundleContext;

	private final State state;

	private final BundleTracker<Bundle> tracker;

	private volatile boolean active = true;

	private Object coordinator;

	private final ExecutorService queue;

	private final ResourceSet resourceSet;

	/**
	 * Create a new EMF Model Extender and start it
	 *
	 * @param bc The bundle context
	 */
	public EMFModelExtender(final BundleContext bc) {
		this.queue = Executors.newSingleThreadExecutor(r->{
			Thread t = Executors.defaultThreadFactory().newThread(r);
			t.setDaemon(true);
			t.setName("Gecko EMF Model Extender Worker Thread");
			return t;
		});
		this.resourceSet = new ResourceSetImpl();
		this.resourceSet.getPackageRegistry().put(EcorePackage.eNS_URI, EcorePackage.eINSTANCE);
		this.resourceSet.getResourceFactoryRegistry().getExtensionToFactoryMap().put(EcorePackage.eNAME, new EcoreResourceFactoryImpl());

		this.bundleContext = bc;
		State s = null;
		try {
			s = State.createOrReadState(bundleContext.getDataFile(State.FILE_NAME));
		} catch ( final ClassNotFoundException | IOException e ) {
			logger.log(Level.SEVERE, e, ()->"Unable to read persisted state from " + State.FILE_NAME);
			s = new State();
		}
		this.state = s;
		this.tracker = new org.osgi.util.tracker.BundleTracker<>(this.bundleContext,
				Bundle.ACTIVE|Bundle.STARTING|Bundle.STOPPING|Bundle.RESOLVED|Bundle.INSTALLED,

				new BundleTrackerCustomizer<Bundle>() {

			@Override
			public Bundle addingBundle(final Bundle bundle, final BundleEvent event) {
				final int bundleState = bundle.getState();
				if ( active &&
						(bundleState == Bundle.ACTIVE || bundleState == Bundle.STARTING) ) {
					logger.fine(()->"Adding bundle " + getBundleIdentity(bundle) + " : " + getBundleState(bundleState));
					queue.submit(()->{
						if ( processAddBundle(bundle) ) {
							process();
						}
					});
				}
				return bundle;
			}

			@Override
			public void modifiedBundle(final Bundle bundle, final BundleEvent event, final Bundle object) {
				if (active) {
					final int bundleState = bundle.getState();
					switch (bundleState) {
					case Bundle.ACTIVE:
					case Bundle.STARTING:
						this.addingBundle(bundle, event);
						break;
					case Bundle.STOPPING:
						this.removedBundle(bundle, event, object);
						break;
					default:
						break;
					}

				}
			}

			@Override
			public void removedBundle(final Bundle bundle, final BundleEvent event, final Bundle object) {
				final int bundleState = bundle.getState();
				if ( active && (bundleState == Bundle.UNINSTALLED || bundleState == Bundle.STOPPING)) {
					logger.fine(()->"Removing bundle " + getBundleIdentity(bundle) + " : " + getBundleState(bundleState));
					try {
						if ( processRemoveBundle(bundle.getBundleId()) ) {
							process();
						}
					} catch ( final IllegalStateException ise) {
						logger.log(Level.SEVERE, ise, ()->"Error processing bundle " + getBundleIdentity(bundle));
					}
				}
			}

		});
	}

	private String getBundleIdentity(final Bundle bundle) {
		if ( bundle.getSymbolicName() == null ) {
			return bundle.getBundleId() + " (" + bundle.getLocation() + ")";
		} else {
			return bundle.getSymbolicName() + ":" + bundle.getVersion() + " (" + bundle.getBundleId() + ")";
		}
	}

	private String getBundleState(int state) {
		switch ( state ) {
			case Bundle.ACTIVE : return "active";
			case Bundle.INSTALLED : return "installed";
			case Bundle.RESOLVED : return "resolved";
			case Bundle.STARTING : return "starting";
			case Bundle.STOPPING : return "stopping";
			case Bundle.UNINSTALLED : return "uninstalled";
			default: return String.valueOf(state);
		}
	}

	/**
	 * Shut down the extender
	 */
	public void shutdown() {
		this.active = false;
		this.queue.shutdown();
		try {
			if (!this.queue.awaitTermination(1, TimeUnit.SECONDS)) { //optional *
				this.queue.shutdownNow(); 
			}
		} catch (InterruptedException e) {
			logger.log(Level.SEVERE, e, ()->"Error shutting down EMF Model Extender executor service");
			Thread.currentThread().interrupt();
		}
		this.tracker.close();
	}

	/**
	 * Start the extender.
	 */
	public void start() {
		final Bundle[] bundles = this.bundleContext.getBundles();
		final Set<Long> ids = new HashSet<>();
		for(final Bundle b : bundles) {
			ids.add(b.getBundleId());
			final int bundleState = b.getState();
			if ( bundleState == Bundle.ACTIVE || bundleState == Bundle.STARTING ) {
				processAddBundle(b);
			}
		}
		for(final long id : state.getKnownBundleIds()) {
			if ( !ids.contains(id) ) {
				processRemoveBundle(id);
			}
		}
		this.process();
		this.tracker.open();
	}

	public boolean processAddBundle(final Bundle bundle) {
		final long bundleId = bundle.getBundleId();
		final long bundleLastModified = bundle.getLastModified();

		final Long lastModified = state.getLastModified(bundleId);
		if ( lastModified != null && lastModified.longValue() == bundleLastModified ) {
			// no changes, nothing to do
			return false;
		}

		List<Model> bundleModels = Collections.emptyList();
		try {
			final Set<String> paths = ModelHelper.isModelBundle(bundle, this.bundleContext.getBundle().getBundleId());
			if ( !paths.isEmpty() ) {
				final ModelHelper.Diagnostic report = new ModelHelper.Diagnostic();
				bundleModels = ModelHelper.readModelsFromBundle(bundle, resourceSet, paths, report);
				for(final String w : report.warnings) {
					logger.log(Level.WARNING, w);
				}
				for(final String e : report.errors) {
					logger.log(Level.SEVERE, e);
				}
			}
		} catch ( final IllegalStateException ise) {
			logger.log(Level.SEVERE, ise, ()->"Error processing bundle " + getBundleIdentity(bundle));
		}
		if ( lastModified != null ) {
			processRemoveBundle(bundleId);
		}
		if ( !bundleModels.isEmpty() ) {
			bundleModels.forEach(state::add);
			state.setLastModified(bundleId, bundleLastModified);
			return true;
		}
		return lastModified != null;
	}

	public boolean processRemoveBundle(final long bundleId) {
		if ( state.getLastModified(bundleId) != null ) {
			state.removeLastModified(bundleId);
			for(final String ns : state.getNamespaces()) {
				final ModelNamespace modelList = state.getModels(ns);
				modelList.uninstall(bundleId);
			}
			return true;
		}
		return false;
	}

	/**
	 * Set or unset the coordinator service
	 * @param coordinator The coordinator service or {@code null}
	 */
	public void setCoordinator(final Object coordinator) {
		this.coordinator = coordinator;
	}

	/**
	 * Process the state to activate/deactivate configurations
	 */
	public void process() {
		final Object localCoordinator = this.coordinator;
		Object coordination = null;
		if ( localCoordinator != null ) {
			coordination = CoordinatorUtil.getCoordination(localCoordinator);
		}

		try {
			for(final String mns : state.getNamespaces()) {
				final ModelNamespace modelList = state.getModels(mns);

				if ( modelList.hasChanges() && process(modelList) ) {
					try {
						State.writeState(this.bundleContext.getDataFile(State.FILE_NAME), state);
					} catch ( final IOException ioe) {
						logger.log(Level.SEVERE, ioe, ()->"Unable to persist state to " + State.FILE_NAME);
					}
				}
			}

		} finally {
			if ( coordination != null ) {
				CoordinatorUtil.endCoordination(coordination);
			}
		}
	}

	/**
	 * Process changes to a pid.
	 * @param modelList The config list
	 * @return {@code true} if the change has been processed, {@code false} if a retry is required
	 */
	public boolean process(final ModelNamespace modelList) {
		Model toActivate = null;
		Model toDeactivate = null;

		for(final Model model : modelList) {
			switch ( model.getState() ) {
			case INSTALL     : // activate if first found
				if ( toActivate == null ) {
					toActivate = model;
				}
				break;

			case IGNORED     : // same as installed
			case INSTALLED   : // check if we have to uninstall
				if ( toActivate == null ) {
					toActivate = model;
				} else {
					model.setState(ModelState.INSTALL);
				}
				break;

			case UNINSTALL   : // deactivate if first found (we should only find one anyway)
				if ( toDeactivate == null ) {
					toDeactivate = model;
				}
				break;

			case UNINSTALLED : // nothing to do
				break;
			}

		}
		// if there is a configuration to activate, we can directly activate it
		// without deactivating (reducing the changes of the configuration from two
		// to one)
		boolean noRetryNeeded = true;
		if ( toActivate != null && toActivate.getState() == ModelState.INSTALL ) {
			noRetryNeeded = activate(modelList, toActivate);
		}
		if ( toActivate == null && toDeactivate != null ) {
			noRetryNeeded = deactivate(modelList, toDeactivate);
		}

		if ( noRetryNeeded ) {
			// remove all uninstall(ed) configurations
			final Iterator<Model> iter = modelList.iterator();
			boolean foundInstalled = false;
			while ( iter.hasNext() ) {
				final Model model = iter.next();
				if ( model.getState() == ModelState.UNINSTALL || model.getState() == ModelState.UNINSTALLED ) {
					iter.remove();
				} else if ( model.getState() == ModelState.INSTALLED ) {
					if ( foundInstalled ) {
						model.setState(ModelState.INSTALL);
					} else {
						foundInstalled = true;
					}
				}
			}

			// mark as processed
			modelList.setHasChanges(false);
		}
		return noRetryNeeded;
	}

	/**
	 * Try to activate a configuration
	 * Check policy and change count
	 * @param modelList The configuration list
	 * @param model The configuration to activate
	 * @return {@code true} if activation was successful
	 */
	public boolean activate(final ModelNamespace modelList, final Model model) {

		final Bundle modelBundle = model.getBundleId() == -1 ? this.bundleContext.getBundle() : this.bundleContext.getBundle(Constants.SYSTEM_BUNDLE_LOCATION).getBundleContext().getBundle(model.getBundleId());
		EPackage ePackage = model.getEPackage();
		Dictionary<String,Object> properties = model.getProperties();
		ModelExtenderConfigurator configurator = new ModelExtenderConfigurator(ePackage, null, null);

		ServiceRegistration<?> modelConfigRegistration = modelBundle.getBundleContext().registerService(new String[]{EPackageConfigurator.class.getName(), 
				ResourceFactoryConfigurator.class.getName()}, configurator, properties);
		ServiceRegistration<EPackage> modelRegistration = modelBundle.getBundleContext().registerService(EPackage.class, ePackage, properties);
		model.setModelConfigRegistration(modelConfigRegistration);
		model.setModelRegistration(modelRegistration);
		model.setState(ModelState.INSTALLED);
		modelList.setChangeCount(modelList.getChangeCount() + 1);
		modelList.setLastInstalled(model);
		return true;
	}

	/**
	 * Try to deactivate a configuration
	 * Check policy and change count
	 * @param model The configuration
	 */
	public boolean deactivate(final ModelNamespace configList, final Model model) {
		ServiceRegistration<EPackage> modelRegistration = model.getModelRegistration();
		if (modelRegistration != null) {
			try {
				modelRegistration.unregister();
			} catch (IllegalStateException ise) {
				logger.log(Level.FINE, ise, ()->"Model - Service might be already unregistered");
			} finally {
				model.setModelRegistration(null);
			}
		}
		ServiceRegistration<?> modelConfigRegistration = model.getModelConfigRegistration();
		if (modelConfigRegistration != null) {
			try {
				modelConfigRegistration.unregister();
			} catch (IllegalStateException ise) {
				logger.log(Level.FINE, ise, ()->"Model Configuration Service might be already unregistered");
			} finally {
				model.setModelConfigRegistration(null);
			}
		}
		model.setState(ModelState.UNINSTALLED);
		configList.setChangeCount(-1);
		configList.setLastInstalled(null);

		return true;
	}
}
