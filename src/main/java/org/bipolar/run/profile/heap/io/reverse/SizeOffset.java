/*    */ package org.bipolar.run.profile.heap.io.reverse;
/*    */ 
/*    */ import org.bipolar.run.profile.heap.io.RawSerializer;
/*    */ import java.io.DataInput;
/*    */ import java.io.DataOutput;
/*    */ import java.io.IOException;
/*    */ import org.jetbrains.annotations.NotNull;
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public class SizeOffset
/*    */ {
/*    */   private final int mySize;
/*    */   private final long myOffset;
/*    */   
/*    */   public SizeOffset(int size, long offset) {
/* 33 */     this.mySize = size;
/* 34 */     this.myOffset = offset;
/*    */   }
/*    */   
/*    */   public int getSize() {
/* 38 */     return this.mySize;
/*    */   }
/*    */   
/*    */   public long getOffset() {
/* 42 */     return this.myOffset;
/*    */   }
/*    */   
/*    */   public static class MySerializer implements RawSerializer<SizeOffset> {
/* 46 */     private static final MySerializer ourInstance = new MySerializer();
/*    */     
/*    */     public static MySerializer getInstance() {
/* 49 */       return ourInstance;
/*    */     }
/*    */ 
/*    */     
/*    */     public long getRecordSize() {
/* 54 */       return 12L;
/*    */     }
/*    */ 
/*    */     
/*    */     public void write(@NotNull DataOutput os, @NotNull SizeOffset offset) throws IOException {
/* 59 */       if (os == null) $$$reportNull$$$0(0);  if (offset == null) $$$reportNull$$$0(1);  RawSerializer.Helper.serializeInt(offset.getSize(), os);
/* 60 */       RawSerializer.Helper.serializeLong(offset.getOffset(), os);
/*    */     }
/*    */ 
/*    */     
/*    */     public SizeOffset read(@NotNull DataInput is) throws IOException {
/* 65 */       if (is == null) $$$reportNull$$$0(2);  return new SizeOffset(RawSerializer.Helper.deserializeInt(is), RawSerializer.Helper.deserializeLong(is));
/*    */     }
/*    */   }
/*    */ }


/* Location:              C:\Program Files\JetBrains\WebStorm 2021.1\plugins\NodeJS\lib\NodeJS.jar!\com\jetbrains\nodejs\run\profile\heap\io\reverse\SizeOffset.class
 * Java compiler version: 11 (55.0)
 * JD-Core Version:       1.1.3
 */