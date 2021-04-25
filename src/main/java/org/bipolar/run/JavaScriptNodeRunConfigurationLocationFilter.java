/*    */ package org.bipolar.run;
/*    */ import com.intellij.lang.javascript.DialectDetector;
/*    */ import com.intellij.lang.javascript.JavaScriptFileType;
/*    */ import com.intellij.util.containers.ContainerUtil;
/*    */ import kotlin.collections.CollectionsKt;
/*    */ import kotlin.jvm.internal.Intrinsics;
/*    */ 
/*    */ @Metadata(mv = {1, 4, 2}, bv = {1, 0, 3}, k = 1, d1 = {"\000\030\n\002\030\002\n\002\030\002\n\002\b\002\n\002\020\036\n\002\030\002\n\002\b\003\b\002\030\0002\0020\001B\005¢\006\002\020\002R\032\020\003\032\b\022\004\022\0020\0050\0048VX\004¢\006\006\032\004\b\006\020\007¨\006\b"}, d2 = {"Lcom/jetbrains/nodejs/run/JavaScriptNodeRunConfigurationLocationFilter;", "Lcom/intellij/javascript/nodejs/execution/NodeRunConfigurationLocationFilter;", "()V", "fileTypes", "", "Lcom/intellij/openapi/fileTypes/FileType;", "getFileTypes", "()Ljava/util/Collection;", "intellij.nodeJS"})
/*    */ final class JavaScriptNodeRunConfigurationLocationFilter extends NodeRunConfigurationLocationFilter {
/*    */   @NotNull
/*    */   public Collection<FileType> getFileTypes() {
/* 12 */     Intrinsics.checkNotNullExpressionValue(DialectDetector.JAVASCRIPT_FILE_TYPES, "DialectDetector.JAVASCRIPT_FILE_TYPES"); Intrinsics.checkNotNullExpressionValue(ContainerUtil.concat(CollectionsKt.listOf(JavaScriptFileType.INSTANCE), CollectionsKt.toList(DialectDetector.JAVASCRIPT_FILE_TYPES)), "ContainerUtil.concat(lis…RIPT_FILE_TYPES.toList())"); return ContainerUtil.concat(CollectionsKt.listOf(JavaScriptFileType.INSTANCE), CollectionsKt.toList(DialectDetector.JAVASCRIPT_FILE_TYPES));
/*    */   }
/*    */ }


/* Location:              C:\Program Files\JetBrains\WebStorm 2021.1\plugins\NodeJS\lib\NodeJS.jar!\com\jetbrains\nodejs\run\JavaScriptNodeRunConfigurationLocationFilter.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */