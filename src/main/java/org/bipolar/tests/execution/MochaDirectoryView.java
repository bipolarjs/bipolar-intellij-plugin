package org.bipolar.tests.execution;

import com.intellij.openapi.fileChooser.FileChooserDescriptorFactory;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.TextFieldWithBrowseButton;
import com.intellij.openapi.util.io.FileUtil;
import com.intellij.util.ui.FormBuilder;
import com.intellij.util.ui.SwingHelper;
import com.intellij.webcore.ui.PathShortener;

import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JPanel;

import com.jetbrains.nodejs.NodeJSBundle;
import org.jetbrains.annotations.NotNull;

public class MochaDirectoryView
        extends MochaTestKindView {
    private final TextFieldWithBrowseButton myTestDirTextFieldWithBrowseButton;

    public MochaDirectoryView(@NotNull Project project) {
        this.myTestDirTextFieldWithBrowseButton = createTestDirPathTextField(project);
        PathShortener.enablePathShortening(this.myTestDirTextFieldWithBrowseButton.getTextField(), null);
        this.myRecursiveCheckBox = new JCheckBox(NodeJSBundle.message("rc.mocha.testRunScope.allInDirectory.includeSubdirectories", new Object[0]));
        this


                .myPanel = (new FormBuilder()).setAlignLabelOnRight(false).addLabeledComponent(NodeJSBundle.message("rc.mocha.testRunScope.allInDirectory.testDirectory.label", new Object[0]), (JComponent) this.myTestDirTextFieldWithBrowseButton).addLabeledComponent("", this.myRecursiveCheckBox).getPanel();
    }

    private final JCheckBox myRecursiveCheckBox;
    private final JPanel myPanel;

    @NotNull
    private static TextFieldWithBrowseButton createTestDirPathTextField(@NotNull Project project) {
        TextFieldWithBrowseButton textFieldWithBrowseButton = new TextFieldWithBrowseButton();
        SwingHelper.installFileCompletionAndBrowseDialog(project, textFieldWithBrowseButton,


                NodeJSBundle.message("rc.mocha.testRunScope.allInDirectory.testDirectory.browseDialogTitle", new Object[0]),
                FileChooserDescriptorFactory.createSingleFolderDescriptor());

        return textFieldWithBrowseButton;
    }


    @NotNull
    public JComponent getComponent() {
        return this.myPanel;
    }


    public void resetFrom(@NotNull MochaRunSettings settings) {
        this.myTestDirTextFieldWithBrowseButton.setText(FileUtil.toSystemDependentName(settings.getTestDirPath()));
        this.myRecursiveCheckBox.setSelected(settings.isRecursive());
    }

    public void applyTo(@NotNull MochaRunSettings.Builder builder) {
        builder.setTestDirPath(PathShortener.getAbsolutePath(this.myTestDirTextFieldWithBrowseButton.getTextField()));
        builder.setRecursive(this.myRecursiveCheckBox.isSelected());
    }
}
