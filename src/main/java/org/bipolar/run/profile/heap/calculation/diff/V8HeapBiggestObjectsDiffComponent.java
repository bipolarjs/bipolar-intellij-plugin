/*     */ package org.bipolar.run.profile.heap.calculation.diff;
/*     */ 
/*     */ import com.intellij.openapi.actionSystem.ActionGroup;
/*     */ import com.intellij.openapi.actionSystem.ActionManager;
/*     */ import com.intellij.openapi.actionSystem.AnAction;
/*     */ import com.intellij.openapi.actionSystem.DefaultActionGroup;
/*     */ import com.intellij.openapi.project.Project;
/*     */ import com.intellij.ui.PopupHandler;
/*     */ import com.intellij.ui.SpeedSearchComparator;
/*     */ import com.intellij.ui.TreeTableSpeedSearch;
/*     */ import com.intellij.ui.components.JBScrollPane;
/*     */ import com.intellij.ui.treeStructure.treetable.TreeTable;
/*     */ import com.intellij.ui.treeStructure.treetable.TreeTableModel;
/*     */ import com.intellij.util.BeforeAfter;
/*     */ import org.bipolar.NodeJSBundle;
/*     */ import org.bipolar.run.profile.V8Utils;
/*     */ import org.bipolar.run.profile.cpu.view.ProfilingView;
/*     */ import org.bipolar.run.profile.heap.view.components.DataProviderPanel;
/*     */ import org.bipolar.run.profile.heap.view.components.V8HeapTreeTable;
/*     */ import org.bipolar.run.profile.heap.view.models.V8HeapContainmentTreeTableModel;
/*     */ import java.awt.Component;
/*     */ import javax.swing.JComponent;
/*     */ import javax.swing.tree.TreeCellRenderer;
/*     */ import javax.swing.tree.TreePath;
/*     */
import org.jetbrains.annotations.Nullable;
/*     */ 
/*     */ public class V8HeapBiggestObjectsDiffComponent
/*     */   implements ProfilingView<V8HeapTreeTable>
/*     */ {
/*     */   private final V8HeapTreeTable myTable;
/*     */   private final Project myProject;
/*     */   
/*     */   public V8HeapBiggestObjectsDiffComponent(Project project, V8DiffCachingReader reader, String baseName, String changedName) {
/*  34 */     this.myProject = project;
/*  35 */     this.myReader = reader;
/*  36 */     this.myBaseName = baseName;
/*  37 */     this.myChangedName = changedName;
/*     */     
/*  39 */     this.myTable = new V8HeapTreeTable((TreeTableModel)new BiggestObjectsDiffTableModel(project, reader), reader.getResourses());
/*  40 */     TreeTableSpeedSearch search = new TreeTableSpeedSearch((TreeTable)this.myTable, o -> {
/*     */           Object component = o.getLastPathComponent();
/*     */           
/*     */           if (component instanceof BeforeAfter) {
/*     */             BeforeAfter<V8HeapContainmentTreeTableModel.NamedEntry> beforeAfter = (BeforeAfter<V8HeapContainmentTreeTableModel.NamedEntry>)component;
/*     */             
/*     */             return (beforeAfter.getAfter() != null) ? ((V8HeapContainmentTreeTableModel.NamedEntry)beforeAfter.getAfter()).getName() : ((V8HeapContainmentTreeTableModel.NamedEntry)beforeAfter.getBefore()).getName();
/*     */           } 
/*     */           return component.toString();
/*     */         });
/*  50 */     search.setComparator(new SpeedSearchComparator(false, true));
/*  51 */     String sample = "" + reader.getChangedReader().getRetainedSize(0) + " 100% a b";
/*  52 */     V8Utils.afterModelReset(this.myTable, sample, reader.getResourses(), (TreeCellRenderer)new DiffCellRenderer(project, reader
/*  53 */           .getBaseReader(), reader.getChangedReader(), 
/*  54 */           AggregateDifferenceEmphasizer.getInstance()));
/*     */   }
/*     */   private final V8DiffCachingReader myReader; private final String myBaseName; private final String myChangedName;
/*     */   
/*     */   public JComponent getMainComponent() {
/*  59 */     DataProviderPanel panel = DataProviderPanel.wrap((JComponent)new JBScrollPane((Component)this.myTable));
/*  60 */     panel.register(V8HeapSummaryDiffComponent.SELECTED_PAIR.getName(), () -> {
/*     */           TreePath value = this.myTable.getTree().getSelectionPath();
/*  62 */           return (value != null && value.getLastPathComponent() instanceof BeforeAfter) ? value.getLastPathComponent() : null;
/*     */         });
/*     */ 
/*     */ 
/*     */     
/*  67 */     return (JComponent)panel;
/*     */   }
/*     */ 
/*     */   
/*     */   public String getName() {
/*  72 */     return NodeJSBundle.message("profile.heap.biggest.objects.diff.name", new Object[0]);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void addActions(DefaultActionGroup group) {
/*  79 */     MarkUnmarkDiffAction markUnmarkDiffAction = new MarkUnmarkDiffAction(this.myProject, this.myBaseName, this.myChangedName, this.myReader.getBaseReader().getDigest(), this.myReader.getChangedReader().getDigest());
/*     */ 
/*     */     
/*  82 */     NavigateInSourceSnapshotAction navigateInSourceSnapshotAction = new NavigateInSourceSnapshotAction(this.myProject, this.myBaseName, this.myChangedName, this.myReader.getBaseReader().getDigest(), this.myReader.getChangedReader().getDigest());
/*     */     
/*  84 */     group.add((AnAction)markUnmarkDiffAction);
/*  85 */     group.add((AnAction)navigateInSourceSnapshotAction);
/*     */     
/*  87 */     DefaultActionGroup popupGroup = new DefaultActionGroup();
/*  88 */     popupGroup.add((AnAction)markUnmarkDiffAction);
/*  89 */     popupGroup.add((AnAction)navigateInSourceSnapshotAction);
/*  90 */     PopupHandler.installPopupHandler((JComponent)this.myTable, (ActionGroup)popupGroup, "V8_HEAP_DIFF_PROFILING_POPUP", ActionManager.getInstance());
/*     */   }
/*     */ 
/*     */   
/*     */   public String getError() {
/*  95 */     return null;
/*     */   }
/*     */ 
/*     */   
/*     */   @Nullable
/*     */   public V8HeapTreeTable getTreeTable() {
/* 101 */     return this.myTable;
/*     */   }
/*     */   
/*     */   public void defaultExpand() {}
/*     */ }


/* Location:              C:\Program Files\JetBrains\WebStorm 2021.1\plugins\NodeJS\lib\NodeJS.jar!\com\jetbrains\nodejs\run\profile\heap\calculation\diff\V8HeapBiggestObjectsDiffComponent.class
 * Java compiler version: 11 (55.0)
 * JD-Core Version:       1.1.3
 */