package org.gecko.emf.osgi.codegen.templates.model;

import org.eclipse.emf.codegen.ecore.genmodel.*;
import org.gecko.emf.osgi.codegen.templates.model.helper.GeneratorHelper;

public class EPackageConfiguratorClass
{
  protected static String nl;
  public static synchronized EPackageConfiguratorClass create(String lineSeparator)
  {
    nl = lineSeparator;
    EPackageConfiguratorClass result = new EPackageConfiguratorClass();
    nl = null;
    return result;
  }

  public final String NL = nl == null ? (System.getProperties().getProperty("line.separator")) : nl;
  protected final String TEXT_1 = "";
  protected final String TEXT_2 = "/*";
  protected final String TEXT_3 = NL + " * ";
  protected final String TEXT_4 = NL + " */" + NL + "package ";
  protected final String TEXT_5 = ".configuration;" + NL;
  protected final String TEXT_6 = NL + NL + "/**" + NL + " * <!-- begin-user-doc -->" + NL + " * The <b>EPackageConfiguration</b> and <b>ResourceFactoryConfigurator</b> for the model." + NL + " * The package will be registered into a OSGi base model registry." + NL + " * <!-- end-user-doc -->";
  protected final String TEXT_7 = NL + " * <!-- begin-model-doc -->" + NL + " * ";
  protected final String TEXT_8 = NL + " * <!-- end-model-doc -->";
  protected final String TEXT_9 = NL + " * @see EPackageConfigurator" + NL + " * @generated" + NL + " */" + NL + "public class ";
  protected final String TEXT_10 = "EPackageConfigurator implements EPackageConfigurator" + NL + "{" + NL + "\t" + NL + "\tprivate ";
  protected final String TEXT_11 = " ePackage;" + NL + "" + NL + "\tprotected ";
  protected final String TEXT_12 = "EPackageConfigurator(";
  protected final String TEXT_13 = " ePackage){" + NL + "\t\tthis.ePackage = ePackage;" + NL + "\t}" + NL + "\t" + NL + "\t/**" + NL + "\t * (non-Javadoc)" + NL + "\t * @see org.gecko.emf.osgi.EPackageRegistryConfigurator#configureEPackage(org.eclipse.emf.ecore.EPackage.Registry)" + NL + "\t * @generated" + NL + "\t */" + NL + "\t@Override" + NL + "\tpublic void configureEPackage(org.eclipse.emf.ecore.EPackage.Registry registry) {" + NL + "\t\tregistry.put(";
  protected final String TEXT_14 = ".eNS_URI, ePackage);" + NL + "\t}" + NL + "\t" + NL + "\t/**" + NL + "\t * (non-Javadoc)" + NL + "\t * @see org.gecko.emf.osgi.EPackageRegistryConfigurator#unconfigureEPackage(org.eclipse.emf.ecore.EPackage.Registry)" + NL + "\t * @generated" + NL + "\t */" + NL + "\t@Override" + NL + "\tpublic void unconfigureEPackage(org.eclipse.emf.ecore.EPackage.Registry registry) {" + NL + "\t\tregistry.remove(";
  protected final String TEXT_15 = ".eNS_URI);" + NL + "\t}" + NL + "\t" + NL + "\t/**" + NL + "\t * A method providing the Properties the services around this Model should be registered with." + NL + "\t * @generated" + NL + "\t */" + NL + "\tpublic ";
  protected final String TEXT_16 = "<String, Object> getServiceProperties() {" + NL + "\t\t";
  protected final String TEXT_17 = "<String, Object> properties = new ";
  protected final String TEXT_18 = "<String, Object>();" + NL + "\t\tproperties.put(";
  protected final String TEXT_19 = ".EMF_MODEL_NAME, ";
  protected final String TEXT_20 = ".eNAME);" + NL + "\t\tproperties.put(";
  protected final String TEXT_21 = ".EMF_MODEL_NSURI, ";
  protected final String TEXT_22 = ".eNS_URI);" + NL + "\t\tproperties.put(";
  protected final String TEXT_23 = ".EMF_MODEL_REGISTRATION, ";
  protected final String TEXT_24 = ".MODEL_REGISTRATION_PROVIDED);" + NL + "\t\tproperties.put(";
  protected final String TEXT_25 = ".EMF_MODEL_FILE_EXT, \"";
  protected final String TEXT_26 = "\");";
  protected final String TEXT_27 = NL + "\t\tproperties.put(";
  protected final String TEXT_28 = ".EMF_MODEL_CONTENT_TYPE, \"";
  protected final String TEXT_29 = ".EMF_MODEL_VERSION, \"";
  protected final String TEXT_30 = "\");" + NL + "\t\treturn properties;" + NL + "\t}" + NL + "}";

  public String generate(Object argument)
  {
    final StringBuffer stringBuffer = new StringBuffer();
    final GenPackage genPackage = (GenPackage)((Object[])argument)[0]; final GenModel genModel=genPackage.getGenModel();
    stringBuffer.append(TEXT_1);
    stringBuffer.append(TEXT_2);
    {GenBase copyrightHolder = argument instanceof GenBase ? (GenBase)argument : argument instanceof Object[] && ((Object[])argument)[0] instanceof GenBase ? (GenBase)((Object[])argument)[0] : null;
    if (copyrightHolder != null && copyrightHolder.hasCopyright()) {
    stringBuffer.append(TEXT_3);
    stringBuffer.append(copyrightHolder.getCopyright(copyrightHolder.getGenModel().getIndentation(stringBuffer)));
    }}
    stringBuffer.append(TEXT_4);
    stringBuffer.append(genPackage.getInterfacePackageName());
    stringBuffer.append(TEXT_5);
    genModel.markImportLocation(stringBuffer, genPackage);
    genModel.addImport("org.gecko.emf.osgi.configurator.EPackageConfigurator");
    genModel.addImport(genPackage.getQualifiedPackageInterfaceName());
    stringBuffer.append(TEXT_6);
    if (genPackage.hasDocumentation()) {
    stringBuffer.append(TEXT_7);
    stringBuffer.append(genPackage.getDocumentation(genModel.getIndentation(stringBuffer)));
    stringBuffer.append(TEXT_8);
    }
    stringBuffer.append(TEXT_9);
    stringBuffer.append(genPackage.getPrefix());
    stringBuffer.append(TEXT_10);
    stringBuffer.append(genPackage.getPackageInterfaceName());
    stringBuffer.append(TEXT_11);
    stringBuffer.append(genPackage.getPrefix());
    stringBuffer.append(TEXT_12);
    stringBuffer.append(genPackage.getPackageInterfaceName());
    stringBuffer.append(TEXT_13);
    stringBuffer.append(genPackage.getPackageInterfaceName());
    stringBuffer.append(TEXT_14);
    stringBuffer.append(genPackage.getPackageInterfaceName());
    stringBuffer.append(TEXT_15);
    stringBuffer.append(genModel.getImportedName("java.util.Map"));
    stringBuffer.append(TEXT_16);
    stringBuffer.append(genModel.getImportedName("java.util.Map"));
    stringBuffer.append(TEXT_17);
    stringBuffer.append(genModel.getImportedName("java.util.HashMap"));
    stringBuffer.append(TEXT_18);
    stringBuffer.append(genModel.getImportedName("org.gecko.emf.osgi.constants.EMFNamespaces"));
    stringBuffer.append(TEXT_19);
    stringBuffer.append(genPackage.getPackageInterfaceName());
    stringBuffer.append(TEXT_20);
    stringBuffer.append(genModel.getImportedName("org.gecko.emf.osgi.constants.EMFNamespaces"));
    stringBuffer.append(TEXT_21);
    stringBuffer.append(genPackage.getPackageInterfaceName());
    stringBuffer.append(TEXT_22);
    stringBuffer.append(genModel.getImportedName("org.gecko.emf.osgi.constants.EMFNamespaces"));
    stringBuffer.append(TEXT_23);
    stringBuffer.append(genModel.getImportedName("org.gecko.emf.osgi.constants.EMFNamespaces"));
    stringBuffer.append(TEXT_24);
    stringBuffer.append(genModel.getImportedName("org.gecko.emf.osgi.constants.EMFNamespaces"));
    stringBuffer.append(TEXT_25);
    stringBuffer.append(genPackage.getFileExtension());
    stringBuffer.append(TEXT_26);
     if (genPackage.getContentTypeIdentifier() != null) {
    stringBuffer.append(TEXT_27);
    stringBuffer.append(genModel.getImportedName("org.gecko.emf.osgi.constants.EMFNamespaces"));
    stringBuffer.append(TEXT_28);
    stringBuffer.append(genPackage.getContentTypeIdentifier());
    stringBuffer.append(TEXT_26);
    }
    stringBuffer.append(TEXT_27);
    stringBuffer.append(genModel.getImportedName("org.gecko.emf.osgi.constants.EMFNamespaces"));
    stringBuffer.append(TEXT_29);
    stringBuffer.append(GeneratorHelper.getVersion(genPackage));
    stringBuffer.append(TEXT_30);
    genModel.emitSortedImports();
    return stringBuffer.toString();
  }
}
