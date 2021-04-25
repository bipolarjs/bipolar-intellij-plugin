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
/*     */ import org.bipolar.run.profile.heap.data.V8HeapEdge;
/*     */ import org.bipolar.run.profile.heap.data.V8HeapEntry;
/*     */ import org.bipolar.run.profile.heap.view.nodes.FixedNodesListNode;
/*     */ import org.bipolar.run.profile.heap.view.nodes.FixedRetainerNode;
/*     */ import org.bipolar.run.profile.heap.view.renderers.DirectTreeTableRenderer;
/*     */ import java.util.ArrayList;
/*     */ import java.util.Collections;
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
/*     */ public class SearchDetailsTreeModel
/*     */   implements TreeTableModelWithCustomRenderer
/*     */ {
/*     */   private final ColumnInfo[] myColumns;
/*  47 */   private final Object myRoot = new Object();
/*     */   private final List<List<FixedRetainerNode>> myData;
/*     */   private final V8CachingReader myReader;
/*     */   
/*     */   public SearchDetailsTreeModel(final Project project, V8CachingReader reader, List<Pair<V8HeapEdge, V8HeapEntry>> nodes) {
/*  52 */     this.myReader = reader;
/*  53 */     this.myData = new ArrayList<>();
/*     */     
/*  55 */     this.myColumns = new ColumnInfo[1];
/*  56 */     this.myColumns[0] = new ColumnInfo<FixedNodesListNode, FixedNodesListNode>(NodeJSBundle.message("profile.search.table.column.object.name", new Object[0]))
/*     */       {
/*     */         @Nullable
/*     */         public FixedNodesListNode valueOf(FixedNodesListNode entry) {
/*  60 */           return entry;
/*     */         }
/*     */ 
/*     */         
/*     */         public TableCellRenderer getCustomizedRenderer(FixedNodesListNode o, TableCellRenderer renderer) {
/*  65 */           if (renderer instanceof TreeTableCellRenderer) {
/*  66 */             ((TreeTableCellRenderer)renderer).setCellRenderer((TreeCellRenderer)new DirectTreeTableRenderer(project, SearchDetailsTreeModel.this.myReader));
/*     */           }
/*  68 */           return super.getCustomizedRenderer(o, renderer);
/*     */         }
/*     */       };
/*  71 */     fillData(nodes);
/*     */   }
/*     */ 
/*     */   
/*     */   public TableCellRenderer getCustomizedRenderer(int column, Object o, TableCellRenderer renderer) {
/*  76 */     return this.myColumns[column].getCustomizedRenderer(o, renderer);
/*     */   }
/*     */ 
/*     */   
/*     */   public TreePath getPathByNode(@NotNull FixedNodesListNode node) {
/*  81 */     if (node == null) $$$reportNull$$$0(0);  List<FixedRetainerNode> nodes = this.myData.get(node.getVariantId());
/*  82 */     Object[] path = new Object[nodes.size() + 1];
/*  83 */     path[0] = new V8HeapContainmentTreeTableModel.NamedEntry(this.myReader.getNode(0L), "", "", -1L);
/*  84 */     for (int i = nodes.size() - 1; i >= 0; i--) {
/*  85 */       FixedNodesListNode pathElement = (FixedNodesListNode)nodes.get(i);
/*  86 */       path[nodes.size() - i] = pathElement;
/*     */     } 
/*  88 */     return new TreePath(path);
/*     */   }
/*     */   
/*     */   private void fillData(List<Pair<V8HeapEdge, V8HeapEntry>> nodes) {
/*  92 */     if (nodes == null)
/*  93 */       return;  for (int i = 0; i < nodes.size(); i++) {
/*  94 */       Pair<V8HeapEdge, V8HeapEntry> pair = nodes.get(i);
/*  95 */       V8HeapEntry heapEntry = (V8HeapEntry)pair.getSecond();
/*  96 */       V8HeapEdge heapEdge = (V8HeapEdge)pair.getFirst();
/*  97 */       List<FixedRetainerNode> nodeList = getChainToRoot(i, heapEntry, heapEdge, this.myReader);
/*  98 */       Collections.reverse(nodeList);
/*  99 */       for (int j = 0; j < nodeList.size(); j++) {
/* 100 */         FixedRetainerNode node = nodeList.get(j);
/* 101 */         node.setLevelNum(j);
/*     */       } 
/* 103 */       if (!nodeList.isEmpty()) {
/* 104 */         this.myData.add(nodeList);
/*     */       }
/*     */     } 
/*     */   }
/*     */   
/*     */   public boolean isEmpty() {
/* 110 */     return this.myData.isEmpty();
/*     */   } public static List<FixedRetainerNode> getChainToRoot(int variantId, V8HeapEntry heapEntry, V8HeapEdge heapEdge, V8CachingReader reader) {
/*     */     FixedRetainerNode first;
/*     */     long startWith;
/* 114 */     List<FixedRetainerNode> nodeList = new ArrayList<>();
/*     */ 
/*     */     
/* 117 */     if (heapEdge != null) {
/* 118 */       startWith = heapEdge.getFromIndex();
/* 119 */       V8HeapEntry parent = reader.getNode(startWith);
/*     */       
/* 121 */       first = new FixedRetainerNode(heapEntry, reader.getString(heapEntry.getNameId()), heapEdge.getPresentation(reader), heapEdge.getFileOffset(), variantId, 0, parent, reader.getString(parent.getNameId()));
/*     */     } else {
/* 123 */       startWith = reader.getNodeParent((int)heapEntry.getId());
/* 124 */       V8HeapEntry parent = reader.getNode(startWith);
/* 125 */       Pair<V8HeapEntry, V8HeapEdge> p = reader.getChildById(parent, heapEntry.getId());
/*     */       
/* 127 */       if (!reader.isShowHidden() && ((V8HeapEntry)p.getFirst()).getSnapshotObjectId() == -1L) {
/*     */         
/* 129 */         FixedRetainerNode retainerNode = new FixedRetainerNode(heapEntry, reader.getString(heapEntry.getNameId()), "", -1L, variantId, 0, null, "");
/*     */         
/* 131 */         retainerNode.setIsUnreachable(true);
/* 132 */         nodeList.add(retainerNode);
/* 133 */         return nodeList;
/*     */       } 
/*     */       
/* 136 */       first = new FixedRetainerNode(heapEntry, reader.getString(heapEntry.getNameId()), ((V8HeapEdge)p.getSecond()).getPresentation(reader), ((V8HeapEdge)p.getSecond()).getFileOffset(), variantId, 0, parent, reader.getString(parent.getNameId()));
/*     */     } 
/*     */     
/* 139 */     ArrayList<Long> idsInPath = new ArrayList<>();
/* 140 */     fillPath(startWith, idsInPath, reader);
/*     */     
/* 142 */     V8HeapEntry current = reader.getNode(0L);
/* 143 */     Collections.reverse(idsInPath);
/* 144 */     for (int j = 0; j < idsInPath.size(); j++) {
/* 145 */       Long pathElement = idsInPath.get(j);
/* 146 */       Pair<V8HeapEntry, V8HeapEdge> p = reader.getChildById(current, pathElement.longValue());
/* 147 */       if (!reader.isShowHidden() && ((V8HeapEntry)p.getFirst()).getSnapshotObjectId() == -1L) {
/* 148 */         nodeList.clear();
/* 149 */         nodeList.add(first);
/* 150 */         first.setIsUnreachable(true);
/* 151 */         return nodeList;
/*     */       } 
/* 153 */       nodeList.add(new FixedRetainerNode((V8HeapEntry)p.getFirst(), reader.getString(((V8HeapEntry)p.getFirst()).getNameId()), ((V8HeapEdge)p.getSecond()).getPresentation(reader), ((V8HeapEdge)p
/* 154 */             .getSecond()).getFileOffset(), variantId, -1, current, reader.getString(current.getNameId())));
/* 155 */       current = (V8HeapEntry)p.getFirst();
/*     */     } 
/*     */     
/* 158 */     nodeList.add(first);
/* 159 */     return nodeList;
/*     */   }
/*     */   
/*     */   private static void fillPath(long id, List<Long> path, V8CachingReader reader) {
/* 163 */     long curId = id;
/* 164 */     while (curId > 0L) {
/* 165 */       path.add(Long.valueOf(curId));
/* 166 */       curId = reader.getNodeParent((int)curId);
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   public int getColumnCount() {
/* 172 */     return this.myColumns.length;
/*     */   }
/*     */ 
/*     */   
/*     */   public String getColumnName(int column) {
/* 177 */     return this.myColumns[column].getName();
/*     */   }
/*     */ 
/*     */   
/*     */   public Class getColumnClass(int column) {
/* 182 */     return (column == 0) ? TreeTableModel.class : String.class;
/*     */   }
/*     */ 
/*     */   
/*     */   public Object getValueAt(Object node, int column) {
/* 187 */     return this.myColumns[column].valueOf(node);
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean isCellEditable(Object node, int column) {
/* 192 */     return false;
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
/* 205 */     return this.myRoot;
/*     */   }
/*     */ 
/*     */   
/*     */   public Object getChild(Object parent, int index) {
/* 210 */     if (this.myRoot.equals(parent))
/* 211 */       return ((List)this.myData.get(index)).get(0); 
/* 212 */     if (parent instanceof FixedNodesListNode) {
/* 213 */       FixedNodesListNode node = (FixedNodesListNode)parent;
/* 214 */       List<FixedRetainerNode> list = this.myData.get(node.getVariantId());
/* 215 */       return (list.size() == node.getLevelNum() + 1) ? null : list.get(node.getLevelNum() + 1);
/*     */     } 
/* 217 */     return null;
/*     */   }
/*     */ 
/*     */   
/*     */   public int getChildCount(Object parent) {
/* 222 */     if (this.myRoot.equals(parent))
/* 223 */       return this.myData.size(); 
/* 224 */     if (parent instanceof FixedNodesListNode) {
/* 225 */       FixedNodesListNode node = (FixedNodesListNode)parent;
/* 226 */       List<FixedRetainerNode> list = this.myData.get(node.getVariantId());
/* 227 */       return (list.size() == node.getLevelNum() + 1) ? 0 : 1;
/*     */     } 
/* 229 */     return 0;
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean isLeaf(Object node) {
/* 234 */     return (getChildCount(node) == 0);
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public void valueForPathChanged(TreePath path, Object newValue) {}
/*     */ 
/*     */   
/*     */   public int getIndexOfChild(Object parent, Object child) {
/* 243 */     if (this.myRoot.equals(parent)) {
/* 244 */       for (int i = 0; i < this.myData.size(); i++) {
/* 245 */         List<FixedRetainerNode> nodes = this.myData.get(i);
/* 246 */         if (((FixedRetainerNode)nodes.get(0)).equals(child)) return i; 
/*     */       } 
/* 248 */     } else if (parent instanceof FixedNodesListNode && child instanceof FixedNodesListNode) {
/* 249 */       return 0;
/*     */     } 
/* 251 */     return 0;
/*     */   }
/*     */   
/*     */   public void addTreeModelListener(TreeModelListener l) {}
/*     */   
/*     */   public void removeTreeModelListener(TreeModelListener l) {}
/*     */ }


/* Location:              C:\Program Files\JetBrains\WebStorm 2021.1\plugins\NodeJS\lib\NodeJS.jar!\com\jetbrains\nodejs\run\profile\heap\view\models\SearchDetailsTreeModel.class
 * Java compiler version: 11 (55.0)
 * JD-Core Version:       1.1.3
 */