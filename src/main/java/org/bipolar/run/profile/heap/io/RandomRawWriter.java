/*    */ package org.bipolar.run.profile.heap.io;
/*    */ 
/*    */ import java.io.ByteArrayOutputStream;
/*    */ import java.io.Closeable;
/*    */ import java.io.DataOutputStream;
/*    */ import java.io.File;
/*    */ import java.io.FileNotFoundException;
/*    */ import java.io.IOException;
/*    */ import java.io.RandomAccessFile;
/*    */ import org.jetbrains.annotations.NotNull;
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
/*    */ public class RandomRawWriter<T extends Positioned>
/*    */   implements Closeable
/*    */ {
/*    */   @NotNull
/*    */   private final RawSerializer<? super T> mySerializer;
/*    */   private final RandomAccessFile myRandomAccessFile;
/*    */   
/*    */   public RandomRawWriter(@NotNull File file, @NotNull RawSerializer<? super T> serializer) throws FileNotFoundException {
/* 30 */     this.mySerializer = serializer;
/* 31 */     this.myRandomAccessFile = new RandomAccessFile(file, "rw");
/*    */   }
/*    */   
/*    */   public void write(T t) throws IOException {
/* 35 */     this.myRandomAccessFile.seek(t.getOffset());
/* 36 */     ByteArrayOutputStream out = new ByteArrayOutputStream();
/* 37 */     DataOutputStream stream = new DataOutputStream(out);
/* 38 */     this.mySerializer.write(stream, t);
/* 39 */     this.myRandomAccessFile.write(out.toByteArray());
/*    */   }
/*    */ 
/*    */   
/*    */   public void close() throws IOException {
/* 44 */     this.myRandomAccessFile.close();
/*    */   }
/*    */ }


/* Location:              C:\Program Files\JetBrains\WebStorm 2021.1\plugins\NodeJS\lib\NodeJS.jar!\com\jetbrains\nodejs\run\profile\heap\io\RandomRawWriter.class
 * Java compiler version: 11 (55.0)
 * JD-Core Version:       1.1.3
 */