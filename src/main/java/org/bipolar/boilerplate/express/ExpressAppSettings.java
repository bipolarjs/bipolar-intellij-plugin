/*    */ package org.bipolar.boilerplate.express;
/*    */ 
/*    */ import com.intellij.util.text.SemVer;
/*    */ import org.jetbrains.annotations.NotNull;
/*    */ import org.jetbrains.annotations.Nullable;
/*    */ 
/*    */ 
/*    */ 
/*    */ public class ExpressAppSettings
/*    */ {
/*    */   private final SemVer myPackageVersion;
/*    */   private final ExpressTemplateEngine myTemplateEngine;
/*    */   private final ExpressStylesheetEngine myCssEngine;
/*    */   
/*    */   public ExpressAppSettings(@Nullable SemVer packageVersion, @NotNull ExpressTemplateEngine templateEngine, @NotNull ExpressStylesheetEngine cssEngine) {
/* 16 */     this.myPackageVersion = packageVersion;
/* 17 */     this.myTemplateEngine = templateEngine;
/* 18 */     this.myCssEngine = cssEngine;
/*    */   }
/*    */   
/*    */   @Nullable
/*    */   public SemVer getPackageVersion() {
/* 23 */     return this.myPackageVersion;
/*    */   }
/*    */   
/*    */   @NotNull
/*    */   public ExpressTemplateEngine getTemplateEngine() {
/* 28 */     if (this.myTemplateEngine == null) $$$reportNull$$$0(2);  return this.myTemplateEngine;
/*    */   }
/*    */   
/*    */   @NotNull
/*    */   public ExpressStylesheetEngine getStylesheetEngine() {
/* 33 */     if (this.myCssEngine == null) $$$reportNull$$$0(3);  return this.myCssEngine;
/*    */   }
/*    */ }


/* Location:              C:\Program Files\JetBrains\WebStorm 2021.1\plugins\NodeJS\lib\NodeJS.jar!\com\jetbrains\nodejs\boilerplate\express\ExpressAppSettings.class
 * Java compiler version: 11 (55.0)
 * JD-Core Version:       1.1.3
 */