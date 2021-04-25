/*    */ package org.bipolar.mocha.execution;
/*    */ 
/*    */ import com.intellij.openapi.Disposable;
/*    */ import com.intellij.openapi.fileChooser.FileChooserDescriptorFactory;
/*    */ import com.intellij.openapi.fileChooser.FileChooserFactory;
/*    */ import com.intellij.openapi.project.Project;
/*    */ import com.intellij.ui.RawCommandLineEditor;
/*    */ import com.intellij.ui.components.JBTextField;
/*    */ import com.intellij.util.ObjectUtils;
/*    */ import com.intellij.util.ui.FormBuilder;
/*    */ import org.bipolar.NodeJSBundle;
/*    */ import javax.swing.JComponent;
/*    */ import javax.swing.JPanel;
/*    */ import org.jetbrains.annotations.NotNull;
/*    */ import org.jetbrains.annotations.Nullable;
/*    */ 
/*    */ public class MochaPatternView extends MochaTestKindView {
/*    */   private final RawCommandLineEditor myPatternComponent;
/*    */   
/*    */   public MochaPatternView(@NotNull Project project) {
/* 21 */     this.myPatternComponent = new RawCommandLineEditor();
/* 22 */     configurePatternEmptyText((JBTextField)ObjectUtils.tryCast(this.myPatternComponent.getTextField(), JBTextField.class));
/* 23 */     FileChooserFactory.getInstance().installFileCompletion(this.myPatternComponent
/* 24 */         .getTextField(), 
/* 25 */         FileChooserDescriptorFactory.createSingleFileOrFolderDescriptor(), true, (Disposable)project);
/*    */ 
/*    */ 
/*    */     
/* 29 */     this
/*    */ 
/*    */       
/* 32 */       .myPanel = (new FormBuilder()).setAlignLabelOnRight(false).addLabeledComponent(NodeJSBundle.message("rc.mocha.testRunScope.filePattern.filePattern.label", new Object[0]), (JComponent)this.myPatternComponent).getPanel();
/*    */   }
/*    */   private final JPanel myPanel;
/*    */   private static void configurePatternEmptyText(@Nullable JBTextField field) {
/* 36 */     if (field != null) {
/* 37 */       field.getEmptyText().setText(NodeJSBundle.message("rc.mocha.testRunScope.filePattern.filePattern.emptyText", new Object[0]));
/*    */     }
/*    */   }
/*    */ 
/*    */   
/*    */   @NotNull
/*    */   public JComponent getComponent() {
/* 44 */     if (this.myPanel == null) $$$reportNull$$$0(1);  return this.myPanel;
/*    */   }
/*    */ 
/*    */   
/*    */   public void resetFrom(@NotNull MochaRunSettings settings) {
/* 49 */     if (settings == null) $$$reportNull$$$0(2);  this.myPatternComponent.setText(settings.getTestFilePattern());
/*    */   }
/*    */ 
/*    */   
/*    */   public void applyTo(@NotNull MochaRunSettings.Builder builder) {
/* 54 */     if (builder == null) $$$reportNull$$$0(3);  builder.setTestFilePattern(this.myPatternComponent.getText());
/*    */   }
/*    */ }


/* Location:              C:\Program Files\JetBrains\WebStorm 2021.1\plugins\NodeJS\lib\NodeJS.jar!\com\jetbrains\nodejs\mocha\execution\MochaPatternView.class
 * Java compiler version: 11 (55.0)
 * JD-Core Version:       1.1.3
 */