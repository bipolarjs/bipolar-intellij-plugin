package org.bipolar.tests.execution;

import com.intellij.lang.javascript.JavaScriptBundle;
import com.intellij.openapi.fileChooser.FileChooserDescriptorFactory;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.TextFieldWithBrowseButton;
import com.intellij.openapi.util.io.FileUtil;
import com.intellij.util.ui.FormBuilder;
import com.intellij.util.ui.SwingHelper;
import com.intellij.webcore.ui.PathShortener;

import javax.swing.JComponent;

import org.jetbrains.annotations.NotNull;

public class MochaTestFileView
        extends MochaTestKindView {
    private final TextFieldWithBrowseButton myTestFileTextFieldWithBrowseButton;
    private final FormBuilder myFormBuilder;

    public MochaTestFileView(@NotNull Project project) {
        this.myTestFileTextFieldWithBrowseButton = new TextFieldWithBrowseButton();
        PathShortener.enablePathShortening(this.myTestFileTextFieldWithBrowseButton.getTextField(), null);
        SwingHelper.installFileCompletionAndBrowseDialog(project, this.myTestFileTextFieldWithBrowseButton,


                JavaScriptBundle.message("rc.testRunScope.testFile.browseTitle", new Object[0]),
                FileChooserDescriptorFactory.createSingleFileDescriptor());

        this

                .myFormBuilder = (new FormBuilder()).setAlignLabelOnRight(false).addLabeledComponent(JavaScriptBundle.message("rc.testRunScope.testFile.label", new Object[0]), (JComponent) this.myTestFileTextFieldWithBrowseButton);
    }


    @NotNull
    public JComponent getComponent() {
        return this.myFormBuilder.getPanel();
    }


    public void resetFrom(@NotNull MochaRunSettings settings) {
        this.myTestFileTextFieldWithBrowseButton.setText(FileUtil.toSystemDependentName(settings.getTestFilePath()));
    }


    public void applyTo(@NotNull MochaRunSettings.Builder builder) {
        builder.setTestFilePath(PathShortener.getAbsolutePath(this.myTestFileTextFieldWithBrowseButton.getTextField()));
    }

    @NotNull
    FormBuilder getFormBuilder() {
        return this.myFormBuilder;
    }
}

