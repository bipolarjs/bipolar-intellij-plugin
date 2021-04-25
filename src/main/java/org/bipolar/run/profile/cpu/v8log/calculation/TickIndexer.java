/*     */ package org.bipolar.run.profile.cpu.v8log.calculation;
/*     */ 
/*     */ import org.bipolar.run.profile.heap.io.IntegerRawSerializer;
/*     */ import org.bipolar.run.profile.heap.io.RawSerializer;
/*     */ import java.io.DataInput;
/*     */ import java.io.DataOutput;
/*     */ import java.io.IOException;
/*     */ import java.util.ArrayList;
/*     */ import java.util.List;
/*     */ import org.jetbrains.annotations.NotNull;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class TickIndexer
/*     */ {
/*     */   private final List<Integer> myFloorTickIdx;
/*     */   private final List<Integer> myCeilingTickIdx;
/*     */   private final long myIndexInterval;
/*     */   private int myNextIdx;
/*     */   private int myTickIdx;
/*     */   private long myLastTick;
/*     */   
/*     */   public TickIndexer(long indexInterval) {
/*  27 */     this.myIndexInterval = indexInterval;
/*  28 */     this.myFloorTickIdx = new ArrayList<>();
/*  29 */     this.myCeilingTickIdx = new ArrayList<>();
/*  30 */     this.myNextIdx = 1;
/*  31 */     this.myTickIdx = 0;
/*  32 */     this.myLastTick = 0L;
/*     */   }
/*     */   
/*     */   private TickIndexer(List<Integer> floorTickIdx, List<Integer> ceilingTickIdx, int tickIdx, long lastTick, long indexInterval) {
/*  36 */     this.myFloorTickIdx = floorTickIdx;
/*  37 */     this.myCeilingTickIdx = ceilingTickIdx;
/*  38 */     this.myTickIdx = tickIdx;
/*  39 */     this.myLastTick = lastTick;
/*  40 */     this.myIndexInterval = indexInterval;
/*     */   }
/*     */   
/*     */   public void nextTick(long tick) {
/*  44 */     assert this.myIndexInterval > 0L;
/*  45 */     if (tick < this.myLastTick) {
/*  46 */       this.myTickIdx++;
/*     */       return;
/*     */     } 
/*  49 */     if (tick >= this.myNextIdx * this.myIndexInterval) {
/*  50 */       int toIdx = (int)Math.floor(tick / this.myIndexInterval);
/*  51 */       int floor = (this.myTickIdx == 0) ? 0 : (this.myTickIdx - 1);
/*  52 */       for (int i = this.myNextIdx; i <= toIdx; i++) {
/*  53 */         this.myFloorTickIdx.add(Integer.valueOf(floor));
/*  54 */         this.myCeilingTickIdx.add(Integer.valueOf(this.myTickIdx));
/*     */       } 
/*  56 */       this.myNextIdx = toIdx + 1;
/*     */     } 
/*  58 */     this.myLastTick = tick;
/*  59 */     this.myTickIdx++;
/*     */   }
/*     */   
/*     */   public int getNumTicks() {
/*  63 */     return this.myTickIdx;
/*     */   }
/*     */   
/*     */   public long getLastTick() {
/*  67 */     return this.myLastTick;
/*     */   }
/*     */   
/*     */   public boolean isEmpty() {
/*  71 */     return (this.myFloorTickIdx.isEmpty() || this.myCeilingTickIdx.isEmpty());
/*     */   }
/*     */   
/*     */   public Integer getFloorIndexFor(long ts) {
/*  75 */     assert ts >= 0L;
/*  76 */     if (this.myFloorTickIdx.isEmpty()) return Integer.valueOf(0); 
/*  77 */     int floor = (int)Math.floor(ts / this.myIndexInterval);
/*  78 */     return Integer.valueOf((floor > this.myFloorTickIdx.size()) ? ((Integer)this.myFloorTickIdx.get(this.myFloorTickIdx.size() - 1)).intValue() : (
/*  79 */         (floor == 0) ? 0 : ((Integer)this.myFloorTickIdx.get(floor - 1)).intValue()));
/*     */   }
/*     */   
/*     */   public Integer getCeilIndexFor(long ts) {
/*  83 */     assert ts >= 0L;
/*  84 */     if (this.myCeilingTickIdx.isEmpty()) return Integer.valueOf(this.myTickIdx - 1); 
/*  85 */     int ceil = (int)Math.ceil(ts / this.myIndexInterval);
/*  86 */     if (this.myCeilingTickIdx.isEmpty()) return Integer.valueOf(-1); 
/*  87 */     return Integer.valueOf((ceil > this.myCeilingTickIdx.size()) ? ((Integer)this.myCeilingTickIdx.get(this.myCeilingTickIdx.size() - 1)).intValue() : (
/*  88 */         (ceil == 0) ? 0 : ((Integer)this.myCeilingTickIdx.get(ceil - 1)).intValue()));
/*     */   }
/*     */   
/*     */   public static class MySerializer
/*     */     implements RawSerializer<TickIndexer> {
/*     */     public long getRecordSize() {
/*  94 */       return -1L;
/*     */     }
/*     */ 
/*     */     
/*     */     public void write(@NotNull DataOutput os, @NotNull TickIndexer tickIndexer) throws IOException {
/*  99 */       if (os == null) $$$reportNull$$$0(0);  if (tickIndexer == null) $$$reportNull$$$0(1);  os.writeLong(tickIndexer.getLastTick());
/* 100 */       os.writeInt(tickIndexer.getNumTicks());
/* 101 */       os.writeLong(tickIndexer.myIndexInterval);
/* 102 */       writeList(os, tickIndexer.myFloorTickIdx);
/* 103 */       writeList(os, tickIndexer.myCeilingTickIdx);
/*     */     }
/*     */     
/*     */     private void writeList(@NotNull DataOutput os, @NotNull List<Integer> list) throws IOException {
/* 107 */       if (os == null) $$$reportNull$$$0(2);  if (list == null) $$$reportNull$$$0(3);  os.writeInt(list.size());
/* 108 */       IntegerRawSerializer serializer = new IntegerRawSerializer();
/* 109 */       for (Integer integer : list) {
/* 110 */         serializer.write(os, integer);
/*     */       }
/*     */     }
/*     */     
/*     */     private List<Integer> readList(@NotNull DataInput is) throws IOException {
/* 115 */       if (is == null) $$$reportNull$$$0(4);  int size = is.readInt();
/* 116 */       List<Integer> list = new ArrayList<>(size);
/* 117 */       IntegerRawSerializer serializer = new IntegerRawSerializer();
/* 118 */       for (int i = 0; i < size; i++) {
/* 119 */         list.add(serializer.read(is));
/*     */       }
/* 121 */       return list;
/*     */     }
/*     */ 
/*     */     
/*     */     public TickIndexer read(@NotNull DataInput is) throws IOException {
/* 126 */       if (is == null) $$$reportNull$$$0(5);  long lastTick = is.readLong();
/* 127 */       int numTicks = is.readInt();
/* 128 */       long indexInterval = is.readLong();
/*     */       
/* 130 */       return new TickIndexer(readList(is), readList(is), numTicks, lastTick, indexInterval);
/*     */     }
/*     */   }
/*     */ }


/* Location:              C:\Program Files\JetBrains\WebStorm 2021.1\plugins\NodeJS\lib\NodeJS.jar!\com\jetbrains\nodejs\run\profile\cpu\v8log\calculation\TickIndexer.class
 * Java compiler version: 11 (55.0)
 * JD-Core Version:       1.1.3
 */