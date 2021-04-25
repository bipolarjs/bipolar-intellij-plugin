/*    */ package org.bipolar.execution;
/*    */ @Metadata(mv = {1, 4, 2}, bv = {1, 0, 3}, k = 1, d1 = {"\000\f\n\002\030\002\n\002\020\000\n\002\b\003\030\000 \0032\0020\001:\001\003B\005¢\006\002\020\002¨\006\004"}, d2 = {"Lcom/jetbrains/nodejs/execution/NodeRunConfigurationUsageCollector;", "", "()V", "Companion", "intellij.nodeJS"})
/*    */ public final class NodeRunConfigurationUsageCollector {
/*    */   @NotNull
/*    */   public static final Companion Companion = new Companion(null);
/*    */   
/*    */   @Metadata(mv = {1, 4, 2}, bv = {1, 0, 3}, k = 1, d1 = {"\000$\n\002\030\002\n\002\020\000\n\002\b\002\n\002\020\002\n\000\n\002\030\002\n\000\n\002\020\016\n\000\n\002\030\002\n\000\b\003\030\0002\0020\001B\007\b\002¢\006\002\020\002J\036\020\003\032\0020\0042\006\020\005\032\0020\0062\006\020\007\032\0020\b2\006\020\t\032\0020\n¨\006\013"}, d2 = {"Lcom/jetbrains/nodejs/execution/NodeRunConfigurationUsageCollector$Companion;", "", "()V", "trigger", "", "project", "Lcom/intellij/openapi/project/Project;", "featureId", "", "data", "Lcom/intellij/internal/statistic/eventLog/FeatureUsageData;", "intellij.nodeJS"})
/*    */   public static final class Companion {
/*    */     public final void trigger(@NotNull Project project, @NotNull String featureId, @NotNull FeatureUsageData data) {
/* 10 */       Intrinsics.checkNotNullParameter(project, "project"); Intrinsics.checkNotNullParameter(featureId, "featureId"); Intrinsics.checkNotNullParameter(data, "data"); FUCounterUsageLogger.getInstance().logEvent(project, "nodejs.run.configuration", featureId, data);
/*    */     }
/*    */     
/*    */     private Companion() {}
/*    */   }
/*    */ }


/* Location:              C:\Program Files\JetBrains\WebStorm 2021.1\plugins\NodeJS\lib\NodeJS.jar!\com\jetbrains\nodejs\execution\NodeRunConfigurationUsageCollector.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */