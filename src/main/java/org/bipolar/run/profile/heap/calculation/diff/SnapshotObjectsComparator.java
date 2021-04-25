/*    */ package org.bipolar.run.profile.heap.calculation.diff;
/*    */ 
/*    */ import org.bipolar.run.profile.heap.V8CachingReader;
/*    */ import gnu.trove.TLongArrayList;
/*    */ import java.util.HashMap;
/*    */ import java.util.HashSet;
/*    */ import java.util.List;
/*    */ import java.util.Map;
/*    */ import java.util.Objects;
/*    */ import java.util.Set;
/*    */ 
/*    */ 
/*    */ 
/*    */ final class SnapshotObjectsComparator
/*    */ {
/*    */   private final V8CachingReader myBaseReader;
/*    */   private final V8CachingReader myChangedReader;
/*    */   private final TLongArrayList myChangedSnapshotIds;
/*    */   private final boolean myIsSystemAggregate;
/*    */   private final Map<Long, Long> myBaseSnapshotIdToId;
/*    */   private final Map<Long, Long> myBaseEquivalentToChanged;
/*    */   private final Set<Long> myTakenBase;
/*    */   
/*    */   SnapshotObjectsComparator(V8CachingReader baseReader, V8CachingReader changedReader, TLongArrayList baseSnapshotIds, TLongArrayList changedSnapshotIds, List<Long> baseChildren, boolean isSystemAggregate) {
/* 25 */     this.myBaseReader = baseReader;
/* 26 */     this.myChangedReader = changedReader;
/* 27 */     this.myChangedSnapshotIds = changedSnapshotIds;
/* 28 */     this.myIsSystemAggregate = isSystemAggregate;
/*    */     
/* 30 */     this.myBaseSnapshotIdToId = new HashMap<>();
/* 31 */     for (Long child : baseChildren) {
/* 32 */       this.myBaseSnapshotIdToId.put(Long.valueOf(baseSnapshotIds.get(child.intValue())), child);
/*    */     }
/* 34 */     this.myBaseEquivalentToChanged = new HashMap<>();
/* 35 */     this.myTakenBase = new HashSet<>();
/*    */   }
/*    */   
/*    */   public Long getBaseForChanged(Long changedId) {
/* 39 */     Long saved = this.myBaseEquivalentToChanged.get(changedId);
/* 40 */     if (saved != null && saved.longValue() >= 0L) return saved;
/*    */     
/* 42 */     Long baseId = this.myBaseSnapshotIdToId.get(Long.valueOf(this.myChangedSnapshotIds.get(changedId.intValue())));
/* 43 */     if (baseId != null && (
/* 44 */       !this.myIsSystemAggregate || compareNames(baseId, changedId))) {
/* 45 */       this.myBaseEquivalentToChanged.put(changedId, baseId);
/* 46 */       this.myTakenBase.add(baseId);
/* 47 */       return baseId;
/*    */     } 
/*    */     
/* 50 */     this.myBaseEquivalentToChanged.put(changedId, Long.valueOf(-1L));
/* 51 */     return null;
/*    */   }
/*    */   
/*    */   private boolean compareNames(Long baseId, Long changedId) {
/* 55 */     String changedName = this.myChangedReader.getString(this.myChangedReader.getNode(changedId.longValue()).getNameId());
/* 56 */     String baseName = this.myBaseReader.getString(this.myBaseReader.getNode(baseId.longValue()).getNameId());
/* 57 */     return Objects.equals(changedName, baseName);
/*    */   }
/*    */   
/*    */   public boolean haveEquivalent(Long baseId) {
/* 61 */     return this.myTakenBase.contains(baseId);
/*    */   }
/*    */ }


/* Location:              C:\Program Files\JetBrains\WebStorm 2021.1\plugins\NodeJS\lib\NodeJS.jar!\com\jetbrains\nodejs\run\profile\heap\calculation\diff\SnapshotObjectsComparator.class
 * Java compiler version: 11 (55.0)
 * JD-Core Version:       1.1.3
 */