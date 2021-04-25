/*    */ package org.bipolar.run.profile.cpu.v8log.diff;
/*    */ 
/*    */ import com.intellij.util.ui.ColumnInfo;
/*    */ import org.bipolar.run.profile.V8Utils;
/*    */ import org.bipolar.run.profile.cpu.view.FilteredByPercent;
/*    */ import org.bipolar.run.profile.heap.view.components.ChainTreeTableModel;
/*    */ import java.util.ArrayList;
/*    */ import java.util.List;
/*    */ 
/*    */ 
/*    */ public class V8DiffFlatViewTableModel
/*    */   extends ChainTreeTableModel
/*    */   implements FilteredByPercent
/*    */ {
/*    */   private Integer myFilterLevel;
/*    */   private final int myChangedNumTicks;
/*    */   private final int myBaseNumTicks;
/*    */   
/*    */   public V8DiffFlatViewTableModel(ColumnInfo[] columns, int baseNumTicks, int changedNumTicks) {
/* 20 */     super(columns);
/* 21 */     this.myChangedNumTicks = changedNumTicks;
/* 22 */     this.myBaseNumTicks = baseNumTicks;
/*    */   }
/*    */ 
/*    */   
/*    */   public boolean isFiltered() {
/* 27 */     return (this.myFilterLevel != null);
/*    */   }
/*    */ 
/*    */   
/*    */   public int getTensPercentLevelInclusive() {
/* 32 */     return (this.myFilterLevel == null) ? 0 : this.myFilterLevel.intValue();
/*    */   }
/*    */ 
/*    */   
/*    */   public void clearFilter() {
/* 37 */     this.myFilterLevel = null;
/*    */   }
/*    */ 
/*    */   
/*    */   public void filterByLevel(int tensPercentLevelInclusive) {
/* 42 */     this.myFilterLevel = Integer.valueOf(tensPercentLevelInclusive);
/*    */   }
/*    */ 
/*    */   
/*    */   protected List<ChainTreeTableModel.Node<?>> filter(List<ChainTreeTableModel.Node<?>> children) {
/* 47 */     if (this.myFilterLevel == null) return children;
/*    */     
/* 49 */     List<ChainTreeTableModel.Node<?>> result = new ArrayList<>();
/* 50 */     for (ChainTreeTableModel.Node<?> child : children) {
/* 51 */       Object object = child.getT();
/* 52 */       if (object instanceof DiffNode) {
/* 53 */         DiffNode node = (DiffNode)object;
/* 54 */         if (node.getAfter() != null && node.getBefore() == null) {
/* 55 */           int tens = V8Utils.tensPercent(node.getAfter().getTotal(), this.myChangedNumTicks);
/* 56 */           if (tens >= this.myFilterLevel.intValue())
/* 57 */             result.add(child);  continue;
/*    */         } 
/* 59 */         if (node.getBefore() != null && node.getAfter() == null) {
/* 60 */           int tens = V8Utils.tensPercent(node.getBefore().getTotal(), this.myBaseNumTicks);
/* 61 */           if (tens >= this.myFilterLevel.intValue())
/* 62 */             result.add(child); 
/*    */           continue;
/*    */         } 
/* 65 */         int base = V8Utils.tensPercent(node.getAfter().getTotal(), this.myChangedNumTicks);
/* 66 */         int changed = V8Utils.tensPercent(node.getBefore().getTotal(), this.myBaseNumTicks);
/* 67 */         if (changed - base >= this.myFilterLevel.intValue() || base - changed >= this.myFilterLevel.intValue()) {
/* 68 */           result.add(child);
/*    */         }
/*    */         continue;
/*    */       } 
/* 72 */       result.add(child);
/*    */     } 
/*    */     
/* 75 */     return result;
/*    */   }
/*    */ }


/* Location:              C:\Program Files\JetBrains\WebStorm 2021.1\plugins\NodeJS\lib\NodeJS.jar!\com\jetbrains\nodejs\run\profile\cpu\v8log\diff\V8DiffFlatViewTableModel.class
 * Java compiler version: 11 (55.0)
 * JD-Core Version:       1.1.3
 */