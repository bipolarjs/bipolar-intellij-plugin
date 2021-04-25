/*    */ package org.bipolar.run.profile.cpu.v8log.calculation;
/*    */ 
/*    */ import com.intellij.openapi.util.text.StringUtil;
/*    */ import com.intellij.util.ThrowableConsumer;
/*    */ import java.io.IOException;
/*    */ import java.io.InputStream;
/*    */ import java.nio.charset.Charset;
/*    */ import org.jetbrains.annotations.NotNull;
/*    */ import org.jetbrains.annotations.TestOnly;
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
/*    */ public class V8LineReader
/*    */ {
/*    */   @TestOnly
/*    */   public static final String BUF_SIZE_KEY = "node.js.v8.cpu.profiling.line.buf.size";
/* 31 */   private final byte[] myBuf = new byte[Integer.getInteger("node.js.v8.cpu.profiling.line.buf.size", 20000).intValue()];
/*    */   private final InputStream myInputStream;
/*    */   
/*    */   public V8LineReader(@NotNull InputStream in) {
/* 35 */     this.myInputStream = in;
/*    */   }
/*    */   
/*    */   public void readLines(@NotNull ThrowableConsumer<String, IOException> consumer, Charset charset) throws IOException {
/* 39 */     if (consumer == null) $$$reportNull$$$0(1);  int off = 0;
/* 40 */     int size = 0;
/* 41 */     while (size >= 0) {
/* 42 */       size = this.myInputStream.read(this.myBuf, off, this.myBuf.length - off);
/* 43 */       if (size < 0 && off == 0)
/* 44 */         break;  String str = new String(this.myBuf, 0, (size < 0) ? off : (size + off), charset);
/* 45 */       off = 0;
/* 46 */       String[] lines = StringUtil.splitByLines(str.trim(), true);
/* 47 */       if (lines.length == 0)
/* 48 */         continue;  String last = lines[lines.length - 1];
/* 49 */       int linesSize = lines.length - 1;
/* 50 */       if (str.endsWith("\n") || str.endsWith("\r") || lines.length <= 1) {
/* 51 */         linesSize++;
/* 52 */         last = null;
/*    */       } 
/* 54 */       for (int i = 0; i < linesSize; i++) {
/* 55 */         consumer.consume(lines[i]);
/*    */       }
/* 57 */       if (last != null) {
/* 58 */         if (size < 0) {
/* 59 */           if (!StringUtil.isEmptyOrSpaces(last)) consumer.consume(last); 
/*    */           break;
/*    */         } 
/* 62 */         byte[] lastBytes = last.getBytes(charset);
/* 63 */         off = lastBytes.length;
/* 64 */         System.arraycopy(lastBytes, 0, this.myBuf, 0, off);
/*    */       } 
/*    */     } 
/*    */   }
/*    */ }


/* Location:              C:\Program Files\JetBrains\WebStorm 2021.1\plugins\NodeJS\lib\NodeJS.jar!\com\jetbrains\nodejs\run\profile\cpu\v8log\calculation\V8LineReader.class
 * Java compiler version: 11 (55.0)
 * JD-Core Version:       1.1.3
 */