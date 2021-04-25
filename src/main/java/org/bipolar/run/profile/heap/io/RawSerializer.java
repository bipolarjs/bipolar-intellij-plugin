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
/*    */ public interface RawSerializer<T>
/*    */ {
/*    */   long getRecordSize();
/*    */   
/*    */   void write(@NotNull DataOutput paramDataOutput, @NotNull T paramT) throws IOException;
/*    */   
/*    */   T read(@NotNull DataInput paramDataInput) throws IOException;
/*    */   
/*    */   public static final class Helper
/*    */   {
/*    */     public static void serializeInt(int value, @NotNull DataOutput dout) throws IOException {
/* 35 */       if (dout == null) $$$reportNull$$$0(0);  int off = 0;
/* 36 */       for (int i = 0; i < 4; i++) {
/* 37 */         dout.writeByte(value >> off & 0xFF);
/* 38 */         off += 8;
/*    */       } 
/*    */     }
/*    */     
/*    */     public static int deserializeInt(@NotNull DataInput din) throws IOException {
/* 43 */       if (din == null) $$$reportNull$$$0(1);  int result = 0;
/* 44 */       int off = 0;
/* 45 */       for (int i = 0; i < 4; i++) {
/* 46 */         result |= (din.readByte() & 0xFF) << off;
/* 47 */         off += 8;
/*    */       } 
/* 49 */       return result;
/*    */     }
/*    */     
/*    */     public static void serializeLong(long value, @NotNull DataOutput dout) throws IOException {
/* 53 */       if (SYNTHETIC_LOCAL_VARIABLE_2 == null) $$$reportNull$$$0(2);  int off = 0;
/* 54 */       for (int i = 0; i < 8; i++) {
/* 55 */         SYNTHETIC_LOCAL_VARIABLE_2.writeByte((byte)(int)(value >> off & 0xFFL));
/* 56 */         off += 8;
/*    */       } 
/*    */     }
/*    */     
/*    */     public static long deserializeLong(@NotNull DataInput din) throws IOException {
/* 61 */       if (din == null) $$$reportNull$$$0(3);  long result = 0L;
/* 62 */       int off = 0;
/* 63 */       for (int i = 0; i < 8; i++) {
/* 64 */         result |= (din.readByte() & 0xFF) << off;
/* 65 */         off += 8;
/*    */       } 
/* 67 */       return result;
/*    */     }
/*    */   }
/*    */ }


/* Location:              C:\Program Files\JetBrains\WebStorm 2021.1\plugins\NodeJS\lib\NodeJS.jar!\com\jetbrains\nodejs\run\profile\heap\io\RawSerializer.class
 * Java compiler version: 11 (55.0)
 * JD-Core Version:       1.1.3
 */