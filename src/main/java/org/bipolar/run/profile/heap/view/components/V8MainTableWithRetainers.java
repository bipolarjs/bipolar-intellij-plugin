/*     */ package org.bipolar.run.profile.heap.view.components;
/*     */ 
/*     */ import com.intellij.CommonBundle;
/*     */ import com.intellij.execution.ui.layout.impl.JBRunnerTabs;
/*     */ import com.intellij.openapi.Disposable;
/*     */ import com.intellij.openapi.actionSystem.AnAction;
/*     */ import com.intellij.openapi.actionSystem.DefaultActionGroup;
/*     */ import com.intellij.openapi.application.ApplicationManager;
/*     */ import com.intellij.openapi.application.ModalityState;
/*     */ import com.intellij.openapi.project.Project;
/*     */ import com.intellij.openapi.ui.MessageType;
/*     */ import com.intellij.openapi.util.Comparing;
/*     */ import com.intellij.openapi.util.Getter;
/*     */ import com.intellij.openapi.util.NlsContexts.TabTitle;
/*     */ import com.intellij.openapi.util.Pair;
/*     */ import com.intellij.openapi.util.ZipperUpdater;
/*     */ import com.intellij.ui.JBSplitter;
/*     */ import com.intellij.ui.SpeedSearchComparator;
/*     */ import com.intellij.ui.TreeTableSpeedSearch;
/*     */ import com.intellij.ui.components.JBScrollPane;
/*     */ import com.intellij.ui.tabs.JBTabs;
/*     */ import com.intellij.ui.tabs.TabInfo;
/*     */ import com.intellij.ui.treeStructure.treetable.TreeTable;
/*     */ import com.intellij.ui.treeStructure.treetable.TreeTableModel;
/*     */ import com.intellij.util.ArrayUtil;
/*     */ import com.intellij.util.ConcurrencyUtil;
/*     */ import com.intellij.util.concurrency.EdtExecutorService;
/*     */ import org.bipolar.NodeJSBundle;
/*     */ import org.bipolar.run.profile.CloseTabAction;
/*     */ import org.bipolar.run.profile.V8Utils;
/*     */ import org.bipolar.run.profile.cpu.view.TreeTableModelWithCustomRenderer;
/*     */ import org.bipolar.run.profile.heap.CompositeCloseable;
/*     */ import org.bipolar.run.profile.heap.V8CachingReader;
/*     */ import org.bipolar.run.profile.heap.data.Aggregate;
/*     */ import org.bipolar.run.profile.heap.data.NodeSurrounders;
/*     */ import org.bipolar.run.profile.heap.data.V8HeapEdge;
/*     */ import org.bipolar.run.profile.heap.data.V8HeapEntry;
/*     */ import org.bipolar.run.profile.heap.data.V8HeapGraphEdgeType;
/*     */ import org.bipolar.run.profile.heap.data.V8HeapNodeType;
/*     */ import org.bipolar.run.profile.heap.view.actions.MarkUnmarkAction;
/*     */ import org.bipolar.run.profile.heap.view.actions.V8NavigateToMainTreeAction;
/*     */ import org.bipolar.run.profile.heap.view.models.RetainersTreeModel;
/*     */ import org.bipolar.run.profile.heap.view.models.SearchDetailsTreeModel;
/*     */ import org.bipolar.run.profile.heap.view.models.V8HeapContainmentTreeTableModel;
/*     */
/*     */ import org.bipolar.run.profile.heap.view.nodes.FixedRetainerNode;
/*     */ import org.bipolar.run.profile.settings.NodeProfilingSettings;
/*     */ import org.bipolar.util.ui.UIHelper;
/*     */ import java.awt.BorderLayout;
/*     */ import java.awt.Component;
/*     */ import java.io.Closeable;
/*     */ import java.util.ArrayList;
/*     */ import java.util.Arrays;
/*     */ import java.util.List;
/*     */ import java.util.concurrent.ExecutorService;
/*     */ import java.util.concurrent.TimeUnit;
/*     */ import java.util.concurrent.atomic.AtomicReference;
/*     */ import javax.swing.JComponent;
/*     */ import javax.swing.JPanel;
/*     */ import javax.swing.JTree;
/*     */ import javax.swing.SwingUtilities;
/*     */ import javax.swing.event.TreeSelectionEvent;
/*     */ import javax.swing.event.TreeSelectionListener;
/*     */ import javax.swing.tree.TreePath;
/*     */
import org.jetbrains.annotations.NotNull;
/*     */ import org.jetbrains.annotations.Nullable;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class V8MainTableWithRetainers<T extends TreeTableModelWithCustomRenderer>
/*     */ {
/*     */   @NotNull
/*     */   private final Project myProject;
/*     */   private final T myMainTreeModel;
/*     */   @NotNull
/*     */   private final V8CachingReader myReader;
/*     */   @NotNull
/*     */   private final CompositeCloseable myCloseable;
/*     */   private final Runnable myRetainersUpdater;
/*     */   private V8MainTreeNavigator myMainTreeNavigator;
/*     */   private final JPanel myDetailsPanel;
/*     */   private RetainersTreeModel myRetainersTreeModel;
/*     */   private final JBSplitter myMainSplitter;
/*     */   private final V8HeapTreeTable myTable;
/*     */   private final ZipperUpdater myUpdater;
/*     */   private final AtomicReference<Pair<Pair<V8HeapEntry, V8HeapEdge>, TreePath>> myMainSelection;
/*     */   private final AtomicReference<NodeSurrounders> myLastCalculatedSelectionValue;
/*     */   private final JBSplitter mySecondSplitter;
/*     */   private final JBTabs myDetails;
/*     */   private boolean myUseTreeSelectionForRetainers;
/*     */   
/*     */   public V8MainTableWithRetainers(@NotNull Project project, @NotNull T mainTreeModel, @NotNull V8CachingReader reader, @NotNull CompositeCloseable closeable, @NotNull Disposable disposable) {
/*  94 */     this.myProject = project;
/*  95 */     this.myMainTreeModel = mainTreeModel;
/*  96 */     this.myReader = reader;
/*  97 */     this.myCloseable = closeable;
/*  98 */     this.myUseTreeSelectionForRetainers = true;
/*  99 */     this.myUpdater = new ZipperUpdater(300, disposable);
/* 100 */     closeable.register(new Closeable()
/*     */         {
/*     */           public void close() {
/* 103 */             V8MainTableWithRetainers.this.myUpdater.stop();
/*     */           }
/*     */         });
/* 106 */     this.myTable = V8Utils.createTable(project, (TreeTableModel)this.myMainTreeModel, this.myReader);
/* 107 */     TreeTableSpeedSearch search = new TreeTableSpeedSearch((TreeTable)this.myTable, o -> {
/*     */           Object component = o.getLastPathComponent();
/*     */ 
/*     */           
/*     */           return (component instanceof Aggregate) ? ((Aggregate)component).getPresentation(this.myReader) : component.toString();
/*     */         });
/*     */     
/* 114 */     search.setComparator(new SpeedSearchComparator(false, true));
/*     */     
/* 116 */     this.myDetails = (JBTabs)JBRunnerTabs.create(project, disposable);
/* 117 */     this.myDetailsPanel = new JPanel(new BorderLayout());
/* 118 */     this.myDetails.addTab((new TabInfo(this.myDetailsPanel)).setText(NodeJSBundle.message("profile.cpu.main_table.details.text", new Object[0])));
/* 119 */     updateDetails(UIHelper.wrapInCenteredPanel(NodeJSBundle.message("profile.cpu.nothing_to_show.label", new Object[0])));
/*     */     
/* 121 */     this.myMainSplitter = new JBSplitter(false);
/* 122 */     this.mySecondSplitter = new JBSplitter(true);
/* 123 */     this.mySecondSplitter.setFirstComponent((JComponent)new JBScrollPane((Component)this.myTable));
/* 124 */     this.mySecondSplitter.setSecondComponent(this.myDetails.getComponent());
/* 125 */     this.myMainSplitter.setFirstComponent((JComponent)this.mySecondSplitter);
/*     */     
/* 127 */     this.myMainSelection = new AtomicReference<>();
/* 128 */     this.myLastCalculatedSelectionValue = new AtomicReference<>();
/* 129 */     final ExecutorService threadExecutor = ConcurrencyUtil.newSingleThreadExecutor("V8 calculate retainers");
/* 130 */     closeable.register(new Closeable()
/*     */         {
/*     */           public void close() {
/* 133 */             threadExecutor.shutdownNow();
/*     */           }
/*     */         });
/* 136 */     this.myRetainersUpdater = (() -> {
/*     */         if (this.myLastCalculatedSelectionValue.get() != null && this.myMainSelection.get() != null && Comparing.equal(((NodeSurrounders)this.myLastCalculatedSelectionValue.get()).getNodeId(), ((Pair)this.myMainSelection.get()).getFirst())) {
/*     */           updateRetainersView();
/*     */           
/*     */           return;
/*     */         } 
/*     */         
/*     */         threadExecutor.execute(());
/*     */       });
/*     */     
/* 146 */     mainTableSelectionListener();
/* 147 */     this.myMainTreeNavigator = new MyV8MainTreeNavigator(project);
/*     */   }
/*     */   
/*     */   private void updateDetails(JComponent component) {
/* 151 */     this.myDetailsPanel.removeAll();
/* 152 */     this.myDetailsPanel.add(component, "Center");
/* 153 */     this.myDetailsPanel.revalidate();
/* 154 */     this.myDetailsPanel.repaint();
/*     */   }
/*     */   
/*     */   public void addTab(@TabTitle String text, JComponent component, DefaultActionGroup group, boolean selectTab) {
/* 158 */     CloseTabAction action = new CloseTabAction(this.myDetails);
/* 159 */     group.add((AnAction)action);
/* 160 */     TabInfo info = addTabWithoutClose(text, component, group, selectTab);
/* 161 */     action.setInfo(info);
/*     */   }
/*     */   
/*     */   public TabInfo addTabWithoutClose(@TabTitle String text, JComponent component, DefaultActionGroup group, boolean selectTab) {
/* 165 */     JComponent wrapper = V8Utils.wrapWithActions(component, group);
/* 166 */     TabInfo info = (new TabInfo(wrapper)).setText(text);
/* 167 */     this.myDetails.addTab(info);
/* 168 */     if (selectTab) {
/* 169 */       this.myDetails.select(info, true);
/*     */     }
/* 171 */     return info;
/*     */   }
/*     */   
/*     */   public V8MainTreeNavigator getMainTreeNavigator() {
/* 175 */     return this.myMainTreeNavigator;
/*     */   }
/*     */ 
/*     */   
/*     */   public JBSplitter getMainSplitter() {
/* 180 */     return this.myMainSplitter;
/*     */   }
/*     */   
/*     */   public V8HeapTreeTable getTable() {
/* 184 */     return this.myTable;
/*     */   }
/*     */   
/*     */   public T getMainTreeModel() {
/* 188 */     return this.myMainTreeModel;
/*     */   }
/*     */   
/*     */   public RetainersTreeModel getRetainersTreeModel() {
/* 192 */     return this.myRetainersTreeModel;
/*     */   }
/*     */   
/*     */   private void calculateRetainers() {
/* 196 */     Pair<Pair<V8HeapEntry, V8HeapEdge>, TreePath> selection = this.myMainSelection.get();
/* 197 */     if (this.myLastCalculatedSelectionValue.get() != null && Comparing.equal(((NodeSurrounders)this.myLastCalculatedSelectionValue.get()).getNodeId(), selection.getFirst()))
/*     */       return; 
/* 199 */     V8HeapEntry selectedEntry = (V8HeapEntry)((Pair)selection.getFirst()).getFirst();
/* 200 */     int count = this.myReader.getRetainersCount(selectedEntry);
/* 201 */     List<Pair<V8HeapEntry, V8HeapEdge>> retainers = new ArrayList<>();
/* 202 */     if (count > 0) {
/* 203 */       for (int i = 0; i < count; i++) {
/* 204 */         Pair<V8HeapEntry, V8HeapEdge> childPair = this.myReader.getRetainerChild(selectedEntry, i);
/* 205 */         if (this.myReader.isShowHidden() || (!V8HeapNodeType.kHidden.equals(((V8HeapEntry)childPair.getFirst()).getType()) && 
/* 206 */           !V8HeapGraphEdgeType.kHidden.equals(((V8HeapEdge)childPair.getSecond()).getType()))) {
/* 207 */           retainers.add(childPair);
/*     */         }
/*     */       } 
/*     */     }
/* 211 */     this.myLastCalculatedSelectionValue.set(new NodeSurrounders((Pair)selection.getFirst(), (selection.getSecond() == null) ? null : ((TreePath)selection.getSecond()).getPath(), retainers));
/* 212 */     updateRetainersView();
/*     */   }
/*     */   
/*     */   private void updateRetainersView() {
/* 216 */     ApplicationManager.getApplication().invokeLater(() -> {
/*     */           NodeSurrounders surrounders = this.myLastCalculatedSelectionValue.get();
/*     */           if (surrounders == null) {
/*     */             return;
/*     */           }
/*     */           Pair<V8HeapEntry, V8HeapEdge> realRoot = (this.myRetainersTreeModel == null) ? null : Pair.create(this.myRetainersTreeModel.getMain(), this.myRetainersTreeModel.getMainEdge());
/*     */           if (realRoot != null && Comparing.equal(surrounders.getNodeId(), realRoot)) {
/*     */             return;
/*     */           }
/*     */           if (this.myMainSelection.get() == null || !Comparing.equal(((Pair)this.myMainSelection.get()).getFirst(), surrounders.getNodeId())) {
/*     */             return;
/*     */           }
/*     */           this.myRetainersTreeModel = new RetainersTreeModel(this.myProject, this.myReader, (V8HeapEntry)surrounders.getNodeId().getFirst(), (V8HeapEdge)surrounders.getNodeId().getSecond(), surrounders.getRetainers(), surrounders.getPathToRoot());
/*     */           final V8HeapTreeTable retainersTable = new V8HeapTreeTable((TreeTableModel)this.myRetainersTreeModel, this.myReader.getResourses());
/*     */           retainersTable.setRootVisible(false);
/*     */           retainersTable.setSelectionMode(0);
/*     */           DataProviderPanel wrap = DataProviderPanel.wrap((JComponent)new JBScrollPane((Component)retainersTable));
/*     */           wrap.register(V8NavigateToMainTreeAction.TREE_PATH.getName(), ());
/*     */           wrap.register(MarkUnmarkAction.SELECTED_NODE.getName(), ());
/*     */           wrap.register(MarkUnmarkAction.REVALIDATION.getName(), new Getter<Object>()
/*     */               {
/*     */                 private final Runnable runnable;
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
/*     */                 public Object get() {
/* 256 */                   return this.runnable;
/*     */                 }
/*     */               });
/*     */           
/*     */           updateDetails((JComponent)wrap);
/*     */           
/*     */           retainersTable.setModel((TreeTableModel)this.myRetainersTreeModel);
/*     */           
/*     */           V8Utils.afterModelReset(this.myProject, this.myReader, retainersTable);
/*     */           
/*     */           this.myRetainersTreeModel.expandByDefault(retainersTable);
/*     */           V8Utils.installHeapPopupMenu(this.myProject, (TreeTable)retainersTable, this.myReader, this.myMainTreeNavigator);
/* 268 */         }ModalityState.any(), o -> this.myCloseable.isDisposeStarted());
/*     */   }
/*     */   
/*     */   private void mainTableSelectionListener() {
/* 272 */     this.myTable.getTree().getSelectionModel().setSelectionMode(1);
/* 273 */     this.myTable.getTree().addTreeSelectionListener(new TreeSelectionListener()
/*     */         {
/*     */           public void valueChanged(TreeSelectionEvent e)
/*     */           {
/* 277 */             TreePath path = V8MainTableWithRetainers.this.myTable.getTree().getSelectionPath();
/* 278 */             int row = V8MainTableWithRetainers.this.myTable.getSelectedRow();
/*     */             
/* 280 */             if (path != null && path.getLastPathComponent() instanceof V8HeapContainmentTreeTableModel.NamedEntry) {
/* 281 */               V8HeapContainmentTreeTableModel.NamedEntry namedEntry = (V8HeapContainmentTreeTableModel.NamedEntry)path.getLastPathComponent();
/* 282 */               if (V8MainTableWithRetainers.this.myMainSelection.get() != null && 
/* 283 */                 Comparing.equal(((Pair)((Pair)V8MainTableWithRetainers.this.myMainSelection.get()).getFirst()).getFirst(), namedEntry.getEntry()) && 
/* 284 */                 Comparing.equal(((Pair)V8MainTableWithRetainers.this.myMainSelection.get()).getSecond(), path)) {
/*     */                 return;
/*     */               }
/*     */ 
/*     */               
/* 289 */               V8MainTableWithRetainers.this.updateDetails(UIHelper.wrapInCenteredPanel(CommonBundle.getLoadingTreeNodeText()));
/* 290 */               V8MainTableWithRetainers.this.myRetainersTreeModel = null;
/* 291 */               V8HeapEdge heapEdge = (namedEntry.getLinkOffset() > 0L) ? V8MainTableWithRetainers.this.myReader.getEdge(namedEntry.getLinkOffset() / 37L) : null;
/* 292 */               V8MainTableWithRetainers.this.myMainSelection.set(Pair.create(Pair.create(namedEntry.getEntry(), heapEdge), V8MainTableWithRetainers.this.myUseTreeSelectionForRetainers ? path : null));
/* 293 */               V8MainTableWithRetainers.this.myUpdater.queue(V8MainTableWithRetainers.this.myRetainersUpdater);
/*     */             } else {
/* 295 */               V8MainTableWithRetainers.this.myMainSelection.set(null);
/* 296 */               V8MainTableWithRetainers.this.updateDetails(UIHelper.wrapInCenteredPanel(NodeJSBundle.message("profile.cpu.nothing_to_show.label", new Object[0])));
/* 297 */               V8MainTableWithRetainers.this.myRetainersTreeModel = null;
/*     */             } 
/*     */           }
/*     */         });
/*     */   }
/*     */   
/*     */   public void setUseTreeSelectionForRetainers(boolean useTreeSelectionForRetainers) {
/* 304 */     this.myUseTreeSelectionForRetainers = useTreeSelectionForRetainers;
/*     */   }
/*     */   
/*     */   public void setMainTreeNavigator(V8MainTreeNavigator mainTableNavigator) {
/* 308 */     this.myMainTreeNavigator = mainTableNavigator;
/*     */   }
/*     */   
/*     */   private final class MyV8MainTreeNavigator
/*     */     implements V8MainTreeNavigator
/*     */   {
/*     */     private MyV8MainTreeNavigator(Project project) {
/* 315 */       this.myProject = project;
/*     */     } @NotNull
/*     */     private final Project myProject;
/*     */     private boolean showError() {
/* 319 */       NodeProfilingSettings.HEAP_NOTIFICATION_GROUP.createNotification(
/* 320 */           NodeJSBundle.message("profile.cpu.main_table.cannot_navigate.notification.content", new Object[0]), MessageType.WARNING)
/* 321 */         .notify(this.myProject);
/* 322 */       return false;
/*     */     }
/*     */ 
/*     */     
/*     */     public boolean navigateTo(@NotNull TreePath path) {
/* 327 */       if (path == null) $$$reportNull$$$0(1);  TreePath convertedPath = V8MainTableWithRetainers.this.myReader.translateIntoPathFromNodesChain(path.getPath());
/* 328 */       if (convertedPath == null) return showError(); 
/* 329 */       expandImpl((TreeTable)V8MainTableWithRetainers.this.myTable, convertedPath);
/* 330 */       return true;
/*     */     }
/*     */     
/*     */     private void expandImpl(TreeTable table, TreePath convertedPath) {
/* 334 */       table.clearSelection();
/* 335 */       expandPathIteratively((JTree)table.getTree(), 0, convertedPath.getPath(), () -> {
/*     */             table.addSelectedPath(convertedPath);
/*     */             int row = table.getTree().getRowForPath(convertedPath);
/*     */             if (row >= 0) {
/*     */               table.scrollRectToVisible(table.getCellRect(row, 0, true));
/*     */             }
/*     */           });
/*     */     }
/*     */ 
/*     */     
/*     */     public boolean navigateTo(@NotNull V8HeapEntry node, @Nullable V8HeapEdge edgeToNode) {
/* 346 */       if (node == null) $$$reportNull$$$0(2); 
/* 347 */       List<V8HeapContainmentTreeTableModel.NamedEntry> nodes = new ArrayList<>(SearchDetailsTreeModel.getChainToRoot(-1, node, edgeToNode, V8MainTableWithRetainers.this.myReader));
/* 348 */       for (V8HeapContainmentTreeTableModel.NamedEntry entry : nodes) {
/* 349 */         if (entry instanceof FixedRetainerNode && ((FixedRetainerNode)entry).isUnreachable()) return showError(); 
/*     */       } 
/* 351 */       nodes.add(0, new V8HeapContainmentTreeTableModel.NamedEntry(V8MainTableWithRetainers.this.myReader.getNode(0L), "", "", -1L));
/* 352 */       TreePath path = V8MainTableWithRetainers.this.myReader.translateIntoPathFromNodesChain(ArrayUtil.toObjectArray(nodes));
/* 353 */       if (path == null) return showError(); 
/* 354 */       expandImpl((TreeTable)V8MainTableWithRetainers.this.myTable, path);
/* 355 */       return true;
/*     */     }
/*     */     
/*     */     private void expandPathIteratively(@NotNull JTree tree, int idxTo, Object[] pathElements, Runnable finalContinuation) {
/* 359 */       if (tree == null) $$$reportNull$$$0(3);  Object[] subPath = Arrays.copyOf(pathElements, idxTo + 1);
/* 360 */       tree.expandPath(new TreePath(subPath));
/* 361 */       SwingUtilities.invokeLater(() -> {
/*     */             if (pathElements.length == subPath.length) {
/*     */               EdtExecutorService.getScheduledExecutorInstance().schedule(finalContinuation, 20L, TimeUnit.MILLISECONDS);
/*     */             } else {
/*     */               expandPathIteratively(tree, idxTo + 1, pathElements, finalContinuation);
/*     */             } 
/*     */           });
/*     */     }
/*     */   }
/*     */ }


/* Location:              C:\Program Files\JetBrains\WebStorm 2021.1\plugins\NodeJS\lib\NodeJS.jar!\com\jetbrains\nodejs\run\profile\heap\view\components\V8MainTableWithRetainers.class
 * Java compiler version: 11 (55.0)
 * JD-Core Version:       1.1.3
 */