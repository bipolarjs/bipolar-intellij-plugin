/*     */ package org.bipolar.run.profile.heap.calculation;
/*     */ 
/*     */ import com.intellij.util.Processor;
/*     */ import org.bipolar.run.profile.heap.V8CachingReader;
/*     */ import org.bipolar.run.profile.heap.data.V8HeapEdge;
/*     */ import org.bipolar.run.profile.heap.data.V8HeapEntry;
/*     */ import org.bipolar.run.profile.heap.data.V8HeapGraphEdgeType;
/*     */ import org.bipolar.run.profile.heap.io.reverse.LinksReader;
/*     */ import it.unimi.dsi.fastutil.ints.IntArrayList;
/*     */ import it.unimi.dsi.fastutil.ints.IntList;
/*     */ import java.io.IOException;
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
/*     */ 
/*     */ public class DominatorTreeBuilder
/*     */ {
/*     */   private IntList myDominators;
/*     */   private IntList myDominatorsTree;
/*     */   private IntList myAffected;
/*     */   private final int myNodesCnt;
/*     */   private final V8CachingReader myReader;
/*     */   private final V8PostOrderBuilder myPostOrderBuilder;
/*     */   @NotNull
/*     */   private final Flags myFlags;
/*     */   private int myRootPostOrderIndex;
/*     */   private V8HeapEntry myRoot;
/*     */   private final LinksReader<V8HeapEdge> myReverseIndexReader;
/*     */   private final boolean myShowHiddenData;
/*     */   
/*     */   public DominatorTreeBuilder(int nodesCnt, V8CachingReader reader, V8PostOrderBuilder postOrderBuilder, @NotNull Flags flags, @NotNull LinksReader<V8HeapEdge> linksReader, boolean showHiddenData) {
/*  52 */     this.myNodesCnt = nodesCnt;
/*  53 */     this.myReader = reader;
/*  54 */     this.myPostOrderBuilder = postOrderBuilder;
/*  55 */     this.myFlags = flags;
/*  56 */     this.myReverseIndexReader = linksReader;
/*  57 */     this.myShowHiddenData = showHiddenData;
/*  58 */     this.myDominators = (IntList)new IntArrayList(nodesCnt);
/*  59 */     this.myAffected = (IntList)new IntArrayList(nodesCnt);
/*     */   }
/*     */   
/*     */   private void initDominators(int nodesCnt) {
/*  63 */     this.myRootPostOrderIndex = nodesCnt - 1;
/*  64 */     for (int i = 0; i < this.myRootPostOrderIndex; i++) {
/*  65 */       this.myDominators.add(nodesCnt);
/*  66 */       this.myAffected.add(0);
/*     */     } 
/*  68 */     this.myAffected.add(0);
/*  69 */     this.myDominators.add(this.myRootPostOrderIndex);
/*     */   }
/*     */   
/*     */   public void execute() throws IOException {
/*  73 */     initDominators(this.myNodesCnt);
/*     */     
/*  75 */     this.myRoot = this.myReader.getNode(0L);
/*  76 */     for (V8HeapEdge edge : this.myReader.getChildren(this.myRoot)) {
/*  77 */       if (weakOrShortcut(edge))
/*  78 */         continue;  this.myAffected.set(this.myPostOrderBuilder.getNodeToPostOrder().getInt((int)edge.getToIndex()), 1);
/*     */     } 
/*     */     
/*  81 */     while (iteration());
/*     */     
/*  83 */     this.myDominatorsTree = (IntList)new IntArrayList(this.myNodesCnt);
/*  84 */     for (int i = 0; i < this.myNodesCnt; i++) {
/*  85 */       this.myDominatorsTree.add(-1);
/*     */     }
/*  87 */     for (int postOrderIndex = 0; postOrderIndex < this.myNodesCnt; postOrderIndex++) {
/*  88 */       int nodeIdx = this.myPostOrderBuilder.getPostOrderToNode().getInt(postOrderIndex);
/*  89 */       int dominatorByPostOrder = this.myDominators.getInt(postOrderIndex);
/*  90 */       if (dominatorByPostOrder < this.myNodesCnt) {
/*  91 */         this.myDominatorsTree.set(nodeIdx, this.myPostOrderBuilder.getPostOrderToNode().getInt(dominatorByPostOrder));
/*     */       } else {
/*  93 */         this.myDominatorsTree.set(nodeIdx, 0);
/*     */       } 
/*     */     } 
/*  96 */     this.myDominators = null;
/*  97 */     this.myAffected = null;
/*     */   }
/*     */   
/*     */   public IntList getDominatorsTree() {
/* 101 */     return this.myDominatorsTree;
/*     */   }
/*     */   
/*     */   private static boolean weakOrShortcut(V8HeapEdge edge) {
/* 105 */     return (V8HeapGraphEdgeType.kWeak.equals(edge.getType()) || V8HeapGraphEdgeType.kShortcut.equals(edge.getType()));
/*     */   }
/*     */   
/*     */   private boolean iteration() throws IOException {
/* 109 */     boolean changed = false;
/* 110 */     for (int postOrderIndex = this.myRootPostOrderIndex - 1; postOrderIndex >= 0; postOrderIndex--) {
/* 111 */       if (this.myAffected.getInt(postOrderIndex) != 0) {
/* 112 */         this.myAffected.set(postOrderIndex, 0);
/* 113 */         if (this.myDominators.getInt(postOrderIndex) != this.myRootPostOrderIndex) {
/* 114 */           int nodeIdx = this.myPostOrderBuilder.getPostOrderToNode().getInt(postOrderIndex);
/* 115 */           final boolean isNodePageObject = this.myFlags.isPage(nodeIdx);
/*     */           
/* 117 */           final int[] newDominatorIndex = new int[1];
/* 118 */           newDominatorIndex[0] = this.myNodesCnt;
/* 119 */           final boolean[] orphanNode = new boolean[1];
/* 120 */           orphanNode[0] = true;
/*     */           
/* 122 */           this.myReverseIndexReader.read(nodeIdx, new Processor<V8HeapEdge>()
/*     */               {
/*     */                 private boolean breakFlag = false;
/*     */                 
/*     */                 public boolean process(V8HeapEdge edge) {
/* 127 */                   if (this.breakFlag) return false; 
/* 128 */                   if (DominatorTreeBuilder.weakOrShortcut(edge)) return true; 
/* 129 */                   orphanNode[0] = false;
/* 130 */                   boolean retainerFlag = DominatorTreeBuilder.this.myFlags.isPage((int)edge.getFromIndex());
/* 131 */                   if (!DominatorTreeBuilder.this.myShowHiddenData && edge.getFromIndex() != 0L && isNodePageObject && !retainerFlag) return true;
/*     */                   
/* 133 */                   int retainerPostIndex = DominatorTreeBuilder.this.myPostOrderBuilder.getNodeToPostOrder().getInt((int)edge.getFromIndex());
/* 134 */                   if (DominatorTreeBuilder.this.myDominators.getInt(retainerPostIndex) != DominatorTreeBuilder.this.myNodesCnt) {
/* 135 */                     if (newDominatorIndex[0] == DominatorTreeBuilder.this.myNodesCnt) {
/* 136 */                       newDominatorIndex[0] = retainerPostIndex;
/*     */                     } else {
/*     */                       
/* 139 */                       while (retainerPostIndex != newDominatorIndex[0]) {
/* 140 */                         while (retainerPostIndex < newDominatorIndex[0]) {
/* 141 */                           retainerPostIndex = DominatorTreeBuilder.this.myDominators.getInt(retainerPostIndex);
/*     */                         }
/* 143 */                         while (newDominatorIndex[0] < retainerPostIndex) {
/* 144 */                           newDominatorIndex[0] = DominatorTreeBuilder.this.myDominators.getInt(newDominatorIndex[0]);
/*     */                         }
/*     */                       } 
/* 147 */                       if (newDominatorIndex[0] == DominatorTreeBuilder.this.myRootPostOrderIndex) {
/* 148 */                         this.breakFlag = true;
/* 149 */                         return false;
/*     */                       } 
/*     */                     } 
/*     */                   }
/* 153 */                   return true;
/*     */                 }
/*     */               });
/*     */           
/* 157 */           if (orphanNode[0]) {
/* 158 */             newDominatorIndex[0] = this.myRootPostOrderIndex;
/*     */           }
/* 160 */           if (newDominatorIndex[0] != this.myNodesCnt && this.myDominators.getInt(postOrderIndex) != newDominatorIndex[0])
/* 161 */           { this.myDominators.set(postOrderIndex, newDominatorIndex[0]);
/* 162 */             for (V8HeapEdge edge : this.myReader.getChildrenByNodeId(Long.valueOf(nodeIdx))) {
/* 163 */               this.myAffected.set(this.myPostOrderBuilder.getNodeToPostOrder().getInt((int)edge.getToIndex()), 1);
/*     */             }
/* 165 */             changed = true; } 
/*     */         } 
/*     */       } 
/* 168 */     }  return changed;
/*     */   }
/*     */ }


/* Location:              C:\Program Files\JetBrains\WebStorm 2021.1\plugins\NodeJS\lib\NodeJS.jar!\com\jetbrains\nodejs\run\profile\heap\calculation\DominatorTreeBuilder.class
 * Java compiler version: 11 (55.0)
 * JD-Core Version:       1.1.3
 */