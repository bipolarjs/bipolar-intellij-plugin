/*     */ package org.bipolar.nodeunit.execution;
/*     */ import com.intellij.execution.ExecutionException;
/*     */ import com.intellij.execution.Executor;
/*     */ import com.intellij.execution.configurations.ConfigurationFactory;
/*     */ import com.intellij.execution.configurations.LocatableConfigurationBase;
/*     */ import com.intellij.execution.configurations.RunConfiguration;
/*     */ import com.intellij.execution.configurations.RunProfileState;
/*     */ import com.intellij.execution.configurations.RuntimeConfigurationException;
/*     */ import com.intellij.execution.runners.ExecutionEnvironment;
/*     */ import com.intellij.javascript.JSRunProfileWithCompileBeforeLaunchOption;
/*     */ import com.intellij.javascript.nodejs.debug.NodeDebugRunConfiguration;
/*     */ import com.intellij.javascript.nodejs.interpreter.NodeJsInterpreter;
/*     */ import com.intellij.javascript.nodejs.util.NodePackage;
/*     */ import com.intellij.openapi.options.SettingsEditor;
/*     */ import com.intellij.openapi.project.Project;
/*     */ import com.intellij.openapi.util.InvalidDataException;
/*     */ import com.intellij.openapi.util.WriteExternalException;
/*     */ import com.intellij.openapi.vfs.VirtualFile;
/*     */ import org.bipolar.NodeJSBundle;
/*     */ import org.bipolar.nodeunit.execution.ui.NodeunitSettingsEditor;
/*     */ import org.jdom.Element;
/*     */ import org.jetbrains.annotations.NotNull;
/*     */ 
/*     */ public class NodeunitRunConfiguration extends LocatableConfigurationBase implements NodeDebugRunConfiguration, JSRunProfileWithCompileBeforeLaunchOption {
/*  25 */   private NodeunitSettings mySettings = (new NodeunitSettings.Builder()).build();
/*     */   
/*     */   public NodeunitRunConfiguration(@NotNull Project project, ConfigurationFactory factory, String name) {
/*  28 */     super(project, factory, name);
/*     */   }
/*     */ 
/*     */   
/*     */   public void onNewConfigurationCreated() {
/*  33 */     if (this.mySettings.getWorkingDirectory().isEmpty()) {
/*  34 */       VirtualFile dir = getProject().getBaseDir();
/*  35 */       if (dir != null) {
/*  36 */         this.mySettings = this.mySettings.builder().setWorkingDirectory(dir.getPath()).build();
/*     */       }
/*     */     } 
/*  39 */     if (this.mySettings.getTestType() == NodeunitTestType.DIRECTORY && this.mySettings.getDirectory().isEmpty()) {
/*  40 */       String workingDirPath = this.mySettings.getWorkingDirectory();
/*  41 */       VirtualFile workingDir = LocalFileSystem.getInstance().findFileByPath(workingDirPath);
/*  42 */       if (workingDir != null && workingDir.isValid() && workingDir.isDirectory()) {
/*  43 */         String[] testDirNames = { "test", "spec", "tests", "specs" };
/*  44 */         VirtualFile testDir = null;
/*  45 */         for (String testDirName : testDirNames) {
/*  46 */           testDir = workingDir.findChild(testDirName);
/*  47 */           if (testDir != null && testDir.isValid() && testDir.isDirectory()) {
/*     */             break;
/*     */           }
/*     */         } 
/*  51 */         if (testDir != null) {
/*  52 */           this.mySettings = this.mySettings.builder().setDirectory(testDir.getPath()).build();
/*     */         }
/*     */       } 
/*     */     } 
/*  56 */     if (this.mySettings.getNodeunitPackage().isEmptyPath()) {
/*  57 */       NodeJsInterpreter interpreter = this.mySettings.getInterpreterRef().resolve(getProject());
/*  58 */       NodePackage pkg = NodeunitExecutionUtils.PKG.findFirstDirectDependencyPackage(getProject(), interpreter, null);
/*  59 */       this.mySettings = this.mySettings.builder().setNodeunitPackage(pkg).build();
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   @NotNull
/*     */   public SettingsEditor<? extends RunConfiguration> getConfigurationEditor() {
/*  66 */     return (SettingsEditor<? extends RunConfiguration>)new NodeunitSettingsEditor(getProject());
/*     */   }
/*     */   @NotNull
/*     */   public NodeunitSettings getSettings() {
/*  70 */     if (this.mySettings == null) $$$reportNull$$$0(1);  return this.mySettings;
/*     */   }
/*     */   
/*     */   public void setSettings(@NotNull NodeunitSettings settings) {
/*  74 */     if (settings == null) $$$reportNull$$$0(2);  this.mySettings = settings;
/*     */   }
/*     */ 
/*     */   
/*     */   public void readExternal(@NotNull Element element) throws InvalidDataException {
/*  79 */     if (element == null) $$$reportNull$$$0(3);  super.readExternal(element);
/*  80 */     NodeunitSettings settings = NodeunitSettingsSerializationUtils.readFromJDomElement(element);
/*  81 */     setSettings(settings);
/*     */   }
/*     */ 
/*     */   
/*     */   public void writeExternal(@NotNull Element element) throws WriteExternalException {
/*  86 */     if (element == null) $$$reportNull$$$0(4);  super.writeExternal(element);
/*  87 */     NodeunitSettingsSerializationUtils.writeToJDomElement(element, this.mySettings);
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public RunProfileState getState(@NotNull Executor executor, @NotNull ExecutionEnvironment environment) throws ExecutionException {
/*  93 */     if (executor == null) $$$reportNull$$$0(5);  if (environment == null) $$$reportNull$$$0(6);  try { checkConfiguration(); }
/*     */     
/*  95 */     catch (RuntimeConfigurationException e)
/*  96 */     { throw new ExecutionException(e.getMessage()); }
/*     */     
/*  98 */     return new NodeunitRunProfileState(environment, this.mySettings);
/*     */   }
/*     */ 
/*     */   
/*     */   public void checkConfiguration() throws RuntimeConfigurationException {
/* 103 */     NodeunitExecutionUtils.checkConfiguration(getProject(), this.mySettings);
/*     */   }
/*     */   
/*     */   @NotNull
/*     */   public String suggestedName() {
/* 108 */     NodeunitSettings settings = getSettings();
/* 109 */     if (settings.getTestType() == NodeunitTestType.DIRECTORY) {
/* 110 */       if (!settings.getDirectory().isEmpty()) {
/* 111 */         if (NodeJSBundle.message("nodeunit.rc.run.all.in.label", new Object[] { settings.getDirectory() }) == null) $$$reportNull$$$0(7);  return NodeJSBundle.message("nodeunit.rc.run.all.in.label", new Object[] { settings.getDirectory() });
/*     */       }
/*     */     
/*     */     }
/* 115 */     else if (!settings.getJsFile().isEmpty()) {
/* 116 */       if (settings.getJsFile() == null) $$$reportNull$$$0(8);  return settings.getJsFile();
/*     */     } 
/*     */ 
/*     */     
/* 120 */     if (NodeJSBundle.message("nodeunit.rc.unnamed.label", new Object[0]) == null) $$$reportNull$$$0(9);  return NodeJSBundle.message("nodeunit.rc.unnamed.label", new Object[0]);
/*     */   }
/*     */ }


/* Location:              C:\Program Files\JetBrains\WebStorm 2021.1\plugins\NodeJS\lib\NodeJS.jar!\com\jetbrains\nodejs\nodeunit\execution\NodeunitRunConfiguration.class
 * Java compiler version: 11 (55.0)
 * JD-Core Version:       1.1.3
 */