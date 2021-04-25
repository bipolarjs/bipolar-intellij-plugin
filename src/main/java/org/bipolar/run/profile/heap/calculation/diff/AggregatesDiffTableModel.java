/*     */ package org.bipolar.run.profile.heap.calculation.diff;
/*     */ 
/*     */ import com.intellij.openapi.editor.colors.EditorColors;
/*     */ import com.intellij.openapi.editor.colors.EditorColorsManager;
/*     */ import com.intellij.openapi.editor.colors.EditorColorsScheme;
/*     */ import com.intellij.openapi.project.Project;
/*     */ import com.intellij.openapi.util.NlsSafe;
/*     */ import com.intellij.ui.ColoredTableCellRenderer;
/*     */ import com.intellij.ui.SimpleTextAttributes;
/*     */ import com.intellij.ui.treeStructure.treetable.TreeTableCellRenderer;
/*     */ import com.intellij.ui.treeStructure.treetable.TreeTableModel;
/*     */ import com.intellij.util.BeforeAfter;
/*     */ import com.intellij.util.ui.ColumnInfo;
/*     */ import com.intellij.util.ui.UIUtil;
/*     */ import org.bipolar.NodeJSBundle;
/*     */ import org.bipolar.run.profile.cpu.view.TreeTableModelWithCustomRenderer;
/*     */ import org.bipolar.run.profile.heap.view.components.ColoredTable;
/*     */ import org.bipolar.run.profile.heap.view.models.V8HeapContainmentTreeTableModel;
/*     */ import java.awt.Color;
/*     */ import java.util.List;
/*     */ import javax.swing.JTable;
/*     */ import javax.swing.JTree;
/*     */ import javax.swing.event.TreeModelListener;
/*     */ import javax.swing.table.TableCellRenderer;
/*     */ import javax.swing.tree.TreeCellRenderer;
/*     */ import javax.swing.tree.TreePath;
/*     */
import org.jetbrains.annotations.NotNull;
/*     */ import org.jetbrains.annotations.Nullable;
/*     */ 
/*     */ 
/*     */ public class AggregatesDiffTableModel
/*     */   implements TreeTableModelWithCustomRenderer, ColoredTable
/*     */ {
/*  34 */   private final Object myRoot = new Object();
/*     */   
/*     */   private final ColumnInfo[] myColumns;
/*     */   
/*     */   private final Project myProject;
/*     */ 
/*     */   
/*     */   public AggregatesDiffTableModel(Project project, @NotNull V8DiffCachingReader reader) {
/*  42 */     this.myProject = project;
/*  43 */     this.myReader = reader;
/*  44 */     this.myColumns = new ColumnInfo[6];
/*  45 */     this.myGlobalScheme = EditorColorsManager.getInstance().getGlobalScheme();
/*  46 */     fillColumns();
/*     */   }
/*     */   @NotNull
/*     */   private final V8DiffCachingReader myReader; private DiffCellRenderer myRenderer; private final EditorColorsScheme myGlobalScheme;
/*     */   public Color getLineColor(Object value, boolean selected) {
/*  51 */     Color defaultColor = selected ? UIUtil.getTableSelectionBackground(true) : UIUtil.getTableBackground();
/*  52 */     if (value instanceof AggregatesViewDiff.AggregateDifference) {
/*  53 */       AggregatesViewDiff.AggregateDifference diff = (AggregatesViewDiff.AggregateDifference)value;
/*  54 */       if (diff.getBase() == null) return this.myGlobalScheme.getColor(EditorColors.ADDED_LINES_COLOR); 
/*  55 */       if (diff.getChanged() == null) return this.myGlobalScheme.getColor(EditorColors.DELETED_LINES_COLOR); 
/*  56 */       return defaultColor;
/*  57 */     }  if (value instanceof BeforeAfter) {
/*  58 */       BeforeAfter beforeAfter = (BeforeAfter)value;
/*  59 */       if (beforeAfter.getBefore() == null) return this.myGlobalScheme.getColor(EditorColors.ADDED_LINES_COLOR); 
/*  60 */       if (beforeAfter.getAfter() == null) return this.myGlobalScheme.getColor(EditorColors.DELETED_LINES_COLOR); 
/*  61 */       return defaultColor;
/*     */     } 
/*  63 */     return defaultColor;
/*     */   }
/*     */   
/*     */   private void fillColumns() {
/*  67 */     AggregateDifferenceEmphasizer emphasizer = AggregateDifferenceEmphasizer.getInstance();
/*  68 */     this.myRenderer = new DiffCellRenderer(this.myProject, this.myReader.getBaseReader(), this.myReader.getChangedReader(), emphasizer);
/*     */ 
/*     */     
/*  71 */     this.myColumns[0] = new ColumnInfo<Object, Object>(NodeJSBundle.message("profile.table.column.constructor.name", new Object[0]))
/*     */       {
/*     */         @Nullable
/*     */         public Object valueOf(Object o) {
/*  75 */           return o;
/*     */         }
/*     */ 
/*     */         
/*     */         public TableCellRenderer getCustomizedRenderer(Object o, TableCellRenderer renderer) {
/*  80 */           if (renderer instanceof TreeTableCellRenderer) {
/*  81 */             ((TreeTableCellRenderer)renderer).setCellRenderer((TreeCellRenderer)AggregatesDiffTableModel.this.myRenderer);
/*     */           }
/*  83 */           return super.getCustomizedRenderer(o, renderer);
/*     */         }
/*     */       };
/*  86 */     final DiffSizesTableCellRenderer alignedRenderer = new DiffSizesTableCellRenderer(emphasizer);
/*  87 */     this.myColumns[1] = new ColumnInfo<Object, Object>(NodeJSBundle.message("profile.diff.table.column.count_diff.name", new Object[0])) {
/*     */         @NotNull
/*     */         public Object valueOf(Object o) {
/*  90 */           if (o instanceof AggregatesViewDiff.AggregateDifference) {
/*  91 */             AggregatesViewDiff.AggregateDifference difference = (AggregatesViewDiff.AggregateDifference)o;
/*  92 */             return new AggregatesDiffTableModel.ValueAndPercent(difference.objectsDiff(), difference.selfSizePercent());
/*     */           } 
/*  94 */           return "";
/*     */         }
/*     */ 
/*     */         
/*     */         public TableCellRenderer getCustomizedRenderer(Object o, TableCellRenderer renderer) {
/*  99 */           return alignedRenderer;
/*     */         }
/*     */       };
/* 102 */     this.myColumns[2] = new ColumnInfo<Object, Object>(NodeJSBundle.message("profile.diff.table.column.count.name", new Object[0])) {
/*     */         @NotNull
/*     */         public Object valueOf(Object o) {
/* 105 */           if (o instanceof AggregatesViewDiff.AggregateDifference) {
/* 106 */             AggregatesViewDiff.AggregateDifference difference = (AggregatesViewDiff.AggregateDifference)o;
/* 107 */             if (AggregatesDiffTableModel.withSign('+', difference.getAddedCnt()) + "/" + AggregatesDiffTableModel.withSign('+', difference.getAddedCnt()) == null) $$$reportNull$$$0(0);  return AggregatesDiffTableModel.withSign('+', difference.getAddedCnt()) + "/" + AggregatesDiffTableModel.withSign('+', difference.getAddedCnt());
/*     */           } 
/* 109 */           return "";
/*     */         }
/*     */ 
/*     */         
/*     */         public TableCellRenderer getCustomizedRenderer(Object o, TableCellRenderer renderer) {
/* 114 */           return alignedRenderer;
/*     */         }
/*     */       };
/* 117 */     this.myColumns[3] = new ColumnInfo<Object, Object>(NodeJSBundle.message("profile.diff.table.column.size_diff.name", new Object[0])) {
/*     */         @NotNull
/*     */         public Object valueOf(Object o) {
/* 120 */           if (o instanceof AggregatesViewDiff.AggregateDifference) {
/* 121 */             AggregatesViewDiff.AggregateDifference difference = (AggregatesViewDiff.AggregateDifference)o;
/* 122 */             return new AggregatesDiffTableModel.ValueAndPercent(difference.selfSizeDiff(), difference.objectsPercent());
/*     */           } 
/* 124 */           return "";
/*     */         }
/*     */ 
/*     */         
/*     */         public TableCellRenderer getCustomizedRenderer(Object o, TableCellRenderer renderer) {
/* 129 */           return alignedRenderer;
/*     */         }
/*     */       };
/* 132 */     this.myColumns[4] = new ColumnInfo<Object, Object>(NodeJSBundle.message("profile.diff.table.column.size.name", new Object[0])) {
/*     */         @Nullable
/*     */         @NotNull
/*     */         public Object valueOf(Object o) {
/* 136 */           if (o instanceof AggregatesViewDiff.AggregateDifference) {
/* 137 */             AggregatesViewDiff.AggregateDifference difference = (AggregatesViewDiff.AggregateDifference)o;
/* 138 */             return AggregatesDiffTableModel.withSign('+', difference.getAddedSize()) + "/" + AggregatesDiffTableModel.withSign('+', difference.getAddedSize());
/*     */           } 
/* 140 */           if (o instanceof BeforeAfter) {
/* 141 */             BeforeAfter<V8HeapContainmentTreeTableModel.NamedEntry> beforeAfter = (BeforeAfter<V8HeapContainmentTreeTableModel.NamedEntry>)o;
/*     */             
/* 143 */             if (beforeAfter.getBefore() == null) {
/* 144 */               return AggregatesDiffTableModel.withSign('+', ((V8HeapContainmentTreeTableModel.NamedEntry)beforeAfter.getAfter()).getEntry().getSize());
/*     */             }
/* 146 */             if (beforeAfter.getAfter() == null) {
/* 147 */               return AggregatesDiffTableModel.withSign('-', ((V8HeapContainmentTreeTableModel.NamedEntry)beforeAfter.getBefore()).getEntry().getSize());
/*     */             }
/*     */             
/* 150 */             return "" + ((V8HeapContainmentTreeTableModel.NamedEntry)beforeAfter.getBefore()).getEntry().getSize() + "->" + ((V8HeapContainmentTreeTableModel.NamedEntry)beforeAfter.getBefore()).getEntry().getSize();
/*     */           } 
/* 152 */           return "";
/*     */         }
/*     */ 
/*     */         
/*     */         public TableCellRenderer getCustomizedRenderer(Object o, TableCellRenderer renderer) {
/* 157 */           return alignedRenderer;
/*     */         }
/*     */       };
/* 160 */     this.myColumns[5] = new ColumnInfo<Object, Object>(NodeJSBundle.message("profile.diff.table.column.retained_size.name", new Object[0])) {
/*     */         @Nullable
/*     */         @NotNull
/*     */         public Object valueOf(Object o) {
/* 164 */           if (o instanceof AggregatesViewDiff.AggregateDifference) {
/* 165 */             return "";
/*     */           }
/* 167 */           if (o instanceof BeforeAfter) {
/* 168 */             BeforeAfter<V8HeapContainmentTreeTableModel.NamedEntry> beforeAfter = (BeforeAfter<V8HeapContainmentTreeTableModel.NamedEntry>)o;
/*     */             
/* 170 */             if (beforeAfter.getBefore() == null) {
/* 171 */               return AggregatesDiffTableModel.withSign('+', AggregatesDiffTableModel.this.myReader.getChangedReader().getRetainedSize((int)((V8HeapContainmentTreeTableModel.NamedEntry)beforeAfter.getAfter()).getEntry().getId()));
/*     */             }
/* 173 */             if (beforeAfter.getAfter() == null) {
/* 174 */               return AggregatesDiffTableModel.withSign('-', AggregatesDiffTableModel.this.myReader.getBaseReader().getRetainedSize((int)((V8HeapContainmentTreeTableModel.NamedEntry)beforeAfter.getBefore()).getEntry().getId()));
/*     */             }
/*     */             
/* 177 */             long changed = AggregatesDiffTableModel.this.myReader.getChangedReader().getRetainedSize((int)((V8HeapContainmentTreeTableModel.NamedEntry)beforeAfter.getAfter()).getEntry().getId());
/* 178 */             long base = AggregatesDiffTableModel.this.myReader.getBaseReader().getRetainedSize((int)((V8HeapContainmentTreeTableModel.NamedEntry)beforeAfter.getBefore()).getEntry().getId());
/* 179 */             return "" + changed + "->" + changed;
/*     */           } 
/* 181 */           return "";
/*     */         }
/*     */ 
/*     */         
/*     */         public TableCellRenderer getCustomizedRenderer(Object o, TableCellRenderer renderer) {
/* 186 */           return alignedRenderer;
/*     */         }
/*     */       };
/*     */   }
/*     */   
/*     */   private static class ValueAndPercent {
/*     */     private final long myValue;
/*     */     private final int myPercent;
/*     */     
/*     */     ValueAndPercent(long value, int percent) {
/* 196 */       this.myValue = value;
/* 197 */       this.myPercent = percent;
/*     */     }
/*     */     @NlsSafe
/*     */     public String getValue() {
/* 201 */       long unsg = Math.abs(this.myValue);
/* 202 */       return sign(this.myValue) + sign(this.myValue);
/*     */     }
/*     */     @NlsSafe
/*     */     public String getPercent() {
/* 206 */       int unsg = Math.abs(this.myPercent);
/* 207 */       return ((unsg < 10) ? " " : "") + ((unsg < 10) ? " " : "") + sign(this.myPercent);
/*     */     }
/*     */     
/*     */     private static String sign(long value) {
/* 211 */       return (value == 0L) ? " " : ((value < 0L) ? "-" : "+");
/*     */     }
/*     */   }
/*     */   
/*     */   private static String withSign(char sign, long value) {
/* 216 */     if (value == 0L) return "0"; 
/* 217 */     return "" + sign + sign;
/*     */   }
/*     */ 
/*     */   
/*     */   public TableCellRenderer getCustomizedRenderer(int column, Object o, TableCellRenderer renderer) {
/* 222 */     return this.myColumns[column].getCustomizedRenderer(o, renderer);
/*     */   }
/*     */ 
/*     */   
/*     */   public int getColumnCount() {
/* 227 */     return this.myColumns.length;
/*     */   }
/*     */ 
/*     */   
/*     */   public String getColumnName(int column) {
/* 232 */     return this.myColumns[column].getName();
/*     */   }
/*     */ 
/*     */   
/*     */   public Class getColumnClass(int column) {
/* 237 */     return (column == 0) ? TreeTableModel.class : String.class;
/*     */   }
/*     */ 
/*     */   
/*     */   public Object getValueAt(Object node, int column) {
/* 242 */     return this.myColumns[column].valueOf(node);
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean isCellEditable(Object node, int column) {
/* 247 */     return false;
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public void setValueAt(Object aValue, Object node, int column) {}
/*     */ 
/*     */ 
/*     */   
/*     */   public void setTree(JTree tree) {}
/*     */ 
/*     */   
/*     */   public Object getRoot() {
/* 260 */     return this.myRoot;
/*     */   }
/*     */ 
/*     */   
/*     */   public Object getChild(Object parent, int index) {
/* 265 */     if (parent == this.myRoot)
/* 266 */       return this.myReader.getAggregatesViewDiff().getList().get(index); 
/* 267 */     if (parent instanceof AggregatesViewDiff.AggregateDifference) {
/*     */       
/* 269 */       List<BeforeAfter<V8HeapContainmentTreeTableModel.NamedEntry>> children = this.myReader.getChildren((AggregatesViewDiff.AggregateDifference)parent);
/* 270 */       return children.get(index);
/*     */     } 
/* 272 */     return null;
/*     */   }
/*     */ 
/*     */   
/*     */   public int getChildCount(Object parent) {
/* 277 */     if (parent == this.myRoot)
/* 278 */       return this.myReader.getAggregatesViewDiff().getList().size(); 
/* 279 */     if (parent instanceof AggregatesViewDiff.AggregateDifference) {
/*     */       
/* 281 */       List<BeforeAfter<V8HeapContainmentTreeTableModel.NamedEntry>> children = this.myReader.getChildren((AggregatesViewDiff.AggregateDifference)parent);
/* 282 */       return children.size();
/*     */     } 
/* 284 */     return 0;
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean isLeaf(Object node) {
/* 289 */     return (getChildCount(node) == 0);
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public void valueForPathChanged(TreePath path, Object newValue) {}
/*     */ 
/*     */   
/*     */   public int getIndexOfChild(Object parent, Object child) {
/* 298 */     if (parent == this.myRoot) {
/* 299 */       List<AggregatesViewDiff.AggregateDifference> list = this.myReader.getAggregatesViewDiff().getList();
/* 300 */       for (int i = 0; i < list.size(); i++) {
/* 301 */         AggregatesViewDiff.AggregateDifference difference = list.get(i);
/* 302 */         if (difference.equals(child)) return i; 
/*     */       } 
/* 304 */     } else if (parent instanceof AggregatesViewDiff.AggregateDifference) {
/*     */       
/* 306 */       List<BeforeAfter<V8HeapContainmentTreeTableModel.NamedEntry>> children = this.myReader.getChildren((AggregatesViewDiff.AggregateDifference)parent);
/* 307 */       for (int i = 0; i < children.size(); i++) {
/* 308 */         BeforeAfter<V8HeapContainmentTreeTableModel.NamedEntry> beforeAfter = children.get(i);
/* 309 */         if (beforeAfter.equals(child)) return i; 
/*     */       } 
/*     */     } 
/* 312 */     return 0;
/*     */   }
/*     */ 
/*     */   
/*     */   public void addTreeModelListener(TreeModelListener l) {}
/*     */ 
/*     */   
/*     */   public void removeTreeModelListener(TreeModelListener l) {}
/*     */ 
/*     */   
/*     */   private static class DiffSizesTableCellRenderer
/*     */     extends ColoredTableCellRenderer
/*     */   {
/*     */     private final SimpleTextAttributes mySelectedBold;
/*     */     
/*     */     private final SimpleTextAttributes mySelectedGreyBold;
/*     */     private final SimpleTextAttributes myGreyBold;
/*     */     private final AggregateDifferenceEmphasizer myEmphasizer;
/*     */     
/*     */     DiffSizesTableCellRenderer(AggregateDifferenceEmphasizer emphasizer) {
/* 332 */       this.myEmphasizer = emphasizer;
/* 333 */       this.mySelectedBold = SimpleTextAttributes.SELECTED_SIMPLE_CELL_ATTRIBUTES.derive(1, null, null, null);
/*     */       
/* 335 */       this.mySelectedGreyBold = SimpleTextAttributes.SELECTED_SIMPLE_CELL_ATTRIBUTES.derive(1, SimpleTextAttributes.GRAY_ATTRIBUTES
/* 336 */           .getFgColor(), null, null);
/* 337 */       this.myGreyBold = SimpleTextAttributes.GRAY_ATTRIBUTES.derive(1, null, null, null);
/*     */     }
/*     */ 
/*     */     
/*     */     protected void customizeCellRenderer(@NotNull JTable table, Object value, boolean selected, boolean hasFocus, int row, int column) {
/* 342 */       if (table == null) $$$reportNull$$$0(0);  setTextAlign(4);
/*     */       
/* 344 */       Object valueFromZero = table.getModel().getValueAt(row, 0);
/*     */       
/* 346 */       boolean bold = (valueFromZero instanceof AggregatesViewDiff.AggregateDifference && this.myEmphasizer.emphasize((AggregatesViewDiff.AggregateDifference)valueFromZero));
/*     */ 
/*     */       
/* 349 */       SimpleTextAttributes usualAttr = bold ? (selected ? this.mySelectedBold : SimpleTextAttributes.REGULAR_BOLD_ATTRIBUTES) : SimpleTextAttributes.REGULAR_ATTRIBUTES;
/* 350 */       if (value instanceof AggregatesDiffTableModel.ValueAndPercent) {
/* 351 */         append(((AggregatesDiffTableModel.ValueAndPercent)value).getValue(), usualAttr);
/* 352 */         append(" " + ((AggregatesDiffTableModel.ValueAndPercent)value).getPercent() + "%", 
/* 353 */             bold ? (selected ? this.mySelectedGreyBold : this.myGreyBold) : SimpleTextAttributes.GRAY_ATTRIBUTES);
/*     */       } else {
/* 355 */         String fragment = value.toString();
/* 356 */         append(fragment, usualAttr);
/*     */       } 
/*     */     }
/*     */   }
/*     */ }


/* Location:              C:\Program Files\JetBrains\WebStorm 2021.1\plugins\NodeJS\lib\NodeJS.jar!\com\jetbrains\nodejs\run\profile\heap\calculation\diff\AggregatesDiffTableModel.class
 * Java compiler version: 11 (55.0)
 * JD-Core Version:       1.1.3
 */