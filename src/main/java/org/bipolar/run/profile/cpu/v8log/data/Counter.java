/*    */ package org.bipolar.run.profile.cpu.v8log.data;
/*    */ 
/*    */ 
/*    */ 
/*    */ public class Counter
/*    */ {
/*  7 */   private int myCnt = 0;
/*    */   
/*    */   public int incrementAndGet() {
/* 10 */     return ++this.myCnt;
/*    */   }
/*    */   
/*    */   public void add(int weight) {
/* 14 */     this.myCnt += weight;
/*    */   }
/*    */   
/*    */   public int getCnt() {
/* 18 */     return this.myCnt;
/*    */   }
/*    */   
/*    */   public void setCnt(int cnt) {
/* 22 */     this.myCnt = cnt;
/*    */   }
/*    */ }


/* Location:              C:\Program Files\JetBrains\WebStorm 2021.1\plugins\NodeJS\lib\NodeJS.jar!\com\jetbrains\nodejs\run\profile\cpu\v8log\data\Counter.class
 * Java compiler version: 11 (55.0)
 * JD-Core Version:       1.1.3
 */