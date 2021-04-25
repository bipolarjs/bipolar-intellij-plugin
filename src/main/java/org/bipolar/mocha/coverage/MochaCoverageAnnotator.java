/*    */ package org.bipolar.mocha.coverage;
/*    */ 
/*    */ import com.intellij.coverage.BaseCoverageAnnotator;
/*    */ import com.intellij.coverage.CoverageBundle;
/*    */ import com.intellij.coverage.CoverageDataManager;
/*    */ import com.intellij.coverage.CoverageSuitesBundle;
/*    */ import com.intellij.coverage.SimpleCoverageAnnotator;
/*    */ import com.intellij.openapi.components.ServiceManager;
/*    */ import com.intellij.openapi.project.Project;
/*    */ import com.intellij.psi.PsiDirectory;
/*    */ import org.bipolar.NodeJSBundle;
/*    */ import org.jetbrains.annotations.NotNull;
/*    */ import org.jetbrains.annotations.Nullable;
/*    */ 
/*    */ public class MochaCoverageAnnotator
/*    */   extends SimpleCoverageAnnotator {
/*    */   public MochaCoverageAnnotator(@NotNull Project project) {
/* 18 */     super(project);
/*    */   }
/*    */   
/*    */   public static MochaCoverageAnnotator getInstance(@NotNull Project project) {
/* 22 */     if (project == null) $$$reportNull$$$0(1);  return (MochaCoverageAnnotator)ServiceManager.getService(project, MochaCoverageAnnotator.class);
/*    */   }
/*    */ 
/*    */   
/*    */   protected boolean shouldCollectCoverageInsideLibraryDirs() {
/* 27 */     return false;
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   @Nullable
/*    */   public String getDirCoverageInformationString(@NotNull PsiDirectory directory, @NotNull CoverageSuitesBundle currentSuite, @NotNull CoverageDataManager manager) {
/* 35 */     if (directory == null) $$$reportNull$$$0(2);  if (currentSuite == null) $$$reportNull$$$0(3);  if (manager == null) $$$reportNull$$$0(4);  BaseCoverageAnnotator.DirCoverageInfo coverageInfo = getDirCoverageInfo(directory, currentSuite);
/* 36 */     if (coverageInfo == null) {
/* 37 */       return null;
/*    */     }
/*    */     
/* 40 */     if (manager.isSubCoverageActive()) {
/* 41 */       return (coverageInfo.coveredLineCount > 0) ? NodeJSBundle.message("mocha.coverage.view.dir_covered.text", new Object[0]) : null;
/*    */     }
/*    */     
/* 44 */     String filesCoverageInfo = getFilesCoverageInformationString(coverageInfo);
/* 45 */     if (filesCoverageInfo != null) {
/* 46 */       StringBuilder builder = new StringBuilder();
/* 47 */       builder.append(filesCoverageInfo);
/* 48 */       String linesCoverageInfo = getLinesCoverageInformationString((BaseCoverageAnnotator.FileCoverageInfo)coverageInfo);
/* 49 */       if (linesCoverageInfo != null) {
/* 50 */         builder.append(": ").append(linesCoverageInfo);
/*    */       }
/* 52 */       return builder.toString();
/*    */     } 
/* 54 */     return null;
/*    */   }
/*    */ 
/*    */   
/*    */   protected String getLinesCoverageInformationString(@NotNull BaseCoverageAnnotator.FileCoverageInfo info) {
/* 59 */     if (info == null) $$$reportNull$$$0(5);  if (info.totalLineCount == 0) {
/* 60 */       return null;
/*    */     }
/* 62 */     if (info.coveredLineCount == 0) {
/* 63 */       return CoverageBundle.message("lines.covered.info.not.covered", new Object[0]);
/*    */     }
/*    */     
/* 66 */     return "" + calcCoveragePercentage(info) + calcCoveragePercentage(info);
/*    */   }
/*    */ 
/*    */ 
/*    */   
/*    */   @Nullable
/*    */   protected String getFilesCoverageInformationString(@NotNull BaseCoverageAnnotator.DirCoverageInfo info) {
/* 73 */     if (info == null) $$$reportNull$$$0(6);  if (info.totalFilesCount == 0) return null; 
/* 74 */     return NodeJSBundle.message("mocha.coverage.view.files_coverage_info.text", new Object[] { Integer.valueOf(info.totalFilesCount) });
/*    */   }
/*    */ }


/* Location:              C:\Program Files\JetBrains\WebStorm 2021.1\plugins\NodeJS\lib\NodeJS.jar!\com\jetbrains\nodejs\mocha\coverage\MochaCoverageAnnotator.class
 * Java compiler version: 11 (55.0)
 * JD-Core Version:       1.1.3
 */