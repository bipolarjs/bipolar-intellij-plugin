package org.bipolar.tests.execution;

import com.intellij.execution.RunManager;
import com.intellij.execution.actions.ConfigurationContext;
import com.intellij.execution.actions.ConfigurationFromContext;
import com.intellij.execution.configurations.ConfigurationFactory;
import com.intellij.execution.configurations.ConfigurationType;
import com.intellij.execution.configurations.RunConfiguration;
import com.intellij.ide.plugins.IdeaPluginDescriptor;
import com.intellij.ide.plugins.PluginManager;
import com.intellij.javascript.nodejs.PackageJsonData;
import com.intellij.javascript.nodejs.util.NodePackage;
import com.intellij.javascript.testFramework.JsTestElementPath;
import com.intellij.javascript.testFramework.PreferableRunConfiguration;
import com.intellij.javascript.testFramework.interfaces.mochaTdd.MochaTddFileStructure;
import com.intellij.javascript.testFramework.interfaces.mochaTdd.MochaTddFileStructureBuilder;
import com.intellij.javascript.testFramework.jasmine.JasmineFileStructure;
import com.intellij.javascript.testFramework.jasmine.JasmineFileStructureBuilder;
import com.intellij.javascript.testing.JsTestRunConfigurationProducer;
import com.intellij.lang.javascript.buildTools.npm.PackageJsonUtil;
import com.intellij.lang.javascript.ecmascript6.TypeScriptUtil;
import com.intellij.lang.javascript.psi.JSFile;
import com.intellij.lang.javascript.psi.JSTestFileType;
import com.intellij.lang.javascript.psi.util.JSProjectUtil;
import com.intellij.openapi.extensions.PluginId;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.roots.ProjectFileIndex;
import com.intellij.openapi.util.Pair;
import com.intellij.openapi.util.Ref;
import com.intellij.openapi.util.TextRange;
import com.intellij.openapi.util.io.FileUtil;
import com.intellij.openapi.util.registry.Registry;
import com.intellij.openapi.util.text.StringUtil;
import com.intellij.openapi.vfs.LocalFileSystem;
import com.intellij.openapi.vfs.VfsUtilCore;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.util.PsiUtilCore;
import com.intellij.util.ObjectUtils;
import com.intellij.util.SmartList;
import org.bipolar.tests.BipolarUtil;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class MochaRunConfigurationProducer extends JsTestRunConfigurationProducer<BipolarRunConfiguration> {
    public MochaRunConfigurationProducer() {
        super(BipolarUtil.PACKAGE_DESCRIPTOR, getStopPackageNames());
    }


    @NotNull
    public ConfigurationFactory getConfigurationFactory() {
        return (ConfigurationFactory) BipolarConfigurationType.getInstance();
    }

    @NotNull
    private static List<String> getStopPackageNames() {
        IdeaPluginDescriptor karma = PluginManager.getInstance().findEnabledPlugin(PluginId.getId("Karma"));
        if (karma != null && karma.isEnabled()) {
            return Collections.singletonList("karma");
        }
        return (List) Collections.emptyList();
    }

    private boolean isActiveFor(@NotNull PsiElement element, @NotNull ConfigurationContext context) {
        VirtualFile file = PsiUtilCore.getVirtualFile(element);
        if (file == null) {
            return false;
        }
        if (isTestRunnerPackageAvailableFor(element, context)) {
            return true;
        }
        List<VirtualFile> roots = collectMochaTestRoots(element.getProject());
        if (roots.isEmpty()) {
            return false;
        }
        Set<VirtualFile> dirs = new HashSet<>();
        for (VirtualFile root : roots) {
            if (root.isDirectory()) {
                dirs.add(root);
                continue;
            }
            if (root.equals(file)) {
                return true;
            }
        }
        return VfsUtilCore.isUnder(file, dirs);
    }

    @NotNull
    private static List<VirtualFile> collectMochaTestRoots(@NotNull Project project) {
        List<RunConfiguration> list = RunManager.getInstance(project).getConfigurationsList((ConfigurationType) BipolarConfigurationType.getInstance());
        SmartList<VirtualFile> smartList = new SmartList();
        for (RunConfiguration configuration : list) {
            if (configuration instanceof BipolarRunConfiguration) {
                MochaRunSettings settings = ((BipolarRunConfiguration) configuration).getRunSettings();
                String path = null;
                if (settings.getTestKind() == MochaTestKind.DIRECTORY) {
                    path = settings.getTestDirPath();
                } else if (settings.getTestKind() == MochaTestKind.TEST_FILE || settings
                        .getTestKind() == MochaTestKind.SUITE || settings
                        .getTestKind() == MochaTestKind.TEST) {
                    path = settings.getTestFilePath();
                }
                if (!StringUtil.isEmptyOrSpaces(path)) {
                    VirtualFile dir = LocalFileSystem.getInstance().findFileByPath(path);
                    if (dir != null) {
                        smartList.add(dir);
                    }
                }
            }
        }
        return (List<VirtualFile>) smartList;
    }


    protected boolean setupConfigurationFromCompatibleContext(@NotNull BipolarRunConfiguration configuration, @NotNull ConfigurationContext context, @NotNull Ref<PsiElement> sourceElement) {
        PsiElement element = context.getPsiLocation();
        if (element == null) {
            return false;
        }
        if (!isActiveFor(element, context)) {
            return false;
        }
        TestElementInfo elementRunInfo = createTestElementRunInfo(element, configuration.getRunSettings());
        if (elementRunInfo == null) {
            return false;
        }
        MochaRunSettings runSettings = elementRunInfo.getRunSettings();
        if (runSettings.getTestKind() == MochaTestKind.DIRECTORY) {
            return false;
        }
        if (StringUtil.isEmptyOrSpaces(runSettings.getExtraMochaOptions())) {
            String compilerMochaOption = getLanguageCompilerMochaOption(runSettings, getOriginalPsiFile(element));
            if (compilerMochaOption != null) {
                runSettings = runSettings.builder().setExtraMochaOptions(compilerMochaOption).build();
            }
        }
        configuration.setRunSettings(runSettings);
        sourceElement.set(elementRunInfo.getEnclosingTestElement());
        configuration.setGeneratedName();
        return true;
    }

    @Nullable
    private static PsiFile getOriginalPsiFile(@Nullable PsiElement element) {
        PsiFile file = (element != null) ? element.getContainingFile() : null;
        return (file != null) ? file.getOriginalFile() : null;
    }

    @Nullable
    private static String getLanguageCompilerMochaOption(@NotNull MochaRunSettings runSettings, @Nullable PsiFile psiFile) {
        if (!Registry.is("mocha.add.option.require.ts-node/register")) {
            return null;
        }
        if (psiFile == null) {
            return null;
        }
        VirtualFile workingDir = LocalFileSystem.getInstance().findFileByPath(runSettings.getWorkingDir());
        if (workingDir == null || !workingDir.isValid()) {
            return null;
        }
        VirtualFile packageJsonWithConfig = (VirtualFile) PackageJsonUtil.processUpPackageJsonFilesAndFindFirst(psiFile.getProject(), workingDir, packageJson -> PackageJsonData.getOrCreate(packageJson).getTopLevelProperties().contains("mocha") ? packageJson : null);


        if (packageJsonWithConfig != null) {
            return null;
        }
        String[] mochaConfigPaths = {
                ".mocharc.js", ".mocharc.cjs", ".mocharc.yaml", ".mocharc.yml", ".mocharc.json", ".mocharc.jsonc", "test/mocha.opts", "mocha.opts"
        };


        for (String mochaOptsPath : mochaConfigPaths) {
            VirtualFile mochaConfigFile = workingDir.findFileByRelativePath(mochaOptsPath);
            if (mochaConfigFile != null && mochaConfigFile.isValid() && !mochaConfigFile.isDirectory()) {
                return null;
            }
        }
        Project project = psiFile.getProject();
        NodePackage pkg = BipolarUtil.getMochaPackage(project);
        if (pkg.nameMatches("mocha-webpack")) {
            return null;
        }
        if (TypeScriptUtil.TYPESCRIPT_FILE_TYPES.contains(psiFile.getFileType()) && hasAnyDependencyOn(project, workingDir, "ts-node")) {
            return "--require ts-node/register";
        }
        return null;
    }


    private static boolean hasAnyDependencyOn(@NotNull Project project, @NotNull VirtualFile contextFileOrDir, @NotNull String dependencyName) {
        Ref<Boolean> result = Ref.create(Boolean.valueOf(false));
        PackageJsonUtil.processUpPackageJsonFiles(project, contextFileOrDir, packageJson -> {
            PackageJsonData data = PackageJsonData.getOrCreate(packageJson);
            if (data.isDependencyOfAnyType(dependencyName)) {
                result.set(Boolean.valueOf(true));
                return false;
            }
            return true;
        });
        return ((Boolean) result.get()).booleanValue();
    }

    @Nullable
    private static Pair<String, JsTestElementPath> createSuiteOrTestData(@NotNull PsiElement element) {
        if (element instanceof com.intellij.psi.PsiFileSystemItem) {
            return null;
        }
        JSFile jsFile = (JSFile) ObjectUtils.tryCast(element.getContainingFile(), JSFile.class);
        TextRange textRange = element.getTextRange();
        if (jsFile == null || textRange == null) {
            return null;
        }
        JasmineFileStructure jasmineStructure = (JasmineFileStructure) JasmineFileStructureBuilder.getInstance().fetchCachedTestFileStructure(jsFile);
        JsTestElementPath path = jasmineStructure.findTestElementPath(textRange);
        if (path != null) {
            return Pair.create("bdd", path);
        }
        MochaTddFileStructure tddStructure = (MochaTddFileStructure) MochaTddFileStructureBuilder.getInstance().fetchCachedTestFileStructure(jsFile);
        path = tddStructure.findTestElementPath(textRange);
        if (path != null) {
            if (tddStructure.hasMochaTypeScriptDeclarations()) {
                return Pair.create("mocha-typescript", path);
            }
            return Pair.create("tdd", path);
        }
        return null;
    }


    protected boolean isConfigurationFromCompatibleContext(@NotNull BipolarRunConfiguration configuration, @NotNull ConfigurationContext context) {
        PsiElement element = context.getPsiLocation();
        if (element == null) {
            return false;
        }
        TestElementInfo elementRunInfo = createTestElementRunInfo(element, configuration.getRunSettings());
        if (elementRunInfo == null) {
            return false;
        }
        MochaRunSettings thisRunSettings = elementRunInfo.getRunSettings();
        MochaRunSettings thatRunSettings = configuration.getRunSettings();
        if (thisRunSettings.getTestKind() != thatRunSettings.getTestKind()) {
            return false;
        }
        MochaTestKind testKind = thisRunSettings.getTestKind();
        if (testKind == MochaTestKind.DIRECTORY) {
            return thisRunSettings.getTestDirPath().equals(thatRunSettings.getTestDirPath());
        }
        if (testKind == MochaTestKind.PATTERN) {
            return thisRunSettings.getTestFilePattern().equals(thatRunSettings.getTestFilePattern());
        }
        if (testKind == MochaTestKind.TEST_FILE) {
            return thisRunSettings.getTestFilePath().equals(thatRunSettings.getTestFilePath());
        }
        if (testKind == MochaTestKind.SUITE) {
            return (thisRunSettings.getTestFilePath().equals(thatRunSettings.getTestFilePath()) && thisRunSettings
                    .getSuiteNames().equals(thatRunSettings.getSuiteNames()));
        }
        if (testKind == MochaTestKind.TEST) {
            return (thisRunSettings.getTestFilePath().equals(thatRunSettings.getTestFilePath()) && thisRunSettings
                    .getTestNames().equals(thatRunSettings.getTestNames()));
        }
        return false;
    }

    @Nullable
    private static TestElementInfo createTestElementRunInfo(@NotNull PsiElement element, @NotNull MochaRunSettings templateRunSettings) {
        VirtualFile virtualFile = PsiUtilCore.getVirtualFile(element);
        if (virtualFile == null) {
            return null;
        }
        Pair<String, JsTestElementPath> pair = createSuiteOrTestData(element);
        if (StringUtil.isEmptyOrSpaces(templateRunSettings.getWorkingDir())) {
            String workingDir = guessWorkingDir(element.getProject(), virtualFile);
            templateRunSettings = templateRunSettings.builder().setWorkingDir(workingDir).build();
        }
        if (pair == null) {
            return createFileInfo(element, virtualFile, templateRunSettings);
        }
        MochaRunSettings.Builder builder = templateRunSettings.builder();
        builder.setTestFilePath(virtualFile.getPath());
        if (templateRunSettings.getUi().isEmpty()) {
            builder.setUi((String) pair.getFirst());
        }
        JsTestElementPath testElementPath = (JsTestElementPath) pair.getSecond();
        String testName = testElementPath.getTestName();
        if (testName == null) {
            builder.setTestKind(MochaTestKind.SUITE);
            builder.setSuiteNames(testElementPath.getSuiteNames());
        } else {

            builder.setTestKind(MochaTestKind.TEST);
            List<String> names = new ArrayList<>(testElementPath.getSuiteNames());
            names.add(testName);
            builder.setTestNames(names);
        }
        return new TestElementInfo(builder.build(), testElementPath.getTestElement());
    }

    @NotNull
    private static String guessWorkingDir(@NotNull Project project, @NotNull VirtualFile contextFile) {
        VirtualFile configFile = JSProjectUtil.findFileUpToContentRoot(project, contextFile, new String[]{"package.json"});
        VirtualFile workingDir = (configFile != null) ? configFile.getParent() : null;
        if (workingDir == null) {
            workingDir = ProjectFileIndex.getInstance(project).getContentRootForFile(contextFile);
        }
        if (workingDir == null) {
            workingDir = contextFile.getParent();
        }
        return (workingDir != null) ? FileUtil.toSystemDependentName(workingDir.getPath()) : "";
    }


    @Nullable
    private static TestElementInfo createFileInfo(@NotNull PsiElement element, @NotNull VirtualFile virtualFile, @NotNull MochaRunSettings templateRunSettings) {
        if (virtualFile.isDirectory()) {
            MochaRunSettings.Builder builder = templateRunSettings.builder();
            builder.setTestKind(MochaTestKind.DIRECTORY);
            builder.setTestDirPath(virtualFile.getPath());
            return new TestElementInfo(builder.build(), element);
        }
        JSFile psiFile = (JSFile) ObjectUtils.tryCast(element.getContainingFile(), JSFile.class);
        JSTestFileType testFileType = (psiFile == null) ? null : psiFile.getTestFileType();
        if (psiFile != null && testFileType != null) {
            MochaRunSettings.Builder builder = templateRunSettings.builder();
            builder.setTestKind(MochaTestKind.TEST_FILE);
            builder.setTestFilePath(virtualFile.getPath());
            if (templateRunSettings.getUi().isEmpty()) {
                String ui = BipolarUtil.findUi(psiFile);
                builder.setUi((String) ObjectUtils.notNull(ui, "bdd"));
            }
            return new TestElementInfo(builder.build(), (PsiElement) psiFile);
        }
        return null;
    }


    public boolean isPreferredConfiguration(ConfigurationFromContext self, ConfigurationFromContext other) {
        if (other != null) {
            PreferableRunConfiguration otherRc = (PreferableRunConfiguration) ObjectUtils.tryCast(other.getConfiguration(), PreferableRunConfiguration.class);
            if (otherRc != null && otherRc.isPreferredOver(self.getConfiguration(), self.getSourceElement())) {
                return false;
            }
        }
        return true;
    }

    private static class TestElementInfo {
        private final MochaRunSettings myRunSettings;
        private final PsiElement myEnclosingTestElement;

        TestElementInfo(@NotNull MochaRunSettings runSettings, @NotNull PsiElement enclosingTestElement) {
            this.myRunSettings = runSettings;
            this.myEnclosingTestElement = enclosingTestElement;
        }

        @NotNull
        public MochaRunSettings getRunSettings() {
            return this.myRunSettings;
        }

        @NotNull
        public PsiElement getEnclosingTestElement() {
            return this.myEnclosingTestElement;
        }
    }
}

