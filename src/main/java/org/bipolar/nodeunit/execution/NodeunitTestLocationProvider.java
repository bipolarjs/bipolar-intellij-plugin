/*     */ package org.bipolar.nodeunit.execution;
/*     */ 
/*     */ import com.intellij.execution.Location;
/*     */ import com.intellij.execution.PsiLocation;
/*     */ import com.intellij.execution.testframework.sm.runner.SMTestLocator;
/*     */ import com.intellij.javascript.testFramework.exports.ExportsTestFileStructure;
/*     */ import com.intellij.javascript.testFramework.exports.ExportsTestFileStructureBuilder;
/*     */ import com.intellij.lang.javascript.psi.JSFile;
/*     */ import com.intellij.openapi.diagnostic.Logger;
/*     */ import com.intellij.openapi.project.Project;
/*     */ import com.intellij.openapi.vfs.LocalFileSystem;
/*     */ import com.intellij.openapi.vfs.VirtualFile;
/*     */ import com.intellij.psi.PsiElement;
/*     */ import com.intellij.psi.PsiFile;
/*     */ import com.intellij.psi.PsiManager;
/*     */ import com.intellij.psi.search.GlobalSearchScope;
/*     */ import com.intellij.util.ObjectUtils;
/*     */ import java.io.File;
/*     */ import java.util.ArrayList;
/*     */ import java.util.Collections;
/*     */ import java.util.List;
/*     */ import org.jetbrains.annotations.NotNull;
/*     */ import org.jetbrains.annotations.Nullable;
/*     */ 
/*     */ public class NodeunitTestLocationProvider
/*     */   implements SMTestLocator {
/*  27 */   private static final Logger LOG = Logger.getInstance(NodeunitTestLocationProvider.class);
/*     */   
/*     */   private static final String PROTOCOL = "nodeunit";
/*     */   
/*     */   private final File myWorkingDirectory;
/*     */   
/*     */   public NodeunitTestLocationProvider(@NotNull File workingDirectory) {
/*  34 */     this.myWorkingDirectory = workingDirectory;
/*     */   }
/*     */ 
/*     */   
/*     */   @NotNull
/*     */   public List<Location> getLocation(@NotNull String protocol, @NotNull String path, @NotNull Project project, @NotNull GlobalSearchScope scope) {
/*  40 */     if (protocol == null) $$$reportNull$$$0(1);  if (path == null) $$$reportNull$$$0(2);  if (project == null) $$$reportNull$$$0(3);  if (scope == null) $$$reportNull$$$0(4);  if (!"nodeunit".equals(protocol)) {
/*  41 */       if (Collections.emptyList() == null) $$$reportNull$$$0(5);  return (List)Collections.emptyList();
/*     */     } 
/*  43 */     List<String> parts = decodePath(path);
/*  44 */     if (parts.isEmpty()) {
/*  45 */       if (Collections.emptyList() == null) $$$reportNull$$$0(6);  return (List)Collections.emptyList();
/*     */     } 
/*  47 */     JSFile jsFile = findJSFile(project, parts.get(0));
/*  48 */     if (jsFile == null) {
/*  49 */       if (Collections.emptyList() == null) $$$reportNull$$$0(7);  return (List)Collections.emptyList();
/*     */     } 
/*  51 */     ExportsTestFileStructure structure = (ExportsTestFileStructure)ExportsTestFileStructureBuilder.getInstance().fetchCachedTestFileStructure(jsFile);
/*  52 */     PsiElement testPsiElement = structure.findPsiElement(parts.subList(1, parts.size()));
/*  53 */     PsiElement navResult = (PsiElement)ObjectUtils.notNull(testPsiElement, jsFile);
/*  54 */     Location psiLocation = PsiLocation.fromPsiElement(navResult);
/*  55 */     if (Collections.singletonList(psiLocation) == null) $$$reportNull$$$0(8);  return Collections.singletonList(psiLocation);
/*     */   }
/*     */   
/*     */   @Nullable
/*     */   private JSFile findJSFile(@NotNull Project project, @NotNull String moduleName) {
/*  60 */     if (project == null) $$$reportNull$$$0(9);  if (moduleName == null) $$$reportNull$$$0(10);  if (!moduleName.endsWith(".js")) {
/*  61 */       moduleName = moduleName + ".js";
/*     */     }
/*  63 */     File file = new File(this.myWorkingDirectory, moduleName);
/*  64 */     if (file.isFile()) {
/*  65 */       VirtualFile jsVFile = LocalFileSystem.getInstance().findFileByIoFile(file);
/*  66 */       if (jsVFile == null || !jsVFile.exists()) {
/*  67 */         jsVFile = LocalFileSystem.getInstance().refreshAndFindFileByIoFile(file);
/*     */       }
/*  69 */       if (jsVFile != null && jsVFile.exists()) {
/*  70 */         PsiManager psiManager = PsiManager.getInstance(project);
/*  71 */         PsiFile psiFile = psiManager.findFile(jsVFile);
/*  72 */         return (JSFile)ObjectUtils.tryCast(psiFile, JSFile.class);
/*     */       } 
/*     */     } 
/*  75 */     return null;
/*     */   }
/*     */   
/*     */   private static List<String> decodePath(@NotNull String s) {
/*  79 */     if (s == null) $$$reportNull$$$0(11);  List<String> path = new ArrayList<>();
/*  80 */     StringBuilder builder = new StringBuilder();
/*  81 */     int i = 0;
/*  82 */     while (i < s.length()) {
/*  83 */       char ch = s.charAt(i);
/*  84 */       if (ch == ':') {
/*  85 */         int j = i + 1;
/*  86 */         boolean ok = false;
/*  87 */         if (j < s.length()) {
/*  88 */           char nextChar = s.charAt(j);
/*  89 */           if (nextChar == ',' || nextChar == ':') {
/*  90 */             builder.append(nextChar);
/*  91 */             i++;
/*  92 */             ok = true;
/*     */           } 
/*     */         } 
/*  95 */         if (!ok) {
/*  96 */           LOG.warn("Found malformed nodeunit locationHint data '" + s + "' at index " + i + ".");
/*  97 */           return Collections.emptyList();
/*     */         } 
/*  99 */       } else if (ch == ',') {
/* 100 */         path.add(builder.toString());
/* 101 */         builder.setLength(0);
/*     */       } else {
/* 103 */         builder.append(ch);
/*     */       } 
/* 105 */       i++;
/*     */     } 
/* 107 */     path.add(builder.toString());
/* 108 */     return path;
/*     */   }
/*     */ }


/* Location:              C:\Program Files\JetBrains\WebStorm 2021.1\plugins\NodeJS\lib\NodeJS.jar!\com\jetbrains\nodejs\nodeunit\execution\NodeunitTestLocationProvider.class
 * Java compiler version: 11 (55.0)
 * JD-Core Version:       1.1.3
 */