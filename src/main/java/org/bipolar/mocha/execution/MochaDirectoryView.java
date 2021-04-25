/*    */ package org.bipolar.mocha.execution;
/*    */ 
/*    */ import com.intellij.openapi.fileChooser.FileChooserDescriptorFactory;
/*    */ import com.intellij.openapi.project.Project;
/*    */ import com.intellij.openapi.ui.TextFieldWithBrowseButton;
/*    */ import com.intellij.openapi.util.io.FileUtil;
/*    */ import com.intellij.util.ui.FormBuilder;
/*    */ import com.intellij.util.ui.SwingHelper;
/*    */ import com.intellij.webcore.ui.PathShortener;
/*    */ import org.bipolar.NodeJSBundle;
/*    */ import javax.swing.JCheckBox;
/*    */ import javax.swing.JComponent;
/*    */ import javax.swing.JPanel;
/*    */ import org.jetbrains.annotations.NotNull;
/*    */ 
/*    */ public class MochaDirectoryView
/*    */   extends MochaTestKindView {
/*    */   private final TextFieldWithBrowseButton myTestDirTextFieldWithBrowseButton;
/*    */   
/*    */   public MochaDirectoryView(@NotNull Project project) {
/* 21 */     this.myTestDirTextFieldWithBrowseButton = createTestDirPathTextField(project);
/* 22 */     PathShortener.enablePathShortening(this.myTestDirTextFieldWithBrowseButton.getTextField(), null);
/* 23 */     this.myRecursiveCheckBox = new JCheckBox(NodeJSBundle.message("rc.mocha.testRunScope.allInDirectory.includeSubdirectories", new Object[0]));
/* 24 */     this
/*    */ 
/*    */ 
/*    */ 
/*    */       
/* 29 */       .myPanel = (new FormBuilder()).setAlignLabelOnRight(false).addLabeledComponent(NodeJSBundle.message("rc.mocha.testRunScope.allInDirectory.testDirectory.label", new Object[0]), (JComponent)this.myTestDirTextFieldWithBrowseButton).addLabeledComponent("", this.myRecursiveCheckBox).getPanel();
/*    */   }
/*    */   private final JCheckBox myRecursiveCheckBox; private final JPanel myPanel;
/*    */   @NotNull
/*    */   private static TextFieldWithBrowseButton createTestDirPathTextField(@NotNull Project project) {
/* 34 */     if (project == null) $$$reportNull$$$0(1);  TextFieldWithBrowseButton textFieldWithBrowseButton = new TextFieldWithBrowseButton();
/* 35 */     SwingHelper.installFileCompletionAndBrowseDialog(project, textFieldWithBrowseButton, 
/*    */ 
/*    */         
/* 38 */         NodeJSBundle.message("rc.mocha.testRunScope.allInDirectory.testDirectory.browseDialogTitle", new Object[0]), 
/* 39 */         FileChooserDescriptorFactory.createSingleFolderDescriptor());
/*    */     
/* 41 */     if (textFieldWithBrowseButton == null) $$$reportNull$$$0(2);  return textFieldWithBrowseButton;
/*    */   }
/*    */ 
/*    */   
/*    */   @NotNull
/*    */   public JComponent getComponent() {
/* 47 */     if (this.myPanel == null) $$$reportNull$$$0(3);  return this.myPanel;
/*    */   }
/*    */ 
/*    */   
/*    */   public void resetFrom(@NotNull MochaRunSettings settings) {
/* 52 */     if (settings == null) $$$reportNull$$$0(4);  this.myTestDirTextFieldWithBrowseButton.setText(FileUtil.toSystemDependentName(settings.getTestDirPath()));
/* 53 */     this.myRecursiveCheckBox.setSelected(settings.isRecursive());
/*    */   }
/*    */ 
/*    */   
/*    */   public void applyTo(@NotNull MochaRunSettings.Builder builder) {
/* 58 */     if (builder == null) $$$reportNull$$$0(5);  builder.setTestDirPath(PathShortener.getAbsolutePath(this.myTestDirTextFieldWithBrowseButton.getTextField()));
/* 59 */     builder.setRecursive(this.myRecursiveCheckBox.isSelected());
/*    */   }
/*    */ }


/* Location:              C:\Program Files\JetBrains\WebStorm 2021.1\plugins\NodeJS\lib\NodeJS.jar!\com\jetbrains\nodejs\mocha\execution\MochaDirectoryView.class
 * Java compiler version: 11 (55.0)
 * JD-Core Version:       1.1.3
 */