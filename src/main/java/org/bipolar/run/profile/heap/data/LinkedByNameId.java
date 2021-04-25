/*    */ package org.bipolar.run.profile.heap.data;
/*    */ 
/*    */ import org.bipolar.run.profile.heap.io.RawSerializer;
/*    */ import java.io.DataInput;
/*    */ import java.io.DataOutput;
/*    */ import java.io.IOException;
/*    */ import org.jetbrains.annotations.NotNull;
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
/*    */ 
/*    */ 
/*    */ public class LinkedByNameId
/*    */ {
/*    */   private final long myId;
/*    */   private final boolean myIsNode;
/*    */   private long mySecondId;
/*    */   
/*    */   public LinkedByNameId(long id, boolean isNode) {
/* 35 */     this.myId = id;
/* 36 */     this.myIsNode = isNode;
/*    */   }
/*    */   
/*    */   public long getSecondId() {
/* 40 */     return this.mySecondId;
/*    */   }
/*    */   
/*    */   public void setSecondId(long secondId) {
/* 44 */     this.mySecondId = secondId;
/*    */   }
/*    */   
/*    */   public long getId() {
/* 48 */     return this.myId;
/*    */   }
/*    */   
/*    */   public boolean isNode() {
/* 52 */     return this.myIsNode;
/*    */   }
/*    */ 
/*    */   
/*    */   public boolean equals(Object o) {
/* 57 */     if (this == o) return true; 
/* 58 */     if (o == null || getClass() != o.getClass()) return false;
/*    */     
/* 60 */     LinkedByNameId id = (LinkedByNameId)o;
/*    */     
/* 62 */     if (this.myId != id.myId) return false; 
/* 63 */     if (this.myIsNode != id.myIsNode) return false;
/*    */     
/* 65 */     return true;
/*    */   }
/*    */ 
/*    */   
/*    */   public int hashCode() {
/* 70 */     int result = (int)(this.myId ^ this.myId >>> 32L);
/* 71 */     result = 31 * result + (this.myIsNode ? 1 : 0);
/* 72 */     return result;
/*    */   }
/*    */   
/*    */   public static class Serializer implements RawSerializer<LinkedByNameId> {
/* 76 */     private static final Serializer ourInstance = new Serializer();
/*    */     
/*    */     public static Serializer getInstance() {
/* 79 */       return ourInstance;
/*    */     }
/*    */ 
/*    */     
/*    */     public long getRecordSize() {
/* 84 */       return 9L;
/*    */     }
/*    */ 
/*    */     
/*    */     public void write(@NotNull DataOutput os, @NotNull LinkedByNameId id) throws IOException {
/* 89 */       if (os == null) $$$reportNull$$$0(0);  if (id == null) $$$reportNull$$$0(1);  RawSerializer.Helper.serializeLong(id.getId(), os);
/* 90 */       os.writeBoolean(id.isNode());
/*    */     }
/*    */ 
/*    */     
/*    */     public LinkedByNameId read(@NotNull DataInput is) throws IOException {
/* 95 */       if (is == null) $$$reportNull$$$0(2);  return new LinkedByNameId(RawSerializer.Helper.deserializeLong(is), is.readBoolean());
/*    */     }
/*    */   }
/*    */ }


/* Location:              C:\Program Files\JetBrains\WebStorm 2021.1\plugins\NodeJS\lib\NodeJS.jar!\com\jetbrains\nodejs\run\profile\heap\data\LinkedByNameId.class
 * Java compiler version: 11 (55.0)
 * JD-Core Version:       1.1.3
 */