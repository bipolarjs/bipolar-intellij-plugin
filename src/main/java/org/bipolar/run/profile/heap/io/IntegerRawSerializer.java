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
/*    */ public class IntegerRawSerializer
/*    */   implements RawSerializer<Integer>
/*    */ {
/*    */   public long getRecordSize() {
/* 30 */     return 4L;
/*    */   }
/*    */ 
/*    */   
/*    */   public void write(@NotNull DataOutput os, @NotNull Integer integer) throws IOException {
/* 35 */     if (os == null) $$$reportNull$$$0(0);  if (integer == null) $$$reportNull$$$0(1);  RawSerializer.Helper.serializeInt(integer.intValue(), os);
/*    */   }
/*    */ 
/*    */   
/*    */   public Integer read(@NotNull DataInput is) throws IOException {
/* 40 */     if (is == null) $$$reportNull$$$0(2);  return Integer.valueOf(RawSerializer.Helper.deserializeInt(is));
/*    */   }
/*    */ }


/* Location:              C:\Program Files\JetBrains\WebStorm 2021.1\plugins\NodeJS\lib\NodeJS.jar!\com\jetbrains\nodejs\run\profile\heap\io\IntegerRawSerializer.class
 * Java compiler version: 11 (55.0)
 * JD-Core Version:       1.1.3
 */