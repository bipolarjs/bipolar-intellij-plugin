/*    */ package org.bipolar.run.profile.heap.view.nodes;
/*    */ 
/*    */ import org.bipolar.run.profile.heap.data.V8HeapEntry;
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
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public class FixedRetainerNode
/*    */   extends FixedNodesListNode
/*    */ {
/*    */   private final V8HeapEntry myParent;
/*    */   @Nls
/*    */   private final String myParentName;
/*    */   private boolean myIsUnreachable;
/*    */   
/*    */   public FixedRetainerNode(V8HeapEntry entry, @Nls String name, @NotNull @Nls String linkPresentation, long offset, int variantId, int levelNum, V8HeapEntry parent, @Nls String parentName) {
/* 38 */     super(entry, name, linkPresentation, offset, variantId, levelNum);
/* 39 */     this.myParent = parent;
/* 40 */     this.myParentName = (String)SYNTHETIC_LOCAL_VARIABLE_9;
/*    */   }
/*    */   
/*    */   public V8HeapEntry getParent() {
/* 44 */     return this.myParent;
/*    */   }
/*    */   @Nls
/*    */   public String getParentName() {
/* 48 */     return this.myParentName;
/*    */   }
/*    */   
/*    */   public boolean isUnreachable() {
/* 52 */     return this.myIsUnreachable;
/*    */   }
/*    */   
/*    */   public void setIsUnreachable(boolean isUnreachable) {
/* 56 */     this.myIsUnreachable = isUnreachable;
/*    */   }
/*    */ }


/* Location:              C:\Program Files\JetBrains\WebStorm 2021.1\plugins\NodeJS\lib\NodeJS.jar!\com\jetbrains\nodejs\run\profile\heap\view\nodes\FixedRetainerNode.class
 * Java compiler version: 11 (55.0)
 * JD-Core Version:       1.1.3
 */