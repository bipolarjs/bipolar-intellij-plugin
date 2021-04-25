/*     */ package org.bipolar.mocha.execution;
/*     */ 
/*     */ import com.intellij.execution.Location;
/*     */ import com.intellij.execution.PsiLocation;
/*     */ import com.intellij.execution.testframework.sm.runner.SMTestLocator;
/*     */ import com.intellij.javascript.testFramework.JsTestFileByTestNameIndex;
/*     */ import com.intellij.javascript.testFramework.exports.ExportsTestFileStructure;
/*     */ import com.intellij.javascript.testFramework.exports.ExportsTestFileStructureBuilder;
/*     */ import com.intellij.javascript.testFramework.interfaces.mochaTdd.MochaTddFileStructure;
/*     */ import com.intellij.javascript.testFramework.interfaces.mochaTdd.MochaTddFileStructureBuilder;
/*     */ import com.intellij.javascript.testFramework.jasmine.JasmineFileStructure;
/*     */ import com.intellij.javascript.testFramework.jasmine.JasmineFileStructureBuilder;
/*     */ import com.intellij.javascript.testFramework.qunit.QUnitFileStructure;
/*     */ import com.intellij.javascript.testFramework.qunit.QUnitFileStructureBuilder;
/*     */ import com.intellij.javascript.testFramework.util.EscapeUtils;
/*     */ import com.intellij.javascript.testFramework.util.JsTestFqn;
/*     */ import com.intellij.lang.javascript.psi.JSFile;
/*     */ import com.intellij.lang.javascript.psi.JSTestFileType;
/*     */ import com.intellij.openapi.project.Project;
/*     */ import com.intellij.openapi.util.io.FileUtil;
/*     */ import com.intellij.openapi.util.text.StringUtil;
/*     */ import com.intellij.openapi.vfs.LocalFileSystem;
/*     */ import com.intellij.openapi.vfs.VirtualFile;
/*     */ import com.intellij.psi.PsiElement;
/*     */ import com.intellij.psi.PsiFile;
/*     */ import com.intellij.psi.PsiManager;
/*     */ import com.intellij.psi.search.GlobalSearchScope;
/*     */ import com.intellij.util.ObjectUtils;
/*     */ import com.intellij.util.containers.ContainerUtil;
/*     */ import java.util.Collections;
/*     */ import java.util.List;
/*     */ import org.jetbrains.annotations.NotNull;
/*     */ import org.jetbrains.annotations.Nullable;
/*     */ 
/*     */ 
/*     */ 
/*     */ public class MochaTestLocationProvider
/*     */   implements SMTestLocator
/*     */ {
/*     */   private static final String SUITE_PROTOCOL_ID = "suite";
/*     */   private static final String TEST_PROTOCOL_ID = "test";
/*     */   private static final char SPLIT_CHAR = '.';
/*     */   private final String myUi;
/*     */   
/*     */   public MochaTestLocationProvider(@NotNull String ui) {
/*  46 */     this.myUi = ui;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   @NotNull
/*     */   public List<Location> getLocation(@NotNull String protocol, @NotNull String path, @Nullable String metaInfo, @NotNull Project project, @NotNull GlobalSearchScope scope) {
/*  56 */     if (protocol == null) $$$reportNull$$$0(1);  if (path == null) $$$reportNull$$$0(2);  if (project == null) $$$reportNull$$$0(3);  if (scope == null) $$$reportNull$$$0(4);  boolean suite = "suite".equals(protocol);
/*  57 */     if (suite || "test".equals(protocol)) {
/*  58 */       Location location = getTestLocation(project, path, metaInfo, suite);
/*  59 */       if (ContainerUtil.createMaybeSingletonList(location) == null) $$$reportNull$$$0(5);  return ContainerUtil.createMaybeSingletonList(location);
/*     */     } 
/*     */     
/*  62 */     if (Collections.emptyList() == null) $$$reportNull$$$0(6);  return (List)Collections.emptyList();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   @NotNull
/*     */   public List<Location> getLocation(@NotNull String protocol, @NotNull String path, @NotNull Project project, @NotNull GlobalSearchScope scope) {
/*  71 */     if (protocol == null) $$$reportNull$$$0(7);  if (path == null) $$$reportNull$$$0(8);  if (project == null) $$$reportNull$$$0(9);  if (scope == null) $$$reportNull$$$0(10);  throw new IllegalStateException("Should not be called");
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   @Nullable
/*     */   private Location getTestLocation(@NotNull Project project, @NotNull String locationData, @Nullable String testFilePath, boolean isSuite) {
/*     */     PsiElement psiElement;
/*  79 */     if (project == null) $$$reportNull$$$0(11);  if (locationData == null) $$$reportNull$$$0(12);  List<String> path = EscapeUtils.split(locationData, '.');
/*  80 */     if (path.isEmpty()) {
/*  81 */       return null;
/*     */     }
/*     */ 
/*     */     
/*  85 */     if ("bdd".equals(this.myUi)) {
/*  86 */       psiElement = findJasmineElement(project, path, testFilePath);
/*     */     }
/*  88 */     else if ("qunit".equals(this.myUi)) {
/*  89 */       psiElement = findQUnitElement(project, path, testFilePath, isSuite);
/*     */     }
/*  91 */     else if ("exports".equals(this.myUi)) {
/*  92 */       psiElement = findExportsElement(project, path, testFilePath);
/*     */     }
/*  94 */     else if ("tdd".equals(this.myUi) || "mocha-typescript".equals(this.myUi)) {
/*  95 */       psiElement = findTddElement(project, path, testFilePath, isSuite);
/*     */     } else {
/*     */       
/*  98 */       psiElement = findAppropriateElement(project, path, testFilePath, isSuite);
/*     */     } 
/* 100 */     if (psiElement != null) {
/* 101 */       return PsiLocation.fromPsiElement(psiElement);
/*     */     }
/* 103 */     return null;
/*     */   }
/*     */   
/*     */   private PsiElement findAppropriateElement(Project project, @NotNull List<String> path, @Nullable String testFilePath, boolean suite) {
/* 107 */     if (path == null) $$$reportNull$$$0(13);  String lowerCasedUi = StringUtil.toLowerCase(this.myUi);
/*     */     
/* 109 */     boolean bdd = false, qUnit = false, exports = false;
/* 110 */     if (lowerCasedUi.contains("bdd")) {
/* 111 */       bdd = true;
/* 112 */       PsiElement element = findJasmineElement(project, path, testFilePath);
/* 113 */       if (element != null) {
/* 114 */         return element;
/*     */       }
/*     */     } 
/* 117 */     if (lowerCasedUi.contains("qunit")) {
/* 118 */       qUnit = true;
/* 119 */       PsiElement element = findQUnitElement(project, path, testFilePath, suite);
/* 120 */       if (element != null) {
/* 121 */         return element;
/*     */       }
/*     */     } 
/* 124 */     if (lowerCasedUi.contains("exports")) {
/* 125 */       exports = true;
/* 126 */       PsiElement element = findExportsElement(project, path, testFilePath);
/* 127 */       if (element != null) {
/* 128 */         return element;
/*     */       }
/*     */     } 
/* 131 */     if (!bdd) {
/* 132 */       PsiElement element = findJasmineElement(project, path, testFilePath);
/* 133 */       if (element != null) {
/* 134 */         return element;
/*     */       }
/*     */     } 
/* 137 */     if (!qUnit) {
/* 138 */       PsiElement element = findQUnitElement(project, path, testFilePath, suite);
/* 139 */       if (element != null) {
/* 140 */         return element;
/*     */       }
/*     */     } 
/* 143 */     if (!exports) {
/* 144 */       PsiElement element = findExportsElement(project, path, testFilePath);
/* 145 */       if (element != null) {
/* 146 */         return element;
/*     */       }
/*     */     } 
/* 149 */     return null;
/*     */   }
/*     */   
/*     */   @Nullable
/*     */   private static PsiElement findJasmineElement(Project project, @NotNull List<String> location, @Nullable String testFilePath) {
/* 154 */     if (location == null) $$$reportNull$$$0(14);  VirtualFile executedFile = findFile(testFilePath);
/* 155 */     JsTestFqn testFqn = new JsTestFqn(JSTestFileType.JASMINE, location);
/* 156 */     GlobalSearchScope scope = GlobalSearchScope.projectScope(project);
/* 157 */     List<VirtualFile> jsTestVirtualFiles = JsTestFileByTestNameIndex.findFiles(testFqn, scope, executedFile);
/* 158 */     for (VirtualFile file : jsTestVirtualFiles) {
/* 159 */       PsiFile psiFile = PsiManager.getInstance(project).findFile(file);
/* 160 */       if (psiFile instanceof JSFile) {
/* 161 */         JSFile jsFile = (JSFile)psiFile;
/* 162 */         JasmineFileStructureBuilder builder = JasmineFileStructureBuilder.getInstance();
/* 163 */         JasmineFileStructure jasmineFileStructure = (JasmineFileStructure)builder.fetchCachedTestFileStructure(jsFile);
/* 164 */         PsiElement element = jasmineFileStructure.findPsiElement(testFqn.getNames(), null);
/* 165 */         if (element != null && element.isValid()) {
/* 166 */           return element;
/*     */         }
/*     */       } 
/*     */     } 
/* 170 */     return null;
/*     */   }
/*     */   
/*     */   @Nullable
/*     */   private static VirtualFile findFile(@Nullable String filePath) {
/* 175 */     if (StringUtil.isEmptyOrSpaces(filePath)) return null; 
/* 176 */     return LocalFileSystem.getInstance().findFileByPath(FileUtil.toSystemIndependentName(filePath));
/*     */   }
/*     */   
/*     */   @Nullable
/*     */   private static PsiElement findQUnitElement(@NotNull Project project, @NotNull List<String> location, @Nullable String testFilePath, boolean suite) {
/*     */     String moduleName, testName;
/* 182 */     if (project == null) $$$reportNull$$$0(15);  if (location == null) $$$reportNull$$$0(16);  VirtualFile executedFile = findFile(testFilePath);
/*     */     
/* 184 */     if (suite) {
/* 185 */       moduleName = location.get(0);
/* 186 */       testName = null;
/*     */     
/*     */     }
/* 189 */     else if (location.size() > 1) {
/* 190 */       moduleName = location.get(0);
/* 191 */       testName = location.get(1);
/*     */     } else {
/*     */       
/* 194 */       moduleName = "Default Module";
/* 195 */       testName = location.get(0);
/*     */     } 
/*     */     
/* 198 */     String key = JsTestFileByTestNameIndex.createQUnitKey(moduleName, testName);
/* 199 */     GlobalSearchScope scope = GlobalSearchScope.projectScope(project);
/* 200 */     List<VirtualFile> jsTestVirtualFiles = JsTestFileByTestNameIndex.findFilesByKey(key, scope, executedFile);
/* 201 */     for (VirtualFile file : jsTestVirtualFiles) {
/* 202 */       PsiFile psiFile = PsiManager.getInstance(project).findFile(file);
/* 203 */       if (psiFile instanceof JSFile) {
/* 204 */         JSFile jsFile = (JSFile)psiFile;
/* 205 */         QUnitFileStructureBuilder builder = QUnitFileStructureBuilder.getInstance();
/* 206 */         QUnitFileStructure qunitFileStructure = (QUnitFileStructure)builder.fetchCachedTestFileStructure(jsFile);
/* 207 */         PsiElement element = qunitFileStructure.findPsiElement(moduleName, testName);
/* 208 */         if (element != null && element.isValid()) {
/* 209 */           return element;
/*     */         }
/*     */       } 
/*     */     } 
/* 213 */     return null;
/*     */   }
/*     */   
/*     */   @Nullable
/*     */   private static PsiElement findExportsElement(@NotNull Project project, @NotNull List<String> location, @Nullable String testFilePath) {
/* 218 */     if (project == null) $$$reportNull$$$0(17);  if (location == null) $$$reportNull$$$0(18);  JSFile file = findJSFile(project, testFilePath);
/* 219 */     if (file == null) {
/* 220 */       return null;
/*     */     }
/* 222 */     ExportsTestFileStructure structure = (ExportsTestFileStructure)ExportsTestFileStructureBuilder.getInstance().fetchCachedTestFileStructure(file);
/* 223 */     return structure.findPsiElement(location);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   @Nullable
/*     */   private static PsiElement findTddElement(@NotNull Project project, @NotNull List<String> location, @Nullable String testFilePath, boolean suite) {
/* 231 */     if (project == null) $$$reportNull$$$0(19);  if (location == null) $$$reportNull$$$0(20);  VirtualFile executedFile = findFile(testFilePath);
/* 232 */     GlobalSearchScope scope = GlobalSearchScope.projectScope(project);
/* 233 */     JsTestFqn testFqn = new JsTestFqn(JSTestFileType.TDD, location);
/* 234 */     List<VirtualFile> jsTestVirtualFiles = JsTestFileByTestNameIndex.findFiles(testFqn, scope, executedFile);
/* 235 */     for (VirtualFile file : jsTestVirtualFiles) {
/* 236 */       PsiFile psiFile = PsiManager.getInstance(project).findFile(file);
/* 237 */       if (psiFile instanceof JSFile) {
/* 238 */         JSFile jsFile = (JSFile)psiFile;
/* 239 */         MochaTddFileStructure structure = (MochaTddFileStructure)MochaTddFileStructureBuilder.getInstance().fetchCachedTestFileStructure(jsFile);
/* 240 */         List<String> suiteNames = suite ? location : location.subList(0, location.size() - 1);
/* 241 */         String testName = suite ? null : (String)ContainerUtil.getLastItem(location);
/* 242 */         PsiElement element = structure.findPsiElement(suiteNames, testName);
/* 243 */         if (element != null && element.isValid()) {
/* 244 */           return element;
/*     */         }
/*     */       } 
/*     */     } 
/* 248 */     return null;
/*     */   }
/*     */   
/*     */   @Nullable
/*     */   private static JSFile findJSFile(@NotNull Project project, @Nullable String testFilePath) {
/* 253 */     if (project == null) $$$reportNull$$$0(21);  VirtualFile file = findFile(testFilePath);
/* 254 */     if (file == null || !file.isValid()) {
/* 255 */       return null;
/*     */     }
/* 257 */     PsiFile psiFile = PsiManager.getInstance(project).findFile(file);
/* 258 */     return (JSFile)ObjectUtils.tryCast(psiFile, JSFile.class);
/*     */   }
/*     */ }


/* Location:              C:\Program Files\JetBrains\WebStorm 2021.1\plugins\NodeJS\lib\NodeJS.jar!\com\jetbrains\nodejs\mocha\execution\MochaTestLocationProvider.class
 * Java compiler version: 11 (55.0)
 * JD-Core Version:       1.1.3
 */