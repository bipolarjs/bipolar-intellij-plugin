/*     */ package org.bipolar.run.profile.settings;
/*     */ 
/*     */

/*     */ import com.intellij.execution.ui.AdjustingTabSettingsEditor;
/*     */ import com.intellij.openapi.fileChooser.FileChooserDescriptor;
/*     */ import com.intellij.openapi.fileChooser.FileChooserDescriptorFactory;
/*     */ import com.intellij.openapi.fileChooser.FileChooserDialog;
/*     */ import com.intellij.openapi.fileChooser.FileChooserFactory;
/*     */
        /*     */ import com.intellij.openapi.options.SettingsEditor;
/*     */ import com.intellij.openapi.project.Project;
/*     */ import com.intellij.openapi.ui.TextFieldWithBrowseButton;
/*     */ import com.intellij.openapi.util.io.FileUtil;
/*     */ import com.intellij.openapi.util.text.StringUtil;
/*     */ import com.intellij.openapi.vfs.VirtualFile;
/*     */ import com.intellij.ui.ContextHelpLabel;
/*     */ import com.intellij.ui.components.JBCheckBox;
/*     */ import com.intellij.ui.components.JBLabel;
/*     */
        /*     */ import com.intellij.uiDesigner.core.GridLayoutManager;
/*     */
        /*     */ import com.intellij.util.ui.FormBuilder;
/*     */ import com.intellij.util.ui.UIUtil;
/*     */ import org.bipolar.NodeJSBundle;
/*     */ import org.bipolar.run.NodeJsRunConfiguration;
/*     */ import java.awt.Component;
/*     */
        /*     */
        /*     */ import java.awt.event.ActionEvent;
/*     */ import java.awt.event.ActionListener;
/*     */ import java.io.File;
/*     */
        /*     */ import javax.swing.JComponent;
/*     */ import javax.swing.JPanel;
/*     */
        import org.jetbrains.annotations.NotNull;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class NodeProfilingPanel<T extends NodeJsRunConfiguration>
/*     */   extends SettingsEditor<T>
/*     */   implements AdjustingTabSettingsEditor
/*     */ {
/*     */   private JBCheckBox myProfile;
/*     */   private TextFieldWithBrowseButton myLogFileField;
/*     */   private JPanel myMainPanel;
/*     */   private JPanel myViewerParameters;
/*     */   private JBCheckBox myAllowHeapProfiling;
/*     */   private JBLabel myNoteLabel;
/*     */   @NotNull
/*     */   private final Project myProject;
/*     */   private JBCheckBox myOneLogFile;
/*     */   
/*     */   public NodeProfilingPanel(@NotNull Project project) {
/*  60 */     this.myProject = project;
/*  61 */     $$$setupUI$$$();
/*     */   }
/*     */   
/*     */   @NotNull
/*     */   protected JComponent createEditor() {
/*  66 */     if (this.myMainPanel == null) $$$reportNull$$$0(1);  return this.myMainPanel;
/*     */   }
/*     */ 
/*     */   
/*     */   protected void resetEditorFrom(@NotNull T rc) {
/*  71 */     if (rc == null) $$$reportNull$$$0(2);  NodeProfilingSettings settings = rc.getNodeProfilingSettings();
/*  72 */     ((GridLayoutManager)this.myMainPanel.getLayout()).setHGap(2);
/*  73 */     this.myProfile.setSelected(settings.isProfile());
/*  74 */     String text = StringUtil.notNullize(settings.getLogFolder());
/*  75 */     boolean haveLogFile = (settings.getLogFolder() != null);
/*  76 */     if (haveLogFile) {
/*  77 */       File file = new File(text);
/*  78 */       if (!file.isAbsolute()) {
/*  79 */         String basePath = this.myProject.getBasePath();
/*  80 */         if (basePath != null) {
/*  81 */           if (".".equals(text)) {
/*  82 */             file = new File(basePath);
/*     */           } else {
/*     */             
/*  85 */             file = new File(basePath, text);
/*     */           } 
/*     */         }
/*     */       } 
/*  89 */       text = FileUtil.toSystemDependentName(file.getAbsolutePath());
/*     */     } else {
/*  91 */       text = this.myProject.getBasePath();
/*     */     } 
/*  93 */     this.myLogFileField.setText(text);
/*  94 */     this.myOneLogFile.setSelected(settings.isOneLogFile());
/*  95 */     cpuEnabled(settings.isProfile());
/*     */     
/*  97 */     this.myAllowHeapProfiling.setSelected(settings.isAllowRuntimeHeapSnapshot());
/*     */   }
/*     */ 
/*     */   
/*     */   protected void applyEditorTo(@NotNull T rc) {
/* 102 */     if (rc == null) $$$reportNull$$$0(3);  NodeProfilingSettings settings = rc.getNodeProfilingSettings();
/* 103 */     settings.setProfile(this.myProfile.isSelected());
/* 104 */     String text = getLogFileText();
/* 105 */     settings.setLogFolder(StringUtil.isEmptyOrSpaces(text) ? null : text);
/* 106 */     settings.setOneLogFile(this.myOneLogFile.isSelected());
/* 107 */     settings.setOpenViewer(true);
/*     */     
/* 109 */     settings.setAllowRuntimeHeapSnapshot(this.myAllowHeapProfiling.isSelected());
/*     */   }
/*     */   
/*     */   private String getLogFileText() {
/* 113 */     String text = this.myLogFileField.getText();
/* 114 */     String basePath = this.myProject.getBasePath();
/* 115 */     if (basePath != null) {
/* 116 */       String relativePath = FileUtil.getRelativePath(FileUtil.toSystemIndependentName(basePath), 
/* 117 */           FileUtil.toSystemIndependentName(text), '/');
/* 118 */       if (relativePath != null) {
/* 119 */         text = relativePath;
/*     */       }
/*     */     } 
/* 122 */     return text;
/*     */   }
/*     */   
/*     */   private void cpuEnabled(boolean value) {
/* 126 */     UIUtil.setEnabled(this.myViewerParameters, value, true);
/*     */   }
/*     */   
/*     */   private void createUIComponents() {
/* 130 */     this.myProfile = new JBCheckBox(NodeJSBundle.message("runConfiguration.nodejs.profiling.cpu.record.checkbox", new Object[0]));
/* 131 */     this.myProfile.addActionListener(new ActionListener()
/*     */         {
/*     */           public void actionPerformed(ActionEvent e) {
/* 134 */             NodeProfilingPanel.this.cpuEnabled(NodeProfilingPanel.this.myProfile.isSelected());
/*     */           }
/*     */         });
/* 137 */     final FileChooserDescriptor folderDescriptor = FileChooserDescriptorFactory.createSingleFolderDescriptor();
/* 138 */     this.myLogFileField = new TextFieldWithBrowseButton(new ActionListener()
/*     */         {
/*     */           public void actionPerformed(ActionEvent e)
/*     */           {
/* 142 */             FileChooserDialog chooser = FileChooserFactory.getInstance().createFileChooser(folderDescriptor, NodeProfilingPanel.this.myProject, (Component)NodeProfilingPanel.this.myLogFileField);
/* 143 */             VirtualFile[] fileWrapper = chooser.choose(NodeProfilingPanel.this.myProject, new VirtualFile[] { this.this$0.myProject.getBaseDir() });
/* 144 */             if (fileWrapper.length == 1) {
/* 145 */               NodeProfilingPanel.this.myLogFileField.setText(fileWrapper[0].getPath());
/*     */             }
/*     */           }
/*     */         });
/* 149 */     FileChooserFactory.getInstance().installFileCompletion(this.myLogFileField.getTextField(), folderDescriptor, false, null);
/*     */     
/* 151 */     FormBuilder formBuilder = FormBuilder.createFormBuilder().setVerticalGap(2);
/* 152 */     formBuilder.addLabeledComponent(NodeJSBundle.message("runConfiguration.nodejs.profiling.cpu.log.folder.label", new Object[0]), (JComponent)this.myLogFileField);
/* 153 */     this.myOneLogFile = new JBCheckBox(NodeJSBundle.message("runConfiguration.nodejs.profiling.cpu.one.log.file.checkbox", new Object[0]));
/* 154 */     formBuilder.addComponent((JComponent)this.myOneLogFile).addVerticalGap(4);
/* 155 */     this.myViewerParameters = formBuilder.getPanel();
/*     */     
/* 157 */     this.myAllowHeapProfiling = new JBCheckBox(NodeJSBundle.message("runConfiguration.nodejs.profiling.heap.take.snapshots.checkbox", new Object[0]));
/* 158 */     this.myNoteLabel = (JBLabel)ContextHelpLabel.create(NodeJSBundle.message("runConfiguration.nodejs.profiling.heap.inspect.explanation", new Object[0]));
/*     */   }
/*     */ }


/* Location:              C:\Program Files\JetBrains\WebStorm 2021.1\plugins\NodeJS\lib\NodeJS.jar!\com\jetbrains\nodejs\run\profile\settings\NodeProfilingPanel.class
 * Java compiler version: 11 (55.0)
 * JD-Core Version:       1.1.3
 */