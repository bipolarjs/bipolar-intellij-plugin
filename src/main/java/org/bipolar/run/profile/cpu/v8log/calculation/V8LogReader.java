/*    */ package org.bipolar.run.profile.cpu.v8log.calculation;
/*    */ 
/*    */ import com.google.common.annotations.VisibleForTesting;
/*    */ import com.intellij.idea.RareLogger;
/*    */ import com.intellij.openapi.diagnostic.Logger;
/*    */ import com.intellij.openapi.util.text.StringUtil;
/*    */ import com.intellij.util.ThrowableConsumer;
/*    */ import java.io.BufferedInputStream;
/*    */ import java.io.File;
/*    */ import java.io.FileInputStream;
/*    */ import java.io.IOException;
/*    */ import java.nio.charset.StandardCharsets;
/*    */ import java.util.List;
/*    */ import java.util.Map;
/*    */ import org.jetbrains.annotations.NotNull;
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public class V8LogReader
/*    */ {
/* 25 */   private static final Logger LOG = RareLogger.wrap(Logger.getInstance(V8LogReader.class), false);
/*    */   private final File myFile;
/*    */   private final Map<String, ThrowableConsumer<List<String>, IOException>> myParsers;
/*    */   
/*    */   public V8LogReader(File file, Map<String, ThrowableConsumer<List<String>, IOException>> parsers) {
/* 30 */     this.myFile = file;
/* 31 */     this.myParsers = parsers;
/*    */   }
/*    */   
/*    */   public void read() throws IOException {
/* 35 */     FileInputStream fis = new FileInputStream(this.myFile);
/*    */     try {
/* 37 */       (new V8LineReader(new BufferedInputStream(fis))).readLines(line -> processLine(line), StandardCharsets.UTF_8);
/*    */     } finally {
/* 39 */       fis.close();
/*    */     } 
/*    */   }
/*    */   
/*    */   private void processLine(String line) throws IOException {
/* 44 */     List<String> fields = splitIntoFields(line);
/*    */     String commandName;
/* 46 */     if (fields.isEmpty() || StringUtil.isEmptyOrSpaces(commandName = fields.get(0)))
/* 47 */       return;  ThrowableConsumer<List<String>, IOException> parser = this.myParsers.get(commandName.trim());
/* 48 */     if (parser == null) {
/* 49 */       LOG.info("Unknown v8 log field: " + commandName);
/*    */     } else {
/* 51 */       parser.consume(fields.subList(1, fields.size()));
/*    */     } 
/*    */   }
/*    */   
/*    */   @VisibleForTesting
/*    */   @NotNull
/*    */   public static List<String> splitIntoFields(@NotNull String line) {
/* 58 */     if (line == null) $$$reportNull$$$0(0);  if (StringUtil.splitHonorQuotes(line, ',') == null) $$$reportNull$$$0(1);  return StringUtil.splitHonorQuotes(line, ',');
/*    */   }
/*    */ }


/* Location:              C:\Program Files\JetBrains\WebStorm 2021.1\plugins\NodeJS\lib\NodeJS.jar!\com\jetbrains\nodejs\run\profile\cpu\v8log\calculation\V8LogReader.class
 * Java compiler version: 11 (55.0)
 * JD-Core Version:       1.1.3
 */