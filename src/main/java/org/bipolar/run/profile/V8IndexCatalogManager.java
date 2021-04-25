/*     */ package org.bipolar.run.profile;
/*     */ import com.intellij.openapi.application.ApplicationManager;
/*     */ import com.intellij.openapi.diagnostic.Logger;
/*     */ import com.intellij.openapi.util.Pair;
/*     */ import com.intellij.openapi.util.ZipperUpdater;
/*     */ import com.intellij.openapi.util.io.FileUtil;
/*     */ import org.bipolar.run.profile.heap.calculation.ByteArrayWrapper;
/*     */ import java.io.ByteArrayInputStream;
/*     */ import java.io.ByteArrayOutputStream;
/*     */ import java.io.File;
/*     */ import java.io.FileInputStream;
/*     */ import java.io.IOException;
/*     */ import java.io.ObjectInputStream;
/*     */ import java.io.ObjectOutputStream;
/*     */ import java.nio.charset.StandardCharsets;
/*     */ import java.security.MessageDigest;
/*     */ import java.util.ArrayList;
/*     */ import java.util.HashMap;
/*     */ import java.util.List;
/*     */ import java.util.Map;
/*     */ import java.util.Set;
/*     */ import org.jetbrains.annotations.NotNull;
/*     */ 
/*     */ public class V8IndexCatalogManager {
/*  25 */   private static final Logger LOG = Logger.getInstance(V8IndexCatalogManager.class);
/*     */   private static final int ourIndexMagic = 32456;
/*     */   private final Map<ByteArrayWrapper, File> myMap;
/*     */   private final Object myLock;
/*     */   private final ZipperUpdater myUpdater;
/*     */   private final File myIndex;
/*     */   private final File mySystemDir;
/*     */   private final Runnable myWriteIndexRunnable;
/*     */   
/*     */   public V8IndexCatalogManager() {
/*  35 */     this.myLock = new Object();
/*  36 */     this.myMap = new HashMap<>();
/*  37 */     this.myUpdater = new ZipperUpdater(300, Alarm.ThreadToUse.POOLED_THREAD, (Disposable)ApplicationManager.getApplication());
/*  38 */     this.mySystemDir = getSystemDir();
/*  39 */     this.myIndex = new File(this.mySystemDir, "index");
/*  40 */     this.myWriteIndexRunnable = (() -> writeIndex());
/*  41 */     readIndex();
/*     */   }
/*     */   
/*     */   public static V8IndexCatalogManager getInstance() {
/*  45 */     return (V8IndexCatalogManager)ApplicationManager.getApplication().getService(V8IndexCatalogManager.class);
/*     */   }
/*     */   
/*     */   public static <T extends Enum<T>> Map<T, List<Pair<String, byte[]>>> readDigests(@NotNull File file, Class<T> clazz) throws IOException {
/*  49 */     if (file == null) $$$reportNull$$$0(0);  if (!file.exists()) return null; 
/*  50 */     byte[] bytes = FileUtil.loadFileBytes(file);
/*  51 */     ByteArrayInputStream bis = new ByteArrayInputStream(bytes);
/*  52 */     ObjectInputStream is = new ObjectInputStream(bis);
/*  53 */     int num = is.readInt();
/*  54 */     Map<T, List<Pair<String, byte[]>>> map = new HashMap<>(num);
/*  55 */     for (int i = 0; i < num; i++) {
/*  56 */       T category; String categoryCode = is.readUTF();
/*     */       
/*     */       try {
/*  59 */         category = Enum.valueOf(clazz, categoryCode);
/*  60 */       } catch (IllegalArgumentException e) {
/*  61 */         return null;
/*     */       } 
/*  63 */       String fileName = is.readUTF();
/*  64 */       int size = is.readInt();
/*  65 */       byte[] digest = new byte[size];
/*  66 */       LOG.assertTrue((is.read(digest) == size));
/*  67 */       List<Pair<String, byte[]>> list = map.computeIfAbsent(category, k -> new ArrayList());
/*  68 */       list.add(Pair.create(fileName, digest));
/*     */     } 
/*  70 */     return map;
/*     */   }
/*     */   
/*     */   public static <T extends Enum<T>> void writeDigests(@NotNull Map<T, List<File>> filesMap, @NotNull File file) throws IOException {
/*  74 */     if (filesMap == null) $$$reportNull$$$0(1);  if (file == null) $$$reportNull$$$0(2);  ByteArrayOutputStream bytes = new ByteArrayOutputStream();
/*  75 */     ObjectOutputStream out = new ObjectOutputStream(bytes);
/*     */     
/*  77 */     Set<Map.Entry<T, List<File>>> entries = new HashSet<>(filesMap.entrySet());
/*  78 */     int cnt = 0;
/*  79 */     for (Map.Entry<T, List<File>> entry : entries) {
/*  80 */       List<File> list = entry.getValue();
/*  81 */       cnt += list.size();
/*     */     } 
/*  83 */     out.writeInt(cnt);
/*  84 */     for (Map.Entry<T, List<File>> entry : entries) {
/*  85 */       List<File> list = entry.getValue();
/*  86 */       for (File categoryFile : list) {
/*  87 */         byte[] digest = digestFile(categoryFile, new byte[0][]);
/*  88 */         if (digest != null) {
/*  89 */           out.writeUTF(((Enum)entry.getKey()).name());
/*  90 */           out.writeUTF(categoryFile.getName());
/*  91 */           out.writeInt(digest.length);
/*  92 */           out.write(digest);
/*     */         } 
/*     */       } 
/*     */     } 
/*     */     
/*  97 */     out.flush();
/*  98 */     FileUtil.writeToFile(file, bytes.toByteArray());
/*     */   }
/*     */   
/* 101 */   private static final byte[] version = new byte[] { 15 };
/*     */   public static byte[] digestFile(@NotNull File file, byte[]... additional) throws IOException {
/* 103 */     if (file == null) $$$reportNull$$$0(3);  MessageDigest md = DigestUtil.md5();
/* 104 */     md.update(version);
/* 105 */     md.update(file.getName().getBytes(StandardCharsets.UTF_8));
/* 106 */     md.update(String.valueOf(file.lastModified()).getBytes(StandardCharsets.UTF_8));
/* 107 */     md.update(String.valueOf(file.length()).getBytes(StandardCharsets.UTF_8));
/* 108 */     if (!checkBeginning(file, md)) return null; 
/* 109 */     for (byte[] bytes : additional) {
/* 110 */       md.update(bytes);
/*     */     }
/* 112 */     return md.digest();
/*     */   }
/*     */   
/*     */   private static boolean checkBeginning(@NotNull File snapshotFile, MessageDigest md) throws IOException {
/* 116 */     if (snapshotFile == null) $$$reportNull$$$0(4);  if (snapshotFile.length() > 10000L) {
/* 117 */       byte[] bytes = FileUtil.loadFirstAndClose(new FileInputStream(snapshotFile), 1000);
/* 118 */       md.update(bytes);
/*     */     } 
/* 120 */     return true;
/*     */   }
/*     */   
/*     */   @Nullable
/*     */   public File getOrCreate(byte[] digest) {
/* 125 */     synchronized (this.myLock) {
/* 126 */       ByteArrayWrapper wrapper = new ByteArrayWrapper(digest);
/* 127 */       File file = this.myMap.get(wrapper);
/* 128 */       if (file == null) {
/*     */         try {
/* 130 */           file = FileUtil.createTempDirectory(this.mySystemDir, "v8", null, false);
/* 131 */           this.myMap.put(wrapper, file);
/* 132 */           scheduleWriteIndex();
/*     */         }
/* 134 */         catch (IOException e) {
/* 135 */           LOG.info(e);
/* 136 */           return null;
/*     */         } 
/*     */       }
/* 139 */       return file;
/*     */     } 
/*     */   }
/*     */   
/*     */   private void readIndex() {
/* 144 */     synchronized (this.myLock) {
/* 145 */       this.myMap.clear();
/*     */       try {
/* 147 */         if (this.myIndex.exists()) {
/* 148 */           byte[] bytes = FileUtil.loadFileBytes(this.myIndex);
/* 149 */           ObjectInputStream ois = new ObjectInputStream(new ByteArrayInputStream(bytes));
/* 150 */           int control = ois.readInt();
/* 151 */           if (control != 32456) {
/* 152 */             clearIndexDirectory();
/*     */             
/*     */             return;
/*     */           } 
/* 156 */           boolean dirty = false;
/* 157 */           int size = ois.readInt();
/* 158 */           for (int i = 0; i < size; i++) {
/* 159 */             String folderName = ois.readUTF();
/* 160 */             int digestSize = ois.readInt();
/* 161 */             byte[] digest = new byte[digestSize];
/* 162 */             ois.read(digest);
/*     */             
/* 164 */             File folder = new File(this.mySystemDir, folderName);
/* 165 */             if (folder.exists()) {
/* 166 */               if (!folder.isDirectory()) {
/* 167 */                 clearIndexDirectory();
/*     */                 return;
/*     */               } 
/* 170 */               this.myMap.put(new ByteArrayWrapper(digest), folder);
/*     */             } else {
/*     */               
/* 173 */               dirty = true;
/*     */             } 
/*     */           } 
/* 176 */           if (dirty) scheduleWriteIndex();
/*     */         
/*     */         } 
/* 179 */       } catch (IOException e) {
/* 180 */         clearIndexDirectory();
/*     */       } 
/*     */     } 
/*     */   }
/*     */   
/*     */   private void scheduleWriteIndex() {
/* 186 */     this.myUpdater.queue(this.myWriteIndexRunnable);
/*     */   }
/*     */   
/*     */   private void clearIndexDirectory() {
/* 190 */     FileUtil.delete(this.mySystemDir);
/* 191 */     this.mySystemDir.mkdirs();
/* 192 */     this.myMap.clear();
/*     */   }
/*     */   
/*     */   private void writeIndex() {
/* 196 */     synchronized (this.myLock) {
/*     */       try {
/* 198 */         ByteArrayOutputStream bytes = new ByteArrayOutputStream();
/* 199 */         ObjectOutputStream stream = new ObjectOutputStream(bytes);
/* 200 */         stream.writeInt(32456);
/* 201 */         stream.writeInt(this.myMap.size());
/* 202 */         for (Map.Entry<ByteArrayWrapper, File> entry : this.myMap.entrySet()) {
/* 203 */           stream.writeUTF(((File)entry.getValue()).getName());
/* 204 */           ByteArrayWrapper wrapper = entry.getKey();
/* 205 */           stream.writeInt((wrapper.getData()).length);
/* 206 */           stream.write(wrapper.getData());
/*     */         } 
/* 208 */         stream.flush();
/* 209 */         FileUtil.writeToFile(this.myIndex, bytes.toByteArray());
/* 210 */       } catch (IOException e) {
/* 211 */         LOG.info(e);
/*     */       } 
/*     */     } 
/*     */   }
/*     */   
/*     */   @NotNull
/*     */   public static File getSystemDir() {
/* 218 */     return new File(PathManager.getSystemPath(), ApplicationManager.getApplication().isUnitTestMode() ? "v8test" : "v8");
/*     */   }
/*     */ }


/* Location:              C:\Program Files\JetBrains\WebStorm 2021.1\plugins\NodeJS\lib\NodeJS.jar!\com\jetbrains\nodejs\run\profile\V8IndexCatalogManager.class
 * Java compiler version: 11 (55.0)
 * JD-Core Version:       1.1.3
 */