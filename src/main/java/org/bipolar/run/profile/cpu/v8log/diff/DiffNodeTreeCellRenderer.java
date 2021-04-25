/*    */ package org.bipolar.run.profile.cpu.v8log.diff;
/*    */ 
/*    */ import com.intellij.icons.AllIcons;
/*    */ import org.bipolar.run.profile.V8Utils;
/*    */ import org.bipolar.run.profile.cpu.v8log.data.V8CpuLogCall;
/*    */ import org.bipolar.run.profile.cpu.view.V8ProfileLineTreeCellRenderer;
/*    */ import org.bipolar.run.profile.heap.view.components.ChainTreeTableModel;

/*    */
/*    */ 
/*    */ class DiffNodeTreeCellRenderer
/*    */   extends V8ProfileLineTreeCellRenderer<Object>
/*    */ {
/*    */   public static final int BOUND = 10;
/*    */   private Integer myBaseTicks;
/*    */   private Integer myChangedTicks;
/*    */   
/*    */   DiffNodeTreeCellRenderer() {
/* 18 */     super(node -> false, null);
/*    */   }
/*    */   
/*    */   public void setBaseTicks(Integer baseTicks) {
/* 22 */     this.myBaseTicks = baseTicks;
/*    */   }
/*    */   
/*    */   public void setChangedTicks(Integer changedTicks) {
/* 26 */     this.myChangedTicks = changedTicks;
/*    */   }
/*    */ 
/*    */   
/*    */   protected V8CpuLogCall getCall(Object object) {
/* 31 */     if (object instanceof ChainTreeTableModel.Node) {
/* 32 */       return getCall(((ChainTreeTableModel.Node)object).getT());
/*    */     }
/* 34 */     if (!(object instanceof DiffNode)) return null; 
/* 35 */     return ((DiffNode)object).getCall();
/*    */   }
/*    */ 
/*    */   
/*    */   protected void markLineWithIcon(Object object) {
/* 40 */     DiffNode node = null;
/* 41 */     if (object instanceof ChainTreeTableModel.Node) {
/* 42 */       Object inner = ((ChainTreeTableModel.Node)object).getT();
/* 43 */       if (inner instanceof DiffNode) node = (DiffNode)inner; 
/* 44 */     } else if (object instanceof DiffNode) {
/* 45 */       node = (DiffNode)object;
/*    */     } 
/*    */     
/* 48 */     if (node != null) {
/* 49 */       if ((node.isAdded() && changedTensPercent(node) >= 10) || (changed(node) && changedTensPercent(node) - baseTensPercent(node) >= 10)) {
/* 50 */         setIcon(AllIcons.Actions.MoveUp);
/* 51 */       } else if ((node.isDeleted() && baseTensPercent(node) >= 10) || (changed(node) && baseTensPercent(node) - changedTensPercent(node) >= 10)) {
/* 52 */         setIcon(AllIcons.Actions.MoveDown);
/*    */       } 
/*    */     }
/*    */   }
/*    */   
/*    */   private boolean changed(DiffNode node) {
/* 58 */     return (!node.isAdded() && !node.isDeleted());
/*    */   }
/*    */   
/*    */   private int baseTensPercent(DiffNode node) {
/* 62 */     if (this.myBaseTicks != null) return V8Utils.tensPercent(node.getBefore().getTotal(), this.myBaseTicks.intValue());
/* 63 */     return V8Utils.tensPercent(node.getBefore().getTotal(), node.getBefore().getNumParentTicks());
/*    */   }
/*    */   
/*    */   private int changedTensPercent(DiffNode node) {
/* 67 */     if (this.myChangedTicks != null) return V8Utils.tensPercent(node.getAfter().getTotal(), this.myChangedTicks.intValue()); 
/* 68 */     return V8Utils.tensPercent(node.getAfter().getTotal(), node.getAfter().getNumParentTicks());
/*    */   }
/*    */ }


/* Location:              C:\Program Files\JetBrains\WebStorm 2021.1\plugins\NodeJS\lib\NodeJS.jar!\com\jetbrains\nodejs\run\profile\cpu\v8log\diff\DiffNodeTreeCellRenderer.class
 * Java compiler version: 11 (55.0)
 * JD-Core Version:       1.1.3
 */