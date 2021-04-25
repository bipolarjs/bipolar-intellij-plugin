/*     */ package org.bipolar.run.profile.heap.view.models;
/*     */ 
/*     */ import com.intellij.openapi.project.Project;
/*     */ import com.intellij.openapi.util.Pair;
/*     */ import com.intellij.ui.treeStructure.treetable.TreeTableCellRenderer;
/*     */ import com.intellij.ui.treeStructure.treetable.TreeTableModel;
/*     */ import com.intellij.util.ui.ColumnInfo;
/*     */ import org.bipolar.NodeJSBundle;
/*     */ import org.bipolar.run.profile.cpu.view.TreeTableModelWithCustomRenderer;
/*     */ import org.bipolar.run.profile.heap.V8CachingReader;
/*     */ import org.bipolar.run.profile.heap.data.Aggregate;
/*     */ import org.bipolar.run.profile.heap.data.V8HeapEdge;
/*     */ import org.bipolar.run.profile.heap.data.V8HeapEntry;
/*     */ import org.bipolar.run.profile.heap.view.renderers.DirectTreeTableRenderer;
/*     */ import org.bipolar.run.profile.heap.view.renderers.RightAlignedRenderer;
/*     */ import org.bipolar.run.profile.heap.view.renderers.SizeRenderer;
/*     */ import java.util.ArrayList;
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
/*     */ 
/*     */ public class AggregatesTableModel
/*     */   implements TreeTableModelWithCustomRenderer
/*     */ {
/*  48 */   private final Object myRoot = new Object();
/*     */   
/*     */   private final ColumnInfo[] myColumns;
/*     */   
/*     */   @NotNull
/*     */   private final Project myProject;
/*     */   
/*     */   public AggregatesTableModel(@NotNull Project project, @NotNull V8CachingReader reader) {
/*  56 */     this.myProject = project;
/*  57 */     this.myReader = reader;
/*  58 */     this.myColumns = new ColumnInfo[5];
/*  59 */     fillColumns();
/*  60 */     Map<Long, Aggregate> map = this.myReader.getAggregatesMap();
/*  61 */     this.myKeys = new ArrayList<>(map.keySet());
/*     */   } @NotNull
/*     */   private final V8CachingReader myReader; private DirectTreeTableRenderer myRenderer; private final List<Long> myKeys;
/*     */   private void fillColumns() {
/*  65 */     this.myRenderer = new DirectTreeTableRenderer(this.myProject, this.myReader);
/*     */     
/*  67 */     this.myColumns[0] = new ColumnInfo<Object, Object>(NodeJSBundle.message("profile.table.column.constructor.name", new Object[0]))
/*     */       {
/*     */         @Nullable
/*     */         public Object valueOf(Object o) {
/*  71 */           return o;
/*     */         }
/*     */ 
/*     */         
/*     */         public TableCellRenderer getCustomizedRenderer(Object o, TableCellRenderer renderer) {
/*  76 */           if (renderer instanceof TreeTableCellRenderer) {
/*  77 */             ((TreeTableCellRenderer)renderer).setCellRenderer((TreeCellRenderer)AggregatesTableModel.this.myRenderer);
/*     */           }
/*  79 */           return super.getCustomizedRenderer(o, renderer);
/*     */         }
/*     */       };
/*  82 */     final RightAlignedRenderer alignedRenderer = new RightAlignedRenderer();
/*  83 */     this.myColumns[1] = new ColumnInfo<Object, Object>(NodeJSBundle.message("profile.table.column.distance.name", new Object[0])) {
/*     */         @NotNull
/*     */         public Object valueOf(Object o) {
/*  86 */           if (o instanceof V8HeapContainmentTreeTableModel.NamedEntry) {
/*  87 */             V8HeapContainmentTreeTableModel.NamedEntry entry = (V8HeapContainmentTreeTableModel.NamedEntry)o;
/*  88 */             int distance = AggregatesTableModel.this.myReader.getDistance((int)entry.getEntry().getId());
/*  89 */             if (AggregatesTableModel.renderDistance(distance) == null) $$$reportNull$$$0(0);  return AggregatesTableModel.renderDistance(distance);
/*     */           } 
/*  91 */           if (o instanceof Aggregate) {
/*  92 */             int distance = ((Aggregate)o).getDistance();
/*  93 */             if (AggregatesTableModel.renderDistance(distance) == null) $$$reportNull$$$0(1);  return AggregatesTableModel.renderDistance(distance);
/*     */           } 
/*  95 */           return "";
/*     */         }
/*     */ 
/*     */         
/*     */         public TableCellRenderer getCustomizedRenderer(Object o, TableCellRenderer renderer) {
/* 100 */           return (TableCellRenderer)alignedRenderer;
/*     */         }
/*     */       };
/* 103 */     this.myColumns[2] = new ColumnInfo<Object, Object>(NodeJSBundle.message("profile.table.column.objects_count.name", new Object[0])) {
/*     */         @NotNull
/*     */         public Object valueOf(Object o) {
/* 106 */           if (o instanceof Aggregate) {
/* 107 */             if (String.valueOf(((Aggregate)o).getCnt()) == null) $$$reportNull$$$0(0);  return String.valueOf(((Aggregate)o).getCnt());
/*     */           } 
/* 109 */           return "";
/*     */         }
/*     */ 
/*     */         
/*     */         public TableCellRenderer getCustomizedRenderer(Object o, TableCellRenderer renderer) {
/* 114 */           return (TableCellRenderer)alignedRenderer;
/*     */         }
/*     */       };
/* 117 */     final SizeRenderer sizeRenderer = new SizeRenderer(this.myReader.getRetainedSize(0));
/* 118 */     this.myColumns[3] = new ColumnInfo<Object, Object>(NodeJSBundle.message("profile.table.column.shallow_size.name", new Object[0])) {
/*     */         @NotNull
/*     */         public Object valueOf(Object o) {
/* 121 */           if (o instanceof V8HeapContainmentTreeTableModel.NamedEntry) {
/* 122 */             V8HeapContainmentTreeTableModel.NamedEntry entry = (V8HeapContainmentTreeTableModel.NamedEntry)o;
/* 123 */             if (Long.valueOf(entry.getEntry().getSize()) == null) $$$reportNull$$$0(0);  return Long.valueOf(entry.getEntry().getSize());
/*     */           } 
/* 125 */           if (o instanceof Aggregate) {
/* 126 */             if (Long.valueOf(((Aggregate)o).getSelfSize()) == null) $$$reportNull$$$0(1);  return Long.valueOf(((Aggregate)o).getSelfSize());
/*     */           } 
/* 128 */           return "";
/*     */         }
/*     */ 
/*     */         
/*     */         public TableCellRenderer getCustomizedRenderer(Object o, TableCellRenderer renderer) {
/* 133 */           return (TableCellRenderer)sizeRenderer;
/*     */         }
/*     */       };
/* 136 */     this.myColumns[4] = new ColumnInfo<Object, Object>(NodeJSBundle.message("profile.table.column.retained.size.name", new Object[0])) {
/*     */         @NotNull
/*     */         public Object valueOf(Object o) {
/* 139 */           if (o instanceof V8HeapContainmentTreeTableModel.NamedEntry) {
/* 140 */             V8HeapContainmentTreeTableModel.NamedEntry entry = (V8HeapContainmentTreeTableModel.NamedEntry)o;
/* 141 */             if (Long.valueOf(AggregatesTableModel.this.myReader.getRetainedSize((int)entry.getEntry().getId())) == null) $$$reportNull$$$0(0);  return Long.valueOf(AggregatesTableModel.this.myReader.getRetainedSize((int)entry.getEntry().getId()));
/*     */           } 
/* 143 */           if (o instanceof Aggregate) {
/* 144 */             if (Long.valueOf(((Aggregate)o).getRetained()) == null) $$$reportNull$$$0(1);  return Long.valueOf(((Aggregate)o).getRetained());
/*     */           } 
/* 146 */           return "";
/*     */         }
/*     */ 
/*     */         
/*     */         public TableCellRenderer getCustomizedRenderer(Object o, TableCellRenderer renderer) {
/* 151 */           return (TableCellRenderer)sizeRenderer;
/*     */         }
/*     */       };
/*     */   }
/*     */   
/*     */   @NotNull
/*     */   private static String renderDistance(int distance) {
/* 158 */     if (((distance >= 100000000 || distance < 0) ? "-" : String.valueOf(distance)) == null) $$$reportNull$$$0(2);  return (distance >= 100000000 || distance < 0) ? "-" : String.valueOf(distance);
/*     */   }
/*     */ 
/*     */   
/*     */   public TableCellRenderer getCustomizedRenderer(int column, Object o, TableCellRenderer renderer) {
/* 163 */     return this.myColumns[column].getCustomizedRenderer(o, renderer);
/*     */   }
/*     */ 
/*     */   
/*     */   public int getColumnCount() {
/* 168 */     return this.myColumns.length;
/*     */   }
/*     */ 
/*     */   
/*     */   public String getColumnName(int column) {
/* 173 */     return this.myColumns[column].getName();
/*     */   }
/*     */ 
/*     */   
/*     */   public Class getColumnClass(int column) {
/* 178 */     return (column == 0) ? TreeTableModel.class : String.class;
/*     */   }
/*     */ 
/*     */   
/*     */   public Object getValueAt(Object node, int column) {
/* 183 */     return this.myColumns[column].valueOf(node);
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean isCellEditable(Object node, int column) {
/* 188 */     return false;
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
/* 203 */     return this.myRoot;
/*     */   }
/*     */ 
/*     */   
/*     */   public Object getChild(Object parent, int index) {
/* 208 */     if (parent == this.myRoot)
/* 209 */       return this.myReader.getAggregatesMap().get(this.myKeys.get(index)); 
/* 210 */     if (parent instanceof Aggregate) {
/* 211 */       int id = ((Aggregate)parent).getId();
/* 212 */       List<Long> children = this.myReader.getAggregatesChildren(id);
/*     */       
/* 214 */       V8HeapContainmentTreeTableModel.NamedEntry top = V8HeapContainmentTreeTableModel.NamedEntry.create(((Long)children.get(index)).longValue(), this.myReader);
/* 215 */       top.setDoNotShowLink(true);
/* 216 */       return top;
/* 217 */     }  if (parent instanceof V8HeapContainmentTreeTableModel.NamedEntry) {
/* 218 */       Pair<V8HeapEntry, V8HeapEdge> pair = this.myReader.getChild(((V8HeapContainmentTreeTableModel.NamedEntry)parent).getEntry(), index);
/* 219 */       V8HeapEdge edge = (V8HeapEdge)pair.getSecond();
/* 220 */       return new V8HeapContainmentTreeTableModel.NamedEntry((V8HeapEntry)pair.getFirst(), this.myReader.getString(((V8HeapEntry)pair.getFirst()).getNameId()), edge.getPresentation(this.myReader), edge.getFileOffset());
/*     */     } 
/* 222 */     return null;
/*     */   }
/*     */ 
/*     */   
/*     */   public int getChildCount(Object parent) {
/* 227 */     if (parent == this.myRoot)
/* 228 */       return this.myKeys.size(); 
/* 229 */     if (parent instanceof Aggregate)
/* 230 */       return ((Aggregate)parent).getCnt(); 
/* 231 */     if (parent instanceof V8HeapContainmentTreeTableModel.NamedEntry) {
/* 232 */       return this.myReader.getChildren(((V8HeapContainmentTreeTableModel.NamedEntry)parent).getEntry()).size();
/*     */     }
/* 234 */     return 0;
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean isLeaf(Object node) {
/* 239 */     return (getChildCount(node) == 0);
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public void valueForPathChanged(TreePath path, Object newValue) {}
/*     */ 
/*     */ 
/*     */   
/*     */   public int getIndexOfChild(Object parent, Object child) {
/* 249 */     if (parent == this.myRoot) {
/* 250 */       if (child instanceof Aggregate) {
/* 251 */         Map<Long, Aggregate> aggregatesMap = this.myReader.getAggregatesMap();
/* 252 */         for (int i = 0; i < this.myKeys.size(); i++) {
/* 253 */           Long key = this.myKeys.get(i);
/* 254 */           Aggregate aggregate = aggregatesMap.get(key);
/* 255 */           if (aggregate.getId() == ((Aggregate)child).getId()) {
/* 256 */             return i;
/*     */           }
/*     */         } 
/*     */       } 
/* 260 */     } else if (parent instanceof Aggregate && child instanceof V8HeapContainmentTreeTableModel.NamedEntry) {
/* 261 */       int id = ((Aggregate)parent).getId();
/* 262 */       List<Long> children = this.myReader.getAggregatesChildren(id);
/* 263 */       long childId = ((V8HeapContainmentTreeTableModel.NamedEntry)child).getEntry().getId();
/* 264 */       for (int i = 0; i < children.size(); i++) {
/* 265 */         Long idx = children.get(i);
/* 266 */         if (idx.longValue() == childId) return i; 
/*     */       } 
/* 268 */     } else if (parent instanceof V8HeapContainmentTreeTableModel.NamedEntry && child instanceof V8HeapContainmentTreeTableModel.NamedEntry) {
/* 269 */       return this.myReader.getChildIndex(((V8HeapContainmentTreeTableModel.NamedEntry)parent).getEntry(), ((V8HeapContainmentTreeTableModel.NamedEntry)child).getLinkOffset());
/*     */     } 
/*     */     
/* 272 */     return 0;
/*     */   }
/*     */   
/*     */   public void addTreeModelListener(TreeModelListener l) {}
/*     */   
/*     */   public void removeTreeModelListener(TreeModelListener l) {}
/*     */ }


/* Location:              C:\Program Files\JetBrains\WebStorm 2021.1\plugins\NodeJS\lib\NodeJS.jar!\com\jetbrains\nodejs\run\profile\heap\view\models\AggregatesTableModel.class
 * Java compiler version: 11 (55.0)
 * JD-Core Version:       1.1.3
 */