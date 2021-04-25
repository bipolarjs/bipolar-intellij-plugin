package org.bipolar.tests.execution;

import com.intellij.execution.Executor;
import com.intellij.execution.configurations.RunConfigurationBase;
import com.intellij.execution.configurations.RunProfileState;
import com.intellij.execution.runners.ExecutionEnvironment;
import com.intellij.execution.testframework.AbstractTestProxy;
import com.intellij.execution.testframework.TestConsoleProperties;
import com.intellij.execution.testframework.TestFrameworkRunningModel;
import com.intellij.execution.testframework.actions.AbstractRerunFailedTestsAction;
import com.intellij.execution.testframework.sm.runner.ui.SMTRunnerConsoleView;
import com.intellij.javascript.testFramework.util.EscapeUtils;
import com.intellij.openapi.ui.ComponentContainer;
import com.intellij.openapi.vfs.VirtualFileManager;

import java.util.ArrayList;
import java.util.List;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class MochaRerunFailedTestAction extends AbstractRerunFailedTestsAction {
    public MochaRerunFailedTestAction(@NotNull SMTRunnerConsoleView consoleView, @NotNull MochaConsoleProperties consoleProperties) {
        super((ComponentContainer) consoleView);
        init((TestConsoleProperties) consoleProperties);
        setModel((TestFrameworkRunningModel) consoleView.getResultsViewer());
    }


    @Nullable
    protected AbstractRerunFailedTestsAction.MyRunProfile getRunProfile(@NotNull ExecutionEnvironment environment) {
        BipolarRunConfiguration configuration = (BipolarRunConfiguration) this.myConsoleProperties.getConfiguration();

        final MochaRunProfileState state = new MochaRunProfileState(configuration.getProject(), configuration, environment, configuration.getMochaPackage(), configuration.getRunSettings());
        state.setFailedTests(convertToTestFqns(getFailedTests(configuration.getProject())));
        return new AbstractRerunFailedTestsAction.MyRunProfile((RunConfigurationBase) configuration) {
            public RunProfileState getState(@NotNull Executor executor, @NotNull ExecutionEnvironment environment) {
                return (RunProfileState) state;
            }
        };
    }

    @NotNull
    private static List<List<String>> convertToTestFqns(@NotNull List<AbstractTestProxy> tests) {
        List<List<String>> result = new ArrayList<>();
        for (AbstractTestProxy test : tests) {
            List<String> fqn = convertToTestFqn(test);
            if (fqn != null) {
                result.add(fqn);
            }
        }
        return result;
    }

    @Nullable
    private static List<String> convertToTestFqn(@NotNull AbstractTestProxy test) {
        String url = test.getLocationUrl();
        if (test.isLeaf() && url != null) {
            List<String> testFqn = EscapeUtils.split(VirtualFileManager.extractPath(url), '.');
            if (!testFqn.isEmpty()) {
                return testFqn;
            }
        }
        return null;
    }
}
