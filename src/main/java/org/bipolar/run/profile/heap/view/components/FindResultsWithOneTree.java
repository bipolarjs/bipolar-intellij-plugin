/*     */ package org.bipolar.run.profile.heap.view.components;
/*     */ 
/*     */ import com.intellij.icons.AllIcons;
/*     */ import com.intellij.openapi.actionSystem.AnAction;
/*     */ import com.intellij.openapi.actionSystem.AnActionEvent;
/*     */ import com.intellij.openapi.actionSystem.DefaultActionGroup;
/*     */ import com.intellij.openapi.actionSystem.ToggleAction;
/*     */ import com.intellij.openapi.application.ApplicationManager;
/*     */ import com.intellij.openapi.project.Project;
/*     */ import com.intellij.openapi.util.Getter;
/*     */ import com.intellij.openapi.wm.IdeFocusManager;
/*     */ import com.intellij.ui.components.JBScrollPane;
/*     */ import com.intellij.ui.treeStructure.treetable.TreeTable;
/*     */ import com.intellij.ui.treeStructure.treetable.TreeTableModel;
/*     */
/*     */ import com.intellij.util.containers.Convertor;
/*     */ import com.intellij.util.ui.tree.TreeUtil;
/*     */ import org.bipolar.NodeJSBundle;
/*     */ import org.bipolar.run.profile.V8Utils;
/*     */ import org.bipolar.run.profile.heap.V8CachingReader;
/*     */ import org.bipolar.run.profile.heap.view.actions.GoToSourceAction;
/*     */ import org.bipolar.run.profile.heap.view.actions.MarkUnmarkAction;
/*     */ import org.bipolar.run.profile.heap.view.actions.V8NavigateToMainTreeAction;
/*     */
/*     */ import org.bipolar.run.profile.heap.view.nodes.FixedRetainerNode;
/*     */ import java.awt.Component;
/*     */
/*     */
/*     */
/*     */ import javax.swing.JComponent;
/*     */ import javax.swing.JTree;
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
/*     */ public class FindResultsWithOneTree
/*     */ {
/*     */   private final Project myProject;
/*     */   private final V8CachingReader myReader;
/*     */   private final String myText;
/*     */   private final int myNumResults;
/*     */   private final V8MainTreeNavigator myNavigator;
/*     */   private Integer myLimit;
/*     */   private SearchResultsTreeModelFactory.ChainTreeModelWithTopLevelFilter<String> myByStringsModel;
/*     */   private SearchResultsTreeModelFactory.ChainTreeModelWithTopLevelFilter<String> myByTypes;
/*     */   private V8HeapTreeTable myTable;
/*     */   
/*     */   public FindResultsWithOneTree(Project project, V8CachingReader reader, String text, int numResults, V8MainTreeNavigator navigator) {
/*  63 */     this.myProject = project;
/*  64 */     this.myReader = reader;
/*  65 */     this.myText = text;
/*  66 */     this.myNumResults = numResults;
/*  67 */     this.myNavigator = navigator;
/*     */   }
/*     */ 
/*     */   
/*     */   public DataProviderPanel showMe(SearchResultsTreeModelFactory.ChainTreeModelWithTopLevelFilter<String> byStringsModel, SearchResultsTreeModelFactory.ChainTreeModelWithTopLevelFilter<String> byTypes) {
/*  72 */     this.myByStringsModel = byStringsModel;
/*  73 */     this.myByTypes = byTypes;
/*  74 */     this.myTable = V8Utils.createTable(this.myProject, (TreeTableModel)byTypes, this.myReader);
/*  75 */     V8Utils.installHeapPopupMenu(this.myProject, (TreeTable)this.myTable, this.myReader, this.myNavigator);
/*     */     
/*  77 */     DataProviderPanel wrapper = DataProviderPanel.wrap((JComponent)new JBScrollPane((Component)this.myTable));
/*  78 */     fillDataContext((TreeTable)this.myTable, wrapper);
/*     */     
/*  80 */     this.myTable.getSelectionModel().setSelectionMode(0);
/*  81 */     return wrapper;
/*     */   }
/*     */   
/*     */   public void addActions(DefaultActionGroup group) {
/*  85 */     group.add((AnAction)new GroupByAction());
/*  86 */     MarkUnmarkAction markUnmarkAction = new MarkUnmarkAction(this.myProject, this.myReader);
/*  87 */     markUnmarkAction.setTable((TreeTable)this.myTable);
/*  88 */     group.add((AnAction)markUnmarkAction);
/*  89 */     V8NavigateToMainTreeAction toMainTreeAction = new V8NavigateToMainTreeAction();
/*  90 */     toMainTreeAction.setTable((TreeTable)this.myTable);
/*  91 */     group.add((AnAction)toMainTreeAction);
/*  92 */     group.add((AnAction)new GoToSourceAction(this.myReader, (TreeTable)this.myTable));
/*  93 */     group.add((AnAction)new V8Utils.ExpandAllAction(this.myTable));
/*  94 */     group.add((AnAction)new V8Utils.CollapseAllAction((TreeTable)this.myTable));
/*     */   }
/*     */   
/*     */   public void defaultExpand() {
/*  98 */     ApplicationManager.getApplication().invokeLater(() -> {
/*     */           if (this.myNumResults <= 10) {
/*     */             TreeUtil.expandAll((JTree)this.myTable.getTree());
/*     */           } else {
/*     */             SearchResultsTreeModelFactory.expandTop(this.myTable.getTree(), this.myTable.getTableModel());
/*     */           } 
/*     */           if (this.myByStringsModel.getChildCount(this.myByStringsModel.getRoot()) > 0) {
/*     */             this.myTable.addRowSelectionInterval(0, 0);
/*     */           }
/*     */           IdeFocusManager.getGlobalInstance().doWhenFocusSettlesDown(());
/*     */         });
/*     */   }
/*     */   
/*     */   private void fillDataContext(final TreeTable table, DataProviderPanel wrapper) {
/* 112 */     Convertor<Convertor<FixedRetainerNode, Object>, Object> convertor = convertor1 -> {
/*     */         TreePath value = table.getTree().getSelectionPath();
/*     */         if (value != null && value.getLastPathComponent() instanceof ChainTreeTableModel.Node) {
/*     */           ChainTreeTableModel.Node<FixedRetainerNode> node = (ChainTreeTableModel.Node)value.getLastPathComponent();
/*     */           if (node.getT() instanceof FixedRetainerNode) {
/*     */             return convertor1.convert(node.getT());
/*     */           }
/*     */         } 
/*     */         return null;
/*     */       };
/* 122 */     wrapper.register(V8NavigateToMainTreeAction.TREE_PATH.getName(), () -> convertor.convert(()));
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
/* 137 */     wrapper.register(MarkUnmarkAction.SELECTED_NODE.getName(), () -> convertor.convert(()));
/* 138 */     wrapper.register(MarkUnmarkAction.UNREACHABLE_NODE.getName(), () -> convertor.convert(()));
/* 139 */     wrapper.register(MarkUnmarkAction.SELECTED_LINK.getName(), () -> convertor.convert(()));
/* 140 */     wrapper.register(MarkUnmarkAction.REVALIDATION.getName(), new Getter<Object>()
/*     */         {
/*     */           private final Runnable runnable;
/*     */ 
/*     */ 
/*     */ 
/*     */           
/*     */           public Object get() {
/* 148 */             return this.runnable;
/*     */           }
/*     */         });
/*     */   }
/*     */   
/*     */   public void moreResultsThan(Integer limit) {
/* 154 */     this.myLimit = limit;
/*     */   }
/*     */   
/*     */   private class GroupByAction extends ToggleAction {
/*     */     private boolean myIsGroupedByType;
/*     */     
/*     */     GroupByAction() {
/* 161 */       super(NodeJSBundle.messagePointer("action.GroupByAction.group.by.type.text", new Object[0]),
/* 162 */           NodeJSBundle.messagePointer("action.GroupByAction.group.by.type.text", new Object[0]), AllIcons.Actions.GroupByPrefix);
/* 163 */       this.myIsGroupedByType = true;
/*     */     }
/*     */ 
/*     */     
/*     */     public boolean isSelected(@NotNull AnActionEvent e) {
/* 168 */       if (e == null) $$$reportNull$$$0(0);  return this.myIsGroupedByType;
/*     */     }
/*     */ 
/*     */     
/*     */     public void setSelected(@NotNull AnActionEvent e, boolean state) {
/* 173 */       if (e == null) $$$reportNull$$$0(1);  this.myIsGroupedByType = state;
/* 174 */       if (this.myIsGroupedByType) {
/* 175 */         FindResultsWithOneTree.this.myTable.setModel((TreeTableModel)FindResultsWithOneTree.this.myByTypes);
/*     */       } else {
/* 177 */         FindResultsWithOneTree.this.myTable.setModel((TreeTableModel)FindResultsWithOneTree.this.myByStringsModel);
/*     */       } 
/* 179 */       V8Utils.afterModelReset(FindResultsWithOneTree.this.myProject, FindResultsWithOneTree.this.myReader, FindResultsWithOneTree.this.myTable);
/* 180 */       FindResultsWithOneTree.this.myTable.revalidate();
/* 181 */       FindResultsWithOneTree.this.myTable.repaint();
/* 182 */       FindResultsWithOneTree.this.defaultExpand();
/*     */     }
/*     */   }
/*     */ }


/* Location:              C:\Program Files\JetBrains\WebStorm 2021.1\plugins\NodeJS\lib\NodeJS.jar!\com\jetbrains\nodejs\run\profile\heap\view\components\FindResultsWithOneTree.class
 * Java compiler version: 11 (55.0)
 * JD-Core Version:       1.1.3
 */