/*     */ package org.bipolar.run.profile.heap.calculation.diff;
/*     */ 
/*     */ import com.intellij.util.BeforeAfter;
/*     */ import com.intellij.util.containers.SLRUMap;
/*     */ import org.bipolar.run.profile.heap.CompositeCloseable;
/*     */ import org.bipolar.run.profile.heap.V8CachingReader;
/*     */ import org.bipolar.run.profile.heap.view.models.V8HeapContainmentTreeTableModel;
/*     */ import gnu.trove.TLongArrayList;
/*     */ import java.io.IOException;
/*     */ import java.util.ArrayList;
/*     */ import java.util.Collections;
/*     */ import java.util.Comparator;
/*     */ import java.util.Iterator;
/*     */ import java.util.List;
/*     */ import org.jetbrains.annotations.NotNull;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class V8DiffCachingReader
/*     */ {
/*     */   private final CompositeCloseable myResourses;
/*     */   private final V8CachingReader myBaseReader;
/*     */   private final V8CachingReader myChangedReader;
/*     */   private final TLongArrayList myBaseSnapshotIds;
/*     */   private final TLongArrayList myChangedSnapshotIds;
/*     */   private final AggregatesViewDiff myAggregatesViewDiff;
/*     */   private final SLRUMap<String, List<BeforeAfter<V8HeapContainmentTreeTableModel.NamedEntry>>> myMergedAggregatesMap;
/*     */   private final Object myLock;
/*     */   private final TLongArrayList myBaseSizes;
/*     */   private final TLongArrayList myChangedSizes;
/*     */   private final List<BeforeAfter<V8HeapContainmentTreeTableModel.NamedEntry>> myBiggestObjectsDiff;
/*     */   private static final int ourMaxBiggestForDiff = 100;
/*     */   
/*     */   public V8DiffCachingReader(V8CachingReader baseReader, V8CachingReader changedReader, TLongArrayList baseSnapshotIds, TLongArrayList changedSnapshotIds, TLongArrayList baseSizes, TLongArrayList changedSizes, AggregatesViewDiff aggregatesViewDiff, List<BeforeAfter<V8HeapContainmentTreeTableModel.NamedEntry>> biggestObjectsDiff) {
/*  38 */     this.myBaseSizes = baseSizes;
/*  39 */     this.myChangedSizes = changedSizes;
/*  40 */     this.myBiggestObjectsDiff = biggestObjectsDiff;
/*  41 */     this.myResourses = new CompositeCloseable();
/*  42 */     this.myBaseReader = baseReader;
/*  43 */     this.myChangedReader = changedReader;
/*  44 */     this.myBaseSnapshotIds = baseSnapshotIds;
/*  45 */     this.myChangedSnapshotIds = changedSnapshotIds;
/*  46 */     this.myAggregatesViewDiff = aggregatesViewDiff;
/*  47 */     this.myMergedAggregatesMap = new SLRUMap(200, 500);
/*  48 */     this.myLock = new Object();
/*  49 */     prepare();
/*     */   }
/*     */   
/*     */   public List<BeforeAfter<V8HeapContainmentTreeTableModel.NamedEntry>> getBiggestObjectsDiff() {
/*  53 */     return this.myBiggestObjectsDiff;
/*     */   }
/*     */   
/*     */   public CompositeCloseable getResourses() {
/*  57 */     return this.myResourses;
/*     */   }
/*     */   
/*     */   public AggregatesViewDiff getAggregatesViewDiff() {
/*  61 */     return this.myAggregatesViewDiff;
/*     */   }
/*     */   
/*     */   public V8CachingReader getBaseReader() {
/*  65 */     return this.myBaseReader;
/*     */   }
/*     */   
/*     */   public V8CachingReader getChangedReader() {
/*  69 */     return this.myChangedReader;
/*     */   }
/*     */   
/*     */   public void prepare() {
/*  73 */     List<AggregatesViewDiff.AggregateDifference> list = this.myAggregatesViewDiff.getList();
/*  74 */     Iterator<AggregatesViewDiff.AggregateDifference> iterator = list.iterator();
/*  75 */     while (iterator.hasNext()) {
/*  76 */       AggregatesViewDiff.AggregateDifference difference = iterator.next();
/*  77 */       if (getChildren(difference).isEmpty()) iterator.remove();
/*     */     
/*     */     } 
/*     */   }
/*     */   
/*     */   public List<BeforeAfter<V8HeapContainmentTreeTableModel.NamedEntry>> getChildren(AggregatesViewDiff.AggregateDifference difference) {
/*  83 */     synchronized (this.myLock) {
/*  84 */       List<BeforeAfter<V8HeapContainmentTreeTableModel.NamedEntry>> list = (List<BeforeAfter<V8HeapContainmentTreeTableModel.NamedEntry>>)this.myMergedAggregatesMap.get(difference.getClassIdx());
/*  85 */       if (list != null) return list;
/*     */       
/*  87 */       this.myMergedAggregatesMap.put(difference.getClassIdx(), list = new ArrayList<>());
/*     */       
/*  89 */       if (difference.getBase() == null) {
/*  90 */         int id = difference.getChanged().getId();
/*  91 */         List<Long> children = getSortedOneSideChildren(id, this.myChangedReader);
/*  92 */         for (Long child : children) {
/*  93 */           list.add(new BeforeAfter(null, V8HeapContainmentTreeTableModel.NamedEntry.createWithoutLink(child
/*  94 */                   .longValue(), this.myChangedReader)));
/*     */         }
/*  96 */         return list;
/*     */       } 
/*  98 */       if (difference.getChanged() == null) {
/*  99 */         int id = difference.getBase().getId();
/* 100 */         List<Long> children = getSortedOneSideChildren(id, this.myBaseReader);
/* 101 */         for (Long child : children) {
/* 102 */           list.add(new BeforeAfter(V8HeapContainmentTreeTableModel.NamedEntry.createWithoutLink(child.longValue(), this.myBaseReader), null));
/*     */         }
/* 104 */         return list;
/*     */       } 
/*     */       
/* 107 */       if (!merge(difference, list)) {
/*     */         
/* 109 */         this.myMergedAggregatesMap.remove(difference.getClassIdx());
/* 110 */         return Collections.emptyList();
/*     */       } 
/* 112 */       return list;
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   private boolean merge(AggregatesViewDiff.AggregateDifference difference, List<BeforeAfter<V8HeapContainmentTreeTableModel.NamedEntry>> list) {
/* 118 */     List<Long> changedChildren = this.myChangedReader.getAggregatesChildren(difference.getChanged().getId());
/* 119 */     List<Long> baseChildren = this.myBaseReader.getAggregatesChildren(difference.getBase().getId());
/*     */ 
/*     */     
/* 122 */     SnapshotObjectsComparator comparator = new SnapshotObjectsComparator(this.myBaseReader, this.myChangedReader, this.myBaseSnapshotIds, this.myChangedSnapshotIds, baseChildren, difference.isSystem());
/* 123 */     calculateMovement(comparator, baseChildren, changedChildren, difference);
/*     */     
/* 125 */     changedChildren.sort(createByRetainedSizesComparator(this.myChangedReader));
/* 126 */     baseChildren.sort(createByRetainedSizesComparator(this.myBaseReader));
/*     */     
/* 128 */     for (Long child : changedChildren) {
/* 129 */       Long idInBase = comparator.getBaseForChanged(child);
/* 130 */       if (idInBase != null) {
/* 131 */         boolean retainedSizesDiffer = (this.myChangedReader.getRetainedSize(child.intValue()) != this.myBaseReader.getRetainedSize(idInBase.intValue()));
/* 132 */         boolean sizesDiffer = (this.myChangedSizes.get(child.intValue()) != this.myBaseSizes.get(idInBase.intValue()));
/* 133 */         if (!sizesDiffer && !retainedSizesDiffer)
/*     */           continue; 
/*     */       } 
/* 136 */       V8HeapContainmentTreeTableModel.NamedEntry changedEntry = V8HeapContainmentTreeTableModel.NamedEntry.createWithoutLink(child.longValue(), this.myChangedReader);
/*     */       
/* 138 */       V8HeapContainmentTreeTableModel.NamedEntry baseEntry = (idInBase == null) ? null : V8HeapContainmentTreeTableModel.NamedEntry.createWithoutLink(idInBase.longValue(), this.myBaseReader);
/* 139 */       list.add(new BeforeAfter(baseEntry, changedEntry));
/* 140 */       if (list.size() >= 100)
/*     */         break; 
/*     */     } 
/* 143 */     for (Long child : baseChildren) {
/* 144 */       if (list.size() >= 100)
/* 145 */         break;  if (!comparator.haveEquivalent(child)) {
/*     */         
/* 147 */         V8HeapContainmentTreeTableModel.NamedEntry entry = V8HeapContainmentTreeTableModel.NamedEntry.createWithoutLink(child.longValue(), this.myBaseReader);
/* 148 */         list.add(new BeforeAfter(entry, null));
/*     */       } 
/*     */     } 
/* 151 */     return !list.isEmpty();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private void calculateMovement(SnapshotObjectsComparator comparator, List<Long> baseChildren, List<Long> changedChildren, AggregatesViewDiff.AggregateDifference difference) {
/* 158 */     int addedCnt = 0;
/* 159 */     int removedCnt = 0;
/* 160 */     long addedSize = 0L;
/* 161 */     long removedSize = 0L;
/*     */     
/* 163 */     for (Long changedId : changedChildren) {
/* 164 */       Long baseId = comparator.getBaseForChanged(changedId);
/* 165 */       long changedSize = this.myChangedSizes.get(changedId.intValue());
/* 166 */       if (baseId == null) {
/* 167 */         addedCnt++;
/* 168 */         addedSize += changedSize;
/*     */         continue;
/*     */       } 
/* 171 */       long sizeDiff = changedSize - this.myBaseSizes.get(baseId.intValue());
/* 172 */       if (sizeDiff > 0L) {
/* 173 */         addedSize += sizeDiff; continue;
/*     */       } 
/* 175 */       removedSize += sizeDiff;
/*     */     } 
/*     */ 
/*     */     
/* 179 */     for (Long baseId : baseChildren) {
/* 180 */       if (comparator.haveEquivalent(baseId))
/* 181 */         continue;  removedCnt++;
/* 182 */       removedSize += this.myBaseSizes.get(baseId.intValue());
/*     */     } 
/*     */     
/* 185 */     difference.setAddedCnt(addedCnt);
/* 186 */     difference.setAddedSize(addedSize);
/* 187 */     difference.setRemovedCnt(removedCnt);
/* 188 */     difference.setRemovedSize(removedSize);
/*     */   }
/*     */   
/*     */   @NotNull
/*     */   private static List<Long> getSortedOneSideChildren(int id, V8CachingReader reader) {
/* 193 */     List<Long> children = reader.getAggregatesChildren(id);
/* 194 */     children.sort(createByRetainedSizesComparator(reader));
/* 195 */     if (children == null) $$$reportNull$$$0(0);  return children; } @NotNull
/*     */   public static Comparator<Long> createByRetainedSizesComparator(V8CachingReader reader) {
/*     */     if (((o1, o2) -> {
/*     */         int retainedCompare = Long.compare(reader.getRetainedSize(o2.intValue()), reader.getRetainedSize(o1.intValue()));
/*     */         return (retainedCompare == 0) ? o1.compareTo(o2) : retainedCompare;
/* 200 */       }) == null) $$$reportNull$$$0(1);  return (o1, o2) -> {
/*     */         int retainedCompare = Long.compare(reader.getRetainedSize(o2.intValue()), reader.getRetainedSize(o1.intValue()));
/*     */         return (retainedCompare == 0) ? o1.compareTo(o2) : retainedCompare;
/*     */       };
/*     */   }
/*     */ 
/*     */   
/*     */   public void close() throws IOException {
/* 208 */     this.myResourses.close();
/* 209 */     this.myBaseReader.close();
/* 210 */     this.myChangedReader.close();
/*     */   }
/*     */ }


/* Location:              C:\Program Files\JetBrains\WebStorm 2021.1\plugins\NodeJS\lib\NodeJS.jar!\com\jetbrains\nodejs\run\profile\heap\calculation\diff\V8DiffCachingReader.class
 * Java compiler version: 11 (55.0)
 * JD-Core Version:       1.1.3
 */