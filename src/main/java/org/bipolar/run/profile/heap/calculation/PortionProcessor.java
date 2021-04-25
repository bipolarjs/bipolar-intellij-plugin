/*    */ package org.bipolar.run.profile.heap.calculation;
/*    */ 
/*    */ import com.intellij.util.PairConsumer;
/*    */ import com.intellij.util.ThrowableConsumer;
/*    */ import com.intellij.util.containers.Convertor;
/*    */ import java.io.IOException;
/*    */ import java.util.HashMap;
/*    */ import java.util.HashSet;
/*    */ import java.util.Map;
/*    */ import java.util.Set;
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
/*    */ class PortionProcessor<In, Out>
/*    */ {
/*    */   private final long myFromIdx;
/*    */   private final long myToIdx;
/*    */   @NotNull
/*    */   private final Convertor<? super In, ? extends Out> myConvertor;
/*    */   private final Map<Long, Set<Out>> myMap;
/*    */   private PairConsumer<Long, Set<Out>> myFilter;
/*    */   
/*    */   PortionProcessor(long fromIdx, long toIdx, @NotNull Convertor<? super In, ? extends Out> convertor) {
/* 40 */     this.myFromIdx = fromIdx;
/* 41 */     this.myToIdx = toIdx;
/* 42 */     this.myConvertor = (Convertor<? super In, ? extends Out>)SYNTHETIC_LOCAL_VARIABLE_5;
/* 43 */     this.myMap = new HashMap<>();
/*    */   }
/*    */   
/*    */   public void setFilter(PairConsumer<Long, Set<Out>> filter) {
/* 47 */     this.myFilter = filter;
/*    */   }
/*    */   
/*    */   public void accept(long idx, In item) {
/* 51 */     if (idx >= this.myFromIdx && idx <= this.myToIdx) {
/* 52 */       Set<Out> list = this.myMap.get(Long.valueOf(idx));
/* 53 */       if (list == null) {
/* 54 */         this.myMap.put(Long.valueOf(idx), list = new HashSet<>());
/*    */       }
/* 56 */       list.add((Out)this.myConvertor.convert(item));
/*    */     } 
/*    */   }
/*    */ 
/*    */   
/*    */   public void flush(@NotNull ThrowableConsumer<Long, IOException> sizesConsumer, @NotNull ThrowableConsumer<Out, IOException> linksConsumer) throws IOException {
/* 62 */     if (sizesConsumer == null) $$$reportNull$$$0(1);  if (linksConsumer == null) $$$reportNull$$$0(2);  if (this.myFilter != null) {
/* 63 */       for (Map.Entry<Long, Set<Out>> entry : this.myMap.entrySet()) {
/* 64 */         this.myFilter.consume(entry.getKey(), entry.getValue());
/*    */       }
/*    */     }
/* 67 */     for (long i = this.myFromIdx; i <= this.myToIdx; i++) {
/* 68 */       Set<Out> list = this.myMap.get(Long.valueOf(i));
/* 69 */       sizesConsumer.consume(Long.valueOf(((list == null) ? 0L : list.size())));
/* 70 */       if (list != null)
/* 71 */         for (Out link : list)
/* 72 */           linksConsumer.consume(link);  
/*    */     } 
/*    */   }
/*    */ }


/* Location:              C:\Program Files\JetBrains\WebStorm 2021.1\plugins\NodeJS\lib\NodeJS.jar!\com\jetbrains\nodejs\run\profile\heap\calculation\PortionProcessor.class
 * Java compiler version: 11 (55.0)
 * JD-Core Version:       1.1.3
 */