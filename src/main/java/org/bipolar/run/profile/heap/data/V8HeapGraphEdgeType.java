/*    */ package org.bipolar.run.profile.heap.data;
/*    */ 
/*    */ import java.util.HashMap;
/*    */ import java.util.Map;
/*    */ import org.jetbrains.annotations.Nullable;
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
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public enum V8HeapGraphEdgeType
/*    */ {
/* 27 */   kContextVariable(0, "context variable", "A variable from a function context"),
/* 28 */   kElement(1, "element", "An element of an array"),
/* 29 */   kProperty(2, "property", "A named object property"),
/* 30 */   kInternal(3, "internal", "A link that can't be accessed from JS, thus, its name isn't a real property name (e.g. parts of a ConsString)"),
/* 31 */   kHidden(4, "hidden", "A link that is needed for proper sizes calculation, but may be hidden from user"),
/* 32 */   kShortcut(5, "shortcut", "A link that must not be followed during sizes calculation"),
/* 33 */   kWeak(6, "weak", "A weak reference (ignored by the GC)"),
/* 34 */   kInvisible(7, "invisible", "Invisible");
/*    */   private final int myNumber;
/*    */   private final String myName;
/*    */   private final String myDescription;
/*    */   
/*    */   private static class Inner
/*    */   {
/* 41 */     private static final Map<Integer, V8HeapGraphEdgeType> ourMap = new HashMap<>();
/*    */   }
/*    */   
/*    */   V8HeapGraphEdgeType(int number, String name, String description) {
/* 45 */     this.myNumber = number;
/* 46 */     this.myName = name;
/* 47 */     this.myDescription = description;
/* 48 */     Inner.ourMap.put(Integer.valueOf(number), this);
/*    */   }
/*    */   
/*    */   @Nullable
/*    */   public static V8HeapGraphEdgeType getByNumber(int number) {
/* 53 */     return Inner.ourMap.get(Integer.valueOf(number));
/*    */   }
/*    */   
/*    */   public int getNumber() {
/* 57 */     return this.myNumber;
/*    */   }
/*    */   
/*    */   public String getName() {
/* 61 */     return this.myName;
/*    */   }
/*    */   
/*    */   public String getDescription() {
/* 65 */     return this.myDescription;
/*    */   }
/*    */   
/*    */   public static boolean isInternalKind(V8HeapGraphEdgeType type) {
/* 69 */     return (kHidden.equals(type) || kInvisible.equals(type) || kInternal.equals(type) || kWeak.equals(type));
/*    */   }
/*    */ }


/* Location:              C:\Program Files\JetBrains\WebStorm 2021.1\plugins\NodeJS\lib\NodeJS.jar!\com\jetbrains\nodejs\run\profile\heap\data\V8HeapGraphEdgeType.class
 * Java compiler version: 11 (55.0)
 * JD-Core Version:       1.1.3
 */