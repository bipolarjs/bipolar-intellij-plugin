/*     */ package org.bipolar.execution;
/*     */ import com.intellij.execution.DefaultExecutionResult;
/*     */ import com.intellij.execution.ExecutionException;
/*     */ import com.intellij.execution.ExecutionResult;
/*     */ import com.intellij.execution.configuration.EnvironmentVariablesData;
/*     */ import com.intellij.execution.configurations.GeneralCommandLine;
/*     */ import com.intellij.execution.filters.TextConsoleBuilder;
/*     */ import com.intellij.execution.process.ProcessHandler;
/*     */ import com.intellij.execution.runners.ExecutionEnvironment;
/*     */ import com.intellij.execution.ui.ConsoleView;
/*     */ import com.intellij.execution.ui.ExecutionConsole;
/*     */ import com.intellij.execution.util.ProgramParametersConfigurator;
/*     */ import com.intellij.execution.util.ProgramParametersUtil;
/*     */ import com.intellij.execution.util.ScriptFileUtil;
/*     */ import com.intellij.internal.statistic.eventLog.FeatureUsageData;
/*     */ import com.intellij.javascript.debugger.CommandLineDebugConfigurator;
/*     */ import com.intellij.javascript.debugger.DebugPortConfigurator;
/*     */ import com.intellij.javascript.nodejs.NodeCommandLineUtil;
/*     */ import com.intellij.javascript.nodejs.NodeFileTransfer;
/*     */ import com.intellij.javascript.nodejs.debug.NodeDebugCommandLineConfigurator;
/*     */ import com.intellij.javascript.nodejs.interpreter.NodeCommandLineConfigurator;
/*     */ import com.intellij.javascript.nodejs.interpreter.NodeJsInterpreter;
/*     */ import com.intellij.javascript.nodejs.interpreter.remote.NodeJsRemoteInterpreter;
/*     */ import com.intellij.javascript.nodejs.reference.NodePathManager;
/*     */ import com.intellij.lang.javascript.modules.NodeModuleUtil;
/*     */ import com.intellij.openapi.actionSystem.AnAction;
/*     */ import com.intellij.openapi.project.Project;
/*     */ import com.intellij.openapi.project.ProjectUtil;
/*     */ import com.intellij.openapi.util.io.FileUtil;
/*     */ import com.intellij.openapi.util.text.StringUtil;
/*     */ import com.intellij.openapi.vfs.VirtualFile;
/*     */ import com.intellij.psi.search.GlobalSearchScope;
/*     */ import com.intellij.terminal.TerminalExecutionConsole;
/*     */ import com.intellij.util.Function;
/*     */ import org.bipolar.run.NodeJSRunConfigurationExtensionsManager;
/*     */ import org.bipolar.run.NodeJSRuntimeSession;
/*     */ import org.bipolar.run.NodeJsRunConfiguration;
/*     */ import java.nio.file.Paths;
/*     */ import java.util.Collection;
/*     */ import java.util.List;
/*     */ import kotlin.Metadata;
/*     */ import kotlin.Unit;
/*     */ import kotlin.collections.CollectionsKt;
/*     */ import kotlin.jvm.internal.Intrinsics;
/*     */ import kotlin.text.StringsKt;
/*     */ import org.jetbrains.annotations.NotNull;
/*     */ import org.jetbrains.annotations.Nullable;
/*     */ import org.jetbrains.concurrency.Promise;
/*     */ 
/*     */ @Metadata(mv = {1, 4, 2}, bv = {1, 0, 3}, k = 1, d1 = {"\000~\n\002\030\002\n\002\030\002\n\002\030\002\n\002\030\002\n\000\n\002\030\002\n\002\b\003\n\002\030\002\n\000\n\002\030\002\n\000\n\002\020\016\n\002\b\003\n\002\030\002\n\000\n\002\020\002\n\000\n\002\030\002\n\002\b\003\n\002\030\002\n\000\n\002\020\b\n\002\b\003\n\002\030\002\n\000\n\002\030\002\n\002\b\002\n\002\030\002\n\000\n\002\030\002\n\002\030\002\n\000\n\002\030\002\n\002\b\002\n\002\030\002\n\002\b\005\b\020\030\0002\0020\0012\b\022\004\022\0020\0030\002B\025\022\006\020\004\032\0020\005\022\006\020\006\032\0020\003¢\006\002\020\007J\020\020\022\032\0020\0232\006\020\024\032\0020\025H\024J\020\020\026\032\0020\r2\006\020\n\032\0020\013H\002J\030\020\027\032\0020\0232\006\020\030\032\0020\0312\006\020\032\032\0020\033H\024J\020\020\034\032\0020\0232\006\020\030\032\0020\031H\024J\020\020\035\032\0020\0232\006\020\030\032\0020\031H\002J\020\020\036\032\0020\0372\006\020 \032\0020!H\002J\032\020\"\032\0020!2\006\020\030\032\0020\0312\b\020#\032\004\030\0010$H\024J\030\020%\032\b\022\004\022\0020'0&2\b\020#\032\004\030\0010$H\026J\026\020%\032\b\022\004\022\0020'0&2\006\020\032\032\0020\033H\026J\017\020(\032\t\030\0010\r¢\006\002\b)H\026J\022\020*\032\0020\r2\b\020+\032\004\030\0010,H\002J\020\020-\032\0020\r2\006\020\n\032\0020\013H\002J\b\020.\032\0020\021H\002J \020/\032\b\022\004\022\0020!0&2\006\020\030\032\0020\0312\b\020#\032\004\030\0010$H\024J\036\020/\032\b\022\004\022\0020!0&2\006\020\030\032\0020\0312\006\020\032\032\0020\033H\024J\b\0200\032\0020\023H\002R\016\020\b\032\0020\tX\004¢\006\002\n\000R\020\020\n\032\004\030\0010\013X\016¢\006\002\n\000R\024\020\f\032\0020\r8TX\004¢\006\006\032\004\b\016\020\017R\020\020\020\032\004\030\0010\021X\016¢\006\002\n\000¨\0061"}, d2 = {"Lcom/jetbrains/nodejs/execution/NodeCommandLineState;", "Lcom/intellij/javascript/nodejs/debug/NodeLocalDebuggableRunProfileState;", "Lcom/intellij/javascript/debugger/execution/DebuggableProcessState;", "Lcom/jetbrains/nodejs/run/NodeJsRunConfiguration;", "environment", "Lcom/intellij/execution/runners/ExecutionEnvironment;", "runConfiguration", "(Lcom/intellij/execution/runners/ExecutionEnvironment;Lcom/jetbrains/nodejs/run/NodeJsRunConfiguration;)V", "extensionsManager", "Lcom/jetbrains/nodejs/run/NodeJSRunConfigurationExtensionsManager;", "fileTransfer", "Lcom/intellij/javascript/nodejs/NodeFileTransfer;", "inputPath", "", "getInputPath", "()Ljava/lang/String;", "session", "Lcom/jetbrains/nodejs/run/NodeJSRuntimeSession;", "addConsoleFilters", "", "builder", "Lcom/intellij/execution/filters/TextConsoleBuilder;", "configureAppFilePath", "configureCommandLine", "commandLine", "Lcom/intellij/execution/configurations/GeneralCommandLine;", "debugPort", "", "configureEnvironment", "configureNodeInterpreter", "createConsole", "Lcom/intellij/execution/ui/ConsoleView;", "processHandler", "Lcom/intellij/execution/process/ProcessHandler;", "createProcessHandler", "configurator", "Lcom/intellij/javascript/debugger/CommandLineDebugConfigurator;", "execute", "Lorg/jetbrains/concurrency/Promise;", "Lcom/intellij/execution/ExecutionResult;", "getDebugHost", "Lorg/jetbrains/annotations/Nullable;", "getInterpreterTypeForStat", "interpreter", "Lcom/intellij/javascript/nodejs/interpreter/NodeJsInterpreter;", "getWorkingDirectory", "initSession", "startProcess", "triggerUsage", "intellij.nodeJS"})
/*     */ public class NodeCommandLineState extends DebuggableProcessState<NodeJsRunConfiguration> implements NodeLocalDebuggableRunProfileState {
/*     */   public NodeCommandLineState(@NotNull ExecutionEnvironment environment, @NotNull NodeJsRunConfiguration runConfiguration) {
/*  53 */     super((DebuggableProcessRunConfiguration)runConfiguration, environment);
/*     */     
/*  55 */     this.extensionsManager = NodeJSRunConfigurationExtensionsManager.Companion.getInstance();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private final NodeJSRunConfigurationExtensionsManager extensionsManager;
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private NodeFileTransfer fileTransfer;
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private NodeJSRuntimeSession session;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   @Metadata(mv = {1, 4, 2}, bv = {1, 0, 3}, k = 3, d1 = {"\000\020\n\000\n\002\030\002\n\002\b\002\n\002\030\002\n\000\020\000\032\n \002*\004\030\0010\0010\0012\016\020\003\032\n \002*\004\030\0010\0040\004H\n¢\006\002\b\005"}, d2 = {"<anonymous>", "Lcom/intellij/execution/DefaultExecutionResult;", "kotlin.jvm.PlatformType", "it", "Lcom/intellij/execution/process/ProcessHandler;", "fun"})
/*     */   static final class NodeCommandLineState$execute$1<Param, Result>
/*     */     implements Function<ProcessHandler, DefaultExecutionResult>
/*     */   {
/*     */     public final DefaultExecutionResult fun(ProcessHandler it) {
/*  83 */       Intrinsics.checkNotNullExpressionValue(it, "it"); return new DefaultExecutionResult((ExecutionConsole)NodeCommandLineState.this.createConsole(it), it);
/*     */     } } @Metadata(mv = {1, 4, 2}, bv = {1, 0, 3}, k = 3, d1 = {"\000\020\n\000\n\002\030\002\n\002\b\002\n\002\030\002\n\000\020\000\032\n \002*\004\030\0010\0010\0012\016\020\003\032\n \002*\004\030\0010\0040\004H\n¢\006\002\b\005"}, d2 = {"<anonymous>", "Lcom/intellij/execution/ExecutionResult;", "kotlin.jvm.PlatformType", "it", "Lcom/intellij/execution/DefaultExecutionResult;", "fun"})
/*  85 */   static final class NodeCommandLineState$execute$2<Param, Result> implements Function<DefaultExecutionResult, ExecutionResult> { public final ExecutionResult fun(DefaultExecutionResult it) { NodeCommandLineState.this.triggerUsage();
/*  86 */       if (it == null) throw new NullPointerException("null cannot be cast to non-null type com.intellij.execution.DefaultExecutionResult");  DefaultExecutionResult executionResult = it;
/*  87 */       Intrinsics.checkNotNullExpressionValue(NodeCommandLineState.this.initSession().getRunDebugActions(), "initSession().runDebugActions"); List runDebugActions = NodeCommandLineState.this.initSession().getRunDebugActions();
/*  88 */       List list1 = runDebugActions; boolean bool = false; if (!list1.isEmpty())
/*  89 */       { Collection $this$toTypedArray$iv = runDebugActions; int $i$f$toTypedArray = 0;
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
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */         
/* 247 */         Collection thisCollection$iv = $this$toTypedArray$iv;
/* 248 */         if (thisCollection$iv.toArray((Object[])new AnAction[0]) == null) throw new NullPointerException("null cannot be cast to non-null type kotlin.Array<T>");  executionResult.setActions(Arrays.<AnAction>copyOf((AnAction[])thisCollection$iv.toArray((Object[])new AnAction[0]), ((AnAction[])thisCollection$iv.toArray((Object[])new AnAction[0])).length)); }  return (ExecutionResult)executionResult; } } @NotNull protected String getInputPath() { String inputPath = ((NodeJsRunConfiguration)getConfiguration()).getInputPath(); CharSequence charSequence1 = inputPath; boolean bool1 = false, bool2 = false; charSequence1 = inputPath; bool1 = false; if (charSequence1 == null); charSequence1 = ""; int $i$f$trim = 0; CharSequence $this$trim$iv$iv = charSequence1; int i = 0; int startIndex$iv$iv = 0;
/* 249 */     int endIndex$iv$iv = $this$trim$iv$iv.length() - 1;
/* 250 */     boolean startFound$iv$iv = false;
/*     */     
/* 252 */     while (startIndex$iv$iv <= endIndex$iv$iv) {
/* 253 */       int index$iv$iv = !startFound$iv$iv ? startIndex$iv$iv : endIndex$iv$iv;
/* 254 */       char it = $this$trim$iv$iv.charAt(index$iv$iv); int $i$a$-trim-NodeCommandLineState$inputPath$1 = 0;
/*     */     } 
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
/*     */ 
/*     */ 
/*     */     
/* 269 */     return ((charSequence1 == null || StringsKt.isBlank(charSequence1))) ? $this$trim$iv$iv.subSequence(startIndex$iv$iv, endIndex$iv$iv + 1).toString() : super.getInputPath(); }
/*     */ 
/*     */   
/*     */   @Metadata(mv = {1, 4, 2}, bv = {1, 0, 3}, k = 3, d1 = {"\000\026\n\000\n\002\030\002\n\002\030\002\n\002\b\002\n\002\020\002\n\002\b\002\020\000\032&\022\f\022\n \003*\004\030\0010\0020\002 \003*\022\022\f\022\n \003*\004\030\0010\0020\002\030\0010\0010\0012\016\020\004\032\n \003*\004\030\0010\0050\005H\n¢\006\004\b\006\020\007"}, d2 = {"<anonymous>", "Lorg/jetbrains/concurrency/Promise;", "Lcom/intellij/execution/process/ProcessHandler;", "kotlin.jvm.PlatformType", "it", "", "fun", "(Lkotlin/Unit;)Lorg/jetbrains/concurrency/Promise;"})
/*     */   static final class NodeCommandLineState$startProcess$1<Param, Result> implements Function<Unit, Promise<ProcessHandler>> {
/*     */     public final Promise<ProcessHandler> fun(Unit it) {
/*     */       NodeCommandLineUtil.configureUsefulEnvironment(this.$commandLine);
/*     */       for (String extensionParameter : NodeCommandLineState.this.initSession().getNodeParameters((this.$configurator != null)))
/*     */         this.$commandLine.addParameter(extensionParameter); 
/*     */       Intrinsics.checkNotNull(NodeCommandLineState.this.fileTransfer);
/*     */       String appFilePath = NodeCommandLineState.this.configureAppFilePath(NodeCommandLineState.this.fileTransfer);
/*     */       CharSequence charSequence = appFilePath;
/*     */       boolean bool = false;
/*     */       if ((charSequence.length() > 0))
/*     */         this.$commandLine.addParameters(new String[] { appFilePath }); 
/*     */       this.$commandLine.addParameters(ProgramParametersConfigurator.expandMacrosAndParseParameters(NodeCommandLineState.access$getConfiguration$p(NodeCommandLineState.this).getApplicationParameters()));
/*     */       return NodeCommandLineState.this.startProcess(this.$commandLine, this.$configurator);
/*     */     }
/*     */     
/*     */     NodeCommandLineState$startProcess$1(GeneralCommandLine param1GeneralCommandLine, CommandLineDebugConfigurator param1CommandLineDebugConfigurator) {}
/*     */   }
/*     */   
/*     */   private final NodeJSRuntimeSession initSession() {
/*     */     if (this.session != null) {
/*     */       Intrinsics.checkNotNull(this.session);
/*     */       return this.session;
/*     */     } 
/*     */     this.fileTransfer = this.extensionsManager.createFileTransfer((NodeJsRunConfiguration)getConfiguration());
/*     */     Intrinsics.checkNotNull(this.fileTransfer);
/*     */     this.session = this.extensionsManager.createRuntimeSession((NodeJsRunConfiguration)getConfiguration(), getEnvironment(), this.fileTransfer);
/*     */     Intrinsics.checkNotNull(this.session);
/*     */     return this.session;
/*     */   }
/*     */   
/*     */   protected void addConsoleFilters(@NotNull TextConsoleBuilder builder) {
/*     */     Intrinsics.checkNotNullParameter(builder, "builder");
/*     */     builder.addFilter((Filter)new NodeConsoleAdditionalFilter(getEnvironment().getProject(), ((NodeJsRunConfiguration)getConfiguration()).getWorkingDirectory()));
/*     */   }
/*     */   
/*     */   @NotNull
/*     */   public Promise<ExecutionResult> execute(int debugPort) {
/*     */     return execute(NodeDebugCommandLineConfigurator.Companion.new(debugPort, null));
/*     */   }
/*     */   
/*     */   @NotNull
/*     */   public Promise<ExecutionResult> execute(@Nullable CommandLineDebugConfigurator configurator) {
/*     */     Intrinsics.checkNotNullExpressionValue(NodeCommandLineUtil.createCommandLine(), "NodeCommandLineUtil.createCommandLine()");
/*     */     GeneralCommandLine commandLine = NodeCommandLineUtil.createCommandLine();
/*     */     Intrinsics.checkNotNullExpressionValue(startProcess(commandLine, configurator).then(new NodeCommandLineState$execute$1<>()).then(new NodeCommandLineState$execute$2<>()), "startProcess(commandLine…  executionResult\n      }");
/*     */     return startProcess(commandLine, configurator).then(new NodeCommandLineState$execute$1<>()).then(new NodeCommandLineState$execute$2<>());
/*     */   }
/*     */   
/*     */   private final void triggerUsage() {
/*     */     FeatureUsageData data = (new FeatureUsageData()).addData("interpreter_type", getInterpreterTypeForStat(((NodeJsRunConfiguration)getConfiguration()).getInterpreter()));
/*     */     Intrinsics.checkNotNullExpressionValue(getEnvironment().getProject(), "environment.project");
/*     */     NodeRunConfigurationUsageCollector.Companion.trigger(getEnvironment().getProject(), "exec.params", data);
/*     */   }
/*     */   
/*     */   private final String getInterpreterTypeForStat(NodeJsInterpreter interpreter) {
/*     */     if (interpreter == null)
/*     */       return "undefined"; 
/*     */     if (interpreter instanceof NodeJsRemoteInterpreter) {
/*     */       Intrinsics.checkNotNullExpressionValue(((NodeJsRemoteInterpreter)interpreter).getRemoteUrl(), "interpreter.remoteUrl");
/*     */       int ind = StringsKt.indexOf$default(((NodeJsRemoteInterpreter)interpreter).getRemoteUrl(), "://", 0, false, 6, null);
/*     */       if (ind > 0) {
/*     */         Intrinsics.checkNotNullExpressionValue(((NodeJsRemoteInterpreter)interpreter).getRemoteUrl(), "interpreter.remoteUrl");
/*     */         String str = ((NodeJsRemoteInterpreter)interpreter).getRemoteUrl();
/*     */         boolean bool1 = false, bool2 = false;
/*     */         if (str == null)
/*     */           throw new NullPointerException("null cannot be cast to non-null type java.lang.String"); 
/*     */         Intrinsics.checkNotNullExpressionValue(str.substring(bool1, ind), "(this as java.lang.Strin…ing(startIndex, endIndex)");
/*     */         return NodeJsRemoteInterpreterType.isKnownRemoteReferenceName(((NodeJsRemoteInterpreter)interpreter).getRemoteUrl()) ? ("Remote " + str.substring(bool1, ind)) : "third.party remote";
/*     */       } 
/*     */       NodeCommandLineStateKt.access$getLOG$p().error("Malformed remote interpreter URL " + ((NodeJsRemoteInterpreter)interpreter).getRemoteUrl());
/*     */       return "Remote unknown";
/*     */     } 
/*     */     if (interpreter instanceof com.intellij.javascript.nodejs.interpreter.wsl.WslNodeInterpreter)
/*     */       return "WSL"; 
/*     */     if (interpreter instanceof com.intellij.javascript.nodejs.interpreter.local.NodeJsLocalInterpreter)
/*     */       return "Local"; 
/*     */     return "Unknown";
/*     */   }
/*     */   
/*     */   private final ConsoleView createConsole(ProcessHandler processHandler) {
/*     */     NodeCommandLineState$createConsole$consoleBuilder$1 consoleBuilder = new NodeCommandLineState$createConsole$consoleBuilder$1(processHandler, getEnvironment().getProject());
/*     */     addConsoleFilters((TextConsoleBuilder)consoleBuilder);
/*     */     Intrinsics.checkNotNullExpressionValue(consoleBuilder.getConsole(), "consoleBuilder.console");
/*     */     ConsoleView console = consoleBuilder.getConsole();
/*     */     console.attachToProcess(processHandler);
/*     */     return console;
/*     */   }
/*     */   
/*     */   @Metadata(mv = {1, 4, 2}, bv = {1, 0, 3}, k = 1, d1 = {"\000\021\n\000\n\002\030\002\n\000\n\002\030\002\n\000*\001\000\b\n\030\0002\0020\001J\b\020\002\032\0020\003H\024¨\006\004"}, d2 = {"org/bipolar/execution/NodeCommandLineState$createConsole$consoleBuilder$1", "Lcom/intellij/execution/filters/TextConsoleBuilderImpl;", "createConsole", "Lcom/intellij/execution/ui/ConsoleView;", "intellij.nodeJS"})
/*     */   public static final class NodeCommandLineState$createConsole$consoleBuilder$1 extends TextConsoleBuilderImpl {
/*     */     NodeCommandLineState$createConsole$consoleBuilder$1(ProcessHandler $captured_local_variable$1, Project $super_call_param$2) {
/*     */       super($super_call_param$2);
/*     */     }
/*     */     
/*     */     @NotNull
/*     */     protected ConsoleView createConsole() {
/*     */       if (NodeCommandLineUtil.shouldUseTerminalConsole(this.$processHandler))
/*     */         return (ConsoleView)new TerminalExecutionConsole(getProject(), this.$processHandler); 
/*     */       return (ConsoleView)new NodeCommandLineState$createConsole$consoleBuilder$1$createConsole$1(NodeCommandLineState.this.getEnvironment().getProject(), getScope(), isViewer(), true);
/*     */     }
/*     */     
/*     */     @Metadata(mv = {1, 4, 2}, bv = {1, 0, 3}, k = 1, d1 = {"\000\027\n\000\n\002\030\002\n\000\n\002\020\000\n\000\n\002\020\016\n\000*\001\000\b\n\030\0002\0020\001J\022\020\002\032\004\030\0010\0032\006\020\004\032\0020\005H\026¨\006\006"}, d2 = {"org/bipolar/execution/NodeCommandLineState$createConsole$consoleBuilder$1$createConsole$1", "Lcom/intellij/execution/impl/ConsoleViewImpl;", "getData", "", "dataId", "", "intellij.nodeJS"})
/*     */     public static final class NodeCommandLineState$createConsole$consoleBuilder$1$createConsole$1 extends ConsoleViewImpl {
/*     */       NodeCommandLineState$createConsole$consoleBuilder$1$createConsole$1(Project $super_call_param$1, GlobalSearchScope $super_call_param$2, boolean $super_call_param$3, boolean $super_call_param$4) {
/*     */         super($super_call_param$1, $super_call_param$2, $super_call_param$3, $super_call_param$4);
/*     */       }
/*     */       
/*     */       @Nullable
/*     */       public Object getData(@NotNull String dataId) {
/*     */         Intrinsics.checkNotNullParameter(dataId, "dataId");
/*     */         if (super.getData(dataId) == null)
/*     */           super.getData(dataId); 
/*     */         return LangDataKeys.RUN_PROFILE.is(dataId) ? NodeCommandLineState.this.getEnvironment().getRunProfile() : null;
/*     */       }
/*     */     }
/*     */   }
/*     */   
/*     */   @NotNull
/*     */   protected Promise<ProcessHandler> startProcess(@NotNull GeneralCommandLine commandLine, int debugPort) {
/*     */     Intrinsics.checkNotNullParameter(commandLine, "commandLine");
/*     */     return startProcess(commandLine, NodeDebugCommandLineConfigurator.Companion.new(debugPort, null));
/*     */   }
/*     */   
/*     */   @NotNull
/*     */   protected Promise<ProcessHandler> startProcess(@NotNull GeneralCommandLine commandLine, @Nullable CommandLineDebugConfigurator configurator) {
/*     */     Intrinsics.checkNotNullParameter(commandLine, "commandLine");
/*     */     initSession();
/*     */     Intrinsics.checkNotNullExpressionValue(ProgramParametersConfigurator.expandMacrosAndParseParameters(((NodeJsRunConfiguration)getConfiguration()).getProgramParameters()), "ProgramParametersConfigu…ration.programParameters)");
/*     */     List extraArgs = ProgramParametersConfigurator.expandMacrosAndParseParameters(((NodeJsRunConfiguration)getConfiguration()).getProgramParameters());
/*     */     if (configurator == null);
/*     */     Intrinsics.checkNotNullExpressionValue(NodeCommandLineStateKt.access$getNODE_CLI_RUN_CONFIGURATOR$p().configureDebugArgs(commandLine, extraArgs).thenAsync(new NodeCommandLineState$startProcess$1<>(commandLine, configurator)), "(configurator ?: NODE_CL…ne, configurator)\n      }");
/*     */     return NodeCommandLineStateKt.access$getNODE_CLI_RUN_CONFIGURATOR$p().configureDebugArgs(commandLine, extraArgs).thenAsync(new NodeCommandLineState$startProcess$1<>(commandLine, configurator));
/*     */   }
/*     */   
/*     */   protected void configureCommandLine(@NotNull GeneralCommandLine commandLine, int debugPort) {
/*     */     Intrinsics.checkNotNullParameter(commandLine, "commandLine");
/*     */     commandLine.withCharset(Charsets.UTF_8);
/*     */     Intrinsics.checkNotNull(this.fileTransfer);
/*     */     commandLine.setWorkDirectory(getWorkingDirectory(this.fileTransfer));
/*     */     ProcessStreamsSynchronizer.redirectErrorStreamIfNeeded(commandLine);
/*     */   }
/*     */   
/*     */   private final String getWorkingDirectory(NodeFileTransfer fileTransfer) {
/*     */     Intrinsics.checkNotNullExpressionValue(StringUtil.notNullize(((NodeJsRunConfiguration)getConfiguration()).getWorkingDirectory()), "StringUtil.notNullize(co…uration.workingDirectory)");
/*     */     String workingDirectory = StringUtil.notNullize(((NodeJsRunConfiguration)getConfiguration()).getWorkingDirectory());
/*     */     if (!fileTransfer.isLocal() && !StringUtil.isEmptyOrSpaces(workingDirectory)) {
/*     */       Intrinsics.checkNotNullExpressionValue(fileTransfer.getMappingFor(workingDirectory), "fileTransfer.getMappingFor(workingDirectory)");
/*     */       workingDirectory = fileTransfer.getMappingFor(workingDirectory);
/*     */     } 
/*     */     return workingDirectory;
/*     */   }
/*     */   
/*     */   protected void configureEnvironment(@NotNull GeneralCommandLine commandLine) {
/*     */     Intrinsics.checkNotNullParameter(commandLine, "commandLine");
/*     */     Intrinsics.checkNotNullExpressionValue(((NodeJsRunConfiguration)getConfiguration()).getEnvData(), "configuration.envData");
/*     */     EnvironmentVariablesData envData = ((NodeJsRunConfiguration)getConfiguration()).getEnvData();
/*     */     Intrinsics.checkNotNullExpressionValue(FileUtil.toSystemIndependentName(StringUtil.notNullize(((NodeJsRunConfiguration)getConfiguration()).getInputPath())), "FileUtil.toSystemIndepen…configuration.inputPath))");
/*     */     String path = FileUtil.toSystemIndependentName(StringUtil.notNullize(((NodeJsRunConfiguration)getConfiguration()).getInputPath()));
/*     */     VirtualFile file = LocalFileSystem.getInstance().findFileByPath(path);
/*     */     if (ScratchUtil.isScratch(file)) {
/*     */       Intrinsics.checkNotNullExpressionValue(((NodeJsRunConfiguration)getConfiguration()).getProject(), "configuration.project");
/*     */       VirtualFile baseDir = ProjectUtil.guessProjectDir(((NodeJsRunConfiguration)getConfiguration()).getProject());
/*     */       if (baseDir != null) {
/*     */         VirtualFile nodeModules = NodeModuleUtil.findChildNodeModulesDir(baseDir);
/*     */         if (nodeModules != null) {
/*     */           Intrinsics.checkNotNullExpressionValue(NodePathManager.prependPath(envData, FileUtil.toSystemDependentName(nodeModules.getPath())), "NodePathManager.prependP…ntName(nodeModules.path))");
/*     */           envData = NodePathManager.prependPath(envData, FileUtil.toSystemDependentName(nodeModules.getPath()));
/*     */         } 
/*     */       } 
/*     */     } 
/*     */     envData.configureCommandLine(commandLine, true);
/*     */   }
/*     */   
/*     */   private final String configureAppFilePath(NodeFileTransfer fileTransfer) {
/*     */     CharSequence charSequence = getInputPath();
/*     */     boolean bool = false;
/*     */     if ((charSequence.length() == 0))
/*     */       return getInputPath(); 
/*     */     String expandedInputPath = ProgramParametersUtil.expandPathAndMacros(getInputPath(), ProgramParametersUtil.getModule((CommonProgramRunConfigurationParameters)getConfiguration()), getEnvironment().getProject());
/*     */     Intrinsics.checkNotNullExpressionValue(ScriptFileUtil.getLocalFilePath(expandedInputPath), "ScriptFileUtil.getLocalFilePath(expandedInputPath)");
/*     */     String appFilePath = ScriptFileUtil.getLocalFilePath(expandedInputPath);
/*     */     Object file = Paths.get(appFilePath, new String[0]);
/*     */     Intrinsics.checkNotNullExpressionValue(file, "file");
/*     */     if (!file.isAbsolute()) {
/*     */       String str1 = ((NodeJsRunConfiguration)getConfiguration()).getWorkingDirectory();
/*     */       boolean bool1 = false, bool2 = false;
/*     */       String it = str1;
/*     */       int $i$a$-let-NodeCommandLineState$configureAppFilePath$1 = 0;
/*     */       file = Paths.get(it, new String[] { appFilePath });
/*     */       ((NodeJsRunConfiguration)getConfiguration()).getWorkingDirectory();
/*     */     } 
/*     */     Intrinsics.checkNotNullExpressionValue(file.toFile(), "file.toFile()");
/*     */     String absolutePath = file.toFile().getAbsolutePath();
/*     */     Intrinsics.checkNotNullExpressionValue(absolutePath, "absolutePath");
/*     */     Intrinsics.checkNotNullExpressionValue(fileTransfer.getMappingFor(absolutePath), "fileTransfer.getMappingFor(absolutePath)");
/*     */     return fileTransfer.isLocal() ? absolutePath : fileTransfer.getMappingFor(absolutePath);
/*     */   }
/*     */   
/*     */   @NotNull
/*     */   protected ProcessHandler createProcessHandler(@NotNull GeneralCommandLine commandLine, @Nullable CommandLineDebugConfigurator configurator) {
/*     */     Intrinsics.checkNotNullParameter(commandLine, "commandLine");
/*     */     configureNodeInterpreter(commandLine);
/*     */     NodeJSRuntimeSession executionSession = initSession();
/*     */     if (!(configurator instanceof DebugPortConfigurator));
/*     */     (DebugPortConfigurator)null;
/*     */     int debugPort = ((DebugPortConfigurator)null != null) ? ((DebugPortConfigurator)null).getDebugPort() : -1;
/*     */     int i = debugPort;
/*     */     List usedPorts = (debugPort >= 0) ? CollectionsKt.toList((Iterable)new IntRange(i, debugPort + 16)) : CollectionsKt.emptyList();
/*     */     ProcessHandler handler = executionSession.createProcessHandler(commandLine, configurator, usedPorts);
/*     */     NodeCommandLineUtil.transferUseInspectorProtocol(commandLine, handler);
/*     */     Intrinsics.checkNotNullExpressionValue(handler, "handler");
/*     */     return handler;
/*     */   }
/*     */   
/*     */   private final void configureNodeInterpreter(GeneralCommandLine commandLine) {
/*     */     NodeCommandLineConfigurator nodeCommandLineConfigurator1;
/*     */     Intrinsics.checkNotNullExpressionValue(((NodeJsRunConfiguration)getConfiguration()).getInterpreterRef().resolveNotNull(((NodeJsRunConfiguration)getConfiguration()).getProject()), "configuration.interprete…ll(configuration.project)");
/*     */     NodeJsInterpreter interpreter = ((NodeJsRunConfiguration)getConfiguration()).getInterpreterRef().resolveNotNull(((NodeJsRunConfiguration)getConfiguration()).getProject());
/*     */     try {
/*     */       nodeCommandLineConfigurator1 = NodeCommandLineConfigurator.find(interpreter);
/*     */     } catch (ExecutionException e) {
/*     */       nodeCommandLineConfigurator1 = null;
/*     */     } 
/*     */     NodeCommandLineConfigurator configurator = nodeCommandLineConfigurator1;
/*     */     if (configurator != null) {
/*     */       configurator.configure(commandLine, NodeCommandLineConfigurator.defaultOptions(((NodeJsRunConfiguration)getConfiguration()).getProject()));
/*     */     } else {
/*     */       if (((NodeJsRunConfiguration)getConfiguration()).getEffectiveExePath() != null) {
/*     */         Intrinsics.checkNotNullExpressionValue(((NodeJsRunConfiguration)getConfiguration()).getEffectiveExePath(), "configuration.effectiveE…_not_specified.message\"))");
/*     */         String exePath = ((NodeJsRunConfiguration)getConfiguration()).getEffectiveExePath();
/*     */         commandLine.setExePath(((NodeJsRunConfiguration)getConfiguration()).correctExePath(exePath));
/*     */         return;
/*     */       } 
/*     */       ((NodeJsRunConfiguration)getConfiguration()).getEffectiveExePath();
/*     */       throw (Throwable)new ExecutionException(NodeJSBundle.message("rc.node.program_not_specified.message", new Object[0]));
/*     */     } 
/*     */   }
/*     */   
/*     */   @Nullable
/*     */   public String getDebugHost() {
/*     */     return initSession().getDebugHost();
/*     */   }
/*     */ }


/* Location:              C:\Program Files\JetBrains\WebStorm 2021.1\plugins\NodeJS\lib\NodeJS.jar!\com\jetbrains\nodejs\execution\NodeCommandLineState.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */