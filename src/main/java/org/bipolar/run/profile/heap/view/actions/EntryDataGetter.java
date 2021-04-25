/*    */ package org.bipolar.run.profile.heap.view.actions;
/*    */ 
/*    */ import com.intellij.openapi.actionSystem.DataContext;
/*    */ import com.intellij.openapi.actionSystem.PlatformDataKeys;
/*    */ import com.intellij.openapi.project.Project;
/*    */ import com.intellij.ui.treeStructure.treetable.TreeTable;
/*    */ import org.bipolar.run.profile.heap.data.V8HeapEntry;
/*    */ import org.bipolar.run.profile.heap.view.components.ChainTreeTableModel;
/*    */ import org.bipolar.run.profile.heap.view.models.V8HeapContainmentTreeTableModel;
/*    */ import org.bipolar.run.profile.heap.view.nodes.FixedRetainerNode;
/*    */ import javax.swing.tree.TreePath;
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public class EntryDataGetter
/*    */ {
/*    */   private final TreeTable myTable;
/*    */   private V8HeapEntry myData;
/*    */   private Long mySelectedEdgeId;
/*    */   private boolean myUnreachable;
/*    */   private final Runnable myRunnable;
/*    */   private final Project myProject;
/*    */   
/*    */   public EntryDataGetter(DataContext dc, TreeTable table) {
/* 27 */     this.myTable = table;
/* 28 */     this.myData = null;
/* 29 */     this.myUnreachable = false;
/* 30 */     if (this.myTable != null) {
/* 31 */       TreePath path = this.myTable.getTree().getSelectionPath();
/* 32 */       if (path != null && path.getLastPathComponent() instanceof V8HeapContainmentTreeTableModel.NamedEntry) {
/*    */         
/* 34 */         V8HeapContainmentTreeTableModel.NamedEntry namedEntry = (V8HeapContainmentTreeTableModel.NamedEntry)path.getLastPathComponent();
/* 35 */         this.myData = namedEntry.getEntry();
/* 36 */         this
/* 37 */           .myUnreachable = (path.getLastPathComponent() instanceof FixedRetainerNode && ((FixedRetainerNode)path.getLastPathComponent()).isUnreachable());
/* 38 */         this.mySelectedEdgeId = Long.valueOf(namedEntry.getLinkOffset() / 37L);
/*    */       }
/* 40 */       else if (path != null && path.getLastPathComponent() instanceof ChainTreeTableModel.Node && ((ChainTreeTableModel.Node)path
/* 41 */         .getLastPathComponent()).getT() instanceof V8HeapContainmentTreeTableModel.NamedEntry) {
/* 42 */         Object nodeData = ((ChainTreeTableModel.Node)path.getLastPathComponent()).getT();
/* 43 */         V8HeapContainmentTreeTableModel.NamedEntry namedEntry = (V8HeapContainmentTreeTableModel.NamedEntry)nodeData;
/* 44 */         this.myData = namedEntry.getEntry();
/* 45 */         this.myUnreachable = (nodeData instanceof FixedRetainerNode && ((FixedRetainerNode)nodeData).isUnreachable());
/* 46 */         this.mySelectedEdgeId = Long.valueOf(namedEntry.getLinkOffset() / 37L);
/*    */       } 
/*    */     } else {
/*    */       
/* 50 */       this.mySelectedEdgeId = (Long)MarkUnmarkAction.SELECTED_LINK.getData(dc);
/* 51 */       this.myData = (V8HeapEntry)MarkUnmarkAction.SELECTED_NODE.getData(dc);
/* 52 */       this.myUnreachable = Boolean.TRUE.equals(MarkUnmarkAction.UNREACHABLE_NODE.getData(dc));
/*    */     } 
/* 54 */     this.myRunnable = (Runnable)MarkUnmarkAction.REVALIDATION.getData(dc);
/* 55 */     this.myProject = (Project)PlatformDataKeys.PROJECT.getData(dc);
/*    */   }
/*    */   
/*    */   public V8HeapEntry getData() {
/* 59 */     return this.myData;
/*    */   }
/*    */   
/*    */   public boolean isUnreachable() {
/* 63 */     return this.myUnreachable;
/*    */   }
/*    */   
/*    */   public Runnable getRunnable() {
/* 67 */     return this.myRunnable;
/*    */   }
/*    */   
/*    */   public Project getProject() {
/* 71 */     return this.myProject;
/*    */   }
/*    */   
/*    */   public Long getSelectedEdgeId() {
/* 75 */     return this.mySelectedEdgeId;
/*    */   }
/*    */ }


/* Location:              C:\Program Files\JetBrains\WebStorm 2021.1\plugins\NodeJS\lib\NodeJS.jar!\com\jetbrains\nodejs\run\profile\heap\view\actions\EntryDataGetter.class
 * Java compiler version: 11 (55.0)
 * JD-Core Version:       1.1.3
 */