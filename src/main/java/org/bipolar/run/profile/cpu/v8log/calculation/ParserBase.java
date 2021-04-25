/*    */ package org.bipolar.run.profile.cpu.v8log.calculation;
/*    */ 
/*    */ import com.intellij.openapi.diagnostic.Logger;
/*    */ import com.intellij.util.ThrowableConsumer;
/*    */ import org.bipolar.run.profile.cpu.v8log.data.ArgumentType;
/*    */ import java.io.IOException;
/*    */ import java.util.List;
/*    */ 
/*    */ 
/*    */ 
/*    */ abstract class ParserBase
/*    */   implements ThrowableConsumer<List<String>, IOException>
/*    */ {
/* 14 */   private static final Logger LOG = Logger.getInstance(ParserBase.class);
/* 15 */   public static final ParserBase EMPTY = new ParserBase(0, -1, new ArgumentType[0])
/*    */     {
/*    */       protected void process(List<String> strings) {}
/*    */     };
/*    */ 
/*    */   
/*    */   private final int myMinNumArgs;
/*    */   private final int myMaxNumArgs;
/*    */   private final ArgumentType[] myTypes;
/*    */   
/*    */   ParserBase(int minNumArgs, int maxNumArgs, ArgumentType... types) {
/* 26 */     this.myMinNumArgs = minNumArgs;
/* 27 */     this.myMaxNumArgs = maxNumArgs;
/* 28 */     this.myTypes = types;
/*    */   }
/*    */ 
/*    */   
/*    */   public void consume(List<String> strings) throws IOException {
/* 33 */     if (this.myMinNumArgs > strings.size()) {
/* 34 */       LOG.info("Not enough arguments for " + getClass().getName());
/*    */       return;
/*    */     } 
/* 37 */     for (int i = 0; i < strings.size(); i++) {
/* 38 */       String s = strings.get(i);
/* 39 */       if (this.myTypes.length <= i)
/* 40 */         break;  if (ArgumentType.address.equals(this.myTypes[i]) && V8TickProcessor.parseAddress(s) == null) {
/* 41 */         LOG.info("string in position " + i + " is not an address: " + s + " for " + getClass().getName());
/*    */         return;
/*    */       } 
/* 44 */       if (ArgumentType.number.equals(this.myTypes[i]) && V8TickProcessor.parseNumber(s) == -1L) {
/* 45 */         LOG.info("string in position " + i + " is not a number: " + s + " for " + getClass().getName());
/*    */         return;
/*    */       } 
/*    */     } 
/* 49 */     process(strings);
/*    */   }
/*    */   
/*    */   protected abstract void process(List<String> paramList) throws IOException;
/*    */ }


/* Location:              C:\Program Files\JetBrains\WebStorm 2021.1\plugins\NodeJS\lib\NodeJS.jar!\com\jetbrains\nodejs\run\profile\cpu\v8log\calculation\ParserBase.class
 * Java compiler version: 11 (55.0)
 * JD-Core Version:       1.1.3
 */