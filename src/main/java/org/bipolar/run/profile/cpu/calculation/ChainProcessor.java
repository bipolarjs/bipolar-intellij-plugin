/*     */ package org.bipolar.run.profile.cpu.calculation;
/*     */ 
/*     */ import com.intellij.openapi.util.Pair;
/*     */ import com.intellij.util.Parent;
/*     */ import java.util.ArrayDeque;
/*     */ import java.util.ArrayList;
/*     */ import java.util.HashMap;
/*     */ import java.util.List;
/*     */ import java.util.Map;
/*     */ import org.jetbrains.annotations.NotNull;
/*     */ 
/*     */ public class ChainProcessor<T extends Parent<T>> {
/*     */   private final ArrayDeque<Chain<T>> myInput;
/*     */   private final List<Chain<T>> myRollUp;
/*     */   
/*     */   public ChainProcessor(List<Chain<T>> input) {
/*  17 */     this.myInput = new ArrayDeque<>(input);
/*  18 */     this.myRollUp = new ArrayList<>();
/*     */   }
/*     */   
/*     */   public List<Chain<T>> getRollUp() {
/*  22 */     return this.myRollUp;
/*     */   }
/*     */   
/*     */   public void process() {
/*  26 */     while (!this.myInput.isEmpty()) {
/*  27 */       Chain<T> chain = this.myInput.removeFirst();
/*  28 */       if (!findOneLineRepeats(chain)) {
/*  29 */         findRepeats(chain);
/*     */       }
/*     */     } 
/*     */   }
/*     */   
/*     */   private void findRepeats(@NotNull Chain<T> chain) {
/*  35 */     if (chain == null) $$$reportNull$$$0(0);  Map<Pair<Long, Long>, Integer> map = new HashMap<>();
/*  36 */     Long previous = null;
/*  37 */     List<Long> ids = chain.getIds();
/*  38 */     for (int i = 0; i < ids.size(); i++) {
/*  39 */       Long id = ids.get(i);
/*  40 */       if (previous != null) {
/*  41 */         Pair<Long, Long> pair = Pair.create(previous, id);
/*  42 */         Integer position = map.get(pair);
/*  43 */         if (position != null && 
/*  44 */           tryMatch(chain, position.intValue(), i - 1))
/*     */           return; 
/*  46 */         map.put(pair, Integer.valueOf(i - 1));
/*     */       } 
/*  48 */       previous = id;
/*     */     } 
/*     */   }
/*     */   
/*     */   private boolean tryMatch(@NotNull Chain<T> chain, int start, int repeat) {
/*  53 */     if (chain == null) $$$reportNull$$$0(1);  int patternLen = repeat - start;
/*  54 */     if (patternLen <= 1) return false;
/*     */     
/*  56 */     int nextStart = start + 2 - patternLen;
/*  57 */     if (nextStart >= 0 && 
/*  58 */       tryMatchFromIndex(chain, patternLen, nextStart)) return true;
/*     */     
/*  60 */     if (nextStart != start && 
/*  61 */       tryMatchFromIndex(chain, patternLen, start)) return true; 
/*  62 */     return false;
/*     */   }
/*     */   
/*     */   private boolean tryMatchFromIndex(@NotNull Chain<T> chain, int patternLen, int start) {
/*  66 */     if (chain == null) $$$reportNull$$$0(2);  int firstStart = start;
/*  67 */     int successfulEnd = -1;
/*  68 */     while (start + patternLen <= chain.size() && 
/*  69 */       patternMatch(chain, start, patternLen)) {
/*  70 */       successfulEnd = start + patternLen + patternLen;
/*  71 */       start += patternLen;
/*     */     } 
/*  73 */     if (successfulEnd > 0) {
/*  74 */       List<Chain<T>> chains = chain.splitWithRollup(firstStart, successfulEnd, patternLen);
/*  75 */       Chain<T> last = chains.get(chains.size() - 1);
/*  76 */       if (!last.isRolled()) this.myInput.add(last); 
/*  77 */       for (Chain<T> inner : chains) {
/*  78 */         if (inner.isRolled()) {
/*  79 */           this.myRollUp.add(inner);
/*     */           break;
/*     */         } 
/*     */       } 
/*  83 */       return true;
/*     */     } 
/*  85 */     return false;
/*     */   }
/*     */ 
/*     */   
/*     */   private boolean patternMatch(Chain<T> chain, int start, int len) {
/*  90 */     List<Long> ids = chain.getIds();
/*  91 */     if (start + len > ids.size() || start + len + len > ids.size()) return false; 
/*  92 */     return ids.subList(start, start + len).equals(ids.subList(start + len, start + len + len));
/*     */   }
/*     */ 
/*     */   
/*     */   private boolean findOneLineRepeats(@NotNull Chain<T> chain) {
/*  97 */     if (chain == null) $$$reportNull$$$0(3);  Long previous = null;
/*  98 */     int start = -1;
/*  99 */     for (int i = 0; i < chain.size(); i++) {
/* 100 */       Long current = chain.get(i);
/* 101 */       if (current.equals(previous)) {
/* 102 */         if (start < 0) start = i - 1;
/*     */       
/*     */       }
/* 105 */       else if (start >= 0) {
/* 106 */         return splitWithOneItemRepeatingFragment(chain, start, i);
/*     */       } 
/*     */       
/* 109 */       previous = current;
/*     */     } 
/* 111 */     if (start >= 0)
/* 112 */       return splitWithOneItemRepeatingFragment(chain, start, chain.size()); 
/* 113 */     return false;
/*     */   }
/*     */   
/*     */   private boolean splitWithOneItemRepeatingFragment(@NotNull Chain<T> chain, int start, int i) {
/* 117 */     if (chain == null) $$$reportNull$$$0(4);  List<Chain<T>> chains = chain.splitWithRollup(start, i, 1);
/* 118 */     for (Chain<T> subChain : chains) {
/* 119 */       if (subChain.isRolled()) {
/* 120 */         this.myRollUp.add(subChain);
/*     */         continue;
/*     */       } 
/* 123 */       this.myInput.add(subChain);
/*     */     } 
/*     */     
/* 126 */     return true;
/*     */   }
/*     */ }


/* Location:              C:\Program Files\JetBrains\WebStorm 2021.1\plugins\NodeJS\lib\NodeJS.jar!\com\jetbrains\nodejs\run\profile\cpu\calculation\ChainProcessor.class
 * Java compiler version: 11 (55.0)
 * JD-Core Version:       1.1.3
 */