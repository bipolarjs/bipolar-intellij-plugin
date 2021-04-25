/*    */ package org.bipolar.nodeunit.execution.ui;
/*    */ 
/*    */ import com.intellij.openapi.options.ConfigurationException;
/*    */ import com.intellij.openapi.options.SettingsEditor;
/*    */ import com.intellij.openapi.project.Project;
/*    */ import org.bipolar.nodeunit.execution.NodeunitRunConfiguration;
/*    */ import org.bipolar.nodeunit.execution.NodeunitSettings;
/*    */ import javax.swing.JComponent;
/*    */
import org.jetbrains.annotations.NotNull;
/*    */ 
/*    */ public class NodeunitSettingsEditor
/*    */   extends SettingsEditor<NodeunitRunConfiguration> {
/*    */   @NotNull
/*    */   private final RootSettingsController myRoot;
/*    */   
/*    */   public NodeunitSettingsEditor(@NotNull Project project) {
/* 17 */     this.myRoot = new RootSettingsController(new Context(project));
/*    */   }
/*    */ 
/*    */   
/*    */   protected void resetEditorFrom(@NotNull NodeunitRunConfiguration runConfiguration) {
/* 22 */     if (runConfiguration == null) $$$reportNull$$$0(1);  NodeunitSettings settings = runConfiguration.getSettings();
/* 23 */     this.myRoot.resetFrom(settings);
/*    */   }
/*    */ 
/*    */   
/*    */   protected void applyEditorTo(@NotNull NodeunitRunConfiguration runConfiguration) throws ConfigurationException {
/* 28 */     if (runConfiguration == null) $$$reportNull$$$0(2);  NodeunitSettings.Builder builder = new NodeunitSettings.Builder();
/* 29 */     this.myRoot.applyTo(builder);
/* 30 */     runConfiguration.setSettings(builder.build());
/*    */   }
/*    */   
/*    */   @NotNull
/*    */   protected JComponent createEditor() {
/* 35 */     if (this.myRoot.getComponent() == null) $$$reportNull$$$0(3);  return this.myRoot.getComponent();
/*    */   }
/*    */ }


/* Location:              C:\Program Files\JetBrains\WebStorm 2021.1\plugins\NodeJS\lib\NodeJS.jar!\com\jetbrains\nodejs\nodeunit\executio\\ui\NodeunitSettingsEditor.class
 * Java compiler version: 11 (55.0)
 * JD-Core Version:       1.1.3
 */