/*    */ package org.bipolar.util;
/*    */ 
/*    */ import com.intellij.openapi.application.ApplicationManager;
/*    */ import com.intellij.openapi.util.io.FileUtil;
/*    */ import com.intellij.util.PathUtil;
/*    */ import java.io.File;
/*    */ import java.io.IOException;
/*    */ import org.jetbrains.annotations.NotNull;
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public final class NodeJsCodeLocator
/*    */ {
/*    */   @NotNull
/*    */   public static File getBundledJsDir() {
/* 17 */     String jarPath = PathUtil.getJarPathForClass(NodeJsCodeLocator.class);
/* 18 */     if (jarPath.endsWith(".jar")) {
/* 19 */       File jarFile = new File(jarPath);
/* 20 */       if (!jarFile.isFile()) {
/* 21 */         throw new RuntimeException("Not a file (" + jarFile.getAbsolutePath() + ")");
/*    */       }
/* 23 */       File pluginBaseDir = jarFile.getParentFile().getParentFile();
/* 24 */       return new File(pluginBaseDir, "js");
/*    */     } 
/*    */     
/* 27 */     String srcDir = jarPath.replace("/out/classes/production/intellij.nodeJS", "/plugins/NodeJS/src");
/* 28 */     if (ApplicationManager.getApplication().isInternal() && (new File(srcDir)).isDirectory()) {
/* 29 */       jarPath = srcDir;
/*    */     }
/*    */     
/* 32 */     return new File(jarPath, "js");
/*    */   }
/*    */   
/*    */   @NotNull
/*    */   public static File getFileRelativeToJsDir(@NotNull String relativePath) throws IOException {
/* 37 */     if (relativePath == null) $$$reportNull$$$0(0);  File jsDir = getBundledJsDir();
/* 38 */     String systemDependentRelativePath = FileUtil.toSystemDependentName(relativePath);
/* 39 */     File file = new File(jsDir, systemDependentRelativePath);
/* 40 */     if (!file.isFile()) {
/* 41 */       throw new IOException("Cannot find " + relativePath);
/*    */     }
/* 43 */     if (file == null) $$$reportNull$$$0(1);  return file;
/*    */   }
/*    */ }


/* Location:              C:\Program Files\JetBrains\WebStorm 2021.1\plugins\NodeJS\lib\NodeJS.jar!\com\jetbrains\nodej\\util\NodeJsCodeLocator.class
 * Java compiler version: 11 (55.0)
 * JD-Core Version:       1.1.3
 */