/*     */ package org.bipolar.run.profile.cpu.calculation;
/*     */ 
/*     */ import com.intellij.javascript.nodejs.library.core.NodeCoreLibraryManager;
/*     */ import com.intellij.lang.javascript.JSTokenTypes;
/*     */ import com.intellij.lang.javascript.index.JavaScriptIndex;
/*     */ import com.intellij.lang.javascript.modules.NodeModuleUtil;
/*     */ import com.intellij.lang.javascript.psi.JSDefinitionExpression;
/*     */ import com.intellij.lang.javascript.psi.JSQualifiedName;
/*     */ import com.intellij.lang.javascript.psi.ecmal4.JSQualifiedNamedElement;
/*     */ import com.intellij.navigation.NavigationItem;
/*     */ import com.intellij.openapi.fileEditor.OpenFileDescriptor;
/*     */ import com.intellij.openapi.progress.EmptyProgressIndicator;
/*     */ import com.intellij.openapi.progress.ProgressIndicator;
/*     */ import com.intellij.openapi.project.Project;
/*     */ import com.intellij.openapi.util.io.FileUtil;
/*     */ import com.intellij.openapi.vfs.LocalFileSystem;
/*     */ import com.intellij.openapi.vfs.VirtualFile;
/*     */ import com.intellij.pom.Navigatable;
/*     */ import com.intellij.psi.PsiElement;
/*     */ import com.intellij.psi.PsiFile;
/*     */ import com.intellij.psi.impl.search.LowLevelSearchUtil;
/*     */ import com.intellij.psi.search.FilenameIndex;
/*     */ import com.intellij.psi.search.GlobalSearchScope;
/*     */ import com.intellij.psi.search.TextOccurenceProcessor;
/*     */ import com.intellij.util.containers.ContainerUtil;
/*     */ import com.intellij.util.text.StringSearcher;
/*     */ import java.io.File;
/*     */ import java.util.ArrayList;
/*     */ import java.util.Collection;
/*     */ import java.util.HashSet;
/*     */ import java.util.List;
/*     */ import java.util.Set;
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
/*     */ 
/*     */ public class V8ProfileLineFileDescriptor
/*     */ {
/*     */   private final String myName;
/*     */   private final String myPath;
/*     */   private final int myRow;
/*     */   private final int myCol;
/*     */   
/*     */   public V8ProfileLineFileDescriptor(String name, String path, int row, int col) {
/*  57 */     this.myName = name;
/*  58 */     this.myPath = path;
/*  59 */     this.myRow = row;
/*  60 */     this.myCol = col;
/*     */   }
/*     */   
/*     */   public String getPathOrName() {
/*  64 */     return (this.myPath == null) ? this.myName : this.myPath;
/*     */   }
/*     */   
/*     */   public String getName() {
/*  68 */     return this.myName;
/*     */   }
/*     */   
/*     */   public String getPath() {
/*  72 */     return this.myPath;
/*     */   }
/*     */   
/*     */   public int getRow() {
/*  76 */     return this.myRow;
/*     */   }
/*     */   
/*     */   public int getCol() {
/*  80 */     return this.myCol;
/*     */   }
/*     */   
/*     */   public Navigatable[] getNavigatables(Project project, String functionName, boolean isNative) {
/*  84 */     if (this.myPath != null) {
/*  85 */       VirtualFile file = LocalFileSystem.getInstance().refreshAndFindFileByIoFile(new File(this.myPath));
/*  86 */       if (file != null) {
/*  87 */         return new Navigatable[] { (Navigatable)new OpenFileDescriptor(project, file, Math.max(0, this.myRow - 1), Math.max(0, this.myCol - 1)) };
/*     */       }
/*  89 */       int idx = this.myPath.indexOf("node_modules");
/*  90 */       if (idx > 0) {
/*  91 */         return getNavigatablesFromNodeModules(project, idx);
/*     */       }
/*     */     } 
/*  94 */     if (this.myPath == null) {
/*  95 */       NodeCoreLibraryManager coreLibraryManager = NodeCoreLibraryManager.getInstance(project);
/*  96 */       VirtualFile fileByName = coreLibraryManager.findCoreModuleFileByName(this.myName);
/*  97 */       if (fileByName != null) {
/*  98 */         return new Navigatable[] { (Navigatable)new OpenFileDescriptor(project, fileByName, Math.max(0, this.myRow - 1), Math.max(0, this.myCol - 1)) };
/*     */       }
/*     */     } 
/*     */     
/* 102 */     JavaScriptIndex instance = JavaScriptIndex.getInstance(project);
/* 103 */     Set<Navigatable> items = ContainerUtil.set((Object[])instance.getFileByName(this.myName, true));
/* 104 */     Set<Navigatable> list = new HashSet<>();
/* 105 */     for (Navigatable navigationItem : items) {
/* 106 */       if (navigationItem instanceof PsiFile && GlobalSearchScope.allScope(project).contains(((PsiFile)navigationItem).getVirtualFile())) {
/* 107 */         PsiFile jsFile = (PsiFile)navigationItem;
/* 108 */         Collection<NavigationItem> innerSymbols = lookIntoFile(jsFile, functionName);
/* 109 */         if (!innerSymbols.isEmpty()) {
/* 110 */           list.addAll(innerSymbols);
/*     */         }
/* 112 */         list.add(jsFile); continue;
/*     */       } 
/* 114 */       if (navigationItem instanceof PsiFile) {
/* 115 */         list.add(new OpenFileDescriptor(project, ((PsiFile)navigationItem).getVirtualFile(), Math.max(0, this.myRow - 1), Math.max(0, this.myCol - 1))); continue;
/* 116 */       }  list.add(navigationItem);
/*     */     } 
/*     */     
/* 119 */     return list.<Navigatable>toArray(new Navigatable[0]);
/*     */   }
/*     */   
/*     */   private Navigatable[] getNavigatablesFromNodeModules(Project project, int idx) {
/* 123 */     LocalFileSystem lfs = LocalFileSystem.getInstance();
/* 124 */     String subPath = this.myPath.substring(idx).replace('\\', '/');
/* 125 */     List<VirtualFile> candidates = new ArrayList<>();
/*     */     
/* 127 */     Collection<VirtualFile> files = FilenameIndex.getVirtualFilesByName(project, "node_modules", GlobalSearchScope.allScope(project));
/* 128 */     for (VirtualFile file : files) {
/* 129 */       if (file.isDirectory()) {
/* 130 */         VirtualFile child = lfs.refreshAndFindFileByIoFile(new File(file.getPath(), subPath));
/* 131 */         if (child != null && child.isValid()) candidates.add(child);
/*     */       
/*     */       } 
/*     */     } 
/* 135 */     Collection<VirtualFile> packageJsonFiles = FilenameIndex.getVirtualFilesByName(project, "package.json", GlobalSearchScope.allScope(project));
/* 136 */     for (VirtualFile file : packageJsonFiles) {
/* 137 */       if (!file.isDirectory() && file.getParent() != null) {
/* 138 */         VirtualFile child = lfs.refreshAndFindFileByIoFile(new File(file.getParent().getPath(), subPath));
/* 139 */         if (child != null && child.isValid()) candidates.add(child); 
/*     */       } 
/*     */     } 
/* 142 */     candidates.sort(NodeModuleUtil.VIRTUAL_FILE_COMPARATOR);
/*     */     
/* 144 */     if ((Navigatable[])ContainerUtil.map2Array(candidates, Navigatable.class, file -> new OpenFileDescriptor(project, file, Math.max(0, this.myRow - 1), Math.max(0, this.myCol - 1))) == null) $$$reportNull$$$0(0);  return (Navigatable[])ContainerUtil.map2Array(candidates, Navigatable.class, file -> new OpenFileDescriptor(project, file, Math.max(0, this.myRow - 1), Math.max(0, this.myCol - 1)));
/*     */   }
/*     */   
/*     */   private static Collection<NavigationItem> lookIntoFile(PsiFile file, String functionName) {
/* 148 */     Collection<NavigationItem> list = new ArrayList<>();
/* 149 */     int idx = functionName.lastIndexOf('.');
/* 150 */     String clazzName = (idx > 0) ? functionName.substring(0, idx) : null;
/* 151 */     String cutFunctionName = (idx >= 0) ? functionName.substring(idx + 1) : functionName;
/*     */     
/* 153 */     TextOccurenceProcessor processor = (element, offsetInElement) -> {
/*     */         JSQualifiedNamedElement named = null;
/*     */         if (element instanceof JSDefinitionExpression && cutFunctionName.equals(((JSDefinitionExpression)element).getName())) {
/*     */           named = (JSQualifiedNamedElement)element;
/*     */         } else if (element.getNode().getElementType() == JSTokenTypes.IDENTIFIER && element.getParent() instanceof com.intellij.lang.javascript.psi.JSFunction) {
/*     */           named = (JSQualifiedNamedElement)element.getParent();
/*     */         } 
/*     */         if (named == null) {
/*     */           return true;
/*     */         }
/*     */         JSQualifiedName namespace = named.getNamespace();
/*     */         if (clazzName == null || (namespace != null && clazzName.equals(namespace.getQualifiedName())))
/*     */           list.add((NavigationItem)element); 
/*     */         return true;
/*     */       };
/* 168 */     StringSearcher searcher = new StringSearcher(cutFunctionName, true, true);
/* 169 */     LowLevelSearchUtil.processElementsContainingWordInElement(processor, (PsiElement)file, searcher, true, (ProgressIndicator)new EmptyProgressIndicator());
/* 170 */     return list;
/*     */   }
/*     */   
/*     */   public String getShortLink() {
/* 174 */     if (this.myRow < 0) return this.myName; 
/* 175 */     return this.myName + ":" + this.myName + ":" + this.myRow;
/*     */   }
/*     */   
/*     */   public String getLink() {
/* 179 */     if (this.myRow < 0) return FileUtil.toSystemDependentName(getPathOrName()); 
/* 180 */     return FileUtil.toSystemDependentName(getPathOrName()) + ":" + FileUtil.toSystemDependentName(getPathOrName()) + ":" + this.myRow;
/*     */   }
/*     */ }


/* Location:              C:\Program Files\JetBrains\WebStorm 2021.1\plugins\NodeJS\lib\NodeJS.jar!\com\jetbrains\nodejs\run\profile\cpu\calculation\V8ProfileLineFileDescriptor.class
 * Java compiler version: 11 (55.0)
 * JD-Core Version:       1.1.3
 */