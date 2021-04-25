/*    */ package org.bipolar.run;
/*    */ 
/*    */ import com.intellij.execution.configurations.RunConfiguration;
/*    */
/*    */ import com.intellij.execution.configurations.RunnerSettings;
/*    */ import com.intellij.execution.process.ProcessHandler;
/*    */ import com.intellij.execution.runners.ExecutionEnvironment;
/*    */ import com.intellij.ide.browsers.BrowserStarter;
/*    */ import com.intellij.ide.browsers.StartBrowserSettings;
/*    */ import com.intellij.javascript.nodejs.NodeFileTransfer;
/*    */ import com.intellij.javascript.nodejs.interpreter.NodeJsInterpreterChangeListener;
/*    */ import com.intellij.openapi.options.SettingsEditor;
/*    */ import com.intellij.openapi.util.InvalidDataException;
/*    */ import com.intellij.openapi.util.NlsContexts.TabTitle;
/*    */ import com.intellij.util.Consumer;
/*    */ import org.bipolar.NodeJSBundle;
/*    */ import org.jdom.Element;
/*    */ import org.jetbrains.annotations.NotNull;
/*    */ import org.jetbrains.annotations.Nullable;
/*    */ 
/*    */ 
/*    */ 
/*    */ public class NodeJSStartBrowserRunConfigurationExtension
/*    */   extends NodeJSRunConfigurationExtension
/*    */ {
/*    */   @Nullable
/*    */   protected SettingsEditor<NodeJsRunConfiguration> createEditor(@NotNull NodeJsRunConfiguration configuration, Consumer<NodeJsInterpreterChangeListener> listenerRegistrar) {
/* 28 */     if (configuration == null) $$$reportNull$$$0(0);  return new StartBrowserSettingsEditor<>();
/*    */   }
/*    */ 
/*    */ 
/*    */   
/*    */   protected void readExternal(@NotNull NodeJsRunConfiguration runConfiguration, @NotNull Element element) throws InvalidDataException {
/* 34 */     if (runConfiguration == null) $$$reportNull$$$0(1);  if (element == null) $$$reportNull$$$0(2);  runConfiguration.setStartBrowserSettings(StartBrowserSettings.readExternal(element));
/*    */   }
/*    */ 
/*    */   
/*    */   protected void writeExternal(@NotNull NodeJsRunConfiguration runConfiguration, @NotNull Element element) {
/* 39 */     if (runConfiguration == null) $$$reportNull$$$0(3);  if (element == null) $$$reportNull$$$0(4);  runConfiguration.getStartBrowserSettings().writeExternal(element);
/*    */   }
/*    */ 
/*    */   
/*    */   public boolean isApplicableFor(@NotNull NodeJsRunConfiguration configuration) {
/* 44 */     if (configuration == null) $$$reportNull$$$0(5);  return true;
/*    */   }
/*    */ 
/*    */   
/*    */   public boolean isEnabledFor(@NotNull NodeJsRunConfiguration applicableConfiguration, @Nullable RunnerSettings runnerSettings) {
/* 49 */     if (applicableConfiguration == null) $$$reportNull$$$0(6);  return true;
/*    */   }
/*    */ 
/*    */ 
/*    */   
/*    */   @Nullable
/*    */   protected NodeJSRuntimeSession createLocalRuntimeSession(@NotNull NodeJsRunConfiguration runConfiguration, @NotNull ExecutionEnvironment environment) {
/* 56 */     if (runConfiguration == null) $$$reportNull$$$0(7);  if (environment == null) $$$reportNull$$$0(8);  return new MyRuntimeSessionHelper(runConfiguration);
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   @Nullable
/*    */   protected NodeJSRuntimeSession createRemoteRuntimeSession(@NotNull NodeJsRunConfiguration runConfiguration, @NotNull ExecutionEnvironment environment, @NotNull NodeFileTransfer fileTransfer) {
/* 64 */     if (runConfiguration == null) $$$reportNull$$$0(9);  if (environment == null) $$$reportNull$$$0(10);  if (fileTransfer == null) $$$reportNull$$$0(11);  return new MyRuntimeSessionHelper(runConfiguration);
/*    */   }
/*    */   
/*    */   @Nullable
/*    */   @TabTitle
/*    */   public String getEditorTitle() {
/* 70 */     return getTabTitle();
/*    */   } @NotNull
/*    */   @TabTitle
/*    */   public static String getTabTitle() {
/* 74 */     if (NodeJSBundle.message("rc.nodejs.browser.liveEdit.tab.name", new Object[0]) == null) $$$reportNull$$$0(12);  return NodeJSBundle.message("rc.nodejs.browser.liveEdit.tab.name", new Object[0]);
/*    */   }
/*    */ 
/*    */   
/*    */   public int getEditorPriority() {
/* 79 */     return -100;
/*    */   }
/*    */   
/*    */   private static class MyRuntimeSessionHelper implements NodeJSRuntimeSession {
/*    */     private final NodeJsRunConfiguration myConfiguration;
/*    */     
/*    */     MyRuntimeSessionHelper(NodeJsRunConfiguration configuration) {
/* 86 */       this.myConfiguration = configuration;
/*    */     }
/*    */ 
/*    */     
/*    */     public void onProcessStarted(ProcessHandler processHandler) {
/* 91 */       (new BrowserStarter((RunConfiguration)this.myConfiguration, this.myConfiguration.getStartBrowserSettings(), processHandler)).start();
/*    */     }
/*    */   }
/*    */ }


/* Location:              C:\Program Files\JetBrains\WebStorm 2021.1\plugins\NodeJS\lib\NodeJS.jar!\com\jetbrains\nodejs\run\NodeJSStartBrowserRunConfigurationExtension.class
 * Java compiler version: 11 (55.0)
 * JD-Core Version:       1.1.3
 */