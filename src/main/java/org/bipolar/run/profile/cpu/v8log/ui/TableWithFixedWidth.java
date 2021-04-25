/*    */ package org.bipolar.run.profile.cpu.v8log.ui;
/*    */ 
/*    */ import com.intellij.ui.table.JBTable;
/*    */ import java.awt.Dimension;
/*    */ import javax.swing.table.TableColumnModel;
/*    */ import javax.swing.table.TableModel;
/*    */ 
/*    */ 
/*    */ 
/*    */ public class TableWithFixedWidth
/*    */   extends JBTable
/*    */ {
/* 13 */   private int myMinWidth = 0;
/*    */   
/*    */   private boolean myFixWidth;
/*    */   
/*    */   public TableWithFixedWidth() {}
/*    */   
/*    */   public TableWithFixedWidth(TableModel model) {
/* 20 */     super(model);
/*    */   }
/*    */   
/*    */   public TableWithFixedWidth(TableModel model, TableColumnModel columnModel) {
/* 24 */     super(model, columnModel);
/*    */   }
/*    */   
/*    */   public TableWithFixedWidth fixWidth() {
/* 28 */     this.myFixWidth = true;
/* 29 */     return this;
/*    */   }
/*    */ 
/*    */   
/*    */   public Dimension getPreferredSize() {
/* 34 */     Dimension size = super.getPreferredSize();
/* 35 */     if (this.myMinWidth == 0) return size; 
/* 36 */     if (this.myFixWidth) return new Dimension(this.myMinWidth, size.height); 
/* 37 */     int parentWidth = getParent().getWidth();
/* 38 */     if (this.myMinWidth > parentWidth) return new Dimension(this.myMinWidth, size.height); 
/* 39 */     return new Dimension(parentWidth, size.height);
/*    */   }
/*    */   
/*    */   public void setMinWidth(int total) {
/* 43 */     this.myMinWidth = total;
/*    */   }
/*    */ }


/* Location:              C:\Program Files\JetBrains\WebStorm 2021.1\plugins\NodeJS\lib\NodeJS.jar!\com\jetbrains\nodejs\run\profile\cpu\v8lo\\ui\TableWithFixedWidth.class
 * Java compiler version: 11 (55.0)
 * JD-Core Version:       1.1.3
 */