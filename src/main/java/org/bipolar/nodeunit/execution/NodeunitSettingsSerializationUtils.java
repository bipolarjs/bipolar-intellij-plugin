/*     */ package org.bipolar.nodeunit.execution;
/*     */ 
/*     */ import com.intellij.execution.configuration.EnvironmentVariablesData;
/*     */ import com.intellij.javascript.nodejs.interpreter.NodeJsInterpreterRef;
/*     */ import com.intellij.javascript.nodejs.util.NodePackage;
/*     */ import com.intellij.openapi.util.JDOMExternalizer;
/*     */ import com.intellij.openapi.util.io.FileUtil;
/*     */ import com.intellij.openapi.util.text.StringUtil;
/*     */ import com.intellij.util.JdomKt;
/*     */ import com.intellij.util.ObjectUtils;
/*     */ import org.jdom.Element;
/*     */ import org.jetbrains.annotations.NotNull;
/*     */ import org.jetbrains.annotations.Nullable;
/*     */ 
/*     */ 
/*     */ public final class NodeunitSettingsSerializationUtils
/*     */ {
/*     */   private enum Key
/*     */   {
/*  20 */     NODE_PATH("nodePath"),
/*  21 */     PASS_PARENT_ENV_VARS("passParentEnvVars"),
/*  22 */     NODEUNIT_MODULE_DIR("nodeunitModuleDir"),
/*  23 */     WORKING_DIRECTORY("workingDirectory"),
/*  24 */     TEST_TYPE("testType"),
/*  25 */     DIRECTORY("directory"),
/*  26 */     JS_FILE("jsFile"),
/*  27 */     TEST_NAME("testName");
/*     */     
/*     */     private final String key;
/*     */     
/*     */     Key(String key) {
/*  32 */       this.key = key;
/*     */     }
/*     */     
/*     */     public String getKey() {
/*  36 */       return this.key;
/*     */     } }
/*     */   
/*     */   @NotNull
/*     */   public static NodeunitSettings readFromJDomElement(@NotNull Element element) {
/*  41 */     if (element == null) $$$reportNull$$$0(0);  NodeunitSettings.Builder builder = new NodeunitSettings.Builder();
/*     */     
/*  43 */     String interpreterRefName = JDOMExternalizer.readString(element, Key.NODE_PATH.getKey());
/*  44 */     if (interpreterRefName != null) {
/*  45 */       builder.setInterpreterRef(NodeJsInterpreterRef.create(interpreterRefName));
/*     */     }
/*     */     
/*  48 */     EnvironmentVariablesData envData = EnvironmentVariablesData.readExternal(element);
/*  49 */     builder.setEnvData(envData);
/*     */     
/*  51 */     String workingDirectoryPath = readString(element, Key.WORKING_DIRECTORY);
/*  52 */     if (!workingDirectoryPath.isEmpty()) {
/*  53 */       builder.setWorkingDirectory(FileUtil.toSystemDependentName(workingDirectoryPath));
/*     */     }
/*     */     
/*  56 */     builder.setNodeunitPackage(new NodePackage(readString(element, Key.NODEUNIT_MODULE_DIR)));
/*     */     
/*  58 */     NodeunitTestType testType = readEnumByName(element);
/*  59 */     builder.setTestType(testType);
/*  60 */     if (testType == NodeunitTestType.DIRECTORY) {
/*  61 */       readDirectory(builder, element);
/*  62 */     } else if (testType == NodeunitTestType.JS_FILE) {
/*  63 */       readJsFile(builder, element);
/*  64 */     } else if (testType == NodeunitTestType.TEST) {
/*  65 */       readTest(builder, element);
/*     */     } else {
/*  67 */       throw new RuntimeException("Unknown testType: " + testType);
/*     */     } 
/*  69 */     if (builder.build() == null) $$$reportNull$$$0(1);  return builder.build();
/*     */   }
/*     */   
/*     */   private static void readDirectory(@NotNull NodeunitSettings.Builder builder, @NotNull Element element) {
/*  73 */     if (builder == null) $$$reportNull$$$0(2);  if (element == null) $$$reportNull$$$0(3);  String directory = readString(element, Key.DIRECTORY);
/*  74 */     builder.setDirectory(FileUtil.toSystemDependentName(directory));
/*     */   }
/*     */   
/*     */   private static void readJsFile(@NotNull NodeunitSettings.Builder builder, @NotNull Element element) {
/*  78 */     if (builder == null) $$$reportNull$$$0(4);  if (element == null) $$$reportNull$$$0(5);  String jsFile = readString(element, Key.JS_FILE);
/*  79 */     builder.setJsFilePath(FileUtil.toSystemDependentName(jsFile));
/*     */   }
/*     */   
/*     */   private static void readTest(@NotNull NodeunitSettings.Builder builder, @NotNull Element element) {
/*  83 */     if (builder == null) $$$reportNull$$$0(6);  if (element == null) $$$reportNull$$$0(7);  readJsFile(builder, element);
/*  84 */     String testName = readString(element, Key.TEST_NAME);
/*  85 */     builder.setTest(testName);
/*     */   }
/*     */   
/*     */   public static void writeToJDomElement(@NotNull Element element, @NotNull NodeunitSettings settings) {
/*  89 */     if (element == null) $$$reportNull$$$0(8);  if (settings == null) $$$reportNull$$$0(9);  JdomKt.addOptionTag(element, Key.NODE_PATH.getKey(), settings.getInterpreterRef().getReferenceName(), "setting");
/*     */     
/*  91 */     settings.getEnvData().writeExternal(element);
/*  92 */     writeString(element, Key.NODEUNIT_MODULE_DIR, settings.getNodeunitPackage().getSystemIndependentPath());
/*  93 */     writeString(element, Key.WORKING_DIRECTORY, FileUtil.toSystemIndependentName(settings.getWorkingDirectory()));
/*     */     
/*  95 */     NodeunitTestType testType = settings.getTestType();
/*  96 */     writeString(element, Key.TEST_TYPE, testType.name());
/*  97 */     if (testType == NodeunitTestType.DIRECTORY) {
/*  98 */       writeDirectory(element, settings);
/*  99 */     } else if (testType == NodeunitTestType.JS_FILE) {
/* 100 */       writeJsFile(element, settings);
/* 101 */     } else if (testType == NodeunitTestType.TEST) {
/* 102 */       writeTest(element, settings);
/*     */     } else {
/* 104 */       throw new RuntimeException("Unknown testType: " + testType);
/*     */     } 
/*     */   }
/*     */   
/*     */   private static void writeTest(@NotNull Element element, @NotNull NodeunitSettings settings) {
/* 109 */     if (element == null) $$$reportNull$$$0(10);  if (settings == null) $$$reportNull$$$0(11);  writeJsFile(element, settings);
/* 110 */     String testName = settings.getTestName();
/* 111 */     writeString(element, Key.TEST_NAME, testName);
/*     */   }
/*     */   
/*     */   private static void writeDirectory(@NotNull Element element, @NotNull NodeunitSettings settings) {
/* 115 */     if (element == null) $$$reportNull$$$0(12);  if (settings == null) $$$reportNull$$$0(13);  String directory = FileUtil.toSystemIndependentName(settings.getDirectory());
/* 116 */     writeString(element, Key.DIRECTORY, directory);
/*     */   }
/*     */   
/*     */   private static void writeJsFile(@NotNull Element element, @NotNull NodeunitSettings settings) {
/* 120 */     if (element == null) $$$reportNull$$$0(14);  if (settings == null) $$$reportNull$$$0(15);  String jsFilePath = settings.getJsFile();
/* 121 */     writeString(element, Key.JS_FILE, FileUtil.toSystemIndependentName(jsFilePath));
/*     */   }
/*     */   
/*     */   @NotNull
/*     */   private static <E extends Enum<E>> E readEnumByName(@NotNull Element element) {
/* 126 */     if (element == null) $$$reportNull$$$0(16);  String str = readString(element, Key.TEST_TYPE);
/* 127 */     E enumConstant = (E)findEnumByName(NodeunitTestType.JS_FILE.getDeclaringClass(), str);
/* 128 */     if ((E)ObjectUtils.notNull(enumConstant, NodeunitTestType.JS_FILE) == null) $$$reportNull$$$0(17);  return (E)ObjectUtils.notNull(enumConstant, NodeunitTestType.JS_FILE);
/*     */   }
/*     */   
/*     */   private static <E extends Enum> E findEnumByName(@NotNull Class<E> enumClass, @NotNull String value) {
/* 132 */     if (enumClass == null) $$$reportNull$$$0(18);  if (value == null) $$$reportNull$$$0(19);  for (Enum enum_ : (Enum[])enumClass.getEnumConstants()) {
/* 133 */       if (value.equals(enum_.name())) {
/* 134 */         return (E)enum_;
/*     */       }
/*     */     } 
/* 137 */     return null;
/*     */   }
/*     */   
/*     */   private static void writeString(@NotNull Element element, @NotNull Key key, @Nullable String value) {
/* 141 */     if (element == null) $$$reportNull$$$0(20);  if (key == null) $$$reportNull$$$0(21);  if (value != null)
/* 142 */       JdomKt.addOptionTag(element, key.getKey(), value, "setting"); 
/*     */   }
/*     */   
/*     */   @NotNull
/*     */   private static String readString(@NotNull Element element, @NotNull Key key) {
/* 147 */     if (element == null) $$$reportNull$$$0(22);  if (key == null) $$$reportNull$$$0(23);  String value = JDOMExternalizer.readString(element, key.getKey());
/* 148 */     if (StringUtil.notNullize(value) == null) $$$reportNull$$$0(24);  return StringUtil.notNullize(value);
/*     */   }
/*     */ }


/* Location:              C:\Program Files\JetBrains\WebStorm 2021.1\plugins\NodeJS\lib\NodeJS.jar!\com\jetbrains\nodejs\nodeunit\execution\NodeunitSettingsSerializationUtils.class
 * Java compiler version: 11 (55.0)
 * JD-Core Version:       1.1.3
 */