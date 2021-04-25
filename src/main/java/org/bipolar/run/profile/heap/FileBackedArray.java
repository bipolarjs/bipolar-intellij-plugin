/*     */ package org.bipolar.run.profile.heap;
/*     */ 
/*     */ import com.intellij.openapi.util.io.FileUtil;
/*     */ import com.intellij.util.ThrowableConsumer;
/*     */ import org.bipolar.run.profile.heap.io.RawSerializer;
/*     */ import org.bipolar.run.profile.heap.io.SequentialRawReader;
/*     */ import org.bipolar.run.profile.heap.io.SequentialRawWriter;
/*     */ import java.io.Closeable;
/*     */ import java.io.File;
/*     */ import java.io.IOException;
/*     */ import java.io.RandomAccessFile;
/*     */ import java.lang.reflect.Array;
/*     */
import org.jetbrains.annotations.NotNull;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class FileBackedArray<T>
/*     */   implements GenericArray<T>
/*     */ {
/*     */   private static final long ourSizeThreshold = 102400L;
/*     */   private final CloseableGenericArray<T> myFirstPartDelegate;
/*     */   private final CloseableGenericArray<T> myDelegate;
/*     */   private final long myFirstInSecondPart;
/*     */   
/*     */   public FileBackedArray(long size, @NotNull File file, @NotNull RawSerializer<T> serializer, @NotNull T nullObject, long threshold) throws IOException {
/*  44 */     long threshold = (SYNTHETIC_LOCAL_VARIABLE_6 < 0L) ? 102400L : SYNTHETIC_LOCAL_VARIABLE_6;
/*     */     
/*  46 */     if (size * serializer.getRecordSize() > threshold) {
/*  47 */       this.myDelegate = new MyFileBased<>(file, serializer, size - threshold, nullObject, threshold);
/*  48 */       this.myFirstPartDelegate = new MyArrayBased<>((T[])Array.newInstance(nullObject.getClass(), (int)threshold));
/*  49 */       this.myFirstInSecondPart = threshold;
/*     */     } else {
/*  51 */       this.myDelegate = new MyArrayBased<>((T[])Array.newInstance(nullObject.getClass(), (int)size));
/*  52 */       this.myFirstPartDelegate = this.myDelegate;
/*  53 */       this.myFirstInSecondPart = 0L;
/*     */     } 
/*     */   }
/*     */   
/*     */   public void closeAndReplay(@NotNull ThrowableConsumer<T, IOException> consumer) throws IOException {
/*  58 */     if (consumer == null) $$$reportNull$$$0(3);  this.myFirstPartDelegate.replay(consumer);
/*  59 */     if (this.myFirstInSecondPart > 0L) {
/*  60 */       this.myDelegate.replay(consumer);
/*     */     }
/*  62 */     close();
/*     */   }
/*     */   
/*     */   public void close() throws IOException {
/*  66 */     this.myFirstPartDelegate.close();
/*  67 */     this.myDelegate.close();
/*     */   }
/*     */ 
/*     */   
/*     */   public void set(long i, T t) throws IOException {
/*  72 */     if (i >= this.myFirstInSecondPart) {
/*  73 */       this.myDelegate.set(i, t);
/*     */     } else {
/*  75 */       this.myFirstPartDelegate.set(i, t);
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   public T get(long i) throws IOException {
/*  81 */     if (i >= this.myFirstInSecondPart) {
/*  82 */       return this.myDelegate.get(i);
/*     */     }
/*  84 */     return this.myFirstPartDelegate.get(i);
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public long size() {
/*  90 */     return this.myFirstInSecondPart + this.myDelegate.size();
/*     */   }
/*     */   
/*     */   private static final class MyFileBased<T> implements CloseableGenericArray<T> {
/*     */     private File myFile;
/*     */     @NotNull
/*     */     private final RawSerializer<T> mySerializer;
/*     */     private final long mySize;
/*     */     private final T myNullObject;
/*     */     private final long myOffset;
/*     */     private RandomAccessFile myRaf;
/*     */     
/*     */     private MyFileBased(@NotNull File file, @NotNull RawSerializer<T> serializer, long size, T nullObject, long offset) throws IOException {
/* 103 */       this.myFile = file;
/* 104 */       this.mySerializer = serializer;
/* 105 */       this.mySize = size;
/* 106 */       this.myNullObject = nullObject;
/* 107 */       this.myOffset = SYNTHETIC_LOCAL_VARIABLE_6;
/*     */       
/* 109 */       SequentialRawWriter<T> writer = new SequentialRawWriter(this.myFile, serializer);
/*     */       try {
/* 111 */         for (int j = 0; j < this.mySize; j++) {
/* 112 */           writer.write(nullObject);
/*     */         }
/*     */       } finally {
/* 115 */         writer.close();
/*     */       } 
/* 117 */       this.myRaf = new RandomAccessFile(file, "rw");
/*     */     }
/*     */ 
/*     */     
/*     */     public void set(long i, T t) throws IOException {
/* 122 */       this.myRaf.seek(this.mySerializer.getRecordSize() * (i - this.myOffset));
/* 123 */       this.mySerializer.write(this.myRaf, t);
/*     */     }
/*     */ 
/*     */     
/*     */     public T get(long i) throws IOException {
/* 128 */       long offset = (i - this.myOffset) * this.mySerializer.getRecordSize();
/* 129 */       if (this.myRaf.length() < offset + this.mySerializer.getRecordSize()) {
/* 130 */         return null;
/*     */       }
/* 132 */       this.myRaf.seek(offset);
/* 133 */       T read = (T)this.mySerializer.read(this.myRaf);
/* 134 */       return this.myNullObject.equals(read) ? null : read;
/*     */     }
/*     */ 
/*     */     
/*     */     public long size() {
/* 139 */       return this.mySize;
/*     */     }
/*     */ 
/*     */     
/*     */     public void close() throws IOException {
/* 144 */       if (this.myRaf != null) {
/* 145 */         this.myRaf.close();
/* 146 */         this.myRaf = null;
/*     */       } 
/* 148 */       if (this.myFile != null) {
/* 149 */         FileUtil.delete(this.myFile);
/* 150 */         this.myFile = null;
/*     */       } 
/*     */     }
/*     */ 
/*     */     
/*     */     public void replay(@NotNull ThrowableConsumer<T, IOException> consumer) throws IOException {
/* 156 */       if (consumer == null) $$$reportNull$$$0(2);  this.myRaf.close();
/* 157 */       this.myRaf = null;
/*     */       
/* 159 */       SequentialRawReader<T> reader = new SequentialRawReader(this.myFile, this.mySerializer, this.mySize);
/*     */       try {
/* 161 */         for (int i = 0; i < this.mySize; i++) {
/* 162 */           consumer.consume(reader.read());
/*     */         }
/*     */       } finally {
/* 165 */         reader.close();
/*     */       } 
/*     */     }
/*     */   }
/*     */   
/*     */   private static final class MyArrayBased<T> implements CloseableGenericArray<T> {
/*     */     private T[] myArr;
/*     */     
/*     */     private MyArrayBased(T[] arr) {
/* 174 */       this.myArr = arr;
/*     */     }
/*     */ 
/*     */     
/*     */     public void set(long i, T t) {
/* 179 */       this.myArr[(int)i] = t;
/*     */     }
/*     */ 
/*     */     
/*     */     public T get(long i) {
/* 184 */       return this.myArr[(int)i];
/*     */     }
/*     */ 
/*     */     
/*     */     public long size() {
/* 189 */       return this.myArr.length;
/*     */     }
/*     */ 
/*     */     
/*     */     public void close() throws IOException {
/* 194 */       this.myArr = null;
/*     */     }
/*     */ 
/*     */     
/*     */     public void replay(@NotNull ThrowableConsumer<T, IOException> consumer) throws IOException {
/* 199 */       if (consumer == null) $$$reportNull$$$0(0);  for (T t : this.myArr)
/* 200 */         consumer.consume(t); 
/*     */     }
/*     */   }
/*     */   
/*     */   private static interface CloseableGenericArray<T> extends GenericArray<T>, Closeable {
/*     */     void replay(@NotNull ThrowableConsumer<T, IOException> param1ThrowableConsumer) throws IOException;
/*     */   }
/*     */ }


/* Location:              C:\Program Files\JetBrains\WebStorm 2021.1\plugins\NodeJS\lib\NodeJS.jar!\com\jetbrains\nodejs\run\profile\heap\FileBackedArray.class
 * Java compiler version: 11 (55.0)
 * JD-Core Version:       1.1.3
 */