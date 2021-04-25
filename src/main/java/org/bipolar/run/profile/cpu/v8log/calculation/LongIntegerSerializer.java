/*    */ package org.bipolar.run.profile.cpu.v8log.calculation;
/*    */ 
/*    */ import com.intellij.openapi.util.Pair;
/*    */ import org.bipolar.run.profile.heap.io.IntegerRawSerializer;
/*    */ import org.bipolar.run.profile.heap.io.LongRawSerializer;
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
/*    */ public class LongIntegerSerializer
/*    */   implements RawSerializer<Pair<Long, Integer>>
/*    */ {
/* 21 */   private final LongRawSerializer myLongRawSerializer = new LongRawSerializer();
/* 22 */   private final IntegerRawSerializer myIntegerRawSerializer = new IntegerRawSerializer();
/*    */ 
/*    */ 
/*    */   
/*    */   public long getRecordSize() {
/* 27 */     return 12L;
/*    */   }
/*    */ 
/*    */   
/*    */   public void write(@NotNull DataOutput os, @NotNull Pair<Long, Integer> pair) throws IOException {
/* 32 */     if (os == null) $$$reportNull$$$0(0);  if (pair == null) $$$reportNull$$$0(1);  this.myLongRawSerializer.write(os, (Long)pair.getFirst());
/* 33 */     this.myIntegerRawSerializer.write(os, (Integer)pair.getSecond());
/*    */   }
/*    */ 
/*    */   
/*    */   public Pair<Long, Integer> read(@NotNull DataInput is) throws IOException {
/* 38 */     if (is == null) $$$reportNull$$$0(2);  return Pair.create(this.myLongRawSerializer.read(is), this.myIntegerRawSerializer.read(is));
/*    */   }
/*    */ }


/* Location:              C:\Program Files\JetBrains\WebStorm 2021.1\plugins\NodeJS\lib\NodeJS.jar!\com\jetbrains\nodejs\run\profile\cpu\v8log\calculation\LongIntegerSerializer.class
 * Java compiler version: 11 (55.0)
 * JD-Core Version:       1.1.3
 */