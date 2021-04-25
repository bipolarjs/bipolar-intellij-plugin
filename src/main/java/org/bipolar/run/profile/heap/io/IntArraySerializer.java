/*    */ package org.bipolar.run.profile.heap.io;
/*    */ 
/*    */ import java.io.DataInput;
/*    */ import java.io.DataOutput;
/*    */ import java.io.IOException;
/*    */ import org.jetbrains.annotations.NotNull;
/*    */ 
/*    */ 
/*    */ 
/*    */ public class IntArraySerializer
/*    */   implements RawSerializer<int[]>
/*    */ {
/*    */   private final int mySize;
/*    */   
/*    */   public IntArraySerializer(int size) {
/* 16 */     this.mySize = size;
/*    */   }
/*    */ 
/*    */   
/*    */   public long getRecordSize() {
/* 21 */     return 4L * this.mySize;
/*    */   }
/*    */ 
/*    */   
/*    */   public void write(@NotNull DataOutput os, int[] ints) throws IOException {
/* 26 */     if (os == null) $$$reportNull$$$0(0);  if (ints == null) $$$reportNull$$$0(1);  for (int anInteger : ints) {
/* 27 */       RawSerializer.Helper.serializeInt(anInteger, os);
/*    */     }
/*    */   }
/*    */ 
/*    */   
/*    */   public int[] read(@NotNull DataInput is) throws IOException {
/* 33 */     if (is == null) $$$reportNull$$$0(2);  int[] array = new int[this.mySize];
/* 34 */     for (int i = 0; i < this.mySize; i++) {
/* 35 */       array[i] = RawSerializer.Helper.deserializeInt(is);
/*    */     }
/* 37 */     return array;
/*    */   }
/*    */ }


/* Location:              C:\Program Files\JetBrains\WebStorm 2021.1\plugins\NodeJS\lib\NodeJS.jar!\com\jetbrains\nodejs\run\profile\heap\io\IntArraySerializer.class
 * Java compiler version: 11 (55.0)
 * JD-Core Version:       1.1.3
 */