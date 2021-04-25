/*     */ package org.bipolar.mocha.execution;
/*     */ import com.intellij.execution.Executor;
/*     */ import com.intellij.execution.RunManager;
/*     */ import com.intellij.execution.configurations.ConfigurationFactory;
/*     */ import com.intellij.execution.configurations.LocatableConfigurationBase;
/*     */ import com.intellij.execution.configurations.RunConfiguration;
/*     */ import com.intellij.execution.configurations.RunProfileState;
/*     */ import com.intellij.execution.configurations.RuntimeConfigurationError;
/*     */ import com.intellij.execution.configurations.RuntimeConfigurationException;
/*     */ import com.intellij.execution.configurations.RuntimeConfigurationWarning;
/*     */ import com.intellij.execution.runners.ExecutionEnvironment;
/*     */
/*     */ import com.intellij.execution.testframework.TestRunnerBundle;
/*     */ import com.intellij.execution.testframework.sm.runner.SMRunnerConsolePropertiesProvider;
/*     */ import com.intellij.execution.testframework.sm.runner.SMTRunnerConsoleProperties;
/*     */ import com.intellij.javascript.JSRunProfileWithCompileBeforeLaunchOption;
/*     */ import com.intellij.javascript.nodejs.debug.NodeDebugRunConfiguration;
/*     */ import com.intellij.javascript.nodejs.interpreter.NodeInterpreterUtil;
/*     */ import com.intellij.javascript.nodejs.interpreter.NodeJsInterpreter;
/*     */ import com.intellij.javascript.nodejs.util.NodePackage;
/*     */ import com.intellij.javascript.testFramework.PreferableRunConfiguration;
/*     */ import com.intellij.openapi.options.SettingsEditor;
/*     */ import com.intellij.openapi.project.Project;
/*     */ import com.intellij.openapi.roots.ProjectFileIndex;
/*     */ import com.intellij.openapi.util.InvalidDataException;
/*     */ import com.intellij.openapi.util.NlsSafe;
/*     */ import com.intellij.openapi.util.WriteExternalException;
/*     */ import com.intellij.openapi.util.io.FileUtil;
/*     */ import com.intellij.openapi.util.text.StringUtil;
/*     */ import com.intellij.openapi.vfs.LocalFileSystem;
/*     */ import com.intellij.openapi.vfs.VfsUtilCore;
/*     */ import com.intellij.openapi.vfs.VirtualFile;
/*     */ import com.intellij.psi.PsiElement;
/*     */ import com.intellij.util.ObjectUtils;
/*     */ import com.intellij.util.containers.ContainerUtil;
/*     */ import org.bipolar.NodeJSBundle;
/*     */ import org.bipolar.mocha.MochaUtil;
/*     */ import java.io.File;
/*     */ import org.jdom.Element;
/*     */ import org.jetbrains.annotations.NotNull;
/*     */ import org.jetbrains.annotations.Nullable;
/*     */ import org.jetbrains.io.LocalFileFinder;
/*     */ 
/*     */ public class MochaRunConfiguration extends LocatableConfigurationBase<MochaRunConfiguration> implements JSRunProfileWithCompileBeforeLaunchOption, NodeDebugRunConfiguration, PreferableRunConfiguration, SMRunnerConsolePropertiesProvider {
/*  45 */   private MochaRunSettings myRunSettings = (new MochaRunSettings.Builder()).build();
/*     */   
/*     */   protected MochaRunConfiguration(Project project, ConfigurationFactory factory, String name) {
/*  48 */     super(project, factory, name);
/*     */   }
/*     */ 
/*     */   
/*     */   @NotNull
/*     */   public SettingsEditor<MochaRunConfiguration> getConfigurationEditor() {
/*  54 */     return new MochaRunConfigurationEditor(getProject());
/*     */   }
/*     */ 
/*     */   
/*     */   public void readExternal(@NotNull Element element) throws InvalidDataException {
/*  59 */     if (element == null) $$$reportNull$$$0(0);  super.readExternal(element);
/*  60 */     this.myRunSettings = MochaRunSettingsSerializationUtil.readFromXml(element);
/*     */   }
/*     */ 
/*     */   
/*     */   public void writeExternal(@NotNull Element element) throws WriteExternalException {
/*  65 */     if (element == null) $$$reportNull$$$0(1);  super.writeExternal(element);
/*  66 */     MochaRunSettingsSerializationUtil.writeToXml(element, this.myRunSettings);
/*     */   }
/*     */ 
/*     */   
/*     */   @Nullable
/*     */   public RunProfileState getState(@NotNull Executor executor, @NotNull ExecutionEnvironment environment) {
/*  72 */     if (executor == null) $$$reportNull$$$0(2);  if (environment == null) $$$reportNull$$$0(3);  return (RunProfileState)new MochaRunProfileState(getProject(), this, environment, 
/*     */ 
/*     */         
/*  75 */         getMochaPackage(), this.myRunSettings);
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   @Nullable
/*     */   public NodeJsInterpreter getInterpreter() {
/*  82 */     return this.myRunSettings.getInterpreterRef().resolve(getProject());
/*     */   }
/*     */   
/*     */   @NotNull
/*     */   NodePackage getMochaPackage() {
/*  87 */     if (RunManager.getInstance(getProject()).isTemplate((RunConfiguration)this)) {
/*  88 */       if ((NodePackage)ObjectUtils.notNull(this.myRunSettings.getMochaPackage(), new NodePackage("")) == null) $$$reportNull$$$0(4);  return (NodePackage)ObjectUtils.notNull(this.myRunSettings.getMochaPackage(), new NodePackage(""));
/*     */     } 
/*  90 */     NodePackage pkg = this.myRunSettings.getMochaPackage();
/*  91 */     if (pkg == null) {
/*  92 */       Project project = getProject();
/*  93 */       NodeJsInterpreter interpreter = this.myRunSettings.getInterpreterRef().resolve(project);
/*  94 */       pkg = MochaUtil.PACKAGE_DESCRIPTOR.findFirstDirectDependencyPackage(project, interpreter, getContextFile());
/*  95 */       if (pkg.isEmptyPath()) {
/*  96 */         pkg = MochaUtil.getMochaPackage(project);
/*     */       } else {
/*     */         
/*  99 */         MochaUtil.setMochaPackage(project, pkg);
/*     */       } 
/* 101 */       this.myRunSettings = this.myRunSettings.builder().setMochaPackage(pkg).build();
/*     */     } 
/* 103 */     if (pkg == null) $$$reportNull$$$0(5);  return pkg;
/*     */   }
/*     */   
/*     */   @Nullable
/*     */   private VirtualFile getContextFile() {
/* 108 */     VirtualFile f = findFile(this.myRunSettings.getTestFilePath());
/* 109 */     if (f == null) {
/* 110 */       f = findFile(this.myRunSettings.getTestDirPath());
/*     */     }
/* 112 */     if (f == null) {
/* 113 */       f = findFile(this.myRunSettings.getWorkingDir());
/*     */     }
/* 115 */     return f;
/*     */   }
/*     */ 
/*     */   
/*     */   @NotNull
/*     */   public SMTRunnerConsoleProperties createTestConsoleProperties(@NotNull Executor executor) {
/* 121 */     if (executor == null) $$$reportNull$$$0(6);  if (createTestConsoleProperties(executor, this.myRunSettings.getUi(), false) == null) $$$reportNull$$$0(7);  return (SMTRunnerConsoleProperties)createTestConsoleProperties(executor, this.myRunSettings.getUi(), false);
/*     */   }
/*     */   
/*     */   @NotNull
/*     */   public MochaConsoleProperties createTestConsoleProperties(@NotNull Executor executor, @NotNull String ui, boolean withTerminalConsole) {
/* 126 */     if (executor == null) $$$reportNull$$$0(8);  if (ui == null) $$$reportNull$$$0(9);  return new MochaConsoleProperties(this, executor, new MochaTestLocationProvider(ui), withTerminalConsole);
/*     */   }
/*     */   
/*     */   @Nullable
/*     */   private static VirtualFile findFile(@NotNull String path) {
/* 131 */     if (path == null) $$$reportNull$$$0(10);  return FileUtil.isAbsolute(path) ? LocalFileSystem.getInstance().findFileByPath(path) : null;
/*     */   }
/*     */ 
/*     */   
/*     */   public void checkConfiguration() throws RuntimeConfigurationException {
/* 136 */     NodeInterpreterUtil.checkForRunConfiguration(this.myRunSettings.getInterpreterRef().resolve(getProject()));
/* 137 */     getMochaPackage().validateForRunConfiguration("mocha");
/* 138 */     validatePath(true, "working directory", this.myRunSettings.getWorkingDir(), true, true);
/* 139 */     if (StringUtil.isEmptyOrSpaces(this.myRunSettings.getUi())) {
/* 140 */       throw new RuntimeConfigurationError(NodeJSBundle.message("dialog.message.unspecified.mocha.user.interface", new Object[0]));
/*     */     }
/* 142 */     MochaTestKind testKind = this.myRunSettings.getTestKind();
/* 143 */     if (MochaTestKind.DIRECTORY == testKind) {
/* 144 */       validatePath(true, "test directory", this.myRunSettings.getTestDirPath(), false, true);
/*     */     }
/* 146 */     else if (MochaTestKind.PATTERN != testKind) {
/*     */ 
/*     */       
/* 149 */       if (MochaTestKind.TEST_FILE == testKind || MochaTestKind.SUITE == testKind || MochaTestKind.TEST == testKind) {
/* 150 */         validatePath(false, "test file", this.myRunSettings.getTestFilePath(), true, false);
/* 151 */         if (MochaTestKind.SUITE == testKind && 
/* 152 */           this.myRunSettings.getSuiteNames().isEmpty()) {
/* 153 */           throw new RuntimeConfigurationError(NodeJSBundle.message("dialog.message.unspecified.suite.name", new Object[0]));
/*     */         }
/*     */         
/* 156 */         if (MochaTestKind.TEST == testKind && 
/* 157 */           this.myRunSettings.getTestNames().isEmpty()) {
/* 158 */           throw new RuntimeConfigurationError(NodeJSBundle.message("dialog.message.unspecified.test.name", new Object[0]));
/*     */         }
/*     */       } 
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private static void validatePath(boolean shouldBeDirectory, @NotNull String name, @Nullable String path, boolean shouldBeAbsolute, boolean warnIfNonexistent) throws RuntimeConfigurationException {
/* 169 */     if (name == null) $$$reportNull$$$0(11);  if (StringUtil.isEmptyOrSpaces(path)) {
/* 170 */       throw new RuntimeConfigurationError(NodeJSBundle.message("dialog.message.unspecified", new Object[] { name }));
/*     */     }
/* 172 */     File file = new File(path);
/* 173 */     if (shouldBeAbsolute && !file.isAbsolute()) {
/* 174 */       throw new RuntimeConfigurationError(NodeJSBundle.message("dialog.message.no.such", new Object[] { name }));
/*     */     }
/* 176 */     boolean exists = shouldBeDirectory ? file.isDirectory() : file.isFile();
/* 177 */     if (!exists) {
/* 178 */       if (warnIfNonexistent)
/*     */       {
/* 180 */         throw new RuntimeConfigurationWarning(NodeJSBundle.message("dialog.message.no.such", new Object[] { name }));
/*     */       }
/* 182 */       throw new RuntimeConfigurationError(NodeJSBundle.message("dialog.message.no.such", new Object[] { name }));
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   public String suggestedName() {
/* 188 */     MochaRunSettings runSettings = this.myRunSettings;
/* 189 */     MochaTestKind testKind = runSettings.getTestKind();
/* 190 */     if (testKind == MochaTestKind.DIRECTORY) {
/* 191 */       return getRelativePath(getProject(), runSettings.getTestDirPath());
/*     */     }
/* 193 */     if (testKind == MochaTestKind.PATTERN) {
/* 194 */       return runSettings.getTestFilePattern();
/*     */     }
/* 196 */     if (testKind == MochaTestKind.TEST_FILE) {
/* 197 */       return getRelativePath(getProject(), runSettings.getTestFilePath());
/*     */     }
/* 199 */     if (runSettings.getTestKind() == MochaTestKind.SUITE) {
/* 200 */       return StringUtil.join(runSettings.getSuiteNames(), ".");
/*     */     }
/* 202 */     if (runSettings.getTestKind() == MochaTestKind.TEST) {
/* 203 */       return StringUtil.join(runSettings.getTestNames(), ".");
/*     */     }
/* 205 */     return TestRunnerBundle.message("all.tests.scope.presentable.text", new Object[0]);
/*     */   }
/*     */ 
/*     */   
/*     */   @Nullable
/*     */   public String getActionName() {
/* 211 */     MochaRunSettings runSettings = this.myRunSettings;
/* 212 */     MochaTestKind testKind = runSettings.getTestKind();
/* 213 */     if (testKind == MochaTestKind.DIRECTORY) {
/* 214 */       return getLastPathComponent(runSettings.getTestDirPath());
/*     */     }
/* 216 */     if (testKind == MochaTestKind.PATTERN) {
/* 217 */       return runSettings.getTestFilePattern();
/*     */     }
/* 219 */     if (testKind == MochaTestKind.TEST_FILE) {
/* 220 */       return getLastPathComponent(runSettings.getTestFilePath());
/*     */     }
/* 222 */     if (runSettings.getTestKind() == MochaTestKind.SUITE) {
/* 223 */       return StringUtil.notNullize((String)ContainerUtil.getLastItem(runSettings.getSuiteNames()));
/*     */     }
/* 225 */     if (runSettings.getTestKind() == MochaTestKind.TEST) {
/* 226 */       return StringUtil.notNullize((String)ContainerUtil.getLastItem(runSettings.getTestNames()));
/*     */     }
/* 228 */     return TestRunnerBundle.message("all.tests.scope.presentable.text", new Object[0]);
/*     */   }
/*     */   @NotNull
/*     */   @NlsSafe
/*     */   private static String getRelativePath(@NotNull Project project, @NotNull String path) {
/* 233 */     if (project == null) $$$reportNull$$$0(12);  if (path == null) $$$reportNull$$$0(13);  VirtualFile file = LocalFileFinder.findFile(path);
/* 234 */     if (file != null && file.isValid()) {
/* 235 */       VirtualFile root = ProjectFileIndex.getInstance(project).getContentRootForFile(file);
/* 236 */       if (root != null && root.isValid()) {
/* 237 */         String relativePath = VfsUtilCore.getRelativePath(file, root, File.separatorChar);
/* 238 */         if (StringUtil.isNotEmpty(relativePath)) {
/* 239 */           if (relativePath == null) $$$reportNull$$$0(14);  return relativePath;
/*     */         } 
/*     */       } 
/*     */     } 
/* 243 */     return getLastPathComponent(path);
/*     */   }
/*     */   @NotNull
/*     */   @NlsSafe
/*     */   private static String getLastPathComponent(@NotNull String path) {
/* 248 */     if (path == null) $$$reportNull$$$0(15);  int lastIndex = path.lastIndexOf('/');
/* 249 */     if (((lastIndex >= 0) ? path.substring(lastIndex + 1) : path) == null) $$$reportNull$$$0(16);  return (lastIndex >= 0) ? path.substring(lastIndex + 1) : path;
/*     */   }
/*     */   
/*     */   @NotNull
/*     */   public MochaRunSettings getRunSettings() {
/* 254 */     if (this.myRunSettings == null) $$$reportNull$$$0(17);  return this.myRunSettings;
/*     */   }
/*     */   
/*     */   public void setRunSettings(@NotNull MochaRunSettings runSettings) {
/* 258 */     if (runSettings == null) $$$reportNull$$$0(18);  NodePackage pkg = runSettings.getMochaPackage();
/* 259 */     if (pkg != null && pkg.isEmptyPath() && RunManager.getInstance(getProject()).isTemplate((RunConfiguration)this)) {
/* 260 */       runSettings = runSettings.builder().setMochaPackage(null).build();
/*     */     }
/* 262 */     this.myRunSettings = runSettings;
/* 263 */     if (pkg != null) {
/* 264 */       MochaUtil.setMochaPackage(getProject(), pkg);
/*     */     }
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean isPreferredOver(@NotNull RunConfiguration otherRc, @NotNull PsiElement sourceElement) {
/* 270 */     if (otherRc == null) $$$reportNull$$$0(19);  if (sourceElement == null) $$$reportNull$$$0(20);  return true;
/*     */   }
/*     */ 
/*     */   
/*     */   public void onNewConfigurationCreated() {
/* 275 */     MochaRunSettings.Builder builder = this.myRunSettings.builder();
/* 276 */     if (this.myRunSettings.getUi().isEmpty()) {
/* 277 */       builder.setUi("bdd");
/*     */     }
/* 279 */     if (this.myRunSettings.getWorkingDir().trim().isEmpty()) {
/* 280 */       VirtualFile dir = getProject().getBaseDir();
/* 281 */       if (dir != null) {
/* 282 */         builder.setWorkingDir(dir.getPath());
/*     */       }
/*     */     } 
/* 285 */     if (this.myRunSettings.getTestKind() == MochaTestKind.DIRECTORY && this.myRunSettings.getTestDirPath().trim().isEmpty()) {
/* 286 */       String workingDirPath = FileUtil.toSystemIndependentName(this.myRunSettings.getWorkingDir());
/* 287 */       VirtualFile workingDir = LocalFileSystem.getInstance().findFileByPath(workingDirPath);
/* 288 */       if (workingDir != null && workingDir.isValid() && workingDir.isDirectory()) {
/* 289 */         String[] testDirNames = { "test", "spec", "tests", "specs" };
/* 290 */         VirtualFile testDir = null;
/* 291 */         for (String testDirName : testDirNames) {
/* 292 */           testDir = workingDir.findChild(testDirName);
/* 293 */           if (testDir != null && testDir.isValid() && testDir.isDirectory()) {
/*     */             break;
/*     */           }
/*     */         } 
/* 297 */         if (testDir != null) {
/* 298 */           builder.setTestDirPath(testDir.getPath()).build();
/*     */         }
/*     */       } 
/*     */     } 
/* 302 */     this.myRunSettings = builder.build();
/*     */   }
/*     */ }


/* Location:              C:\Program Files\JetBrains\WebStorm 2021.1\plugins\NodeJS\lib\NodeJS.jar!\com\jetbrains\nodejs\mocha\execution\MochaRunConfiguration.class
 * Java compiler version: 11 (55.0)
 * JD-Core Version:       1.1.3
 */