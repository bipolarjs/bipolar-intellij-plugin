/*     */ package org.bipolar.run.profile.heap.calculation;
/*     */ 
/*     */ import org.bipolar.run.profile.V8Utils;
/*     */ import org.bipolar.run.profile.heap.data.Aggregate;
/*     */ import it.unimi.dsi.fastutil.ints.IntList;
/*     */ import it.unimi.dsi.fastutil.longs.LongArrayList;
/*     */ import it.unimi.dsi.fastutil.longs.LongList;
/*     */ import java.io.Externalizable;
/*     */ import java.io.IOException;
/*     */ import java.io.ObjectInput;
/*     */ import java.io.ObjectOutput;
/*     */ import java.util.ArrayList;
/*     */ import java.util.Comparator;
/*     */ import java.util.List;
/*     */ import java.util.Map;
/*     */ import java.util.TreeMap;
/*     */ 
/*     */ public final class V8HeapInMemoryIndexes
/*     */   implements Externalizable {
/*     */   private final Flags myFlags;
/*     */   private IntList myParents;
/*     */   private IntList myDistances;
/*     */   private IntList myUnreachable;
/*     */   private IntList myOnlyWeak;
/*     */   private List<Integer> myBiggest;
/*     */   private TreeMap<Long, Aggregate> myAggregateMap;
/*     */   private LongList myRetainedSizes;
/*     */   
/*     */   public V8HeapInMemoryIndexes() {
/*  30 */     this.myFlags = new Flags();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public V8HeapInMemoryIndexes(Flags flags, LongList retainedSizes, IntList parents, List<Integer> biggest, IntList distances, Map<Long, Aggregate> aggregateMap, IntList unreachable, IntList onlyWeak) {
/*  39 */     this.myFlags = flags;
/*  40 */     this.myRetainedSizes = retainedSizes;
/*  41 */     this.myParents = parents;
/*  42 */     this.myBiggest = biggest;
/*  43 */     this.myDistances = distances;
/*  44 */     this.myAggregateMap = new TreeMap<>(aggregateMap);
/*  45 */     this.myUnreachable = unreachable;
/*  46 */     this.myOnlyWeak = onlyWeak;
/*     */   }
/*     */   
/*     */   public Flags getFlags() {
/*  50 */     return this.myFlags;
/*     */   }
/*     */   
/*     */   public LongList getRetainedSizes() {
/*  54 */     return this.myRetainedSizes;
/*     */   }
/*     */   
/*     */   public IntList getParents() {
/*  58 */     return this.myParents;
/*     */   }
/*     */   
/*     */   public List<Integer> getBiggest() {
/*  62 */     return this.myBiggest;
/*     */   }
/*     */   
/*     */   public IntList getDistances() {
/*  66 */     return this.myDistances;
/*     */   }
/*     */   
/*     */   public TreeMap<Long, Aggregate> getAggregateMap() {
/*  70 */     return this.myAggregateMap;
/*     */   }
/*     */   
/*     */   public IntList getUnreachable() {
/*  74 */     return this.myUnreachable;
/*     */   }
/*     */   
/*     */   public IntList getOnlyWeak() {
/*  78 */     return this.myOnlyWeak;
/*     */   }
/*     */ 
/*     */   
/*     */   public void writeExternal(ObjectOutput out) throws IOException {
/*  83 */     this.myFlags.writeExternal(out);
/*  84 */     V8Utils.writeIntList(this.myParents, out);
/*  85 */     V8Utils.writeIntList(this.myDistances, out);
/*  86 */     V8Utils.writeIntList(this.myUnreachable, out);
/*  87 */     V8Utils.writeIntList(this.myOnlyWeak, out);
/*     */     
/*  89 */     out.writeInt(this.myBiggest.size());
/*  90 */     for (Integer integer : this.myBiggest) {
/*  91 */       out.writeInt(integer.intValue());
/*     */     }
/*     */     
/*  94 */     out.writeInt(this.myAggregateMap.size());
/*  95 */     for (Map.Entry<Long, Aggregate> entry : this.myAggregateMap.entrySet()) {
/*  96 */       out.writeLong(((Long)entry.getKey()).longValue());
/*  97 */       ((Aggregate)entry.getValue()).writeExternal(out);
/*     */     } 
/*     */     
/* 100 */     out.writeInt(this.myRetainedSizes.size());
/* 101 */     for (int i = 0; i < this.myRetainedSizes.size(); i++) {
/* 102 */       out.writeLong(this.myRetainedSizes.getLong(i));
/*     */     }
/*     */   }
/*     */ 
/*     */   
/*     */   public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
/* 108 */     this.myFlags.readExternal(in);
/* 109 */     this.myParents = V8Utils.readIntList(in);
/* 110 */     this.myDistances = V8Utils.readIntList(in);
/* 111 */     this.myUnreachable = V8Utils.readIntList(in);
/* 112 */     this.myOnlyWeak = V8Utils.readIntList(in);
/*     */     
/* 114 */     int biggestSize = in.readInt();
/* 115 */     this.myBiggest = new ArrayList<>();
/* 116 */     for (int i = 0; i < biggestSize; i++) {
/* 117 */       this.myBiggest.add(Integer.valueOf(in.readInt()));
/*     */     }
/*     */     
/* 120 */     int aggregateSize = in.readInt();
/* 121 */     this.myAggregateMap = new TreeMap<>();
/* 122 */     for (int j = 0; j < aggregateSize; j++) {
/* 123 */       long key = in.readLong();
/* 124 */       Aggregate aggregate = new Aggregate();
/* 125 */       aggregate.readExternal(in);
/* 126 */       this.myAggregateMap.put(Long.valueOf(key), aggregate);
/*     */     } 
/*     */     
/* 129 */     int retainedSize = in.readInt();
/* 130 */     this.myRetainedSizes = (LongList)new LongArrayList(retainedSize);
/* 131 */     for (int k = 0; k < retainedSize; k++) {
/* 132 */       this.myRetainedSizes.add(in.readLong());
/*     */     }
/*     */   }
/*     */   
/*     */   public void sortAggregates(Comparator<Long> comparator) {
/* 137 */     TreeMap<Long, Aggregate> map = this.myAggregateMap;
/* 138 */     this.myAggregateMap = new TreeMap<>(comparator);
/* 139 */     this.myAggregateMap.putAll(map);
/*     */   }
/*     */ }


/* Location:              C:\Program Files\JetBrains\WebStorm 2021.1\plugins\NodeJS\lib\NodeJS.jar!\com\jetbrains\nodejs\run\profile\heap\calculation\V8HeapInMemoryIndexes.class
 * Java compiler version: 11 (55.0)
 * JD-Core Version:       1.1.3
 */