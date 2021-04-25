/*     */ package org.bipolar.run;
/*     */ import com.intellij.diagnostic.logging.LogConfigurationPanel;
/*     */ import com.intellij.execution.ExecutionException;
/*     */ import com.intellij.execution.ExecutionResult;
/*     */ import com.intellij.execution.Executor;
/*     */ import com.intellij.execution.RunManager;
/*     */ import com.intellij.execution.RunnerAndConfigurationSettings;
/*     */ import com.intellij.execution.configuration.EnvironmentVariablesData;
/*     */ import com.intellij.execution.configurations.ConfigurationFactory;
/*     */ import com.intellij.execution.configurations.LocatableConfigurationBase;
/*     */ import com.intellij.execution.configurations.RefactoringListenerProvider;
/*     */ import com.intellij.execution.configurations.RunConfiguration;
/*     */ import com.intellij.execution.configurations.RunConfigurationBase;
/*     */
/*     */ import com.intellij.execution.configurations.RunProfile;
/*     */ import com.intellij.execution.configurations.RunProfileState;
/*     */ import com.intellij.execution.configurations.RuntimeConfigurationError;
/*     */ import com.intellij.execution.configurations.RuntimeConfigurationException;
/*     */ import com.intellij.execution.runners.ExecutionEnvironment;
/*     */ import com.intellij.execution.util.ProgramParametersUtil;
/*     */ import com.intellij.execution.util.ScriptFileUtil;
/*     */ import com.intellij.ide.browsers.StartBrowserSettings;
/*     */ import com.intellij.javascript.JSRunProfileWithCompileBeforeLaunchOption;
/*     */ import com.intellij.javascript.nodejs.NodeCommandLineUtil;
/*     */ import com.intellij.javascript.nodejs.PackageJsonData;
/*     */ import com.intellij.javascript.nodejs.interpreter.NodeJsInterpreter;
/*     */ import com.intellij.javascript.nodejs.interpreter.NodeJsInterpreterRef;
/*     */ import com.intellij.javascript.nodejs.interpreter.local.NodeJsLocalInterpreter;
/*     */ import com.intellij.javascript.nodejs.interpreter.remote.NodeJSRemoteInterpreterManager;
/*     */ import com.intellij.javascript.nodejs.interpreter.remote.NodeJsRemoteInterpreter;
/*     */ import com.intellij.openapi.options.SettingsEditor;
/*     */ import com.intellij.openapi.options.SettingsEditorGroup;
/*     */ import com.intellij.openapi.project.Project;
/*     */ import com.intellij.openapi.project.ProjectUtil;
/*     */ import com.intellij.openapi.util.io.FileUtil;
/*     */ import com.intellij.openapi.util.text.StringUtil;
/*     */ import com.intellij.openapi.vfs.VirtualFile;
/*     */ import com.intellij.psi.PsiElement;
/*     */ import com.intellij.refactoring.listeners.RefactoringElementListener;
/*     */ import com.intellij.util.ObjectUtils;
/*     */ import com.intellij.util.PathUtil;
/*     */ import com.intellij.util.PathUtilRt;
/*     */ import com.intellij.util.io.PathKt;
/*     */ import com.intellij.util.net.NetUtils;
/*     */ import com.intellij.xdebugger.XDebugProcess;
/*     */ import com.intellij.xdebugger.XDebugSession;
/*     */ import com.jetbrains.nodeJs.NodeDebugProgramRunnerKt;
/*     */ import com.jetbrains.nodeJs.NodeJSDebuggableConfiguration;
/*     */ import org.bipolar.NodeJSBundle;
/*     */ import org.bipolar.execution.NodeCommandLineState;
/*     */ import org.bipolar.run.profile.settings.NodeProfilingSettings;
/*     */ import java.io.IOException;
/*     */ import java.net.InetSocketAddress;
/*     */ import java.nio.file.InvalidPathException;
/*     */ import java.nio.file.Path;
/*     */ import java.nio.file.Paths;
/*     */ import java.nio.file.attribute.BasicFileAttributes;
/*     */ import java.util.Map;
/*     */
import org.jdom.Element;
/*     */ import org.jetbrains.annotations.NotNull;
/*     */ import org.jetbrains.annotations.Nullable;
/*     */ 
/*     */ public final class NodeJsRunConfiguration extends LocatableConfigurationBase<Element> implements NodeJSDebuggableConfiguration, RefactoringListenerProvider, DebuggableProcessRunConfiguration, PersistentStateComponent<Element>, JSRunProfileWithCompileBeforeLaunchOption {
/*     */   @NotNull
/*  65 */   private StartBrowserSettings myStartBrowserSettings = new StartBrowserSettings(); @NotNull
/*     */   private NodeProfilingSettings myNodeProfilingSettings;
/*     */   @NotNull
/*  68 */   private EnvironmentVariablesData myEnvData = EnvironmentVariablesData.DEFAULT;
/*     */ 
/*     */   
/*     */   NodeJsRunConfiguration(Project project, ConfigurationFactory factory, @Nullable String name) {
/*  72 */     super(project, factory, name);
/*     */     
/*  74 */     this.myNodeProfilingSettings = new NodeProfilingSettings();
/*     */   }
/*     */ 
/*     */   
/*     */   @NotNull
/*     */   protected NodeJsRunConfigurationState getOptions() {
/*  80 */     if ((NodeJsRunConfigurationState)super.getOptions() == null) $$$reportNull$$$0(0);  return (NodeJsRunConfigurationState)super.getOptions();
/*     */   }
/*     */   
/*     */   @TestOnly
/*     */   public void setNodeInterpreter(@NotNull String path) {
/*  85 */     if (path == null) $$$reportNull$$$0(1);  getOptions().setInterpreterRef(NodeJsInterpreterRef.create((NodeJsInterpreter)new NodeJsLocalInterpreter(path)));
/*     */   }
/*     */ 
/*     */   
/*     */   @Nullable
/*     */   public String getWorkingDirectory() {
/*  91 */     return getOptions().getWorkingDir();
/*     */   }
/*     */ 
/*     */   
/*     */   public void setWorkingDirectory(@Nullable String value) {
/*  96 */     getOptions().setWorkingDir(FileUtil.toSystemIndependentName(StringUtil.notNullize(value)));
/*     */   }
/*     */   
/*     */   @NotNull
/*     */   public StartBrowserSettings getStartBrowserSettings() {
/* 101 */     if (this.myStartBrowserSettings == null) $$$reportNull$$$0(2);  return this.myStartBrowserSettings;
/*     */   }
/*     */   
/*     */   public void setStartBrowserSettings(@NotNull StartBrowserSettings settings) {
/* 105 */     if (settings == null) $$$reportNull$$$0(3);  this.myStartBrowserSettings = settings;
/*     */   }
/*     */   
/*     */   @NotNull
/*     */   public NodeProfilingSettings getNodeProfilingSettings() {
/* 110 */     if (this.myNodeProfilingSettings == null) $$$reportNull$$$0(4);  return this.myNodeProfilingSettings;
/*     */   }
/*     */   
/*     */   public void setNodeProfilingSettings(@NotNull NodeProfilingSettings nodeProfilingSettings) {
/* 114 */     if (nodeProfilingSettings == null) $$$reportNull$$$0(5);  this.myNodeProfilingSettings = nodeProfilingSettings;
/*     */   }
/*     */ 
/*     */   
/*     */   public RunConfiguration clone() {
/* 119 */     NodeJsRunConfiguration clone = (NodeJsRunConfiguration)super.clone();
/* 120 */     clone.myNodeProfilingSettings = new NodeProfilingSettings(this.myNodeProfilingSettings);
/* 121 */     return (RunConfiguration)clone;
/*     */   }
/*     */ 
/*     */   
/*     */   public void onNewConfigurationCreated() {
/* 126 */     String workingDir = getWorkingDirectory();
/* 127 */     if (StringUtil.isEmptyOrSpaces(workingDir)) {
/* 128 */       getOptions().setWorkingDir(StringUtil.notNullize(getProject().getBasePath()));
/*     */     }
/*     */   }
/*     */ 
/*     */   
/*     */   @NotNull
/*     */   public SettingsEditor<NodeJsRunConfiguration> getConfigurationEditor() {
/* 135 */     SettingsEditorGroup<NodeJsRunConfiguration> group = new SettingsEditorGroup();
/* 136 */     NodeRunConfigurationEditor settings = new NodeRunConfigurationEditor(getProject());
/* 137 */     group.addEditor(NodeJSBundle.message("rc.nodejs.configuration.tab.name", new Object[0]), settings);
/* 138 */     NodeJSRunConfigurationExtensionsManager.getInstance().appendEditorsNode(this, group, settings);
/* 139 */     group.addEditor(ExecutionBundle.message("logs.tab.title", new Object[0]), (SettingsEditor)new LogConfigurationPanel());
/* 140 */     if (group == null) $$$reportNull$$$0(6);  return (SettingsEditor<NodeJsRunConfiguration>)group;
/*     */   }
/*     */ 
/*     */   
/*     */   public RunProfileState getState(@NotNull Executor executor, @NotNull ExecutionEnvironment executionEnvironment) {
/* 145 */     if (executor == null) $$$reportNull$$$0(7);  if (executionEnvironment == null) $$$reportNull$$$0(8);  return createState(executionEnvironment);
/*     */   }
/*     */   
/*     */   @NotNull
/*     */   private RunProfileState createState(@NotNull ExecutionEnvironment environment) {
/* 150 */     if (environment == null) $$$reportNull$$$0(9);  return (RunProfileState)new NodeCommandLineState(environment, this);
/*     */   }
/*     */ 
/*     */   
/*     */   public void checkConfiguration() throws RuntimeConfigurationException {
/* 155 */     NodeJsInterpreter interpreter = getInterpreter();
/* 156 */     NodeInterpreterUtil.checkForRunConfiguration(interpreter);
/* 157 */     NodeJSRunConfigurationExtensionsManager.getInstance().checkConfiguration(this);
/*     */     
/* 159 */     String workingDirPath = getWorkingDirectory();
/* 160 */     Path workingDir = (workingDirPath == null) ? null : Paths.get(workingDirPath, new String[0]);
/* 161 */     if (workingDir == null || !workingDir.isAbsolute() || !workingDir.toFile().isDirectory()) {
/* 162 */       throw new RuntimeConfigurationError(NodeJSBundle.message("rc.nodejs.error.incorrect.workingDirectory.text", new Object[0]));
/*     */     }
/* 164 */     String pathToJsFileWithMacros = getInputPath();
/* 165 */     String pathToJsFile = ProgramParametersUtil.expandPathAndMacros(pathToJsFileWithMacros, 
/* 166 */         ProgramParametersUtil.getModule((CommonProgramRunConfigurationParameters)this), getProject());
/* 167 */     if (Objects.equals(pathToJsFile, pathToJsFileWithMacros)) {
/* 168 */       validatePath(workingDir, pathToJsFile);
/*     */     }
/* 170 */     if (this.myStartBrowserSettings.isSelected() && this.myStartBrowserSettings.getUrl() == null)
/* 171 */       throw new RuntimeConfigurationError(NodeJSBundle.message("rc.nodejs.error.unspecified.url", new Object[] {
/* 172 */               NodeJSStartBrowserRunConfigurationExtension.getTabTitle()
/*     */             })); 
/* 174 */     this.myNodeProfilingSettings.check(pathToJsFile);
/*     */   }
/*     */   
/*     */   private static void validatePath(@NotNull Path workingDir, @Nullable String jsFilePath) throws RuntimeConfigurationError {
/* 178 */     if (workingDir == null) $$$reportNull$$$0(10);  if (jsFilePath == null)
/* 179 */       return;  if (ScriptFileUtil.isMemoryScriptPath(jsFilePath)) {
/* 180 */       if (ScriptFileUtil.findScriptFileByPath(jsFilePath) == null) {
/* 181 */         throw new RuntimeConfigurationError(NodeJSBundle.message("rc.nodejs.error.script.not.found.text", new Object[0]));
/*     */       }
/*     */       return;
/*     */     } 
/* 185 */     Path jsFile = getPath(jsFilePath);
/* 186 */     if (jsFile != null && jsFile.isAbsolute()) {
/* 187 */       BasicFileAttributes attributes = PathKt.basicAttributesIfExists(jsFile);
/* 188 */       if (attributes != null && (attributes.isRegularFile() || attributes.isSymbolicLink())) {
/*     */         return;
/*     */       }
/*     */     } 
/*     */     try {
/* 193 */       jsFile = workingDir.resolve(jsFilePath);
/* 194 */       BasicFileAttributes attributes = PathKt.basicAttributesIfExists(jsFile);
/* 195 */       if (attributes != null && (attributes.isRegularFile() || attributes.isSymbolicLink())) {
/*     */         return;
/*     */       }
/*     */     }
/* 199 */     catch (InvalidPathException invalidPathException) {}
/*     */     
/* 201 */     throw new RuntimeConfigurationError(NodeJSBundle.message("rc.nodejs.error.incorrect.JavaScript.file.path.text", new Object[0]));
/*     */   }
/*     */   
/*     */   @Nullable
/*     */   private static Path getPath(@Nullable String filePath) {
/* 206 */     if (filePath == null) return null; 
/*     */     try {
/* 208 */       return Paths.get(filePath, new String[0]);
/*     */     }
/* 210 */     catch (InvalidPathException e) {
/* 211 */       return null;
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   @Nullable
/*     */   public NodeJsInterpreter getInterpreter() {
/* 218 */     return getOptions().getInterpreterRef().resolve(getProject());
/*     */   }
/*     */   
/*     */   @NotNull
/*     */   public NodeJsInterpreterRef getInterpreterRef() {
/* 223 */     if (getOptions().getInterpreterRef() == null) $$$reportNull$$$0(11);  return getOptions().getInterpreterRef();
/*     */   }
/*     */ 
/*     */   
/*     */   public String suggestedName() {
/* 228 */     String filename = PathUtilRt.getFileName(getOptions().getPathToJsFile());
/* 229 */     return filename;
/*     */   }
/*     */ 
/*     */   
/*     */   public void loadState(@NotNull Element element) {
/* 234 */     if (element == null) $$$reportNull$$$0(12);  super.loadState(element);
/*     */     
/* 236 */     this.myEnvData = EnvironmentVariablesData.readExternal(element);
/*     */     
/* 238 */     NodeJSRunConfigurationExtensionsManager.getInstance().readExternal((RunConfigurationBase)this, element);
/*     */   }
/*     */   
/*     */   public void addCoffeeScriptNodeOptionIfNeeded() {
/* 242 */     String pathToJsFile = getOptions().getPathToJsFile();
/* 243 */     if (pathToJsFile == null || !pathToJsFile.endsWith(".coffee")) {
/*     */       return;
/*     */     }
/*     */     
/* 247 */     String packageName = guessCoffeeScriptPackage(getProject());
/* 248 */     String nodeOption = "--require " + packageName + "/register";
/* 249 */     String nodeParameters = StringUtil.notNullize(getOptions().getNodeParameters());
/* 250 */     if (!nodeParameters.contains(nodeOption)) {
/* 251 */       nodeParameters = StringUtil.trimEnd(nodeParameters, " ");
/* 252 */       if (!nodeParameters.isEmpty()) nodeParameters = nodeParameters + " "; 
/* 253 */       getOptions().setNodeParameters(nodeParameters + nodeParameters);
/*     */     } 
/*     */   }
/*     */   
/*     */   @NotNull
/*     */   private static String guessCoffeeScriptPackage(@NotNull Project project) {
/* 259 */     if (project == null) $$$reportNull$$$0(13);  VirtualFile root = project.isDefault() ? null : ProjectUtil.guessProjectDir(project);
/* 260 */     VirtualFile packageJson = PackageJsonUtil.findChildPackageJsonFile(root);
/* 261 */     if (packageJson != null) {
/* 262 */       String oldPackageName = "coffee-script";
/* 263 */       if (PackageJsonData.getOrCreate(packageJson).isDependencyOfAnyType(oldPackageName)) {
/* 264 */         if (oldPackageName == null) $$$reportNull$$$0(14);  return oldPackageName;
/*     */       } 
/*     */     } 
/* 267 */     return "coffeescript";
/*     */   }
/*     */ 
/*     */   
/*     */   public Element getState() {
/* 272 */     Element element = new Element("state");
/* 273 */     writeExternal(element);
/*     */     
/* 275 */     if (!EnvironmentVariablesData.DEFAULT.equals(this.myEnvData)) {
/* 276 */       this.myEnvData.writeExternal(element);
/*     */     }
/*     */     
/* 279 */     NodeJSRunConfigurationExtensionsManager.getInstance().writeExternal((RunConfigurationBase)this, element);
/* 280 */     return element;
/*     */   }
/*     */ 
/*     */   
/*     */   @Nullable
/*     */   public RefactoringElementListener getRefactoringElementListener(PsiElement element) {
/* 286 */     return NodeJsRunConfigurationRefactoringHandler.createRefactoringElementListener(this, element);
/*     */   }
/*     */ 
/*     */   
/*     */   @Nullable
/*     */   public String getProgramParameters() {
/* 292 */     return getOptions().getNodeParameters();
/*     */   }
/*     */ 
/*     */   
/*     */   public void setProgramParameters(@Nullable String value) {
/* 297 */     getOptions().setNodeParameters(value);
/*     */   }
/*     */   
/*     */   @Nullable
/*     */   public String getApplicationParameters() {
/* 302 */     return getOptions().getApplicationParameters();
/*     */   }
/*     */   
/*     */   public void setApplicationParameters(@Nullable String applicationParameters) {
/* 306 */     getOptions().setApplicationParameters(applicationParameters);
/*     */   }
/*     */ 
/*     */   
/*     */   @NotNull
/*     */   public Map<String, String> getEnvs() {
/* 312 */     if (this.myEnvData.getEnvs() == null) $$$reportNull$$$0(15);  return this.myEnvData.getEnvs();
/*     */   }
/*     */ 
/*     */   
/*     */   public void setEnvs(@NotNull Map<String, String> envs) {
/* 317 */     if (envs == null) $$$reportNull$$$0(16);  this.myEnvData = EnvironmentVariablesData.create(envs, this.myEnvData.isPassParentEnvs());
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean isPassParentEnvs() {
/* 322 */     return this.myEnvData.isPassParentEnvs();
/*     */   }
/*     */ 
/*     */   
/*     */   public void setPassParentEnvs(boolean passParentEnvs) {
/* 327 */     this.myEnvData = EnvironmentVariablesData.create(this.myEnvData.getEnvs(), passParentEnvs);
/*     */   }
/*     */   
/*     */   @NotNull
/*     */   public EnvironmentVariablesData getEnvData() {
/* 332 */     if (this.myEnvData == null) $$$reportNull$$$0(17);  return this.myEnvData;
/*     */   }
/*     */   
/*     */   public void setEnvData(@NotNull EnvironmentVariablesData envData) {
/* 336 */     if (envData == null) $$$reportNull$$$0(18);  this.myEnvData = envData;
/*     */   }
/*     */   
/*     */   @Nullable
/*     */   public static NodeJsRunConfiguration getDefaultRunConfiguration(@NotNull Project project) {
/* 341 */     if (project == null) $$$reportNull$$$0(19);  RunManager runManager = RunManager.getInstance(project);
/* 342 */     RunnerAndConfigurationSettings settings = runManager.getConfigurationTemplate((ConfigurationFactory)NodeJsRunConfigurationType.getInstance());
/* 343 */     return (NodeJsRunConfiguration)ObjectUtils.tryCast(settings.getConfiguration(), NodeJsRunConfiguration.class);
/*     */   }
/*     */ 
/*     */   
/*     */   @Nullable
/*     */   public String getExePath() {
/* 349 */     return PathUtil.toSystemIndependentName(StringUtil.nullize(getRawExePath()));
/*     */   }
/*     */   
/*     */   public String getRawExePath() {
/* 353 */     NodeJsInterpreter interpreter = getInterpreter();
/* 354 */     if (interpreter == null) {
/* 355 */       return "";
/*     */     }
/* 357 */     return interpreter.getOldPath();
/*     */   }
/*     */ 
/*     */   
/*     */   @Nullable
/*     */   public String getInputPath() {
/* 363 */     return getOptions().getPathToJsFile();
/*     */   }
/*     */   
/*     */   public void setInputPath(@Nullable String value) {
/* 367 */     value = StringUtil.nullize(value);
/* 368 */     if (value == null) {
/* 369 */       getOptions().setPathToJsFile(null);
/*     */     } else {
/*     */       
/* 372 */       getOptions().setPathToJsFile(FileUtilRt.toSystemIndependentName(value));
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   @Nullable
/*     */   public String getEffectiveExePath() throws ExecutionException {
/* 379 */     NodeJsInterpreter interpreter = getInterpreter();
/* 380 */     NodeJsRemoteInterpreter remoteInterpreter = NodeJsRemoteInterpreter.tryCast(interpreter);
/* 381 */     if (remoteInterpreter != null) {
/* 382 */       NodeJSRemoteInterpreterManager manager = NodeJSRemoteInterpreterManager.getInstanceOrPrompt(
/* 383 */           NodeJSBundle.message("dialog.title.node.js.run.configuration", new Object[0]));
/* 384 */       if (manager != null) {
/*     */         try {
/* 386 */           if (manager.producesSshSdkCredentials(remoteInterpreter)) {
/* 387 */             return StringUtil.nullize(manager.getCredentialsByInterpreter(remoteInterpreter).getInterpreterPath());
/*     */           }
/*     */         }
/* 390 */         catch (InterruptedException e) {
/* 391 */           throw new ExecutionException(e);
/*     */         } 
/*     */       }
/*     */     } 
/* 395 */     return StringUtil.nullize(getRawExePath());
/*     */   }
/*     */ 
/*     */   
/*     */   @Nullable
/*     */   public String getEffectiveWorkingDirectory() {
/* 401 */     return PathUtil.toSystemDependentName(StringUtil.nullize(getWorkingDirectory()));
/*     */   }
/*     */ 
/*     */   
/*     */   @NotNull
/*     */   public String correctExePath(@NotNull String exePath) {
/* 407 */     if (exePath == null) $$$reportNull$$$0(20);  if (exePath == null) $$$reportNull$$$0(21);  return exePath;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   @NotNull
/*     */   public XDebugProcess createDebugProcess(@NotNull InetSocketAddress socketAddress, @NotNull XDebugSession session, @Nullable ExecutionResult executionResult, @NotNull ExecutionEnvironment environment) {
/* 416 */     if (socketAddress == null) $$$reportNull$$$0(22);  if (session == null) $$$reportNull$$$0(23);  if (environment == null) $$$reportNull$$$0(24);  if (NodeDebugProgramRunnerKt.createDebugProcess(this, socketAddress, session, executionResult) == null) $$$reportNull$$$0(25);  return (XDebugProcess)NodeDebugProgramRunnerKt.createDebugProcess(this, socketAddress, session, executionResult);
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean hasConfiguredDebugAddress() {
/* 421 */     return (NodeCommandLineUtil.findDebugPort(getProgramParameters()) != -1);
/*     */   }
/*     */ 
/*     */   
/*     */   @NotNull
/*     */   public InetSocketAddress computeDebugAddress(RunProfileState state) throws ExecutionException {
/* 427 */     int debugPort = NodeCommandLineUtil.findDebugPort(getProgramParameters());
/* 428 */     if (debugPort == -1) {
/*     */       try {
/* 430 */         debugPort = NetUtils.findAvailableSocketPort();
/*     */       }
/* 432 */       catch (IOException e) {
/* 433 */         throw new ExecutionException(XDebuggerBundle.message("error.message.cannot.find.available.port", new Object[0]), e);
/*     */       } 
/*     */     }
/*     */     
/* 437 */     if (state instanceof NodeCommandLineState) {
/* 438 */       String host = ((NodeCommandLineState)state).getDebugHost();
/* 439 */       if (host != null) return new InetSocketAddress(host, debugPort); 
/*     */     } 
/* 441 */     return new InetSocketAddress(NodeCommandLineUtil.getNodeLoopbackAddress(), debugPort);
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean canRun(@NotNull String executorId, @NotNull RunProfile profile) {
/* 446 */     if (executorId == null) $$$reportNull$$$0(26);  if (profile == null) $$$reportNull$$$0(27);  return (getOptions().getPathToJsFile() == null || !getOptions().getPathToJsFile().endsWith(".coffee"));
/*     */   }
/*     */ }


/* Location:              C:\Program Files\JetBrains\WebStorm 2021.1\plugins\NodeJS\lib\NodeJS.jar!\com\jetbrains\nodejs\run\NodeJsRunConfiguration.class
 * Java compiler version: 11 (55.0)
 * JD-Core Version:       1.1.3
 */