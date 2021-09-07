package org.geckoprojects.emf.example.model.manual.configuration;

import org.eclipse.emf.ecore.resource.Resource.Factory.Registry;
import org.geckoprojects.emf.core.api.EPackageConfigurator;
import org.geckoprojects.emf.core.api.ResourceFactoryConfigurator;
import org.geckoprojects.emf.example.model.manual.ManualPackage;
import org.geckoprojects.emf.example.model.manual.util.ManualResourceFactoryImpl;

public class ManualPackageConfigurator implements EPackageConfigurator, ResourceFactoryConfigurator {

	/* 
	 * (non-Javadoc)
	 * @see org.gecko.emf.osgi.ResourceFactoryConfigurator#configureResourceFactory(org.eclipse.emf.ecore.resource.Resource.Factory.Registry)
	 */
	@Override
	public void configureResourceFactory(Registry registry) {
		registry.getExtensionToFactoryMap().put("manual", new ManualResourceFactoryImpl());
		registry.getContentTypeToFactoryMap().put("manual#1.0", new ManualResourceFactoryImpl());
	}

	/* 
	 * (non-Javadoc)
	 * @see org.gecko.emf.osgi.ResourceFactoryConfigurator#unconfigureResourceFactory(org.eclipse.emf.ecore.resource.Resource.Factory.Registry)
	 */
	@Override
	public void unconfigureResourceFactory(Registry registry) {
		registry.getExtensionToFactoryMap().remove("manual");
		registry.getContentTypeToFactoryMap().remove("manual#1.0");
	}

	/* 
	 * (non-Javadoc)
	 * @see org.gecko.emf.osgi.EPackageRegistryConfigurator#configureEPackage(org.eclipse.emf.ecore.EPackage.Registry)
	 */
	@Override
	public void configureEPackage(org.eclipse.emf.ecore.EPackage.Registry registry) {
		registry.put(ManualPackage.eNS_URI, ManualPackage.eINSTANCE);
	}

	/* 
	 * (non-Javadoc)
	 * @see org.gecko.emf.osgi.EPackageRegistryConfigurator#unconfigureEPackage(org.eclipse.emf.ecore.EPackage.Registry)
	 */
	@Override
	public void unconfigureEPackage(org.eclipse.emf.ecore.EPackage.Registry registry) {
		registry.remove(ManualPackage.eNS_URI);
	}

}