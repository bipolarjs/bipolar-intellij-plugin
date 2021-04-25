/*    */ package org.bipolar.run.profile.heap.calculation;
/*    */ 
/*    */ import org.bipolar.run.profile.V8Utils;
/*    */ import it.unimi.dsi.fastutil.ints.IntArrayList;
/*    */ import it.unimi.dsi.fastutil.ints.IntList;
/*    */ import java.io.Externalizable;
/*    */ import java.io.IOException;
/*    */ import java.io.ObjectInput;
/*    */ import java.io.ObjectOutput;
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public class Flags
/*    */   implements Externalizable
/*    */ {
/*    */   private static final int canBeQueried = 1;
/*    */   private static final int detachedDOMTreeNode = 2;
/*    */   private static final int pageObject = 4;
/*    */   private static final int visitedMarkerMask = 65535;
/*    */   private static final int visitedMarker = 65536;
/*    */   private IntList myFlags;
/*    */   
/*    */   public Flags(int nodesCnt) {
/* 26 */     this.myFlags = (IntList)new IntArrayList(nodesCnt);
/* 27 */     for (int i = 0; i < nodesCnt; i++) {
/* 28 */       this.myFlags.add(0);
/*    */     }
/*    */   }
/*    */ 
/*    */   
/*    */   public Flags() {}
/*    */   
/*    */   boolean visitedAndPage(int idx) {
/* 36 */     return ((this.myFlags.getInt(idx) & 0x10004) > 0);
/*    */   }
/*    */   
/*    */   void clearVisited(int idx) {
/* 40 */     int val = this.myFlags.getInt(idx);
/* 41 */     this.myFlags.set(idx, val & 0xFFFF);
/*    */   }
/*    */   
/*    */   public boolean isPage(int idx) {
/* 45 */     return ((this.myFlags.getInt(idx) & 0x4) > 0);
/*    */   }
/*    */   
/*    */   void addPage(int idx) {
/* 49 */     int val = this.myFlags.getInt(idx);
/* 50 */     this.myFlags.set(idx, val | 0x4);
/*    */   }
/*    */   
/*    */   void addVisited(int idx) {
/* 54 */     int val = this.myFlags.getInt(idx);
/* 55 */     this.myFlags.set(idx, val | 0x10000);
/*    */   }
/*    */   
/*    */   public boolean isQueriable(int idx) {
/* 59 */     return ((this.myFlags.getInt(idx) & 0x1) > 0);
/*    */   }
/*    */   
/*    */   void addQueriableFlag(int idx) {
/* 63 */     int val = this.myFlags.getInt(idx);
/* 64 */     this.myFlags.set(idx, val | 0x1);
/*    */   }
/*    */   
/*    */   void addDetachedFlag(int idx) {
/* 68 */     int val = this.myFlags.getInt(idx);
/* 69 */     this.myFlags.set(idx, val | 0x2);
/*    */   }
/*    */   
/*    */   public boolean isDetached(int idx) {
/* 73 */     return ((this.myFlags.getInt(idx) & 0x2) > 0);
/*    */   }
/*    */ 
/*    */   
/*    */   public void writeExternal(ObjectOutput out) throws IOException {
/* 78 */     V8Utils.writeIntList(this.myFlags, out);
/*    */   }
/*    */ 
/*    */   
/*    */   public void readExternal(ObjectInput in) throws IOException {
/* 83 */     this.myFlags = V8Utils.readIntList(in);
/*    */   }
/*    */ }


/* Location:              C:\Program Files\JetBrains\WebStorm 2021.1\plugins\NodeJS\lib\NodeJS.jar!\com\jetbrains\nodejs\run\profile\heap\calculation\Flags.class
 * Java compiler version: 11 (55.0)
 * JD-Core Version:       1.1.3
 */