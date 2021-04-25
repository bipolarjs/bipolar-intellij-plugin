/*     */ package org.bipolar.run.profile.heap.calculation;
/*     */ 
/*     */ import com.intellij.openapi.application.ApplicationManager;
/*     */ import com.intellij.openapi.util.NlsSafe;
/*     */ import com.intellij.openapi.util.Pair;
/*     */ import com.intellij.openapi.util.io.FileUtil;
/*     */ import org.bipolar.run.profile.heap.IndexFiles;
/*     */ import org.bipolar.run.profile.heap.io.RawSerializer;
/*     */ import org.bipolar.util.CloseableProcessor;
/*     */ import java.io.BufferedInputStream;
/*     */ import java.io.BufferedOutputStream;
/*     */ import java.io.Closeable;
/*     */ import java.io.DataInput;
/*     */ import java.io.DataInputStream;
/*     */ import java.io.DataOutputStream;
/*     */ import java.io.File;
/*     */ import java.io.FileInputStream;
/*     */ import java.io.FileNotFoundException;
/*     */ import java.io.FileOutputStream;
/*     */ import java.io.IOException;
/*     */ import java.io.RandomAccessFile;
/*     */ import java.nio.charset.StandardCharsets;
/*     */ import java.util.ArrayList;
/*     */ import java.util.HashMap;
/*     */ import java.util.List;
/*     */ import java.util.Map;
/*     */ import java.util.concurrent.atomic.AtomicBoolean;
/*     */ import java.util.concurrent.atomic.AtomicInteger;
/*     */
import org.jetbrains.annotations.Nls;
/*     */ import org.jetbrains.annotations.NotNull;
/*     */ import org.jetbrains.annotations.Nullable;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class V8StringIndex
/*     */   implements Closeable
/*     */ {
/*     */   @Nullable
/*     */   private final IndexFiles myIndexFiles;
/*     */   private List<OneFileWriter> myWriters;
/*     */   private List<OneFileReader> myReaders;
/*     */   private long myCnt;
/*     */   
/*     */   public V8StringIndex(@NotNull IndexFiles indexFiles) throws IOException {
/*  48 */     this.myIndexFiles = indexFiles;
/*  49 */     this.myWriters = new ArrayList<>();
/*  50 */     this.myWriters.add(new OneFileWriter(indexFiles.generate(V8HeapIndexManager.Category.strings, "v8strings")));
/*  51 */     this.myCnt = 0L;
/*     */   }
/*     */   
/*     */   public V8StringIndex(File... files) throws IOException {
/*  55 */     this.myReaders = new ArrayList<>(files.length);
/*  56 */     for (File file : files) {
/*  57 */       this.myReaders.add(new OneFileReader(file));
/*     */     }
/*  59 */     this.myIndexFiles = null;
/*     */   }
/*     */   
/*     */   public long addString(@NotNull String s) throws IOException {
/*  63 */     if (s == null) $$$reportNull$$$0(1);  this.myCnt++;
/*  64 */     if (!((OneFileWriter)this.myWriters.get(this.myWriters.size() - 1)).addString(s)) {
/*  65 */       this.myWriters.add(new OneFileWriter(this.myIndexFiles.generate(V8HeapIndexManager.Category.strings, "v8strings")));
/*     */     }
/*  67 */     return this.myCnt - 1L;
/*     */   }
/*     */   
/*     */   public long getCnt() {
/*  71 */     return this.myCnt;
/*     */   }
/*     */   
/*     */   public void startReading() throws IOException {
/*  75 */     this.myReaders = new ArrayList<>(this.myWriters.size());
/*  76 */     for (OneFileWriter writer : this.myWriters) {
/*  77 */       writer.close();
/*  78 */       this.myReaders.add(writer.createReader());
/*     */     } 
/*  80 */     this.myWriters = null;
/*     */   }
/*     */   @Nls
/*     */   public String readString(long i) throws IOException {
/*  84 */     int idx = (int)(i / 10000L);
/*  85 */     return ((OneFileReader)this.myReaders.get(idx)).readString((int)(i - (idx * 10000)));
/*     */   }
/*     */   
/*     */   public void deleteFiles() throws IOException {
/*  89 */     if (this.myWriters != null) {
/*  90 */       for (OneFileWriter writer : this.myWriters) {
/*  91 */         writer.close();
/*     */       }
/*     */     }
/*     */     
/*  95 */     if (this.myReaders != null) {
/*  96 */       for (OneFileReader reader : this.myReaders) {
/*  97 */         reader.close();
/*     */       }
/*     */     }
/*     */   }
/*     */ 
/*     */   
/*     */   public void iterate(@NotNull final CloseableProcessor<Pair<Long, String>, IOException> processor) throws IOException {
/* 104 */     if (processor == null) $$$reportNull$$$0(2);  for (int i = 0; i < this.myReaders.size(); i++) {
/* 105 */       OneFileReader reader = this.myReaders.get(i);
/* 106 */       int offset = 10000 * i;
/* 107 */       reader.iterate(offset, new CloseableProcessor<Pair<Long, String>, IOException>()
/*     */           {
/*     */             public void exceptionThrown(@NotNull IOException e) {
/* 110 */               if (e == null) $$$reportNull$$$0(0);  processor.exceptionThrown(e);
/*     */             }
/*     */ 
/*     */ 
/*     */             
/*     */             public void close() throws IOException {}
/*     */ 
/*     */             
/*     */             public boolean process(Pair<Long, String> pair) {
/* 119 */               processor.process(pair);
/* 120 */               return true;
/*     */             }
/*     */           });
/*     */     } 
/* 124 */     processor.close();
/*     */   }
/*     */   
/*     */   public void parallelIterate(@NotNull final CloseableProcessor<Pair<Long, String>, IOException> processor) {
/* 128 */     if (processor == null) $$$reportNull$$$0(3);  final AtomicInteger cnt = new AtomicInteger(this.myReaders.size());
/* 129 */     final AtomicBoolean continueIteration = new AtomicBoolean(true);
/*     */     
/* 131 */     for (int i = 0; i < this.myReaders.size(); i++) {
/* 132 */       OneFileReader reader = this.myReaders.get(i);
/* 133 */       int offset = 10000 * i;
/* 134 */       ApplicationManager.getApplication().executeOnPooledThread(() -> {
/*     */             try {
/*     */               reader.iterate(offset, new CloseableProcessor<Pair<Long, String>, IOException>()
/*     */                   {
/*     */                     public void exceptionThrown(@NotNull IOException e) {
/* 139 */                       if (e == null) $$$reportNull$$$0(0);  processor.exceptionThrown(e);
/*     */                     }
/*     */ 
/*     */                     
/*     */                     public void close() throws IOException {
/* 144 */                       if (cnt.decrementAndGet() <= 0) {
/* 145 */                         processor.close();
/*     */                       }
/*     */                     }
/*     */ 
/*     */                     
/*     */                     public boolean process(Pair pair) {
/* 151 */                       if (!continueIteration.get()) return false; 
/* 152 */                       if (!processor.process(pair)) return false; 
/* 153 */                       return true;
/*     */                     }
/*     */                   });
/*     */             }
/* 157 */             catch (IOException e) {
/*     */               continueIteration.set(false);
/*     */               
/*     */               processor.exceptionThrown(e);
/*     */               try {
/*     */                 processor.close();
/* 163 */               } catch (IOException iOException) {}
/*     */             } 
/*     */           });
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void close() throws IOException {
/* 173 */     if (this.myWriters != null) {
/* 174 */       for (OneFileWriter writer : this.myWriters) {
/* 175 */         writer.close();
/*     */       }
/*     */     }
/* 178 */     if (this.myReaders != null)
/* 179 */       for (OneFileReader reader : this.myReaders)
/* 180 */         reader.close();  
/*     */   }
/*     */   
/*     */   public static class OneFileWriter
/*     */     implements Closeable
/*     */   {
/*     */     public static final int SIZE = 10000;
/*     */     private final File myFile;
/*     */     private final Map<Integer, Long> myPositionsMap;
/*     */     private int myCnt;
/*     */     private long myPosition;
/*     */     private final BufferedOutputStream myStream;
/*     */     private int myMaxSize;
/*     */     
/*     */     public OneFileWriter(@NotNull File file) throws IOException {
/* 195 */       this.myFile = file;
/* 196 */       this.myPositionsMap = new HashMap<>();
/* 197 */       this.myStream = new BufferedOutputStream(new FileOutputStream(this.myFile));
/* 198 */       this.myPosition = 0L;
/* 199 */       this.myMaxSize = 0;
/*     */     }
/*     */     
/*     */     public boolean addString(@NotNull String s) throws IOException {
/* 203 */       if (s == null) $$$reportNull$$$0(1);  byte[] bytes = s.getBytes(StandardCharsets.UTF_8);
/* 204 */       this.myStream.write(bytes);
/* 205 */       this.myMaxSize = Math.max(this.myMaxSize, bytes.length);
/* 206 */       this.myPositionsMap.put(Integer.valueOf(this.myCnt++), Long.valueOf(this.myPosition));
/* 207 */       this.myPosition += bytes.length;
/* 208 */       return (this.myCnt < 10000);
/*     */     }
/*     */ 
/*     */     
/*     */     public void close() throws IOException {
/* 213 */       this.myStream.close();
/*     */ 
/*     */       
/* 216 */       this.myPositionsMap.put(Integer.valueOf(this.myCnt), Long.valueOf(this.myPosition));
/*     */       
/* 218 */       File connected = FileUtil.createTempFile("v8strings", null);
/* 219 */       FileUtil.copy(this.myFile, connected);
/* 220 */       FileUtil.delete(this.myFile);
/* 221 */       BufferedInputStream stream = new BufferedInputStream(new FileInputStream(connected));
/* 222 */       BufferedOutputStream out = new BufferedOutputStream(new FileOutputStream(this.myFile));
/* 223 */       DataOutputStream dos = new DataOutputStream(out);
/* 224 */       RawSerializer.Helper.serializeInt(this.myPositionsMap.size(), dos);
/* 225 */       RawSerializer.Helper.serializeInt(this.myMaxSize, dos);
/*     */       try {
/* 227 */         for (int i = 0; i < this.myPositionsMap.size(); i++) {
/* 228 */           RawSerializer.Helper.serializeLong(((Long)this.myPositionsMap.get(Integer.valueOf(i))).longValue(), dos);
/*     */         }
/* 230 */         FileUtil.copy(stream, out);
/*     */       } finally {
/* 232 */         stream.close();
/* 233 */         out.close();
/* 234 */         FileUtil.delete(connected);
/*     */       } 
/*     */     }
/*     */     
/*     */     public V8StringIndex.OneFileReader createReader() throws FileNotFoundException {
/* 239 */       return new V8StringIndex.OneFileReader(this.myFile, this.myPositionsMap, this.myMaxSize);
/*     */     }
/*     */   }
/*     */   
/*     */   public static class OneFileReader implements Closeable {
/*     */     private final File myFile;
/*     */     private final Map<Integer, Long> myPositionsMap;
/*     */     private final int myMaxSize;
/*     */     private final RandomAccessFile myReader;
/*     */     private final int myStartOffset;
/*     */     
/*     */     public OneFileReader(File file, Map<Integer, Long> positionsMap, int maxSize) throws FileNotFoundException {
/* 251 */       this.myFile = file;
/* 252 */       this.myPositionsMap = positionsMap;
/* 253 */       this.myMaxSize = maxSize;
/* 254 */       this.myReader = new RandomAccessFile(file, "r");
/* 255 */       this.myStartOffset = 8 + this.myPositionsMap.size() * 8;
/*     */     }
/*     */     
/*     */     public OneFileReader(@NotNull File file) throws IOException {
/* 259 */       this.myFile = file;
/* 260 */       FileInputStream fis = new FileInputStream(file);
/*     */       try {
/* 262 */         BufferedInputStream stream = new BufferedInputStream(fis);
/* 263 */         DataInput din = new DataInputStream(stream);
/* 264 */         int mapSize = RawSerializer.Helper.deserializeInt(din);
/* 265 */         this.myPositionsMap = new HashMap<>(mapSize);
/* 266 */         this.myMaxSize = RawSerializer.Helper.deserializeInt(din);
/* 267 */         for (int i = 0; i < mapSize; i++) {
/* 268 */           this.myPositionsMap.put(Integer.valueOf(i), Long.valueOf(RawSerializer.Helper.deserializeLong(din)));
/*     */         }
/*     */       } finally {
/*     */         try {
/* 272 */           fis.close();
/*     */         }
/* 274 */         catch (IOException iOException) {}
/*     */       } 
/*     */ 
/*     */       
/* 278 */       this.myReader = new RandomAccessFile(file, "r");
/* 279 */       this.myStartOffset = 8 + this.myPositionsMap.size() * 8;
/*     */     }
/*     */     @NlsSafe
/*     */     public String readString(int i) throws IOException {
/* 283 */       Long curPos = this.myPositionsMap.get(Integer.valueOf(i));
/* 284 */       long nextPos = ((Long)this.myPositionsMap.get(Integer.valueOf(i + 1))).longValue();
/* 285 */       byte[] buf = new byte[(int)(nextPos - curPos.longValue())];
/*     */       
/* 287 */       this.myReader.seek(this.myStartOffset + curPos.longValue());
/* 288 */       this.myReader.read(buf);
/*     */       
/* 290 */       return new String(buf, StandardCharsets.UTF_8);
/*     */     }
/*     */     
/*     */     public void iterate(long offset, @NotNull CloseableProcessor<Pair<Long, String>, IOException> processor) throws IOException {
/* 294 */       if (SYNTHETIC_LOCAL_VARIABLE_3 == null) $$$reportNull$$$0(1);  BufferedInputStream stream = new BufferedInputStream(new FileInputStream(this.myFile));
/*     */       try {
/* 296 */         stream.skip(this.myStartOffset);
/*     */         
/* 298 */         byte[] buf = new byte[this.myMaxSize];
/* 299 */         int i = 0;
/* 300 */         Long curPos = this.myPositionsMap.get(Integer.valueOf(i));
/* 301 */         long nextPos = ((Long)this.myPositionsMap.get(Integer.valueOf(i + 1))).longValue();
/* 302 */         int numBytes = stream.read(buf, 0, (int)(nextPos - curPos.longValue()));
/* 303 */         for (; i < this.myPositionsMap.size() - 1 && SYNTHETIC_LOCAL_VARIABLE_3.process(Pair.create(Long.valueOf(offset + i), new String(buf, 0, numBytes, StandardCharsets.UTF_8))); i++);
/*     */       } finally {
/*     */         
/* 306 */         SYNTHETIC_LOCAL_VARIABLE_3.close();
/* 307 */         stream.close();
/*     */       } 
/*     */     }
/*     */ 
/*     */     
/*     */     public void close() throws IOException {
/* 313 */       this.myReader.close();
/*     */     }
/*     */   }
/*     */ }


/* Location:              C:\Program Files\JetBrains\WebStorm 2021.1\plugins\NodeJS\lib\NodeJS.jar!\com\jetbrains\nodejs\run\profile\heap\calculation\V8StringIndex.class
 * Java compiler version: 11 (55.0)
 * JD-Core Version:       1.1.3
 */