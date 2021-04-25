/*     */ package org.bipolar.run.profile.cpu.view;
/*     */ 
/*     */ import com.intellij.openapi.Disposable;
/*     */ import com.intellij.openapi.actionSystem.AnAction;
/*     */ import com.intellij.openapi.actionSystem.DefaultActionGroup;
/*     */ import com.intellij.openapi.project.Project;
/*     */ import com.intellij.openapi.util.Factory;
/*     */ import com.intellij.openapi.util.Pair;
/*     */ import com.intellij.ui.scale.JBUIScale;
/*     */ import com.intellij.ui.treeStructure.treetable.TreeTable;
/*     */ import com.intellij.util.Consumer;
/*     */ import com.intellij.util.containers.ContainerUtil;
/*     */ import com.intellij.util.containers.MultiMap;
/*     */ import com.intellij.util.ui.tree.TreeUtil;
/*     */ import org.bipolar.NodeJSBundle;
/*     */ import org.bipolar.run.profile.TreeTableWidthController;
/*     */ import org.bipolar.run.profile.cpu.calculation.V8ProfileLine;
/*     */ import org.bipolar.run.profile.cpu.v8log.data.V8CpuLogCall;
/*     */ import org.bipolar.run.profile.cpu.v8log.reading.CpuProfilingView;
/*     */ import org.bipolar.run.profile.cpu.v8log.reading.V8LogCachingReader;
/*     */ import org.bipolar.run.profile.cpu.v8log.reading.V8SwitchViewActionsFactory;
/*     */ import java.io.IOException;
/*     */ import java.util.ArrayList;
/*     */ import java.util.Collection;
/*     */ import java.util.Comparator;
/*     */ import java.util.List;
/*     */ import java.util.Map;
/*     */ import java.util.function.Supplier;
/*     */ import javax.swing.JComponent;
/*     */ import javax.swing.JTree;
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
/*     */ public class TopCallsV8ProfilingComponent
/*     */   implements CpuProfilingView
/*     */ {
/*  50 */   public static final Supplier<String> TOP_CALLS = NodeJSBundle.messagePointer("profile.cpu.top_calls.title", new Object[0]);
/*     */   
/*     */   @Nls
/*     */   private String myErrorText;
/*     */   @NotNull
/*     */   private final Project myProject;
/*     */   @NotNull
/*     */   private final V8LogCachingReader myReader;
/*     */   @Nullable
/*     */   private final List<Pair<String, List<V8ProfileLine>>> myLines;
/*     */   private final Disposable myDisposable;
/*     */   private final Consumer<String> myNotificator;
/*     */   private V8ProfilingCallTreeTable myTable;
/*     */   private StatisticsTreeTableWithDetails myMasterDetails;
/*     */   private Factory<SearchInV8TreeAction.Searcher> myFactory;
/*     */   private V8SwitchViewActionsFactory myActionsFactory;
/*     */   
/*     */   public TopCallsV8ProfilingComponent(@NotNull Project project, @NotNull V8LogCachingReader reader, @Nullable List<Pair<String, List<V8ProfileLine>>> lines, @NotNull LineColorProvider fileColor, Disposable disposable, Consumer<String> notificator) throws IOException {
/*  68 */     this.myProject = project;
/*  69 */     this.myReader = reader;
/*  70 */     this.myLines = lines;
/*  71 */     this.myDisposable = disposable;
/*  72 */     this.myNotificator = notificator;
/*     */     
/*  74 */     createMainComponent(fileColor);
/*  75 */     this.myMasterDetails.addDetails((new TopCallsSelfDiagram(this.myProject, this.myReader, fileColor, JBUIScale.scale(140))).getComponent(), null, 
/*  76 */         NodeJSBundle.message("profile.top_call_diagram.title", new Object[0]), false);
/*     */   }
/*     */ 
/*     */   
/*     */   public V8ProfilingCallTreeTable getTreeTable() {
/*  81 */     return this.myTable;
/*     */   }
/*     */ 
/*     */   
/*     */   public void defaultExpand() {
/*  86 */     expand();
/*     */   }
/*     */ 
/*     */   
/*     */   public JComponent getMainComponent() {
/*  91 */     return this.myMasterDetails.getComponent();
/*     */   }
/*     */ 
/*     */   
/*     */   public String getName() {
/*  96 */     return TOP_CALLS.get();
/*     */   }
/*     */ 
/*     */   
/*     */   public void addActions(DefaultActionGroup group) {
/* 101 */     group.add(V8ProfilingCallTreeComponent.createEditSourceAction((JComponent)this.myTable));
/* 102 */     group.add((AnAction)V8ProfilingCallTreeComponent.createFilterAction(this.myTable, NodeJSBundle.message("profile.cpu.top_call.total.filter.text", new Object[0]), () -> expand(), null));
/* 103 */     group.add((AnAction)new SearchInV8TreeAction(this.myProject, this.myMasterDetails, this.myReader, getSearcherFactory()));
/*     */     
/* 105 */     V8CpuNavigationAction navigationAction = new V8CpuNavigationAction();
/* 106 */     group.add((AnAction)navigationAction.addActions(new AnAction[] { (AnAction)this.myActionsFactory.getToBottomUp(), (AnAction)this.myActionsFactory.getToTopDown() }));
/* 107 */     V8NavigateToFlameChartIntervalAction.addToGroup(navigationAction.getGroup(), this.myReader, this.myNotificator);
/*     */   }
/*     */ 
/*     */   
/*     */   public void registerItself(V8SwitchViewActionsFactory factory) {
/* 112 */     this.myActionsFactory = factory;
/* 113 */     this.myActionsFactory.setTopCalls(this);
/*     */   }
/*     */ 
/*     */   
/*     */   public StatisticsTreeTableWithDetails getMasterDetails() {
/* 118 */     return this.myMasterDetails;
/*     */   }
/*     */ 
/*     */   
/*     */   public Factory<SearchInV8TreeAction.Searcher> getSearcherFactory() {
/* 123 */     if (this.myFactory != null) return this.myFactory; 
/* 124 */     this.myFactory = (() -> new SearchInV8TreeAction.Searcher() {
/* 125 */         private final TopCalledV8TreeTableModel myModel = (TopCalledV8TreeTableModel)TopCallsV8ProfilingComponent.this.myTable.getTableModel();
/* 126 */         private final MultiMap myMap = new MultiMap();
/* 127 */         private final List myCalls = new ArrayList();
/* 128 */         private final List myNumbers = new ArrayList();
/*     */ 
/*     */         
/*     */         public boolean search(@NotNull String text, boolean caseSensitive) {
/* 132 */           if (text == null) $$$reportNull$$$0(0);  Map<V8ProfileLine, String> map = this.myModel.search(text, caseSensitive);
/* 133 */           if (map.isEmpty()) return false; 
/* 134 */           for (Map.Entry<V8ProfileLine, String> entry : map.entrySet()) {
/* 135 */             String key = ((V8ProfileLine)entry.getKey()).getPresentation(false);
/* 136 */             this.myMap.putValue(key, Pair.create(entry.getKey(), entry.getValue()));
/*     */           } 
/* 138 */           this.myCalls.addAll(ContainerUtil.map(this.myMap.keySet(), key -> ((V8ProfileLine)((Pair)this.myMap.get(key).iterator().next()).getFirst()).getCall()));
/* 139 */           this.myCalls.sort(Comparator.comparing(o -> o.getPresentation(false)));
/* 140 */           for (V8CpuLogCall call : this.myCalls) {
/* 141 */             this.myNumbers.add(Long.valueOf(this.myMap.get(call.getPresentation(false)).size()));
/*     */           }
/* 143 */           return true;
/*     */         }
/*     */ 
/*     */         
/*     */         public List<V8CpuLogCall> getUniqueCalls() {
/* 148 */           return this.myCalls;
/*     */         }
/*     */ 
/*     */         
/*     */         public List<Long> getNumbersOfCalls() {
/* 153 */           return this.myNumbers;
/*     */         }
/*     */ 
/*     */         
/*     */         public List<TreePath> getPathsToExpand(@NotNull V8CpuLogCall call) {
/* 158 */           if (call == null) $$$reportNull$$$0(1);  Collection pairs = this.myMap.get(call.getPresentation(false));
/* 159 */           List<TreePath> list = new ArrayList();
/* 160 */           for (Pair pair : pairs) {
/* 161 */             list.add(this.myModel.createPathToCall((String)pair.getSecond(), ((V8ProfileLine)pair.getFirst()).getCall().getStringId()));
/*     */           }
/* 163 */           return list;
/*     */         }
/*     */       });
/* 166 */     return this.myFactory;
/*     */   }
/*     */ 
/*     */   
/*     */   public String getError() {
/* 171 */     return this.myErrorText;
/*     */   }
/*     */   
/*     */   private void createMainComponent(@NotNull LineColorProvider fileColor) {
/* 175 */     if (fileColor == null) $$$reportNull$$$0(3);  if (this.myLines == null) {
/* 176 */       this.myErrorText = NodeJSBundle.message("profile.cpu.fragment_not_found.error.text", new Object[0]);
/*     */       
/*     */       return;
/*     */     } 
/* 180 */     TopCalledV8TreeTableModel model = new TopCalledV8TreeTableModel(this.myLines, fileColor);
/* 181 */     model.filterByLevel(10);
/* 182 */     this.myTable = new V8ProfilingCallTreeTable(this.myProject, model, this.myDisposable);
/* 183 */     V8ProfilingCallTreeComponent.adjustTable(this.myTable, () -> defaultExpand());
/* 184 */     TreeTableWidthController controller = new TreeTableWidthController(this.myTable, this.myDisposable);
/* 185 */     controller.setMeaningfulRenderer((TreeCellRenderer)model.getZeroRenderer());
/* 186 */     this.myTable.setController(controller);
/*     */     
/* 188 */     this.myMasterDetails = new StatisticsTreeTableWithDetails(this.myProject, this.myTable, V8ProfilingCallTreeComponent.TREE_PATH_V8_LINE_CONVERTOR, this.myDisposable);
/*     */   }
/*     */   
/*     */   public void expand() {
/* 192 */     if (this.myTable != null) {
/* 193 */       TreeUtil.expandAll((JTree)this.myTable.getTree());
/* 194 */       int maxSelf = 0;
/*     */       
/* 196 */       int selectedRow = -1;
/* 197 */       for (int i = 0; i < this.myTable.getTree().getRowCount(); i++) {
/* 198 */         TreePath treePath = this.myTable.getTree().getPathForRow(i);
/* 199 */         Object o = treePath.getLastPathComponent();
/* 200 */         if (o instanceof V8ProfileLine && ((V8ProfileLine)o).getSelfTensPercent() > maxSelf) {
/* 201 */           maxSelf = ((V8ProfileLine)o).getSelfTensPercent();
/* 202 */           selectedRow = i;
/*     */         } 
/*     */       } 
/* 205 */       if (selectedRow >= 0) {
/* 206 */         this.myTable.clearSelection();
/* 207 */         this.myTable.addRowSelectionInterval(selectedRow, selectedRow);
/*     */       } 
/* 209 */       V8ProfilingCallTreeComponent.scrollToSelectedRow((TreeTable)this.myTable);
/* 210 */       this.myTable.revalidate();
/* 211 */       this.myTable.repaint();
/*     */     } 
/*     */   }
/*     */   
/*     */   public boolean navigateByStackTrace(@NotNull Long stackId) {
/* 216 */     if (stackId == null) $$$reportNull$$$0(4); 
/* 217 */     TreePath call = ((TopCalledV8TreeTableModel)this.myTable.getTableModel()).createPathToCall("JavaScript", stackId.longValue());
/* 218 */     if (call != null) {
/* 219 */       V8ProfilingCallTreeComponent.resetDataToTable(this.myTable, () -> {
/*     */             this.myTable.revalidate();
/*     */             
/*     */             this.myTable.repaint();
/*     */             this.myTable.getTree().expandPath(call.getParentPath());
/*     */           }() -> {
/*     */             int row = this.myTable.getTree().getRowForPath(call);
/*     */             if (row >= 0) {
/*     */               this.myTable.clearSelection();
/*     */               this.myTable.addRowSelectionInterval(row, row);
/*     */               V8ProfilingCallTreeComponent.scrollToSelectedRow((TreeTable)this.myTable);
/*     */             } 
/*     */           });
/* 232 */       return true;
/*     */     } 
/* 234 */     return false;
/*     */   }
/*     */ }


/* Location:              C:\Program Files\JetBrains\WebStorm 2021.1\plugins\NodeJS\lib\NodeJS.jar!\com\jetbrains\nodejs\run\profile\cpu\view\TopCallsV8ProfilingComponent.class
 * Java compiler version: 11 (55.0)
 * JD-Core Version:       1.1.3
 */