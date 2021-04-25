/*    */ package org.bipolar.run;
/*    */ 
/*    */ import com.intellij.javascript.debugger.execution.DebuggableProcessRunConfigurationBase;
/*    */ import com.intellij.openapi.util.text.StringUtil;
/*    */ import com.intellij.openapi.vfs.LocalFileSystem;
/*    */ import com.intellij.openapi.vfs.VfsUtilCore;
/*    */ import com.intellij.openapi.vfs.VirtualFile;
/*    */ import com.intellij.psi.PsiElement;
/*    */ import com.intellij.psi.util.PsiUtilBase;
/*    */ import com.intellij.refactoring.listeners.RefactoringElementListener;
/*    */ import com.intellij.refactoring.listeners.UndoRefactoringElementAdapter;
/*    */ import org.jetbrains.annotations.NotNull;
/*    */ import org.jetbrains.annotations.Nullable;
/*    */ 
/*    */ 
/*    */ public final class NodeJsRunConfigurationRefactoringHandler
/*    */ {
/*    */   @Nullable
/*    */   public static RefactoringElementListener createRefactoringElementListener(@NotNull NodeJsRunConfiguration configuration, @Nullable PsiElement element) {
/* 20 */     if (configuration == null) $$$reportNull$$$0(0);  if (element != null) {
/* 21 */       VirtualFile elementVirtualFile = PsiUtilBase.asVirtualFile(element);
/* 22 */       if (elementVirtualFile != null) {
/* 23 */         String workingDirectoryRelativePath = getWorkingDirectoryRelativePath(configuration, elementVirtualFile);
/* 24 */         String inputFileRelativePath = getInputFileRelativePath(configuration, elementVirtualFile);
/* 25 */         if (workingDirectoryRelativePath != null || inputFileRelativePath != null) {
/* 26 */           return (RefactoringElementListener)new NodeRcRefactoringElementListener(configuration, workingDirectoryRelativePath, inputFileRelativePath);
/*    */         }
/*    */       } 
/*    */     } 
/* 30 */     return null;
/*    */   }
/*    */ 
/*    */   
/*    */   @Nullable
/*    */   private static String getWorkingDirectoryRelativePath(@NotNull NodeJsRunConfiguration configuration, @NotNull VirtualFile refactoredDir) {
/* 36 */     if (configuration == null) $$$reportNull$$$0(1);  if (refactoredDir == null) $$$reportNull$$$0(2);  String workingDirectoryPath = configuration.getWorkingDirectory();
/* 37 */     if (StringUtil.isNotEmpty(workingDirectoryPath)) {
/* 38 */       VirtualFile workingDirectory = LocalFileSystem.getInstance().findFileByPath(workingDirectoryPath);
/* 39 */       if (workingDirectory != null) {
/* 40 */         return VfsUtilCore.getRelativePath(workingDirectory, refactoredDir);
/*    */       }
/*    */     } 
/* 43 */     return null;
/*    */   }
/*    */   
/*    */   @Nullable
/*    */   private static String getInputFileRelativePath(@NotNull NodeJsRunConfiguration configuration, @NotNull VirtualFile refactoredFile) {
/* 48 */     if (configuration == null) $$$reportNull$$$0(3);  if (refactoredFile == null) $$$reportNull$$$0(4);  VirtualFile inputFile = DebuggableProcessRunConfigurationBase.findInputVirtualFile(configuration);
/* 49 */     if (inputFile != null) {
/* 50 */       return VfsUtilCore.getRelativePath(inputFile, refactoredFile);
/*    */     }
/* 52 */     return null;
/*    */   }
/*    */   
/*    */   private static class NodeRcRefactoringElementListener
/*    */     extends UndoRefactoringElementAdapter
/*    */   {
/*    */     private final NodeJsRunConfiguration myConfiguration;
/*    */     private final String myWorkingDirectoryRelativePath;
/*    */     private final String myInputFileRelativePath;
/*    */     
/*    */     NodeRcRefactoringElementListener(@NotNull NodeJsRunConfiguration configuration, @Nullable String workingDirectoryRelativePath, @Nullable String inputFileRelativePath) {
/* 63 */       this.myConfiguration = configuration;
/* 64 */       this.myWorkingDirectoryRelativePath = workingDirectoryRelativePath;
/* 65 */       this.myInputFileRelativePath = inputFileRelativePath;
/*    */     }
/*    */ 
/*    */     
/*    */     protected void refactored(@NotNull PsiElement element, @Nullable String oldQualifiedName) {
/* 70 */       if (element == null) $$$reportNull$$$0(1);  VirtualFile newFile = PsiUtilBase.asVirtualFile(element);
/* 71 */       if (newFile != null) {
/* 72 */         if (this.myWorkingDirectoryRelativePath != null) {
/* 73 */           VirtualFile workingDirectory = newFile.findFileByRelativePath(this.myWorkingDirectoryRelativePath);
/* 74 */           if (workingDirectory != null) {
/* 75 */             this.myConfiguration.setWorkingDirectory(workingDirectory.getPath());
/*    */           }
/*    */         } 
/* 78 */         if (this.myInputFileRelativePath != null) {
/* 79 */           VirtualFile inputFile = newFile.findFileByRelativePath(this.myInputFileRelativePath);
/* 80 */           if (inputFile != null)
/* 81 */             this.myConfiguration.setInputPath(inputFile.getPath()); 
/*    */         } 
/*    */       } 
/*    */     }
/*    */   }
/*    */ }


/* Location:              C:\Program Files\JetBrains\WebStorm 2021.1\plugins\NodeJS\lib\NodeJS.jar!\com\jetbrains\nodejs\run\NodeJsRunConfigurationRefactoringHandler.class
 * Java compiler version: 11 (55.0)
 * JD-Core Version:       1.1.3
 */