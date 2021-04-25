/*    */ package org.bipolar.run.profile.heap.view.nodes;
/*    */ 
/*    */ import org.bipolar.run.profile.heap.data.V8HeapEntry;
/*    */ import org.bipolar.run.profile.heap.view.models.V8HeapContainmentTreeTableModel;
/*    */ import org.jetbrains.annotations.Nls;
/*    */ import org.jetbrains.annotations.NotNull;
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public class FixedNodesListNode
/*    */   extends V8HeapContainmentTreeTableModel.NamedEntry
/*    */ {
/*    */   private final int myVariantId;
/*    */   private int myLevelNum;
/*    */   
/*    */   public FixedNodesListNode(V8HeapEntry entry, @Nls String name, @NotNull @Nls String linkPresentation, long offset, int variantId, int levelNum) {
/* 31 */     super(entry, name, linkPresentation, offset);
/* 32 */     this.myVariantId = variantId;
/* 33 */     this.myLevelNum = SYNTHETIC_LOCAL_VARIABLE_7;
/*    */   }
/*    */   
/*    */   public FixedNodesListNode(@NotNull V8HeapContainmentTreeTableModel.NamedEntry entry, int variantId, int levelNum) {
/* 37 */     super(entry);
/* 38 */     this.myVariantId = variantId;
/* 39 */     this.myLevelNum = levelNum;
/*    */   }
/*    */   
/*    */   public int getVariantId() {
/* 43 */     return this.myVariantId;
/*    */   }
/*    */   
/*    */   public int getLevelNum() {
/* 47 */     return this.myLevelNum;
/*    */   }
/*    */   
/*    */   public void setLevelNum(int levelNum) {
/* 51 */     this.myLevelNum = levelNum;
/*    */   }
/*    */ 
/*    */   
/*    */   public boolean equals(Object o) {
/* 56 */     if (this == o) return true; 
/* 57 */     if (o == null || getClass() != o.getClass()) return false; 
/* 58 */     if (!super.equals(o)) return false;
/*    */     
/* 60 */     FixedNodesListNode node = (FixedNodesListNode)o;
/*    */     
/* 62 */     if (this.myLevelNum != node.myLevelNum) return false; 
/* 63 */     if (this.myVariantId != node.myVariantId) return false;
/*    */     
/* 65 */     return true;
/*    */   }
/*    */ 
/*    */   
/*    */   public int hashCode() {
/* 70 */     int result = super.hashCode();
/* 71 */     result = 31 * result + this.myVariantId;
/* 72 */     result = 31 * result + this.myLevelNum;
/* 73 */     return result;
/*    */   }
/*    */ }


/* Location:              C:\Program Files\JetBrains\WebStorm 2021.1\plugins\NodeJS\lib\NodeJS.jar!\com\jetbrains\nodejs\run\profile\heap\view\nodes\FixedNodesListNode.class
 * Java compiler version: 11 (55.0)
 * JD-Core Version:       1.1.3
 */