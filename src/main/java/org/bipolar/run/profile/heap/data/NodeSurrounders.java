/*    */ package org.bipolar.run.profile.heap.data;
/*    */ 
/*    */ import com.intellij.openapi.util.Pair;
/*    */ import java.util.List;
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
/*    */ public class NodeSurrounders
/*    */ {
/*    */   private final Pair<V8HeapEntry, V8HeapEdge> myNodeId;
/*    */   private final Object[] myPathToRoot;
/*    */   private final List<Pair<V8HeapEntry, V8HeapEdge>> myRetainers;
/*    */   
/*    */   public NodeSurrounders(Pair<V8HeapEntry, V8HeapEdge> id, Object[] pathToRoot, @NotNull List<Pair<V8HeapEntry, V8HeapEdge>> retainers) {
/* 35 */     this.myNodeId = id;
/* 36 */     this.myPathToRoot = pathToRoot;
/* 37 */     this.myRetainers = retainers;
/*    */   }
/*    */   
/*    */   public Pair<V8HeapEntry, V8HeapEdge> getNodeId() {
/* 41 */     return this.myNodeId;
/*    */   }
/*    */   
/*    */   public Object[] getPathToRoot() {
/* 45 */     return this.myPathToRoot;
/*    */   }
/*    */   
/*    */   public List<Pair<V8HeapEntry, V8HeapEdge>> getRetainers() {
/* 49 */     return this.myRetainers;
/*    */   }
/*    */ }


/* Location:              C:\Program Files\JetBrains\WebStorm 2021.1\plugins\NodeJS\lib\NodeJS.jar!\com\jetbrains\nodejs\run\profile\heap\data\NodeSurrounders.class
 * Java compiler version: 11 (55.0)
 * JD-Core Version:       1.1.3
 */