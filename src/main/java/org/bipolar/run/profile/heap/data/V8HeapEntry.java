/*     */ package org.bipolar.run.profile.heap.data;
/*     */ 
/*     */ import org.bipolar.run.profile.heap.io.Positioned;
/*     */ import org.bipolar.run.profile.heap.io.RawSerializer;
/*     */ import java.io.DataInput;
/*     */ import java.io.DataOutput;
/*     */ import java.io.IOException;
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
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public final class V8HeapEntry
/*     */   implements Positioned
/*     */ {
/*     */   private final V8HeapNodeType myType;
/*     */   private final long myNameId;
/*     */   private final long mySnapshotObjectId;
/*     */   private final long myChildrenCount;
/*     */   private final long mySize;
/*     */   private final long myTraceId;
/*     */   private final long myFileOffset;
/*     */   private long myEdgesOffset;
/*     */   private long myRetainedSize;
/*     */   private long myReverseEdgesOffset;
/*     */   private int myParentsCount;
/*     */   public static final long ourRecordSize = 84L;
/*     */   private int myDistance;
/*     */   
/*     */   private V8HeapEntry(V8HeapNodeType type, long nameId, long snapshotObjectId, long childrenCount, long size, long traceId, long offset, long edgesOffset, long retainedSize, int distance, long reverseEdgesOffset, int parentsCount) {
/*  56 */     this.myType = type;
/*  57 */     this.myNameId = nameId;
/*  58 */     this.mySnapshotObjectId = snapshotObjectId;
/*  59 */     this.myChildrenCount = childrenCount;
/*  60 */     this.mySize = size;
/*  61 */     this.myTraceId = traceId;
/*  62 */     this.myFileOffset = offset;
/*  63 */     this.myEdgesOffset = edgesOffset;
/*  64 */     this.myRetainedSize = retainedSize;
/*  65 */     this.myDistance = distance;
/*  66 */     this.myReverseEdgesOffset = reverseEdgesOffset;
/*  67 */     this.myParentsCount = parentsCount;
/*     */   }
/*     */ 
/*     */   
/*     */   public static V8HeapEntry createFromJson(int typeCode, long nameId, long snapshotId, long childrenCount, long size, long traceId, long seqNumber) {
/*  72 */     return new V8HeapEntry(V8HeapNodeType.getByNumber(typeCode), nameId, snapshotId, childrenCount, size, traceId, seqNumber * 84L, -1L, 0L, -1, 0L, 0);
/*     */   }
/*     */ 
/*     */   
/*     */   public void setEdgesOffset(long edgesOffset) {
/*  77 */     this.myEdgesOffset = edgesOffset;
/*     */   }
/*     */   
/*     */   public void setRetainedSize(long retainedSize) {
/*  81 */     this.myRetainedSize = retainedSize;
/*     */   }
/*     */   
/*     */   public long getReverseEdgesOffset() {
/*  85 */     return this.myReverseEdgesOffset;
/*     */   }
/*     */   
/*     */   public int getParentsCount() {
/*  89 */     return this.myParentsCount;
/*     */   }
/*     */   
/*     */   public void setReverseEdgesOffset(long reverseEdgesOffset) {
/*  93 */     this.myReverseEdgesOffset = reverseEdgesOffset;
/*     */   }
/*     */   
/*     */   public void setParentsCount(int parentsCount) {
/*  97 */     this.myParentsCount = parentsCount;
/*     */   }
/*     */   
/*     */   public V8HeapNodeType getType() {
/* 101 */     return this.myType;
/*     */   }
/*     */   
/*     */   public long getNameId() {
/* 105 */     return this.myNameId;
/*     */   }
/*     */   
/*     */   public long getSnapshotObjectId() {
/* 109 */     return this.mySnapshotObjectId;
/*     */   }
/*     */   
/*     */   public long getChildrenCount() {
/* 113 */     return this.myChildrenCount;
/*     */   }
/*     */   
/*     */   public long getSize() {
/* 117 */     return this.mySize;
/*     */   }
/*     */   
/*     */   public long getTraceId() {
/* 121 */     return this.myTraceId;
/*     */   }
/*     */   
/*     */   public long getRetainedSize() {
/* 125 */     return this.myRetainedSize;
/*     */   }
/*     */   
/*     */   public long getFileOffset() {
/* 129 */     return this.myFileOffset;
/*     */   }
/*     */   
/*     */   public long getEdgesOffset() {
/* 133 */     return this.myEdgesOffset;
/*     */   }
/*     */   
/*     */   public long getId() {
/* 137 */     return getFileOffset() / 84L;
/*     */   }
/*     */ 
/*     */   
/*     */   public long getOffset() {
/* 142 */     return getFileOffset();
/*     */   }
/*     */   
/*     */   public void setDistance(int distance) {
/* 146 */     this.myDistance = distance;
/*     */   }
/*     */   
/*     */   public int getDistance() {
/* 150 */     return this.myDistance;
/*     */   }
/*     */   
/*     */   public static class MyRawSerializer implements RawSerializer<V8HeapEntry> {
/* 154 */     private static final MyRawSerializer ourInstance = new MyRawSerializer();
/*     */     
/*     */     public static MyRawSerializer getInstance() {
/* 157 */       return ourInstance;
/*     */     }
/*     */ 
/*     */     
/*     */     public long getRecordSize() {
/* 162 */       return 84L;
/*     */     }
/*     */ 
/*     */     
/*     */     public void write(@NotNull DataOutput dout, @NotNull V8HeapEntry entry) throws IOException {
/* 167 */       if (dout == null) $$$reportNull$$$0(0);  if (entry == null) $$$reportNull$$$0(1);  RawSerializer.Helper.serializeInt(entry.getType().getNumber(), dout);
/*     */       
/* 169 */       RawSerializer.Helper.serializeLong(entry.getNameId(), dout);
/* 170 */       RawSerializer.Helper.serializeLong(entry.getSnapshotObjectId(), dout);
/*     */       
/* 172 */       RawSerializer.Helper.serializeLong(entry.getChildrenCount(), dout);
/* 173 */       RawSerializer.Helper.serializeLong(entry.getSize(), dout);
/* 174 */       RawSerializer.Helper.serializeLong(entry.getTraceId(), dout);
/*     */       
/* 176 */       RawSerializer.Helper.serializeLong(entry.getFileOffset(), dout);
/* 177 */       RawSerializer.Helper.serializeLong(entry.myEdgesOffset, dout);
/* 178 */       RawSerializer.Helper.serializeLong(entry.getRetainedSize(), dout);
/*     */       
/* 180 */       RawSerializer.Helper.serializeInt(entry.getDistance(), dout);
/*     */       
/* 182 */       RawSerializer.Helper.serializeLong(entry.getReverseEdgesOffset(), dout);
/* 183 */       RawSerializer.Helper.serializeInt(entry.getParentsCount(), dout);
/*     */     }
/*     */ 
/*     */     
/*     */     public V8HeapEntry read(@NotNull DataInput in) throws IOException {
/* 188 */       if (in == null) $$$reportNull$$$0(2);  int type = RawSerializer.Helper.deserializeInt(in);
/* 189 */       long nameId = RawSerializer.Helper.deserializeLong(in);
/* 190 */       long snapshotId = RawSerializer.Helper.deserializeLong(in);
/*     */       
/* 192 */       long childrenCount = RawSerializer.Helper.deserializeLong(in);
/* 193 */       long size = RawSerializer.Helper.deserializeLong(in);
/* 194 */       long traceId = RawSerializer.Helper.deserializeLong(in);
/*     */       
/* 196 */       long fileOffset = RawSerializer.Helper.deserializeLong(in);
/* 197 */       long edgesOffset = RawSerializer.Helper.deserializeLong(in);
/* 198 */       long retainedSize = RawSerializer.Helper.deserializeLong(in);
/* 199 */       int distance = RawSerializer.Helper.deserializeInt(in);
/* 200 */       long reverseEdgesOffset = RawSerializer.Helper.deserializeLong(in);
/* 201 */       int parentsCount = RawSerializer.Helper.deserializeInt(in);
/*     */       
/* 203 */       return new V8HeapEntry(V8HeapNodeType.getByNumber(type), nameId, snapshotId, childrenCount, size, traceId, fileOffset, edgesOffset, retainedSize, distance, reverseEdgesOffset, parentsCount);
/*     */     }
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean equals(Object o) {
/* 210 */     if (this == o) return true; 
/* 211 */     if (o == null || getClass() != o.getClass()) return false;
/*     */     
/* 213 */     V8HeapEntry entry = (V8HeapEntry)o;
/*     */     
/* 215 */     if (this.mySnapshotObjectId != entry.mySnapshotObjectId) return false;
/*     */     
/* 217 */     return true;
/*     */   }
/*     */ 
/*     */   
/*     */   public int hashCode() {
/* 222 */     return (int)(this.mySnapshotObjectId ^ this.mySnapshotObjectId >>> 32L);
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
/*     */   public long getClassIndex() {
/* 237 */     if (V8HeapNodeType.kObject.equals(this.myType) || V8HeapNodeType.kNative.equals(this.myType)) {
/* 238 */       return this.myNameId;
/*     */     }
/* 240 */     return (-1 - this.myType.getNumber());
/*     */   }
/*     */ }


/* Location:              C:\Program Files\JetBrains\WebStorm 2021.1\plugins\NodeJS\lib\NodeJS.jar!\com\jetbrains\nodejs\run\profile\heap\data\V8HeapEntry.class
 * Java compiler version: 11 (55.0)
 * JD-Core Version:       1.1.3
 */