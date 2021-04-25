/*     */ package org.bipolar.run;
/*     */

/*     */ import com.intellij.execution.configuration.EnvironmentVariablesComponent;
/*     */ import com.intellij.execution.ui.CommonProgramParametersPanel;
/*     */ import com.intellij.javascript.nodejs.interpreter.NodeJsInterpreter;
/*     */ import com.intellij.javascript.nodejs.interpreter.NodeJsInterpreterChangeListener;
/*     */ import com.intellij.javascript.nodejs.interpreter.NodeJsInterpreterField;
/*     */ import com.intellij.lang.javascript.buildTools.base.JsbtUtil;
/*     */ import com.intellij.openapi.Disposable;
/*     */ import com.intellij.openapi.fileChooser.FileChooserDescriptor;
/*     */ import com.intellij.openapi.fileChooser.FileChooserDescriptorFactory;
/*     */ import com.intellij.openapi.fileChooser.FileChooserFactory;
/*     */ import com.intellij.openapi.options.ConfigurationException;
/*     */ import com.intellij.openapi.options.SettingsEditor;
/*     */ import com.intellij.openapi.project.Project;
/*     */ import com.intellij.openapi.ui.TextComponentAccessor;
/*     */ import com.intellij.openapi.ui.TextFieldWithBrowseButton;
/*     */ import com.intellij.openapi.util.Disposer;
/*     */ import com.intellij.ui.RawCommandLineEditor;
/*     */ import com.intellij.ui.components.JBPanel;
/*     */ import com.intellij.ui.components.fields.ExtendableTextField;
/*     */
        /*     */
        /*     */
        /*     */ import com.intellij.util.PathUtil;
/*     */ import com.intellij.util.ui.FormBuilder;
/*     */ import com.intellij.webcore.ui.PathShortener;
/*     */ import org.bipolar.codeInsight.NodeRunConfigurationNodePathProviderImpl;
/*     */ import java.awt.BorderLayout;
/*     */
        /*     */
        /*     */
        /*     */ import java.util.ArrayList;
/*     */ import javax.swing.JComponent;
/*     */
        /*     */ import javax.swing.JPanel;
/*     */ import javax.swing.JTextField;
/*     */
        import org.jetbrains.annotations.NotNull;
/*     */ import org.jetbrains.annotations.Nullable;
/*     */ 
/*     */ public class NodeRunConfigurationEditor extends SettingsEditor<NodeJsRunConfiguration> {
/*     */   private JPanel panel;
/*     */   private TextFieldWithBrowseButton pathToJSFile;
/*     */   private TextFieldWithBrowseButton workingDir;
/*     */   private RawCommandLineEditor applicationParametersTextField;
/*     */   private RawCommandLineEditor nodeParameters;
/*     */   private EnvironmentVariablesComponent myEnvVariablesComponent;
/*     */   
/*     */   NodeRunConfigurationEditor(Project project) {
/*  50 */     this.myProject = project;
/*     */     
/*  52 */     $$$setupUI$$$(); this.myListeners = new ArrayList<>();
/*  53 */     this.myChildBuilder = new FormBuilder();
/*  54 */     this.myEmbeddedEditors = new ArrayList<>();
/*  55 */     CommonProgramParametersPanel.addMacroSupport((ExtendableTextField)this.nodeParameters.getEditorField());
/*  56 */     JsbtUtil.resetFontToDefault(this.nodeParameters);
/*  57 */     CommonProgramParametersPanel.addMacroSupport((ExtendableTextField)this.applicationParametersTextField.getEditorField());
/*  58 */     JsbtUtil.resetFontToDefault(this.applicationParametersTextField);
/*     */   }
/*     */   private JComponent pathToNode; private JPanel myChildContainer; private NodeJsInterpreterField myInterpreterField; private final Project myProject; private final List<NodeJsInterpreterChangeListener> myListeners; private final FormBuilder myChildBuilder; private final ArrayList<SettingsEditor<NodeJsRunConfiguration>> myEmbeddedEditors;
/*     */   public void addChildComponent(@NotNull SettingsEditor<NodeJsRunConfiguration> embeddedEditor) {
/*  62 */     if (embeddedEditor == null) $$$reportNull$$$0(0);  this.myEmbeddedEditors.add(embeddedEditor);
/*  63 */     this.myChildBuilder.addComponent(embeddedEditor.getComponent());
/*  64 */     this.myChildContainer.removeAll();
/*  65 */     this.myChildContainer.add(this.myChildBuilder.getPanel(), "Center");
/*     */   }
/*     */ 
/*     */   
/*     */   protected void resetEditorFrom(@NotNull NodeJsRunConfiguration configuration) {
/*  70 */     if (configuration == null) $$$reportNull$$$0(1);  this.myInterpreterField.setInterpreterRef(configuration.getOptions().getInterpreterRef());
/*  71 */     this.workingDir.getTextField().setText(PathUtil.toSystemDependentName(configuration.getWorkingDirectory()));
/*  72 */     this.pathToJSFile.getTextField().setText(PathUtil.toSystemDependentName(configuration.getOptions().getPathToJsFile()));
/*  73 */     this.applicationParametersTextField.setText(configuration.getOptions().getApplicationParameters());
/*  74 */     this.nodeParameters.setText(configuration.getOptions().getNodeParameters());
/*  75 */     this.myEnvVariablesComponent.setEnvData(configuration.getEnvData());
/*  76 */     for (SettingsEditor<NodeJsRunConfiguration> editor : this.myEmbeddedEditors) {
/*  77 */       editor.resetFrom(configuration);
/*     */     }
/*     */   }
/*     */ 
/*     */   
/*     */   protected void applyEditorTo(@NotNull NodeJsRunConfiguration configuration) throws ConfigurationException {
/*  83 */     if (configuration == null) $$$reportNull$$$0(2);  NodeJsRunConfigurationState state = configuration.getOptions();
/*  84 */     state.setInterpreterRef(this.myInterpreterField.getInterpreterRef());
/*  85 */     state.setPathToJsFile(PathUtil.toSystemIndependentName(PathShortener.getTextWithExpandedUserHome(this.pathToJSFile.getTextField())));
/*  86 */     configuration.setWorkingDirectory(PathShortener.getAbsolutePath(this.workingDir.getTextField()));
/*     */     
/*  88 */     configuration.setApplicationParameters(this.applicationParametersTextField.getText());
/*  89 */     configuration.setProgramParameters(this.nodeParameters.getText());
/*  90 */     configuration.setEnvData(this.myEnvVariablesComponent.getEnvData());
/*     */     
/*  92 */     NodeRunConfigurationNodePathProviderImpl.getInstance(this.myProject).dropCache();
/*  93 */     for (SettingsEditor<NodeJsRunConfiguration> editor : this.myEmbeddedEditors) {
/*  94 */       editor.applyTo(configuration);
/*     */     }
/*     */   }
/*     */ 
/*     */   
/*     */   @NotNull
/*     */   protected JComponent createEditor() {
/* 101 */     if (this.panel == null) $$$reportNull$$$0(3);  return this.panel;
/*     */   }
/*     */   
/*     */   public void addListener(@NotNull NodeJsInterpreterChangeListener listener) {
/* 105 */     if (listener == null) $$$reportNull$$$0(4);  this.myListeners.add(listener);
/*     */   }
/*     */   
/*     */   private void createUIComponents() {
/* 109 */     this.myChildContainer = (JPanel)new JBPanel(new BorderLayout());
/* 110 */     this.myInterpreterField = new NodeJsInterpreterField(this.myProject, true);
/* 111 */     this.myInterpreterField.addChangeListener(new NodeJsInterpreterChangeListener()
/*     */         {
/*     */           public void interpreterChanged(@Nullable NodeJsInterpreter newInterpreter) {
/* 114 */             for (NodeJsInterpreterChangeListener listener : NodeRunConfigurationEditor.this.myListeners) {
/* 115 */               listener.interpreterChanged(newInterpreter);
/*     */             }
/*     */           }
/*     */         });
/* 119 */     this.pathToNode = (JComponent)this.myInterpreterField;
/* 120 */     this.workingDir = createWorkingDirectory(this.myProject, (Disposable)this);
/* 121 */     this.pathToJSFile = createPathToJsFile(this.myProject, this.workingDir.getTextField());
/*     */   }
/*     */ 
/*     */   
/*     */   protected void disposeEditor() {
/* 126 */     super.disposeEditor();
/* 127 */     for (SettingsEditor<NodeJsRunConfiguration> editor : this.myEmbeddedEditors)
/*     */     {
/* 129 */       Disposer.dispose((Disposable)editor);
/*     */     }
/* 131 */     this.myListeners.clear();
/*     */   }
/*     */   
/*     */   @NotNull
/*     */   private static TextFieldWithBrowseButton createWorkingDirectory(@NotNull Project project, @NotNull Disposable parent) {
/* 136 */     if (project == null) $$$reportNull$$$0(5);  if (parent == null) $$$reportNull$$$0(6);  TextFieldWithBrowseButton workingDir = new TextFieldWithBrowseButton();
/* 137 */     FileChooserDescriptor descriptor = FileChooserDescriptorFactory.createSingleFolderDescriptor();
/* 138 */     workingDir.addBrowseFolderListener(null, null, project, descriptor, TextComponentAccessor.TEXT_FIELD_WHOLE_TEXT);
/* 139 */     PathShortener.enablePathShortening(workingDir.getTextField(), null);
/* 140 */     FileChooserFactory.getInstance().installFileCompletion(workingDir.getTextField(), descriptor, false, parent);
/* 141 */     if (workingDir == null) $$$reportNull$$$0(7);  return workingDir;
/*     */   }
/*     */ 
/*     */   
/*     */   @NotNull
/*     */   private static TextFieldWithBrowseButton createPathToJsFile(@NotNull Project project, @NotNull JTextField workingDir) {
/* 147 */     if (project == null) $$$reportNull$$$0(8);  if (workingDir == null) $$$reportNull$$$0(9);  FileChooserDescriptor descriptor = FileChooserDescriptorFactory.createSingleFileNoJarsDescriptor();
/* 148 */     TextFieldWithBrowseButton pathToJSFile = new TextFieldWithBrowseButton();
/* 149 */     PathShortener.enablePathShortening(pathToJSFile.getTextField(), workingDir);
/* 150 */     pathToJSFile.addBrowseFolderListener(null, null, project, descriptor, TextComponentAccessor.TEXT_FIELD_WHOLE_TEXT);
/* 151 */     if (pathToJSFile == null) $$$reportNull$$$0(10);  return pathToJSFile;
/*     */   }
/*     */ }


/* Location:              C:\Program Files\JetBrains\WebStorm 2021.1\plugins\NodeJS\lib\NodeJS.jar!\com\jetbrains\nodejs\run\NodeRunConfigurationEditor.class
 * Java compiler version: 11 (55.0)
 * JD-Core Version:       1.1.3
 */