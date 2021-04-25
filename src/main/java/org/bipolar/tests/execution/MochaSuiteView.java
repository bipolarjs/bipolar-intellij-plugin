package org.bipolar.tests.execution;

import com.intellij.javascript.testFramework.util.TestFullNameView;
import com.intellij.lang.javascript.JavaScriptBundle;
import com.intellij.openapi.project.Project;

import javax.swing.JComponent;
import javax.swing.JPanel;

import org.jetbrains.annotations.NotNull;

public class MochaSuiteView extends MochaTestKindView {
    private final MochaTestFileView myTestFileView;
    private final TestFullNameView mySuiteNameView;
    private final JPanel myPanel;

    public MochaSuiteView(@NotNull Project project) {
        this.myTestFileView = new MochaTestFileView(project);
        this.mySuiteNameView = new TestFullNameView(JavaScriptBundle.message("rc.testOrSuiteScope.suite.title", new Object[0]));
        this

                .myPanel = this.myTestFileView.getFormBuilder().addLabeledComponent(JavaScriptBundle.message("rc.testOrSuiteScope.suite.label", new Object[0]), this.mySuiteNameView.getComponent()).getPanel();
    }


    @NotNull
    public JComponent getComponent() {
        return this.myPanel;
    }


    public void resetFrom(@NotNull MochaRunSettings settings) {
        this.myTestFileView.resetFrom(settings);
        this.mySuiteNameView.setNames(settings.getSuiteNames());
    }


    public void applyTo(@NotNull MochaRunSettings.Builder builder) {
        this.myTestFileView.applyTo(builder);
        builder.setSuiteNames(this.mySuiteNameView.getNames());
    }
}
