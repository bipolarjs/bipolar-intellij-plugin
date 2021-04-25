/*    */ package org.bipolar.util;
/*    */ 
/*    */ import com.intellij.lang.javascript.JavaScriptFileType;
/*    */ import com.intellij.lang.javascript.TypeScriptFileType;
/*    */ import com.intellij.openapi.fileTypes.FileType;
/*    */ import com.intellij.openapi.fileTypes.impl.HashBangFileTypeDetector;
/*    */ 
/*    */ public class NodeFileTypeDetector {
/*    */   public static class JavaScriptFileTypeDetector extends HashBangFileTypeDetector {
/*    */     public JavaScriptFileTypeDetector() {
/* 11 */       super((FileType)JavaScriptFileType.INSTANCE, " node");
/*    */     }
/*    */   }
/*    */   
/*    */   public static class TypeScriptFileTypeDetector extends HashBangFileTypeDetector {
/*    */     public TypeScriptFileTypeDetector() {
/* 17 */       super((FileType)TypeScriptFileType.INSTANCE, " ts-node");
/*    */     }
/*    */   }
/*    */ }


/* Location:              C:\Program Files\JetBrains\WebStorm 2021.1\plugins\NodeJS\lib\NodeJS.jar!\com\jetbrains\nodej\\util\NodeFileTypeDetector.class
 * Java compiler version: 11 (55.0)
 * JD-Core Version:       1.1.3
 */