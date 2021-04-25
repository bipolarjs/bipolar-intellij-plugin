/*     */ package org.bipolar.mocha.execution;
/*     */ import com.intellij.execution.configuration.EnvironmentVariablesTextFieldWithBrowseButton;
/*     */ import com.intellij.ide.BrowserUtil;
/*     */ import com.intellij.javascript.nodejs.interpreter.NodeJsInterpreterField;
/*     */ import com.intellij.javascript.nodejs.interpreter.NodeJsInterpreterRef;
/*     */ import com.intellij.javascript.nodejs.util.NodePackageField;
/*     */ import com.intellij.lang.javascript.JavaScriptBundle;
/*     */ import com.intellij.openapi.application.ModalityState;
/*     */ import com.intellij.openapi.fileChooser.FileChooserDescriptorFactory;
/*     */
/*     */ import com.intellij.openapi.options.SettingsEditor;
/*     */ import com.intellij.openapi.project.Project;
/*     */ import com.intellij.openapi.ui.ComponentWithBrowseButton;
/*     */ import com.intellij.openapi.ui.DialogWrapper;
/*     */ import com.intellij.openapi.ui.TextFieldWithBrowseButton;
/*     */ import com.intellij.openapi.util.io.FileUtil;
/*     */ import com.intellij.ui.RawCommandLineEditor;
/*     */ import com.intellij.ui.TextFieldWithHistory;
/*     */ import com.intellij.ui.scale.JBUIScale;
/*     */ import com.intellij.util.ui.JBUI;
/*     */ import com.intellij.util.ui.SwingHelper;
/*     */ import com.intellij.util.ui.UIUtil;
/*     */ import com.intellij.webcore.ui.PathShortener;
/*     */ import org.bipolar.NodeJSBundle;
/*     */ import org.bipolar.mocha.MochaUtil;
/*     */ import java.awt.BorderLayout;
/*     */ import java.awt.Component;
/*     */ import java.awt.FlowLayout;
/*     */ import java.awt.GridBagConstraints;
/*     */ import java.awt.Insets;
/*     */ import java.awt.event.ActionEvent;
/*     */ import java.awt.event.ActionListener;
/*     */ import java.util.HashMap;
/*     */ import java.util.Map;
/*     */ import javax.swing.ButtonGroup;
/*     */ import javax.swing.JComponent;
/*     */ import javax.swing.JLabel;
/*     */ import javax.swing.JPanel;
/*     */ import javax.swing.JRadioButton;
/*     */ import javax.swing.JTextField;
/*     */ import org.jetbrains.annotations.NotNull;
/*     */ import org.jetbrains.annotations.Nullable;
/*     */ 
/*     */ public class MochaRunConfigurationEditor extends SettingsEditor<MochaRunConfiguration> {
/*     */   private final Project myProject;
/*     */   private final JPanel myComponent;
/*     */   private final NodeJsInterpreterField myNodeInterpreterField;
/*     */   private final RawCommandLineEditor myNodeOptions;
/*  49 */   private final Map<MochaTestKind, JRadioButton> myRadioButtonMap = new HashMap<>(); private final TextFieldWithBrowseButton myWorkingDirTextFieldWithBrowseButton; private final EnvironmentVariablesTextFieldWithBrowseButton myEnvironmentVariablesTextFieldWithBrowseButton; private final NodePackageField myMochaPackageField; private final TextFieldWithHistory myUiComponent; private final RawCommandLineEditor myExtraMochaOptions;
/*  50 */   private final Map<MochaTestKind, MochaTestKindView> myTestKindViewMap = new HashMap<>();
/*     */   private final JPanel mySelectedTestKindPanel;
/*  52 */   private final int myLongestLabelWidth = ((new JLabel(UIUtil.removeMnemonic(JavaScriptBundle.message("rc.environmentVariables.label", new Object[0])))).getPreferredSize()).width;
/*     */   
/*     */   public MochaRunConfigurationEditor(@NotNull Project project) {
/*  55 */     this.myProject = project;
/*  56 */     this.myNodeInterpreterField = new NodeJsInterpreterField(project, false);
/*  57 */     this.myNodeOptions = new RawCommandLineEditor();
/*  58 */     this.myWorkingDirTextFieldWithBrowseButton = createWorkingDirTextField(project);
/*  59 */     this.myEnvironmentVariablesTextFieldWithBrowseButton = new EnvironmentVariablesTextFieldWithBrowseButton();
/*  60 */     this.myMochaPackageField = new NodePackageField(this.myNodeInterpreterField, MochaUtil.PACKAGE_DESCRIPTOR, null);
/*  61 */     this.myUiComponent = createUiComponent();
/*  62 */     ComponentWithBrowseButton<TextFieldWithHistory> uiComponentWithInfoButton = createUiComponentWithInfoButton();
/*  63 */     this.myExtraMochaOptions = createExtraOptionsEditor();
/*  64 */     JPanel testKindPanel = createTestKindRadioButtonPanel();
/*  65 */     this.mySelectedTestKindPanel = new JPanel(new BorderLayout());
/*  66 */     this
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */       
/*  78 */       .myComponent = (new FormBuilder()).setAlignLabelOnRight(false).addLabeledComponent(NodeJsInterpreterField.getLabelTextForComponent(), (JComponent)this.myNodeInterpreterField).addLabeledComponent(JavaScriptBundle.message("rc.nodeOptions.label", new Object[0]), (JComponent)this.myNodeOptions).addLabeledComponent(JavaScriptBundle.message("rc.workingDirectory.label", new Object[0]), (JComponent)this.myWorkingDirTextFieldWithBrowseButton).addLabeledComponent(JavaScriptBundle.message("rc.environmentVariables.label", new Object[0]), (JComponent)this.myEnvironmentVariablesTextFieldWithBrowseButton).addLabeledComponent(NodeJSBundle.message("rc.mocha.mochaPackageField.label", new Object[0]), (JComponent)this.myMochaPackageField).addLabeledComponent(NodeJSBundle.message("rc.mocha.userInterfaceField.label", new Object[0]), SwingHelper.wrapWithoutStretch((JComponent)uiComponentWithInfoButton)).addLabeledComponent(NodeJSBundle.message("rc.mocha.extraMochaOptionsField.label", new Object[0]), (JComponent)this.myExtraMochaOptions).addSeparator(8).addComponent(testKindPanel).addComponent(this.mySelectedTestKindPanel).addComponentFillVertically(new JPanel(), 0).getPanel();
/*     */   }
/*     */   
/*     */   @NotNull
/*     */   private static RawCommandLineEditor createExtraOptionsEditor() {
/*  83 */     RawCommandLineEditor editor = new RawCommandLineEditor();
/*  84 */     JTextField field = editor.getTextField();
/*  85 */     if (field instanceof com.intellij.ui.components.fields.ExpandableTextField) {
/*  86 */       field.putClientProperty("monospaced", Boolean.valueOf(false));
/*     */     }
/*  88 */     if (field instanceof ComponentWithEmptyText) {
/*  89 */       ((ComponentWithEmptyText)field).getEmptyText().setText(NodeJSBundle.message("rc.mocha.extraMochaOptionsField.emptyText", new Object[0]));
/*     */     }
/*  91 */     if (editor == null) $$$reportNull$$$0(1);  return editor;
/*     */   }
/*     */   
/*     */   @NotNull
/*     */   private JPanel createTestKindRadioButtonPanel() {
/*  96 */     JPanel testKindPanel = new JPanel(new FlowLayout(1, JBUIScale.scale(30), 0));
/*  97 */     testKindPanel.setBorder((Border)JBUI.Borders.emptyLeft(10));
/*  98 */     ButtonGroup buttonGroup = new ButtonGroup();
/*  99 */     for (MochaTestKind testKind : MochaTestKind.values()) {
/* 100 */       JRadioButton radioButton = new JRadioButton(UIUtil.removeMnemonic(testKind.getName()));
/* 101 */       int index = UIUtil.getDisplayMnemonicIndex(testKind.getName());
/* 102 */       if (index != -1) {
/* 103 */         radioButton.setMnemonic(testKind.getName().charAt(index + 1));
/* 104 */         radioButton.setDisplayedMnemonicIndex(index);
/*     */       } 
/* 106 */       radioButton.addActionListener(new ActionListener()
/*     */           {
/*     */             public void actionPerformed(ActionEvent e) {
/* 109 */               MochaRunConfigurationEditor.this.setTestKind(testKind);
/*     */             }
/*     */           });
/* 112 */       this.myRadioButtonMap.put(testKind, radioButton);
/* 113 */       testKindPanel.add(radioButton);
/* 114 */       buttonGroup.add(radioButton);
/*     */     } 
/* 116 */     if (testKindPanel == null) $$$reportNull$$$0(2);  return testKindPanel;
/*     */   }
/*     */   
/*     */   @Nullable
/*     */   private MochaTestKind getTestKind() {
/* 121 */     for (Map.Entry<MochaTestKind, JRadioButton> entry : this.myRadioButtonMap.entrySet()) {
/* 122 */       if (((JRadioButton)entry.getValue()).isSelected()) {
/* 123 */         return entry.getKey();
/*     */       }
/*     */     } 
/* 126 */     return null;
/*     */   }
/*     */   
/*     */   private void setTestKind(@NotNull MochaTestKind testKind) {
/* 130 */     if (testKind == null) $$$reportNull$$$0(3);  MochaTestKind selectedTestKind = getTestKind();
/* 131 */     if (selectedTestKind != testKind) {
/* 132 */       JRadioButton radioButton = this.myRadioButtonMap.get(testKind);
/* 133 */       radioButton.setSelected(true);
/*     */     } 
/* 135 */     MochaTestKindView view = getTestKindView(testKind);
/* 136 */     setCenterBorderLayoutComponent(this.mySelectedTestKindPanel, view.getComponent());
/*     */   }
/*     */   
/*     */   private static void setCenterBorderLayoutComponent(@NotNull JPanel panel, @NotNull Component child) {
/* 140 */     if (panel == null) $$$reportNull$$$0(4);  if (child == null) $$$reportNull$$$0(5);  Component prevChild = ((BorderLayout)panel.getLayout()).getLayoutComponent("Center");
/* 141 */     if (prevChild != null) {
/* 142 */       panel.remove(prevChild);
/*     */     }
/* 144 */     panel.add(child, "Center");
/* 145 */     panel.revalidate();
/* 146 */     panel.repaint();
/*     */   }
/*     */   
/*     */   @NotNull
/*     */   private MochaTestKindView getTestKindView(@NotNull MochaTestKind testKind) {
/* 151 */     if (testKind == null) $$$reportNull$$$0(6);  MochaTestKindView view = this.myTestKindViewMap.get(testKind);
/* 152 */     if (view == null) {
/* 153 */       view = testKind.createView(this.myProject);
/* 154 */       this.myTestKindViewMap.put(testKind, view);
/* 155 */       JComponent component = view.getComponent();
/* 156 */       if (component.getLayout() instanceof java.awt.GridBagLayout) {
/* 157 */         component.add(Box.createHorizontalStrut(this.myLongestLabelWidth), new GridBagConstraints(0, -1, 1, 1, 0.0D, 0.0D, 13, 0, 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */               
/* 163 */               (Insets)JBUI.insetsRight(10), 0, 0));
/*     */       }
/*     */     } 
/*     */ 
/*     */     
/* 168 */     if (view == null) $$$reportNull$$$0(7);  return view;
/*     */   }
/*     */   
/*     */   @NotNull
/*     */   private ComponentWithBrowseButton<TextFieldWithHistory> createUiComponentWithInfoButton() {
/* 173 */     if (SwingHelper.wrapWithInfoButton((JComponent)this.myUiComponent, NodeJSBundle.message("rc.mocha.ui_parameter.show_description_in_browser.tooltip", new Object[0]), new ActionListener()
/*     */         {
/*     */           public void actionPerformed(ActionEvent e) {
/* 176 */             BrowserUtil.browse("https://mochajs.org/#interfaces"); } }) == null) $$$reportNull$$$0(8);  return SwingHelper.wrapWithInfoButton((JComponent)this.myUiComponent, NodeJSBundle.message("rc.mocha.ui_parameter.show_description_in_browser.tooltip", new Object[0]), new ActionListener() { public void actionPerformed(ActionEvent e) { BrowserUtil.browse("https://mochajs.org/#interfaces"); }
/*     */            }
/*     */       );
/*     */   }
/*     */   
/*     */   @NotNull
/*     */   private static TextFieldWithBrowseButton createWorkingDirTextField(@NotNull Project project) {
/* 183 */     if (project == null) $$$reportNull$$$0(9);  TextFieldWithBrowseButton textFieldWithBrowseButton = new TextFieldWithBrowseButton();
/* 184 */     SwingHelper.installFileCompletionAndBrowseDialog(project, textFieldWithBrowseButton, 
/*     */ 
/*     */         
/* 187 */         JavaScriptBundle.message("rc.workingDirectory.browseDialogTitle", new Object[0]), 
/* 188 */         FileChooserDescriptorFactory.createSingleFolderDescriptor());
/*     */     
/* 190 */     PathShortener.enablePathShortening(textFieldWithBrowseButton.getTextField(), null);
/* 191 */     if (textFieldWithBrowseButton == null) $$$reportNull$$$0(10);  return textFieldWithBrowseButton;
/*     */   }
/*     */   
/*     */   @NotNull
/*     */   private static TextFieldWithHistory createUiComponent() {
/* 196 */     TextFieldWithHistory textFieldWithHistory = new TextFieldWithHistory();
/* 197 */     textFieldWithHistory.setHistorySize(-1);
/* 198 */     textFieldWithHistory.setHistory(MochaUtil.getMochaUiList());
/* 199 */     if (textFieldWithHistory == null) $$$reportNull$$$0(11);  return textFieldWithHistory;
/*     */   }
/*     */ 
/*     */   
/*     */   protected void resetEditorFrom(@NotNull MochaRunConfiguration configuration) {
/* 204 */     if (configuration == null) $$$reportNull$$$0(12);  MochaRunSettings runSettings = configuration.getRunSettings();
/*     */     
/* 206 */     this.myNodeInterpreterField.setInterpreterRef(runSettings.getInterpreterRef());
/* 207 */     this.myNodeOptions.setText(runSettings.getNodeOptions());
/* 208 */     this.myWorkingDirTextFieldWithBrowseButton.setText(FileUtil.toSystemDependentName(runSettings.getWorkingDir()));
/*     */     
/* 210 */     this.myEnvironmentVariablesTextFieldWithBrowseButton.setData(runSettings.getEnvData());
/*     */     
/* 212 */     this.myMochaPackageField.setSelected(configuration.getMochaPackage());
/*     */     
/* 214 */     this.myUiComponent.setTextAndAddToHistory(runSettings.getUi());
/* 215 */     this.myExtraMochaOptions.setText(runSettings.getExtraMochaOptions());
/* 216 */     setTestKind(runSettings.getTestKind());
/* 217 */     MochaTestKindView view = getTestKindView(runSettings.getTestKind());
/* 218 */     view.resetFrom(runSettings);
/*     */     
/* 220 */     updatePreferredWidth();
/*     */   }
/*     */   
/*     */   private void updatePreferredWidth() {
/* 224 */     DialogWrapper dialogWrapper = DialogWrapper.findInstance(this.myComponent);
/* 225 */     if (dialogWrapper instanceof com.intellij.openapi.options.ex.SingleConfigurableEditor) {
/*     */       
/* 227 */       this.myNodeInterpreterField.setPreferredWidthToFitText();
/* 228 */       this.myMochaPackageField.setPreferredWidthToFitText();
/* 229 */       SwingHelper.setPreferredWidthToFitText(this.myWorkingDirTextFieldWithBrowseButton);
/* 230 */       ApplicationManager.getApplication().invokeLater(() -> SwingHelper.adjustDialogSizeToFitPreferredSize(dialogWrapper), ModalityState.any());
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   protected void applyEditorTo(@NotNull MochaRunConfiguration configuration) {
/* 236 */     if (configuration == null) $$$reportNull$$$0(13);  MochaRunSettings.Builder builder = new MochaRunSettings.Builder();
/*     */     
/* 238 */     NodeJsInterpreterRef interpreterRef = this.myNodeInterpreterField.getInterpreterRef();
/* 239 */     builder.setInterpreterRef(interpreterRef);
/*     */     
/* 241 */     builder.setNodeOptions(this.myNodeOptions.getText());
/* 242 */     builder.setWorkingDir(PathShortener.getAbsolutePath(this.myWorkingDirTextFieldWithBrowseButton.getTextField()));
/* 243 */     builder.setEnvData(this.myEnvironmentVariablesTextFieldWithBrowseButton.getData());
/*     */     
/* 245 */     builder.setMochaPackage(this.myMochaPackageField.getSelected());
/*     */     
/* 247 */     builder.setUi(StringUtil.notNullize(this.myUiComponent.getText()));
/*     */     
/* 249 */     builder.setExtraMochaOptions(this.myExtraMochaOptions.getText());
/*     */     
/* 251 */     MochaTestKind testKind = getTestKind();
/* 252 */     if (testKind != null) {
/* 253 */       builder.setTestKind(testKind);
/* 254 */       MochaTestKindView view = getTestKindView(testKind);
/* 255 */       view.applyTo(builder);
/*     */     } 
/* 257 */     configuration.setRunSettings(builder.build());
/*     */   }
/*     */ 
/*     */   
/*     */   @NotNull
/*     */   protected JComponent createEditor() {
/* 263 */     if (this.myComponent == null) $$$reportNull$$$0(14);  return this.myComponent;
/*     */   }
/*     */ }


/* Location:              C:\Program Files\JetBrains\WebStorm 2021.1\plugins\NodeJS\lib\NodeJS.jar!\com\jetbrains\nodejs\mocha\execution\MochaRunConfigurationEditor.class
 * Java compiler version: 11 (55.0)
 * JD-Core Version:       1.1.3
 */