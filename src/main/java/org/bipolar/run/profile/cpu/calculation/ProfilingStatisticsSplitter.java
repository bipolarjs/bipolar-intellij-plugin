/*    */ package org.bipolar.run.profile.cpu.calculation;
/*    */ 
/*    */ import java.util.ArrayList;
/*    */ import java.util.List;
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
/*    */ 
/*    */ public class ProfilingStatisticsSplitter
/*    */ {
/*    */   private static final String TOP_DOWN_TITLE = "[Top down (heavy) profile]:";
/*    */   private static final String BOTTOM_UP_TITLE = "[Bottom up (heavy) profile]:";
/*    */   private static final String HEADER = "Statistical profiling result from";
/*    */   private final List<String> myTail;
/*    */   private final List<String> myBottom;
/*    */   private final List<String> myTopCalls;
/*    */   
/*    */   public ProfilingStatisticsSplitter(@NotNull List<String> strings) {
/* 35 */     boolean headerFound = false;
/* 36 */     this.myTail = new ArrayList<>();
/* 37 */     this.myBottom = new ArrayList<>();
/* 38 */     this.myTopCalls = new ArrayList<>();
/* 39 */     for (int i = 0; i < strings.size(); i++) {
/* 40 */       String string = strings.get(i);
/* 41 */       if (!headerFound && string.trim().startsWith("Statistical profiling result from")) {
/* 42 */         headerFound = true;
/*    */       
/*    */       }
/* 45 */       else if (headerFound) {
/* 46 */         if ("[Top down (heavy) profile]:".equals(string)) {
/* 47 */           this.myTail.addAll(strings.subList(i, strings.size()));
/*    */           break;
/*    */         } 
/* 50 */         if ("[Bottom up (heavy) profile]:".equals(string) || !this.myBottom.isEmpty()) {
/* 51 */           this.myBottom.add(string);
/*    */         } else {
/*    */           
/* 54 */           this.myTopCalls.add(string);
/*    */         } 
/*    */       } 
/*    */     } 
/*    */   } public boolean isEmpty() {
/* 59 */     return (this.myBottom.isEmpty() && this.myTail.isEmpty() && this.myTopCalls.isEmpty());
/*    */   }
/*    */   
/*    */   public List<String> getTopDown() {
/* 63 */     return this.myTail;
/*    */   }
/*    */   
/*    */   public List<String> getBottomUp() {
/* 67 */     return this.myBottom;
/*    */   }
/*    */   
/*    */   public List<String> getTopCalls() {
/* 71 */     return this.myTopCalls;
/*    */   }
/*    */ }


/* Location:              C:\Program Files\JetBrains\WebStorm 2021.1\plugins\NodeJS\lib\NodeJS.jar!\com\jetbrains\nodejs\run\profile\cpu\calculation\ProfilingStatisticsSplitter.class
 * Java compiler version: 11 (55.0)
 * JD-Core Version:       1.1.3
 */