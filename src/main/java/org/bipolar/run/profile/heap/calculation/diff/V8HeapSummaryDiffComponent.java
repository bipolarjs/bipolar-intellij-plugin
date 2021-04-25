/*     */ package org.bipolar.run.profile.heap.calculation.diff;
/*     */ import com.intellij.openapi.actionSystem.ActionGroup;
/*     */ import com.intellij.openapi.actionSystem.ActionManager;
/*     */ import com.intellij.openapi.actionSystem.AnAction;
/*     */ import com.intellij.openapi.actionSystem.AnActionEvent;
/*     */ import com.intellij.openapi.actionSystem.DataKey;
/*     */ import com.intellij.openapi.actionSystem.DefaultActionGroup;
/*     */ import com.intellij.openapi.project.Project;
/*     */ import com.intellij.openapi.ui.popup.IPopupChooserBuilder;
/*     */ import com.intellij.openapi.ui.popup.JBPopup;
/*     */ import com.intellij.openapi.ui.popup.JBPopupFactory;
/*     */ import com.intellij.ui.SpeedSearchComparator;
/*     */ import com.intellij.ui.TreeTableSpeedSearch;
/*     */ import com.intellij.ui.components.JBScrollPane;
/*     */ import com.intellij.ui.treeStructure.treetable.TreeTable;
/*     */ import com.intellij.util.BeforeAfter;
/*     */ import com.intellij.util.Consumer;
/*     */ import com.intellij.util.containers.ContainerUtil;
/*     */ import org.bipolar.NodeJSBundle;
/*     */ import org.bipolar.run.profile.V8Utils;
/*     */ import org.bipolar.run.profile.heap.view.components.DataProviderPanel;
/*     */ import org.bipolar.run.profile.heap.view.components.V8HeapTreeTable;
/*     */ import org.bipolar.run.profile.heap.view.models.V8HeapContainmentTreeTableModel;
/*     */ import javax.swing.JComponent;
/*     */ import javax.swing.tree.TreeCellRenderer;
/*     */ import javax.swing.tree.TreePath;
/*     */
import org.jetbrains.annotations.Nullable;
/*     */ 
/*     */ public class V8HeapSummaryDiffComponent implements ProfilingView<V8HeapTreeTable> {
/*  30 */   public static final DataKey<BeforeAfter<V8HeapContainmentTreeTableModel.NamedEntry>> SELECTED_PAIR = DataKey.create("Node.Js.Profiling.Heap.Diff.Selected.Pair");
/*     */   private final V8HeapTreeTable myTable;
/*     */   private final Project myProject;
/*     */   private final V8DiffCachingReader myReader;
/*     */   private final String myBaseName;
/*     */   private final String myChangedName;
/*     */   
/*     */   public V8HeapSummaryDiffComponent(Project project, V8DiffCachingReader reader, String baseName, String changedName) {
/*  38 */     this.myProject = project;
/*  39 */     this.myReader = reader;
/*  40 */     this.myBaseName = baseName;
/*  41 */     this.myChangedName = changedName;
/*  42 */     this.myTable = new V8HeapTreeTable((TreeTableModel)new AggregatesDiffTableModel(project, reader), reader.getResourses());
/*  43 */     TreeTableSpeedSearch search = new TreeTableSpeedSearch((TreeTable)this.myTable, o -> {
/*     */           Object component = o.getLastPathComponent();
/*     */           
/*     */           if (component instanceof AggregatesViewDiff.AggregateDifference) {
/*     */             return ((AggregatesViewDiff.AggregateDifference)component).getClassIdx();
/*     */           }
/*     */           if (component instanceof BeforeAfter) {
/*     */             BeforeAfter<V8HeapContainmentTreeTableModel.NamedEntry> beforeAfter = (BeforeAfter<V8HeapContainmentTreeTableModel.NamedEntry>)component;
/*     */             return (beforeAfter.getAfter() != null) ? ((V8HeapContainmentTreeTableModel.NamedEntry)beforeAfter.getAfter()).getName() : ((V8HeapContainmentTreeTableModel.NamedEntry)beforeAfter.getBefore()).getName();
/*     */           } 
/*     */           return component.toString();
/*     */         });
/*  55 */     search.setComparator(new SpeedSearchComparator(false, true));
/*  56 */     V8Utils.afterModelReset(this.myTable, null, reader.getResourses(), (TreeCellRenderer)new DiffCellRenderer(project, reader
/*  57 */           .getBaseReader(), reader.getChangedReader(), AggregateDifferenceEmphasizer.getInstance()));
/*     */   }
/*     */ 
/*     */   
/*     */   public JComponent getMainComponent() {
/*  62 */     DataProviderPanel panel = DataProviderPanel.wrap((JComponent)new JBScrollPane((Component)this.myTable));
/*  63 */     panel.register(SELECTED_PAIR.getName(), () -> {
/*     */           TreePath value = this.myTable.getTree().getSelectionPath();
/*  65 */           return (value != null && value.getLastPathComponent() instanceof BeforeAfter) ? value.getLastPathComponent() : null;
/*     */         });
/*     */ 
/*     */ 
/*     */     
/*  70 */     return (JComponent)panel;
/*     */   }
/*     */ 
/*     */   
/*     */   public String getName() {
/*  75 */     return NodeJSBundle.message("profile.heap.summary.diff.name", new Object[0]);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void addActions(DefaultActionGroup group) {
/*  82 */     MarkUnmarkDiffAction markUnmarkDiffAction = new MarkUnmarkDiffAction(this.myProject, this.myBaseName, this.myChangedName, this.myReader.getBaseReader().getDigest(), this.myReader.getChangedReader().getDigest());
/*     */ 
/*     */     
/*  85 */     NavigateInSourceSnapshotAction navigateInSourceSnapshotAction = new NavigateInSourceSnapshotAction(this.myProject, this.myBaseName, this.myChangedName, this.myReader.getBaseReader().getDigest(), this.myReader.getChangedReader().getDigest());
/*     */     
/*  87 */     group.add((AnAction)markUnmarkDiffAction);
/*  88 */     group.add((AnAction)navigateInSourceSnapshotAction);
/*     */     
/*  90 */     DefaultActionGroup popupGroup = new DefaultActionGroup();
/*  91 */     popupGroup.add((AnAction)markUnmarkDiffAction);
/*  92 */     popupGroup.add((AnAction)navigateInSourceSnapshotAction);
/*  93 */     PopupHandler.installPopupHandler((JComponent)this.myTable, (ActionGroup)popupGroup, "V8_HEAP_DIFF_PROFILING_POPUP", ActionManager.getInstance());
/*     */   }
/*     */ 
/*     */   
/*     */   public String getError() {
/*  98 */     return null;
/*     */   }
/*     */ 
/*     */   
/*     */   @Nullable
/*     */   public V8HeapTreeTable getTreeTable() {
/* 104 */     return this.myTable;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void defaultExpand() {}
/*     */ 
/*     */ 
/*     */   
/*     */   static void selectSourceProfile(String baseName, String changedName, AnActionEvent e, Consumer<? super Boolean> consumer) {
/* 115 */     String before = "Before: " + baseName;
/* 116 */     String after = "After: " + changedName;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/* 125 */     IPopupChooserBuilder<String> popupChooserBuilder = JBPopupFactory.getInstance().createPopupChooserBuilder(ContainerUtil.newArrayList((Object[])new String[] { before, after })).setTitle(NodeJSBundle.message("popup.title.select.source.profile", new Object[0])).setMovable(true).setResizable(true).setCancelKeyEnabled(true).setCancelOnWindowDeactivation(true).setCancelOnClickOutside(true).setItemChosenCallback(v -> consumer.consume(Boolean.valueOf(v.equals(before))));
/* 126 */     JBPopup popup = popupChooserBuilder.createPopup();
/* 127 */     V8Utils.showPopup(e, popup);
/*     */   }
/*     */ }


/* Location:              C:\Program Files\JetBrains\WebStorm 2021.1\plugins\NodeJS\lib\NodeJS.jar!\com\jetbrains\nodejs\run\profile\heap\calculation\diff\V8HeapSummaryDiffComponent.class
 * Java compiler version: 11 (55.0)
 * JD-Core Version:       1.1.3
 */