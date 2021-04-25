/*     */ package org.bipolar.run.profile.cpu.v8log.diff;
/*     */ 
/*     */ import com.intellij.openapi.util.NlsContexts.ColumnName;
/*     */ import com.intellij.openapi.util.Pair;
/*     */ import com.intellij.ui.ColoredTableCellRenderer;
/*     */ import com.intellij.ui.SimpleTextAttributes;
/*     */ import com.intellij.ui.treeStructure.treetable.TreeTableCellRenderer;
/*     */ import com.intellij.ui.treeStructure.treetable.TreeTableModel;
/*     */ import com.intellij.util.ui.ColumnInfo;
/*     */ import org.bipolar.NodeJSBundle;
/*     */ import org.bipolar.run.profile.V8Utils;
/*     */ import org.bipolar.run.profile.cpu.calculation.CallTreeType;
/*     */ import org.bipolar.run.profile.cpu.view.FilteredByPercent;
/*     */ import org.bipolar.run.profile.cpu.view.TreeTableModelWithCustomRenderer;
/*     */ import java.util.ArrayList;
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
/*     */ 
/*     */ public class V8CpuDiffTableModel
/*     */   implements FilteredByPercent, TreeTableModelWithCustomRenderer
/*     */ {
/*     */   private final DiffNode myRoot;
/*     */   private final CallTreeType myCallType;
/*     */   private final int myBaseNumTicks;
/*     */   private final int myChangedNumTicks;
/*     */   private final DiffNodeTreeCellRenderer myRenderer;
/*     */   private final List<ColumnInfo> myColumns;
/*     */   private Integer myFilterLevel;
/*     */   
/*     */   public V8CpuDiffTableModel(DiffNode root, CallTreeType type, int baseNumTicks, int changedNumTicks) {
/*  40 */     this.myRoot = root;
/*  41 */     this.myCallType = type;
/*  42 */     this.myBaseNumTicks = baseNumTicks;
/*  43 */     this.myChangedNumTicks = changedNumTicks;
/*  44 */     this.myRenderer = new DiffNodeTreeCellRenderer();
/*  45 */     if (CallTreeType.topDown.equals(type)) {
/*  46 */       this.myRenderer.setBaseTicks(Integer.valueOf(baseNumTicks));
/*  47 */       this.myRenderer.setChangedTicks(Integer.valueOf(changedNumTicks));
/*     */     } 
/*  49 */     this
/*  50 */       .myColumns = CallTreeType.topDown.equals(type) ? createTopDownColumns((TreeCellRenderer)this.myRenderer, baseNumTicks, changedNumTicks) : createBottomUpColumns((TreeCellRenderer)this.myRenderer);
/*     */   }
/*     */ 
/*     */   
/*     */   public static List<ColumnInfo> createTopDownColumns(final TreeCellRenderer diffCellRenderer, int baseNumTicks, int changedNumTicks) {
/*  55 */     List<ColumnInfo> list = new ArrayList<>();
/*  56 */     list.add(new ColumnInfo<Object, Object>(NodeJSBundle.message("profile.cpu.diff_table.column.calls.name", new Object[0]))
/*     */         {
/*     */           @Nullable
/*     */           public Object valueOf(Object node) {
/*  60 */             return node;
/*     */           }
/*     */ 
/*     */           
/*     */           public TableCellRenderer getCustomizedRenderer(Object o, TableCellRenderer renderer) {
/*  65 */             if (renderer instanceof TreeTableCellRenderer) {
/*  66 */               ((TreeTableCellRenderer)renderer).setCellRenderer(diffCellRenderer);
/*     */             }
/*  68 */             return renderer;
/*     */           }
/*     */         });
/*  71 */     DiffRenderer alignedRenderer = new DiffRenderer();
/*     */     
/*  73 */     ColumnInfo<Object, Object> totalPercentDiff = new TopDownDiffPercentColumn(NodeJSBundle.message("profile.cpu.diff_table.total_perc_diff.column.name", new Object[0]), (TableCellRenderer)alignedRenderer, baseNumTicks, changedNumTicks)
/*     */       {
/*     */         protected int getTicks(DiffNode.Ticks ticks) {
/*  76 */           return ticks.getTotal();
/*     */         }
/*     */       };
/*     */     
/*  80 */     ColumnInfo<Object, Object> selfPercentDiff = new TopDownDiffPercentColumn(NodeJSBundle.message("profile.cpu.diff_table.self_perc_diff.column.name", new Object[0]), (TableCellRenderer)alignedRenderer, baseNumTicks, changedNumTicks)
/*     */       {
/*     */         protected int getTicks(DiffNode.Ticks ticks) {
/*  83 */           return ticks.getSelf();
/*     */         }
/*     */       };
/*  86 */     ColumnInfo<Object, Object> total = new TicksColumn(NodeJSBundle.message("profile.cpu.diff_table.total.column.name", new Object[0]), (TableCellRenderer)alignedRenderer)
/*     */       {
/*     */         protected int getTicks(DiffNode.Ticks ticks) {
/*  89 */           return ticks.getTotal();
/*     */         }
/*     */       };
/*  92 */     ColumnInfo<Object, Object> self = new TicksColumn(NodeJSBundle.message("profile.cpu.diff_table.self.column.name", new Object[0]), (TableCellRenderer)alignedRenderer)
/*     */       {
/*     */         protected int getTicks(DiffNode.Ticks ticks) {
/*  95 */           return ticks.getSelf();
/*     */         }
/*     */       };
/*     */     
/*  99 */     list.add(totalPercentDiff);
/* 100 */     list.add(selfPercentDiff);
/* 101 */     list.add(total);
/* 102 */     list.add(self);
/* 103 */     return list;
/*     */   }
/*     */   private static List<ColumnInfo> createBottomUpColumns(final TreeCellRenderer diffCellRenderer) {
/* 106 */     List<ColumnInfo> list = new ArrayList<>();
/* 107 */     list.add(new ColumnInfo<DiffNode, DiffNode>(NodeJSBundle.message("profile.cpu.diff_table.column.calls.name", new Object[0]))
/*     */         {
/*     */           @Nullable
/*     */           public DiffNode valueOf(DiffNode node) {
/* 111 */             return node;
/*     */           }
/*     */ 
/*     */           
/*     */           public TableCellRenderer getCustomizedRenderer(DiffNode o, TableCellRenderer renderer) {
/* 116 */             if (renderer instanceof TreeTableCellRenderer) {
/* 117 */               ((TreeTableCellRenderer)renderer).setCellRenderer(diffCellRenderer);
/*     */             }
/* 119 */             return renderer;
/*     */           }
/*     */         });
/* 122 */     DiffRenderer alignedRenderer = new DiffRenderer();
/*     */     
/* 124 */     ColumnInfo<Object, Object> percentDiff = new BottomUpPercentDiff(NodeJSBundle.message("profile.cpu.diff_table.parent_perc_diff.column.name", new Object[0]), (TableCellRenderer)alignedRenderer);
/* 125 */     ColumnInfo<Object, Object> total = new TicksColumn(NodeJSBundle.message("profile.cpu.diff_table.ticks.column.name", new Object[0]), (TableCellRenderer)alignedRenderer)
/*     */       {
/*     */         protected int getTicks(DiffNode.Ticks ticks) {
/* 128 */           return ticks.getTotal();
/*     */         }
/*     */       };
/*     */     
/* 132 */     list.add(percentDiff);
/* 133 */     list.add(total);
/* 134 */     return list;
/*     */   }
/*     */   
/*     */   public TreeCellRenderer getRenderer() {
/* 138 */     return (TreeCellRenderer)this.myRenderer;
/*     */   }
/*     */   
/*     */   private static String sign(int base, int changed) {
/* 142 */     return (base == changed) ? "" : ((changed > base) ? "+" : "");
/*     */   }
/*     */ 
/*     */   
/*     */   public TableCellRenderer getCustomizedRenderer(int column, Object o, TableCellRenderer renderer) {
/* 147 */     return ((ColumnInfo)this.myColumns.get(column)).getCustomizedRenderer(o, renderer);
/*     */   }
/*     */ 
/*     */   
/*     */   public int getColumnCount() {
/* 152 */     return this.myColumns.size();
/*     */   }
/*     */ 
/*     */   
/*     */   public String getColumnName(int column) {
/* 157 */     return ((ColumnInfo)this.myColumns.get(column)).getName();
/*     */   }
/*     */ 
/*     */   
/*     */   public Class getColumnClass(int column) {
/* 162 */     return (column == 0) ? TreeTableModel.class : String.class;
/*     */   }
/*     */ 
/*     */   
/*     */   public Object getValueAt(Object node, int column) {
/* 167 */     return ((ColumnInfo)this.myColumns.get(column)).valueOf(node);
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean isCellEditable(Object node, int column) {
/* 172 */     return false;
/*     */   }
/*     */ 
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
/*     */   
/*     */   public Object getRoot() {
/* 187 */     return this.myRoot;
/*     */   }
/*     */ 
/*     */   
/*     */   public Object getChild(Object parent, int index) {
/* 192 */     if (parent instanceof DiffNode) {
/* 193 */       return getFilteredChildren((DiffNode)parent).get(index);
/*     */     }
/* 195 */     return null;
/*     */   }
/*     */ 
/*     */   
/*     */   public int getChildCount(Object parent) {
/* 200 */     if (parent instanceof DiffNode) {
/* 201 */       return getFilteredChildren((DiffNode)parent).size();
/*     */     }
/* 203 */     return 0;
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean isLeaf(Object node) {
/* 208 */     return (getChildCount(node) == 0);
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public void valueForPathChanged(TreePath path, Object newValue) {}
/*     */ 
/*     */ 
/*     */   
/*     */   public int getIndexOfChild(Object parent, Object child) {
/* 218 */     if (parent instanceof DiffNode) {
/* 219 */       List<DiffNode> children = getFilteredChildren((DiffNode)parent);
/* 220 */       for (int i = 0; i < children.size(); i++) {
/* 221 */         DiffNode node = children.get(i);
/* 222 */         if (node == child) return i; 
/*     */       } 
/* 224 */       return 0;
/*     */     } 
/* 226 */     return 0;
/*     */   }
/*     */   
/*     */   private List<DiffNode> getFilteredChildren(DiffNode parent) {
/* 230 */     if (this.myFilterLevel == null || this.myFilterLevel.intValue() == 0) return parent.getChildren(); 
/* 231 */     List<DiffNode> result = new ArrayList<>();
/* 232 */     for (DiffNode node : parent.getChildren()) {
/* 233 */       int i; boolean include = false;
/* 234 */       if (node.getAfter() != null) {
/* 235 */         include = (tensPercent(node.getAfter(), false) >= this.myFilterLevel.intValue());
/* 236 */       } else if (node.getBefore() != null) {
/* 237 */         i = include | ((tensPercent(node.getBefore(), true) >= this.myFilterLevel.intValue()) ? 1 : 0);
/*     */       } 
/* 239 */       if (i != 0) result.add(node); 
/*     */     } 
/* 241 */     return result;
/*     */   }
/*     */   
/*     */   int tensPercent(DiffNode.Ticks ticks, boolean isBefore) {
/* 245 */     if (CallTreeType.bottomUp.equals(this.myCallType)) {
/* 246 */       return V8Utils.tensPercent(ticks.getTotal(), ticks.getNumParentTicks());
/*     */     }
/* 248 */     return V8Utils.tensPercent(ticks.getTotal(), isBefore ? this.myBaseNumTicks : this.myChangedNumTicks);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void addTreeModelListener(TreeModelListener l) {}
/*     */ 
/*     */ 
/*     */   
/*     */   public void removeTreeModelListener(TreeModelListener l) {}
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean isFiltered() {
/* 263 */     return (this.myFilterLevel != null);
/*     */   }
/*     */ 
/*     */   
/*     */   public int getTensPercentLevelInclusive() {
/* 268 */     return (this.myFilterLevel == null) ? 0 : this.myFilterLevel.intValue();
/*     */   }
/*     */ 
/*     */   
/*     */   public void clearFilter() {
/* 273 */     this.myFilterLevel = null;
/*     */   }
/*     */ 
/*     */   
/*     */   public void filterByLevel(int tensPercentLevelInclusive) {
/* 278 */     this.myFilterLevel = Integer.valueOf(tensPercentLevelInclusive);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   static class DiffRenderer
/*     */     extends ColoredTableCellRenderer
/*     */   {
/* 287 */     private final SimpleTextAttributes mySelectedBold = SimpleTextAttributes.SELECTED_SIMPLE_CELL_ATTRIBUTES.derive(1, null, null, null);
/*     */     
/* 289 */     private final SimpleTextAttributes mySelectedGreyBold = SimpleTextAttributes.SELECTED_SIMPLE_CELL_ATTRIBUTES.derive(1, SimpleTextAttributes.GRAY_ATTRIBUTES
/* 290 */         .getFgColor(), null, null);
/* 291 */     private final SimpleTextAttributes myGreyBold = SimpleTextAttributes.GRAY_ATTRIBUTES.derive(1, null, null, null);
/*     */ 
/*     */ 
/*     */     
/*     */     protected void customizeCellRenderer(@NotNull JTable table, @Nullable Object value, boolean selected, boolean hasFocus, int row, int column) {
/* 296 */       if (table == null) $$$reportNull$$$0(0);  setTextAlign(4);
/*     */       
/* 298 */       if (value instanceof Pair) {
/* 299 */         String fragment = ((Pair)value).getFirst().toString();
/* 300 */         append(fragment, SimpleTextAttributes.REGULAR_ATTRIBUTES);
/* 301 */         if (((Pair)value).getSecond() != null) {
/* 302 */           append("  " + ((Pair)value).getSecond(), SimpleTextAttributes.GRAY_ATTRIBUTES);
/*     */         }
/* 304 */       } else if (value != null) {
/* 305 */         String fragment = value.toString();
/* 306 */         append(fragment, SimpleTextAttributes.REGULAR_ATTRIBUTES);
/*     */       } 
/*     */     }
/*     */   }
/*     */   
/*     */   private static abstract class TopDownDiffPercentColumn extends ColumnInfo<Object, Object> {
/*     */     private final TableCellRenderer myAlignedRenderer;
/*     */     private final int myChangedNumTicks;
/*     */     private final int myBaseNumTicks;
/*     */     
/*     */     TopDownDiffPercentColumn(@ColumnName String name, TableCellRenderer renderer, int baseNumTicks, int changedNumTicks) {
/* 317 */       super(name);
/* 318 */       this.myAlignedRenderer = renderer;
/* 319 */       this.myChangedNumTicks = changedNumTicks;
/* 320 */       this.myBaseNumTicks = baseNumTicks;
/*     */     }
/*     */ 
/*     */     
/*     */     protected abstract int getTicks(DiffNode.Ticks param1Ticks);
/*     */     
/*     */     @Nullable
/*     */     public Object valueOf(Object object) {
/* 328 */       if (object instanceof DiffNode) {
/* 329 */         DiffNode node = (DiffNode)object;
/* 330 */         if (node.isAdded()) {
/* 331 */           String percent = "+" + V8Utils.formatPercent(V8Utils.tensPercent(getTicks(node.getAfter()), this.myChangedNumTicks));
/* 332 */           return Pair.create(percent, null);
/*     */         } 
/* 334 */         if (node.isDeleted()) {
/* 335 */           String percent = "-" + V8Utils.formatPercent(V8Utils.tensPercent(getTicks(node.getBefore()), this.myBaseNumTicks));
/* 336 */           return Pair.create(percent, null);
/*     */         } 
/*     */         
/* 339 */         int base = V8Utils.tensPercent(getTicks(node.getBefore()), this.myBaseNumTicks);
/* 340 */         int changed = V8Utils.tensPercent(getTicks(node.getAfter()), this.myChangedNumTicks);
/* 341 */         if (base == 0 && changed == 0) {
/* 342 */           return Pair.create("0%", null);
/*     */         }
/* 344 */         return Pair.create(V8CpuDiffTableModel.sign(base, changed) + V8CpuDiffTableModel.sign(base, changed), 
/* 345 */             V8Utils.formatPercent(base) + "/" + V8Utils.formatPercent(base));
/*     */       } 
/*     */       
/* 348 */       return "";
/*     */     }
/*     */ 
/*     */     
/*     */     public TableCellRenderer getCustomizedRenderer(Object o, TableCellRenderer renderer) {
/* 353 */       return this.myAlignedRenderer;
/*     */     }
/*     */   }
/*     */   
/*     */   private static class BottomUpPercentDiff extends ColumnInfo<Object, Object> {
/*     */     private final TableCellRenderer myAlignedRenderer;
/*     */     
/*     */     BottomUpPercentDiff(@ColumnName String name, TableCellRenderer renderer) {
/* 361 */       super(name);
/* 362 */       this.myAlignedRenderer = renderer;
/*     */     }
/*     */ 
/*     */     
/*     */     @Nullable
/*     */     public Object valueOf(Object object) {
/* 368 */       if (object instanceof DiffNode) {
/* 369 */         DiffNode node = (DiffNode)object;
/* 370 */         if (node.isAdded()) {
/* 371 */           String percent = "+" + V8Utils.formatPercent(V8Utils.tensPercent(node.getAfter().getTotal(), node.getAfter().getNumParentTicks()));
/* 372 */           return Pair.create(percent, null);
/*     */         } 
/* 374 */         if (node.isDeleted()) {
/* 375 */           String percent = "-" + V8Utils.formatPercent(V8Utils.tensPercent(node.getBefore().getTotal(), node.getBefore().getNumParentTicks()));
/* 376 */           return Pair.create(percent, null);
/*     */         } 
/*     */         
/* 379 */         int base = V8Utils.tensPercent(node.getBefore().getTotal(), node.getBefore().getNumParentTicks());
/* 380 */         int changed = V8Utils.tensPercent(node.getAfter().getTotal(), node.getAfter().getNumParentTicks());
/* 381 */         if (base == 0 && changed == 0) {
/* 382 */           return Pair.create("0%", null);
/*     */         }
/* 384 */         return Pair.create(V8CpuDiffTableModel.sign(base, changed) + V8CpuDiffTableModel.sign(base, changed), 
/* 385 */             V8Utils.formatPercent(base) + "/" + V8Utils.formatPercent(base));
/*     */       } 
/*     */       
/* 388 */       return "";
/*     */     }
/*     */ 
/*     */     
/*     */     public TableCellRenderer getCustomizedRenderer(Object o, TableCellRenderer renderer) {
/* 393 */       return this.myAlignedRenderer;
/*     */     }
/*     */   }
/*     */   
/*     */   private static abstract class TicksColumn extends ColumnInfo<Object, Object> {
/*     */     private final TableCellRenderer myAlignedRenderer;
/*     */     
/*     */     TicksColumn(@ColumnName String name, TableCellRenderer renderer) {
/* 401 */       super(name);
/* 402 */       this.myAlignedRenderer = renderer;
/*     */     }
/*     */ 
/*     */     
/*     */     protected abstract int getTicks(DiffNode.Ticks param1Ticks);
/*     */     
/*     */     @Nullable
/*     */     public Object valueOf(Object o) {
/* 410 */       if (o instanceof DiffNode) {
/* 411 */         DiffNode node = (DiffNode)o;
/* 412 */         if (node.isAdded()) {
/* 413 */           return Integer.valueOf(getTicks(node.getAfter()));
/*     */         }
/* 415 */         if (node.isDeleted()) {
/* 416 */           return Integer.valueOf(getTicks(node.getBefore()));
/*     */         }
/*     */         
/* 419 */         return "" + getTicks(node.getBefore()) + "/" + getTicks(node.getBefore());
/*     */       } 
/*     */       
/* 422 */       return "";
/*     */     }
/*     */ 
/*     */     
/*     */     public TableCellRenderer getCustomizedRenderer(Object o, TableCellRenderer renderer) {
/* 427 */       return this.myAlignedRenderer;
/*     */     }
/*     */   }
/*     */ }


/* Location:              C:\Program Files\JetBrains\WebStorm 2021.1\plugins\NodeJS\lib\NodeJS.jar!\com\jetbrains\nodejs\run\profile\cpu\v8log\diff\V8CpuDiffTableModel.class
 * Java compiler version: 11 (55.0)
 * JD-Core Version:       1.1.3
 */