/*     */ package org.bipolar.run.profile.heap.data;
/*     */ 
/*     */ import org.bipolar.run.profile.heap.V8CachingReader;
/*     */ import java.io.Externalizable;
/*     */ import java.io.IOException;
/*     */ import java.io.ObjectInput;
/*     */ import java.io.ObjectOutput;
/*     */ import org.jetbrains.annotations.Nls;
/*     */ import org.jetbrains.annotations.NotNull;
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
/*     */ public class Aggregate
/*     */   implements Externalizable, Presentable
/*     */ {
/*     */   private int myId;
/*     */   private long myClassIdx;
/*     */   private int myDistance;
/*     */   private long mySelfSize;
/*     */   private long myRetained;
/*     */   private int myCnt;
/*     */   private V8HeapNodeType myType;
/*     */   
/*     */   public Aggregate() {}
/*     */   
/*     */   public Aggregate(int id, long classIdx, int distance, long selfSize, V8HeapNodeType type) {
/*  45 */     this.myId = id;
/*  46 */     this.myClassIdx = classIdx;
/*  47 */     this.myDistance = distance;
/*  48 */     this.mySelfSize = selfSize;
/*  49 */     this.myType = type;
/*  50 */     this.myRetained = 0L;
/*  51 */     this.myCnt = 1;
/*     */   }
/*     */   
/*     */   public int getId() {
/*  55 */     return this.myId;
/*     */   }
/*     */   
/*     */   public long getClassIdx() {
/*  59 */     return this.myClassIdx;
/*     */   }
/*     */   
/*     */   public int getDistance() {
/*  63 */     return this.myDistance;
/*     */   }
/*     */   
/*     */   public long getSelfSize() {
/*  67 */     return this.mySelfSize;
/*     */   }
/*     */   
/*     */   public V8HeapNodeType getType() {
/*  71 */     return this.myType;
/*     */   }
/*     */   
/*     */   public void setDistance(int distance) {
/*  75 */     this.myDistance = distance;
/*     */   }
/*     */   
/*     */   public void addSize(long size) {
/*  79 */     this.mySelfSize += size;
/*  80 */     this.myCnt++;
/*     */   }
/*     */   
/*     */   public void addRetained(long size) {
/*  84 */     this.myRetained += size;
/*     */   }
/*     */   
/*     */   public int getCnt() {
/*  88 */     return this.myCnt;
/*     */   }
/*     */   
/*     */   public long getRetained() {
/*  92 */     return this.myRetained;
/*     */   }
/*     */   
/*     */   @Nls
/*     */   public String getPresentation(@NotNull V8CachingReader reader) {
/*  97 */     if (reader == null) $$$reportNull$$$0(0);  return getClassNameByClassIdx(reader, this.myClassIdx);
/*     */   }
/*     */   @Nls
/*     */   public static String getClassNameByClassIdx(@NotNull V8CachingReader reader, long classIndex) {
/* 101 */     if (reader == null) $$$reportNull$$$0(1);  if (classIndex >= 0L) {
/* 102 */       return reader.getString(classIndex);
/*     */     }
/* 104 */     return "(" + V8HeapNodeType.getByNumber((int)-classIndex - 1).getName() + ")";
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public void writeExternal(ObjectOutput out) throws IOException {
/* 110 */     out.writeInt(this.myId);
/* 111 */     out.writeLong(this.myClassIdx);
/* 112 */     out.writeInt(this.myDistance);
/* 113 */     out.writeLong(this.mySelfSize);
/* 114 */     out.writeLong(this.myRetained);
/* 115 */     out.writeInt(this.myCnt);
/* 116 */     out.writeUTF(this.myType.name());
/*     */   }
/*     */ 
/*     */   
/*     */   public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
/* 121 */     this.myId = in.readInt();
/* 122 */     this.myClassIdx = in.readLong();
/* 123 */     this.myDistance = in.readInt();
/* 124 */     this.mySelfSize = in.readLong();
/* 125 */     this.myRetained = in.readLong();
/* 126 */     this.myCnt = in.readInt();
/*     */     try {
/* 128 */       this.myType = V8HeapNodeType.valueOf(in.readUTF());
/* 129 */     } catch (IllegalArgumentException e) {
/* 130 */       throw new IOException(e);
/*     */     } 
/*     */   }
/*     */ }


/* Location:              C:\Program Files\JetBrains\WebStorm 2021.1\plugins\NodeJS\lib\NodeJS.jar!\com\jetbrains\nodejs\run\profile\heap\data\Aggregate.class
 * Java compiler version: 11 (55.0)
 * JD-Core Version:       1.1.3
 */