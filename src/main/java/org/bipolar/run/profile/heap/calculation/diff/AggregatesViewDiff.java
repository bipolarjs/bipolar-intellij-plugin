/*     */ package org.bipolar.run.profile.heap.calculation.diff;
/*     */ 
/*     */ import org.bipolar.run.profile.heap.data.Aggregate;
/*     */ import org.bipolar.run.profile.heap.data.V8HeapNodeType;
/*     */ import java.util.ArrayList;
/*     */ import java.util.List;
/*     */ import org.jetbrains.annotations.NotNull;
/*     */ import org.jetbrains.annotations.Nullable;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class AggregatesViewDiff
/*     */ {
/*  18 */   private final List<AggregateDifference> myDiffMap = new ArrayList<>();
/*     */ 
/*     */   
/*     */   public void addAggregate(@NotNull AggregateDifference difference) {
/*  22 */     if (difference == null) $$$reportNull$$$0(0);  this.myDiffMap.add(difference);
/*     */   }
/*     */   
/*     */   public List<AggregateDifference> getList() {
/*  26 */     return this.myDiffMap;
/*     */   }
/*     */   
/*     */   public static class AggregateDifference
/*     */   {
/*     */     private final String myClassName;
/*     */     private final V8HeapNodeType myType;
/*     */     @Nullable
/*     */     private final Aggregate myBase;
/*     */     @Nullable
/*     */     private final Aggregate myChanged;
/*     */     private int myAddedCnt;
/*     */     private int myRemovedCnt;
/*     */     private long myAddedSize;
/*     */     private long myRemovedSize;
/*     */     
/*     */     public AggregateDifference(String className, V8HeapNodeType type, @Nullable Aggregate base, @Nullable Aggregate changed) {
/*  43 */       assert base != null || changed != null;
/*  44 */       this.myClassName = className;
/*  45 */       this.myType = type;
/*  46 */       this.myBase = base;
/*  47 */       this.myChanged = changed;
/*  48 */       if (base == null) {
/*  49 */         this.myAddedCnt = changed.getCnt();
/*  50 */         this.myAddedSize = changed.getSelfSize();
/*  51 */         this.myRemovedCnt = 0;
/*  52 */         this.myRemovedSize = 0L;
/*     */       } 
/*  54 */       if (changed == null) {
/*  55 */         this.myAddedCnt = 0;
/*  56 */         this.myAddedSize = 0L;
/*  57 */         this.myRemovedCnt = base.getCnt();
/*  58 */         this.myRemovedSize = base.getSelfSize();
/*     */       } 
/*     */     }
/*     */     
/*     */     public boolean isSystem() {
/*  63 */       return (this.myBase == null) ? ((this.myChanged.getClassIdx() < 0L)) : ((this.myBase.getClassIdx() < 0L));
/*     */     }
/*     */     
/*     */     public int getAddedCnt() {
/*  67 */       return this.myAddedCnt;
/*     */     }
/*     */     
/*     */     public void setAddedCnt(int addedCnt) {
/*  71 */       this.myAddedCnt = addedCnt;
/*     */     }
/*     */     
/*     */     public int getRemovedCnt() {
/*  75 */       return this.myRemovedCnt;
/*     */     }
/*     */     
/*     */     public void setRemovedCnt(int removedCnt) {
/*  79 */       this.myRemovedCnt = removedCnt;
/*     */     }
/*     */     
/*     */     public long getAddedSize() {
/*  83 */       return this.myAddedSize;
/*     */     }
/*     */     
/*     */     public void setAddedSize(long addedSize) {
/*  87 */       this.myAddedSize = addedSize;
/*     */     }
/*     */     
/*     */     public long getRemovedSize() {
/*  91 */       return this.myRemovedSize;
/*     */     }
/*     */     
/*     */     public void setRemovedSize(long removedSize) {
/*  95 */       this.myRemovedSize = removedSize;
/*     */     }
/*     */     
/*     */     public String getClassIdx() {
/*  99 */       return this.myClassName;
/*     */     }
/*     */     
/*     */     public V8HeapNodeType getType() {
/* 103 */       return this.myType;
/*     */     }
/*     */     
/*     */     @Nullable
/*     */     public Aggregate getBase() {
/* 108 */       return this.myBase;
/*     */     }
/*     */     
/*     */     @Nullable
/*     */     public Aggregate getChanged() {
/* 113 */       return this.myChanged;
/*     */     }
/*     */     
/*     */     public long selfSizeDiff() {
/* 117 */       return (this.myBase == null) ? this.myChanged.getSelfSize() : ((this.myChanged == null) ? -this.myBase.getSelfSize() : (
/* 118 */         this.myChanged.getSelfSize() - this.myBase.getSelfSize()));
/*     */     }
/*     */     
/*     */     public int selfSizePercent() {
/* 122 */       if (this.myChanged == null) return -1; 
/* 123 */       if (this.myBase == null) return -1; 
/* 124 */       long delta = selfSizeDiff();
/* 125 */       double val = delta / this.myChanged.getSelfSize() * 100.0D;
/* 126 */       return (int)Math.round(val);
/*     */     }
/*     */     
/*     */     public int objectsPercent() {
/* 130 */       if (this.myChanged == null) return -1; 
/* 131 */       if (this.myBase == null) return -1; 
/* 132 */       long delta = objectsDiff();
/* 133 */       double val = delta / this.myChanged.getCnt() * 100.0D;
/* 134 */       return (int)Math.round(val);
/*     */     }
/*     */     
/*     */     public long objectsDiff() {
/* 138 */       return (this.myBase == null) ? this.myChanged.getCnt() : ((this.myChanged == null) ? -this.myBase.getCnt() : (
/* 139 */         this.myChanged.getCnt() - this.myBase.getCnt()));
/*     */     }
/*     */ 
/*     */     
/*     */     public boolean equals(Object o) {
/* 144 */       if (this == o) return true; 
/* 145 */       if (o == null || getClass() != o.getClass()) return false;
/*     */       
/* 147 */       AggregateDifference that = (AggregateDifference)o;
/*     */       
/* 149 */       if ((this.myClassName != null) ? !this.myClassName.equals(that.myClassName) : (that.myClassName != null)) return false; 
/* 150 */       if (this.myType != that.myType) return false;
/*     */       
/* 152 */       return true;
/*     */     }
/*     */ 
/*     */     
/*     */     public int hashCode() {
/* 157 */       int result = (this.myClassName != null) ? this.myClassName.hashCode() : 0;
/* 158 */       result = 31 * result + ((this.myType != null) ? this.myType.hashCode() : 0);
/* 159 */       return result;
/*     */     }
/*     */     
/*     */     public boolean somethingChanged() {
/* 163 */       return (this.myAddedCnt > 0 || this.myAddedSize > 0L || this.myRemovedCnt > 0 || this.myRemovedSize > 0L);
/*     */     }
/*     */   }
/*     */ }


/* Location:              C:\Program Files\JetBrains\WebStorm 2021.1\plugins\NodeJS\lib\NodeJS.jar!\com\jetbrains\nodejs\run\profile\heap\calculation\diff\AggregatesViewDiff.class
 * Java compiler version: 11 (55.0)
 * JD-Core Version:       1.1.3
 */