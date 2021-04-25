/*    */ package org.bipolar.mocha.coverage;
/*    */ 
/*    */ import com.intellij.coverage.CoverageEngine;
/*    */ import com.intellij.coverage.CoverageRunner;
/*    */ import com.intellij.coverage.CoverageSuite;
/*    */ import com.intellij.javascript.nodejs.interpreter.NodeJsInterpreter;
/*    */ import com.intellij.javascript.testing.CoverageProjectDataLoader;
/*    */ import com.intellij.openapi.diagnostic.Logger;
/*    */ import com.intellij.rt.coverage.data.ProjectData;
/*    */ import java.io.File;
/*    */ import java.util.Objects;
/*    */ import org.jetbrains.annotations.NotNull;
/*    */ import org.jetbrains.annotations.Nullable;
/*    */ 
/*    */ public class MochaCoverageRunner
/*    */   extends CoverageRunner
/*    */ {
/* 18 */   private static final Logger LOG = Logger.getInstance(MochaCoverageRunner.class);
/*    */   private String myWorkingDirectory;
/*    */   private NodeJsInterpreter myInterpreter;
/*    */   
/*    */   public void setWorkingDirectory(@Nullable String workingDirectory) {
/* 23 */     this.myWorkingDirectory = workingDirectory;
/*    */   }
/*    */   
/*    */   public void setInterpreter(@Nullable NodeJsInterpreter interpreter) {
/* 27 */     this.myInterpreter = interpreter;
/*    */   }
/*    */   
/*    */   @NotNull
/*    */   public static MochaCoverageRunner getInstance() {
/* 32 */     if ((MochaCoverageRunner)Objects.requireNonNull((MochaCoverageRunner)CoverageRunner.getInstance(MochaCoverageRunner.class)) == null) $$$reportNull$$$0(0);  return Objects.requireNonNull((MochaCoverageRunner)CoverageRunner.getInstance(MochaCoverageRunner.class));
/*    */   }
/*    */ 
/*    */   
/*    */   public ProjectData loadCoverageData(@NotNull File sessionDataFile, @Nullable CoverageSuite baseCoverageSuite) {
/* 37 */     if (sessionDataFile == null) $$$reportNull$$$0(1);  File basePathDir = getBaseDir();
/*    */     try {
/* 39 */       return CoverageProjectDataLoader.readProjectData(sessionDataFile, basePathDir, this.myInterpreter);
/*    */     }
/* 41 */     catch (Exception e) {
/* 42 */       LOG.warn("Can't read coverage data", e);
/*    */       
/* 44 */       return null;
/*    */     } 
/*    */   }
/*    */   @NotNull
/*    */   private File getBaseDir() {
/* 49 */     String basePath = this.myWorkingDirectory;
/* 50 */     if (basePath != null) {
/* 51 */       return new File(basePath);
/*    */     }
/* 53 */     return new File(".");
/*    */   }
/*    */ 
/*    */   
/*    */   @NotNull
/*    */   public String getPresentableName() {
/* 59 */     return "MochaPresentableName";
/*    */   }
/*    */ 
/*    */   
/*    */   @NotNull
/*    */   public String getId() {
/* 65 */     return "MochaJavaScriptTestRunnerCoverage";
/*    */   }
/*    */ 
/*    */   
/*    */   @NotNull
/*    */   public String getDataFileExtension() {
/* 71 */     return "dat";
/*    */   }
/*    */ 
/*    */   
/*    */   public boolean acceptsCoverageEngine(@NotNull CoverageEngine engine) {
/* 76 */     if (engine == null) $$$reportNull$$$0(2);  return engine instanceof MochaCoverageEngine;
/*    */   }
/*    */ }


/* Location:              C:\Program Files\JetBrains\WebStorm 2021.1\plugins\NodeJS\lib\NodeJS.jar!\com\jetbrains\nodejs\mocha\coverage\MochaCoverageRunner.class
 * Java compiler version: 11 (55.0)
 * JD-Core Version:       1.1.3
 */