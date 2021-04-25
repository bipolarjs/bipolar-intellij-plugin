/*     */ package org.bipolar.run.profile.heap.view.components;
/*     */ import com.intellij.openapi.actionSystem.AnAction;
/*     */ import com.intellij.openapi.actionSystem.DefaultActionGroup;
/*     */ import com.intellij.openapi.progress.ProgressManager;
/*     */ import com.intellij.openapi.progress.Task;
/*     */ import com.intellij.openapi.project.Project;
/*     */ import com.intellij.openapi.wm.IdeFocusManager;
/*     */ import com.intellij.ui.SpeedSearchComparator;
/*     */ import com.intellij.ui.TreeTableSpeedSearch;
/*     */ import com.intellij.ui.components.JBScrollPane;
/*     */ import com.intellij.ui.treeStructure.treetable.TreeTable;
/*     */ import com.intellij.ui.treeStructure.treetable.TreeTableModel;
/*     */ import com.intellij.util.ThreeState;
/*     */ import com.intellij.util.ui.update.UiNotifyConnector;
/*     */ import org.bipolar.run.profile.V8Utils;
/*     */ import org.bipolar.run.profile.heap.V8CachingReader;
/*     */ import org.bipolar.run.profile.heap.view.actions.GoToSourceAction;
/*     */ import org.bipolar.run.profile.heap.view.actions.MarkUnmarkAction;
/*     */ import org.bipolar.run.profile.heap.view.actions.V8NavigateToMainTreeAction;
/*     */ import java.awt.BorderLayout;
/*     */ import java.awt.Component;
/*     */ import java.util.List;
/*     */ import java.util.concurrent.atomic.AtomicReference;
/*     */ import javax.swing.JComponent;
/*     */ import javax.swing.JLabel;
/*     */ import javax.swing.JPanel;
/*     */ import javax.swing.tree.TreePath;
/*     */
import org.jetbrains.annotations.NotNull;
/*     */ 
/*     */ public final class DistancesInspectionView {
/*     */   @NotNull
/*     */   private final Project myProject;
/*     */   @NotNull
/*     */   private final V8CachingReader myReader;
/*     */   @NotNull
/*     */   private final V8MainTreeNavigator myNavigator;
/*     */   private final AtomicReference<ThreeState> myState;
/*     */   private V8HeapTreeTable myTable;
/*     */   private JPanel myMain;
/*     */   
/*     */   public DistancesInspectionView(@NotNull Project project, @NotNull V8CachingReader reader, @NotNull V8MainTreeNavigator navigator) {
/*  42 */     this.myProject = project;
/*  43 */     this.myReader = reader;
/*  44 */     this.myNavigator = navigator;
/*  45 */     this.myState = new AtomicReference<>(ThreeState.NO);
/*     */   }
/*     */   private MarkUnmarkAction myMarkUnmarkAction; private V8NavigateToMainTreeAction myToMainTreeAction; private GoToSourceAction myToSourceAction; private V8Utils.ExpandAllAction myExpandAllAction; private V8Utils.CollapseAllAction myCollapseAllAction; private JLabel myLabel;
/*     */   public JComponent getMainComponent() {
/*  49 */     if (!ThreeState.NO.equals(this.myState.get())) return this.myMain;
/*     */     
/*  51 */     this.myMain = new JPanel(new BorderLayout());
/*  52 */     JPanel inner = new JPanel(new BorderLayout());
/*  53 */     this.myLabel = new JLabel(CommonBundle.getLoadingTreeNodeText());
/*  54 */     inner.add(this.myLabel, "North");
/*  55 */     this.myMain.add(inner, "Center");
/*     */     
/*  57 */     UiNotifyConnector.doWhenFirstShown(this.myMain, new Runnable()
/*     */         {
/*     */           public void run() {
/*  60 */             DistancesInspectionView.this.myState.set(ThreeState.UNSURE);
/*  61 */             ProgressManager.getInstance().run((Task)new V8DistancesInspection(DistancesInspectionView.this.myProject, DistancesInspectionView.this.myReader)
/*     */                 {
/*     */                   public void onSuccess() {
/*  64 */                     DistancesInspectionView.this.myState.set(ThreeState.YES);
/*  65 */                     DistancesInspectionView.this.updateUI(this);
/*     */                   }
/*     */                 });
/*     */           }
/*     */         });
/*     */     
/*  71 */     return this.myMain;
/*     */   }
/*     */   
/*     */   private void updateUI(V8DistancesInspection inspection) {
/*  75 */     if (inspection.getException() != null) {
/*  76 */       this.myLabel.setText(inspection.getException().getMessage());
/*     */       return;
/*     */     } 
/*  79 */     DistancesInspectionResultsModel model = new DistancesInspectionResultsModel(this.myProject, this.myReader, inspection);
/*  80 */     this.myTable = V8Utils.createTable(this.myProject, (TreeTableModel)model, this.myReader);
/*  81 */     Object root = model.getRoot();
/*  82 */     List<ChainTreeTableModel.Node<?>> children = model.getChildren(root);
/*  83 */     for (ChainTreeTableModel.Node<?> child : children) {
/*  84 */       this.myTable.getTree().expandPath(new TreePath(new Object[] { root, child }));
/*     */     } 
/*  86 */     this.myTable.setSelectionMode(0);
/*  87 */     this.myTable.setRowSelectionInterval(0, 0);
/*     */     
/*  89 */     this.myMarkUnmarkAction.setTable((TreeTable)this.myTable);
/*  90 */     this.myToMainTreeAction.setTable((TreeTable)this.myTable);
/*  91 */     this.myToSourceAction.setTable((TreeTable)this.myTable);
/*  92 */     this.myExpandAllAction.setTable(this.myTable);
/*  93 */     this.myCollapseAllAction.setTable(this.myTable);
/*     */     
/*  95 */     TreeTableSpeedSearch search = new TreeTableSpeedSearch((TreeTable)this.myTable, o -> o.getLastPathComponent().toString());
/*     */ 
/*     */     
/*  98 */     search.setComparator(new SpeedSearchComparator(false, true));
/*     */     
/* 100 */     V8Utils.installHeapPopupMenu(this.myProject, (TreeTable)this.myTable, this.myReader, this.myNavigator);
/*     */     
/* 102 */     Component owner = FocusManager.getCurrentManager().getFocusOwner();
/* 103 */     boolean hasFocus = (this.myMain.getParent().equals(owner) || this.myMain.equals(owner));
/* 104 */     DataProviderPanel panel = DataProviderPanel.wrap((JComponent)new JBScrollPane((Component)this.myTable));
/* 105 */     this.myMain.removeAll();
/* 106 */     this.myMain.add((Component)panel, "Center");
/* 107 */     if (hasFocus) {
/* 108 */       IdeFocusManager.getGlobalInstance().doWhenFocusSettlesDown(() -> IdeFocusManager.getGlobalInstance().requestFocus((Component)this.myTable, true));
/*     */     }
/*     */   }
/*     */   
/*     */   public void addActions(DefaultActionGroup group) {
/* 113 */     this.myMarkUnmarkAction = new MarkUnmarkAction(this.myProject, this.myReader);
/* 114 */     this.myToMainTreeAction = new V8NavigateToMainTreeAction();
/* 115 */     this.myToMainTreeAction.setFixedNavigator(this.myNavigator);
/* 116 */     this.myToSourceAction = new GoToSourceAction(this.myReader, null);
/* 117 */     this.myExpandAllAction = new V8Utils.ExpandAllAction(null);
/* 118 */     this.myCollapseAllAction = new V8Utils.CollapseAllAction(null);
/*     */     
/* 120 */     group.add((AnAction)this.myMarkUnmarkAction);
/* 121 */     group.add((AnAction)this.myToMainTreeAction);
/* 122 */     group.add((AnAction)this.myToSourceAction);
/* 123 */     group.add((AnAction)this.myExpandAllAction);
/* 124 */     group.add((AnAction)this.myCollapseAllAction);
/*     */   }
/*     */ }


/* Location:              C:\Program Files\JetBrains\WebStorm 2021.1\plugins\NodeJS\lib\NodeJS.jar!\com\jetbrains\nodejs\run\profile\heap\view\components\DistancesInspectionView.class
 * Java compiler version: 11 (55.0)
 * JD-Core Version:       1.1.3
 */