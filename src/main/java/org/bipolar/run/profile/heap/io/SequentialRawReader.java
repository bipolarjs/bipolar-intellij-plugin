/*     */ package org.bipolar.run.profile.heap.io;
/*     */ 
/*     */ import org.bipolar.run.profile.heap.calculation.V8HeapProcessor;
/*     */ import org.bipolar.util.CloseableThrowableConsumer;
/*     */ import org.bipolar.util.CloseableThrowableProcessor;
/*     */ import java.io.BufferedInputStream;
/*     */ import java.io.Closeable;
/*     */ import java.io.DataInputStream;
/*     */ import java.io.File;
/*     */ import java.io.FileInputStream;
/*     */ import java.io.FileNotFoundException;
/*     */ import java.io.IOException;
/*     */ import java.io.InputStream;
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
/*     */ public class SequentialRawReader<T>
/*     */   implements Closeable
/*     */ {
/*     */   private final File myFile;
/*     */   private final RawSerializer<? extends T> mySerializer;
/*     */   private InputStream myStream;
/*     */   private DataInputStream myDataInputStream;
/*     */   private final long mySize;
/*     */   private long myCnt;
/*     */   
/*     */   public SequentialRawReader(File file, RawSerializer<? extends T> serializer, long size) throws FileNotFoundException {
/*  37 */     this.myFile = file;
/*  38 */     this.mySerializer = serializer;
/*  39 */     this.myStream = new BufferedInputStream(new FileInputStream(file));
/*  40 */     this.myDataInputStream = new DataInputStream(this.myStream);
/*  41 */     this.mySize = size;
/*  42 */     this.myCnt = 0L;
/*     */   }
/*     */   
/*     */   public SequentialRawReader(File file, RawSerializer<? extends T> serializer) throws FileNotFoundException {
/*  46 */     this.myFile = file;
/*  47 */     this.mySerializer = serializer;
/*  48 */     this.myStream = new BufferedInputStream(new FileInputStream(file));
/*  49 */     this.myDataInputStream = new DataInputStream(this.myStream);
/*  50 */     this.mySize = -1L;
/*  51 */     this.myCnt = 0L;
/*     */   }
/*     */ 
/*     */   
/*     */   public void close() {
/*  56 */     if (this.myStream != null) {
/*     */       try {
/*  58 */         this.myStream.close();
/*     */       }
/*  60 */       catch (IOException e) {
/*  61 */         V8HeapProcessor.LOG.info(e);
/*     */       } 
/*  63 */       this.myStream = null;
/*     */     } 
/*     */   }
/*     */   
/*     */   public boolean hasNext() throws IOException {
/*  68 */     return (this.mySize >= 0L) ? ((this.myCnt < this.mySize)) : ((this.myStream.available() > 0));
/*     */   }
/*     */   
/*     */   public T read() throws IOException {
/*  72 */     this.myCnt++;
/*  73 */     return this.mySerializer.read(this.myDataInputStream);
/*     */   }
/*     */   
/*     */   public void skipBytes(int offset, int elements) throws IOException {
/*  77 */     this.myDataInputStream.skipBytes(offset);
/*  78 */     this.myCnt = elements;
/*     */   }
/*     */   
/*     */   public void skip(long numElements) throws IOException {
/*  82 */     this.myDataInputStream.skipBytes((int)(this.mySerializer.getRecordSize() * numElements));
/*  83 */     this.myCnt = numElements;
/*     */   }
/*     */   
/*     */   public void reset() throws IOException {
/*  87 */     this.myDataInputStream.close();
/*  88 */     this.myStream = new BufferedInputStream(new FileInputStream(this.myFile));
/*  89 */     this.myDataInputStream = new DataInputStream(this.myStream);
/*  90 */     this.myCnt = 0L;
/*     */   }
/*     */ 
/*     */   
/*     */   public void iterate(@NotNull CloseableThrowableProcessor<T, IOException> processor) throws IOException {
/*  95 */     if (processor == null) $$$reportNull$$$0(0);  try { do {  } while (hasNext() && 
/*  96 */         processor.process(read())); }
/*     */     finally
/*     */     
/*  99 */     { processor.close();
/* 100 */       close(); }
/*     */   
/*     */   }
/*     */ 
/*     */   
/*     */   public void iterate(@NotNull CloseableThrowableConsumer<T, IOException> consumer) throws IOException {
/* 106 */     if (consumer == null) { $$$reportNull$$$0(1); } else { try { while (hasNext()) {
/* 107 */           consumer.consume(read());
/*     */         } }
/*     */       finally
/* 110 */       { consumer.close();
/* 111 */         close(); }  return; }  while (hasNext()) consumer.consume(read());  consumer.close(); close();
/*     */   }
/*     */ }


/* Location:              C:\Program Files\JetBrains\WebStorm 2021.1\plugins\NodeJS\lib\NodeJS.jar!\com\jetbrains\nodejs\run\profile\heap\io\SequentialRawReader.class
 * Java compiler version: 11 (55.0)
 * JD-Core Version:       1.1.3
 */