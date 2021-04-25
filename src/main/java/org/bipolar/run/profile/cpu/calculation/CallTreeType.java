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
/*    */ public enum CallTreeType
/*    */ {
/* 22 */   bottomUp("Bottom-up"),
/* 23 */   topDown("Top-down");
/*    */   
/*    */   private final String myDisplayName;
/*    */   
/*    */   CallTreeType(String displayName) {
/* 28 */     this.myDisplayName = displayName;
/*    */   }
/*    */   
/*    */   public String getDisplayName() {
/* 32 */     return this.myDisplayName;
/*    */   }
/*    */ }


/* Location:              C:\Program Files\JetBrains\WebStorm 2021.1\plugins\NodeJS\lib\NodeJS.jar!\com\jetbrains\nodejs\run\profile\cpu\calculation\CallTreeType.class
 * Java compiler version: 11 (55.0)
 * JD-Core Version:       1.1.3
 */