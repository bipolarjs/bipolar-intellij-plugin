/*    */ package org.bipolar.run;
/*    */ 
/*    */ import com.intellij.javascript.nodejs.interpreter.NodeJsInterpreterRef;
/*    */ import com.intellij.util.xmlb.Converter;
/*    */ import kotlin.Metadata;
/*    */ import kotlin.jvm.internal.Intrinsics;
/*    */ import org.jetbrains.annotations.NotNull;
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
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ @Metadata(mv = {1, 4, 2}, bv = {1, 0, 3}, k = 1, d1 = {"\000\030\n\002\030\002\n\002\030\002\n\002\030\002\n\002\b\003\n\002\020\016\n\002\b\003\b\000\030\0002\b\022\004\022\0020\0020\001B\005¢\006\002\020\003J\020\020\004\032\0020\0022\006\020\005\032\0020\006H\026J\020\020\007\032\0020\0062\006\020\b\032\0020\002H\026¨\006\t"}, d2 = {"Lcom/jetbrains/nodejs/run/NodeJsInterpreterRefConverter;", "Lcom/intellij/util/xmlb/Converter;", "Lcom/intellij/javascript/nodejs/interpreter/NodeJsInterpreterRef;", "()V", "fromString", "value", "", "toString", "t", "intellij.nodeJS"})
/*    */ public final class NodeJsInterpreterRefConverter
/*    */   extends Converter<NodeJsInterpreterRef>
/*    */ {
/*    */   @NotNull
/*    */   public NodeJsInterpreterRef fromString(@NotNull String value) {
/* 37 */     Intrinsics.checkNotNullParameter(value, "value"); Intrinsics.checkNotNullExpressionValue(NodeJsInterpreterRef.create(value), "NodeJsInterpreterRef.create(value)"); return NodeJsInterpreterRef.create(value); } @NotNull
/*    */   public String toString(@NotNull NodeJsInterpreterRef t) {
/* 39 */     Intrinsics.checkNotNullParameter(t, "t"); Intrinsics.checkNotNullExpressionValue(t.getReferenceName(), "t.referenceName"); return t.getReferenceName();
/*    */   }
/*    */ }


/* Location:              C:\Program Files\JetBrains\WebStorm 2021.1\plugins\NodeJS\lib\NodeJS.jar!\com\jetbrains\nodejs\run\NodeJsInterpreterRefConverter.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */