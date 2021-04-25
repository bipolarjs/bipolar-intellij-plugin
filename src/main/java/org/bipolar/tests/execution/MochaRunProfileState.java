package org.bipolar.tests.execution;

import com.intellij.execution.DefaultExecutionResult;
import com.intellij.execution.ExecutionException;
import com.intellij.execution.ExecutionResult;
import com.intellij.execution.configurations.GeneralCommandLine;
import com.intellij.execution.configurations.ParametersList;
import com.intellij.execution.filters.Filter;
import com.intellij.execution.process.OSProcessHandler;
import com.intellij.execution.process.ProcessHandler;
import com.intellij.execution.process.ProcessTerminatedListener;
import com.intellij.execution.runners.ExecutionEnvironment;
import com.intellij.execution.testframework.TestConsoleProperties;
import com.intellij.execution.testframework.sm.SMTestRunnerConnectionUtil;
import com.intellij.execution.testframework.ui.BaseTestsOutputConsoleView;
import com.intellij.execution.ui.ConsoleView;
import com.intellij.execution.ui.ExecutionConsole;
import com.intellij.javascript.debugger.CommandLineDebugConfigurator;
import com.intellij.javascript.nodejs.NodeCommandLineUtil;
import com.intellij.javascript.nodejs.NodeConsoleAdditionalFilter;
import com.intellij.javascript.nodejs.NodeStackTraceFilter;
import com.intellij.javascript.nodejs.debug.NodeLocalDebuggableRunProfileStateSync;
import com.intellij.javascript.nodejs.interpreter.NodeInterpreterUtil;
import com.intellij.javascript.nodejs.interpreter.NodeJsInterpreter;
import com.intellij.javascript.nodejs.interpreter.local.NodeJsLocalInterpreter;
import com.intellij.javascript.nodejs.library.yarn.YarnPnpNodePackage;
import com.intellij.javascript.nodejs.util.NodePackage;
import com.intellij.javascript.testing.JSTestRunnerUtil;
import com.intellij.lang.javascript.buildTools.npm.PackageJsonUtil;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.Pair;
import com.intellij.openapi.util.io.FileUtil;
import com.intellij.openapi.util.text.StringUtil;
import com.intellij.util.execution.ParametersListUtil;

import com.jetbrains.nodejs.NodeJSBundle;
import org.bipolar.tests.BipolarUtil;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class MochaRunProfileState
        extends NodeLocalDebuggableRunProfileStateSync {
    private final Project myProject;
    private final BipolarRunConfiguration myRunConfiguration;
    private final ExecutionEnvironment myEnv;
    private final NodePackage myMochaPackage;
    private final MochaRunSettings myRunSettings;
    private List<List<String>> myRerunActionFailedTests;

    public MochaRunProfileState(@NotNull Project project, @NotNull BipolarRunConfiguration runConfiguration, @NotNull ExecutionEnvironment env, @NotNull NodePackage mochaPackage, @NotNull MochaRunSettings runSettings) {
        this.myProject = project;
        this.myRunConfiguration = runConfiguration;
        this.myEnv = env;
        this.myMochaPackage = mochaPackage;
        this.myRunSettings = runSettings;
    }


    @NotNull
    protected ExecutionResult executeSync(@Nullable CommandLineDebugConfigurator configurator) throws ExecutionException {
        NodeJsInterpreter interpreter = this.myRunSettings.getInterpreterRef().resolveNotNull(this.myProject);
        GeneralCommandLine commandLine = NodeCommandLineUtil.createCommandLineForTestTools();
        NodeCommandLineUtil.configureCommandLine(commandLine, configurator, interpreter, debugMode -> configureCommandLine(commandLine, interpreter, debugMode.booleanValue()));


        OSProcessHandler processHandler = NodeCommandLineUtil.createProcessHandler(commandLine, false);
        MochaConsoleProperties consoleProperties = this.myRunConfiguration.createTestConsoleProperties(this.myEnv
                .getExecutor(), this.myRunSettings.getUi(), NodeCommandLineUtil.shouldUseTerminalConsole((ProcessHandler) processHandler));

        ConsoleView consoleView = createSMTRunnerConsoleView(commandLine.getWorkDirectory(), consoleProperties);
        ProcessTerminatedListener.attach((ProcessHandler) processHandler);
        consoleView.attachToProcess((ProcessHandler) processHandler);

        DefaultExecutionResult executionResult = new DefaultExecutionResult((ExecutionConsole) consoleView, (ProcessHandler) processHandler);
        executionResult.setRestartActions(new AnAction[]{(AnAction) consoleProperties.createRerunFailedTestsAction(consoleView)});
        return (ExecutionResult) executionResult;
    }

    @NotNull
    private ConsoleView createSMTRunnerConsoleView(@Nullable File workingDirectory, @NotNull MochaConsoleProperties consoleProperties) {
        BaseTestsOutputConsoleView baseTestsOutputConsoleView = SMTestRunnerConnectionUtil.createConsole(consoleProperties.getTestFrameworkName(), (TestConsoleProperties) consoleProperties);
        consoleProperties.addStackTraceFilter((Filter) new NodeStackTraceFilter(this.myProject, workingDirectory));
        for (Filter filter : consoleProperties.getStackTrackFilters()) {
            baseTestsOutputConsoleView.addMessageFilter(filter);
        }
        baseTestsOutputConsoleView.addMessageFilter((Filter) new NodeConsoleAdditionalFilter(this.myProject, workingDirectory));
        return (ConsoleView) baseTestsOutputConsoleView;
    }

    private void configureCommandLine(@NotNull GeneralCommandLine commandLine, @NotNull NodeJsInterpreter interpreter, boolean debugMode) throws ExecutionException {
        List<String> nodeOptions = new ArrayList<>(commandLine.getParametersList().getParameters());
        commandLine.getParametersList().clearAll();
        commandLine.setCharset(StandardCharsets.UTF_8);
        if (!StringUtil.isEmptyOrSpaces(this.myRunSettings.getWorkingDir())) {
            commandLine.withWorkDirectory(this.myRunSettings.getWorkingDir());
        }
        NodeCommandLineUtil.configureUsefulEnvironment(commandLine);
        NodeCommandLineUtil.prependNodeDirToPATH(commandLine, interpreter);
        this.myRunSettings.getEnvData().configureCommandLine(commandLine, true);

        boolean separateMochaArgs = false;

        if (this.myMochaPackage instanceof YarnPnpNodePackage) {
            ((YarnPnpNodePackage) this.myMochaPackage).addYarnRunToCommandLine(commandLine, this.myProject, interpreter, null);
        } else {

            commandLine.addParameter(getMochaMainJsFile(interpreter, this.myMochaPackage).getAbsolutePath());
        }

        if (BipolarUtil.isVueCliService(this.myMochaPackage)) {
            commandLine.addParameter("test:unit");
        }

        commandLine.addParameters(nodeOptions);
        commandLine.addParameters(ParametersListUtil.parse(this.myRunSettings.getNodeOptions().trim()));

        if (separateMochaArgs) {
            commandLine.addParameter("--");
        }

        List<String> extraMochaOptionList = ParametersListUtil.parse(this.myRunSettings.getExtraMochaOptions().trim());
        commandLine.addParameters(extraMochaOptionList);

        if (debugMode) {


            commandLine.addParameter("--timeout");
            commandLine.addParameter("0");
        }


        commandLine.addParameter("--ui");
        commandLine.addParameter(this.myRunSettings.getUi());

        commandLine.addParameter("--webstorm");

        MochaTestKind testKind = this.myRunSettings.getTestKind();
        if (MochaTestKind.DIRECTORY == testKind) {
            commandLine.addParameter(FileUtil.toSystemDependentName(this.myRunSettings.getTestDirPath()));
            if (this.myRunSettings.isRecursive()) {
                commandLine.addParameter("--recursive");
            }
        } else if (MochaTestKind.PATTERN == testKind) {
            String pattern = this.myRunSettings.getTestFilePattern();
            if (!StringUtil.isEmptyOrSpaces(pattern)) {
                commandLine.addParameters(ParametersList.parse(NodeInterpreterUtil.toRemoteName(pattern, interpreter)));
            }
        } else if (MochaTestKind.TEST_FILE == testKind || MochaTestKind.SUITE == testKind || MochaTestKind.TEST == testKind) {
            commandLine.addParameter(FileUtil.toSystemDependentName(this.myRunSettings.getTestFilePath()));
        }
        String grepPattern = getGrepPattern(testKind);
        if (grepPattern != null) {
            commandLine.addParameter("--grep");
            commandLine.addParameter(grepPattern);
        }
    }

    @Nullable
    private String getGrepPattern(@NotNull MochaTestKind testKind) {
        if (this.myRerunActionFailedTests != null) {
            return JSTestRunnerUtil.getTestsPattern(this.myRerunActionFailedTests, false);
        }
        if (MochaTestKind.SUITE == testKind) {
            return JSTestRunnerUtil.buildTestNamesPattern(this.myProject, this.myRunSettings
                    .getTestFilePath(), this.myRunSettings
                    .getSuiteNames(), true);
        }

        if (MochaTestKind.TEST == testKind) {
            return JSTestRunnerUtil.buildTestNamesPattern(this.myProject, this.myRunSettings
                    .getTestFilePath(), this.myRunSettings
                    .getTestNames(), false);
        }

        return null;
    }

    @NotNull
    public MochaRunSettings getRunSettings() {
        return this.myRunSettings;
    }

    public void setFailedTests(@NotNull List<List<String>> rerunActionFailedTests) {
        this.myRerunActionFailedTests = rerunActionFailedTests;
    }

    @NotNull
    public static File getMochaMainJsFile(@NotNull NodeJsInterpreter interpreter, @NotNull NodePackage mochaPackage) throws ExecutionException {
        String packageName = mochaPackage.getName();
        if ("electron-mocha".equals(packageName)) {
            File mainJsFile;
            if (interpreter instanceof NodeJsLocalInterpreter && ((NodeJsLocalInterpreter) interpreter).isElectron()) {
                mainJsFile = new File(mochaPackage.getSystemDependentPath(), "index.js");
            } else {

                mainJsFile = new File(mochaPackage.getSystemDependentPath(), "bin" + File.separator + "electron-mocha");
            }
            if (mainJsFile.isFile()) {
                return mainJsFile;
            }
        }
        List<Pair<String, String>> binaries = new ArrayList<>();
        if (BipolarUtil.isVueCliService(mochaPackage)) {
            binaries.add(Pair.create("vue-cli-service", "bin/vue-cli-service.js"));
        } else if ("mocha-webpack".equals(packageName)) {
            binaries.add(Pair.create("mocha-webpack", "bin/mocha-webpack"));
        } else {

            binaries.add(Pair.create(PackageJsonUtil.guessDefaultBinaryNameOfDependency(mochaPackage), null));
        }
        binaries.add(Pair.create("mocha", "bin/mocha"));
        for (Pair<String, String> binary : binaries) {
            File mainJsFile = mochaPackage.findBinFile((String) binary.first, (String) binary.second);
            if (mainJsFile != null && mainJsFile.isFile()) {
                return mainJsFile;
            }
        }
        throw new ExecutionException(NodeJSBundle.message("rc.mocha.package_bin_file_not_found.message", new Object[]{packageName}));
    }
}

