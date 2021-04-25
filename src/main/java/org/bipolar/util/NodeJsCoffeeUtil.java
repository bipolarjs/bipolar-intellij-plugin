/*    */ package org.bipolar.util;
/*    */ 
/*    */ import com.intellij.openapi.util.text.StringUtil;
/*    */ import com.intellij.openapi.vfs.VirtualFile;
/*    */ import com.intellij.psi.PsiElement;
/*    */ import com.intellij.psi.PsiFile;
/*    */ import org.jetbrains.annotations.NotNull;
/*    */ import org.jetbrains.annotations.Nullable;
/*    */ 
/*    */ 
/*    */ 
/*    */ public final class NodeJsCoffeeUtil
/*    */ {
/*    */   public static final String COFFEE_SCRIPT_PKG_NAME = "coffee-script";
/*    */   
/*    */   public static boolean isCoffee(@NotNull PsiElement element) {
/* 17 */     if (element == null) $$$reportNull$$$0(0);  VirtualFile file = getVirtualFile(element);
/* 18 */     return (file != null && StringUtil.endsWith(file.getNameSequence(), ".coffee"));
/*    */   }
/*    */   
/*    */   @Nullable
/*    */   private static VirtualFile getVirtualFile(@NotNull PsiElement element) {
/* 23 */     if (element == null) $$$reportNull$$$0(1);  PsiFile psiFile = element.getContainingFile();
/* 24 */     if (psiFile == null) {
/* 25 */       return null;
/*    */     }
/* 27 */     return psiFile.getOriginalFile().getVirtualFile();
/*    */   }
/*    */ }


/* Location:              C:\Program Files\JetBrains\WebStorm 2021.1\plugins\NodeJS\lib\NodeJS.jar!\com\jetbrains\nodej\\util\NodeJsCoffeeUtil.class
 * Java compiler version: 11 (55.0)
 * JD-Core Version:       1.1.3
 */