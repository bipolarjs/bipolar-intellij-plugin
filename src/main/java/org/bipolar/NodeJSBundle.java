/*    */ package org.bipolar;
/*    */ 
/*    */ import com.intellij.DynamicBundle;
/*    */ import java.util.function.Supplier;
/*    */ import org.jetbrains.annotations.Nls;
/*    */ import org.jetbrains.annotations.NonNls;
/*    */ import org.jetbrains.annotations.NotNull;
/*    */ import org.jetbrains.annotations.PropertyKey;
/*    */ 
/*    */ public final class NodeJSBundle extends DynamicBundle {
/*    */   @NonNls
/*    */   private static final String BUNDLE = "messages.NodeJSBundle";
/* 13 */   public static final NodeJSBundle INSTANCE = new NodeJSBundle();
/*    */   private NodeJSBundle() {
/* 15 */     super("messages.NodeJSBundle");
/*    */   } @NotNull
/*    */   @Nls
/*    */   public static String message(@NotNull @PropertyKey(resourceBundle = "messages.NodeJSBundle") String key, Object... params) {
/* 19 */     if (key == null) $$$reportNull$$$0(0);  if (params == null) $$$reportNull$$$0(1);  if (INSTANCE.getMessage(key, params) == null) $$$reportNull$$$0(2);  return INSTANCE.getMessage(key, params);
/*    */   }
/*    */   
/*    */   @NotNull
/*    */   public static Supplier<String> messagePointer(@NotNull @PropertyKey(resourceBundle = "messages.NodeJSBundle") String key, Object... params) {
/* 24 */     if (key == null) $$$reportNull$$$0(3);  if (params == null) $$$reportNull$$$0(4);  if (INSTANCE.getLazyMessage(key, params) == null) $$$reportNull$$$0(5);  return INSTANCE.getLazyMessage(key, params);
/*    */   }
/*    */ }


/* Location:              C:\Program Files\JetBrains\WebStorm 2021.1\plugins\NodeJS\lib\NodeJS.jar!\com\jetbrains\nodejs\NodeJSBundle.class
 * Java compiler version: 11 (55.0)
 * JD-Core Version:       1.1.3
 */