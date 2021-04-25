/*    */ package org.bipolar.doc;
/*    */ 
/*    */ import com.google.common.collect.ImmutableList;
/*    */ import java.util.List;
/*    */ import org.jetbrains.annotations.NotNull;
/*    */ import org.jetbrains.annotations.Nullable;
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public class ModuleMethodDoc
/*    */ {
/*    */   private final String myName;
/*    */   private final String myTextRaw;
/*    */   private final String myDescription;
/*    */   private final ImmutableList<ModuleMethodParamDoc> myParams;
/*    */   
/*    */   public ModuleMethodDoc(@NotNull String name, @Nullable String textRaw, @Nullable String description, @NotNull List<ModuleMethodParamDoc> params) {
/* 23 */     this.myName = name;
/* 24 */     this.myTextRaw = textRaw;
/* 25 */     this.myDescription = description;
/* 26 */     this.myParams = ImmutableList.copyOf(params);
/*    */   }
/*    */   
/*    */   @NotNull
/*    */   public String getName() {
/* 31 */     if (this.myName == null) $$$reportNull$$$0(2);  return this.myName;
/*    */   }
/*    */   
/*    */   @Nullable
/*    */   public String getTextRaw() {
/* 36 */     return this.myTextRaw;
/*    */   }
/*    */   
/*    */   @Nullable
/*    */   public String getDescription() {
/* 41 */     return this.myDescription;
/*    */   }
/*    */   
/*    */   @NotNull
/*    */   public ImmutableList<ModuleMethodParamDoc> getParams() {
/* 46 */     if (this.myParams == null) $$$reportNull$$$0(3);  return this.myParams;
/*    */   }
/*    */ }


/* Location:              C:\Program Files\JetBrains\WebStorm 2021.1\plugins\NodeJS\lib\NodeJS.jar!\com\jetbrains\nodejs\doc\ModuleMethodDoc.class
 * Java compiler version: 11 (55.0)
 * JD-Core Version:       1.1.3
 */