/*     */ package org.bipolar.run.profile.heap.view.models;
/*     */ 
/*     */ import com.intellij.openapi.project.Project;
/*     */ import com.intellij.openapi.util.Pair;
/*     */ import com.intellij.ui.ColoredTreeCellRenderer;
/*     */ import com.intellij.ui.treeStructure.treetable.TreeTableCellRenderer;
/*     */ import com.intellij.ui.treeStructure.treetable.TreeTableModel;
/*     */ import com.intellij.util.ArrayUtil;
/*     */ import com.intellij.util.ui.ColumnInfo;
/*     */ import org.bipolar.NodeJSBundle;
/*     */ import org.bipolar.run.profile.cpu.view.TreeTableModelWithCustomRenderer;
/*     */ import org.bipolar.run.profile.heap.V8CachingReader;
/*     */ import org.bipolar.run.profile.heap.data.V8HeapEdge;
/*     */ import org.bipolar.run.profile.heap.data.V8HeapEntry;
/*     */ import org.bipolar.run.profile.heap.view.components.V8HeapTreeTable;
/*     */ import org.bipolar.run.profile.heap.view.nodes.FixedNodesListNode;
/*     */ import org.bipolar.run.profile.heap.view.nodes.FixedRetainerNode;
/*     */ import org.bipolar.run.profile.heap.view.renderers.DirectTreeTableRenderer;
/*     */ import org.bipolar.run.profile.heap.view.renderers.RightAlignedRenderer;
/*     */ import org.bipolar.run.profile.heap.view.renderers.SizeRenderer;
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
/*     */ 
/*     */ public class RetainersTreeModel
/*     */   implements TreeTableModelWithCustomRenderer
/*     */ {
/*     */   private final ColumnInfo[] myColumns;
/*  53 */   private final Object myRoot = new Object();
/*  54 */   public String myDescription = "Description: ";
/*     */   
/*     */   public List<String> myDescriptionDetails;
/*     */   public static final String ROOT_CHAIN = "Chain from root:";
/*     */   private static final String RETAINERS = "Retainers:";
/*     */   private final List<String> mySecondLevel;
/*     */   private final List<List<FixedRetainerNode>> myData;
/*     */   private final List<FixedNodesListNode> myFromRootChain;
/*     */   private final Project myProject;
/*     */   private final V8CachingReader myReader;
/*     */   private final V8HeapEntry myMain;
/*     */   private final V8HeapEdge myMainEdge;
/*     */   
/*     */   public RetainersTreeModel(Project project, V8CachingReader reader, V8HeapEntry main, V8HeapEdge mainEdge, List<Pair<V8HeapEntry, V8HeapEdge>> data, Object[] pathFromRoot) {
/*  68 */     this.myProject = project;
/*  69 */     this.myReader = reader;
/*  70 */     this.myMain = main;
/*  71 */     this.myMainEdge = mainEdge;
/*  72 */     this.myColumns = new ColumnInfo[4];
/*  73 */     fillColumns(this.myProject, this.myColumns, this.myReader, null);
/*  74 */     this.myData = new ArrayList<>();
/*  75 */     this.myFromRootChain = new ArrayList<>();
/*  76 */     fillData(main, mainEdge, data, pathFromRoot);
/*  77 */     this.mySecondLevel = new ArrayList<>();
/*  78 */     createDescription();
/*  79 */     this.mySecondLevel.add(this.myDescription);
/*  80 */     this.mySecondLevel.add("Chain from root:");
/*  81 */     this.mySecondLevel.add("Retainers:");
/*     */   }
/*     */   
/*     */   private void createDescription() {
/*  85 */     this.myDescriptionDetails = new ArrayList<>(2);
/*  86 */     if (this.myMainEdge != null) {
/*  87 */       this.myDescription = this.myDescription + " link: " + this.myDescription + " /";
/*  88 */       this.myDescriptionDetails.add("Link: " + this.myMainEdge.getType().getDescription());
/*     */     } 
/*  90 */     this.myDescription = this.myDescription + " object: " + this.myDescription;
/*  91 */     this.myDescriptionDetails.add("Object: " + this.myMain.getType().getDescription());
/*     */   }
/*     */   
/*     */   public static void fillColumns(Project project, ColumnInfo[] columns, final V8CachingReader reader, ColoredTreeCellRenderer renderer) {
/*  95 */     final ColoredTreeCellRenderer firstCellRenderer = (renderer == null) ? (ColoredTreeCellRenderer)new DirectTreeTableRenderer(project, reader) : renderer;
/*  96 */     columns[0] = new ColumnInfo<Object, Object>(NodeJSBundle.message("profile.retained.table.column.object.name", new Object[0]))
/*     */       {
/*     */         @Nullable
/*     */         public Object valueOf(Object entry) {
/* 100 */           return entry;
/*     */         }
/*     */ 
/*     */         
/*     */         public TableCellRenderer getCustomizedRenderer(Object o, TableCellRenderer renderer) {
/* 105 */           if (renderer instanceof TreeTableCellRenderer) {
/* 106 */             ((TreeTableCellRenderer)renderer).setCellRenderer((TreeCellRenderer)firstCellRenderer);
/*     */           }
/* 108 */           return super.getCustomizedRenderer(o, renderer);
/*     */         }
/*     */       };
/* 111 */     final RightAlignedRenderer alignedRenderer = new RightAlignedRenderer();
/* 112 */     columns[1] = new ColumnInfo<Object, String>(NodeJSBundle.message("profile.retained.table.column.distance.name", new Object[0])) {
/*     */         @NotNull
/*     */         public String valueOf(Object entry) {
/* 115 */           if (entry instanceof V8HeapContainmentTreeTableModel.NamedEntry) {
/* 116 */             int distance = reader.getDistance((int)((V8HeapContainmentTreeTableModel.NamedEntry)entry).getEntry().getId());
/* 117 */             if (((distance >= 100000000) ? "-" : String.valueOf(distance)) == null) $$$reportNull$$$0(0);  return (distance >= 100000000) ? "-" : String.valueOf(distance);
/*     */           } 
/* 119 */           return "";
/*     */         }
/*     */ 
/*     */         
/*     */         public TableCellRenderer getCustomizedRenderer(Object o, TableCellRenderer renderer) {
/* 124 */           return (TableCellRenderer)alignedRenderer;
/*     */         }
/*     */       };
/* 127 */     final SizeRenderer sizeRenderer = new SizeRenderer(reader.getRetainedSize(0));
/* 128 */     columns[2] = new ColumnInfo<Object, Object>(NodeJSBundle.message("profile.retained.table.column.shallow_size.name", new Object[0])) {
/*     */         @NotNull
/*     */         public Object valueOf(Object entry) {
/* 131 */           if (entry instanceof V8HeapContainmentTreeTableModel.NamedEntry) {
/* 132 */             if (Long.valueOf(((V8HeapContainmentTreeTableModel.NamedEntry)entry).getEntry().getSize()) == null) $$$reportNull$$$0(0);  return Long.valueOf(((V8HeapContainmentTreeTableModel.NamedEntry)entry).getEntry().getSize());
/*     */           } 
/* 134 */           return "";
/*     */         }
/*     */ 
/*     */         
/*     */         public TableCellRenderer getCustomizedRenderer(Object o, TableCellRenderer renderer) {
/* 139 */           return (TableCellRenderer)sizeRenderer;
/*     */         }
/*     */       };
/* 142 */     columns[3] = new ColumnInfo<Object, Object>(NodeJSBundle.message("profile.retained.table.column.retained_size.name", new Object[0])) {
/*     */         @NotNull
/*     */         public Object valueOf(Object entry) {
/* 145 */           if (entry instanceof V8HeapContainmentTreeTableModel.NamedEntry) {
/* 146 */             if (Long.valueOf(reader.getRetainedSize((int)((V8HeapContainmentTreeTableModel.NamedEntry)entry).getEntry().getId())) == null) $$$reportNull$$$0(0);  return Long.valueOf(reader.getRetainedSize((int)((V8HeapContainmentTreeTableModel.NamedEntry)entry).getEntry().getId()));
/*     */           } 
/* 148 */           return "";
/*     */         }
/*     */ 
/*     */         
/*     */         public TableCellRenderer getCustomizedRenderer(Object o, TableCellRenderer renderer) {
/* 153 */           return (TableCellRenderer)sizeRenderer;
/*     */         }
/*     */       };
/*     */   }
/*     */   
/*     */   private void fillData(V8HeapEntry main, V8HeapEdge mainEdge, List<Pair<V8HeapEntry, V8HeapEdge>> data, Object[] pathFromRoot) {
/* 159 */     if (pathFromRoot != null) {
/* 160 */       for (int j = 1; j < pathFromRoot.length; j++) {
/* 161 */         Object node = pathFromRoot[j];
/* 162 */         if (node instanceof V8HeapContainmentTreeTableModel.NamedEntry) {
/* 163 */           V8HeapContainmentTreeTableModel.NamedEntry namedEntry = (V8HeapContainmentTreeTableModel.NamedEntry)node;
/* 164 */           this.myFromRootChain.add(new FixedNodesListNode(namedEntry, -100, j - 1));
/*     */         } 
/*     */       } 
/*     */     } else {
/* 168 */       List<FixedRetainerNode> chain = SearchDetailsTreeModel.getChainToRoot(-100, main, mainEdge, this.myReader);
/* 169 */       for (int j = 0; j < chain.size(); j++) {
/* 170 */         FixedNodesListNode node = (FixedNodesListNode)chain.get(j);
/* 171 */         this.myFromRootChain.add(new FixedNodesListNode((V8HeapContainmentTreeTableModel.NamedEntry)node, node.getVariantId(), j));
/*     */       } 
/*     */     } 
/*     */     
/* 175 */     for (int i = 0; i < data.size(); i++) {
/* 176 */       Pair<V8HeapEntry, V8HeapEdge> pair = data.get(i);
/* 177 */       List<FixedRetainerNode> chain = SearchDetailsTreeModel.getChainToRoot(i, (V8HeapEntry)pair.getFirst(), (V8HeapEdge)pair.getSecond(), this.myReader);
/* 178 */       Collections.reverse(chain);
/* 179 */       for (int j = 0; j < chain.size(); j++) {
/* 180 */         FixedNodesListNode node = (FixedNodesListNode)chain.get(j);
/* 181 */         node.setLevelNum(j);
/*     */       } 
/* 183 */       this.myData.add(chain);
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   public TableCellRenderer getCustomizedRenderer(int column, Object o, TableCellRenderer renderer) {
/* 189 */     return this.myColumns[column].getCustomizedRenderer(o, renderer);
/*     */   }
/*     */ 
/*     */   
/*     */   public int getColumnCount() {
/* 194 */     return this.myColumns.length;
/*     */   }
/*     */ 
/*     */   
/*     */   public String getColumnName(int column) {
/* 199 */     return this.myColumns[column].getName();
/*     */   }
/*     */ 
/*     */   
/*     */   public Class getColumnClass(int column) {
/* 204 */     return (column == 0) ? TreeTableModel.class : String.class;
/*     */   }
/*     */ 
/*     */   
/*     */   public Object getValueAt(Object node, int column) {
/* 209 */     return this.myColumns[column].valueOf(node);
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean isCellEditable(Object node, int column) {
/* 214 */     return false;
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
/* 229 */     return this.myRoot;
/*     */   }
/*     */ 
/*     */   
/*     */   public Object getChild(Object parent, int index) {
/* 234 */     if (parent == this.myRoot)
/* 235 */       return this.mySecondLevel.get(index); 
/* 236 */     if (parent == "Chain from root:")
/* 237 */       return this.myFromRootChain.get(0); 
/* 238 */     if (parent == "Retainers:")
/* 239 */       return ((List)this.myData.get(index)).get(0); 
/* 240 */     if (parent == this.myDescription)
/* 241 */       return this.myDescriptionDetails.get(index); 
/* 242 */     if (parent instanceof FixedNodesListNode) {
/* 243 */       FixedNodesListNode node = (FixedNodesListNode)parent;
/* 244 */       if (((FixedNodesListNode)parent).getVariantId() == -100) {
/* 245 */         return (this.myFromRootChain.size() <= node.getLevelNum() + 1) ? null : this.myFromRootChain.get(node.getLevelNum() + 1);
/*     */       }
/* 247 */       List<FixedRetainerNode> list = this.myData.get(node.getVariantId());
/* 248 */       return (list.size() <= node.getLevelNum() + 1) ? null : list.get(node.getLevelNum() + 1);
/*     */     } 
/*     */     
/* 251 */     return null;
/*     */   }
/*     */ 
/*     */   
/*     */   public int getChildCount(Object parent) {
/* 256 */     if (parent == this.myRoot)
/* 257 */       return this.mySecondLevel.size(); 
/* 258 */     if (parent == "Chain from root:")
/* 259 */       return 1; 
/* 260 */     if (parent == "Retainers:")
/* 261 */       return this.myData.size(); 
/* 262 */     if (parent == this.myDescription)
/* 263 */       return this.myDescriptionDetails.size(); 
/* 264 */     if (parent instanceof FixedNodesListNode) {
/* 265 */       FixedNodesListNode node = (FixedNodesListNode)parent;
/* 266 */       if (((FixedNodesListNode)parent).getVariantId() == -100) {
/* 267 */         return (this.myFromRootChain.size() <= node.getLevelNum() + 1) ? 0 : 1;
/*     */       }
/* 269 */       List<FixedRetainerNode> list = this.myData.get(node.getVariantId());
/* 270 */       return (list.size() <= node.getLevelNum() + 1) ? 0 : 1;
/*     */     } 
/*     */     
/* 273 */     return 0;
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean isLeaf(Object node) {
/* 278 */     return (getChildCount(node) == 0);
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public void valueForPathChanged(TreePath path, Object newValue) {}
/*     */ 
/*     */   
/*     */   public int getIndexOfChild(Object parent, Object child) {
/* 287 */     if (parent == this.myRoot)
/* 288 */       return ((String)this.mySecondLevel.get(0)).equals(child) ? 0 : 1; 
/* 289 */     if (parent == "Chain from root:")
/* 290 */       return 0; 
/* 291 */     if (parent == "Retainers:") {
/* 292 */       for (int i = 0; i < this.myData.size(); i++) {
/* 293 */         List<FixedRetainerNode> nodes = this.myData.get(i);
/* 294 */         if (((FixedRetainerNode)nodes.get(0)).equals(child)) return i; 
/*     */       } 
/* 296 */     } else if (parent == this.myDescription) {
/* 297 */       for (int i = 0; i < this.myDescriptionDetails.size(); i++) {
/* 298 */         String s = this.myDescriptionDetails.get(i);
/* 299 */         if (s.equals(child)) return i; 
/*     */       } 
/* 301 */     } else if (parent instanceof FixedNodesListNode) {
/* 302 */       return 0;
/*     */     } 
/* 304 */     return 0;
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
/*     */   public V8HeapEntry getMain() {
/* 316 */     return this.myMain;
/*     */   }
/*     */   
/*     */   public V8HeapEdge getMainEdge() {
/* 320 */     return this.myMainEdge;
/*     */   }
/*     */   
/*     */   public void expandByDefault(V8HeapTreeTable retainersTable) {
/* 324 */     List<Object> fromRoot = new ArrayList(this.myFromRootChain.size() + 2);
/* 325 */     fromRoot.add(this.myRoot);
/* 326 */     fromRoot.add("Chain from root:");
/* 327 */     fromRoot.addAll(this.myFromRootChain.subList(0, this.myFromRootChain.size() - 1));
/* 328 */     retainersTable.getTree().expandPath(new TreePath(ArrayUtil.toObjectArray(fromRoot)));
/*     */     
/* 330 */     retainersTable.getTree().expandPath(new TreePath(new Object[] { this.myRoot, "Retainers:" }));
/*     */   }
/*     */   
/*     */   public boolean navigatableSelected(Object currentlySelected) {
/* 334 */     if (currentlySelected == null) return false; 
/* 335 */     if (currentlySelected == "Chain from root:")
/* 336 */       return true; 
/* 337 */     if (currentlySelected == "Retainers:")
/* 338 */       return false; 
/* 339 */     if (currentlySelected instanceof FixedRetainerNode)
/* 340 */       return true; 
/* 341 */     if (currentlySelected instanceof FixedNodesListNode) {
/* 342 */       return true;
/*     */     }
/* 344 */     return false;
/*     */   }
/*     */   
/*     */   public List<V8HeapContainmentTreeTableModel.NamedEntry> getPathForSelectionInMainTree(Object currentlySelected) {
/* 348 */     if (currentlySelected == null) return null;
/*     */     
/* 350 */     if (currentlySelected == "Chain from root:")
/* 351 */       return getFromRootFragment(null); 
/* 352 */     if (currentlySelected == "Retainers:")
/* 353 */       return null; 
/* 354 */     if (currentlySelected instanceof FixedRetainerNode)
/* 355 */       return getFromRetainersFragment((FixedRetainerNode)currentlySelected); 
/* 356 */     if (currentlySelected instanceof FixedNodesListNode) {
/* 357 */       return getFromRootFragment((FixedNodesListNode)currentlySelected);
/*     */     }
/* 359 */     return null;
/*     */   }
/*     */   
/*     */   private List<V8HeapContainmentTreeTableModel.NamedEntry> getFromRetainersFragment(@NotNull FixedRetainerNode selected) {
/* 363 */     if (selected == null) $$$reportNull$$$0(0);  List<V8HeapContainmentTreeTableModel.NamedEntry> list = new ArrayList<>();
/* 364 */     list.add(new V8HeapContainmentTreeTableModel.NamedEntry(this.myReader.getNode(0L), "", "", -1L));
/*     */     
/* 366 */     int variantId = selected.getVariantId();
/* 367 */     List<FixedRetainerNode> nodes = this.myData.get(variantId);
/* 368 */     for (int i = nodes.size() - 1; i >= 0; i--) {
/* 369 */       FixedRetainerNode node = nodes.get(i);
/* 370 */       list.add(node);
/* 371 */       if (node.equals(selected))
/*     */         break; 
/* 373 */     }  return list;
/*     */   }
/*     */   
/*     */   private List<V8HeapContainmentTreeTableModel.NamedEntry> getFromRootFragment(@Nullable FixedNodesListNode lastNode) {
/* 377 */     List<V8HeapContainmentTreeTableModel.NamedEntry> list = new ArrayList<>();
/* 378 */     list.add(new V8HeapContainmentTreeTableModel.NamedEntry(this.myReader.getNode(0L), "", "", -1L));
/* 379 */     for (FixedNodesListNode node : this.myFromRootChain) {
/* 380 */       list.add(node);
/* 381 */       if (node.equals(lastNode)) {
/*     */         break;
/*     */       }
/*     */     } 
/* 385 */     return list;
/*     */   }
/*     */ }


/* Location:              C:\Program Files\JetBrains\WebStorm 2021.1\plugins\NodeJS\lib\NodeJS.jar!\com\jetbrains\nodejs\run\profile\heap\view\models\RetainersTreeModel.class
 * Java compiler version: 11 (55.0)
 * JD-Core Version:       1.1.3
 */