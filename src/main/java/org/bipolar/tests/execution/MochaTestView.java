package org.bipolar.tests.execution;

import com.intellij.javascript.testFramework.util.TestFullNameView;
import com.intellij.lang.javascript.JavaScriptBundle;
import com.intellij.openapi.project.Project;

import javax.swing.JComponent;
import javax.swing.JPanel;

import org.jetbrains.annotations.NotNull;

public class MochaTestView extends MochaTestKindView {
    private final MochaTestFileView myTestFileView;
    private final TestFullNameView myTestFullNameView;
    private final JPanel myPanel;

    public MochaTestView(@NotNull Project project) {
        this.myTestFileView = new MochaTestFileView(project);
        this.myTestFullNameView = new TestFullNameView();
        this.myPanel = this.myTestFileView.getFormBuilder().addLabeledComponent(JavaScriptBundle.message("rc.testOrSuiteScope.test.label", new Object[0]), this.myTestFullNameView.getComponent()).getPanel();
    }


    @NotNull
    public JComponent getComponent() {
        return this.myPanel;
    }


    public void resetFrom(@NotNull MochaRunSettings settings) {
        this.myTestFileView.resetFrom(settings);
        this.myTestFullNameView.setNames(settings.getTestNames());
    }


    public void applyTo(@NotNull MochaRunSettings.Builder builder) {
        this.myTestFileView.applyTo(builder);
        builder.setTestNames(this.myTestFullNameView.getNames());
    }
}
