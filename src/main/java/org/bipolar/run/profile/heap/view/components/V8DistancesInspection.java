/*     */ package org.bipolar.run.profile.heap.view.components;
/*     */ import com.intellij.openapi.progress.ProgressIndicator;
/*     */ import com.intellij.openapi.util.Pair;
/*     */ import com.intellij.util.Processor;
/*     */ import org.bipolar.NodeJSBundle;
/*     */ import org.bipolar.run.profile.heap.V8CachingReader;
/*     */ import org.bipolar.run.profile.heap.data.LinkedByNameId;
/*     */ import org.bipolar.run.profile.heap.data.V8HeapEdge;
/*     */ import org.bipolar.run.profile.heap.data.V8HeapEntry;
/*     */ import org.bipolar.run.profile.heap.data.V8HeapGraphEdgeType;
/*     */ import org.bipolar.run.profile.heap.data.V8HeapNodeType;
/*     */ import org.bipolar.run.profile.heap.io.SequentialRawReader;
/*     */
/*     */ import java.io.IOException;
/*     */ import java.util.ArrayList;
/*     */ import java.util.HashMap;
/*     */ import java.util.HashSet;
/*     */ import java.util.Iterator;
/*     */ import java.util.List;
/*     */ import java.util.Map;
/*     */ import java.util.Set;
/*     */ import java.util.TreeMap;
/*     */
import org.jetbrains.annotations.NotNull;
/*     */ 
/*     */ public class V8DistancesInspection extends Task.Backgroundable {
/*  26 */   private static final String[] FILTER_NAMES = new String[] { "__proto__", "prototype", "constructor", "v8", "source", "exports", "super_", "type" }; @NotNull
/*     */   private final V8CachingReader myReader;
/*     */   private static final int MIN_DISTANCE_VARIANCE = 5;
/*     */   private static final int MAX_DISTANCE_VARIANCE = 50;
/*     */   private final Map<Long, TypeData> myByTypesData;
/*     */   private TreeMap<Long, TypeData> mySortedByTypes;
/*     */   private IOException myException;
/*     */   private final HashMap<Long, Set<Long>> myByNamesMap;
/*     */   private ArrayList<List<Long>> mySortedByNames;
/*     */   
/*     */   public V8DistancesInspection(@Nullable Project project, @NotNull V8CachingReader reader) {
/*  37 */     super(project, NodeJSBundle.message("progress.title.checking.snapshot.distances", new Object[0]), false);
/*  38 */     this.myReader = reader;
/*  39 */     this.myByTypesData = new HashMap<>();
/*  40 */     this.myByNamesMap = new HashMap<>();
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public void run(@NotNull ProgressIndicator indicator) {
/*  46 */     if (indicator == null) $$$reportNull$$$0(1);  try { doByTypes(indicator);
/*  47 */       doByNames(indicator); }
/*     */     
/*  49 */     catch (IOException e)
/*  50 */     { this.myException = e; }
/*     */   
/*     */   }
/*     */   
/*     */   private void doByNames(ProgressIndicator indicator) throws IOException {
/*  55 */     indicator.setText(NodeJSBundle.message("progress.text.looking.for.filtered.names.ids", new Object[0]));
/*  56 */     Set<Long> filterIds = fillFilteredStrings();
/*     */     
/*  58 */     indicator.setText(NodeJSBundle.message("progress.text.iterating.reverse.string.index", new Object[0]));
/*  59 */     this.myReader.getStringReverseIndexReaderFactory().create(true).iterate(ids -> {
/*     */           if (ids.size() >= 5) {
/*     */             Set<Long> links = new HashSet<>();
/*     */             for (LinkedByNameId id : ids) {
/*     */               if (!id.isNode()) {
/*     */                 links.add(Long.valueOf(id.getId()));
/*     */               }
/*     */             } 
/*     */             if (links.size() >= 5) {
/*     */               V8HeapEdge edge = this.myReader.getEdge(((Long)links.iterator().next()).longValue());
/*     */               if (!filterIds.contains(Long.valueOf(edge.getNameId())))
/*     */                 this.myByNamesMap.put(Long.valueOf(edge.getNameId()), links); 
/*     */             } 
/*     */           } 
/*     */           return true;
/*     */         });
/*  75 */     indicator.setText(NodeJSBundle.message("progress.text.filtering.by.name.groups.removing.hidden.small", new Object[0]));
/*  76 */     Iterator<Map.Entry<Long, Set<Long>>> iterator = this.myByNamesMap.entrySet().iterator();
/*  77 */     while (iterator.hasNext()) {
/*  78 */       Map.Entry<Long, Set<Long>> entry = iterator.next();
/*  79 */       Set<Integer> set = new HashSet<>();
/*  80 */       Iterator<Long> edgeIterator = ((Set<Long>)entry.getValue()).iterator();
/*  81 */       boolean skipFinishChecks = false;
/*  82 */       while (edgeIterator.hasNext()) {
/*  83 */         Long edgeId = edgeIterator.next();
/*  84 */         V8HeapEdge edge = this.myReader.getEdge(edgeId.longValue());
/*  85 */         if (V8HeapGraphEdgeType.kElement.equals(edge.getType())) {
/*  86 */           iterator.remove();
/*  87 */           skipFinishChecks = true; break;
/*     */         } 
/*  89 */         if (V8HeapGraphEdgeType.isInternalKind(edge.getType()) || nodeIsHidden(this.myReader.getNode(edge.getToIndex()))) {
/*  90 */           edgeIterator.remove(); continue;
/*     */         } 
/*  92 */         set.add(Integer.valueOf(this.myReader.getDistance((int)edge.getToIndex())));
/*     */       } 
/*     */       
/*  95 */       if (!skipFinishChecks && ((
/*  96 */         (Set)entry.getValue()).isEmpty() || set.size() < 5)) iterator.remove(); 
/*     */     } 
/*  98 */     this.mySortedByNames = new ArrayList<>();
/*  99 */     for (Map.Entry<Long, Set<Long>> entry : this.myByNamesMap.entrySet()) {
/* 100 */       this.mySortedByNames.add(new ArrayList<>(entry.getValue()));
/*     */     }
/* 102 */     this.mySortedByNames.sort((o1, o2) -> Integer.compare(o2.size(), o1.size()));
/* 103 */     this.myByNamesMap.clear();
/*     */   }
/*     */   
/*     */   @NotNull
/*     */   private Set<Long> fillFilteredStrings() throws IOException {
/* 108 */     final Set<String> filterStrings = ContainerUtil.set((Object[])FILTER_NAMES);
/* 109 */     final Set<Long> filterIds = new HashSet<>();
/* 110 */     this.myReader.getStringIndex().iterate(new CloseableProcessor<Pair<Long, String>, IOException>()
/*     */         {
/*     */           public void exceptionThrown(@NotNull IOException e) {
/* 113 */             if (e == null) $$$reportNull$$$0(0);  V8DistancesInspection.this.myException = e;
/*     */           }
/*     */ 
/*     */ 
/*     */           
/*     */           public void close() {}
/*     */ 
/*     */           
/*     */           public boolean process(Pair<Long, String> pair) {
/* 122 */             if (filterStrings.remove(pair.getSecond())) {
/* 123 */               filterIds.add((Long)pair.getFirst());
/*     */             }
/* 125 */             return !filterStrings.isEmpty();
/*     */           }
/*     */         });
/* 128 */     if (filterIds == null) $$$reportNull$$$0(2);  return filterIds;
/*     */   }
/*     */   
/*     */   private static boolean nodeIsHidden(V8HeapEntry entry) {
/* 132 */     return (V8HeapNodeType.kHidden.equals(entry.getType()) || V8HeapNodeType.kSynthetic.equals(entry.getType()));
/*     */   }
/*     */   
/*     */   public ArrayList<List<Long>> getByNamesList() {
/* 136 */     return this.mySortedByNames;
/*     */   }
/*     */   
/*     */   private void doByTypes(ProgressIndicator indicator) throws IOException {
/* 140 */     indicator.setText(
/* 141 */         NodeJSBundle.message("progress.text.marking.nodes.referenced.by.hidden.links.only", new Object[0]));
/* 142 */     final IntOpenHashSet onlyHiddenLinks = new IntOpenHashSet();
/* 143 */     LinksReader<V8HeapEdge> linksReader = this.myReader.getReverseLinkIndexReaderFactory().create(true);
/* 144 */     linksReader.iterate(new Processor<List<V8HeapEdge>>() {
/* 145 */           int idx = 0;
/*     */ 
/*     */           
/*     */           public boolean process(List<V8HeapEdge> edges) {
/* 149 */             boolean notHidden = false;
/* 150 */             for (V8HeapEdge edge : edges) {
/* 151 */               if (!V8HeapGraphEdgeType.isInternalKind(edge.getType())) {
/* 152 */                 notHidden = true;
/*     */                 break;
/*     */               } 
/*     */             } 
/* 156 */             if (!notHidden) {
/* 157 */               onlyHiddenLinks.add(this.idx);
/*     */             }
/* 159 */             this.idx++;
/* 160 */             return true;
/*     */           }
/*     */         });
/*     */     
/* 164 */     indicator.setText(NodeJSBundle.message("progress.text.grouping.nodes.by.classes", new Object[0]));
/*     */ 
/*     */     
/* 167 */     SequentialRawReader<V8HeapEntry> reader = new SequentialRawReader(this.myReader.getNodeIndexFile(), (RawSerializer)V8HeapEntry.MyRawSerializer.getInstance(), this.myReader.getNodeCount());
/* 168 */     reader.iterate(new CloseableThrowableProcessor<V8HeapEntry, IOException>()
/*     */         {
/*     */           public boolean process(V8HeapEntry entry) {
/* 171 */             if (onlyHiddenLinks.contains((int)entry.getId()) || V8HeapNodeType.kHidden
/* 172 */               .equals(entry.getType()) || V8HeapNodeType.kSynthetic
/* 173 */               .equals(entry.getType()) || V8HeapNodeType.kCode
/* 174 */               .equals(entry.getType())) {
/* 175 */               return true;
/*     */             }
/*     */             
/* 178 */             long classIndex = entry.getClassIndex();
/* 179 */             V8DistancesInspection.TypeData typeData = V8DistancesInspection.this.myByTypesData.get(Long.valueOf(classIndex));
/* 180 */             if (typeData == null) {
/* 181 */               V8DistancesInspection.this.myByTypesData.put(Long.valueOf(classIndex), typeData = new V8DistancesInspection.TypeData());
/*     */             }
/* 183 */             typeData.entry(entry);
/* 184 */             return true;
/*     */           }
/*     */ 
/*     */ 
/*     */ 
/*     */           
/*     */           public void close() {}
/*     */         });
/* 192 */     indicator.setText(NodeJSBundle.message("progress.text.filtering.by.class.groups", new Object[0]));
/* 193 */     Iterator<TypeData> iterator = this.myByTypesData.values().iterator();
/* 194 */     while (iterator.hasNext()) {
/* 195 */       TypeData typeData = iterator.next();
/* 196 */       if (typeData.getMap().size() < 5) iterator.remove();
/*     */     
/*     */     } 
/* 199 */     this.mySortedByTypes = new TreeMap<>((key1, key2) -> this.myByTypesData.isEmpty() ? Long.compare(((TypeData)this.mySortedByTypes.get(key2)).getMaxRetainedSize(), ((TypeData)this.mySortedByTypes.get(key1)).getMaxRetainedSize()) : Long.compare(((TypeData)this.myByTypesData.get(key2)).getMaxRetainedSize(), ((TypeData)this.myByTypesData.get(key1)).getMaxRetainedSize()));
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/* 205 */     this.mySortedByTypes.putAll(this.myByTypesData);
/* 206 */     this.myByTypesData.clear();
/*     */   }
/*     */   
/*     */   public TreeMap<Long, TypeData> getSortedByTypes() {
/* 210 */     return this.mySortedByTypes;
/*     */   }
/*     */   
/*     */   public IOException getException() {
/* 214 */     return this.myException;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   class TypeData
/*     */   {
/* 223 */     private long myMaxRetainedSize = 0L;
/* 224 */     private final TreeMap<Integer, Pair<V8HeapEntry, V8HeapEdge>> myMap = new TreeMap<>();
/*     */     private boolean mySomethingMissing;
/*     */     
/*     */     private void putEntry(@NotNull V8HeapEntry entry, int distance) {
/* 228 */       if (entry == null) $$$reportNull$$$0(0);  long parentId = V8DistancesInspection.this.myReader.getNodeParent((int)entry.getId());
/* 229 */       V8HeapEntry parent = V8DistancesInspection.this.myReader.getNode(parentId);
/* 230 */       Pair<V8HeapEntry, V8HeapEdge> p = V8DistancesInspection.this.myReader.getChildById(parent, entry.getId());
/* 231 */       if (parent.getSnapshotObjectId() == -1L || ((V8HeapEntry)p.getFirst()).getSnapshotObjectId() == -1L) {
/* 232 */         this.myMap.put(Integer.valueOf(distance), Pair.create(entry, null));
/*     */       } else {
/* 234 */         this.myMap.put(Integer.valueOf(distance), p);
/*     */       } 
/*     */     }
/*     */     
/*     */     private long add(@NotNull V8HeapEntry entry, int distance) {
/* 239 */       if (entry == null) $$$reportNull$$$0(1);  long retainedSize = V8DistancesInspection.this.myReader.getRetainedSize((int)entry.getId());
/*     */       
/* 241 */       Pair<V8HeapEntry, V8HeapEdge> before = this.myMap.get(Integer.valueOf(distance));
/* 242 */       if (before != null) {
/* 243 */         if (V8DistancesInspection.this.myReader.getRetainedSize((int)((V8HeapEntry)before.getFirst()).getId()) < retainedSize) {
/* 244 */           putEntry(entry, distance);
/*     */         }
/*     */       } else {
/* 247 */         putEntry(entry, distance);
/*     */       } 
/* 249 */       this.myMaxRetainedSize = Math.max(this.myMaxRetainedSize, retainedSize);
/* 250 */       return retainedSize;
/*     */     }
/*     */     
/*     */     public void entry(@NotNull V8HeapEntry entry) {
/* 254 */       if (entry == null) $$$reportNull$$$0(2);  int distance = V8DistancesInspection.this.myReader.getDistance((int)entry.getId());
/* 255 */       if (distance < 0 || distance >= 100000000)
/* 256 */         return;  add(entry, distance);
/*     */       
/* 258 */       if (this.myMap.size() > 50) {
/* 259 */         Integer key = null;
/* 260 */         long size = Long.MAX_VALUE;
/* 261 */         for (Map.Entry<Integer, Pair<V8HeapEntry, V8HeapEdge>> entryEntry : this.myMap.entrySet()) {
/* 262 */           long currentSize = V8DistancesInspection.this.myReader.getRetainedSize((int)((V8HeapEntry)((Pair)entryEntry.getValue()).getFirst()).getId());
/* 263 */           if (currentSize <= size) {
/* 264 */             key = entryEntry.getKey();
/* 265 */             size = currentSize;
/*     */           } 
/*     */         } 
/* 268 */         if (key != null) {
/* 269 */           this.myMap.remove(key);
/* 270 */           this.mySomethingMissing = true;
/*     */         } 
/*     */       } 
/*     */     }
/*     */     
/*     */     public boolean isSomethingMissing() {
/* 276 */       return this.mySomethingMissing;
/*     */     }
/*     */     
/*     */     public long getMaxRetainedSize() {
/* 280 */       return this.myMaxRetainedSize;
/*     */     }
/*     */     
/*     */     public TreeMap<Integer, Pair<V8HeapEntry, V8HeapEdge>> getMap() {
/* 284 */       return this.myMap;
/*     */     }
/*     */   }
/*     */ }


/* Location:              C:\Program Files\JetBrains\WebStorm 2021.1\plugins\NodeJS\lib\NodeJS.jar!\com\jetbrains\nodejs\run\profile\heap\view\components\V8DistancesInspection.class
 * Java compiler version: 11 (55.0)
 * JD-Core Version:       1.1.3
 */