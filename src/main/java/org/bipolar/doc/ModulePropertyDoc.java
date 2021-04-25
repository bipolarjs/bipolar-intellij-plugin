/*    */ package org.bipolar.doc;
/*    */ 
/*    */ import org.jetbrains.annotations.NotNull;
/*    */ import org.jetbrains.annotations.Nullable;
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public class ModulePropertyDoc
/*    */ {
/*    */   private final String myName;
/*    */   private final String myTextRaw;
/*    */   private final String myDescription;
/*    */   
/*    */   public ModulePropertyDoc(@NotNull String name, @Nullable String textRaw, @Nullable String description) {
/* 16 */     this.myName = name;
/* 17 */     this.myTextRaw = textRaw;
/* 18 */     this.myDescription = description;
/*    */   }
/*    */   
/*    */   @NotNull
/*    */   public String getName() {
/* 23 */     if (this.myName == null) $$$reportNull$$$0(1);  return this.myName;
/*    */   }
/*    */   
/*    */   @Nullable
/*    */   public String getTextRaw() {
/* 28 */     return this.myTextRaw;
/*    */   }
/*    */   
/*    */   @Nullable
/*    */   public String getDescription() {
/* 33 */     return this.myDescription;
/*    */   }
/*    */ }


/* Location:              C:\Program Files\JetBrains\WebStorm 2021.1\plugins\NodeJS\lib\NodeJS.jar!\com\jetbrains\nodejs\doc\ModulePropertyDoc.class
 * Java compiler version: 11 (55.0)
 * JD-Core Version:       1.1.3
 */