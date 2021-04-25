/*    */ package org.bipolar.doc;
/*    */ 
/*    */ import com.intellij.openapi.diagnostic.Logger;
/*    */ import java.io.File;
/*    */ import java.io.IOException;
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public final class Test
/*    */ {
/* 13 */   private static final Logger LOG = Logger.getInstance(Test.class);
/*    */   
/*    */   public static void main(String[] args) throws IOException {
/* 16 */     NodeDocSplitter splitter = new NodeDocSplitter(new File("/home/segrey/work/idea-master/system/webstorm/extLibs/nodejs-v0.11.4-src/core-modules-sources/doc/api/all.json"), new File("/home/segrey/tmp/core_modules_doc"));
/*    */ 
/*    */ 
/*    */     
/* 20 */     splitter.split();
/*    */   }
/*    */ }


/* Location:              C:\Program Files\JetBrains\WebStorm 2021.1\plugins\NodeJS\lib\NodeJS.jar!\com\jetbrains\nodejs\doc\Test.class
 * Java compiler version: 11 (55.0)
 * JD-Core Version:       1.1.3
 */