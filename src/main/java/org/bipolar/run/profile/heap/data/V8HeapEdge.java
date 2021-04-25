/*     */ package org.bipolar.run.profile.heap.data;
/*     */ 
/*     */ import org.bipolar.run.profile.heap.V8CachingReader;
/*     */ import org.bipolar.run.profile.heap.io.Positioned;
/*     */ import org.bipolar.run.profile.heap.io.RawSerializer;
/*     */ import java.io.DataInput;
/*     */ import java.io.DataOutput;
/*     */ import java.io.IOException;
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
/*     */ 
/*     */ public final class V8HeapEdge
/*     */   implements Positioned
/*     */ {
/*     */   private static final byte BACK_MASK = 1;
/*     */   private static final byte RESOLVED_MASK = 16;
/*     */   private final V8HeapGraphEdgeType myType;
/*     */   private final long myNameId;
/*     */   private final long myToIndex;
/*     */   private long myFromIndex;
/*     */   private final long myFileOffset;
/*     */   private final byte myIsBack;
/*     */   public static final long ourRecordSize = 37L;
/*     */   
/*     */   private V8HeapEdge(V8HeapGraphEdgeType type, long nameId, long fromIndex, long toIndex, long offset, byte isBack) {
/*  47 */     this.myType = type;
/*     */     
/*  49 */     this.myNameId = nameId;
/*  50 */     this.myFromIndex = fromIndex;
/*  51 */     this.myToIndex = toIndex;
/*  52 */     this.myFileOffset = offset;
/*  53 */     this.myIsBack = isBack;
/*     */   }
/*     */   
/*     */   public static V8HeapEdge createFromJson(int typeCode, long nameId, long toIndex, long seqNumber) {
/*  57 */     return new V8HeapEdge(V8HeapGraphEdgeType.getByNumber(typeCode), nameId, -1L, toIndex / 6L, seqNumber * 37L, (byte)0);
/*     */   }
/*     */   
/*     */   public void setFromIndex(long fromIndex) {
/*  61 */     this.myFromIndex = fromIndex;
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean hasStringName() {
/*  66 */     return (!V8HeapGraphEdgeType.kElement.equals(this.myType) && !V8HeapGraphEdgeType.kHidden.equals(this.myType) && 
/*  67 */       !V8HeapGraphEdgeType.kWeak.equals(this.myType));
/*     */   }
/*     */   @Nls
/*     */   private String _name(@NotNull V8CachingReader objectsProxyReader) {
/*  71 */     if (objectsProxyReader == null) $$$reportNull$$$0(0);  return hasStringName() ? objectsProxyReader.getString(this.myNameId) : String.valueOf(this.myNameId);
/*     */   }
/*     */   
/*     */   public Integer nameAsInt(@NotNull V8CachingReader objectsProxyReader) {
/*  75 */     if (objectsProxyReader == null) $$$reportNull$$$0(1);  String name = _name(objectsProxyReader);
/*     */     try {
/*  77 */       return Integer.valueOf(Integer.parseInt(name));
/*  78 */     } catch (NumberFormatException e) {
/*  79 */       return null;
/*     */     } 
/*     */   }
/*     */   @Nls
/*     */   public String getPresentation(@NotNull V8CachingReader objectsProxyReader) {
/*  84 */     if (objectsProxyReader == null) $$$reportNull$$$0(2);  String name = _name(objectsProxyReader);
/*  85 */     Integer isNumber = null;
/*  86 */     if (V8HeapGraphEdgeType.kShortcut.equals(getType())) {
/*     */       try {
/*  88 */         isNumber = Integer.valueOf(Integer.parseInt(name));
/*  89 */       } catch (NumberFormatException e) {
/*  90 */         isNumber = null;
/*     */       } 
/*     */     }
/*     */     
/*  94 */     switch (getType()) {
/*     */       case kContextVariable:
/*  96 */         return "->" + name;
/*     */       case kElement:
/*  98 */         return "[" + name + "]";
/*     */       case kWeak:
/* 100 */         return "[[" + name + "]]";
/*     */       case kProperty:
/* 102 */         return !name.contains(" ") ? ("." + name) : ("[\"" + name + "\"]");
/*     */       case kShortcut:
/* 104 */         if (isNumber != null) {
/* 105 */           return "[" + name + "]";
/*     */         }
/*     */         
/* 108 */         return !name.contains(" ") ? ("." + name) : ("[\"" + name + "\"]");
/*     */       
/*     */       case kInternal:
/*     */       case kHidden:
/*     */       case kInvisible:
/* 113 */         return "{" + name + "}";
/*     */     } 
/* 115 */     return "?" + name + "?";
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
/*     */   public V8HeapGraphEdgeType getType() {
/* 134 */     return this.myType;
/*     */   }
/*     */   
/*     */   public long getNameId() {
/* 138 */     return this.myNameId;
/*     */   }
/*     */   
/*     */   public long getToIndex() {
/* 142 */     return this.myToIndex;
/*     */   }
/*     */   
/*     */   public long getFileOffset() {
/* 146 */     return this.myFileOffset;
/*     */   }
/*     */   
/*     */   public long getId() {
/* 150 */     return getFileOffset() / 37L;
/*     */   }
/*     */   
/*     */   public long getFromIndex() {
/* 154 */     return this.myFromIndex;
/*     */   }
/*     */ 
/*     */   
/*     */   public long getOffset() {
/* 159 */     return getFileOffset();
/*     */   }
/*     */   
/*     */   public static class MyRawSerializer implements RawSerializer<V8HeapEdge> {
/* 163 */     private static final MyRawSerializer ourInstance = new MyRawSerializer();
/*     */     
/*     */     public static MyRawSerializer getInstance() {
/* 166 */       return ourInstance;
/*     */     }
/*     */ 
/*     */     
/*     */     public long getRecordSize() {
/* 171 */       return 37L;
/*     */     }
/*     */ 
/*     */     
/*     */     public void write(@NotNull DataOutput dout, @NotNull V8HeapEdge edge) throws IOException {
/* 176 */       if (dout == null) $$$reportNull$$$0(0);  if (edge == null) $$$reportNull$$$0(1);  RawSerializer.Helper.serializeInt(edge.getType().getNumber(), dout);
/* 177 */       RawSerializer.Helper.serializeLong(edge.getNameId(), dout);
/* 178 */       RawSerializer.Helper.serializeLong(edge.getFromIndex(), dout);
/* 179 */       RawSerializer.Helper.serializeLong(edge.getToIndex(), dout);
/*     */       
/* 181 */       RawSerializer.Helper.serializeLong(edge.getFileOffset(), dout);
/* 182 */       dout.writeByte(edge.myIsBack);
/*     */     }
/*     */ 
/*     */     
/*     */     public V8HeapEdge read(@NotNull DataInput in) throws IOException {
/* 187 */       if (in == null) $$$reportNull$$$0(2);  int typeId = RawSerializer.Helper.deserializeInt(in);
/* 188 */       long nameId = RawSerializer.Helper.deserializeLong(in);
/* 189 */       long fromIndex = RawSerializer.Helper.deserializeLong(in);
/* 190 */       long toIndex = RawSerializer.Helper.deserializeLong(in);
/*     */       
/* 192 */       long fileOffset = RawSerializer.Helper.deserializeLong(in);
/* 193 */       byte isBack = in.readByte();
/*     */       
/* 195 */       return new V8HeapEdge(V8HeapGraphEdgeType.getByNumber(typeId), nameId, fromIndex, toIndex, fileOffset, isBack);
/*     */     }
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean equals(Object o) {
/* 201 */     if (this == o) return true; 
/* 202 */     if (o == null || getClass() != o.getClass()) return false;
/*     */     
/* 204 */     V8HeapEdge edge = (V8HeapEdge)o;
/*     */     
/* 206 */     if (this.myFileOffset != edge.myFileOffset) return false;
/*     */     
/* 208 */     return true;
/*     */   }
/*     */ 
/*     */   
/*     */   public int hashCode() {
/* 213 */     return (int)(this.myFileOffset ^ this.myFileOffset >>> 32L);
/*     */   }
/*     */ }


/* Location:              C:\Program Files\JetBrains\WebStorm 2021.1\plugins\NodeJS\lib\NodeJS.jar!\com\jetbrains\nodejs\run\profile\heap\data\V8HeapEdge.class
 * Java compiler version: 11 (55.0)
 * JD-Core Version:       1.1.3
 */