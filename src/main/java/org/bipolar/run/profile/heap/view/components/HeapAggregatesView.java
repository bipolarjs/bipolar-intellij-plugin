/*     */ package org.bipolar.run.profile.heap.view.components;
/*     */ 
/*     */ import com.intellij.openapi.Disposable;
/*     */ import com.intellij.openapi.actionSystem.AnAction;
/*     */ import com.intellij.openapi.actionSystem.DefaultActionGroup;
/*     */ import com.intellij.openapi.project.Project;
/*     */ import com.intellij.openapi.util.Getter;
/*     */ import com.intellij.ui.treeStructure.treetable.TreeTable;
/*     */ import com.intellij.util.ArrayUtil;
/*     */ import org.bipolar.NodeJSBundle;
/*     */ import org.bipolar.run.profile.V8Utils;
/*     */ import org.bipolar.run.profile.cpu.view.ProfilingView;
/*     */ import org.bipolar.run.profile.heap.V8CachingReader;
/*     */ import org.bipolar.run.profile.heap.view.actions.GoToSourceAction;
/*     */ import org.bipolar.run.profile.heap.view.actions.MarkUnmarkAction;
/*     */ import org.bipolar.run.profile.heap.view.actions.V8NavigateToMainTreeAction;
/*     */ import org.bipolar.run.profile.heap.view.models.AggregatesTableModel;
/*     */ import org.bipolar.run.profile.heap.view.models.V8HeapContainmentTreeTableModel;
/*     */ import java.util.List;
/*     */ import javax.swing.JComponent;
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
/*     */ public class HeapAggregatesView
/*     */   implements ProfilingView<V8HeapTreeTable>
/*     */ {
/*     */   @NotNull
/*     */   private final Project myProject;
/*     */   @NotNull
/*     */   private final V8CachingReader myReader;
/*     */   @NotNull
/*     */   private final V8MainTreeNavigator myContainmentNavigator;
/*     */   private final V8MainTableWithRetainers<AggregatesTableModel> myMainTableWithRetainers;
/*     */   
/*     */   public HeapAggregatesView(@NotNull Project project, @NotNull V8CachingReader reader, @NotNull V8MainTreeNavigator containmentNavigator, @NotNull Disposable disposable) {
/*  51 */     this.myContainmentNavigator = containmentNavigator;
/*  52 */     this.myReader = reader;
/*  53 */     this.myProject = project;
/*  54 */     this
/*     */       
/*  56 */       .myMainTableWithRetainers = new V8MainTableWithRetainers<>(this.myProject, new AggregatesTableModel(this.myProject, this.myReader), this.myReader, this.myReader.getResourses(), disposable);
/*  57 */     this.myMainTableWithRetainers.setUseTreeSelectionForRetainers(false);
/*  58 */     this.myMainTableWithRetainers.setMainTreeNavigator(this.myContainmentNavigator);
/*     */   }
/*     */ 
/*     */   
/*     */   public JComponent getMainComponent() {
/*  63 */     DataProviderPanel panel = DataProviderPanel.wrap((JComponent)this.myMainTableWithRetainers.getMainSplitter());
/*  64 */     panel.register(V8NavigateToMainTreeAction.MAIN_TREE_NAVIGATOR.getName(), () -> this.myContainmentNavigator);
/*  65 */     panel.register(V8NavigateToMainTreeAction.TREE_PATH.getName(), () -> {
/*     */           if (this.myMainTableWithRetainers.getTable().isFocusOwner()) {
/*     */             TreePath path = this.myMainTableWithRetainers.getTable().getTree().getSelectionPath();
/*     */ 
/*     */             
/*     */             if (path != null && path.getLastPathComponent() instanceof V8HeapContainmentTreeTableModel.NamedEntry && this.myMainTableWithRetainers.getRetainersTreeModel() != null && this.myMainTableWithRetainers.getRetainersTreeModel().getMain() != null && this.myMainTableWithRetainers.getRetainersTreeModel().getMain().equals(((V8HeapContainmentTreeTableModel.NamedEntry)path.getLastPathComponent()).getEntry())) {
/*     */               List<V8HeapContainmentTreeTableModel.NamedEntry> list = this.myMainTableWithRetainers.getRetainersTreeModel().getPathForSelectionInMainTree("Chain from root:");
/*     */ 
/*     */               
/*     */               return (list == null) ? null : new TreePath(ArrayUtil.toObjectArray(list));
/*     */             } 
/*     */           } 
/*     */           
/*     */           return null;
/*     */         });
/*     */     
/*  81 */     panel.register(MarkUnmarkAction.SELECTED_NODE.getName(), () -> {
/*     */           TreePath value = this.myMainTableWithRetainers.getTable().getTree().getSelectionPath();
/*  83 */           return (value != null && value.getLastPathComponent() instanceof V8HeapContainmentTreeTableModel.NamedEntry) ? ((V8HeapContainmentTreeTableModel.NamedEntry)value.getLastPathComponent()).getEntry() : null;
/*     */         });
/*     */ 
/*     */ 
/*     */     
/*  88 */     panel.register(MarkUnmarkAction.REVALIDATION.getName(), new Getter<Object>() {
/*  89 */           private final Runnable runnable = new Runnable()
/*     */             {
/*     */               public void run() {
/*  92 */                 HeapAggregatesView.this.myMainTableWithRetainers.getTable().revalidate();
/*  93 */                 HeapAggregatesView.this.myMainTableWithRetainers.getTable().repaint();
/*     */               }
/*     */             };
/*     */ 
/*     */           
/*     */           public Object get() {
/*  99 */             return this.runnable;
/*     */           }
/*     */         });
/* 102 */     return (JComponent)panel;
/*     */   }
/*     */ 
/*     */   
/*     */   public String getName() {
/* 107 */     return NodeJSBundle.message("profile.heap.summary.title", new Object[0]);
/*     */   }
/*     */ 
/*     */   
/*     */   public void addActions(DefaultActionGroup group) {
/* 112 */     group.add((AnAction)new MarkUnmarkAction(this.myProject, this.myReader));
/* 113 */     group.add((AnAction)new V8NavigateToMainTreeAction());
/* 114 */     group.add((AnAction)new GoToSourceAction(this.myReader, (TreeTable)this.myMainTableWithRetainers.getTable()));
/*     */     
/* 116 */     V8Utils.installHeapPopupMenu(this.myProject, (TreeTable)this.myMainTableWithRetainers.getTable(), this.myReader, this.myContainmentNavigator);
/*     */   }
/*     */ 
/*     */   
/*     */   public String getError() {
/* 121 */     return null;
/*     */   }
/*     */ 
/*     */   
/*     */   @Nullable
/*     */   public V8HeapTreeTable getTreeTable() {
/* 127 */     return this.myMainTableWithRetainers.getTable();
/*     */   }
/*     */   
/*     */   public void defaultExpand() {}
/*     */ }


/* Location:              C:\Program Files\JetBrains\WebStorm 2021.1\plugins\NodeJS\lib\NodeJS.jar!\com\jetbrains\nodejs\run\profile\heap\view\components\HeapAggregatesView.class
 * Java compiler version: 11 (55.0)
 * JD-Core Version:       1.1.3
 */