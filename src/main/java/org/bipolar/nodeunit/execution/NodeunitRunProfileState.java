/*     */ package org.bipolar.nodeunit.execution;
/*     */ import com.intellij.execution.DefaultExecutionResult;
/*     */ import com.intellij.execution.ExecutionException;
/*     */ import com.intellij.execution.ExecutionResult;
/*     */ import com.intellij.execution.Executor;
/*     */ import com.intellij.execution.configurations.GeneralCommandLine;
/*     */ import com.intellij.execution.configurations.RunConfiguration;
/*     */ import com.intellij.execution.configurations.RunProfileState;
/*     */ import com.intellij.execution.filters.Filter;
/*     */ import com.intellij.execution.process.ProcessHandler;
/*     */ import com.intellij.execution.process.ProcessTerminatedListener;
/*     */ import com.intellij.execution.runners.ExecutionEnvironment;
/*     */ import com.intellij.execution.testframework.TestConsoleProperties;
/*     */ import com.intellij.execution.testframework.autotest.ToggleAutoTestAction;
/*     */ import com.intellij.execution.testframework.sm.SMTestRunnerConnectionUtil;
/*     */ import com.intellij.execution.testframework.sm.runner.SMTRunnerConsoleProperties;
/*     */ import com.intellij.execution.testframework.sm.runner.SMTestLocator;
/*     */ import com.intellij.execution.testframework.sm.runner.ui.SMTRunnerConsoleView;
/*     */ import com.intellij.execution.ui.ConsoleView;
/*     */ import com.intellij.execution.ui.ExecutionConsole;
/*     */ import com.intellij.javascript.nodejs.NodeCommandLineUtil;
/*     */ import com.intellij.javascript.nodejs.NodeConsoleAdditionalFilter;
/*     */ import com.intellij.javascript.nodejs.NodeStackTraceFilter;
/*     */ import com.intellij.javascript.nodejs.debug.NodeLocalDebugRunProfileState;
/*     */ import com.intellij.javascript.nodejs.interpreter.NodeJsInterpreter;
/*     */ import com.intellij.javascript.nodejs.interpreter.local.NodeJsLocalInterpreter;
/*     */ import com.intellij.javascript.testing.JsTestConsoleProperties;
/*     */ import com.intellij.openapi.actionSystem.AnAction;
/*     */ import com.intellij.util.config.AbstractProperty;
/*     */ import org.bipolar.NodeJSBundle;
/*     */ import org.bipolar.util.NodeJsCodeLocator;
/*     */ import java.io.File;
/*     */ import java.io.IOException;
/*     */ import java.nio.charset.StandardCharsets;
/*     */ import org.jetbrains.annotations.NotNull;
/*     */ 
/*     */ public class NodeunitRunProfileState implements RunProfileState, NodeLocalDebugRunProfileState {
/*     */   private final ExecutionEnvironment myEnvironment;
/*     */   
/*     */   protected NodeunitRunProfileState(@NotNull ExecutionEnvironment environment, @NotNull NodeunitSettings settings) {
/*  41 */     this.myEnvironment = environment;
/*  42 */     this.mySettings = settings;
/*     */   }
/*     */   private final NodeunitSettings mySettings;
/*     */   
/*     */   @NotNull
/*     */   public ExecutionResult execute(int debugPort) throws ExecutionException {
/*  48 */     NodeJsInterpreter interpreter = this.mySettings.getInterpreterRef().resolve(this.myEnvironment.getProject());
/*  49 */     NodeJsLocalInterpreter localInterpreter = NodeJsLocalInterpreter.castAndValidate(interpreter);
/*  50 */     ProcessHandler processHandler = startProcess(localInterpreter, debugPort);
/*  51 */     ProcessTerminatedListener.attach(processHandler);
/*     */     
/*  53 */     File workingDir = new File(this.mySettings.getWorkingDirectory());
/*  54 */     ConsoleView consoleView = createConsole(processHandler, this.myEnvironment, new NodeunitTestLocationProvider(workingDir));
/*  55 */     consoleView.addMessageFilter((Filter)new NodeStackTraceFilter(this.myEnvironment.getProject(), workingDir));
/*  56 */     consoleView.addMessageFilter((Filter)new NodeConsoleAdditionalFilter(this.myEnvironment.getProject(), workingDir));
/*     */     
/*  58 */     DefaultExecutionResult executionResult = new DefaultExecutionResult((ExecutionConsole)consoleView, processHandler);
/*  59 */     executionResult.setRestartActions(new AnAction[] { (AnAction)new ToggleAutoTestAction() });
/*  60 */     if (executionResult == null) $$$reportNull$$$0(2);  return (ExecutionResult)executionResult;
/*     */   }
/*     */   
/*     */   @NotNull
/*     */   private ProcessHandler startProcess(@NotNull NodeJsLocalInterpreter interpreter, int debugPort) throws ExecutionException {
/*  65 */     if (interpreter == null) $$$reportNull$$$0(3);  GeneralCommandLine commandLine = new GeneralCommandLine();
/*  66 */     commandLine.withCharset(StandardCharsets.UTF_8);
/*  67 */     this.mySettings.getEnvData().configureCommandLine(commandLine, true);
/*  68 */     commandLine.withWorkDirectory(this.mySettings.getWorkingDirectory());
/*  69 */     commandLine.setExePath(interpreter.getInterpreterSystemDependentPath());
/*  70 */     NodeCommandLineUtil.addNodeOptionsForDebugging(commandLine, Collections.emptyList(), debugPort, true, (NodeJsInterpreter)interpreter, true);
/*  71 */     File mainFile = getNodeunitMainFile();
/*  72 */     commandLine.addParameter(mainFile.getAbsolutePath());
/*  73 */     commandLine.addParameter(this.mySettings.getNodeunitPackage().getSystemDependentPath());
/*  74 */     NodeunitTestType testType = this.mySettings.getTestType();
/*  75 */     if (testType == NodeunitTestType.DIRECTORY) {
/*  76 */       commandLine.addParameter(this.mySettings.getDirectory());
/*     */     } else {
/*  78 */       commandLine.addParameter(this.mySettings.getJsFile());
/*     */     } 
/*  80 */     if (NodeCommandLineUtil.createProcessHandler(commandLine, false) == null) $$$reportNull$$$0(4);  return (ProcessHandler)NodeCommandLineUtil.createProcessHandler(commandLine, false);
/*     */   }
/*     */   
/*     */   @NotNull
/*     */   public static File getNodeunitMainFile() throws ExecutionException {
/*  85 */     String relativePath = "nodeunit/nodeunit-intellij-starter.js";
/*     */     try {
/*  87 */       if (NodeJsCodeLocator.getFileRelativeToJsDir(relativePath) == null) $$$reportNull$$$0(5);  return NodeJsCodeLocator.getFileRelativeToJsDir(relativePath);
/*     */     }
/*  89 */     catch (IOException e) {
/*  90 */       throw new ExecutionException(NodeJSBundle.message("rc.nodeunit.cannot_find_intellij_starter.error.message", new Object[] { relativePath }), e);
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   private static ConsoleView createConsole(@NotNull ProcessHandler processHandler, @NotNull ExecutionEnvironment env, @NotNull SMTestLocator locator) {
/*  97 */     if (processHandler == null) $$$reportNull$$$0(6);  if (env == null) $$$reportNull$$$0(7);  if (locator == null) $$$reportNull$$$0(8);  NodeunitRunConfiguration runConfiguration = (NodeunitRunConfiguration)env.getRunProfile();
/*  98 */     NodeunitConsoleProperties consoleProperties = new NodeunitConsoleProperties(runConfiguration, env.getExecutor(), locator);
/*  99 */     SMTRunnerConsoleView sMTRunnerConsoleView = SMTestRunnerConnectionUtil.createConsole((SMTRunnerConsoleProperties)consoleProperties);
/* 100 */     sMTRunnerConsoleView.attachToProcess(processHandler);
/* 101 */     return (ConsoleView)sMTRunnerConsoleView;
/*     */   }
/*     */   
/*     */   private static class NodeunitConsoleProperties extends JsTestConsoleProperties {
/*     */     private final SMTestLocator myLocator;
/*     */     
/*     */     NodeunitConsoleProperties(@NotNull NodeunitRunConfiguration configuration, @NotNull Executor executor, @NotNull SMTestLocator locator) {
/* 108 */       super((RunConfiguration)configuration, "Nodeunit", executor);
/* 109 */       this.myLocator = locator;
/* 110 */       setIfUndefined((AbstractProperty)TestConsoleProperties.HIDE_PASSED_TESTS, false);
/* 111 */       setIfUndefined((AbstractProperty)TestConsoleProperties.HIDE_IGNORED_TEST, true);
/* 112 */       setIfUndefined((AbstractProperty)TestConsoleProperties.SCROLL_TO_SOURCE, true);
/* 113 */       setIfUndefined((AbstractProperty)TestConsoleProperties.SELECT_FIRST_DEFECT, true);
/* 114 */       setPrintTestingStartedTime(false);
/*     */     }
/*     */ 
/*     */     
/*     */     public SMTestLocator getTestLocator() {
/* 119 */       return this.myLocator;
/*     */     }
/*     */   }
/*     */ }


/* Location:              C:\Program Files\JetBrains\WebStorm 2021.1\plugins\NodeJS\lib\NodeJS.jar!\com\jetbrains\nodejs\nodeunit\execution\NodeunitRunProfileState.class
 * Java compiler version: 11 (55.0)
 * JD-Core Version:       1.1.3
 */