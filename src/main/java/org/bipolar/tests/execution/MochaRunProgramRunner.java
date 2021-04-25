package org.bipolar.tests.execution;

import com.intellij.execution.ExecutionException;
import com.intellij.execution.ExecutionResult;
import com.intellij.execution.configurations.RunProfile;
import com.intellij.execution.configurations.RunProfileState;
import com.intellij.execution.runners.ExecutionEnvironment;
import com.intellij.execution.runners.GenericProgramRunner;
import com.intellij.execution.runners.ProgramRunner;
import com.intellij.execution.runners.RerunTestsAction;
import com.intellij.execution.runners.RerunTestsNotification;
import com.intellij.execution.runners.RunContentBuilder;
import com.intellij.execution.ui.RunContentDescriptor;
import com.intellij.openapi.fileEditor.FileDocumentManager;
import org.jetbrains.annotations.NotNull;

public class MochaRunProgramRunner
        extends GenericProgramRunner {
    @NotNull
    public String getRunnerId() {
        return "RunnerForMochaJavaScript";
    }


    public boolean canRun(@NotNull String executorId, @NotNull RunProfile profile) {
        return ("Run".equals(executorId) && profile instanceof BipolarRunConfiguration);
    }


    protected RunContentDescriptor doExecute(@NotNull RunProfileState state, @NotNull ExecutionEnvironment environment) throws ExecutionException {
        FileDocumentManager.getInstance().saveAllDocuments();
        ExecutionResult executionResult = state.execute(environment.getExecutor(), (ProgramRunner) this);
        if (executionResult == null) {
            return null;
        }

        RunContentDescriptor descriptor = (new RunContentBuilder(executionResult, environment)).showRunContent(environment.getContentToReuse());
        RerunTestsNotification.showRerunNotification(environment.getContentToReuse(), executionResult.getExecutionConsole());
        RerunTestsAction.register(descriptor);
        return descriptor;
    }
}
