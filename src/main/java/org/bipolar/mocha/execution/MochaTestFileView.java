/*    */ package org.bipolar.mocha.execution;
/*    */ 
/*    */ import com.intellij.lang.javascript.JavaScriptBundle;
/*    */ import com.intellij.openapi.fileChooser.FileChooserDescriptorFactory;
/*    */ import com.intellij.openapi.project.Project;
/*    */ import com.intellij.openapi.ui.TextFieldWithBrowseButton;
/*    */ import com.intellij.openapi.util.io.FileUtil;
/*    */ import com.intellij.util.ui.FormBuilder;
/*    */ import com.intellij.util.ui.SwingHelper;
/*    */ import com.intellij.webcore.ui.PathShortener;
/*    */ import javax.swing.JComponent;
/*    */ import org.jetbrains.annotations.NotNull;
/*    */ 
/*    */ public class MochaTestFileView
/*    */   extends MochaTestKindView
/*    */ {
/*    */   private final TextFieldWithBrowseButton myTestFileTextFieldWithBrowseButton;
/*    */   private final FormBuilder myFormBuilder;
/*    */   
/*    */   public MochaTestFileView(@NotNull Project project) {
/* 21 */     this.myTestFileTextFieldWithBrowseButton = new TextFieldWithBrowseButton();
/* 22 */     PathShortener.enablePathShortening(this.myTestFileTextFieldWithBrowseButton.getTextField(), null);
/* 23 */     SwingHelper.installFileCompletionAndBrowseDialog(project, this.myTestFileTextFieldWithBrowseButton, 
/*    */ 
/*    */         
/* 26 */         JavaScriptBundle.message("rc.testRunScope.testFile.browseTitle", new Object[0]), 
/* 27 */         FileChooserDescriptorFactory.createSingleFileDescriptor());
/*    */     
/* 29 */     this
/*    */       
/* 31 */       .myFormBuilder = (new FormBuilder()).setAlignLabelOnRight(false).addLabeledComponent(JavaScriptBundle.message("rc.testRunScope.testFile.label", new Object[0]), (JComponent)this.myTestFileTextFieldWithBrowseButton);
/*    */   }
/*    */ 
/*    */   
/*    */   @NotNull
/*    */   public JComponent getComponent() {
/* 37 */     if (this.myFormBuilder.getPanel() == null) $$$reportNull$$$0(1);  return this.myFormBuilder.getPanel();
/*    */   }
/*    */ 
/*    */   
/*    */   public void resetFrom(@NotNull MochaRunSettings settings) {
/* 42 */     if (settings == null) $$$reportNull$$$0(2);  this.myTestFileTextFieldWithBrowseButton.setText(FileUtil.toSystemDependentName(settings.getTestFilePath()));
/*    */   }
/*    */ 
/*    */   
/*    */   public void applyTo(@NotNull MochaRunSettings.Builder builder) {
/* 47 */     if (builder == null) $$$reportNull$$$0(3);  builder.setTestFilePath(PathShortener.getAbsolutePath(this.myTestFileTextFieldWithBrowseButton.getTextField()));
/*    */   }
/*    */   
/*    */   @NotNull
/*    */   FormBuilder getFormBuilder() {
/* 52 */     if (this.myFormBuilder == null) $$$reportNull$$$0(4);  return this.myFormBuilder;
/*    */   }
/*    */ }


/* Location:              C:\Program Files\JetBrains\WebStorm 2021.1\plugins\NodeJS\lib\NodeJS.jar!\com\jetbrains\nodejs\mocha\execution\MochaTestFileView.class
 * Java compiler version: 11 (55.0)
 * JD-Core Version:       1.1.3
 */