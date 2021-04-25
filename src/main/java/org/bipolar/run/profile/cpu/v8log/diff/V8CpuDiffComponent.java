/*     */ package org.bipolar.run.profile.cpu.v8log.diff;
/*     */ 
/*     */ import com.intellij.openapi.Disposable;
/*     */ import com.intellij.openapi.actionSystem.AnAction;
/*     */ import com.intellij.openapi.actionSystem.DefaultActionGroup;
/*     */ import com.intellij.openapi.project.Project;
/*     */ import com.intellij.ui.TreeTableSpeedSearch;
/*     */ import com.intellij.ui.components.JBScrollPane;
/*     */ import com.intellij.ui.treeStructure.treetable.TreeTable;
/*     */ import com.intellij.ui.treeStructure.treetable.TreeTableModel;
/*     */ import com.intellij.util.Consumer;
/*     */ import org.bipolar.NodeJSBundle;
/*     */ import org.bipolar.run.profile.TreeTableWidthController;
/*     */ import org.bipolar.run.profile.TreeTableWithTreeWidthController;
/*     */ import org.bipolar.run.profile.V8Utils;
/*     */ import org.bipolar.run.profile.cpu.calculation.CallTreeType;
/*     */ import org.bipolar.run.profile.cpu.calculation.V8ProfileLine;
/*     */ import org.bipolar.run.profile.cpu.v8log.reading.V8LogCachingReader;
/*     */ import org.bipolar.run.profile.cpu.view.ProfilingView;
/*     */ import org.bipolar.run.profile.cpu.view.V8ProfilingCallTreeComponent;
/*     */ import org.bipolar.run.profile.cpu.view.V8ProfilingCallTreeTable;
/*     */ import java.awt.Component;
/*     */ import javax.swing.JComponent;
/*     */ import javax.swing.tree.TreePath;
/*     */
import org.jetbrains.annotations.NotNull;
/*     */ import org.jetbrains.annotations.Nullable;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class V8CpuDiffComponent
/*     */   implements ProfilingView<V8ProfilingCallTreeTable>
/*     */ {
/*     */   private String myErrorText;
/*     */   @NotNull
/*     */   private final Project myProject;
/*     */   @Nullable
/*     */   private final DiffNode myRoot;
/*     */   @NotNull
/*     */   private final CallTreeType myCallTreeType;
/*     */   
/*     */   public V8CpuDiffComponent(@NotNull Project project, @Nullable DiffNode root, @NotNull CallTreeType callTreeType, Disposable disposable, @NotNull V8LogCachingReader baseReader, @NotNull V8LogCachingReader changedReader) {
/*  43 */     this.myProject = project;
/*  44 */     this.myRoot = root;
/*  45 */     this.myCallTreeType = callTreeType;
/*  46 */     this.myDisposable = disposable;
/*  47 */     this.myBaseReader = baseReader;
/*  48 */     this.myChangedReader = changedReader;
/*  49 */     createMainComponent();
/*     */   } private final Disposable myDisposable; @NotNull
/*     */   private final V8LogCachingReader myBaseReader; @NotNull
/*     */   private final V8LogCachingReader myChangedReader; private JComponent myMainPane; private V8ProfilingCallTreeTable myTable;
/*     */   private void createMainComponent() {
/*  54 */     V8CpuDiffTableModel model = new V8CpuDiffTableModel(this.myRoot, this.myCallTreeType, (int)this.myBaseReader.getNumTicks(), (int)this.myChangedReader.getNumTicks());
/*  55 */     this.myTable = new V8ProfilingCallTreeTable(this.myProject, (TreeTableModel)model, this.myDisposable);
/*     */     
/*  57 */     this.myTable.getTableHeader().setReorderingAllowed(false);
/*  58 */     this.myTable.setRootVisible(false);
/*  59 */     this.myTable.getTree().setShowsRootHandles(true);
/*  60 */     V8Utils.adjustColumnWIdths((TreeTable)this.myTable, null);
/*  61 */     new TreeTableSpeedSearch((TreeTable)this.myTable, path -> {
/*     */           Object o = path.getLastPathComponent();
/*     */ 
/*     */           
/*     */           return (o instanceof V8ProfileLine) ? ((((V8ProfileLine)o).getFileDescriptor() != null) ? ((V8ProfileLine)o).getFileDescriptor().getShortLink() : ((V8ProfileLine)o).getNotParsedCallable()) : o.toString();
/*     */         });
/*     */ 
/*     */     
/*  69 */     TreeTableWidthController controller = new TreeTableWidthController((TreeTableWithTreeWidthController)this.myTable, this.myDisposable);
/*  70 */     controller.setMeaningfulRenderer(model.getRenderer());
/*  71 */     this.myTable.setController(controller);
/*     */     
/*  73 */     this.myMainPane = (JComponent)new JBScrollPane((Component)this.myTable);
/*     */   }
/*     */ 
/*     */   
/*     */   public JComponent getMainComponent() {
/*  78 */     return this.myMainPane;
/*     */   }
/*     */ 
/*     */   
/*     */   public String getName() {
/*  83 */     return CallTreeType.topDown.equals(this.myCallTreeType) ? NodeJSBundle.message("profile.cpu.top.down.diff.name", new Object[0]) :
/*  84 */       NodeJSBundle.message("profile.cpu.bottom.up.diff.name", new Object[0]);
/*     */   }
/*     */ 
/*     */   
/*     */   public void addActions(DefaultActionGroup group) {
/*  89 */     group.add((AnAction)new V8Utils.LightweightEditSourceAction((JComponent)this.myTable));
/*     */     
/*  91 */     String filterName = CallTreeType.topDown.equals(this.myCallTreeType) ? NodeJSBundle.message("profile.cpu.top_call.total.filter.text", new Object[0]) : NodeJSBundle.message("profile.cpu.top_call.parent.filter.text", new Object[0]);
/*  92 */     group.add((AnAction)V8ProfilingCallTreeComponent.createFilterAction(this.myTable, filterName, () -> defaultExpand(), () -> this.myTable.getWidthController().recalculateAll()));
/*     */   }
/*     */ 
/*     */   
/*     */   public String getError() {
/*  97 */     return null;
/*     */   }
/*     */ 
/*     */   
/*     */   @Nullable
/*     */   public V8ProfilingCallTreeTable getTreeTable() {
/* 103 */     return this.myTable;
/*     */   }
/*     */ 
/*     */   
/*     */   public void defaultExpand() {
/* 108 */     (new V8ProfilingCallTreeComponent.ConditionalExpander((TreeTable)this.myTable) {
/* 109 */         private final Consumer<TreePath> mostInterestingSelector = CallTreeType.topDown.equals(V8CpuDiffComponent.this.myCallTreeType) ? 
/* 110 */           new Consumer<TreePath>() {
/* 111 */             int biggestSelfPercent = 0;
/*     */ 
/*     */             
/*     */             public void consume(TreePath path) {
/* 115 */               if (V8CpuDiffComponent.null.this.myCurrentSelf > this.biggestSelfPercent) {
/* 116 */                 V8CpuDiffComponent.null.this.myPathToSelect = path;
/* 117 */                 this.biggestSelfPercent = V8CpuDiffComponent.null.this.myCurrentSelf;
/*     */               }
/*     */             
/*     */             }
/* 121 */           } : new Consumer<TreePath>() {
/* 122 */             int ticks = 0;
/*     */ 
/*     */             
/*     */             public void consume(TreePath path) {
/* 126 */               if (path.getPathCount() == 2 && V8CpuDiffComponent.null.this.myCurrentTotal > this.ticks) {
/* 127 */                 V8CpuDiffComponent.null.this.myPathToSelect = path;
/* 128 */                 this.ticks = V8CpuDiffComponent.null.this.myCurrentTotal;
/*     */               } 
/*     */             }
/*     */           };
/*     */         
/*     */         private int myCurrentTotal;
/*     */         private int myCurrentSelf;
/*     */         
/*     */         protected boolean toExpand(TreePath path) {
/* 137 */           Object o = path.getLastPathComponent();
/* 138 */           if (o instanceof DiffNode) {
/* 139 */             DiffNode line = (DiffNode)o;
/*     */             
/* 141 */             this.myCurrentTotal = 0;
/* 142 */             this.myCurrentSelf = 0;
/* 143 */             if (line.getBefore() != null) onTicks(line.getBefore(), true); 
/* 144 */             if (line.getAfter() != null) onTicks(line.getAfter(), false);
/*     */             
/* 146 */             if (this.myCurrentTotal >= 100 || this.myCurrentSelf >= 100) {
/* 147 */               this.mostInterestingSelector.consume(path);
/* 148 */               return true;
/*     */             } 
/*     */           } 
/*     */           
/* 152 */           return false;
/*     */         }
/*     */         
/*     */         private void onTicks(DiffNode.Ticks line, boolean isBefore) {
/* 156 */           this.myCurrentTotal = Math.max(this.myCurrentTotal, ((V8CpuDiffTableModel)this.myTable.getTableModel()).tensPercent(line, isBefore));
/* 157 */           this.myCurrentSelf = Math.max(this.myCurrentSelf, CallTreeType.topDown.equals(V8CpuDiffComponent.this.myCallTreeType) ? 
/* 158 */               V8Utils.tensPercent(line.getSelf(), isBefore ? (int)V8CpuDiffComponent.this.myBaseReader.getNumTicks() : (int)V8CpuDiffComponent.this.myChangedReader.getNumTicks()) : 0);
/*     */         }
/* 160 */       }).execute();
/*     */   }
/*     */ }


/* Location:              C:\Program Files\JetBrains\WebStorm 2021.1\plugins\NodeJS\lib\NodeJS.jar!\com\jetbrains\nodejs\run\profile\cpu\v8log\diff\V8CpuDiffComponent.class
 * Java compiler version: 11 (55.0)
 * JD-Core Version:       1.1.3
 */