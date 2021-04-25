/*    */ package org.bipolar.run.profile.heap.view.components;
/*    */ 
/*    */ 
/*    */ 
/*    */ public class StaticIdGenerator
/*    */ {
/*  7 */   private int myCnt = 0;
/*    */   
/*    */   public int next() {
/* 10 */     return this.myCnt++;
/*    */   }
/*    */ }


/* Location:              C:\Program Files\JetBrains\WebStorm 2021.1\plugins\NodeJS\lib\NodeJS.jar!\com\jetbrains\nodejs\run\profile\heap\view\components\StaticIdGenerator.class
 * Java compiler version: 11 (55.0)
 * JD-Core Version:       1.1.3
 */