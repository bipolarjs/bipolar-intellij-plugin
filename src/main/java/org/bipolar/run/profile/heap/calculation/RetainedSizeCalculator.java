/*     */ package org.bipolar.run.profile.heap.calculation;
/*     */ 
/*     */ import com.intellij.openapi.util.Pair;
/*     */ import org.bipolar.run.profile.heap.IndexFiles;
/*     */ import org.bipolar.run.profile.heap.V8CachingReader;
/*     */ import org.bipolar.run.profile.heap.data.Aggregate;
/*     */ import org.bipolar.run.profile.heap.data.V8HeapEdge;
/*     */ import org.bipolar.run.profile.heap.data.V8HeapEntry;
/*     */ import org.bipolar.run.profile.heap.data.V8HeapNodeType;
/*     */ import org.bipolar.run.profile.heap.io.RawSerializer;
/*     */ import org.bipolar.run.profile.heap.io.SequentialRawReader;
/*     */ import org.bipolar.run.profile.heap.io.reverse.LinksReader;
/*     */ import org.bipolar.run.profile.heap.io.reverse.LinksReaderFactory;
/*     */ import org.bipolar.util.CloseableThrowableConsumer;
/*     */ import it.unimi.dsi.fastutil.ints.IntList;
/*     */ import it.unimi.dsi.fastutil.longs.LongArrayList;
/*     */ import it.unimi.dsi.fastutil.longs.LongList;
/*     */ import java.io.Closeable;
/*     */ import java.io.File;
/*     */ import java.io.IOException;
/*     */ import java.util.ArrayList;
/*     */ import java.util.List;
/*     */ import java.util.Map;
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
/*     */ public class RetainedSizeCalculator
/*     */   implements Closeable
/*     */ {
/*     */   private final long myNodesCnt;
/*     */   private final boolean myShowHiddenData;
/*     */   private LongArrayList myRetainedSizes;
/*     */   private List<Integer> myBiggest;
/*     */   private LinksReaderFactory<Long> myAggregatesLinksReaderFactory;
/*     */   private V8HeapInMemoryIndexes myInMemoryIndexes;
/*     */   
/*     */   public RetainedSizeCalculator(long cnt, boolean showHiddenData) {
/*  52 */     this.myNodesCnt = cnt;
/*  53 */     this.myShowHiddenData = showHiddenData;
/*     */   }
/*     */   public void execute(@NotNull V8CachingReader cachingReader, @NotNull LinksReaderFactory<V8HeapEdge> reverseLinkIndexProcessor, @NotNull File nodeIndexFile, @NotNull IndexFiles indexFiles, V8ImportantStringsHolder stringsHolder) throws IOException {
/*     */     V8PostOrderBuilder postOrderBuilder;
/*     */     DominatorTreeBuilder dominatorTreeBuilder;
/*  58 */     if (cachingReader == null) $$$reportNull$$$0(0);  if (reverseLinkIndexProcessor == null) $$$reportNull$$$0(1);  if (nodeIndexFile == null) $$$reportNull$$$0(2);  if (indexFiles == null) $$$reportNull$$$0(3);  V8FlagsCalculator calculator = new V8FlagsCalculator((int)this.myNodesCnt, cachingReader, stringsHolder);
/*  59 */     Flags flags = calculator.execute();
/*     */     
/*  61 */     V8DistancesCalculator distancesCalculator = new V8DistancesCalculator((int)this.myNodesCnt, cachingReader, calculator.getDocumentDOMRoot(), calculator.getGcRoots(), stringsHolder);
/*  62 */     distancesCalculator.execute();
/*  63 */     IntList distances = distancesCalculator.getDistances();
/*  64 */     IntList parents = distancesCalculator.getParents();
/*     */     
/*  66 */     LinksReader<V8HeapEdge> randomReverseReader = reverseLinkIndexProcessor.create(false);
/*     */ 
/*     */     
/*     */     try {
/*  70 */       postOrderBuilder = new V8PostOrderBuilder((int)this.myNodesCnt, cachingReader, flags, randomReverseReader, this.myShowHiddenData);
/*  71 */       postOrderBuilder.execute();
/*  72 */       dominatorTreeBuilder = new DominatorTreeBuilder((int)this.myNodesCnt, cachingReader, postOrderBuilder, flags, randomReverseReader, this.myShowHiddenData);
/*  73 */       dominatorTreeBuilder.execute();
/*     */     } finally {
/*  75 */       randomReverseReader.close();
/*     */     } 
/*     */     
/*  78 */     calculateRetainedSizes(nodeIndexFile, postOrderBuilder, dominatorTreeBuilder.getDominatorsTree());
/*     */     
/*  80 */     calculateBiggestObjects(flags, cachingReader, 100);
/*     */     
/*  82 */     DominatedNodesBuilder dominatedNodesBuilder = new DominatedNodesBuilder(dominatorTreeBuilder.getDominatorsTree(), (int)this.myNodesCnt);
/*  83 */     dominatedNodesBuilder.execute();
/*     */     
/*  85 */     AggregatesBuilder aggregatesBuilder = new AggregatesBuilder(indexFiles, cachingReader, flags, this.myShowHiddenData, (int)this.myNodesCnt, dominatedNodesBuilder, this.myRetainedSizes, distances, postOrderBuilder.getUnreachable());
/*  86 */     aggregatesBuilder.execute();
/*  87 */     Map<Long, Aggregate> aggregateMap = aggregatesBuilder.getAggregateMap();
/*  88 */     this.myAggregatesLinksReaderFactory = aggregatesBuilder.createLinksReaderFactory();
/*  89 */     IntList unreachable = postOrderBuilder.getUnreachable();
/*  90 */     IntList onlyWeak = postOrderBuilder.getOnlyWeak();
/*     */     
/*  92 */     this.myInMemoryIndexes = new V8HeapInMemoryIndexes(flags, (LongList)this.myRetainedSizes, parents, this.myBiggest, distances, aggregateMap, unreachable, onlyWeak);
/*     */   }
/*     */ 
/*     */   
/*     */   public V8HeapInMemoryIndexes getInMemoryIndexes() {
/*  97 */     return this.myInMemoryIndexes;
/*     */   }
/*     */   
/*     */   public LinksReaderFactory<Long> getAggregatesLinksReaderFactory() {
/* 101 */     return this.myAggregatesLinksReaderFactory;
/*     */   }
/*     */   
/*     */   private void calculateBiggestObjects(Flags flagsCalculator, V8CachingReader cachingReader, int number) {
/* 105 */     SelectTopOnConstArray select = new SelectTopOnConstArray(this.myRetainedSizes, 100, integer -> 
/* 106 */         (flagsCalculator.isPage(integer.intValue()) && (this.myShowHiddenData || !V8HeapNodeType.kHidden.equals(cachingReader.getNode(integer.intValue()).getType()))));
/*     */     
/* 108 */     select.execute();
/* 109 */     this.myBiggest = new ArrayList<>();
/* 110 */     for (Pair<Long, Integer> pair : select.getTop()) {
/* 111 */       this.myBiggest.add((Integer)pair.getSecond());
/* 112 */       if (this.myBiggest.size() >= number) {
/*     */         break;
/*     */       }
/*     */     } 
/*     */   }
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
/*     */   private void calculateRetainedSizes(@NotNull File nodeIndexFile, V8PostOrderBuilder postOrderBuilder, IntList dominatorsTree) throws IOException {
/* 151 */     if (nodeIndexFile == null) $$$reportNull$$$0(4);  this.myRetainedSizes = new LongArrayList((int)this.myNodesCnt);
/* 152 */     (new SequentialRawReader(nodeIndexFile, (RawSerializer)V8HeapEntry.MyRawSerializer.getInstance(), this.myNodesCnt))
/* 153 */       .iterate(new CloseableThrowableConsumer<V8HeapEntry, IOException>()
/*     */         {
/*     */           public void close() {}
/*     */ 
/*     */ 
/*     */           
/*     */           public void consume(V8HeapEntry entry) {
/* 160 */             RetainedSizeCalculator.this.myRetainedSizes.add(entry.getSize());
/*     */           }
/*     */         });
/* 163 */     for (int postOrder = 0; postOrder < this.myNodesCnt - 1L; postOrder++) {
/* 164 */       int node = postOrderBuilder.getPostOrderToNode().getInt(postOrder);
/* 165 */       int dominator = dominatorsTree.getInt(node);
/* 166 */       this.myRetainedSizes.set(dominator, this.myRetainedSizes.getLong(node) + this.myRetainedSizes.getLong(dominator));
/*     */     } 
/*     */   }
/*     */   
/*     */   public void close() throws IOException {}
/*     */ }


/* Location:              C:\Program Files\JetBrains\WebStorm 2021.1\plugins\NodeJS\lib\NodeJS.jar!\com\jetbrains\nodejs\run\profile\heap\calculation\RetainedSizeCalculator.class
 * Java compiler version: 11 (55.0)
 * JD-Core Version:       1.1.3
 */