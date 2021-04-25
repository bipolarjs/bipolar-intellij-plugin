package org.bipolar.tests.execution;

import com.intellij.execution.Executor;
import com.intellij.execution.RunManager;
import com.intellij.execution.configurations.*;
import com.intellij.execution.runners.ExecutionEnvironment;
import com.intellij.execution.testframework.TestRunnerBundle;
import com.intellij.execution.testframework.sm.runner.SMRunnerConsolePropertiesProvider;
import com.intellij.execution.testframework.sm.runner.SMTRunnerConsoleProperties;
import com.intellij.javascript.JSRunProfileWithCompileBeforeLaunchOption;
import com.intellij.javascript.nodejs.debug.NodeDebugRunConfiguration;
import com.intellij.javascript.nodejs.interpreter.NodeInterpreterUtil;
import com.intellij.javascript.nodejs.interpreter.NodeJsInterpreter;
import com.intellij.javascript.nodejs.util.NodePackage;
import com.intellij.javascript.testFramework.PreferableRunConfiguration;
import com.intellij.openapi.options.SettingsEditor;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.roots.ProjectFileIndex;
import com.intellij.openapi.util.InvalidDataException;
import com.intellij.openapi.util.NlsSafe;
import com.intellij.openapi.util.WriteExternalException;
import com.intellij.openapi.util.io.FileUtil;
import com.intellij.openapi.util.text.StringUtil;
import com.intellij.openapi.vfs.LocalFileSystem;
import com.intellij.openapi.vfs.VfsUtilCore;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiElement;
import com.intellij.util.ObjectUtils;
import com.intellij.util.containers.ContainerUtil;
import com.jetbrains.nodejs.NodeJSBundle;
import org.bipolar.tests.BipolarUtil;
import org.jdom.Element;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.io.LocalFileFinder;

import java.io.File;

public class BipolarRunConfiguration extends LocatableConfigurationBase<BipolarRunConfiguration> implements JSRunProfileWithCompileBeforeLaunchOption, NodeDebugRunConfiguration, PreferableRunConfiguration, SMRunnerConsolePropertiesProvider {
    private MochaRunSettings myRunSettings = (new MochaRunSettings.Builder()).build();

    protected BipolarRunConfiguration(Project project, ConfigurationFactory factory, String name) {
        super(project, factory, name);
    }


    @NotNull
    public SettingsEditor<BipolarRunConfiguration> getConfigurationEditor() {
        return new MochaRunConfigurationEditor(getProject());
    }

    public void readExternal(@NotNull Element element) throws InvalidDataException {
        super.readExternal(element);
        this.myRunSettings = MochaRunSettingsSerializationUtil.readFromXml(element);
    }

    public void writeExternal(@NotNull Element element) throws WriteExternalException {
        super.writeExternal(element);
        MochaRunSettingsSerializationUtil.writeToXml(element, this.myRunSettings);
    }

    @Nullable
    public RunProfileState getState(@NotNull Executor executor, @NotNull ExecutionEnvironment environment) {
        return new MochaRunProfileState(getProject(), this, environment, getMochaPackage(), this.myRunSettings);
    }

    @Nullable
    public NodeJsInterpreter getInterpreter() {
        return this.myRunSettings.getInterpreterRef().resolve(getProject());
    }

    @NotNull
    NodePackage getMochaPackage() {
        if (RunManager.getInstance(getProject()).isTemplate(this)) {
            return ObjectUtils.notNull(this.myRunSettings.getMochaPackage(), new NodePackage(""));
        }
        NodePackage pkg = this.myRunSettings.getMochaPackage();
        if (pkg == null) {
            Project project = getProject();
            NodeJsInterpreter interpreter = this.myRunSettings.getInterpreterRef().resolve(project);
            pkg = BipolarUtil.PACKAGE_DESCRIPTOR.findFirstDirectDependencyPackage(project, interpreter, getContextFile());
            if (pkg.isEmptyPath()) {
                pkg = BipolarUtil.getMochaPackage(project);
            } else {

                BipolarUtil.setMochaPackage(project, pkg);
            }
            this.myRunSettings = this.myRunSettings.builder().setMochaPackage(pkg).build();
        }
        return pkg;
    }

    @Nullable
    private VirtualFile getContextFile() {
        VirtualFile f = findFile(this.myRunSettings.getTestFilePath());
        if (f == null) {
            f = findFile(this.myRunSettings.getTestDirPath());
        }
        if (f == null) {
            f = findFile(this.myRunSettings.getWorkingDir());
        }
        return f;
    }


    @NotNull
    public SMTRunnerConsoleProperties createTestConsoleProperties(@NotNull Executor executor) {
        return createTestConsoleProperties(executor, this.myRunSettings.getUi(), false);
    }

    @NotNull
    public MochaConsoleProperties createTestConsoleProperties(@NotNull Executor executor, @NotNull String ui, boolean withTerminalConsole) {
        return new MochaConsoleProperties(this, executor, new MochaTestLocationProvider(ui), withTerminalConsole);
    }

    @Nullable
    private static VirtualFile findFile(@NotNull String path) {
        return FileUtil.isAbsolute(path) ? LocalFileSystem.getInstance().findFileByPath(path) : null;
    }


    public void checkConfiguration() throws RuntimeConfigurationException {
        NodeInterpreterUtil.checkForRunConfiguration(this.myRunSettings.getInterpreterRef().resolve(getProject()));
        getMochaPackage().validateForRunConfiguration("mocha");
        validatePath(true, "working directory", this.myRunSettings.getWorkingDir(), true, true);
        if (StringUtil.isEmptyOrSpaces(this.myRunSettings.getUi())) {
            throw new RuntimeConfigurationError(NodeJSBundle.message("dialog.message.unspecified.mocha.user.interface"));
        }
        MochaTestKind testKind = this.myRunSettings.getTestKind();
        if (MochaTestKind.DIRECTORY == testKind) {
            validatePath(true, "test directory", this.myRunSettings.getTestDirPath(), false, true);
        } else if (MochaTestKind.PATTERN != testKind) {


            if (MochaTestKind.TEST_FILE == testKind || MochaTestKind.SUITE == testKind || MochaTestKind.TEST == testKind) {
                validatePath(false, "test file", this.myRunSettings.getTestFilePath(), true, false);
                if (MochaTestKind.SUITE == testKind &&
                        this.myRunSettings.getSuiteNames().isEmpty()) {
                    throw new RuntimeConfigurationError(NodeJSBundle.message("dialog.message.unspecified.suite.name"));
                }

                if (MochaTestKind.TEST == testKind &&
                        this.myRunSettings.getTestNames().isEmpty()) {
                    throw new RuntimeConfigurationError(NodeJSBundle.message("dialog.message.unspecified.test.name"));
                }
            }
        }
    }


    private static void validatePath(boolean shouldBeDirectory, @NotNull String name, @Nullable String path, boolean shouldBeAbsolute, boolean warnIfNonexistent) throws RuntimeConfigurationException {
        if (StringUtil.isEmptyOrSpaces(path)) {
            throw new RuntimeConfigurationError(NodeJSBundle.message("dialog.message.unspecified", name));
        }
        File file = new File(path);
        if (shouldBeAbsolute && !file.isAbsolute()) {
            throw new RuntimeConfigurationError(NodeJSBundle.message("dialog.message.no.such", name));
        }
        boolean exists = shouldBeDirectory ? file.isDirectory() : file.isFile();
        if (!exists) {
            if (warnIfNonexistent) {
                throw new RuntimeConfigurationWarning(NodeJSBundle.message("dialog.message.no.such", name));
            }
            throw new RuntimeConfigurationError(NodeJSBundle.message("dialog.message.no.such", name));
        }
    }


    public String suggestedName() {
        MochaRunSettings runSettings = this.myRunSettings;
        MochaTestKind testKind = runSettings.getTestKind();
        if (testKind == MochaTestKind.DIRECTORY) {
            return getRelativePath(getProject(), runSettings.getTestDirPath());
        }
        if (testKind == MochaTestKind.PATTERN) {
            return runSettings.getTestFilePattern();
        }
        if (testKind == MochaTestKind.TEST_FILE) {
            return getRelativePath(getProject(), runSettings.getTestFilePath());
        }
        if (runSettings.getTestKind() == MochaTestKind.SUITE) {
            return StringUtil.join(runSettings.getSuiteNames(), ".");
        }
        if (runSettings.getTestKind() == MochaTestKind.TEST) {
            return StringUtil.join(runSettings.getTestNames(), ".");
        }
        return TestRunnerBundle.message("all.tests.scope.presentable.text");
    }


    @Nullable
    public String getActionName() {
        MochaRunSettings runSettings = this.myRunSettings;
        MochaTestKind testKind = runSettings.getTestKind();
        if (testKind == MochaTestKind.DIRECTORY) {
            return getLastPathComponent(runSettings.getTestDirPath());
        }
        if (testKind == MochaTestKind.PATTERN) {
            return runSettings.getTestFilePattern();
        }
        if (testKind == MochaTestKind.TEST_FILE) {
            return getLastPathComponent(runSettings.getTestFilePath());
        }
        if (runSettings.getTestKind() == MochaTestKind.SUITE) {
            return StringUtil.notNullize(ContainerUtil.getLastItem(runSettings.getSuiteNames()));
        }
        if (runSettings.getTestKind() == MochaTestKind.TEST) {
            return StringUtil.notNullize(ContainerUtil.getLastItem(runSettings.getTestNames()));
        }
        return TestRunnerBundle.message("all.tests.scope.presentable.text");
    }

    @NotNull
    @NlsSafe
    private static String getRelativePath(@NotNull Project project, @NotNull String path) {
        VirtualFile file = LocalFileFinder.findFile(path);
        if (file != null && file.isValid()) {
            VirtualFile root = ProjectFileIndex.getInstance(project).getContentRootForFile(file);
            if (root != null && root.isValid()) {
                String relativePath = VfsUtilCore.getRelativePath(file, root, File.separatorChar);
                if (StringUtil.isNotEmpty(relativePath)) {
                    return relativePath;
                }
            }
        }
        return getLastPathComponent(path);
    }

    @NotNull
    @NlsSafe
    private static String getLastPathComponent(@NotNull String path) {
        int lastIndex = path.lastIndexOf('/');
        return (lastIndex >= 0) ? path.substring(lastIndex + 1) : path;
    }

    @NotNull
    public MochaRunSettings getRunSettings() {
        return this.myRunSettings;
    }

    public void setRunSettings(@NotNull MochaRunSettings runSettings) {
        NodePackage pkg = runSettings.getMochaPackage();
        if (pkg != null && pkg.isEmptyPath() && RunManager.getInstance(getProject()).isTemplate(this)) {
            runSettings = runSettings.builder().setMochaPackage(null).build();
        }
        this.myRunSettings = runSettings;
        if (pkg != null) {
            BipolarUtil.setMochaPackage(getProject(), pkg);
        }
    }


    public boolean isPreferredOver(@NotNull RunConfiguration otherRc, @NotNull PsiElement sourceElement) {
        return true;
    }


    public void onNewConfigurationCreated() {
        MochaRunSettings.Builder builder = this.myRunSettings.builder();
        if (this.myRunSettings.getUi().isEmpty()) {
            builder.setUi("bdd");
        }
        if (this.myRunSettings.getWorkingDir().trim().isEmpty()) {
            VirtualFile dir = getProject().getBaseDir();
            if (dir != null) {
                builder.setWorkingDir(dir.getPath());
            }
        }
        if (this.myRunSettings.getTestKind() == MochaTestKind.DIRECTORY && this.myRunSettings.getTestDirPath().trim().isEmpty()) {
            String workingDirPath = FileUtil.toSystemIndependentName(this.myRunSettings.getWorkingDir());
            VirtualFile workingDir = LocalFileSystem.getInstance().findFileByPath(workingDirPath);
            if (workingDir != null && workingDir.isValid() && workingDir.isDirectory()) {
                String[] testDirNames = {"test", "spec", "tests", "specs"};
                VirtualFile testDir = null;
                for (String testDirName : testDirNames) {
                    testDir = workingDir.findChild(testDirName);
                    if (testDir != null && testDir.isValid() && testDir.isDirectory()) {
                        break;
                    }
                }
                if (testDir != null) {
                    builder.setTestDirPath(testDir.getPath()).build();
                }
            }
        }
        this.myRunSettings = builder.build();
    }
}
