/*    */ package org.bipolar.run;
/*    */ 
/*    */ import com.intellij.openapi.project.Project;
/*    */ import com.intellij.util.PathUtil;
/*    */ import com.intellij.util.text.StringKt;
/*    */ import kotlin.Metadata;
/*    */ import kotlin.jvm.internal.Intrinsics;
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
/*    */ 
/*    */ 
/*    */ @Metadata(mv = {1, 4, 2}, bv = {1, 0, 3}, k = 2, d1 = {"\000\020\n\000\n\002\020\016\n\002\b\002\n\002\030\002\n\000\032\034\020\000\032\004\030\0010\0012\b\020\002\032\004\030\0010\0012\006\020\003\032\0020\004H\000¨\006\005"}, d2 = {"normalizeWorkingDirToStore", "", "value", "project", "Lcom/intellij/openapi/project/Project;", "intellij.nodeJS"})
/*    */ public final class NodeJsRunConfigurationStateKt
/*    */ {
/*    */   @Nullable
/*    */   public static final String normalizeWorkingDirToStore(@Nullable String value, @NotNull Project project) {
/* 25 */     Intrinsics.checkNotNullParameter(project, "project"); String v = PathUtil.toSystemIndependentName(StringKt.nullize$default(value, false, 1, null));
/*    */     
/* 27 */     if (v != null && Intrinsics.areEqual(v, project.getBasePath())) {
/* 28 */       return null;
/*    */     }
/*    */     
/* 31 */     return v;
/*    */   }
/*    */ }


/* Location:              C:\Program Files\JetBrains\WebStorm 2021.1\plugins\NodeJS\lib\NodeJS.jar!\com\jetbrains\nodejs\run\NodeJsRunConfigurationStateKt.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */