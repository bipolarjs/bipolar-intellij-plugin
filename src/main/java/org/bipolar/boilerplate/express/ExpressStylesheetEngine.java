/*    */ package org.bipolar.boilerplate.express;
/*    */ 
/*    */ import org.bipolar.util.ui.WithDisplayName;
/*    */
import org.jetbrains.annotations.Nls;
/*    */ import org.jetbrains.annotations.NotNull;
/*    */ import org.jetbrains.annotations.Nullable;
/*    */ 
/*    */ public enum ExpressStylesheetEngine
/*    */   implements WithDisplayName
/*    */ {
/* 11 */   PLAIN_CSS("Plain CSS", null),
/* 12 */   LESS("LESS", "less"),
/* 13 */   STYLUS("Stylus", "stylus"),
/* 14 */   COMPASS("Compass", "compass"),
/* 15 */   SASS("SASS", "sass");
/*    */   @Nls
/*    */   private final String myDisplayName;
/*    */   private final String myCliOption;
/*    */   
/*    */   ExpressStylesheetEngine(String displayName, String cliOption) {
/* 21 */     this.myDisplayName = displayName;
/* 22 */     this.myCliOption = cliOption;
/*    */   }
/*    */   
/*    */   @NotNull
/*    */   @Nls
/*    */   public String getDisplayName() {
/* 28 */     if (this.myDisplayName == null) $$$reportNull$$$0(1);  return this.myDisplayName;
/*    */   }
/*    */   
/*    */   @Nullable
/*    */   public String getCliOption() {
/* 33 */     return this.myCliOption;
/*    */   }
/*    */ }


/* Location:              C:\Program Files\JetBrains\WebStorm 2021.1\plugins\NodeJS\lib\NodeJS.jar!\com\jetbrains\nodejs\boilerplate\express\ExpressStylesheetEngine.class
 * Java compiler version: 11 (55.0)
 * JD-Core Version:       1.1.3
 */