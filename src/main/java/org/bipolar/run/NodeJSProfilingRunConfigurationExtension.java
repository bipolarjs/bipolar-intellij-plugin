/*     */ package org.bipolar.run;
/*     */ 
/*     */ import com.intellij.execution.ExecutionException;
/*     */
/*     */ import com.intellij.execution.configurations.RunnerSettings;
/*     */ import com.intellij.execution.runners.ExecutionEnvironment;
/*     */ import com.intellij.javascript.nodejs.NodeFileTransfer;
/*     */ import com.intellij.javascript.nodejs.NodeLocalFileTransfer;
/*     */ import com.intellij.javascript.nodejs.NodeProfilingRuntimeSettings;
/*     */ import com.intellij.javascript.nodejs.debug.NodeDebugCommandLineConfiguratorKt;
/*     */ import com.intellij.javascript.nodejs.interpreter.NodeJsInterpreter;
/*     */ import com.intellij.javascript.nodejs.interpreter.NodeJsInterpreterChangeListener;
/*     */ import com.intellij.openapi.actionSystem.AnAction;
/*     */ import com.intellij.openapi.options.SettingsEditor;
/*     */ import com.intellij.openapi.util.InvalidDataException;
/*     */ import com.intellij.openapi.util.text.StringUtil;
/*     */ import com.intellij.util.Consumer;
/*     */ import com.intellij.util.SmartList;
/*     */ import com.intellij.util.execution.ParametersListUtil;
/*     */ import com.intellij.util.net.NetUtils;
/*     */ import org.bipolar.NodeJSBundle;
/*     */ import org.bipolar.execution.NodeProfilingRuntimeConfigurer;
/*     */ import org.bipolar.run.profile.settings.NodeProfilingPanel;
/*     */ import org.bipolar.run.profile.settings.NodeProfilingSettings;
/*     */ import java.io.IOException;
/*     */ import java.util.Collections;
/*     */ import java.util.List;
/*     */
import org.jdom.Element;
/*     */ import org.jetbrains.annotations.NotNull;
/*     */ import org.jetbrains.annotations.Nullable;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class NodeJSProfilingRunConfigurationExtension
/*     */   extends NodeJSRunConfigurationExtension
/*     */ {
/*     */   protected NodeJSRuntimeSession createRemoteRuntimeSession(@NotNull NodeJsRunConfiguration runConfiguration, @NotNull ExecutionEnvironment environment, @NotNull NodeFileTransfer fileTransfer) throws ExecutionException {
/*  42 */     if (runConfiguration == null) $$$reportNull$$$0(0);  if (environment == null) $$$reportNull$$$0(1);  if (fileTransfer == null) $$$reportNull$$$0(2);  NodeProfilingSettings settings = runConfiguration.getNodeProfilingSettings();
/*  43 */     if (!settings.isProfile() && !settings.isAllowRuntimeHeapSnapshot())
/*  44 */       return null; 
/*  45 */     NodeProfilingRuntimeConfigurer configurer = new NodeProfilingRuntimeConfigurer(runConfiguration, environment);
/*  46 */     return new MyLocalRuntimeSessionHelper(settings, runConfiguration, configurer, fileTransfer);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   @Nullable
/*     */   protected NodeJSRuntimeSession createLocalRuntimeSession(@NotNull NodeJsRunConfiguration runConfiguration, @NotNull ExecutionEnvironment environment) throws ExecutionException {
/*  54 */     if (runConfiguration == null) $$$reportNull$$$0(3);  if (environment == null) $$$reportNull$$$0(4);  NodeProfilingSettings settings = runConfiguration.getNodeProfilingSettings();
/*  55 */     if (!settings.isProfile() && !settings.isAllowRuntimeHeapSnapshot())
/*  56 */       return null; 
/*  57 */     NodeProfilingRuntimeConfigurer configurer = new NodeProfilingRuntimeConfigurer(runConfiguration, environment);
/*  58 */     return new MyLocalRuntimeSessionHelper(settings, runConfiguration, configurer, (NodeFileTransfer)NodeLocalFileTransfer.INSTANCE);
/*     */   }
/*     */ 
/*     */   
/*     */   protected void writeExternal(@NotNull NodeJsRunConfiguration runConfiguration, @NotNull Element element) {
/*  63 */     if (runConfiguration == null) $$$reportNull$$$0(5);  if (element == null) $$$reportNull$$$0(6);  runConfiguration.getNodeProfilingSettings().writeExternal(element);
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   protected void readExternal(@NotNull NodeJsRunConfiguration runConfiguration, @NotNull Element element) throws InvalidDataException {
/*  69 */     if (runConfiguration == null) $$$reportNull$$$0(7);  if (element == null) $$$reportNull$$$0(8);  NodeProfilingSettings profilingSettings = NodeProfilingSettings.readExternal(element);
/*  70 */     runConfiguration.setNodeProfilingSettings(profilingSettings);
/*     */   }
/*     */ 
/*     */   
/*     */   @Nullable
/*     */   public String getEditorTitle() {
/*  76 */     return NodeJSBundle.message("rc.nodejs.profiling.tab.name", new Object[0]);
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean isApplicableFor(@NotNull NodeJsRunConfiguration configuration) {
/*  81 */     if (configuration == null) $$$reportNull$$$0(9);  return true;
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean isEnabledFor(@NotNull NodeJsRunConfiguration applicableConfiguration, @Nullable RunnerSettings runnerSettings) {
/*  86 */     if (applicableConfiguration == null) $$$reportNull$$$0(10);  return true;
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   @Nullable
/*     */   protected SettingsEditor<NodeJsRunConfiguration> createEditor(@NotNull NodeJsRunConfiguration configuration, Consumer<NodeJsInterpreterChangeListener> listenerRegistrar) {
/*  93 */     if (configuration == null) $$$reportNull$$$0(11);  return (SettingsEditor<NodeJsRunConfiguration>)new NodeProfilingPanel(configuration.getProject());
/*     */   }
/*     */   
/*     */   private static class MyLocalRuntimeSessionHelper implements NodeJSRuntimeSession {
/*     */     private final NodeProfilingSettings mySettings;
/*     */     private final NodeJsRunConfiguration myRunConfiguration;
/*     */     private final NodeProfilingRuntimeConfigurer myConfigurer;
/*     */     private final NodeFileTransfer myFileTransfer;
/*     */     private final NodeProfilingRuntimeSettings myRuntimeSettings;
/* 102 */     private int myInspectorPort = -1;
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     MyLocalRuntimeSessionHelper(NodeProfilingSettings settings, NodeJsRunConfiguration runConfiguration, NodeProfilingRuntimeConfigurer configurer, NodeFileTransfer fileTransfer) throws ExecutionException {
/* 108 */       this.mySettings = settings;
/* 109 */       this.myRunConfiguration = runConfiguration;
/* 110 */       this.myConfigurer = configurer;
/* 111 */       this.myFileTransfer = fileTransfer;
/*     */       try {
/* 113 */         String workingDirectory = this.myRunConfiguration.getWorkingDirectory();
/* 114 */         assert workingDirectory != null;
/* 115 */         this
/* 116 */           .myRuntimeSettings = this.mySettings.createRuntimeSettings(this.myRunConfiguration.getProject(), this.myFileTransfer.getMappingFor(workingDirectory), this.myFileTransfer);
/* 117 */         this.myConfigurer.setRuntimeSettings(this.myRuntimeSettings);
/* 118 */       } catch (IOException e) {
/* 119 */         throw new ExecutionException(e);
/*     */       } 
/*     */     }
/*     */ 
/*     */     
/*     */     @NotNull
/*     */     public List<String> getNodeParameters(boolean isDebugStarted) throws IOException {
/* 126 */       SmartList<String> smartList = new SmartList();
/* 127 */       NodeJsInterpreter interpreter = this.myRunConfiguration.getInterpreter();
/*     */ 
/*     */       
/* 130 */       if (!isDebugStarted) {
/* 131 */         this.myInspectorPort = NetUtils.findAvailableSocketPort();
/* 132 */         smartList.add("--inspect=" + 
/* 133 */             NodeDebugCommandLineConfiguratorKt.createDebugPortString(this.myInspectorPort, interpreter));
/*     */       } 
/* 135 */       if (this.myRuntimeSettings != null) {
/* 136 */         String runtimeParameters = this.myRuntimeSettings.getNodeParameters();
/* 137 */         if (!StringUtil.isEmptyOrSpaces(runtimeParameters)) {
/* 138 */           smartList.addAll(ParametersListUtil.parse(runtimeParameters));
/*     */         }
/*     */       } 
/* 141 */       if ((smartList.isEmpty() ? (List)Collections.emptyList() : (List<String>)smartList) == null) $$$reportNull$$$0(0);  return smartList.isEmpty() ? (List)Collections.emptyList() : (List<String>)smartList;
/*     */     }
/*     */ 
/*     */     
/*     */     @NotNull
/*     */     public List<AnAction> getRunDebugActions() {
/* 147 */       this.myConfigurer.onCommandLineCreation();
/* 148 */       if ((this.myConfigurer.isTakeHeapSnapshots() ? 
/* 149 */         (List)Collections.singletonList(this.myConfigurer.createSnapshotAction(this.myInspectorPort)) : 
/* 150 */         (List)Collections.emptyList()) == null) $$$reportNull$$$0(1);  return this.myConfigurer.isTakeHeapSnapshots() ? (List)Collections.singletonList(this.myConfigurer.createSnapshotAction(this.myInspectorPort)) : (List)Collections.emptyList();
/*     */     }
/*     */ 
/*     */     
/*     */     @NotNull
/*     */     public List<Integer> getUsedPorts() {
/* 156 */       if (this.myInspectorPort != -1) {
/* 157 */         if (Collections.singletonList(Integer.valueOf(this.myInspectorPort)) == null) $$$reportNull$$$0(2);  return Collections.singletonList(Integer.valueOf(this.myInspectorPort));
/*     */       } 
/* 159 */       if (Collections.emptyList() == null) $$$reportNull$$$0(3);  return (List)Collections.emptyList();
/*     */     }
/*     */   }
/*     */ }


/* Location:              C:\Program Files\JetBrains\WebStorm 2021.1\plugins\NodeJS\lib\NodeJS.jar!\com\jetbrains\nodejs\run\NodeJSProfilingRunConfigurationExtension.class
 * Java compiler version: 11 (55.0)
 * JD-Core Version:       1.1.3
 */