/*     */ package org.bipolar.run.profile.heap.view.actions;
/*     */ 
/*     */ import com.intellij.openapi.actionSystem.AnActionEvent;
/*     */ import com.intellij.openapi.actionSystem.DataContext;
/*     */ import com.intellij.openapi.actionSystem.DataKey;
/*     */ import com.intellij.openapi.project.DumbAwareAction;
/*     */ import com.intellij.openapi.util.Pair;
/*     */ import com.intellij.ui.treeStructure.treetable.TreeTable;
/*     */ import org.bipolar.NodeJSBundle;
/*     */ import org.bipolar.run.profile.heap.data.V8HeapEdge;
/*     */ import org.bipolar.run.profile.heap.data.V8HeapEntry;
/*     */ import org.bipolar.run.profile.heap.view.components.ChainTreeTableModel;
/*     */ import org.bipolar.run.profile.heap.view.components.V8MainTreeNavigator;
/*     */ import org.bipolar.run.profile.heap.view.models.V8HeapContainmentTreeTableModel;
/*     */ import icons.NodeJSIcons;
/*     */ import javax.swing.tree.TreePath;
/*     */
import org.jetbrains.annotations.NotNull;
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
/*     */ public class V8NavigateToMainTreeAction
/*     */   extends DumbAwareAction
/*     */ {
/*  39 */   public static final DataKey<V8MainTreeNavigator> MAIN_TREE_NAVIGATOR = DataKey.create("V8_MAIN_TREE_NAVIGATOR");
/*  40 */   public static final DataKey<TreePath> TREE_PATH = DataKey.create("V8_TREE_PATH");
/*  41 */   public static final DataKey<Pair<V8HeapEntry, V8HeapEdge>> TREE_NODE = DataKey.create("V8_TREE_NODE");
/*     */   
/*     */   private V8MainTreeNavigator myFixedNavigator;
/*     */   private TreeTable myTable;
/*     */   
/*     */   public V8NavigateToMainTreeAction() {
/*  47 */     super(NodeJSBundle.messagePointer("action.V8NavigateToMainTreeAction.navigate.in.main.tree.text", new Object[0]),
/*  48 */         NodeJSBundle.messagePointer("action.V8NavigateToMainTreeAction.navigate.in.main.tree.text", new Object[0]), NodeJSIcons.Navigate_inMainTree);
/*     */   }
/*     */   
/*     */   private class DataGetter {
/*     */     private V8MainTreeNavigator myNavigator;
/*     */     private final TreePath myTreePath;
/*     */     private Pair<V8HeapEntry, V8HeapEdge> myNode;
/*     */     
/*     */     DataGetter(AnActionEvent e) {
/*  57 */       DataContext dc = e.getDataContext();
/*  58 */       this.myNavigator = (V8MainTreeNavigator)V8NavigateToMainTreeAction.MAIN_TREE_NAVIGATOR.getData(dc);
/*  59 */       this.myTreePath = (TreePath)V8NavigateToMainTreeAction.TREE_PATH.getData(dc);
/*  60 */       this.myNode = (Pair<V8HeapEntry, V8HeapEdge>)V8NavigateToMainTreeAction.TREE_NODE.getData(dc);
/*     */       
/*  62 */       if (V8NavigateToMainTreeAction.this.myFixedNavigator != null) {
/*  63 */         this.myNavigator = V8NavigateToMainTreeAction.this.myFixedNavigator;
/*     */       }
/*  65 */       if (V8NavigateToMainTreeAction.this.myTable != null) {
/*  66 */         TreePath path = V8NavigateToMainTreeAction.this.myTable.getTree().getSelectionPath();
/*  67 */         if (path != null) {
/*  68 */           if (path.getLastPathComponent() instanceof V8HeapContainmentTreeTableModel.NamedEntry) {
/*  69 */             this.myNode = Pair.create(((V8HeapContainmentTreeTableModel.NamedEntry)path.getLastPathComponent()).getEntry(), null);
/*  70 */           } else if (path.getLastPathComponent() instanceof ChainTreeTableModel.Node && ((ChainTreeTableModel.Node)path
/*  71 */             .getLastPathComponent()).getT() instanceof V8HeapContainmentTreeTableModel.NamedEntry) {
/*     */             
/*  73 */             V8HeapContainmentTreeTableModel.NamedEntry namedEntry = (V8HeapContainmentTreeTableModel.NamedEntry)((ChainTreeTableModel.Node)path.getLastPathComponent()).getT();
/*  74 */             this.myNode = Pair.create(namedEntry.getEntry(), null);
/*     */           } 
/*     */         }
/*     */       } 
/*     */     }
/*     */     
/*     */     public boolean enabled() {
/*  81 */       return (this.myNavigator != null && (this.myTreePath != null || this.myNode != null));
/*     */     }
/*     */     
/*     */     public V8MainTreeNavigator getNavigator() {
/*  85 */       return this.myNavigator;
/*     */     }
/*     */     
/*     */     public TreePath getTreePath() {
/*  89 */       return this.myTreePath;
/*     */     }
/*     */     
/*     */     public Pair<V8HeapEntry, V8HeapEdge> getNode() {
/*  93 */       return this.myNode;
/*     */     }
/*     */   }
/*     */   
/*     */   public void setFixedNavigator(V8MainTreeNavigator fixedNavigator) {
/*  98 */     this.myFixedNavigator = fixedNavigator;
/*     */   }
/*     */   
/*     */   public void setTable(TreeTable table) {
/* 102 */     this.myTable = table;
/*     */   }
/*     */ 
/*     */   
/*     */   public void update(@NotNull AnActionEvent e) {
/* 107 */     if (e == null) $$$reportNull$$$0(0);  super.update(e);
/* 108 */     boolean enabled = (new DataGetter(e)).enabled();
/* 109 */     e.getPresentation().setEnabled(enabled);
/*     */   }
/*     */ 
/*     */   
/*     */   public void actionPerformed(@NotNull AnActionEvent e) {
/* 114 */     if (e == null) $$$reportNull$$$0(1);  DataGetter getter = new DataGetter(e);
/* 115 */     if (!getter.enabled())
/*     */       return; 
/* 117 */     if (getter.getTreePath() != null) {
/* 118 */       getter.getNavigator().navigateTo(getter.getTreePath());
/*     */     } else {
/* 120 */       Pair<V8HeapEntry, V8HeapEdge> node = getter.getNode();
/* 121 */       getter.getNavigator().navigateTo((V8HeapEntry)node.getFirst(), (V8HeapEdge)node.getSecond());
/*     */     } 
/*     */   }
/*     */ }


/* Location:              C:\Program Files\JetBrains\WebStorm 2021.1\plugins\NodeJS\lib\NodeJS.jar!\com\jetbrains\nodejs\run\profile\heap\view\actions\V8NavigateToMainTreeAction.class
 * Java compiler version: 11 (55.0)
 * JD-Core Version:       1.1.3
 */