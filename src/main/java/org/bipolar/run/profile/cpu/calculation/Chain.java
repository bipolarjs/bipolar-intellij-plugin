/*    */ package org.bipolar.run.profile.cpu.calculation;
/*    */ 
/*    */ import com.intellij.util.Parent;
/*    */ import com.intellij.util.ThreeState;
/*    */ import java.util.ArrayList;
/*    */ import java.util.List;
/*    */ import org.jetbrains.annotations.NotNull;
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public class Chain<T extends Parent<T>>
/*    */ {
/*    */   private final T myStart;
/*    */   private final List<Long> myIds;
/*    */   private ThreeState myState;
/*    */   private int myPatternLength;
/*    */   
/*    */   public Chain(T start, List<Long> ids) {
/* 20 */     this.myStart = start;
/* 21 */     this.myIds = ids;
/* 22 */     this.myState = ThreeState.UNSURE;
/*    */   }
/*    */   
/*    */   public int getPatternLength() {
/* 26 */     return this.myPatternLength;
/*    */   }
/*    */   
/*    */   public Chain setPatternLength(int patternLength) {
/* 30 */     this.myPatternLength = patternLength;
/* 31 */     return this;
/*    */   }
/*    */   
/*    */   public List<Long> getIds() {
/* 35 */     return this.myIds;
/*    */   }
/*    */   
/*    */   public Long get(int i) {
/* 39 */     return this.myIds.get(i);
/*    */   }
/*    */   
/*    */   public int size() {
/* 43 */     return this.myIds.size();
/*    */   }
/*    */   
/*    */   public T getStart() {
/* 47 */     return this.myStart;
/*    */   }
/*    */   
/*    */   public boolean isEmpty() {
/* 51 */     return (this.myStart == null);
/*    */   }
/*    */   
/*    */   public boolean isRolled() {
/* 55 */     return ThreeState.YES.equals(this.myState);
/*    */   }
/*    */   
/*    */   public Chain<T> rollUp(int patternLength) {
/* 59 */     this.myState = ThreeState.YES;
/* 60 */     this.myPatternLength = patternLength;
/* 61 */     return this;
/*    */   }
/*    */   
/*    */   public Chain<T> keep() {
/* 65 */     this.myState = ThreeState.NO;
/* 66 */     return this;
/*    */   }
/*    */   
/*    */   public ThreeState getState() {
/* 70 */     return this.myState;
/*    */   }
/*    */   
/*    */   public final List<Chain<T>> splitWithRollup(int from, int toExcluded, int patternLength) {
/* 74 */     assert from >= 0;
/* 75 */     assert toExcluded <= this.myIds.size();
/* 76 */     List<Chain<T>> list = new ArrayList<>();
/* 77 */     if (from > 0) {
/* 78 */       list.add(new Chain(this.myStart, this.myIds.subList(0, from)));
/*    */     }
/* 80 */     T selectedStart = (from == 0) ? this.myStart : moveX(this.myStart, from);
/* 81 */     list.add((new Chain(selectedStart, this.myIds.subList(from, toExcluded))).rollUp(patternLength));
/* 82 */     if (toExcluded < this.myIds.size()) {
/* 83 */       list.add(new Chain(moveX(this.myStart, toExcluded), this.myIds.subList(toExcluded, this.myIds.size())));
/*    */     }
/* 85 */     return list;
/*    */   }
/*    */   private static <S extends Parent<S>> S moveX(@NotNull S from, int steps) {
/*    */     Parent parent;
/* 89 */     if (from == null) $$$reportNull$$$0(0);  S current = from;
/* 90 */     for (int i = 0; i < steps; i++) {
/* 91 */       assert current.getChildren().size() == 1;
/* 92 */       parent = current.getChildren().get(0);
/*    */     } 
/* 94 */     return (S)parent;
/*    */   }
/*    */ }


/* Location:              C:\Program Files\JetBrains\WebStorm 2021.1\plugins\NodeJS\lib\NodeJS.jar!\com\jetbrains\nodejs\run\profile\cpu\calculation\Chain.class
 * Java compiler version: 11 (55.0)
 * JD-Core Version:       1.1.3
 */