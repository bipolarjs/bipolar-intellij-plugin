/*     */ package org.bipolar.boilerplate.npmInit;
/*     */ 
/*     */ import com.intellij.execution.ExecutionException;
/*     */ import com.intellij.execution.filters.Filter;
/*     */
/*     */ import com.intellij.javascript.nodejs.interpreter.NodeJsInterpreter;
/*     */ import com.intellij.javascript.nodejs.interpreter.NodeJsInterpreterField;
/*     */ import com.intellij.javascript.nodejs.interpreter.NodeJsInterpreterManager;
/*     */ import com.intellij.javascript.nodejs.library.core.NodeCoreLibraryConfigurator;
/*     */ import com.intellij.javascript.nodejs.npm.NpmManager;
/*     */ import com.intellij.javascript.nodejs.npm.NpmUtil;
/*     */ import com.intellij.javascript.nodejs.packages.NodePackageUtil;
/*     */ import com.intellij.javascript.nodejs.util.NodePackage;
/*     */ import com.intellij.javascript.nodejs.util.NodePackageField;
/*     */ import com.intellij.javascript.nodejs.util.NodePackageRef;
/*     */ import com.intellij.lang.javascript.boilerplate.NpmPackageProjectGenerator;
/*     */ import com.intellij.lang.javascript.boilerplate.NpxPackageDescriptor;
/*     */ import com.intellij.lang.javascript.buildTools.npm.PackageJsonUtil;
/*     */ import com.intellij.openapi.application.ApplicationManager;
/*     */ import com.intellij.openapi.module.Module;
/*     */ import com.intellij.openapi.project.Project;
/*     */ import com.intellij.openapi.roots.ContentEntry;
/*     */ import com.intellij.openapi.startup.StartupManager;
/*     */ import com.intellij.openapi.vfs.VirtualFile;
/*     */ import com.intellij.platform.ProjectGeneratorPeer;
/*     */ import com.intellij.util.ArrayUtilRt;
/*     */ import com.intellij.util.PathUtil;
/*     */
/*     */ import org.bipolar.NodeJSBundle;
/*     */ import icons.JavaScriptLanguageIcons;
/*     */ import java.io.File;
/*     */ import java.util.Collections;
/*     */ import java.util.List;
/*     */ import javax.swing.Icon;
/*     */
import org.jetbrains.annotations.Nls;
/*     */ import org.jetbrains.annotations.NotNull;
/*     */ 
/*     */ public class NpmInitProjectGenerator
/*     */   extends NpmPackageProjectGenerator {
/*     */   public void generateProject(@NotNull Project project, @NotNull VirtualFile baseDir, @NotNull NpmPackageProjectGenerator.Settings settings, @NotNull Module module) {
/*  41 */     if (project == null) $$$reportNull$$$0(0);  if (baseDir == null) $$$reportNull$$$0(1);  if (settings == null) $$$reportNull$$$0(2);  if (module == null) $$$reportNull$$$0(3);  super.generateProject(project, baseDir, settings, module);
/*  42 */     StartupManager.getInstance(project).runWhenProjectIsInitialized(() -> NpmManager.getInstance(project).setPackageRef(settings.myPackageRef));
/*     */   }
/*     */ 
/*     */   
/*     */   @NotNull
/*     */   protected Runnable postInstall(@NotNull Project project, @NotNull VirtualFile baseDir, File workingDir) {
/*  48 */     if (project == null) $$$reportNull$$$0(4);  if (baseDir == null) $$$reportNull$$$0(5);  super.postInstall(project, baseDir, workingDir).run();
/*  49 */     if ((() -> ApplicationManager.getApplication().invokeLater(())) == null) $$$reportNull$$$0(6);  return () -> ApplicationManager.getApplication().invokeLater(());
/*     */   }
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
/*     */   @Nls
/*     */   @NotNull
/*     */   public String getName() {
/*  69 */     if (NodeJSBundle.message("npm.init.generator.name", new Object[0]) == null) $$$reportNull$$$0(7);  return NodeJSBundle.message("npm.init.generator.name", new Object[0]);
/*     */   }
/*     */ 
/*     */   
/*     */   public String getDescription() {
/*  74 */     return NodeJSBundle.message("npm.init.generator.description", new Object[0]);
/*     */   }
/*     */ 
/*     */   
/*     */   protected Filter[] filters(@NotNull Project project, @NotNull VirtualFile baseDir) {
/*  79 */     if (project == null) $$$reportNull$$$0(8);  if (baseDir == null) $$$reportNull$$$0(9);  if (Filter.EMPTY_ARRAY == null) $$$reportNull$$$0(10);  return Filter.EMPTY_ARRAY;
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   @NotNull
/*     */   protected String executable(@NotNull NodePackage pkg) {
/*  86 */     if (pkg == null) $$$reportNull$$$0(11);  try { if (NpmUtil.getValidNpmCliJsFilePath(pkg) == null) $$$reportNull$$$0(12);  return NpmUtil.getValidNpmCliJsFilePath(pkg); }
/*     */     
/*  88 */     catch (ExecutionException e)
/*  89 */     { return ""; }
/*     */   
/*     */   }
/*     */ 
/*     */   
/*     */   protected String[] generatorArgs(@NotNull Project project, @NotNull VirtualFile baseDir) {
/*  95 */     if (project == null) $$$reportNull$$$0(13);  if (baseDir == null) $$$reportNull$$$0(14);  if (ArrayUtilRt.EMPTY_STRING_ARRAY == null) $$$reportNull$$$0(15);  return ArrayUtilRt.EMPTY_STRING_ARRAY;
/*     */   }
/*     */ 
/*     */   
/*     */   protected void customizeModule(@NotNull VirtualFile baseDir, ContentEntry entry) {
/* 100 */     if (baseDir == null) $$$reportNull$$$0(16); 
/*     */   }
/*     */   
/*     */   protected String[] generatorArgs(@NotNull Project project, @NotNull VirtualFile baseDir, @NotNull NpmPackageProjectGenerator.Settings settings) {
/* 104 */     if (project == null) $$$reportNull$$$0(17);  if (baseDir == null) $$$reportNull$$$0(18);  if (settings == null) $$$reportNull$$$0(19);  (new String[2])[0] = "init"; (new String[2])[1] = "-y"; if (new String[2] == null) $$$reportNull$$$0(20);  return new String[2];
/*     */   }
/*     */ 
/*     */   
/*     */   protected String validateProjectPath(@NotNull String path) {
/* 109 */     if (path == null) $$$reportNull$$$0(21);  String error = NodePackageUtil.validateNpmPackageName(PathUtil.getFileName(path));
/* 110 */     if (error != null) {
/* 111 */       return error;
/*     */     }
/* 113 */     return super.validateProjectPath(path);
/*     */   }
/*     */ 
/*     */   
/*     */   @NotNull
/*     */   protected String packageName() {
/* 119 */     return "npm";
/*     */   }
/*     */ 
/*     */   
/*     */   @NotNull
/*     */   protected String presentablePackageName() {
/* 125 */     if (NpmUtil.getPackageManagerPackageFieldLabelText() == null) $$$reportNull$$$0(22);  return NpmUtil.getPackageManagerPackageFieldLabelText();
/*     */   }
/*     */ 
/*     */   
/*     */   @NotNull
/*     */   protected List<NpxPackageDescriptor.NpxCommand> getNpxCommands() {
/* 131 */     if (Collections.emptyList() == null) $$$reportNull$$$0(23);  return (List)Collections.emptyList();
/*     */   }
/*     */ 
/*     */   
/*     */   public Icon getIcon() {
/* 136 */     return JavaScriptLanguageIcons.Nodejs.Nodejs;
/*     */   }
/*     */ 
/*     */   
/*     */   @NotNull
/*     */   public ProjectGeneratorPeer<NpmPackageProjectGenerator.Settings> createPeer() {
/* 142 */     return (ProjectGeneratorPeer<NpmPackageProjectGenerator.Settings>)new NpmPackageProjectGenerator.NpmPackageGeneratorPeer()
/*     */       {
/*     */         @NotNull
/*     */         protected NodePackageField createAndInitPackageField(@NotNull NodeJsInterpreterField interpreterField) {
/* 146 */           if (interpreterField == null) $$$reportNull$$$0(0);  NodePackageField field = NpmUtil.createPackageManagerPackageField(interpreterField, true);
/* 147 */           field.setSelectedRef(NodePackageRef.create("npm"));
/* 148 */           if (field == null) $$$reportNull$$$0(1);  return field;
/*     */         }
/*     */       };
/*     */   }
/*     */ }


/* Location:              C:\Program Files\JetBrains\WebStorm 2021.1\plugins\NodeJS\lib\NodeJS.jar!\com\jetbrains\nodejs\boilerplate\npmInit\NpmInitProjectGenerator.class
 * Java compiler version: 11 (55.0)
 * JD-Core Version:       1.1.3
 */