package org.bipolar.tests.execution;

import com.intellij.execution.configuration.EnvironmentVariablesComponent;
import com.intellij.execution.configuration.EnvironmentVariablesData;
import com.intellij.javascript.nodejs.interpreter.NodeJsInterpreterRef;
import com.intellij.openapi.util.JDOMExternalizerUtil;
import com.intellij.openapi.util.io.FileUtil;
import com.intellij.openapi.util.text.StringUtil;
import org.bipolar.tests.BipolarUtil;

import java.util.Collections;
import java.util.List;

import org.jdom.Element;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;


public final class MochaRunSettingsSerializationUtil {
    private static final String NODE_INTERPRETER__KEY = "node-interpreter";
    private static final String NODE_OPTIONS__KEY = "node-options";
    private static final String MOCHA_PACKAGE___KEY = "mocha-package";
    private static final String WORKING_DIRECTORY__KEY = "working-directory";
    private static final String PASS_PARENT_ENV__KEY = "pass-parent-env";
    private static final String UI__KEY = "ui";
    private static final String EXTRA_MOCHA_OPTIONS__KEY = "extra-mocha-options";
    private static final String TEST_KIND__KEY = "test-kind";
    private static final String TEST_DIRECTORY__KEY = "test-directory";
    private static final String RECURSIVE__KEY = "recursive";
    private static final String TEST_FILE_PATTERN__KEY = "test-pattern";
    private static final String TEST_FILE__KEY = "test-file";
    private static final String TEST_NAMES__KEY = "test-names";
    private static final String TEST_NAME__KEY = "name";

    @NotNull
    public static MochaRunSettings readFromXml(@NotNull Element parent) {
        MochaRunSettings.Builder builder = new MochaRunSettings.Builder();

        String interpreterRefName = readTagNullable(parent, "node-interpreter");
        builder.setInterpreterRef(NodeJsInterpreterRef.create(StringUtil.notNullize(interpreterRefName)));

        String nodeOptions = readTag(parent, "node-options");
        builder.setNodeOptions(nodeOptions);

        String pkg = readTagNullable(parent, "mocha-package");
        if (pkg != null) {
            builder.setMochaPackage(BipolarUtil.PACKAGE_DESCRIPTOR.createPackage(pkg));
        }

        String workingDirPath = readTag(parent, "working-directory");
        builder.setWorkingDir(FileUtil.toSystemDependentName(workingDirPath));

        EnvironmentVariablesData envData = EnvironmentVariablesData.readExternal(parent);
        String passParentEnvStr = readTag(parent, "pass-parent-env");
        if (StringUtil.isNotEmpty(passParentEnvStr)) {
            envData = EnvironmentVariablesData.create(envData.getEnvs(), Boolean.parseBoolean(passParentEnvStr));
        }
        builder.setEnvData(envData);

        builder.setUi(readTag(parent, "ui"));

        String extraMochaOptions = readTag(parent, "extra-mocha-options");
        builder.setExtraMochaOptions(extraMochaOptions);

        MochaTestKind testKind = deserializeTestKind(readTag(parent, "test-kind"));
        builder.setTestKind(testKind);
        if (MochaTestKind.DIRECTORY == testKind) {
            String testDirPath = readTag(parent, "test-directory");
            builder.setTestDirPath(FileUtil.toSystemDependentName(testDirPath));

            String recursiveStr = readTag(parent, "recursive");
            builder.setRecursive(Boolean.parseBoolean(recursiveStr));
        } else if (MochaTestKind.PATTERN == testKind) {
            builder.setTestFilePattern(FileUtil.toSystemDependentName(StringUtil.notNullize(readTag(parent, "test-pattern"))));
        } else if (MochaTestKind.TEST_FILE == testKind || MochaTestKind.SUITE == testKind || MochaTestKind.TEST == testKind) {
            builder.setTestFilePath(FileUtil.toSystemDependentName(StringUtil.notNullize(readTag(parent, "test-file"))));
            if (MochaTestKind.SUITE == testKind) {
                builder.setSuiteNames(readTestNames(parent));
            } else if (MochaTestKind.TEST == testKind) {
                builder.setTestNames(readTestNames(parent));
            }
        }
        return builder.build();
    }

    private static List<String> readTestNames(@NotNull Element parent) {
        Element testNamesElement = parent.getChild("test-names");
        if (testNamesElement == null) {
            return Collections.emptyList();
        }
        return JDOMExternalizerUtil.getChildrenValueAttributes(testNamesElement, "name");
    }

    @NotNull
    private static MochaTestKind deserializeTestKind(@Nullable String testKindStr) {
        try {
            return MochaTestKind.valueOf(testKindStr);
        } catch (Exception ignored) {
            return MochaTestKind.DIRECTORY;
        }
    }

    @NotNull
    private static String readTag(@NotNull Element parent, @NotNull String tagName) {
        return StringUtil.notNullize(readTagNullable(parent, tagName));
    }

    @Nullable
    private static String readTagNullable(@NotNull Element parent, @NotNull String tagName) {
        Element child = parent.getChild(tagName);
        String value = null;
        if (child != null) {
            value = child.getText();
        }
        return value;
    }

    public static void writeToXml(@NotNull Element parent, @NotNull MochaRunSettings runSettings) {
        NodeJsInterpreterRef interpreterRef = runSettings.getInterpreterRef();
        writeTag(parent, "node-interpreter", interpreterRef.getReferenceName());
        writeTag(parent, "node-options", runSettings.getNodeOptions());

        if (runSettings.getMochaPackage() != null) {
            writeTag(parent, "mocha-package", runSettings.getMochaPackage().getSystemIndependentPath());
        }

        String workingDirPath = FileUtil.toSystemIndependentName(runSettings.getWorkingDir());
        writeTag(parent, "working-directory", workingDirPath);

        writeTag(parent, "pass-parent-env", String.valueOf(runSettings.getEnvData().isPassParentEnvs()));
        EnvironmentVariablesComponent.writeExternal(parent, runSettings.getEnvData().getEnvs());

        writeTag(parent, "ui", runSettings.getUi());

        writeTag(parent, "extra-mocha-options", runSettings.getExtraMochaOptions());

        MochaTestKind testKind = runSettings.getTestKind();
        writeTag(parent, "test-kind", testKind.name());
        if (MochaTestKind.DIRECTORY == testKind) {
            writeTag(parent, "test-directory", FileUtil.toSystemIndependentName(runSettings.getTestDirPath()));
            writeTag(parent, "recursive", String.valueOf(runSettings.isRecursive()));
        } else if (MochaTestKind.PATTERN == testKind) {
            writeTag(parent, "test-pattern", FileUtil.toSystemIndependentName(runSettings.getTestFilePattern()));
        } else if (MochaTestKind.TEST_FILE == testKind || MochaTestKind.SUITE == testKind || MochaTestKind.TEST == testKind) {
            writeTag(parent, "test-file", FileUtil.toSystemIndependentName(runSettings.getTestFilePath()));
            if (MochaTestKind.SUITE == testKind) {
                writeTestNames(parent, runSettings.getSuiteNames());
            } else if (MochaTestKind.TEST == testKind) {
                writeTestNames(parent, runSettings.getTestNames());
            }
        }
    }

    private static void writeTestNames(@NotNull Element parent, @NotNull List<String> testNames) {
        if (!testNames.isEmpty()) {
            Element testNamesElement = new Element("test-names");
            JDOMExternalizerUtil.addChildrenWithValueAttribute(testNamesElement, "name", testNames);
            parent.addContent(testNamesElement);
        }
    }

    private static void writeTag(@NotNull Element parent, @NotNull String tagName, @NotNull String value) {
        Element element = new Element(tagName);
        element.setText(value);
        parent.addContent(element);
    }
}
