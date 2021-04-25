/*    */ package org.bipolar.run.profile.cpu.v8log.reading;
/*    */ 
/*    */ import java.util.ArrayList;
/*    */ import java.util.Collections;
/*    */ import java.util.Iterator;
/*    */ import java.util.List;
/*    */ import java.util.Map;
/*    */ import java.util.TreeMap;
/*    */ 
/*    */ 
/*    */ public class FlameData
/*    */ {
/*    */   private final int myFirstTickOffset;
/*    */   private final List<Long> myTimes;
/*    */   
/*    */   public FlameData(int firstTickOffset, List<Long> times) {
/* 17 */     this.myFirstTickOffset = firstTickOffset;
/* 18 */     this.myTimes = times;
/* 19 */     this.myStackLineData = new ArrayList<>();
/* 20 */     this.myTickIdx = 0;
/*    */   }
/*    */   private final List<TreeMap<Long, StackLineData>> myStackLineData; private int myTickIdx; private List<Long> myPrevStack;
/*    */   public void tick(List<Long> stack) {
/* 24 */     Long ts = this.myTimes.get(this.myTickIdx);
/* 25 */     Collections.reverse(stack);
/* 26 */     int lastCommonId = (this.myPrevStack == null) ? -1 : getLastCommonId(this.myPrevStack, stack);
/* 27 */     if (this.myPrevStack != null) {
/* 28 */       finishStack(ts, lastCommonId);
/*    */     }
/* 30 */     for (int i = lastCommonId + 1; i < stack.size(); i++) {
/*    */       TreeMap<Long, StackLineData> map;
/* 32 */       if (this.myStackLineData.size() <= i) {
/* 33 */         map = new TreeMap<>();
/* 34 */         this.myStackLineData.add(map);
/*    */       } else {
/*    */         
/* 37 */         map = this.myStackLineData.get(i);
/*    */       } 
/* 39 */       map.put(ts, new StackLineData(0L, ((Long)stack.get(i)).longValue()));
/*    */     } 
/* 41 */     this.myPrevStack = stack;
/* 42 */     this.myTickIdx++;
/*    */   }
/*    */   
/*    */   public List<TreeMap<Long, StackLineData>> getStackLineData() {
/* 46 */     return this.myStackLineData;
/*    */   }
/*    */   
/*    */   public List<Long> getTimes() {
/* 50 */     return this.myTimes;
/*    */   }
/*    */   
/*    */   public int getFirstTickOffset() {
/* 54 */     return this.myFirstTickOffset;
/*    */   }
/*    */   
/*    */   private void finishStack(Long ts, int lastCommonId) {
/* 58 */     for (int i = lastCommonId + 1; i < this.myPrevStack.size(); i++) {
/* 59 */       Map.Entry<Long, StackLineData> entry = ((TreeMap<Long, StackLineData>)this.myStackLineData.get(i)).lastEntry();
/* 60 */       StackLineData lineData = entry.getValue();
/* 61 */       lineData.setDuration(ts.longValue() - ((Long)entry.getKey()).longValue());
/*    */     } 
/*    */   }
/*    */   
/*    */   public void finish() {
/* 66 */     if (this.myTimes.isEmpty())
/* 67 */       return;  long lastTs = ((Long)this.myTimes.get(this.myTimes.size() - 1)).longValue() + 2000L;
/* 68 */     for (int i = 0; i < this.myPrevStack.size(); i++) {
/* 69 */       Long id = this.myPrevStack.get(i);
/* 70 */       Map.Entry<Long, StackLineData> entry = ((TreeMap<Long, StackLineData>)this.myStackLineData.get(i)).lastEntry();
/* 71 */       StackLineData lineData = entry.getValue();
/* 72 */       if (id.equals(Long.valueOf(lineData.getStringId()))) {
/* 73 */         lineData.setDuration(lastTs - ((Long)entry.getKey()).longValue());
/*    */       }
/*    */     } 
/*    */   }
/*    */   
/*    */   private static int getLastCommonId(List<Long> one, List<Long> two) {
/* 79 */     int idx = 0;
/* 80 */     for (; idx < one.size() && idx < two.size(); idx++) {
/* 81 */       if (!((Long)one.get(idx)).equals(two.get(idx))) return idx - 1; 
/*    */     } 
/* 83 */     return idx - 1;
/*    */   }
/*    */   
/*    */   static List<Long> filterUnknown(List<Long> stack) {
/* 87 */     Iterator<Long> iterator = stack.iterator();
/* 88 */     while (iterator.hasNext()) {
/* 89 */       Long code = iterator.next();
/* 90 */       if (code.longValue() == 1L) {
/* 91 */         iterator.remove();
/*    */       }
/*    */     } 
/* 94 */     return stack;
/*    */   }
/*    */ }


/* Location:              C:\Program Files\JetBrains\WebStorm 2021.1\plugins\NodeJS\lib\NodeJS.jar!\com\jetbrains\nodejs\run\profile\cpu\v8log\reading\FlameData.class
 * Java compiler version: 11 (55.0)
 * JD-Core Version:       1.1.3
 */