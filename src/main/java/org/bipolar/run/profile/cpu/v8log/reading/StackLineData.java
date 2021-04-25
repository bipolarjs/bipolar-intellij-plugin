/*    */ package org.bipolar.run.profile.cpu.v8log.reading;
/*    */ 
/*    */ 
/*    */ 
/*    */ public class StackLineData
/*    */ {
/*    */   private long myDuration;
/*    */   private final long myStringId;
/*    */   
/*    */   public StackLineData(long duration, long stringId) {
/* 11 */     this.myDuration = duration;
/* 12 */     this.myStringId = stringId;
/*    */   }
/*    */   
/*    */   public long getDuration() {
/* 16 */     return this.myDuration;
/*    */   }
/*    */   
/*    */   public long getStringId() {
/* 20 */     return this.myStringId;
/*    */   }
/*    */   
/*    */   public void setDuration(long duration) {
/* 24 */     this.myDuration = duration;
/*    */   }
/*    */ }


/* Location:              C:\Program Files\JetBrains\WebStorm 2021.1\plugins\NodeJS\lib\NodeJS.jar!\com\jetbrains\nodejs\run\profile\cpu\v8log\reading\StackLineData.class
 * Java compiler version: 11 (55.0)
 * JD-Core Version:       1.1.3
 */