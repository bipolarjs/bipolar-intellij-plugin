/*     */ package org.bipolar.run.profile.cpu.view;
/*     */ 
/*     */ import com.intellij.openapi.util.Pair;
/*     */ import com.intellij.openapi.util.text.StringUtil;
/*     */ import com.intellij.ui.ColoredTreeCellRenderer;
/*     */ import com.intellij.ui.treeStructure.treetable.TreeTableCellRenderer;
/*     */ import com.intellij.ui.treeStructure.treetable.TreeTableModel;
/*     */ import com.intellij.util.Processor;
/*     */ import com.intellij.util.ui.ColumnInfo;
/*     */ import org.bipolar.NodeJSBundle;
/*     */ import org.bipolar.run.profile.V8Utils;
/*     */ import org.bipolar.run.profile.cpu.calculation.V8ProfileLine;
/*     */ import org.bipolar.run.profile.cpu.v8log.data.V8CpuLogCall;
/*     */ import java.util.ArrayList;
/*     */ import java.util.HashMap;
/*     */ import java.util.List;
/*     */ import java.util.Map;
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
/*     */ public class TopCalledV8TreeTableModel
/*     */   implements FilteredByPercent, TreeTableModelWithCustomRenderer
/*     */ {
/*  45 */   private final Object myRoot = new Object();
/*     */   
/*     */   private final List<String> myGroups;
/*     */   private final Map<String, List<V8ProfileLine>> myCalls;
/*     */   private final ColumnInfo[] myColumns;
/*     */   private List<String> myFilteredGroups;
/*     */   private Map<String, List<V8ProfileLine>> myFilteredCalls;
/*  52 */   private int myTensPercentLevelInclusive = 0;
/*     */   private V8ProfileLineTreeCellRenderer<V8ProfileLine> myZeroRenderer;
/*     */   
/*     */   public TopCalledV8TreeTableModel(@NotNull List<Pair<String, List<V8ProfileLine>>> data, @NotNull LineColorProvider fileColor) {
/*  56 */     this.myCalls = new HashMap<>(data.size(), 1.0F);
/*  57 */     this.myGroups = new ArrayList<>(data.size());
/*  58 */     for (Pair<String, List<V8ProfileLine>> pair : data) {
/*  59 */       this.myCalls.put((String)pair.getFirst(), (List<V8ProfileLine>)pair.getSecond());
/*  60 */       this.myGroups.add((String)pair.getFirst());
/*     */     } 
/*  62 */     List<ColumnInfo> list = createColumns(fileColor);
/*  63 */     this.myColumns = list.<ColumnInfo>toArray(ColumnInfo.EMPTY_ARRAY);
/*     */   }
/*     */   
/*     */   public V8ProfileLineTreeCellRenderer getZeroRenderer() {
/*  67 */     return this.myZeroRenderer;
/*     */   }
/*     */ 
/*     */   
/*     */   public int getColumnCount() {
/*  72 */     return this.myColumns.length;
/*     */   }
/*     */ 
/*     */   
/*     */   public String getColumnName(int column) {
/*  77 */     return this.myColumns[column].getName();
/*     */   }
/*     */ 
/*     */   
/*     */   public TableCellRenderer getCustomizedRenderer(int column, Object o, TableCellRenderer renderer) {
/*  82 */     return this.myColumns[column].getCustomizedRenderer(o, renderer);
/*     */   }
/*     */ 
/*     */   
/*     */   public Class getColumnClass(int column) {
/*  87 */     return (column == 0) ? TreeTableModel.class : String.class;
/*     */   }
/*     */ 
/*     */   
/*     */   public Object getValueAt(Object node, int column) {
/*  92 */     return this.myColumns[column].valueOf(node);
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean isCellEditable(Object node, int column) {
/*  97 */     return false;
/*     */   }
/*     */ 
/*     */   
/*     */   public void setValueAt(Object aValue, Object node, int column) {
/* 102 */     throw new UnsupportedOperationException();
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public void setTree(JTree tree) {}
/*     */ 
/*     */   
/*     */   public Object getRoot() {
/* 111 */     return this.myRoot;
/*     */   }
/*     */ 
/*     */   
/*     */   public Object getChild(Object parent, int index) {
/* 116 */     List<String> groups = (this.myFilteredGroups != null) ? this.myFilteredGroups : this.myGroups;
/* 117 */     Map<String, List<V8ProfileLine>> calls = (this.myFilteredCalls != null) ? this.myFilteredCalls : this.myCalls;
/*     */     
/* 119 */     if (this.myRoot == parent)
/* 120 */       return groups.get(index); 
/* 121 */     if (parent instanceof String) {
/* 122 */       List<V8ProfileLine> lines = calls.get(parent);
/* 123 */       return (lines == null) ? null : lines.get(index);
/*     */     } 
/* 125 */     return null;
/*     */   }
/*     */ 
/*     */   
/*     */   public int getChildCount(Object parent) {
/* 130 */     List<String> groups = (this.myFilteredGroups != null) ? this.myFilteredGroups : this.myGroups;
/* 131 */     Map<String, List<V8ProfileLine>> calls = (this.myFilteredCalls != null) ? this.myFilteredCalls : this.myCalls;
/*     */     
/* 133 */     if (this.myRoot == parent)
/* 134 */       return groups.size(); 
/* 135 */     if (parent instanceof String) {
/* 136 */       List<V8ProfileLine> lines = calls.get(parent);
/* 137 */       return (lines == null) ? 0 : lines.size();
/*     */     } 
/* 139 */     return 0;
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean isLeaf(Object node) {
/* 144 */     return (getChildCount(node) == 0);
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public void valueForPathChanged(TreePath path, Object newValue) {}
/*     */ 
/*     */   
/*     */   public int getIndexOfChild(Object parent, Object child) {
/* 153 */     List<String> groups = (this.myFilteredGroups != null) ? this.myFilteredGroups : this.myGroups;
/* 154 */     Map<String, List<V8ProfileLine>> calls = (this.myFilteredCalls != null) ? this.myFilteredCalls : this.myCalls;
/*     */     
/* 156 */     if (this.myRoot == parent) {
/* 157 */       for (int i = 0; i < groups.size(); i++) {
/* 158 */         String s = groups.get(i);
/* 159 */         if (child == s) {
/* 160 */           return i;
/*     */         }
/*     */       } 
/* 163 */     } else if (parent instanceof String) {
/* 164 */       List<V8ProfileLine> lines = calls.get(parent);
/* 165 */       for (int i = 0; i < lines.size(); i++) {
/* 166 */         V8ProfileLine line = lines.get(i);
/* 167 */         if (line == child) {
/* 168 */           return i;
/*     */         }
/*     */       } 
/*     */     } 
/* 172 */     return 0;
/*     */   }
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
/*     */   public boolean isFiltered() {
/* 185 */     return (this.myFilteredCalls != null && this.myFilteredGroups != null);
/*     */   }
/*     */ 
/*     */   
/*     */   public int getTensPercentLevelInclusive() {
/* 190 */     return this.myTensPercentLevelInclusive;
/*     */   }
/*     */ 
/*     */   
/*     */   public void clearFilter() {
/* 195 */     this.myFilteredGroups = null;
/* 196 */     this.myFilteredCalls = null;
/* 197 */     this.myTensPercentLevelInclusive = 0;
/*     */   }
/*     */ 
/*     */   
/*     */   public void filterByLevel(int tensPercentLevelInclusive) {
/* 202 */     this.myTensPercentLevelInclusive = tensPercentLevelInclusive;
/* 203 */     this.myFilteredCalls = new HashMap<>();
/* 204 */     for (Map.Entry<String, List<V8ProfileLine>> entry : this.myCalls.entrySet()) {
/* 205 */       String group = entry.getKey();
/* 206 */       List<V8ProfileLine> lines = entry.getValue();
/* 207 */       List<V8ProfileLine> filtered = new ArrayList<>();
/* 208 */       for (V8ProfileLine line : lines) {
/* 209 */         if (line.getTotalTensPercent() >= tensPercentLevelInclusive) {
/* 210 */           filtered.add(line.cloneWithoutChildren(null));
/*     */         }
/*     */       } 
/* 213 */       if (!filtered.isEmpty()) {
/* 214 */         this.myFilteredCalls.put(group, filtered);
/*     */       }
/*     */     } 
/* 217 */     this.myFilteredGroups = new ArrayList<>(this.myFilteredCalls.size());
/* 218 */     for (String group : this.myGroups) {
/* 219 */       if (this.myFilteredCalls.containsKey(group)) {
/* 220 */         this.myFilteredGroups.add(group);
/*     */       }
/*     */     } 
/*     */   }
/*     */   
/*     */   public Map<V8ProfileLine, String> search(@NotNull String text, boolean caseSensitive) {
/* 226 */     if (text == null) $$$reportNull$$$0(2);  Map<V8ProfileLine, String> map = new HashMap<>();
/* 227 */     for (Map.Entry<String, List<V8ProfileLine>> entry : this.myCalls.entrySet()) {
/* 228 */       for (V8ProfileLine line : entry.getValue()) {
/* 229 */         String presentation = line.getPresentation(false);
/*     */         
/* 231 */         boolean matches = caseSensitive ? presentation.contains(text) : StringUtil.toLowerCase(presentation).contains(StringUtil.toLowerCase(text));
/* 232 */         if (matches) map.put(line, entry.getKey()); 
/*     */       } 
/*     */     } 
/* 235 */     return map;
/*     */   }
/*     */   
/*     */   private List<ColumnInfo> createColumns(@NotNull LineColorProvider fileColor) {
/* 239 */     if (fileColor == null) $$$reportNull$$$0(3);  List<ColumnInfo> list = new ArrayList<>();
/* 240 */     Processor<V8ProfileLine> processor = line -> V8TreeTableModel.v8LineIsAboveThresholds(line);
/* 241 */     this.myZeroRenderer = new V8ProfileLineTreeCellRenderer<V8ProfileLine>(processor, fileColor)
/*     */       {
/*     */         protected V8CpuLogCall getCall(Object object) {
/* 244 */           if (!(object instanceof V8ProfileLine)) return null; 
/* 245 */           return ((V8ProfileLine)object).getCall();
/*     */         }
/*     */       };
/* 248 */     list.add(new V8ProfileLineColumnInfo(this.myZeroRenderer));
/*     */     
/* 250 */     ColumnInfo<Object, String> totalPercent = new V8ColumnForNumbers(NodeJSBundle.message("profile.cpu.top_called_table.total_perc.column.name", new Object[0]), processor) {
/*     */         @NotNull
/*     */         public String valueOf(Object line) {
/* 253 */           if (line instanceof V8ProfileLine) {
/*     */             
/* 255 */             if (V8Utils.formatPercent(((V8ProfileLine)line).getTotalTensPercent()) == null) $$$reportNull$$$0(0);  return V8Utils.formatPercent(((V8ProfileLine)line).getTotalTensPercent());
/*     */           } 
/* 257 */           return "";
/*     */         }
/*     */       };
/*     */     
/* 261 */     ColumnInfo<Object, String> selfPercent = new V8ColumnForNumbers(NodeJSBundle.message("profile.cpu.top_called_table.self_perc.column.name", new Object[0]), processor) {
/*     */         @NotNull
/*     */         public String valueOf(Object line) {
/* 264 */           if (line instanceof V8ProfileLine) {
/*     */             
/* 266 */             if (V8Utils.formatPercent(((V8ProfileLine)line).getSelfTensPercent()) == null) $$$reportNull$$$0(0);  return V8Utils.formatPercent(((V8ProfileLine)line).getSelfTensPercent());
/*     */           } 
/* 268 */           return "";
/*     */         }
/*     */       };
/*     */     
/* 272 */     ColumnInfo<Object, String> total = new V8ColumnForNumbers(NodeJSBundle.message("profile.cpu.top_called_table.total.column.name", new Object[0]), processor) {
/*     */         @NotNull
/*     */         public String valueOf(Object line) {
/* 275 */           if (line instanceof V8ProfileLine) {
/* 276 */             if (String.valueOf(((V8ProfileLine)line).getTotalTicks()) == null) $$$reportNull$$$0(0);  return String.valueOf(((V8ProfileLine)line).getTotalTicks());
/*     */           } 
/* 278 */           return "";
/*     */         }
/*     */       };
/*     */     
/* 282 */     ColumnInfo<Object, String> self = new V8ColumnForNumbers(NodeJSBundle.message("profile.cpu.top_called_table.self.column.name", new Object[0]), processor) {
/*     */         @NotNull
/*     */         public String valueOf(Object line) {
/* 285 */           if (line instanceof V8ProfileLine) {
/* 286 */             if (String.valueOf(((V8ProfileLine)line).getSelfTicks()) == null) $$$reportNull$$$0(0);  return String.valueOf(((V8ProfileLine)line).getSelfTicks());
/*     */           } 
/* 288 */           return "";
/*     */         }
/*     */       };
/*     */     
/* 292 */     list.add(self);
/* 293 */     list.add(selfPercent);
/* 294 */     list.add(total);
/* 295 */     list.add(totalPercent);
/* 296 */     return list;
/*     */   }
/*     */   
/*     */   @Nullable
/*     */   public TreePath createPathToCall(@NotNull String group, long stringId) {
/* 301 */     if (group == null) $$$reportNull$$$0(4);  Object[] path = new Object[3];
/* 302 */     path[0] = getRoot();
/* 303 */     path[1] = group;
/* 304 */     V8ProfileLine found = findLine(group, stringId, (this.myFilteredCalls != null) ? this.myFilteredCalls : this.myCalls);
/* 305 */     if (found == null && 
/* 306 */       this.myFilteredCalls != null) {
/* 307 */       found = findLine(group, stringId, this.myCalls);
/* 308 */       if (found != null) clearFilter();
/*     */     
/*     */     } 
/* 311 */     if (found == null) return null; 
/* 312 */     path[2] = found;
/* 313 */     return new TreePath(path);
/*     */   }
/*     */ 
/*     */   
/*     */   private V8ProfileLine findLine(@NotNull String group, long stringId, @NotNull Map<String, List<V8ProfileLine>> map) {
/* 318 */     if (group == null) $$$reportNull$$$0(5);  if (SYNTHETIC_LOCAL_VARIABLE_4 == null) $$$reportNull$$$0(6);  List<V8ProfileLine> list = (List<V8ProfileLine>)SYNTHETIC_LOCAL_VARIABLE_4.get(group);
/* 319 */     if (list == null) return null; 
/* 320 */     for (V8ProfileLine line : list) {
/* 321 */       if (line.getCall().getStringId() == stringId) {
/* 322 */         return line;
/*     */       }
/*     */     } 
/* 325 */     return null;
/*     */   }
/*     */   
/*     */   private static class V8ProfileLineColumnInfo extends ColumnInfo<Object, String> { private final ColoredTreeCellRenderer myCellRenderer;
/*     */     
/*     */     V8ProfileLineColumnInfo(@NotNull V8ProfileLineTreeCellRenderer renderer) {
/* 331 */       super(NodeJSBundle.message("profile.cpu.top_called_table.column.calls.name", new Object[0]));
/* 332 */       this.myCellRenderer = renderer;
/*     */     }
/*     */ 
/*     */     
/*     */     @Nullable
/*     */     public String valueOf(Object line) {
/* 338 */       return line.toString();
/*     */     }
/*     */ 
/*     */     
/*     */     public TableCellRenderer getCustomizedRenderer(Object o, TableCellRenderer renderer) {
/* 343 */       if (renderer instanceof TreeTableCellRenderer) {
/* 344 */         ((TreeTableCellRenderer)renderer).setCellRenderer((TreeCellRenderer)this.myCellRenderer);
/*     */       }
/* 346 */       return renderer;
/*     */     } }
/*     */ 
/*     */ }


/* Location:              C:\Program Files\JetBrains\WebStorm 2021.1\plugins\NodeJS\lib\NodeJS.jar!\com\jetbrains\nodejs\run\profile\cpu\view\TopCalledV8TreeTableModel.class
 * Java compiler version: 11 (55.0)
 * JD-Core Version:       1.1.3
 */