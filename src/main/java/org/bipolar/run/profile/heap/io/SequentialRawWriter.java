/*    */ package org.bipolar.run.profile.heap.io;
/*    */ 
/*    */ import com.intellij.util.io.DataOutputStream;
/*    */ import org.bipolar.run.profile.heap.calculation.V8HeapProcessor;
/*    */ import java.io.BufferedOutputStream;
/*    */ import java.io.Closeable;
/*    */ import java.io.DataOutput;
/*    */ import java.io.File;
/*    */ import java.io.FileNotFoundException;
/*    */ import java.io.FileOutputStream;
/*    */ import java.io.IOException;
/*    */ import java.io.OutputStream;
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
/*    */ public class SequentialRawWriter<T>
/*    */   implements Closeable
/*    */ {
/*    */   private final RawSerializer<? super T> mySerializer;
/*    */   private OutputStream myStream;
/*    */   private final DataOutputStream myDataOutputStream;
/*    */   
/*    */   public SequentialRawWriter(File file, RawSerializer<? super T> serializer) throws FileNotFoundException {
/* 32 */     this(file, serializer, false);
/*    */   }
/*    */   
/*    */   public SequentialRawWriter(File file, RawSerializer<? super T> serializer, boolean append) throws FileNotFoundException {
/* 36 */     this.mySerializer = serializer;
/* 37 */     this.myStream = new BufferedOutputStream(new FileOutputStream(file));
/* 38 */     this.myDataOutputStream = new DataOutputStream(this.myStream);
/*    */   }
/*    */ 
/*    */   
/*    */   public void close() {
/* 43 */     if (this.myStream != null) {
/*    */       try {
/* 45 */         this.myStream.close();
/*    */       }
/* 47 */       catch (IOException e) {
/* 48 */         V8HeapProcessor.LOG.info(e);
/*    */       } 
/* 50 */       this.myStream = null;
/*    */     } 
/*    */   }
/*    */   
/*    */   public int write(T t) throws IOException {
/* 55 */     int was = this.myDataOutputStream.getWrittenBytesCount();
/* 56 */     this.mySerializer.write((DataOutput)this.myDataOutputStream, t);
/* 57 */     int is = this.myDataOutputStream.getWrittenBytesCount();
/* 58 */     return is - was;
/*    */   }
/*    */ }


/* Location:              C:\Program Files\JetBrains\WebStorm 2021.1\plugins\NodeJS\lib\NodeJS.jar!\com\jetbrains\nodejs\run\profile\heap\io\SequentialRawWriter.class
 * Java compiler version: 11 (55.0)
 * JD-Core Version:       1.1.3
 */