/*     */ package org.bipolar.run.profile.cpu.v8log.ui;
/*     */ import com.intellij.ide.ui.UISettings;
/*     */ import com.intellij.util.containers.Convertor;
/*     */ import com.intellij.util.ui.UIUtil;
/*     */ import org.bipolar.run.profile.V8Utils;
/*     */ import java.awt.BasicStroke;
/*     */ import java.awt.Color;
/*     */ import java.awt.Dimension;
/*     */ import java.awt.Font;
/*     */ import java.awt.Graphics;
/*     */ import java.awt.Graphics2D;
/*     */ import java.awt.Rectangle;
/*     */ import java.util.TreeMap;
/*     */ import javax.swing.JPanel;
/*     */
import org.jetbrains.annotations.Nls;
/*     */ import org.jetbrains.annotations.NotNull;
/*     */ 
/*     */ public abstract class ChartPanel extends JPanel {
/*     */   protected final int myLeft;
/*     */   protected int myRightMargin;
/*     */   protected int myTop;
/*     */   protected int myGridTop;
/*     */   protected int myHeight;
/*     */   protected long myRealLeft;
/*     */   protected long myRealRight;
/*     */   protected static final int myDragArea = 10;
/*     */   protected Font myGridFont;
/*     */   protected int myGridFontHeight;
/*     */   protected boolean myDrawLabels = true;
/*     */   
/*     */   public ChartPanel(int left, int rightMargin, int top, int height, long realLeft, long realRight) {
/*  32 */     this.myLeft = left;
/*  33 */     this.myRightMargin = rightMargin;
/*  34 */     this.myTop = top;
/*  35 */     this.myGridTop = this.myGridFontHeight;
/*  36 */     this.myHeight = height;
/*  37 */     this.myRealLeft = realLeft;
/*  38 */     this.myRealRight = realRight;
/*  39 */     this.myGridFont = getFont().deriveFont(UIUtil.getFontSize(UIUtil.FontSize.SMALL));
/*  40 */     this.myGridFontHeight = getFontMetrics(this.myGridFont).getHeight();
/*     */   }
/*     */   
/*     */   protected int pixelsFromReal(long real) {
/*  44 */     return this.myLeft + (int)Math.round((getWidth() - this.myLeft - this.myRightMargin) / (this.myRealRight - this.myRealLeft) * (real - this.myRealLeft));
/*     */   }
/*     */   
/*     */   protected long realFromPixels(int pixels) {
/*  48 */     return this.myRealLeft + Math.round((this.myRealRight - this.myRealLeft) / (getWidth() - this.myLeft - this.myRightMargin) * (pixels - this.myLeft));
/*     */   }
/*     */ 
/*     */   
/*     */   public Dimension getMinimumSize() {
/*  53 */     return new Dimension(this.myLeft + this.myRightMargin + 10, this.myHeight + this.myTop);
/*     */   }
/*     */ 
/*     */   
/*     */   public Dimension getPreferredSize() {
/*  58 */     Dimension size = super.getPreferredSize();
/*  59 */     return new Dimension(size.width, this.myHeight + this.myTop);
/*     */   }
/*     */ 
/*     */   
/*     */   public void paint(Graphics g) {
/*  64 */     super.paint(g);
/*  65 */     V8Utils.safeDraw((Graphics2D)g, graphics -> {
/*     */           UISettings.setupAntialiasing(graphics);
/*     */           int width1 = getWidth();
/*     */           drawChart(graphics, this.myLeft, this.myTop, width1 - this.myLeft - this.myRightMargin, this.myHeight, this.myGridFontHeight);
/*     */           drawTsGrid(graphics);
/*     */         });
/*  71 */     paintBorder(g);
/*     */   }
/*     */   
/*     */   protected void drawTsGrid(Graphics2D g) {
/*  75 */     GridParameters grid = new GridParameters();
/*  76 */     BasicStroke dashed = new BasicStroke(0.1F);
/*     */     
/*  78 */     g.setStroke(dashed);
/*  79 */     g.setColor(Color.gray);
/*  80 */     int unit = grid.getUnit();
/*  81 */     long current = Math.round(this.myRealLeft / unit) * unit;
/*  82 */     g.setFont(this.myGridFont);
/*  83 */     Rectangle visibleRect = getVisibleRect();
/*  84 */     while (current < this.myRealRight + unit) {
/*  85 */       int x = pixelsFromReal(current);
/*  86 */       if (x > this.myLeft && x < getWidth() - this.myRightMargin) {
/*  87 */         g.drawLine(x, visibleRect.y, x, visibleRect.y + this.myTop + getHeight());
/*  88 */         if (this.myDrawLabels) {
/*  89 */           g.drawString((String)grid.getToStringConvertor().convert(Long.valueOf(current / 1000L)), x + 3, visibleRect.y + this.myGridTop);
/*     */         }
/*     */       } 
/*  92 */       current += unit;
/*     */     } 
/*  94 */     g.setStroke(new BasicStroke(1.0F));
/*  95 */     g.setColor(Color.black);
/*     */   }
/*     */ 
/*     */   
/*  99 */   private static final TreeMap<Long, Convertor<Long, String>> ourGridMap = new TreeMap<>();
/*     */ 
/*     */   
/*     */   static {
/* 103 */     ourGridMap.put(Long.valueOf(1000L), createTsConvertor());
/* 104 */     ourGridMap.put(Long.valueOf(10000L), createTsConvertor());
/* 105 */     ourGridMap.put(Long.valueOf(100000L), ts -> {
/*     */           if (ts.longValue() > 1000L) {
/*     */             long whole = (long)Math.floor(ts.longValue() / 1000.0D);
/*     */             return "" + whole + "." + whole + "s";
/*     */           } 
/*     */           return "" + ts + "ms";
/*     */         });
/* 112 */     ourGridMap.put(Long.valueOf(1000000L), createSecondsConvertor());
/* 113 */     ourGridMap.put(Long.valueOf(10000000L), createSecondsConvertor());
/* 114 */   } private static final Convertor<Long, String> ourMinConvertor = createMinConvertor(); static {
/* 115 */     ourGridMap.put(Long.valueOf(60000000L), ourMinConvertor);
/* 116 */     ourGridMap.put(Long.valueOf(600000000L), ourMinConvertor);
/*     */   }
/*     */   @Nls
/*     */   public static String getIntervalDescription(long start, long end) {
/* 120 */     long interval = end - start;
/* 121 */     Long prevUnit = null;
/* 122 */     Convertor<Long, String> convertor = null;
/* 123 */     for (Long unit : ourGridMap.keySet()) {
/* 124 */       if (interval / unit.longValue() < 10L) {
/* 125 */         convertor = (prevUnit != null) ? ourGridMap.get(prevUnit) : ourGridMap.get(unit);
/*     */         break;
/*     */       } 
/* 128 */       prevUnit = unit;
/*     */     } 
/* 130 */     if (convertor == null) {
/* 131 */       convertor = (Convertor<Long, String>)ourGridMap.lastEntry().getValue();
/*     */     }
/* 133 */     return (String)convertor.convert(Long.valueOf(start / 1000L)) + "-" + (String)convertor.convert(Long.valueOf(start / 1000L));
/*     */   }
/*     */   
/*     */   private class GridParameters {
/*     */     private final int myUnit;
/*     */     private final Convertor<Long, String> myToStringConvertor;
/*     */     
/*     */     GridParameters() {
/* 141 */       for (Long unit : ChartPanel.ourGridMap.keySet()) {
/* 142 */         if (stepIsBig(ChartPanel.this.pixelsFromReal(ChartPanel.this.myRealLeft + unit.intValue()))) {
/* 143 */           this.myUnit = unit.intValue();
/* 144 */           this.myToStringConvertor = ChartPanel.ourGridMap.get(unit);
/*     */           return;
/*     */         } 
/*     */       } 
/* 148 */       this.myUnit = 60000000;
/* 149 */       this.myToStringConvertor = ChartPanel.ourMinConvertor;
/*     */     }
/*     */     
/*     */     public int getUnit() {
/* 153 */       return this.myUnit;
/*     */     }
/*     */     
/*     */     public Convertor<Long, String> getToStringConvertor() {
/* 157 */       return this.myToStringConvertor;
/*     */     }
/*     */     
/*     */     private boolean stepIsBig(int pixels) {
/* 161 */       return (pixels > 100);
/*     */     }
/*     */   }
/*     */   
/*     */   @NotNull
/*     */   private static Convertor<Long, String> createMinConvertor() {
/* 167 */     if ((ts -> "" + ts.longValue() / 60000L + "min") == null) $$$reportNull$$$0(0);  return ts -> "" + ts.longValue() / 60000L + "min";
/*     */   }
/*     */   
/*     */   @NotNull
/*     */   private static Convertor<Long, String> createTsConvertor() {
/* 172 */     if ((ts -> "" + ts + "ms") == null) $$$reportNull$$$0(1);  return ts -> "" + ts + "ms";
/*     */   }
/*     */   
/*     */   private static Convertor<Long, String> createSecondsConvertor() {
/* 176 */     return ts -> "" + ts.longValue() / 1000L + "s";
/*     */   }
/*     */   
/*     */   protected boolean aroundBound(int x, int bound) {
/* 180 */     return (x >= bound - 10 && x <= bound + 10);
/*     */   }
/*     */   
/*     */   protected abstract void drawChart(Graphics2D paramGraphics2D, int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5);
/*     */ }


/* Location:              C:\Program Files\JetBrains\WebStorm 2021.1\plugins\NodeJS\lib\NodeJS.jar!\com\jetbrains\nodejs\run\profile\cpu\v8lo\\ui\ChartPanel.class
 * Java compiler version: 11 (55.0)
 * JD-Core Version:       1.1.3
 */