/*    */ package org.bipolar.run.profile.heap.data;
/*    */ 
/*    */ import java.io.Externalizable;
/*    */ import java.io.IOException;
/*    */ import java.io.ObjectInput;
/*    */ import java.io.ObjectOutput;
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public class V8HeapHeader
/*    */   implements Externalizable
/*    */ {
/*    */   private String mySnapshotName;
/*    */   private long myNodesCnt;
/*    */   private long myEdgesCnt;
/*    */   private long myTraceFunctionsCnt;
/*    */   
/*    */   public V8HeapHeader() {}
/*    */   
/*    */   public V8HeapHeader(String snapshotName, long nodesCnt, long edgesCnt, long traceFunctionsCnt) {
/* 36 */     this.mySnapshotName = snapshotName;
/* 37 */     this.myNodesCnt = nodesCnt;
/* 38 */     this.myEdgesCnt = edgesCnt;
/* 39 */     this.myTraceFunctionsCnt = traceFunctionsCnt;
/*    */   }
/*    */   
/*    */   public String getSnapshotName() {
/* 43 */     return this.mySnapshotName;
/*    */   }
/*    */   
/*    */   public long getNodesCnt() {
/* 47 */     return this.myNodesCnt;
/*    */   }
/*    */   
/*    */   public long getEdgesCnt() {
/* 51 */     return this.myEdgesCnt;
/*    */   }
/*    */   
/*    */   public long getTraceFunctionsCnt() {
/* 55 */     return this.myTraceFunctionsCnt;
/*    */   }
/*    */ 
/*    */   
/*    */   public void writeExternal(ObjectOutput out) throws IOException {
/* 60 */     out.writeLong(getNodesCnt());
/* 61 */     out.writeLong(getEdgesCnt());
/* 62 */     out.writeLong(getTraceFunctionsCnt());
/* 63 */     out.writeUTF(getSnapshotName());
/*    */   }
/*    */ 
/*    */   
/*    */   public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
/* 68 */     this.myNodesCnt = in.readLong();
/* 69 */     this.myEdgesCnt = in.readLong();
/* 70 */     this.myTraceFunctionsCnt = in.readLong();
/* 71 */     this.mySnapshotName = in.readUTF();
/*    */   }
/*    */ }


/* Location:              C:\Program Files\JetBrains\WebStorm 2021.1\plugins\NodeJS\lib\NodeJS.jar!\com\jetbrains\nodejs\run\profile\heap\data\V8HeapHeader.class
 * Java compiler version: 11 (55.0)
 * JD-Core Version:       1.1.3
 */