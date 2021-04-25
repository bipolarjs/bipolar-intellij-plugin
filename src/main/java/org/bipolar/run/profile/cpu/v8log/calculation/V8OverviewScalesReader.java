/*    */ package org.bipolar.run.profile.cpu.v8log.calculation;
/*    */ 
/*    */ import com.intellij.openapi.util.Pair;
/*    */ import org.bipolar.run.profile.heap.CompositeCloseable;
/*    */ import org.bipolar.run.profile.heap.io.SequentialRawReader;
/*    */ import org.bipolar.util.CloseableThrowableConsumer;
/*    */ import java.io.Closeable;
/*    */ import java.io.File;
/*    */ import java.io.FileNotFoundException;
/*    */ import java.io.IOException;
/*    */ import java.util.ArrayList;
/*    */ import java.util.List;
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public class V8OverviewScalesReader
/*    */ {
/* 22 */   private final List<Long> myStepSizes = new ArrayList<>();
/* 23 */   private final List<SequentialRawReader<Pair<Long, Integer>>> myReaders = new ArrayList<>(); public V8OverviewScalesReader(List<File> overviewFiles, CompositeCloseable resources, long tickStep) throws FileNotFoundException {
/* 24 */     long step = tickStep;
/* 25 */     if (overviewFiles != null) {
/* 26 */       LongIntegerSerializer serializer = new LongIntegerSerializer();
/* 27 */       for (File file : overviewFiles) {
/* 28 */         this.myStepSizes.add(Long.valueOf(step));
/* 29 */         this.myReaders.add((SequentialRawReader<Pair<Long, Integer>>)resources.register((Closeable)new SequentialRawReader(file, serializer)));
/* 30 */         step *= 4L;
/*    */       } 
/*    */     } 
/*    */   }
/*    */   
/*    */   public List<Pair<Long, Integer>> getMostDetailedOverview(long from, long to) throws IOException {
/* 36 */     return this.myStepSizes.isEmpty() ? null : calculateWith(0, from, to);
/*    */   }
/*    */   
/*    */   public List<Pair<Long, Integer>> getStackOverview(long from, long to) throws IOException {
/* 40 */     long interval = to - from;
/* 41 */     for (int i = this.myStepSizes.size() - 1; i >= 0; i--) {
/* 42 */       if (interval / ((Long)this.myStepSizes.get(i)).longValue() >= 500L) {
/* 43 */         if (i == this.myStepSizes.size() - 1) return calculateWith(i, from, to); 
/* 44 */         return calculateWith(i + 1, from, to);
/*    */       } 
/*    */     } 
/* 47 */     return null;
/*    */   }
/*    */ 
/*    */   
/*    */   public List<Pair<Long, Integer>> calculateWith(int i, long from, long to) throws IOException {
/* 52 */     long stepSize = ((Long)this.myStepSizes.get(i)).longValue();
/* 53 */     final int fromIdx = (int)Math.floor((from / stepSize));
/* 54 */     final int toIdx = (int)Math.ceil(to / stepSize);
/*    */     
/* 56 */     final List<Pair<Long, Integer>> result = new ArrayList<>();
/* 57 */     SequentialRawReader<Pair<Long, Integer>> reader = this.myReaders.get(i);
/* 58 */     reader.reset();
/* 59 */     reader.skip(fromIdx);
/* 60 */     reader.iterate(new CloseableThrowableConsumer<Pair<Long, Integer>, IOException>() {
/* 61 */           int cnt = fromIdx;
/*    */ 
/*    */ 
/*    */           
/*    */           public void close() throws IOException {}
/*    */ 
/*    */           
/*    */           public void consume(Pair<Long, Integer> pair) throws IOException {
/* 69 */             if (this.cnt <= toIdx) {
/* 70 */               result.add(pair);
/*    */             }
/* 72 */             this.cnt++;
/*    */           }
/*    */         });
/* 75 */     return result;
/*    */   }
/*    */ }


/* Location:              C:\Program Files\JetBrains\WebStorm 2021.1\plugins\NodeJS\lib\NodeJS.jar!\com\jetbrains\nodejs\run\profile\cpu\v8log\calculation\V8OverviewScalesReader.class
 * Java compiler version: 11 (55.0)
 * JD-Core Version:       1.1.3
 */