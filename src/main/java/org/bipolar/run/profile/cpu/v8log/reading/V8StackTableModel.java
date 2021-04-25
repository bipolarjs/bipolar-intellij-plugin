/*     */ package org.bipolar.run.profile.cpu.v8log.reading;
/*     */ 
/*     */ import com.intellij.ui.ColoredTableCellRenderer;
/*     */ import com.intellij.ui.JBColor;
/*     */ import com.intellij.ui.SimpleTextAttributes;
/*     */ import com.intellij.ui.scale.JBUIScale;
/*     */ import com.intellij.util.ui.ColorIcon;
/*     */ import com.intellij.util.ui.JBScalableIcon;
/*     */ import org.bipolar.NodeJSBundle;
/*     */ import org.bipolar.run.profile.cpu.calculation.V8ProfileLine;
/*     */ import org.bipolar.run.profile.cpu.v8log.data.V8CpuLogCall;
/*     */ import org.bipolar.run.profile.cpu.v8log.ui.FlameColors;
/*     */ import org.bipolar.run.profile.cpu.view.V8ProfileLineTreeCellRenderer;
/*     */ import java.awt.Color;
/*     */ import java.io.IOException;
/*     */ import java.util.ArrayList;
/*     */ import java.util.List;
/*     */ import javax.swing.Icon;
/*     */ import javax.swing.JTable;
/*     */ import javax.swing.event.TableModelListener;
/*     */ import javax.swing.table.AbstractTableModel;
/*     */ import javax.swing.table.TableCellRenderer;
/*     */
import org.jetbrains.annotations.NotNull;
/*     */ import org.jetbrains.annotations.Nullable;
/*     */ 
/*     */ public class V8StackTableModel
/*     */   extends AbstractTableModel
/*     */ {
/*  29 */   private final String[] HEADERS = new String[] { "Function", "File", "Duration" };
/*     */   
/*     */   private final V8LogCachingReader myReader;
/*     */   
/*     */   private final List<? extends V8CpuLogCall> myLines;
/*     */   private final List<JBColor> myColors;
/*     */   private final List<Long> myDurations;
/*     */   private final ColoredTableCellRenderer myRenderer;
/*     */   private ColoredTableCellRenderer myLastColumnRenderer;
/*     */   
/*     */   public V8StackTableModel(V8LogCachingReader reader, @NotNull List<? extends V8CpuLogCall> lines, @NotNull List<Long> durations) throws IOException {
/*  40 */     this.myReader = reader;
/*  41 */     this.myLines = lines;
/*  42 */     this.myColors = new ArrayList<>();
/*  43 */     this.myDurations = durations;
/*  44 */     fillData();
/*  45 */     this.myLastColumnRenderer = new ColoredTableCellRenderer()
/*     */       {
/*     */         protected void customizeCellRenderer(@NotNull JTable table, @Nullable Object value, boolean selected, boolean hasFocus, int row, int column) {
/*  48 */           if (table == null) $$$reportNull$$$0(0);  V8CpuLogCall call = V8StackTableModel.this.getCall(row);
/*  49 */           if (call != null) {
/*     */             
/*  51 */             V8ProfileLineTreeCellRenderer.Attributes attributes = V8ProfileLineTreeCellRenderer.getAttributes(call.isLocal(), (call.isNative() || call.isNotNavigatable()));
/*  52 */             if (value instanceof Long) {
/*  53 */               long lValue = ((Long)value).longValue();
/*  54 */               if (lValue < 0L)
/*  55 */                 return;  append(NodeJSBundle.message("profile.cpu.milliseconds.text", new Object[] { Long.valueOf(Math.round(lValue / 1000.0D)) }), attributes.getAttributes(false));
/*     */               return;
/*     */             } 
/*     */           } 
/*  59 */           if (value != null) {
/*  60 */             String fragment = value.toString();
/*  61 */             append(fragment);
/*     */           } 
/*     */         }
/*     */       };
/*  65 */     this.myRenderer = new ColoredTableCellRenderer()
/*     */       {
/*     */         protected void customizeCellRenderer(@NotNull JTable table, @Nullable Object value, boolean selected, boolean hasFocus, int row, int column) {
/*  68 */           if (table == null) $$$reportNull$$$0(0);  String fragment = value.toString();
/*  69 */           if (value instanceof V8ProfileLine.ExecKind) {
/*  70 */             append(fragment, SimpleTextAttributes.GRAY_ATTRIBUTES);
/*     */             return;
/*     */           } 
/*  73 */           V8CpuLogCall call = V8StackTableModel.this.getCall(row);
/*  74 */           if (call != null) {
/*     */             
/*  76 */             V8ProfileLineTreeCellRenderer.Attributes attributes = V8ProfileLineTreeCellRenderer.getAttributes(call.isLocal(), (call.isNative() || call.isNotNavigatable()));
/*  77 */             if (value instanceof String) {
/*  78 */               if (column == 0) {
/*  79 */                 setIcon((Icon)JBUIScale.scaleIcon((JBScalableIcon)new ColorIcon(16, (Color)V8StackTableModel.this.myColors.get(row), true)));
/*  80 */                 if (0L == call.getStringId()) {
/*  81 */                   append(call.getPresentation(), SimpleTextAttributes.GRAY_ATTRIBUTES);
/*     */                 }
/*  83 */                 else if (!V8ProfileLine.ExecKind.Function.equals(call.getExecKind()) && 
/*  84 */                   !V8ProfileLine.ExecKind.LazyCompile.equals(call.getExecKind())) {
/*  85 */                   append("(" + call.getExecKind() + ")", SimpleTextAttributes.GRAY_ATTRIBUTES);
/*     */                 } else {
/*     */                   
/*  88 */                   append(fragment, attributes.getAttributes(false));
/*  89 */                   if (V8ProfileLine.ExecKind.LazyCompile.equals(call.getExecKind())) {
/*  90 */                     append(" (" + NodeJSBundle.message("profile.cpu.record.kind.lazy.text", new Object[0]) + ")", SimpleTextAttributes.GRAY_ATTRIBUTES);
/*     */                   }
/*     */                 } 
/*     */               } else {
/*     */                 
/*  95 */                 if (0L == call.getStringId())
/*  96 */                   return;  append(fragment, attributes.getAttributes(false));
/*     */               } 
/*     */             }
/*     */           } else {
/* 100 */             append(fragment);
/*     */           } 
/*     */         }
/*     */       };
/*     */   }
/*     */   
/*     */   public V8StackTableModel changeLastColumn(@NotNull ColoredTableCellRenderer renderer, @NotNull String header) {
/* 107 */     if (renderer == null) $$$reportNull$$$0(2);  if (header == null) $$$reportNull$$$0(3);  this.myLastColumnRenderer = renderer;
/* 108 */     this.HEADERS[this.HEADERS.length - 1] = header;
/* 109 */     return this;
/*     */   }
/*     */   
/*     */   private void fillData() throws IOException {
/* 113 */     for (V8CpuLogCall line : this.myLines) {
/* 114 */       this.myColors.add(FlameColors.getColor(line.getStringId(), this.myReader.getCodeScopeByStringId(line.getStringId())));
/*     */     }
/*     */   }
/*     */ 
/*     */   
/*     */   public int getRowCount() {
/* 120 */     return this.myLines.size();
/*     */   }
/*     */ 
/*     */   
/*     */   public int getColumnCount() {
/* 125 */     return this.HEADERS.length;
/*     */   }
/*     */ 
/*     */   
/*     */   public String getColumnName(int columnIndex) {
/* 130 */     return this.HEADERS[columnIndex];
/*     */   }
/*     */ 
/*     */   
/*     */   public Class<?> getColumnClass(int columnIndex) {
/* 135 */     if (columnIndex == 3) return Long.class; 
/* 136 */     return String.class;
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean isCellEditable(int rowIndex, int columnIndex) {
/* 141 */     return false;
/*     */   }
/*     */   
/*     */   public V8CpuLogCall getCall(int row) {
/* 145 */     return this.myLines.get(row);
/*     */   }
/*     */ 
/*     */   
/*     */   public Object getValueAt(int rowIndex, int columnIndex) {
/* 150 */     V8CpuLogCall call = this.myLines.get(rowIndex);
/* 151 */     if (columnIndex == 0)
/* 152 */       return call.getCodeState().getPrefix() + call.getCodeState().getPrefix(); 
/* 153 */     if (columnIndex == 2) {
/* 154 */       return Long.valueOf((this.myDurations.size() <= rowIndex) ? -1L : ((Long)this.myDurations.get(rowIndex)).longValue());
/*     */     }
/* 156 */     return (call.getDescriptor() != null) ? call.getDescriptor().getShortLink() : call.getNotParsedCallable();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void setValueAt(Object aValue, int rowIndex, int columnIndex) {}
/*     */ 
/*     */ 
/*     */   
/*     */   public void addTableModelListener(TableModelListener l) {}
/*     */ 
/*     */ 
/*     */   
/*     */   public void removeTableModelListener(TableModelListener l) {}
/*     */ 
/*     */ 
/*     */   
/*     */   public TableCellRenderer getCellRenderer(int row, int column) {
/* 175 */     if (column == this.HEADERS.length - 1) return (TableCellRenderer)this.myLastColumnRenderer; 
/* 176 */     return (TableCellRenderer)this.myRenderer;
/*     */   }
/*     */ }


/* Location:              C:\Program Files\JetBrains\WebStorm 2021.1\plugins\NodeJS\lib\NodeJS.jar!\com\jetbrains\nodejs\run\profile\cpu\v8log\reading\V8StackTableModel.class
 * Java compiler version: 11 (55.0)
 * JD-Core Version:       1.1.3
 */