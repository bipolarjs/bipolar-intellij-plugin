/*    */ package org.bipolar.run.profile.heap.io;
/*    */ 
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
/*    */ 
/*    */ public class LongRawSerializer
/*    */   implements RawSerializer<Long>
/*    */ {
/*    */   public long getRecordSize() {
/* 31 */     return 8L;
/*    */   }
/*    */ 
/*    */   
/*    */   public void write(@NotNull DataOutput os, @NotNull Long aLong) throws IOException {
/* 36 */     if (os == null) $$$reportNull$$$0(0);  if (aLong == null) $$$reportNull$$$0(1);  RawSerializer.Helper.serializeLong(aLong.longValue(), os);
/*    */   }
/*    */ 
/*    */   
/*    */   public Long read(@NotNull DataInput is) throws IOException {
/* 41 */     if (is == null) $$$reportNull$$$0(2);  return Long.valueOf(RawSerializer.Helper.deserializeLong(is));
/*    */   }
/*    */ }


/* Location:              C:\Program Files\JetBrains\WebStorm 2021.1\plugins\NodeJS\lib\NodeJS.jar!\com\jetbrains\nodejs\run\profile\heap\io\LongRawSerializer.class
 * Java compiler version: 11 (55.0)
 * JD-Core Version:       1.1.3
 */