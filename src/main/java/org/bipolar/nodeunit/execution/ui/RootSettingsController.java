/*     */ package org.bipolar.nodeunit.execution.ui;
/*     */ 
/*     */ import com.google.common.collect.ImmutableMap;
/*     */ import com.google.common.collect.Maps;
/*     */ import com.intellij.execution.configuration.EnvironmentVariablesTextFieldWithBrowseButton;
/*     */ import com.intellij.javascript.nodejs.interpreter.NodeJsInterpreterField;
/*     */ import com.intellij.javascript.nodejs.util.NodePackageField;
/*     */ import com.intellij.openapi.fileChooser.FileChooserDescriptorFactory;
/*     */ import com.intellij.openapi.ui.ComboBox;
/*     */ import com.intellij.openapi.ui.TextFieldWithBrowseButton;
/*     */ import com.intellij.openapi.util.io.FileUtil;
/*     */ import com.intellij.ui.CollectionComboBoxModel;
/*     */ import com.intellij.ui.SimpleListCellRenderer;
/*     */ import com.intellij.util.ui.FormBuilder;
/*     */ import org.bipolar.NodeJSBundle;
/*     */ import org.bipolar.nodeunit.execution.NodeunitExecutionUtils;
/*     */ import org.bipolar.nodeunit.execution.NodeunitSettings;
/*     */ import org.bipolar.nodeunit.execution.NodeunitTestType;
/*     */ import java.awt.event.ActionEvent;
/*     */ import java.awt.event.ActionListener;
/*     */ import java.util.Arrays;
/*     */ import java.util.List;
/*     */ import javax.swing.ComboBoxModel;
/*     */ import javax.swing.JComponent;
/*     */ import javax.swing.JLabel;
/*     */ import javax.swing.JPanel;
/*     */ import javax.swing.ListCellRenderer;
/*     */
import org.jetbrains.annotations.NotNull;
/*     */ 
/*     */ class RootSettingsController
/*     */   implements SettingsController
/*     */ {
/*     */   private final JPanel myPanel;
/*     */   private final ComboBox myTestTypeOptionComboBox;
/*     */   private final ImmutableMap<NodeunitTestType, TestTypeOption> myOptionByTestTypeMap;
/*     */   private final OneOfSettingsController<TestTypeOption> myTestTypeContentSettingsController;
/*     */   
/*     */   RootSettingsController(@NotNull Context context) {
/*  39 */     this.myNodeInterpreterField = new NodeJsInterpreterField(context.getProject(), false);
/*  40 */     this.myWorkingDirectoryTextFieldWithBrowseButton = new TextFieldWithBrowseButton();
/*  41 */     this.myWorkingDirectoryTextFieldWithBrowseButton.addBrowseFolderListener(
/*  42 */         NodeJSBundle.message("nodeunit.rc.working_directory.select.title", new Object[0]), null, context
/*     */         
/*  44 */         .getProject(), 
/*  45 */         FileChooserDescriptorFactory.createSingleFolderDescriptor());
/*     */     
/*  47 */     this.myEnvVariablesField = new EnvironmentVariablesTextFieldWithBrowseButton();
/*  48 */     this.myNodeunitPackageField = new NodePackageField(this.myNodeInterpreterField, NodeunitExecutionUtils.PKG, null);
/*     */     
/*  50 */     List<TestTypeOption> testTypeOptions = createTestTypeOptions();
/*  51 */     this.myOptionByTestTypeMap = createOptionByTestTypeMap(testTypeOptions);
/*  52 */     this.myTestTypeOptionComboBox = createTestTypeOptionComboBox(testTypeOptions);
/*     */     
/*  54 */     JLabel envVarLabel = new JLabel(NodeJSBundle.message("nodeunit.rc.environment_variables.label", new Object[0]));
/*     */     
/*  56 */     this
/*     */ 
/*     */       
/*  59 */       .myTestTypeContentSettingsController = new OneOfSettingsController<>(new Context(context.getProject(), envVarLabel, this.myWorkingDirectoryTextFieldWithBrowseButton.getTextField()), testTypeOptions);
/*     */ 
/*     */ 
/*     */     
/*  63 */     FormBuilder builder = FormBuilder.createFormBuilder();
/*  64 */     builder.addLabeledComponent(NodeJsInterpreterField.getLabelTextForComponent(), (JComponent)this.myNodeInterpreterField);
/*  65 */     builder.addLabeledComponent(NodeJSBundle.message("nodeunit.rc.working_directory.label", new Object[0]), (JComponent)this.myWorkingDirectoryTextFieldWithBrowseButton);
/*     */     
/*  67 */     builder.addLabeledComponent(envVarLabel, (JComponent)this.myEnvVariablesField);
/*  68 */     builder.addLabeledComponent(NodeJSBundle.message("nodeunit.rc.nodeunit_module.label", new Object[0]), (JComponent)this.myNodeunitPackageField);
/*  69 */     builder.addLabeledComponent(NodeJSBundle.message("nodeunit.rc.run.label", new Object[0]), (JComponent)this.myTestTypeOptionComboBox);
/*  70 */     builder.addComponent(this.myTestTypeContentSettingsController.getComponent());
/*  71 */     this.myPanel = builder.getPanel();
/*     */   }
/*     */   private final NodeJsInterpreterField myNodeInterpreterField; private final EnvironmentVariablesTextFieldWithBrowseButton myEnvVariablesField; private final TextFieldWithBrowseButton myWorkingDirectoryTextFieldWithBrowseButton; private final NodePackageField myNodeunitPackageField;
/*     */   @NotNull
/*     */   public JComponent getComponent() {
/*  76 */     if (this.myPanel == null) $$$reportNull$$$0(1);  return this.myPanel;
/*     */   }
/*     */ 
/*     */   
/*     */   public void resetFrom(@NotNull NodeunitSettings settings) {
/*  81 */     if (settings == null) $$$reportNull$$$0(2);  this.myNodeInterpreterField.setInterpreterRef(settings.getInterpreterRef());
/*  82 */     this.myEnvVariablesField.setData(settings.getEnvData());
/*  83 */     this.myNodeunitPackageField.setSelected(settings.getNodeunitPackage());
/*  84 */     this.myWorkingDirectoryTextFieldWithBrowseButton.setText(FileUtil.toSystemDependentName(settings.getWorkingDirectory()));
/*  85 */     selectTestType(settings.getTestType());
/*  86 */     this.myTestTypeContentSettingsController.resetFrom(settings);
/*     */   }
/*     */ 
/*     */   
/*     */   public void applyTo(@NotNull NodeunitSettings.Builder builder) {
/*  91 */     if (builder == null) $$$reportNull$$$0(3);  builder.setInterpreterRef(this.myNodeInterpreterField.getInterpreterRef());
/*  92 */     builder.setEnvData(this.myEnvVariablesField.getData());
/*  93 */     builder.setNodeunitPackage(this.myNodeunitPackageField.getSelected());
/*  94 */     builder.setWorkingDirectory(this.myWorkingDirectoryTextFieldWithBrowseButton.getText());
/*  95 */     builder.setTestType(getSelectedTestType());
/*  96 */     this.myTestTypeContentSettingsController.applyTo(builder);
/*     */   }
/*     */   @NotNull
/*     */   private static List<TestTypeOption> createTestTypeOptions() {
/* 100 */     if (Arrays.asList(new TestTypeOption[] { new TestTypeOption(NodeunitTestType.DIRECTORY, 
/* 101 */             NodeJSBundle.message("nodeunit.rc.run_all_in_directory.label", new Object[0]), DirectorySettingsController.FACTORY), new TestTypeOption(NodeunitTestType.JS_FILE, 
/*     */             
/* 103 */             NodeJSBundle.message("nodeunit.rc.run_test_file.label", new Object[0]), JsTestFileSettingsController.FACTORY) }) == null) $$$reportNull$$$0(4);  return Arrays.asList(new TestTypeOption[] { new TestTypeOption(NodeunitTestType.DIRECTORY, NodeJSBundle.message("nodeunit.rc.run_all_in_directory.label", new Object[0]), DirectorySettingsController.FACTORY), new TestTypeOption(NodeunitTestType.JS_FILE, NodeJSBundle.message("nodeunit.rc.run_test_file.label", new Object[0]), JsTestFileSettingsController.FACTORY) });
/*     */   }
/*     */ 
/*     */   
/*     */   @NotNull
/*     */   public static ImmutableMap<NodeunitTestType, TestTypeOption> createOptionByTestTypeMap(@NotNull List<TestTypeOption> testTypeOptions) {
/* 109 */     if (testTypeOptions == null) $$$reportNull$$$0(5);  if (Maps.uniqueIndex(testTypeOptions, input -> input.getTestType()) == null) $$$reportNull$$$0(6);  return Maps.uniqueIndex(testTypeOptions, input -> input.getTestType());
/*     */   }
/*     */   @NotNull
/*     */   private ComboBox createTestTypeOptionComboBox(@NotNull List<TestTypeOption> testTypeOptions) {
/* 113 */     if (testTypeOptions == null) $$$reportNull$$$0(7);  ComboBox<TestTypeOption> comboBox = new ComboBox((ComboBoxModel)new CollectionComboBoxModel(testTypeOptions));
/* 114 */     comboBox.setRenderer((ListCellRenderer)SimpleListCellRenderer.create("", TestTypeOption::getDisplayName));
/* 115 */     comboBox.addActionListener(new ActionListener()
/*     */         {
/*     */           public void actionPerformed(ActionEvent e) {
/* 118 */             RootSettingsController.this.selectTestType(RootSettingsController.this.getSelectedTestType());
/*     */           }
/*     */         });
/* 121 */     if (comboBox == null) $$$reportNull$$$0(8);  return comboBox;
/*     */   }
/*     */   
/*     */   private void selectTestType(@NotNull NodeunitTestType testType) {
/* 125 */     if (testType == null) $$$reportNull$$$0(9);  TestTypeOption testTypeOption = (TestTypeOption)this.myOptionByTestTypeMap.get(testType);
/* 126 */     if (testTypeOption == null) {
/* 127 */       throw new RuntimeException("Can't find TestTypeOption by " + testType + ", index is " + this.myOptionByTestTypeMap);
/*     */     }
/* 129 */     ComboBoxModel comboBoxModel = this.myTestTypeOptionComboBox.getModel();
/* 130 */     if (comboBoxModel.getSelectedItem() != testTypeOption) {
/* 131 */       comboBoxModel.setSelectedItem(testTypeOption);
/*     */     }
/* 133 */     this.myTestTypeContentSettingsController.select(testTypeOption);
/*     */   }
/*     */   @NotNull
/*     */   private NodeunitTestType getSelectedTestType() {
/* 137 */     if (((TestTypeOption)this.myTestTypeOptionComboBox.getSelectedItem()).getTestType() == null) $$$reportNull$$$0(10);  return ((TestTypeOption)this.myTestTypeOptionComboBox.getSelectedItem()).getTestType();
/*     */   }
/*     */ 
/*     */   
/*     */   static class TestTypeOption
/*     */     implements IdProvider, SettingsControllerFactory
/*     */   {
/*     */     private final NodeunitTestType myTestType;
/*     */     private final String myDisplayName;
/*     */     private final SettingsControllerFactory mySettingsControllerFactory;
/*     */     
/*     */     TestTypeOption(@NotNull NodeunitTestType testType, @NotNull String displayName, @NotNull SettingsControllerFactory settingsControllerFactory) {
/* 149 */       this.myTestType = testType;
/* 150 */       this.myDisplayName = displayName;
/* 151 */       this.mySettingsControllerFactory = settingsControllerFactory;
/*     */     }
/*     */     @NotNull
/*     */     public NodeunitTestType getTestType() {
/* 155 */       if (this.myTestType == null) $$$reportNull$$$0(3);  return this.myTestType;
/*     */     }
/*     */     
/*     */     @NotNull
/*     */     public String getId() {
/* 160 */       if (this.myTestType.name() == null) $$$reportNull$$$0(4);  return this.myTestType.name();
/*     */     }
/*     */     
/*     */     @NotNull
/*     */     public SettingsController createSettingsController(@NotNull Context context) {
/* 165 */       if (context == null) $$$reportNull$$$0(5);  if (this.mySettingsControllerFactory.createSettingsController(context) == null) $$$reportNull$$$0(6);  return this.mySettingsControllerFactory.createSettingsController(context);
/*     */     }
/*     */     @NotNull
/*     */     public String getDisplayName() {
/* 169 */       if (this.myDisplayName == null) $$$reportNull$$$0(7);  return this.myDisplayName;
/*     */     }
/*     */   }
/*     */ }


/* Location:              C:\Program Files\JetBrains\WebStorm 2021.1\plugins\NodeJS\lib\NodeJS.jar!\com\jetbrains\nodejs\nodeunit\executio\\ui\RootSettingsController.class
 * Java compiler version: 11 (55.0)
 * JD-Core Version:       1.1.3
 */