/*    */ package org.bipolar.run.profile.cpu.v8log.data;
/*    */ 
/*    */ import org.jetbrains.annotations.Nullable;
/*    */ 
/*    */ 
/*    */ 
/*    */ public enum CodeState
/*    */ {
/*  9 */   compiled(0, ""), optimizable(1, "~"), optimized(2, "*");
/*    */   
/*    */   private final String myPrefix;
/*    */   private final int myCode;
/*    */   
/*    */   CodeState(int code, String prefix) {
/* 15 */     this.myCode = code;
/* 16 */     this.myPrefix = prefix;
/*    */   }
/*    */   
/*    */   public int getCode() {
/* 20 */     return this.myCode;
/*    */   }
/*    */   
/*    */   public String getPrefix() {
/* 24 */     return this.myPrefix;
/*    */   }
/*    */   
/*    */   @Nullable
/*    */   public static CodeState fromCode(int code) {
/* 29 */     for (CodeState state : values()) {
/* 30 */       if (state.getCode() == code) return state; 
/*    */     } 
/* 32 */     return null;
/*    */   }
/*    */   
/*    */   @Nullable
/*    */   public static CodeState fromStrState(String state) {
/* 37 */     for (CodeState codeState : values()) {
/* 38 */       if (codeState.getPrefix().equals(state)) return codeState; 
/*    */     } 
/* 40 */     return null;
/*    */   }
/*    */   
/*    */   public static CodeState safeValueOf(String name) {
/*    */     try {
/* 45 */       return valueOf(name);
/* 46 */     } catch (IllegalArgumentException e) {
/* 47 */       return null;
/*    */     } 
/*    */   }
/*    */ }


/* Location:              C:\Program Files\JetBrains\WebStorm 2021.1\plugins\NodeJS\lib\NodeJS.jar!\com\jetbrains\nodejs\run\profile\cpu\v8log\data\CodeState.class
 * Java compiler version: 11 (55.0)
 * JD-Core Version:       1.1.3
 */