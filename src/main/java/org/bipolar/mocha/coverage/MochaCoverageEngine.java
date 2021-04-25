/*     */ package org.bipolar.mocha.coverage;
/*     */ import com.intellij.coverage.CoverageAnnotator;
/*     */ import com.intellij.coverage.CoverageDataManager;
/*     */ import com.intellij.coverage.CoverageFileProvider;
/*     */ import com.intellij.coverage.CoverageRunner;
/*     */ import com.intellij.coverage.CoverageSuite;
/*     */ import com.intellij.coverage.CoverageSuitesBundle;
/*     */ import com.intellij.coverage.view.CoverageListRootNode;
/*     */ import com.intellij.coverage.view.CoverageViewExtension;
/*     */ import com.intellij.coverage.view.CoverageViewManager;
/*     */ import com.intellij.coverage.view.DirectoryCoverageViewExtension;
/*     */ import com.intellij.execution.configurations.RunConfigurationBase;
/*     */ import com.intellij.execution.configurations.RunProfile;
/*     */ import com.intellij.execution.configurations.WrappingRunConfiguration;
/*     */ import com.intellij.execution.configurations.coverage.CoverageEnabledConfiguration;
/*     */ import com.intellij.execution.testframework.AbstractTestProxy;
/*     */ import com.intellij.ide.util.treeView.AbstractTreeNode;
/*     */ import com.intellij.openapi.application.ReadAction;
/*     */ import com.intellij.openapi.module.Module;
/*     */ import com.intellij.openapi.project.Project;
/*     */ import com.intellij.openapi.roots.ProjectFileIndex;
/*     */ import com.intellij.openapi.util.text.StringUtil;
/*     */ import com.intellij.openapi.vfs.VfsUtil;
/*     */ import com.intellij.openapi.vfs.VirtualFile;
/*     */ import com.intellij.psi.PsiDirectory;
/*     */ import com.intellij.psi.PsiElement;
/*     */ import com.intellij.psi.PsiFile;
/*     */ import com.intellij.psi.PsiManager;
/*     */ import com.intellij.psi.PsiNamedElement;
/*     */ import com.intellij.rt.coverage.data.ProjectData;
/*     */ import com.intellij.util.containers.ContainerUtil;
/*     */ import java.io.File;
/*     */ import java.util.Collections;
/*     */ import java.util.Date;
/*     */ import java.util.List;
/*     */ import java.util.Set;
/*     */ import org.bipolar.mocha.execution.MochaRunConfiguration;
import org.jetbrains.annotations.Nls;
/*     */ import org.jetbrains.annotations.NotNull;
/*     */ import org.jetbrains.annotations.Nullable;
/*     */ 
/*     */ public class MochaCoverageEngine extends CoverageEngine {
/*     */   public static final String ID = "MochaJavaScriptTestRunnerCoverage";
/*     */   
/*     */   public boolean isApplicableTo(@NotNull RunConfigurationBase configuration) {
/*  45 */     if (configuration == null) $$$reportNull$$$0(0);  return WrappingRunConfiguration.unwrapRunProfile((RunProfile)configuration) instanceof MochaRunConfiguration;
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean canHavePerTestCoverage(@NotNull RunConfigurationBase configuration) {
/*  50 */     if (configuration == null) $$$reportNull$$$0(1);  return false;
/*     */   }
/*     */ 
/*     */   
/*     */   @NotNull
/*     */   public CoverageEnabledConfiguration createCoverageEnabledConfiguration(@NotNull RunConfigurationBase configuration) {
/*  56 */     if (configuration == null) $$$reportNull$$$0(2);  return new MochaCoverageEnabledConfiguration(configuration);
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
/*     */   public CoverageSuite createCoverageSuite(@NotNull CoverageRunner covRunner, @NotNull String name, @NotNull CoverageFileProvider coverageDataFileProvider, String[] filters, long lastCoverageTimeStamp, @Nullable String suiteToMerge, boolean coverageByTestEnabled, boolean tracingEnabled, boolean trackTestFolders, Project project) {
/*  70 */     if (covRunner == null) $$$reportNull$$$0(3);  if (name == null) $$$reportNull$$$0(4);  if (coverageDataFileProvider == null) $$$reportNull$$$0(5);  return (CoverageSuite)new MochaCoverageSuite(covRunner, name, coverageDataFileProvider, lastCoverageTimeStamp, coverageByTestEnabled, tracingEnabled, trackTestFolders, (Project)SYNTHETIC_LOCAL_VARIABLE_11, this);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public CoverageSuite createCoverageSuite(@NotNull CoverageRunner covRunner, @NotNull String name, @NotNull CoverageFileProvider coverageDataFileProvider, @NotNull CoverageEnabledConfiguration config) {
/*  80 */     if (covRunner == null) $$$reportNull$$$0(6);  if (name == null) $$$reportNull$$$0(7);  if (coverageDataFileProvider == null) $$$reportNull$$$0(8);  if (config == null) $$$reportNull$$$0(9);  if (config instanceof MochaCoverageEnabledConfiguration) {
/*  81 */       Project project = config.getConfiguration().getProject();
/*  82 */       return createCoverageSuite(covRunner, name, coverageDataFileProvider, null, (new Date())
/*  83 */           .getTime(), null, false, false, true, project);
/*     */     } 
/*  85 */     return null;
/*     */   }
/*     */ 
/*     */   
/*     */   public CoverageSuite createEmptyCoverageSuite(@NotNull CoverageRunner coverageRunner) {
/*  90 */     if (coverageRunner == null) $$$reportNull$$$0(10);  return (CoverageSuite)new MochaCoverageSuite(this);
/*     */   }
/*     */ 
/*     */   
/*     */   @NotNull
/*     */   public CoverageAnnotator getCoverageAnnotator(@NotNull Project project) {
/*  96 */     if (project == null) $$$reportNull$$$0(11);  if (MochaCoverageAnnotator.getInstance(project) == null) $$$reportNull$$$0(12);  return (CoverageAnnotator)MochaCoverageAnnotator.getInstance(project);
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean coverageEditorHighlightingApplicableTo(@NotNull PsiFile psiFile) {
/* 101 */     if (psiFile == null) $$$reportNull$$$0(13);  return (psiFile instanceof com.intellij.lang.javascript.psi.JSFile || psiFile instanceof com.intellij.psi.impl.source.html.HtmlFileImpl);
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean acceptedByFilters(@NotNull PsiFile psiFile, @NotNull CoverageSuitesBundle suite) {
/* 106 */     if (psiFile == null) $$$reportNull$$$0(14);  if (suite == null) $$$reportNull$$$0(15);  return true;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean recompileProjectAndRerunAction(@NotNull Module module, @NotNull CoverageSuitesBundle suite, @NotNull Runnable chooseSuiteAction) {
/* 113 */     if (module == null) $$$reportNull$$$0(16);  if (suite == null) $$$reportNull$$$0(17);  if (chooseSuiteAction == null) $$$reportNull$$$0(18);  return false;
/*     */   }
/*     */ 
/*     */   
/*     */   public String getQualifiedName(@NotNull File outputFile, @NotNull PsiFile sourceFile) {
/* 118 */     if (outputFile == null) $$$reportNull$$$0(19);  if (sourceFile == null) $$$reportNull$$$0(20);  return getQName(sourceFile);
/*     */   }
/*     */   
/*     */   @Nullable
/*     */   private static String getQName(@NotNull PsiFile sourceFile) {
/* 123 */     if (sourceFile == null) $$$reportNull$$$0(21);  VirtualFile file = sourceFile.getVirtualFile();
/* 124 */     if (file == null) {
/* 125 */       return null;
/*     */     }
/* 127 */     return file.getPath();
/*     */   }
/*     */ 
/*     */   
/*     */   @NotNull
/*     */   public Set<String> getQualifiedNames(@NotNull PsiFile sourceFile) {
/* 133 */     if (sourceFile == null) $$$reportNull$$$0(22);  String qName = getQName(sourceFile);
/* 134 */     if (((qName != null) ? Collections.singleton(qName) : (Set)Collections.emptySet()) == null) $$$reportNull$$$0(23);  return (qName != null) ? Collections.singleton(qName) : (Set)Collections.emptySet();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean includeUntouchedFileInCoverage(@NotNull String qualifiedName, @NotNull File outputFile, @NotNull PsiFile sourceFile, @NotNull CoverageSuitesBundle suite) {
/* 142 */     if (qualifiedName == null) $$$reportNull$$$0(24);  if (outputFile == null) $$$reportNull$$$0(25);  if (sourceFile == null) $$$reportNull$$$0(26);  if (suite == null) $$$reportNull$$$0(27);  return false;
/*     */   }
/*     */ 
/*     */   
/*     */   public List<Integer> collectSrcLinesForUntouchedFile(@NotNull File classFile, @NotNull CoverageSuitesBundle suite) {
/* 147 */     if (classFile == null) $$$reportNull$$$0(28);  if (suite == null) $$$reportNull$$$0(29);  return null;
/*     */   }
/*     */ 
/*     */   
/*     */   public List<PsiElement> findTestsByNames(String[] testNames, @NotNull Project project) {
/* 152 */     if (project == null) $$$reportNull$$$0(30);  if (testNames == null) $$$reportNull$$$0(31);  return Collections.emptyList();
/*     */   }
/*     */ 
/*     */   
/*     */   public String getTestMethodName(@NotNull PsiElement element, @NotNull AbstractTestProxy testProxy) {
/* 157 */     if (element == null) $$$reportNull$$$0(32);  if (testProxy == null) $$$reportNull$$$0(33);  return null;
/*     */   }
/*     */   
/*     */   @Nls
/*     */   public String getPresentableText() {
/* 162 */     return "MochaJavaScriptTestRunnerCoverage";
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean coverageProjectViewStatisticsApplicableTo(VirtualFile fileOrDir) {
/* 167 */     return !fileOrDir.isDirectory();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public CoverageViewExtension createCoverageViewExtension(final Project project, final CoverageSuitesBundle suiteBundle, CoverageViewManager.StateBean stateBean) {
/* 174 */     return (CoverageViewExtension)new DirectoryCoverageViewExtension(project, getCoverageAnnotator(project), suiteBundle, stateBean)
/*     */       {
/*     */         public List<AbstractTreeNode<?>> getChildrenNodes(AbstractTreeNode node) {
/* 177 */           return ContainerUtil.filter(super.getChildrenNodes(node), child -> !StringUtil.equals(child.getName(), ".idea"));
/*     */         }
/*     */ 
/*     */ 
/*     */         
/*     */         @NotNull
/*     */         public AbstractTreeNode createRootNode() {
/* 184 */           if ((AbstractTreeNode)ReadAction.compute(() -> { VirtualFile rootDir = MochaCoverageEngine.findRootDir(project, suiteBundle); if (rootDir == null) rootDir = this.myProject.getBaseDir();  PsiDirectory psiRootDir = PsiManager.getInstance(this.myProject).findDirectory(rootDir); return new CoverageListRootNode(this.myProject, (PsiNamedElement)psiRootDir, this.mySuitesBundle, this.myStateBean); }) == null) $$$reportNull$$$0(0);  return (AbstractTreeNode)ReadAction.compute(() -> {
/*     */                 VirtualFile rootDir = MochaCoverageEngine.findRootDir(project, suiteBundle);
/*     */                 if (rootDir == null) {
/*     */                   rootDir = this.myProject.getBaseDir();
/*     */                 }
/*     */                 PsiDirectory psiRootDir = PsiManager.getInstance(this.myProject).findDirectory(rootDir);
/*     */                 return new CoverageListRootNode(this.myProject, (PsiNamedElement)psiRootDir, this.mySuitesBundle, this.myStateBean);
/*     */               });
/*     */         }
/*     */       };
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   @Nullable
/*     */   private static VirtualFile findRootDir(@NotNull Project project, @NotNull CoverageSuitesBundle suitesBundle) {
/* 202 */     if (project == null) $$$reportNull$$$0(34);  if (suitesBundle == null) $$$reportNull$$$0(35);  CoverageDataManager coverageDataManager = CoverageDataManager.getInstance(project);
/* 203 */     for (CoverageSuite suite : suitesBundle.getSuites()) {
/* 204 */       ProjectData data = suite.getCoverageData(coverageDataManager);
/* 205 */       if (data != null) {
/* 206 */         for (String path : data.getClasses().keySet()) {
/* 207 */           VirtualFile file = VfsUtil.findFileByIoFile(new File(path), false);
/* 208 */           if (file != null && file.isValid()) {
/* 209 */             ProjectFileIndex projectFileIndex = ProjectFileIndex.getInstance(project);
/* 210 */             VirtualFile contentRoot = projectFileIndex.getContentRootForFile(file);
/* 211 */             if (contentRoot != null && contentRoot.isDirectory() && contentRoot.isValid()) {
/* 212 */               return contentRoot;
/*     */             }
/*     */           } 
/*     */         } 
/*     */       }
/*     */     } 
/* 218 */     return null;
/*     */   }
/*     */ }


/* Location:              C:\Program Files\JetBrains\WebStorm 2021.1\plugins\NodeJS\lib\NodeJS.jar!\com\jetbrains\nodejs\mocha\coverage\MochaCoverageEngine.class
 * Java compiler version: 11 (55.0)
 * JD-Core Version:       1.1.3
 */