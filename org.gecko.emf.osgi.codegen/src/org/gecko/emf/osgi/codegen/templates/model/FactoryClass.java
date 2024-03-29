package org.gecko.emf.osgi.codegen.templates.model;

import java.util.*;
import org.eclipse.emf.codegen.ecore.genmodel.*;
import org.eclipse.emf.codegen.ecore.genmodel.util.GenModelUtil;

public class FactoryClass
{
  protected static String nl;
  public static synchronized FactoryClass create(String lineSeparator)
  {
    nl = lineSeparator;
    FactoryClass result = new FactoryClass();
    nl = null;
    return result;
  }

  public final String NL = nl == null ? (System.getProperties().getProperty("line.separator")) : nl;
  protected final String TEXT_1 = "";
  protected final String TEXT_2 = "/*";
  protected final String TEXT_3 = NL + " * ";
  protected final String TEXT_4 = NL + " */";
  protected final String TEXT_5 = NL + "package ";
  protected final String TEXT_6 = ";";
  protected final String TEXT_7 = NL;
  protected final String TEXT_8 = NL + "/**" + NL + " * <!-- begin-user-doc -->" + NL + " * The <b>Factory</b> for the model." + NL + " * It provides a create method for each non-abstract class of the model." + NL + " * <!-- end-user-doc -->";
  protected final String TEXT_9 = NL + " * @see ";
  protected final String TEXT_10 = NL + " * @generated" + NL + " */";
  protected final String TEXT_11 = NL + "/**" + NL + " * <!-- begin-user-doc -->" + NL + " * An implementation of the model <b>Factory</b>." + NL + " * <!-- end-user-doc -->";
  protected final String TEXT_12 = NL + "@Deprecated";
  protected final String TEXT_13 = NL + "@SuppressWarnings(\"deprecation\")";
  protected final String TEXT_14 = NL + "public class ";
  protected final String TEXT_15 = " extends ";
  protected final String TEXT_16 = " implements ";
  protected final String TEXT_17 = NL + "@";
  protected final String TEXT_18 = NL + "public interface ";
  protected final String TEXT_19 = NL + "{";
  protected final String TEXT_20 = NL + "\t/**" + NL + "\t * <!-- begin-user-doc -->" + NL + "\t * <!-- end-user-doc -->" + NL + "\t * @generated" + NL + "\t */" + NL + "\t";
  protected final String TEXT_21 = " copyright = ";
  protected final String TEXT_22 = NL + "\t/**" + NL + "\t * The singleton instance of the factory." + NL + "\t * <!-- begin-user-doc -->" + NL + "\t * <!-- end-user-doc -->" + NL + "\t * @generated" + NL + "\t */" + NL + "\t";
  protected final String TEXT_23 = " eINSTANCE = init();" + NL;
  protected final String TEXT_24 = " INSTANCE = ";
  protected final String TEXT_25 = ".eINSTANCE;" + NL;
  protected final String TEXT_26 = " eINSTANCE = ";
  protected final String TEXT_27 = ".init();" + NL;
  protected final String TEXT_28 = NL + "\t/**" + NL + "\t * Creates the default factory implementation." + NL + "\t * <!-- begin-user-doc -->" + NL + "\t * <!-- end-user-doc -->" + NL + "\t * @generated" + NL + "\t */";
  protected final String TEXT_29 = NL + "\tpublic static ";
  protected final String TEXT_30 = " init()" + NL + "\t{" + NL + "\t\ttry" + NL + "\t\t{" + NL + "\t\t\t";
  protected final String TEXT_31 = " the";
  protected final String TEXT_32 = " = (";
  protected final String TEXT_33 = ")";
  protected final String TEXT_34 = ".Registry.INSTANCE.getEFactory(";
  protected final String TEXT_35 = ".eNS_URI);" + NL + "\t\t\tif (the";
  protected final String TEXT_36 = " != null)" + NL + "\t\t\t{" + NL + "\t\t\t\treturn the";
  protected final String TEXT_37 = ";" + NL + "\t\t\t}" + NL + "\t\t}" + NL + "\t\tcatch (Exception exception)" + NL + "\t\t{" + NL + "\t\t\t";
  protected final String TEXT_38 = ".INSTANCE.log(exception);" + NL + "\t\t}" + NL + "\t\treturn new ";
  protected final String TEXT_39 = "();" + NL + "\t}" + NL + "" + NL + "\t/**" + NL + "\t * Creates an instance of the factory." + NL + "\t * <!-- begin-user-doc -->" + NL + "\t * <!-- end-user-doc -->" + NL + "\t * @generated" + NL + "\t */" + NL + "\tpublic ";
  protected final String TEXT_40 = "()" + NL + "\t{" + NL + "\t\tsuper();" + NL + "\t}" + NL + "" + NL + "\t/**" + NL + "\t * <!-- begin-user-doc -->" + NL + "\t * <!-- end-user-doc -->" + NL + "\t * @generated" + NL + "\t */";
  protected final String TEXT_41 = NL + "\t@Override";
  protected final String TEXT_42 = NL + "\tpublic EObject create(EClass eClass)" + NL + "\t{" + NL + "\t\tswitch (eClass.getClassifierID())" + NL + "\t\t{";
  protected final String TEXT_43 = NL + "\t\t\tcase ";
  protected final String TEXT_44 = ".";
  protected final String TEXT_45 = ": return ";
  protected final String TEXT_46 = "create";
  protected final String TEXT_47 = "();";
  protected final String TEXT_48 = NL + "\t\t\tdefault:" + NL + "\t\t\t\tthrow new IllegalArgumentException(\"The class '\" + eClass.getName() + \"' is not a valid classifier\");";
  protected final String TEXT_49 = NL + "\t\t}" + NL + "\t}" + NL;
  protected final String TEXT_50 = NL + "\t/**" + NL + "\t * <!-- begin-user-doc -->" + NL + "\t * <!-- end-user-doc -->" + NL + "\t * @generated" + NL + "\t */";
  protected final String TEXT_51 = NL + "\tpublic Object createFromString(";
  protected final String TEXT_52 = " eDataType, String initialValue)" + NL + "\t{" + NL + "\t\tswitch (eDataType.getClassifierID())" + NL + "\t\t{";
  protected final String TEXT_53 = ":" + NL + "\t\t\t\treturn create";
  protected final String TEXT_54 = "FromString(eDataType, initialValue);";
  protected final String TEXT_55 = NL + "\t\t\tdefault:" + NL + "\t\t\t\tthrow new IllegalArgumentException(\"The datatype '\" + eDataType.getName() + \"' is not a valid classifier\");";
  protected final String TEXT_56 = NL + "\t\t}" + NL + "\t}" + NL + "" + NL + "\t/**" + NL + "\t * <!-- begin-user-doc -->" + NL + "\t * <!-- end-user-doc -->" + NL + "\t * @generated" + NL + "\t */";
  protected final String TEXT_57 = NL + "\tpublic String convertToString(";
  protected final String TEXT_58 = " eDataType, Object instanceValue)" + NL + "\t{" + NL + "\t\tswitch (eDataType.getClassifierID())" + NL + "\t\t{";
  protected final String TEXT_59 = ":" + NL + "\t\t\t\treturn convert";
  protected final String TEXT_60 = "ToString(eDataType, instanceValue);";
  protected final String TEXT_61 = NL + "\t/**" + NL + "\t * <!-- begin-user-doc -->" + NL + "\t * <!-- end-user-doc -->";
  protected final String TEXT_62 = NL + "\t * ";
  protected final String TEXT_63 = NL + "\t * @generated" + NL + "\t */";
  protected final String TEXT_64 = NL + "\t@Deprecated";
  protected final String TEXT_65 = NL + "\tpublic ";
  protected final String TEXT_66 = " create";
  protected final String TEXT_67 = "()" + NL + "\t{";
  protected final String TEXT_68 = NL + "\t\t";
  protected final String TEXT_69 = " ";
  protected final String TEXT_70 = " =";
  protected final String TEXT_71 = "return";
  protected final String TEXT_72 = "super.create(";
  protected final String TEXT_73 = ");";
  protected final String TEXT_74 = " new ";
  protected final String TEXT_75 = "()";
  protected final String TEXT_76 = "{}";
  protected final String TEXT_77 = NL + "\t\treturn ";
  protected final String TEXT_78 = NL + "\t}" + NL;
  protected final String TEXT_79 = NL + "\t@SuppressWarnings(\"unchecked\")";
  protected final String TEXT_80 = "(";
  protected final String TEXT_81 = "final ";
  protected final String TEXT_82 = "String ";
  protected final String TEXT_83 = "it";
  protected final String TEXT_84 = "literal";
  protected final String TEXT_85 = ")" + NL + "\t{";
  protected final String TEXT_86 = " result = ";
  protected final String TEXT_87 = ".get(literal);" + NL + "\t\tif (result == null) throw new IllegalArgumentException(\"The value '\" + literal + \"' is not a valid enumerator of '\" + ";
  protected final String TEXT_88 = ".getName() + \"'\");";
  protected final String TEXT_89 = NL + "\t\treturn result;";
  protected final String TEXT_90 = NL + "\t\treturn new ";
  protected final String TEXT_91 = "(create";
  protected final String TEXT_92 = "(literal));";
  protected final String TEXT_93 = NL + "\t\treturn create";
  protected final String TEXT_94 = "(literal);";
  protected final String TEXT_95 = ".create";
  protected final String TEXT_96 = ".createFromString(";
  protected final String TEXT_97 = ", literal);";
  protected final String TEXT_98 = NL + "\t\tif (literal == null) return null;" + NL + "\t\t";
  protected final String TEXT_99 = " result = new ";
  protected final String TEXT_100 = "<";
  protected final String TEXT_101 = ">";
  protected final String TEXT_102 = NL + "\t\tfor (";
  protected final String TEXT_103 = " stringTokenizer = new ";
  protected final String TEXT_104 = "(literal); stringTokenizer.hasMoreTokens(); )";
  protected final String TEXT_105 = NL + "\t\tfor (String item : split(literal))";
  protected final String TEXT_106 = NL + "\t\t{";
  protected final String TEXT_107 = NL + "\t\t\tString item = stringTokenizer.nextToken();";
  protected final String TEXT_108 = NL + "\t\t\tresult.add(create";
  protected final String TEXT_109 = "(item));";
  protected final String TEXT_110 = "FromString(";
  protected final String TEXT_111 = ", item));";
  protected final String TEXT_112 = NL + "\t\t\tresult.add(";
  protected final String TEXT_113 = NL + "\t\t}" + NL + "\t\treturn result;";
  protected final String TEXT_114 = NL + "\t\tif (literal == null) return ";
  protected final String TEXT_115 = ";" + NL + "\t\t";
  protected final String TEXT_116 = ";" + NL + "\t\tRuntimeException exception = null;";
  protected final String TEXT_117 = NL + "\t\ttry" + NL + "\t\t{";
  protected final String TEXT_118 = NL + "\t\t\tresult = create";
  protected final String TEXT_119 = NL + "\t\t\tresult = (";
  protected final String TEXT_120 = ")create";
  protected final String TEXT_121 = NL + "\t\t\tresult = ";
  protected final String TEXT_122 = NL + "\t\t\tif (";
  protected final String TEXT_123 = "result != null && ";
  protected final String TEXT_124 = ".INSTANCE.validate(";
  protected final String TEXT_125 = ", ";
  protected final String TEXT_126 = "new ";
  protected final String TEXT_127 = "(result)";
  protected final String TEXT_128 = "result";
  protected final String TEXT_129 = ", null, null))" + NL + "\t\t\t{" + NL + "\t\t\t\treturn result;" + NL + "\t\t\t}" + NL + "\t\t}" + NL + "\t\tcatch (RuntimeException e)" + NL + "\t\t{" + NL + "\t\t\texception = e;" + NL + "\t\t}";
  protected final String TEXT_130 = NL + "\t\tif (";
  protected final String TEXT_131 = "result != null || ";
  protected final String TEXT_132 = "exception == null) return result;" + NL + "    " + NL + "\t\tthrow exception;";
  protected final String TEXT_133 = NL + "\t\treturn (";
  protected final String TEXT_134 = ")super.createFromString(literal);";
  protected final String TEXT_135 = NL + "\t\t// TODO: implement this method" + NL + "\t\t// Ensure that you remove @generated or mark it @generated NOT" + NL + "\t\tthrow new ";
  protected final String TEXT_136 = NL + "\t\treturn ((";
  protected final String TEXT_137 = ")super.createFromString(";
  protected final String TEXT_138 = ", literal)).";
  protected final String TEXT_139 = "super.createFromString(";
  protected final String TEXT_140 = " eDataType, String initialValue)" + NL + "\t{";
  protected final String TEXT_141 = "(initialValue);";
  protected final String TEXT_142 = ".get(initialValue);" + NL + "\t\tif (result == null) throw new IllegalArgumentException(\"The value '\" + initialValue + \"' is not a valid enumerator of '\" + eDataType.getName() + \"'\");";
  protected final String TEXT_143 = ", initialValue);";
  protected final String TEXT_144 = NL + "\t\tif (initialValue == null) return null;" + NL + "\t\t";
  protected final String TEXT_145 = "(initialValue); stringTokenizer.hasMoreTokens(); )";
  protected final String TEXT_146 = NL + "\t\tfor (String item : split(initialValue))";
  protected final String TEXT_147 = "(initialValue));";
  protected final String TEXT_148 = " result = null;" + NL + "\t\tRuntimeException exception = null;";
  protected final String TEXT_149 = NL + "\t\t\tif (result != null && ";
  protected final String TEXT_150 = ".INSTANCE.validate(eDataType, result, null, null))" + NL + "\t\t\t{" + NL + "\t\t\t\treturn result;" + NL + "\t\t\t}" + NL + "\t\t}" + NL + "\t\tcatch (RuntimeException e)" + NL + "\t\t{" + NL + "\t\t\texception = e;" + NL + "\t\t}";
  protected final String TEXT_151 = NL + "\t\tif (result != null || exception == null) return result;" + NL + "    " + NL + "\t\tthrow exception;";
  protected final String TEXT_152 = "super.createFromString(initialValue);";
  protected final String TEXT_153 = "super.createFromString(eDataType, initialValue);";
  protected final String TEXT_154 = NL + "\tpublic String convert";
  protected final String TEXT_155 = "instanceValue";
  protected final String TEXT_156 = NL + "\t\treturn instanceValue == null ? null : instanceValue.toString();";
  protected final String TEXT_157 = NL + "\t\treturn instanceValue == null ? null : convert";
  protected final String TEXT_158 = "(instanceValue";
  protected final String TEXT_159 = NL + "\t\treturn convert";
  protected final String TEXT_160 = "(instanceValue);";
  protected final String TEXT_161 = ".convert";
  protected final String TEXT_162 = ".convertToString(";
  protected final String TEXT_163 = ", instanceValue);";
  protected final String TEXT_164 = NL + "\t\tif (instanceValue == null) return null;" + NL + "\t\tif (instanceValue.isEmpty()) return \"\";" + NL + "\t\t";
  protected final String TEXT_165 = " i = instanceValue.iterator(); i.hasNext(); )";
  protected final String TEXT_166 = " item : instanceValue)";
  protected final String TEXT_167 = NL + "\t\t\tresult.append(convert";
  protected final String TEXT_168 = "((";
  protected final String TEXT_169 = "));";
  protected final String TEXT_170 = "ToString(";
  protected final String TEXT_171 = NL + "\t\t\tresult.append(";
  protected final String TEXT_172 = NL + "\t\t\tresult.append(' ');" + NL + "\t\t}" + NL + "\t\treturn result.substring(0, result.length() - 1);";
  protected final String TEXT_173 = NL + "\t\tif (instanceValue == null) return null;";
  protected final String TEXT_174 = ".isInstance(instanceValue))" + NL + "\t\t{" + NL + "\t\t\ttry" + NL + "\t\t\t{";
  protected final String TEXT_175 = NL + "\t\t\t\tString value = convert";
  protected final String TEXT_176 = "(((";
  protected final String TEXT_177 = ")instanceValue).";
  protected final String TEXT_178 = "());";
  protected final String TEXT_179 = ")instanceValue);";
  protected final String TEXT_180 = NL + "\t\t\t\tString value = ";
  protected final String TEXT_181 = NL + "\t\t\t\tif (value != null) return value;" + NL + "\t\t\t}" + NL + "\t\t\tcatch (Exception e)" + NL + "\t\t\t{" + NL + "\t\t\t\t// Keep trying other member types until all have failed." + NL + "\t\t\t}" + NL + "\t\t}";
  protected final String TEXT_182 = NL + "\t\t\tString value = convert";
  protected final String TEXT_183 = "(instanceValue)";
  protected final String TEXT_184 = NL + "\t\t\tString value = ";
  protected final String TEXT_185 = NL + "\t\t\tif (value != null) return value;" + NL + "\t\t}" + NL + "\t\tcatch (Exception e)" + NL + "\t\t{" + NL + "\t\t\t// Keep trying other member types until all have failed." + NL + "\t\t}";
  protected final String TEXT_186 = NL + "\t\tthrow new IllegalArgumentException(\"Invalid value: '\"+instanceValue+\"' for datatype :\"+";
  protected final String TEXT_187 = ".getName());";
  protected final String TEXT_188 = NL + "\t\treturn super.convertToString(instanceValue);";
  protected final String TEXT_189 = NL + "\t\treturn super.convertToString(";
  protected final String TEXT_190 = ", new ";
  protected final String TEXT_191 = "(instanceValue));";
  protected final String TEXT_192 = " eDataType, Object instanceValue)" + NL + "\t{";
  protected final String TEXT_193 = NL + "\t\tif (instanceValue == null) return null;" + NL + "\t\t";
  protected final String TEXT_194 = " list = (";
  protected final String TEXT_195 = ")instanceValue;" + NL + "\t\tif (list.isEmpty()) return \"\";" + NL + "\t\t";
  protected final String TEXT_196 = " i = list.iterator(); i.hasNext(); )";
  protected final String TEXT_197 = " item : list)";
  protected final String TEXT_198 = ")instanceValue)";
  protected final String TEXT_199 = NL + "\t\tthrow new IllegalArgumentException(\"Invalid value: '\"+instanceValue+\"' for datatype :\"+eDataType.getName());";
  protected final String TEXT_200 = ")instanceValue";
  protected final String TEXT_201 = ").";
  protected final String TEXT_202 = NL + "\t\treturn super.convertToString(eDataType, instanceValue);";
  protected final String TEXT_203 = NL + "\t/**" + NL + "\t * Returns a new object of class '<em>";
  protected final String TEXT_204 = "</em>'." + NL + "\t * <!-- begin-user-doc -->" + NL + "\t * <!-- end-user-doc -->" + NL + "\t * @return a new object of class '<em>";
  protected final String TEXT_205 = "</em>'.";
  protected final String TEXT_206 = NL + "\t";
  protected final String TEXT_207 = "();" + NL;
  protected final String TEXT_208 = NL + "\t/**" + NL + "\t * Returns an instance of data type '<em>";
  protected final String TEXT_209 = "</em>' corresponding the given literal." + NL + "\t * <!-- begin-user-doc -->" + NL + "\t * <!-- end-user-doc -->" + NL + "\t * @param literal a literal of the data type." + NL + "\t * @return a new instance value of the data type.";
  protected final String TEXT_210 = "(String literal);" + NL + "" + NL + "\t/**" + NL + "\t * Returns a literal representation of an instance of data type '<em>";
  protected final String TEXT_211 = "</em>'." + NL + "\t * <!-- begin-user-doc -->" + NL + "\t * <!-- end-user-doc -->" + NL + "\t * @param instanceValue an instance value of the data type." + NL + "\t * @return a literal representation of the instance value.";
  protected final String TEXT_212 = NL + "\tString convert";
  protected final String TEXT_213 = " instanceValue);" + NL;
  protected final String TEXT_214 = NL + "\t/**" + NL + "\t * Returns the package supported by this factory." + NL + "\t * <!-- begin-user-doc -->" + NL + "\t * <!-- end-user-doc -->" + NL + "\t * @return the package supported by this factory." + NL + "\t * @generated" + NL + "\t */" + NL + "\t";
  protected final String TEXT_215 = " get";
  protected final String TEXT_216 = "()" + NL + "\t{" + NL + "\t\treturn (";
  protected final String TEXT_217 = ")getEPackage();" + NL + "\t}" + NL;
  protected final String TEXT_218 = NL + "\t/**" + NL + "\t * <!-- begin-user-doc -->" + NL + "\t * <!-- end-user-doc -->" + NL + "\t * @deprecated" + NL + "\t * @generated" + NL + "\t */";
  protected final String TEXT_219 = " getPackage()" + NL + "\t{" + NL + "\t\treturn ";
  protected final String TEXT_220 = ".eINSTANCE;" + NL + "\t}" + NL;
  protected final String TEXT_221 = NL + "} //";

  public String generate(Object argument)
  {
    final StringBuffer stringBuffer = new StringBuffer();
    
/**
 * Copyright (c) 2002-2010 IBM Corporation and others.
 * All rights reserved.   This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v20.html
 *
 * Contributors:
 *   IBM - Initial API and implementation
 */

    GenPackage genPackage = (GenPackage)((Object[])argument)[0]; GenModel genModel=genPackage.getGenModel(); /* Trick to import java.util.* without warnings */Iterator.class.getName();
    final boolean isJDK50 = genModel.getComplianceLevel().getValue() >= GenJDKLevel.JDK50;
    boolean isInterface = Boolean.TRUE.equals(((Object[])argument)[1]); boolean isImplementation = Boolean.TRUE.equals(((Object[])argument)[2]); boolean useInterfaceOverrideAnnotation = genModel.useInterfaceOverrideAnnotation() && !(isInterface && isImplementation);
    String publicStaticFinalFlag = isImplementation ? "public static final " : "";
    stringBuffer.append(TEXT_1);
    stringBuffer.append(TEXT_2);
    {GenBase copyrightHolder = argument instanceof GenBase ? (GenBase)argument : argument instanceof Object[] && ((Object[])argument)[0] instanceof GenBase ? (GenBase)((Object[])argument)[0] : null;
    if (copyrightHolder != null && copyrightHolder.hasCopyright()) {
    stringBuffer.append(TEXT_3);
    stringBuffer.append(copyrightHolder.getCopyright(copyrightHolder.getGenModel().getIndentation(stringBuffer)));
    }}
    stringBuffer.append(TEXT_4);
    if (isInterface || genModel.isSuppressInterfaces()) {
    stringBuffer.append(TEXT_5);
    stringBuffer.append(genPackage.getReflectionPackageName());
    stringBuffer.append(TEXT_6);
    } else {
    stringBuffer.append(TEXT_5);
    stringBuffer.append(genPackage.getClassPackageName());
    stringBuffer.append(TEXT_6);
    }
    stringBuffer.append(TEXT_7);
    if (isImplementation) {
    genModel.addPseudoImport("org.eclipse.emf.ecore.impl.MinimalEObjectImpl.Container");
    genModel.addPseudoImport("org.eclipse.emf.ecore.impl.MinimalEObjectImpl.Container.Dynamic");
    genModel.addImport("org.eclipse.emf.ecore.EClass");
    genModel.addImport("org.eclipse.emf.ecore.EObject");
    if (!genPackage.hasJavaLangConflict() && !genPackage.hasInterfaceImplConflict() && !genPackage.getClassPackageName().equals(genPackage.getInterfacePackageName())) genModel.addImport(genPackage.getInterfacePackageName() + ".*");
    }
    genModel.markImportLocation(stringBuffer);
    stringBuffer.append(TEXT_7);
    if (isInterface) {
    stringBuffer.append(TEXT_8);
    if (!genModel.isSuppressEMFMetaData()) {
    stringBuffer.append(TEXT_9);
    stringBuffer.append(genPackage.getQualifiedPackageInterfaceName());
    }
    if (genPackage.hasAPITags()) {
    stringBuffer.append(TEXT_3);
    stringBuffer.append(genPackage.getAPITags(genModel.getIndentation(stringBuffer)));
    }
    stringBuffer.append(TEXT_10);
    } else {
    stringBuffer.append(TEXT_11);
    if (genPackage.hasAPITags()) {
    stringBuffer.append(TEXT_3);
    stringBuffer.append(genPackage.getAPITags(genModel.getIndentation(stringBuffer)));
    }
    stringBuffer.append(TEXT_10);
    }
    if (isJDK50 && genPackage.hasAPIDeprecatedTag()) {
    stringBuffer.append(TEXT_12);
    }
    if (isImplementation) {
    if (isJDK50 && !genPackage.hasAPIDeprecatedTag()) { List<GenClassifier> genClassifiers = new ArrayList<GenClassifier>(genPackage.getGenClassifiers()); for (Iterator<GenClassifier> i = genClassifiers.iterator(); i.hasNext(); ) { GenClassifier genClassifier = i.next(); if (genClassifier instanceof GenClass && ((GenClass)genClassifier).isAbstract()) i.remove(); } if (GenModelUtil.hasAPIDeprecatedTag(genClassifiers)) {
    stringBuffer.append(TEXT_13);
    }}
    stringBuffer.append(TEXT_14);
    stringBuffer.append(genPackage.getFactoryClassName());
    stringBuffer.append(TEXT_15);
    stringBuffer.append(genModel.getImportedName("org.eclipse.emf.ecore.impl.EFactoryImpl"));
    if (!genModel.isSuppressInterfaces()) {
    stringBuffer.append(TEXT_16);
    stringBuffer.append(genPackage.getImportedFactoryInterfaceName());
    }
    } else {
    stringBuffer.append(TEXT_17);
    stringBuffer.append(genModel.getImportedName("org.osgi.annotation.versioning.ProviderType"));
    stringBuffer.append(TEXT_18);
    stringBuffer.append(genPackage.getFactoryInterfaceName());
    if (!genModel.isSuppressEMFMetaData()) {
    stringBuffer.append(TEXT_15);
    stringBuffer.append(genModel.getImportedName("org.eclipse.emf.ecore.EFactory"));
    }
    }
    stringBuffer.append(TEXT_19);
    if (genModel.hasCopyrightField()) {
    stringBuffer.append(TEXT_20);
    stringBuffer.append(publicStaticFinalFlag);
    stringBuffer.append(genModel.getImportedName("java.lang.String"));
    stringBuffer.append(TEXT_21);
    stringBuffer.append(genModel.getCopyrightFieldLiteral());
    stringBuffer.append(TEXT_6);
    stringBuffer.append(genModel.getNonNLS());
    stringBuffer.append(TEXT_7);
    }
    if (isImplementation && (genModel.isSuppressEMFMetaData() || genModel.isSuppressInterfaces())) {
    stringBuffer.append(TEXT_22);
    stringBuffer.append(publicStaticFinalFlag);
    stringBuffer.append(genPackage.getFactoryClassName());
    stringBuffer.append(TEXT_23);
    }
    if (isInterface && genModel.isSuppressEMFMetaData()) {
    stringBuffer.append(TEXT_22);
    stringBuffer.append(publicStaticFinalFlag);
    stringBuffer.append(genPackage.getFactoryInterfaceName());
    stringBuffer.append(TEXT_24);
    stringBuffer.append(genPackage.getQualifiedFactoryClassName());
    stringBuffer.append(TEXT_25);
    } else if (isInterface && !genModel.isSuppressInterfaces()) {
    stringBuffer.append(TEXT_22);
    stringBuffer.append(publicStaticFinalFlag);
    stringBuffer.append(genPackage.getFactoryInterfaceName());
    stringBuffer.append(TEXT_26);
    stringBuffer.append(genPackage.getQualifiedFactoryClassName());
    stringBuffer.append(TEXT_27);
    }
    if (isImplementation) {
    stringBuffer.append(TEXT_28);
    String factoryType = genModel.isSuppressEMFMetaData() ? genPackage.getFactoryClassName() : genPackage.getImportedFactoryInterfaceName();
    stringBuffer.append(TEXT_29);
    stringBuffer.append(factoryType);
    stringBuffer.append(TEXT_30);
    stringBuffer.append(factoryType);
    stringBuffer.append(TEXT_31);
    stringBuffer.append(genPackage.getFactoryName());
    stringBuffer.append(TEXT_32);
    stringBuffer.append(factoryType);
    stringBuffer.append(TEXT_33);
    stringBuffer.append(genModel.getImportedName("org.eclipse.emf.ecore.EPackage"));
    stringBuffer.append(TEXT_34);
    stringBuffer.append(genPackage.getPackageInterfaceName());
    stringBuffer.append(TEXT_35);
    stringBuffer.append(genPackage.getFactoryName());
    stringBuffer.append(TEXT_36);
    stringBuffer.append(genPackage.getFactoryName());
    stringBuffer.append(TEXT_37);
    stringBuffer.append(genModel.getImportedName("org.eclipse.emf.ecore.plugin.EcorePlugin"));
    stringBuffer.append(TEXT_38);
    stringBuffer.append(genPackage.getImportedFactoryClassName());
    stringBuffer.append(TEXT_39);
    stringBuffer.append(genPackage.getFactoryClassName());
    stringBuffer.append(TEXT_40);
    if (genModel.useClassOverrideAnnotation()) {
    stringBuffer.append(TEXT_41);
    }
    stringBuffer.append(TEXT_42);
    for (GenClass genClass : genPackage.getGenClasses()) {
    if (!genClass.isAbstract()) {
    stringBuffer.append(TEXT_43);
    stringBuffer.append(genPackage.getImportedPackageInterfaceName());
    stringBuffer.append(TEXT_44);
    stringBuffer.append(genClass.getClassifierID());
    stringBuffer.append(TEXT_45);
    stringBuffer.append(!genClass.isEObjectExtension() ? "(EObject)" : "" );
    stringBuffer.append(TEXT_46);
    stringBuffer.append(genClass.getName());
    stringBuffer.append(TEXT_47);
    }
    }
    stringBuffer.append(TEXT_48);
    stringBuffer.append(genModel.getNonNLS());
    stringBuffer.append(genModel.getNonNLS(2));
    stringBuffer.append(TEXT_49);
    if (!genPackage.getAllGenDataTypes().isEmpty()) {
    stringBuffer.append(TEXT_50);
    if (genModel.useClassOverrideAnnotation()) {
    stringBuffer.append(TEXT_41);
    }
    stringBuffer.append(TEXT_51);
    stringBuffer.append(genModel.getImportedName("org.eclipse.emf.ecore.EDataType"));
    stringBuffer.append(TEXT_52);
    for (GenDataType genDataType : genPackage.getAllGenDataTypes()) {
    if (genDataType.isSerializable()) {
    stringBuffer.append(TEXT_43);
    stringBuffer.append(genPackage.getImportedPackageInterfaceName());
    stringBuffer.append(TEXT_44);
    stringBuffer.append(genDataType.getClassifierID());
    stringBuffer.append(TEXT_53);
    stringBuffer.append(genDataType.getName());
    stringBuffer.append(TEXT_54);
    }
    }
    stringBuffer.append(TEXT_55);
    stringBuffer.append(genModel.getNonNLS());
    stringBuffer.append(genModel.getNonNLS(2));
    stringBuffer.append(TEXT_56);
    if (genModel.useClassOverrideAnnotation()) {
    stringBuffer.append(TEXT_41);
    }
    stringBuffer.append(TEXT_57);
    stringBuffer.append(genModel.getImportedName("org.eclipse.emf.ecore.EDataType"));
    stringBuffer.append(TEXT_58);
    for (GenDataType genDataType : genPackage.getAllGenDataTypes()) {
    if (genDataType.isSerializable()) {
    stringBuffer.append(TEXT_43);
    stringBuffer.append(genPackage.getImportedPackageInterfaceName());
    stringBuffer.append(TEXT_44);
    stringBuffer.append(genDataType.getClassifierID());
    stringBuffer.append(TEXT_59);
    stringBuffer.append(genDataType.getName());
    stringBuffer.append(TEXT_60);
    }
    }
    stringBuffer.append(TEXT_55);
    stringBuffer.append(genModel.getNonNLS());
    stringBuffer.append(genModel.getNonNLS(2));
    stringBuffer.append(TEXT_49);
    }
    for (GenClass genClass : genPackage.getGenClasses()) {
    if (!genClass.isAbstract()) { boolean isUnnecessaryAssignmentBeforeReturn = genModel.getCodeStyle().contains(GenCodeStyle.UNNECESSARY_ASSIGNMENT_BEFORE_RETURN);
    stringBuffer.append(TEXT_61);
    if (genClass.hasAPITags()) {
    stringBuffer.append(TEXT_62);
    stringBuffer.append(genClass.getAPITags(genModel.getIndentation(stringBuffer)));
    }
    stringBuffer.append(TEXT_63);
    if (isJDK50 && genClass.hasAPIDeprecatedTag()) {
    stringBuffer.append(TEXT_64);
    }
    if (useInterfaceOverrideAnnotation && !genClass.isMapEntry()) {
    stringBuffer.append(TEXT_41);
    }
    stringBuffer.append(TEXT_65);
    stringBuffer.append(genClass.getTypeParameters());
    stringBuffer.append(genClass.getImportedInterfaceName());
    stringBuffer.append(genClass.getInterfaceTypeArguments());
    stringBuffer.append(TEXT_66);
    stringBuffer.append(genClass.getName());
    stringBuffer.append(TEXT_67);
    if (genClass.isDynamic()) {
    stringBuffer.append(TEXT_68);
    if (!isUnnecessaryAssignmentBeforeReturn) {
    stringBuffer.append(genClass.getImportedInterfaceName());
    stringBuffer.append(genClass.getInterfaceTypeArguments());
    stringBuffer.append(TEXT_69);
    stringBuffer.append(genClass.getSafeUncapName());
    stringBuffer.append(TEXT_70);
    } else {
    stringBuffer.append(TEXT_71);
    }
    stringBuffer.append(TEXT_69);
    stringBuffer.append(genClass.getCastFromEObject());
    stringBuffer.append(TEXT_72);
    stringBuffer.append(genClass.getQualifiedClassifierAccessor());
    stringBuffer.append(TEXT_73);
    } else {
    stringBuffer.append(TEXT_68);
    if (!isUnnecessaryAssignmentBeforeReturn) {
    stringBuffer.append(genClass.getImportedClassName());
    stringBuffer.append(genClass.getClassTypeArguments());
    stringBuffer.append(TEXT_69);
    stringBuffer.append(genClass.getSafeUncapName());
    stringBuffer.append(TEXT_70);
    } else {
    stringBuffer.append(TEXT_71);
    }
    stringBuffer.append(TEXT_74);
    stringBuffer.append(genClass.getImportedClassName());
    stringBuffer.append(genClass.getClassTypeArguments());
    stringBuffer.append(TEXT_75);
    if (genModel.isSuppressInterfaces() && !genPackage.getReflectionPackageName().equals(genPackage.getInterfacePackageName())) {
    stringBuffer.append(TEXT_76);
    }
    stringBuffer.append(TEXT_6);
    }
    if (!isUnnecessaryAssignmentBeforeReturn) {
    stringBuffer.append(TEXT_77);
    stringBuffer.append(genClass.getSafeUncapName());
    stringBuffer.append(TEXT_6);
    }
    stringBuffer.append(TEXT_78);
    }
    }
    for (GenDataType genDataType : genPackage.getAllGenDataTypes()) {
    if (genDataType.isSerializable()) {
    if (genPackage.isDataTypeConverters() || genDataType.hasCreatorBody()) { String eDataType = genDataType.getQualifiedClassifierAccessor();
    stringBuffer.append(TEXT_61);
    if (genDataType.hasAPITags()) {
    stringBuffer.append(TEXT_62);
    stringBuffer.append(genDataType.getAPITags(genModel.getIndentation(stringBuffer)));
    }
    stringBuffer.append(TEXT_63);
    if (genModel.useGenerics() && genDataType.isUncheckedCast() && !genDataType.hasCreatorBody()) {
    stringBuffer.append(TEXT_79);
    }
    if (isJDK50 && genDataType.hasAPIDeprecatedTag()) {
    stringBuffer.append(TEXT_64);
    }
    if (genPackage.isDataTypeConverters() && useInterfaceOverrideAnnotation) {
    stringBuffer.append(TEXT_41);
    }
    stringBuffer.append(TEXT_65);
    stringBuffer.append(genDataType.getImportedParameterizedInstanceClassName());
    stringBuffer.append(TEXT_66);
    stringBuffer.append(genDataType.getName());
    stringBuffer.append(TEXT_80);
    if (genDataType.hasCreatorBody()) {
    stringBuffer.append(TEXT_81);
    }
    stringBuffer.append(TEXT_82);
    if (genDataType.hasCreatorBody()) {
    stringBuffer.append(TEXT_83);
    } else {
    stringBuffer.append(TEXT_84);
    }
    stringBuffer.append(TEXT_85);
    if (genDataType.hasCreatorBody()) {
    stringBuffer.append(TEXT_68);
    stringBuffer.append(genDataType.getCreatorBody(genModel.getIndentation(stringBuffer)));
    } else if (genDataType instanceof GenEnum) {
    stringBuffer.append(TEXT_68);
    stringBuffer.append(genDataType.getImportedInstanceClassName());
    stringBuffer.append(TEXT_86);
    stringBuffer.append(genDataType.getImportedInstanceClassName());
    stringBuffer.append(TEXT_87);
    stringBuffer.append(eDataType);
    stringBuffer.append(TEXT_88);
    stringBuffer.append(genModel.getNonNLS());
    stringBuffer.append(genModel.getNonNLS(2));
    stringBuffer.append(genModel.getNonNLS(3));
    stringBuffer.append(TEXT_89);
    } else if (genDataType.getBaseType() != null) { GenDataType genBaseType = genDataType.getBaseType(); boolean isPrimitiveConversion = !genDataType.isPrimitiveType() && genBaseType.isPrimitiveType();
    if (genBaseType.getGenPackage() == genPackage) {
    if (isPrimitiveConversion && !isJDK50) {
    stringBuffer.append(TEXT_90);
    stringBuffer.append(genDataType.getObjectInstanceClassName());
    stringBuffer.append(TEXT_91);
    stringBuffer.append(genBaseType.getName());
    stringBuffer.append(TEXT_92);
    } else {
    stringBuffer.append(TEXT_93);
    stringBuffer.append(genBaseType.getName());
    stringBuffer.append(TEXT_94);
    }
    } else if (genBaseType.getGenPackage().isDataTypeConverters()) {
    if (isPrimitiveConversion && !isJDK50) {
    stringBuffer.append(TEXT_90);
    stringBuffer.append(genDataType.getObjectInstanceClassName());
    stringBuffer.append(TEXT_80);
    stringBuffer.append(genBaseType.getGenPackage().getQualifiedEFactoryInternalInstanceAccessor());
    stringBuffer.append(TEXT_95);
    stringBuffer.append(genBaseType.getName());
    stringBuffer.append(TEXT_92);
    } else {
    stringBuffer.append(TEXT_77);
    stringBuffer.append(genBaseType.getGenPackage().getQualifiedEFactoryInternalInstanceAccessor());
    stringBuffer.append(TEXT_95);
    stringBuffer.append(genBaseType.getName());
    stringBuffer.append(TEXT_94);
    }
    } else {
    stringBuffer.append(TEXT_77);
    if (!genDataType.isObjectType()) {
    stringBuffer.append(TEXT_80);
    stringBuffer.append(genDataType.getObjectInstanceClassName());
    stringBuffer.append(TEXT_33);
    }
    stringBuffer.append(genBaseType.getGenPackage().getQualifiedEFactoryInternalInstanceAccessor());
    stringBuffer.append(TEXT_96);
    stringBuffer.append(genBaseType.getQualifiedClassifierAccessor());
    stringBuffer.append(TEXT_97);
    }
    } else if (genDataType.getItemType() != null) { GenDataType genItemType = genDataType.getItemType(); 
    stringBuffer.append(TEXT_98);
    stringBuffer.append(genDataType.getImportedParameterizedInstanceClassName());
    stringBuffer.append(TEXT_99);
    stringBuffer.append(genModel.getImportedName("java.util.ArrayList"));
    if (genModel.useGenerics()) {
    stringBuffer.append(TEXT_100);
    stringBuffer.append(genItemType.getObjectType().getImportedParameterizedInstanceClassName());
    stringBuffer.append(TEXT_101);
    }
    stringBuffer.append(TEXT_47);
    if (genModel.getRuntimeVersion().getValue() < GenRuntimeVersion.EMF26_VALUE) {
    stringBuffer.append(TEXT_102);
    stringBuffer.append(genModel.getImportedName("java.util.StringTokenizer"));
    stringBuffer.append(TEXT_103);
    stringBuffer.append(genModel.getImportedName("java.util.StringTokenizer"));
    stringBuffer.append(TEXT_104);
    } else {
    stringBuffer.append(TEXT_105);
    }
    stringBuffer.append(TEXT_106);
    if (genModel.getRuntimeVersion().getValue() < GenRuntimeVersion.EMF26_VALUE) {
    stringBuffer.append(TEXT_107);
    }
    if (genItemType.getGenPackage() == genPackage) {
    if (genPackage.isDataTypeConverters()) { genItemType = genItemType.getObjectType();
    stringBuffer.append(TEXT_108);
    stringBuffer.append(genItemType.getName());
    stringBuffer.append(TEXT_109);
    } else {
    stringBuffer.append(TEXT_108);
    stringBuffer.append(genItemType.getName());
    stringBuffer.append(TEXT_110);
    stringBuffer.append(genItemType.getQualifiedClassifierAccessor());
    stringBuffer.append(TEXT_111);
    }
    } else {
    if (genItemType.getGenPackage().isDataTypeConverters()) { genItemType = genItemType.getObjectType();
    stringBuffer.append(TEXT_112);
    stringBuffer.append(genItemType.getGenPackage().getQualifiedEFactoryInternalInstanceAccessor());
    stringBuffer.append(TEXT_95);
    stringBuffer.append(genItemType.getName());
    stringBuffer.append(TEXT_109);
    } else {
    stringBuffer.append(TEXT_112);
    stringBuffer.append(genItemType.getGenPackage().getQualifiedEFactoryInternalInstanceAccessor());
    stringBuffer.append(TEXT_96);
    stringBuffer.append(genItemType.getQualifiedClassifierAccessor());
    stringBuffer.append(TEXT_111);
    }
    }
    stringBuffer.append(TEXT_113);
    } else if (!genDataType.getMemberTypes().isEmpty()) {
    stringBuffer.append(TEXT_114);
    stringBuffer.append(genDataType.getStaticValue(null));
    stringBuffer.append(TEXT_115);
    stringBuffer.append(genDataType.getImportedInstanceClassName());
    stringBuffer.append(TEXT_86);
    stringBuffer.append(genDataType.getStaticValue(null));
    stringBuffer.append(TEXT_116);
    for (GenDataType genMemberType : genDataType.getMemberTypes()) {
    stringBuffer.append(TEXT_117);
    if (genMemberType.getGenPackage() == genPackage) {
    if (genPackage.isDataTypeConverters()) { if (!genDataType.isPrimitiveType()) genMemberType = genMemberType.getObjectType();
    stringBuffer.append(TEXT_118);
    stringBuffer.append(genMemberType.getName());
    stringBuffer.append(TEXT_94);
    } else {
    stringBuffer.append(TEXT_119);
    stringBuffer.append(genDataType.getObjectInstanceClassName());
    stringBuffer.append(TEXT_120);
    stringBuffer.append(genMemberType.getName());
    stringBuffer.append(TEXT_110);
    stringBuffer.append(genMemberType.getQualifiedClassifierAccessor());
    stringBuffer.append(TEXT_97);
    }
    } else {
    if (genPackage.isDataTypeConverters()) { if (!genDataType.isPrimitiveType()) genMemberType = genMemberType.getObjectType();
    stringBuffer.append(TEXT_121);
    stringBuffer.append(genMemberType.getGenPackage().getQualifiedEFactoryInternalInstanceAccessor());
    stringBuffer.append(TEXT_95);
    stringBuffer.append(genMemberType.getName());
    stringBuffer.append(TEXT_94);
    } else {
    stringBuffer.append(TEXT_119);
    stringBuffer.append(genDataType.getObjectInstanceClassName());
    stringBuffer.append(TEXT_33);
    stringBuffer.append(genMemberType.getGenPackage().getQualifiedEFactoryInternalInstanceAccessor());
    stringBuffer.append(TEXT_96);
    stringBuffer.append(genMemberType.getQualifiedClassifierAccessor());
    stringBuffer.append(TEXT_97);
    }
    }
    stringBuffer.append(TEXT_122);
    if (!genDataType.isPrimitiveType()) {
    stringBuffer.append(TEXT_123);
    }
    stringBuffer.append(genModel.getImportedName("org.eclipse.emf.ecore.util.Diagnostician"));
    stringBuffer.append(TEXT_124);
    stringBuffer.append(eDataType);
    stringBuffer.append(TEXT_125);
    if (genDataType.isPrimitiveType() && !isJDK50) {
    stringBuffer.append(TEXT_126);
    stringBuffer.append(genDataType.getObjectInstanceClassName());
    stringBuffer.append(TEXT_127);
    } else {
    stringBuffer.append(TEXT_128);
    }
    stringBuffer.append(TEXT_129);
    }
    stringBuffer.append(TEXT_130);
    if (!genDataType.isPrimitiveType()) {
    stringBuffer.append(TEXT_131);
    }
    stringBuffer.append(TEXT_132);
    } else if (!genDataType.hasConversionDelegate() && genModel.useGenerics() && (genDataType.isArrayType() || !genDataType.getEcoreDataType().getETypeParameters().isEmpty() || genDataType.getEcoreDataType().getInstanceTypeName().contains("<"))) {
    stringBuffer.append(TEXT_133);
    stringBuffer.append(genDataType.getImportedParameterizedObjectInstanceClassName());
    stringBuffer.append(TEXT_134);
    } else if (!genDataType.hasConversionDelegate() && genDataType.isArrayType()) {
    stringBuffer.append(TEXT_135);
    stringBuffer.append(genModel.getImportedName("java.lang.UnsupportedOperationException"));
    stringBuffer.append(TEXT_47);
    } else if (genDataType.isPrimitiveType() && !isJDK50) {
    stringBuffer.append(TEXT_136);
    stringBuffer.append(genDataType.getObjectInstanceClassName());
    stringBuffer.append(TEXT_137);
    stringBuffer.append(eDataType);
    stringBuffer.append(TEXT_138);
    stringBuffer.append(genDataType.getPrimitiveValueFunction());
    stringBuffer.append(TEXT_47);
    } else {
    stringBuffer.append(TEXT_77);
    if (!genDataType.isObjectType()) {
    stringBuffer.append(TEXT_80);
    stringBuffer.append(genDataType.getImportedParameterizedObjectInstanceClassName());
    stringBuffer.append(TEXT_33);
    }
    stringBuffer.append(TEXT_139);
    stringBuffer.append(eDataType);
    stringBuffer.append(TEXT_97);
    }
    stringBuffer.append(TEXT_78);
    }
    stringBuffer.append(TEXT_61);
    if (genDataType.hasAPITags()) {
    stringBuffer.append(TEXT_62);
    stringBuffer.append(genDataType.getAPITags(genModel.getIndentation(stringBuffer)));
    }
    stringBuffer.append(TEXT_63);
    if (!genPackage.isDataTypeConverters() && genModel.useGenerics() && genDataType.isUncheckedCast() && !genDataType.hasCreatorBody()) {
    stringBuffer.append(TEXT_79);
    }
    if (isJDK50 && genDataType.hasAPIDeprecatedTag()) {
    stringBuffer.append(TEXT_64);
    }
    stringBuffer.append(TEXT_65);
    stringBuffer.append(genDataType.getImportedParameterizedObjectInstanceClassName());
    stringBuffer.append(TEXT_66);
    stringBuffer.append(genDataType.getName());
    stringBuffer.append(TEXT_110);
    stringBuffer.append(genModel.getImportedName("org.eclipse.emf.ecore.EDataType"));
    stringBuffer.append(TEXT_140);
    if (genDataType instanceof GenEnum) {
    if (genPackage.isDataTypeConverters() || genDataType.hasCreatorBody()) {
    stringBuffer.append(TEXT_93);
    stringBuffer.append(genDataType.getName());
    stringBuffer.append(TEXT_141);
    } else {
    stringBuffer.append(TEXT_68);
    stringBuffer.append(((GenEnum)genDataType).getImportedInstanceClassName());
    stringBuffer.append(TEXT_86);
    stringBuffer.append(((GenEnum)genDataType).getImportedInstanceClassName());
    stringBuffer.append(TEXT_142);
    stringBuffer.append(genModel.getNonNLS());
    stringBuffer.append(genModel.getNonNLS(2));
    stringBuffer.append(genModel.getNonNLS(3));
    stringBuffer.append(TEXT_89);
    }
    } else if (genDataType.getBaseType() != null) { GenDataType genBaseType = genDataType.getBaseType(); 
    if (genBaseType.getGenPackage() == genPackage) {
    stringBuffer.append(TEXT_77);
    if (!genDataType.getObjectInstanceClassName().equals(genBaseType.getObjectInstanceClassName())) {
    stringBuffer.append(TEXT_80);
    stringBuffer.append(genDataType.getObjectInstanceClassName());
    stringBuffer.append(TEXT_33);
    }
    stringBuffer.append(TEXT_46);
    stringBuffer.append(genBaseType.getName());
    stringBuffer.append(TEXT_110);
    stringBuffer.append(genBaseType.getQualifiedClassifierAccessor());
    stringBuffer.append(TEXT_143);
    } else {
    stringBuffer.append(TEXT_77);
    if (!genDataType.isObjectType()) {
    stringBuffer.append(TEXT_80);
    stringBuffer.append(genDataType.getObjectInstanceClassName());
    stringBuffer.append(TEXT_33);
    }
    stringBuffer.append(genBaseType.getGenPackage().getQualifiedEFactoryInternalInstanceAccessor());
    stringBuffer.append(TEXT_96);
    stringBuffer.append(genBaseType.getQualifiedClassifierAccessor());
    stringBuffer.append(TEXT_143);
    }
    } else if (genDataType.getItemType() != null) { GenDataType genItemType = genDataType.getItemType(); 
    if (genPackage.isDataTypeConverters()) {
    stringBuffer.append(TEXT_93);
    stringBuffer.append(genDataType.getName());
    stringBuffer.append(TEXT_141);
    } else {
    stringBuffer.append(TEXT_144);
    stringBuffer.append(genDataType.getImportedParameterizedObjectInstanceClassName());
    stringBuffer.append(TEXT_99);
    stringBuffer.append(genModel.getImportedName("java.util.ArrayList"));
    if (genModel.useGenerics()) {
    stringBuffer.append(TEXT_100);
    stringBuffer.append(genItemType.getObjectType().getImportedParameterizedInstanceClassName());
    stringBuffer.append(TEXT_101);
    }
    stringBuffer.append(TEXT_47);
    if (genModel.getRuntimeVersion().getValue() < GenRuntimeVersion.EMF26_VALUE) {
    stringBuffer.append(TEXT_102);
    stringBuffer.append(genModel.getImportedName("java.util.StringTokenizer"));
    stringBuffer.append(TEXT_103);
    stringBuffer.append(genModel.getImportedName("java.util.StringTokenizer"));
    stringBuffer.append(TEXT_145);
    } else {
    stringBuffer.append(TEXT_146);
    }
    stringBuffer.append(TEXT_106);
    if (genModel.getRuntimeVersion().getValue() < GenRuntimeVersion.EMF26_VALUE) {
    stringBuffer.append(TEXT_107);
    }
    if (genItemType.getGenPackage() == genPackage) {
    stringBuffer.append(TEXT_108);
    stringBuffer.append(genItemType.getName());
    stringBuffer.append(TEXT_110);
    stringBuffer.append(genItemType.getQualifiedClassifierAccessor());
    stringBuffer.append(TEXT_111);
    } else {
    stringBuffer.append(TEXT_112);
    if (!genItemType.isObjectType()) {
    stringBuffer.append(TEXT_80);
    stringBuffer.append(genItemType.getObjectInstanceClassName());
    stringBuffer.append(TEXT_33);
    }
    stringBuffer.append(genItemType.getGenPackage().getQualifiedEFactoryInternalInstanceAccessor());
    stringBuffer.append(TEXT_96);
    stringBuffer.append(genItemType.getQualifiedClassifierAccessor());
    stringBuffer.append(TEXT_111);
    }
    stringBuffer.append(TEXT_113);
    }
    } else if (!genDataType.getMemberTypes().isEmpty()) {
    if (genPackage.isDataTypeConverters()) {
    if (genDataType.isPrimitiveType() && !isJDK50) {
    stringBuffer.append(TEXT_90);
    stringBuffer.append(genDataType.getObjectInstanceClassName());
    stringBuffer.append(TEXT_91);
    stringBuffer.append(genDataType.getName());
    stringBuffer.append(TEXT_147);
    } else {
    stringBuffer.append(TEXT_93);
    stringBuffer.append(genDataType.getName());
    stringBuffer.append(TEXT_141);
    }
    } else {
    stringBuffer.append(TEXT_144);
    stringBuffer.append(genDataType.getObjectInstanceClassName());
    stringBuffer.append(TEXT_148);
    for (GenDataType genMemberType : genDataType.getMemberTypes()) {
    stringBuffer.append(TEXT_117);
    if (genMemberType.getGenPackage() == genPackage) {
    stringBuffer.append(TEXT_121);
    if (!genDataType.isObjectType() && !genDataType.getObjectInstanceClassName().equals(genMemberType.getObjectInstanceClassName())) {
    stringBuffer.append(TEXT_80);
    stringBuffer.append(genDataType.getObjectInstanceClassName());
    stringBuffer.append(TEXT_33);
    }
    stringBuffer.append(TEXT_46);
    stringBuffer.append(genMemberType.getName());
    stringBuffer.append(TEXT_110);
    stringBuffer.append(genMemberType.getQualifiedClassifierAccessor());
    stringBuffer.append(TEXT_143);
    } else {
    stringBuffer.append(TEXT_121);
    if (!genDataType.isObjectType()) {
    stringBuffer.append(TEXT_80);
    stringBuffer.append(genDataType.getObjectInstanceClassName());
    stringBuffer.append(TEXT_33);
    }
    stringBuffer.append(genMemberType.getGenPackage().getQualifiedEFactoryInternalInstanceAccessor());
    stringBuffer.append(TEXT_96);
    stringBuffer.append(genMemberType.getQualifiedClassifierAccessor());
    stringBuffer.append(TEXT_143);
    }
    stringBuffer.append(TEXT_149);
    stringBuffer.append(genModel.getImportedName("org.eclipse.emf.ecore.util.Diagnostician"));
    stringBuffer.append(TEXT_150);
    }
    stringBuffer.append(TEXT_151);
    }
    } else if (genPackage.isDataTypeConverters() || genDataType.hasCreatorBody()) {
    stringBuffer.append(TEXT_93);
    stringBuffer.append(genDataType.getName());
    stringBuffer.append(TEXT_141);
    } else if (!genDataType.hasConversionDelegate() && genModel.useGenerics() && (genDataType.isArrayType() || !genDataType.getEcoreDataType().getETypeParameters().isEmpty() || genDataType.getEcoreDataType().getInstanceTypeName().contains("<"))) {
    stringBuffer.append(TEXT_77);
    if (!genDataType.isObjectType()) {
    stringBuffer.append(TEXT_80);
    stringBuffer.append(genDataType.getImportedParameterizedObjectInstanceClassName());
    stringBuffer.append(TEXT_33);
    }
    stringBuffer.append(TEXT_152);
    } else if (!genDataType.hasConversionDelegate() && genDataType.isArrayType()) {
    stringBuffer.append(TEXT_135);
    stringBuffer.append(genModel.getImportedName("java.lang.UnsupportedOperationException"));
    stringBuffer.append(TEXT_47);
    } else {
    stringBuffer.append(TEXT_77);
    if (!genDataType.isObjectType()) {
    stringBuffer.append(TEXT_80);
    stringBuffer.append(genDataType.getImportedParameterizedObjectInstanceClassName());
    stringBuffer.append(TEXT_33);
    }
    stringBuffer.append(TEXT_153);
    }
    stringBuffer.append(TEXT_78);
    if (genPackage.isDataTypeConverters() || genDataType.hasConverterBody()) { String eDataType = genDataType.getQualifiedClassifierAccessor();
    stringBuffer.append(TEXT_61);
    if (genDataType.hasAPITags()) {
    stringBuffer.append(TEXT_62);
    stringBuffer.append(genDataType.getAPITags(genModel.getIndentation(stringBuffer)));
    }
    stringBuffer.append(TEXT_63);
    if (isJDK50 && genDataType.hasAPIDeprecatedTag()) {
    stringBuffer.append(TEXT_64);
    }
    if (genPackage.isDataTypeConverters() && useInterfaceOverrideAnnotation) {
    stringBuffer.append(TEXT_41);
    }
    stringBuffer.append(TEXT_154);
    stringBuffer.append(genDataType.getName());
    stringBuffer.append(TEXT_80);
    if (genDataType.hasCreatorBody()) {
    stringBuffer.append(TEXT_81);
    }
    stringBuffer.append(genDataType.getImportedBoundedWildcardInstanceClassName());
    stringBuffer.append(TEXT_69);
    if (genDataType.hasConverterBody()) {
    stringBuffer.append(TEXT_83);
    } else {
    stringBuffer.append(TEXT_155);
    }
    stringBuffer.append(TEXT_85);
    if (genDataType.hasConverterBody()) {
    stringBuffer.append(TEXT_68);
    stringBuffer.append(genDataType.getConverterBody(genModel.getIndentation(stringBuffer)));
    } else if (genDataType instanceof GenEnum) {
    stringBuffer.append(TEXT_156);
    } else if (genDataType.getBaseType() != null) { GenDataType genBaseType = genDataType.getBaseType(); boolean isPrimitiveConversion = !genDataType.isPrimitiveType() && genBaseType.isPrimitiveType();
    if (genBaseType.getGenPackage() == genPackage) {
    if (isPrimitiveConversion) {
    stringBuffer.append(TEXT_157);
    stringBuffer.append(genBaseType.getName());
    stringBuffer.append(TEXT_158);
    if (!isJDK50) {
    stringBuffer.append(TEXT_44);
    stringBuffer.append(genBaseType.getPrimitiveValueFunction());
    stringBuffer.append(TEXT_75);
    }
    stringBuffer.append(TEXT_73);
    } else {
    stringBuffer.append(TEXT_159);
    stringBuffer.append(genBaseType.getName());
    stringBuffer.append(TEXT_160);
    }
    } else if (genBaseType.getGenPackage().isDataTypeConverters()) {
    stringBuffer.append(TEXT_77);
    stringBuffer.append(genBaseType.getGenPackage().getQualifiedFactoryInstanceAccessor());
    stringBuffer.append(TEXT_161);
    stringBuffer.append(genBaseType.getName());
    stringBuffer.append(TEXT_160);
    } else {
    stringBuffer.append(TEXT_77);
    stringBuffer.append(genBaseType.getGenPackage().getQualifiedEFactoryInternalInstanceAccessor());
    stringBuffer.append(TEXT_162);
    stringBuffer.append(genBaseType.getQualifiedClassifierAccessor());
    stringBuffer.append(TEXT_163);
    }
    } else if (genDataType.getItemType() != null) { GenDataType genItemType = genDataType.getItemType(); 
    stringBuffer.append(TEXT_164);
    stringBuffer.append(genModel.getImportedName(genModel.useGenerics() ? "java.lang.StringBuilder" : "java.lang.StringBuffer"));
    stringBuffer.append(TEXT_99);
    stringBuffer.append(genModel.getImportedName(genModel.useGenerics() ? "java.lang.StringBuilder" : "java.lang.StringBuffer"));
    stringBuffer.append(TEXT_47);
    String item; if (!genModel.useGenerics()) { item = "i.next()"; 
    stringBuffer.append(TEXT_102);
    stringBuffer.append(genModel.getImportedName("java.util.Iterator"));
    stringBuffer.append(TEXT_165);
    } else { item = "item";
    stringBuffer.append(TEXT_102);
    stringBuffer.append(genModel.getImportedName("java.lang.Object"));
    stringBuffer.append(TEXT_166);
    }
    stringBuffer.append(TEXT_106);
    if (genItemType.getGenPackage() == genPackage) {
    if (genPackage.isDataTypeConverters()) { genItemType = genItemType.getObjectType();
    stringBuffer.append(TEXT_167);
    stringBuffer.append(genItemType.getName());
    stringBuffer.append(TEXT_168);
    stringBuffer.append(genItemType.getObjectInstanceClassName());
    stringBuffer.append(TEXT_33);
    stringBuffer.append(item);
    stringBuffer.append(TEXT_169);
    } else {
    stringBuffer.append(TEXT_167);
    stringBuffer.append(genItemType.getName());
    stringBuffer.append(TEXT_170);
    stringBuffer.append(genItemType.getQualifiedClassifierAccessor());
    stringBuffer.append(TEXT_125);
    stringBuffer.append(item);
    stringBuffer.append(TEXT_169);
    }
    } else {
    if (genItemType.getGenPackage().isDataTypeConverters()) { genItemType = genItemType.getObjectType();
    stringBuffer.append(TEXT_171);
    stringBuffer.append(genItemType.getGenPackage().getQualifiedEFactoryInternalInstanceAccessor());
    stringBuffer.append(TEXT_161);
    stringBuffer.append(genItemType.getName());
    stringBuffer.append(TEXT_168);
    stringBuffer.append(genItemType.getObjectInstanceClassName());
    stringBuffer.append(TEXT_33);
    stringBuffer.append(item);
    stringBuffer.append(TEXT_169);
    } else {
    stringBuffer.append(TEXT_171);
    stringBuffer.append(genItemType.getGenPackage().getQualifiedEFactoryInternalInstanceAccessor());
    stringBuffer.append(TEXT_162);
    stringBuffer.append(genItemType.getQualifiedClassifierAccessor());
    stringBuffer.append(TEXT_125);
    stringBuffer.append(item);
    stringBuffer.append(TEXT_169);
    }
    }
    stringBuffer.append(TEXT_172);
    } else if (!genDataType.getMemberTypes().isEmpty()) {
    if (!genDataType.isPrimitiveType()) {
    stringBuffer.append(TEXT_173);
    for (GenDataType genMemberType : genDataType.getMemberTypes()) {
    stringBuffer.append(TEXT_130);
    stringBuffer.append(genMemberType.getQualifiedClassifierAccessor());
    stringBuffer.append(TEXT_174);
    if (genMemberType.getGenPackage() == genPackage) {
    if (genPackage.isDataTypeConverters()) {
    if (genMemberType.getQualifiedInstanceClassName().equals(genDataType.getQualifiedInstanceClassName())) {
    stringBuffer.append(TEXT_175);
    stringBuffer.append(genMemberType.getName());
    stringBuffer.append(TEXT_160);
    } else if (genMemberType.isPrimitiveType() && !isJDK50) {
    stringBuffer.append(TEXT_175);
    stringBuffer.append(genMemberType.getName());
    stringBuffer.append(TEXT_176);
    stringBuffer.append(genMemberType.getObjectType().getImportedInstanceClassName());
    stringBuffer.append(TEXT_177);
    stringBuffer.append(genMemberType.getPrimitiveValueFunction());
    stringBuffer.append(TEXT_178);
    } else {
    stringBuffer.append(TEXT_175);
    stringBuffer.append(genMemberType.getName());
    stringBuffer.append(TEXT_168);
    stringBuffer.append(genMemberType.getObjectType().getImportedBoundedWildcardInstanceClassName());
    stringBuffer.append(TEXT_179);
    }
    } else {
    stringBuffer.append(TEXT_175);
    stringBuffer.append(genMemberType.getName());
    stringBuffer.append(TEXT_170);
    stringBuffer.append(genMemberType.getQualifiedClassifierAccessor());
    stringBuffer.append(TEXT_163);
    }
    } else {
    if (genMemberType.getGenPackage().isDataTypeConverters()) { genMemberType = genMemberType.getObjectType();
    stringBuffer.append(TEXT_180);
    stringBuffer.append(genMemberType.getGenPackage().getQualifiedEFactoryInternalInstanceAccessor());
    stringBuffer.append(TEXT_161);
    stringBuffer.append(genMemberType.getName());
    stringBuffer.append(TEXT_168);
    stringBuffer.append(genMemberType.getObjectInstanceClassName());
    stringBuffer.append(TEXT_179);
    } else {
    stringBuffer.append(TEXT_180);
    stringBuffer.append(genMemberType.getGenPackage().getQualifiedEFactoryInternalInstanceAccessor());
    stringBuffer.append(TEXT_162);
    stringBuffer.append(genMemberType.getQualifiedClassifierAccessor());
    stringBuffer.append(TEXT_163);
    }
    }
    stringBuffer.append(TEXT_181);
    }
    } else {
    for (GenDataType genMemberType : genDataType.getMemberTypes()) {
    stringBuffer.append(TEXT_117);
    if (genMemberType.getGenPackage() == genPackage) {
    if (genPackage.isDataTypeConverters()) {
    stringBuffer.append(TEXT_182);
    stringBuffer.append(genMemberType.getName());
    stringBuffer.append(TEXT_160);
    } else {
    stringBuffer.append(TEXT_182);
    stringBuffer.append(genMemberType.getName());
    stringBuffer.append(TEXT_170);
    stringBuffer.append(genMemberType.getQualifiedClassifierAccessor());
    stringBuffer.append(TEXT_125);
    if (!isJDK50) {
    stringBuffer.append(TEXT_126);
    stringBuffer.append(genMemberType.getObjectInstanceClassName());
    stringBuffer.append(TEXT_183);
    } else {
    stringBuffer.append(TEXT_155);
    }
    stringBuffer.append(TEXT_73);
    }
    } else {
    if (genMemberType.getGenPackage().isDataTypeConverters()) {
    stringBuffer.append(TEXT_184);
    stringBuffer.append(genMemberType.getGenPackage().getQualifiedEFactoryInternalInstanceAccessor());
    stringBuffer.append(TEXT_161);
    stringBuffer.append(genMemberType.getName());
    stringBuffer.append(TEXT_160);
    } else {
    stringBuffer.append(TEXT_184);
    stringBuffer.append(genMemberType.getGenPackage().getQualifiedEFactoryInternalInstanceAccessor());
    stringBuffer.append(TEXT_162);
    stringBuffer.append(genMemberType.getQualifiedClassifierAccessor());
    stringBuffer.append(TEXT_125);
    if (!isJDK50) {
    stringBuffer.append(TEXT_126);
    stringBuffer.append(genMemberType.getObjectInstanceClassName());
    stringBuffer.append(TEXT_183);
    } else {
    stringBuffer.append(TEXT_155);
    }
    stringBuffer.append(TEXT_73);
    }
    }
    stringBuffer.append(TEXT_185);
    }
    }
    stringBuffer.append(TEXT_186);
    stringBuffer.append(eDataType);
    stringBuffer.append(TEXT_187);
    } else if (!genDataType.hasConversionDelegate() && genModel.useGenerics() && (genDataType.isArrayType() || !genDataType.getEcoreDataType().getETypeParameters().isEmpty() || genDataType.getEcoreDataType().getInstanceTypeName().contains("<"))) {
    stringBuffer.append(TEXT_188);
    } else if (!genDataType.hasConversionDelegate() && genDataType.isArrayType()) {
    stringBuffer.append(TEXT_135);
    stringBuffer.append(genModel.getImportedName("java.lang.UnsupportedOperationException"));
    stringBuffer.append(TEXT_47);
    } else if (genDataType.isPrimitiveType() && !isJDK50) {
    stringBuffer.append(TEXT_189);
    stringBuffer.append(eDataType);
    stringBuffer.append(TEXT_190);
    stringBuffer.append(genDataType.getObjectInstanceClassName());
    stringBuffer.append(TEXT_191);
    } else {
    stringBuffer.append(TEXT_189);
    stringBuffer.append(eDataType);
    stringBuffer.append(TEXT_163);
    }
    stringBuffer.append(TEXT_78);
    }
    stringBuffer.append(TEXT_61);
    if (genDataType.hasAPITags()) {
    stringBuffer.append(TEXT_62);
    stringBuffer.append(genDataType.getAPITags(genModel.getIndentation(stringBuffer)));
    }
    stringBuffer.append(TEXT_63);
    if (genModel.useGenerics() && (genDataType.getItemType() != null || genDataType.isUncheckedCast()) && (genPackage.isDataTypeConverters() || genDataType.hasCreatorBody())) {
    stringBuffer.append(TEXT_79);
    }
    if (isJDK50 && genDataType.hasAPIDeprecatedTag()) {
    stringBuffer.append(TEXT_64);
    }
    stringBuffer.append(TEXT_154);
    stringBuffer.append(genDataType.getName());
    stringBuffer.append(TEXT_170);
    stringBuffer.append(genModel.getImportedName("org.eclipse.emf.ecore.EDataType"));
    stringBuffer.append(TEXT_192);
    if (genDataType instanceof GenEnum) {
    if (genPackage.isDataTypeConverters() || genDataType.hasConverterBody()) {
    stringBuffer.append(TEXT_159);
    stringBuffer.append(genDataType.getName());
    stringBuffer.append(TEXT_168);
    stringBuffer.append(genDataType.getImportedInstanceClassName());
    stringBuffer.append(TEXT_179);
    } else {
    stringBuffer.append(TEXT_156);
    }
    } else if (genDataType.getBaseType() != null) { GenDataType genBaseType = genDataType.getBaseType(); 
    if (genBaseType.getGenPackage() == genPackage) {
    stringBuffer.append(TEXT_159);
    stringBuffer.append(genBaseType.getName());
    stringBuffer.append(TEXT_170);
    stringBuffer.append(genBaseType.getQualifiedClassifierAccessor());
    stringBuffer.append(TEXT_163);
    } else {
    stringBuffer.append(TEXT_77);
    stringBuffer.append(genBaseType.getGenPackage().getQualifiedEFactoryInternalInstanceAccessor());
    stringBuffer.append(TEXT_162);
    stringBuffer.append(genBaseType.getQualifiedClassifierAccessor());
    stringBuffer.append(TEXT_163);
    }
    } else if (genDataType.getItemType() != null) { GenDataType genItemType = genDataType.getItemType(); 
    if (genPackage.isDataTypeConverters() || genDataType.hasCreatorBody()) {
    stringBuffer.append(TEXT_159);
    stringBuffer.append(genDataType.getName());
    stringBuffer.append(TEXT_168);
    stringBuffer.append(genDataType.getImportedBoundedWildcardInstanceClassName());
    stringBuffer.append(TEXT_179);
    } else { final String singleWildcard = genModel.useGenerics() ? "<?>" : "";
    stringBuffer.append(TEXT_193);
    stringBuffer.append(genModel.getImportedName("java.util.List"));
    stringBuffer.append(singleWildcard);
    stringBuffer.append(TEXT_194);
    stringBuffer.append(genModel.getImportedName("java.util.List"));
    stringBuffer.append(singleWildcard);
    stringBuffer.append(TEXT_195);
    stringBuffer.append(genModel.getImportedName("java.lang.StringBuffer"));
    stringBuffer.append(TEXT_99);
    stringBuffer.append(genModel.getImportedName("java.lang.StringBuffer"));
    stringBuffer.append(TEXT_47);
    String item; if (!genModel.useGenerics()) { item = "i.next()"; 
    stringBuffer.append(TEXT_102);
    stringBuffer.append(genModel.getImportedName("java.util.Iterator"));
    stringBuffer.append(TEXT_196);
    } else { item = "item";
    stringBuffer.append(TEXT_102);
    stringBuffer.append(genModel.getImportedName("java.lang.Object"));
    stringBuffer.append(TEXT_197);
    }
    stringBuffer.append(TEXT_106);
    if (genItemType.getGenPackage() == genPackage) {
    stringBuffer.append(TEXT_167);
    stringBuffer.append(genItemType.getName());
    stringBuffer.append(TEXT_170);
    stringBuffer.append(genItemType.getQualifiedClassifierAccessor());
    stringBuffer.append(TEXT_125);
    stringBuffer.append(item);
    stringBuffer.append(TEXT_169);
    } else {
    stringBuffer.append(TEXT_171);
    stringBuffer.append(genItemType.getGenPackage().getQualifiedEFactoryInternalInstanceAccessor());
    stringBuffer.append(TEXT_162);
    stringBuffer.append(genItemType.getQualifiedClassifierAccessor());
    stringBuffer.append(TEXT_125);
    stringBuffer.append(item);
    stringBuffer.append(TEXT_169);
    }
    stringBuffer.append(TEXT_172);
    }
    } else if (!genDataType.getMemberTypes().isEmpty()) {
    if (genPackage.isDataTypeConverters() || genDataType.hasConverterBody()) {
    if (genDataType.isPrimitiveType()) {
    stringBuffer.append(TEXT_157);
    stringBuffer.append(genDataType.getName());
    stringBuffer.append(TEXT_176);
    stringBuffer.append(genDataType.getObjectInstanceClassName());
    stringBuffer.append(TEXT_198);
    if (!isJDK50) {
    stringBuffer.append(TEXT_44);
    stringBuffer.append(genDataType.getPrimitiveValueFunction());
    stringBuffer.append(TEXT_75);
    }
    stringBuffer.append(TEXT_73);
    } else {
    stringBuffer.append(TEXT_159);
    stringBuffer.append(genDataType.getName());
    stringBuffer.append(TEXT_160);
    }
    } else {
    stringBuffer.append(TEXT_173);
    for (GenDataType genMemberType : genDataType.getMemberTypes()) {
    stringBuffer.append(TEXT_130);
    stringBuffer.append(genMemberType.getQualifiedClassifierAccessor());
    stringBuffer.append(TEXT_174);
    if (genMemberType.getGenPackage() == genPackage) {
    stringBuffer.append(TEXT_175);
    stringBuffer.append(genMemberType.getName());
    stringBuffer.append(TEXT_170);
    stringBuffer.append(genMemberType.getQualifiedClassifierAccessor());
    stringBuffer.append(TEXT_163);
    } else {
    stringBuffer.append(TEXT_180);
    stringBuffer.append(genMemberType.getGenPackage().getQualifiedEFactoryInternalInstanceAccessor());
    stringBuffer.append(TEXT_162);
    stringBuffer.append(genMemberType.getQualifiedClassifierAccessor());
    stringBuffer.append(TEXT_163);
    }
    stringBuffer.append(TEXT_181);
    }
    stringBuffer.append(TEXT_199);
    }
    } else if (genPackage.isDataTypeConverters() || genDataType.hasConverterBody()) {
    if (genDataType.isPrimitiveType()) {
    stringBuffer.append(TEXT_157);
    stringBuffer.append(genDataType.getName());
    stringBuffer.append(TEXT_80);
    if (!isJDK50) {
    stringBuffer.append(TEXT_80);
    }
    stringBuffer.append(TEXT_80);
    stringBuffer.append(genDataType.getObjectInstanceClassName());
    stringBuffer.append(TEXT_200);
    if (!isJDK50) {
    stringBuffer.append(TEXT_201);
    stringBuffer.append(genDataType.getPrimitiveValueFunction());
    stringBuffer.append(TEXT_75);
    }
    stringBuffer.append(TEXT_73);
    } else {
    stringBuffer.append(TEXT_159);
    stringBuffer.append(genDataType.getName());
    stringBuffer.append(TEXT_168);
    stringBuffer.append(genDataType.getImportedBoundedWildcardInstanceClassName());
    stringBuffer.append(TEXT_179);
    }
    } else if (!genDataType.hasConversionDelegate() && genModel.useGenerics() && (genDataType.isArrayType() || !genDataType.getEcoreDataType().getETypeParameters().isEmpty() || genDataType.getEcoreDataType().getInstanceTypeName().contains("<"))) {
    stringBuffer.append(TEXT_188);
    } else if (!genDataType.hasConversionDelegate() && genDataType.isArrayType()) {
    stringBuffer.append(TEXT_135);
    stringBuffer.append(genModel.getImportedName("java.lang.UnsupportedOperationException"));
    stringBuffer.append(TEXT_47);
    } else {
    stringBuffer.append(TEXT_202);
    }
    stringBuffer.append(TEXT_78);
    }
    }
    } else {
    for (GenClass genClass : genPackage.getGenClasses()) {
    if (genClass.hasFactoryInterfaceCreateMethod()) {
    stringBuffer.append(TEXT_203);
    stringBuffer.append(genClass.getFormattedName());
    stringBuffer.append(TEXT_204);
    stringBuffer.append(genClass.getFormattedName());
    stringBuffer.append(TEXT_205);
    if (genClass.hasAPITags()) {
    stringBuffer.append(TEXT_62);
    stringBuffer.append(genClass.getAPITags(genModel.getIndentation(stringBuffer)));
    }
    stringBuffer.append(TEXT_63);
    if (isJDK50 && genClass.hasAPIDeprecatedTag()) {
    stringBuffer.append(TEXT_64);
    }
    stringBuffer.append(TEXT_206);
    stringBuffer.append(genClass.getTypeParameters());
    stringBuffer.append(genClass.getImportedInterfaceName());
    stringBuffer.append(genClass.getInterfaceTypeArguments());
    stringBuffer.append(TEXT_66);
    stringBuffer.append(genClass.getName());
    stringBuffer.append(TEXT_207);
    }
    }
    if (genPackage.isDataTypeConverters()) {
    for (GenDataType genDataType : genPackage.getAllGenDataTypes()) {
    if (genDataType.isSerializable()) {
    stringBuffer.append(TEXT_208);
    stringBuffer.append(genDataType.getFormattedName());
    stringBuffer.append(TEXT_209);
    if (genDataType.hasAPITags()) {
    stringBuffer.append(TEXT_62);
    stringBuffer.append(genDataType.getAPITags(genModel.getIndentation(stringBuffer)));
    }
    stringBuffer.append(TEXT_63);
    if (isJDK50 && genDataType.hasAPIDeprecatedTag()) {
    stringBuffer.append(TEXT_64);
    }
    stringBuffer.append(TEXT_206);
    stringBuffer.append(genDataType.getImportedParameterizedInstanceClassName());
    stringBuffer.append(TEXT_66);
    stringBuffer.append(genDataType.getName());
    stringBuffer.append(TEXT_210);
    stringBuffer.append(genDataType.getFormattedName());
    stringBuffer.append(TEXT_211);
    if (genDataType.hasAPITags()) {
    stringBuffer.append(TEXT_62);
    stringBuffer.append(genDataType.getAPITags(genModel.getIndentation(stringBuffer)));
    }
    stringBuffer.append(TEXT_63);
    if (isJDK50 && genDataType.hasAPIDeprecatedTag()) {
    stringBuffer.append(TEXT_64);
    }
    stringBuffer.append(TEXT_212);
    stringBuffer.append(genDataType.getName());
    stringBuffer.append(TEXT_80);
    stringBuffer.append(genDataType.getImportedBoundedWildcardInstanceClassName());
    stringBuffer.append(TEXT_213);
    }
    }
    }
    }
    if (!isImplementation && !genModel.isSuppressEMFMetaData()) {
    stringBuffer.append(TEXT_214);
    stringBuffer.append(genPackage.getPackageInterfaceName());
    stringBuffer.append(TEXT_215);
    stringBuffer.append(genPackage.getBasicPackageName());
    stringBuffer.append(TEXT_207);
    } else if (isImplementation) {
    stringBuffer.append(TEXT_50);
    if (useInterfaceOverrideAnnotation && !genModel.isSuppressEMFMetaData()) {
    stringBuffer.append(TEXT_41);
    }
    stringBuffer.append(TEXT_65);
    stringBuffer.append(genPackage.getImportedPackageInterfaceName());
    stringBuffer.append(TEXT_215);
    stringBuffer.append(genPackage.getBasicPackageName());
    stringBuffer.append(TEXT_216);
    stringBuffer.append(genPackage.getImportedPackageInterfaceName());
    stringBuffer.append(TEXT_217);
    if (!genModel.getCodeStyle().contains(GenCodeStyle.UNNECESSARY_DEPRECATED_METHOD)) {
    stringBuffer.append(TEXT_218);
    if (genModel.useClassOverrideAnnotation()) {
    stringBuffer.append(TEXT_64);
    }
    stringBuffer.append(TEXT_29);
    stringBuffer.append(genPackage.getImportedPackageInterfaceName());
    stringBuffer.append(TEXT_219);
    stringBuffer.append(genPackage.getImportedPackageInterfaceName());
    stringBuffer.append(TEXT_220);
    }
    }
    stringBuffer.append(TEXT_221);
    stringBuffer.append(isInterface ? genPackage.getFactoryInterfaceName() : genPackage.getFactoryClassName());
    genModel.emitSortedImports();
    stringBuffer.append(TEXT_7);
    return stringBuffer.toString();
  }
}
