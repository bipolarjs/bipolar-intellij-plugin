/*    */ package org.bipolar.run.profile.heap.view.renderers;
/*    */ 
/*    */ import com.intellij.ui.ColoredTableCellRenderer;
/*    */ import com.intellij.ui.SimpleTextAttributes;
/*    */ import com.intellij.util.ui.StartupUiUtil;
/*    */ import com.intellij.util.ui.UIUtil;
/*    */ import javax.swing.JTable;
/*    */ import org.jetbrains.annotations.Nls;
/*    */ import org.jetbrains.annotations.NotNull;
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
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public class SizeRenderer
/*    */   extends ColoredTableCellRenderer
/*    */ {
/* 32 */   private final SimpleTextAttributes myBlackTextAttributes = SimpleTextAttributes.REGULAR_ATTRIBUTES
/* 33 */     .derive(-1, UIUtil.getTableForeground(), null, null);
/* 34 */   private final SimpleTextAttributes mySelectionAttributes = SimpleTextAttributes.REGULAR_ATTRIBUTES
/* 35 */     .derive(-1, UIUtil.getTableSelectionForeground(), null, null);
/*    */   private final long myTotalSize;
/*    */   
/*    */   public SizeRenderer(long size) {
/* 39 */     this.myTotalSize = size;
/*    */   }
/*    */ 
/*    */   
/*    */   protected void customizeCellRenderer(@NotNull JTable table, Object value, boolean selected, boolean hasFocus, int row, int column) {
/* 44 */     if (table == null) $$$reportNull$$$0(0);  setPaintFocusBorder(false);
/* 45 */     setTextAlign(4);
/* 46 */     boolean lineHasFocus = table.hasFocus();
/* 47 */     boolean useSelection = (selected && (lineHasFocus || StartupUiUtil.isUnderDarcula()));
/* 48 */     SimpleTextAttributes attr = useSelection ? this.mySelectionAttributes : this.myBlackTextAttributes;
/* 49 */     if (value instanceof Long) {
/* 50 */       append(formatSize(Long.toString(((Long)value).longValue())), attr);
/* 51 */       append(" ");
/* 52 */       int tens = (int)Math.round(((Long)value).doubleValue() / this.myTotalSize * 1000.0D);
/* 53 */       if (tens < 100) {
/* 54 */         append(" ");
/*    */       }
/* 56 */       String percent = "" + tens / 10 + "." + tens / 10 + "%";
/* 57 */       append(percent, useSelection ? this.mySelectionAttributes : SimpleTextAttributes.GRAYED_ATTRIBUTES);
/*    */     } else {
/* 59 */       String fragment = value.toString();
/* 60 */       append(fragment, attr);
/*    */     } 
/* 62 */     if (!lineHasFocus && selected) {
/* 63 */       setBackground(UIUtil.getTreeUnfocusedSelectionBackground());
/*    */     }
/*    */   }
/*    */   
/*    */   @NotNull
/*    */   public static String formatSize(long number) {
/* 69 */     return formatSize(String.valueOf(number));
/*    */   }
/*    */   @NotNull
/*    */   @Nls
/*    */   public static String formatSize(@Nls String number) {
/* 74 */     if (number.length() <= 3) { if (number == null) $$$reportNull$$$0(1);  return number; }
/* 75 */      int spaceIdx = number.length() % 3;
/* 76 */     spaceIdx = (spaceIdx > 0) ? (spaceIdx - 1) : 2;
/* 77 */     StringBuilder sb = new StringBuilder();
/* 78 */     for (int i = 0; i < number.length(); i++) {
/* 79 */       sb.append(number.charAt(i));
/* 80 */       if (i == spaceIdx && i != number.length() - 1) {
/* 81 */         sb.append(' ');
/* 82 */         spaceIdx += 3;
/*    */       } 
/*    */     } 
/* 85 */     String s = sb.toString();
/* 86 */     if (s == null) $$$reportNull$$$0(2);  return s;
/*    */   }
/*    */ 
/*    */   
/*    */   protected SimpleTextAttributes modifyAttributes(SimpleTextAttributes attributes) {
/* 91 */     return attributes;
/*    */   }
/*    */ }


/* Location:              C:\Program Files\JetBrains\WebStorm 2021.1\plugins\NodeJS\lib\NodeJS.jar!\com\jetbrains\nodejs\run\profile\heap\view\renderers\SizeRenderer.class
 * Java compiler version: 11 (55.0)
 * JD-Core Version:       1.1.3
 */