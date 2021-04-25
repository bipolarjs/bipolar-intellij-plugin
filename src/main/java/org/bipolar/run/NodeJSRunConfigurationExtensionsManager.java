/*     */ package org.bipolar.run;
/*     */ import com.intellij.execution.ExecutionException;
/*     */ import com.intellij.execution.configurations.GeneralCommandLine;
/*     */ import com.intellij.execution.process.ProcessHandler;
/*     */ import com.intellij.execution.runners.ExecutionEnvironment;
/*     */ import com.intellij.javascript.debugger.CommandLineDebugConfigurator;
/*     */ import com.intellij.javascript.nodejs.NodeFileTransfer;
/*     */ import com.intellij.javascript.nodejs.interpreter.NodeJsInterpreterChangeListener;
/*     */ import com.intellij.openapi.options.SettingsEditor;
/*     */ import com.intellij.openapi.options.SettingsEditorGroup;
/*     */ import com.intellij.util.SmartList;
/*     */ import java.util.List;
/*     */ import kotlin.Metadata;
/*     */ import kotlin.jvm.functions.Function1;
/*     */ import kotlin.jvm.internal.Intrinsics;
/*     */ import org.jetbrains.annotations.NotNull;
/*     */ import org.jetbrains.annotations.Nullable;
/*     */ 
/*     */ @Metadata(mv = {1, 4, 2}, bv = {1, 0, 3}, k = 1, d1 = {"\000@\n\002\030\002\n\002\030\002\n\002\030\002\n\002\030\002\n\002\b\002\n\002\020\002\n\002\b\002\n\002\030\002\n\000\n\002\030\002\n\002\b\002\n\002\030\002\n\000\n\002\030\002\n\002\b\002\n\002\030\002\n\002\b\004\030\000 \0252\016\022\004\022\0020\002\022\004\022\0020\0030\001:\002\025\026B\005¢\006\002\020\004J$\020\005\032\0020\0062\006\020\007\032\0020\0022\f\020\b\032\b\022\004\022\0020\0020\t2\006\020\n\032\0020\013J\016\020\f\032\0020\0062\006\020\007\032\0020\002J\016\020\r\032\0020\0162\006\020\007\032\0020\002J\036\020\017\032\0020\0202\006\020\021\032\0020\0022\006\020\022\032\0020\0232\006\020\024\032\0020\016¨\006\027"}, d2 = {"Lcom/jetbrains/nodejs/run/NodeJSRunConfigurationExtensionsManager;", "Lcom/intellij/execution/configuration/RunConfigurationExtensionsManager;", "Lcom/jetbrains/nodejs/run/NodeJsRunConfiguration;", "Lcom/jetbrains/nodejs/run/NodeJSRunConfigurationExtension;", "()V", "appendEditorsNode", "", "configuration", "group", "Lcom/intellij/openapi/options/SettingsEditorGroup;", "settings", "Lcom/jetbrains/nodejs/run/NodeRunConfigurationEditor;", "checkConfiguration", "createFileTransfer", "Lcom/intellij/javascript/nodejs/NodeFileTransfer;", "createRuntimeSession", "Lcom/jetbrains/nodejs/run/NodeJSRuntimeSession;", "runConfiguration", "environment", "Lcom/intellij/execution/runners/ExecutionEnvironment;", "fileTransfer", "Companion", "MyRuntimeSessionHelper", "intellij.nodeJS"})
/*     */ public final class NodeJSRunConfigurationExtensionsManager extends RunConfigurationExtensionsManager<NodeJsRunConfiguration, NodeJSRunConfigurationExtension> {
/*     */   public NodeJSRunConfigurationExtensionsManager() {
/*  22 */     super(NodeJSRunConfigurationExtension.EP_NAME);
/*     */   }
/*     */ 
/*     */   
/*     */   @NotNull
/*     */   public static final Companion Companion = new Companion(null);
/*     */   
/*     */   @Metadata(mv = {1, 4, 2}, bv = {1, 0, 3}, k = 3, d1 = {"\000\016\n\000\n\002\020\002\n\000\n\002\030\002\n\000\020\000\032\0020\0012\006\020\002\032\0020\003H\n¢\006\002\b\004"}, d2 = {"<anonymous>", "", "listener", "Lcom/intellij/javascript/nodejs/interpreter/NodeJsInterpreterChangeListener;", "invoke"})
/*     */   static final class NodeJSRunConfigurationExtensionsManager$appendEditorsNode$listenerRegistrar$1
/*     */     extends Lambda
/*     */     implements Function1<NodeJsInterpreterChangeListener, Unit>
/*     */   {
/*     */     public final void invoke(@NotNull NodeJsInterpreterChangeListener listener) {
/*  35 */       Intrinsics.checkNotNullParameter(listener, "listener"); this.$settings.addListener(listener);
/*     */     } NodeJSRunConfigurationExtensionsManager$appendEditorsNode$listenerRegistrar$1(NodeRunConfigurationEditor param1NodeRunConfigurationEditor) { super(1); } } @Metadata(mv = {1, 4, 2}, bv = {1, 0, 3}, k = 3, d1 = {"\000\020\n\000\n\002\020\b\n\000\n\002\030\002\n\002\b\002\020\000\032\0020\0012\016\020\002\032\n \004*\004\030\0010\0030\003H\n¢\006\002\b\005"}, d2 = {"<anonymous>", "", "it", "Lcom/jetbrains/nodejs/run/NodeJSRunConfigurationExtension;", "kotlin.jvm.PlatformType", "applyAsInt"})
/*  37 */   static final class NodeJSRunConfigurationExtensionsManager$appendEditorsNode$1<T> implements ToIntFunction<NodeJSRunConfigurationExtension> { public static final NodeJSRunConfigurationExtensionsManager$appendEditorsNode$1 INSTANCE = new NodeJSRunConfigurationExtensionsManager$appendEditorsNode$1(); public final int applyAsInt(NodeJSRunConfigurationExtension it) { Intrinsics.checkNotNullExpressionValue(it, "it"); return it.getEditorPriority(); }
/*     */      }
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
/*     */   @Metadata(mv = {1, 4, 2}, bv = {1, 0, 3}, k = 1, d1 = {"\000\024\n\002\030\002\n\002\020\000\n\002\b\002\n\002\030\002\n\002\b\004\b\003\030\0002\0020\001B\007\b\002¢\006\002\020\002R\032\020\003\032\0020\0048FX\004¢\006\f\022\004\b\005\020\002\032\004\b\006\020\007¨\006\b"}, d2 = {"Lcom/jetbrains/nodejs/run/NodeJSRunConfigurationExtensionsManager$Companion;", "", "()V", "instance", "Lcom/jetbrains/nodejs/run/NodeJSRunConfigurationExtensionsManager;", "getInstance$annotations", "getInstance", "()Lcom/jetbrains/nodejs/run/NodeJSRunConfigurationExtensionsManager;", "intellij.nodeJS"})
/*     */   public static final class Companion
/*     */   {
/*     */     private Companion() {}
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
/*     */     @NotNull
/*     */     public final NodeJSRunConfigurationExtensionsManager getInstance() {
/*     */       int $i$f$service = 0;
/* 147 */       Class<NodeJSRunConfigurationExtensionsManager> serviceClass$iv = NodeJSRunConfigurationExtensionsManager.class;
/* 148 */       if (ApplicationManager.getApplication().getService(serviceClass$iv) != null) return (NodeJSRunConfigurationExtensionsManager)ApplicationManager.getApplication().getService(serviceClass$iv);  ApplicationManager.getApplication().getService(serviceClass$iv);
/* 149 */       throw (Throwable)new RuntimeException("Cannot find service " + serviceClass$iv.getName() + " (classloader=" + serviceClass$iv.getClassLoader() + ')');
/*     */     }
/*     */   }
/*     */   
/*     */   @NotNull
/*     */   public final NodeJSRuntimeSession createRuntimeSession(@NotNull NodeJsRunConfiguration runConfiguration, @NotNull ExecutionEnvironment environment, @NotNull NodeFileTransfer fileTransfer) throws ExecutionException {
/*     */     Intrinsics.checkNotNullParameter(runConfiguration, "runConfiguration");
/*     */     Intrinsics.checkNotNullParameter(environment, "environment");
/*     */     Intrinsics.checkNotNullParameter(fileTransfer, "fileTransfer");
/*     */     return new MyRuntimeSessionHelper(this, runConfiguration, environment, fileTransfer);
/*     */   }
/*     */   
/*     */   public final void appendEditorsNode(@NotNull NodeJsRunConfiguration configuration, @NotNull SettingsEditorGroup group, @NotNull NodeRunConfigurationEditor settings) {
/*     */     Intrinsics.checkNotNullParameter(configuration, "configuration");
/*     */     Intrinsics.checkNotNullParameter(group, "group");
/*     */     Intrinsics.checkNotNullParameter(settings, "settings");
/*     */     Function1 listenerRegistrar = new NodeJSRunConfigurationExtensionsManager$appendEditorsNode$listenerRegistrar$1(settings);
/*     */     List extensions = getApplicableExtensions((RunConfigurationBase)configuration);
/*     */     Intrinsics.checkNotNullExpressionValue(Comparator.comparingInt(NodeJSRunConfigurationExtensionsManager$appendEditorsNode$1.INSTANCE), "Comparator.comparingInt(…on { it.editorPriority })");
/*     */     CollectionsKt.sortWith(extensions, Comparator.comparingInt(NodeJSRunConfigurationExtensionsManager$appendEditorsNode$1.INSTANCE));
/*     */     for (NodeJSRunConfigurationExtension extension : extensions) {
/*     */       if (listenerRegistrar != null)
/*     */         Function1 function1 = listenerRegistrar; 
/*     */       if (extension.createEditor(configuration, new NodeJSRunConfigurationExtensionsManager$sam$com_intellij_util_Consumer$0(function1)) != null) {
/*     */         Intrinsics.checkNotNullExpressionValue(extension.createEditor(configuration, new NodeJSRunConfigurationExtensionsManager$sam$com_intellij_util_Consumer$0(function1)), "extension.createEditor(c…nerRegistrar) ?: continue");
/*     */         SettingsEditor<NodeJsRunConfiguration> editor = extension.createEditor(configuration, new NodeJSRunConfigurationExtensionsManager$sam$com_intellij_util_Consumer$0(function1));
/*     */         if (extension.showEditorInMainPage(configuration)) {
/*     */           settings.addChildComponent(editor);
/*     */           continue;
/*     */         } 
/*     */         group.addEditor(extension.getEditorTitle(), editor);
/*     */         continue;
/*     */       } 
/*     */       extension.createEditor(configuration, new NodeJSRunConfigurationExtensionsManager$sam$com_intellij_util_Consumer$0(function1));
/*     */     } 
/*     */   }
/*     */   
/*     */   @NotNull
/*     */   public final NodeFileTransfer createFileTransfer(@NotNull NodeJsRunConfiguration configuration) throws ExecutionException {
/*     */     // Byte code:
/*     */     //   0: aload_1
/*     */     //   1: ldc 'configuration'
/*     */     //   3: invokestatic checkNotNullParameter : (Ljava/lang/Object;Ljava/lang/String;)V
/*     */     //   6: aload_0
/*     */     //   7: checkcast com/intellij/execution/configuration/RunConfigurationExtensionsManager
/*     */     //   10: astore_2
/*     */     //   11: iconst_0
/*     */     //   12: istore_3
/*     */     //   13: aload_2
/*     */     //   14: invokevirtual getExtensionPoint : ()Lcom/intellij/openapi/extensions/ExtensionPointName;
/*     */     //   17: invokevirtual getIterable : ()Ljava/lang/Iterable;
/*     */     //   20: invokeinterface iterator : ()Ljava/util/Iterator;
/*     */     //   25: astore #4
/*     */     //   27: aload #4
/*     */     //   29: invokeinterface hasNext : ()Z
/*     */     //   34: ifeq -> 96
/*     */     //   37: aload #4
/*     */     //   39: invokeinterface next : ()Ljava/lang/Object;
/*     */     //   44: checkcast com/intellij/execution/configuration/RunConfigurationExtensionBase
/*     */     //   47: astore #5
/*     */     //   49: aload #5
/*     */     //   51: ifnull -> 93
/*     */     //   54: aload #5
/*     */     //   56: aload_1
/*     */     //   57: checkcast com/intellij/execution/configurations/RunConfigurationBase
/*     */     //   60: invokevirtual isApplicableFor : (Lcom/intellij/execution/configurations/RunConfigurationBase;)Z
/*     */     //   63: ifeq -> 93
/*     */     //   66: aload #5
/*     */     //   68: checkcast com/jetbrains/nodejs/run/NodeJSRunConfigurationExtension
/*     */     //   71: astore #6
/*     */     //   73: iconst_0
/*     */     //   74: istore #7
/*     */     //   76: aload #6
/*     */     //   78: aload_1
/*     */     //   79: invokevirtual overrideFileTransfer : (Lcom/jetbrains/nodejs/run/NodeJsRunConfiguration;)Lcom/intellij/javascript/nodejs/NodeFileTransfer;
/*     */     //   82: astore #8
/*     */     //   84: aload #8
/*     */     //   86: ifnull -> 92
/*     */     //   89: aload #8
/*     */     //   91: areturn
/*     */     //   92: nop
/*     */     //   93: goto -> 27
/*     */     //   96: nop
/*     */     //   97: getstatic com/intellij/javascript/nodejs/NodeLocalFileTransfer.INSTANCE : Lcom/intellij/javascript/nodejs/NodeLocalFileTransfer;
/*     */     //   100: dup
/*     */     //   101: ldc 'NodeLocalFileTransfer.INSTANCE'
/*     */     //   103: invokestatic checkNotNullExpressionValue : (Ljava/lang/Object;Ljava/lang/String;)V
/*     */     //   106: checkcast com/intellij/javascript/nodejs/NodeFileTransfer
/*     */     //   109: areturn
/*     */     // Line number table:
/*     */     //   Java source line number -> byte code offset
/*     */     //   #51	-> 6
/*     */     //   #147	-> 13
/*     */     //   #147	-> 27
/*     */     //   #148	-> 49
/*     */     //   #149	-> 66
/*     */     //   #52	-> 76
/*     */     //   #53	-> 84
/*     */     //   #54	-> 89
/*     */     //   #56	-> 92
/*     */     //   #147	-> 93
/*     */     //   #152	-> 96
/*     */     //   #57	-> 97
/*     */     // Local variable table:
/*     */     //   start	length	slot	name	descriptor
/*     */     //   84	8	8	transfer	Lcom/intellij/javascript/nodejs/NodeFileTransfer;
/*     */     //   73	20	6	it	Lcom/jetbrains/nodejs/run/NodeJSRunConfigurationExtension;
/*     */     //   76	17	7	$i$a$-processApplicableExtensions-NodeJSRunConfigurationExtensionsManager$createFileTransfer$1	I
/*     */     //   49	44	5	extension$iv	Lcom/intellij/execution/configuration/RunConfigurationExtensionBase;
/*     */     //   11	86	2	this_$iv	Lcom/intellij/execution/configuration/RunConfigurationExtensionsManager;
/*     */     //   13	84	3	$i$f$processApplicableExtensions	I
/*     */     //   0	110	0	this	Lcom/jetbrains/nodejs/run/NodeJSRunConfigurationExtensionsManager;
/*     */     //   0	110	1	configuration	Lcom/jetbrains/nodejs/run/NodeJsRunConfiguration;
/*     */   }
/*     */   
/*     */   public final void checkConfiguration(@NotNull NodeJsRunConfiguration configuration) throws RuntimeConfigurationException {
/*     */     // Byte code:
/*     */     //   0: aload_1
/*     */     //   1: ldc 'configuration'
/*     */     //   3: invokestatic checkNotNullParameter : (Ljava/lang/Object;Ljava/lang/String;)V
/*     */     //   6: aload_0
/*     */     //   7: checkcast com/intellij/execution/configuration/RunConfigurationExtensionsManager
/*     */     //   10: astore_2
/*     */     //   11: iconst_0
/*     */     //   12: istore_3
/*     */     //   13: aload_2
/*     */     //   14: invokevirtual getExtensionPoint : ()Lcom/intellij/openapi/extensions/ExtensionPointName;
/*     */     //   17: invokevirtual getIterable : ()Ljava/lang/Iterable;
/*     */     //   20: invokeinterface iterator : ()Ljava/util/Iterator;
/*     */     //   25: astore #4
/*     */     //   27: aload #4
/*     */     //   29: invokeinterface hasNext : ()Z
/*     */     //   34: ifeq -> 86
/*     */     //   37: aload #4
/*     */     //   39: invokeinterface next : ()Ljava/lang/Object;
/*     */     //   44: checkcast com/intellij/execution/configuration/RunConfigurationExtensionBase
/*     */     //   47: astore #5
/*     */     //   49: aload #5
/*     */     //   51: ifnull -> 83
/*     */     //   54: aload #5
/*     */     //   56: aload_1
/*     */     //   57: checkcast com/intellij/execution/configurations/RunConfigurationBase
/*     */     //   60: invokevirtual isApplicableFor : (Lcom/intellij/execution/configurations/RunConfigurationBase;)Z
/*     */     //   63: ifeq -> 83
/*     */     //   66: aload #5
/*     */     //   68: checkcast com/jetbrains/nodejs/run/NodeJSRunConfigurationExtension
/*     */     //   71: astore #6
/*     */     //   73: iconst_0
/*     */     //   74: istore #7
/*     */     //   76: aload #6
/*     */     //   78: aload_1
/*     */     //   79: invokevirtual checkConfiguration : (Lcom/jetbrains/nodejs/run/NodeJsRunConfiguration;)V
/*     */     //   82: nop
/*     */     //   83: goto -> 27
/*     */     //   86: nop
/*     */     //   87: return
/*     */     // Line number table:
/*     */     //   Java source line number -> byte code offset
/*     */     //   #62	-> 6
/*     */     //   #153	-> 13
/*     */     //   #153	-> 27
/*     */     //   #154	-> 49
/*     */     //   #155	-> 66
/*     */     //   #63	-> 76
/*     */     //   #64	-> 82
/*     */     //   #153	-> 83
/*     */     //   #158	-> 86
/*     */     //   #65	-> 87
/*     */     // Local variable table:
/*     */     //   start	length	slot	name	descriptor
/*     */     //   73	10	6	it	Lcom/jetbrains/nodejs/run/NodeJSRunConfigurationExtension;
/*     */     //   76	7	7	$i$a$-processApplicableExtensions-NodeJSRunConfigurationExtensionsManager$checkConfiguration$1	I
/*     */     //   49	34	5	extension$iv	Lcom/intellij/execution/configuration/RunConfigurationExtensionBase;
/*     */     //   11	76	2	this_$iv	Lcom/intellij/execution/configuration/RunConfigurationExtensionsManager;
/*     */     //   13	74	3	$i$f$processApplicableExtensions	I
/*     */     //   0	88	0	this	Lcom/jetbrains/nodejs/run/NodeJSRunConfigurationExtensionsManager;
/*     */     //   0	88	1	configuration	Lcom/jetbrains/nodejs/run/NodeJsRunConfiguration;
/*     */   }
/*     */   
/*     */   @NotNull
/*     */   public static final NodeJSRunConfigurationExtensionsManager getInstance() {
/*     */     return Companion.getInstance();
/*     */   }
/*     */   
/*     */   @Metadata(mv = {1, 4, 2}, bv = {1, 0, 3}, k = 1, d1 = {"\000^\n\002\030\002\n\002\030\002\n\000\n\002\030\002\n\000\n\002\030\002\n\000\n\002\030\002\n\002\b\002\n\002\020 \n\000\n\002\030\002\n\000\n\002\030\002\n\000\n\002\030\002\n\000\n\002\020!\n\002\020\b\n\000\n\002\020\016\n\002\b\002\n\002\020\013\n\000\n\002\030\002\n\002\b\002\n\002\020\002\n\002\b\002\b\004\030\0002\0020\001B\035\022\006\020\002\032\0020\003\022\006\020\004\032\0020\005\022\006\020\006\032\0020\007¢\006\002\020\bJ*\020\013\032\004\030\0010\f2\006\020\r\032\0020\0162\b\020\017\032\004\030\0010\0202\f\020\021\032\b\022\004\022\0020\0230\022H\026J\n\020\024\032\004\030\0010\025H\026J\026\020\026\032\b\022\004\022\0020\0250\n2\006\020\027\032\0020\030H\026J\016\020\031\032\b\022\004\022\0020\0320\nH\026J\016\020\033\032\b\022\004\022\0020\0230\022H\026J\020\020\034\032\0020\0352\006\020\036\032\0020\fH\026R\024\020\t\032\b\022\004\022\0020\0010\nX\004¢\006\002\n\000¨\006\037"}, d2 = {"Lcom/jetbrains/nodejs/run/NodeJSRunConfigurationExtensionsManager$MyRuntimeSessionHelper;", "Lcom/jetbrains/nodejs/run/NodeJSRuntimeSession;", "runConfiguration", "Lcom/jetbrains/nodejs/run/NodeJsRunConfiguration;", "environment", "Lcom/intellij/execution/runners/ExecutionEnvironment;", "fileTransfer", "Lcom/intellij/javascript/nodejs/NodeFileTransfer;", "(Lcom/jetbrains/nodejs/run/NodeJSRunConfigurationExtensionsManager;Lcom/jetbrains/nodejs/run/NodeJsRunConfiguration;Lcom/intellij/execution/runners/ExecutionEnvironment;Lcom/intellij/javascript/nodejs/NodeFileTransfer;)V", "sessions", "", "createProcessHandler", "Lcom/intellij/execution/process/ProcessHandler;", "commandLine", "Lcom/intellij/execution/configurations/GeneralCommandLine;", "debugConfigurator", "Lcom/intellij/javascript/debugger/CommandLineDebugConfigurator;", "openPorts", "", "", "getDebugHost", "", "getNodeParameters", "isDebugStarted", "", "getRunDebugActions", "Lcom/intellij/openapi/actionSystem/AnAction;", "getUsedPorts", "onProcessStarted", "", "processHandler", "intellij.nodeJS"})
/*     */   private final class MyRuntimeSessionHelper implements NodeJSRuntimeSession {
/*     */     private final List<NodeJSRuntimeSession> sessions;
/*     */     
/*     */     public MyRuntimeSessionHelper(@NotNull NodeJSRunConfigurationExtensionsManager this$0, @NotNull NodeJsRunConfiguration runConfiguration, @NotNull ExecutionEnvironment environment, NodeFileTransfer fileTransfer) {
/*     */       // Byte code:
/*     */       //   0: aload_2
/*     */       //   1: ldc 'runConfiguration'
/*     */       //   3: invokestatic checkNotNullParameter : (Ljava/lang/Object;Ljava/lang/String;)V
/*     */       //   6: aload_3
/*     */       //   7: ldc 'environment'
/*     */       //   9: invokestatic checkNotNullParameter : (Ljava/lang/Object;Ljava/lang/String;)V
/*     */       //   12: aload #4
/*     */       //   14: ldc 'fileTransfer'
/*     */       //   16: invokestatic checkNotNullParameter : (Ljava/lang/Object;Ljava/lang/String;)V
/*     */       //   19: aload_0
/*     */       //   20: aload_1
/*     */       //   21: putfield this$0 : Lcom/jetbrains/nodejs/run/NodeJSRunConfigurationExtensionsManager;
/*     */       //   24: aload_0
/*     */       //   25: invokespecial <init> : ()V
/*     */       //   28: nop
/*     */       //   29: new java/util/ArrayList
/*     */       //   32: dup
/*     */       //   33: invokespecial <init> : ()V
/*     */       //   36: astore #5
/*     */       //   38: aload_1
/*     */       //   39: checkcast com/intellij/execution/configuration/RunConfigurationExtensionsManager
/*     */       //   42: astore #6
/*     */       //   44: iconst_0
/*     */       //   45: istore #7
/*     */       //   47: aload #6
/*     */       //   49: invokevirtual getExtensionPoint : ()Lcom/intellij/openapi/extensions/ExtensionPointName;
/*     */       //   52: invokevirtual getIterable : ()Ljava/lang/Iterable;
/*     */       //   55: invokeinterface iterator : ()Ljava/util/Iterator;
/*     */       //   60: astore #8
/*     */       //   62: aload #8
/*     */       //   64: invokeinterface hasNext : ()Z
/*     */       //   69: ifeq -> 157
/*     */       //   72: aload #8
/*     */       //   74: invokeinterface next : ()Ljava/lang/Object;
/*     */       //   79: checkcast com/intellij/execution/configuration/RunConfigurationExtensionBase
/*     */       //   82: astore #9
/*     */       //   84: aload #9
/*     */       //   86: ifnull -> 154
/*     */       //   89: aload #9
/*     */       //   91: aload_2
/*     */       //   92: invokevirtual isApplicableFor : (Lcom/intellij/execution/configurations/RunConfigurationBase;)Z
/*     */       //   95: ifeq -> 154
/*     */       //   98: aload #9
/*     */       //   100: checkcast com/jetbrains/nodejs/run/NodeJSRunConfigurationExtension
/*     */       //   103: astore #10
/*     */       //   105: iconst_0
/*     */       //   106: istore #11
/*     */       //   108: nop
/*     */       //   109: aload #4
/*     */       //   111: invokeinterface isLocal : ()Z
/*     */       //   116: ifeq -> 129
/*     */       //   119: aload #10
/*     */       //   121: aload_2
/*     */       //   122: aload_3
/*     */       //   123: invokevirtual createLocalRuntimeSession : (Lcom/jetbrains/nodejs/run/NodeJsRunConfiguration;Lcom/intellij/execution/runners/ExecutionEnvironment;)Lcom/jetbrains/nodejs/run/NodeJSRuntimeSession;
/*     */       //   126: goto -> 138
/*     */       //   129: aload #10
/*     */       //   131: aload_2
/*     */       //   132: aload_3
/*     */       //   133: aload #4
/*     */       //   135: invokevirtual createRemoteRuntimeSession : (Lcom/jetbrains/nodejs/run/NodeJsRunConfiguration;Lcom/intellij/execution/runners/ExecutionEnvironment;Lcom/intellij/javascript/nodejs/NodeFileTransfer;)Lcom/jetbrains/nodejs/run/NodeJSRuntimeSession;
/*     */       //   138: astore #12
/*     */       //   140: aload #12
/*     */       //   142: ifnull -> 153
/*     */       //   145: aload #5
/*     */       //   147: aload #12
/*     */       //   149: invokevirtual add : (Ljava/lang/Object;)Z
/*     */       //   152: pop
/*     */       //   153: nop
/*     */       //   154: goto -> 62
/*     */       //   157: nop
/*     */       //   158: aload_0
/*     */       //   159: aload #5
/*     */       //   161: checkcast java/util/List
/*     */       //   164: putfield sessions : Ljava/util/List;
/*     */       //   167: return
/*     */       // Line number table:
/*     */       //   Java source line number -> byte code offset
/*     */       //   #67	-> 19
/*     */       //   #70	-> 28
/*     */       //   #71	-> 29
/*     */       //   #72	-> 38
/*     */       //   #147	-> 47
/*     */       //   #147	-> 62
/*     */       //   #148	-> 84
/*     */       //   #149	-> 98
/*     */       //   #73	-> 108
/*     */       //   #74	-> 109
/*     */       //   #75	-> 129
/*     */       //   #73	-> 138
/*     */       //   #77	-> 140
/*     */       //   #78	-> 145
/*     */       //   #80	-> 153
/*     */       //   #147	-> 154
/*     */       //   #152	-> 157
/*     */       //   #81	-> 158
/*     */       //   #82	-> 167
/*     */       // Local variable table:
/*     */       //   start	length	slot	name	descriptor
/*     */       //   140	13	12	session	Lcom/jetbrains/nodejs/run/NodeJSRuntimeSession;
/*     */       //   105	49	10	extension	Lcom/jetbrains/nodejs/run/NodeJSRunConfigurationExtension;
/*     */       //   108	46	11	$i$a$-processApplicableExtensions-NodeJSRunConfigurationExtensionsManager$MyRuntimeSessionHelper$1	I
/*     */       //   84	70	9	extension$iv	Lcom/intellij/execution/configuration/RunConfigurationExtensionBase;
/*     */       //   44	114	6	this_$iv	Lcom/intellij/execution/configuration/RunConfigurationExtensionsManager;
/*     */       //   47	111	7	$i$f$processApplicableExtensions	I
/*     */       //   38	129	5	sessions	Ljava/util/ArrayList;
/*     */       //   0	168	0	this	Lcom/jetbrains/nodejs/run/NodeJSRunConfigurationExtensionsManager$MyRuntimeSessionHelper;
/*     */       //   0	168	1	this$0	Lcom/jetbrains/nodejs/run/NodeJSRunConfigurationExtensionsManager;
/*     */       //   0	168	2	runConfiguration	Lcom/jetbrains/nodejs/run/NodeJsRunConfiguration;
/*     */       //   0	168	3	environment	Lcom/intellij/execution/runners/ExecutionEnvironment;
/*     */       //   0	168	4	fileTransfer	Lcom/intellij/javascript/nodejs/NodeFileTransfer;
/*     */     }
/*     */     
/*     */     @NotNull
/*     */     public List<String> getNodeParameters(boolean isDebugStarted) throws IOException {
/*     */       SmartList list = new SmartList();
/*     */       for (NodeJSRuntimeSession session : this.sessions)
/*     */         list.addAll(session.getNodeParameters(isDebugStarted)); 
/*     */       return (List<String>)list;
/*     */     }
/*     */     
/*     */     @NotNull
/*     */     public List<AnAction> getRunDebugActions() {
/*     */       SmartList list = new SmartList();
/*     */       for (NodeJSRuntimeSession session : this.sessions)
/*     */         list.addAll(session.getRunDebugActions()); 
/*     */       return (List<AnAction>)list;
/*     */     }
/*     */     
/*     */     @NotNull
/*     */     public List<Integer> getUsedPorts() {
/*     */       SmartList list = new SmartList();
/*     */       for (NodeJSRuntimeSession session : this.sessions)
/*     */         list.addAll(session.getUsedPorts()); 
/*     */       return (List<Integer>)list;
/*     */     }
/*     */     
/*     */     @Nullable
/*     */     public ProcessHandler createProcessHandler(@NotNull GeneralCommandLine commandLine, @Nullable CommandLineDebugConfigurator debugConfigurator, @NotNull List<? extends Integer> openPorts) throws ExecutionException {
/*     */       Intrinsics.checkNotNullParameter(commandLine, "commandLine");
/*     */       Intrinsics.checkNotNullParameter(openPorts, "openPorts");
/*     */       List<Integer> extendedPorts = getUsedPorts();
/*     */       extendedPorts.addAll(openPorts);
/*     */       ProcessHandler handler = (ProcessHandler)null;
/*     */       for (NodeJSRuntimeSession session : this.sessions) {
/*     */         handler = session.createProcessHandler(commandLine, debugConfigurator, extendedPorts);
/*     */         if (handler != null)
/*     */           break; 
/*     */       } 
/*     */       if (handler == null)
/*     */         handler = (ProcessHandler)NodeCommandLineUtil.createProcessHandler(commandLine, true, debugConfigurator); 
/*     */       onProcessStarted(handler);
/*     */       return handler;
/*     */     }
/*     */     
/*     */     public void onProcessStarted(@NotNull ProcessHandler processHandler) {
/*     */       Intrinsics.checkNotNullParameter(processHandler, "processHandler");
/*     */       for (NodeJSRuntimeSession session : this.sessions)
/*     */         session.onProcessStarted(processHandler); 
/*     */     }
/*     */     
/*     */     @Nullable
/*     */     public String getDebugHost() {
/*     */       for (NodeJSRuntimeSession session : this.sessions) {
/*     */         if (session.getDebugHost() != null) {
/*     */           String str1 = session.getDebugHost();
/*     */           boolean bool1 = false, bool2 = false;
/*     */           String it = str1;
/*     */           int $i$a$-let-NodeJSRunConfigurationExtensionsManager$MyRuntimeSessionHelper$getDebugHost$1 = 0;
/*     */           return it;
/*     */         } 
/*     */         session.getDebugHost();
/*     */       } 
/*     */       return null;
/*     */     }
/*     */   }
/*     */ }


/* Location:              C:\Program Files\JetBrains\WebStorm 2021.1\plugins\NodeJS\lib\NodeJS.jar!\com\jetbrains\nodejs\run\NodeJSRunConfigurationExtensionsManager.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */