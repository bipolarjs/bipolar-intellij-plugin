/*    */ package org.bipolar.run.profile.cpu.v8log.data;
/*    */ 
/*    */ import org.bipolar.run.profile.cpu.v8log.calculation.V8LogIndexesWriter;
/*    */ import java.util.ArrayList;
/*    */ import java.util.List;
/*    */ import java.util.TreeMap;
/*    */
import org.jetbrains.annotations.NotNull;
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
/*    */ public class EventsStripeData
/*    */ {
/* 20 */   private final TreeMap<Long, V8LogIndexesWriter.TimerEvent> myExecution = new TreeMap<>();
/* 21 */   private final TreeMap<Long, V8LogIndexesWriter.TimerEvent> myExternal = new TreeMap<>();
/* 22 */   private final TreeMap<Long, V8LogIndexesWriter.TimerEvent> myEngine = new TreeMap<>();
/* 23 */   private final TreeMap<Long, V8LogIndexesWriter.TimerEvent> myGc = new TreeMap<>();
/*    */ 
/*    */   
/*    */   public void addEvent(@NotNull V8LogIndexesWriter.TimerEvent timerEvent) {
/* 27 */     if (timerEvent == null) $$$reportNull$$$0(0);  V8EventType type = timerEvent.getEventType();
/* 28 */     if (V8EventType.Execute.equals(type)) { this.myExecution.put(Long.valueOf(timerEvent.getStartNanos()), timerEvent); }
/* 29 */     else if (V8EventType.External.equals(type)) { this.myExternal.put(Long.valueOf(timerEvent.getStartNanos()), timerEvent); }
/* 30 */     else if (type.isEngine()) { this.myEngine.put(Long.valueOf(timerEvent.getStartNanos()), timerEvent); }
/* 31 */     else if (type.isGc()) { this.myGc.put(Long.valueOf(timerEvent.getStartNanos()), timerEvent); }
/*    */   
/*    */   }
/*    */   public TreeMap<Long, V8LogIndexesWriter.TimerEvent> getExecution() {
/* 35 */     return this.myExecution;
/*    */   }
/*    */   
/*    */   public TreeMap<Long, V8LogIndexesWriter.TimerEvent> getExternal() {
/* 39 */     return this.myExternal;
/*    */   }
/*    */   
/*    */   public TreeMap<Long, V8LogIndexesWriter.TimerEvent> getEngine() {
/* 43 */     return this.myEngine;
/*    */   }
/*    */   
/*    */   public TreeMap<Long, V8LogIndexesWriter.TimerEvent> getGc() {
/* 47 */     return this.myGc;
/*    */   }
/*    */   
/*    */   public List<TreeMap<Long, V8LogIndexesWriter.TimerEvent>> getOrdered() {
/* 51 */     List<TreeMap<Long, V8LogIndexesWriter.TimerEvent>> list = new ArrayList<>();
/* 52 */     list.add(this.myGc);
/* 53 */     list.add(this.myEngine);
/* 54 */     list.add(this.myExternal);
/* 55 */     list.add(this.myExecution);
/* 56 */     return list;
/*    */   }
/*    */ }


/* Location:              C:\Program Files\JetBrains\WebStorm 2021.1\plugins\NodeJS\lib\NodeJS.jar!\com\jetbrains\nodejs\run\profile\cpu\v8log\data\EventsStripeData.class
 * Java compiler version: 11 (55.0)
 * JD-Core Version:       1.1.3
 */