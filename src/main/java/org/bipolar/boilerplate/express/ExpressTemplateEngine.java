/*    */ package org.bipolar.boilerplate.express;
/*    */ 
/*    */ import com.intellij.openapi.util.text.StringUtil;
/*    */ import org.bipolar.util.ui.WithDisplayName;
/*    */
import org.jetbrains.annotations.Nls;
/*    */ import org.jetbrains.annotations.NotNull;
/*    */ 
/*    */ 
/*    */ 
/*    */ public enum ExpressTemplateEngine
/*    */   implements WithDisplayName
/*    */ {
/* 13 */   DUST("Dust"),
/* 14 */   EJS("EJS"),
/* 15 */   HBS("Handlebars"),
/* 16 */   HOGAN("Hogan.js"),
/* 17 */   JADE("Jade"),
/* 18 */   PUG("Pug (Jade)"),
/* 19 */   TWIG("Twig"),
/* 20 */   VASH("Vash"),
/* 21 */   NO_VIEW("None");
/*    */   @Nls
/*    */   private final String myDisplayName;
/*    */   
/*    */   ExpressTemplateEngine(String displayName) {
/* 26 */     this.myDisplayName = displayName;
/*    */   }
/*    */ 
/*    */   
/*    */   @NotNull
/*    */   public String getDisplayName() {
/* 32 */     if (this.myDisplayName == null) $$$reportNull$$$0(1);  return this.myDisplayName;
/*    */   }
/*    */   
/*    */   @NotNull
/*    */   public String getCliOption() {
/* 37 */     if (StringUtil.toLowerCase(name()) == null) $$$reportNull$$$0(2);  return StringUtil.toLowerCase(name());
/*    */   }
/*    */ }


/* Location:              C:\Program Files\JetBrains\WebStorm 2021.1\plugins\NodeJS\lib\NodeJS.jar!\com\jetbrains\nodejs\boilerplate\express\ExpressTemplateEngine.class
 * Java compiler version: 11 (55.0)
 * JD-Core Version:       1.1.3
 */