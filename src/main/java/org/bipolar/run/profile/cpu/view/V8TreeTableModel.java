/*     */ package org.bipolar.run.profile.cpu.view;
/*     */ 
/*     */ import com.intellij.openapi.util.Pair;
/*     */ import com.intellij.openapi.util.text.StringUtil;
/*     */ import com.intellij.ui.ColoredTreeCellRenderer;
/*     */ import com.intellij.ui.treeStructure.treetable.TreeTableCellRenderer;
/*     */ import com.intellij.ui.treeStructure.treetable.TreeTableModel;
/*     */ import com.intellij.util.ArrayUtil;
/*     */ import com.intellij.util.Processor;
/*     */ import com.intellij.util.containers.Predicate;
/*     */ import com.intellij.util.ui.ColumnInfo;
/*     */ import org.bipolar.NodeJSBundle;
/*     */ import org.bipolar.run.profile.V8Utils;
/*     */ import org.bipolar.run.profile.cpu.calculation.CallTreeType;
/*     */ import org.bipolar.run.profile.cpu.calculation.V8ProfileLine;
/*     */ import org.bipolar.run.profile.cpu.v8log.data.V8CpuLogCall;
/*     */ import java.util.ArrayDeque;
/*     */ import java.util.ArrayList;
/*     */ import java.util.List;
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
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class V8TreeTableModel
/*     */   implements FilteredByPercent, TreeTableModelWithCustomRenderer
/*     */ {
/*     */   public static final int SELF_THRESHOLD = 100;
/*     */   public static final int TOTAL_THRESHOLD = 100;
/*     */   private final V8ProfileLine myRoot;
/*     */   @NotNull
/*     */   private final CallTreeType myCallTreeType;
/*     */   private final ColumnInfo[] myColumns;
/*     */   private final V8ProfileLineTreeCellRenderer<V8ProfileLine> myZeroCellRenderer;
/*     */   private V8ProfileLine myFiltered;
/*     */   
/*     */   private static List<ColumnInfo> createBottomUpColumns(@NotNull Processor<V8ProfileLine> processor, @NotNull V8ProfileLineTreeCellRenderer renderer) {
/*  58 */     if (processor == null) $$$reportNull$$$0(0);  if (renderer == null) $$$reportNull$$$0(1);  List<ColumnInfo> list = new ArrayList<>();
/*  59 */     list.add(new V8ProfileLineV8ProfileLineColumnInfo(renderer));
/*  60 */     ColumnInfo<V8ProfileLine, String> ticks = new V8ColumnForNumbers<V8ProfileLine>(NodeJSBundle.message("profile.cpu.table.ticks.column.name", new Object[0]), processor) {
/*     */         @NotNull
/*     */         public String valueOf(V8ProfileLine line) {
/*  63 */           if (String.valueOf(line.getTotalTicks()) == null) $$$reportNull$$$0(0);  return String.valueOf(line.getTotalTicks());
/*     */         }
/*     */       };
/*     */     
/*  67 */     ColumnInfo<V8ProfileLine, String> ofParent = new V8ColumnForNumbers<V8ProfileLine>(NodeJSBundle.message("profile.cpu.table.of_parent.column.name", new Object[0]), processor)
/*     */       {
/*     */         @NotNull
/*     */         public String valueOf(V8ProfileLine line) {
/*  71 */           if (V8Utils.formatPercent(line.getTotalTensPercent()) == null) $$$reportNull$$$0(0);  return V8Utils.formatPercent(line.getTotalTensPercent());
/*     */         }
/*     */       };
/*  74 */     list.add(ticks);
/*  75 */     list.add(ofParent);
/*  76 */     return list;
/*     */   }
/*     */ 
/*     */   
/*     */   private static List<ColumnInfo> createTopDownColumns(@NotNull Processor<V8ProfileLine> processor, @NotNull V8ProfileLineTreeCellRenderer renderer) {
/*  81 */     if (processor == null) $$$reportNull$$$0(2);  if (renderer == null) $$$reportNull$$$0(3);  List<ColumnInfo> list = new ArrayList<>();
/*  82 */     list.add(new V8ProfileLineV8ProfileLineColumnInfo(renderer));
/*     */     
/*  84 */     ColumnInfo<V8ProfileLine, String> totalPercent = new V8ColumnForNumbers<V8ProfileLine>(NodeJSBundle.message("profile.cpu.table.total_percentage.column.name", new Object[0]), processor)
/*     */       {
/*     */         @NotNull
/*     */         public String valueOf(V8ProfileLine line) {
/*  88 */           if (V8Utils.formatPercent(line.getTotalTensPercent()) == null) $$$reportNull$$$0(0);  return V8Utils.formatPercent(line.getTotalTensPercent());
/*     */         }
/*     */       };
/*     */     
/*  92 */     ColumnInfo<V8ProfileLine, String> self = new V8ColumnForNumbers<V8ProfileLine>(NodeJSBundle.message("profile.cpu.table.self_percentage.column.name", new Object[0]), processor)
/*     */       {
/*     */         @NotNull
/*     */         public String valueOf(V8ProfileLine line) {
/*  96 */           if (V8Utils.formatPercent(line.getSelfTensPercent()) == null) $$$reportNull$$$0(0);  return V8Utils.formatPercent(line.getSelfTensPercent());
/*     */         }
/*     */       };
/*     */     
/* 100 */     ColumnInfo<V8ProfileLine, String> total = new V8ColumnForNumbers<V8ProfileLine>(NodeJSBundle.message("profile.cpu.table.total.column.name", new Object[0]), processor) {
/*     */         @NotNull
/*     */         public String valueOf(V8ProfileLine line) {
/* 103 */           if (String.valueOf(line.getTotalTicks()) == null) $$$reportNull$$$0(0);  return String.valueOf(line.getTotalTicks());
/*     */         }
/*     */       };
/* 106 */     ColumnInfo<V8ProfileLine, String> selfTicks = new V8ColumnForNumbers<V8ProfileLine>(NodeJSBundle.message("profile.cpu.table.self.column.name", new Object[0]), processor) {
/*     */         @NotNull
/*     */         public String valueOf(V8ProfileLine line) {
/* 109 */           if (String.valueOf(line.getSelfTicks()) == null) $$$reportNull$$$0(0);  return String.valueOf(line.getSelfTicks());
/*     */         }
/*     */       };
/*     */     
/* 113 */     list.add(total);
/* 114 */     list.add(totalPercent);
/* 115 */     list.add(selfTicks);
/* 116 */     list.add(self);
/* 117 */     return list;
/*     */   }
/*     */   
/* 120 */   private int myTensPercentLevelInclusive = 0;
/*     */ 
/*     */ 
/*     */   
/*     */   public V8TreeTableModel(@NotNull V8ProfileLine root, @NotNull CallTreeType callTreeType, @NotNull LineColorProvider fileColors) {
/* 125 */     this.myRoot = root;
/* 126 */     this.myCallTreeType = callTreeType;
/* 127 */     Processor<V8ProfileLine> processor = value -> (v8LineIsAboveThresholds(value) && (CallTreeType.topDown.equals(this.myCallTreeType) || (value.getParent() != null && value.getParent().getParent() == null)));
/*     */     
/* 129 */     this.myZeroCellRenderer = new V8ProfileLineTreeCellRenderer<V8ProfileLine>(processor, fileColors)
/*     */       {
/*     */         protected V8CpuLogCall getCall(Object object) {
/* 132 */           if (!(object instanceof V8ProfileLine)) return null; 
/* 133 */           return ((V8ProfileLine)object).getCall();
/*     */         }
/*     */       };
/*     */     
/* 137 */     List<ColumnInfo> listColumns = CallTreeType.bottomUp.equals(callTreeType) ? createBottomUpColumns(processor, this.myZeroCellRenderer) : createTopDownColumns(processor, this.myZeroCellRenderer);
/* 138 */     this.myColumns = listColumns.<ColumnInfo>toArray(ColumnInfo.EMPTY_ARRAY);
/*     */   }
/*     */   
/*     */   public V8ProfileLineTreeCellRenderer getZeroCellRenderer() {
/* 142 */     return this.myZeroCellRenderer;
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean isFiltered() {
/* 147 */     return (this.myFiltered != null);
/*     */   }
/*     */ 
/*     */   
/*     */   public int getTensPercentLevelInclusive() {
/* 152 */     return this.myTensPercentLevelInclusive;
/*     */   }
/*     */ 
/*     */   
/*     */   public void clearFilter() {
/* 157 */     this.myTensPercentLevelInclusive = 0;
/* 158 */     this.myFiltered = null;
/*     */   }
/*     */ 
/*     */   
/*     */   public void filterByLevel(int tensPercentLevelInclusive) {
/* 163 */     this.myTensPercentLevelInclusive = tensPercentLevelInclusive;
/* 164 */     Predicate<V8ProfileLine> predicate = input -> (input.getTotalTensPercent() >= tensPercentLevelInclusive);
/* 165 */     this.myFiltered = new V8ProfileLine(-1, -1, -1, -1, null, 0, "", -1L);
/* 166 */     ArrayDeque<Pair<V8ProfileLine, V8ProfileLine>> queue = new ArrayDeque<>();
/* 167 */     for (V8ProfileLine line : this.myRoot.getChildren()) {
/* 168 */       if (predicate.apply(line)) {
/* 169 */         queue.add(Pair.create(this.myFiltered, line));
/*     */       }
/*     */     } 
/* 172 */     while (!queue.isEmpty()) {
/* 173 */       Pair<V8ProfileLine, V8ProfileLine> pair = queue.removeFirst();
/* 174 */       if (!predicate.apply(pair.getSecond()))
/* 175 */         continue;  V8ProfileLine copy = ((V8ProfileLine)pair.getSecond()).cloneWithoutChildren((V8ProfileLine)pair.getFirst());
/* 176 */       List<V8ProfileLine> children = ((V8ProfileLine)pair.getSecond()).getChildren();
/* 177 */       for (V8ProfileLine child : children) {
/* 178 */         queue.add(Pair.create(copy, child));
/*     */       }
/*     */     } 
/*     */   }
/*     */   
/*     */   public List<V8ProfileLine> search(@NotNull String s, boolean caseSensitive) {
/* 184 */     if (s == null) $$$reportNull$$$0(7);  List<V8ProfileLine> list = new ArrayList<>();
/* 185 */     ArrayDeque<V8ProfileLine> queue = new ArrayDeque<>();
/* 186 */     queue.add(this.myRoot);
/* 187 */     while (!queue.isEmpty()) {
/* 188 */       V8ProfileLine line = queue.removeFirst();
/* 189 */       String presentation = line.getPresentation(false);
/* 190 */       boolean matches = caseSensitive ? presentation.contains(s) : StringUtil.toLowerCase(presentation).contains(StringUtil.toLowerCase(s));
/* 191 */       if (matches) list.add(line); 
/* 192 */       queue.addAll(line.getChildren());
/*     */     } 
/* 194 */     return list;
/*     */   }
/*     */ 
/*     */   
/*     */   public int getColumnCount() {
/* 199 */     return this.myColumns.length;
/*     */   }
/*     */ 
/*     */   
/*     */   public String getColumnName(int column) {
/* 204 */     return this.myColumns[column].getName();
/*     */   }
/*     */ 
/*     */   
/*     */   public TableCellRenderer getCustomizedRenderer(int column, Object o, TableCellRenderer renderer) {
/* 209 */     return this.myColumns[column].getCustomizedRenderer(o, renderer);
/*     */   }
/*     */ 
/*     */   
/*     */   public Class getColumnClass(int column) {
/* 214 */     return (column == 0) ? TreeTableModel.class : String.class;
/*     */   }
/*     */ 
/*     */   
/*     */   public Object getValueAt(Object node, int column) {
/* 219 */     return this.myColumns[column].valueOf(node);
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean isCellEditable(Object node, int column) {
/* 224 */     return false;
/*     */   }
/*     */ 
/*     */   
/*     */   public void setValueAt(Object aValue, Object node, int column) {
/* 229 */     throw new UnsupportedOperationException();
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public void setTree(JTree tree) {}
/*     */ 
/*     */   
/*     */   public Object getRoot() {
/* 238 */     return (this.myFiltered != null) ? this.myFiltered : this.myRoot;
/*     */   }
/*     */ 
/*     */   
/*     */   public Object getChild(Object parent, int index) {
/* 243 */     return ((V8ProfileLine)parent).getChildren().get(index);
/*     */   }
/*     */ 
/*     */   
/*     */   public int getChildCount(Object parent) {
/* 248 */     return ((V8ProfileLine)parent).getChildren().size();
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean isLeaf(Object node) {
/* 253 */     return (getChildCount(node) == 0);
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public void valueForPathChanged(TreePath path, Object newValue) {}
/*     */ 
/*     */   
/*     */   public int getIndexOfChild(Object parent, Object child) {
/* 262 */     List<V8ProfileLine> children = ((V8ProfileLine)parent).getChildren();
/* 263 */     for (int i = 0; i < children.size(); i++) {
/* 264 */       V8ProfileLine current = children.get(i);
/* 265 */       if (child.equals(current)) return i; 
/*     */     } 
/* 267 */     return 0;
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public void addTreeModelListener(TreeModelListener l) {}
/*     */ 
/*     */   
/*     */   public void removeTreeModelListener(TreeModelListener l) {}
/*     */ 
/*     */   
/*     */   @Nullable
/*     */   public TreePath createSelectionPathForStackTrace(@NotNull List<Long> stack) {
/* 280 */     if (stack == null) $$$reportNull$$$0(8);  List<Object> nodesList = findPath(stack, (V8ProfileLine)getRoot());
/* 281 */     if (nodesList.size() <= stack.size() && 
/* 282 */       this.myFiltered != null) {
/* 283 */       nodesList = findPath(stack, this.myRoot);
/* 284 */       if (nodesList.size() > 1) clearFilter();
/*     */     
/*     */     } 
/* 287 */     if (nodesList.size() == 1) {
/* 288 */       return null;
/*     */     }
/* 290 */     return new TreePath(ArrayUtil.toObjectArray(nodesList));
/*     */   }
/*     */   
/*     */   @NotNull
/*     */   private static List<Object> findPath(@NotNull List<Long> stack, V8ProfileLine root) {
/* 295 */     if (stack == null) $$$reportNull$$$0(9);  List<Object> nodesList = new ArrayList();
/* 296 */     V8ProfileLine line = root;
/* 297 */     nodesList.add(line);
/* 298 */     for (Long stringId : stack) {
/* 299 */       List<V8ProfileLine> children = line.getChildren();
/* 300 */       V8ProfileLine found = null;
/* 301 */       for (V8ProfileLine child : children) {
/* 302 */         if (child.getCall().getStringId() == stringId.longValue()) {
/* 303 */           found = child;
/* 304 */           line = found;
/*     */           break;
/*     */         } 
/*     */       } 
/* 308 */       if (found == null)
/* 309 */         break;  nodesList.add(found);
/*     */     } 
/* 311 */     if (nodesList == null) $$$reportNull$$$0(10);  return nodesList;
/*     */   }
/*     */   
/*     */   public static class V8ProfileLineV8ProfileLineColumnInfo extends ColumnInfo<V8ProfileLine, V8ProfileLine> {
/*     */     private final ColoredTreeCellRenderer myRenderer;
/*     */     
/*     */     public V8ProfileLineV8ProfileLineColumnInfo(@NotNull V8ProfileLineTreeCellRenderer renderer) {
/* 318 */       super(NodeJSBundle.message("profile.cpu.table.column.calls.name", new Object[0]));
/* 319 */       this.myRenderer = renderer;
/*     */     }
/*     */ 
/*     */     
/*     */     @Nullable
/*     */     public V8ProfileLine valueOf(V8ProfileLine line) {
/* 325 */       return line;
/*     */     }
/*     */ 
/*     */     
/*     */     public TableCellRenderer getCustomizedRenderer(V8ProfileLine line, TableCellRenderer renderer) {
/* 330 */       if (renderer instanceof TreeTableCellRenderer) {
/* 331 */         ((TreeTableCellRenderer)renderer).setCellRenderer((TreeCellRenderer)this.myRenderer);
/*     */       }
/* 333 */       return renderer;
/*     */     }
/*     */   }
/*     */   
/*     */   static boolean v8LineIsAboveThresholds(V8ProfileLine line) {
/* 338 */     return (line.getSelfTensPercent() >= 100 || line.getTotalTensPercent() >= 100);
/*     */   }
/*     */ }


/* Location:              C:\Program Files\JetBrains\WebStorm 2021.1\plugins\NodeJS\lib\NodeJS.jar!\com\jetbrains\nodejs\run\profile\cpu\view\V8TreeTableModel.class
 * Java compiler version: 11 (55.0)
 * JD-Core Version:       1.1.3
 */