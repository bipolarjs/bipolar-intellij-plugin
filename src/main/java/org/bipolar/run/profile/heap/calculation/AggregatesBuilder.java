/*     */ package org.bipolar.run.profile.heap.calculation;
/*     */ 
/*     */ import org.bipolar.run.profile.heap.IndexFiles;
/*     */ import org.bipolar.run.profile.heap.V8CachingReader;
/*     */ import org.bipolar.run.profile.heap.data.Aggregate;
/*     */ import org.bipolar.run.profile.heap.data.V8HeapEntry;
/*     */ import org.bipolar.run.profile.heap.data.V8HeapNodeType;
/*     */ import org.bipolar.run.profile.heap.io.LongRawSerializer;
/*     */ import org.bipolar.run.profile.heap.io.RawSerializer;
/*     */ import org.bipolar.run.profile.heap.io.SequentialRawWriter;
/*     */ import org.bipolar.run.profile.heap.io.reverse.LinksReaderFactory;
/*     */ import org.bipolar.run.profile.heap.io.reverse.SizeOffset;
/*     */ import gnu.trove.TLongHashSet;
/*     */ import it.unimi.dsi.fastutil.ints.IntArrayList;
/*     */ import it.unimi.dsi.fastutil.ints.IntList;
/*     */ import it.unimi.dsi.fastutil.longs.LongArrayList;
/*     */ import java.io.File;
/*     */ import java.io.IOException;
/*     */ import java.util.ArrayDeque;
/*     */ import java.util.HashMap;
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
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public final class AggregatesBuilder
/*     */ {
/*     */   private static final int PORTION_SIZE = 10000;
/*     */   @NotNull
/*     */   private final IndexFiles myIndexFiles;
/*     */   @NotNull
/*     */   private final V8CachingReader myReader;
/*     */   @NotNull
/*     */   private final Flags myFlags;
/*     */   private final boolean myShowHiddenData;
/*     */   @NotNull
/*     */   private final DominatedNodesBuilder myDominatedNodesBuilder;
/*     */   private final LongArrayList myRetainedSizes;
/*     */   private final IntList myDistances;
/*     */   private final IntList myUnreachable;
/*     */   private File myNumAggregated;
/*     */   private File myLinks;
/*     */   private final Map<Long, Aggregate> myAggregateMap;
/*     */   private final LongArrayList myTmpClassShortcut;
/*     */   private final LongArrayList myTmpSize;
/*     */   private final IntList myTmpTypeShortcut;
/*     */   
/*     */   public AggregatesBuilder(@NotNull IndexFiles indexFiles, @NotNull V8CachingReader reader, @NotNull Flags flags, boolean showHiddenData, long nodesCnt, @NotNull DominatedNodesBuilder dominatedNodesBuilder, LongArrayList retainedSizes, IntList distances, IntList unreachable) {
/*  67 */     this.myIndexFiles = indexFiles;
/*  68 */     this.myReader = reader;
/*  69 */     this.myFlags = flags;
/*  70 */     this.myShowHiddenData = showHiddenData;
/*  71 */     this.myDominatedNodesBuilder = dominatedNodesBuilder;
/*  72 */     this.myRetainedSizes = retainedSizes;
/*  73 */     this.myDistances = distances;
/*  74 */     this.myUnreachable = (IntList)SYNTHETIC_LOCAL_VARIABLE_10;
/*  75 */     this.myAggregateMap = new HashMap<>();
/*  76 */     this.myTmpTypeShortcut = (IntList)new IntArrayList((int)nodesCnt);
/*  77 */     this.myTmpClassShortcut = new LongArrayList((int)nodesCnt);
/*  78 */     this.myTmpSize = new LongArrayList((int)nodesCnt);
/*     */   }
/*     */   
/*     */   public void execute() throws IOException {
/*  82 */     this.myNumAggregated = this.myIndexFiles.generate(V8HeapIndexManager.Category.aggregateNum, ".aggregate");
/*  83 */     this.myLinks = this.myIndexFiles.generate(V8HeapIndexManager.Category.aggregateLinks, ".aggregateLinks");
/*     */ 
/*     */     
/*  86 */     SequentialRawWriter<SizeOffset> numWriter = new SequentialRawWriter(this.myNumAggregated, (RawSerializer)SizeOffset.MySerializer.getInstance());
/*  87 */     SequentialRawWriter<Long> writer = new SequentialRawWriter(this.myLinks, (RawSerializer)new LongRawSerializer());
/*     */     
/*     */     try {
/*  90 */       long from = -100L;
/*  91 */       long to = 9999L;
/*  92 */       AggregatesPortionBuilder first = new AggregatesPortionBuilder(this.myReader, this.myFlags, this.myShowHiddenData, from, to, 0, this.myDistances, this.myUnreachable)
/*     */         {
/*     */           protected void additionalProcessing(V8HeapEntry entry) {
/*  95 */             AggregatesBuilder.this.myTmpTypeShortcut.add(entry.getType().getNumber());
/*  96 */             AggregatesBuilder.this.myTmpClassShortcut.add(entry.getClassIndex());
/*  97 */             AggregatesBuilder.this.myTmpSize.add(entry.getSize());
/*     */           }
/*     */         };
/* 100 */       first.calculate(numWriter, writer);
/* 101 */       this.myAggregateMap.putAll(first.getAggregates());
/*     */       
/* 103 */       from = 10001L;
/* 104 */       to = from + 10000L - 1L;
/* 105 */       long stringNum = this.myReader.getStringIndex().getCnt();
/* 106 */       while (to <= stringNum) {
/* 107 */         AggregatesPortionBuilder builder = new AggregatesPortionBuilder(this.myReader, this.myFlags, this.myShowHiddenData, from, to, this.myAggregateMap.size(), this.myDistances, this.myUnreachable);
/*     */         
/* 109 */         builder.calculate(numWriter, writer);
/* 110 */         this.myAggregateMap.putAll(builder.getAggregates());
/* 111 */         from = to + 1L;
/* 112 */         to = from + 10000L - 1L;
/*     */       } 
/*     */     } finally {
/* 115 */       numWriter.close();
/* 116 */       writer.close();
/*     */     } 
/*     */     
/* 119 */     calculateRetainedSizes(this.myDominatedNodesBuilder.getDominatedIdx(), this.myDominatedNodesBuilder.getDominatedLinks());
/*     */   }
/*     */   
/*     */   public LinksReaderFactory<Long> createLinksReaderFactory() {
/* 123 */     return new LinksReaderFactory((RawSerializer)new LongRawSerializer(), this.myNumAggregated, this.myLinks);
/*     */   }
/*     */   
/*     */   public Map<Long, Aggregate> getAggregateMap() {
/* 127 */     return this.myAggregateMap;
/*     */   }
/*     */   
/*     */   public void calculateRetainedSizes(IntList dominatedIdx, IntList dominatedLinks) {
/* 131 */     TLongHashSet seen = new TLongHashSet();
/* 132 */     ArrayDeque<Long> sizes = new ArrayDeque<>();
/* 133 */     ArrayDeque<Long> classes = new ArrayDeque<>();
/*     */     
/* 135 */     ArrayDeque<Integer> queue = new ArrayDeque<>();
/* 136 */     queue.add(Integer.valueOf(0));
/* 137 */     int nativeNumber = V8HeapNodeType.kNative.getNumber();
/*     */     
/* 139 */     while (!queue.isEmpty()) {
/* 140 */       int nodeIdx = ((Integer)queue.removeLast()).intValue();
/*     */       
/* 142 */       long classIdx = this.myTmpClassShortcut.getLong(nodeIdx);
/* 143 */       int type = this.myTmpTypeShortcut.getInt(nodeIdx);
/* 144 */       long size = this.myTmpSize.getLong(nodeIdx);
/*     */       
/* 146 */       Aggregate aggregate = this.myAggregateMap.get(Long.valueOf(classIdx));
/* 147 */       if (aggregate == null);
/*     */ 
/*     */ 
/*     */       
/* 151 */       int domFrom = dominatedIdx.getInt(nodeIdx);
/* 152 */       int domTo = (nodeIdx == dominatedIdx.size() - 1) ? dominatedLinks.size() : dominatedIdx.getInt(nodeIdx + 1);
/*     */       
/* 154 */       if (!seen.contains(classIdx) && (this.myShowHiddenData || this.myFlags.isPage(nodeIdx)) && (nativeNumber == type || size > 0L)) {
/*     */         
/* 156 */         long current = this.myRetainedSizes.getLong(nodeIdx);
/* 157 */         if (aggregate != null) {
/* 158 */           aggregate.addRetained(current);
/*     */         }
/* 160 */         if (domFrom != domTo) {
/* 161 */           seen.add(classIdx);
/* 162 */           sizes.addLast(Long.valueOf(queue.size()));
/* 163 */           classes.addLast(Long.valueOf(classIdx));
/*     */         } 
/*     */       } 
/*     */       
/* 167 */       for (int i = domFrom; i < domTo; i++) {
/* 168 */         int dependent = dominatedLinks.getInt(i);
/* 169 */         if (dependent >= 0) {
/* 170 */           queue.addLast(Integer.valueOf(dependent));
/*     */         }
/*     */       } 
/*     */       
/* 174 */       while (!sizes.isEmpty() && ((Long)sizes.getLast()).longValue() == queue.size()) {
/* 175 */         sizes.removeLast();
/* 176 */         Long classIndex = classes.removeLast();
/* 177 */         seen.remove(classIndex.longValue());
/*     */       } 
/*     */     } 
/*     */   }
/*     */ }


/* Location:              C:\Program Files\JetBrains\WebStorm 2021.1\plugins\NodeJS\lib\NodeJS.jar!\com\jetbrains\nodejs\run\profile\heap\calculation\AggregatesBuilder.class
 * Java compiler version: 11 (55.0)
 * JD-Core Version:       1.1.3
 */