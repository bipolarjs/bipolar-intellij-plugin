/*    */ package org.bipolar.run.profile.cpu.v8log.diff;
/*    */ 
/*    */ import org.bipolar.run.profile.cpu.CallHolder;
/*    */ import org.bipolar.run.profile.cpu.v8log.data.V8CpuLogCall;
/*    */ import java.util.ArrayList;
/*    */ import java.util.List;
/*    */ import org.jetbrains.annotations.Nullable;
/*    */ 
/*    */ 
/*    */ 
/*    */ public class DiffNode
/*    */   implements CallHolder
/*    */ {
/*    */   private final V8CpuLogCall myCall;
/*    */   @Nullable
/*    */   private final Ticks myBefore;
/*    */   @Nullable
/*    */   private final Ticks myAfter;
/*    */   private final List<DiffNode> myChildren;
/*    */   
/*    */   public DiffNode(V8CpuLogCall call, @Nullable Ticks before, @Nullable Ticks after) {
/* 22 */     this.myCall = call;
/* 23 */     this.myBefore = before;
/* 24 */     this.myAfter = after;
/* 25 */     this.myChildren = new ArrayList<>();
/*    */   }
/*    */   
/*    */   public boolean isAdded() {
/* 29 */     return (this.myBefore == null);
/*    */   }
/*    */   
/*    */   public boolean isDeleted() {
/* 33 */     return (this.myAfter == null);
/*    */   }
/*    */   
/*    */   public void addChild(DiffNode child) {
/* 37 */     this.myChildren.add(child);
/*    */   }
/*    */ 
/*    */   
/*    */   public V8CpuLogCall getCall() {
/* 42 */     return this.myCall;
/*    */   }
/*    */   
/*    */   @Nullable
/*    */   public Ticks getBefore() {
/* 47 */     return this.myBefore;
/*    */   }
/*    */   
/*    */   @Nullable
/*    */   public Ticks getAfter() {
/* 52 */     return this.myAfter;
/*    */   }
/*    */   
/*    */   public List<DiffNode> getChildren() {
/* 56 */     return this.myChildren;
/*    */   }
/*    */ 
/*    */   
/*    */   public String toString() {
/* 61 */     return this.myCall.getPresentation();
/*    */   }
/*    */   
/*    */   public static class Ticks {
/*    */     private final int myTotal;
/*    */     private final int mySelf;
/*    */     private int myNumParentTicks;
/*    */     
/*    */     public Ticks(int total, int self) {
/* 70 */       this.myTotal = total;
/* 71 */       this.mySelf = self;
/*    */     }
/*    */     
/*    */     public int getTotal() {
/* 75 */       return this.myTotal;
/*    */     }
/*    */     
/*    */     public int getSelf() {
/* 79 */       return this.mySelf;
/*    */     }
/*    */     
/*    */     public int getNumParentTicks() {
/* 83 */       return this.myNumParentTicks;
/*    */     }
/*    */     
/*    */     public void setNumParentTicks(int numParentTicks) {
/* 87 */       this.myNumParentTicks = numParentTicks;
/*    */     }
/*    */   }
/*    */ }


/* Location:              C:\Program Files\JetBrains\WebStorm 2021.1\plugins\NodeJS\lib\NodeJS.jar!\com\jetbrains\nodejs\run\profile\cpu\v8log\diff\DiffNode.class
 * Java compiler version: 11 (55.0)
 * JD-Core Version:       1.1.3
 */