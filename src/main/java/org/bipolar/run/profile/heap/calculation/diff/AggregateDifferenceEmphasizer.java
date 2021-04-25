/*    */ package org.bipolar.run.profile.heap.calculation.diff;
/*    */ 
/*    */ import org.jetbrains.annotations.NotNull;
/*    */ 
/*    */ 
/*    */ public class AggregateDifferenceEmphasizer
/*    */   implements AggregateDifferenceEmphasizerI
/*    */ {
/*  9 */   private static final AggregateDifferenceEmphasizer ourInstance = new AggregateDifferenceEmphasizer();
/*    */   
/*    */   public static AggregateDifferenceEmphasizer getInstance() {
/* 12 */     return ourInstance;
/*    */   }
/*    */ 
/*    */   
/*    */   public boolean emphasize(@NotNull AggregatesViewDiff.AggregateDifference difference) {
/* 17 */     if (difference == null) $$$reportNull$$$0(0);  return (difference.selfSizeDiff() > 50000L || difference.objectsDiff() > 3000L);
/*    */   }
/*    */ }


/* Location:              C:\Program Files\JetBrains\WebStorm 2021.1\plugins\NodeJS\lib\NodeJS.jar!\com\jetbrains\nodejs\run\profile\heap\calculation\diff\AggregateDifferenceEmphasizer.class
 * Java compiler version: 11 (55.0)
 * JD-Core Version:       1.1.3
 */