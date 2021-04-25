/*    */ package org.bipolar.run.profile.heap.io;
/*    */ 
/*    */ import com.intellij.openapi.util.Pair;
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
/*    */ public class RawLongLongSerializer
/*    */   implements RawSerializer<Pair<Long, Long>>
/*    */ {
/* 29 */   private static final RawLongLongSerializer ourInstance = new RawLongLongSerializer();
/*    */   
/*    */   public static RawLongLongSerializer getInstance() {
/* 32 */     return ourInstance;
/*    */   }
/*    */ 
/*    */   
/*    */   public long getRecordSize() {
/* 37 */     return 16L;
/*    */   }
/*    */ 
/*    */   
/*    */   public void write(@NotNull DataOutput os, @NotNull Pair<Long, Long> pair) throws IOException {
/* 42 */     if (os == null) $$$reportNull$$$0(0);  if (pair == null) $$$reportNull$$$0(1);  RawSerializer.Helper.serializeLong(((Long)pair.getFirst()).longValue(), os);
/* 43 */     RawSerializer.Helper.serializeLong(((Long)pair.getSecond()).longValue(), os);
/*    */   }
/*    */ 
/*    */   
/*    */   public Pair<Long, Long> read(@NotNull DataInput is) throws IOException {
/* 48 */     if (is == null) $$$reportNull$$$0(2);  return Pair.create(Long.valueOf(RawSerializer.Helper.deserializeLong(is)), Long.valueOf(RawSerializer.Helper.deserializeLong(is)));
/*    */   }
/*    */ }


/* Location:              C:\Program Files\JetBrains\WebStorm 2021.1\plugins\NodeJS\lib\NodeJS.jar!\com\jetbrains\nodejs\run\profile\heap\io\RawLongLongSerializer.class
 * Java compiler version: 11 (55.0)
 * JD-Core Version:       1.1.3
 */