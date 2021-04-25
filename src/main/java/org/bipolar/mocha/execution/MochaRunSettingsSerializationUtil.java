/*     */ package org.bipolar.mocha.execution;
/*     */ 
/*     */ import com.intellij.execution.configuration.EnvironmentVariablesComponent;
/*     */ import com.intellij.execution.configuration.EnvironmentVariablesData;
/*     */ import com.intellij.javascript.nodejs.interpreter.NodeJsInterpreterRef;
/*     */ import com.intellij.openapi.util.JDOMExternalizerUtil;
/*     */ import com.intellij.openapi.util.io.FileUtil;
/*     */ import com.intellij.openapi.util.text.StringUtil;
/*     */ import org.bipolar.mocha.MochaUtil;
/*     */ import java.util.Collections;
/*     */ import java.util.List;
/*     */ import org.jdom.Element;
/*     */ import org.jetbrains.annotations.NotNull;
/*     */ import org.jetbrains.annotations.Nullable;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public final class MochaRunSettingsSerializationUtil
/*     */ {
/*     */   private static final String NODE_INTERPRETER__KEY = "node-interpreter";
/*     */   private static final String NODE_OPTIONS__KEY = "node-options";
/*     */   private static final String MOCHA_PACKAGE___KEY = "mocha-package";
/*     */   private static final String WORKING_DIRECTORY__KEY = "working-directory";
/*     */   private static final String PASS_PARENT_ENV__KEY = "pass-parent-env";
/*     */   private static final String UI__KEY = "ui";
/*     */   private static final String EXTRA_MOCHA_OPTIONS__KEY = "extra-mocha-options";
/*     */   private static final String TEST_KIND__KEY = "test-kind";
/*     */   private static final String TEST_DIRECTORY__KEY = "test-directory";
/*     */   private static final String RECURSIVE__KEY = "recursive";
/*     */   private static final String TEST_FILE_PATTERN__KEY = "test-pattern";
/*     */   private static final String TEST_FILE__KEY = "test-file";
/*     */   private static final String TEST_NAMES__KEY = "test-names";
/*     */   private static final String TEST_NAME__KEY = "name";
/*     */   
/*     */   @NotNull
/*     */   public static MochaRunSettings readFromXml(@NotNull Element parent) {
/*  38 */     if (parent == null) $$$reportNull$$$0(0);  MochaRunSettings.Builder builder = new MochaRunSettings.Builder();
/*     */     
/*  40 */     String interpreterRefName = readTagNullable(parent, "node-interpreter");
/*  41 */     builder.setInterpreterRef(NodeJsInterpreterRef.create(StringUtil.notNullize(interpreterRefName)));
/*     */     
/*  43 */     String nodeOptions = readTag(parent, "node-options");
/*  44 */     builder.setNodeOptions(nodeOptions);
/*     */     
/*  46 */     String pkg = readTagNullable(parent, "mocha-package");
/*  47 */     if (pkg != null) {
/*  48 */       builder.setMochaPackage(MochaUtil.PACKAGE_DESCRIPTOR.createPackage(pkg));
/*     */     }
/*     */     
/*  51 */     String workingDirPath = readTag(parent, "working-directory");
/*  52 */     builder.setWorkingDir(FileUtil.toSystemDependentName(workingDirPath));
/*     */     
/*  54 */     EnvironmentVariablesData envData = EnvironmentVariablesData.readExternal(parent);
/*  55 */     String passParentEnvStr = readTag(parent, "pass-parent-env");
/*  56 */     if (StringUtil.isNotEmpty(passParentEnvStr)) {
/*  57 */       envData = EnvironmentVariablesData.create(envData.getEnvs(), Boolean.parseBoolean(passParentEnvStr));
/*     */     }
/*  59 */     builder.setEnvData(envData);
/*     */     
/*  61 */     builder.setUi(readTag(parent, "ui"));
/*     */     
/*  63 */     String extraMochaOptions = readTag(parent, "extra-mocha-options");
/*  64 */     builder.setExtraMochaOptions(extraMochaOptions);
/*     */     
/*  66 */     MochaTestKind testKind = deserializeTestKind(readTag(parent, "test-kind"));
/*  67 */     builder.setTestKind(testKind);
/*  68 */     if (MochaTestKind.DIRECTORY == testKind) {
/*  69 */       String testDirPath = readTag(parent, "test-directory");
/*  70 */       builder.setTestDirPath(FileUtil.toSystemDependentName(testDirPath));
/*     */       
/*  72 */       String recursiveStr = readTag(parent, "recursive");
/*  73 */       builder.setRecursive(Boolean.parseBoolean(recursiveStr));
/*     */     }
/*  75 */     else if (MochaTestKind.PATTERN == testKind) {
/*  76 */       builder.setTestFilePattern(FileUtil.toSystemDependentName(StringUtil.notNullize(readTag(parent, "test-pattern"))));
/*     */     }
/*  78 */     else if (MochaTestKind.TEST_FILE == testKind || MochaTestKind.SUITE == testKind || MochaTestKind.TEST == testKind) {
/*  79 */       builder.setTestFilePath(FileUtil.toSystemDependentName(StringUtil.notNullize(readTag(parent, "test-file"))));
/*  80 */       if (MochaTestKind.SUITE == testKind) {
/*  81 */         builder.setSuiteNames(readTestNames(parent));
/*     */       }
/*  83 */       else if (MochaTestKind.TEST == testKind) {
/*  84 */         builder.setTestNames(readTestNames(parent));
/*     */       } 
/*     */     } 
/*  87 */     if (builder.build() == null) $$$reportNull$$$0(1);  return builder.build();
/*     */   }
/*     */   
/*     */   private static List<String> readTestNames(@NotNull Element parent) {
/*  91 */     if (parent == null) $$$reportNull$$$0(2);  Element testNamesElement = parent.getChild("test-names");
/*  92 */     if (testNamesElement == null) {
/*  93 */       return Collections.emptyList();
/*     */     }
/*  95 */     return JDOMExternalizerUtil.getChildrenValueAttributes(testNamesElement, "name");
/*     */   }
/*     */   
/*     */   @NotNull
/*     */   private static MochaTestKind deserializeTestKind(@Nullable String testKindStr) {
/*     */     try {
/* 101 */       if (MochaTestKind.valueOf(testKindStr) == null) $$$reportNull$$$0(3);  return MochaTestKind.valueOf(testKindStr);
/* 102 */     } catch (Exception ignored) {
/* 103 */       if (MochaTestKind.DIRECTORY == null) $$$reportNull$$$0(4);  return MochaTestKind.DIRECTORY;
/*     */     } 
/*     */   }
/*     */   
/*     */   @NotNull
/*     */   private static String readTag(@NotNull Element parent, @NotNull String tagName) {
/* 109 */     if (parent == null) $$$reportNull$$$0(5);  if (tagName == null) $$$reportNull$$$0(6);  if (StringUtil.notNullize(readTagNullable(parent, tagName)) == null) $$$reportNull$$$0(7);  return StringUtil.notNullize(readTagNullable(parent, tagName));
/*     */   }
/*     */   
/*     */   @Nullable
/*     */   private static String readTagNullable(@NotNull Element parent, @NotNull String tagName) {
/* 114 */     if (parent == null) $$$reportNull$$$0(8);  if (tagName == null) $$$reportNull$$$0(9);  Element child = parent.getChild(tagName);
/* 115 */     String value = null;
/* 116 */     if (child != null) {
/* 117 */       value = child.getText();
/*     */     }
/* 119 */     return value;
/*     */   }
/*     */   
/*     */   public static void writeToXml(@NotNull Element parent, @NotNull MochaRunSettings runSettings) {
/* 123 */     if (parent == null) $$$reportNull$$$0(10);  if (runSettings == null) $$$reportNull$$$0(11);  NodeJsInterpreterRef interpreterRef = runSettings.getInterpreterRef();
/* 124 */     writeTag(parent, "node-interpreter", interpreterRef.getReferenceName());
/* 125 */     writeTag(parent, "node-options", runSettings.getNodeOptions());
/*     */     
/* 127 */     if (runSettings.getMochaPackage() != null) {
/* 128 */       writeTag(parent, "mocha-package", runSettings.getMochaPackage().getSystemIndependentPath());
/*     */     }
/*     */     
/* 131 */     String workingDirPath = FileUtil.toSystemIndependentName(runSettings.getWorkingDir());
/* 132 */     writeTag(parent, "working-directory", workingDirPath);
/*     */     
/* 134 */     writeTag(parent, "pass-parent-env", String.valueOf(runSettings.getEnvData().isPassParentEnvs()));
/* 135 */     EnvironmentVariablesComponent.writeExternal(parent, runSettings.getEnvData().getEnvs());
/*     */     
/* 137 */     writeTag(parent, "ui", runSettings.getUi());
/*     */     
/* 139 */     writeTag(parent, "extra-mocha-options", runSettings.getExtraMochaOptions());
/*     */     
/* 141 */     MochaTestKind testKind = runSettings.getTestKind();
/* 142 */     writeTag(parent, "test-kind", testKind.name());
/* 143 */     if (MochaTestKind.DIRECTORY == testKind) {
/* 144 */       writeTag(parent, "test-directory", FileUtil.toSystemIndependentName(runSettings.getTestDirPath()));
/* 145 */       writeTag(parent, "recursive", String.valueOf(runSettings.isRecursive()));
/*     */     }
/* 147 */     else if (MochaTestKind.PATTERN == testKind) {
/* 148 */       writeTag(parent, "test-pattern", FileUtil.toSystemIndependentName(runSettings.getTestFilePattern()));
/*     */     }
/* 150 */     else if (MochaTestKind.TEST_FILE == testKind || MochaTestKind.SUITE == testKind || MochaTestKind.TEST == testKind) {
/* 151 */       writeTag(parent, "test-file", FileUtil.toSystemIndependentName(runSettings.getTestFilePath()));
/* 152 */       if (MochaTestKind.SUITE == testKind) {
/* 153 */         writeTestNames(parent, runSettings.getSuiteNames());
/*     */       }
/* 155 */       else if (MochaTestKind.TEST == testKind) {
/* 156 */         writeTestNames(parent, runSettings.getTestNames());
/*     */       } 
/*     */     } 
/*     */   }
/*     */   
/*     */   private static void writeTestNames(@NotNull Element parent, @NotNull List<String> testNames) {
/* 162 */     if (parent == null) $$$reportNull$$$0(12);  if (testNames == null) $$$reportNull$$$0(13);  if (!testNames.isEmpty()) {
/* 163 */       Element testNamesElement = new Element("test-names");
/* 164 */       JDOMExternalizerUtil.addChildrenWithValueAttribute(testNamesElement, "name", testNames);
/* 165 */       parent.addContent(testNamesElement);
/*     */     } 
/*     */   }
/*     */   
/*     */   private static void writeTag(@NotNull Element parent, @NotNull String tagName, @NotNull String value) {
/* 170 */     if (parent == null) $$$reportNull$$$0(14);  if (tagName == null) $$$reportNull$$$0(15);  if (value == null) $$$reportNull$$$0(16);  Element element = new Element(tagName);
/* 171 */     element.setText(value);
/* 172 */     parent.addContent(element);
/*     */   }
/*     */ }


/* Location:              C:\Program Files\JetBrains\WebStorm 2021.1\plugins\NodeJS\lib\NodeJS.jar!\com\jetbrains\nodejs\mocha\execution\MochaRunSettingsSerializationUtil.class
 * Java compiler version: 11 (55.0)
 * JD-Core Version:       1.1.3
 */