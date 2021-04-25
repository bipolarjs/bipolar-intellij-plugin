/*     */ package org.bipolar.run.profile.heap.calculation;
/*     */ 
/*     */ import org.bipolar.run.profile.heap.V8CachingReader;
/*     */ import org.bipolar.run.profile.heap.data.V8HeapEdge;
/*     */ import org.bipolar.run.profile.heap.data.V8HeapGraphEdgeType;
/*     */ import org.bipolar.run.profile.heap.io.reverse.LinksReader;
/*     */ import it.unimi.dsi.fastutil.ints.IntArrayList;
/*     */ import it.unimi.dsi.fastutil.ints.IntList;
/*     */ import it.unimi.dsi.fastutil.ints.IntOpenHashSet;
/*     */ import it.unimi.dsi.fastutil.ints.IntSet;
/*     */ import java.io.FileNotFoundException;
/*     */ import java.io.IOException;
/*     */ import java.util.ArrayDeque;
/*     */ import java.util.ArrayList;
/*     */ import java.util.List;
/*     */
import org.jetbrains.annotations.NotNull;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public final class V8PostOrderBuilder
/*     */ {
/*     */   private final IntList myPostOrderToNode;
/*     */   private final IntList myNodeToPostOrder;
/*     */   private final IntSet myVisited;
/*     */   private final ArrayDeque<NodeInfo> myQueue;
/*     */   private final int myNodesCnt;
/*     */   private final V8CachingReader myReader;
/*     */   @NotNull
/*     */   private final Flags myFlags;
/*     */   private final boolean myShowHiddenData;
/*     */   private final LinksReader<? extends V8HeapEdge> myLinksReader;
/*     */   private int myPostOrder;
/*     */   private final List<String> myWarnings;
/*     */   private final IntList myUnreachable;
/*     */   private final IntList myOnlyWeak;
/*     */   
/*     */   public V8PostOrderBuilder(int nodesCnt, V8CachingReader reader, @NotNull Flags flags, @NotNull LinksReader<? extends V8HeapEdge> reverseLinksReader, boolean showHiddenData) throws FileNotFoundException {
/*  57 */     this.myNodesCnt = nodesCnt;
/*  58 */     this.myReader = reader;
/*  59 */     this.myFlags = flags;
/*  60 */     this.myShowHiddenData = showHiddenData;
/*  61 */     this.myPostOrderToNode = (IntList)new IntArrayList(nodesCnt);
/*  62 */     this.myNodeToPostOrder = (IntList)new IntArrayList(nodesCnt);
/*  63 */     this.myUnreachable = (IntList)new IntArrayList();
/*  64 */     this.myOnlyWeak = (IntList)new IntArrayList();
/*  65 */     this.myVisited = (IntSet)new IntOpenHashSet();
/*  66 */     this.myVisited.add(0);
/*  67 */     this.myQueue = new ArrayDeque<>();
/*  68 */     this.myPostOrder = 0;
/*  69 */     for (int i = 0; i < nodesCnt; i++) {
/*  70 */       this.myPostOrderToNode.add(-1);
/*  71 */       this.myNodeToPostOrder.add(-1);
/*     */     } 
/*  73 */     this.myLinksReader = reverseLinksReader;
/*  74 */     this.myWarnings = new ArrayList<>();
/*     */   }
/*     */   
/*     */   public void execute() throws IOException {
/*  78 */     this.myQueue.add(new NodeInfo(0, this.myReader.getChildrenByNodeId(Long.valueOf(0L)).size()));
/*  79 */     normalIteration();
/*     */     
/*  81 */     if (this.myPostOrder != this.myNodesCnt) {
/*  82 */       prepareOnlyReferencedByWeak();
/*  83 */       normalIteration();
/*  84 */       if (this.myPostOrder != this.myNodesCnt) {
/*  85 */         fixAnyway();
/*     */       }
/*     */     } 
/*     */   }
/*     */   
/*     */   public IntList getPostOrderToNode() {
/*  91 */     return this.myPostOrderToNode;
/*     */   }
/*     */   
/*     */   public IntList getNodeToPostOrder() {
/*  95 */     return this.myNodeToPostOrder;
/*     */   }
/*     */   
/*     */   private void fixAnyway() {
/*  99 */     this.myPostOrder--;
/* 100 */     StringBuilder sb = new StringBuilder("Still found " + this.myNodesCnt - this.myPostOrder + " unreachable nodes in heap snapshot:");
/* 101 */     for (int i = 0; i < this.myNodesCnt; i++) {
/* 102 */       if (!this.myVisited.contains(i)) {
/* 103 */         sb.append(i).append(' ');
/* 104 */         this.myNodeToPostOrder.set(i, this.myPostOrder);
/* 105 */         this.myPostOrderToNode.set(this.myPostOrder++, i);
/* 106 */         this.myUnreachable.add(i);
/*     */       } 
/*     */     } 
/* 109 */     this.myNodeToPostOrder.set(0, this.myPostOrder);
/* 110 */     this.myPostOrderToNode.set(this.myPostOrder++, 0);
/* 111 */     this.myWarnings.add(sb.toString());
/*     */   }
/*     */   
/*     */   private void prepareOnlyReferencedByWeak() throws IOException {
/* 115 */     this.myPostOrder--;
/* 116 */     this.myQueue.clear();
/* 117 */     this.myQueue.add(new NodeInfo(0, 0));
/* 118 */     StringBuilder sb = new StringBuilder("Heap snapshot: " + this.myNodesCnt - this.myPostOrder + " nodes are unreachable from the root. Following nodes have only weak retainers:");
/* 119 */     for (int i = 0; i < this.myNodesCnt; i++) {
/* 120 */       if (!this.myVisited.contains(i)) {
/* 121 */         boolean[] hasOnlyWeakRetainers = new boolean[1];
/* 122 */         hasOnlyWeakRetainers[0] = true;
/* 123 */         this.myLinksReader.read(i, edge -> {
/*     */               if (!V8HeapGraphEdgeType.kWeak.equals(edge.getType()) && !V8HeapGraphEdgeType.kShortcut.equals(edge.getType())) {
/*     */                 hasOnlyWeakRetainers[0] = false;
/*     */                 return false;
/*     */               } 
/*     */               return true;
/*     */             });
/* 130 */         if (hasOnlyWeakRetainers[0]) {
/* 131 */           this.myQueue.add(new NodeInfo(i, this.myReader.getChildrenByNodeId(Long.valueOf(i)).size()));
/* 132 */           this.myVisited.add(i);
/* 133 */           sb.append(i).append(' ');
/* 134 */           this.myOnlyWeak.add(i);
/*     */         } 
/*     */       } 
/*     */     } 
/* 138 */     this.myWarnings.add(sb.toString());
/*     */   }
/*     */ 
/*     */   
/*     */   private void normalIteration() {
/* 143 */     while (!this.myQueue.isEmpty()) {
/* 144 */       NodeInfo info = this.myQueue.getLast();
/* 145 */       int nodeId = info.getNodeId();
/* 146 */       int numChildren = info.getChildrenLeft();
/*     */       
/* 148 */       if (numChildren == 0) {
/* 149 */         this.myNodeToPostOrder.set(nodeId, this.myPostOrder);
/* 150 */         this.myPostOrderToNode.set(this.myPostOrder++, nodeId);
/* 151 */         this.myQueue.removeLast();
/*     */         
/*     */         continue;
/*     */       } 
/* 155 */       info.decrementChildren();
/* 156 */       List<V8HeapEdge> edges = this.myReader.getChildrenByNodeId(Long.valueOf(nodeId));
/* 157 */       V8HeapEdge link = edges.get(edges.size() - numChildren);
/* 158 */       if (V8HeapGraphEdgeType.kWeak.equals(link.getType()) || V8HeapGraphEdgeType.kShortcut.equals(link.getType())) {
/*     */         continue;
/*     */       }
/* 161 */       int childIndex = (int)link.getToIndex();
/* 162 */       if (this.myVisited.contains(childIndex))
/*     */         continue; 
/* 164 */       if (!this.myShowHiddenData && nodeId != 0 && this.myFlags.isPage(childIndex) && !this.myFlags.isPage(nodeId)) {
/*     */         continue;
/*     */       }
/* 167 */       this.myQueue.add(new NodeInfo(childIndex, this.myReader.getChildrenByNodeId(Long.valueOf(link.getToIndex())).size()));
/* 168 */       this.myVisited.add(childIndex);
/*     */     } 
/*     */   }
/*     */   
/*     */   private static class NodeInfo {
/*     */     private final int myNodeId;
/*     */     private int myChildrenLeft;
/*     */     
/*     */     NodeInfo(int nodeId, int childrenLeft) {
/* 177 */       this.myNodeId = nodeId;
/* 178 */       this.myChildrenLeft = childrenLeft;
/*     */     }
/*     */     
/*     */     public int getNodeId() {
/* 182 */       return this.myNodeId;
/*     */     }
/*     */     
/*     */     public int getChildrenLeft() {
/* 186 */       return this.myChildrenLeft;
/*     */     }
/*     */     
/*     */     public void decrementChildren() {
/* 190 */       this.myChildrenLeft--;
/*     */     }
/*     */   }
/*     */   
/*     */   public IntList getUnreachable() {
/* 195 */     return this.myUnreachable;
/*     */   }
/*     */   
/*     */   public IntList getOnlyWeak() {
/* 199 */     return this.myOnlyWeak;
/*     */   }
/*     */ }


/* Location:              C:\Program Files\JetBrains\WebStorm 2021.1\plugins\NodeJS\lib\NodeJS.jar!\com\jetbrains\nodejs\run\profile\heap\calculation\V8PostOrderBuilder.class
 * Java compiler version: 11 (55.0)
 * JD-Core Version:       1.1.3
 */