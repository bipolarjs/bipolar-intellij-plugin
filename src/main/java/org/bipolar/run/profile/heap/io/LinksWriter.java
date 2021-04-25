/*    */ package org.bipolar.run.profile.heap.io;
/*    */ 
/*    */ import org.bipolar.run.profile.heap.io.reverse.LinksReaderFactory;
/*    */ import org.bipolar.run.profile.heap.io.reverse.SizeOffset;
/*    */ import java.io.Closeable;
/*    */ import java.io.File;
/*    */ import java.io.FileNotFoundException;
/*    */ import java.io.IOException;
/*    */ import java.util.Collection;
/*    */
import org.jetbrains.annotations.NotNull;
/*    */ 
/*    */ 
/*    */ 
/*    */ public class LinksWriter<T>
/*    */   implements Closeable
/*    */ {
/*    */   private final File myNumFile;
/*    */   private final File myLinksFile;
/*    */   private final RawSerializer<T> mySerializer;
/*    */   private final SequentialRawWriter<SizeOffset> myNumLinksWriter;
/*    */   private final SequentialRawWriter<T> myLinksWriter;
/*    */   private long myOffset;
/*    */   
/*    */   public LinksWriter(File numFile, File linksFile, RawSerializer<T> serializer) throws FileNotFoundException {
/* 25 */     this.myNumFile = numFile;
/* 26 */     this.myLinksFile = linksFile;
/* 27 */     this.mySerializer = serializer;
/* 28 */     this.myNumLinksWriter = new SequentialRawWriter<>(this.myNumFile, (RawSerializer<? super SizeOffset>)SizeOffset.MySerializer.getInstance());
/* 29 */     this.myLinksWriter = new SequentialRawWriter<>(this.myLinksFile, serializer);
/* 30 */     this.myOffset = 0L;
/*    */   }
/*    */   
/*    */   public void write(@NotNull Collection<T> coll) throws IOException {
/* 34 */     if (coll == null) $$$reportNull$$$0(0);  for (T t : coll) {
/* 35 */       this.myLinksWriter.write(t);
/*    */     }
/* 37 */     this.myNumLinksWriter.write(new SizeOffset(coll.size(), this.myOffset));
/* 38 */     this.myOffset += coll.size();
/*    */   }
/*    */   
/*    */   public void writeVariableValue(@NotNull T t) throws IOException {
/* 42 */     if (t == null) $$$reportNull$$$0(1);  int size = this.myLinksWriter.write(t);
/* 43 */     this.myNumLinksWriter.write(new SizeOffset(size, this.myOffset));
/* 44 */     this.myOffset += size;
/*    */   }
/*    */ 
/*    */   
/*    */   public void close() throws IOException {
/* 49 */     this.myLinksWriter.close();
/* 50 */     this.myNumLinksWriter.close();
/*    */   }
/*    */   
/*    */   public LinksReaderFactory<T> getFactory() {
/* 54 */     return new LinksReaderFactory(this.mySerializer, this.myNumFile, this.myLinksFile);
/*    */   }
/*    */ }


/* Location:              C:\Program Files\JetBrains\WebStorm 2021.1\plugins\NodeJS\lib\NodeJS.jar!\com\jetbrains\nodejs\run\profile\heap\io\LinksWriter.class
 * Java compiler version: 11 (55.0)
 * JD-Core Version:       1.1.3
 */