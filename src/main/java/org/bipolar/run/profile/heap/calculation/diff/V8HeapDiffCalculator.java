/*     */ package org.bipolar.run.profile.heap.calculation.diff;
/*     */ 
/*     */ import com.intellij.openapi.util.Pair;
/*     */ import com.intellij.util.BeforeAfter;
/*     */ import org.bipolar.run.profile.heap.TempFiles;
/*     */ import org.bipolar.run.profile.heap.V8CachingReader;
/*     */ import org.bipolar.run.profile.heap.data.Aggregate;
/*     */ import org.bipolar.run.profile.heap.data.V8HeapEntry;
/*     */ import org.bipolar.run.profile.heap.io.RawSerializer;
/*     */ import org.bipolar.run.profile.heap.io.SequentialRawReader;
/*     */ import org.bipolar.run.profile.heap.view.models.V8HeapContainmentTreeTableModel;
/*     */ import org.bipolar.util.CloseableThrowableConsumer;
/*     */ import gnu.trove.TLongArrayList;
/*     */ import java.io.IOException;
/*     */ import java.util.ArrayList;
/*     */ import java.util.List;
/*     */ import java.util.TreeMap;
/*     */
import org.jetbrains.annotations.NotNull;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class V8HeapDiffCalculator
/*     */ {
/*     */   @NotNull
/*     */   private final V8CachingReader myBaseReader;
/*     */   @NotNull
/*     */   private final V8CachingReader myChangedReader;
/*     */   private final TempFiles myTempFiles;
/*     */   private AggregatesViewDiff myAggregatesViewDiff;
/*     */   private TLongArrayList myBaseSnapshotIds;
/*     */   private TLongArrayList myChangedSnapshotIds;
/*     */   private TLongArrayList myBaseSnapshotSizes;
/*     */   private TLongArrayList myChangedSnapshotSizes;
/*     */   private final List<BeforeAfter<V8HeapContainmentTreeTableModel.NamedEntry>> myBiggestObjectsDiff;
/*     */   
/*     */   public V8HeapDiffCalculator(@NotNull V8CachingReader baseReader, @NotNull V8CachingReader changedReader, TempFiles tempFiles) {
/*  38 */     this.myBaseReader = baseReader;
/*  39 */     this.myChangedReader = changedReader;
/*  40 */     this.myTempFiles = tempFiles;
/*  41 */     this.myBiggestObjectsDiff = new ArrayList<>();
/*     */   }
/*     */   
/*     */   public void execute() throws IOException {
/*  45 */     this.myAggregatesViewDiff = calculate();
/*  46 */     this.myBaseSnapshotIds = new TLongArrayList(this.myBaseReader.getNodeCount());
/*  47 */     this.myBaseSnapshotSizes = new TLongArrayList(this.myBaseReader.getNodeCount());
/*  48 */     this.myChangedSnapshotIds = new TLongArrayList(this.myChangedReader.getNodeCount());
/*  49 */     this.myChangedSnapshotSizes = new TLongArrayList(this.myChangedReader.getNodeCount());
/*     */     
/*  51 */     calculateSelfIndexes(this.myBaseReader, this.myBaseSnapshotIds, this.myBaseSnapshotSizes);
/*  52 */     calculateSelfIndexes(this.myChangedReader, this.myChangedSnapshotIds, this.myChangedSnapshotSizes);
/*     */     
/*  54 */     calculateBiggestObjectsDiff();
/*     */   }
/*     */   
/*     */   private void calculateBiggestObjectsDiff() {
/*  58 */     List<Integer> list = this.myBaseReader.getBiggestObjects();
/*  59 */     List<Long> biggestObjectIds = new ArrayList<>(list.size());
/*  60 */     for (Integer integer : list) {
/*  61 */       biggestObjectIds.add(Long.valueOf(integer.longValue()));
/*     */     }
/*  63 */     SnapshotObjectsComparator comparator = new SnapshotObjectsComparator(this.myBaseReader, this.myChangedReader, this.myBaseSnapshotIds, this.myChangedSnapshotIds, biggestObjectIds, false);
/*     */     
/*  65 */     List<Integer> changedList = this.myChangedReader.getBiggestObjects();
/*  66 */     List<Long> changedBiggestObjectIds = new ArrayList<>(changedList.size());
/*  67 */     for (Integer integer : changedList) {
/*  68 */       changedBiggestObjectIds.add(Long.valueOf(integer.longValue()));
/*     */     }
/*  70 */     changedBiggestObjectIds.sort(V8DiffCachingReader.createByRetainedSizesComparator(this.myChangedReader));
/*  71 */     biggestObjectIds.sort(V8DiffCachingReader.createByRetainedSizesComparator(this.myBaseReader));
/*  72 */     for (Long id : changedBiggestObjectIds) {
/*  73 */       Long idInBase = comparator.getBaseForChanged(id);
/*     */       
/*  75 */       V8HeapContainmentTreeTableModel.NamedEntry changedEntry = V8HeapContainmentTreeTableModel.NamedEntry.createWithoutLink(id.longValue(), this.myChangedReader);
/*     */       
/*  77 */       V8HeapContainmentTreeTableModel.NamedEntry baseEntry = (idInBase == null) ? null : V8HeapContainmentTreeTableModel.NamedEntry.createWithoutLink(idInBase.longValue(), this.myBaseReader);
/*  78 */       this.myBiggestObjectsDiff.add(new BeforeAfter(baseEntry, changedEntry));
/*     */     } 
/*  80 */     for (Long id : biggestObjectIds) {
/*  81 */       if (!comparator.haveEquivalent(id)) {
/*     */         
/*  83 */         V8HeapContainmentTreeTableModel.NamedEntry entry = V8HeapContainmentTreeTableModel.NamedEntry.createWithoutLink(id.longValue(), this.myBaseReader);
/*  84 */         this.myBiggestObjectsDiff.add(new BeforeAfter(entry, null));
/*     */       } 
/*     */     } 
/*     */   }
/*     */   
/*     */   public List<BeforeAfter<V8HeapContainmentTreeTableModel.NamedEntry>> getBiggestObjectsDiff() {
/*  90 */     return this.myBiggestObjectsDiff;
/*     */   }
/*     */   
/*     */   public AggregatesViewDiff getAggregatesViewDiff() {
/*  94 */     return this.myAggregatesViewDiff;
/*     */   }
/*     */   
/*     */   public TLongArrayList getBaseSnapshotIds() {
/*  98 */     return this.myBaseSnapshotIds;
/*     */   }
/*     */   
/*     */   public TLongArrayList getChangedSnapshotIds() {
/* 102 */     return this.myChangedSnapshotIds;
/*     */   }
/*     */   
/*     */   public TLongArrayList getBaseSnapshotSizes() {
/* 106 */     return this.myBaseSnapshotSizes;
/*     */   }
/*     */   
/*     */   public TLongArrayList getChangedSnapshotSizes() {
/* 110 */     return this.myChangedSnapshotSizes;
/*     */   }
/*     */ 
/*     */   
/*     */   private static void calculateSelfIndexes(V8CachingReader reader, final TLongArrayList snapshotIds, final TLongArrayList sizes) throws IOException {
/* 115 */     for (int i = 0; i < reader.getNodeCount(); i++) {
/* 116 */       snapshotIds.add(0L);
/* 117 */       sizes.add(0L);
/*     */     } 
/*     */ 
/*     */     
/* 121 */     SequentialRawReader<V8HeapEntry> indexReader = new SequentialRawReader(reader.getNodeIndexFile(), (RawSerializer)V8HeapEntry.MyRawSerializer.getInstance(), reader.getNodeCount());
/* 122 */     indexReader.iterate(new CloseableThrowableConsumer<V8HeapEntry, IOException>()
/*     */         {
/*     */           public void close() throws IOException {}
/*     */ 
/*     */ 
/*     */           
/*     */           public void consume(V8HeapEntry entry) throws IOException {
/* 129 */             snapshotIds.set((int)entry.getId(), entry.getSnapshotObjectId());
/* 130 */             sizes.set((int)entry.getId(), entry.getSize());
/*     */           }
/*     */         });
/*     */   }
/*     */   
/*     */   private AggregatesViewDiff calculate() {
/* 136 */     List<Pair<Aggregate, String>> baseList = processAggregates(this.myBaseReader);
/* 137 */     List<Pair<Aggregate, String>> changedList = processAggregates(this.myChangedReader);
/*     */     
/* 139 */     AggregatesViewDiff diff = new AggregatesViewDiff();
/* 140 */     int base = 0;
/* 141 */     int changed = 0;
/* 142 */     while (base < baseList.size() && changed < changedList.size()) {
/* 143 */       Pair<Aggregate, String> baseAggregate = baseList.get(base);
/* 144 */       Pair<Aggregate, String> changedAggregate = changedList.get(changed);
/*     */       
/* 146 */       int compare = ((String)baseAggregate.getSecond()).compareTo((String)changedAggregate.getSecond());
/* 147 */       if (compare < 0) {
/* 148 */         diff.addAggregate(new AggregatesViewDiff.AggregateDifference((String)baseAggregate.getSecond(), ((Aggregate)baseAggregate.getFirst()).getType(), (Aggregate)baseAggregate
/* 149 */               .getFirst(), null));
/* 150 */         base++; continue;
/* 151 */       }  if (compare > 0) {
/* 152 */         diff.addAggregate(new AggregatesViewDiff.AggregateDifference((String)changedAggregate.getSecond(), ((Aggregate)changedAggregate.getFirst()).getType(), null, (Aggregate)changedAggregate
/* 153 */               .getFirst()));
/* 154 */         changed++;
/*     */         
/*     */         continue;
/*     */       } 
/* 158 */       AggregatesViewDiff.AggregateDifference difference = new AggregatesViewDiff.AggregateDifference((String)baseAggregate.getSecond(), ((Aggregate)baseAggregate.getFirst()).getType(), (Aggregate)baseAggregate.getFirst(), (Aggregate)changedAggregate.getFirst());
/* 159 */       diff.addAggregate(difference);
/* 160 */       base++;
/* 161 */       changed++;
/*     */     } 
/*     */     
/* 164 */     while (base < baseList.size()) {
/* 165 */       Pair<Aggregate, String> baseAggregate = baseList.get(base);
/* 166 */       diff.addAggregate(new AggregatesViewDiff.AggregateDifference((String)baseAggregate.getSecond(), ((Aggregate)baseAggregate.getFirst()).getType(), (Aggregate)baseAggregate
/* 167 */             .getFirst(), null));
/* 168 */       base++;
/*     */     } 
/* 170 */     while (changed < changedList.size()) {
/* 171 */       Pair<Aggregate, String> changedAggregate = changedList.get(changed);
/* 172 */       diff.addAggregate(new AggregatesViewDiff.AggregateDifference((String)changedAggregate.getSecond(), ((Aggregate)changedAggregate.getFirst()).getType(), null, (Aggregate)changedAggregate
/* 173 */             .getFirst()));
/* 174 */       changed++;
/*     */     } 
/*     */     
/* 177 */     return diff;
/*     */   }
/*     */   
/*     */   private static List<Pair<Aggregate, String>> processAggregates(V8CachingReader reader) {
/* 181 */     TreeMap<Long, Aggregate> map = reader.getAggregatesMap();
/* 182 */     List<Pair<Aggregate, String>> list = new ArrayList<>();
/* 183 */     for (Aggregate aggregate : map.values()) {
/* 184 */       list.add(Pair.create(aggregate, aggregate.getPresentation(reader)));
/*     */     }
/* 186 */     list.sort(Pair.comparingBySecond());
/* 187 */     return list;
/*     */   }
/*     */ }


/* Location:              C:\Program Files\JetBrains\WebStorm 2021.1\plugins\NodeJS\lib\NodeJS.jar!\com\jetbrains\nodejs\run\profile\heap\calculation\diff\V8HeapDiffCalculator.class
 * Java compiler version: 11 (55.0)
 * JD-Core Version:       1.1.3
 */