/*     */ package org.bipolar.run.profile.cpu.v8log.calculation;
/*     */ 
/*     */ import com.intellij.openapi.util.Pair;
/*     */ import org.bipolar.run.profile.heap.CompositeCloseable;
/*     */ import org.bipolar.run.profile.heap.IndexFiles;
/*     */ import org.bipolar.run.profile.heap.io.IntegerRawSerializer;
/*     */ import org.bipolar.run.profile.heap.io.LongRawSerializer;
/*     */ import org.bipolar.run.profile.heap.io.RawSerializer;
/*     */ import org.bipolar.run.profile.heap.io.SequentialRawReader;
/*     */ import org.bipolar.run.profile.heap.io.SequentialRawWriter;
/*     */ import org.bipolar.util.CloseableThrowableConsumer;
/*     */ import java.io.Closeable;
/*     */ import java.io.File;
/*     */ import java.io.IOException;
/*     */ import java.util.ArrayList;
/*     */ import java.util.List;
/*     */
import org.jetbrains.annotations.NotNull;
/*     */ 
/*     */ 
/*     */ public class V8OverviewScalesBuilder
/*     */ {
/*     */   public static final long TICK_STEP = 50000L;
/*     */   public static final int MAX_POINTS = 500;
/*     */   @NotNull
/*     */   private final File myTsFile;
/*     */   @NotNull
/*     */   private final File myStackSizeFile;
/*     */   private final long myNumTicks;
/*     */   private final long myLastTick;
/*     */   @NotNull
/*     */   private final IndexFiles<V8LogIndexesWriter.Category> myIndexFiles;
/*     */   private final long myTickStep;
/*     */   private final List<File> myOverviewFiles;
/*     */   
/*     */   public V8OverviewScalesBuilder(@NotNull File tsFile, @NotNull File stackSizeFile, long numTicks, long lastTick, @NotNull IndexFiles<V8LogIndexesWriter.Category> indexFiles, long tickStep) {
/*  36 */     this.myTsFile = tsFile;
/*  37 */     this.myStackSizeFile = stackSizeFile;
/*  38 */     this.myNumTicks = numTicks;
/*  39 */     this.myLastTick = lastTick;
/*  40 */     this.myIndexFiles = (IndexFiles<V8LogIndexesWriter.Category>)SYNTHETIC_LOCAL_VARIABLE_7;
/*  41 */     this.myTickStep = SYNTHETIC_LOCAL_VARIABLE_8;
/*  42 */     this.myOverviewFiles = new ArrayList<>();
/*     */   }
/*     */   
/*     */   public List<File> getOverviewFiles() {
/*  46 */     return this.myOverviewFiles;
/*     */   }
/*     */   
/*     */   public void execute() throws IOException {
/*  50 */     execute(false);
/*     */   }
/*     */   
/*     */   public void execute(boolean testMode) throws IOException {
/*  54 */     CompositeCloseable resources = new CompositeCloseable();
/*     */     
/*     */     try {
/*  57 */       final List<ScaleWriter> scaleWriters = new ArrayList<>();
/*  58 */       long step = this.myTickStep;
/*  59 */       LongIntegerSerializer serializer = new LongIntegerSerializer();
/*  60 */       while (this.myLastTick / step > 500L || (testMode && this.myLastTick / step > 0L)) {
/*  61 */         File file = this.myIndexFiles.generate(V8LogIndexesWriter.Category.overviewScale, "overviewScale");
/*  62 */         this.myOverviewFiles.add(file);
/*  63 */         SequentialRawWriter<Pair<Long, Integer>> writer = new SequentialRawWriter(file, serializer);
/*  64 */         scaleWriters.add((ScaleWriter)resources.register(new ScaleWriter(step, writer)));
/*  65 */         step *= 4L;
/*     */       } 
/*     */       
/*  68 */       SequentialRawReader<Long> tsReader = (SequentialRawReader<Long>)resources.register((Closeable)new SequentialRawReader(this.myTsFile, (RawSerializer)new LongRawSerializer()));
/*     */       
/*  70 */       final SequentialRawReader<Integer> stackSizeReader = (SequentialRawReader<Integer>)resources.register((Closeable)new SequentialRawReader(this.myStackSizeFile, (RawSerializer)new IntegerRawSerializer()));
/*  71 */       tsReader.iterate(new CloseableThrowableConsumer<Long, IOException>()
/*     */           {
/*     */             public void close() throws IOException {
/*  74 */               for (V8OverviewScalesBuilder.ScaleWriter writer : scaleWriters) {
/*  75 */                 writer.finish();
/*     */               }
/*     */             }
/*     */ 
/*     */             
/*     */             public void consume(Long ts) throws IOException {
/*  81 */               Integer size = (Integer)stackSizeReader.read();
/*  82 */               for (V8OverviewScalesBuilder.ScaleWriter writer : scaleWriters) {
/*  83 */                 writer.tick(ts.longValue(), size.intValue());
/*     */               }
/*     */             }
/*     */           });
/*     */     } finally {
/*  88 */       resources.close();
/*     */     } 
/*     */   }
/*     */   
/*     */   private static class ScaleWriter implements Closeable {
/*     */     private int myMax;
/*     */     private long myPrevTs;
/*     */     private long myLastTick;
/*     */     private final long myStep;
/*     */     private final SequentialRawWriter<Pair<Long, Integer>> myWriter;
/*     */     
/*     */     ScaleWriter(long step, SequentialRawWriter<Pair<Long, Integer>> writer) {
/* 100 */       this.myStep = step;
/* 101 */       this.myWriter = writer;
/* 102 */       this.myMax = 0;
/* 103 */       this.myPrevTs = 0L;
/* 104 */       this.myLastTick = 0L;
/*     */     }
/*     */     
/*     */     public void tick(long tick, int stackSize) throws IOException {
/* 108 */       if (tick < this.myLastTick)
/* 109 */         return;  if (tick - this.myStep >= this.myPrevTs) {
/* 110 */         this.myWriter.write(Pair.create(Long.valueOf(this.myPrevTs + this.myStep / 2L), Integer.valueOf(this.myMax)));
/* 111 */         this.myMax = 0;
/* 112 */         this.myPrevTs += this.myStep;
/* 113 */         while (this.myPrevTs + this.myStep <= tick) {
/* 114 */           this.myWriter.write(Pair.create(Long.valueOf(this.myPrevTs + this.myStep / 2L), Integer.valueOf(0)));
/* 115 */           this.myPrevTs += this.myStep;
/*     */         } 
/*     */       } 
/* 118 */       this.myMax = Math.max(this.myMax, stackSize);
/* 119 */       this.myLastTick = tick;
/*     */     }
/*     */     
/*     */     public void finish() throws IOException {
/* 123 */       this.myWriter.write(Pair.create(Long.valueOf((this.myPrevTs + this.myLastTick) / 2L), Integer.valueOf(this.myMax)));
/*     */     }
/*     */ 
/*     */     
/*     */     public void close() throws IOException {
/* 128 */       this.myWriter.close();
/*     */     }
/*     */   }
/*     */ }


/* Location:              C:\Program Files\JetBrains\WebStorm 2021.1\plugins\NodeJS\lib\NodeJS.jar!\com\jetbrains\nodejs\run\profile\cpu\v8log\calculation\V8OverviewScalesBuilder.class
 * Java compiler version: 11 (55.0)
 * JD-Core Version:       1.1.3
 */