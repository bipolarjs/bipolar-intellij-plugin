/*    */ package org.bipolar.run.profile.heap.view.renderers;
/*    */ 
/*    */ import com.intellij.ui.ColoredTableCellRenderer;
/*    */ import com.intellij.ui.SimpleTextAttributes;
/*    */ import com.intellij.util.ui.StartupUiUtil;
/*    */ import com.intellij.util.ui.UIUtil;
/*    */ import java.awt.Color;
/*    */ import javax.swing.JTable;
/*    */ import org.jetbrains.annotations.NotNull;
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public class RightAlignedRenderer
/*    */   extends ColoredTableCellRenderer
/*    */ {
/* 17 */   private final SimpleTextAttributes myBlackTextAttributes = SimpleTextAttributes.REGULAR_ATTRIBUTES
/* 18 */     .derive(-1, UIUtil.getTableForeground(), null, null);
/* 19 */   private final SimpleTextAttributes mySelectionAttributes = SimpleTextAttributes.REGULAR_ATTRIBUTES
/* 20 */     .derive(-1, UIUtil.getTableSelectionForeground(), null, null);
/* 21 */   private final SimpleTextAttributes myBlackBoldTextAttributes = SimpleTextAttributes.REGULAR_ATTRIBUTES
/* 22 */     .derive(1, UIUtil.getTableForeground(), null, null);
/* 23 */   private final SimpleTextAttributes myBoldSelectionAttributes = SimpleTextAttributes.REGULAR_ATTRIBUTES
/* 24 */     .derive(1, UIUtil.getTableSelectionForeground(), null, null);
/* 25 */   private Color myBackground = UIUtil.getTreeUnfocusedSelectionBackground();
/*    */ 
/*    */   
/*    */   protected void customizeCellRenderer(@NotNull JTable table, Object value, boolean selected, boolean hasFocus, int row, int column) {
/* 29 */     if (table == null) $$$reportNull$$$0(0);  setTextAlign(4);
/* 30 */     boolean lineHasFocus = table.hasFocus();
/* 31 */     String fragment = value.toString();
/* 32 */     if (!lineHasFocus && selected) {
/* 33 */       SimpleTextAttributes attributes; setBackground(this.myBackground);
/*    */       
/* 35 */       if (isBold(value)) {
/* 36 */         attributes = StartupUiUtil.isUnderDarcula() ? this.myBoldSelectionAttributes : this.myBlackBoldTextAttributes;
/*    */       } else {
/* 38 */         attributes = StartupUiUtil.isUnderDarcula() ? this.mySelectionAttributes : this.myBlackTextAttributes;
/*    */       } 
/* 40 */       append(fragment, attributes);
/* 41 */     } else if (selected) {
/* 42 */       append(fragment, isBold(value) ? this.myBoldSelectionAttributes : this.mySelectionAttributes);
/*    */     } else {
/* 44 */       append(fragment, isBold(value) ? this.myBlackBoldTextAttributes : this.myBlackTextAttributes);
/*    */     } 
/* 46 */     setPaintFocusBorder(false);
/*    */   }
/*    */   
/*    */   public RightAlignedRenderer setDefaultBackground(Color background) {
/* 50 */     this.myBackground = background;
/* 51 */     return this;
/*    */   }
/*    */   
/*    */   protected boolean isBold(Object value) {
/* 55 */     return false;
/*    */   }
/*    */ 
/*    */   
/*    */   protected SimpleTextAttributes modifyAttributes(SimpleTextAttributes attributes) {
/* 60 */     return attributes;
/*    */   }
/*    */ }


/* Location:              C:\Program Files\JetBrains\WebStorm 2021.1\plugins\NodeJS\lib\NodeJS.jar!\com\jetbrains\nodejs\run\profile\heap\view\renderers\RightAlignedRenderer.class
 * Java compiler version: 11 (55.0)
 * JD-Core Version:       1.1.3
 */