/*    */ package org.bipolar.run.profile.heap.io;
/*    */ 
/*    */ import java.io.DataInput;
/*    */ import java.io.DataOutput;
/*    */ import java.io.IOException;
/*    */ import org.jetbrains.annotations.NotNull;
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public class StringRawSerializer
/*    */   implements RawSerializer<String>
/*    */ {
/*    */   public long getRecordSize() {
/* 15 */     return -1L;
/*    */   }
/*    */ 
/*    */   
/*    */   public void write(@NotNull DataOutput os, @NotNull String s) throws IOException {
/* 20 */     if (os == null) $$$reportNull$$$0(0);  if (s == null) $$$reportNull$$$0(1);  os.writeUTF(s);
/*    */   }
/*    */ 
/*    */   
/*    */   public String read(@NotNull DataInput is) throws IOException {
/* 25 */     if (is == null) $$$reportNull$$$0(2);  return is.readUTF();
/*    */   }
/*    */ }


/* Location:              C:\Program Files\JetBrains\WebStorm 2021.1\plugins\NodeJS\lib\NodeJS.jar!\com\jetbrains\nodejs\run\profile\heap\io\StringRawSerializer.class
 * Java compiler version: 11 (55.0)
 * JD-Core Version:       1.1.3
 */