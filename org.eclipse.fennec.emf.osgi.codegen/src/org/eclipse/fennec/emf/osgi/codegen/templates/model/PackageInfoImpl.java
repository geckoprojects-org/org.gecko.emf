package org.eclipse.fennec.emf.osgi.codegen.templates.model;

import org.eclipse.emf.codegen.ecore.genmodel.*;
import org.eclipse.fennec.emf.osgi.codegen.templates.model.helper.GeneratorHelper;

public class PackageInfoImpl
{
  protected static String nl;
  public static synchronized PackageInfoImpl create(String lineSeparator)
  {
    nl = lineSeparator;
    PackageInfoImpl result = new PackageInfoImpl();
    nl = null;
    return result;
  }

  public final String NL = nl == null ? (System.getProperties().getProperty("line.separator")) : nl;
  protected final String TEXT_1 = "";
  protected final String TEXT_2 = "/*";
  protected final String TEXT_3 = NL + " * ";
  protected final String TEXT_4 = NL + " */" + NL + "@org.osgi.annotation.bundle.Export" + NL + "@org.osgi.annotation.versioning.Version(\"";
  protected final String TEXT_5 = "\")" + NL + "package ";
  protected final String TEXT_6 = ".";
  protected final String TEXT_7 = ";";

  public String generate(Object argument)
  {
    final StringBuffer stringBuffer = new StringBuffer();
    final GenPackage genPackage = (GenPackage)((Object[])argument)[0];
    stringBuffer.append(TEXT_1);
    stringBuffer.append(TEXT_2);
    {GenBase copyrightHolder = argument instanceof GenBase ? (GenBase)argument : argument instanceof Object[] && ((Object[])argument)[0] instanceof GenBase ? (GenBase)((Object[])argument)[0] : null;
    if (copyrightHolder != null && copyrightHolder.hasCopyright()) {
    stringBuffer.append(TEXT_3);
    stringBuffer.append(copyrightHolder.getCopyright(copyrightHolder.getGenModel().getIndentation(stringBuffer)));
    }}
    stringBuffer.append(TEXT_4);
    stringBuffer.append(GeneratorHelper.getVersion(genPackage));
    stringBuffer.append(TEXT_5);
    stringBuffer.append(genPackage.getInterfacePackageName());
    stringBuffer.append(TEXT_6);
    stringBuffer.append(genPackage.getClassPackageSuffix());
    stringBuffer.append(TEXT_7);
    return stringBuffer.toString();
  }
}
