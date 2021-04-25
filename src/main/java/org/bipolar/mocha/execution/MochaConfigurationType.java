/*    */ package org.bipolar.mocha.execution;
/*    */ import com.intellij.execution.configurations.ConfigurationFactory;
/*    */ import com.intellij.execution.configurations.ConfigurationTypeUtil;
/*    */ import com.intellij.execution.configurations.RunConfiguration;
/*    */ import com.intellij.execution.configurations.RunConfigurationSingletonPolicy;
/*    */ import com.intellij.execution.configurations.SimpleConfigurationType;
/*    */ import com.intellij.openapi.project.DumbAware;
/*    */ import com.intellij.openapi.project.Project;
/*    */ import com.intellij.openapi.util.NotNullLazyValue;
/*    */ import org.bipolar.NodeJSBundle;
/*    */ import icons.NodeJSIcons;
/*    */ import org.jetbrains.annotations.NotNull;
/*    */ 
/*    */ public final class MochaConfigurationType extends SimpleConfigurationType implements DumbAware {
/*    */   public MochaConfigurationType() {
/* 16 */     super("mocha-javascript-test-runner", NodeJSBundle.message("rc.mocha.run_configuration_type.name", new Object[0]), null, NotNullLazyValue.createValue(() -> NodeJSIcons.Mocha));
/*    */   }
/*    */ 
/*    */   
/*    */   @NotNull
/*    */   public String getTag() {
/* 22 */     return "mocha";
/*    */   }
/*    */ 
/*    */   
/*    */   public String getHelpTopic() {
/* 27 */     return "reference.dialogs.rundebug.mocha-javascript-test-runner";
/*    */   }
/*    */ 
/*    */   
/*    */   @NotNull
/*    */   public RunConfiguration createTemplateConfiguration(@NotNull Project project) {
/* 33 */     if (project == null) $$$reportNull$$$0(0);  return (RunConfiguration)new MochaRunConfiguration(project, (ConfigurationFactory)this, null);
/*    */   }
/*    */ 
/*    */   
/*    */   @NotNull
/*    */   public RunConfigurationSingletonPolicy getSingletonPolicy() {
/* 39 */     if (RunConfigurationSingletonPolicy.SINGLE_INSTANCE_ONLY == null) $$$reportNull$$$0(1);  return RunConfigurationSingletonPolicy.SINGLE_INSTANCE_ONLY;
/*    */   }
/*    */ 
/*    */   
/*    */   public boolean isEditableInDumbMode() {
/* 44 */     return true;
/*    */   }
/*    */   
/*    */   @NotNull
/*    */   public static MochaConfigurationType getInstance() {
/* 49 */     if ((MochaConfigurationType)ConfigurationTypeUtil.findConfigurationType(MochaConfigurationType.class) == null) $$$reportNull$$$0(2);  return (MochaConfigurationType)ConfigurationTypeUtil.findConfigurationType(MochaConfigurationType.class);
/*    */   }
/*    */ }


/* Location:              C:\Program Files\JetBrains\WebStorm 2021.1\plugins\NodeJS\lib\NodeJS.jar!\com\jetbrains\nodejs\mocha\execution\MochaConfigurationType.class
 * Java compiler version: 11 (55.0)
 * JD-Core Version:       1.1.3
 */