/*    */ package org.bipolar.run.profile.cpu.v8log.calculation;
/*    */ 
/*    */ import org.bipolar.run.profile.cpu.v8log.data.Counter;
/*    */ import java.util.ArrayDeque;
/*    */ import java.util.Collection;
/*    */ import java.util.HashMap;
/*    */ import java.util.List;
/*    */ import java.util.Map;
/*    */ import org.jetbrains.annotations.NotNull;
/*    */ 
/*    */ 
/*    */ 
/*    */ public class CallTree
/*    */ {
/* 15 */   private final CallTreeNode myRoot = new CallTreeNode(Long.valueOf(-1L));
/*    */ 
/*    */   
/*    */   public CallTreeNode getRoot() {
/* 19 */     return this.myRoot;
/*    */   }
/*    */   
/*    */   public void addPath(@NotNull List<Long> strings) {
/* 23 */     if (strings == null) $$$reportNull$$$0(0);  if (strings.isEmpty())
/* 24 */       return;  CallTreeNode current = this.myRoot;
/* 25 */     for (Long path : strings) {
/* 26 */       current = current.findOrAddChild(path);
/*    */     }
/* 28 */     current.getSelfWeight().incrementAndGet();
/*    */   }
/*    */   
/*    */   public void computeTotalWeight() {
/* 32 */     ArrayDeque<CallTreeNode> in = new ArrayDeque<>();
/* 33 */     ArrayDeque<CallTreeNode> out = new ArrayDeque<>();
/* 34 */     in.add(this.myRoot);
/* 35 */     while (!in.isEmpty()) {
/* 36 */       CallTreeNode node = in.removeFirst();
/* 37 */       out.add(node);
/* 38 */       Collection<CallTreeNode> children = node.getChildren().values();
/* 39 */       in.addAll(children);
/*    */     } 
/* 41 */     while (!out.isEmpty()) {
/* 42 */       CallTreeNode node = out.removeLast();
/* 43 */       node.computeTotalWeight();
/*    */     } 
/*    */   }
/*    */   
/*    */   public static class CallTreeNode
/*    */   {
/*    */     private final Long myName;
/*    */     private final Counter mySelfWeight;
/*    */     private final Counter myTotalWeight;
/*    */     private final Map<Long, CallTreeNode> myChildren;
/*    */     
/*    */     public CallTreeNode(Long name) {
/* 55 */       this.myName = name;
/* 56 */       this.mySelfWeight = new Counter();
/* 57 */       this.myTotalWeight = new Counter();
/* 58 */       this.myChildren = new HashMap<>();
/*    */     }
/*    */     
/*    */     public CallTreeNode findOrAddChild(@NotNull Long name) {
/* 62 */       if (name == null) $$$reportNull$$$0(0);  CallTreeNode node = this.myChildren.get(name);
/* 63 */       if (node != null) return node; 
/* 64 */       this.myChildren.put(name, node = new CallTreeNode(name));
/* 65 */       return node;
/*    */     }
/*    */     
/*    */     public void computeTotalWeight() {
/* 69 */       this.myTotalWeight.add(this.mySelfWeight.getCnt());
/* 70 */       for (CallTreeNode node : this.myChildren.values()) {
/* 71 */         this.myTotalWeight.add(node.getTotalWeight().getCnt());
/*    */       }
/*    */     }
/*    */     
/*    */     public Long getName() {
/* 76 */       return this.myName;
/*    */     }
/*    */     
/*    */     public Counter getSelfWeight() {
/* 80 */       return this.mySelfWeight;
/*    */     }
/*    */     
/*    */     public Counter getTotalWeight() {
/* 84 */       return this.myTotalWeight;
/*    */     }
/*    */     
/*    */     public Map<Long, CallTreeNode> getChildren() {
/* 88 */       return this.myChildren;
/*    */     }
/*    */   }
/*    */ }


/* Location:              C:\Program Files\JetBrains\WebStorm 2021.1\plugins\NodeJS\lib\NodeJS.jar!\com\jetbrains\nodejs\run\profile\cpu\v8log\calculation\CallTree.class
 * Java compiler version: 11 (55.0)
 * JD-Core Version:       1.1.3
 */