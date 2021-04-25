/*     */ package org.bipolar.run.profile.cpu.v8log.ui;
/*     */ 
/*     */ import com.intellij.openapi.util.Pair;
/*     */ import com.intellij.ui.Gray;
/*     */ import com.intellij.ui.JBColor;
/*     */ import com.intellij.util.Consumer;
/*     */ import com.intellij.util.ui.UIUtil;
/*     */ import org.bipolar.run.profile.V8Utils;
/*     */ import org.bipolar.run.profile.cpu.v8log.reading.V8LogCachingReader;
/*     */ import java.awt.BasicStroke;
/*     */ import java.awt.Color;
/*     */ import java.awt.Cursor;
/*     */ import java.awt.Graphics2D;
/*     */ import java.awt.event.MouseAdapter;
/*     */ import java.awt.event.MouseEvent;
/*     */ import java.io.IOException;
/*     */ import java.util.Collections;
/*     */ import java.util.List;
/*     */ import java.util.concurrent.TimeUnit;
/*     */
import org.jetbrains.annotations.NotNull;
/*     */ 
/*     */ public abstract class V8CpuOverviewChart
/*     */   extends ChartPanel {
/*  24 */   public static final JBColor BACK_COLOR = new JBColor((Color)Gray._220, (Color)Gray._120);
/*  25 */   public static final JBColor SELECTION_COLOR = new JBColor((Color)Gray._255, UIUtil.getLabelBackground());
/*  26 */   public static final Color FILL_COLOR = (Color)new JBColor(new Color(196, 213, 248), new Color(98, 108, 161));
/*  27 */   public static final Color LINE_COLOR = (Color)new JBColor(new Color(156, 173, 248), new Color(76, 87, 137));
/*     */   
/*     */   private final V8LogCachingReader myReader;
/*     */   
/*     */   private long myStartTs;
/*     */   
/*     */   private long myEndTs;
/*     */   
/*     */   private List<Pair<Long, Integer>> myOverview;
/*     */   protected long myLeftBound;
/*     */   protected long myRightBound;
/*     */   
/*     */   public V8CpuOverviewChart(int left, int rightMargin, int top, int height, V8LogCachingReader reader, long realLeft, long realRight) throws IOException {
/*  40 */     super(left, rightMargin, top, height, (realLeft > 0L) ? realLeft : 0L, (realRight > 0L) ? Math.min(realRight, reader.getLastTick()) : reader.getLastTick());
/*  41 */     this.myGridTop = this.myGridFontHeight;
/*  42 */     this.myTop = this.myGridTop;
/*  43 */     this.myReader = reader;
/*  44 */     this.myOverview = Collections.emptyList();
/*  45 */     setInterval(this.myRealLeft, this.myRealRight);
/*  46 */     this.myLeftBound = this.myRealLeft;
/*  47 */     this.myRightBound = this.myRealRight;
/*     */     
/*  49 */     MouseAdapter mouseAdapter = createMouseAdapter();
/*  50 */     addMouseListener(mouseAdapter);
/*  51 */     addMouseMotionListener(mouseAdapter);
/*  52 */     selectionUpdated();
/*     */   }
/*     */   
/*     */   public void setSelection(long from, long to) {
/*  56 */     this.myLeftBound = Math.max(this.myRealLeft, from);
/*  57 */     this.myRightBound = Math.min(this.myRealRight, to);
/*     */     
/*  59 */     selectionUpdated();
/*  60 */     repaint();
/*     */   }
/*     */   
/*     */   @NotNull
/*     */   private MouseAdapter createMouseAdapter() {
/*  65 */     return new MouseAdapter()
/*     */       {
/*     */         private long myDragStartValue;
/*     */         private long myDragStartLeft;
/*     */         private long myDragStartRight;
/*     */         private boolean myInsideSelection;
/*     */         private boolean myAboveRightBound;
/*     */         private boolean myAboveLeftBound;
/*     */         
/*     */         public void mouseDragged(MouseEvent e) {
/*  75 */           int x = e.getX();
/*  76 */           if (this.myAboveLeftBound) {
/*  77 */             if (x > V8CpuOverviewChart.this.pixelsFromReal(V8CpuOverviewChart.this.myRightBound) - 10)
/*  78 */               return;  long was = V8CpuOverviewChart.this.myLeftBound;
/*  79 */             V8CpuOverviewChart.this.myLeftBound = V8CpuOverviewChart.this.realFromPixels(Math.max(V8CpuOverviewChart.this.myLeft, x));
/*  80 */             if (was != V8CpuOverviewChart.this.myLeftBound) V8CpuOverviewChart.this.selectionUpdated(); 
/*  81 */             V8CpuOverviewChart.this.repaint();
/*     */           }
/*  83 */           else if (this.myAboveRightBound) {
/*  84 */             if (x < V8CpuOverviewChart.this.pixelsFromReal(V8CpuOverviewChart.this.myLeftBound) + 10)
/*  85 */               return;  long was = V8CpuOverviewChart.this.myRightBound;
/*  86 */             V8CpuOverviewChart.this.myRightBound = V8CpuOverviewChart.this.realFromPixels(Math.min(x, V8CpuOverviewChart.this.getWidth() - V8CpuOverviewChart.this.myRightMargin));
/*  87 */             if (was != V8CpuOverviewChart.this.myRightBound) V8CpuOverviewChart.this.selectionUpdated(); 
/*  88 */             V8CpuOverviewChart.this.repaint();
/*  89 */           } else if (this.myInsideSelection && this.myDragStartValue > 0L) {
/*  90 */             long delta = V8CpuOverviewChart.this.realFromPixels(x) - this.myDragStartValue;
/*  91 */             long wasLeft = this.myDragStartLeft;
/*  92 */             long wasRight = this.myDragStartRight;
/*  93 */             V8CpuOverviewChart.this.myLeftBound = Math.max(V8CpuOverviewChart.this.myRealLeft, this.myDragStartLeft + delta);
/*  94 */             V8CpuOverviewChart.this.myRightBound = Math.min(V8CpuOverviewChart.this.myRealRight, this.myDragStartRight + delta);
/*  95 */             if (wasLeft != V8CpuOverviewChart.this.myLeftBound && wasRight != V8CpuOverviewChart.this.myRightBound) V8CpuOverviewChart.this.selectionUpdated(); 
/*  96 */             V8CpuOverviewChart.this.repaint();
/*     */           } 
/*     */         }
/*     */ 
/*     */         
/*     */         public void mousePressed(MouseEvent e) {
/* 102 */           if (this.myInsideSelection) {
/* 103 */             V8CpuOverviewChart.this.setCursor(new Cursor(13));
/* 104 */             this.myDragStartValue = V8CpuOverviewChart.this.realFromPixels(e.getX());
/* 105 */             this.myDragStartLeft = V8CpuOverviewChart.this.myLeftBound;
/* 106 */             this.myDragStartRight = V8CpuOverviewChart.this.myRightBound;
/*     */           } 
/*     */         }
/*     */ 
/*     */         
/*     */         public void mouseMoved(MouseEvent e) {
/* 112 */           int x = e.getX();
/* 113 */           this.myInsideSelection = false;
/*     */           
/* 115 */           int leftPixels = V8CpuOverviewChart.this.pixelsFromReal(V8CpuOverviewChart.this.myLeftBound);
/* 116 */           int rightPixels = V8CpuOverviewChart.this.pixelsFromReal(V8CpuOverviewChart.this.myRightBound);
/* 117 */           this.myAboveLeftBound = V8CpuOverviewChart.this.aroundBound(x, leftPixels);
/* 118 */           this.myAboveRightBound = V8CpuOverviewChart.this.aroundBound(x, rightPixels);
/* 119 */           if (this.myAboveLeftBound || this.myAboveRightBound) {
/* 120 */             V8CpuOverviewChart.this.setCursor(new Cursor(10));
/* 121 */             this.myDragStartValue = -1L;
/* 122 */           } else if (this.myInsideSelection = (x > leftPixels && x < rightPixels)) {
/* 123 */             V8CpuOverviewChart.this.setCursor(new Cursor(12));
/*     */           } else {
/*     */             
/* 126 */             V8CpuOverviewChart.this.setCursor(Cursor.getDefaultCursor());
/* 127 */             this.myDragStartValue = -1L;
/*     */           } 
/*     */         }
/*     */ 
/*     */         
/*     */         public void mouseClicked(MouseEvent e) {
/* 133 */           if (V8CpuOverviewChart.this.myLeftBound == V8CpuOverviewChart.this.myRealLeft && V8CpuOverviewChart.this.myRightBound == V8CpuOverviewChart.this.myRealRight) {
/* 134 */             long tenPercent = (V8CpuOverviewChart.this.myRealRight - V8CpuOverviewChart.this.myRealLeft) / 10L;
/* 135 */             long point = V8CpuOverviewChart.this.realFromPixels(e.getX());
/* 136 */             V8CpuOverviewChart.this.setSelection(point - tenPercent / 2L, point + tenPercent / 2L);
/*     */           } 
/*     */         }
/*     */       };
/*     */   }
/*     */   
/*     */   public void initialSelection() {
/* 143 */     long tenPercent = (this.myRealRight - this.myRealLeft) / 10L;
/* 144 */     long point = tenPercent * 5L;
/* 145 */     long radius = (tenPercent / 1000L > TimeUnit.SECONDS.toMillis(2L)) ? (tenPercent / 20L) : (tenPercent / 2L);
/* 146 */     if (this.myOverview != null) {
/* 147 */       int step = this.myOverview.size() / 40;
/* 148 */       step = Math.max(1, step);
/* 149 */       int max = 0;
/* 150 */       int maxIdx = -1;
/* 151 */       Integer firstWhere80 = null; int i;
/* 152 */       for (i = step / 2; i < this.myOverview.size(); i += step) {
/* 153 */         Pair<Long, Integer> pair = this.myOverview.get(i);
/* 154 */         if (firstWhere80 == null && ((Integer)pair.getSecond()).intValue() > this.myReader.getMaxStackSize() * 0.8D) {
/* 155 */           firstWhere80 = Integer.valueOf(i);
/*     */         }
/* 157 */         if (((Integer)pair.getSecond()).intValue() > max) {
/* 158 */           maxIdx = i;
/*     */         }
/*     */       } 
/* 161 */       if (firstWhere80 != null) {
/* 162 */         point = ((Long)((Pair)this.myOverview.get(firstWhere80.intValue())).getFirst()).longValue();
/* 163 */       } else if (maxIdx >= 0) {
/* 164 */         point = ((Long)((Pair)this.myOverview.get(maxIdx)).getFirst()).longValue();
/*     */       } 
/*     */     } 
/*     */     
/* 168 */     setSelection(point - radius, point + radius);
/*     */   }
/*     */   
/*     */   public boolean isSelectionNarrow() {
/* 172 */     return (this.myLeftBound > this.myRealLeft || this.myRightBound < this.myRealRight);
/*     */   }
/*     */   
/*     */   public long getLeftBound() {
/* 176 */     return this.myLeftBound;
/*     */   }
/*     */   
/*     */   public long getRightBound() {
/* 180 */     return this.myRightBound;
/*     */   }
/*     */   
/*     */   public void setInterval(long from, long to) throws IOException {
/* 184 */     this.myStartTs = from;
/* 185 */     this.myEndTs = to;
/* 186 */     this.myOverview = this.myReader.getStackOverview(this.myStartTs, this.myEndTs);
/* 187 */     if (this.myStartTs < 0L) this.myStartTs = 0L; 
/* 188 */     if (this.myEndTs < 0L) this.myEndTs = this.myReader.getLastTick(); 
/*     */   }
/*     */   
/*     */   public boolean showsOverview() {
/* 192 */     return (this.myOverview != null);
/*     */   }
/*     */ 
/*     */   
/*     */   protected void drawChart(Graphics2D graphics, final int left, final int top, final int width, final int height, final int gridGap) {
/* 197 */     V8Utils.safeDraw(graphics, new Consumer<Graphics2D>() { int prevX;
/*     */           
/*     */           public void consume(Graphics2D graphics) {
/* 200 */             int leftSelection = V8CpuOverviewChart.this.pixelsFromReal(V8CpuOverviewChart.this.myLeftBound);
/* 201 */             int rightSelection = V8CpuOverviewChart.this.pixelsFromReal(V8CpuOverviewChart.this.myRightBound);
/*     */             
/* 203 */             fillBackground(graphics, leftSelection, rightSelection);
/*     */             
/* 205 */             if (V8CpuOverviewChart.this.myOverview == null) {
/* 206 */               String text = "Too many data points. Select interval and zoom.";
/* 207 */               int textWidth = V8CpuOverviewChart.this.getFontMetrics(V8CpuOverviewChart.this.getFont()).stringWidth("Too many data points. Select interval and zoom.");
/* 208 */               int position = (width - left - textWidth) / 2;
/* 209 */               graphics.drawString("Too many data points. Select interval and zoom.", position, top + gridGap + (height - gridGap) / 2);
/* 210 */               V8CpuOverviewChart.this.drawChartBound(graphics, top, left, height, width);
/* 211 */               drawSelectionLines(graphics, leftSelection, rightSelection, width);
/*     */               
/*     */               return;
/*     */             } 
/* 215 */             graphics.setStroke(new BasicStroke(1.0F));
/* 216 */             double pointsPerStackStep = (height - gridGap) / V8CpuOverviewChart.this.myReader.getMaxStackSize();
/* 217 */             graphics.setColor(V8CpuOverviewChart.FILL_COLOR);
/* 218 */             iterateDots(pointsPerStackStep, top, height, () -> graphics.fillPolygon(new int[] { this.prevX, this.prevX, this.nextX, this.nextX }, new int[] { top + height, this.prevY, this.nextY, top + height }, 4));
/*     */ 
/*     */             
/* 221 */             graphics.setColor(V8CpuOverviewChart.LINE_COLOR);
/* 222 */             iterateDots(pointsPerStackStep, top, height, () -> graphics.drawLine(this.prevX, this.prevY, this.nextX, this.nextY));
/* 223 */             V8CpuOverviewChart.this.drawChartBound(graphics, top, left, height, width);
/* 224 */             drawSelectionLines(graphics, leftSelection, rightSelection, width);
/*     */           }
/*     */ 
/*     */           
/*     */           int prevY;
/*     */           int nextX;
/*     */           int nextY;
/*     */           
/*     */           private void iterateDots(double pointsPerStackStep, int top, int height, Runnable drawer) {
/* 233 */             this.prevX = V8CpuOverviewChart.this.pixelsFromReal(V8CpuOverviewChart.this.myRealLeft);
/* 234 */             this.prevY = top + height;
/* 235 */             for (Pair<Long, Integer> pair : V8CpuOverviewChart.this.myOverview) {
/* 236 */               this.nextX = V8CpuOverviewChart.this.pixelsFromReal(((Long)pair.getFirst()).longValue());
/* 237 */               this.nextY = (int)(top + Math.round(height - ((Integer)pair.getSecond()).intValue() * pointsPerStackStep));
/* 238 */               drawer.run();
/* 239 */               this.prevX = this.nextX;
/* 240 */               this.prevY = this.nextY;
/*     */             } 
/*     */           }
/*     */           
/*     */           private void drawSelectionLines(Graphics2D graphics, int leftSelection, int rightSelection, int width) {
/* 245 */             graphics.setStroke(new BasicStroke(2.0F));
/* 246 */             graphics.setColor(Color.gray);
/* 247 */             leftSelection = (leftSelection == 0) ? 2 : leftSelection;
/* 248 */             rightSelection = (rightSelection == width) ? (rightSelection - 2) : rightSelection;
/* 249 */             graphics.drawLine(leftSelection, 0, leftSelection, top + height);
/* 250 */             graphics.drawLine(rightSelection, 0, rightSelection, top + height);
/*     */           }
/*     */           
/*     */           private void fillBackground(Graphics2D graphics, int leftSelection, int rightSelection) {
/* 254 */             graphics.setColor((Color)V8CpuOverviewChart.BACK_COLOR);
/* 255 */             graphics.drawRect(left, 0, leftSelection - left, height + top);
/* 256 */             graphics.fillRect(left, 0, leftSelection - left, height + top);
/*     */             
/* 258 */             graphics.setColor((Color)V8CpuOverviewChart.SELECTION_COLOR);
/* 259 */             graphics.drawRect(leftSelection, 0, rightSelection - leftSelection, height + top);
/* 260 */             graphics.fillRect(leftSelection, 0, rightSelection - leftSelection, height + top);
/*     */             
/* 262 */             graphics.setColor((Color)V8CpuOverviewChart.BACK_COLOR);
/* 263 */             graphics.drawRect(rightSelection, 0, left + width - rightSelection, height + top);
/* 264 */             graphics.fillRect(rightSelection, 0, left + width - rightSelection, height + top);
/*     */           } }
/*     */       );
/*     */   }
/*     */   
/*     */   private void drawChartBound(Graphics2D graphics, int top, int left, int height, int width) {
/* 270 */     graphics.setColor(getBackground());
/* 271 */     graphics.fillRect(0, top, left, height);
/* 272 */     graphics.fillRect(left + width, top, getWidth() - left + width, height);
/*     */   }
/*     */   
/*     */   protected abstract void selectionUpdated();
/*     */ }


/* Location:              C:\Program Files\JetBrains\WebStorm 2021.1\plugins\NodeJS\lib\NodeJS.jar!\com\jetbrains\nodejs\run\profile\cpu\v8lo\\ui\V8CpuOverviewChart.class
 * Java compiler version: 11 (55.0)
 * JD-Core Version:       1.1.3
 */