/*    */ package org.bipolar.doc;
/*    */ 
/*    */ import com.intellij.util.text.SemVer;
/*    */ import java.io.File;
/*    */ import org.jetbrains.annotations.NotNull;
/*    */ 
/*    */ 
/*    */ public class NodeDoc
/*    */ {
/*    */   private final SemVer myNodeVersion;
/*    */   private final File myDocDir;
/*    */   
/*    */   public NodeDoc(@NotNull SemVer nodeVersion, @NotNull File docDir) {
/* 14 */     this.myNodeVersion = nodeVersion;
/* 15 */     this.myDocDir = docDir;
/*    */   }
/*    */ }


/* Location:              C:\Program Files\JetBrains\WebStorm 2021.1\plugins\NodeJS\lib\NodeJS.jar!\com\jetbrains\nodejs\doc\NodeDoc.class
 * Java compiler version: 11 (55.0)
 * JD-Core Version:       1.1.3
 */