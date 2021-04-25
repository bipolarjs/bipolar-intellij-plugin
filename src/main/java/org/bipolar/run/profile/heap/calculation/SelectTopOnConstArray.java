/*     */ package org.bipolar.run.profile.heap.calculation;
/*     */ 
/*     */ import com.intellij.openapi.util.Pair;
/*     */ import com.intellij.util.Processor;
/*     */ import com.intellij.util.containers.ContainerUtil;
/*     */ import it.unimi.dsi.fastutil.longs.LongArrayList;
/*     */ import java.util.ArrayList;
/*     */ import java.util.HashSet;
/*     */ import java.util.List;
/*     */ import java.util.Set;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class SelectTopOnConstArray
/*     */ {
/*     */   private final LongArrayList myList;
/*     */   private final int myNumber;
/*     */   private final Processor<? super Integer> myFilter;
/*     */   private final Set<Integer> myExcludeSet;
/*     */   private final List<Pair<Long, Integer>> myIntermediate;
/*     */   private List<Pair<Long, Integer>> myTop;
/*     */   
/*     */   public SelectTopOnConstArray(LongArrayList list, int number, Processor<? super Integer> filter) {
/*  41 */     this.myList = list;
/*  42 */     this.myNumber = number;
/*  43 */     this.myFilter = filter;
/*     */     
/*  45 */     this.myExcludeSet = new HashSet<>();
/*  46 */     this.myIntermediate = new ArrayList<>();
/*     */   }
/*     */   
/*     */   public void execute() {
/*  50 */     long max = findMax();
/*     */     
/*  52 */     findBound(max, this.myNumber);
/*     */     
/*  54 */     this.myIntermediate.sort((o1, o2) -> ((Long)o2.getFirst()).compareTo((Long)o1.getFirst()));
/*     */ 
/*     */ 
/*     */     
/*  58 */     this.myTop = new ArrayList<>(ContainerUtil.getFirstItems(this.myIntermediate, this.myNumber));
/*     */   }
/*     */   
/*     */   public List<Pair<Long, Integer>> getTop() {
/*  62 */     return this.myTop;
/*     */   }
/*     */   
/*     */   private void findBound(long max, int number) {
/*  66 */     long lowerBound = max / 2L;
/*  67 */     long upperBound = max + 1L;
/*     */     
/*  69 */     int defense = 1000;
/*  70 */     while (number > 0 && defense-- > 0) {
/*  71 */       int cnt = countPercent(lowerBound, upperBound, number);
/*  72 */       if (cnt <= 3 * number) {
/*  73 */         gatherExceeding(lowerBound, upperBound);
/*  74 */         number -= cnt;
/*  75 */         upperBound = lowerBound;
/*  76 */         lowerBound /= 4L; continue;
/*     */       } 
/*  78 */       lowerBound = (long)(lowerBound * 1.5D);
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   private void gatherExceeding(long greaterOrEqualTo, long lessThen) {
/*  84 */     for (int i = 0; i < this.myList.size(); i++) {
/*  85 */       if (!excluded(i)) {
/*  86 */         long value = this.myList.getLong(i);
/*  87 */         if (value >= greaterOrEqualTo && value < lessThen)
/*  88 */           this.myIntermediate.add(Pair.create(Long.valueOf(value), Integer.valueOf(i))); 
/*     */       } 
/*     */     } 
/*     */   }
/*     */   
/*     */   private int countPercent(long greaterOrEqualTo, long lessThen, int number) {
/*  94 */     int cnt = 0;
/*  95 */     for (int i = 0; i < this.myList.size() && cnt <= number; i++) {
/*  96 */       if (!excluded(i)) {
/*  97 */         long value = this.myList.getLong(i);
/*  98 */         if (value >= greaterOrEqualTo && value < lessThen) cnt++; 
/*     */       } 
/* 100 */     }  return cnt;
/*     */   }
/*     */   
/*     */   private long findMax() {
/* 104 */     long max = 0L;
/* 105 */     for (int i = 0; i < this.myList.size(); i++) {
/* 106 */       if (!excluded(i)) {
/*     */         
/* 108 */         long value = this.myList.getLong(i);
/* 109 */         if (value > max)
/* 110 */           max = value; 
/*     */       } 
/*     */     } 
/* 113 */     return max;
/*     */   }
/*     */   
/*     */   private boolean excluded(int i) {
/* 117 */     return (!this.myFilter.process(Integer.valueOf(i)) || this.myExcludeSet.contains(Integer.valueOf(i)));
/*     */   }
/*     */ }


/* Location:              C:\Program Files\JetBrains\WebStorm 2021.1\plugins\NodeJS\lib\NodeJS.jar!\com\jetbrains\nodejs\run\profile\heap\calculation\SelectTopOnConstArray.class
 * Java compiler version: 11 (55.0)
 * JD-Core Version:       1.1.3
 */