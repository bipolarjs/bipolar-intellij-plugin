/*    */ package org.bipolar.run.profile.heap.calculation;
/*    */ 
/*    */ import com.intellij.util.containers.Convertor;
/*    */ import org.bipolar.run.profile.heap.IndexFiles;
/*    */ import org.bipolar.run.profile.heap.data.V8HeapEdge;
/*    */ import org.bipolar.run.profile.heap.io.RawSerializer;
/*    */ import org.bipolar.run.profile.heap.io.SequentialRawReader;
/*    */ import org.bipolar.run.profile.heap.io.reverse.LinksReaderFactory;
/*    */ import org.bipolar.util.CloseableThrowableConsumer;
/*    */ import it.unimi.dsi.fastutil.ints.IntArrayList;
/*    */ import it.unimi.dsi.fastutil.ints.IntList;
/*    */ import java.io.File;
/*    */ import java.io.FileNotFoundException;
/*    */ import java.io.IOException;
/*    */
import org.jetbrains.annotations.NotNull;
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
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public final class ReverseLinkIndexProcessor
/*    */   implements EdgeProcessor
/*    */ {
/*    */   private static final int BOUND = 500000;
/*    */   private final ProcessInPortions<V8HeapEdge, V8HeapEdge> myDelegate;
/* 41 */   private IntList myReverseNum = (IntList)new IntArrayList(); public ReverseLinkIndexProcessor(long nodesCnt, @NotNull IndexFiles indexFiles) throws IOException {
/* 42 */     for (int i = 0; i < nodesCnt; i++) {
/* 43 */       this.myReverseNum.add(0);
/*    */     }
/* 45 */     this
/*    */ 
/*    */ 
/*    */       
/* 49 */       .myDelegate = new ProcessInPortions<>(nodesCnt, -1L, (RawSerializer<V8HeapEdge>)V8HeapEdge.MyRawSerializer.getInstance(), (RawSerializer<V8HeapEdge>)V8HeapEdge.MyRawSerializer.getInstance(), o -> Long.valueOf(o.getToIndex()), Convertor.self(), SYNTHETIC_LOCAL_VARIABLE_3.generate(V8HeapIndexManager.Category.reverseNumLinks, ".num.index"), SYNTHETIC_LOCAL_VARIABLE_3.generate(V8HeapIndexManager.Category.reverseLinks, ".index"));
/*    */   }
/*    */ 
/*    */   
/*    */   public void continueCalculation(@NotNull File edgeIndexFile, long numEdges, long nodesCnt) throws IOException {
/* 54 */     if (edgeIndexFile == null) $$$reportNull$$$0(1);  int from = 0;
/* 55 */     while (from < SYNTHETIC_LOCAL_VARIABLE_4) {
/* 56 */       int cnt = 0;
/* 57 */       int i = from;
/* 58 */       for (; i < SYNTHETIC_LOCAL_VARIABLE_4 && cnt < 500000; i++) {
/* 59 */         cnt += this.myReverseNum.getInt(i);
/*    */       }
/* 61 */       int to = i - 1;
/* 62 */       to = (to - from < 0) ? from : to;
/* 63 */       (new SequentialRawReader(edgeIndexFile, (RawSerializer)V8HeapEdge.MyRawSerializer.getInstance(), numEdges)).iterate(this.myDelegate
/* 64 */           .getStageCalculator(from, to));
/* 65 */       from = to + 1;
/*    */     } 
/* 67 */     this.myReverseNum = null;
/*    */   }
/*    */ 
/*    */   
/*    */   public CloseableThrowableConsumer<V8HeapEdge, IOException> getFirstStageCalculator() {
/* 72 */     return new CloseableThrowableConsumer<V8HeapEdge, IOException>()
/*    */       {
/*    */         public void close() {}
/*    */ 
/*    */ 
/*    */         
/*    */         public void consume(V8HeapEdge edge) {
/* 79 */           ReverseLinkIndexProcessor.this.myReverseNum.set((int)edge.getToIndex(), ReverseLinkIndexProcessor.this.myReverseNum.getInt((int)edge.getToIndex()) + 1);
/*    */         }
/*    */       };
/*    */   }
/*    */   
/*    */   public File getReverseLinksFile() {
/* 85 */     return this.myDelegate.getLinksFile();
/*    */   }
/*    */   
/*    */   public File getNumReverseLinksFile() {
/* 89 */     return this.myDelegate.getNumLinksFile();
/*    */   }
/*    */   
/*    */   public LinksReaderFactory<V8HeapEdge> getLinksReaderFactory() throws FileNotFoundException {
/* 93 */     return this.myDelegate.getLinksReaderFactory();
/*    */   }
/*    */ 
/*    */   
/*    */   public void close() throws IOException {
/* 98 */     this.myDelegate.close();
/*    */   }
/*    */ }


/* Location:              C:\Program Files\JetBrains\WebStorm 2021.1\plugins\NodeJS\lib\NodeJS.jar!\com\jetbrains\nodejs\run\profile\heap\calculation\ReverseLinkIndexProcessor.class
 * Java compiler version: 11 (55.0)
 * JD-Core Version:       1.1.3
 */