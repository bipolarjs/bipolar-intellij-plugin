/*    */ package org.bipolar.run;
/*    */ 
/*    */

/*    */ import com.intellij.openapi.project.Project;
/*    */
        /*    */
        /*    */ import kotlin.Metadata;
/*    */ import org.jetbrains.annotations.NotNull;
/*    */ 
/*    */ @Metadata(mv = {1, 4, 2}, bv = {1, 0, 3}, k = 1, d1 = {"\0006\n\002\030\002\n\002\030\002\n\002\030\002\n\002\b\002\n\002\030\002\n\000\n\002\030\002\n\000\n\002\020\016\n\000\n\002\030\002\n\002\030\002\n\002\b\002\n\002\020\013\n\002\b\002\030\000 \0202\0020\0012\0020\002:\001\020B\005¢\006\002\020\003J\020\020\004\032\0020\0052\006\020\006\032\0020\007H\026J\b\020\b\032\0020\tH\026J\022\020\n\032\f\022\006\b\001\022\0020\f\030\0010\013H\026J\b\020\r\032\0020\tH\026J\b\020\016\032\0020\017H\026¨\006\021"}, d2 = {"Lcom/jetbrains/nodejs/run/NodeJsRunConfigurationType;", "Lcom/intellij/execution/configurations/SimpleConfigurationType;", "Lcom/intellij/openapi/project/DumbAware;", "()V", "createTemplateConfiguration", "Lcom/jetbrains/nodejs/run/NodeJsRunConfiguration;", "project", "Lcom/intellij/openapi/project/Project;", "getHelpTopic", "", "getOptionsClass", "Ljava/lang/Class;", "Lcom/intellij/openapi/components/BaseState;", "getTag", "isEditableInDumbMode", "", "Companion", "intellij.nodeJS"})
/*    */ public final class NodeJsRunConfigurationType extends SimpleConfigurationType implements DumbAware {
/* 12 */   public NodeJsRunConfigurationType() { super("NodeJSConfigurationType", 
/* 13 */         "Node.js", 
/* 14 */         null, 
/* 15 */         NotNullLazyValue.createValue(null.INSTANCE)); } @NotNull
/*    */   public static final Companion Companion = new Companion(null); @Metadata(mv = {1, 4, 2}, bv = {1, 0, 3}, k = 1, d1 = {"\000\022\n\002\030\002\n\002\020\000\n\002\b\002\n\002\030\002\n\000\b\003\030\0002\0020\001B\007\b\002¢\006\002\020\002J\b\020\003\032\0020\004H\007¨\006\005"}, d2 = {"Lcom/jetbrains/nodejs/run/NodeJsRunConfigurationType$Companion;", "", "()V", "getInstance", "Lcom/jetbrains/nodejs/run/NodeJsRunConfigurationType;", "intellij.nodeJS"})
/*    */   public static final class Companion { private Companion() {} @JvmStatic
/*    */     @NotNull
/* 19 */     public final NodeJsRunConfigurationType getInstance() { int $i$f$runConfigurationType = 0; return 
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
/* 33 */         (NodeJsRunConfigurationType)ConfigurationTypeUtil.findConfigurationType(NodeJsRunConfigurationType.class); }
/*    */      }
/*    */ 
/*    */   
/*    */   @NotNull
/*    */   public String getTag() {
/*    */     return "nodeJs";
/*    */   }
/*    */   
/*    */   @NotNull
/*    */   public NodeJsRunConfiguration createTemplateConfiguration(@NotNull Project project) {
/*    */     Intrinsics.checkNotNullParameter(project, "project");
/*    */     return new NodeJsRunConfiguration(project, (ConfigurationFactory)this, null);
/*    */   }
/*    */   
/*    */   @Nullable
/*    */   public Class<? extends BaseState> getOptionsClass() {
/*    */     return (Class)NodeJsRunConfigurationState.class;
/*    */   }
/*    */   
/*    */   @NotNull
/*    */   public String getHelpTopic() {
/*    */     return "reference.dialogs.rundebug.NodeJSConfigurationType";
/*    */   }
/*    */   
/*    */   public boolean isEditableInDumbMode() {
/*    */     return true;
/*    */   }
/*    */   
/*    */   @JvmStatic
/*    */   @NotNull
/*    */   public static final NodeJsRunConfigurationType getInstance() {
/*    */     return Companion.getInstance();
/*    */   }
/*    */ }


/* Location:              C:\Program Files\JetBrains\WebStorm 2021.1\plugins\NodeJS\lib\NodeJS.jar!\com\jetbrains\nodejs\run\NodeJsRunConfigurationType.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */