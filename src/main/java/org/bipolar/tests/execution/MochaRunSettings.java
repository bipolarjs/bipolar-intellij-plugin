package org.bipolar.tests.execution;

import com.google.common.collect.ImmutableList;
import com.intellij.execution.configuration.EnvironmentVariablesData;
import com.intellij.javascript.nodejs.interpreter.NodeJsInterpreterRef;
import com.intellij.javascript.nodejs.util.NodePackage;
import com.intellij.openapi.util.NlsSafe;
import com.intellij.openapi.util.io.FileUtil;

import java.util.List;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;


public final class MochaRunSettings {
    private final NodeJsInterpreterRef myInterpreterRef;
    private final String myNodeOptions;
    private final NodePackage myMochaPackage;
    private final String myWorkingDir;
    private final EnvironmentVariablesData myEnvData;
    private final String myUi;
    private final String myExtraMochaOptions;
    private final MochaTestKind myTestKind;
    private final String myTestDirPath;
    private final boolean myRecursive;
    private final String myTestFilePattern;
    private final String myTestFilePath;
    private final List<String> mySuiteNames;
    private final List<String> myTestNames;

    private MochaRunSettings(@NotNull Builder builder) {
        this.myInterpreterRef = builder.myInterpreterRef;
        this.myNodeOptions = builder.myNodeOptions;
        this.myMochaPackage = builder.myMochaPackage;
        this.myWorkingDir = FileUtil.toSystemIndependentName(builder.myWorkingDir);
        this.myEnvData = builder.myEnvData;
        this.myUi = builder.myUi;
        this.myExtraMochaOptions = builder.myExtraMochaOptions;
        this.myTestKind = builder.myTestKind;
        this.myTestDirPath = FileUtil.toSystemIndependentName(builder.myTestDirPath);
        this.myRecursive = builder.myRecursive;
        this.myTestFilePattern = builder.myTestFilePattern;
        this.myTestFilePath = FileUtil.toSystemIndependentName(builder.myTestFilePath);
        this.mySuiteNames = (List<String>) ImmutableList.copyOf(builder.mySuiteNames);
        this.myTestNames = (List<String>) ImmutableList.copyOf(builder.myTestNames);
    }

    @NotNull
    public NodeJsInterpreterRef getInterpreterRef() {
        return this.myInterpreterRef;
    }

    @NotNull
    public String getNodeOptions() {
        return this.myNodeOptions;
    }

    @Nullable
    public NodePackage getMochaPackage() {
        return this.myMochaPackage;
    }

    @NotNull
    public String getWorkingDir() {
        return this.myWorkingDir;
    }

    @NotNull
    public EnvironmentVariablesData getEnvData() {
        return this.myEnvData;
    }

    @NotNull
    public String getUi() {
        return this.myUi;
    }

    @NotNull
    public String getExtraMochaOptions() {
        return this.myExtraMochaOptions;
    }

    @NotNull
    public MochaTestKind getTestKind() {
        return this.myTestKind;
    }

    @NotNull
    public String getTestDirPath() {
        return this.myTestDirPath;
    }

    public boolean isRecursive() {
        return this.myRecursive;
    }

    @NotNull
    @NlsSafe
    public String getTestFilePattern() {
        return this.myTestFilePattern;
    }

    @NotNull
    public String getTestFilePath() {
        return this.myTestFilePath;
    }

    @NotNull
    public List<String> getSuiteNames() {
        return this.mySuiteNames;
    }

    @NotNull
    public List<String> getTestNames() {
        return this.myTestNames;
    }

    @NotNull
    public Builder builder() {
        return new Builder(this);
    }

    @NotNull
    public static Builder builder(@NotNull MochaRunSettings runSettings) {
        return new Builder(runSettings);
    }

    public static class Builder {
        private NodeJsInterpreterRef myInterpreterRef = NodeJsInterpreterRef.createProjectRef();
        private String myNodeOptions = "";
        private NodePackage myMochaPackage = null;
        private String myWorkingDir = "";
        private EnvironmentVariablesData myEnvData = EnvironmentVariablesData.DEFAULT;
        private String myUi = "";
        private String myExtraMochaOptions = "";
        private MochaTestKind myTestKind = MochaTestKind.DIRECTORY;
        private String myTestDirPath = "";
        private boolean myRecursive = false;
        private String myTestFilePattern = "";
        private String myTestFilePath = "";
        private List<String> mySuiteNames = ImmutableList.of();
        private List<String> myTestNames = ImmutableList.of();


        public Builder() {
        }

        public Builder(@NotNull MochaRunSettings runSettings) {
            this.myInterpreterRef = runSettings.getInterpreterRef();
            this.myNodeOptions = runSettings.getNodeOptions();
            this.myMochaPackage = runSettings.getMochaPackage();
            this.myWorkingDir = runSettings.getWorkingDir();
            this.myEnvData = runSettings.getEnvData();
            this.myUi = runSettings.getUi();
            this.myExtraMochaOptions = runSettings.getExtraMochaOptions();
            this.myTestKind = runSettings.getTestKind();
            this.myTestDirPath = runSettings.getTestDirPath();
            this.myRecursive = runSettings.isRecursive();
            this.myTestFilePattern = runSettings.getTestFilePattern();
            this.myTestFilePath = runSettings.getTestFilePath();
            this.mySuiteNames = runSettings.getSuiteNames();
            this.myTestNames = runSettings.getTestNames();
        }

        @NotNull
        public Builder setInterpreterRef(@NotNull NodeJsInterpreterRef interpreterRef) {
            this.myInterpreterRef = interpreterRef;
            return this;
        }

        @NotNull
        public Builder setNodeOptions(@NotNull String nodeOptions) {
            this.myNodeOptions = nodeOptions;
            return this;
        }

        @NotNull
        public Builder setMochaPackage(@Nullable NodePackage mochaPackage) {
            this.myMochaPackage = mochaPackage;
            return this;
        }

        @NotNull
        public Builder setWorkingDir(@NotNull String workingDir) {
            this.myWorkingDir = workingDir;
            return this;
        }

        @NotNull
        public Builder setEnvData(@NotNull EnvironmentVariablesData envData) {
            this.myEnvData = envData;
            return this;
        }

        @NotNull
        public Builder setUi(@NotNull String ui) {
            this.myUi = ui;
            return this;
        }

        @NotNull
        public Builder setExtraMochaOptions(@NotNull String extraMochaOptions) {
            this.myExtraMochaOptions = extraMochaOptions;
            return this;
        }

        @NotNull
        public Builder setTestKind(@NotNull MochaTestKind testKind) {
            this.myTestKind = testKind;
            return this;
        }

        @NotNull
        public Builder setTestDirPath(@NotNull String testDirPath) {
            this.myTestDirPath = testDirPath;
            return this;
        }

        @NotNull
        public Builder setRecursive(boolean recursive) {
            this.myRecursive = recursive;
            return this;
        }

        @NotNull
        public Builder setTestFilePattern(@NotNull String testFilePattern) {
            this.myTestFilePattern = testFilePattern;
            return this;
        }

        @NotNull
        public Builder setTestFilePath(@NotNull String testFilePath) {
            this.myTestFilePath = testFilePath;
            return this;
        }

        @NotNull
        public Builder setSuiteNames(@NotNull List<String> suiteNames) {
            this.mySuiteNames = suiteNames;
            return this;
        }

        @NotNull
        public Builder setTestNames(@NotNull List<String> testNames) {
            this.myTestNames = testNames;
            return this;
        }

        @NotNull
        public MochaRunSettings build() {
            return new MochaRunSettings(this);
        }
    }
}
