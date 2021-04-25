/*    */ package org.bipolar.run.profile.heap.calculation;
/*    */ 
/*    */ import org.bipolar.run.profile.heap.IndexFiles;
/*    */ import org.bipolar.run.profile.heap.data.LinkedByNameId;
/*    */ import org.bipolar.run.profile.heap.data.V8HeapEdge;
/*    */ import org.bipolar.run.profile.heap.data.V8HeapEntry;
/*    */ import org.bipolar.run.profile.heap.io.reverse.LinksReaderFactory;
/*    */ import org.bipolar.util.CloseableThrowableConsumer;
/*    */ import java.io.Closeable;
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
/*    */ public class ReverseIndexes
/*    */   implements Closeable
/*    */ {
/*    */   private final ReverseLinkIndexProcessor myReverseLinkIndexProcessor;
/*    */   private final StringReverseIndexProcessor myReverseStringIndexProcessor;
/*    */   private final long myNodesCnt;
/*    */   
/*    */   public ReverseIndexes(long nodesCnt, @NotNull IndexFiles indexFiles, File nodeIndex) throws IOException {
/* 40 */     this.myNodesCnt = nodesCnt;
/* 41 */     this.myReverseLinkIndexProcessor = new ReverseLinkIndexProcessor(nodesCnt, indexFiles);
/* 42 */     this.myReverseStringIndexProcessor = new StringReverseIndexProcessor(indexFiles, (File)SYNTHETIC_LOCAL_VARIABLE_4);
/*    */   }
/*    */   
/*    */   public void continueCalculation(@NotNull File edgeIndexFile, long numEdges) throws IOException {
/* 46 */     if (edgeIndexFile == null) $$$reportNull$$$0(1);  this.myReverseStringIndexProcessor.continueCalculation(edgeIndexFile, numEdges, this.myNodesCnt);
/* 47 */     this.myReverseStringIndexProcessor.close();
/*    */     
/* 49 */     this.myReverseLinkIndexProcessor.continueCalculation(edgeIndexFile, numEdges, this.myNodesCnt);
/* 50 */     this.myReverseLinkIndexProcessor.close();
/*    */   }
/*    */   
/*    */   public CloseableThrowableConsumer<V8HeapEntry, IOException> getNodesFirstStageCalculator() {
/* 54 */     return this.myReverseStringIndexProcessor.getNodesFirstStageCalculator();
/*    */   }
/*    */   
/*    */   public CloseableThrowableConsumer<V8HeapEdge, IOException> getEdgesFirstStageCalculator() {
/* 58 */     final CloseableThrowableConsumer<V8HeapEdge, IOException> edgeCalculator = this.myReverseLinkIndexProcessor.getFirstStageCalculator();
/* 59 */     final CloseableThrowableConsumer<V8HeapEdge, IOException> reverseStringCalculator = this.myReverseStringIndexProcessor.getFirstStageCalculator();
/*    */     
/* 61 */     return new CloseableThrowableConsumer<V8HeapEdge, IOException>()
/*    */       {
/*    */         public void close() throws IOException {
/* 64 */           edgeCalculator.close();
/* 65 */           reverseStringCalculator.close();
/*    */         }
/*    */ 
/*    */         
/*    */         public void consume(V8HeapEdge edge) throws IOException {
/* 70 */           edgeCalculator.consume(edge);
/* 71 */           reverseStringCalculator.consume(edge);
/*    */         }
/*    */       };
/*    */   }
/*    */ 
/*    */   
/*    */   public void close() throws IOException {
/* 78 */     this.myReverseLinkIndexProcessor.close();
/* 79 */     this.myReverseStringIndexProcessor.close();
/*    */   }
/*    */   
/*    */   public void correctStringsSize(long cnt) {
/* 83 */     this.myReverseStringIndexProcessor.correctSize(cnt);
/*    */   }
/*    */   
/*    */   public LinksReaderFactory<LinkedByNameId> getReverseStringIndexProcessor() throws FileNotFoundException {
/* 87 */     return this.myReverseStringIndexProcessor.getLinksReaderFactory();
/*    */   }
/*    */   
/*    */   public LinksReaderFactory<V8HeapEdge> getReverseLinkIndexProcessor() throws FileNotFoundException {
/* 91 */     return this.myReverseLinkIndexProcessor.getLinksReaderFactory();
/*    */   }
/*    */ }


/* Location:              C:\Program Files\JetBrains\WebStorm 2021.1\plugins\NodeJS\lib\NodeJS.jar!\com\jetbrains\nodejs\run\profile\heap\calculation\ReverseIndexes.class
 * Java compiler version: 11 (55.0)
 * JD-Core Version:       1.1.3
 */