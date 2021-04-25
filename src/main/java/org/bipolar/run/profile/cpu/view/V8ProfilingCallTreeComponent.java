/*     */ package org.bipolar.run.profile.cpu.view;
/*     */ 
/*     */ import com.intellij.icons.AllIcons;
/*     */ import com.intellij.openapi.Disposable;
/*     */ import com.intellij.openapi.actionSystem.AnAction;
/*     */ import com.intellij.openapi.actionSystem.AnActionEvent;
/*     */ import com.intellij.openapi.actionSystem.DefaultActionGroup;
/*     */ import com.intellij.openapi.project.Project;
/*     */ import com.intellij.openapi.ui.popup.JBPopup;
/*     */ import com.intellij.openapi.util.Factory;
/*     */ import com.intellij.ui.awt.RelativePoint;
/*     */ import com.intellij.ui.components.SliderSelectorAction;
/*     */ import com.intellij.ui.treeStructure.treetable.TreeTable;
/*     */ import com.intellij.ui.treeStructure.treetable.TreeTableModel;
/*     */ import com.intellij.ui.treeStructure.treetable.TreeTableTree;
/*     */ import com.intellij.util.Consumer;
/*     */ import com.intellij.util.PairConsumer;
/*     */ import com.intellij.util.containers.BidirectionalMap;
/*     */ import com.intellij.util.containers.ContainerUtil;
/*     */ import com.intellij.util.containers.Convertor;
/*     */ import com.intellij.util.containers.MultiMap;
/*     */ import com.intellij.util.ui.tree.TreeUtil;
/*     */ import org.bipolar.NodeJSBundle;
/*     */ import org.bipolar.run.profile.TreeTableWidthController;
/*     */ import org.bipolar.run.profile.V8Utils;
/*     */ import org.bipolar.run.profile.cpu.calculation.CallTreeType;
/*     */ import org.bipolar.run.profile.cpu.calculation.V8ProfileLine;
/*     */ import org.bipolar.run.profile.cpu.v8log.data.V8CpuLogCall;
/*     */ import org.bipolar.run.profile.cpu.v8log.reading.CpuProfilingView;
/*     */ import org.bipolar.run.profile.cpu.v8log.reading.V8LogCachingReader;
/*     */ import org.bipolar.run.profile.cpu.v8log.reading.V8SwitchViewActionsFactory;
/*     */ import java.awt.Rectangle;
/*     */ import java.awt.event.InputEvent;
/*     */ import java.awt.event.MouseEvent;
/*     */ import java.util.ArrayDeque;
/*     */ import java.util.ArrayList;
/*     */ import java.util.Arrays;
/*     */ import java.util.Collection;
/*     */ import java.util.Collections;
/*     */ import java.util.Comparator;
/*     */
/*     */ import java.util.Enumeration;
/*     */ import java.util.Hashtable;
/*     */ import java.util.List;
/*     */ import java.util.Map;
/*     */ import java.util.function.Supplier;
/*     */
/*     */ import javax.swing.JComponent;
/*     */ import javax.swing.JPanel;
/*     */ import javax.swing.JTree;
/*     */ import javax.swing.SwingUtilities;
/*     */ import javax.swing.tree.TreeCellRenderer;
/*     */ import javax.swing.tree.TreePath;
/*     */
import org.jetbrains.annotations.Nls;
/*     */ import org.jetbrains.annotations.NotNull;
/*     */ import org.jetbrains.annotations.Nullable;
/*     */ 
/*     */ 
/*     */ 
/*     */ public class V8ProfilingCallTreeComponent
/*     */   implements CpuProfilingView
/*     */ {
/*     */   public static final Convertor<TreePath, String> TREE_PATH_V8_LINE_CONVERTOR;
/*     */   
/*     */   static {
/*  66 */     TREE_PATH_V8_LINE_CONVERTOR = (path -> {
/*     */         Object o = path.getLastPathComponent();
/*     */         return (o instanceof V8ProfileLine) ? ((V8ProfileLine)o).getPresentation(false) : o.toString();
/*     */       });
/*     */   }
/*     */ 
/*     */   
/*  73 */   public static final Supplier<String> TOP_DOWN = NodeJSBundle.messagePointer("profile.cpu.top.down.name", new Object[0]);
/*  74 */   public static final Supplier<String> BOTTOM_UP = NodeJSBundle.messagePointer("profile.cpu.bottom.up.name", new Object[0]);
/*     */   
/*     */   @Nls
/*     */   private String myErrorText;
/*     */   
/*     */   @NotNull
/*     */   private final Project myProject;
/*     */   
/*     */   @NotNull
/*     */   private final V8LogCachingReader myReader;
/*     */   
/*     */   private final Consumer<String> myNotificator;
/*     */   
/*     */   @Nullable
/*     */   private final V8ProfileLine myRoot;
/*     */   @NotNull
/*     */   private final CallTreeType myCallTreeType;
/*     */   
/*     */   public V8ProfilingCallTreeComponent(@NotNull Project project, @NotNull V8LogCachingReader reader, @NotNull CallTreeType callTreeType, @NotNull LineColorProvider fileColors, Disposable disposable, Consumer<String> notificator) {
/*  93 */     this.myProject = project;
/*  94 */     this.myReader = reader;
/*  95 */     this.myNotificator = notificator;
/*  96 */     this.myRoot = CallTreeType.bottomUp.equals(callTreeType) ? reader.getBottomUp() : reader.getTopDown();
/*  97 */     this.myCallTreeType = callTreeType;
/*  98 */     this.myDisposable = disposable;
/*  99 */     this
/* 100 */       .myPercentFilterName = CallTreeType.topDown.equals(this.myCallTreeType) ? NodeJSBundle.message("profile.cpu.top_call.total.filter.text", new Object[0]) : NodeJSBundle.message("profile.cpu.top_call.parent.filter.text", new Object[0]);
/* 101 */     createMainComponent(fileColors);
/*     */   }
/*     */   private final Disposable myDisposable; private V8ProfilingCallTreeTable myTable; private StatisticsTreeTableWithDetails myMasterDetails; @Nls
/*     */   private final String myPercentFilterName; private Factory<SearchInV8TreeAction.Searcher> myFactory; private V8SwitchViewActionsFactory myActionsFactory;
/*     */   public JComponent getMainComponent() {
/* 106 */     return this.myMasterDetails.getComponent();
/*     */   }
/*     */ 
/*     */   
/*     */   public String getName() {
/* 111 */     return CallTreeType.topDown.equals(this.myCallTreeType) ? TOP_DOWN.get() : BOTTOM_UP.get();
/*     */   }
/*     */ 
/*     */   
/*     */   public void registerItself(V8SwitchViewActionsFactory factory) {
/* 116 */     this.myActionsFactory = factory;
/* 117 */     if (CallTreeType.bottomUp.equals(this.myCallTreeType)) {
/* 118 */       this.myActionsFactory.setBottomUp(this);
/*     */     } else {
/* 120 */       this.myActionsFactory.setTopDown(this);
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   public void addActions(DefaultActionGroup group) {
/* 126 */     if (this.myErrorText != null)
/* 127 */       return;  group.add(createEditSourceAction((JComponent)this.myTable));
/* 128 */     group.add((AnAction)createFilterAction(this.myTable, this.myPercentFilterName, () -> autoExpand(), null));
/* 129 */     group.add((AnAction)new SearchInV8TreeAction(this.myProject, this.myMasterDetails, this.myReader, getSearcherFactory()));
/*     */     
/* 131 */     V8CpuNavigationAction navigationAction = new V8CpuNavigationAction();
/* 132 */     group.add((AnAction)navigationAction);
/* 133 */     if (CallTreeType.bottomUp.equals(this.myCallTreeType)) {
/* 134 */       navigationAction.getGroup().add((AnAction)this.myActionsFactory.getToTopDown());
/*     */     } else {
/* 136 */       navigationAction.getGroup().add((AnAction)this.myActionsFactory.getToBottomUp());
/*     */     } 
/* 138 */     navigationAction.getGroup().add((AnAction)this.myActionsFactory.getToTopCalls());
/* 139 */     V8NavigateToFlameChartIntervalAction.addToGroup(navigationAction.getGroup(), this.myReader, this.myNotificator);
/*     */   }
/*     */ 
/*     */   
/*     */   public StatisticsTreeTableWithDetails getMasterDetails() {
/* 144 */     return this.myMasterDetails;
/*     */   }
/*     */ 
/*     */   
/*     */   public Factory<SearchInV8TreeAction.Searcher> getSearcherFactory() {
/* 149 */     if (this.myFactory != null) return this.myFactory; 
/* 150 */     this.myFactory = (() -> new SearchInV8TreeAction.Searcher() {
/* 151 */         private final MultiMap myMap = new MultiMap();
/* 152 */         private final List myCalls = new ArrayList();
/* 153 */         private final List myNumbers = new ArrayList();
/*     */ 
/*     */         
/*     */         public boolean search(@NotNull String text, boolean caseSensitive) {
/* 157 */           if (text == null) $$$reportNull$$$0(0);  List<V8ProfileLine> lines = ((V8TreeTableModel)V8ProfilingCallTreeComponent.this.myTable.getTableModel()).search(text.trim(), false);
/* 158 */           if (lines.isEmpty()) return false; 
/* 159 */           for (V8ProfileLine line : lines) {
/* 160 */             String key = line.getPresentation(false);
/* 161 */             this.myMap.putValue(key, line);
/*     */           } 
/* 163 */           this.myCalls.addAll(ContainerUtil.map(this.myMap.keySet(), key -> ((V8ProfileLine)this.myMap.get(key).iterator().next()).getCall()));
/* 164 */           this.myCalls.sort(Comparator.comparing(o -> o.getPresentation(false)));
/* 165 */           for (V8CpuLogCall call : this.myCalls) {
/* 166 */             this.myNumbers.add(Long.valueOf(this.myMap.get(call.getPresentation(false)).size()));
/*     */           }
/* 168 */           return true;
/*     */         }
/*     */ 
/*     */         
/*     */         public List<V8CpuLogCall> getUniqueCalls() {
/* 173 */           return this.myCalls;
/*     */         }
/*     */ 
/*     */         
/*     */         public List<Long> getNumbersOfCalls() {
/* 178 */           return this.myNumbers;
/*     */         }
/*     */ 
/*     */         
/*     */         public List<TreePath> getPathsToExpand(@NotNull V8CpuLogCall call) {
/* 183 */           if (call == null) $$$reportNull$$$0(1);  Collection lines = this.myMap.get(call.getPresentation(false));
/* 184 */           List<TreePath> paths = new ArrayList();
/* 185 */           V8TreeTableModel model = (V8TreeTableModel)V8ProfilingCallTreeComponent.this.myTable.getTableModel();
/* 186 */           for (V8ProfileLine line : lines) {
/* 187 */             List<Long> ids = getStackIds(line);
/* 188 */             TreePath path = model.createSelectionPathForStackTrace(ids);
/* 189 */             paths.add(path);
/*     */           } 
/* 191 */           return paths;
/*     */         }
/*     */         
/*     */         private List<Long> getStackIds(@NotNull V8ProfileLine line) {
/* 195 */           if (line == null) $$$reportNull$$$0(2);  List<Long> ids = new ArrayList();
/* 196 */           V8ProfileLine current = line;
/* 197 */           while (current != null && current.getCall().getStringId() != -1L) {
/* 198 */             ids.add(Long.valueOf(current.getCall().getStringId()));
/* 199 */             current = current.getParent();
/*     */           } 
/* 201 */           Collections.reverse(ids);
/* 202 */           return ids;
/*     */         }
/*     */       });
/* 205 */     return this.myFactory;
/*     */   }
/*     */ 
/*     */   
/*     */   public V8ProfilingCallTreeTable getTreeTable() {
/* 210 */     return this.myTable;
/*     */   }
/*     */ 
/*     */   
/*     */   public void defaultExpand() {
/* 215 */     autoExpand();
/*     */   }
/*     */ 
/*     */   
/*     */   public String getError() {
/* 220 */     return this.myErrorText;
/*     */   }
/*     */   
/*     */   private void createMainComponent(@NotNull LineColorProvider fileColors) {
/* 224 */     if (fileColors == null) $$$reportNull$$$0(4);  if (this.myRoot == null) {
/* 225 */       this.myErrorText = NodeJSBundle.message("profile.cpu.fragment_not_found.error.text", new Object[0]);
/*     */       
/*     */       return;
/*     */     } 
/* 229 */     V8TreeTableModel model = new V8TreeTableModel(this.myRoot, this.myCallTreeType, fileColors);
/* 230 */     model.filterByLevel(10);
/* 231 */     this.myTable = new V8ProfilingCallTreeTable(this.myProject, model, this.myDisposable);
/* 232 */     adjustTable(this.myTable, () -> defaultExpand());
/* 233 */     TreeTableWidthController controller = new TreeTableWidthController(this.myTable, this.myDisposable);
/* 234 */     controller.setMeaningfulRenderer((TreeCellRenderer)model.getZeroCellRenderer());
/* 235 */     this.myTable.setController(controller);
/*     */     
/* 237 */     this.myMasterDetails = new StatisticsTreeTableWithDetails(this.myProject, this.myTable, TREE_PATH_V8_LINE_CONVERTOR, this.myDisposable);
/*     */   }
/*     */   
/*     */   static AnAction createEditSourceAction(JComponent component) {
/* 241 */     return (AnAction)new V8Utils.LightweightEditSourceAction(component);
/*     */   }
/*     */   
/* 244 */   private static final int[] FILTER_VALUES = new int[] { 1, 2, 5, 7, 10, 15, 20, 30 };
/*     */ 
/*     */   
/*     */   public static SliderSelectorAction createFilterAction(@NotNull V8ProfilingCallTreeTable table, @NotNull @Nls String actionName, @NotNull Runnable myAutoExpander, Runnable afterCorrection) {
/* 248 */     if (table == null) $$$reportNull$$$0(5);  if (actionName == null) $$$reportNull$$$0(6);  if (myAutoExpander == null) $$$reportNull$$$0(7);  Hashtable<Integer, String> dictionary = new Hashtable<>();
/* 249 */     dictionary.put(Integer.valueOf(1), "no");
/* 250 */     final BidirectionalMap<Integer, Integer> numberToPercent = new BidirectionalMap();
/* 251 */     int cnt = 2;
/* 252 */     for (int value : FILTER_VALUES) {
/* 253 */       numberToPercent.put(Integer.valueOf(cnt++), Integer.valueOf(value));
/*     */     }
/* 255 */     for (Map.Entry<Integer, Integer> entry : (Iterable<Map.Entry<Integer, Integer>>)numberToPercent.entrySet()) {
/* 256 */       dictionary.put(entry.getKey(), "" + entry.getValue() + "%");
/*     */     }
/* 258 */     final FilteredByPercent model = (FilteredByPercent)table.getTableModel();
/* 259 */     Consumer<Integer> consumer = integer -> {
/*     */         if (1 == integer.intValue()) {
/*     */           if (!model.isFiltered())
/*     */             return;  model.clearFilter();
/*     */         } else {
/*     */           int level = ((Integer)numberToPercent.get(integer)).intValue() * 10; if (model.getTensPercentLevelInclusive() == level)
/*     */             return; 
/*     */           model.filterByLevel(level);
/*     */         } 
/*     */         resetDataToTable(table, myAutoExpander, afterCorrection);
/*     */       };
/* 270 */     SliderSelectorAction.Configuration configuration = new SliderSelectorAction.Configuration(1, dictionary, actionName + ": ", consumer)
/*     */       {
/*     */         public int getSelected()
/*     */         {
/* 274 */           int current = model.getTensPercentLevelInclusive();
/* 275 */           if (current == 0) return 1; 
/* 276 */           List<Integer> value = numberToPercent.getKeysByValue(Integer.valueOf(current / 10));
/* 277 */           if (value != null && value.size() == 1) {
/* 278 */             return ((Integer)value.get(0)).intValue();
/*     */           }
/* 280 */           return 1;
/*     */         }
/*     */ 
/*     */         
/*     */         public String getTooltip() {
/* 285 */           int current = model.getTensPercentLevelInclusive();
/* 286 */           if (current == 0) return NodeJSBundle.message("profile.cpu.no.percentage.text", new Object[0]); 
/* 287 */           return "" + current / 10 + "%";
/*     */         }
/*     */       };
/* 290 */     configuration.setShowOk(true);
/* 291 */     return new SliderSelectorAction(actionName, actionName, AllIcons.General.Filter, configuration)
/*     */       {
/*     */         public void update(@NotNull AnActionEvent e) {
/* 294 */           if (e == null) $$$reportNull$$$0(0);  super.update(e);
/* 295 */           e.getPresentation().setEnabledAndVisible(!"V8_CPU_PROFILING_POPUP".equals(e.getPlace()));
/*     */         }
/*     */ 
/*     */         
/*     */         protected void show(AnActionEvent e, JPanel result, JBPopup popup, InputEvent inputEvent) {
/* 300 */           InputEvent event = e.getInputEvent();
/* 301 */           if (event instanceof MouseEvent) {
/* 302 */             popup.show(new RelativePoint((MouseEvent)event));
/*     */           } else {
/* 304 */             popup.showInBestPositionFor(e.getDataContext());
/*     */           } 
/*     */         }
/*     */       };
/*     */   }
/*     */   
/*     */   protected static void resetDataToTable(@NotNull V8ProfilingCallTreeTable table, @NotNull Runnable myAutoExpander, Runnable afterCorrection) {
/* 311 */     if (table == null) $$$reportNull$$$0(8);  if (myAutoExpander == null) $$$reportNull$$$0(9);  table.setModel(table.getTableModel());
/* 312 */     table.revalidate();
/* 313 */     table.repaint();
/* 314 */     adjustTable(table, myAutoExpander);
/* 315 */     if (afterCorrection != null) {
/* 316 */       afterCorrection.run();
/*     */     }
/*     */   }
/*     */   
/*     */   public static void adjustTable(V8ProfilingCallTreeTable table, @NotNull Runnable defaultExpand) {
/* 321 */     if (defaultExpand == null) $$$reportNull$$$0(10);  table.setAutoResizeMode(0);
/* 322 */     table.getTableHeader().setResizingAllowed(false);
/* 323 */     table.getTableHeader().setReorderingAllowed(false);
/* 324 */     table.setRootVisible(false);
/* 325 */     table.getTree().setShowsRootHandles(true);
/* 326 */     defaultExpand.run();
/* 327 */     V8Utils.adjustColumnWIdths((TreeTable)table, null);
/* 328 */     if (table.getWidthController() != null) {
/* 329 */       table.getWidthController().installListener();
/*     */     }
/*     */   }
/*     */   
/*     */   public void expandAll() {
/* 334 */     TreeUtil.expandAll((JTree)this.myTable.getTree());
/* 335 */     if (this.myTable.getSelectedRowCount() == 0 && !this.myTable.isEmpty()) {
/* 336 */       this.myTable.getSelectionModel().addSelectionInterval(0, 0);
/*     */     }
/*     */   }
/*     */   
/*     */   private void autoExpand() {
/* 341 */     (new ConditionalExpander((TreeTable)this.myTable) {
/* 342 */         private final PairConsumer<TreePath, V8ProfileLine> mostInterestingSelector = CallTreeType.topDown.equals(V8ProfilingCallTreeComponent.this.myCallTreeType) ? 
/* 343 */           new PairConsumer<TreePath, V8ProfileLine>() {
/* 344 */             int biggestSelfPercent = 0;
/*     */ 
/*     */             
/*     */             public void consume(TreePath path, V8ProfileLine line) {
/* 348 */               if (line.getSelfTensPercent() > this.biggestSelfPercent) {
/* 349 */                 V8ProfilingCallTreeComponent.null.this.myPathToSelect = path;
/* 350 */                 this.biggestSelfPercent = line.getSelfTensPercent();
/*     */               }
/*     */             
/*     */             }
/* 354 */           } : new PairConsumer<TreePath, V8ProfileLine>() {
/* 355 */             int ticks = 0;
/*     */             
/*     */             public void consume(TreePath path, V8ProfileLine line) {
/* 358 */               if (V8ProfilingCallTreeComponent.null.this.myTable.getTableModel().getRoot().equals(line.getParent()) && line
/* 359 */                 .getTotalTicks() > this.ticks) {
/* 360 */                 V8ProfilingCallTreeComponent.null.this.myPathToSelect = path;
/* 361 */                 this.ticks = line.getTotalTicks();
/*     */               } 
/*     */             }
/*     */           };
/*     */         
/*     */         protected boolean toExpand(TreePath path) {
/* 367 */           Object o = path.getLastPathComponent();
/* 368 */           if (o instanceof V8ProfileLine) {
/* 369 */             V8ProfileLine line = (V8ProfileLine)o;
/* 370 */             if (line.getTotalTensPercent() >= 100 || line
/* 371 */               .getSelfTensPercent() >= 100) {
/* 372 */               this.mostInterestingSelector.consume(path, line);
/* 373 */               return true;
/*     */             } 
/*     */           } 
/*     */           
/* 377 */           return false;
/*     */         }
/* 379 */       }).execute();
/*     */   }
/*     */   
/*     */   public boolean navigateByStackTrace(@NotNull List<Long> stackIds) {
/* 383 */     if (stackIds == null) $$$reportNull$$$0(11);  TreePath path = ((V8TreeTableModel)this.myTable.getTableModel()).createSelectionPathForStackTrace(stackIds);
/* 384 */     if (path == null) return false; 
/* 385 */     navigateToTreePath(this.myTable, path);
/* 386 */     return true;
/*     */   }
/*     */ 
/*     */   
/*     */   private static void navigateToTreePath(V8ProfilingCallTreeTable treeTable, TreePath path) {
/* 391 */     Enumeration<TreePath> descendants = treeTable.getTree().getExpandedDescendants(new TreePath(new Object[] { treeTable.getTableModel().getRoot() }));
/* 392 */     resetDataToTable(treeTable, () -> {
/*     */           treeTable.revalidate();
/*     */           treeTable.repaint();
/*     */           treeTable.getTree().expandPath(path.getParentPath());
/*     */           if (descendants != null) {
/*     */             while (descendants.hasMoreElements()) {
/*     */               TreePath treePath = descendants.nextElement();
/*     */               List<Long> ids = ContainerUtil.map(Arrays.asList(treePath.getPath()), ());
/*     */               TreePath path1 = ((V8TreeTableModel)treeTable.getTableModel()).createSelectionPathForStackTrace(ids.subList(1, ids.size()));
/*     */               if (path1 != null) {
/*     */                 treeTable.getTree().expandPath(path1);
/*     */               }
/*     */             } 
/*     */           }
/*     */         }() -> {
/*     */           int row = treeTable.getTree().getRowForPath(path);
/*     */           if (row >= 0) {
/*     */             treeTable.clearSelection();
/*     */             treeTable.addRowSelectionInterval(row, row);
/*     */             scrollToSelectedRow((TreeTable)treeTable);
/*     */           } else {
/*     */             treeTable.getTree().setSelectionPath(path);
/*     */             treeTable.getTree().scrollPathToVisible(path);
/*     */           } 
/*     */         });
/*     */   }
/*     */   
/*     */   public static abstract class ConditionalExpander
/*     */   {
/*     */     protected final TreeTable myTable;
/*     */     private final ArrayDeque<TreePath> myQueue;
/*     */     protected TreePath myPathToSelect;
/*     */     
/*     */     public ConditionalExpander(TreeTable table) {
/* 426 */       this.myTable = table;
/* 427 */       this.myQueue = new ArrayDeque<>();
/*     */     }
/*     */     
/*     */     public void execute() {
/* 431 */       if (!collectInitialLines())
/* 432 */         return;  TreeTableTree tree = this.myTable.getTree();
/* 433 */       TreeTableModel model = this.myTable.getTableModel();
/* 434 */       while (!this.myQueue.isEmpty()) {
/* 435 */         TreePath path = this.myQueue.removeFirst();
/* 436 */         if (toExpand(path)) {
/* 437 */           tree.expandPath(path);
/*     */           
/* 439 */           Object component = path.getLastPathComponent();
/* 440 */           int count = model.getChildCount(component);
/* 441 */           for (int i = 0; i < count; i++) {
/* 442 */             this.myQueue.add(path.pathByAddingChild(model.getChild(component, i)));
/*     */           }
/*     */         } 
/*     */       } 
/* 446 */       if (this.myPathToSelect != null) {
/* 447 */         this.myTable.clearSelection();
/* 448 */         int rowForPath = tree.getRowForPath(this.myPathToSelect);
/* 449 */         if (rowForPath >= 0) {
/* 450 */           this.myTable.addRowSelectionInterval(rowForPath, rowForPath);
/*     */         }
/* 452 */         V8ProfilingCallTreeComponent.scrollToSelectedRow(this.myTable);
/*     */       } 
/*     */     }
/*     */     
/*     */     protected abstract boolean toExpand(TreePath param1TreePath);
/*     */     
/*     */     private boolean collectInitialLines() {
/* 459 */       TreeTableTree tree = this.myTable.getTree();
/* 460 */       int count = tree.getRowCount();
/* 461 */       if (count == 0) return false; 
/* 462 */       for (int i = 0; i < count; i++) {
/* 463 */         TreePath path = tree.getPathForRow(i);
/* 464 */         this.myQueue.add(path);
/*     */       } 
/* 466 */       return true;
/*     */     }
/*     */   }
/*     */   
/*     */   public static void scrollToSelectedRow(@NotNull TreeTable table) {
/* 471 */     if (table == null) $$$reportNull$$$0(12);  SwingUtilities.invokeLater(() -> {
/*     */           int row = table.getSelectedRow();
/*     */           if (row >= 0) {
/*     */             int rowCount = table.getRowCount();
/*     */             int scrollRowBelow = row + Math.min(rowCount - 1 - row, 2);
/*     */             int scrollRowAbove = row - Math.min(2, row);
/*     */             Rectangle below = table.getCellRect(scrollRowBelow, 0, true);
/*     */             Rectangle above = table.getCellRect(scrollRowAbove, 0, true);
/*     */             table.scrollRectToVisible(new Rectangle(above.x, above.y, below.width, above.x - below.x + below.height));
/*     */           } 
/*     */         });
/*     */   }
/*     */ }


/* Location:              C:\Program Files\JetBrains\WebStorm 2021.1\plugins\NodeJS\lib\NodeJS.jar!\com\jetbrains\nodejs\run\profile\cpu\view\V8ProfilingCallTreeComponent.class
 * Java compiler version: 11 (55.0)
 * JD-Core Version:       1.1.3
 */