/*    */ package org.bipolar.doc;
/*    */ 
/*    */ import java.util.List;
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public class ModuleDoc
/*    */ {
/*    */   private final String myName;
/*    */   private final String myTextRaw;
/*    */   private final Integer myStability;
/*    */   
/*    */   public ModuleDoc(String name, String textRaw, Integer stability, String text, String description, List<ModulePropertyDoc> properties, List<ModuleMethodDoc> methods) {
/* 21 */     this.myName = name;
/* 22 */     this.myTextRaw = textRaw;
/* 23 */     this.myStability = stability;
/*    */   }
/*    */ }


/* Location:              C:\Program Files\JetBrains\WebStorm 2021.1\plugins\NodeJS\lib\NodeJS.jar!\com\jetbrains\nodejs\doc\ModuleDoc.class
 * Java compiler version: 11 (55.0)
 * JD-Core Version:       1.1.3
 */