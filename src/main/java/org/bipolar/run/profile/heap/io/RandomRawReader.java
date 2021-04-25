/*    */ package org.bipolar.run.profile.heap.io;
/*    */ 
/*    */ import com.intellij.util.Processor;
/*    */ import java.io.ByteArrayInputStream;
/*    */ import java.io.Closeable;
/*    */ import java.io.DataInputStream;
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
/*    */ public class RandomRawReader<T>
/*    */   implements Closeable
/*    */ {
/*    */   @NotNull
/*    */   private final RawSerializer<? extends T> mySerializer;
/*    */   private final RandomAccessFile myRandomAccessFile;
/*    */   
/*    */   public RandomRawReader(@NotNull File file, @NotNull RawSerializer<? extends T> serializer) throws FileNotFoundException {
/* 31 */     this.mySerializer = serializer;
/* 32 */     this.myRandomAccessFile = new RandomAccessFile(file, "r");
/*    */   }
/*    */   
/*    */   public T read(long index) throws IOException {
/* 36 */     this.myRandomAccessFile.seek(index * this.mySerializer.getRecordSize());
/* 37 */     byte[] bytes = new byte[(int)this.mySerializer.getRecordSize()];
/* 38 */     this.myRandomAccessFile.read(bytes);
/* 39 */     return this.mySerializer.read(new DataInputStream(new ByteArrayInputStream(bytes)));
/*    */   }
/*    */   
/*    */   public void read(long index, int cnt, Processor<? super T> processor) throws IOException {
/* 43 */     this.myRandomAccessFile.seek(index * this.mySerializer.getRecordSize());
/* 44 */     byte[] bytes = new byte[(int)this.mySerializer.getRecordSize() * cnt];
/* 45 */     this.myRandomAccessFile.read(bytes);
/* 46 */     DataInputStream stream = new DataInputStream(new ByteArrayInputStream(bytes));
/* 47 */     for (int i = 0; i < cnt && 
/* 48 */       processor.process(this.mySerializer.read(stream)); i++);
/*    */   }
/*    */ 
/*    */   
/*    */   public T readRandomLen(long offset, int size) throws IOException {
/* 53 */     this.myRandomAccessFile.seek(offset);
/* 54 */     byte[] bytes = new byte[size];
/* 55 */     this.myRandomAccessFile.read(bytes);
/* 56 */     DataInputStream stream = new DataInputStream(new ByteArrayInputStream(bytes));
/* 57 */     return this.mySerializer.read(stream);
/*    */   }
/*    */ 
/*    */   
/*    */   public void close() throws IOException {
/* 62 */     this.myRandomAccessFile.close();
/*    */   }
/*    */ }


/* Location:              C:\Program Files\JetBrains\WebStorm 2021.1\plugins\NodeJS\lib\NodeJS.jar!\com\jetbrains\nodejs\run\profile\heap\io\RandomRawReader.class
 * Java compiler version: 11 (55.0)
 * JD-Core Version:       1.1.3
 */