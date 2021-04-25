/*     */ package org.bipolar.run.profile;
/*     */ 
/*     */ import com.intellij.openapi.diagnostic.Logger;
/*     */ import com.intellij.openapi.util.Pair;
/*     */ import com.intellij.openapi.util.io.FileUtil;
/*     */ import org.bipolar.run.profile.heap.IndexFiles;
/*     */ import org.bipolar.run.profile.heap.calculation.ByteArrayWrapper;
/*     */ import java.io.File;
/*     */ import java.io.IOException;
/*     */ import java.util.ArrayList;
/*     */ import java.util.Arrays;
/*     */ import java.util.HashMap;
/*     */ import java.util.List;
/*     */ import java.util.Map;
/*     */ import org.jetbrains.annotations.NotNull;
/*     */ 
/*     */ public abstract class V8IndexManager<T extends Enum> {
/*  18 */   private static final Logger LOG = Logger.getInstance(V8IndexManager.class);
/*     */   
/*     */   protected final File mySnapshotFile;
/*     */   
/*     */   private final T[] myCategoryValues;
/*     */   
/*     */   @NotNull
/*     */   private final Class<T> myCategoryClass;
/*     */   protected final ByteArrayWrapper myDigest;
/*     */   
/*     */   public V8IndexManager(@NotNull File snapshotFile, T[] categoryValues) throws IOException {
/*  29 */     this.mySnapshotFile = snapshotFile;
/*  30 */     this.myCategoryValues = categoryValues;
/*  31 */     this.myCategoryClass = (Class)categoryValues[0].getClass();
/*     */     
/*  33 */     byte[] folderName = createDigest(this.mySnapshotFile);
/*  34 */     this.myDigest = new ByteArrayWrapper(folderName);
/*  35 */     if (folderName == null) {
/*  36 */       workInTmp();
/*  37 */       this.myIndexFiles = null;
/*     */       return;
/*     */     } 
/*  40 */     this.myRootDir = V8IndexCatalogManager.getInstance().getOrCreate(folderName);
/*  41 */     if (this.myRootDir == null) {
/*  42 */       workInTmp();
/*  43 */       this.myIndexFiles = null;
/*     */       return;
/*     */     } 
/*  46 */     if (!this.myRootDir.exists()) {
/*  47 */       this.myRootDir.mkdirs();
/*  48 */       this.myIndexFiles = new CategoryIndexFiles<>(this.myRootDir);
/*     */       return;
/*     */     } 
/*  51 */     this.myIndexFiles = new CategoryIndexFiles<>(this.myRootDir);
/*  52 */     if (!tryInitFromDir())
/*  53 */       clearRoot(); 
/*     */   }
/*     */   private File myRootDir; private boolean myInitialized; protected boolean myDoNotSerialize; protected final CategoryIndexFiles<T> myIndexFiles;
/*     */   
/*     */   public void clearRoot() throws IOException {
/*  58 */     if (!FileUtil.delete(this.myRootDir)) {
/*  59 */       LOG.info("Can not clear index directory " + this.myRootDir.getPath() + " for heap indexes processing, going to use tmp directory...");
/*  60 */       workInTmp();
/*     */       return;
/*     */     } 
/*  63 */     this.myRootDir.mkdirs();
/*     */   }
/*     */   
/*     */   public ByteArrayWrapper getDigest() {
/*  67 */     return this.myDigest;
/*     */   }
/*     */ 
/*     */   
/*     */   private void workInTmp() throws IOException {
/*  72 */     this.myRootDir = FileUtil.createTempDirectory("v8", null);
/*  73 */     this.myDoNotSerialize = true;
/*     */   }
/*     */   
/*     */   public boolean isInitialized() {
/*  77 */     return this.myInitialized;
/*     */   }
/*     */   
/*     */   protected File categoryFile(T category) {
/*  81 */     return new File(this.myRootDir, category.name());
/*     */   }
/*     */   
/*     */   private boolean tryInitFromDir() throws IOException {
/*  85 */     T descriptionCategory = getDescriptionCategory();
/*  86 */     File description = categoryFile(descriptionCategory);
/*  87 */     if (!description.exists()) return false; 
/*  88 */     Map<T, List<Pair<String, byte[]>>> digests = (Map)V8IndexCatalogManager.readDigests(description, this.myCategoryClass);
/*  89 */     if (digests == null || !checkAllTypesArePresent(digests)) return false;
/*     */     
/*  91 */     Map<T, List<File>> map = new HashMap<>();
/*  92 */     for (Enum enum_ : digests.keySet()) {
/*  93 */       if (descriptionCategory.equals(enum_))
/*  94 */         continue;  List<Pair<String, byte[]>> shouldBeList = digests.get(enum_);
/*  95 */       if (shouldBeList == null) {
/*  96 */         return false;
/*     */       }
/*     */       
/*  99 */       for (Pair<String, byte[]> shouldBe : shouldBeList) {
/* 100 */         File file = new File(this.myRootDir, (String)shouldBe.getFirst());
/* 101 */         if (!file.exists()) {
/* 102 */           return false;
/*     */         }
/* 104 */         if (!Arrays.equals((byte[])shouldBe.getSecond(), V8IndexCatalogManager.digestFile(file, new byte[0][]))) {
/* 105 */           return false;
/*     */         }
/* 107 */         List<File> list = map.get(enum_);
/* 108 */         if (list == null) map.put((T)enum_, list = new ArrayList<>()); 
/* 109 */         list.add(file);
/*     */       } 
/*     */     } 
/* 112 */     if (map.size() != digests.size()) return false; 
/* 113 */     this.myIndexFiles.putAll(map);
/* 114 */     this.myInitialized = true;
/* 115 */     return true;
/*     */   }
/*     */   
/*     */   protected boolean checkAllTypesArePresent(Map<T, List<Pair<String, byte[]>>> digests) {
/* 119 */     return (digests.size() == this.myCategoryValues.length - 1);
/*     */   }
/*     */   
/*     */   @NotNull
/*     */   public IndexFiles<T> getIndexFiles() {
/* 124 */     if (this.myIndexFiles == null) $$$reportNull$$$0(2);  return this.myIndexFiles;
/*     */   }
/*     */   
/*     */   protected abstract byte[] createDigest(@NotNull File paramFile) throws IOException;
/*     */   
/*     */   protected abstract T getDescriptionCategory();
/*     */ }


/* Location:              C:\Program Files\JetBrains\WebStorm 2021.1\plugins\NodeJS\lib\NodeJS.jar!\com\jetbrains\nodejs\run\profile\V8IndexManager.class
 * Java compiler version: 11 (55.0)
 * JD-Core Version:       1.1.3
 */