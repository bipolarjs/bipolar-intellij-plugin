/*     */ package org.bipolar.run.profile.cpu.view;
/*     */ import com.intellij.ui.JBColor;
/*     */ import com.intellij.ui.scale.JBUIScale;
/*     */ import com.intellij.util.containers.Convertor;
/*     */ import org.bipolar.run.profile.V8Utils;
/*     */ import org.bipolar.run.profile.cpu.v8log.ui.TableWithFixedWidth;
/*     */ import java.awt.BorderLayout;
/*     */ import java.awt.Color;
/*     */ import java.awt.Component;
/*     */ import java.util.ArrayList;
/*     */ import java.util.List;
/*     */ import javax.swing.JLabel;
/*     */ import javax.swing.JPanel;
/*     */ import javax.swing.JTable;
/*     */ import javax.swing.table.AbstractTableModel;
/*     */ import javax.swing.table.TableCellRenderer;
/*     */
/*     */
import org.jetbrains.annotations.NotNull;
/*     */ 
/*     */ public class PieChartLegend<T> extends JPanel {
/*     */   @NotNull
/*     */   private final List<T> myData;
/*     */   @NotNull
/*     */   private final Convertor<? super T, Integer> myDataConvertor;
/*     */   @NotNull
/*     */   private final Convertor<? super T, ? extends JBColor> myColorConvertor;
/*     */   @NotNull
/*     */   private final TableCellRenderer myNameRenderer;
/*     */   @NotNull
/*     */   private final TableCellRenderer myValueRenderer;
/*     */   @NotNull
/*     */   private final TableCellRenderer myColorRenderer;
/*     */   private final List<JLabel> myIconCache;
/*     */   
/*     */   public PieChartLegend(@NotNull List<T> data, @NotNull Convertor<? super T, Integer> convertor, @NotNull Convertor<? super T, ? extends JBColor> colorConvertor, @NotNull TableCellRenderer nameRenderer, @NotNull TableCellRenderer valueRenderer) {
/*  36 */     super(new BorderLayout());
/*  37 */     this.myData = new ArrayList<>(data);
/*  38 */     Collections.reverse(this.myData);
/*  39 */     this.myDataConvertor = convertor;
/*  40 */     this.myColorConvertor = colorConvertor;
/*  41 */     this.myNameRenderer = nameRenderer;
/*  42 */     this.myValueRenderer = valueRenderer;
/*  43 */     this.myColorRenderer = createColorRenderer();
/*     */     
/*  45 */     this.myIconCache = new ArrayList<>();
/*  46 */     for (T t : this.myData) {
/*  47 */       this.myIconCache.add(new JLabel((Icon)JBUIScale.scaleIcon((JBScalableIcon)new ColorIcon(10, (Color)this.myColorConvertor.convert(t)))));
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
/*  58 */     TableWithFixedWidth table = (new TableWithFixedWidth(new MyTableModel()) { public TableCellRenderer getCellRenderer(int row, int column) { if (column == 0) return PieChartLegend.this.myColorRenderer;  if (column == 1) return PieChartLegend.this.myNameRenderer;  if (column == 2) return PieChartLegend.this.myValueRenderer;  return super.getCellRenderer(row, column); } }).fixWidth();
/*  59 */     table.setBackground(UIUtil.getPanelBackground());
/*  60 */     table.setRowSelectionAllowed(false);
/*  61 */     table.setShowColumns(false);
/*  62 */     table.setShowGrid(false);
/*  63 */     V8Utils.adjustTableColumnWidths(table);
/*  64 */     add((Component)table, "North");
/*     */   }
/*     */   
/*     */   private TableCellRenderer createColorRenderer() {
/*  68 */     return new TableCellRenderer()
/*     */       {
/*     */ 
/*     */ 
/*     */ 
/*     */         
/*     */         public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column)
/*     */         {
/*  76 */           return PieChartLegend.this.myIconCache.get(row);
/*     */         }
/*     */       };
/*     */   }
/*     */   
/*     */   private class MyTableModel
/*     */     extends AbstractTableModel {
/*     */     public int getRowCount() {
/*  84 */       return PieChartLegend.this.myData.size();
/*     */     }
/*     */ 
/*     */     
/*     */     public int getColumnCount() {
/*  89 */       return 3;
/*     */     }
/*     */ 
/*     */     
/*     */     public Object getValueAt(int rowIndex, int columnIndex) {
/*  94 */       if (columnIndex == 0)
/*  95 */         return PieChartLegend.this.myColorConvertor.convert(PieChartLegend.this.myData.get(rowIndex)); 
/*  96 */       if (columnIndex == 1)
/*  97 */         return PieChartLegend.this.myData.get(rowIndex); 
/*  98 */       if (columnIndex == 2) {
/*  99 */         return V8Utils.formatPercent(((Integer)PieChartLegend.this.myDataConvertor.convert(PieChartLegend.this.myData.get(rowIndex))).intValue());
/*     */       }
/* 101 */       return null;
/*     */     }
/*     */   }
/*     */ }


/* Location:              C:\Program Files\JetBrains\WebStorm 2021.1\plugins\NodeJS\lib\NodeJS.jar!\com\jetbrains\nodejs\run\profile\cpu\view\PieChartLegend.class
 * Java compiler version: 11 (55.0)
 * JD-Core Version:       1.1.3
 */