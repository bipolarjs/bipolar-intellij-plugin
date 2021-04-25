/*     */ package org.bipolar.run.profile.cpu.v8log.calculation;
/*     */ 
/*     */ import com.intellij.util.Processor;
/*     */ import org.bipolar.run.profile.cpu.v8log.data.EventsStripeData;
/*     */ import org.bipolar.run.profile.heap.CompositeCloseable;
/*     */ import org.bipolar.run.profile.heap.io.reverse.LinksReader;
/*     */ import org.bipolar.run.profile.heap.io.reverse.LinksReaderFactory;
/*     */ import java.io.IOException;
/*     */ import java.util.ArrayList;
/*     */ import java.util.List;
/*     */
import org.jetbrains.annotations.NotNull;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class TimerEventsReader
/*     */ {
/*     */   private final CompositeCloseable myResources;
/*     */   private final LinksReaderFactory<V8LogIndexesWriter.TimerEvent> myEventsReader;
/*     */   @NotNull
/*     */   private final TickIndexer myEventsTickIndexer;
/*     */   @NotNull
/*     */   private final TickIndexer myEventsEndTickIndexer;
/*     */   private final Object myLock;
/*     */   
/*     */   public TimerEventsReader(CompositeCloseable resources, LinksReaderFactory<V8LogIndexesWriter.TimerEvent> eventsReader, @NotNull TickIndexer eventsTickIndexer, @NotNull TickIndexer eventsEndTickIndexer) {
/*  28 */     this.myLock = new Object();
/*  29 */     this.myResources = resources;
/*  30 */     this.myEventsReader = eventsReader;
/*  31 */     this.myEventsTickIndexer = eventsTickIndexer;
/*  32 */     this.myEventsEndTickIndexer = eventsEndTickIndexer;
/*     */   }
/*     */   
/*     */   @NotNull
/*     */   public TickIndexer getEventsTickIndexer() {
/*  37 */     if (this.myEventsTickIndexer == null) $$$reportNull$$$0(2);  return this.myEventsTickIndexer;
/*     */   }
/*     */   
/*     */   @NotNull
/*     */   public TickIndexer getEventsEndTickIndexer() {
/*  42 */     if (this.myEventsEndTickIndexer == null) $$$reportNull$$$0(3);  return this.myEventsEndTickIndexer;
/*     */   }
/*     */   
/*     */   public LinksReaderFactory<V8LogIndexesWriter.TimerEvent> getEventsReader() {
/*  46 */     return this.myEventsReader;
/*     */   }
/*     */   
/*     */   public EventsStripeData getTimerEvents(long from, long to) throws IOException {
/*  50 */     synchronized (this.myLock) {
/*  51 */       if (this.myEventsTickIndexer.getNumTicks() == 0) return new EventsStripeData();
/*     */       
/*  53 */       from = (from < 0L) ? 0L : from;
/*  54 */       to = (to < 0L || to > this.myEventsEndTickIndexer.getLastTick()) ? this.myEventsEndTickIndexer.getLastTick() : to;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */       
/*  61 */       EventsReader reader = new EventsReader(from, to);
/*  62 */       reader.execute();
/*  63 */       List<V8LogIndexesWriter.TimerEvent> events = reader.getEvents();
/*  64 */       EventsStripeData eventsStripeData = new EventsStripeData();
/*  65 */       for (V8LogIndexesWriter.TimerEvent event : events) {
/*  66 */         eventsStripeData.addEvent(event);
/*     */       }
/*  68 */       return eventsStripeData;
/*     */     } 
/*     */   }
/*     */   
/*     */   private class EventsReader {
/*     */     private final long myFromTs;
/*     */     private final long myToTs;
/*     */     private final int myFromRecord;
/*     */     private final int myToRecord;
/*     */     private List<Long> myTimes;
/*     */     private List<V8LogIndexesWriter.TimerEvent> myEvents;
/*     */     
/*     */     EventsReader(long fromTs, long toTs) {
/*  81 */       this.myFromTs = fromTs;
/*  82 */       this.myToTs = toTs;
/*     */       
/*  84 */       this.myFromRecord = Math.min(TimerEventsReader.this.myEventsTickIndexer.getFloorIndexFor(fromTs).intValue(), TimerEventsReader.this.myEventsEndTickIndexer.getFloorIndexFor(fromTs).intValue());
/*  85 */       this.myToRecord = TimerEventsReader.this.myEventsTickIndexer.getCeilIndexFor(toTs).intValue();
/*     */     }
/*     */     
/*     */     public void execute() throws IOException {
/*  89 */       this.myEvents = new ArrayList<>();
/*  90 */       LinksReader<V8LogIndexesWriter.TimerEvent> reader = TimerEventsReader.this.myEventsReader.create(true);
/*  91 */       if (this.myFromRecord != 0) {
/*  92 */         reader.skip(this.myFromRecord, true);
/*     */       }
/*  94 */       reader.iterateRandomLen(new Processor<V8LogIndexesWriter.TimerEvent>() {
/*  95 */             int cnt = TimerEventsReader.EventsReader.this.myFromRecord;
/*     */ 
/*     */             
/*     */             public boolean process(V8LogIndexesWriter.TimerEvent event) {
/*  99 */               long start = event.getStartNanos();
/* 100 */               long end = start + event.getPause();
/* 101 */               if (((start >= TimerEventsReader.EventsReader.this.myFromTs || end >= TimerEventsReader.EventsReader.this.myFromTs) && (start <= TimerEventsReader.EventsReader.this.myToTs || end <= TimerEventsReader.EventsReader.this.myToTs)) || (start <= TimerEventsReader.EventsReader.this.myFromTs && end >= TimerEventsReader.EventsReader.this.myToTs))
/*     */               {
/* 103 */                 TimerEventsReader.EventsReader.this.myEvents.add(event);
/*     */               }
/* 105 */               this.cnt++;
/* 106 */               return (this.cnt <= TimerEventsReader.EventsReader.this.myToRecord || start <= TimerEventsReader.EventsReader.this.myToTs);
/*     */             }
/*     */           });
/*     */     }
/*     */     
/*     */     public List<V8LogIndexesWriter.TimerEvent> getEvents() {
/* 112 */       return this.myEvents;
/*     */     }
/*     */   }
/*     */   
/*     */   class null implements Processor<V8LogIndexesWriter.TimerEvent> {
/*     */     int cnt = TimerEventsReader.EventsReader.this.myFromRecord;
/*     */     
/*     */     public boolean process(V8LogIndexesWriter.TimerEvent event) {
/*     */       long start = event.getStartNanos();
/*     */       long end = start + event.getPause();
/*     */       if (((start >= TimerEventsReader.EventsReader.this.myFromTs || end >= TimerEventsReader.EventsReader.this.myFromTs) && (start <= TimerEventsReader.EventsReader.this.myToTs || end <= TimerEventsReader.EventsReader.this.myToTs)) || (start <= TimerEventsReader.EventsReader.this.myFromTs && end >= TimerEventsReader.EventsReader.this.myToTs))
/*     */         TimerEventsReader.EventsReader.this.myEvents.add(event); 
/*     */       this.cnt++;
/*     */       return (this.cnt <= TimerEventsReader.EventsReader.this.myToRecord || start <= TimerEventsReader.EventsReader.this.myToTs);
/*     */     }
/*     */   }
/*     */ }


/* Location:              C:\Program Files\JetBrains\WebStorm 2021.1\plugins\NodeJS\lib\NodeJS.jar!\com\jetbrains\nodejs\run\profile\cpu\v8log\calculation\TimerEventsReader.class
 * Java compiler version: 11 (55.0)
 * JD-Core Version:       1.1.3
 */