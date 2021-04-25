/*    */ package org.bipolar.run.profile.cpu.v8log.calculation;
/*    */ 
/*    */ import java.math.BigInteger;
/*    */ 
/*    */ 
/*    */ 
/*    */ public interface V8ProfileCallback
/*    */ {
/*  9 */   public static final V8ProfileCallback EMPTY = new V8ProfileCallback()
/*    */     {
/*    */       public void onUnknownMove(BigInteger addr) {}
/*    */ 
/*    */       
/*    */       public void onUnknownDelete(BigInteger addr) {}
/*    */ 
/*    */       
/*    */       public void onUnknownTick(BigInteger addr, long stackPos) {}
/*    */ 
/*    */       
/*    */       public boolean processFunction(String name) {
/* 21 */         return true;
/*    */       }
/*    */     };
/*    */   
/*    */   void onUnknownMove(BigInteger paramBigInteger);
/*    */   
/*    */   void onUnknownDelete(BigInteger paramBigInteger);
/*    */   
/*    */   void onUnknownTick(BigInteger paramBigInteger, long paramLong);
/*    */   
/*    */   boolean processFunction(String paramString);
/*    */ }


/* Location:              C:\Program Files\JetBrains\WebStorm 2021.1\plugins\NodeJS\lib\NodeJS.jar!\com\jetbrains\nodejs\run\profile\cpu\v8log\calculation\V8ProfileCallback.class
 * Java compiler version: 11 (55.0)
 * JD-Core Version:       1.1.3
 */