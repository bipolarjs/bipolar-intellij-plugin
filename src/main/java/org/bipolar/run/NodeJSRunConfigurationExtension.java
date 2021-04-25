/*    */ package org.bipolar.run;
/*    */ 
/*    */ import com.intellij.execution.ExecutionException;
/*    */ import com.intellij.execution.configuration.RunConfigurationExtensionBase;
/*    */ import com.intellij.execution.configurations.GeneralCommandLine;
/*    */
/*    */ import com.intellij.execution.configurations.RunnerSettings;
/*    */ import com.intellij.execution.configurations.RuntimeConfigurationException;
/*    */ import com.intellij.execution.runners.ExecutionEnvironment;
/*    */ import com.intellij.javascript.nodejs.NodeFileTransfer;
/*    */ import com.intellij.javascript.nodejs.interpreter.NodeJsInterpreterChangeListener;
/*    */ import com.intellij.openapi.extensions.ExtensionPointName;
/*    */ import com.intellij.openapi.options.SettingsEditor;
/*    */ import com.intellij.openapi.util.NlsContexts.TabTitle;
/*    */ import com.intellij.util.Consumer;
/*    */ import org.jetbrains.annotations.NotNull;
/*    */ import org.jetbrains.annotations.Nullable;
/*    */ 
/*    */ 
/*    */ public abstract class NodeJSRunConfigurationExtension
/*    */   extends RunConfigurationExtensionBase<NodeJsRunConfiguration>
/*    */ {
/* 23 */   protected static final ExtensionPointName<NodeJSRunConfigurationExtension> EP_NAME = new ExtensionPointName("NodeJS.runConfigurationExtension");
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   protected boolean showEditorInMainPage(@NotNull NodeJsRunConfiguration runConfiguration) {
/* 37 */     if (runConfiguration == null) $$$reportNull$$$0(0);  return false;
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   protected final void patchCommandLine(@NotNull NodeJsRunConfiguration configuration, @Nullable RunnerSettings runnerSettings, @NotNull GeneralCommandLine cmdLine, @NotNull String runnerId) {
/* 46 */     if (configuration == null) $$$reportNull$$$0(1);  if (cmdLine == null) $$$reportNull$$$0(2);  if (runnerId == null) $$$reportNull$$$0(3); 
/*    */   } public void checkConfiguration(@NotNull NodeJsRunConfiguration configuration) throws RuntimeConfigurationException {
/* 48 */     if (configuration == null) $$$reportNull$$$0(4); 
/*    */   }
/*    */   
/*    */   @Nullable
/*    */   protected SettingsEditor<NodeJsRunConfiguration> createEditor(@NotNull NodeJsRunConfiguration configuration, Consumer<NodeJsInterpreterChangeListener> listenerRegistrar) {
/* 53 */     if (configuration == null) $$$reportNull$$$0(5);  return null;
/*    */   }
/*    */   
/*    */   @Nullable
/*    */   @TabTitle
/*    */   public String getEditorTitle() {
/* 59 */     return null;
/*    */   }
/*    */   
/*    */   public int getEditorPriority() {
/* 63 */     return 0;
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   @Nullable
/*    */   public NodeFileTransfer overrideFileTransfer(@NotNull NodeJsRunConfiguration configuration) throws ExecutionException {
/* 71 */     if (configuration == null) $$$reportNull$$$0(6);  return null;
/*    */   }
/*    */   
/*    */   @Nullable
/*    */   protected abstract NodeJSRuntimeSession createLocalRuntimeSession(@NotNull NodeJsRunConfiguration paramNodeJsRunConfiguration, @NotNull ExecutionEnvironment paramExecutionEnvironment) throws ExecutionException;
/*    */   
/*    */   @Nullable
/*    */   protected abstract NodeJSRuntimeSession createRemoteRuntimeSession(@NotNull NodeJsRunConfiguration paramNodeJsRunConfiguration, @NotNull ExecutionEnvironment paramExecutionEnvironment, @NotNull NodeFileTransfer paramNodeFileTransfer) throws ExecutionException;
/*    */ }


/* Location:              C:\Program Files\JetBrains\WebStorm 2021.1\plugins\NodeJS\lib\NodeJS.jar!\com\jetbrains\nodejs\run\NodeJSRunConfigurationExtension.class
 * Java compiler version: 11 (55.0)
 * JD-Core Version:       1.1.3
 */