/*    */ package org.bipolar.run.profile.heap.calculation;
/*    */ 
/*    */ import java.util.Arrays;
/*    */ 
/*    */ 
/*    */ 
/*    */ public class ByteArrayWrapper
/*    */ {
/*    */   private final byte[] myData;
/*    */   
/*    */   public ByteArrayWrapper(byte[] data) {
/* 12 */     this.myData = data;
/*    */   }
/*    */   
/*    */   public byte[] getData() {
/* 16 */     return this.myData;
/*    */   }
/*    */ 
/*    */   
/*    */   public boolean equals(Object o) {
/* 21 */     if (this == o) return true; 
/* 22 */     if (o == null || getClass() != o.getClass()) return false;
/*    */     
/* 24 */     ByteArrayWrapper wrapper = (ByteArrayWrapper)o;
/*    */     
/* 26 */     if (!Arrays.equals(this.myData, wrapper.myData)) return false;
/*    */     
/* 28 */     return true;
/*    */   }
/*    */ 
/*    */   
/*    */   public int hashCode() {
/* 33 */     return (this.myData != null) ? Arrays.hashCode(this.myData) : 0;
/*    */   }
/*    */ }


/* Location:              C:\Program Files\JetBrains\WebStorm 2021.1\plugins\NodeJS\lib\NodeJS.jar!\com\jetbrains\nodejs\run\profile\heap\calculation\ByteArrayWrapper.class
 * Java compiler version: 11 (55.0)
 * JD-Core Version:       1.1.3
 */