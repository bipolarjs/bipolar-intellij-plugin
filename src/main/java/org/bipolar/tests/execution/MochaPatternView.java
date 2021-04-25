package org.bipolar.tests.execution;

import com.intellij.openapi.Disposable;
import com.intellij.openapi.fileChooser.FileChooserDescriptorFactory;
import com.intellij.openapi.fileChooser.FileChooserFactory;
import com.intellij.openapi.project.Project;
import com.intellij.ui.RawCommandLineEditor;
import com.intellij.ui.components.JBTextField;
import com.intellij.util.ObjectUtils;
import com.intellij.util.ui.FormBuilder;

import javax.swing.JComponent;
import javax.swing.JPanel;

import com.jetbrains.nodejs.NodeJSBundle;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class MochaPatternView extends MochaTestKindView {
    private final RawCommandLineEditor myPatternComponent;

    public MochaPatternView(@NotNull Project project) {
        this.myPatternComponent = new RawCommandLineEditor();
        configurePatternEmptyText((JBTextField) ObjectUtils.tryCast(this.myPatternComponent.getTextField(), JBTextField.class));
        FileChooserFactory.getInstance().installFileCompletion(this.myPatternComponent
                        .getTextField(),
                FileChooserDescriptorFactory.createSingleFileOrFolderDescriptor(), true, (Disposable) project);


        this.myPanel = (new FormBuilder()).setAlignLabelOnRight(false).addLabeledComponent(NodeJSBundle.message("rc.mocha.testRunScope.filePattern.filePattern.label", new Object[0]), (JComponent) this.myPatternComponent).getPanel();
    }

    private final JPanel myPanel;

    private static void configurePatternEmptyText(@Nullable JBTextField field) {
        if (field != null) {
            field.getEmptyText().setText(NodeJSBundle.message("rc.mocha.testRunScope.filePattern.filePattern.emptyText", new Object[0]));
        }
    }


    @NotNull
    public JComponent getComponent() {
        return this.myPanel;
    }


    public void resetFrom(@NotNull MochaRunSettings settings) {
        this.myPatternComponent.setText(settings.getTestFilePattern());
    }


    public void applyTo(@NotNull MochaRunSettings.Builder builder) {
        builder.setTestFilePattern(this.myPatternComponent.getText());
    }
}
