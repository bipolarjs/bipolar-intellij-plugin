/*     */ package org.bipolar.boilerplate.express;
/*     */ import com.intellij.execution.ExecutionException;
/*     */ import com.intellij.execution.RunManager;
/*     */ import com.intellij.execution.RunnerAndConfigurationSettings;
/*     */ import com.intellij.execution.configurations.ConfigurationFactory;
/*     */ import com.intellij.execution.configurations.GeneralCommandLine;
/*     */ import com.intellij.execution.filters.Filter;
/*     */ import com.intellij.execution.process.OSProcessHandler;
/*     */ import com.intellij.execution.process.ProcessAdapter;
/*     */ import com.intellij.execution.process.ProcessEvent;
/*     */ import com.intellij.execution.process.ProcessHandler;
/*     */ import com.intellij.execution.process.ProcessOutputType;
/*     */ import com.intellij.execution.process.ProcessTerminatedListener;
/*     */ import com.intellij.execution.ui.RunContentDescriptor;
/*     */ import com.intellij.ide.browsers.StartBrowserSettings;
/*     */ import com.intellij.ide.file.BatchFileChangeListener;
/*     */ import com.intellij.ide.util.projectWizard.SettingsStep;
/*     */ import com.intellij.javascript.nodejs.NodeCommandLineUtil;
/*     */ import com.intellij.javascript.nodejs.interpreter.NodeJsInterpreter;
/*     */ import com.intellij.javascript.nodejs.interpreter.NodeJsInterpreterField;
/*     */ import com.intellij.javascript.nodejs.interpreter.NodeJsInterpreterRef;
/*     */ import com.intellij.javascript.nodejs.packageJson.PackageJsonDependenciesExternalUpdateManager;
/*     */ import com.intellij.javascript.nodejs.util.NodePackage;
/*     */ import com.intellij.javascript.nodejs.util.NodePackageField;
/*     */ import com.intellij.lang.javascript.boilerplate.NpmPackageProjectGenerator;
/*     */ import com.intellij.lang.javascript.boilerplate.NpxPackageDescriptor;
/*     */ import com.intellij.notification.NotificationType;
/*     */ import com.intellij.openapi.application.ApplicationManager;
/*     */ import com.intellij.openapi.diagnostic.Logger;
/*     */ import com.intellij.openapi.progress.util.BackgroundTaskUtil;
/*     */ import com.intellij.openapi.project.Project;
/*     */ import com.intellij.openapi.startup.StartupManager;
/*     */ import com.intellij.openapi.util.Key;
/*     */ import com.intellij.openapi.util.Ref;
/*     */ import com.intellij.openapi.util.io.FileUtil;
/*     */ import com.intellij.openapi.util.text.StringUtil;
/*     */ import com.intellij.openapi.vfs.VfsUtil;
/*     */ import com.intellij.openapi.vfs.VfsUtilCore;
/*     */ import com.intellij.openapi.vfs.VirtualFile;
/*     */ import com.intellij.openapi.vfs.newvfs.RefreshQueue;
/*     */ import com.intellij.platform.ProjectGeneratorPeer;
/*     */ import com.intellij.terminal.TerminalExecutionConsole;
/*     */ import com.intellij.util.ArrayUtil;
/*     */ import com.intellij.util.ArrayUtilRt;
/*     */ import com.intellij.util.ExceptionUtil;
/*     */ import com.intellij.util.ObjectUtils;
/*     */ import com.intellij.util.containers.hash.LinkedHashMap;
/*     */ import com.intellij.util.text.SemVer;
/*     */ import com.intellij.util.ui.SwingHelper;
/*     */ import org.bipolar.NodeJSBundle;
/*     */ import org.bipolar.run.NodeJsRunConfiguration;
/*     */ import org.bipolar.run.NodeJsRunConfigurationType;
/*     */ import java.awt.Component;
/*     */ import java.io.File;
/*     */ import java.util.ArrayList;
/*     */ import java.util.Collections;
/*     */ import java.util.List;
/*     */ import java.util.Map;
/*     */ import java.util.Objects;
/*     */ import javax.swing.Icon;
/*     */ import javax.swing.JPanel;
/*     */
import org.jetbrains.annotations.NotNull;
/*     */ import org.jetbrains.annotations.Nullable;
/*     */ 
/*     */ public class ExpressAppProjectGenerator extends NpmPackageProjectGenerator {
/*  66 */   private static final Logger LOG = Logger.getInstance(ExpressAppProjectGenerator.class);
/*     */   
/*     */   private ProcessHandler myCurrentProcessHandler;
/*     */   
/*     */   @NotNull
/*     */   @Label
/*     */   public String getName() {
/*  73 */     if (NodeJSBundle.message("label.node.js.express.app", new Object[0]) == null) $$$reportNull$$$0(0);  return NodeJSBundle.message("label.node.js.express.app", new Object[0]);
/*     */   }
/*     */ 
/*     */   
/*     */   @DetailedDescription
/*     */   public String getDescription() {
/*  79 */     return NodeJSBundle.message("text.html.express.js.application.skeleton", new Object[0]);
/*     */   }
/*     */ 
/*     */   
/*     */   public Icon getIcon() {
/*  84 */     return JavaScriptLanguageIcons.Nodejs.Express;
/*     */   }
/*     */ 
/*     */   
/*     */   @Nullable
/*     */   public String getHelpId() {
/*  90 */     return "create.from.express-js.skeleton";
/*     */   }
/*     */ 
/*     */   
/*     */   @NotNull
/*     */   protected List<NpxPackageDescriptor.NpxCommand> getNpxCommands() {
/*  96 */     if (Collections.singletonList(new NpxPackageDescriptor.NpxCommand("express-generator", "express")) == null) $$$reportNull$$$0(1);  return Collections.singletonList(new NpxPackageDescriptor.NpxCommand("express-generator", "express"));
/*     */   }
/*     */ 
/*     */   
/*     */   @NotNull
/*     */   protected String executable(@NotNull NodePackage pkg) {
/* 102 */     if (pkg == null) $$$reportNull$$$0(2);  File binFile = pkg.findBinFile("express", "bin" + File.separator + "express-cli.js");
/* 103 */     return (binFile != null) ? binFile.getAbsolutePath() : "not-found-express-bin";
/*     */   }
/*     */ 
/*     */   
/*     */   protected Filter[] filters(@NotNull Project project, @NotNull VirtualFile baseDir) {
/* 108 */     if (project == null) $$$reportNull$$$0(3);  if (baseDir == null) $$$reportNull$$$0(4);  if (Filter.EMPTY_ARRAY == null) $$$reportNull$$$0(5);  return Filter.EMPTY_ARRAY;
/*     */   }
/*     */ 
/*     */   
/*     */   protected String[] generatorArgs(@NotNull Project project, @NotNull VirtualFile baseDir) {
/* 113 */     if (project == null) $$$reportNull$$$0(6);  if (baseDir == null) $$$reportNull$$$0(7);  if (ArrayUtilRt.EMPTY_STRING_ARRAY == null) $$$reportNull$$$0(8);  return ArrayUtilRt.EMPTY_STRING_ARRAY;
/*     */   }
/*     */ 
/*     */   
/*     */   protected String[] generatorArgs(@NotNull Project project, @NotNull VirtualFile baseDir, @NotNull NpmPackageProjectGenerator.Settings settings) {
/* 118 */     if (project == null) $$$reportNull$$$0(9);  if (baseDir == null) $$$reportNull$$$0(10);  if (settings == null) $$$reportNull$$$0(11);  if (ArrayUtil.toStringArray(getExpressParams(settings)) == null) $$$reportNull$$$0(12);  return ArrayUtil.toStringArray(getExpressParams(settings));
/*     */   }
/*     */ 
/*     */   
/*     */   protected void customizeModule(@NotNull VirtualFile baseDir, ContentEntry entry) {
/* 123 */     if (baseDir == null) $$$reportNull$$$0(13); 
/*     */   }
/*     */   
/*     */   @NotNull
/*     */   protected String packageName() {
/* 128 */     return "express-generator";
/*     */   }
/*     */ 
/*     */   
/*     */   @NotNull
/*     */   protected String presentablePackageName() {
/* 134 */     return "express-&generator:";
/*     */   }
/*     */ 
/*     */   
/*     */   @NotNull
/*     */   public ProjectGeneratorPeer<NpmPackageProjectGenerator.Settings> createPeer() {
/* 140 */     final ExpressAppGeneratorPeerHelper helper = new ExpressAppGeneratorPeerHelper();
/* 141 */     return (ProjectGeneratorPeer<NpmPackageProjectGenerator.Settings>)new NpmPackageProjectGenerator.NpmPackageGeneratorPeer()
/*     */       {
/*     */         protected JPanel createPanel() {
/* 144 */           return SwingHelper.newLeftAlignedVerticalPanel(new Component[] { super.createPanel(), this.val$helper.getOptionsPanel() });
/*     */         }
/*     */ 
/*     */         
/*     */         public void buildUI(@NotNull SettingsStep settingsStep) {
/* 149 */           if (settingsStep == null) $$$reportNull$$$0(0);  super.buildUI(settingsStep);
/* 150 */           helper.buildUI(settingsStep);
/*     */         }
/*     */ 
/*     */         
/*     */         @NotNull
/*     */         protected NodePackageField createAndInitPackageField(@NotNull NodeJsInterpreterField interpreterField) {
/* 156 */           if (interpreterField == null) $$$reportNull$$$0(1);  NodePackageField packageField = super.createAndInitPackageField(interpreterField);
/* 157 */           helper.setPackageField(packageField);
/* 158 */           if (packageField == null) $$$reportNull$$$0(2);  return packageField;
/*     */         }
/*     */ 
/*     */         
/*     */         @NotNull
/*     */         public NpmPackageProjectGenerator.Settings getSettings() {
/* 164 */           if (helper.getSettings(super.getSettings()) == null) $$$reportNull$$$0(3);  return helper.getSettings(super.getSettings());
/*     */         }
/*     */       };
/*     */   }
/*     */ 
/*     */   
/*     */   @NotNull
/*     */   protected Runnable postInstall(@NotNull Project project, @NotNull VirtualFile baseDir, File workingDir) {
/* 172 */     if (project == null) $$$reportNull$$$0(14);  if (baseDir == null) $$$reportNull$$$0(15); 
/*     */     if ((() -> {
/*     */         
/*     */         try { doPostInstall(project, baseDir, Objects.<File>requireNonNull(workingDir)); }
/* 176 */         catch (ExecutionException e) { String message = ExceptionUtil.getMessage((Throwable)e); NpmPackageProjectGenerator.NOTIFICATION_GROUP.createNotification(NodeJSBundle.message("generator.express.cannot_generate.notification.title", new Object[] { getName() }), StringUtil.notNullize(message), NotificationType.ERROR, null).notify(project); LOG.warn((Throwable)e); }  super.postInstall(project, baseDir, workingDir).run(); }) == null) $$$reportNull$$$0(16);  return () -> { try { doPostInstall(project, baseDir, Objects.<File>requireNonNull(workingDir)); } catch (ExecutionException e)
/*     */         { String message = ExceptionUtil.getMessage((Throwable)e);
/*     */           NpmPackageProjectGenerator.NOTIFICATION_GROUP.createNotification(NodeJSBundle.message("generator.express.cannot_generate.notification.title", new Object[] { getName() }), StringUtil.notNullize(message), NotificationType.ERROR, null).notify(project);
/*     */           LOG.warn((Throwable)e); }
/*     */         
/*     */         super.postInstall(project, baseDir, workingDir).run();
/*     */       };
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private void doPostInstall(@NotNull final Project project, @NotNull final VirtualFile baseDir, @NotNull File workingDir) throws ExecutionException {
/* 190 */     if (project == null) $$$reportNull$$$0(17);  if (baseDir == null) $$$reportNull$$$0(18);  if (workingDir == null) $$$reportNull$$$0(19);  ProcessHandler generatorProcessHandler = Objects.<ProcessHandler>requireNonNull(this.myCurrentProcessHandler);
/* 191 */     int exitCode = ((Integer)ObjectUtils.notNull(generatorProcessHandler.getExitCode(), Integer.valueOf(-1))).intValue();
/* 192 */     if (exitCode != 0) {
/* 193 */       throw new ExecutionException(NodeJSBundle.message("generator.express.see_errors_in_console.dialog.message", new Object[] { Integer.valueOf(exitCode) }));
/*     */     }
/* 195 */     NodeJsInterpreterRef interpreterRef = NodeJsInterpreterManager.getInstance(project).getInterpreterRef();
/* 196 */     NodeJsInterpreter interpreter = interpreterRef.resolveNotNull(project);
/* 197 */     NodePackage npmPkg = NpmManager.getInstance(project).getPackageOrThrow();
/* 198 */     GeneralCommandLine commandLine = NodeCommandLineUtil.createCommandLine(Boolean.valueOf(true));
/* 199 */     NpmUtil.configureNpmCommandLine(commandLine, workingDir, interpreter, npmPkg, NpmCommand.INSTALL, Collections.emptyList());
/* 200 */     OSProcessHandler oSProcessHandler = NodeCommandLineUtil.createProcessHandler(commandLine, true);
/* 201 */     ((BatchFileChangeListener)BackgroundTaskUtil.syncPublisher(BatchFileChangeListener.TOPIC)).batchChangeStarted(project, "npm install");
/* 202 */     final Runnable installDone = PackageJsonDependenciesExternalUpdateManager.getInstance(project).externalUpdateStarted(null, null);
/* 203 */     ProcessTerminatedListener.attach((ProcessHandler)oSProcessHandler);
/* 204 */     ApplicationManager.getApplication().invokeLater(() -> {
/*     */           TerminalExecutionConsole console = findConsole(project);
/*     */           this.myCurrentProcessHandler = null;
/*     */           if (console != null) {
/*     */             console.attachToProcess(processHandler);
/*     */           }
/*     */           processHandler.notifyTextAvailable("\nInstalling dependencies...\n\n", (Key)ProcessOutputType.SYSTEM);
/*     */           processHandler.startNotify();
/*     */         });
/* 213 */     oSProcessHandler.addProcessListener((ProcessListener)new ProcessAdapter()
/*     */         {
/*     */           public void processTerminated(@NotNull ProcessEvent event) {
/* 216 */             if (event == null) $$$reportNull$$$0(0);  ((BatchFileChangeListener)BackgroundTaskUtil.syncPublisher(BatchFileChangeListener.TOPIC)).batchChangeCompleted(project);
/* 217 */             installDone.run();
/* 218 */             RefreshQueue.getInstance().refresh(true, true, () -> { if (!project.isDisposed()) StartupManager.getInstance(project).runWhenProjectIsInitialized(());  }new VirtualFile[] { this.val$baseDir });
/*     */           }
/*     */         });
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private static void configureProject(@NotNull Project project, @NotNull VirtualFile baseDir) {
/* 228 */     if (project == null) $$$reportNull$$$0(20);  if (baseDir == null) $$$reportNull$$$0(21);  ToggleNodeCoreCodingAssistanceAction.enableNodeCoreLibrary(project, null);
/* 229 */     createRunConfiguration(project, baseDir);
/*     */   }
/*     */   
/*     */   private static void createRunConfiguration(@NotNull Project project, @NotNull VirtualFile baseDir) {
/* 233 */     if (project == null) $$$reportNull$$$0(22);  if (baseDir == null) $$$reportNull$$$0(23);  Ref<VirtualFile> appFileRef = Ref.create();
/* 234 */     String name = FileUtil.toSystemDependentName("bin/www");
/* 235 */     ApplicationManager.getApplication().runReadAction(() -> {
/*     */           for (RunnerAndConfigurationSettings settings : RunManager.getInstance(project).getAllSettings()) {
/*     */             if (name.equals(settings.getName())) {
/*     */               return;
/*     */             }
/*     */           } 
/*     */           appFileRef.set(VfsUtil.findRelativeFile(baseDir, new String[] { "bin", "www" }));
/*     */         });
/* 243 */     if (appFileRef.isNull()) {
/*     */       return;
/*     */     }
/* 246 */     ApplicationManager.getApplication().runWriteAction(() -> {
/*     */           RunnerAndConfigurationSettings rac = RunManager.getInstance(project).createConfiguration("", (ConfigurationFactory) NodeJsRunConfigurationType.getInstance());
/*     */           NodeJsRunConfiguration runConfig = (NodeJsRunConfiguration)ObjectUtils.tryCast(rac.getConfiguration(), NodeJsRunConfiguration.class);
/*     */           if (runConfig == null) {
/*     */             return;
/*     */           }
/*     */           VirtualFile appFile = (VirtualFile)appFileRef.get();
/*     */           runConfig.setName(name);
/*     */           runConfig.setWorkingDirectory(baseDir.getPath());
/*     */           runConfig.setInputPath(VfsUtilCore.getRelativePath(appFile, baseDir, File.separatorChar));
/*     */           LinkedHashMap<String, String> linkedHashMap = new LinkedHashMap();
/*     */           linkedHashMap.putAll(runConfig.getEnvs());
/*     */           linkedHashMap.put("DEBUG", createAppName(baseDir.getName()) + ":*");
/*     */           runConfig.setEnvs((Map)linkedHashMap);
/*     */           StartBrowserSettings browserSettings = new StartBrowserSettings();
/*     */           browserSettings.setUrl("http://localhost:3000/");
/*     */           runConfig.setStartBrowserSettings(browserSettings);
/*     */           RunManager runManager = RunManager.getInstance(project);
/*     */           rac.storeInDotIdeaFolder();
/*     */           runManager.addConfiguration(rac);
/*     */           runManager.setSelectedConfiguration(rac);
/*     */         });
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   @NotNull
/*     */   public static String createAppName(@NotNull String directoryName) {
/* 276 */     if (directoryName == null) $$$reportNull$$$0(24);  if (StringUtil.toLowerCase(directoryName
/* 277 */         .replaceAll("[^A-Za-z0-9.-]+", "-")
/* 278 */         .replaceAll("^[-_.]+|-+$", "")) == null) $$$reportNull$$$0(25);  return StringUtil.toLowerCase(directoryName.replaceAll("[^A-Za-z0-9.-]+", "-").replaceAll("^[-_.]+|-+$", ""));
/*     */   }
/*     */ 
/*     */   
/*     */   protected void onProcessHandlerCreated(@NotNull ProcessHandler processHandler) {
/* 283 */     if (processHandler == null) $$$reportNull$$$0(26);  this.myCurrentProcessHandler = processHandler;
/*     */   }
/*     */   
/*     */   @Nullable
/*     */   private TerminalExecutionConsole findConsole(@NotNull Project project) {
/* 288 */     if (project == null) $$$reportNull$$$0(27);  List<RunContentDescriptor> descriptors = RunContentManager.getInstance(project).getAllDescriptors();
/* 289 */     for (RunContentDescriptor descriptor : descriptors) {
/* 290 */       TerminalExecutionConsole console = (TerminalExecutionConsole)ObjectUtils.tryCast(descriptor.getExecutionConsole(), TerminalExecutionConsole.class);
/* 291 */       if (console != null && descriptor.getProcessHandler() == this.myCurrentProcessHandler) {
/* 292 */         return console;
/*     */       }
/*     */     } 
/* 295 */     return null;
/*     */   }
/*     */   
/*     */   @NotNull
/*     */   private static List<String> getExpressParams(@NotNull NpmPackageProjectGenerator.Settings settings) {
/* 300 */     if (settings == null) $$$reportNull$$$0(28);  List<String> params = new ArrayList<>();
/*     */ 
/*     */     
/* 303 */     params.add("--force");
/* 304 */     ExpressAppSettings expressSettings = ExpressAppGeneratorPeerHelper.getExpressSettings(settings);
/*     */     
/* 306 */     ExpressStylesheetEngine stylesheetEngine = expressSettings.getStylesheetEngine();
/* 307 */     String option = stylesheetEngine.getCliOption();
/* 308 */     if (option != null) {
/* 309 */       params.add("--css");
/* 310 */       params.add(option);
/*     */     } 
/* 312 */     ExpressTemplateEngine templateEngine = expressSettings.getTemplateEngine();
/* 313 */     SemVer version = expressSettings.getPackageVersion();
/* 314 */     if (version == null || version.isGreaterOrEqualThan(4, 15, 0)) {
/* 315 */       if (templateEngine == ExpressTemplateEngine.NO_VIEW) {
/* 316 */         params.add("--no-view");
/*     */       } else {
/*     */         
/* 319 */         params.add("--view");
/* 320 */         params.add(templateEngine.getCliOption());
/*     */       }
/*     */     
/*     */     }
/* 324 */     else if (templateEngine != ExpressTemplateEngine.JADE) {
/* 325 */       if (templateEngine == ExpressTemplateEngine.EJS) {
/* 326 */         params.add("--ejs");
/*     */       }
/* 328 */       else if (templateEngine == ExpressTemplateEngine.HBS) {
/* 329 */         params.add("--hbs");
/*     */       }
/* 331 */       else if (templateEngine == ExpressTemplateEngine.HOGAN) {
/* 332 */         params.add("--hogan");
/*     */       } else {
/*     */         
/* 335 */         throw new RuntimeException("Unexpected template option: " + templateEngine);
/*     */       } 
/*     */     } 
/*     */     
/* 339 */     if (params == null) $$$reportNull$$$0(29);  return params;
/*     */   }
/*     */ }


/* Location:              C:\Program Files\JetBrains\WebStorm 2021.1\plugins\NodeJS\lib\NodeJS.jar!\com\jetbrains\nodejs\boilerplate\express\ExpressAppProjectGenerator.class
 * Java compiler version: 11 (55.0)
 * JD-Core Version:       1.1.3
 */