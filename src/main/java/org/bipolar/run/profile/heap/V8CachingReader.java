/*     */ package org.bipolar.run.profile.heap;
/*     */ 
/*     */ import com.intellij.openapi.util.Pair;
/*     */ import com.intellij.util.Consumer;
/*     */ import com.intellij.util.containers.SLRUMap;
/*     */ import org.bipolar.NodeJSBundle;
/*     */ import org.bipolar.run.profile.heap.calculation.ByteArrayWrapper;
/*     */ import org.bipolar.run.profile.heap.calculation.Flags;
/*     */ import org.bipolar.run.profile.heap.calculation.V8HeapInMemoryIndexes;
/*     */ import org.bipolar.run.profile.heap.calculation.V8ImportantStringsHolder;
/*     */ import org.bipolar.run.profile.heap.calculation.V8StringIndex;
/*     */ import org.bipolar.run.profile.heap.data.Aggregate;
/*     */ import org.bipolar.run.profile.heap.data.LinkedByNameId;
/*     */ import org.bipolar.run.profile.heap.data.V8HeapEdge;
/*     */ import org.bipolar.run.profile.heap.data.V8HeapEntry;
/*     */ import org.bipolar.run.profile.heap.data.V8HeapGraphEdgeType;
/*     */ import org.bipolar.run.profile.heap.data.V8HeapHeader;
/*     */ import org.bipolar.run.profile.heap.data.V8HeapNodeType;
/*     */ import org.bipolar.run.profile.heap.io.RandomRawReader;
/*     */ import org.bipolar.run.profile.heap.io.RawSerializer;
/*     */ import org.bipolar.run.profile.heap.io.reverse.LinksReader;
/*     */ import org.bipolar.run.profile.heap.io.reverse.LinksReaderFactory;
/*     */ import org.bipolar.run.profile.heap.view.models.V8HeapContainmentTreeTableModel;
/*     */ import java.io.Closeable;
/*     */ import java.io.File;
/*     */ import java.io.FileNotFoundException;
/*     */ import java.io.IOException;
/*     */ import java.util.ArrayList;
/*     */ import java.util.Collections;
/*     */ import java.util.Comparator;
/*     */ import java.util.List;
/*     */ import java.util.Map;
/*     */ import java.util.TreeMap;
/*     */ import javax.swing.tree.TreePath;
/*     */
import org.jetbrains.annotations.Nls;
/*     */ import org.jetbrains.annotations.NotNull;
/*     */ import org.jetbrains.annotations.Nullable;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class V8CachingReader
/*     */   implements Closeable
/*     */ {
/*     */   @NotNull
/*     */   private final File myOriginalFile;
/*     */   @NotNull
/*     */   private final ByteArrayWrapper myDigest;
/*     */   @NotNull
/*     */   private final V8StringIndex myStringIndex;
/*     */   @NotNull
/*     */   private final File myNodeIndexFile;
/*     */   @NotNull
/*     */   private final File myEdgeIndexFile;
/*     */   @NotNull
/*     */   private final LinksReaderFactory<LinkedByNameId> myStringReverseIndexProcessor;
/*     */   @NotNull
/*     */   private final LinksReaderFactory<V8HeapEdge> myReverseLinkIndexProcessor;
/*     */   private final SLRUMap<Long, V8HeapEntry> myNodeMap;
/*     */   private final SLRUMap<Long, V8HeapEdge> myEdgeMap;
/*     */   private final SLRUMap<Long, List<V8HeapEdge>> myChildrenMap;
/*     */   private final SLRUMap<Long, List<Long>> myChildrenByAggregateMap;
/*     */   private final SLRUMap<Long, List<V8HeapEdge>> myParentsMap;
/*     */   private final SLRUMap<Long, String> myStringMap;
/*     */   private final RandomRawReader<V8HeapEntry> myNodeReader;
/*     */   private final RandomRawReader<V8HeapEdge> myEdgeReader;
/*     */   private final LinksReader<LinkedByNameId> myReverseStringIndexReader;
/*     */   private final LinksReader<V8HeapEdge> myReverseLinksReader;
/*     */   private final CompositeCloseable myResourses;
/*     */   private final V8HeapHeader myHeader;
/*     */   @Nullable
/*     */   private final Consumer<? super String> myErrorNotificator;
/*     */   private final Comparator<V8HeapEdge> myRetainersComparator;
/*     */   @NotNull
/*     */   private final V8ImportantStringsHolder myStringsHolder;
/*     */   private LinksReader<Long> myAggregatesLinksReader;
/*     */   private final Comparator<V8HeapEdge> myChildrenComparator;
/*     */   private final Object myLock;
/*     */   private LinksReaderFactory<Long> myAggregatesLinksReaderFactory;
/*     */   private final Comparator<Long> myByRetained;
/*     */   private V8HeapEntry myRootNode;
/*     */   private boolean myShowHidden;
/*     */   private V8HeapInMemoryIndexes myInMemoryIndexes;
/*     */   
/*     */   public V8CachingReader(@NotNull File originalFile, @NotNull ByteArrayWrapper digest, CompositeCloseable resourses, V8HeapHeader header, @Nullable Consumer<? super String> errorNotificator, @NotNull V8StringIndex stringIndex, @NotNull File nodeIndexFile, @NotNull File edgeIndexFile, @NotNull LinksReaderFactory<LinkedByNameId> stringReverseIndexProcessor, @NotNull LinksReaderFactory<V8HeapEdge> reverseLinkIndexProcessor, @NotNull V8ImportantStringsHolder stringsHolder) throws IOException {
/*  87 */     this.myOriginalFile = originalFile;
/*  88 */     this.myDigest = digest;
/*  89 */     this.myStringsHolder = stringsHolder;
/*  90 */     this.myShowHidden = true;
/*  91 */     this.myResourses = resourses;
/*  92 */     this.myHeader = header;
/*  93 */     this.myErrorNotificator = errorNotificator;
/*  94 */     this.myStringIndex = stringIndex;
/*  95 */     this.myNodeIndexFile = nodeIndexFile;
/*  96 */     this.myEdgeIndexFile = edgeIndexFile;
/*  97 */     this.myStringReverseIndexProcessor = stringReverseIndexProcessor;
/*  98 */     this.myReverseLinkIndexProcessor = reverseLinkIndexProcessor;
/*  99 */     this.myNodeMap = new SLRUMap(600, 1500);
/* 100 */     this.myChildrenMap = new SLRUMap(600, 1500);
/* 101 */     this.myParentsMap = new SLRUMap(200, 500);
/* 102 */     this.myStringMap = new SLRUMap(200, 500);
/* 103 */     this.myEdgeMap = new SLRUMap(200, 500);
/* 104 */     this.myChildrenByAggregateMap = new SLRUMap(200, 500);
/*     */     
/* 106 */     this.myLock = new Object();
/*     */     
/* 108 */     this.myNodeReader = this.myResourses.<RandomRawReader<V8HeapEntry>>register(new RandomRawReader(nodeIndexFile, (RawSerializer)V8HeapEntry.MyRawSerializer.getInstance()));
/* 109 */     this.myEdgeReader = this.myResourses.<RandomRawReader<V8HeapEdge>>register(new RandomRawReader(edgeIndexFile, (RawSerializer)V8HeapEdge.MyRawSerializer.getInstance()));
/* 110 */     this.myReverseLinksReader = this.myResourses.<LinksReader<V8HeapEdge>>register(reverseLinkIndexProcessor.create(false));
/* 111 */     this.myReverseStringIndexReader = this.myResourses.<LinksReader<LinkedByNameId>>register(stringReverseIndexProcessor.create(false));
/*     */     
/* 113 */     fillInitial();
/* 114 */     this.myChildrenComparator = ((o1, o2) -> Long.compare(getRetainedSize((int)o2.getToIndex()), getRetainedSize((int)o1.getToIndex())));
/* 115 */     this.myRetainersComparator = Comparator.comparingInt(o -> getDistance((int)o.getFromIndex()));
/* 116 */     this.myByRetained = ((o1, o2) -> Long.compare(this.myInMemoryIndexes.getRetainedSizes().getLong(o2.intValue()), this.myInMemoryIndexes.getRetainedSizes().getLong(o1.intValue())));
/*     */   }
/*     */ 
/*     */   
/*     */   @NotNull
/*     */   public File getOriginalFile() {
/* 122 */     if (this.myOriginalFile == null) $$$reportNull$$$0(8);  return this.myOriginalFile;
/*     */   }
/*     */   
/*     */   @NotNull
/*     */   public ByteArrayWrapper getDigest() {
/* 127 */     if (this.myDigest == null) $$$reportNull$$$0(9);  return this.myDigest;
/*     */   }
/*     */   
/*     */   public CompositeCloseable getResourses() {
/* 131 */     return this.myResourses;
/*     */   }
/*     */   
/*     */   @NotNull
/*     */   public LinksReaderFactory<LinkedByNameId> getStringReverseIndexReaderFactory() {
/* 136 */     if (this.myStringReverseIndexProcessor == null) $$$reportNull$$$0(10);  return this.myStringReverseIndexProcessor;
/*     */   }
/*     */   
/*     */   public V8HeapHeader getHeader() {
/* 140 */     return this.myHeader;
/*     */   }
/*     */   
/*     */   @NotNull
/*     */   public LinksReaderFactory<V8HeapEdge> getReverseLinkIndexReaderFactory() {
/* 145 */     if (this.myReverseLinkIndexProcessor == null) $$$reportNull$$$0(11);  return this.myReverseLinkIndexProcessor;
/*     */   }
/*     */   
/*     */   @NotNull
/*     */   public File getNodeIndexFile() {
/* 150 */     if (this.myNodeIndexFile == null) $$$reportNull$$$0(12);  return this.myNodeIndexFile;
/*     */   }
/*     */   
/*     */   @NotNull
/*     */   public File getEdgeIndexFile() {
/* 155 */     if (this.myEdgeIndexFile == null) $$$reportNull$$$0(13);  return this.myEdgeIndexFile;
/*     */   }
/*     */ 
/*     */   
/*     */   public void close() throws IOException {
/* 160 */     this.myResourses.close();
/*     */   }
/*     */   
/*     */   private void fillInitial() throws IOException {
/* 164 */     this.myRootNode = (V8HeapEntry)this.myNodeReader.read(0L);
/* 165 */     this.myNodeMap.put(Long.valueOf(0L), this.myRootNode);
/*     */   }
/*     */   
/*     */   public V8HeapEntry getNode(long id) {
/* 169 */     synchronized (this.myLock) {
/* 170 */       V8HeapEntry cached = (V8HeapEntry)this.myNodeMap.get(Long.valueOf(id));
/* 171 */       if (cached != null) return cached; 
/*     */       try {
/* 173 */         V8HeapEntry node = (V8HeapEntry)this.myNodeReader.read(id);
/* 174 */         this.myNodeMap.put(Long.valueOf(id), node);
/* 175 */         return node;
/*     */       }
/* 177 */       catch (IOException e) {
/* 178 */         reportError(e.getMessage());
/* 179 */         return errorNode();
/*     */       } 
/*     */     } 
/*     */   }
/*     */   
/*     */   public List<V8HeapEdge> getChildrenByNodeId(@NotNull Long parentId) {
/* 185 */     if (parentId == null) $$$reportNull$$$0(14);  synchronized (this.myLock) {
/* 186 */       List<V8HeapEdge> list = (List<V8HeapEdge>)this.myChildrenMap.get(parentId);
/* 187 */       if (list != null) return list; 
/* 188 */       return getChildren(getNode(parentId.longValue()));
/*     */     } 
/*     */   }
/*     */   
/*     */   public List<V8HeapEdge> getChildren(@NotNull V8HeapEntry parent) {
/* 193 */     if (parent == null) $$$reportNull$$$0(15);  synchronized (this.myLock) {
/* 194 */       if (parent.getChildrenCount() == 0L) return Collections.emptyList();
/*     */       
/* 196 */       List<V8HeapEdge> list = (List<V8HeapEdge>)this.myChildrenMap.get(Long.valueOf(parent.getId()));
/* 197 */       if (list == null) {
/*     */         try {
/* 199 */           list = new ArrayList<>((int)parent.getChildrenCount());
/* 200 */           long edgesOffset = parent.getEdgesOffset();
/* 201 */           List<V8HeapEdge> finalList = list;
/* 202 */           this.myEdgeReader.read(edgesOffset, (int)parent.getChildrenCount(), edge -> {
/*     */                 if (this.myShowHidden || (!V8HeapGraphEdgeType.kHidden.equals(edge.getType()) && !V8HeapNodeType.kHidden.equals(getNode(edge.getToIndex()).getType()))) {
/*     */                   finalList.add(edge);
/*     */                 }
/*     */                 
/*     */                 return true;
/*     */               });
/* 209 */           if (this.myInMemoryIndexes != null) {
/* 210 */             list.sort(this.myChildrenComparator);
/*     */           }
/* 212 */           this.myChildrenMap.put(Long.valueOf(parent.getId()), list);
/*     */           
/* 214 */           for (V8HeapEdge edge : list) {
/* 215 */             this.myEdgeMap.put(Long.valueOf(edge.getId()), edge);
/*     */           }
/*     */         }
/* 218 */         catch (IOException e) {
/* 219 */           reportError(e.getMessage());
/* 220 */           return null;
/*     */         } 
/*     */       }
/* 223 */       return Collections.unmodifiableList(list);
/*     */     } 
/*     */   }
/*     */   
/*     */   public V8HeapEdge getEdge(long id) {
/* 228 */     synchronized (this.myLock) {
/* 229 */       V8HeapEdge edge = (V8HeapEdge)this.myEdgeMap.get(Long.valueOf(id));
/* 230 */       if (edge != null) return edge; 
/*     */       try {
/* 232 */         this.myEdgeMap.put(Long.valueOf(id), edge = (V8HeapEdge)this.myEdgeReader.read(id));
/*     */       }
/* 234 */       catch (IOException e) {
/* 235 */         reportError(e.getMessage());
/* 236 */         return null;
/*     */       } 
/* 238 */       return edge;
/*     */     } 
/*     */   }
/*     */   
/*     */   public List<V8HeapEdge> getParents(@NotNull V8HeapEntry child) {
/* 243 */     if (child == null) $$$reportNull$$$0(16);  synchronized (this.myLock) {
/* 244 */       List<V8HeapEdge> edges = (List<V8HeapEdge>)this.myParentsMap.get(Long.valueOf(child.getId()));
/* 245 */       if (edges == null) {
/* 246 */         if (!this.myShowHidden && V8HeapNodeType.kHidden.equals(child.getType())) return Collections.emptyList(); 
/*     */         try {
/* 248 */           edges = new ArrayList<>();
/* 249 */           List<V8HeapEdge> finalEdges = edges;
/* 250 */           this.myReverseLinksReader.read(child.getId(), edge -> {
/*     */                 if (this.myShowHidden || !V8HeapGraphEdgeType.kHidden.equals(edge.getType())) {
/*     */                   finalEdges.add(edge);
/*     */                 }
/*     */                 return true;
/*     */               });
/* 256 */           if (this.myInMemoryIndexes != null) {
/* 257 */             edges.sort(this.myRetainersComparator);
/*     */           }
/* 259 */           this.myParentsMap.put(Long.valueOf(child.getId()), edges);
/*     */         }
/* 261 */         catch (IOException e) {
/* 262 */           reportError(e.getMessage());
/* 263 */           return null;
/*     */         } 
/*     */       } 
/* 266 */       return edges;
/*     */     } 
/*     */   }
/*     */   
/*     */   public int getRetainersCount(@NotNull V8HeapEntry child) {
/* 271 */     if (child == null) $$$reportNull$$$0(17);  List<V8HeapEdge> parents = getParents(child);
/* 272 */     return (parents == null) ? 0 : parents.size();
/*     */   }
/*     */   
/*     */   public Pair<V8HeapEntry, V8HeapEdge> getRetainerChild(@NotNull V8HeapEntry entry, long idx) {
/* 276 */     if (entry == null) $$$reportNull$$$0(18);  synchronized (this.myLock) {
/* 277 */       List<V8HeapEdge> retainers = getParents(entry);
/* 278 */       if (retainers == null || retainers.size() <= idx) {
/* 279 */         return Pair.create(errorNode(), errorEdge());
/*     */       }
/*     */       
/* 282 */       V8HeapEdge edge = retainers.get((int)idx);
/* 283 */       V8HeapEntry node = getNode(edge.getFromIndex());
/* 284 */       return Pair.create(node, edge);
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   public Pair<V8HeapEntry, V8HeapEdge> getChildById(@NotNull V8HeapEntry parent, long id) {
/* 290 */     if (parent == null) $$$reportNull$$$0(19);  synchronized (this.myLock) {
/* 291 */       List<V8HeapEdge> list = getChildren(parent);
/* 292 */       if (list != null) {
/* 293 */         for (V8HeapEdge edge : list) {
/* 294 */           if (edge.getToIndex() == id) {
/* 295 */             return Pair.create(getNode(edge.getToIndex()), edge);
/*     */           }
/*     */         } 
/*     */       }
/* 299 */       return Pair.create(errorNode(), errorEdge());
/*     */     } 
/*     */   }
/*     */   
/*     */   public Pair<V8HeapEntry, V8HeapEdge> getChildByLinkId(@NotNull V8HeapEntry parent, long linkId) {
/* 304 */     if (parent == null) $$$reportNull$$$0(20);  synchronized (this.myLock) {
/* 305 */       List<V8HeapEdge> list = getChildren(parent);
/* 306 */       if (list != null) {
/* 307 */         for (V8HeapEdge edge : list) {
/* 308 */           if (edge.getId() == linkId) {
/* 309 */             return Pair.create(getNode(edge.getToIndex()), edge);
/*     */           }
/*     */         } 
/*     */       }
/* 313 */       return Pair.create(errorNode(), errorEdge());
/*     */     } 
/*     */   }
/*     */   
/*     */   @NotNull
/*     */   private V8HeapEdge errorEdge() {
/* 319 */     if (V8HeapEdge.createFromJson(2, -1L, -1L, -1L) == null) $$$reportNull$$$0(21);  return V8HeapEdge.createFromJson(2, -1L, -1L, -1L);
/*     */   }
/*     */   
/*     */   public Pair<V8HeapEntry, V8HeapEdge> getChild(@NotNull V8HeapEntry parent, long idx) {
/* 323 */     if (parent == null) $$$reportNull$$$0(22);  synchronized (this.myLock) {
/* 324 */       List<V8HeapEdge> list = getChildren(parent);
/* 325 */       if (list == null || list.size() <= idx) {
/* 326 */         return Pair.create(errorNode(), errorEdge());
/*     */       }
/*     */       
/* 329 */       V8HeapEdge edge = list.get((int)idx);
/* 330 */       V8HeapEntry node = getNode(edge.getToIndex());
/* 331 */       return Pair.create(node, edge);
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   public int getRetainersChildIdx(@NotNull V8HeapEntry child, long parentId) {
/* 337 */     if (child == null) $$$reportNull$$$0(23);  synchronized (this.myLock) {
/* 338 */       List<V8HeapEdge> parents = getParents(child);
/* 339 */       if (parents != null)
/* 340 */         for (int i = 0; i < parents.size(); i++) {
/* 341 */           V8HeapEdge edge = parents.get(i);
/* 342 */           if (edge.getFromIndex() == parentId) return i;
/*     */         
/*     */         }  
/* 345 */       return 0;
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   public List<Pair<V8HeapEdge, V8HeapEntry>> getNodesByNameId(long nameId) {
/* 351 */     getString(nameId);
/* 352 */     List<Pair<V8HeapEdge, V8HeapEntry>> result = new ArrayList<>();
/*     */     try {
/* 354 */       this.myReverseStringIndexReader.read(nameId, id -> {
/*     */             if (id.isNode()) {
/*     */               result.add(Pair.create(null, getNode(id.getId())));
/*     */             } else {
/*     */               V8HeapEdge heapEdge = getEdge(id.getId());
/*     */               
/*     */               if (heapEdge != null) {
/*     */                 V8HeapEntry node = getNode(heapEdge.getToIndex());
/*     */                 
/*     */                 result.add(Pair.create(heapEdge, node));
/*     */               } 
/*     */             } 
/*     */             return true;
/*     */           });
/* 368 */     } catch (IOException e) {
/* 369 */       reportError(e.getMessage());
/* 370 */       return Collections.emptyList();
/*     */     } 
/* 372 */     return result;
/*     */   }
/*     */   
/*     */   public int getChildIndex(@NotNull V8HeapEntry parent, long childOffset) {
/* 376 */     if (parent == null) $$$reportNull$$$0(24);  synchronized (this.myLock) {
/* 377 */       List<V8HeapEdge> children = getChildren(parent);
/* 378 */       if (children != null)
/* 379 */         for (int i = 0; i < children.size(); i++) {
/* 380 */           V8HeapEdge edge = children.get(i);
/* 381 */           if (edge.getFileOffset() == childOffset) return i;
/*     */         
/*     */         }  
/* 384 */       return 0;
/*     */     } 
/*     */   }
/*     */   @Nls
/*     */   public String getString(long id) {
/* 389 */     if (id == -1L) return NodeJSBundle.message("profile.cpu.error.text", new Object[0]);
/* 390 */     synchronized (this.myLock) {
/* 391 */       String name = (String)this.myStringMap.get(Long.valueOf(id));
/* 392 */       if (name != null) return name; 
/*     */       try {
/* 394 */         String readString = this.myStringIndex.readString(id);
/* 395 */         this.myStringMap.put(Long.valueOf(id), readString);
/* 396 */         return readString;
/*     */       }
/* 398 */       catch (IOException e) {
/* 399 */         reportError(e.getMessage());
/* 400 */         return NodeJSBundle.message("profile.cpu.error.text", new Object[0]);
/*     */       } 
/*     */     } 
/*     */   }
/*     */   
/*     */   public int getNodeParent(int nodeIdx) {
/* 406 */     return this.myInMemoryIndexes.getParents().getInt(nodeIdx);
/*     */   }
/*     */   
/*     */   public long getRetainedSize(int nodeIdx) {
/* 410 */     return this.myInMemoryIndexes.getRetainedSizes().getLong(nodeIdx);
/*     */   }
/*     */   
/*     */   private void reportError(String message) {
/* 414 */     if (this.myErrorNotificator != null) {
/* 415 */       this.myErrorNotificator.consume(message);
/*     */     }
/*     */   }
/*     */   
/*     */   private static V8HeapEntry errorNode() {
/* 420 */     return V8HeapEntry.createFromJson(V8HeapNodeType.kObject.getNumber(), -1L, -1L, 0L, 0L, 0L, 0L);
/*     */   }
/*     */   
/*     */   @NotNull
/*     */   public V8StringIndex getStringIndex() {
/* 425 */     if (this.myStringIndex == null) $$$reportNull$$$0(25);  return this.myStringIndex;
/*     */   }
/*     */   
/*     */   public List<Integer> getBiggestObjects() {
/* 429 */     return this.myInMemoryIndexes.getBiggest();
/*     */   }
/*     */   
/*     */   public int getDistance(int nodexIdx) {
/* 433 */     return this.myInMemoryIndexes.getDistances().getInt(nodexIdx);
/*     */   }
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
/*     */   public void resortChildren() {
/* 450 */     this.myChildrenMap.clear();
/* 451 */     this.myParentsMap.clear();
/*     */   }
/*     */   
/*     */   public void setAggregatesLinksReaderFactory(LinksReaderFactory<Long> aggregatesLinksReaderFactory) throws FileNotFoundException {
/* 455 */     this.myAggregatesLinksReaderFactory = aggregatesLinksReaderFactory;
/* 456 */     this.myAggregatesLinksReader = this.myResourses.<LinksReader<Long>>register(this.myAggregatesLinksReaderFactory.create(false));
/*     */   }
/*     */   
/*     */   public TreeMap<Long, Aggregate> getAggregatesMap() {
/* 460 */     return this.myInMemoryIndexes.getAggregateMap();
/*     */   }
/*     */   
/*     */   public LinksReaderFactory<Long> getAggregatesLinksReaderFactory() {
/* 464 */     return this.myAggregatesLinksReaderFactory;
/*     */   }
/*     */   
/*     */   public List<Long> getAggregatesChildren(long aggregateId) {
/* 468 */     List<Long> ids = (List<Long>)this.myChildrenByAggregateMap.get(Long.valueOf(aggregateId));
/* 469 */     if (ids != null) return ids; 
/* 470 */     this.myChildrenByAggregateMap.put(Long.valueOf(aggregateId), ids = new ArrayList<>());
/*     */     try {
/* 472 */       List<Long> finalIds = ids;
/* 473 */       this.myAggregatesLinksReader.read(aggregateId, aLong -> {
/*     */             finalIds.add(aLong);
/*     */             return true;
/*     */           });
/* 477 */     } catch (IOException e) {
/* 478 */       reportError(e.getMessage());
/* 479 */       return Collections.emptyList();
/*     */     } 
/* 481 */     ids.sort(this.myByRetained);
/*     */     
/* 483 */     return ids;
/*     */   }
/*     */   
/*     */   @Nullable
/*     */   public TreePath translateIntoPathFromNodesChain(Object[] path) {
/* 488 */     V8HeapEntry current = this.myRootNode;
/* 489 */     Object[] resPath = new Object[path.length];
/* 490 */     resPath[0] = new V8HeapContainmentTreeTableModel.NamedEntry(current, "", "", -1L);
/* 491 */     for (int i = 1; i < path.length; i++) {
/* 492 */       V8HeapContainmentTreeTableModel.NamedEntry namedEntry = (V8HeapContainmentTreeTableModel.NamedEntry)path[i];
/* 493 */       if (!this.myShowHidden && V8HeapNodeType.kHidden.equals(namedEntry.getEntry().getType())) return null; 
/* 494 */       Pair<V8HeapEntry, V8HeapEdge> pair = getChildByLinkId(current, namedEntry.getLinkOffset() / 37L);
/* 495 */       if (((V8HeapEntry)pair.getFirst()).getSnapshotObjectId() == -1L) return null; 
/* 496 */       resPath[i] = new V8HeapContainmentTreeTableModel.NamedEntry((V8HeapEntry)pair.getFirst(), getString(((V8HeapEntry)pair.getFirst()).getNameId()), ((V8HeapEdge)pair
/* 497 */           .getSecond()).getPresentation(this), ((V8HeapEdge)pair.getSecond()).getFileOffset());
/* 498 */       current = (V8HeapEntry)pair.getFirst();
/*     */     } 
/* 500 */     return new TreePath(resPath);
/*     */   }
/*     */   
/*     */   public int getNodeCount() {
/* 504 */     return (int)this.myHeader.getNodesCnt();
/*     */   }
/*     */   
/*     */   public boolean isUnreachable(int id) {
/* 508 */     return this.myInMemoryIndexes.getUnreachable().contains(id);
/*     */   }
/*     */   public boolean isOnlyWeak(int id) {
/* 511 */     return this.myInMemoryIndexes.getOnlyWeak().contains(id);
/*     */   }
/*     */   
/*     */   public Flags getFlags() {
/* 515 */     return this.myInMemoryIndexes.getFlags();
/*     */   }
/*     */   
/*     */   public void resetDoNotShowHidden() {
/* 519 */     this.myShowHidden = false;
/* 520 */     this.myChildrenMap.clear();
/* 521 */     this.myParentsMap.clear();
/*     */   }
/*     */   
/*     */   public boolean isShowHidden() {
/* 525 */     return this.myShowHidden;
/*     */   }
/*     */   
/*     */   public boolean isDetached(int nodeIdx) {
/* 529 */     return this.myInMemoryIndexes.getFlags().isDetached(nodeIdx);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean isReachableFromWindow(V8HeapEntry entry) {
/* 541 */     return (V8HeapNodeType.kString.equals(entry.getType()) || (V8HeapNodeType.kObject
/* 542 */       .equals(entry.getType()) && this.myStringsHolder.isWindowString(entry.getNameId())) || this.myInMemoryIndexes
/* 543 */       .getFlags().isQueriable((int)entry.getId()));
/*     */   }
/*     */   
/*     */   @NotNull
/*     */   public V8ImportantStringsHolder getImportantStringsHolder() {
/* 548 */     if (this.myStringsHolder == null) $$$reportNull$$$0(26);  return this.myStringsHolder;
/*     */   }
/*     */   
/*     */   public V8HeapInMemoryIndexes getInMemoryIndexes() {
/* 552 */     return this.myInMemoryIndexes;
/*     */   }
/*     */   
/*     */   public void setInMemoryIndexes(V8HeapInMemoryIndexes inMemoryIndexes) {
/* 556 */     this.myInMemoryIndexes = inMemoryIndexes;
/* 557 */     Map<Long, Aggregate> aggregateMap = this.myInMemoryIndexes.getAggregateMap();
/* 558 */     this.myInMemoryIndexes.sortAggregates((o1, o2) -> {
/*     */           Aggregate second = (Aggregate)aggregateMap.get(o2);
/*     */           Aggregate first = (Aggregate)aggregateMap.get(o1);
/*     */           int bySizes = Long.compare(second.getRetained(), first.getRetained());
/*     */           return (bySizes != 0) ? bySizes : first.getPresentation(this).compareTo(second.getPresentation(this));
/*     */         });
/*     */   }
/*     */ }


/* Location:              C:\Program Files\JetBrains\WebStorm 2021.1\plugins\NodeJS\lib\NodeJS.jar!\com\jetbrains\nodejs\run\profile\heap\V8CachingReader.class
 * Java compiler version: 11 (55.0)
 * JD-Core Version:       1.1.3
 */