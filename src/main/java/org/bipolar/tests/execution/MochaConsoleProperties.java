package org.bipolar.tests.execution;

import com.intellij.execution.Executor;
import com.intellij.execution.configurations.RunConfiguration;
import com.intellij.execution.process.ProcessHandler;
import com.intellij.execution.testframework.TestConsoleProperties;
import com.intellij.execution.testframework.actions.AbstractRerunFailedTestsAction;
import com.intellij.execution.testframework.sm.runner.SMTestLocator;
import com.intellij.execution.testframework.sm.runner.ui.SMTRunnerConsoleView;
import com.intellij.execution.ui.ConsoleView;
import com.intellij.javascript.testing.JsTestConsoleProperties;

import com.intellij.terminal.TerminalExecutionConsole;
import com.intellij.util.config.AbstractProperty;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class MochaConsoleProperties
        extends JsTestConsoleProperties {
    private static final String FRAMEWORK_NAME = "MochaJavaScriptTestRunner";
    private final SMTestLocator myLocator;
    private final boolean myWithTerminalConsole;

    public MochaConsoleProperties(@NotNull BipolarRunConfiguration configuration, @NotNull Executor executor, @NotNull SMTestLocator locator, boolean withTerminalConsole) {
        super((RunConfiguration) configuration, "MochaJavaScriptTestRunner", executor);
        this.myLocator = locator;
        this.myWithTerminalConsole = withTerminalConsole;
        setUsePredefinedMessageFilter(false);
        setIfUndefined((AbstractProperty) TestConsoleProperties.HIDE_PASSED_TESTS, false);
        setIfUndefined((AbstractProperty) TestConsoleProperties.HIDE_IGNORED_TEST, true);
        setIfUndefined((AbstractProperty) TestConsoleProperties.SCROLL_TO_SOURCE, true);
        setIfUndefined((AbstractProperty) TestConsoleProperties.SELECT_FIRST_DEFECT, true);
        setIdBasedTestTree(true);
        setPrintTestingStartedTime(false);
    }


    @NotNull
    public ConsoleView createConsole() {
        if (this.myWithTerminalConsole) {
            return (ConsoleView) new TerminalExecutionConsole(getProject(), null) {
                public void attachToProcess(@NotNull ProcessHandler processHandler) {
                    attachToProcess(processHandler, false);
                }
            };
        }
        return super.createConsole();
    }


    public SMTestLocator getTestLocator() {
        return this.myLocator;
    }


    @Nullable
    public AbstractRerunFailedTestsAction createRerunFailedTestsAction(ConsoleView consoleView) {
        return new MochaRerunFailedTestAction((SMTRunnerConsoleView) consoleView, this);
    }
}
