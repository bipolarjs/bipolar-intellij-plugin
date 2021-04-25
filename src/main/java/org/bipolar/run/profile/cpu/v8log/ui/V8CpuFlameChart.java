/*     */ package org.bipolar.run.profile.cpu.v8log.ui;
/*     */ 
/*     */ import com.intellij.CommonBundle;
/*     */ import com.intellij.openapi.util.Pair;
/*     */ import com.intellij.ui.JBColor;
/*     */ import com.intellij.ui.components.JBScrollPane;
/*     */ import com.intellij.util.BeforeAfter;
/*     */ import com.intellij.util.Consumer;
/*     */ import com.intellij.util.EmptyConsumer;
/*     */ import com.intellij.util.containers.Convertor;
/*     */ import com.intellij.util.ui.UIUtil;
/*     */ import org.bipolar.run.profile.V8Utils;
/*     */ import org.bipolar.run.profile.cpu.v8log.data.V8CpuLogCall;
/*     */ import org.bipolar.run.profile.cpu.v8log.reading.FlameData;
/*     */ import org.bipolar.run.profile.cpu.v8log.reading.StackLineData;
/*     */ import org.bipolar.run.profile.cpu.v8log.reading.V8CpuViewCallback;
/*     */ import org.bipolar.run.profile.cpu.v8log.reading.V8LogCachingReader;
/*     */ import java.awt.BasicStroke;
/*     */ import java.awt.Color;
/*     */ import java.awt.Cursor;
/*     */ import java.awt.Dimension;
/*     */ import java.awt.FontMetrics;
/*     */ import java.awt.Graphics2D;
/*     */ import java.awt.Rectangle;
/*     */ import java.awt.event.AdjustmentEvent;
/*     */ import java.awt.event.AdjustmentListener;
/*     */ import java.awt.event.MouseAdapter;
/*     */ import java.awt.event.MouseEvent;
/*     */ import java.io.IOException;
/*     */ import java.util.Collections;
/*     */ import java.util.HashMap;
/*     */ import java.util.List;
/*     */ import java.util.Map;
/*     */ import java.util.TreeMap;
/*     */
import org.jetbrains.annotations.NotNull;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public abstract class V8CpuFlameChart
/*     */   extends ChartPanel
/*     */ {
/*     */   public final int STACK_ELEMENT_HEIGHT;
/*     */   private final V8LogCachingReader myReader;
/*     */   private final Consumer<? super String> myNotificator;
/*     */   private JBScrollPane myParentScroll;
/*     */   private FlameData myFlameData;
/*     */   private Map<Long, JBColor> myColors;
/*     */   private boolean myIsLoading;
/*     */   private boolean myHaveData;
/*     */   private volatile long myDetailsPosition;
/*     */   private volatile int myDetailsIdx;
/*     */   private boolean myNearDetailsPosition;
/*     */   private final Object myLock;
/*     */   private long mySelectionFrom;
/*     */   private long mySelectionTo;
/*     */   private final Convertor<Long, Integer> myCoordConvertor;
/*     */   private final int myMinTextWidth;
/*     */   private Map<Long, String> myStrings;
/*     */   private Integer mySelectedInChartRow;
/*     */   private Consumer<Boolean> myUIUpdate;
/*     */   
/*     */   public V8CpuFlameChart(int left, int rightMargin, int top, V8LogCachingReader reader, Consumer<? super String> notificator, Long leftTs, Long rightTs, @NotNull V8CpuViewCallback viewCallback) {
/*  64 */     super(left, rightMargin, top, reader.getMaxStackSize(), (leftTs == null) ? 0L : leftTs.longValue(), (rightTs == null) ? reader.getLastTick() : rightTs.longValue());
/*  65 */     this.myDrawLabels = false;
/*  66 */     setFont(getFont().deriveFont(UIUtil.getFontSize(UIUtil.FontSize.SMALL)));
/*  67 */     FontMetrics metrics = getFontMetrics(getFont());
/*  68 */     this.STACK_ELEMENT_HEIGHT = metrics.getHeight();
/*  69 */     this.myLock = new Object();
/*  70 */     this.mySelectionFrom = 0L;
/*  71 */     this.mySelectionTo = reader.getLastTick();
/*  72 */     this.myNotificator = notificator;
/*  73 */     this.myMinTextWidth = metrics.stringWidth("test");
/*  74 */     this.myHaveData = false;
/*  75 */     this.myIsLoading = false;
/*  76 */     this.myReader = reader;
/*  77 */     this.myDetailsPosition = 0L;
/*  78 */     this.myDetailsIdx = 0;
/*  79 */     this.myCoordConvertor = (ts -> Integer.valueOf(pixelsFromReal(ts.longValue())));
/*  80 */     MouseAdapter adapter = createMouseAdapter();
/*  81 */     addMouseListener(adapter);
/*  82 */     addMouseMotionListener(adapter);
/*     */   }
/*     */   
/*     */   @NotNull
/*     */   private MouseAdapter createMouseAdapter() {
/*  87 */     return new MouseAdapter()
/*     */       {
/*     */         public void mouseDragged(MouseEvent e) {
/*  90 */           if (V8CpuFlameChart.this.myNearDetailsPosition) {
/*  91 */             int x = e.getX();
/*  92 */             if (x < V8CpuFlameChart.this.myLeft || x > V8CpuFlameChart.this.getWidth() - V8CpuFlameChart.this.myRightMargin)
/*  93 */               return;  long was = V8CpuFlameChart.this.myDetailsPosition;
/*  94 */             V8CpuFlameChart.this.myDetailsPosition = V8CpuFlameChart.this.realFromPixels(x);
/*  95 */             if (was != V8CpuFlameChart.this.myDetailsPosition) {
/*  96 */               V8CpuFlameChart.this.calculateNewSelectedRow(e.getY(), V8CpuFlameChart.this.myDetailsPosition);
/*  97 */               V8CpuFlameChart.this.detailsChanged();
/*  98 */               V8CpuFlameChart.this.revalidate();
/*  99 */               V8CpuFlameChart.this.repaint();
/*     */             } 
/*     */           } 
/*     */         }
/*     */ 
/*     */         
/*     */         public void mouseMoved(MouseEvent e) {
/* 106 */           int x = e.getX();
/* 107 */           int detailsPixels = V8CpuFlameChart.this.pixelsFromReal(V8CpuFlameChart.this.myDetailsPosition);
/* 108 */           V8CpuFlameChart.this.myNearDetailsPosition = V8CpuFlameChart.this.aroundBound(x, detailsPixels);
/* 109 */           if (V8CpuFlameChart.this.myNearDetailsPosition) {
/* 110 */             V8CpuFlameChart.this.setCursor(new Cursor(10));
/*     */           } else {
/*     */             
/* 113 */             V8CpuFlameChart.this.setCursor(Cursor.getDefaultCursor());
/*     */           } 
/*     */         }
/*     */ 
/*     */         
/*     */         public void mouseClicked(MouseEvent e) {
/* 119 */           int x = e.getX();
/* 120 */           V8CpuFlameChart.this.myDetailsPosition = V8CpuFlameChart.this.realFromPixels(x);
/* 121 */           V8CpuFlameChart.this.calculateNewSelectedRow(e.getY(), V8CpuFlameChart.this.myDetailsPosition);
/* 122 */           V8CpuFlameChart.this.detailsChanged();
/* 123 */           V8CpuFlameChart.this.revalidate();
/* 124 */           V8CpuFlameChart.this.repaint();
/*     */         }
/*     */       };
/*     */   }
/*     */   
/*     */   private void calculateNewSelectedRowForStringId(long ts, long stringId) {
/* 130 */     if (this.myFlameData == null) {
/* 131 */       this.mySelectedInChartRow = null;
/*     */       return;
/*     */     } 
/* 134 */     for (int i = 0; i < this.myFlameData.getStackLineData().size(); i++) {
/* 135 */       TreeMap<Long, StackLineData> map = this.myFlameData.getStackLineData().get(i);
/* 136 */       Map.Entry<Long, StackLineData> entry = map.floorEntry(Long.valueOf(ts));
/* 137 */       if (entry != null && ((Long)entry.getKey()).longValue() + ((StackLineData)entry.getValue()).getDuration() >= ts && ((StackLineData)entry
/* 138 */         .getValue()).getStringId() == stringId) {
/* 139 */         this.mySelectedInChartRow = Integer.valueOf(i);
/*     */         break;
/*     */       } 
/*     */     } 
/*     */   }
/*     */   
/*     */   private void calculateNewSelectedRow(int y, long ts) {
/* 146 */     if (this.myFlameData == null) {
/* 147 */       this.mySelectedInChartRow = null;
/*     */       return;
/*     */     } 
/* 150 */     int idx = (int)Math.floor(((getWholeHeight() - y) / this.STACK_ELEMENT_HEIGHT));
/* 151 */     int max = this.myFlameData.getStackLineData().size();
/* 152 */     idx = (idx >= max) ? (max - 1) : idx;
/* 153 */     while (idx >= 0 && idx < max) {
/* 154 */       TreeMap<Long, StackLineData> map = this.myFlameData.getStackLineData().get(idx);
/* 155 */       Map.Entry<Long, StackLineData> entry = map.floorEntry(Long.valueOf(ts));
/* 156 */       if (entry != null && ((Long)entry.getKey()).longValue() + ((StackLineData)entry.getValue()).getDuration() >= ts) {
/* 157 */         this.mySelectedInChartRow = Integer.valueOf(idx);
/*     */         return;
/*     */       } 
/* 160 */       idx--;
/*     */     } 
/* 162 */     this.mySelectedInChartRow = null;
/*     */   }
/*     */   
/*     */   public void setParentScroll(JBScrollPane parentScroll) {
/* 166 */     this.myParentScroll = parentScroll;
/* 167 */     this.myParentScroll.getVerticalScrollBar().addAdjustmentListener(new AdjustmentListener()
/*     */         {
/*     */           public void adjustmentValueChanged(AdjustmentEvent e) {
/* 170 */             V8CpuFlameChart.this.repaint();
/*     */           }
/*     */         });
/*     */   }
/*     */   
/*     */   private int getWholeHeight() {
/* 176 */     if (this.myParentScroll != null) {
/* 177 */       if (this.myFlameData == null) return this.myParentScroll.getHeight(); 
/* 178 */       return Math.max(this.myParentScroll.getHeight(), this.myFlameData.getStackLineData().size() * this.STACK_ELEMENT_HEIGHT + this.myTop);
/*     */     } 
/* 180 */     return (this.myFlameData == null) ? (this.myTop + this.STACK_ELEMENT_HEIGHT) : (this.myFlameData.getStackLineData().size() * this.STACK_ELEMENT_HEIGHT + this.myTop);
/*     */   }
/*     */ 
/*     */   
/*     */   public Dimension getMinimumSize() {
/* 185 */     return new Dimension(this.myLeft + this.myRightMargin + 10, getWholeHeight());
/*     */   }
/*     */ 
/*     */   
/*     */   public Dimension getPreferredSize() {
/* 190 */     Dimension size = super.getPreferredSize();
/* 191 */     return new Dimension(size.width, getWholeHeight());
/*     */   }
/*     */   
/*     */   public Integer getSelectedInChartRow() {
/* 195 */     return this.mySelectedInChartRow;
/*     */   }
/*     */   
/*     */   @NotNull
/*     */   public Pair<Long, Integer> getDetailsPosition() {
/* 200 */     if (Pair.create(Long.valueOf(this.myDetailsPosition), Integer.valueOf(this.myDetailsIdx)) == null) $$$reportNull$$$0(1);  return Pair.create(Long.valueOf(this.myDetailsPosition), Integer.valueOf(this.myDetailsIdx));
/*     */   }
/*     */   
/*     */   private int findDetailsIdx() {
/* 204 */     if (this.myFlameData == null) return 0; 
/* 205 */     List<Long> times = this.myFlameData.getTimes();
/* 206 */     int firstTickOffset = this.myFlameData.getFirstTickOffset();
/* 207 */     int idx = Collections.binarySearch((List)times, Long.valueOf(this.myDetailsPosition));
/* 208 */     if (idx >= 0) {
/* 209 */       return firstTickOffset + idx;
/*     */     }
/* 211 */     int position = -idx - 1;
/* 212 */     return (position < 0) ? firstTickOffset : (firstTickOffset + position - 1);
/*     */   }
/*     */   
/*     */   protected void detailsChanged() {
/* 216 */     this.myDetailsIdx = findDetailsIdx();
/*     */   }
/*     */ 
/*     */   
/*     */   public void updateData() {
/*     */     long from, to;
/* 222 */     synchronized (this.myLock) {
/* 223 */       from = this.mySelectionFrom;
/* 224 */       to = this.mySelectionTo;
/*     */     } 
/* 226 */     long minInterval = (getWidth() == 0) ? 1000L : (realFromPixels(this.myMinTextWidth) - realFromPixels(this.myLeft));
/*     */     try {
/* 228 */       FlameData flameData = this.myReader.getStack(from, to);
/* 229 */       Map<Long, JBColor> colors = new HashMap<>();
/* 230 */       Map<Long, String> strings = new HashMap<>();
/* 231 */       if (flameData != null) {
/* 232 */         List<TreeMap<Long, StackLineData>> data = flameData.getStackLineData();
/* 233 */         for (TreeMap<Long, StackLineData> map : data) {
/* 234 */           for (StackLineData lineData : map.values()) {
/* 235 */             long code = lineData.getStringId();
/* 236 */             if (!colors.containsKey(Long.valueOf(code))) colors.put(Long.valueOf(code), FlameColors.getColor(code, this.myReader.getCodeScopeByStringId(code))); 
/* 237 */             if (lineData.getDuration() >= minInterval) {
/* 238 */               V8CpuLogCall call = V8CpuLogCall.create(this.myReader.getStringById(lineData.getStringId()), lineData.getStringId());
/* 239 */               strings.put(Long.valueOf(lineData.getStringId()), call.getShort());
/*     */             } 
/*     */           } 
/*     */         } 
/*     */       } 
/* 244 */       this.myUIUpdate = createUIUpdate(from, to, flameData, colors, strings);
/*     */     }
/* 246 */     catch (IOException e) {
/* 247 */       this.myNotificator.consume("Can not get data for flame chart: " + e.getMessage());
/* 248 */       this.myUIUpdate = EmptyConsumer.getInstance();
/*     */     } 
/*     */   }
/*     */   
/*     */   public Consumer<Boolean> getUIUpdate() {
/* 253 */     return this.myUIUpdate;
/*     */   }
/*     */   
/*     */   public void selection(long from, long to) throws IOException {
/* 257 */     assert from >= 0L;
/* 258 */     assert to >= 0L;
/* 259 */     this.myIsLoading = true;
/* 260 */     synchronized (this.myLock) {
/* 261 */       this.mySelectionFrom = from;
/* 262 */       this.mySelectionTo = to;
/*     */     } 
/*     */   }
/*     */   
/*     */   public BeforeAfter<Long> getSelection() {
/* 267 */     synchronized (this.myLock) {
/* 268 */       return new BeforeAfter(Long.valueOf(this.mySelectionFrom), Long.valueOf(this.mySelectionTo));
/*     */     } 
/*     */   }
/*     */   
/*     */   public boolean isEmpty() {
/* 273 */     return (this.myFlameData == null);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private Consumer<Boolean> createUIUpdate(long from, long to, FlameData flameData, Map<Long, JBColor> colors, Map<Long, String> strings) {
/* 281 */     return reloadingIsEmpty -> {
/*     */         this.myRealLeft = from;
/*     */         this.myRealRight = to;
/*     */         this.myIsLoading = !reloadingIsEmpty.booleanValue();
/*     */         if (flameData != null) {
/*     */           this.myHaveData = true;
/*     */           this.myFlameData = flameData;
/*     */           this.myColors = colors;
/*     */           this.myStrings = strings;
/*     */           if (this.myDetailsPosition <= this.myRealLeft || this.myDetailsPosition >= this.myRealRight) {
/*     */             this.myDetailsPosition = (this.myRealLeft + this.myRealRight) / 2L;
/*     */             calculateNewSelectedRow(0, this.myDetailsPosition);
/*     */             detailsChanged();
/*     */           } 
/*     */           this.myParentScroll.revalidate();
/*     */           this.myParentScroll.repaint();
/*     */         } else {
/*     */           this.myFlameData = null;
/*     */           this.myHaveData = false;
/*     */         } 
/*     */         revalidate();
/*     */         repaint();
/*     */       };
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   protected void drawTsGrid(Graphics2D g) {
/* 309 */     if (this.myHaveData) {
/* 310 */       super.drawTsGrid(g);
/*     */     }
/*     */   }
/*     */ 
/*     */   
/*     */   protected void drawChart(Graphics2D graphics2D, int left, int top, int width, int height, int gridGap) {
/* 316 */     V8Utils.safeDraw(graphics2D, graphics -> {
/*     */           int wholeHeight = getWholeHeight();
/*     */           Rectangle visibleRect = getVisibleRect();
/*     */           if (this.myIsLoading) {
/*     */             displayMessageInCenter(graphics, CommonBundle.getLoadingTreeNodeText(), left, width);
/*     */           } else if (this.myHaveData) {
/*     */             Rectangle clip = graphics.getClipBounds();
/*     */             Drawer drawer = new Drawer(this.myCoordConvertor, wholeHeight, graphics, this.myColors, this.myStrings, getBackground(), clip.y, clip.height, this.myMinTextWidth, left + width, this.STACK_ELEMENT_HEIGHT, visibleRect.y + this.myTop, this.myDetailsPosition, this.mySelectedInChartRow);
/*     */             drawer.draw(this.myFlameData.getStackLineData());
/*     */           } else {
/*     */             displayMessageInCenter(graphics, "Too many data points. Select smaller interval.", left, width);
/*     */           } 
/*     */           graphics.setStroke(new BasicStroke(1.0F));
/*     */           graphics.setColor(Color.gray);
/*     */           int height1 = Math.min(wholeHeight, visibleRect.height - top - 1);
/*     */           if (this.myHaveData) {
/*     */             int details = pixelsFromReal(this.myDetailsPosition);
/*     */             drawDetailsLine(graphics, visibleRect, height1, details, top);
/*     */           } 
/*     */         });
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static void drawDetailsLine(Graphics2D graphics, Rectangle visibleRect, int height, int details, int top) {
/* 344 */     graphics.setStroke(new BasicStroke(2.0F));
/* 345 */     graphics.setColor(Color.black);
/* 346 */     graphics.drawLine(details, visibleRect.y, details, visibleRect.y + visibleRect.height);
/*     */   }
/*     */   
/*     */   private void displayMessageInCenter(Graphics2D graphics, String text, int left, int width) {
/* 350 */     Rectangle visibleRect = getVisibleRect();
/* 351 */     graphics.setColor(Color.gray);
/* 352 */     int stringWidth = graphics.getFontMetrics().stringWidth(text);
/* 353 */     graphics.drawString(text, left + (width - stringWidth) / 2, visibleRect.height / 2);
/*     */   }
/*     */   
/*     */   public void setDetailsPosition(Long real, long stringId) {
/* 357 */     this.myDetailsPosition = real.longValue();
/* 358 */     this.mySelectedInChartRow = null;
/* 359 */     detailsChanged();
/* 360 */     if (stringId >= 0L) {
/* 361 */       calculateNewSelectedRowForStringId(real.longValue(), stringId);
/*     */     }
/* 363 */     revalidate();
/* 364 */     repaint();
/*     */   }
/*     */   
/*     */   public void setSelectedCells(int detailsCount, int[] rows) {
/* 368 */     this.mySelectedInChartRow = (rows == null || rows.length == 0) ? null : Integer.valueOf(detailsCount - rows[0] - 1);
/* 369 */     if (this.mySelectedInChartRow != null && this.myParentScroll != null) {
/* 370 */       int wholeHeight = getWholeHeight();
/* 371 */       int upper = wholeHeight - (this.mySelectedInChartRow.intValue() + 1) * this.STACK_ELEMENT_HEIGHT;
/* 372 */       scrollRectToVisible(new Rectangle(0, upper, getWidth(), this.STACK_ELEMENT_HEIGHT));
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   private static class Drawer
/*     */   {
/*     */     private final Convertor<Long, Integer> myToCoordConvertor;
/*     */     
/*     */     private final int myWholeHeight;
/*     */     
/*     */     private final Graphics2D myGraphics;
/*     */     
/*     */     private final Map<Long, JBColor> myColors;
/*     */     
/*     */     private final Map<Long, String> myStrings;
/*     */     private final Color myBackground;
/*     */     private final int myWidth;
/*     */     private final int myMinWidth;
/*     */     private final int STACK_ELEMENT_HEIGHT;
/*     */     private final int myTop;
/*     */     private final long myDetailsPosition;
/*     */     private final Integer mySelectedRow;
/*     */     private final int myHighestIdx;
/*     */     private final int myLowestIdx;
/*     */     private final FontMetrics myMetrics;
/*     */     private int x1;
/*     */     private int x2;
/*     */     private int y1;
/*     */     private int y2;
/*     */     private int myIdx;
/*     */     private Long myTs;
/*     */     private StackLineData myLineData;
/*     */     
/*     */     Drawer(Convertor<Long, Integer> toCoordConvertor, int wholeHeight, Graphics2D graphics2D, Map<Long, JBColor> colors, Map<Long, String> strings, Color background, int clipYCoord, int clipHeight, int minTextWidth, int width, int stack_element_height, int top, long detailsPosition, Integer selectedRow) {
/* 407 */       this.myToCoordConvertor = toCoordConvertor;
/* 408 */       this.myWholeHeight = wholeHeight;
/* 409 */       this.myGraphics = graphics2D;
/* 410 */       this.myColors = colors;
/* 411 */       this.myStrings = strings;
/* 412 */       this.myBackground = background;
/* 413 */       this.myWidth = width;
/* 414 */       this.STACK_ELEMENT_HEIGHT = stack_element_height;
/* 415 */       this.myTop = top;
/* 416 */       this.myDetailsPosition = detailsPosition;
/* 417 */       this.mySelectedRow = selectedRow;
/* 418 */       this.myHighestIdx = (int)Math.ceil((this.myWholeHeight - clipYCoord) / this.STACK_ELEMENT_HEIGHT) - 1;
/* 419 */       this.myLowestIdx = Math.max(0, (int)(Math.floor((this.myWholeHeight - clipYCoord - clipHeight) / this.STACK_ELEMENT_HEIGHT) - 1.0D));
/*     */       
/* 421 */       this.myMinWidth = minTextWidth;
/* 422 */       graphics2D.setFont(graphics2D.getFont().deriveFont(UIUtil.getFontSize(UIUtil.FontSize.SMALL)));
/* 423 */       this.myMetrics = this.myGraphics.getFontMetrics();
/*     */     }
/*     */     
/*     */     private int lowY(int idx) {
/* 427 */       return this.myWholeHeight - (idx + 1) * this.STACK_ELEMENT_HEIGHT;
/*     */     }
/*     */     
/*     */     private int highY(int idx) {
/* 431 */       return this.myWholeHeight - idx * this.STACK_ELEMENT_HEIGHT;
/*     */     }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     private void iterateData(List<TreeMap<Long, StackLineData>> data, Consumer<StackLineData> consumer) {
/* 443 */       for (this.myIdx = this.myLowestIdx; this.myIdx <= Math.min(this.myHighestIdx, data.size() - 1); this.myIdx++) {
/* 444 */         TreeMap<Long, StackLineData> map = data.get(this.myIdx);
/* 445 */         for (Map.Entry<Long, StackLineData> entry : map.entrySet()) {
/* 446 */           this.myTs = entry.getKey();
/* 447 */           this.myLineData = entry.getValue();
/* 448 */           this.x1 = ((Integer)this.myToCoordConvertor.convert(this.myTs)).intValue();
/* 449 */           this.x2 = ((Integer)this.myToCoordConvertor.convert(Long.valueOf(this.myTs.longValue() + this.myLineData.getDuration()))).intValue();
/* 450 */           this.y1 = lowY(this.myIdx);
/* 451 */           this.y2 = highY(this.myIdx);
/* 452 */           if (this.y1 < this.myTop && this.y2 < this.myTop)
/* 453 */             continue;  if (this.y1 < this.myTop) {
/* 454 */             this.y1 = this.myTop;
/*     */           }
/* 456 */           consumer.consume(this.myLineData);
/*     */         } 
/*     */       } 
/*     */     }
/*     */     
/*     */     public void draw(List<TreeMap<Long, StackLineData>> data) {
/* 462 */       iterateData(data, lineData -> {
/*     */             Color color = (Color)this.myColors.get(Long.valueOf(lineData.getStringId()));
/*     */             this.myGraphics.setColor(color);
/*     */             int width1 = this.x2 - this.x1;
/*     */             this.myGraphics.fillRect(this.x1, this.y1, width1, this.y2 - this.y1);
/*     */           });
/* 468 */       this.myGraphics.setColor(this.myBackground);
/* 469 */       this.myGraphics.setStroke(new BasicStroke(1.0F));
/* 470 */       BorderDrawer[] last = new BorderDrawer[1];
/* 471 */       iterateData(data, data1 -> {
/*     */             BorderDrawer drawer = new BorderDrawer(this.x1, this.x2, this.y1, this.y2);
/*     */             
/*     */             if (this.myTs.longValue() <= this.myDetailsPosition && this.myTs.longValue() + this.myLineData.getDuration() >= this.myDetailsPosition && this.mySelectedRow != null && this.mySelectedRow.intValue() == this.myIdx) {
/*     */               last[0] = drawer;
/*     */             } else {
/*     */               drawer.draw(this.myGraphics);
/*     */             } 
/*     */           });
/* 480 */       if (last[0] != null) {
/* 481 */         this.myGraphics.setColor(Color.black);
/* 482 */         this.myGraphics.setStroke(new BasicStroke(1.5F));
/* 483 */         last[0].draw(this.myGraphics);
/*     */       } 
/* 485 */       iterateData(data, lineData -> {
/*     */             if (this.y2 - this.y1 < this.STACK_ELEMENT_HEIGHT)
/*     */               return; 
/*     */             int correctedX = Math.max(0, this.x1);
/*     */             int correctedWidth = Math.min(this.x2, this.myWidth) - correctedX;
/*     */             if (correctedWidth >= this.myMinWidth) {
/*     */               String text = this.myStrings.get(Long.valueOf(lineData.getStringId()));
/*     */               if (text != null) {
/*     */                 this.myGraphics.setColor(Color.black);
/*     */                 String str = cutString(text, correctedWidth - 4, this.myMetrics);
/*     */                 this.myGraphics.drawString(str, correctedX + 2, this.y2 - this.myMetrics.getDescent());
/*     */               } 
/*     */             } 
/*     */           });
/*     */     }
/*     */     
/*     */     private static class BorderDrawer { private final int x1;
/*     */       private final int x2;
/*     */       private final int y1;
/*     */       private final int y2;
/*     */       
/*     */       BorderDrawer(int x1, int x2, int y1, int y2) {
/* 507 */         this.x1 = x1;
/* 508 */         this.x2 = x2;
/* 509 */         this.y1 = y1;
/* 510 */         this.y2 = y2;
/*     */       }
/*     */       
/*     */       public void draw(Graphics2D graphics) {
/* 514 */         int width = this.x2 - this.x1;
/* 515 */         if (width > 2) {
/* 516 */           graphics.drawLine(this.x1, this.y1, this.x1, this.y2);
/* 517 */           graphics.drawLine(this.x2, this.y1, this.x2, this.y2);
/*     */         } 
/* 519 */         graphics.drawLine(this.x1, this.y1, this.x2, this.y1);
/* 520 */         graphics.drawLine(this.x1, this.y2, this.x2, this.y2);
/*     */       } }
/*     */ 
/*     */     
/*     */     private String cutString(String text, int max, FontMetrics fm) {
/* 525 */       StringBuilder sb = new StringBuilder();
/* 526 */       int width = 0;
/* 527 */       for (int i = 0; i < text.length(); i++) {
/* 528 */         char c = text.charAt(i);
/* 529 */         int cw = fm.charWidth(c);
/* 530 */         if (width + cw > max) return sb.toString(); 
/* 531 */         sb.append(c);
/* 532 */         width += cw;
/*     */       } 
/* 534 */       return sb.toString();
/*     */     }
/*     */   }
/*     */   
/*     */   private static class BorderDrawer {
/*     */     private final int x1;
/*     */     private final int x2;
/*     */     private final int y1;
/*     */     private final int y2;
/*     */     
/*     */     BorderDrawer(int x1, int x2, int y1, int y2) {
/*     */       this.x1 = x1;
/*     */       this.x2 = x2;
/*     */       this.y1 = y1;
/*     */       this.y2 = y2;
/*     */     }
/*     */     
/*     */     public void draw(Graphics2D graphics) {
/*     */       int width = this.x2 - this.x1;
/*     */       if (width > 2) {
/*     */         graphics.drawLine(this.x1, this.y1, this.x1, this.y2);
/*     */         graphics.drawLine(this.x2, this.y1, this.x2, this.y2);
/*     */       } 
/*     */       graphics.drawLine(this.x1, this.y1, this.x2, this.y1);
/*     */       graphics.drawLine(this.x1, this.y2, this.x2, this.y2);
/*     */     }
/*     */   }
/*     */ }


/* Location:              C:\Program Files\JetBrains\WebStorm 2021.1\plugins\NodeJS\lib\NodeJS.jar!\com\jetbrains\nodejs\run\profile\cpu\v8lo\\ui\V8CpuFlameChart.class
 * Java compiler version: 11 (55.0)
 * JD-Core Version:       1.1.3
 */