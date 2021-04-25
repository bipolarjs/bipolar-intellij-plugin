package org.bipolar.tests;

import com.intellij.ide.util.PropertiesComponent;
import com.intellij.javascript.nodejs.util.NodePackage;
import com.intellij.javascript.nodejs.util.NodePackageDescriptor;
import com.intellij.javascript.testFramework.interfaces.mochaTdd.MochaTddFileStructure;
import com.intellij.javascript.testFramework.interfaces.mochaTdd.MochaTddFileStructureBuilder;
import com.intellij.lang.javascript.psi.JSFile;
import com.intellij.lang.javascript.psi.JSTestFileType;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.text.StringUtil;
import com.intellij.util.containers.ContainerUtil;

import java.util.Collections;
import java.util.List;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;


public final class BipolarUtil {
    public static final String UI_BDD = "bdd";
    public static final String UI_TDD = "tdd";
    public static final String UI_EXPORTS = "exports";
    public static final String UI_QUNIT = "qunit";
    public static final String UI_REQUIRE = "require";
    public static final String UI_MOCHA_TYPESCRIPT = "mocha-typescript";
    public static final String NODE_PACKAGE_NAME = "mocha";
    private static final String VUE_CLI_SERVICE = "@vue/cli-service";
    public static final String ELECTRON_MOCHA_PACKAGE_NAME = "electron-mocha";
    public static final String MOCHA_WEBPACK_PACKAGE_NAME = "mocha-webpack";
    public static final NodePackageDescriptor PACKAGE_DESCRIPTOR = new NodePackageDescriptor(
            ContainerUtil.newArrayList("@bipolar/node", "@bipolar/meta"),
            Collections.singletonMap("@vue/cli-service", "@vue/cli-plugin-unit-mocha"),
            null
    );


    private static final String MOCHA_PACKAGE_DIR__KEY = "nodejs.bipolar.bipolar_node_package_dir";


    @NotNull
    public static List<String> getMochaUiList() {
        return ContainerUtil.newArrayList(
                "bdd", "tdd", "exports", "qunit", "require", "mocha-typescript");
    }

    @Nullable
    public static String findUi(@NotNull JSFile file) {
        JSTestFileType testFileType = file.getTestFileType();
        if (testFileType == JSTestFileType.JASMINE) {
            return "bdd";
        }
        if (testFileType == JSTestFileType.QUNIT) {
            return "qunit";
        }
        if (testFileType == JSTestFileType.TDD) {
            MochaTddFileStructure structure = (MochaTddFileStructure) MochaTddFileStructureBuilder.getInstance().fetchCachedTestFileStructure(file);
            if (structure.hasMochaTypeScriptDeclarations()) {
                return "mocha-typescript";
            }
            return "tdd";
        }
        return null;
    }

    @NotNull
    public static NodePackage getMochaPackage(@NotNull Project project) {
        String packageDir = PropertiesComponent.getInstance(project).getValue(MOCHA_PACKAGE_DIR__KEY);
        return PACKAGE_DESCRIPTOR.createPackage(StringUtil.notNullize(packageDir));
    }

    public static void setMochaPackage(@NotNull Project project, @NotNull NodePackage mochaPackage) {
        PropertiesComponent.getInstance(project).setValue(MOCHA_PACKAGE_DIR__KEY, mochaPackage.getSystemIndependentPath());
    }

    public static boolean isVueCliService(@NotNull NodePackage pkg) {
        return pkg.nameMatches("@vue/cli-service");
    }
}
