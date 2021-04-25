/*    */ package org.bipolar.run.profile.heap.io;
/*    */ 
/*    */ import java.io.DataInput;
/*    */ import java.io.DataOutput;
/*    */ import java.io.IOException;
/*    */ import org.jetbrains.annotations.NotNull;
/*    */ 
/*    */ 
/*    */ 
/*    */ public class LongArraySerializer
/*    */   implements RawSerializer<long[]>
/*    */ {
/*    */   private final int mySize;
/*    */   
/*    */   public LongArraySerializer(int size) {
/* 16 */     this.mySize = size;
/*    */   }
/*    */ 
/*    */   
/*    */   public long getRecordSize() {
/* 21 */     return 8L * this.mySize;
/*    */   }
/*    */ 
/*    */   
/*    */   public void write(@NotNull DataOutput os, long[] longs) throws IOException {
/* 26 */     if (os == null) $$$reportNull$$$0(0);  if (longs == null) $$$reportNull$$$0(1);  for (long aLong : longs) {
/* 27 */       RawSerializer.Helper.serializeLong(aLong, os);
/*    */     }
/*    */   }
/*    */ 
/*    */   
/*    */   public long[] read(@NotNull DataInput is) throws IOException {
/* 33 */     if (is == null) $$$reportNull$$$0(2);  long[] array = new long[this.mySize];
/* 34 */     for (int i = 0; i < this.mySize; i++) {
/* 35 */       array[i] = RawSerializer.Helper.deserializeLong(is);
/*    */     }
/* 37 */     return array;
/*    */   }
/*    */ }


/* Location:              C:\Program Files\JetBrains\WebStorm 2021.1\plugins\NodeJS\lib\NodeJS.jar!\com\jetbrains\nodejs\run\profile\heap\io\LongArraySerializer.class
 * Java compiler version: 11 (55.0)
 * JD-Core Version:       1.1.3
 */