/*     */ package org.bipolar.run.profile.cpu.v8log.calculation;
/*     */ 
/*     */ import com.intellij.openapi.util.io.FileUtil;
/*     */ import com.intellij.util.io.DataOutputStream;
/*     */ import org.bipolar.run.profile.cpu.calculation.V8ProfileLine;
/*     */ import org.bipolar.run.profile.cpu.v8log.data.FlatTopCalls;
/*     */ import org.bipolar.run.profile.cpu.v8log.data.V8EventType;
/*     */ import org.bipolar.run.profile.cpu.v8log.reading.V8LogCachingReader;
/*     */ import org.bipolar.run.profile.heap.CompositeCloseable;
/*     */ import org.bipolar.run.profile.heap.IndexFiles;
/*     */ import org.bipolar.run.profile.heap.calculation.ByteArrayWrapper;
/*     */ import org.bipolar.run.profile.heap.io.IntegerRawSerializer;
/*     */ import org.bipolar.run.profile.heap.io.LinksWriter;
/*     */ import org.bipolar.run.profile.heap.io.LongRawSerializer;
/*     */ import org.bipolar.run.profile.heap.io.RawSerializer;
/*     */ import org.bipolar.run.profile.heap.io.SequentialRawWriter;
/*     */ import org.bipolar.run.profile.heap.io.StringRawSerializer;
/*     */ import org.bipolar.run.profile.heap.io.reverse.LinksReaderFactory;
/*     */ import java.io.ByteArrayOutputStream;
/*     */ import java.io.Closeable;
/*     */ import java.io.DataInput;
/*     */ import java.io.DataOutput;
/*     */ import java.io.File;
/*     */ import java.io.IOException;
/*     */ import java.util.Collection;
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
/*     */ public class V8LogIndexesWriter
/*     */   implements Closeable
/*     */ {
/*     */   private final File myV8LogFile;
/*     */   @NotNull
/*     */   private final ByteArrayWrapper myDigest;
/*     */   @NotNull
/*     */   private final IndexFiles<Category> myIndexFiles;
/*     */   private final LinksWriter<Long> myStackWriter;
/*     */   private final SequentialRawWriter<Long> myTimeWriter;
/*     */   private final SequentialRawWriter<Integer> myStackSizeWriter;
/*     */   private final LinksWriter<String> myStrWriter;
/*     */   private final LinksWriter<TimerEvent> myEventsWriter;
/*     */   private final File myNumStackFile;
/*     */   private final File myStackFile;
/*     */   private final File myTimeFile;
/*     */   private final File myNumStringsFile;
/*     */   private final File myStringsFile;
/*     */   
/*     */   public V8LogIndexesWriter(File v8LogFile, @NotNull ByteArrayWrapper digest, @NotNull IndexFiles<Category> indexFiles) throws IOException {
/*  56 */     this.myV8LogFile = v8LogFile;
/*  57 */     this.myDigest = digest;
/*  58 */     this.myIndexFiles = indexFiles;
/*     */     
/*  60 */     this.myNumStackFile = this.myIndexFiles.generate(Category.numStack, "numStack");
/*  61 */     this.myStackFile = this.myIndexFiles.generate(Category.stack, "stack");
/*  62 */     this.myTimeFile = this.myIndexFiles.generate(Category.timeForTick, "time");
/*  63 */     this.myNumStringsFile = this.myIndexFiles.generate(Category.numStrings, "numStr");
/*  64 */     this.myStringsFile = this.myIndexFiles.generate(Category.strings, "str");
/*  65 */     this.myNumEventsFile = this.myIndexFiles.generate(Category.numEvents, "numEvents");
/*  66 */     this.myEventsFile = this.myIndexFiles.generate(Category.events, "events");
/*  67 */     this.myStackSizeFile = this.myIndexFiles.generate(Category.stackSize, "stackSize");
/*     */     
/*  69 */     this.myStackWriter = new LinksWriter(this.myNumStackFile, this.myStackFile, (RawSerializer)new LongRawSerializer());
/*  70 */     this.myTimeWriter = new SequentialRawWriter(this.myTimeFile, (RawSerializer)new LongRawSerializer());
/*  71 */     this.myStrWriter = new LinksWriter(this.myNumStringsFile, this.myStringsFile, (RawSerializer)new StringRawSerializer());
/*  72 */     this.myEventsWriter = new LinksWriter(this.myNumEventsFile, this.myEventsFile, new TimerEvent.MyRawSerializer());
/*  73 */     this.myStackSizeWriter = new SequentialRawWriter(this.myStackSizeFile, (RawSerializer)new IntegerRawSerializer());
/*  74 */     this.myNumTicks = 0L;
/*  75 */     this.myNumIdleTicks = 0;
/*  76 */     this.myNumGcTicks = 0;
/*  77 */     this.myMaxStackSize = 0;
/*  78 */     this.myDurationWorker = new V8LogStackDurationWorker(indexFiles);
/*  79 */     this.myEventsTickIndexer = new TickIndexer(1000000L);
/*  80 */     this.myEventsEndTickIndexer = new TickIndexer(1000000L);
/*     */   }
/*     */   private final File myNumEventsFile; private final File myEventsFile; private final File myStackSizeFile; private final V8LogStackDurationWorker myDurationWorker; private long myNumTicks; private int myNumIdleTicks; private int myNumGcTicks; private int myMaxStackSize; private File myHeaderFile; private long myLastTick;
/*     */   private List<File> myOverviewFiles;
/*     */   @NotNull
/*     */   private final TickIndexer myEventsTickIndexer;
/*     */   @NotNull
/*     */   private final TickIndexer myEventsEndTickIndexer;
/*     */   
/*     */   public V8LogCachingReader createReader(@NotNull CompositeCloseable resourses, @NotNull LinksReaderFactory<Long> durationFactory, @NotNull V8ProfileLine bottomUp, @NotNull V8ProfileLine topDown, @NotNull FlatTopCalls flat, Map<String, V8TickProcessor.CodeType> codeTypes, File distributionFile, File selfDistributionFile) throws IOException {
/*  90 */     if (resourses == null) $$$reportNull$$$0(2);  if (durationFactory == null) $$$reportNull$$$0(3);  if (bottomUp == null) $$$reportNull$$$0(4);  if (topDown == null) $$$reportNull$$$0(5);  if (flat == null) $$$reportNull$$$0(6);  return new V8LogCachingReader(this.myDigest, this.myV8LogFile, this.myHeaderFile, this.myTimeFile, this.myStackSizeFile, new LinksReaderFactory((RawSerializer)new LongRawSerializer(), this.myNumStackFile, this.myStackFile), new LinksReaderFactory((RawSerializer)new StringRawSerializer(), this.myNumStringsFile, this.myStringsFile), new LinksReaderFactory(new TimerEvent.MyRawSerializer(), this.myNumEventsFile, this.myEventsFile), this.myOverviewFiles, durationFactory, bottomUp, topDown, flat, resourses, this.myEventsTickIndexer, this.myEventsEndTickIndexer, codeTypes, distributionFile, selfDistributionFile);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void postProcess() throws IOException {
/*  99 */     V8OverviewScalesBuilder scalesBuilder = new V8OverviewScalesBuilder(this.myTimeFile, this.myStackSizeFile, this.myNumTicks, this.myLastTick, this.myIndexFiles, 50000L);
/*     */     
/* 101 */     scalesBuilder.execute();
/* 102 */     this.myOverviewFiles = scalesBuilder.getOverviewFiles();
/*     */   }
/*     */   
/*     */   public long getNumTicks() {
/* 106 */     return this.myNumTicks;
/*     */   }
/*     */   
/*     */   public void processTick(long nanos, V8TickProcessor.VmState vmState, List<Long> symbolicStack) throws IOException {
/* 110 */     this.myLastTick = nanos;
/* 111 */     this.myTimeWriter.write(Long.valueOf(nanos));
/* 112 */     this.myStackWriter.write(symbolicStack);
/* 113 */     int size = symbolicStack.size();
/* 114 */     this.myStackSizeWriter.write(Integer.valueOf(size));
/* 115 */     this.myNumTicks++;
/* 116 */     this.myMaxStackSize = Math.max(this.myMaxStackSize, size);
/* 117 */     this.myDurationWorker.tick(nanos, symbolicStack);
/*     */   }
/*     */   
/*     */   public void setTickCounters(int numIdle, int numGc) {
/* 121 */     this.myNumIdleTicks = numIdle;
/* 122 */     this.myNumGcTicks = numGc;
/*     */   }
/*     */   
/*     */   public V8LogStackDurationWorker getDurationWorker() {
/* 126 */     return this.myDurationWorker;
/*     */   }
/*     */   
/*     */   public void writeHeader(long lastTs) throws IOException {
/* 130 */     this.myHeaderFile = this.myIndexFiles.generate(Category.header, "header");
/* 131 */     ByteArrayOutputStream out = new ByteArrayOutputStream();
/* 132 */     DataOutputStream stream = new DataOutputStream(out);
/* 133 */     stream.writeLong(lastTs);
/* 134 */     stream.writeLong(this.myNumTicks);
/* 135 */     stream.writeInt(this.myMaxStackSize);
/* 136 */     stream.writeInt(this.myNumGcTicks);
/* 137 */     stream.writeInt(this.myNumIdleTicks);
/* 138 */     stream.flush();
/* 139 */     FileUtil.writeToFile(this.myHeaderFile, out.toByteArray());
/*     */   }
/*     */   
/*     */   public int getMaxStackSize() {
/* 143 */     return this.myMaxStackSize;
/*     */   }
/*     */   
/*     */   public void writeStrings(Collection<String> str) throws IOException {
/* 147 */     for (String s : str) {
/* 148 */       this.myStrWriter.writeVariableValue(s);
/*     */     }
/*     */   }
/*     */   
/*     */   public void recordEvent(TimerEvent event) throws IOException {
/* 153 */     this.myEventsWriter.writeVariableValue(event);
/*     */     
/* 155 */     this.myEventsTickIndexer.nextTick(event.getStartNanos());
/* 156 */     this.myEventsEndTickIndexer.nextTick(event.getStartNanos() + event.getPause());
/*     */   }
/*     */ 
/*     */   
/*     */   public void close() throws IOException {
/* 161 */     this.myTimeWriter.close();
/* 162 */     this.myStrWriter.close();
/* 163 */     this.myStackWriter.close();
/*     */     
/* 165 */     this.myEventsWriter.close();
/* 166 */     this.myStackSizeWriter.close();
/*     */   }
/*     */   
/*     */   public enum Category {
/* 170 */     header, timeForTick, numStrings, strings, numStack, stack, numEvents, events, stackSize, durationByTick1, numDurationByTick1,
/* 171 */     durationByTick2, numDurationByTick2, description, overviewScale, bottomUp, flatTree, topDown,
/* 172 */     eventsIndex, eventsEndsIndex, eventsOverview, codeTypes, distribution, selfDistribution;
/*     */   }
/*     */   
/*     */   public static class TimerEvent {
/*     */     private final long myStartNanos;
/*     */     private final V8EventType myEventType;
/*     */     private final long myPause;
/*     */     
/*     */     public TimerEvent(long startNanos, V8EventType eventType, long pause) {
/* 181 */       this.myStartNanos = startNanos;
/* 182 */       this.myEventType = eventType;
/* 183 */       this.myPause = pause;
/*     */     }
/*     */     
/*     */     public static class MyRawSerializer
/*     */       implements RawSerializer<TimerEvent> {
/*     */       public long getRecordSize() {
/* 189 */         return 0L;
/*     */       }
/*     */ 
/*     */       
/*     */       public void write(@NotNull DataOutput out, @NotNull V8LogIndexesWriter.TimerEvent event) throws IOException {
/* 194 */         if (out == null) $$$reportNull$$$0(0);  if (event == null) $$$reportNull$$$0(1);  out.writeLong(event.myStartNanos);
/* 195 */         out.writeUTF(event.myEventType.getCode());
/* 196 */         out.writeLong(event.myPause);
/*     */       }
/*     */ 
/*     */       
/*     */       public V8LogIndexesWriter.TimerEvent read(@NotNull DataInput in) throws IOException {
/* 201 */         if (in == null) $$$reportNull$$$0(2);  long startNanos = in.readLong();
/* 202 */         V8EventType eventType = V8EventType.getByCode(in.readUTF());
/* 203 */         long pause = in.readLong();
/* 204 */         return new V8LogIndexesWriter.TimerEvent(startNanos, eventType, pause);
/*     */       }
/*     */     }
/*     */     
/*     */     public long getStartNanos() {
/* 209 */       return this.myStartNanos;
/*     */     }
/*     */     
/*     */     public V8EventType getEventType() {
/* 213 */       return this.myEventType;
/*     */     }
/*     */     
/*     */     public long getPause() {
/* 217 */       return this.myPause;
/*     */     }
/*     */   }
/*     */   
/*     */   public static class MyRawSerializer implements RawSerializer<TimerEvent> {
/*     */     public long getRecordSize() {
/*     */       return 0L;
/*     */     }
/*     */     
/*     */     public void write(@NotNull DataOutput out, @NotNull V8LogIndexesWriter.TimerEvent event) throws IOException {
/*     */       if (out == null)
/*     */         $$$reportNull$$$0(0); 
/*     */       if (event == null)
/*     */         $$$reportNull$$$0(1); 
/*     */       out.writeLong(event.myStartNanos);
/*     */       out.writeUTF(event.myEventType.getCode());
/*     */       out.writeLong(event.myPause);
/*     */     }
/*     */     
/*     */     public V8LogIndexesWriter.TimerEvent read(@NotNull DataInput in) throws IOException {
/*     */       if (in == null)
/*     */         $$$reportNull$$$0(2); 
/*     */       long startNanos = in.readLong();
/*     */       V8EventType eventType = V8EventType.getByCode(in.readUTF());
/*     */       long pause = in.readLong();
/*     */       return new V8LogIndexesWriter.TimerEvent(startNanos, eventType, pause);
/*     */     }
/*     */   }
/*     */ }


/* Location:              C:\Program Files\JetBrains\WebStorm 2021.1\plugins\NodeJS\lib\NodeJS.jar!\com\jetbrains\nodejs\run\profile\cpu\v8log\calculation\V8LogIndexesWriter.class
 * Java compiler version: 11 (55.0)
 * JD-Core Version:       1.1.3
 */