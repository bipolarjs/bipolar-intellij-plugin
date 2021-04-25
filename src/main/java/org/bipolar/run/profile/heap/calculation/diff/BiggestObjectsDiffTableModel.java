/*     */ package org.bipolar.run.profile.heap.calculation.diff;
/*     */ 
/*     */ import com.intellij.openapi.project.Project;
/*     */ import com.intellij.ui.treeStructure.treetable.TreeTableCellRenderer;
/*     */ import com.intellij.ui.treeStructure.treetable.TreeTableModel;
/*     */ import com.intellij.util.BeforeAfter;
/*     */ import com.intellij.util.ui.ColumnInfo;
/*     */ import org.bipolar.NodeJSBundle;
/*     */ import org.bipolar.run.profile.cpu.view.TreeTableModelWithCustomRenderer;
/*     */ import org.bipolar.run.profile.heap.view.models.V8HeapContainmentTreeTableModel;
/*     */ import org.bipolar.run.profile.heap.view.renderers.RightAlignedRenderer;
/*     */ import org.bipolar.run.profile.heap.view.renderers.SizeRenderer;
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
/*     */ public class BiggestObjectsDiffTableModel
/*     */   implements TreeTableModelWithCustomRenderer
/*     */ {
/*  26 */   private final Object myRoot = new Object();
/*     */   
/*     */   private final ColumnInfo[] myColumns;
/*     */   @NotNull
/*     */   private final Project myProject;
/*     */   
/*     */   public BiggestObjectsDiffTableModel(@NotNull Project project, @NotNull V8DiffCachingReader reader) {
/*  33 */     this.myProject = project;
/*  34 */     this.myReader = reader;
/*  35 */     this.myColumns = new ColumnInfo[5];
/*  36 */     fillColumns();
/*     */   } @NotNull
/*     */   private final V8DiffCachingReader myReader; private DiffCellRenderer myRenderer;
/*     */   private void fillColumns() {
/*  40 */     this.myRenderer = new DiffCellRenderer(this.myProject, this.myReader.getBaseReader(), this.myReader.getChangedReader(), AggregateDifferenceEmphasizerI.EMPTY);
/*     */     
/*  42 */     final RightAlignedRenderer rightAlignedRenderer = new RightAlignedRenderer();
/*  43 */     this.myColumns[0] = new ColumnInfo<Object, Object>(NodeJSBundle.message("profile.biggest_objects.diff.table.column.object.name", new Object[0]))
/*     */       {
/*     */         @Nullable
/*     */         public Object valueOf(Object o) {
/*  47 */           return o;
/*     */         }
/*     */ 
/*     */         
/*     */         public TableCellRenderer getCustomizedRenderer(Object o, TableCellRenderer renderer) {
/*  52 */           if (renderer instanceof TreeTableCellRenderer) {
/*  53 */             ((TreeTableCellRenderer)renderer).setCellRenderer((TreeCellRenderer)BiggestObjectsDiffTableModel.this.myRenderer);
/*     */           }
/*  55 */           return super.getCustomizedRenderer(o, renderer);
/*     */         }
/*     */       };
/*  58 */     this.myColumns[1] = new ColumnInfo<Object, Object>(NodeJSBundle.message("profile.biggest_objects.diff.table.column.size_diff.name", new Object[0]))
/*     */       {
/*     */         @Nullable
/*     */         public Object valueOf(Object o) {
/*  62 */           if (o instanceof BeforeAfter) {
/*  63 */             BeforeAfter<V8HeapContainmentTreeTableModel.NamedEntry> beforeAfter = (BeforeAfter<V8HeapContainmentTreeTableModel.NamedEntry>)o;
/*     */             
/*  65 */             if (beforeAfter.getBefore() == null) {
/*  66 */               return "+" + SizeRenderer.formatSize(((V8HeapContainmentTreeTableModel.NamedEntry)beforeAfter.getAfter()).getEntry().getSize());
/*     */             }
/*  68 */             if (beforeAfter.getAfter() == null) {
/*  69 */               return "-" + SizeRenderer.formatSize(((V8HeapContainmentTreeTableModel.NamedEntry)beforeAfter.getBefore()).getEntry().getSize());
/*     */             }
/*     */             
/*  72 */             long diff = ((V8HeapContainmentTreeTableModel.NamedEntry)beforeAfter.getAfter()).getEntry().getSize() - ((V8HeapContainmentTreeTableModel.NamedEntry)beforeAfter.getBefore()).getEntry().getSize();
/*  73 */             return BiggestObjectsDiffTableModel.this.sign(diff) + BiggestObjectsDiffTableModel.this.sign(diff);
/*     */           } 
/*  75 */           return null;
/*     */         }
/*     */ 
/*     */         
/*     */         public TableCellRenderer getCustomizedRenderer(Object o, TableCellRenderer renderer) {
/*  80 */           return (TableCellRenderer)rightAlignedRenderer;
/*     */         }
/*     */       };
/*  83 */     this.myColumns[2] = new ColumnInfo<Object, Object>(NodeJSBundle.message("profile.biggest_objects.diff.table.column.size.name", new Object[0]))
/*     */       {
/*     */         @Nullable
/*     */         public Object valueOf(Object o) {
/*  87 */           if (o instanceof BeforeAfter) {
/*  88 */             BeforeAfter<V8HeapContainmentTreeTableModel.NamedEntry> beforeAfter = (BeforeAfter<V8HeapContainmentTreeTableModel.NamedEntry>)o;
/*     */             
/*  90 */             long afterSize = (beforeAfter.getAfter() == null) ? 0L : ((V8HeapContainmentTreeTableModel.NamedEntry)beforeAfter.getAfter()).getEntry().getSize();
/*  91 */             long beforeSize = (beforeAfter.getBefore() == null) ? 0L : ((V8HeapContainmentTreeTableModel.NamedEntry)beforeAfter.getBefore()).getEntry().getSize();
/*  92 */             if (beforeAfter.getBefore() == null) {
/*  93 */               return "+" + SizeRenderer.formatSize(afterSize);
/*     */             }
/*  95 */             if (beforeAfter.getAfter() == null) {
/*  96 */               return "-" + SizeRenderer.formatSize(beforeSize);
/*     */             }
/*     */             
/*  99 */             return (beforeSize == afterSize) ? SizeRenderer.formatSize(beforeSize) : (
/* 100 */               SizeRenderer.formatSize(beforeSize) + "->" + SizeRenderer.formatSize(beforeSize));
/*     */           } 
/* 102 */           return null;
/*     */         }
/*     */ 
/*     */         
/*     */         public TableCellRenderer getCustomizedRenderer(Object o, TableCellRenderer renderer) {
/* 107 */           return (TableCellRenderer)rightAlignedRenderer;
/*     */         }
/*     */       };
/* 110 */     this.myColumns[3] = new ColumnInfo<Object, Object>(NodeJSBundle.message("profile.biggest_objects.diff.table.column.retained_size_diff.name", new Object[0]))
/*     */       {
/*     */         @Nullable
/*     */         public Object valueOf(Object o) {
/* 114 */           if (o instanceof BeforeAfter) {
/* 115 */             BeforeAfter<V8HeapContainmentTreeTableModel.NamedEntry> beforeAfter = (BeforeAfter<V8HeapContainmentTreeTableModel.NamedEntry>)o;
/*     */             
/* 117 */             if (beforeAfter.getBefore() == null) {
/* 118 */               return "+" + SizeRenderer.formatSize(BiggestObjectsDiffTableModel.this.myReader.getChangedReader().getRetainedSize((int)((V8HeapContainmentTreeTableModel.NamedEntry)beforeAfter.getAfter()).getEntry().getId()));
/*     */             }
/* 120 */             if (beforeAfter.getAfter() == null) {
/* 121 */               return "-" + SizeRenderer.formatSize(BiggestObjectsDiffTableModel.this.myReader.getBaseReader().getRetainedSize((int)((V8HeapContainmentTreeTableModel.NamedEntry)beforeAfter.getBefore()).getEntry().getId()));
/*     */             }
/*     */ 
/*     */             
/* 125 */             long diff = BiggestObjectsDiffTableModel.this.myReader.getChangedReader().getRetainedSize((int)((V8HeapContainmentTreeTableModel.NamedEntry)beforeAfter.getAfter()).getEntry().getId()) - BiggestObjectsDiffTableModel.this.myReader.getBaseReader().getRetainedSize((int)((V8HeapContainmentTreeTableModel.NamedEntry)beforeAfter.getBefore()).getEntry().getId());
/* 126 */             return BiggestObjectsDiffTableModel.this.sign(diff) + BiggestObjectsDiffTableModel.this.sign(diff);
/*     */           } 
/* 128 */           return null;
/*     */         }
/*     */ 
/*     */         
/*     */         public TableCellRenderer getCustomizedRenderer(Object o, TableCellRenderer renderer) {
/* 133 */           return (TableCellRenderer)rightAlignedRenderer;
/*     */         }
/*     */       };
/* 136 */     this.myColumns[4] = new ColumnInfo<Object, Object>(NodeJSBundle.message("profile.biggest_objects.diff.table.column.retained_size.name", new Object[0]))
/*     */       {
/*     */         @Nullable
/*     */         public Object valueOf(Object o) {
/* 140 */           if (o instanceof BeforeAfter) {
/* 141 */             BeforeAfter<V8HeapContainmentTreeTableModel.NamedEntry> beforeAfter = (BeforeAfter<V8HeapContainmentTreeTableModel.NamedEntry>)o;
/*     */             
/* 143 */             long afterSize = (beforeAfter.getAfter() == null) ? 0L : BiggestObjectsDiffTableModel.this.myReader.getChangedReader().getRetainedSize((int)((V8HeapContainmentTreeTableModel.NamedEntry)beforeAfter.getAfter()).getEntry().getId());
/* 144 */             long beforeSize = (beforeAfter.getBefore() == null) ? 0L : BiggestObjectsDiffTableModel.this.myReader.getBaseReader().getRetainedSize((int)((V8HeapContainmentTreeTableModel.NamedEntry)beforeAfter.getBefore()).getEntry().getId());
/*     */             
/* 146 */             if (beforeAfter.getBefore() == null) {
/* 147 */               return "+" + SizeRenderer.formatSize(afterSize);
/*     */             }
/* 149 */             if (beforeAfter.getAfter() == null) {
/* 150 */               return "-" + SizeRenderer.formatSize(beforeSize);
/*     */             }
/*     */             
/* 153 */             return (beforeSize == afterSize) ? SizeRenderer.formatSize(beforeSize) : (
/* 154 */               SizeRenderer.formatSize(beforeSize) + "->" + SizeRenderer.formatSize(beforeSize));
/*     */           } 
/* 156 */           return null;
/*     */         }
/*     */ 
/*     */         
/*     */         public TableCellRenderer getCustomizedRenderer(Object o, TableCellRenderer renderer) {
/* 161 */           return (TableCellRenderer)rightAlignedRenderer;
/*     */         }
/*     */       };
/*     */   }
/*     */   
/*     */   private String sign(long value) {
/* 167 */     return (value == 0L) ? " " : ((value < 0L) ? "-" : "+");
/*     */   }
/*     */ 
/*     */   
/*     */   public TableCellRenderer getCustomizedRenderer(int column, Object o, TableCellRenderer renderer) {
/* 172 */     return this.myColumns[column].getCustomizedRenderer(o, renderer);
/*     */   }
/*     */ 
/*     */   
/*     */   public int getColumnCount() {
/* 177 */     return this.myColumns.length;
/*     */   }
/*     */ 
/*     */   
/*     */   public String getColumnName(int column) {
/* 182 */     return this.myColumns[column].getName();
/*     */   }
/*     */ 
/*     */   
/*     */   public Class getColumnClass(int column) {
/* 187 */     return (column == 0) ? TreeTableModel.class : String.class;
/*     */   }
/*     */ 
/*     */   
/*     */   public Object getValueAt(Object node, int column) {
/* 192 */     return this.myColumns[column].valueOf(node);
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean isCellEditable(Object node, int column) {
/* 197 */     return false;
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
/* 210 */     return this.myRoot;
/*     */   }
/*     */ 
/*     */   
/*     */   public Object getChild(Object parent, int index) {
/* 215 */     if (parent == this.myRoot) {
/* 216 */       return this.myReader.getBiggestObjectsDiff().get(index);
/*     */     }
/* 218 */     return null;
/*     */   }
/*     */ 
/*     */   
/*     */   public int getChildCount(Object parent) {
/* 223 */     if (parent == this.myRoot) {
/* 224 */       return this.myReader.getBiggestObjectsDiff().size();
/*     */     }
/* 226 */     return 0;
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean isLeaf(Object node) {
/* 231 */     return (node != this.myRoot);
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public void valueForPathChanged(TreePath path, Object newValue) {}
/*     */ 
/*     */ 
/*     */   
/*     */   public int getIndexOfChild(Object parent, Object child) {
/* 241 */     if (parent == this.myRoot) {
/* 242 */       List<BeforeAfter<V8HeapContainmentTreeTableModel.NamedEntry>> list = this.myReader.getBiggestObjectsDiff();
/* 243 */       for (int i = 0; i < list.size(); i++) {
/* 244 */         BeforeAfter<V8HeapContainmentTreeTableModel.NamedEntry> beforeAfter = list.get(i);
/* 245 */         if (beforeAfter.equals(child)) return i; 
/*     */       } 
/* 247 */       return 0;
/*     */     } 
/* 249 */     return 0;
/*     */   }
/*     */   
/*     */   public void addTreeModelListener(TreeModelListener l) {}
/*     */   
/*     */   public void removeTreeModelListener(TreeModelListener l) {}
/*     */ }


/* Location:              C:\Program Files\JetBrains\WebStorm 2021.1\plugins\NodeJS\lib\NodeJS.jar!\com\jetbrains\nodejs\run\profile\heap\calculation\diff\BiggestObjectsDiffTableModel.class
 * Java compiler version: 11 (55.0)
 * JD-Core Version:       1.1.3
 */