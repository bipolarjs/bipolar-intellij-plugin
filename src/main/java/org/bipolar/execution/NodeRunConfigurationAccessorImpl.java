/*    */ package org.bipolar.execution;
/*    */ 
/*    */ import com.intellij.javascript.nodejs.execution.NodeRunConfigurationAccessor;
/*    */ import com.intellij.openapi.project.Project;
/*    */ import org.bipolar.run.NodeJsRunConfiguration;
/*    */ import org.jetbrains.annotations.NotNull;
/*    */ import org.jetbrains.annotations.Nullable;
/*    */ 
/*    */ public class NodeRunConfigurationAccessorImpl
/*    */   extends NodeRunConfigurationAccessor {
/*    */   private final Project myProject;
/*    */   
/*    */   public NodeRunConfigurationAccessorImpl(@NotNull Project project) {
/* 14 */     this.myProject = project;
/*    */   }
/*    */ 
/*    */   
/*    */   @Nullable
/*    */   public String getTemplateNodeParameters() {
/* 20 */     NodeJsRunConfiguration configuration = NodeJsRunConfiguration.getDefaultRunConfiguration(this.myProject);
/* 21 */     return (configuration != null) ? configuration.getProgramParameters() : null;
/*    */   }
/*    */ }


/* Location:              C:\Program Files\JetBrains\WebStorm 2021.1\plugins\NodeJS\lib\NodeJS.jar!\com\jetbrains\nodejs\execution\NodeRunConfigurationAccessorImpl.class
 * Java compiler version: 11 (55.0)
 * JD-Core Version:       1.1.3
 */