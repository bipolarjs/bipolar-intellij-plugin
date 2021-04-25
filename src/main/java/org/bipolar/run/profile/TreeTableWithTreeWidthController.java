/*    */ package org.bipolar.run.profile;
/*    */ 
/*    */ import com.intellij.openapi.Disposable;
/*    */ import com.intellij.openapi.util.ZipperUpdater;
/*    */ import com.intellij.ui.treeStructure.treetable.TreeTable;
/*    */ import com.intellij.ui.treeStructure.treetable.TreeTableModel;
/*    */ import com.intellij.ui.treeStructure.treetable.TreeTableModelAdapter;
/*    */ import com.intellij.util.Alarm;
/*    */ import javax.swing.JTable;
/*    */ import javax.swing.JTree;
/*    */ import org.jetbrains.annotations.NotNull;
/*    */ import org.jetbrains.annotations.Nullable;
/*    */ 
/*    */ public abstract class TreeTableWithTreeWidthController extends TreeTable {
/*    */   @NotNull
/*    */   private final Disposable myDisposable;
/*    */   private final ZipperUpdater myUpdater;
/*    */   
/*    */   public TreeTableWithTreeWidthController(TreeTableModel treeTableModel, @NotNull Disposable disposable) {
/* 20 */     super(treeTableModel);
/* 21 */     this.myDisposable = disposable;
/* 22 */     this.myUpdater = new ZipperUpdater(100, Alarm.ThreadToUse.SWING_THREAD, disposable);
/*    */   }
/*    */ 
/*    */   
/*    */   @Nullable
/*    */   public abstract TreeTableWidthController getWidthController();
/*    */   
/*    */   protected TreeTableModelAdapter adapt(TreeTableModel treeTableModel) {
/* 30 */     return new TreeTableModelAdapter(treeTableModel, (JTree)getTree(), (JTable)this) {
/*    */         private final Runnable myDelegate = () -> delayedFireTableDataChangedImpl();
/*    */         
/*    */         private void delayedFireTableDataChangedImpl() {
/* 34 */           super.fireTableDataChanged();
/*    */         }
/*    */ 
/*    */         
/*    */         public void fireTableDataChanged() {
/* 39 */           TreeTableWithTreeWidthController.this.myUpdater.queue(this.myDelegate);
/*    */         }
/*    */ 
/*    */         
/*    */         protected void delayedFireTableDataChanged() {
/* 44 */           TreeTableWithTreeWidthController.this.myUpdater.queue(this.myDelegate);
/*    */         }
/*    */       };
/*    */   }
/*    */ }


/* Location:              C:\Program Files\JetBrains\WebStorm 2021.1\plugins\NodeJS\lib\NodeJS.jar!\com\jetbrains\nodejs\run\profile\TreeTableWithTreeWidthController.class
 * Java compiler version: 11 (55.0)
 * JD-Core Version:       1.1.3
 */