/*     */ package org.bipolar.run.profile.cpu.v8log.ui;
/*     */ 
/*     */ import com.intellij.openapi.util.Pair;
/*     */ import com.intellij.util.Consumer;
/*     */ import com.intellij.util.EmptyConsumer;
/*     */ import com.intellij.util.ui.UIUtil;
/*     */ import org.bipolar.run.profile.V8Utils;
/*     */ import org.bipolar.run.profile.cpu.v8log.calculation.V8LogIndexesWriter;
/*     */ import org.bipolar.run.profile.cpu.v8log.data.EventsStripeData;
/*     */ import org.bipolar.run.profile.cpu.v8log.reading.V8LogCachingReader;
/*     */ import java.awt.Color;
/*     */ import java.awt.Dimension;
/*     */ import java.awt.Font;
/*     */ import java.awt.FontMetrics;
/*     */ import java.awt.Graphics2D;
/*     */ import java.io.IOException;
/*     */ import java.util.List;
/*     */ import java.util.TreeMap;
/*     */ 
/*     */ public class EventsStripe
/*     */   extends ChartPanel
/*     */ {
/*     */   public static final int LINE_HEIGHT = 10;
/*     */   public static final int SPACE = 2;
/*     */   private final V8LogCachingReader myReader;
/*     */   private final Consumer<? super String> myNotificator;
/*     */   private final V8CpuFlameChart myFlameChart;
/*     */   private EventsStripeData myEventsStripeData;
/*     */   private boolean myIsLoading;
/*     */   private boolean myEmpty;
/*     */   private final Object myLock;
/*     */   private long mySelectionFrom;
/*     */   private long mySelectionTo;
/*     */   private Consumer<Boolean> myUIUpdate;
/*     */   
/*     */   public EventsStripe(int left, int rightMargin, V8LogCachingReader reader, Consumer<? super String> notificator, V8CpuFlameChart view) {
/*  37 */     super(left, rightMargin, 0, 40, 0L, reader.getLastTick());
/*  38 */     this.myNotificator = notificator;
/*  39 */     this.myFlameChart = view;
/*  40 */     this.myLock = new Object();
/*  41 */     this.mySelectionFrom = 0L;
/*  42 */     this.mySelectionTo = reader.getLastTick();
/*  43 */     this.myEmpty = true;
/*  44 */     this.myIsLoading = false;
/*  45 */     this.myReader = reader;
/*     */     
/*  47 */     this.myGridTop = this.myGridFontHeight;
/*  48 */     this.myTop = this.myGridTop;
/*  49 */     this.myHeight = 40 + this.myTop;
/*     */   }
/*     */   
/*     */   public void updateData() {
/*     */     long from;
/*     */     long to;
/*  55 */     synchronized (this.myLock) {
/*  56 */       from = this.mySelectionFrom;
/*  57 */       to = this.mySelectionTo;
/*     */     } 
/*     */     try {
/*  60 */       EventsStripeData events = this.myReader.getTimerEvents(from, to);
/*  61 */       this.myUIUpdate = createUpdate(from, to, events);
/*     */     }
/*  63 */     catch (IOException e) {
/*  64 */       this.myNotificator.consume("Can not get data for flame chart events: " + e.getMessage());
/*  65 */       this.myUIUpdate = EmptyConsumer.getInstance();
/*     */     } 
/*     */   }
/*     */   
/*     */   public Consumer<Boolean> getUIUpdate() {
/*  70 */     return this.myUIUpdate;
/*     */   }
/*     */   
/*     */   public void selection(long from, long to) throws IOException {
/*  74 */     assert from >= 0L && to >= 0L;
/*  75 */     this.myIsLoading = true;
/*  76 */     synchronized (this.myLock) {
/*  77 */       this.mySelectionFrom = from;
/*  78 */       this.mySelectionTo = to;
/*     */     } 
/*     */   }
/*     */   
/*     */   private Consumer<Boolean> createUpdate(long from, long to, EventsStripeData events) {
/*  83 */     return reloadingIsEmpty -> {
/*     */         this.myRealLeft = from;
/*     */         this.myRealRight = to;
/*     */         this.myIsLoading = !reloadingIsEmpty.booleanValue();
/*     */         if (events != null) {
/*     */           this.myEventsStripeData = events;
/*     */         } else {
/*     */           this.myEventsStripeData = null;
/*     */         } 
/*  92 */         this.myEmpty = (this.myEventsStripeData == null || this.myFlameChart.isEmpty());
/*     */         revalidate();
/*     */         repaint();
/*     */       };
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public Dimension getMinimumSize() {
/* 101 */     return new Dimension(this.myLeft + this.myRightMargin + 10, this.myTop + 40);
/*     */   }
/*     */ 
/*     */   
/*     */   public Dimension getPreferredSize() {
/* 106 */     Dimension size = super.getPreferredSize();
/* 107 */     return new Dimension(size.width, this.myTop + 40);
/*     */   }
/*     */ 
/*     */   
/*     */   protected void drawTsGrid(Graphics2D g) {
/* 112 */     if (!this.myEmpty) {
/* 113 */       super.drawTsGrid(g);
/*     */     }
/*     */   }
/*     */ 
/*     */   
/*     */   protected void drawChart(Graphics2D graphics2D, int left, int top, int width, int height, int gridGap) {
/* 119 */     if (this.myEmpty || this.myIsLoading)
/* 120 */       return;  V8Utils.safeDraw(graphics2D, graphics -> {
/*     */           List<TreeMap<Long, V8LogIndexesWriter.TimerEvent>> ordered = this.myEventsStripeData.getOrdered();
/*     */           for (int i = 0; i < ordered.size(); i++) {
/*     */             TreeMap<Long, V8LogIndexesWriter.TimerEvent> map = ordered.get(i);
/*     */             Color color = FlameColors.EVENTS_COLORS[i];
/*     */             for (V8LogIndexesWriter.TimerEvent event : map.values()) {
/*     */               int startX = Math.max(this.myLeft, pixelsFromReal(event.getStartNanos()));
/*     */               int endX = Math.max(this.myLeft, pixelsFromReal(event.getStartNanos() + event.getPause()));
/*     */               if (startX > width + this.myLeft) {
/*     */                 continue;
/*     */               }
/*     */               if (endX > width + this.myLeft)
/*     */                 endX = width + this.myLeft; 
/*     */               graphics.setColor(color);
/*     */               graphics.fillRect(startX, this.myTop + i * 10, endX - startX, 8);
/*     */             } 
/*     */           } 
/*     */           Pair<Long, Integer> position = this.myFlameChart.getDetailsPosition();
/*     */           V8CpuFlameChart.drawDetailsLine(graphics, getVisibleRect(), height, pixelsFromReal(((Long)position.getFirst()).longValue()), top);
/*     */         });
/*     */   }
/*     */   
/* 142 */   static final String[] LEGEND = new String[] { "GC", "engine", "external", "execution" };
/*     */   public static void drawLegend(Graphics2D graphics2D) {
/* 144 */     V8Utils.safeDraw(graphics2D, graphics -> {
/*     */           FontMetrics fm = graphics.getFontMetrics();
/*     */           Font font1 = graphics.getFont().deriveFont(UIUtil.getFontSize(UIUtil.FontSize.SMALL));
/*     */           graphics.setFont(font1);
/*     */           int height1 = fm.getHeight();
/*     */           int x1 = 10;
/*     */           for (int i = 0; i < FlameColors.EVENTS_COLORS.length; i++) {
/*     */             graphics.setColor(FlameColors.EVENTS_COLORS[i]);
/*     */             graphics.fillRect(x1, height1 + 2 - 10 + 2, 8, 8);
/*     */             graphics.setColor(Color.gray);
/*     */             graphics.drawString(LEGEND[i], x1 + 5 + 10, height1 + 2);
/*     */             x1 += fm.stringWidth(LEGEND[i]) + 20 + 2;
/*     */           } 
/*     */         });
/*     */   }
/*     */   
/*     */   public void setRight(int right) {
/* 161 */     this.myRightMargin = right;
/*     */   }
/*     */   
/*     */   public void detailsChanged() {
/* 165 */     revalidate();
/* 166 */     repaint();
/*     */   }
/*     */ }


/* Location:              C:\Program Files\JetBrains\WebStorm 2021.1\plugins\NodeJS\lib\NodeJS.jar!\com\jetbrains\nodejs\run\profile\cpu\v8lo\\ui\EventsStripe.class
 * Java compiler version: 11 (55.0)
 * JD-Core Version:       1.1.3
 */