/*    */ package org.bipolar.nodeunit.execution;
/*    */ 
/*    */ import com.intellij.execution.configurations.ConfigurationFactory;
/*    */ import com.intellij.execution.configurations.ConfigurationType;
/*    */ import com.intellij.execution.configurations.ConfigurationTypeBase;
/*    */ import com.intellij.execution.configurations.RunConfiguration;
/*    */ import com.intellij.openapi.project.DumbAware;
/*    */ import com.intellij.openapi.project.Project;
/*    */ import com.intellij.openapi.util.NlsSafe;
/*    */ import com.intellij.openapi.util.NotNullLazyValue;
/*    */ import icons.NodeJSIcons;
/*    */
/*    */ import org.jetbrains.annotations.NotNull;
/*    */ 
/*    */ public final class NodeunitRunConfigurationType extends ConfigurationTypeBase implements DumbAware {
/*    */   public NodeunitRunConfigurationType() {
/* 17 */     super("NodeunitConfigurationType", "Nodeunit", null, NotNullLazyValue.createValue(() -> NodeJSIcons.Nodeunit));
/* 18 */     addFactory(new ConfigurationFactory((ConfigurationType)this)
/*    */         {
/*    */           @NotNull
/*    */           public RunConfiguration createTemplateConfiguration(@NotNull Project project) {
/* 22 */             if (project == null) $$$reportNull$$$0(0);  return (RunConfiguration)new NodeunitRunConfiguration(project, this, "Nodeunit");
/*    */           }
/*    */           
/*    */           @NotNull
/*    */           public String getId() {
/* 27 */             return "Nodeunit";
/*    */           }
/*    */ 
/*    */           
/*    */           public boolean isEditableInDumbMode() {
/* 32 */             return true;
/*    */           }
/*    */         });
/*    */   }
/*    */ 
/*    */   
/*    */   public String getHelpTopic() {
/* 39 */     return "reference.dialogs.rundebug.NodeunitConfigurationType";
/*    */   }
/*    */   
/*    */   @NlsSafe
/*    */   private static final String NAME = "Nodeunit";
/*    */ }


/* Location:              C:\Program Files\JetBrains\WebStorm 2021.1\plugins\NodeJS\lib\NodeJS.jar!\com\jetbrains\nodejs\nodeunit\execution\NodeunitRunConfigurationType.class
 * Java compiler version: 11 (55.0)
 * JD-Core Version:       1.1.3
 */