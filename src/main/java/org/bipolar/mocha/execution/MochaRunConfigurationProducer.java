/*     */ package org.bipolar.mocha.execution;
/*     */ import com.intellij.execution.RunManager;
/*     */ import com.intellij.execution.actions.ConfigurationContext;
/*     */ import com.intellij.execution.actions.ConfigurationFromContext;
/*     */ import com.intellij.execution.configurations.ConfigurationFactory;
/*     */ import com.intellij.execution.configurations.ConfigurationType;
/*     */ import com.intellij.execution.configurations.RunConfiguration;
/*     */ import com.intellij.ide.plugins.IdeaPluginDescriptor;
/*     */ import com.intellij.ide.plugins.PluginManager;
/*     */ import com.intellij.javascript.nodejs.PackageJsonData;
/*     */ import com.intellij.javascript.nodejs.util.NodePackage;
/*     */ import com.intellij.javascript.testFramework.JsTestElementPath;
/*     */ import com.intellij.javascript.testFramework.PreferableRunConfiguration;
/*     */ import com.intellij.javascript.testFramework.interfaces.mochaTdd.MochaTddFileStructure;
/*     */ import com.intellij.javascript.testFramework.interfaces.mochaTdd.MochaTddFileStructureBuilder;
/*     */ import com.intellij.javascript.testFramework.jasmine.JasmineFileStructure;
/*     */ import com.intellij.javascript.testFramework.jasmine.JasmineFileStructureBuilder;
/*     */ import com.intellij.javascript.testing.JsTestRunConfigurationProducer;
/*     */ import com.intellij.lang.javascript.buildTools.npm.PackageJsonUtil;
/*     */ import com.intellij.lang.javascript.ecmascript6.TypeScriptUtil;
/*     */ import com.intellij.lang.javascript.psi.JSFile;
/*     */ import com.intellij.lang.javascript.psi.JSTestFileType;
/*     */ import com.intellij.lang.javascript.psi.util.JSProjectUtil;
/*     */ import com.intellij.openapi.extensions.PluginId;
/*     */ import com.intellij.openapi.project.Project;
/*     */ import com.intellij.openapi.roots.ProjectFileIndex;
/*     */ import com.intellij.openapi.util.Pair;
/*     */ import com.intellij.openapi.util.Ref;
/*     */ import com.intellij.openapi.util.TextRange;
/*     */ import com.intellij.openapi.util.io.FileUtil;
/*     */ import com.intellij.openapi.util.text.StringUtil;
/*     */ import com.intellij.openapi.vfs.LocalFileSystem;
/*     */ import com.intellij.openapi.vfs.VirtualFile;
/*     */ import com.intellij.psi.PsiElement;
/*     */ import com.intellij.psi.PsiFile;
/*     */ import com.intellij.psi.util.PsiUtilCore;
/*     */ import com.intellij.util.ObjectUtils;
/*     */ import com.intellij.util.SmartList;
/*     */ import org.bipolar.mocha.MochaUtil;
/*     */ import java.util.ArrayList;
/*     */ import java.util.Collections;
/*     */ import java.util.HashSet;
/*     */ import java.util.List;
/*     */ import java.util.Set;
/*     */ import org.jetbrains.annotations.NotNull;
/*     */ import org.jetbrains.annotations.Nullable;
/*     */ 
/*     */ public class MochaRunConfigurationProducer extends JsTestRunConfigurationProducer<MochaRunConfiguration> {
/*     */   public MochaRunConfigurationProducer() {
/*  50 */     super(MochaUtil.PACKAGE_DESCRIPTOR, getStopPackageNames());
/*     */   }
/*     */ 
/*     */   
/*     */   @NotNull
/*     */   public ConfigurationFactory getConfigurationFactory() {
/*  56 */     if (MochaConfigurationType.getInstance() == null) $$$reportNull$$$0(0);  return (ConfigurationFactory)MochaConfigurationType.getInstance();
/*     */   }
/*     */   
/*     */   @NotNull
/*     */   private static List<String> getStopPackageNames() {
/*  61 */     IdeaPluginDescriptor karma = PluginManager.getInstance().findEnabledPlugin(PluginId.getId("Karma"));
/*  62 */     if (karma != null && karma.isEnabled()) {
/*  63 */       if (Collections.singletonList("karma") == null) $$$reportNull$$$0(1);  return Collections.singletonList("karma");
/*     */     } 
/*  65 */     if (Collections.emptyList() == null) $$$reportNull$$$0(2);  return (List)Collections.emptyList();
/*     */   }
/*     */   
/*     */   private boolean isActiveFor(@NotNull PsiElement element, @NotNull ConfigurationContext context) {
/*  69 */     if (element == null) $$$reportNull$$$0(3);  if (context == null) $$$reportNull$$$0(4);  VirtualFile file = PsiUtilCore.getVirtualFile(element);
/*  70 */     if (file == null) {
/*  71 */       return false;
/*     */     }
/*  73 */     if (isTestRunnerPackageAvailableFor(element, context)) {
/*  74 */       return true;
/*     */     }
/*  76 */     List<VirtualFile> roots = collectMochaTestRoots(element.getProject());
/*  77 */     if (roots.isEmpty()) {
/*  78 */       return false;
/*     */     }
/*  80 */     Set<VirtualFile> dirs = new HashSet<>();
/*  81 */     for (VirtualFile root : roots) {
/*  82 */       if (root.isDirectory()) {
/*  83 */         dirs.add(root); continue;
/*     */       } 
/*  85 */       if (root.equals(file)) {
/*  86 */         return true;
/*     */       }
/*     */     } 
/*  89 */     return VfsUtilCore.isUnder(file, dirs);
/*     */   }
/*     */   
/*     */   @NotNull
/*     */   private static List<VirtualFile> collectMochaTestRoots(@NotNull Project project) {
/*  94 */     if (project == null) $$$reportNull$$$0(5);  List<RunConfiguration> list = RunManager.getInstance(project).getConfigurationsList((ConfigurationType)MochaConfigurationType.getInstance());
/*  95 */     SmartList<VirtualFile> smartList = new SmartList();
/*  96 */     for (RunConfiguration configuration : list) {
/*  97 */       if (configuration instanceof MochaRunConfiguration) {
/*  98 */         MochaRunSettings settings = ((MochaRunConfiguration)configuration).getRunSettings();
/*  99 */         String path = null;
/* 100 */         if (settings.getTestKind() == MochaTestKind.DIRECTORY) {
/* 101 */           path = settings.getTestDirPath();
/*     */         }
/* 103 */         else if (settings.getTestKind() == MochaTestKind.TEST_FILE || settings
/* 104 */           .getTestKind() == MochaTestKind.SUITE || settings
/* 105 */           .getTestKind() == MochaTestKind.TEST) {
/* 106 */           path = settings.getTestFilePath();
/*     */         } 
/* 108 */         if (!StringUtil.isEmptyOrSpaces(path)) {
/* 109 */           VirtualFile dir = LocalFileSystem.getInstance().findFileByPath(path);
/* 110 */           if (dir != null) {
/* 111 */             smartList.add(dir);
/*     */           }
/*     */         } 
/*     */       } 
/*     */     } 
/* 116 */     if (smartList == null) $$$reportNull$$$0(6);  return (List<VirtualFile>)smartList;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected boolean setupConfigurationFromCompatibleContext(@NotNull MochaRunConfiguration configuration, @NotNull ConfigurationContext context, @NotNull Ref<PsiElement> sourceElement) {
/* 123 */     if (configuration == null) $$$reportNull$$$0(7);  if (context == null) $$$reportNull$$$0(8);  if (sourceElement == null) $$$reportNull$$$0(9);  PsiElement element = context.getPsiLocation();
/* 124 */     if (element == null) {
/* 125 */       return false;
/*     */     }
/* 127 */     if (!isActiveFor(element, context)) {
/* 128 */       return false;
/*     */     }
/* 130 */     TestElementInfo elementRunInfo = createTestElementRunInfo(element, configuration.getRunSettings());
/* 131 */     if (elementRunInfo == null) {
/* 132 */       return false;
/*     */     }
/* 134 */     MochaRunSettings runSettings = elementRunInfo.getRunSettings();
/* 135 */     if (runSettings.getTestKind() == MochaTestKind.DIRECTORY) {
/* 136 */       return false;
/*     */     }
/* 138 */     if (StringUtil.isEmptyOrSpaces(runSettings.getExtraMochaOptions())) {
/* 139 */       String compilerMochaOption = getLanguageCompilerMochaOption(runSettings, getOriginalPsiFile(element));
/* 140 */       if (compilerMochaOption != null) {
/* 141 */         runSettings = runSettings.builder().setExtraMochaOptions(compilerMochaOption).build();
/*     */       }
/*     */     } 
/* 144 */     configuration.setRunSettings(runSettings);
/* 145 */     sourceElement.set(elementRunInfo.getEnclosingTestElement());
/* 146 */     configuration.setGeneratedName();
/* 147 */     return true;
/*     */   }
/*     */   
/*     */   @Nullable
/*     */   private static PsiFile getOriginalPsiFile(@Nullable PsiElement element) {
/* 152 */     PsiFile file = (element != null) ? element.getContainingFile() : null;
/* 153 */     return (file != null) ? file.getOriginalFile() : null;
/*     */   }
/*     */   @Nullable
/*     */   private static String getLanguageCompilerMochaOption(@NotNull MochaRunSettings runSettings, @Nullable PsiFile psiFile) {
/* 157 */     if (runSettings == null) $$$reportNull$$$0(10);  if (!Registry.is("mocha.add.option.require.ts-node/register")) {
/* 158 */       return null;
/*     */     }
/* 160 */     if (psiFile == null) {
/* 161 */       return null;
/*     */     }
/* 163 */     VirtualFile workingDir = LocalFileSystem.getInstance().findFileByPath(runSettings.getWorkingDir());
/* 164 */     if (workingDir == null || !workingDir.isValid()) {
/* 165 */       return null;
/*     */     }
/* 167 */     VirtualFile packageJsonWithConfig = (VirtualFile)PackageJsonUtil.processUpPackageJsonFilesAndFindFirst(psiFile.getProject(), workingDir, packageJson -> PackageJsonData.getOrCreate(packageJson).getTopLevelProperties().contains("mocha") ? packageJson : null);
/*     */ 
/*     */     
/* 170 */     if (packageJsonWithConfig != null) {
/* 171 */       return null;
/*     */     }
/* 173 */     String[] mochaConfigPaths = { ".mocharc.js", ".mocharc.cjs", ".mocharc.yaml", ".mocharc.yml", ".mocharc.json", ".mocharc.jsonc", "test/mocha.opts", "mocha.opts" };
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/* 184 */     for (String mochaOptsPath : mochaConfigPaths) {
/* 185 */       VirtualFile mochaConfigFile = workingDir.findFileByRelativePath(mochaOptsPath);
/* 186 */       if (mochaConfigFile != null && mochaConfigFile.isValid() && !mochaConfigFile.isDirectory()) {
/* 187 */         return null;
/*     */       }
/*     */     } 
/* 190 */     Project project = psiFile.getProject();
/* 191 */     NodePackage pkg = MochaUtil.getMochaPackage(project);
/* 192 */     if (pkg.nameMatches("mocha-webpack")) {
/* 193 */       return null;
/*     */     }
/* 195 */     if (TypeScriptUtil.TYPESCRIPT_FILE_TYPES.contains(psiFile.getFileType()) && hasAnyDependencyOn(project, workingDir, "ts-node")) {
/* 196 */       return "--require ts-node/register";
/*     */     }
/* 198 */     return null;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private static boolean hasAnyDependencyOn(@NotNull Project project, @NotNull VirtualFile contextFileOrDir, @NotNull String dependencyName) {
/* 205 */     if (project == null) $$$reportNull$$$0(11);  if (contextFileOrDir == null) $$$reportNull$$$0(12);  if (dependencyName == null) $$$reportNull$$$0(13);  Ref<Boolean> result = Ref.create(Boolean.valueOf(false));
/* 206 */     PackageJsonUtil.processUpPackageJsonFiles(project, contextFileOrDir, packageJson -> {
/*     */           PackageJsonData data = PackageJsonData.getOrCreate(packageJson);
/*     */           if (data.isDependencyOfAnyType(dependencyName)) {
/*     */             result.set(Boolean.valueOf(true));
/*     */             return false;
/*     */           } 
/*     */           return true;
/*     */         });
/* 214 */     return ((Boolean)result.get()).booleanValue();
/*     */   }
/*     */   
/*     */   @Nullable
/*     */   private static Pair<String, JsTestElementPath> createSuiteOrTestData(@NotNull PsiElement element) {
/* 219 */     if (element == null) $$$reportNull$$$0(14);  if (element instanceof com.intellij.psi.PsiFileSystemItem) {
/* 220 */       return null;
/*     */     }
/* 222 */     JSFile jsFile = (JSFile)ObjectUtils.tryCast(element.getContainingFile(), JSFile.class);
/* 223 */     TextRange textRange = element.getTextRange();
/* 224 */     if (jsFile == null || textRange == null) {
/* 225 */       return null;
/*     */     }
/* 227 */     JasmineFileStructure jasmineStructure = (JasmineFileStructure)JasmineFileStructureBuilder.getInstance().fetchCachedTestFileStructure(jsFile);
/* 228 */     JsTestElementPath path = jasmineStructure.findTestElementPath(textRange);
/* 229 */     if (path != null) {
/* 230 */       return Pair.create("bdd", path);
/*     */     }
/* 232 */     MochaTddFileStructure tddStructure = (MochaTddFileStructure)MochaTddFileStructureBuilder.getInstance().fetchCachedTestFileStructure(jsFile);
/* 233 */     path = tddStructure.findTestElementPath(textRange);
/* 234 */     if (path != null) {
/* 235 */       if (tddStructure.hasMochaTypeScriptDeclarations()) {
/* 236 */         return Pair.create("mocha-typescript", path);
/*     */       }
/* 238 */       return Pair.create("tdd", path);
/*     */     } 
/* 240 */     return null;
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   protected boolean isConfigurationFromCompatibleContext(@NotNull MochaRunConfiguration configuration, @NotNull ConfigurationContext context) {
/* 246 */     if (configuration == null) $$$reportNull$$$0(15);  if (context == null) $$$reportNull$$$0(16);  PsiElement element = context.getPsiLocation();
/* 247 */     if (element == null) {
/* 248 */       return false;
/*     */     }
/* 250 */     TestElementInfo elementRunInfo = createTestElementRunInfo(element, configuration.getRunSettings());
/* 251 */     if (elementRunInfo == null) {
/* 252 */       return false;
/*     */     }
/* 254 */     MochaRunSettings thisRunSettings = elementRunInfo.getRunSettings();
/* 255 */     MochaRunSettings thatRunSettings = configuration.getRunSettings();
/* 256 */     if (thisRunSettings.getTestKind() != thatRunSettings.getTestKind()) {
/* 257 */       return false;
/*     */     }
/* 259 */     MochaTestKind testKind = thisRunSettings.getTestKind();
/* 260 */     if (testKind == MochaTestKind.DIRECTORY) {
/* 261 */       return thisRunSettings.getTestDirPath().equals(thatRunSettings.getTestDirPath());
/*     */     }
/* 263 */     if (testKind == MochaTestKind.PATTERN) {
/* 264 */       return thisRunSettings.getTestFilePattern().equals(thatRunSettings.getTestFilePattern());
/*     */     }
/* 266 */     if (testKind == MochaTestKind.TEST_FILE) {
/* 267 */       return thisRunSettings.getTestFilePath().equals(thatRunSettings.getTestFilePath());
/*     */     }
/* 269 */     if (testKind == MochaTestKind.SUITE) {
/* 270 */       return (thisRunSettings.getTestFilePath().equals(thatRunSettings.getTestFilePath()) && thisRunSettings
/* 271 */         .getSuiteNames().equals(thatRunSettings.getSuiteNames()));
/*     */     }
/* 273 */     if (testKind == MochaTestKind.TEST) {
/* 274 */       return (thisRunSettings.getTestFilePath().equals(thatRunSettings.getTestFilePath()) && thisRunSettings
/* 275 */         .getTestNames().equals(thatRunSettings.getTestNames()));
/*     */     }
/* 277 */     return false;
/*     */   }
/*     */   
/*     */   @Nullable
/*     */   private static TestElementInfo createTestElementRunInfo(@NotNull PsiElement element, @NotNull MochaRunSettings templateRunSettings) {
/* 282 */     if (element == null) $$$reportNull$$$0(17);  if (templateRunSettings == null) $$$reportNull$$$0(18);  VirtualFile virtualFile = PsiUtilCore.getVirtualFile(element);
/* 283 */     if (virtualFile == null) {
/* 284 */       return null;
/*     */     }
/* 286 */     Pair<String, JsTestElementPath> pair = createSuiteOrTestData(element);
/* 287 */     if (StringUtil.isEmptyOrSpaces(templateRunSettings.getWorkingDir())) {
/* 288 */       String workingDir = guessWorkingDir(element.getProject(), virtualFile);
/* 289 */       templateRunSettings = templateRunSettings.builder().setWorkingDir(workingDir).build();
/*     */     } 
/* 291 */     if (pair == null) {
/* 292 */       return createFileInfo(element, virtualFile, templateRunSettings);
/*     */     }
/* 294 */     MochaRunSettings.Builder builder = templateRunSettings.builder();
/* 295 */     builder.setTestFilePath(virtualFile.getPath());
/* 296 */     if (templateRunSettings.getUi().isEmpty()) {
/* 297 */       builder.setUi((String)pair.getFirst());
/*     */     }
/* 299 */     JsTestElementPath testElementPath = (JsTestElementPath)pair.getSecond();
/* 300 */     String testName = testElementPath.getTestName();
/* 301 */     if (testName == null) {
/* 302 */       builder.setTestKind(MochaTestKind.SUITE);
/* 303 */       builder.setSuiteNames(testElementPath.getSuiteNames());
/*     */     } else {
/*     */       
/* 306 */       builder.setTestKind(MochaTestKind.TEST);
/* 307 */       List<String> names = new ArrayList<>(testElementPath.getSuiteNames());
/* 308 */       names.add(testName);
/* 309 */       builder.setTestNames(names);
/*     */     } 
/* 311 */     return new TestElementInfo(builder.build(), testElementPath.getTestElement());
/*     */   }
/*     */   
/*     */   @NotNull
/*     */   private static String guessWorkingDir(@NotNull Project project, @NotNull VirtualFile contextFile) {
/* 316 */     if (project == null) $$$reportNull$$$0(19);  if (contextFile == null) $$$reportNull$$$0(20);  VirtualFile configFile = JSProjectUtil.findFileUpToContentRoot(project, contextFile, new String[] { "package.json" });
/* 317 */     VirtualFile workingDir = (configFile != null) ? configFile.getParent() : null;
/* 318 */     if (workingDir == null) {
/* 319 */       workingDir = ProjectFileIndex.getInstance(project).getContentRootForFile(contextFile);
/*     */     }
/* 321 */     if (workingDir == null) {
/* 322 */       workingDir = contextFile.getParent();
/*     */     }
/* 324 */     return (workingDir != null) ? FileUtil.toSystemDependentName(workingDir.getPath()) : "";
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   @Nullable
/*     */   private static TestElementInfo createFileInfo(@NotNull PsiElement element, @NotNull VirtualFile virtualFile, @NotNull MochaRunSettings templateRunSettings) {
/* 331 */     if (element == null) $$$reportNull$$$0(21);  if (virtualFile == null) $$$reportNull$$$0(22);  if (templateRunSettings == null) $$$reportNull$$$0(23);  if (virtualFile.isDirectory()) {
/* 332 */       MochaRunSettings.Builder builder = templateRunSettings.builder();
/* 333 */       builder.setTestKind(MochaTestKind.DIRECTORY);
/* 334 */       builder.setTestDirPath(virtualFile.getPath());
/* 335 */       return new TestElementInfo(builder.build(), element);
/*     */     } 
/* 337 */     JSFile psiFile = (JSFile)ObjectUtils.tryCast(element.getContainingFile(), JSFile.class);
/* 338 */     JSTestFileType testFileType = (psiFile == null) ? null : psiFile.getTestFileType();
/* 339 */     if (psiFile != null && testFileType != null) {
/* 340 */       MochaRunSettings.Builder builder = templateRunSettings.builder();
/* 341 */       builder.setTestKind(MochaTestKind.TEST_FILE);
/* 342 */       builder.setTestFilePath(virtualFile.getPath());
/* 343 */       if (templateRunSettings.getUi().isEmpty()) {
/* 344 */         String ui = MochaUtil.findUi(psiFile);
/* 345 */         builder.setUi((String)ObjectUtils.notNull(ui, "bdd"));
/*     */       } 
/* 347 */       return new TestElementInfo(builder.build(), (PsiElement)psiFile);
/*     */     } 
/* 349 */     return null;
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean isPreferredConfiguration(ConfigurationFromContext self, ConfigurationFromContext other) {
/* 354 */     if (other != null) {
/* 355 */       PreferableRunConfiguration otherRc = (PreferableRunConfiguration)ObjectUtils.tryCast(other.getConfiguration(), PreferableRunConfiguration.class);
/* 356 */       if (otherRc != null && otherRc.isPreferredOver(self.getConfiguration(), self.getSourceElement())) {
/* 357 */         return false;
/*     */       }
/*     */     } 
/* 360 */     return true;
/*     */   }
/*     */   
/*     */   private static class TestElementInfo
/*     */   {
/*     */     private final MochaRunSettings myRunSettings;
/*     */     private final PsiElement myEnclosingTestElement;
/*     */     
/*     */     TestElementInfo(@NotNull MochaRunSettings runSettings, @NotNull PsiElement enclosingTestElement) {
/* 369 */       this.myRunSettings = runSettings;
/* 370 */       this.myEnclosingTestElement = enclosingTestElement;
/*     */     }
/*     */     
/*     */     @NotNull
/*     */     public MochaRunSettings getRunSettings() {
/* 375 */       if (this.myRunSettings == null) $$$reportNull$$$0(2);  return this.myRunSettings;
/*     */     }
/*     */     
/*     */     @NotNull
/*     */     public PsiElement getEnclosingTestElement() {
/* 380 */       if (this.myEnclosingTestElement == null) $$$reportNull$$$0(3);  return this.myEnclosingTestElement;
/*     */     }
/*     */   }
/*     */ }


/* Location:              C:\Program Files\JetBrains\WebStorm 2021.1\plugins\NodeJS\lib\NodeJS.jar!\com\jetbrains\nodejs\mocha\execution\MochaRunConfigurationProducer.class
 * Java compiler version: 11 (55.0)
 * JD-Core Version:       1.1.3
 */