/*     */ package org.bipolar.run.profile.cpu.v8log.reading;
/*     */ 
/*     */ import com.intellij.openapi.util.Pair;
/*     */ import com.intellij.openapi.util.io.FileUtil;
/*     */ import com.intellij.openapi.vfs.LocalFileSystem;
/*     */ import com.intellij.util.PairConsumer;
/*     */ import com.intellij.util.Processor;
/*     */ import com.intellij.util.containers.SLRUMap;
/*     */ import org.bipolar.run.profile.cpu.calculation.V8ProfileLine;
/*     */ import org.bipolar.run.profile.cpu.v8log.calculation.TickIndexer;
/*     */ import org.bipolar.run.profile.cpu.v8log.calculation.TimeDistribution;
/*     */ import org.bipolar.run.profile.cpu.v8log.calculation.TimerEventsReader;
/*     */ import org.bipolar.run.profile.cpu.v8log.calculation.V8LogIndexesWriter;
/*     */ import org.bipolar.run.profile.cpu.v8log.calculation.V8OverviewScalesReader;
/*     */ import org.bipolar.run.profile.cpu.v8log.calculation.V8Profile;
/*     */ import org.bipolar.run.profile.cpu.v8log.calculation.V8ProfileCallback;
/*     */ import org.bipolar.run.profile.cpu.v8log.calculation.V8TickProcessor;
/*     */ import org.bipolar.run.profile.cpu.v8log.data.EventsStripeData;
/*     */ import org.bipolar.run.profile.cpu.v8log.data.FlatTopCalls;
/*     */ import org.bipolar.run.profile.cpu.v8log.data.V8CodeScope;
/*     */ import org.bipolar.run.profile.heap.CompositeCloseable;
/*     */ import org.bipolar.run.profile.heap.calculation.ByteArrayWrapper;
/*     */ import org.bipolar.run.profile.heap.io.IntegerRawSerializer;
/*     */ import org.bipolar.run.profile.heap.io.LongRawSerializer;
/*     */ import org.bipolar.run.profile.heap.io.RandomRawReader;
/*     */ import org.bipolar.run.profile.heap.io.RawSerializer;
/*     */ import org.bipolar.run.profile.heap.io.SequentialRawReader;
/*     */ import org.bipolar.run.profile.heap.io.reverse.LinksReader;
/*     */ import org.bipolar.run.profile.heap.io.reverse.LinksReaderFactory;
/*     */ import org.bipolar.util.CloseableThrowableConsumer;
/*     */ import org.bipolar.util.CloseableThrowableProcessor;
/*     */ import java.io.BufferedOutputStream;
/*     */ import java.io.ByteArrayInputStream;
/*     */ import java.io.Closeable;
/*     */ import java.io.DataInputStream;
/*     */ import java.io.File;
/*     */ import java.io.FileOutputStream;
/*     */ import java.io.IOException;
/*     */ import java.io.PrintStream;
/*     */ import java.util.ArrayDeque;
/*     */ import java.util.ArrayList;
/*     */ import java.util.Collections;
/*     */ import java.util.Iterator;
/*     */ import java.util.List;
/*     */ import java.util.Map;
/*     */
import org.jetbrains.annotations.NotNull;
/*     */ import org.jetbrains.annotations.Nullable;
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
/*     */ public class V8LogCachingReader
/*     */ {
/*     */   private static final long MAX_OVERVIEW_POINTS = 2000L;
/*     */   public static final int TICK_INDEX_STEP = 100000;
/*     */   public static final long GC_STRING_ID = 0L;
/*     */   public static final long UNKNOWN_ID = 1L;
/*     */   public static final long STACKTRACE_CUT = 2L;
/*     */   private final ByteArrayWrapper myDigest;
/*     */   private final File myHeaderFile;
/*     */   private final File myStackSizeFile;
/*     */   private final List<File> myOverviewFiles;
/*     */   private final LinksReaderFactory<Long> myDurationFactory;
/*     */   private final V8ProfileLine myBottomUp;
/*     */   private final V8ProfileLine myTopDown;
/*     */   private final CompositeCloseable myResources;
/*     */   private final File myTimeFile;
/*     */   private final LinksReaderFactory<Long> myStackReader;
/*     */   private final LinksReaderFactory<String> myStringsReaderFactory;
/*     */   private final double myOneTickApprox;
/*     */   private final SequentialRawReader<Long> myTimeReader;
/*     */   private final LinksReader<String> myStringsReader;
/*     */   private final RandomRawReader<Long> myRandomTimeReader;
/*     */   private final LinksReader<Long> myRandomStackReader;
/*     */   private final LinksReader<Long> myRandomDurationReader;
/*     */   private final V8OverviewScalesReader myOverviewScalesReader;
/*     */   
/*     */   public V8LogCachingReader cloneReader(long fromTs, long toTs) throws IOException {
/*  85 */     V8Profile recalculated = recalculateProfile(fromTs, toTs);
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*  92 */     V8LogCachingReader reader = new V8LogCachingReader(this.myDigest, this.myV8LogFile, this.myHeaderFile, this.myTimeFile, this.myStackSizeFile, this.myStackReader, this.myStringsReaderFactory, this.myTimerEventsReader.getEventsReader(), this.myOverviewFiles, this.myDurationFactory, recalculated.getBottomUpRoot(), recalculated.getTopDownRoot(), recalculated.getFlatTopCallsRoot(), new CompositeCloseable(), this.myTickIndexer, this.myTimerEventsReader.getEventsTickIndexer(), this.myTimerEventsReader.getEventsEndTickIndexer(), this.myCodeTypes, this.myDistributionFile, this.mySelfDistributionFile);
/*  93 */     reader.setNumIdleTicks(recalculated.getIdleTicks());
/*  94 */     reader.setNumGcTicks(recalculated.getGcTicks());
/*  95 */     reader.setMaxStackSize(recalculated.getMaxStackSize());
/*  96 */     reader.setNumTicks(recalculated.getNumTicks());
/*  97 */     return reader; } private final File myV8LogFile; private final FlatTopCalls myFlat; @NotNull
/*     */   private final File myDistributionFile; @NotNull
/*     */   private final File mySelfDistributionFile; private final SLRUMap<Long, TimeDistribution> myDistributionCache; private final RandomRawReader<TimeDistribution> myDistributionReader; private final RandomRawReader<TimeDistribution> mySelfDistributionReader; private final SLRUMap<Long, TimeDistribution> mySelfDistributionCache; private long myLastTs; private long myNumTicks; private long myNumIdleTicks; private long myNumGcTicks; @NotNull
/*     */   private final TickIndexer myTickIndexer; @NotNull
/* 101 */   final TimerEventsReader myTimerEventsReader; private final SequentialRawReader<Integer> myStackSizeReader; private final Map<String, V8TickProcessor.CodeType> myCodeTypes; private int myMaxStackSize; private final SLRUMap<Long, String> myStringCache; private final SLRUMap<Integer, List<Long>> myStackCache; private final SLRUMap<Integer, List<Long>> myDurationCache; private final SLRUMap<Long, V8CodeScope> myCodeScopeCache; private final Object myLock; private final LinksReader<Long> mySequentialDurationReader; public V8LogCachingReader cloneReader() throws IOException { return new V8LogCachingReader(this.myDigest, this.myV8LogFile, this.myHeaderFile, this.myTimeFile, this.myStackSizeFile, this.myStackReader, this.myStringsReaderFactory, this.myTimerEventsReader
/* 102 */         .getEventsReader(), this.myOverviewFiles, this.myDurationFactory, this.myBottomUp, this.myTopDown, this.myFlat, new CompositeCloseable(), this.myTickIndexer, this.myTimerEventsReader
/*     */         
/* 104 */         .getEventsTickIndexer(), this.myTimerEventsReader
/* 105 */         .getEventsEndTickIndexer(), this.myCodeTypes, this.myDistributionFile, this.mySelfDistributionFile); }
/*     */ 
/*     */   
/*     */   private V8Profile recalculateProfile(long fromTs, long toTs) throws IOException {
/* 109 */     StackReader stackReader = new StackReader(fromTs, toTs);
/* 110 */     stackReader.execute();
/* 111 */     List<Long> times = stackReader.getTimes();
/* 112 */     List<List<Long>> stackValues = stackReader.getStack();
/* 113 */     V8Profile profile = new V8Profile(V8ProfileCallback.EMPTY);
/* 114 */     int gcTicks = 0;
/* 115 */     int unaccountedTicks = 0;
/* 116 */     int idleTicks = 0;
/* 117 */     int maxStackSize = 0;
/* 118 */     for (int i = 0; i < times.size(); i++) {
/* 119 */       Long ts = times.get(i);
/* 120 */       if (ts.longValue() >= fromTs && ts.longValue() <= toTs) {
/*     */         
/* 122 */         List<Long> currentStack = stackValues.get(i);
/* 123 */         Collections.reverse(currentStack);
/* 124 */         if (!currentStack.isEmpty())
/* 125 */         { Iterator<Long> iterator = currentStack.iterator();
/* 126 */           while (iterator.hasNext()) {
/* 127 */             Long id = iterator.next();
/* 128 */             if (id.longValue() == 0L) {
/* 129 */               gcTicks++;
/* 130 */               iterator.remove(); continue;
/*     */             } 
/* 132 */             if (id.longValue() == 1L) {
/* 133 */               unaccountedTicks++;
/* 134 */               iterator.remove();
/*     */             } 
/*     */           }  }
/* 137 */         else { idleTicks++; }
/* 138 */          maxStackSize = Math.max(maxStackSize, currentStack.size());
/* 139 */         profile.recordTick(currentStack);
/*     */       } 
/* 141 */     }  profile.postProcess(gcTicks, unaccountedTicks, idleTicks, this.myCodeTypes, id -> getStringById(id.longValue()), maxStackSize);
/* 142 */     return profile;
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
/*     */   public V8LogCachingReader(@NotNull ByteArrayWrapper digest, File v8LogFile, File headerFile, File timeFile, File stackSizeFile, LinksReaderFactory<Long> stackReader, LinksReaderFactory<String> stringsReader, LinksReaderFactory<V8LogIndexesWriter.TimerEvent> reader, List<File> overviewFiles, LinksReaderFactory<Long> durationFactory, V8ProfileLine bottomUp, V8ProfileLine topDown, FlatTopCalls flat, CompositeCloseable resources, @NotNull TickIndexer eventsIndexer, @NotNull TickIndexer eventsEndIndexer, @NotNull Map<String, V8TickProcessor.CodeType> codeTypes, @NotNull File distributionFile, @NotNull File selfDistributionFile) throws IOException {
/* 156 */     this(digest, v8LogFile, headerFile, timeFile, stackSizeFile, stackReader, stringsReader, reader, overviewFiles, durationFactory, bottomUp, topDown, flat, resources, null, eventsIndexer, eventsEndIndexer, codeTypes, distributionFile, selfDistributionFile);
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
/*     */   private V8LogCachingReader(@NotNull ByteArrayWrapper digest, File v8LogFile, File headerFile, File timeFile, File stackSizeFile, LinksReaderFactory<Long> stackReader, LinksReaderFactory<String> stringsReader, LinksReaderFactory<V8LogIndexesWriter.TimerEvent> timerEventLinksReaderFactory, List<File> overviewFiles, LinksReaderFactory<Long> durationFactory, V8ProfileLine bottomUp, V8ProfileLine topDown, FlatTopCalls flat, CompositeCloseable resources, @Nullable TickIndexer tickIndexer, @NotNull TickIndexer eventsIndexer, @NotNull TickIndexer eventsEndIndexer, @NotNull Map<String, V8TickProcessor.CodeType> codeTypes, @NotNull File distributionFile, @NotNull File selfDistributionFile) throws IOException {
/* 172 */     this.myV8LogFile = v8LogFile;
/* 173 */     this.myFlat = flat;
/* 174 */     this.myDistributionFile = distributionFile;
/* 175 */     this.mySelfDistributionFile = selfDistributionFile;
/* 176 */     this.myLock = new Object();
/* 177 */     this.myDigest = digest;
/* 178 */     this.myHeaderFile = headerFile;
/* 179 */     this.myStackSizeFile = stackSizeFile;
/* 180 */     this.myOverviewFiles = overviewFiles;
/* 181 */     this.myDurationFactory = durationFactory;
/* 182 */     this.myBottomUp = bottomUp;
/* 183 */     this.myTopDown = topDown;
/* 184 */     this.myResources = resources;
/* 185 */     this.myCodeTypes = codeTypes;
/* 186 */     this.myStringCache = new SLRUMap(1024, 1024);
/* 187 */     this.myStackCache = new SLRUMap(1024, 1024);
/* 188 */     this.myDurationCache = new SLRUMap(1024, 1024);
/* 189 */     this.myCodeScopeCache = new SLRUMap(2048, 2048);
/* 190 */     this.myDistributionCache = new SLRUMap(32, 32);
/* 191 */     this.mySelfDistributionCache = new SLRUMap(32, 32);
/* 192 */     readHeader();
/* 193 */     this.myOneTickApprox = this.myLastTs / this.myNumTicks;
/* 194 */     this.myTimeFile = timeFile;
/* 195 */     this.myStackReader = stackReader;
/* 196 */     this.myStringsReaderFactory = stringsReader;
/* 197 */     this.myStringsReader = (LinksReader<String>)resources.register((Closeable)this.myStringsReaderFactory.create(false));
/* 198 */     if (tickIndexer == null) {
/* 199 */       TickIndexer indexer = new TickIndexer(100000L);
/* 200 */       createTickIndex(indexer);
/* 201 */       this.myTickIndexer = indexer;
/*     */     } else {
/* 203 */       this.myTickIndexer = tickIndexer;
/*     */     } 
/* 205 */     this.myTimerEventsReader = new TimerEventsReader(resources, timerEventLinksReaderFactory, eventsIndexer, eventsEndIndexer);
/*     */     
/* 207 */     this.myStackSizeReader = (SequentialRawReader<Integer>)resources.register((Closeable)new SequentialRawReader(this.myStackSizeFile, (RawSerializer)new IntegerRawSerializer(), this.myNumTicks));
/* 208 */     this.myTimeReader = (SequentialRawReader<Long>)resources.register((Closeable)new SequentialRawReader(this.myTimeFile, (RawSerializer)new LongRawSerializer(), this.myNumTicks));
/*     */     
/* 210 */     this.myRandomTimeReader = (RandomRawReader<Long>)resources.register((Closeable)new RandomRawReader(this.myTimeFile, (RawSerializer)new LongRawSerializer()));
/* 211 */     this.myRandomStackReader = (LinksReader<Long>)resources.register((Closeable)this.myStackReader.create(false));
/* 212 */     this.myRandomDurationReader = (LinksReader<Long>)resources.register((Closeable)this.myDurationFactory.create(false));
/* 213 */     this.mySequentialDurationReader = (LinksReader<Long>)resources.register((Closeable)this.myDurationFactory.create(true));
/* 214 */     this.myOverviewScalesReader = new V8OverviewScalesReader(this.myOverviewFiles, resources, 50000L);
/*     */     
/* 216 */     this.myDistributionReader = (RandomRawReader<TimeDistribution>)resources.register((Closeable)new RandomRawReader(distributionFile, 
/* 217 */           TimeDistribution.getSerializer(TimeDistribution.STANDARD)));
/* 218 */     this.mySelfDistributionReader = (RandomRawReader<TimeDistribution>)resources.register((Closeable)new RandomRawReader(selfDistributionFile, 
/* 219 */           TimeDistribution.getSerializer(TimeDistribution.STANDARD)));
/*     */   }
/*     */   
/*     */   public ByteArrayWrapper getDigest() {
/* 223 */     return this.myDigest;
/*     */   }
/*     */   
/*     */   public File getV8LogFile() {
/* 227 */     return this.myV8LogFile;
/*     */   }
/*     */   
/*     */   public CompositeCloseable getResources() {
/* 231 */     return this.myResources;
/*     */   }
/*     */   
/*     */   private void createTickIndex(final TickIndexer indexer) throws IOException {
/* 235 */     (new SequentialRawReader(this.myTimeFile, (RawSerializer)new LongRawSerializer(), this.myNumTicks)).iterate(new CloseableThrowableConsumer<Long, IOException>()
/*     */         {
/*     */           public void close() throws IOException {}
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */           
/*     */           public void consume(Long tick) throws IOException {
/* 244 */             indexer.nextTick(tick.longValue());
/*     */           }
/*     */         });
/*     */   }
/*     */ 
/*     */   
/*     */   private void readHeader() throws IOException {
/* 251 */     ByteArrayInputStream is = new ByteArrayInputStream(FileUtil.loadFileBytes(this.myHeaderFile));
/* 252 */     DataInputStream dis = new DataInputStream(is);
/* 253 */     this.myLastTs = dis.readLong();
/* 254 */     this.myNumTicks = dis.readLong();
/* 255 */     this.myMaxStackSize = dis.readInt();
/* 256 */     this.myNumGcTicks = dis.readInt();
/* 257 */     this.myNumIdleTicks = dis.readInt();
/*     */   }
/*     */   
/*     */   public long getLastTs() {
/* 261 */     return this.myLastTs;
/*     */   }
/*     */   
/*     */   public long getLastTick() {
/* 265 */     return this.myTickIndexer.getLastTick();
/*     */   }
/*     */   
/*     */   public long getNumTicks() {
/* 269 */     return this.myNumTicks;
/*     */   }
/*     */   
/*     */   public int getMaxStackSize() {
/* 273 */     return this.myMaxStackSize;
/*     */   }
/*     */   
/*     */   public long getNumIdleTicks() {
/* 277 */     return this.myNumIdleTicks;
/*     */   }
/*     */   
/*     */   public long getNumGcTicks() {
/* 281 */     return this.myNumGcTicks;
/*     */   }
/*     */   
/*     */   public V8ProfileLine getTopDown() {
/* 285 */     return this.myTopDown;
/*     */   }
/*     */   
/*     */   public V8ProfileLine getBottomUp() {
/* 289 */     return this.myBottomUp;
/*     */   }
/*     */   
/*     */   public FlatTopCalls getFlat() {
/* 293 */     return this.myFlat;
/*     */   }
/*     */   
/*     */   public void setNumIdleTicks(long numIdleTicks) {
/* 297 */     this.myNumIdleTicks = numIdleTicks;
/*     */   }
/*     */   
/*     */   public void setNumGcTicks(long numGcTicks) {
/* 301 */     this.myNumGcTicks = numGcTicks;
/*     */   }
/*     */   
/*     */   public void setNumTicks(long numTicks) {
/* 305 */     this.myNumTicks = numTicks;
/*     */   }
/*     */   
/*     */   public void setMaxStackSize(int maxStackSize) {
/* 309 */     this.myMaxStackSize = maxStackSize;
/*     */   }
/*     */   
/*     */   @NotNull
/*     */   public TickIndexer getEventsTickIndexer() {
/* 314 */     if (this.myTimerEventsReader.getEventsTickIndexer() == null) $$$reportNull$$$0(12);  return this.myTimerEventsReader.getEventsTickIndexer();
/*     */   }
/*     */   
/*     */   @NotNull
/*     */   public TickIndexer getEventsEndTickIndexer() {
/* 319 */     if (this.myTimerEventsReader.getEventsEndTickIndexer() == null) $$$reportNull$$$0(13);  return this.myTimerEventsReader.getEventsEndTickIndexer();
/*     */   }
/*     */   
/*     */   public Map<String, V8TickProcessor.CodeType> getCodeTypes() {
/* 323 */     return this.myCodeTypes;
/*     */   }
/*     */   
/*     */   public EventsStripeData getTimerEvents(long from, long to) throws IOException {
/* 327 */     return this.myTimerEventsReader.getTimerEvents(from, to);
/*     */   }
/*     */   
/*     */   public List<Pair<Long, Integer>> getStackOverview(long from, long to) throws IOException {
/* 331 */     synchronized (this.myLock) {
/* 332 */       from = (from < 0L) ? 0L : from;
/* 333 */       to = (to < 0L || to > this.myTickIndexer.getLastTick()) ? this.myTickIndexer.getLastTick() : to;
/*     */       
/* 335 */       List<Pair<Long, Integer>> fromScales = this.myOverviewScalesReader.getStackOverview(from, to);
/* 336 */       if (fromScales != null) return fromScales;
/*     */       
/* 338 */       double numPoints = (to - from) / this.myOneTickApprox;
/* 339 */       if (numPoints > 2000.0D) {
/* 340 */         List<Pair<Long, Integer>> mostDetailed = this.myOverviewScalesReader.getMostDetailedOverview(from, to);
/* 341 */         if (mostDetailed != null) {
/* 342 */           return mostDetailed;
/*     */         }
/*     */       } 
/*     */       
/* 346 */       if (this.myTickIndexer.isEmpty()) return Collections.emptyList(); 
/* 347 */       Integer fromRecord = this.myTickIndexer.getFloorIndexFor(from);
/* 348 */       Integer toRecord = this.myTickIndexer.getCeilIndexFor(to);
/*     */       
/* 350 */       this.myStackSizeReader.reset();
/* 351 */       this.myStackSizeReader.skip(fromRecord.intValue());
/* 352 */       this.myTimeReader.reset();
/* 353 */       this.myTimeReader.skip(fromRecord.intValue());
/*     */       
/* 355 */       List<Pair<Long, Integer>> result = new ArrayList<>();
/* 356 */       for (int i = 0; i < toRecord.intValue() - fromRecord.intValue() + 1 && this.myStackSizeReader.hasNext(); i++) {
/* 357 */         Integer stackSize = (Integer)this.myStackSizeReader.read();
/* 358 */         Long time = (Long)this.myTimeReader.read();
/* 359 */         result.add(Pair.create(time, stackSize));
/*     */       } 
/*     */       
/* 362 */       return result;
/*     */     } 
/*     */   }
/*     */   
/*     */   public String getStringById(long id) throws IOException {
/* 367 */     synchronized (this.myLock) {
/* 368 */       String string = (String)this.myStringCache.get(Long.valueOf(id));
/* 369 */       if (string != null) return string; 
/* 370 */       string = (String)this.myStringsReader.readRandomLen(id);
/* 371 */       this.myStringCache.put(Long.valueOf(id), string);
/* 372 */       return string;
/*     */     } 
/*     */   }
/*     */   
/*     */   public V8CodeScope getCodeScopeByStringId(long id) throws IOException {
/* 377 */     synchronized (this.myLock) {
/*     */       
/* 379 */       V8CodeScope scope = (V8CodeScope)this.myCodeScopeCache.get(Long.valueOf(id));
/* 380 */       if (scope != null) return scope; 
/* 381 */       if (0L == id) {
/* 382 */         return V8CodeScope.gc;
/*     */       }
/* 384 */       if (2L == id) return V8CodeScope.stackTraceCut; 
/* 385 */       String stringById = getStringById(id);
/* 386 */       if (stringById.contains(" native ")) {
/* 387 */         this.myCodeScopeCache.put(Long.valueOf(id), V8CodeScope.v8);
/* 388 */         return V8CodeScope.v8;
/*     */       } 
/* 390 */       if (stringById.startsWith("LazyCompile: ") || stringById.startsWith("Function: ")) {
/* 391 */         String[] split = stringById.split(" ");
/* 392 */         for (int i = 1; i < split.length; i++) {
/* 393 */           String s = split[i];
/* 394 */           int idx = s.lastIndexOf(":");
/* 395 */           if (idx >= 0) {
/* 396 */             String path = s.substring(0, idx);
/* 397 */             if (path.contains("\\") || path.contains("/")) {
/* 398 */               this.myCodeScopeCache.put(Long.valueOf(id), V8CodeScope.local);
/* 399 */               return V8CodeScope.local;
/*     */             } 
/*     */             
/* 402 */             this.myCodeScopeCache.put(Long.valueOf(id), V8CodeScope.node);
/* 403 */             return V8CodeScope.node;
/*     */           } 
/*     */         } 
/*     */         
/* 407 */         this.myCodeScopeCache.put(Long.valueOf(id), V8CodeScope.v8);
/* 408 */         return V8CodeScope.v8;
/*     */       } 
/* 410 */       this.myCodeScopeCache.put(Long.valueOf(id), V8CodeScope.v8);
/* 411 */       return V8CodeScope.v8;
/*     */     } 
/*     */   }
/*     */   
/*     */   public List<Long> getDurationList(int idx) throws IOException {
/* 416 */     synchronized (this.myLock) {
/* 417 */       List<Long> longs = (List<Long>)this.myDurationCache.get(Integer.valueOf(idx));
/* 418 */       if (longs != null) return longs; 
/* 419 */       this.myDurationCache.put(Integer.valueOf(idx), longs = new ArrayList<>());
/* 420 */       List<Long> finalLongs = longs;
/* 421 */       this.myRandomDurationReader.read(idx, aLong -> {
/*     */             finalLongs.add(aLong);
/*     */             return true;
/*     */           });
/* 425 */       return longs;
/*     */     } 
/*     */   }
/*     */   
/*     */   public List<Long> getStackForTsIdx(int idx) throws IOException {
/* 430 */     synchronized (this.myLock) {
/* 431 */       List<Long> list = (List<Long>)this.myStackCache.get(Integer.valueOf(idx));
/* 432 */       if (list != null) return list; 
/* 433 */       this.myStackCache.put(Integer.valueOf(idx), list = new ArrayList<>());
/* 434 */       List<Long> finalList = list;
/* 435 */       this.myRandomStackReader.read(idx, stackElement -> {
/*     */             if (stackElement.longValue() == 1L)
/*     */               return true;  finalList.add(stackElement);
/*     */             return true;
/*     */           });
/* 440 */       return list;
/*     */     } 
/*     */   }
/*     */   
/*     */   public FlameData getStack(long from, long to) throws IOException {
/* 445 */     synchronized (this.myTickIndexer) {
/* 446 */       from = (from < 0L) ? 0L : from;
/* 447 */       to = (to < 0L || to > this.myTickIndexer.getLastTick()) ? this.myTickIndexer.getLastTick() : to;
/*     */ 
/*     */       
/* 450 */       double numPoints = (to - from) / this.myOneTickApprox;
/* 451 */       if (numPoints > 2000.0D && to - from > 2000000L) return null;
/*     */       
/* 453 */       StackReader stackReader = new StackReader(from, to);
/* 454 */       stackReader.execute();
/* 455 */       return stackReader.getFlameData();
/*     */     } 
/*     */   }
/*     */   
/*     */   public void createStatisticalReport(File file) throws IOException {
/* 460 */     FileOutputStream fos = new FileOutputStream(file);
/* 461 */     BufferedOutputStream bos = new BufferedOutputStream(fos);
/* 462 */     PrintStream ps = new PrintStream(bos);
/*     */     try {
/* 464 */       List<V8ProfileLine> unknown = this.myFlat.getUnknown();
/* 465 */       ps.format("Statistical profiling result from %s, (%d ticks, %d unaccounted elements).\n", new Object[] { this.myV8LogFile.getAbsolutePath(), Long.valueOf(this.myNumTicks), 
/* 466 */             Integer.valueOf(unknown.isEmpty() ? 0 : ((V8ProfileLine)unknown.get(0)).getTotalTicks()) });
/*     */       
/* 468 */       printFlat(ps);
/*     */       
/* 470 */       ps.println();
/* 471 */       printBottomUp(ps);
/* 472 */       ps.println();
/* 473 */       printTopDown(ps);
/*     */     } finally {
/* 475 */       ps.close();
/*     */     } 
/* 477 */     LocalFileSystem.getInstance().refreshAndFindFileByIoFile(file);
/*     */   }
/*     */   
/*     */   private void printTopDown(PrintStream ps) {
/* 481 */     ps.println("[Top down (heavy) profile]:\nNote: callees occupying less than 0.1% are not shown.\n\n inclusive          self           name\n ticks   total      ticks   total\n");
/*     */ 
/*     */ 
/*     */     
/* 485 */     dfs(this.myTopDown, (line, integer) -> {
/*     */           ps.format("%7d  %6.1f%%  %7d  %6.1f%%  ", new Object[] { Integer.valueOf(line.getTotalTicks()), Double.valueOf(line.getTotalTensPercent() / 10.0D), Integer.valueOf(line.getSelfTicks()), Double.valueOf(line.getSelfTensPercent() / 10.0D) });
/*     */           for (int i = 0; i < integer.intValue(); i++) {
/*     */             ps.print("  ");
/*     */           }
/*     */           ps.println(line.getPresentation(true));
/*     */         });
/*     */   }
/*     */ 
/*     */   
/*     */   private void printBottomUp(PrintStream ps) {
/* 496 */     ps.format("[Bottom up (heavy) profile]:\nNote: percentage shows a share of a particular caller in the total amount of its parent calls.\n", new Object[0]);
/*     */     
/* 498 */     ps.format("Callers occupying less than 0.1%% are not shown.\n", new Object[0]);
/* 499 */     ps.println("\n ticks    parent  name\n");
/* 500 */     dfs(this.myBottomUp, (line, integer) -> {
/*     */           ps.format("%7d  %6.1f%%  ", new Object[] { Integer.valueOf(line.getTotalTicks()), Double.valueOf(line.getTotalTensPercent() / 10.0D) });
/*     */           for (int i = 0; i < integer.intValue(); i++) {
/*     */             ps.print("  ");
/*     */           }
/*     */           ps.println(line.getPresentation(true));
/*     */         });
/*     */   }
/*     */   
/*     */   private static void dfs(V8ProfileLine root, PairConsumer<V8ProfileLine, Integer> consumer) {
/* 510 */     ArrayDeque<Pair<V8ProfileLine, Integer>> queue = new ArrayDeque<>();
/* 511 */     for (V8ProfileLine line : root.getChildren()) {
/* 512 */       queue.add(Pair.create(line, Integer.valueOf(0)));
/*     */     }
/* 514 */     while (!queue.isEmpty()) {
/* 515 */       Pair<V8ProfileLine, Integer> current = queue.removeFirst();
/* 516 */       if (((V8ProfileLine)current.getFirst()).getTotalTensPercent() < 1)
/*     */         continue; 
/* 518 */       consumer.consume(current.getFirst(), current.getSecond());
/* 519 */       ArrayList<V8ProfileLine> list = new ArrayList<>(((V8ProfileLine)current.getFirst()).getChildren());
/* 520 */       Collections.reverse(list);
/* 521 */       for (V8ProfileLine line : list) {
/* 522 */         queue.addFirst(Pair.create(line, Integer.valueOf(((Integer)current.getSecond()).intValue() + 1)));
/*     */       }
/*     */     } 
/*     */   }
/*     */   
/*     */   private void printFlat(PrintStream ps) {
/* 528 */     List<Pair<String, List<V8ProfileLine>>> presentation = this.myFlat.createPresentation();
/* 529 */     ps.println("\n[Top calls]:");
/* 530 */     for (Pair<String, List<V8ProfileLine>> pair : presentation) {
/* 531 */       ps.println();
/* 532 */       ps.format("[%s]:\nticks  total   name\n", new Object[] { pair.getFirst() });
/* 533 */       for (V8ProfileLine line : pair.getSecond()) {
/* 534 */         if (line.getTotalTensPercent() < 1)
/* 535 */           continue;  ps.format("%5d %5.1f%%   %s\n", new Object[] { Integer.valueOf(line.getTotalTicks()), Double.valueOf(line.getTotalTensPercent() / 10.0D), line.getPresentation(true) });
/*     */       } 
/*     */     } 
/*     */   }
/*     */   
/*     */   private class StackReader {
/*     */     private final long myFromTs;
/*     */     private final long myToTs;
/*     */     private final int myFromRecord;
/*     */     private final int myToRecord;
/*     */     private FlameData myFlameData;
/*     */     private List<List<Long>> myStack;
/*     */     private List<Long> myTimes;
/*     */     
/*     */     StackReader(long fromTs, long toTs) {
/* 550 */       this.myFromTs = fromTs;
/* 551 */       this.myToTs = toTs;
/* 552 */       this.myFromRecord = V8LogCachingReader.this.myTickIndexer.getFloorIndexFor(fromTs).intValue();
/* 553 */       this.myToRecord = V8LogCachingReader.this.myTickIndexer.getCeilIndexFor(toTs).intValue();
/*     */     }
/*     */     
/*     */     public void execute() throws IOException {
/* 557 */       V8LogCachingReader.this.myTimeReader.reset();
/* 558 */       if (this.myFromRecord != 0) {
/* 559 */         V8LogCachingReader.this.myTimeReader.skip(this.myFromRecord);
/*     */       }
/* 561 */       this.myTimes = new ArrayList<>();
/* 562 */       V8LogCachingReader.this.myTimeReader.iterate(new CloseableThrowableProcessor<Long, IOException>() {
/* 563 */             int cnt = V8LogCachingReader.StackReader.this.myFromRecord;
/*     */ 
/*     */ 
/*     */             
/*     */             public void close() throws IOException {}
/*     */ 
/*     */             
/*     */             public boolean process(Long ts) throws IOException {
/* 571 */               if (this.cnt <= V8LogCachingReader.StackReader.this.myToRecord)
/*     */               {
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */                 
/* 582 */                 V8LogCachingReader.StackReader.this.myTimes.add(ts);
/*     */               }
/* 584 */               this.cnt++;
/* 585 */               return (this.cnt <= V8LogCachingReader.StackReader.this.myToRecord);
/*     */             }
/*     */           });
/*     */       
/* 589 */       this.myStack = new ArrayList<>();
/* 590 */       this.myFlameData = new FlameData(this.myFromRecord, this.myTimes);
/* 591 */       LinksReader<Long> reader = V8LogCachingReader.this.myStackReader.create(true);
/* 592 */       if (this.myFromRecord != 0) {
/* 593 */         reader.skip(this.myFromRecord, false);
/*     */       }
/* 595 */       reader.iterate(new Processor<List<Long>>() {
/* 596 */             int cnt = V8LogCachingReader.StackReader.this.myFromRecord;
/* 597 */             int idxTimes = 0;
/*     */ 
/*     */             
/*     */             public boolean process(List<Long> longs) {
/* 601 */               if (this.idxTimes < V8LogCachingReader.StackReader.this.myTimes.size()) {
/* 602 */                 longs = FlameData.filterUnknown(longs);
/* 603 */                 V8LogCachingReader.StackReader.this.myStack.add(longs);
/* 604 */                 V8LogCachingReader.StackReader.this.myFlameData.tick(longs);
/*     */               } 
/* 606 */               this.cnt++;
/* 607 */               this.idxTimes++;
/* 608 */               return (this.cnt <= V8LogCachingReader.StackReader.this.myToRecord);
/*     */             }
/*     */           });
/* 611 */       this.myFlameData.finish();
/*     */     }
/*     */     
/*     */     public List<List<Long>> getStack() {
/* 615 */       return this.myStack;
/*     */     }
/*     */     
/*     */     public List<Long> getTimes() {
/* 619 */       return this.myTimes;
/*     */     }
/*     */     
/*     */     public FlameData getFlameData() {
/* 623 */       return this.myFlameData;
/*     */     } } class null implements CloseableThrowableProcessor<Long, IOException> { int cnt = V8LogCachingReader.StackReader.this.myFromRecord; public void close() throws IOException {} public boolean process(Long ts) throws IOException { if (this.cnt <= V8LogCachingReader.StackReader.this.myToRecord)
/*     */         V8LogCachingReader.StackReader.this.myTimes.add(ts); 
/*     */       this.cnt++;
/*     */       return (this.cnt <= V8LogCachingReader.StackReader.this.myToRecord); } }
/*     */   @Nullable
/* 629 */   public TimeDistribution getTimesDistribution(long stringId) throws IOException { return getDistributionImpl(stringId, this.myDistributionCache, this.myDistributionReader); }
/*     */ 
/*     */   
/*     */   @Nullable
/*     */   public TimeDistribution getSelfTimesDistribution(long stringId) throws IOException {
/* 634 */     return getDistributionImpl(stringId, this.mySelfDistributionCache, this.mySelfDistributionReader);
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   @Nullable
/*     */   private static TimeDistribution getDistributionImpl(long stringId, SLRUMap<Long, TimeDistribution> distributionCache, RandomRawReader<TimeDistribution> distributionReader) throws IOException {
/* 641 */     if (stringId < 0L) return null; 
/* 642 */     TimeDistribution distribution = (TimeDistribution)distributionCache.get(Long.valueOf(stringId));
/* 643 */     if (distribution != null) return distribution; 
/* 644 */     TimeDistribution read = (TimeDistribution)distributionReader.read(stringId);
/* 645 */     if (read == null) return null; 
/* 646 */     distributionCache.put(Long.valueOf(stringId), read);
/* 647 */     return read;
/*     */   }
/*     */   
/*     */   class null implements Processor<List<Long>> {
/*     */     int cnt;
/*     */     int idxTimes;
/*     */     
/*     */     null() {
/*     */       this.cnt = V8LogCachingReader.StackReader.this.myFromRecord;
/*     */       this.idxTimes = 0;
/*     */     }
/*     */     
/*     */     public boolean process(List<Long> longs) {
/*     */       if (this.idxTimes < V8LogCachingReader.StackReader.this.myTimes.size()) {
/*     */         longs = FlameData.filterUnknown(longs);
/*     */         V8LogCachingReader.StackReader.this.myStack.add(longs);
/*     */         V8LogCachingReader.StackReader.this.myFlameData.tick(longs);
/*     */       } 
/*     */       this.cnt++;
/*     */       this.idxTimes++;
/*     */       return (this.cnt <= V8LogCachingReader.StackReader.this.myToRecord);
/*     */     }
/*     */   }
/*     */ }


/* Location:              C:\Program Files\JetBrains\WebStorm 2021.1\plugins\NodeJS\lib\NodeJS.jar!\com\jetbrains\nodejs\run\profile\cpu\v8log\reading\V8LogCachingReader.class
 * Java compiler version: 11 (55.0)
 * JD-Core Version:       1.1.3
 */