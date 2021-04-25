/*    */ package org.bipolar.run.profile.cpu.view;
/*    */ 
/*    */ import com.intellij.ui.JBColor;
/*    */ import com.intellij.util.containers.Convertor;
/*    */ import com.intellij.util.ui.StartupUiUtil;
/*    */ import com.intellij.util.ui.UIUtil;
/*    */ import org.bipolar.run.profile.V8Utils;
/*    */ import java.awt.Color;
/*    */ import java.awt.Dimension;
/*    */ import java.awt.Graphics;
/*    */ import java.awt.Graphics2D;
/*    */ import java.awt.RenderingHints;
/*    */ import java.util.List;
/*    */ import javax.swing.JPanel;
/*    */
import org.jetbrains.annotations.NotNull;
/*    */ 
/*    */ 
/*    */ public class PieChartDiagram<T>
/*    */   extends JPanel
/*    */ {
/*    */   private static final int SPACE = 1;
/*    */   private final int myHeight;
/* 23 */   private double myHoleCoeff = 0.75D;
/* 24 */   private int myStartAngle = 135;
/*    */ 
/*    */   
/*    */   @NotNull
/*    */   private final List<? extends T> myData;
/*    */ 
/*    */   
/*    */   public PieChartDiagram(int height, @NotNull List<? extends T> data, @NotNull Convertor<? super T, Integer> dataConvertor, @NotNull Convertor<? super T, ? extends JBColor> colorConvertor) {
/* 32 */     this.myHeight = height;
/* 33 */     this.myData = data;
/* 34 */     this.myDataConvertor = dataConvertor;
/* 35 */     this.myColorConvertor = colorConvertor;
/*    */   } @NotNull
/*    */   private final Convertor<? super T, Integer> myDataConvertor; @NotNull
/*    */   private final Convertor<? super T, ? extends JBColor> myColorConvertor; public PieChartDiagram<T> withHoleCoeff(double holeCoeff) {
/* 39 */     if (holeCoeff > 0.0D && holeCoeff < 1.0D) {
/* 40 */       this.myHoleCoeff = holeCoeff;
/*    */     }
/* 42 */     return this;
/*    */   }
/*    */   
/*    */   public PieChartDiagram<T> withStartAngle(int startAngle) {
/* 46 */     this.myStartAngle = startAngle;
/* 47 */     return this;
/*    */   }
/*    */ 
/*    */   
/*    */   public Dimension getPreferredSize() {
/* 52 */     return new Dimension(this.myHeight + 4, this.myHeight + 4);
/*    */   }
/*    */ 
/*    */   
/*    */   public void paint(Graphics g) {
/* 57 */     super.paint(g);
/* 58 */     V8Utils.safeDraw((Graphics2D)g, graphics2D -> {
/*    */           graphics2D.translate(2, 2);
/*    */           graphics2D.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
/*    */           graphics2D.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
/*    */           graphics2D.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
/*    */           double coeff = (360.0D - (this.myData.size() * 1)) / 1000.0D;
/*    */           int inner = (int)(this.myHeight * this.myHoleCoeff);
/*    */           int innerCenter = (int)(0.5D * this.myHeight * (1.0D - this.myHoleCoeff));
/*    */           int startAngle = this.myStartAngle;
/*    */           Color border1 = StartupUiUtil.isUnderDarcula() ? UIUtil.getPanelBackground().brighter() : UIUtil.getPanelBackground().darker();
/*    */           graphics2D.setColor(border1);
/*    */           graphics2D.fillArc(0, 0, this.myHeight, this.myHeight, 0, 360);
/*    */           for (T line : this.myData) {
/*    */             int angleDiff = (int)Math.round(coeff * ((Integer)this.myDataConvertor.convert(line)).intValue());
/*    */             graphics2D.setColor((Color)this.myColorConvertor.convert(line));
/*    */             graphics2D.fillArc(0, 0, this.myHeight, this.myHeight, startAngle, -angleDiff);
/*    */             graphics2D.setColor(UIUtil.getPanelBackground());
/*    */             startAngle -= angleDiff + 1;
/*    */           } 
/*    */           graphics2D.fillArc(innerCenter, innerCenter, inner, inner, 0, 360);
/*    */           graphics2D.setColor(border1);
/*    */           graphics2D.drawArc(0, 0, this.myHeight, this.myHeight, 0, 360);
/*    */           graphics2D.drawArc(innerCenter, innerCenter, inner, inner, 0, 360);
/*    */         });
/*    */   }
/*    */ }


/* Location:              C:\Program Files\JetBrains\WebStorm 2021.1\plugins\NodeJS\lib\NodeJS.jar!\com\jetbrains\nodejs\run\profile\cpu\view\PieChartDiagram.class
 * Java compiler version: 11 (55.0)
 * JD-Core Version:       1.1.3
 */