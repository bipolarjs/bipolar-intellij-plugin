package org.bipolar.tests.execution;

import com.intellij.execution.Location;
import com.intellij.execution.PsiLocation;
import com.intellij.execution.testframework.sm.runner.SMTestLocator;
import com.intellij.javascript.testFramework.JsTestFileByTestNameIndex;
import com.intellij.javascript.testFramework.exports.ExportsTestFileStructure;
import com.intellij.javascript.testFramework.exports.ExportsTestFileStructureBuilder;
import com.intellij.javascript.testFramework.interfaces.mochaTdd.MochaTddFileStructure;
import com.intellij.javascript.testFramework.interfaces.mochaTdd.MochaTddFileStructureBuilder;
import com.intellij.javascript.testFramework.jasmine.JasmineFileStructure;
import com.intellij.javascript.testFramework.jasmine.JasmineFileStructureBuilder;
import com.intellij.javascript.testFramework.qunit.QUnitFileStructure;
import com.intellij.javascript.testFramework.qunit.QUnitFileStructureBuilder;
import com.intellij.javascript.testFramework.util.EscapeUtils;
import com.intellij.javascript.testFramework.util.JsTestFqn;
import com.intellij.lang.javascript.psi.JSFile;
import com.intellij.lang.javascript.psi.JSTestFileType;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.io.FileUtil;
import com.intellij.openapi.util.text.StringUtil;
import com.intellij.openapi.vfs.LocalFileSystem;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiManager;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.util.ObjectUtils;
import com.intellij.util.containers.ContainerUtil;

import java.util.Collections;
import java.util.List;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;


public class MochaTestLocationProvider implements SMTestLocator {
    private static final String SUITE_PROTOCOL_ID = "suite";
    private static final String TEST_PROTOCOL_ID = "test";
    private static final char SPLIT_CHAR = '.';
    private final String myUi;

    public MochaTestLocationProvider(@NotNull String ui) {
        this.myUi = ui;
    }


    @NotNull
    public List<Location> getLocation(@NotNull String protocol, @NotNull String path, @Nullable String metaInfo, @NotNull Project project, @NotNull GlobalSearchScope scope) {
        boolean suite = "suite".equals(protocol);
        if (suite || "test".equals(protocol)) {
            Location location = getTestLocation(project, path, metaInfo, suite);
            return ContainerUtil.createMaybeSingletonList(location);
        }

        return (List) Collections.emptyList();
    }


    @NotNull
    public List<Location> getLocation(@NotNull String protocol, @NotNull String path, @NotNull Project project, @NotNull GlobalSearchScope scope) {
        throw new IllegalStateException("Should not be called");
    }


    @Nullable
    private Location getTestLocation(@NotNull Project project, @NotNull String locationData, @Nullable String testFilePath, boolean isSuite) {
        PsiElement psiElement;
        List<String> path = EscapeUtils.split(locationData, '.');
        if (path.isEmpty()) {
            return null;
        }


        if ("bdd".equals(this.myUi)) {
            psiElement = findJasmineElement(project, path, testFilePath);
        } else if ("qunit".equals(this.myUi)) {
            psiElement = findQUnitElement(project, path, testFilePath, isSuite);
        } else if ("exports".equals(this.myUi)) {
            psiElement = findExportsElement(project, path, testFilePath);
        } else if ("tdd".equals(this.myUi) || "mocha-typescript".equals(this.myUi)) {
            psiElement = findTddElement(project, path, testFilePath, isSuite);
        } else {

            psiElement = findAppropriateElement(project, path, testFilePath, isSuite);
        }
        if (psiElement != null) {
            return PsiLocation.fromPsiElement(psiElement);
        }
        return null;
    }

    private PsiElement findAppropriateElement(Project project, @NotNull List<String> path, @Nullable String testFilePath, boolean suite) {
        String lowerCasedUi = StringUtil.toLowerCase(this.myUi);

        boolean bdd = false, qUnit = false, exports = false;
        if (lowerCasedUi.contains("bdd")) {
            bdd = true;
            PsiElement element = findJasmineElement(project, path, testFilePath);
            if (element != null) {
                return element;
            }
        }
        if (lowerCasedUi.contains("qunit")) {
            qUnit = true;
            PsiElement element = findQUnitElement(project, path, testFilePath, suite);
            if (element != null) {
                return element;
            }
        }
        if (lowerCasedUi.contains("exports")) {
            exports = true;
            PsiElement element = findExportsElement(project, path, testFilePath);
            if (element != null) {
                return element;
            }
        }
        if (!bdd) {
            PsiElement element = findJasmineElement(project, path, testFilePath);
            if (element != null) {
                return element;
            }
        }
        if (!qUnit) {
            PsiElement element = findQUnitElement(project, path, testFilePath, suite);
            if (element != null) {
                return element;
            }
        }
        if (!exports) {
            PsiElement element = findExportsElement(project, path, testFilePath);
            if (element != null) {
                return element;
            }
        }
        return null;
    }

    @Nullable
    private static PsiElement findJasmineElement(Project project, @NotNull List<String> location, @Nullable String testFilePath) {
        VirtualFile executedFile = findFile(testFilePath);
        JsTestFqn testFqn = new JsTestFqn(JSTestFileType.JASMINE, location);
        GlobalSearchScope scope = GlobalSearchScope.projectScope(project);
        List<VirtualFile> jsTestVirtualFiles = JsTestFileByTestNameIndex.findFiles(testFqn, scope, executedFile);
        for (VirtualFile file : jsTestVirtualFiles) {
            PsiFile psiFile = PsiManager.getInstance(project).findFile(file);
            if (psiFile instanceof JSFile) {
                JSFile jsFile = (JSFile) psiFile;
                JasmineFileStructureBuilder builder = JasmineFileStructureBuilder.getInstance();
                JasmineFileStructure jasmineFileStructure = (JasmineFileStructure) builder.fetchCachedTestFileStructure(jsFile);
                PsiElement element = jasmineFileStructure.findPsiElement(testFqn.getNames(), null);
                if (element != null && element.isValid()) {
                    return element;
                }
            }
        }
        return null;
    }

    @Nullable
    private static VirtualFile findFile(@Nullable String filePath) {
        if (StringUtil.isEmptyOrSpaces(filePath)) return null;
        return LocalFileSystem.getInstance().findFileByPath(FileUtil.toSystemIndependentName(filePath));
    }

    @Nullable
    private static PsiElement findQUnitElement(@NotNull Project project, @NotNull List<String> location, @Nullable String testFilePath, boolean suite) {
        String moduleName, testName;
        VirtualFile executedFile = findFile(testFilePath);

        if (suite) {
            moduleName = location.get(0);
            testName = null;

        } else if (location.size() > 1) {
            moduleName = location.get(0);
            testName = location.get(1);
        } else {

            moduleName = "Default Module";
            testName = location.get(0);
        }

        String key = JsTestFileByTestNameIndex.createQUnitKey(moduleName, testName);
        GlobalSearchScope scope = GlobalSearchScope.projectScope(project);
        List<VirtualFile> jsTestVirtualFiles = JsTestFileByTestNameIndex.findFilesByKey(key, scope, executedFile);
        for (VirtualFile file : jsTestVirtualFiles) {
            PsiFile psiFile = PsiManager.getInstance(project).findFile(file);
            if (psiFile instanceof JSFile) {
                JSFile jsFile = (JSFile) psiFile;
                QUnitFileStructureBuilder builder = QUnitFileStructureBuilder.getInstance();
                QUnitFileStructure qunitFileStructure = (QUnitFileStructure) builder.fetchCachedTestFileStructure(jsFile);
                PsiElement element = qunitFileStructure.findPsiElement(moduleName, testName);
                if (element != null && element.isValid()) {
                    return element;
                }
            }
        }
        return null;
    }

    @Nullable
    private static PsiElement findExportsElement(@NotNull Project project, @NotNull List<String> location, @Nullable String testFilePath) {
        JSFile file = findJSFile(project, testFilePath);
        if (file == null) {
            return null;
        }
        ExportsTestFileStructure structure = (ExportsTestFileStructure) ExportsTestFileStructureBuilder.getInstance().fetchCachedTestFileStructure(file);
        return structure.findPsiElement(location);
    }


    @Nullable
    private static PsiElement findTddElement(@NotNull Project project, @NotNull List<String> location, @Nullable String testFilePath, boolean suite) {
        VirtualFile executedFile = findFile(testFilePath);
        GlobalSearchScope scope = GlobalSearchScope.projectScope(project);
        JsTestFqn testFqn = new JsTestFqn(JSTestFileType.TDD, location);
        List<VirtualFile> jsTestVirtualFiles = JsTestFileByTestNameIndex.findFiles(testFqn, scope, executedFile);
        for (VirtualFile file : jsTestVirtualFiles) {
            PsiFile psiFile = PsiManager.getInstance(project).findFile(file);
            if (psiFile instanceof JSFile) {
                JSFile jsFile = (JSFile) psiFile;
                MochaTddFileStructure structure = (MochaTddFileStructure) MochaTddFileStructureBuilder.getInstance().fetchCachedTestFileStructure(jsFile);
                List<String> suiteNames = suite ? location : location.subList(0, location.size() - 1);
                String testName = suite ? null : (String) ContainerUtil.getLastItem(location);
                PsiElement element = structure.findPsiElement(suiteNames, testName);
                if (element != null && element.isValid()) {
                    return element;
                }
            }
        }
        return null;
    }

    @Nullable
    private static JSFile findJSFile(@NotNull Project project, @Nullable String testFilePath) {
        VirtualFile file = findFile(testFilePath);
        if (file == null || !file.isValid()) {
            return null;
        }
        PsiFile psiFile = PsiManager.getInstance(project).findFile(file);
        return (JSFile) ObjectUtils.tryCast(psiFile, JSFile.class);
    }
}
