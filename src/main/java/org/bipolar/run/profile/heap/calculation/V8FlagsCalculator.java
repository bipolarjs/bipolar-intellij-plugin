/*     */ package org.bipolar.run.profile.heap.calculation;
/*     */ 
/*     */ import org.bipolar.run.profile.heap.V8CachingReader;
/*     */ import org.bipolar.run.profile.heap.data.V8HeapEdge;
/*     */ import org.bipolar.run.profile.heap.data.V8HeapEntry;
/*     */ import org.bipolar.run.profile.heap.data.V8HeapGraphEdgeType;
/*     */ import org.bipolar.run.profile.heap.data.V8HeapNodeType;
/*     */ import java.util.ArrayDeque;
/*     */ import java.util.ArrayList;
/*     */ import java.util.List;
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
/*     */ public class V8FlagsCalculator
/*     */ {
/*     */   private final Flags myFlags;
/*     */   private final V8CachingReader myReader;
/*     */   private V8HeapEntry myGcRoots;
/*     */   private V8HeapEntry myDocumentDOMRoot;
/*     */   private V8HeapEntry myRoot;
/*     */   private List<V8HeapEntry> myImmediateRootChildren;
/*     */   private V8HeapEntry myDetachedDOMTreesRoot;
/*     */   
/*     */   public V8FlagsCalculator(int numNodes, V8CachingReader reader, V8ImportantStringsHolder stringsHolder) {
/*  41 */     this.myReader = reader;
/*  42 */     this.myFlags = new Flags(numNodes);
/*     */     
/*  44 */     this.myImmediateRootChildren = new ArrayList<>();
/*  45 */     this.myRoot = this.myReader.getNode(0L);
/*  46 */     for (V8HeapEdge edge : this.myReader.getChildren(this.myRoot)) {
/*  47 */       V8HeapEntry node = this.myReader.getNode(edge.getToIndex());
/*  48 */       this.myImmediateRootChildren.add(node);
/*  49 */       long nameId = node.getNameId();
/*  50 */       if (nameId == stringsHolder.get("(Detached DOM trees)")) {
/*  51 */         this.myDetachedDOMTreesRoot = node; continue;
/*  52 */       }  if (nameId == stringsHolder.get("(Document DOM trees)")) {
/*  53 */         this.myDocumentDOMRoot = node; continue;
/*  54 */       }  if (nameId == stringsHolder.get("(GC roots)")) {
/*  55 */         this.myGcRoots = node;
/*     */       }
/*     */     } 
/*     */   }
/*     */   
/*     */   public Flags execute() {
/*  61 */     markDetachedDOMTreeNodes();
/*  62 */     markQueriableHeapObjects();
/*  63 */     markPageOwnedNodes();
/*     */     
/*  65 */     this.myRoot = null;
/*  66 */     this.myImmediateRootChildren = null;
/*  67 */     return this.myFlags;
/*     */   }
/*     */   
/*     */   private void markDetachedDOMTreeNodes() {
/*  71 */     if (this.myDetachedDOMTreesRoot == null)
/*     */       return; 
/*  73 */     String secondPattern = "Detached DOM tree";
/*     */     
/*  75 */     for (V8HeapEdge edge : this.myReader.getChildren(this.myDetachedDOMTreesRoot)) {
/*  76 */       V8HeapEntry child = this.myReader.getNode(edge.getToIndex());
/*  77 */       if ((V8HeapNodeType.kObject.equals(child.getType()) || V8HeapNodeType.kNative.equals(child.getType())) && this.myReader
/*  78 */         .getString(child.getNameId()).startsWith("Detached DOM tree")) {
/*  79 */         for (V8HeapEdge heapEdge : this.myReader.getChildren(child)) {
/*  80 */           this.myFlags.addDetachedFlag((int)heapEdge.getToIndex());
/*     */         }
/*     */       }
/*     */     } 
/*     */   }
/*     */   
/*     */   private void markQueriableHeapObjects() {
/*  87 */     ArrayDeque<V8HeapEntry> queue = new ArrayDeque<>();
/*  88 */     for (V8HeapEntry child : this.myImmediateRootChildren) {
/*  89 */       if (!V8HeapNodeType.kSynthetic.equals(child.getType())) {
/*  90 */         queue.add(child);
/*     */       }
/*     */     } 
/*  93 */     while (!queue.isEmpty()) {
/*  94 */       V8HeapEntry entry = queue.removeFirst();
/*  95 */       if (this.myFlags.isQueriable((int)entry.getId()))
/*  96 */         continue;  this.myFlags.addQueriableFlag((int)entry.getId());
/*     */       
/*  98 */       for (V8HeapEdge edge : this.myReader.getChildren(entry)) {
/*  99 */         if (V8HeapGraphEdgeType.isInternalKind(edge.getType())) {
/*     */           continue;
/*     */         }
/* 102 */         if (this.myFlags.isQueriable((int)edge.getToIndex()))
/*     */           continue; 
/* 104 */         queue.add(this.myReader.getNode(edge.getToIndex()));
/*     */       } 
/*     */     } 
/*     */   }
/*     */   
/*     */   private void markPageOwnedNodes() {
/* 110 */     ArrayDeque<Long> queue = new ArrayDeque<>();
/* 111 */     for (V8HeapEdge edge : this.myReader.getChildren(this.myRoot)) {
/* 112 */       if ((edge.getType().equals(V8HeapGraphEdgeType.kElement) && this.myDocumentDOMRoot != null && edge.getToIndex() == this.myDocumentDOMRoot.getId()) || edge
/* 113 */         .getType().equals(V8HeapGraphEdgeType.kShortcut)) {
/*     */         
/* 115 */         queue.add(Long.valueOf(edge.getToIndex()));
/* 116 */         this.myFlags.addVisited((int)edge.getToIndex());
/*     */       } 
/*     */     } 
/*     */     
/* 120 */     while (!queue.isEmpty()) {
/* 121 */       Long index = queue.removeLast();
/* 122 */       int idx = index.intValue();
/* 123 */       this.myFlags.addPage(idx);
/* 124 */       this.myFlags.clearVisited(idx);
/*     */       
/* 126 */       for (V8HeapEdge edge : this.myReader.getChildrenByNodeId(index)) {
/* 127 */         if (this.myFlags.visitedAndPage((int)edge.getToIndex()) || 
/* 128 */           V8HeapGraphEdgeType.kWeak.equals(edge.getType()))
/* 129 */           continue;  this.myFlags.addVisited((int)edge.getToIndex());
/* 130 */         queue.add(Long.valueOf(edge.getToIndex()));
/*     */       } 
/*     */     } 
/*     */   }
/*     */   
/*     */   public V8HeapEntry getDocumentDOMRoot() {
/* 136 */     return this.myDocumentDOMRoot;
/*     */   }
/*     */   
/*     */   public V8HeapEntry getGcRoots() {
/* 140 */     return this.myGcRoots;
/*     */   }
/*     */   
/*     */   public V8HeapEntry getDetachedDOMTreesRoot() {
/* 144 */     return this.myDetachedDOMTreesRoot;
/*     */   }
/*     */ }


/* Location:              C:\Program Files\JetBrains\WebStorm 2021.1\plugins\NodeJS\lib\NodeJS.jar!\com\jetbrains\nodejs\run\profile\heap\calculation\V8FlagsCalculator.class
 * Java compiler version: 11 (55.0)
 * JD-Core Version:       1.1.3
 */