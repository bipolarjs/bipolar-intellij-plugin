/*    */ package org.bipolar.run.profile.heap;
/*    */ 
/*    */ import com.intellij.openapi.util.io.FileUtil;
/*    */ import java.io.File;
/*    */ import java.io.IOException;
/*    */ import java.util.ArrayList;
/*    */ import java.util.List;
/*    */ import org.jetbrains.annotations.NotNull;
/*    */ import org.jetbrains.annotations.Nullable;
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public class TempFiles<T>
/*    */   implements IndexFiles<T>
/*    */ {
/*    */   private final List<File> myFiles;
/*    */   @NotNull
/*    */   private final String myPrefix;
/*    */   
/*    */   public TempFiles(@NotNull String prefix) {
/* 35 */     this.myPrefix = prefix;
/* 36 */     this.myFiles = new ArrayList<>();
/*    */   }
/*    */ 
/*    */   
/*    */   public File generate(@NotNull T category, @Nullable String postfix) throws IOException {
/* 41 */     if (category == null) $$$reportNull$$$0(1);  File file = FileUtil.createTempFile(this.myPrefix, postfix, true);
/* 42 */     this.myFiles.add(file);
/* 43 */     return file;
/*    */   }
/*    */ 
/*    */   
/*    */   public void close() throws IOException {
/* 48 */     for (File file : this.myFiles)
/* 49 */       FileUtil.delete(file); 
/*    */   }
/*    */ }


/* Location:              C:\Program Files\JetBrains\WebStorm 2021.1\plugins\NodeJS\lib\NodeJS.jar!\com\jetbrains\nodejs\run\profile\heap\TempFiles.class
 * Java compiler version: 11 (55.0)
 * JD-Core Version:       1.1.3
 */