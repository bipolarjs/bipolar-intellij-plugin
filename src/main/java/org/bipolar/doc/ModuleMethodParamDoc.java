/*    */ package org.bipolar.doc;
/*    */ 
/*    */ import org.jetbrains.annotations.NotNull;
/*    */ 
/*    */ 
/*    */ 
/*    */ public class ModuleMethodParamDoc
/*    */ {
/*    */   private final String myName;
/*    */   private final boolean myOptional;
/*    */   
/*    */   public ModuleMethodParamDoc(@NotNull String name, boolean optional) {
/* 13 */     this.myName = name;
/* 14 */     this.myOptional = optional;
/*    */   }
/*    */   
/*    */   @NotNull
/*    */   public String getName() {
/* 19 */     if (this.myName == null) $$$reportNull$$$0(1);  return this.myName;
/*    */   }
/*    */   
/*    */   public boolean isOptional() {
/* 23 */     return this.myOptional;
/*    */   }
/*    */ }


/* Location:              C:\Program Files\JetBrains\WebStorm 2021.1\plugins\NodeJS\lib\NodeJS.jar!\com\jetbrains\nodejs\doc\ModuleMethodParamDoc.class
 * Java compiler version: 11 (55.0)
 * JD-Core Version:       1.1.3
 */