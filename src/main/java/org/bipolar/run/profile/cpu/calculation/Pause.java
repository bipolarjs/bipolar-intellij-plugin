/*    */ package org.bipolar.run.profile.cpu.calculation;
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
/*    */ public class Pause
/*    */ {
/*    */   private final float myTs;
/*    */   private final int myPauseMs;
/*    */   private int myTsPoints;
/*    */   
/*    */   public Pause(float ts, int pauseMs) {
/* 27 */     this.myTs = ts;
/* 28 */     this.myPauseMs = pauseMs;
/*    */   }
/*    */   
/*    */   public void setTsPoints(int tsPoints) {
/* 32 */     this.myTsPoints = tsPoints;
/*    */   }
/*    */   
/*    */   public float getTs() {
/* 36 */     return this.myTs;
/*    */   }
/*    */   
/*    */   public int getPauseMs() {
/* 40 */     return this.myPauseMs;
/*    */   }
/*    */   
/*    */   public int getTsPoints() {
/* 44 */     return this.myTsPoints;
/*    */   }
/*    */ }


/* Location:              C:\Program Files\JetBrains\WebStorm 2021.1\plugins\NodeJS\lib\NodeJS.jar!\com\jetbrains\nodejs\run\profile\cpu\calculation\Pause.class
 * Java compiler version: 11 (55.0)
 * JD-Core Version:       1.1.3
 */