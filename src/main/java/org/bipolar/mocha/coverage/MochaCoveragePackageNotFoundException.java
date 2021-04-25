/*     */ package org.bipolar.mocha.coverage;
/*     */ 
/*     */ import com.intellij.execution.ExecutionException;
/*     */ import com.intellij.execution.runners.ExecutionEnvironment;
/*     */ import com.intellij.execution.runners.ExecutionUtil;
/*     */ import com.intellij.javascript.nodejs.interpreter.NodeJsInterpreter;
/*     */ import com.intellij.javascript.nodejs.npm.NpmManager;
/*     */ import com.intellij.javascript.nodejs.npm.NpmUtil;
/*     */ import com.intellij.javascript.nodejs.util.NodePackage;
/*     */ import com.intellij.javascript.nodejs.util.NodePackageRef;
/*     */ import com.intellij.lang.javascript.buildTools.npm.rc.NpmCommand;
/*     */ import com.intellij.lang.javascript.modules.NpmPackageInstallerLight;
/*     */ import com.intellij.openapi.application.ApplicationManager;
/*     */ import com.intellij.openapi.project.Project;
/*     */ import com.intellij.openapi.util.text.HtmlChunk;
/*     */ import com.intellij.openapi.util.text.StringUtil;
/*     */ import com.intellij.util.PathUtil;
/*     */ import com.intellij.util.containers.ContainerUtil;
/*     */ import com.intellij.webcore.packaging.PackageManagementService;
/*     */ import org.bipolar.NodeJSBundle;
/*     */ import java.io.File;
/*     */ import java.util.List;
/*     */ import javax.swing.event.HyperlinkEvent;
/*     */ import javax.swing.event.HyperlinkListener;
/*     */ import org.jetbrains.annotations.Nls;
/*     */ import org.jetbrains.annotations.NotNull;
/*     */ import org.jetbrains.annotations.Nullable;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public final class MochaCoveragePackageNotFoundException
/*     */   extends ExecutionException
/*     */   implements HyperlinkListener
/*     */ {
/*     */   private final ExecutionEnvironment myEnv;
/*     */   private final NodeJsInterpreter myInterpreter;
/*     */   private final String myInstallWorkingDirectory;
/*     */   private final boolean mySaveDev;
/*     */   
/*     */   private MochaCoveragePackageNotFoundException(@NotNull @Nls String reason, @NotNull ExecutionEnvironment env, @NotNull NodeJsInterpreter interpreter, @NotNull String installWorkingDirectory, boolean saveDev) {
/*  45 */     super(reason);
/*  46 */     this.myEnv = env;
/*  47 */     this.myInterpreter = interpreter;
/*  48 */     this.myInstallWorkingDirectory = installWorkingDirectory;
/*  49 */     this.mySaveDev = saveDev;
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public static MochaCoveragePackageNotFoundException create(@NotNull Project project, @NotNull NodePackage mochaPackage, @NotNull ExecutionEnvironment env, @NotNull NodeJsInterpreter interpreter, @NotNull String workingDirectory) {
/*     */     boolean saveDev;
/*     */     String installWorkingDirectory;
/*  57 */     if (project == null) $$$reportNull$$$0(4);  if (mochaPackage == null) $$$reportNull$$$0(5);  if (env == null) $$$reportNull$$$0(6);  if (interpreter == null) $$$reportNull$$$0(7);  if (workingDirectory == null) $$$reportNull$$$0(8);  String mochaRootPath = PathUtil.getParentPath(PathUtil.getParentPath(mochaPackage.getSystemDependentPath()));
/*     */ 
/*     */     
/*  60 */     if ((new File(mochaRootPath, "package.json")).isFile()) {
/*  61 */       saveDev = true;
/*  62 */       installWorkingDirectory = mochaRootPath;
/*     */     }
/*  64 */     else if ((new File(workingDirectory, "package.json")).isFile()) {
/*  65 */       saveDev = true;
/*  66 */       installWorkingDirectory = workingDirectory;
/*     */     } else {
/*     */       
/*  69 */       saveDev = false;
/*  70 */       installWorkingDirectory = mochaRootPath;
/*     */     } 
/*  72 */     String text = NodeJSBundle.message("mocha.coverage.cannot_find_coverage_package.text", new Object[] { createNpmInstallLink(project, saveDev) });
/*  73 */     return new MochaCoveragePackageNotFoundException(text, env, interpreter, installWorkingDirectory, saveDev);
/*     */   }
/*     */   @Nls
/*     */   private static String createNpmInstallLink(@NotNull Project project, boolean saveDev) {
/*  77 */     if (project == null) $$$reportNull$$$0(9);  NodePackageRef npmPkgRef = NpmManager.getInstance(project).getPackageRef();
/*  78 */     List<String> commandParts = ContainerUtil.newArrayList((Object[])new String[] {
/*  79 */           NpmManager.getNpmPackagePresentableName(npmPkgRef), NpmCommand.ADD
/*  80 */           .getCliOption(NpmUtil.isYarnAlikePackageRef(npmPkgRef)), "nyc"
/*     */         });
/*     */     
/*  83 */     ContainerUtil.addIfNotNull(commandParts, saveDev ? NpmUtil.getInstallSaveOption(npmPkgRef, true) : null);
/*  84 */     return HtmlChunk.link("", StringUtil.join(commandParts, " ")).toString();
/*     */   }
/*     */ 
/*     */   
/*     */   public void hyperlinkUpdate(HyperlinkEvent e) {
/*  89 */     if (e != null && e.getEventType() == HyperlinkEvent.EventType.ACTIVATED) {
/*  90 */       Project project = this.myEnv.getProject();
/*  91 */       NpmPackageInstallerLight installerLight = (NpmPackageInstallerLight)ApplicationManager.getApplication().getService(NpmPackageInstallerLight.class);
/*  92 */       NodePackageRef npmPkgRef = NpmManager.getInstance(project).getPackageRef();
/*  93 */       installerLight.installPackage(project, this.myInterpreter, "nyc", null, new File(this.myInstallWorkingDirectory), new PackageManagementService.Listener()
/*     */           {
/*     */             public void operationStarted(String packageName) {}
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */             
/*     */             public void operationFinished(String packageName, @Nullable PackageManagementService.ErrorDescription errorDescription) {
/* 103 */               ApplicationManager.getApplication().invokeLater(() -> {
/*     */                     if (errorDescription == null) {
/*     */                       ExecutionUtil.restart(MochaCoveragePackageNotFoundException.this.myEnv);
/*     */                     }
/*     */                   });
/*     */             }
/*     */           }, 
/* 110 */           this.mySaveDev ? NpmUtil.getInstallSaveOption(npmPkgRef, true) : "");
/*     */     } 
/*     */   }
/*     */ }


/* Location:              C:\Program Files\JetBrains\WebStorm 2021.1\plugins\NodeJS\lib\NodeJS.jar!\com\jetbrains\nodejs\mocha\coverage\MochaCoveragePackageNotFoundException.class
 * Java compiler version: 11 (55.0)
 * JD-Core Version:       1.1.3
 */