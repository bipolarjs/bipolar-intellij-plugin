/*     */ package org.bipolar.run.profile.heap.calculation;
/*     */ 
/*     */ import org.bipolar.run.profile.heap.V8CachingReader;
/*     */ import org.bipolar.run.profile.heap.data.Aggregate;
/*     */ import org.bipolar.run.profile.heap.data.V8HeapEntry;
/*     */ import org.bipolar.run.profile.heap.data.V8HeapNodeType;
/*     */ import org.bipolar.run.profile.heap.io.RawSerializer;
/*     */ import org.bipolar.run.profile.heap.io.SequentialRawReader;
/*     */ import org.bipolar.run.profile.heap.io.SequentialRawWriter;
/*     */ import org.bipolar.run.profile.heap.io.reverse.SizeOffset;
/*     */ import org.bipolar.util.CloseableThrowableConsumer;
/*     */ import it.unimi.dsi.fastutil.ints.IntList;
/*     */ import java.io.IOException;
/*     */ import java.util.ArrayList;
/*     */ import java.util.HashMap;
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
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class AggregatesPortionBuilder
/*     */ {
/*     */   private final V8CachingReader myReader;
/*     */   @NotNull
/*     */   private final Flags myFlags;
/*     */   private final boolean myShowHiddenData;
/*     */   private final Map<Long, Aggregate> myAggregates;
/*     */   private final Map<Integer, List<Long>> myLinks;
/*     */   private final long myFromClassIdx;
/*     */   private final long myToClassIdx;
/*     */   private final int myIdxOffset;
/*     */   private final IntList myDistances;
/*     */   private final IntList myUnreachable;
/*     */   private long myOffset;
/*     */   
/*     */   public AggregatesPortionBuilder(@NotNull V8CachingReader reader, @NotNull Flags flags, boolean showHiddenData, long fromClassIdx, long toClassIdx, int idxOffset, IntList distances, IntList unreachable) {
/*  59 */     this.myReader = reader;
/*  60 */     this.myFlags = flags;
/*  61 */     this.myShowHiddenData = showHiddenData;
/*  62 */     this.myFromClassIdx = fromClassIdx;
/*  63 */     this.myToClassIdx = toClassIdx;
/*  64 */     this.myIdxOffset = idxOffset;
/*  65 */     this.myDistances = (IntList)SYNTHETIC_LOCAL_VARIABLE_9;
/*  66 */     this.myUnreachable = (IntList)SYNTHETIC_LOCAL_VARIABLE_10;
/*  67 */     this.myAggregates = new HashMap<>();
/*  68 */     this.myLinks = new HashMap<>();
/*  69 */     this.myOffset = 0L;
/*     */   }
/*     */   
/*     */   public void calculate(@NotNull final SequentialRawWriter<SizeOffset> numsWriter, @NotNull final SequentialRawWriter<Long> linksWriter) throws IOException {
/*  73 */     if (numsWriter == null) $$$reportNull$$$0(2);  if (linksWriter == null) $$$reportNull$$$0(3); 
/*  74 */     SequentialRawReader<V8HeapEntry> reader = new SequentialRawReader(this.myReader.getNodeIndexFile(), (RawSerializer)V8HeapEntry.MyRawSerializer.getInstance(), this.myDistances.size());
/*     */     
/*  76 */     reader.iterate(new CloseableThrowableConsumer<V8HeapEntry, IOException>()
/*     */         {
/*     */           public void close() throws IOException {
/*  79 */             for (int i = AggregatesPortionBuilder.this.myIdxOffset; i < AggregatesPortionBuilder.this.myIdxOffset + AggregatesPortionBuilder.this.myLinks.size(); i++) {
/*  80 */               List<Long> list = AggregatesPortionBuilder.this.myLinks.get(Integer.valueOf(i));
/*  81 */               numsWriter.write(new SizeOffset(list.size(), AggregatesPortionBuilder.this.myOffset));
/*  82 */               AggregatesPortionBuilder.this.myOffset += list.size();
/*  83 */               for (Long link : list) {
/*  84 */                 linksWriter.write(link);
/*     */               }
/*     */             } 
/*     */           }
/*     */ 
/*     */           
/*     */           public void consume(V8HeapEntry entry) throws IOException {
/*  91 */             AggregatesPortionBuilder.this.additionalProcessing(entry);
/*  92 */             if (!AggregatesPortionBuilder.this.myShowHiddenData && !AggregatesPortionBuilder.this.myFlags.isPage((int)entry.getId())) {
/*     */               return;
/*     */             }
/*  95 */             if (!V8HeapNodeType.kNative.equals(entry.getType()) && entry.getSize() == 0L)
/*  96 */               return;  long classIndex = entry.getClassIndex();
/*  97 */             if (classIndex < AggregatesPortionBuilder.this.myFromClassIdx || classIndex > AggregatesPortionBuilder.this.myToClassIdx)
/*     */               return; 
/*  99 */             Aggregate aggregate = AggregatesPortionBuilder.this.myAggregates.get(Long.valueOf(classIndex));
/* 100 */             int distance = AggregatesPortionBuilder.this.myDistances.getInt((int)entry.getId());
/* 101 */             if (aggregate == null) {
/* 102 */               AggregatesPortionBuilder.this.myAggregates.put(Long.valueOf(classIndex), 
/*     */                   
/* 104 */                   aggregate = new Aggregate(AggregatesPortionBuilder.this.myIdxOffset + AggregatesPortionBuilder.this.myAggregates.size(), classIndex, distance, entry.getSize(), entry.getType()));
/* 105 */               List<Long> list = new ArrayList<>();
/* 106 */               list.add(Long.valueOf(entry.getId()));
/* 107 */               AggregatesPortionBuilder.this.myLinks.put(Integer.valueOf(aggregate.getId()), list);
/*     */             } else {
/*     */               
/* 110 */               aggregate.addSize(entry.getSize());
/* 111 */               if (distance < aggregate.getDistance()) {
/* 112 */                 aggregate.setDistance(distance);
/*     */               }
/* 114 */               ((List<Long>)AggregatesPortionBuilder.this.myLinks.get(Integer.valueOf(aggregate.getId()))).add(Long.valueOf(entry.getId()));
/*     */             } 
/*     */           }
/*     */         });
/*     */   }
/*     */ 
/*     */   
/*     */   protected void additionalProcessing(V8HeapEntry entry) {}
/*     */   
/*     */   public Map<Long, Aggregate> getAggregates() {
/* 124 */     return this.myAggregates;
/*     */   }
/*     */ }


/* Location:              C:\Program Files\JetBrains\WebStorm 2021.1\plugins\NodeJS\lib\NodeJS.jar!\com\jetbrains\nodejs\run\profile\heap\calculation\AggregatesPortionBuilder.class
 * Java compiler version: 11 (55.0)
 * JD-Core Version:       1.1.3
 */