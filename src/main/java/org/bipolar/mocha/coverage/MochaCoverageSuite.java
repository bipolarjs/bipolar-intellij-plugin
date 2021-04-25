/*    */ package org.bipolar.mocha.coverage;
/*    */ 
/*    */ import com.intellij.coverage.BaseCoverageSuite;
/*    */ import com.intellij.coverage.CoverageEngine;
/*    */ import com.intellij.coverage.CoverageFileProvider;
/*    */ import com.intellij.coverage.CoverageRunner;
/*    */ import com.intellij.openapi.project.Project;
/*    */ import org.jetbrains.annotations.NotNull;
/*    */ import org.jetbrains.annotations.Nullable;
/*    */ 
/*    */ public class MochaCoverageSuite
/*    */   extends BaseCoverageSuite {
/*    */   private final MochaCoverageEngine myCoverageEngine;
/*    */   
/*    */   public MochaCoverageSuite(MochaCoverageEngine coverageEngine) {
/* 16 */     this.myCoverageEngine = coverageEngine;
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public MochaCoverageSuite(CoverageRunner coverageRunner, String name, @Nullable CoverageFileProvider fileProvider, long lastCoverageTimeStamp, boolean coverageByTestEnabled, boolean tracingEnabled, boolean trackTestFolders, Project project, MochaCoverageEngine coverageEngine) {
/* 28 */     super(name, fileProvider, lastCoverageTimeStamp, coverageByTestEnabled, tracingEnabled, trackTestFolders, coverageRunner, project);
/*    */     
/* 30 */     this.myCoverageEngine = coverageEngine;
/*    */   }
/*    */ 
/*    */   
/*    */   @NotNull
/*    */   public CoverageEngine getCoverageEngine() {
/* 36 */     if (this.myCoverageEngine == null) $$$reportNull$$$0(0);  return this.myCoverageEngine;
/*    */   }
/*    */ }


/* Location:              C:\Program Files\JetBrains\WebStorm 2021.1\plugins\NodeJS\lib\NodeJS.jar!\com\jetbrains\nodejs\mocha\coverage\MochaCoverageSuite.class
 * Java compiler version: 11 (55.0)
 * JD-Core Version:       1.1.3
 */