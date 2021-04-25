/*    */ package org.bipolar.run.profile;
/*    */ 
/*    */ import com.intellij.openapi.util.io.FileUtil;
/*    */ import org.bipolar.run.profile.heap.IndexFiles;
/*    */ import java.io.File;
/*    */ import java.io.IOException;
/*    */ import java.util.ArrayList;
/*    */ import java.util.HashMap;
/*    */ import java.util.List;
/*    */ import java.util.Map;
/*    */ import org.jetbrains.annotations.NotNull;
/*    */ import org.jetbrains.annotations.Nullable;
/*    */ 
/*    */ 
/*    */ 
/*    */ public class CategoryIndexFiles<T extends Enum>
/*    */   implements IndexFiles<T>
/*    */ {
/*    */   private final Map<T, List<File>> myFilesMap;
/*    */   private final Object myLock;
/*    */   private final File myRootDir;
/*    */   
/*    */   public CategoryIndexFiles(File rootDir) {
/* 24 */     this.myRootDir = rootDir;
/* 25 */     this.myLock = new Object();
/* 26 */     this.myFilesMap = new HashMap<>();
/*    */   }
/*    */   
/*    */   public Map<T, List<File>> getFilesMap() {
/* 30 */     synchronized (this.myLock) {
/* 31 */       return this.myFilesMap;
/*    */     } 
/*    */   }
/*    */   
/*    */   public void putAll(Map<T, List<File>> map) {
/* 36 */     synchronized (this.myLock) {
/* 37 */       this.myFilesMap.putAll(map);
/*    */     } 
/*    */   }
/*    */   
/*    */   public List<File> getFiles(T category) {
/* 42 */     synchronized (this.myLock) {
/* 43 */       return this.myFilesMap.get(category);
/*    */     } 
/*    */   }
/*    */   
/*    */   public File getOneFile(T category) throws IOException {
/*    */     List<File> files;
/* 49 */     synchronized (this.myLock) {
/* 50 */       files = this.myFilesMap.get(category);
/*    */     } 
/* 52 */     if (files.size() != 1) throw new IOException("Wrong number of index files of type: " + category); 
/* 53 */     return files.get(0);
/*    */   }
/*    */ 
/*    */   
/*    */   public File generate(@NotNull T category, @Nullable String postfix) throws IOException {
/* 58 */     if (category == null) $$$reportNull$$$0(0);  File file = FileUtil.createTempFile(this.myRootDir, "v8", postfix, true, false);
/* 59 */     synchronized (this.myLock) {
/* 60 */       List<File> list = this.myFilesMap.get(category);
/* 61 */       if (list == null) this.myFilesMap.put(category, list = new ArrayList<>()); 
/* 62 */       list.add(file);
/*    */     } 
/* 64 */     return file;
/*    */   }
/*    */   
/*    */   public void close() throws IOException {}
/*    */ }


/* Location:              C:\Program Files\JetBrains\WebStorm 2021.1\plugins\NodeJS\lib\NodeJS.jar!\com\jetbrains\nodejs\run\profile\CategoryIndexFiles.class
 * Java compiler version: 11 (55.0)
 * JD-Core Version:       1.1.3
 */