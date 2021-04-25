/*     */ package org.bipolar.run.profile.cpu.v8log.diff;
/*     */ 
/*     */ import com.intellij.openapi.Disposable;
/*     */ import com.intellij.openapi.actionSystem.AnAction;
/*     */ import com.intellij.openapi.actionSystem.DefaultActionGroup;
/*     */ import com.intellij.openapi.project.Project;
/*     */ import com.intellij.openapi.util.Pair;
/*     */ import com.intellij.ui.TreeTableSpeedSearch;
/*     */ import com.intellij.ui.components.JBScrollPane;
/*     */ import com.intellij.ui.treeStructure.treetable.TreeTable;
/*     */ import com.intellij.ui.treeStructure.treetable.TreeTableModel;
/*     */ import com.intellij.util.ui.ColumnInfo;
/*     */ import org.bipolar.NodeJSBundle;
/*     */ import org.bipolar.run.profile.TreeTableWidthController;
/*     */ import org.bipolar.run.profile.TreeTableWithTreeWidthController;
/*     */ import org.bipolar.run.profile.V8Utils;
/*     */ import org.bipolar.run.profile.cpu.CallHolder;
/*     */ import org.bipolar.run.profile.cpu.calculation.V8ProfileLine;
/*     */ import org.bipolar.run.profile.cpu.v8log.reading.V8LogCachingReader;
/*     */ import org.bipolar.run.profile.cpu.view.ProfilingView;
/*     */ import org.bipolar.run.profile.cpu.view.V8ProfilingCallTreeComponent;
/*     */ import org.bipolar.run.profile.cpu.view.V8ProfilingCallTreeTable;
/*     */ import org.bipolar.run.profile.heap.view.components.ChainTreeTableModel;
/*     */ import java.awt.Component;
/*     */ import java.util.List;
/*     */ import javax.swing.JComponent;
/*     */ import javax.swing.tree.TreeCellRenderer;
/*     */ import javax.swing.tree.TreePath;
/*     */
import org.jetbrains.annotations.NotNull;
/*     */ import org.jetbrains.annotations.Nullable;
/*     */ 
/*     */ public class V8CpuDiffFlatViewComponent implements ProfilingView<V8ProfilingCallTreeTable> {
/*     */   @NotNull
/*     */   private final Project myProject;
/*     */   private final Disposable myDisposable;
/*     */   @NotNull
/*     */   private final V8LogCachingReader myBaseReader;
/*     */   @NotNull
/*     */   private final V8LogCachingReader myChangedReader;
/*     */   @NotNull
/*     */   private final List<? extends Pair<String, List<DiffNode>>> myFlatDiff;
/*     */   private final DiffNodeTreeCellRenderer myRenderer;
/*     */   private JComponent myMainPane;
/*     */   private V8ProfilingCallTreeTable myTable;
/*     */   
/*     */   public V8CpuDiffFlatViewComponent(@NotNull Project project, Disposable disposable, @NotNull V8LogCachingReader baseReader, @NotNull V8LogCachingReader changedReader, @NotNull List<? extends Pair<String, List<DiffNode>>> flatDiff) {
/*  47 */     this.myProject = project;
/*  48 */     this.myDisposable = disposable;
/*  49 */     this.myBaseReader = baseReader;
/*  50 */     this.myChangedReader = changedReader;
/*  51 */     this.myFlatDiff = flatDiff;
/*  52 */     this.myRenderer = new DiffNodeTreeCellRenderer();
/*  53 */     this.myRenderer.setBaseTicks(Integer.valueOf((int)this.myBaseReader.getNumTicks()));
/*  54 */     this.myRenderer.setChangedTicks(Integer.valueOf((int)this.myChangedReader.getNumTicks()));
/*  55 */     createMainComponent();
/*     */   }
/*     */   
/*     */   private void createMainComponent() {
/*  59 */     List<ColumnInfo> columns = V8CpuDiffTableModel.createTopDownColumns((TreeCellRenderer)this.myRenderer, (int)this.myBaseReader.getNumTicks(), (int)this.myChangedReader.getNumTicks());
/*     */     
/*  61 */     V8DiffFlatViewTableModel mainModel = new V8DiffFlatViewTableModel(columns.<ColumnInfo>toArray(ColumnInfo.EMPTY_ARRAY), (int)this.myBaseReader.getNumTicks(), (int)this.myChangedReader.getNumTicks());
/*     */     
/*  63 */     for (Pair<String, List<DiffNode>> pair : this.myFlatDiff) {
/*  64 */       ChainTreeTableModel.Node keyNode = mainModel.createNode(pair.getFirst());
/*  65 */       mainModel.addTopKey(keyNode);
/*     */       
/*  67 */       for (DiffNode node : pair.getSecond()) {
/*  68 */         ChainTreeTableModel.Node modelNode = mainModel.createNode(node);
/*  69 */         keyNode.getChildren().add(modelNode);
/*     */       } 
/*     */     } 
/*     */     
/*  73 */     this.myTable = new V8ProfilingCallTreeTable(this.myProject, (TreeTableModel)mainModel, this.myDisposable)
/*     */       {
/*     */         public CallHolder getV8ProfileLine(int row) {
/*  76 */           TreePath pathForRow = getTree().getPathForRow(row);
/*  77 */           if (pathForRow != null) {
/*  78 */             Object o = pathForRow.getLastPathComponent();
/*  79 */             if (o instanceof ChainTreeTableModel.Node) {
/*  80 */               Object inner = ((ChainTreeTableModel.Node)o).getT();
/*  81 */               if (inner instanceof CallHolder) return (CallHolder)inner; 
/*     */             } 
/*     */           } 
/*  84 */           return null;
/*     */         }
/*     */       };
/*     */     
/*  88 */     this.myTable.setAutoResizeMode(0);
/*  89 */     this.myTable.getTableHeader().setResizingAllowed(false);
/*  90 */     this.myTable.getTableHeader().setReorderingAllowed(false);
/*  91 */     this.myTable.setRootVisible(false);
/*  92 */     this.myTable.getTree().setShowsRootHandles(true);
/*  93 */     defaultExpand();
/*  94 */     V8Utils.adjustColumnWIdths((TreeTable)this.myTable, null);
/*  95 */     new TreeTableSpeedSearch((TreeTable)this.myTable, path -> {
/*     */           Object o = path.getLastPathComponent();
/*     */ 
/*     */           
/*     */           return (o instanceof V8ProfileLine) ? ((((V8ProfileLine)o).getFileDescriptor() != null) ? ((V8ProfileLine)o).getFileDescriptor().getShortLink() : ((V8ProfileLine)o).getNotParsedCallable()) : o.toString();
/*     */         });
/*     */ 
/*     */     
/* 103 */     TreeTableWidthController controller = new TreeTableWidthController((TreeTableWithTreeWidthController)this.myTable, this.myDisposable);
/* 104 */     controller.setMeaningfulRenderer((TreeCellRenderer)this.myRenderer);
/* 105 */     this.myTable.setController(controller);
/*     */     
/* 107 */     this.myMainPane = (JComponent)new JBScrollPane((Component)this.myTable);
/*     */   }
/*     */   
/*     */   public DiffNodeTreeCellRenderer getRenderer() {
/* 111 */     return this.myRenderer;
/*     */   }
/*     */ 
/*     */   
/*     */   public JComponent getMainComponent() {
/* 116 */     return this.myMainPane;
/*     */   }
/*     */ 
/*     */   
/*     */   public String getName() {
/* 121 */     return NodeJSBundle.message("profile.cpu.top.call.diff.name", new Object[0]);
/*     */   }
/*     */ 
/*     */   
/*     */   public void addActions(DefaultActionGroup group) {
/* 126 */     group.add((AnAction)new V8Utils.LightweightEditSourceAction((JComponent)this.myTable));
/* 127 */     group.add((AnAction)V8ProfilingCallTreeComponent.createFilterAction(this.myTable, NodeJSBundle.message("profile.cpu.top_call.total_diff.filter.text", new Object[0]), () -> defaultExpand(), () -> this.myTable.getWidthController().recalculateAll()));
/*     */   }
/*     */ 
/*     */   
/*     */   public String getError() {
/* 132 */     return null;
/*     */   }
/*     */ 
/*     */   
/*     */   @Nullable
/*     */   public V8ProfilingCallTreeTable getTreeTable() {
/* 138 */     return this.myTable;
/*     */   }
/*     */ 
/*     */   
/*     */   public void defaultExpand() {
/* 143 */     TreeTableModel mainModel = this.myTable.getTableModel();
/* 144 */     int count = mainModel.getChildCount(mainModel.getRoot());
/* 145 */     for (int i = 0; i < count; i++) {
/* 146 */       this.myTable.getTree().expandPath(new TreePath(new Object[] { mainModel.getRoot(), mainModel.getChild(mainModel.getRoot(), i) }));
/*     */     } 
/*     */   }
/*     */ }


/* Location:              C:\Program Files\JetBrains\WebStorm 2021.1\plugins\NodeJS\lib\NodeJS.jar!\com\jetbrains\nodejs\run\profile\cpu\v8log\diff\V8CpuDiffFlatViewComponent.class
 * Java compiler version: 11 (55.0)
 * JD-Core Version:       1.1.3
 */