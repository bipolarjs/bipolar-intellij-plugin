package org.bipolar.tests.execution;

import com.intellij.execution.configuration.EnvironmentVariablesTextFieldWithBrowseButton;
import com.intellij.ide.BrowserUtil;
import com.intellij.javascript.nodejs.interpreter.NodeJsInterpreterField;
import com.intellij.javascript.nodejs.interpreter.NodeJsInterpreterRef;
import com.intellij.javascript.nodejs.util.NodePackageField;
import com.intellij.lang.javascript.JavaScriptBundle;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.application.ModalityState;
import com.intellij.openapi.fileChooser.FileChooserDescriptorFactory;

import com.intellij.openapi.options.SettingsEditor;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.ComponentWithBrowseButton;
import com.intellij.openapi.ui.DialogWrapper;
import com.intellij.openapi.ui.TextFieldWithBrowseButton;
import com.intellij.openapi.util.io.FileUtil;
import com.intellij.openapi.util.text.StringUtil;
import com.intellij.ui.RawCommandLineEditor;
import com.intellij.ui.TextFieldWithHistory;
import com.intellij.ui.scale.JBUIScale;
import com.intellij.util.ui.*;
import com.intellij.webcore.ui.PathShortener;


import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashMap;
import java.util.Map;
import javax.swing.*;
import javax.swing.border.Border;

import com.jetbrains.nodejs.NodeJSBundle;
import org.bipolar.tests.BipolarUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class MochaRunConfigurationEditor extends SettingsEditor<BipolarRunConfiguration> {
    private final Project myProject;
    private final JPanel myComponent;
    private final NodeJsInterpreterField myNodeInterpreterField;
    private final RawCommandLineEditor myNodeOptions;
    private final Map<MochaTestKind, JRadioButton> myRadioButtonMap = new HashMap<>();
    private final TextFieldWithBrowseButton myWorkingDirTextFieldWithBrowseButton;
    private final EnvironmentVariablesTextFieldWithBrowseButton myEnvironmentVariablesTextFieldWithBrowseButton;
    private final NodePackageField myMochaPackageField;
    private final TextFieldWithHistory myUiComponent;
    private final RawCommandLineEditor myExtraMochaOptions;
    private final Map<MochaTestKind, MochaTestKindView> myTestKindViewMap = new HashMap<>();
    private final JPanel mySelectedTestKindPanel;
    private final int myLongestLabelWidth = ((new JLabel(UIUtil.removeMnemonic(JavaScriptBundle.message("rc.environmentVariables.label", new Object[0])))).getPreferredSize()).width;

    public MochaRunConfigurationEditor(@NotNull Project project) {
        this.myProject = project;
        this.myNodeInterpreterField = new NodeJsInterpreterField(project, false);
        this.myNodeOptions = new RawCommandLineEditor();
        this.myWorkingDirTextFieldWithBrowseButton = createWorkingDirTextField(project);
        this.myEnvironmentVariablesTextFieldWithBrowseButton = new EnvironmentVariablesTextFieldWithBrowseButton();
        this.myMochaPackageField = new NodePackageField(this.myNodeInterpreterField, BipolarUtil.PACKAGE_DESCRIPTOR, null);
        this.myUiComponent = createUiComponent();
        ComponentWithBrowseButton<TextFieldWithHistory> uiComponentWithInfoButton = createUiComponentWithInfoButton();
        this.myExtraMochaOptions = createExtraOptionsEditor();
        JPanel testKindPanel = createTestKindRadioButtonPanel();
        this.mySelectedTestKindPanel = new JPanel(new BorderLayout());
        this


                .myComponent = (new FormBuilder()).setAlignLabelOnRight(false).addLabeledComponent(NodeJsInterpreterField.getLabelTextForComponent(), (JComponent) this.myNodeInterpreterField).addLabeledComponent(JavaScriptBundle.message("rc.nodeOptions.label", new Object[0]), (JComponent) this.myNodeOptions).addLabeledComponent(JavaScriptBundle.message("rc.workingDirectory.label", new Object[0]), (JComponent) this.myWorkingDirTextFieldWithBrowseButton).addLabeledComponent(JavaScriptBundle.message("rc.environmentVariables.label", new Object[0]), (JComponent) this.myEnvironmentVariablesTextFieldWithBrowseButton).addLabeledComponent(NodeJSBundle.message("rc.mocha.mochaPackageField.label", new Object[0]), (JComponent) this.myMochaPackageField).addLabeledComponent(NodeJSBundle.message("rc.mocha.userInterfaceField.label", new Object[0]), SwingHelper.wrapWithoutStretch((JComponent) uiComponentWithInfoButton)).addLabeledComponent(NodeJSBundle.message("rc.mocha.extraMochaOptionsField.label", new Object[0]), (JComponent) this.myExtraMochaOptions).addSeparator(8).addComponent(testKindPanel).addComponent(this.mySelectedTestKindPanel).addComponentFillVertically(new JPanel(), 0).getPanel();
    }

    @NotNull
    private static RawCommandLineEditor createExtraOptionsEditor() {
        RawCommandLineEditor editor = new RawCommandLineEditor();
        JTextField field = editor.getTextField();
        if (field instanceof com.intellij.ui.components.fields.ExpandableTextField) {
            field.putClientProperty("monospaced", Boolean.valueOf(false));
        }
        if (field instanceof ComponentWithEmptyText) {
            ((ComponentWithEmptyText) field).getEmptyText().setText(NodeJSBundle.message("rc.mocha.extraMochaOptionsField.emptyText", new Object[0]));
        }
        return editor;
    }

    @NotNull
    private JPanel createTestKindRadioButtonPanel() {
        JPanel testKindPanel = new JPanel(new FlowLayout(1, JBUIScale.scale(30), 0));
        testKindPanel.setBorder((Border) JBUI.Borders.emptyLeft(10));
        ButtonGroup buttonGroup = new ButtonGroup();
        for (MochaTestKind testKind : MochaTestKind.values()) {
            JRadioButton radioButton = new JRadioButton(UIUtil.removeMnemonic(testKind.getName()));
            int index = UIUtil.getDisplayMnemonicIndex(testKind.getName());
            if (index != -1) {
                radioButton.setMnemonic(testKind.getName().charAt(index + 1));
                radioButton.setDisplayedMnemonicIndex(index);
            }
            radioButton.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    MochaRunConfigurationEditor.this.setTestKind(testKind);
                }
            });
            this.myRadioButtonMap.put(testKind, radioButton);
            testKindPanel.add(radioButton);
            buttonGroup.add(radioButton);
        }
        return testKindPanel;
    }

    @Nullable
    private MochaTestKind getTestKind() {
        for (Map.Entry<MochaTestKind, JRadioButton> entry : this.myRadioButtonMap.entrySet()) {
            if (((JRadioButton) entry.getValue()).isSelected()) {
                return entry.getKey();
            }
        }
        return null;
    }

    private void setTestKind(@NotNull MochaTestKind testKind) {
        MochaTestKind selectedTestKind = getTestKind();
        if (selectedTestKind != testKind) {
            JRadioButton radioButton = this.myRadioButtonMap.get(testKind);
            radioButton.setSelected(true);
        }
        MochaTestKindView view = getTestKindView(testKind);
        setCenterBorderLayoutComponent(this.mySelectedTestKindPanel, view.getComponent());
    }

    private static void setCenterBorderLayoutComponent(@NotNull JPanel panel, @NotNull Component child) {
        Component prevChild = ((BorderLayout) panel.getLayout()).getLayoutComponent("Center");
        if (prevChild != null) {
            panel.remove(prevChild);
        }
        panel.add(child, "Center");
        panel.revalidate();
        panel.repaint();
    }

    @NotNull
    private MochaTestKindView getTestKindView(@NotNull MochaTestKind testKind) {
        MochaTestKindView view = this.myTestKindViewMap.get(testKind);
        if (view == null) {
            view = testKind.createView(this.myProject);
            this.myTestKindViewMap.put(testKind, view);
            JComponent component = view.getComponent();
            if (component.getLayout() instanceof java.awt.GridBagLayout) {
                component.add(Box.createHorizontalStrut(this.myLongestLabelWidth), new GridBagConstraints(0, -1, 1, 1, 0.0D, 0.0D, 13, 0,


                        (Insets) JBUI.insetsRight(10), 0, 0));
            }
        }


        return view;
    }

    @NotNull
    private ComponentWithBrowseButton<TextFieldWithHistory> createUiComponentWithInfoButton() {
        return SwingHelper.wrapWithInfoButton(
                this.myUiComponent,
                NodeJSBundle.message("rc.mocha.ui_parameter.show_description_in_browser.tooltip"),
                new ActionListener() {
                    public void actionPerformed(ActionEvent e) {
                        BrowserUtil.browse("https://mochajs.org/#interfaces");
                    }
                }
        );
    }

    @NotNull
    private static TextFieldWithBrowseButton createWorkingDirTextField(@NotNull Project project) {
        TextFieldWithBrowseButton textFieldWithBrowseButton = new TextFieldWithBrowseButton();
        SwingHelper.installFileCompletionAndBrowseDialog(project, textFieldWithBrowseButton,


                JavaScriptBundle.message("rc.workingDirectory.browseDialogTitle", new Object[0]),
                FileChooserDescriptorFactory.createSingleFolderDescriptor());

        PathShortener.enablePathShortening(textFieldWithBrowseButton.getTextField(), null);
        return textFieldWithBrowseButton;
    }

    @NotNull
    private static TextFieldWithHistory createUiComponent() {
        TextFieldWithHistory textFieldWithHistory = new TextFieldWithHistory();
        textFieldWithHistory.setHistorySize(-1);
        textFieldWithHistory.setHistory(BipolarUtil.getMochaUiList());
        return textFieldWithHistory;
    }


    protected void resetEditorFrom(@NotNull BipolarRunConfiguration configuration) {
        MochaRunSettings runSettings = configuration.getRunSettings();

        this.myNodeInterpreterField.setInterpreterRef(runSettings.getInterpreterRef());
        this.myNodeOptions.setText(runSettings.getNodeOptions());
        this.myWorkingDirTextFieldWithBrowseButton.setText(FileUtil.toSystemDependentName(runSettings.getWorkingDir()));

        this.myEnvironmentVariablesTextFieldWithBrowseButton.setData(runSettings.getEnvData());

        this.myMochaPackageField.setSelected(configuration.getMochaPackage());

        this.myUiComponent.setTextAndAddToHistory(runSettings.getUi());
        this.myExtraMochaOptions.setText(runSettings.getExtraMochaOptions());
        setTestKind(runSettings.getTestKind());
        MochaTestKindView view = getTestKindView(runSettings.getTestKind());
        view.resetFrom(runSettings);

        updatePreferredWidth();
    }

    private void updatePreferredWidth() {
        DialogWrapper dialogWrapper = DialogWrapper.findInstance(this.myComponent);
        if (dialogWrapper instanceof com.intellij.openapi.options.ex.SingleConfigurableEditor) {

            this.myNodeInterpreterField.setPreferredWidthToFitText();
            this.myMochaPackageField.setPreferredWidthToFitText();
            SwingHelper.setPreferredWidthToFitText(this.myWorkingDirTextFieldWithBrowseButton);
            ApplicationManager.getApplication().invokeLater(() -> SwingHelper.adjustDialogSizeToFitPreferredSize(dialogWrapper), ModalityState.any());
        }
    }


    protected void applyEditorTo(@NotNull BipolarRunConfiguration configuration) {
        MochaRunSettings.Builder builder = new MochaRunSettings.Builder();

        NodeJsInterpreterRef interpreterRef = this.myNodeInterpreterField.getInterpreterRef();
        builder.setInterpreterRef(interpreterRef);

        builder.setNodeOptions(this.myNodeOptions.getText());
        builder.setWorkingDir(PathShortener.getAbsolutePath(this.myWorkingDirTextFieldWithBrowseButton.getTextField()));
        builder.setEnvData(this.myEnvironmentVariablesTextFieldWithBrowseButton.getData());

        builder.setMochaPackage(this.myMochaPackageField.getSelected());

        builder.setUi(StringUtil.notNullize(this.myUiComponent.getText()));

        builder.setExtraMochaOptions(this.myExtraMochaOptions.getText());

        MochaTestKind testKind = getTestKind();
        if (testKind != null) {
            builder.setTestKind(testKind);
            MochaTestKindView view = getTestKindView(testKind);
            view.applyTo(builder);
        }
        configuration.setRunSettings(builder.build());
    }


    @NotNull
    protected JComponent createEditor() {
        return this.myComponent;
    }
}
