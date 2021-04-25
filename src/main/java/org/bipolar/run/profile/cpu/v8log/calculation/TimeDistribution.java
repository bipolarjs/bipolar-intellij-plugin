/*     */ package org.bipolar.run.profile.cpu.v8log.calculation;
/*     */ 
/*     */ import org.bipolar.run.profile.heap.io.IntArraySerializer;
/*     */ import org.bipolar.run.profile.heap.io.LongArraySerializer;
/*     */ import org.bipolar.run.profile.heap.io.RawSerializer;
/*     */ import java.io.DataInput;
/*     */ import java.io.DataOutput;
/*     */ import java.io.IOException;
/*     */ import java.util.Arrays;
/*     */ import org.jetbrains.annotations.NotNull;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class TimeDistribution
/*     */ {
/*  17 */   public static final DistributionParameters STANDARD = new DistributionParameters(2L, 8);
/*     */   
/*     */   private final DistributionParameters myParameters;
/*     */   private final long[] myEndTimes;
/*     */   private final int[] myCounters;
/*     */   private final long[] mySampleStartTs;
/*     */   private int myOutOfScaleCounter;
/*     */   private long myMaxStartTs;
/*     */   private long myMax;
/*     */   
/*     */   public TimeDistribution() {
/*  28 */     this(STANDARD);
/*     */   }
/*     */   
/*     */   public TimeDistribution(DistributionParameters parameters) {
/*  32 */     this.myParameters = parameters;
/*  33 */     this.myCounters = new int[this.myParameters.getScaleSize()];
/*  34 */     this.mySampleStartTs = new long[this.myParameters.getScaleSize()];
/*  35 */     for (int i = 0; i < this.myParameters.getScaleSize(); i++) {
/*  36 */       this.myCounters[i] = 0;
/*  37 */       this.mySampleStartTs[i] = -1L;
/*     */     } 
/*     */     
/*  40 */     this.myEndTimes = new long[this.myParameters.getScaleSize()];
/*  41 */     fillEndTimes();
/*  42 */     this.myOutOfScaleCounter = 0;
/*  43 */     this.myMaxStartTs = -1L;
/*  44 */     this.myMax = 0L;
/*     */   }
/*     */   
/*     */   private void fillEndTimes() {
/*  48 */     long end = this.myParameters.getUnit() * 1000L;
/*  49 */     for (int i = 0; i < this.myParameters.getScaleSize(); i++) {
/*  50 */       this.myEndTimes[i] = end;
/*  51 */       end *= 2L;
/*     */     } 
/*     */   }
/*     */   
/*     */   public boolean isEmpty() {
/*  56 */     return (this.myMaxStartTs == -1L);
/*     */   }
/*     */   
/*     */   public long[] getEndTimes() {
/*  60 */     return this.myEndTimes;
/*     */   }
/*     */   
/*     */   public int[] getCounters() {
/*  64 */     return this.myCounters;
/*     */   }
/*     */   
/*     */   public long[] getSampleStartTs() {
/*  68 */     return this.mySampleStartTs;
/*     */   }
/*     */   
/*     */   public int getOutOfScaleCounter() {
/*  72 */     return this.myOutOfScaleCounter;
/*     */   }
/*     */   
/*     */   public long getMaxStartTs() {
/*  76 */     return this.myMaxStartTs;
/*     */   }
/*     */   
/*     */   public long getMax() {
/*  80 */     return this.myMax;
/*     */   }
/*     */   
/*     */   public int getTypicalIndex() {
/*  84 */     int max = 0;
/*  85 */     int maxIdx = -1;
/*  86 */     for (int i = 0; i < this.myCounters.length; i++) {
/*  87 */       int counter = this.myCounters[i];
/*  88 */       if (counter > max) {
/*  89 */         max = counter;
/*  90 */         maxIdx = i;
/*     */       } 
/*     */     } 
/*  93 */     return maxIdx;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private TimeDistribution(DistributionParameters parameters, int[] counters, long[] sampleStartTs, int outOfScaleCounter, long maxStartTs, long max) {
/* 101 */     this.myParameters = parameters;
/* 102 */     this.myEndTimes = new long[this.myParameters.getScaleSize()];
/* 103 */     fillEndTimes();
/* 104 */     this.myCounters = counters;
/* 105 */     this.mySampleStartTs = sampleStartTs;
/* 106 */     this.myOutOfScaleCounter = outOfScaleCounter;
/* 107 */     this.myMaxStartTs = maxStartTs;
/* 108 */     this.myMax = max;
/*     */   }
/*     */   
/*     */   public void register(long startTs, long duration) {
/* 112 */     if (duration > this.myMax) {
/* 113 */       this.myMaxStartTs = startTs;
/* 114 */       this.myMax = duration;
/*     */     } 
/* 116 */     int idx = Arrays.binarySearch(this.myEndTimes, duration);
/* 117 */     int updateIdx = (idx >= 0) ? idx : (-1 - idx);
/* 118 */     if (updateIdx == this.myParameters.getScaleSize()) {
/* 119 */       this.myOutOfScaleCounter++;
/*     */     } else {
/* 121 */       this.myCounters[updateIdx] = this.myCounters[updateIdx] + 1;
/* 122 */       if (this.mySampleStartTs[updateIdx] == -1L) this.mySampleStartTs[updateIdx] = startTs; 
/*     */     } 
/*     */   }
/*     */   
/*     */   public static RawSerializer<TimeDistribution> getSerializer(@NotNull final DistributionParameters parameters) {
/* 127 */     if (parameters == null) $$$reportNull$$$0(0);  final int size = parameters.getScaleSize();
/* 128 */     final LongArraySerializer longArraySerializer = new LongArraySerializer(size);
/* 129 */     final IntArraySerializer intArraySerializer = new IntArraySerializer(size);
/*     */     
/* 131 */     return new RawSerializer<TimeDistribution>()
/*     */       {
/*     */         public long getRecordSize() {
/* 134 */           return size * 12L + 4L + 16L;
/*     */         }
/*     */ 
/*     */         
/*     */         public void write(@NotNull DataOutput os, @NotNull TimeDistribution distribution) throws IOException {
/* 139 */           if (os == null) $$$reportNull$$$0(0);  if (distribution == null) $$$reportNull$$$0(1);  longArraySerializer.write(os, distribution.mySampleStartTs);
/* 140 */           intArraySerializer.write(os, distribution.myCounters);
/* 141 */           RawSerializer.Helper.serializeInt(distribution.myOutOfScaleCounter, os);
/* 142 */           RawSerializer.Helper.serializeLong(distribution.myMaxStartTs, os);
/* 143 */           RawSerializer.Helper.serializeLong(distribution.myMax, os);
/*     */         }
/*     */ 
/*     */         
/*     */         public TimeDistribution read(@NotNull DataInput is) throws IOException {
/* 148 */           if (is == null) $$$reportNull$$$0(2);  long[] sampleStartTs = longArraySerializer.read(is);
/* 149 */           int[] counters = intArraySerializer.read(is);
/* 150 */           int outOfScale = RawSerializer.Helper.deserializeInt(is);
/* 151 */           long maxStartTs = RawSerializer.Helper.deserializeLong(is);
/* 152 */           long max = RawSerializer.Helper.deserializeLong(is);
/*     */           
/* 154 */           return new TimeDistribution(parameters, counters, sampleStartTs, outOfScale, maxStartTs, max);
/*     */         }
/*     */       };
/*     */   }
/*     */   
/*     */   public static class DistributionParameters {
/*     */     private final long myUnit;
/*     */     private final int myScaleSize;
/*     */     
/*     */     public DistributionParameters(long unit, int scaleSize) {
/* 164 */       this.myUnit = unit;
/* 165 */       this.myScaleSize = scaleSize;
/*     */     }
/*     */     
/*     */     public long getUnit() {
/* 169 */       return this.myUnit;
/*     */     }
/*     */     
/*     */     public int getScaleSize() {
/* 173 */       return this.myScaleSize;
/*     */     }
/*     */   }
/*     */ }


/* Location:              C:\Program Files\JetBrains\WebStorm 2021.1\plugins\NodeJS\lib\NodeJS.jar!\com\jetbrains\nodejs\run\profile\cpu\v8log\calculation\TimeDistribution.class
 * Java compiler version: 11 (55.0)
 * JD-Core Version:       1.1.3
 */