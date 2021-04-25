/*     */ package org.bipolar.run.profile.cpu.v8log.calculation;
/*     */ 
/*     */ import org.bipolar.run.profile.heap.CompositeCloseable;
/*     */ import org.bipolar.run.profile.heap.IndexFiles;
/*     */ import org.bipolar.run.profile.heap.io.LinksWriter;
/*     */ import org.bipolar.run.profile.heap.io.LongRawSerializer;
/*     */ import org.bipolar.run.profile.heap.io.RawSerializer;
/*     */ import org.bipolar.run.profile.heap.io.SequentialRawWriter;
/*     */ import org.bipolar.run.profile.heap.io.reverse.LinksReader;
/*     */ import org.bipolar.run.profile.heap.io.reverse.LinksReaderFactory;
/*     */ import it.unimi.dsi.fastutil.longs.Long2LongMap;
/*     */ import it.unimi.dsi.fastutil.longs.Long2LongOpenHashMap;
/*     */ import it.unimi.dsi.fastutil.longs.LongArrayList;
/*     */ import java.io.Closeable;
/*     */ import java.io.File;
/*     */ import java.io.IOException;
/*     */ import java.util.ArrayList;
/*     */ import java.util.Collection;
/*     */ import java.util.HashMap;
/*     */ import java.util.Iterator;
/*     */ import java.util.List;
/*     */ import java.util.Map;
/*     */
import org.jetbrains.annotations.NotNull;
/*     */ 
/*     */ 
/*     */ 
/*     */ public final class V8LogStackDurationWorker
/*     */   implements Closeable
/*     */ {
/*     */   private Long2LongMap myDurationByIdMap;
/*     */   @NotNull
/*     */   private final IndexFiles<V8LogIndexesWriter.Category> myIndexFiles;
/*     */   private ArrayList<Duration> myStack;
/*     */   private final LinksWriter<Long> myWriter;
/*     */   private long myDurationCnt;
/*     */   private long myTick;
/*     */   private File myNumDurationFile;
/*     */   private File myDurationFile;
/*     */   private final File myNumDurationIdFile;
/*     */   private final File myDurationIdFile;
/*     */   private final Map<Long, TimeDistribution> myDistribution;
/*     */   private final Map<Long, TimeDistribution> mySelfDistribution;
/*     */   private File myDistributionFile;
/*     */   private File mySelfDistributionFile;
/*     */   
/*     */   public V8LogStackDurationWorker(@NotNull IndexFiles<V8LogIndexesWriter.Category> indexFiles) throws IOException {
/*  47 */     this.myIndexFiles = indexFiles;
/*  48 */     this.myNumDurationIdFile = indexFiles.generate(V8LogIndexesWriter.Category.numDurationByTick1, "numDuration");
/*  49 */     this.myDurationIdFile = indexFiles.generate(V8LogIndexesWriter.Category.durationByTick1, "duration");
/*  50 */     this.myWriter = new LinksWriter(this.myNumDurationIdFile, this.myDurationIdFile, (RawSerializer)new LongRawSerializer());
/*  51 */     this.myStack = new ArrayList<>();
/*  52 */     this.myDurationByIdMap = (Long2LongMap)new Long2LongOpenHashMap();
/*  53 */     this.myDurationCnt = 0L;
/*  54 */     this.myTick = 0L;
/*  55 */     this.myDistribution = new HashMap<>();
/*  56 */     this.mySelfDistribution = new HashMap<>();
/*     */   }
/*     */   
/*     */   public LinksReaderFactory<Long> getDurationFactory() {
/*  60 */     return new LinksReaderFactory((RawSerializer)new LongRawSerializer(), this.myNumDurationFile, this.myDurationFile);
/*     */   }
/*     */   
/*     */   public void tick(long nanos, List<Long> bottomUpStringNumbers) throws IOException {
/*  64 */     this.myTick = nanos;
/*     */     
/*  66 */     int stackId = this.myStack.size() - 1;
/*  67 */     int newIdx = bottomUpStringNumbers.size() - 1;
/*  68 */     for (; newIdx >= 0 && stackId >= 0; newIdx--, stackId--) {
/*  69 */       Long newId = bottomUpStringNumbers.get(newIdx);
/*  70 */       Duration oldId = this.myStack.get(stackId);
/*  71 */       if (!newId.equals(Long.valueOf(oldId.getStringId())))
/*     */         break; 
/*  73 */     }  boolean selfTimePut = false;
/*  74 */     for (int i = 0; i <= stackId; i++) {
/*  75 */       Duration duration = this.myStack.get(i);
/*  76 */       int interval = (int)(nanos - duration.getStartTs());
/*  77 */       this.myDurationByIdMap.put(duration.getDurationId(), interval);
/*  78 */       if (!selfTimePut && 1L != duration.getStringId()) {
/*  79 */         registerInDistribution(this.mySelfDistribution, duration.getStringId(), duration.getStartTs(), interval);
/*  80 */         selfTimePut = true;
/*     */       } 
/*  82 */       registerInDistribution(this.myDistribution, duration.getStringId(), duration.getStartTs(), interval);
/*     */     } 
/*  84 */     List<Duration> tail = this.myStack.subList(stackId + 1, this.myStack.size());
/*  85 */     this.myStack = new ArrayList<>(bottomUpStringNumbers.size());
/*  86 */     List<Long> durationIdsList = new ArrayList<>();
/*  87 */     for (int j = 0; j <= newIdx; j++) {
/*  88 */       long durationId = this.myDurationCnt++;
/*  89 */       this.myStack.add(new Duration(((Long)bottomUpStringNumbers.get(j)).longValue(), durationId, nanos));
/*  90 */       durationIdsList.add(Long.valueOf(durationId));
/*     */     } 
/*  92 */     for (Duration duration : tail) {
/*  93 */       this.myStack.add(duration);
/*  94 */       durationIdsList.add(Long.valueOf(duration.getDurationId()));
/*     */     } 
/*  96 */     this.myWriter.write(durationIdsList);
/*     */   }
/*     */   
/*     */   private static void registerInDistribution(@NotNull Map<Long, TimeDistribution> map, long stringId, long startTs, int interval) {
/* 100 */     if (map == null) $$$reportNull$$$0(1);  TimeDistribution distribution = map.get(Long.valueOf(stringId));
/* 101 */     if (distribution == null) map.put(Long.valueOf(stringId), distribution = new TimeDistribution()); 
/* 102 */     distribution.register(startTs, SYNTHETIC_LOCAL_VARIABLE_5);
/*     */   }
/*     */   
/*     */   public void recalculateDurationsWriteDistribution(@NotNull Long maxStringId) throws IOException {
/* 106 */     if (maxStringId == null) $$$reportNull$$$0(2);  this.myWriter.close();
/* 107 */     for (Duration duration : this.myStack) {
/* 108 */       this.myDurationByIdMap.put(duration.getDurationId(), (int)(this.myTick - duration.getStartTs()));
/*     */     }
/* 110 */     CompositeCloseable closeable = new CompositeCloseable();
/*     */     try {
/* 112 */       this.myNumDurationFile = this.myIndexFiles.generate(V8LogIndexesWriter.Category.numDurationByTick2, "numDuration2");
/* 113 */       this.myDurationFile = this.myIndexFiles.generate(V8LogIndexesWriter.Category.durationByTick2, "duration2");
/*     */       
/* 115 */       LinksWriter<Long> writer = (LinksWriter<Long>)closeable.register((Closeable)new LinksWriter(this.myNumDurationFile, this.myDurationFile, (RawSerializer)new LongRawSerializer()));
/*     */       
/* 117 */       LinksReader<Long> reader = (LinksReader<Long>)closeable.register((Closeable)(new LinksReaderFactory((RawSerializer)new LongRawSerializer(), this.myNumDurationIdFile, this.myDurationIdFile)).create(true));
/* 118 */       reader.iterateWithThrowable(longs -> {
/*     */             LongArrayList duration = new LongArrayList(); Iterator<Long> iterator = longs.iterator(); while (iterator.hasNext()) {
/*     */               long id = ((Long)iterator.next()).longValue();
/*     */               duration.add(this.myDurationByIdMap.get(id));
/*     */             } 
/*     */             writer.write((Collection)duration);
/*     */           });
/*     */     } finally {
/* 126 */       closeable.close();
/*     */     } 
/* 128 */     this.myDurationByIdMap = null;
/*     */     
/* 130 */     writeDistributions(maxStringId);
/*     */   }
/*     */   
/*     */   private void writeDistributions(Long maxStringId) throws IOException {
/* 134 */     this.myDistributionFile = this.myIndexFiles.generate(V8LogIndexesWriter.Category.distribution, "Distribution");
/* 135 */     this.mySelfDistributionFile = this.myIndexFiles.generate(V8LogIndexesWriter.Category.selfDistribution, "SelfDistribution");
/* 136 */     CompositeCloseable closeable = new CompositeCloseable();
/*     */     try {
/* 138 */       writeDistributionMap(this.myDistribution, this.myDistributionFile, maxStringId, closeable);
/* 139 */       writeDistributionMap(this.mySelfDistribution, this.mySelfDistributionFile, maxStringId, closeable);
/*     */     } finally {
/* 141 */       closeable.close();
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private static void writeDistributionMap(@NotNull Map<Long, TimeDistribution> map, @NotNull File distributionFile, Long maxStringId, CompositeCloseable closeable) throws IOException {
/* 149 */     if (map == null) $$$reportNull$$$0(3);  if (distributionFile == null) $$$reportNull$$$0(4);  RawSerializer<TimeDistribution> serializer = TimeDistribution.getSerializer(TimeDistribution.STANDARD);
/*     */     
/* 151 */     SequentialRawWriter<TimeDistribution> writer = (SequentialRawWriter<TimeDistribution>)closeable.register((Closeable)new SequentialRawWriter(distributionFile, serializer)); long i;
/* 152 */     for (i = 0L; i < maxStringId.longValue(); i++) {
/* 153 */       TimeDistribution distribution = map.get(Long.valueOf(i));
/* 154 */       distribution = (distribution == null) ? new TimeDistribution() : distribution;
/* 155 */       writer.write(distribution);
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   public void close() throws IOException {
/* 161 */     this.myWriter.close();
/*     */   }
/*     */   
/*     */   public File getDistributionFile() {
/* 165 */     return this.myDistributionFile;
/*     */   }
/*     */   
/*     */   public File getSelfDistributionFile() {
/* 169 */     return this.mySelfDistributionFile;
/*     */   }
/*     */   
/*     */   private static class Duration {
/*     */     private final long myStringId;
/*     */     private final long myDurationId;
/*     */     private final long myStartTs;
/*     */     
/*     */     Duration(long stringId, long durationId, long startTs) {
/* 178 */       this.myStringId = stringId;
/* 179 */       this.myDurationId = durationId;
/* 180 */       this.myStartTs = startTs;
/*     */     }
/*     */     
/*     */     public long getStringId() {
/* 184 */       return this.myStringId;
/*     */     }
/*     */     
/*     */     public long getDurationId() {
/* 188 */       return this.myDurationId;
/*     */     }
/*     */     
/*     */     public long getStartTs() {
/* 192 */       return this.myStartTs;
/*     */     }
/*     */   }
/*     */ }


/* Location:              C:\Program Files\JetBrains\WebStorm 2021.1\plugins\NodeJS\lib\NodeJS.jar!\com\jetbrains\nodejs\run\profile\cpu\v8log\calculation\V8LogStackDurationWorker.class
 * Java compiler version: 11 (55.0)
 * JD-Core Version:       1.1.3
 */