/*     */ package org.bipolar.run.profile.heap.calculation;
/*     */ 
/*     */ import com.intellij.util.Consumer;
/*     */ import com.intellij.util.PairConsumer;
/*     */ import com.intellij.util.PairProcessor;
/*     */ import org.bipolar.run.profile.heap.V8CachingReader;
/*     */ import org.bipolar.run.profile.heap.data.V8HeapEdge;
/*     */ import org.bipolar.run.profile.heap.data.V8HeapEntry;
/*     */ import org.bipolar.run.profile.heap.data.V8HeapGraphEdgeType;
/*     */ import org.bipolar.run.profile.heap.data.V8HeapNodeType;
/*     */ import it.unimi.dsi.fastutil.ints.IntArrayList;
/*     */ import it.unimi.dsi.fastutil.ints.IntList;
/*     */ import it.unimi.dsi.fastutil.ints.IntOpenHashSet;
/*     */
/*     */ import java.util.ArrayDeque;
/*     */ import java.util.Deque;
/*     */ import java.util.function.IntConsumer;
/*     */ import org.jetbrains.annotations.NotNull;
/*     */ import org.jetbrains.annotations.Nullable;
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
/*     */ public class V8DistancesCalculator
/*     */ {
/*     */   private static final int NO_DISTANCE = -5;
/*     */   public static final int SYSTEM_DISTANCE = 100000000;
/*     */   private final IntList myDistances;
/*     */   private final IntList myParents;
/*     */   private final int myNodesCnt;
/*     */   private final V8CachingReader myReader;
/*     */   @Nullable
/*     */   private final V8HeapEntry myDocumentDOMTreeRoot;
/*     */   @Nullable
/*     */   private final V8HeapEntry myGcRootsRoot;
/*     */   private final V8ImportantStringsHolder myStringsHolder;
/*     */   private int myVisitedCnt;
/*     */   private static final String SLOPPY = "sloppy_function_map";
/*     */   private static final String SYSTEM_NATIVE = "system / NativeContext";
/*     */   private static final String MAP_DESCRIPTORS = "(map descriptors)";
/*     */   
/*     */   public V8DistancesCalculator(int nodesCnt, @NotNull V8CachingReader reader, @Nullable V8HeapEntry documentDOMTreeRoot, @Nullable V8HeapEntry gcRootsRoot, V8ImportantStringsHolder stringsHolder) {
/*  56 */     this.myNodesCnt = nodesCnt;
/*  57 */     this.myReader = reader;
/*  58 */     this.myDocumentDOMTreeRoot = documentDOMTreeRoot;
/*  59 */     this.myGcRootsRoot = gcRootsRoot;
/*  60 */     this.myStringsHolder = stringsHolder;
/*  61 */     this.myDistances = (IntList)new IntArrayList();
/*  62 */     this.myParents = (IntList)new IntArrayList();
/*  63 */     this.myVisitedCnt = 0;
/*     */   }
/*     */   
/*     */   public void execute() {
/*  67 */     for (int i = 0; i < this.myNodesCnt; i++) {
/*  68 */       this.myDistances.add(-5);
/*  69 */       this.myParents.add(-1);
/*     */     } 
/*  71 */     ArrayDeque<Integer> queue = new ArrayDeque<>();
/*  72 */     foreachUserRoot(idx -> enqueue(idx.intValue(), 1, queue, 0));
/*     */     
/*  74 */     PairProcessor<V8HeapEntry, V8HeapEdge> filter = createFilter();
/*  75 */     bfs(queue, filter);
/*     */     
/*  77 */     this.myVisitedCnt = 0;
/*  78 */     queue.clear();
/*     */     
/*  80 */     foreachRoot((idx, parent) -> enqueue(idx.intValue(), 100000000, queue, parent.intValue()));
/*     */     
/*  82 */     bfs(queue, filter);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   @NotNull
/*     */   public PairProcessor<V8HeapEntry, V8HeapEdge> createFilter() {
/*     */     if (((node, edge) -> {
/*     */         if (V8HeapNodeType.kHidden.equals(node.getType())) {
/*  93 */           boolean isSloppy = (edge.hasStringName() && this.myStringsHolder.get("sloppy_function_map") == edge.getNameId());
/*     */           boolean isSystemNative = (this.myStringsHolder.get("system / NativeContext") == node.getNameId());
/*  95 */           return (!isSloppy || !isSystemNative);
/*     */         }  if (V8HeapNodeType.kArray.equals(node.getType()))
/*     */         { if (this.myStringsHolder.get("(map descriptors)") != node.getNameId())
/*     */             return true;  Integer integer = edge.nameAsInt(this.myReader);
/*  99 */           return (integer != null && (integer.intValue() < 2 || integer.intValue() % 3 != 1)); }  return true; }) == null) $$$reportNull$$$0(1);  return (node, edge) -> { if (V8HeapNodeType.kHidden.equals(node.getType())) { boolean isSloppy = (edge.hasStringName() && this.myStringsHolder.get("sloppy_function_map") == edge.getNameId()); boolean isSystemNative = (this.myStringsHolder.get("system / NativeContext") == node.getNameId()); return (!isSloppy || !isSystemNative); }  if (V8HeapNodeType.kArray.equals(node.getType())) { if (this.myStringsHolder.get("(map descriptors)") != node.getNameId()) return true;  Integer integer = edge.nameAsInt(this.myReader); return (integer != null && (integer.intValue() < 2 || integer.intValue() % 3 != 1)); }
/*     */         
/*     */         return true;
/*     */       };
/*     */   }
/*     */   
/*     */   private void foreachUserRoot(Consumer<Integer> consumer) {
/* 106 */     if (this.myGcRootsRoot == null)
/*     */       return; 
/* 108 */     IntOpenHashSet intOpenHashSet = new IntOpenHashSet();
/* 109 */     IntConsumer action = integer -> {
/*     */         if (!visited.contains(integer)) {
/*     */           visited.add(integer);
/*     */           
/*     */           consumer.consume(Integer.valueOf(integer));
/*     */         } 
/*     */       };
/* 116 */     for (V8HeapEdge edge : this.myReader.getChildrenByNodeId(Long.valueOf(0L))) {
/* 117 */       if (isUserRoot((int)edge.getToIndex())) {
/* 118 */         action.accept((int)edge.getToIndex());
/*     */       }
/*     */     } 
/*     */   }
/*     */   
/*     */   private void foreachRoot(PairConsumer<Integer, Integer> consumer) {
/* 124 */     if (this.myGcRootsRoot == null)
/*     */       return; 
/* 126 */     IntOpenHashSet intOpenHashSet = new IntOpenHashSet();
/* 127 */     PairConsumer<Integer, Integer> action = (integer, parent) -> {
/*     */         if (!visited.contains(integer)) {
/*     */           visited.add(integer);
/*     */           consumer.consume(integer, parent);
/*     */         } 
/*     */       };
/* 133 */     for (V8HeapEdge edge : this.myReader.getChildren(this.myGcRootsRoot)) {
/* 134 */       for (V8HeapEdge innerEdge : this.myReader.getChildrenByNodeId(Long.valueOf(edge.getToIndex()))) {
/* 135 */         action.consume(Integer.valueOf((int)innerEdge.getToIndex()), Integer.valueOf((int)edge.getToIndex()));
/*     */       }
/* 137 */       action.consume(Integer.valueOf((int)edge.getToIndex()), Integer.valueOf((int)this.myGcRootsRoot.getId()));
/*     */     } 
/* 139 */     for (V8HeapEdge edge : this.myReader.getChildrenByNodeId(Long.valueOf(0L))) {
/* 140 */       action.consume(Integer.valueOf((int)edge.getToIndex()), Integer.valueOf(0));
/*     */     }
/*     */   }
/*     */   
/*     */   private boolean isUserRoot(int nodeIdx) {
/* 145 */     if (this.myDocumentDOMTreeRoot != null && this.myDocumentDOMTreeRoot.getId() == nodeIdx) return true; 
/* 146 */     return !V8HeapNodeType.kSynthetic.equals(this.myReader.getNode(nodeIdx).getType());
/*     */   }
/*     */   
/*     */   private void enqueue(int nodeIdx, int distance, Deque<Integer> queue, int parentNode) {
/* 150 */     if (this.myDistances.getInt(nodeIdx) != -5)
/* 151 */       return;  this.myDistances.set(nodeIdx, distance);
/* 152 */     this.myParents.set(nodeIdx, parentNode);
/* 153 */     queue.addLast(Integer.valueOf(nodeIdx));
/* 154 */     this.myVisitedCnt++;
/*     */   }
/*     */   
/*     */   private void bfs(ArrayDeque<Integer> queue, @NotNull PairProcessor<V8HeapEntry, V8HeapEdge> filter) {
/* 158 */     if (filter == null) $$$reportNull$$$0(2);  while (!queue.isEmpty()) {
/* 159 */       int nodeIdx = ((Integer)queue.removeFirst()).intValue();
/* 160 */       int newDistance = this.myDistances.getInt(nodeIdx) + 1;
/*     */       
/* 162 */       V8HeapEntry node = this.myReader.getNode(nodeIdx);
/* 163 */       for (V8HeapEdge edge : this.myReader.getChildren(node)) {
/* 164 */         if (V8HeapGraphEdgeType.kWeak.equals(edge.getType()))
/* 165 */           continue;  int edgeToIndex = (int)edge.getToIndex();
/* 166 */         if (this.myDistances.getInt(edgeToIndex) != -5 || 
/* 167 */           !filter.process(node, edge))
/* 168 */           continue;  this.myDistances.set(edgeToIndex, newDistance);
/* 169 */         this.myParents.set(edgeToIndex, nodeIdx);
/* 170 */         queue.addLast(Integer.valueOf(edgeToIndex));
/* 171 */         this.myVisitedCnt++;
/*     */       } 
/*     */     } 
/* 174 */     if (this.myVisitedCnt > this.myNodesCnt);
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public IntList getDistances() {
/* 180 */     return this.myDistances;
/*     */   }
/*     */   
/*     */   public IntList getParents() {
/* 184 */     return this.myParents;
/*     */   }
/*     */ }


/* Location:              C:\Program Files\JetBrains\WebStorm 2021.1\plugins\NodeJS\lib\NodeJS.jar!\com\jetbrains\nodejs\run\profile\heap\calculation\V8DistancesCalculator.class
 * Java compiler version: 11 (55.0)
 * JD-Core Version:       1.1.3
 */