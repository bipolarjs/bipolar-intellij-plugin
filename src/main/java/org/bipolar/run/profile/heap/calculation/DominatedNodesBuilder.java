/*    */ package org.bipolar.run.profile.heap.calculation;
/*    */ 
/*    */ import it.unimi.dsi.fastutil.ints.IntArrayList;
/*    */ import it.unimi.dsi.fastutil.ints.IntList;
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
/*    */ public final class DominatedNodesBuilder
/*    */ {
/*    */   private final int myNodesCnt;
/*    */   private final IntList myDominatorsTree;
/*    */   private final IntList myDominatedIdx;
/*    */   private final IntList myDominatedLinks;
/*    */   
/*    */   public DominatedNodesBuilder(IntList dominatorsTree, int nodesCnt) {
/* 32 */     this.myDominatorsTree = dominatorsTree;
/* 33 */     this.myNodesCnt = nodesCnt;
/* 34 */     this.myDominatedIdx = (IntList)new IntArrayList(this.myNodesCnt);
/* 35 */     this.myDominatedLinks = (IntList)new IntArrayList(this.myNodesCnt);
/*    */   }
/*    */   public void execute() {
/*    */     int i;
/* 39 */     for (i = 0; i < this.myNodesCnt; i++) {
/* 40 */       this.myDominatedIdx.add(0);
/* 41 */       this.myDominatedLinks.add(-1);
/*    */     } 
/*    */     
/* 44 */     for (i = 1; i < this.myNodesCnt; i++) {
/* 45 */       int dominator = this.myDominatorsTree.getInt(i);
/* 46 */       this.myDominatedIdx.set(dominator, this.myDominatedIdx.getInt(dominator) + 1);
/*    */     } 
/*    */     
/* 49 */     int offset = 0;
/* 50 */     IntArrayList intArrayList = new IntArrayList(this.myNodesCnt); int j;
/* 51 */     for (j = 0; j < this.myNodesCnt; j++) {
/* 52 */       int cnt = this.myDominatedIdx.getInt(j);
/* 53 */       intArrayList.add(cnt);
/* 54 */       this.myDominatedIdx.set(j, offset);
/* 55 */       offset += cnt;
/*    */     } 
/*    */     
/* 58 */     for (j = 1; j < this.myNodesCnt; j++) {
/* 59 */       int dominator = this.myDominatorsTree.getInt(j);
/* 60 */       int left = intArrayList.getInt(dominator);
/* 61 */       int was = this.myDominatedIdx.getInt(dominator);
/* 62 */       if (was >= 0 && left > 0) {
/* 63 */         this.myDominatedLinks.set(was + left - 1, j);
/* 64 */         intArrayList.set(dominator, left - 1);
/*    */       } 
/*    */     } 
/*    */   }
/*    */   
/*    */   public IntList getDominatedIdx() {
/* 70 */     return this.myDominatedIdx;
/*    */   }
/*    */   
/*    */   public IntList getDominatedLinks() {
/* 74 */     return this.myDominatedLinks;
/*    */   }
/*    */ }


/* Location:              C:\Program Files\JetBrains\WebStorm 2021.1\plugins\NodeJS\lib\NodeJS.jar!\com\jetbrains\nodejs\run\profile\heap\calculation\DominatedNodesBuilder.class
 * Java compiler version: 11 (55.0)
 * JD-Core Version:       1.1.3
 */