/*    */ package org.bipolar.run.profile.heap;
/*    */ 
/*    */ import com.intellij.openapi.progress.ProgressIndicator;
/*    */ import org.bipolar.NodeJSBundle;
/*    */
import org.jetbrains.annotations.Nls;
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
/*    */ public class TimeReporter
/*    */ {
/*    */   private final long myStart;
/*    */   @Nls
/*    */   private final String myName;
/*    */   private final ProgressIndicator myIndicator;
/*    */   private long myStage;
/*    */   
/*    */   public TimeReporter(@Nls String name, ProgressIndicator indicator) {
/* 32 */     this.myName = name;
/* 33 */     this.myIndicator = indicator;
/* 34 */     this.myStart = System.currentTimeMillis();
/* 35 */     this.myStage = this.myStart;
/*    */   }
/*    */   
/*    */   private void report(@Nls String s) {
/* 39 */     if (this.myIndicator != null) {
/* 40 */       this.myIndicator.setText(s);
/*    */     } else {
/* 42 */       System.out.println(s);
/*    */     } 
/*    */   }
/*    */   
/*    */   public void reportTotal() {
/* 47 */     report(NodeJSBundle.message("profile.action_took_time.text", new Object[] { this.myName, formatDuration(System.currentTimeMillis() - this.myStart) }));
/*    */   }
/*    */   
/*    */   public void reportStage(String name) {
/* 51 */     long time = System.currentTimeMillis();
/* 52 */     report(NodeJSBundle.message("profile.action_took_time.text", new Object[] { name, formatDuration(time - this.myStage) }));
/* 53 */     this.myStage = time;
/*    */   }
/*    */   
/*    */   private static String formatDuration(long time) {
/* 57 */     if (time < 1000L) {
/* 58 */       return "" + time + " ms";
/*    */     }
/* 60 */     return "" + time / 1000L + " sec " + time / 1000L + " ms";
/*    */   }
/*    */ }


/* Location:              C:\Program Files\JetBrains\WebStorm 2021.1\plugins\NodeJS\lib\NodeJS.jar!\com\jetbrains\nodejs\run\profile\heap\TimeReporter.class
 * Java compiler version: 11 (55.0)
 * JD-Core Version:       1.1.3
 */