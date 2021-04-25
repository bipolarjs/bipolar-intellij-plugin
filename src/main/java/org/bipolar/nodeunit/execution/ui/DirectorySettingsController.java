/*    */ package org.bipolar.nodeunit.execution.ui;
/*    */ import com.intellij.openapi.fileChooser.FileChooserDescriptor;
/*    */ import com.intellij.openapi.fileChooser.FileChooserDescriptorFactory;
/*    */ import com.intellij.openapi.ui.TextFieldWithBrowseButton;
/*    */ import com.intellij.ui.components.JBLabel;
/*    */ import org.bipolar.NodeJSBundle;
/*    */ import org.bipolar.nodeunit.execution.NodeunitSettings;
/*    */ import org.bipolar.util.RelativePathUIUtil;
/*    */ import java.awt.Component;
/*    */ import java.awt.GridBagConstraints;
/*    */ import java.awt.GridBagLayout;
/*    */ import java.awt.Insets;
/*    */ import java.util.Objects;
/*    */ import javax.swing.JComponent;
/*    */ import javax.swing.JPanel;
/*    */ import javax.swing.JTextField;
/*    */
import org.jetbrains.annotations.NotNull;
/*    */ 
/*    */ class DirectorySettingsController implements SettingsController {
/* 20 */   public static final SettingsControllerFactory FACTORY = new SettingsControllerFactory()
/*    */     {
/*    */       @NotNull
/*    */       public SettingsController createSettingsController(@NotNull Context context) {
/* 24 */         if (context == null) $$$reportNull$$$0(0);  return new DirectorySettingsController(context);
/*    */       }
/*    */     };
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   @NotNull
/* 32 */   private final JPanel myPanel = new JPanel(new GridBagLayout());
/*    */   DirectorySettingsController(@NotNull Context context) {
/* 34 */     JBLabel directoryLabel = new JBLabel(NodeJSBundle.message("nodeunit.rc.run_all_in_directory.directory.label", new Object[0]));
/* 35 */     directoryLabel.setAnchor(context.getAnchor());
/* 36 */     this.myPanel.add((Component)directoryLabel, new GridBagConstraints(0, 0, 1, 1, 0.0D, 0.0D, 17, 0, new Insets(4, 0, 0, 10), 0, 0));
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */     
/* 46 */     JTextField workingDirectory = Objects.<JTextField>requireNonNull(context.getWorkingDirectory());
/* 47 */     FileChooserDescriptor directoryDescriptor = FileChooserDescriptorFactory.createSingleFolderDescriptor();
/* 48 */     this.myDirectoryTextFieldWithBrowseButton = RelativePathUIUtil.createRelativePathTextFieldAndTrackBaseDirChanges(context
/* 49 */         .getProject(), directoryDescriptor, workingDirectory
/*    */         
/* 51 */         .getDocument());
/*    */     
/* 53 */     this.myDirectoryTextFieldWithBrowseButton.addBrowseFolderListener(
/* 54 */         NodeJSBundle.message("nodeunit.rc.run_all_in_directory.directory.browseTitle", new Object[0]), null, context
/*    */         
/* 56 */         .getProject(), directoryDescriptor, TextComponentAccessor.TEXT_FIELD_WHOLE_TEXT);
/*    */ 
/*    */ 
/*    */     
/* 60 */     this.myPanel.add((Component)this.myDirectoryTextFieldWithBrowseButton, new GridBagConstraints(1, 0, 1, 1, 1.0D, 0.0D, 17, 2, new Insets(4, 0, 0, 0), 0, 0));
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   @NotNull
/*    */   private final TextFieldWithBrowseButton myDirectoryTextFieldWithBrowseButton;
/*    */ 
/*    */ 
/*    */   
/*    */   @NotNull
/*    */   public JComponent getComponent() {
/* 73 */     if (this.myPanel == null) $$$reportNull$$$0(1);  return this.myPanel;
/*    */   }
/*    */ 
/*    */   
/*    */   public void resetFrom(@NotNull NodeunitSettings settings) {
/* 78 */     if (settings == null) $$$reportNull$$$0(2);  this.myDirectoryTextFieldWithBrowseButton.setText(settings.getDirectory());
/*    */   }
/*    */ 
/*    */   
/*    */   public void applyTo(@NotNull NodeunitSettings.Builder settingsBuilder) {
/* 83 */     if (settingsBuilder == null) $$$reportNull$$$0(3);  settingsBuilder.setDirectory(this.myDirectoryTextFieldWithBrowseButton.getText());
/*    */   }
/*    */ }


/* Location:              C:\Program Files\JetBrains\WebStorm 2021.1\plugins\NodeJS\lib\NodeJS.jar!\com\jetbrains\nodejs\nodeunit\executio\\ui\DirectorySettingsController.class
 * Java compiler version: 11 (55.0)
 * JD-Core Version:       1.1.3
 */