/*     */ package org.bipolar.run.profile.heap.calculation;
/*     */ 
/*     */ import com.intellij.util.PairConsumer;
/*     */ import com.intellij.util.ThrowableConsumer;
/*     */ import com.intellij.util.containers.Convertor;
/*     */ import org.bipolar.run.profile.heap.io.RawSerializer;
/*     */ import org.bipolar.run.profile.heap.io.SequentialRawReader;
/*     */ import org.bipolar.run.profile.heap.io.SequentialRawWriter;
/*     */ import org.bipolar.run.profile.heap.io.reverse.LinksReaderFactory;
/*     */ import org.bipolar.run.profile.heap.io.reverse.SizeOffset;
/*     */ import org.bipolar.util.CloseableThrowableConsumer;
/*     */ import java.io.Closeable;
/*     */ import java.io.File;
/*     */ import java.io.FileNotFoundException;
/*     */ import java.io.IOException;
/*     */ import java.util.Set;
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
/*     */ public class ProcessInPortions<In, Out>
/*     */   implements Closeable
/*     */ {
/*     */   private static final int PORTION_SIZE = 10000;
/*     */   @NotNull
/*     */   private final RawSerializer<Out> myOutSerializer;
/*     */   private final long myPortionSize;
/*     */   private long myNodesCnt;
/*     */   @NotNull
/*     */   private final RawSerializer<In> mySerializer;
/*     */   @NotNull
/*     */   private final Convertor<? super In, Long> myIndexConvertor;
/*     */   @NotNull
/*     */   private final Convertor<? super In, ? extends Out> myInOutConvertor;
/*     */   private final File myNumLinksFile;
/*     */   private final File myLinksFile;
/*     */   private final SequentialRawWriter<SizeOffset> myNumLinksWriter;
/*     */   private final SequentialRawWriter<Out> myReverseLinksWriter;
/*     */   private long myOffset;
/*     */   private PairConsumer<Long, Set<Out>> myFilter;
/*     */   
/*     */   public ProcessInPortions(long nodesCnt, long portionSize, @NotNull RawSerializer<In> inSerializer, @NotNull RawSerializer<Out> outSerializer, @NotNull Convertor<? super In, Long> indexConvertor, @NotNull Convertor<? super In, ? extends Out> inOutConvertor, File numLinksFile, File linksFile) throws IOException {
/*  58 */     this.myOutSerializer = outSerializer;
/*  59 */     this.myNodesCnt = (nodesCnt <= 0L) ? 10000L : nodesCnt;
/*  60 */     this.mySerializer = inSerializer;
/*  61 */     this.myIndexConvertor = indexConvertor;
/*  62 */     this.myInOutConvertor = inOutConvertor;
/*  63 */     this.myNumLinksFile = (File)SYNTHETIC_LOCAL_VARIABLE_9;
/*  64 */     this.myLinksFile = (File)SYNTHETIC_LOCAL_VARIABLE_10;
/*     */     
/*  66 */     this.myNumLinksWriter = new SequentialRawWriter(this.myNumLinksFile, (RawSerializer)SizeOffset.MySerializer.getInstance());
/*  67 */     this.myReverseLinksWriter = new SequentialRawWriter(this.myLinksFile, outSerializer);
/*  68 */     this.myPortionSize = (portionSize <= 0L) ? 10000L : portionSize;
/*  69 */     this.myOffset = 0L;
/*     */   }
/*     */   
/*     */   public void setFilter(@NotNull PairConsumer<Long, Set<Out>> filter) {
/*  73 */     if (filter == null) $$$reportNull$$$0(4);  this.myFilter = filter;
/*     */   }
/*     */   
/*     */   public void correctSize(long value) {
/*  77 */     this.myNodesCnt = value;
/*     */   }
/*     */   
/*     */   public void continueCalculation(@NotNull File inFile, long size) throws IOException {
/*  81 */     if (inFile == null) $$$reportNull$$$0(5);  int steps = (int)(this.myNodesCnt / this.myPortionSize);
/*  82 */     for (int i = 1; i <= steps; i++) {
/*  83 */       long from = i * this.myPortionSize;
/*  84 */       if (from >= this.myNodesCnt)
/*  85 */         break;  long to = from + this.myPortionSize - 1L;
/*  86 */       to = Math.min(to, this.myNodesCnt - 1L);
/*     */       
/*  88 */       (new SequentialRawReader(inFile, this.mySerializer, size)).iterate(getStageCalculator(from, to));
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   public void continueCalculation(@NotNull ThrowableConsumer<CloseableThrowableConsumer<In, IOException>, IOException> consumer) throws IOException {
/*  94 */     if (consumer == null) $$$reportNull$$$0(6);  int steps = (int)(this.myNodesCnt / this.myPortionSize);
/*  95 */     for (int i = 1; i <= steps; i++) {
/*  96 */       long from = i * this.myPortionSize;
/*  97 */       if (from >= this.myNodesCnt)
/*  98 */         break;  long to = from + this.myPortionSize - 1L;
/*  99 */       to = Math.min(to, this.myNodesCnt - 1L);
/*     */       
/* 101 */       consumer.consume(getStageCalculator(from, to));
/*     */     } 
/*     */   }
/*     */   
/*     */   public CloseableThrowableConsumer<In, IOException> getFirstStageCalculator() {
/* 106 */     return getStageCalculator(0L, Math.min(this.myPortionSize - 1L, this.myNodesCnt - 1L));
/*     */   }
/*     */   
/*     */   @NotNull
/*     */   public CloseableThrowableConsumer<In, IOException> getStageCalculator(long from, long to) {
/* 111 */     final PortionProcessor<In, Out> calculator = new PortionProcessor<>(from, to, this.myInOutConvertor);
/* 112 */     if (this.myFilter != null) {
/* 113 */       calculator.setFilter(this.myFilter);
/*     */     }
/* 115 */     return new CloseableThrowableConsumer<In, IOException>()
/*     */       {
/*     */         public void consume(In in) throws IOException {
/* 118 */           calculator.accept(((Long)ProcessInPortions.this.myIndexConvertor.convert(in)).longValue(), in);
/*     */         }
/*     */ 
/*     */         
/*     */         public void close() throws IOException {
/* 123 */           calculator.flush(size -> {
/*     */                 ProcessInPortions.this.myNumLinksWriter.write(new SizeOffset(size.intValue(), ProcessInPortions.this.myOffset));
/*     */                 ProcessInPortions.this.myOffset += size.longValue();
/*     */               }out -> ProcessInPortions.this.myReverseLinksWriter.write(out));
/*     */         }
/*     */       };
/*     */   }
/*     */ 
/*     */   
/*     */   public void close() throws IOException {
/* 133 */     this.myNumLinksWriter.close();
/* 134 */     this.myReverseLinksWriter.close();
/*     */   }
/*     */   
/*     */   public LinksReaderFactory<Out> getLinksReaderFactory() throws FileNotFoundException {
/* 138 */     return new LinksReaderFactory(this.myOutSerializer, this.myNumLinksFile, this.myLinksFile);
/*     */   }
/*     */   
/*     */   public File getNumLinksFile() {
/* 142 */     return this.myNumLinksFile;
/*     */   }
/*     */   
/*     */   public File getLinksFile() {
/* 146 */     return this.myLinksFile;
/*     */   }
/*     */ }


/* Location:              C:\Program Files\JetBrains\WebStorm 2021.1\plugins\NodeJS\lib\NodeJS.jar!\com\jetbrains\nodejs\run\profile\heap\calculation\ProcessInPortions.class
 * Java compiler version: 11 (55.0)
 * JD-Core Version:       1.1.3
 */