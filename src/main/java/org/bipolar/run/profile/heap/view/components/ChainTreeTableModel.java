/*     */ package org.bipolar.run.profile.heap.view.components;
/*     */ 
/*     */ import com.intellij.ui.treeStructure.treetable.TreeTableModel;
/*     */ import com.intellij.util.ui.ColumnInfo;
/*     */ import org.bipolar.run.profile.cpu.view.TreeTableModelWithCustomRenderer;
/*     */ import java.util.ArrayList;
/*     */ import java.util.List;
/*     */ import javax.swing.JTree;
/*     */ import javax.swing.event.TreeModelListener;
/*     */ import javax.swing.table.TableCellRenderer;
/*     */ import javax.swing.tree.TreePath;
/*     */ 
/*     */ 
/*     */ 
/*     */ public class ChainTreeTableModel
/*     */   implements TreeTableModelWithCustomRenderer
/*     */ {
/*     */   private final ColumnInfo[] myColumns;
/*     */   private final Node<Object> myRoot;
/*     */   
/*     */   public ChainTreeTableModel(ColumnInfo[] columns) {
/*  22 */     this.myColumns = columns;
/*  23 */     this.myRoot = new Node(new Object(), new ArrayList<>());
/*     */   }
/*     */   
/*     */   public void addTopKey(Node key) {
/*  27 */     this.myRoot.getChildren().add(key);
/*     */   }
/*     */   
/*     */   public Node createNode(Object o) {
/*  31 */     Node node = new Node(o, new ArrayList<>());
/*  32 */     return node;
/*     */   }
/*     */   
/*     */   private static Object castForColumns(Object object) {
/*  36 */     if (object instanceof Node) {
/*  37 */       return ((Node)object).getT();
/*     */     }
/*  39 */     return object;
/*     */   }
/*     */ 
/*     */   
/*     */   public int getColumnCount() {
/*  44 */     return this.myColumns.length;
/*     */   }
/*     */ 
/*     */   
/*     */   public String getColumnName(int column) {
/*  49 */     return this.myColumns[column].getName();
/*     */   }
/*     */ 
/*     */   
/*     */   public Class getColumnClass(int column) {
/*  54 */     return (column == 0) ? TreeTableModel.class : String.class;
/*     */   }
/*     */ 
/*     */   
/*     */   public TableCellRenderer getCustomizedRenderer(int column, Object o, TableCellRenderer renderer) {
/*  59 */     return this.myColumns[column].getCustomizedRenderer(castForColumns(o), renderer);
/*     */   }
/*     */ 
/*     */   
/*     */   public Object getValueAt(Object node, int column) {
/*  64 */     return this.myColumns[column].valueOf(castForColumns(node));
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean isCellEditable(Object node, int column) {
/*  69 */     return false;
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public void setValueAt(Object aValue, Object node, int column) {}
/*     */ 
/*     */   
/*     */   public void setTree(JTree tree) {}
/*     */ 
/*     */   
/*     */   public Object getRootObject() {
/*  81 */     return this.myRoot.getT();
/*     */   }
/*     */ 
/*     */   
/*     */   public Object getRoot() {
/*  86 */     return this.myRoot;
/*     */   }
/*     */ 
/*     */   
/*     */   public Object getChild(Object parent, int index) {
/*  91 */     List<Node<?>> children = getChildren(parent);
/*  92 */     if (children == null || children.size() <= index) return null; 
/*  93 */     return children.get(index);
/*     */   }
/*     */ 
/*     */   
/*     */   public int getChildCount(Object parent) {
/*  98 */     List<Node<?>> children = getChildren(parent);
/*  99 */     if (children == null) return 0; 
/* 100 */     return children.size();
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean isLeaf(Object node) {
/* 105 */     List<Node<?>> children = getChildren(node);
/* 106 */     if (children == null) return true; 
/* 107 */     return children.isEmpty();
/*     */   }
/*     */ 
/*     */   
/*     */   public int getIndexOfChild(Object parent, Object child) {
/* 112 */     List<Node<?>> children = getChildren(parent);
/* 113 */     if (children == null) return 0; 
/* 114 */     for (int i = 0; i < children.size(); i++) {
/* 115 */       Object<?> current = (Object<?>)children.get(i);
/* 116 */       if (child.equals(current)) return i; 
/*     */     } 
/* 118 */     return 0;
/*     */   }
/*     */   
/*     */   protected List<Node<?>> getChildren(Object object) {
/* 122 */     if (!(object instanceof Node)) return null; 
/* 123 */     Node<?> node = (Node)object;
/*     */     
/* 125 */     return filter(node.getChildren());
/*     */   }
/*     */   
/*     */   protected List<Node<?>> filter(List<Node<?>> children) {
/* 129 */     return children;
/*     */   }
/*     */ 
/*     */   
/*     */   public void valueForPathChanged(TreePath path, Object newValue) {}
/*     */ 
/*     */   
/*     */   public void addTreeModelListener(TreeModelListener l) {}
/*     */ 
/*     */   
/*     */   public void removeTreeModelListener(TreeModelListener l) {}
/*     */ 
/*     */   
/*     */   public static class Node<T>
/*     */   {
/*     */     private final T myT;
/*     */     
/*     */     private final List<Node<T>> myChildren;
/*     */     private int myMeaningfulChildren;
/*     */     private boolean myOnlyPartOfChildren;
/*     */     
/*     */     Node(T t, List<Node<T>> children) {
/* 151 */       this.myT = t;
/* 152 */       this.myChildren = children;
/*     */     }
/*     */     
/*     */     public int getMeaningfulChildren() {
/* 156 */       return this.myMeaningfulChildren;
/*     */     }
/*     */     
/*     */     public void setMeaningfulChildren(int meaningfulChildren) {
/* 160 */       this.myMeaningfulChildren = meaningfulChildren;
/*     */     }
/*     */     
/*     */     public T getT() {
/* 164 */       return this.myT;
/*     */     }
/*     */     
/*     */     public List<Node<T>> getChildren() {
/* 168 */       return this.myChildren;
/*     */     }
/*     */     
/*     */     public boolean isOnlyPartOfChildren() {
/* 172 */       return this.myOnlyPartOfChildren;
/*     */     }
/*     */     
/*     */     public void setOnlyPartOfChildren(boolean onlyPartOfChildren) {
/* 176 */       this.myOnlyPartOfChildren = onlyPartOfChildren;
/*     */     }
/*     */ 
/*     */     
/*     */     public String toString() {
/* 181 */       return this.myT.toString();
/*     */     }
/*     */ 
/*     */     
/*     */     public boolean equals(Object o) {
/* 186 */       if (this == o) return true; 
/* 187 */       if (o == null || getClass() != o.getClass()) return false;
/*     */       
/* 189 */       Node<?> node = (Node)o;
/*     */       
/* 191 */       if (!this.myT.equals(node.myT)) return false;
/*     */       
/* 193 */       return true;
/*     */     }
/*     */ 
/*     */     
/*     */     public int hashCode() {
/* 198 */       return this.myT.hashCode();
/*     */     }
/*     */   }
/*     */ }


/* Location:              C:\Program Files\JetBrains\WebStorm 2021.1\plugins\NodeJS\lib\NodeJS.jar!\com\jetbrains\nodejs\run\profile\heap\view\components\ChainTreeTableModel.class
 * Java compiler version: 11 (55.0)
 * JD-Core Version:       1.1.3
 */