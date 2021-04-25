/*    */ package org.bipolar.run.profile.heap.data;
/*    */ 
/*    */ import org.bipolar.NodeJSBundle;
/*    */ import java.util.HashMap;
/*    */ import java.util.Map;
/*    */ import java.util.function.Supplier;
/*    */
import org.jetbrains.annotations.Nls;
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
/*    */ public enum V8HeapNodeType
/*    */ {
/*    */   private final int myNumber;
/*    */   @Nls
/*    */   private final String myName;
/* 33 */   kHidden(0, "hidden",NodeJSBundle.messagePointer("profile.cpu.node_type.hidden.description", new Object[0])),
/* 34 */   kArray(1, "array", NodeJSBundle.messagePointer("profile.cpu.node_type.array.description", new Object[0])),
/* 35 */   kString(2, "string", NodeJSBundle.messagePointer("profile.cpu.node_type.string.description", new Object[0])),
/* 36 */   kObject(3, "object", NodeJSBundle.messagePointer("profile.cpu.node_type.object.description", new Object[0])),
/* 37 */   kCode(4, "compiled code", NodeJSBundle.messagePointer("profile.cpu.node_type.compiled_code.description", new Object[0])),
/* 38 */   kClosure(5, "closure", NodeJSBundle.messagePointer("profile.cpu.node_type.closure.description", new Object[0])),
/* 39 */   kRegExp(6, "regexp", NodeJSBundle.messagePointer("profile.cpu.node_type.regexp.description", new Object[0])),
/* 40 */   kHeapNumber(7, "number", NodeJSBundle.messagePointer("profile.cpu.node_type.number.description", new Object[0])),
/* 41 */   kNative(8, "native", NodeJSBundle.messagePointer("profile.cpu.node_type.native.description", new Object[0])),
/* 42 */   kSynthetic(9, "synthetic", NodeJSBundle.messagePointer("profile.cpu.node_type.synthetic.description", new Object[0])),
/* 43 */   kConsString(10, "concatenated string", NodeJSBundle.messagePointer("profile.cpu.node_type.concatenated_string.description", new Object[0])),
/* 44 */   kSlicedString(11, "sliced string", NodeJSBundle.messagePointer("profile.cpu.node_type.sliced_string.description", new Object[0])),
/* 45 */   kSymbol(12, "Symbol", NodeJSBundle.messagePointer("profile.cpu.node_type.symbol.description", new Object[0])),
/* 46 */   kBigInt(13, "BigInt", NodeJSBundle.messagePointer("profile.cpu.node_type.bigint.description", new Object[0]));
/*    */   private final Supplier<String> myDescriptionSupplier;
/*    */   
/*    */   private static class Inner {
/* 50 */     private static final Map<Integer, V8HeapNodeType> ourMap = new HashMap<>();
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   V8HeapNodeType(@Nls int number, String name, Supplier<String> descriptionSupplier) {
/* 58 */     this.myNumber = number;
/* 59 */     this.myName = name;
/* 60 */     this.myDescriptionSupplier = descriptionSupplier;
/* 61 */     Inner.ourMap.put(Integer.valueOf(number), this);
/*    */   }
/*    */   
/*    */   @Nullable
/*    */   public static V8HeapNodeType getByNumber(int number) {
/* 66 */     return Inner.ourMap.get(Integer.valueOf(number));
/*    */   }
/*    */   
/*    */   public int getNumber() {
/* 70 */     return this.myNumber;
/*    */   }
/*    */   @Nls
/*    */   public String getName() {
/* 74 */     return this.myName;
/*    */   }
/*    */   @Nls
/*    */   public String getDescription() {
/* 78 */     return this.myDescriptionSupplier.get();
/*    */   }
/*    */   
/*    */   public boolean isStringType() {
/* 82 */     return (this == kConsString || this == kSlicedString || this == kString);
/*    */   }
/*    */ }


/* Location:              C:\Program Files\JetBrains\WebStorm 2021.1\plugins\NodeJS\lib\NodeJS.jar!\com\jetbrains\nodejs\run\profile\heap\data\V8HeapNodeType.class
 * Java compiler version: 11 (55.0)
 * JD-Core Version:       1.1.3
 */