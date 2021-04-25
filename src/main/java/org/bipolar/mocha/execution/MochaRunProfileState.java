/*     */ package org.bipolar.mocha.execution;
/*     */ 
/*     */ import com.intellij.execution.DefaultExecutionResult;
/*     */ import com.intellij.execution.ExecutionException;
/*     */ import com.intellij.execution.ExecutionResult;
/*     */ import com.intellij.execution.configurations.GeneralCommandLine;
/*     */ import com.intellij.execution.configurations.ParametersList;
/*     */ import com.intellij.execution.filters.Filter;
/*     */ import com.intellij.execution.process.OSProcessHandler;
/*     */ import com.intellij.execution.process.ProcessHandler;
/*     */ import com.intellij.execution.process.ProcessTerminatedListener;
/*     */ import com.intellij.execution.runners.ExecutionEnvironment;
/*     */ import com.intellij.execution.testframework.TestConsoleProperties;
/*     */ import com.intellij.execution.testframework.sm.SMTestRunnerConnectionUtil;
/*     */ import com.intellij.execution.testframework.ui.BaseTestsOutputConsoleView;
/*     */ import com.intellij.execution.ui.ConsoleView;
/*     */ import com.intellij.execution.ui.ExecutionConsole;
/*     */ import com.intellij.javascript.debugger.CommandLineDebugConfigurator;
/*     */ import com.intellij.javascript.nodejs.NodeCommandLineUtil;
/*     */ import com.intellij.javascript.nodejs.NodeConsoleAdditionalFilter;
/*     */ import com.intellij.javascript.nodejs.NodeStackTraceFilter;
/*     */ import com.intellij.javascript.nodejs.debug.NodeLocalDebuggableRunProfileStateSync;
/*     */ import com.intellij.javascript.nodejs.interpreter.NodeInterpreterUtil;
/*     */ import com.intellij.javascript.nodejs.interpreter.NodeJsInterpreter;
/*     */ import com.intellij.javascript.nodejs.interpreter.local.NodeJsLocalInterpreter;
/*     */ import com.intellij.javascript.nodejs.library.yarn.YarnPnpNodePackage;
/*     */ import com.intellij.javascript.nodejs.util.NodePackage;
/*     */ import com.intellij.javascript.testing.JSTestRunnerUtil;
/*     */ import com.intellij.lang.javascript.buildTools.npm.PackageJsonUtil;
/*     */ import com.intellij.openapi.actionSystem.AnAction;
/*     */ import com.intellij.openapi.project.Project;
/*     */ import com.intellij.openapi.util.Pair;
/*     */ import com.intellij.openapi.util.io.FileUtil;
/*     */ import com.intellij.openapi.util.text.StringUtil;
/*     */ import com.intellij.util.execution.ParametersListUtil;
/*     */ import org.bipolar.NodeJSBundle;
/*     */ import org.bipolar.mocha.MochaUtil;
/*     */ import org.bipolar.mocha.coverage.MochaCoverageRunState;
/*     */ import org.bipolar.util.NodeJsCodeLocator;
/*     */ import java.io.File;
/*     */ import java.io.IOException;
/*     */ import java.nio.charset.StandardCharsets;
/*     */ import java.util.ArrayList;
/*     */ import java.util.List;
/*     */
import org.jetbrains.annotations.NotNull;
/*     */ import org.jetbrains.annotations.Nullable;
/*     */ 
/*     */ public class MochaRunProfileState
/*     */   extends NodeLocalDebuggableRunProfileStateSync {
/*     */   private final Project myProject;
/*     */   private final MochaRunConfiguration myRunConfiguration;
/*     */   private final ExecutionEnvironment myEnv;
/*     */   private final NodePackage myMochaPackage;
/*     */   private final MochaRunSettings myRunSettings;
/*     */   private final MochaCoverageRunState myCoverageRunState;
/*     */   private List<List<String>> myRerunActionFailedTests;
/*     */   
/*     */   public MochaRunProfileState(@NotNull Project project, @NotNull MochaRunConfiguration runConfiguration, @NotNull ExecutionEnvironment env, @NotNull NodePackage mochaPackage, @NotNull MochaRunSettings runSettings) {
/*  59 */     this.myProject = project;
/*  60 */     this.myRunConfiguration = runConfiguration;
/*  61 */     this.myEnv = env;
/*  62 */     this.myMochaPackage = mochaPackage;
/*  63 */     this.myRunSettings = runSettings;
/*  64 */     this.myCoverageRunState = MochaCoverageRunState.create(env, this.myMochaPackage, runSettings);
/*     */   }
/*     */ 
/*     */   
/*     */   @NotNull
/*     */   protected ExecutionResult executeSync(@Nullable CommandLineDebugConfigurator configurator) throws ExecutionException {
/*  70 */     NodeJsInterpreter interpreter = this.myRunSettings.getInterpreterRef().resolveNotNull(this.myProject);
/*  71 */     GeneralCommandLine commandLine = NodeCommandLineUtil.createCommandLineForTestTools();
/*  72 */     NodeCommandLineUtil.configureCommandLine(commandLine, configurator, interpreter, debugMode -> configureCommandLine(commandLine, interpreter, debugMode.booleanValue()));
/*     */ 
/*     */     
/*  75 */     OSProcessHandler processHandler = NodeCommandLineUtil.createProcessHandler(commandLine, false);
/*  76 */     MochaConsoleProperties consoleProperties = this.myRunConfiguration.createTestConsoleProperties(this.myEnv
/*  77 */         .getExecutor(), this.myRunSettings.getUi(), NodeCommandLineUtil.shouldUseTerminalConsole((ProcessHandler)processHandler));
/*     */     
/*  79 */     ConsoleView consoleView = createSMTRunnerConsoleView(commandLine.getWorkDirectory(), consoleProperties);
/*  80 */     ProcessTerminatedListener.attach((ProcessHandler)processHandler);
/*  81 */     consoleView.attachToProcess((ProcessHandler)processHandler);
/*     */     
/*  83 */     DefaultExecutionResult executionResult = new DefaultExecutionResult((ExecutionConsole)consoleView, (ProcessHandler)processHandler);
/*  84 */     executionResult.setRestartActions(new AnAction[] { (AnAction)consoleProperties.createRerunFailedTestsAction(consoleView) });
/*  85 */     if (executionResult == null) $$$reportNull$$$0(5);  return (ExecutionResult)executionResult;
/*     */   }
/*     */   
/*     */   @NotNull
/*     */   private ConsoleView createSMTRunnerConsoleView(@Nullable File workingDirectory, @NotNull MochaConsoleProperties consoleProperties) {
/*  90 */     if (consoleProperties == null) $$$reportNull$$$0(6);  BaseTestsOutputConsoleView baseTestsOutputConsoleView = SMTestRunnerConnectionUtil.createConsole(consoleProperties.getTestFrameworkName(), (TestConsoleProperties)consoleProperties);
/*  91 */     consoleProperties.addStackTraceFilter((Filter)new NodeStackTraceFilter(this.myProject, workingDirectory));
/*  92 */     for (Filter filter : consoleProperties.getStackTrackFilters()) {
/*  93 */       baseTestsOutputConsoleView.addMessageFilter(filter);
/*     */     }
/*  95 */     baseTestsOutputConsoleView.addMessageFilter((Filter)new NodeConsoleAdditionalFilter(this.myProject, workingDirectory));
/*  96 */     if (baseTestsOutputConsoleView == null) $$$reportNull$$$0(7);  return (ConsoleView)baseTestsOutputConsoleView;
/*     */   }
/*     */   
/*     */   private void configureCommandLine(@NotNull GeneralCommandLine commandLine, @NotNull NodeJsInterpreter interpreter, boolean debugMode) throws ExecutionException {
/* 100 */     if (commandLine == null) $$$reportNull$$$0(8);  if (interpreter == null) $$$reportNull$$$0(9);  List<String> nodeOptions = new ArrayList<>(commandLine.getParametersList().getParameters());
/* 101 */     commandLine.getParametersList().clearAll();
/* 102 */     commandLine.setCharset(StandardCharsets.UTF_8);
/* 103 */     if (!StringUtil.isEmptyOrSpaces(this.myRunSettings.getWorkingDir())) {
/* 104 */       commandLine.withWorkDirectory(this.myRunSettings.getWorkingDir());
/*     */     }
/* 106 */     NodeCommandLineUtil.configureUsefulEnvironment(commandLine);
/* 107 */     NodeCommandLineUtil.prependNodeDirToPATH(commandLine, interpreter);
/* 108 */     this.myRunSettings.getEnvData().configureCommandLine(commandLine, true);
/*     */     
/* 110 */     boolean separateMochaArgs = false;
/* 111 */     if (this.myCoverageRunState != null) {
/* 112 */       separateMochaArgs = this.myCoverageRunState.configure(commandLine, interpreter);
/*     */     }
/*     */     
/* 115 */     if (this.myMochaPackage instanceof YarnPnpNodePackage) {
/* 116 */       ((YarnPnpNodePackage)this.myMochaPackage).addYarnRunToCommandLine(commandLine, this.myProject, interpreter, null);
/*     */     } else {
/*     */       
/* 119 */       commandLine.addParameter(getMochaMainJsFile(interpreter, this.myMochaPackage).getAbsolutePath());
/*     */     } 
/*     */     
/* 122 */     if (MochaUtil.isVueCliService(this.myMochaPackage)) {
/* 123 */       commandLine.addParameter("test:unit");
/*     */     }
/*     */     
/* 126 */     commandLine.addParameters(nodeOptions);
/* 127 */     commandLine.addParameters(ParametersListUtil.parse(this.myRunSettings.getNodeOptions().trim()));
/*     */     
/* 129 */     if (separateMochaArgs) {
/* 130 */       commandLine.addParameter("--");
/*     */     }
/*     */     
/* 133 */     List<String> extraMochaOptionList = ParametersListUtil.parse(this.myRunSettings.getExtraMochaOptions().trim());
/* 134 */     commandLine.addParameters(extraMochaOptionList);
/*     */     
/* 136 */     if (debugMode) {
/*     */ 
/*     */ 
/*     */       
/* 140 */       commandLine.addParameter("--timeout");
/* 141 */       commandLine.addParameter("0");
/*     */     } 
/*     */ 
/*     */ 
/*     */     
/* 146 */     commandLine.addParameter("--ui");
/* 147 */     commandLine.addParameter(this.myRunSettings.getUi());
/*     */     
/* 149 */     commandLine.addParameter("--reporter");
/* 150 */     commandLine.addParameter(getMochaReporterFile().getAbsolutePath());
/*     */     
/* 152 */     MochaTestKind testKind = this.myRunSettings.getTestKind();
/* 153 */     if (MochaTestKind.DIRECTORY == testKind) {
/* 154 */       commandLine.addParameter(FileUtil.toSystemDependentName(this.myRunSettings.getTestDirPath()));
/* 155 */       if (this.myRunSettings.isRecursive())
/*     */       {
/* 157 */         commandLine.addParameter("--recursive");
/*     */       }
/*     */     }
/* 160 */     else if (MochaTestKind.PATTERN == testKind) {
/* 161 */       String pattern = this.myRunSettings.getTestFilePattern();
/* 162 */       if (!StringUtil.isEmptyOrSpaces(pattern)) {
/* 163 */         commandLine.addParameters(ParametersList.parse(NodeInterpreterUtil.toRemoteName(pattern, interpreter)));
/*     */       }
/*     */     }
/* 166 */     else if (MochaTestKind.TEST_FILE == testKind || MochaTestKind.SUITE == testKind || MochaTestKind.TEST == testKind) {
/* 167 */       commandLine.addParameter(FileUtil.toSystemDependentName(this.myRunSettings.getTestFilePath()));
/*     */     } 
/* 169 */     String grepPattern = getGrepPattern(testKind);
/* 170 */     if (grepPattern != null) {
/* 171 */       commandLine.addParameter("--grep");
/* 172 */       commandLine.addParameter(grepPattern);
/*     */     } 
/*     */   }
/*     */   
/*     */   @Nullable
/*     */   private String getGrepPattern(@NotNull MochaTestKind testKind) {
/* 178 */     if (testKind == null) $$$reportNull$$$0(10);  if (this.myRerunActionFailedTests != null) {
/* 179 */       return JSTestRunnerUtil.getTestsPattern(this.myRerunActionFailedTests, false);
/*     */     }
/* 181 */     if (MochaTestKind.SUITE == testKind) {
/* 182 */       return JSTestRunnerUtil.buildTestNamesPattern(this.myProject, this.myRunSettings
/* 183 */           .getTestFilePath(), this.myRunSettings
/* 184 */           .getSuiteNames(), true);
/*     */     }
/*     */     
/* 187 */     if (MochaTestKind.TEST == testKind) {
/* 188 */       return JSTestRunnerUtil.buildTestNamesPattern(this.myProject, this.myRunSettings
/* 189 */           .getTestFilePath(), this.myRunSettings
/* 190 */           .getTestNames(), false);
/*     */     }
/*     */     
/* 193 */     return null;
/*     */   }
/*     */   
/*     */   @NotNull
/*     */   public MochaRunSettings getRunSettings() {
/* 198 */     if (this.myRunSettings == null) $$$reportNull$$$0(11);  return this.myRunSettings;
/*     */   }
/*     */   
/*     */   @Nullable
/*     */   public MochaCoverageRunState getCoverageRunState() {
/* 203 */     return this.myCoverageRunState;
/*     */   }
/*     */   
/*     */   public void setFailedTests(@NotNull List<List<String>> rerunActionFailedTests) {
/* 207 */     if (rerunActionFailedTests == null) $$$reportNull$$$0(12);  this.myRerunActionFailedTests = rerunActionFailedTests;
/*     */   }
/*     */   
/*     */   @NotNull
/*     */   public static File getMochaMainJsFile(@NotNull NodeJsInterpreter interpreter, @NotNull NodePackage mochaPackage) throws ExecutionException {
/* 212 */     if (interpreter == null) $$$reportNull$$$0(13);  if (mochaPackage == null) $$$reportNull$$$0(14);  String packageName = mochaPackage.getName();
/* 213 */     if ("electron-mocha".equals(packageName)) {
/*     */       File mainJsFile;
/* 215 */       if (interpreter instanceof NodeJsLocalInterpreter && ((NodeJsLocalInterpreter)interpreter).isElectron()) {
/* 216 */         mainJsFile = new File(mochaPackage.getSystemDependentPath(), "index.js");
/*     */       } else {
/*     */         
/* 219 */         mainJsFile = new File(mochaPackage.getSystemDependentPath(), "bin" + File.separator + "electron-mocha");
/*     */       } 
/* 221 */       if (mainJsFile.isFile()) {
/* 222 */         if (mainJsFile == null) $$$reportNull$$$0(15);  return mainJsFile;
/*     */       } 
/*     */     } 
/* 225 */     List<Pair<String, String>> binaries = new ArrayList<>();
/* 226 */     if (MochaUtil.isVueCliService(mochaPackage)) {
/* 227 */       binaries.add(Pair.create("vue-cli-service", "bin/vue-cli-service.js"));
/*     */     }
/* 229 */     else if ("mocha-webpack".equals(packageName)) {
/* 230 */       binaries.add(Pair.create("mocha-webpack", "bin/mocha-webpack"));
/*     */     } else {
/*     */       
/* 233 */       binaries.add(Pair.create(PackageJsonUtil.guessDefaultBinaryNameOfDependency(mochaPackage), null));
/*     */     } 
/* 235 */     binaries.add(Pair.create("mocha", "bin/mocha"));
/* 236 */     for (Pair<String, String> binary : binaries) {
/* 237 */       File mainJsFile = mochaPackage.findBinFile((String)binary.first, (String)binary.second);
/* 238 */       if (mainJsFile != null && mainJsFile.isFile()) {
/* 239 */         if (mainJsFile == null) $$$reportNull$$$0(16);  return mainJsFile;
/*     */       } 
/*     */     } 
/* 242 */     throw new ExecutionException(NodeJSBundle.message("rc.mocha.package_bin_file_not_found.message", new Object[] { packageName }));
/*     */   }
/*     */   
/*     */   @NotNull
/*     */   private static File getMochaReporterFile() throws ExecutionException {
/*     */     try {
/* 248 */       if (NodeJsCodeLocator.getFileRelativeToJsDir("mocha-intellij/lib/mochaIntellijReporter.js") == null) $$$reportNull$$$0(17);  return NodeJsCodeLocator.getFileRelativeToJsDir("mocha-intellij/lib/mochaIntellijReporter.js");
/*     */     }
/* 250 */     catch (IOException e) {
/* 251 */       throw new ExecutionException(NodeJSBundle.message("rc.mocha.intellij_mocha_reporter_not_found.message", new Object[0]), e);
/*     */     } 
/*     */   }
/*     */ }


/* Location:              C:\Program Files\JetBrains\WebStorm 2021.1\plugins\NodeJS\lib\NodeJS.jar!\com\jetbrains\nodejs\mocha\execution\MochaRunProfileState.class
 * Java compiler version: 11 (55.0)
 * JD-Core Version:       1.1.3
 */