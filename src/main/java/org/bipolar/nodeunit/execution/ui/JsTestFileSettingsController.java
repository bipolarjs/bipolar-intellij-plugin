/*     */ package org.bipolar.nodeunit.execution.ui;
/*     */ import com.intellij.openapi.fileChooser.FileChooserDescriptor;
/*     */ import com.intellij.openapi.fileChooser.FileChooserDescriptorFactory;
/*     */
/*     */ import com.intellij.openapi.ui.TextBrowseFolderListener;
/*     */ import com.intellij.openapi.ui.TextFieldWithBrowseButton;
/*     */ import com.intellij.openapi.util.text.StringUtil;
/*     */ import com.intellij.openapi.vfs.VfsUtil;
/*     */ import com.intellij.openapi.vfs.VirtualFile;
/*     */ import com.intellij.ui.components.JBLabel;
/*     */ import org.bipolar.NodeJSBundle;
/*     */ import org.bipolar.nodeunit.execution.NodeunitSettings;
/*     */ import org.bipolar.util.RelativePathUIUtil;
/*     */ import java.awt.Component;
/*     */ import java.awt.GridBagConstraints;
/*     */ import java.awt.Insets;
/*     */ import java.io.File;
/*     */ import java.util.Objects;
/*     */ import javax.swing.JPanel;
/*     */ import javax.swing.JTextField;
/*     */
import org.jetbrains.annotations.NotNull;
/*     */ import org.jetbrains.annotations.Nullable;
/*     */ 
/*     */ class JsTestFileSettingsController implements SettingsController {
/*  25 */   public static final SettingsControllerFactory FACTORY = new SettingsControllerFactory()
/*     */     {
/*     */       @NotNull
/*     */       public SettingsController createSettingsController(@NotNull Context context) {
/*  29 */         if (context == null) $$$reportNull$$$0(0);  return new JsTestFileSettingsController(context);
/*     */       }
/*     */     };
/*     */ 
/*     */ 
/*     */   
/*     */   @NotNull
/*  36 */   private final JPanel myPanel = new JPanel(new GridBagLayout()); JsTestFileSettingsController(@NotNull final Context context) {
/*  37 */     JBLabel jsTestFileLabel = new JBLabel(NodeJSBundle.message("nodeunit.rc.run_test_file.file.label", new Object[0]));
/*  38 */     jsTestFileLabel.setAnchor(context.getAnchor());
/*  39 */     this.myPanel.add((Component)jsTestFileLabel, new GridBagConstraints(0, 0, 1, 1, 0.0D, 0.0D, 17, 0, new Insets(4, 0, 0, 10), 0, 0));
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*  49 */     FileChooserDescriptor descriptor = FileChooserDescriptorFactory.createSingleFileNoJarsDescriptor();
/*  50 */     descriptor.setTitle(NodeJSBundle.message("nodeunit.rc.run_test_file.file.browseTitle", new Object[0]));
/*  51 */     JTextField workingDirectory = Objects.<JTextField>requireNonNull(context.getWorkingDirectory());
/*  52 */     this.myJsTestFilePathTextFieldWithBrowseButton = RelativePathUIUtil.createRelativePathTextFieldAndTrackBaseDirChanges(context
/*  53 */         .getProject(), descriptor, workingDirectory
/*     */         
/*  55 */         .getDocument());
/*     */     
/*  57 */     this.myJsTestFilePathTextFieldWithBrowseButton.addBrowseFolderListener(new TextBrowseFolderListener(descriptor, context.getProject())
/*     */         {
/*     */           @Nullable
/*     */           protected VirtualFile getInitialFile() {
/*  61 */             JTextField workingDirectory = Objects.<JTextField>requireNonNull(context.getWorkingDirectory());
/*  62 */             String workingDirectoryStr = StringUtil.notNullize(workingDirectory.getText());
/*  63 */             if (workingDirectoryStr.isEmpty()) {
/*  64 */               return null;
/*     */             }
/*  66 */             File wd = new File(workingDirectoryStr);
/*  67 */             String text = getComponentText();
/*  68 */             File resultFile = new File(text);
/*  69 */             if (!resultFile.isAbsolute()) {
/*  70 */               resultFile = new File(wd, text);
/*     */             }
/*  72 */             if (resultFile.exists()) {
/*  73 */               return VfsUtil.findFileByIoFile(resultFile, false);
/*     */             }
/*  75 */             return super.getInitialFile();
/*     */           }
/*     */         });
/*  78 */     this.myPanel.add((Component)this.myJsTestFilePathTextFieldWithBrowseButton, new GridBagConstraints(1, 0, 1, 1, 1.0D, 0.0D, 17, 2, new Insets(4, 0, 0, 0), 0, 0));
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   @NotNull
/*     */   private final TextFieldWithBrowseButton myJsTestFilePathTextFieldWithBrowseButton;
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   @NotNull
/*     */   public JComponent getComponent() {
/*  92 */     if (this.myPanel == null) $$$reportNull$$$0(1);  return this.myPanel;
/*     */   }
/*     */ 
/*     */   
/*     */   public void resetFrom(@NotNull NodeunitSettings settings) {
/*  97 */     if (settings == null) $$$reportNull$$$0(2);  this.myJsTestFilePathTextFieldWithBrowseButton.setText(settings.getJsFile());
/*     */   }
/*     */ 
/*     */   
/*     */   public void applyTo(@NotNull NodeunitSettings.Builder settingsBuilder) {
/* 102 */     if (settingsBuilder == null) $$$reportNull$$$0(3);  settingsBuilder.setJsFilePath(this.myJsTestFilePathTextFieldWithBrowseButton.getText());
/*     */   }
/*     */ }


/* Location:              C:\Program Files\JetBrains\WebStorm 2021.1\plugins\NodeJS\lib\NodeJS.jar!\com\jetbrains\nodejs\nodeunit\executio\\ui\JsTestFileSettingsController.class
 * Java compiler version: 11 (55.0)
 * JD-Core Version:       1.1.3
 */