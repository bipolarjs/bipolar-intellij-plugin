/*    */ package org.bipolar.run.profile.heap.calculation.diff;
/*    */ 
/*    */ import org.jetbrains.annotations.NotNull;
/*    */ 
/*    */ 
/*    */ 
/*    */ public interface AggregateDifferenceEmphasizerI
/*    */ {
/*  9 */   public static final AggregateDifferenceEmphasizerI EMPTY = new AggregateDifferenceEmphasizerI()
/*    */     {
/*    */       public boolean emphasize(@NotNull AggregatesViewDiff.AggregateDifference difference) {
/* 12 */         if (difference == null) $$$reportNull$$$0(0);  return false;
/*    */       }
/*    */     };
/*    */   
/*    */   boolean emphasize(@NotNull AggregatesViewDiff.AggregateDifference paramAggregateDifference);
/*    */ }


/* Location:              C:\Program Files\JetBrains\WebStorm 2021.1\plugins\NodeJS\lib\NodeJS.jar!\com\jetbrains\nodejs\run\profile\heap\calculation\diff\AggregateDifferenceEmphasizerI.class
 * Java compiler version: 11 (55.0)
 * JD-Core Version:       1.1.3
 */