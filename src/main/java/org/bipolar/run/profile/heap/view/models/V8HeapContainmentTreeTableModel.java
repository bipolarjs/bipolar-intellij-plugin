/*     */ package org.bipolar.run.profile.heap.view.models;
/*     */ 
/*     */ import com.intellij.openapi.project.Project;
/*     */ import com.intellij.openapi.util.Pair;
/*     */ import com.intellij.ui.ColoredTreeCellRenderer;
/*     */ import com.intellij.ui.treeStructure.treetable.TreeTableCellRenderer;
/*     */ import com.intellij.ui.treeStructure.treetable.TreeTableModel;
/*     */ import com.intellij.util.ui.ColumnInfo;
/*     */ import org.bipolar.NodeJSBundle;
/*     */ import org.bipolar.run.profile.cpu.view.TreeTableModelWithCustomRenderer;
/*     */ import org.bipolar.run.profile.heap.V8CachingReader;
/*     */ import org.bipolar.run.profile.heap.data.V8HeapEdge;
/*     */ import org.bipolar.run.profile.heap.data.V8HeapEntry;
/*     */ import org.bipolar.run.profile.heap.view.renderers.DirectTreeTableRenderer;
/*     */ import org.bipolar.run.profile.heap.view.renderers.RightAlignedRenderer;
/*     */ import org.bipolar.run.profile.heap.view.renderers.SizeRenderer;
/*     */ import javax.swing.JTree;
/*     */ import javax.swing.event.TreeModelListener;
/*     */ import javax.swing.table.TableCellRenderer;
/*     */ import javax.swing.tree.TreeCellRenderer;
/*     */ import javax.swing.tree.TreePath;
/*     */
import org.jetbrains.annotations.Nls;
/*     */ import org.jetbrains.annotations.NotNull;
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
/*     */ public class V8HeapContainmentTreeTableModel
/*     */   implements TreeTableModelWithCustomRenderer
/*     */ {
/*     */   private final ColumnInfo[] myColumns;
/*     */   private final NamedEntry myRoot;
/*     */   @NotNull
/*     */   protected final V8CachingReader myReader;
/*     */   private ColoredTreeCellRenderer myRenderer;
/*     */   
/*     */   public V8HeapContainmentTreeTableModel(Project project, @NotNull V8CachingReader reader) {
/*  52 */     this.myReader = reader;
/*  53 */     this.myRoot = new NamedEntry(reader.getNode(0L), reader.getString(0L), "", -1L);
/*  54 */     this.myRenderer = (ColoredTreeCellRenderer)new DirectTreeTableRenderer(project, this.myReader);
/*  55 */     this.myColumns = new ColumnInfo[4];
/*  56 */     fillColumns(this.myColumns, this.myReader);
/*     */   }
/*     */   
/*     */   private void fillColumns(ColumnInfo[] columns, final V8CachingReader reader) {
/*  60 */     columns[0] = new ColumnInfo<NamedEntry, NamedEntry>(NodeJSBundle.message("profile.heap.containment_table.column.object.name", new Object[0]))
/*     */       {
/*     */         @Nullable
/*     */         public V8HeapContainmentTreeTableModel.NamedEntry valueOf(V8HeapContainmentTreeTableModel.NamedEntry entry) {
/*  64 */           return entry;
/*     */         }
/*     */ 
/*     */         
/*     */         public TableCellRenderer getCustomizedRenderer(V8HeapContainmentTreeTableModel.NamedEntry o, TableCellRenderer renderer) {
/*  69 */           if (renderer instanceof TreeTableCellRenderer) {
/*  70 */             ((TreeTableCellRenderer)renderer).setCellRenderer((TreeCellRenderer)V8HeapContainmentTreeTableModel.this.myRenderer);
/*     */           }
/*  72 */           return super.getCustomizedRenderer(o, renderer);
/*     */         }
/*     */       };
/*  75 */     final RightAlignedRenderer alignedRenderer = new RightAlignedRenderer();
/*  76 */     columns[1] = new ColumnInfo<NamedEntry, String>(NodeJSBundle.message("profile.heap.containment_table.column.distance.name", new Object[0])) {
/*     */         @NotNull
/*     */         public String valueOf(V8HeapContainmentTreeTableModel.NamedEntry entry) {
/*  79 */           int distance = reader.getDistance((int)entry.getEntry().getId());
/*  80 */           if (((distance >= 100000000) ? "-" : String.valueOf(distance)) == null) $$$reportNull$$$0(0);  return (distance >= 100000000) ? "-" : String.valueOf(distance);
/*     */         }
/*     */ 
/*     */         
/*     */         public TableCellRenderer getCustomizedRenderer(V8HeapContainmentTreeTableModel.NamedEntry o, TableCellRenderer renderer) {
/*  85 */           return (TableCellRenderer)alignedRenderer;
/*     */         }
/*     */       };
/*  88 */     final SizeRenderer sizeRenderer = new SizeRenderer(reader.getRetainedSize(0));
/*  89 */     columns[2] = new ColumnInfo<NamedEntry, Object>(NodeJSBundle.message("profile.heap.containment_table.column.shallow_size.name", new Object[0])) {
/*     */         @NotNull
/*     */         public Object valueOf(V8HeapContainmentTreeTableModel.NamedEntry entry) {
/*  92 */           if (Long.valueOf(entry.getEntry().getSize()) == null) $$$reportNull$$$0(0);  return Long.valueOf(entry.getEntry().getSize());
/*     */         }
/*     */ 
/*     */         
/*     */         public TableCellRenderer getCustomizedRenderer(V8HeapContainmentTreeTableModel.NamedEntry o, TableCellRenderer renderer) {
/*  97 */           return (TableCellRenderer)sizeRenderer;
/*     */         }
/*     */       };
/* 100 */     columns[3] = new ColumnInfo<NamedEntry, Object>(NodeJSBundle.message("profile.heap.containment_table.column.retained_size.name", new Object[0])) {
/*     */         @NotNull
/*     */         public Object valueOf(V8HeapContainmentTreeTableModel.NamedEntry entry) {
/* 103 */           if (Long.valueOf(reader.getRetainedSize((int)entry.getEntry().getId())) == null) $$$reportNull$$$0(0);  return Long.valueOf(reader.getRetainedSize((int)entry.getEntry().getId()));
/*     */         }
/*     */ 
/*     */         
/*     */         public TableCellRenderer getCustomizedRenderer(V8HeapContainmentTreeTableModel.NamedEntry o, TableCellRenderer renderer) {
/* 108 */           return (TableCellRenderer)sizeRenderer;
/*     */         }
/*     */       };
/*     */   }
/*     */   
/*     */   public void setNodeRenderer(ColoredTreeCellRenderer renderer) {
/* 114 */     this.myRenderer = renderer;
/*     */   }
/*     */ 
/*     */   
/*     */   public TableCellRenderer getCustomizedRenderer(int column, Object o, TableCellRenderer renderer) {
/* 119 */     return this.myColumns[column].getCustomizedRenderer(o, renderer);
/*     */   }
/*     */ 
/*     */   
/*     */   public int getColumnCount() {
/* 124 */     return this.myColumns.length;
/*     */   }
/*     */ 
/*     */   
/*     */   public String getColumnName(int column) {
/* 129 */     return this.myColumns[column].getName();
/*     */   }
/*     */ 
/*     */   
/*     */   public Class getColumnClass(int column) {
/* 134 */     return (column == 0) ? TreeTableModel.class : String.class;
/*     */   }
/*     */ 
/*     */   
/*     */   public Object getValueAt(Object node, int column) {
/* 139 */     return this.myColumns[column].valueOf(node);
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean isCellEditable(Object node, int column) {
/* 144 */     return false;
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
/* 157 */     return this.myRoot;
/*     */   }
/*     */ 
/*     */   
/*     */   public Object getChild(Object parent, int index) {
/* 162 */     if (parent instanceof NamedEntry) {
/* 163 */       Pair<V8HeapEntry, V8HeapEdge> pair = this.myReader.getChild(((NamedEntry)parent).getEntry(), index);
/* 164 */       V8HeapEdge edge = (V8HeapEdge)pair.getSecond();
/* 165 */       return new NamedEntry((V8HeapEntry)pair.getFirst(), this.myReader.getString(((V8HeapEntry)pair.getFirst()).getNameId()), edge.getPresentation(this.myReader), edge.getFileOffset());
/*     */     } 
/* 167 */     return null;
/*     */   }
/*     */ 
/*     */   
/*     */   public int getChildCount(Object parent) {
/* 172 */     if (parent instanceof NamedEntry) {
/* 173 */       return this.myReader.getChildren(((NamedEntry)parent).getEntry()).size();
/*     */     }
/* 175 */     return 0;
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean isLeaf(Object node) {
/* 180 */     return (getChildCount(node) == 0);
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public void valueForPathChanged(TreePath path, Object newValue) {}
/*     */ 
/*     */   
/*     */   public int getIndexOfChild(Object parent, Object child) {
/* 189 */     if (parent instanceof NamedEntry && child instanceof NamedEntry) {
/* 190 */       return this.myReader.getChildIndex(((NamedEntry)parent).getEntry(), ((NamedEntry)child).getLinkOffset());
/*     */     }
/* 192 */     return 0;
/*     */   }
/*     */   
/*     */   public void addTreeModelListener(TreeModelListener l) {}
/*     */   
/*     */   public void removeTreeModelListener(TreeModelListener l) {}
/*     */   
/*     */   public static class NamedEntry
/*     */   {
/*     */     private final V8HeapEntry myEntry;
/*     */     @NotNull
/*     */     @Nls
/*     */     private final String myLinkPresentation;
/*     */     private final long myLinkOffset;
/*     */     @Nls
/*     */     private final String myName;
/*     */     private final boolean myLinkHidden;
/*     */     private final boolean myIsProperty;
/*     */     private boolean myDoNotShowLink;
/*     */     
/*     */     public static NamedEntry createWithoutLink(long nodeId, V8CachingReader reader) {
/* 213 */       NamedEntry entry = create(nodeId, reader);
/* 214 */       entry.setDoNotShowLink(true);
/* 215 */       return entry;
/*     */     }
/*     */     
/*     */     public static NamedEntry create(long nodeId, V8CachingReader reader) {
/* 219 */       V8HeapEntry node = reader.getNode(nodeId);
/* 220 */       return new NamedEntry(node, reader.getString(node.getNameId()), "", -1L);
/*     */     }
/*     */     
/*     */     public static NamedEntry create(@NotNull V8HeapEdge edge, V8CachingReader reader) {
/* 224 */       if (edge == null) $$$reportNull$$$0(0);  V8HeapEntry entry = reader.getNode(edge.getToIndex());
/* 225 */       return new NamedEntry(entry, reader.getString(entry.getNameId()), edge.getPresentation(reader), edge.getFileOffset());
/*     */     }
/*     */     
/*     */     public NamedEntry(V8HeapEntry entry, @Nls String name, @NotNull @Nls String linkPresentation, long offset) {
/* 229 */       this.myEntry = entry;
/* 230 */       this.myName = name;
/* 231 */       this.myLinkOffset = offset;
/* 232 */       this.myLinkHidden = (linkPresentation.startsWith("{") && linkPresentation.endsWith("}"));
/* 233 */       this
/* 234 */         .myIsProperty = (linkPresentation.startsWith(".") || (linkPresentation.startsWith("[\"") && linkPresentation.endsWith("\"]")) || (linkPresentation.startsWith("[") && linkPresentation.endsWith("]")));
/* 235 */       if (this.myLinkHidden) {
/* 236 */         this.myLinkPresentation = linkPresentation.substring(1, linkPresentation.length() - 1);
/* 237 */       } else if (this.myIsProperty) {
/* 238 */         if (linkPresentation.startsWith(".")) {
/* 239 */           this.myLinkPresentation = linkPresentation.substring(1);
/* 240 */         } else if (linkPresentation.startsWith("[\"")) {
/* 241 */           this.myLinkPresentation = linkPresentation.substring(2, linkPresentation.length() - 2);
/*     */         } else {
/* 243 */           this.myLinkPresentation = linkPresentation;
/*     */         } 
/*     */       } else {
/* 246 */         this.myLinkPresentation = linkPresentation;
/*     */       } 
/*     */     }
/*     */     
/*     */     protected NamedEntry(@NotNull NamedEntry entry) {
/* 251 */       this.myEntry = entry.getEntry();
/* 252 */       this.myLinkPresentation = entry.getLinkPresentation();
/* 253 */       this.myLinkOffset = entry.getLinkOffset();
/* 254 */       this.myName = entry.getName();
/* 255 */       this.myLinkHidden = entry.isLinkHidden();
/* 256 */       this.myIsProperty = entry.isProperty();
/*     */     }
/*     */     
/*     */     public boolean isDoNotShowLink() {
/* 260 */       return this.myDoNotShowLink;
/*     */     }
/*     */     
/*     */     public void setDoNotShowLink(boolean doNotShowLink) {
/* 264 */       this.myDoNotShowLink = doNotShowLink;
/*     */     }
/*     */     
/*     */     public long getLinkOffset() {
/* 268 */       return this.myLinkOffset;
/*     */     }
/*     */     
/*     */     public V8HeapEntry getEntry() {
/* 272 */       return this.myEntry;
/*     */     }
/*     */     
/*     */     public String toString() {
/* 276 */       return this.myLinkPresentation + " :: " + this.myLinkPresentation + " @" + this.myName;
/*     */     }
/*     */     @NotNull
/*     */     @Nls
/*     */     public String getLinkPresentation() {
/* 281 */       if (this.myLinkPresentation == null) $$$reportNull$$$0(3);  return this.myLinkPresentation;
/*     */     }
/*     */     
/*     */     public boolean isLinkHidden() {
/* 285 */       return this.myLinkHidden;
/*     */     }
/*     */     
/*     */     public boolean isProperty() {
/* 289 */       return this.myIsProperty;
/*     */     }
/*     */     @Nls
/*     */     public String getName() {
/* 293 */       return this.myName;
/*     */     }
/*     */ 
/*     */     
/*     */     public boolean equals(Object o) {
/* 298 */       if (this == o) return true; 
/* 299 */       if (o == null || getClass() != o.getClass()) return false;
/*     */       
/* 301 */       NamedEntry entry = (NamedEntry)o;
/*     */       
/* 303 */       if (this.myLinkOffset != entry.myLinkOffset) return false; 
/* 304 */       if ((this.myEntry != null) ? !this.myEntry.equals(entry.myEntry) : (entry.myEntry != null)) return false;
/*     */       
/* 306 */       return true;
/*     */     }
/*     */ 
/*     */     
/*     */     public int hashCode() {
/* 311 */       int result = (this.myEntry != null) ? this.myEntry.hashCode() : 0;
/* 312 */       result = 31 * result + (int)(this.myLinkOffset ^ this.myLinkOffset >>> 32L);
/* 313 */       return result;
/*     */     }
/*     */   }
/*     */ }


/* Location:              C:\Program Files\JetBrains\WebStorm 2021.1\plugins\NodeJS\lib\NodeJS.jar!\com\jetbrains\nodejs\run\profile\heap\view\models\V8HeapContainmentTreeTableModel.class
 * Java compiler version: 11 (55.0)
 * JD-Core Version:       1.1.3
 */