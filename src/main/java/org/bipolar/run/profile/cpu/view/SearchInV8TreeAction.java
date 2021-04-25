/*     */ package org.bipolar.run.profile.cpu.view;
/*     */ import com.intellij.icons.AllIcons;
/*     */ import com.intellij.javascript.nodejs.NodeUIUtil;
/*     */ import com.intellij.openapi.actionSystem.ActionGroup;
/*     */ import com.intellij.openapi.actionSystem.ActionManager;
/*     */ import com.intellij.openapi.actionSystem.AnAction;
/*     */ import com.intellij.openapi.actionSystem.AnActionEvent;
/*     */ import com.intellij.openapi.actionSystem.DefaultActionGroup;
/*     */ import com.intellij.openapi.actionSystem.PlatformDataKeys;
/*     */ import com.intellij.openapi.project.DumbAwareAction;
/*     */ import com.intellij.openapi.project.Project;
/*     */ import com.intellij.openapi.ui.DialogBuilder;
/*     */ import com.intellij.openapi.util.Factory;
/*     */ import com.intellij.openapi.util.NlsSafe;
/*     */ import com.intellij.openapi.wm.IdeFocusManager;
/*     */ import com.intellij.ui.ColoredTableCellRenderer;
/*     */ import com.intellij.ui.PopupHandler;
/*     */ import com.intellij.ui.SearchTextField;
/*     */ import com.intellij.ui.components.JBLabel;
/*     */ import com.intellij.ui.components.JBPanel;
/*     */ import com.intellij.ui.treeStructure.treetable.TreeTable;
/*     */ import com.intellij.util.ui.JBUI;
/*     */ import org.bipolar.NodeJSBundle;
/*     */ import org.bipolar.run.profile.TreeTableWithTreeWidthController;
/*     */ import org.bipolar.run.profile.V8Utils;
/*     */ import org.bipolar.run.profile.cpu.v8log.data.V8CpuLogCall;
/*     */ import org.bipolar.run.profile.cpu.v8log.reading.V8LogCachingReader;
/*     */ import org.bipolar.run.profile.cpu.v8log.reading.V8StackTableModel;
/*     */ import org.bipolar.run.profile.cpu.v8log.ui.StackTraceTable;
/*     */ import org.bipolar.run.profile.cpu.v8log.ui.TableWithFixedWidth;
/*     */ import icons.NodeJSIcons;
/*     */ import java.awt.BorderLayout;
/*     */ import java.awt.Component;
/*     */ import java.awt.Dimension;
/*     */ import java.io.IOException;
/*     */ import java.util.List;
/*     */
/*     */
/*     */ import javax.swing.JComponent;
/*     */ import javax.swing.border.Border;
/*     */ import javax.swing.tree.TreePath;
/*     */
import org.jetbrains.annotations.NonNls;
/*     */ import org.jetbrains.annotations.NotNull;
/*     */ 
/*     */ public class SearchInV8TreeAction extends DumbAwareAction {
/*     */   public SearchInV8TreeAction(Project project, StatisticsTreeTableWithDetails component, V8LogCachingReader reader, @NotNull Factory<? extends Searcher> searcherFactory) {
/*  47 */     super(NodeJSBundle.messagePointer("action.SearchInV8TreeAction.search.text", new Object[0]),
/*  48 */         NodeJSBundle.messagePointer("action.SearchInV8TreeAction.search.text", new Object[0]), AllIcons.Actions.Search);
/*  49 */     this.myProject = project;
/*  50 */     this.myMainComponent = component;
/*  51 */     this.myReader = reader;
/*  52 */     this.mySearcherFactory = searcherFactory;
/*     */   }
/*     */   
/*     */   private final Project myProject;
/*     */   private final StatisticsTreeTableWithDetails myMainComponent;
/*     */   private final V8LogCachingReader myReader;
/*     */   @NotNull
/*     */   private final Factory<? extends Searcher> mySearcherFactory;
/*     */   @NonNls
/*     */   public static final String SEARCH_ID = "Node.js.profiling.cpu.search.history";
/*     */   
/*     */   public void actionPerformed(@NotNull AnActionEvent e) {
/*  64 */     if (e == null) $$$reportNull$$$0(1);  TreeTableWithTreeWidthController table = this.myMainComponent.getTable();
/*  65 */     String text = getSearchString();
/*  66 */     if (!StringUtil.isEmptyOrSpaces(text)) {
/*  67 */       Searcher searcher = (Searcher)this.mySearcherFactory.create();
/*  68 */       if (!searcher.search(text.trim(), false)) {
/*  69 */         NodeUIUtil.balloonInfo(this.myProject, NodeJSBundle.message("popup.content.nothing.found.by.search.string", new Object[] { text }), table.getLocationOnScreen(), null);
/*     */         
/*     */         return;
/*     */       } 
/*     */       try {
/*  74 */         V8StackTableModel model = (new V8StackTableModel(this.myReader, searcher.getUniqueCalls(), searcher.getNumbersOfCalls())).changeLastColumn((ColoredTableCellRenderer)new RightAlignedRenderer(), "Number");
/*  75 */         StackTraceTable stackTraceTable = new StackTraceTable(this.myProject, model);
/*  76 */         V8Utils.adjustTableColumnWidths((TableWithFixedWidth)stackTraceTable);
/*  77 */         DefaultActionGroup tableGroup = new DefaultActionGroup();
/*  78 */         V8Utils.LightweightEditSourceAction editSourceAction = new V8Utils.LightweightEditSourceAction((JComponent)stackTraceTable);
/*  79 */         tableGroup.add((AnAction)editSourceAction);
/*  80 */         tableGroup.add(createNavigateToMainTreeAction(stackTraceTable, searcher));
/*  81 */         PopupHandler.installPopupHandler((JComponent)stackTraceTable, (ActionGroup)tableGroup, "V8_CPU_PROFILING_POPUP", ActionManager.getInstance());
/*  82 */         this.myMainComponent.addDetails((JComponent)stackTraceTable, null, NodeJSBundle.message("tab.title.search.results.for", new Object[] { text }), true);
/*  83 */         stackTraceTable.addRowSelectionInterval(0, 0);
/*  84 */         IdeFocusManager.getGlobalInstance().doWhenFocusSettlesDown(() -> IdeFocusManager.getGlobalInstance().requestFocus((Component)stackTraceTable, true));
/*     */       }
/*  86 */       catch (IOException e1) {
/*  87 */         NodeUIUtil.balloonInfo(this.myProject, NodeJSBundle.message("popup.content.error.when.opening.search.results", new Object[] { e1.getMessage() }), table.getLocationOnScreen(), null);
/*     */       } 
/*     */     } 
/*     */   }
/*     */   @NlsSafe
/*     */   private static String getSearchString() {
/*  93 */     SearchTextField searchField = new SearchTextField("Node.js.profiling.cpu.search.history")
/*     */       {
/*     */         public Dimension getMinimumSize() {
/*  96 */           Dimension size = super.getMinimumSize();
/*  97 */           return new Dimension(150, size.height);
/*     */         }
/*     */       };
/* 100 */     searchField.setBorder((Border)JBUI.Borders.emptyLeft(5));
/* 101 */     DialogBuilder builder = new DialogBuilder();
/* 102 */     JBPanel panel = new JBPanel(new BorderLayout());
/* 103 */     panel.add((Component)new JBLabel(NodeJSBundle.message("label.search.for", new Object[0])), "West");
/* 104 */     panel.add((Component)searchField, "East");
/* 105 */     builder.setTitle(NodeJSBundle.message("dialog.title.search.in.v8.cpu.profiling.log", new Object[0]));
/* 106 */     builder.centerPanel((JComponent)panel).setDimensionServiceKey("Node.js.profiling.cpu.search.history");
/* 107 */     builder.setPreferredFocusComponent((JComponent)searchField);
/* 108 */     if (!builder.showAndGet()) return null; 
/* 109 */     searchField.addCurrentTextToHistory();
/* 110 */     return searchField.getText();
/*     */   }
/*     */   
/*     */   private AnAction createNavigateToMainTreeAction(@NotNull final StackTraceTable stackTable, final Searcher searcher) {
/* 114 */     if (stackTable == null) $$$reportNull$$$0(2);  return (AnAction)new DumbAwareAction(
/* 115 */         NodeJSBundle.messagePointer("action.DumbAware.SearchInV8TreeAction.text.show.in.tree", new Object[0]), 
/* 116 */         NodeJSBundle.messagePointer("action.DumbAware.SearchInV8TreeAction.description.show.in.tree", new Object[0]), NodeJSIcons.Navigate_inMainTree)
/*     */       {
/*     */         public void actionPerformed(@NotNull AnActionEvent e)
/*     */         {
/* 120 */           if (e == null) $$$reportNull$$$0(0);  V8CpuLogCall call = stackTable.getCall();
/* 121 */           if (call == null)
/* 122 */             return;  SearchInV8TreeAction.showCallsInTree(searcher, call, SearchInV8TreeAction.this.myMainComponent);
/*     */         }
/*     */ 
/*     */         
/*     */         public void update(@NotNull AnActionEvent e) {
/* 127 */           if (e == null) $$$reportNull$$$0(1);  super.update(e);
/* 128 */           Project project = (Project)e.getData(PlatformDataKeys.PROJECT);
/* 129 */           boolean enabled = (project != null && stackTable.getCall() != null);
/* 130 */           e.getPresentation().setEnabled(enabled);
/*     */         }
/*     */       };
/*     */   }
/*     */ 
/*     */   
/*     */   public static void showCallsInTree(@NotNull Searcher searcher, @NotNull V8CpuLogCall call, @NotNull StatisticsTreeTableWithDetails mainComponent) {
/* 137 */     if (searcher == null) $$$reportNull$$$0(3);  if (call == null) $$$reportNull$$$0(4);  if (mainComponent == null) $$$reportNull$$$0(5);  V8ProfilingCallTreeTable table = mainComponent.getTable();
/* 138 */     V8Utils.collapseAllBare((TreeTable)table);
/* 139 */     table.getTree().expandPath(new TreePath(table.getTree().getModel().getRoot()));
/* 140 */     List<TreePath> paths = searcher.getPathsToExpand(call);
/* 141 */     V8ProfilingCallTreeComponent.resetDataToTable(table, () -> {
/*     */           for (TreePath path : paths)
/*     */             table.getTree().expandPath(path.getParentPath()); 
/*     */         }() -> mainComponent.showSpeedSearch(call));
/*     */   }
/*     */   
/*     */   public static interface Searcher {
/*     */     boolean search(@NotNull String param1String, boolean param1Boolean);
/*     */     
/*     */     List<V8CpuLogCall> getUniqueCalls();
/*     */     
/*     */     List<Long> getNumbersOfCalls();
/*     */     
/*     */     List<TreePath> getPathsToExpand(@NotNull V8CpuLogCall param1V8CpuLogCall);
/*     */   }
/*     */ }


/* Location:              C:\Program Files\JetBrains\WebStorm 2021.1\plugins\NodeJS\lib\NodeJS.jar!\com\jetbrains\nodejs\run\profile\cpu\view\SearchInV8TreeAction.class
 * Java compiler version: 11 (55.0)
 * JD-Core Version:       1.1.3
 */