/*    */ package org.bipolar.run.profile.cpu.v8log.data;
/*    */ 
/*    */ import java.util.HashMap;
/*    */ import java.util.Map;
/*    */ import org.jetbrains.annotations.NotNull;
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public enum V8EventType
/*    */ {
/* 12 */   Execute("V8.Execute", "execution", false, 0, false, false),
/* 13 */   External("V8.External", "external", false, 0, false, false),
/*    */   
/* 15 */   CompileFullCode("V8.CompileFullCode", "compile unopt", true, 0, false, true),
/*    */   
/* 17 */   RecompileSynchronous("V8.RecompileSynchronous", "recompile sync", true, 0, false, true),
/* 18 */   RecompileConcurrent("V8.RecompileConcurrent", "recompile async", false, 1, false, true),
/* 19 */   CompileEval("V8.CompileEval", "compile eval", true, 0, false, true),
/*    */   
/* 21 */   IcMiss("V8.IcMiss", "ic miss", false, 0, false, true),
/* 22 */   Parse("V8.Parse", "parse", true, 0, false, true),
/* 23 */   PreParse("V8.PreParse", "preparse", true, 0, false, true),
/*    */   
/* 25 */   ParseLazy("V8.ParseLazy", "lazy parse", true, 0, false, true),
/*    */   
/* 27 */   GCScavenger("V8.GCScavenger", "gc scavenge", true, 0, true, false),
/* 28 */   GCCompactor("V8.GCCompactor", "gc compaction", true, 0, true, false),
/*    */   
/* 30 */   GCContext("V8.GCContext", "gc context", true, 0, true, false);
/*    */   static {
/* 32 */     MAP_BY_CODE = new HashMap<>();
/*    */     
/* 34 */     for (V8EventType type : values())
/* 35 */       MAP_BY_CODE.put(type.getCode(), type); 
/*    */   }
/*    */   private static final Map<String, V8EventType> MAP_BY_CODE;
/*    */   private final String myCode;
/*    */   private final String myName;
/*    */   private final boolean myPause;
/*    */   private final int myThreadId;
/*    */   private final boolean myIsGc;
/*    */   private final boolean myIsEngine;
/*    */   
/*    */   V8EventType(String code, String name, boolean pause, int threadId, boolean isGc, boolean isEngine) {
/* 46 */     this.myCode = code;
/* 47 */     this.myName = name;
/* 48 */     this.myPause = pause;
/* 49 */     this.myThreadId = threadId;
/* 50 */     this.myIsGc = isGc;
/* 51 */     this.myIsEngine = isEngine;
/*    */   }
/*    */   
/*    */   public String getCode() {
/* 55 */     return this.myCode;
/*    */   }
/*    */   
/*    */   public String getName() {
/* 59 */     return this.myName;
/*    */   }
/*    */   
/*    */   public boolean isPause() {
/* 63 */     return this.myPause;
/*    */   }
/*    */   
/*    */   public int getThreadId() {
/* 67 */     return this.myThreadId;
/*    */   }
/*    */   
/*    */   public boolean isGc() {
/* 71 */     return this.myIsGc;
/*    */   }
/*    */   
/*    */   public boolean isEngine() {
/* 75 */     return this.myIsEngine;
/*    */   }
/*    */   
/*    */   public static V8EventType getByCode(@NotNull String code) {
/* 79 */     if (code == null) $$$reportNull$$$0(0);  return MAP_BY_CODE.get(code);
/*    */   }
/*    */ }


/* Location:              C:\Program Files\JetBrains\WebStorm 2021.1\plugins\NodeJS\lib\NodeJS.jar!\com\jetbrains\nodejs\run\profile\cpu\v8log\data\V8EventType.class
 * Java compiler version: 11 (55.0)
 * JD-Core Version:       1.1.3
 */