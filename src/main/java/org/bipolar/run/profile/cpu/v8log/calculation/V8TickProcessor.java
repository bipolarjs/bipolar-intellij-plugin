/*     */ package org.bipolar.run.profile.cpu.v8log.calculation;
/*     */ import com.intellij.openapi.diagnostic.Logger;
/*     */ import com.intellij.openapi.progress.ProgressManager;
/*     */ import com.intellij.openapi.util.text.StringUtil;
/*     */ import com.intellij.util.BeforeAfter;
/*     */ import com.intellij.util.ThrowableConsumer;
/*     */ import com.intellij.util.containers.BidirectionalMap;
/*     */ import org.bipolar.NodeJSBundle;
/*     */ import org.bipolar.run.profile.cpu.v8log.data.ArgumentType;
/*     */ import org.bipolar.run.profile.cpu.v8log.data.CodeState;
/*     */ import org.bipolar.run.profile.cpu.v8log.data.Counter;
/*     */ import org.bipolar.run.profile.cpu.v8log.data.V8EventType;
/*     */ import org.bipolar.run.profile.heap.CompositeCloseable;
/*     */ import org.bipolar.run.profile.heap.IndexFiles;
/*     */ import org.bipolar.run.profile.heap.calculation.ByteArrayWrapper;
/*     */ import java.io.File;
/*     */ import java.io.IOException;
/*     */ import java.math.BigInteger;
/*     */ import java.util.ArrayDeque;
/*     */ import java.util.ArrayList;
/*     */ import java.util.Collections;
/*     */ import java.util.Comparator;
/*     */ import java.util.HashMap;
/*     */ import java.util.List;
/*     */ import java.util.Map;
/*     */ import java.util.TreeMap;
/*     */
import org.jetbrains.annotations.NotNull;
/*     */ import org.jetbrains.annotations.Nullable;
/*     */ 
/*     */ public class V8TickProcessor implements V8LogReaderListener {
/*  31 */   private static final Logger LOG = Logger.getInstance(V8TickProcessor.class);
/*     */   
/*     */   private final File myFile;
/*     */   private final long myDistortionPerEntryNano;
/*     */   private final V8LogIndexesWriter myIndexesWriter;
/*     */   private long myDistortionNano;
/*     */   private final Map<String, ThrowableConsumer<List<String>, IOException>> myParsers;
/*     */   private final Map<TickCounterType, Counter> myTickCounters;
/*     */   private final V8Profile myProfile;
/*     */   private final Map<String, CodeType> myCodeTypes;
/*     */   private final Map<String, V8EventType> myEventTypes;
/*     */   private final Map<V8EventType, ArrayDeque<BeforeAfter<Long>>> myEventRanges;
/*  43 */   private long myLastTsMainThread = -1L;
/*  44 */   private long myLastTsBackgroundThread = -1L;
/*     */ 
/*     */ 
/*     */   
/*     */   private final ArrayDeque<V8EventType> myMainThreadEventStack;
/*     */ 
/*     */ 
/*     */   
/*     */   private final ArrayDeque<V8EventType> myBackgroundThreadEventStack;
/*     */ 
/*     */   
/*     */   private boolean myWithTimestamp = false;
/*     */ 
/*     */   
/*     */   private static final long myLastZippedTs = 0L;
/*     */ 
/*     */   
/*     */   private static final long myMinRangeLen = 0L;
/*     */ 
/*     */   
/*     */   private long myZipMoment;
/*     */ 
/*     */   
/*     */   private static final long zipInterval = 100000000L;
/*     */ 
/*     */   
/*     */   private static final long pauseTolerance = 5000L;
/*     */ 
/*     */ 
/*     */   
/*     */   public V8LogCachingReader execute(@NotNull CompositeCloseable resourses) throws IOException {
/*  75 */     if (resourses == null) $$$reportNull$$$0(3);  ProgressManager.progress(NodeJSBundle.message("progress.text.reading.v8.log", new Object[0]));
/*  76 */     (new V8LogReader(this.myFile, this.myParsers)).read();
/*  77 */     zipTimerEvents(true, 0L);
/*  78 */     ProgressManager.progress(NodeJSBundle.message("progress.text.recording.string.index", new Object[0]));
/*  79 */     BidirectionalMap<String, Long> map = this.myProfile.getCodeMap().getStringsMap();
/*  80 */     TreeMap<Long, String> ordered = new TreeMap<>();
/*  81 */     for (Map.Entry<String, Long> entry : (Iterable<Map.Entry<String, Long>>)map.entrySet()) {
/*  82 */       ordered.put(entry.getValue(), entry.getKey());
/*     */     }
/*  84 */     this.myIndexesWriter.writeStrings(ordered.values());
/*  85 */     this.myIndexesWriter.setTickCounters(((Counter)this.myTickCounters.get(TickCounterType.idle)).getCnt(), ((Counter)this.myTickCounters.get(TickCounterType.gc)).getCnt());
/*  86 */     this.myIndexesWriter.writeHeader(Math.max(this.myLastTsBackgroundThread, this.myLastTsMainThread));
/*  87 */     this.myIndexesWriter.close();
/*  88 */     ProgressManager.progress(NodeJSBundle.message("progress.text.calculating.calls.durations", new Object[0]));
/*  89 */     this.myIndexesWriter.getDurationWorker().recalculateDurationsWriteDistribution((Long)ordered.pollLastEntry().getKey());
/*  90 */     ProgressManager.progress(NodeJSBundle.message("progress.text.calculating.overview.approximations", new Object[0]));
/*  91 */     this.myIndexesWriter.postProcess();
/*     */     
/*  93 */     this.myProfile.postProcess(((Counter)this.myTickCounters.get(TickCounterType.gc)).getCnt(), ((Counter)this.myTickCounters.get(TickCounterType.unaccounted)).getCnt(), ((Counter)this.myTickCounters
/*  94 */         .get(TickCounterType.idle)).getCnt(), this.myCodeTypes, stringId -> this.myProfile.getCodeMap().getStringByCode(stringId.longValue()), this.myIndexesWriter
/*  95 */         .getMaxStackSize());
/*  96 */     return this.myIndexesWriter.createReader(resourses, this.myIndexesWriter.getDurationWorker().getDurationFactory(), this.myProfile
/*  97 */         .getBottomUpRoot(), this.myProfile.getTopDownRoot(), this.myProfile.getFlatTopCallsRoot(), this.myCodeTypes, this.myIndexesWriter
/*  98 */         .getDurationWorker().getDistributionFile(), this.myIndexesWriter
/*  99 */         .getDurationWorker().getSelfDistributionFile());
/*     */   }
/*     */   
/*     */   public Map<TickCounterType, Counter> getTickCounters() {
/* 103 */     return this.myTickCounters;
/*     */   }
/*     */   
/*     */   public V8Profile getProfile() {
/* 107 */     return this.myProfile;
/*     */   }
/*     */ 
/*     */   
/*     */   private void fillParsers() {
/* 112 */     this.myParsers.put("shared-library", processSharedLibrary());
/* 113 */     this.myParsers.put("code-creation", processCodeCreation());
/* 114 */     this.myParsers.put("code-move", processCodeMove());
/* 115 */     this.myParsers.put("code-delete", processCodeDelete());
/* 116 */     this.myParsers.put("sfi-move", processFunctionMove());
/* 117 */     this.myParsers.put("snapshot-pos", ParserBase.EMPTY);
/* 118 */     this.myParsers.put("tick", processTick());
/* 119 */     this.myParsers.put("heap-sample-begin", ParserBase.EMPTY);
/* 120 */     this.myParsers.put("heap-sample-end", ParserBase.EMPTY);
/* 121 */     this.myParsers.put("timer-event-start", processTimerEventStart());
/* 122 */     this.myParsers.put("timer-event-end", processTimerEventEnd());
/* 123 */     this.myParsers.put("profiler", ParserBase.EMPTY);
/* 124 */     this.myParsers.put("function-creation", ParserBase.EMPTY);
/* 125 */     this.myParsers.put("function-move", ParserBase.EMPTY);
/* 126 */     this.myParsers.put("function-delete", ParserBase.EMPTY);
/* 127 */     this.myParsers.put("heap-sample-item", ParserBase.EMPTY);
/* 128 */     this.myParsers.put("code-allocate", ParserBase.EMPTY);
/* 129 */     this.myParsers.put("begin-code-region", ParserBase.EMPTY);
/* 130 */     this.myParsers.put("end-code-region", ParserBase.EMPTY);
/* 131 */     this.myParsers.put("code-disable-optimization", ParserBase.EMPTY);
/* 132 */     this.myParsers.put("code-deopt", ParserBase.EMPTY);
/* 133 */     this.myParsers.put("current-time", ParserBase.EMPTY);
/* 134 */     this.myParsers.put("v8-version", new ParserBase(4, 4, new ArgumentType[] { ArgumentType.str, ArgumentType.number, ArgumentType.number, ArgumentType.number })
/*     */         {
/*     */           protected void process(List<String> strings) {
/* 137 */             long major = V8TickProcessor.parseNumber(strings.get(0));
/* 138 */             V8TickProcessor.this.myWithTimestamp = (major >= 6L);
/*     */           }
/*     */         });
/*     */   }
/*     */   
/*     */   private void fillEventTypesAndRanges() {
/* 144 */     for (V8EventType type : V8EventType.values())
/* 145 */       this.myEventRanges.put(type, new ArrayDeque<>()); 
/*     */   }
/*     */   
/*     */   public V8TickProcessor(@NotNull ByteArrayWrapper digest, @NotNull File file, long distortionPerEntryNano, @NotNull IndexFiles<V8LogIndexesWriter.Category> indexFiles) throws IOException {
/* 149 */     this.myZipMoment = 0L; this.myFile = file; this.myDistortionPerEntryNano = distortionPerEntryNano; this.myDistortionNano = 0L; this.myParsers = new HashMap<>(); this.myCodeTypes = new HashMap<>(); this.myTickCounters = new HashMap<>(); for (TickCounterType type : TickCounterType.values())
/*     */       this.myTickCounters.put(type, new Counter());  this.myProfile = new V8Profile(createCallback()); this.myIndexesWriter = new V8LogIndexesWriter(this.myFile, digest, (IndexFiles<V8LogIndexesWriter.Category>)SYNTHETIC_LOCAL_VARIABLE_5); fillParsers(); this.myEventTypes = new HashMap<>(); this.myEventRanges = new HashMap<>();
/*     */     fillEventTypesAndRanges();
/*     */     this.myMainThreadEventStack = new ArrayDeque<>();
/* 153 */     this.myBackgroundThreadEventStack = new ArrayDeque<>(); } private void zipTimerEvents(boolean force, long tick) throws IOException { if (!force) {
/* 154 */       if (this.myZipMoment + 100000000L > tick)
/* 155 */         return;  if (!this.myBackgroundThreadEventStack.isEmpty() || !this.myMainThreadEventStack.isEmpty())
/*     */         return; 
/* 157 */     }  this.myZipMoment = tick;
/*     */     
/* 159 */     List<V8LogIndexesWriter.TimerEvent> writeQueue = new ArrayList<>();
/* 160 */     for (Map.Entry<V8EventType, ArrayDeque<BeforeAfter<Long>>> entry : this.myEventRanges.entrySet()) {
/* 161 */       V8EventType type = entry.getKey();
/* 162 */       ArrayDeque<BeforeAfter<Long>> deque = entry.getValue();
/* 163 */       if (deque.isEmpty())
/* 164 */         continue;  BeforeAfter<Long> previous = null;
/* 165 */       for (BeforeAfter<Long> beforeAfter : deque) {
/* 166 */         if (previous == null) {
/* 167 */           previous = beforeAfter;
/*     */           continue;
/*     */         } 
/* 170 */         if (((Long)previous.getAfter()).longValue() + 5000L >= ((Long)beforeAfter.getBefore()).longValue()) {
/*     */           
/* 172 */           previous = new BeforeAfter(previous.getBefore(), beforeAfter.getAfter()); continue;
/*     */         } 
/* 174 */         writeQueue.add(new V8LogIndexesWriter.TimerEvent(((Long)previous.getBefore()).longValue(), type, ((Long)previous.getAfter()).longValue() - ((Long)previous.getBefore()).longValue()));
/* 175 */         previous = beforeAfter;
/*     */       } 
/*     */       
/* 178 */       if (previous != null) {
/* 179 */         writeQueue.add(new V8LogIndexesWriter.TimerEvent(((Long)previous.getBefore()).longValue(), type, ((Long)previous.getAfter()).longValue() - ((Long)previous.getBefore()).longValue()));
/*     */       }
/*     */     } 
/* 182 */     writeQueue.sort(Comparator.comparingLong(V8LogIndexesWriter.TimerEvent::getStartNanos));
/* 183 */     for (V8LogIndexesWriter.TimerEvent event : writeQueue) {
/* 184 */       this.myIndexesWriter.recordEvent(event);
/*     */     } }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private ParserBase processTimerEventStart() {
/* 194 */     return new ParserBase(2, 2, new ArgumentType[] { ArgumentType.str, ArgumentType.number })
/*     */       {
/*     */         protected void process(List<String> strings) throws IOException {
/* 197 */           V8TickProcessor.this.myDistortionNano += V8TickProcessor.this.myDistortionPerEntryNano;
/* 198 */           String eventName = StringUtil.unquoteString(strings.get(0));
/* 199 */           V8EventType type = V8EventType.getByCode(eventName.trim());
/* 200 */           if (type == null)
/*     */             return; 
/* 202 */           ArrayDeque<V8EventType> stack = (type.getThreadId() == 0) ? V8TickProcessor.this.myMainThreadEventStack : V8TickProcessor.this.myBackgroundThreadEventStack;
/* 203 */           long lastTs = V8TickProcessor.this.getLastTs(type);
/*     */           
/* 205 */           long tick = V8TickProcessor.parseNumber(strings.get(1)) - V8TickProcessor.this.myDistortionNano;
/* 206 */           tick = Math.max(tick, lastTs + 0L);
/*     */           
/* 208 */           V8EventType last = stack.isEmpty() ? null : stack.getLast();
/* 209 */           if (last != null) {
/* 210 */             ((ArrayDeque<BeforeAfter>)V8TickProcessor.this.myEventRanges.get(last)).add(new BeforeAfter(Long.valueOf(lastTs), Long.valueOf(tick)));
/*     */           }
/*     */           
/* 213 */           stack.addLast(type);
/* 214 */           V8TickProcessor.this.setLastTs(type, tick);
/* 215 */           V8TickProcessor.this.zipTimerEvents(false, tick);
/*     */         }
/*     */       };
/*     */   }
/*     */   
/*     */   private long getLastTs(V8EventType type) {
/* 221 */     return (type.getThreadId() == 0) ? this.myLastTsMainThread : this.myLastTsBackgroundThread;
/*     */   }
/*     */   
/*     */   private ParserBase processTimerEventEnd() {
/* 225 */     return new ParserBase(2, 2, new ArgumentType[] { ArgumentType.str, ArgumentType.number })
/*     */       {
/*     */         protected void process(List<String> strings) throws IOException {
/* 228 */           V8TickProcessor.this.myDistortionNano += V8TickProcessor.this.myDistortionPerEntryNano;
/* 229 */           String eventName = StringUtil.unquoteString(strings.get(0));
/* 230 */           V8EventType type = V8EventType.getByCode(eventName.trim());
/* 231 */           if (type == null)
/* 232 */             return;  long tick = V8TickProcessor.parseNumber(strings.get(1)) - V8TickProcessor.this.myDistortionNano;
/*     */           
/* 234 */           ArrayDeque<V8EventType> stack = (type.getThreadId() == 0) ? V8TickProcessor.this.myMainThreadEventStack : V8TickProcessor.this.myBackgroundThreadEventStack;
/* 235 */           if (stack.isEmpty())
/* 236 */             return;  V8EventType lastEvent = stack.removeLast();
/* 237 */           while (!type.equals(lastEvent)) {
/*     */             
/* 239 */             if (stack.isEmpty())
/* 240 */               return;  lastEvent = stack.removeLast();
/*     */           } 
/*     */           
/* 243 */           V8TickProcessor.this.processEventTypeEnd(type, tick);
/*     */         }
/*     */       };
/*     */   }
/*     */   
/*     */   private void processEventTypeEnd(V8EventType type, long tick) throws IOException {
/* 249 */     long lastTs = getLastTs(type);
/* 250 */     tick = Math.max(tick, lastTs + 0L);
/*     */     
/* 252 */     ((ArrayDeque<BeforeAfter>)this.myEventRanges.get(type)).add(new BeforeAfter(Long.valueOf(lastTs), Long.valueOf(tick)));
/* 253 */     setLastTs(type, tick);
/* 254 */     zipTimerEvents(false, tick);
/*     */   }
/*     */   
/*     */   private void setLastTs(V8EventType type, long tick) {
/* 258 */     if (type.getThreadId() == 0) {
/* 259 */       this.myLastTsMainThread = tick;
/*     */     } else {
/* 261 */       this.myLastTsBackgroundThread = tick;
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   public ParserBase advanceDistortion() {
/* 267 */     return new ParserBase(0, -1, new ArgumentType[0])
/*     */       {
/*     */         protected void process(List<String> strings) throws IOException {
/* 270 */           V8TickProcessor.this.myDistortionNano += V8TickProcessor.this.myDistortionPerEntryNano;
/*     */         }
/*     */       };
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public ParserBase processTick() {
/* 279 */     return new ParserBase(5, -1, new ArgumentType[] { ArgumentType.address, ArgumentType.number, ArgumentType.number, ArgumentType.address, ArgumentType.number })
/*     */       {
/*     */         protected void process(List<String> strings) throws IOException {
/* 282 */           V8TickProcessor.this.myDistortionNano += V8TickProcessor.this.myDistortionPerEntryNano;
/* 283 */           ((Counter)V8TickProcessor.this.myTickCounters.get(V8TickProcessor.TickCounterType.total)).incrementAndGet();
/* 284 */           long vmStateCode = V8TickProcessor.parseNumber(strings.get(4));
/* 285 */           V8TickProcessor.VmState vmState = V8TickProcessor.VmState.fromCode((int)vmStateCode);
/* 286 */           if (V8TickProcessor.VmState.GC.equals(vmState)) {
/* 287 */             ((Counter)V8TickProcessor.this.myTickCounters.get(V8TickProcessor.TickCounterType.gc)).incrementAndGet();
/*     */           }
/*     */           
/* 290 */           BigInteger pc = V8TickProcessor.parseAddress(strings.get(0));
/* 291 */           long isExternalCallback = V8TickProcessor.parseNumber(strings.get(2));
/* 292 */           BigInteger tosOrExternalCallback = V8TickProcessor.parseAddress(strings.get(3));
/* 293 */           if (isExternalCallback > 0L) {
/* 294 */             pc = tosOrExternalCallback;
/* 295 */             tosOrExternalCallback = null;
/* 296 */           } else if (tosOrExternalCallback != null) {
/* 297 */             CodeMap.CodeEntry funcEntry = V8TickProcessor.this.myProfile.findEntry(tosOrExternalCallback);
/* 298 */             if (!(funcEntry instanceof CodeMap.DynamicFuncCodeEntry)) {
/* 299 */               tosOrExternalCallback = null;
/*     */             }
/*     */           } 
/* 302 */           long nanos = V8TickProcessor.parseNumber(strings.get(1));
/* 303 */           long realNanos = nanos - V8TickProcessor.this.myDistortionNano;
/* 304 */           ProgressManager.progress2(NodeJSBundle.message("progress.details.processing.tick.at.ms", new Object[] { Long.valueOf(realNanos / 1000L) }));
/* 305 */           if (strings.size() > 5) {
/* 306 */             List<String> stack = strings.subList(5, strings.size());
/* 307 */             List<BigInteger> parsedStack = V8TickProcessor.processStack(pc, tosOrExternalCallback, stack);
/* 308 */             List<Long> symbolicStack = V8TickProcessor.this.myProfile.resolveAndFilterFuncs(parsedStack);
/* 309 */             if (V8TickProcessor.VmState.GC.equals(vmState)) {
/* 310 */               symbolicStack.add(0, Long.valueOf(0L));
/*     */             }
/* 312 */             V8TickProcessor.this.myIndexesWriter.processTick(realNanos, vmState, symbolicStack);
/* 313 */             V8TickProcessor.this.myProfile.recordTick(symbolicStack);
/*     */           } else {
/* 315 */             if (V8TickProcessor.VmState.GC.equals(vmState)) {
/* 316 */               List<Long> stack = Collections.singletonList(Long.valueOf(0L));
/* 317 */               V8TickProcessor.this.myIndexesWriter.processTick(realNanos, vmState, stack);
/*     */             } else {
/*     */               
/* 320 */               ((Counter)V8TickProcessor.this.myTickCounters.get(V8TickProcessor.TickCounterType.idle)).incrementAndGet();
/* 321 */               V8TickProcessor.this.myIndexesWriter.processTick(realNanos, vmState, Collections.emptyList());
/*     */             } 
/* 323 */             V8TickProcessor.this.myProfile.recordTick(Collections.emptyList());
/*     */           } 
/*     */         }
/*     */       };
/*     */   }
/*     */   
/*     */   private static List<BigInteger> processStack(BigInteger pc, BigInteger func, List<String> tail) {
/* 330 */     List<BigInteger> list = new ArrayList<>();
/* 331 */     list.add(pc);
/* 332 */     BigInteger prevFrame = pc;
/* 333 */     if (func != null) {
/* 334 */       list.add(func);
/*     */     }
/* 336 */     for (String val : tail) {
/* 337 */       if (val.startsWith("+")) {
/* 338 */         BigInteger addr = parseAddressOrNum(val.substring(1));
/* 339 */         if (addr != null) {
/* 340 */           prevFrame = prevFrame.add(addr);
/* 341 */           list.add(prevFrame); continue;
/*     */         } 
/* 343 */         LOG.info("wrong address: " + val); continue;
/*     */       } 
/* 345 */       if (val.startsWith("-")) {
/* 346 */         BigInteger addr = parseAddressOrNum(val.substring(1));
/* 347 */         if (addr != null) {
/* 348 */           prevFrame = prevFrame.subtract(addr);
/* 349 */           list.add(prevFrame); continue;
/*     */         } 
/* 351 */         LOG.info("wrong address: " + val); continue;
/*     */       } 
/* 353 */       if (!val.startsWith("o")) {
/* 354 */         BigInteger address = parseAddress(val);
/* 355 */         if (address != null) {
/* 356 */           list.add(address);
/*     */           continue;
/*     */         } 
/* 359 */         LOG.info("wrong address: " + val);
/*     */       } 
/*     */     } 
/*     */     
/* 363 */     return list;
/*     */   }
/*     */ 
/*     */   
/*     */   public ParserBase processFunctionMove() {
/* 368 */     return new ParserBase(2, 2, new ArgumentType[] { ArgumentType.address, ArgumentType.address })
/*     */       {
/*     */         protected void process(List<String> strings) {
/* 371 */           BigInteger from = V8TickProcessor.parseAddress(strings.get(0));
/* 372 */           BigInteger to = V8TickProcessor.parseAddress(strings.get(1));
/* 373 */           V8TickProcessor.this.myProfile.moveFunc(from, to);
/*     */         }
/*     */       };
/*     */   }
/*     */ 
/*     */   
/*     */   public ParserBase processCodeDelete() {
/* 380 */     return new ParserBase(1, 1, new ArgumentType[] { ArgumentType.address })
/*     */       {
/*     */         protected void process(List<String> strings) {
/* 383 */           V8TickProcessor.this.myProfile.deleteCode(V8TickProcessor.parseAddress(strings.get(0)));
/*     */         }
/*     */       };
/*     */   }
/*     */ 
/*     */   
/*     */   public ParserBase processCodeMove() {
/* 390 */     return new ParserBase(2, 2, new ArgumentType[] { ArgumentType.address, ArgumentType.address })
/*     */       {
/*     */         protected void process(List<String> strings) {
/* 393 */           BigInteger from = V8TickProcessor.parseAddress(strings.get(0));
/* 394 */           BigInteger to = V8TickProcessor.parseAddress(strings.get(1));
/* 395 */           V8TickProcessor.this.myProfile.moveCode(from, to);
/*     */         }
/*     */       };
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
/*     */   public ThrowableConsumer<List<String>, IOException> processCodeCreation() {
/* 416 */     return strings -> {
/*     */         ThrowableConsumer<List<String>, IOException> parserBase = this.myWithTimestamp ? new MyCodeCreationWithTimestampParser() : new MyOldCodeCreationParser();
/*     */         parserBase.consume(strings);
/*     */       };
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public ParserBase processSharedLibrary() {
/* 426 */     return new ParserBase(3, 3, new ArgumentType[] { ArgumentType.str, ArgumentType.address, ArgumentType.address })
/*     */       {
/*     */         protected void process(List<String> strings) {
/* 429 */           String name = StringUtil.unquoteString(strings.get(0));
/* 430 */           BigInteger start = V8TickProcessor.parseAddress(strings.get(1));
/* 431 */           BigInteger end = V8TickProcessor.parseAddress(strings.get(2));
/* 432 */           CodeMap.CodeEntry entry = V8TickProcessor.this.myProfile.addLibrary(name, start, end);
/* 433 */           V8TickProcessor.this.myCodeTypes.put(entry.getName(), V8TickProcessor.CodeType.SHARED_LIB);
/*     */         }
/*     */       };
/*     */   }
/*     */ 
/*     */   
/*     */   @NotNull
/*     */   private V8ProfileCallback createCallback() {
/* 441 */     return new V8ProfileCallback()
/*     */       {
/*     */         public void onUnknownMove(BigInteger addr) {
/* 444 */           V8TickProcessor.LOG.info("unknown move: " + addr);
/*     */         }
/*     */ 
/*     */         
/*     */         public void onUnknownDelete(BigInteger addr) {
/* 449 */           V8TickProcessor.LOG.info("unknown delete: " + addr);
/*     */         }
/*     */ 
/*     */         
/*     */         public void onUnknownTick(BigInteger addr, long stackPos) {
/* 454 */           if (stackPos == 0L) {
/* 455 */             ((Counter)V8TickProcessor.this.myTickCounters.get(V8TickProcessor.TickCounterType.unaccounted)).incrementAndGet();
/*     */           }
/*     */         }
/*     */ 
/*     */         
/*     */         public boolean processFunction(String name) {
/* 461 */           return true;
/*     */         }
/*     */       };
/*     */   }
/*     */   
/*     */   public enum CodeType {
/* 467 */     CPP, SHARED_LIB;
/*     */   }
/*     */   
/*     */   public enum TickCounterType {
/* 471 */     total, unaccounted, excluded, gc, idle;
/*     */   }
/*     */   
/*     */   public enum VmState {
/* 475 */     JS(0), GC(1), COMPILER(2), OTHER(3), EXTERNAL(4), IDLE(5);
/*     */     
/*     */     private final int myCode;
/*     */     
/*     */     VmState(int code) {
/* 480 */       this.myCode = code;
/*     */     }
/*     */     
/*     */     public int getCode() {
/* 484 */       return this.myCode;
/*     */     }
/*     */     
/*     */     @Nullable
/*     */     public static VmState fromCode(int code) {
/* 489 */       for (VmState state : values()) {
/* 490 */         if (state.getCode() == code) return state; 
/*     */       } 
/* 492 */       return null;
/*     */     }
/*     */   }
/*     */   
/*     */   private static BigInteger parseAddressOrNum(String s) {
/* 497 */     if (s.startsWith("0x")) return parseAddress(s); 
/* 498 */     return BigInteger.valueOf(parseNumber(s));
/*     */   }
/*     */   
/*     */   public static BigInteger parseAddress(String s) {
/*     */     try {
/* 503 */       if (s.startsWith("0x")) return new BigInteger(s.substring(2), 16); 
/* 504 */       return new BigInteger(s, 16);
/* 505 */     } catch (NumberFormatException e) {
/* 506 */       LOG.info(e);
/* 507 */       return null;
/*     */     } 
/*     */   }
/*     */   
/*     */   static long parseNumber(String s) {
/*     */     try {
/* 513 */       return Long.parseLong(s);
/* 514 */     } catch (NumberFormatException e) {
/* 515 */       return -1L;
/*     */     } 
/*     */   }
/*     */   
/*     */   private class MyOldCodeCreationParser extends ParserBase {
/*     */     MyOldCodeCreationParser() {
/* 521 */       super(5, 7, new ArgumentType[] { ArgumentType.str, ArgumentType.number, ArgumentType.address, ArgumentType.number, ArgumentType.str, ArgumentType.address, ArgumentType.str });
/*     */     }
/*     */ 
/*     */ 
/*     */     
/*     */     public void consume(List<String> strings) throws IOException {
/* 527 */       if (strings.size() > 5 && ((String)strings.get(strings.size() - 1)).contains("\"")) {
/* 528 */         strings.set(4, StringUtil.join(strings.subList(4, strings.size()), ", "));
/* 529 */         super.consume(strings.subList(0, 5));
/*     */       } else {
/* 531 */         super.consume(strings);
/*     */       } 
/*     */     }
/*     */ 
/*     */     
/*     */     protected void process(List<String> strings) {
/* 537 */       String type = StringUtil.unquoteString(strings.get(0));
/*     */       
/* 539 */       BigInteger start = V8TickProcessor.parseAddress(strings.get(2));
/* 540 */       int size = (int)V8TickProcessor.parseNumber(strings.get(3));
/* 541 */       String name = StringUtil.unquoteString(strings.get(4));
/* 542 */       if (strings.size() > 5 && !((String)strings.get(strings.size() - 1)).contains("\"")) {
/* 543 */         BigInteger funcAddress = V8TickProcessor.parseAddress(strings.get(5));
/* 544 */         String state = (strings.size() == 7) ? strings.get(6) : "";
/* 545 */         CodeState codeState = CodeState.fromStrState(state);
/* 546 */         codeState = (codeState == null) ? CodeState.compiled : codeState;
/* 547 */         V8TickProcessor.this.myProfile.addFuncCode(type, name, start, size, funcAddress, codeState);
/*     */       } else {
/* 549 */         V8TickProcessor.this.myProfile.addCode(type, name, start, size);
/*     */       } 
/*     */     }
/*     */   }
/*     */   
/*     */   private class MyCodeCreationWithTimestampParser extends ParserBase {
/*     */     MyCodeCreationWithTimestampParser() {
/* 556 */       super(6, 8, new ArgumentType[] { ArgumentType.str, ArgumentType.number, ArgumentType.number, ArgumentType.address, ArgumentType.number, ArgumentType.str, ArgumentType.address, ArgumentType.str });
/*     */     }
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     protected void process(List<String> strings) {
/* 563 */       String type = StringUtil.unquoteString(strings.get(0));
/*     */ 
/*     */       
/* 566 */       BigInteger startAddress = V8TickProcessor.parseAddress(strings.get(3));
/* 567 */       int size = (int)V8TickProcessor.parseNumber(strings.get(4));
/* 568 */       String name = StringUtil.unquoteString(strings.get(5));
/* 569 */       if (strings.size() > 7) {
/* 570 */         BigInteger funcAddress = V8TickProcessor.parseAddress(strings.get(6));
/* 571 */         String state = StringUtil.notNullize(strings.get(7));
/* 572 */         CodeState codeState = CodeState.fromStrState(state);
/* 573 */         codeState = (codeState == null) ? CodeState.compiled : codeState;
/* 574 */         V8TickProcessor.this.myProfile.addFuncCode(type, name, startAddress, size, funcAddress, codeState);
/*     */       } else {
/* 576 */         V8TickProcessor.this.myProfile.addCode(type, name, startAddress, size);
/*     */       } 
/*     */     }
/*     */   }
/*     */ }


/* Location:              C:\Program Files\JetBrains\WebStorm 2021.1\plugins\NodeJS\lib\NodeJS.jar!\com\jetbrains\nodejs\run\profile\cpu\v8log\calculation\V8TickProcessor.class
 * Java compiler version: 11 (55.0)
 * JD-Core Version:       1.1.3
 */