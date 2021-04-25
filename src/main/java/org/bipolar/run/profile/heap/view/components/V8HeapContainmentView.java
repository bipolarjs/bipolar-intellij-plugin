/*     */ package org.bipolar.run.profile.heap.view.components;
/*     */ 
/*     */ import com.intellij.icons.AllIcons;
/*     */ import com.intellij.openapi.Disposable;
/*     */ import com.intellij.openapi.actionSystem.AnAction;
/*     */ import com.intellij.openapi.actionSystem.AnActionEvent;
/*     */ import com.intellij.openapi.actionSystem.CustomShortcutSet;
/*     */ import com.intellij.openapi.actionSystem.DefaultActionGroup;
/*     */ import com.intellij.openapi.actionSystem.PlatformDataKeys;
/*     */ import com.intellij.openapi.actionSystem.ShortcutSet;
/*     */ import com.intellij.openapi.project.DumbAwareAction;
/*     */ import com.intellij.openapi.project.Project;
/*     */ import com.intellij.openapi.ui.Messages;
/*     */ import com.intellij.openapi.util.Getter;
/*     */ import com.intellij.ui.treeStructure.treetable.TreeTable;
/*     */ import org.bipolar.NodeJSBundle;
/*     */ import org.bipolar.run.profile.V8Utils;
/*     */ import org.bipolar.run.profile.cpu.view.ProfilingView;
/*     */ import org.bipolar.run.profile.heap.V8CachingReader;
/*     */ import org.bipolar.run.profile.heap.data.V8HeapEdge;
/*     */ import org.bipolar.run.profile.heap.view.actions.GoToSourceAction;
/*     */ import org.bipolar.run.profile.heap.view.actions.MarkUnmarkAction;
/*     */ import org.bipolar.run.profile.heap.view.actions.V8NavigateToMainTreeAction;
/*     */ import org.bipolar.run.profile.heap.view.models.V8HeapContainmentTreeTableModel;
/*     */ import javax.swing.JComponent;
/*     */ import javax.swing.KeyStroke;
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
/*     */ public class V8HeapContainmentView
/*     */   implements ProfilingView<V8HeapTreeTable>
/*     */ {
/*     */   @NotNull
/*     */   private final Project myProject;
/*     */   @NotNull
/*     */   private final V8CachingReader myReader;
/*     */   @Nls
/*     */   private final String myName;
/*     */   private final V8MainTableWithRetainers<V8HeapContainmentTreeTableModel> myTableWithRetainers;
/*     */   private DataProviderPanel myPanel;
/*     */   
/*     */   public V8HeapContainmentView(@NotNull Project project, @NotNull V8CachingReader reader, @Nls String name, @NotNull Disposable disposable) {
/*  59 */     this.myProject = project;
/*  60 */     this.myReader = reader;
/*  61 */     this.myName = name;
/*  62 */     this
/*     */       
/*  64 */       .myTableWithRetainers = new V8MainTableWithRetainers<>(project, new V8HeapContainmentTreeTableModel(project, this.myReader), reader, reader.getResourses(), disposable);
/*  65 */     createUI();
/*     */   }
/*     */ 
/*     */   
/*     */   public JComponent getMainComponent() {
/*  70 */     return (JComponent)this.myPanel;
/*     */   }
/*     */   
/*     */   private void createUI() {
/*  74 */     this.myPanel = DataProviderPanel.wrap((JComponent)this.myTableWithRetainers.getMainSplitter());
/*  75 */     this.myPanel.register(V8NavigateToMainTreeAction.MAIN_TREE_NAVIGATOR.getName(), () -> this.myTableWithRetainers.getMainTreeNavigator());
/*  76 */     this.myPanel.register(MarkUnmarkAction.SELECTED_LINK.getName(), () -> {
/*     */           TreePath value = this.myTableWithRetainers.getTable().getTree().getSelectionPath();
/*     */           
/*     */           if (value != null && value.getLastPathComponent() instanceof V8HeapContainmentTreeTableModel.NamedEntry) {
/*     */             V8HeapContainmentTreeTableModel.NamedEntry namedEntry = (V8HeapContainmentTreeTableModel.NamedEntry)value.getLastPathComponent();
/*     */             return Long.valueOf(namedEntry.getLinkOffset() / 37L);
/*     */           } 
/*     */           return null;
/*     */         });
/*  85 */     this.myPanel.register(MarkUnmarkAction.SELECTED_NODE.getName(), () -> {
/*     */           TreePath value = this.myTableWithRetainers.getTable().getTree().getSelectionPath();
/*  87 */           return (value != null && value.getLastPathComponent() instanceof V8HeapContainmentTreeTableModel.NamedEntry) ? ((V8HeapContainmentTreeTableModel.NamedEntry)value.getLastPathComponent()).getEntry() : null;
/*     */         });
/*     */ 
/*     */ 
/*     */     
/*  92 */     this.myPanel.register(MarkUnmarkAction.REVALIDATION.getName(), new Getter<Object>() {
/*  93 */           private final Runnable runnable = new Runnable()
/*     */             {
/*     */               public void run() {
/*  96 */                 V8HeapContainmentView.this.myTableWithRetainers.getTable().revalidate();
/*  97 */                 V8HeapContainmentView.this.myTableWithRetainers.getTable().repaint();
/*     */               }
/*     */             };
/*     */ 
/*     */           
/*     */           public Object get() {
/* 103 */             return this.runnable;
/*     */           }
/*     */         });
/* 106 */     DistancesInspectionView view = new DistancesInspectionView(this.myProject, this.myReader, this.myTableWithRetainers.getMainTreeNavigator());
/* 107 */     DefaultActionGroup group = new DefaultActionGroup();
/* 108 */     view.addActions(group);
/* 109 */     this.myTableWithRetainers.addTabWithoutClose(NodeJSBundle.message("profile.heap.tab.distances_inspection.title", new Object[0]), view.getMainComponent(), group, false);
/*     */   }
/*     */ 
/*     */   
/*     */   public String getName() {
/* 114 */     return NodeJSBundle.message("profile.heap.containment.name", new Object[0]);
/*     */   }
/*     */ 
/*     */   
/*     */   public void addActions(DefaultActionGroup group) {
/* 119 */     V8HeapTreeTable v8HeapTreeTable = this.myTableWithRetainers.getTable();
/* 120 */     group.add((AnAction)new MarkUnmarkAction(this.myProject, this.myReader));
/* 121 */     group.add((AnAction)new MySearchAction(this.myTableWithRetainers, this.myReader));
/* 122 */     group.add((AnAction)new V8NavigateToMainTreeAction());
/* 123 */     group.add((AnAction)new GoToSourceAction(this.myReader, (TreeTable)this.myTableWithRetainers.getTable()));
/* 124 */     group.add((AnAction)new CompareWithSnapshotAction(this.myReader, this.myProject, this.myName));
/* 125 */     if (Boolean.getBoolean("idea.nodejs.v8.heap.profiling.show.node.details")) {
/* 126 */       MyNodeDetailsAction nodeDetailsAction = new MyNodeDetailsAction((TreeTable)v8HeapTreeTable, this.myReader);
/* 127 */       group.add((AnAction)nodeDetailsAction);
/* 128 */       nodeDetailsAction.registerCustomShortcutSet((ShortcutSet)new CustomShortcutSet(KeyStroke.getKeyStroke(81, 128)), (JComponent)v8HeapTreeTable);
/*     */     } 
/*     */     
/* 131 */     V8Utils.installHeapPopupMenu(this.myProject, (TreeTable)v8HeapTreeTable, this.myReader, null);
/*     */   }
/*     */ 
/*     */   
/*     */   public String getError() {
/* 136 */     return null;
/*     */   }
/*     */ 
/*     */   
/*     */   @Nullable
/*     */   public V8HeapTreeTable getTreeTable() {
/* 142 */     return null;
/*     */   }
/*     */ 
/*     */   
/*     */   public void defaultExpand() {}
/*     */   
/*     */   private static class MyNodeDetailsAction
/*     */     extends DumbAwareAction
/*     */   {
/*     */     private final TreeTable myTable;
/*     */     private final V8CachingReader myReader;
/*     */     
/*     */     MyNodeDetailsAction(TreeTable table, V8CachingReader reader) {
/* 155 */       super(NodeJSBundle.messagePointer("action.V8HeapContainmentView.show.node.details.text", new Object[0]), 
/* 156 */           NodeJSBundle.messagePointer("action.V8HeapContainmentView.show.node.details.description", new Object[0]), AllIcons.Actions.PreviewDetails);
/* 157 */       this.myTable = table;
/* 158 */       this.myReader = reader;
/*     */     }
/*     */ 
/*     */     
/*     */     public void actionPerformed(@NotNull AnActionEvent e) {
/* 163 */       if (e == null) $$$reportNull$$$0(0);  Project project = (Project)e.getData(PlatformDataKeys.PROJECT);
/* 164 */       if (project == null)
/*     */         return; 
/* 166 */       int row = this.myTable.getSelectedRow();
/* 167 */       if (row >= 0) {
/* 168 */         Object component = this.myTable.getTree().getPathForRow(row).getLastPathComponent();
/* 169 */         if (component instanceof V8HeapContainmentTreeTableModel.NamedEntry) {
/* 170 */           V8HeapContainmentTreeTableModel.NamedEntry entry = (V8HeapContainmentTreeTableModel.NamedEntry)component;
/* 171 */           StringBuilder sb = (new StringBuilder()).append("Node name: ").append(this.myReader.getString(entry.getEntry().getNameId()));
/* 172 */           V8HeapEdge edge = this.myReader.getEdge(entry.getLinkOffset() / 37L);
/*     */           
/* 174 */           sb.append(", type: ").append(entry.getEntry().getType()).append("\nEdge name: ").append(entry.getLinkPresentation())
/* 175 */             .append(", type: ").append(edge.getType()).append("\nis queriable: ").append(this.myReader.getFlags().isQueriable(
/* 176 */                 (int)entry.getEntry().getId())).append("\nis page: ").append(this.myReader.getFlags().isPage(
/* 177 */                 (int)entry.getEntry().getId()));
/*     */           
/* 179 */           String message = sb.toString();
/* 180 */           Messages.showInfoMessage(project, message, V8HeapComponent.TOOL_WINDOW_TITLE.get());
/*     */         } 
/*     */       } 
/*     */     }
/*     */   }
/*     */   
/*     */   private static class MySearchAction extends DumbAwareAction {
/*     */     private final V8MainTableWithRetainers<V8HeapContainmentTreeTableModel> myTableWithRetainers;
/*     */     private final V8CachingReader myReader;
/*     */     
/*     */     MySearchAction(V8MainTableWithRetainers<V8HeapContainmentTreeTableModel> tableWithRetainers, V8CachingReader reader) {
/* 191 */       super(NodeJSBundle.messagePointer("action.V8HeapContainmentView.search.string.text", new Object[0]), 
/* 192 */           NodeJSBundle.messagePointer("action.V8HeapContainmentView.search.string.description", new Object[0]), AllIcons.Actions.Search);
/* 193 */       this.myTableWithRetainers = tableWithRetainers;
/* 194 */       this.myReader = reader;
/*     */     }
/*     */ 
/*     */     
/*     */     public void actionPerformed(@NotNull AnActionEvent e) {
/* 199 */       if (e == null) $$$reportNull$$$0(0);  Project project = (Project)e.getData(PlatformDataKeys.PROJECT);
/* 200 */       if (project == null)
/*     */         return; 
/* 202 */       SearchDialog dialog = new SearchDialog(project, this.myReader, this.myTableWithRetainers);
/* 203 */       dialog.search();
/*     */     }
/*     */   }
/*     */   
/*     */   public V8MainTableWithRetainers<V8HeapContainmentTreeTableModel> getTableWithRetainers() {
/* 208 */     return this.myTableWithRetainers;
/*     */   }
/*     */ }


/* Location:              C:\Program Files\JetBrains\WebStorm 2021.1\plugins\NodeJS\lib\NodeJS.jar!\com\jetbrains\nodejs\run\profile\heap\view\components\V8HeapContainmentView.class
 * Java compiler version: 11 (55.0)
 * JD-Core Version:       1.1.3
 */