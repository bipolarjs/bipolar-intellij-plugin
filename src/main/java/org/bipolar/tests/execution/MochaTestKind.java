package org.bipolar.tests.execution;

import com.intellij.lang.javascript.JavaScriptBundle;
import com.intellij.openapi.project.Project;

import java.util.function.Supplier;

import com.jetbrains.nodejs.NodeJSBundle;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.NotNull;

public enum MochaTestKind {
    DIRECTORY(NodeJSBundle.messagePointer("rc.mocha.testRunScope.allInDirectory", new Object[0])) {
        @NotNull
        public MochaTestKindView createView(@NotNull Project project) {
            return new MochaDirectoryView(project);
        }
    },

    PATTERN(NodeJSBundle.messagePointer("rc.mocha.testRunScope.filePattern", new Object[0])) {
        @NotNull
        public MochaTestKindView createView(@NotNull Project project) {
            return new MochaPatternView(project);
        }
    },

    TEST_FILE(JavaScriptBundle.messagePointer("rc.testRunScope.testFile", new Object[0])) {
        @NotNull
        public MochaTestKindView createView(@NotNull Project project) {
            return new MochaTestFileView(project);
        }
    },

    SUITE(JavaScriptBundle.messagePointer("rc.testRunScope.suite", new Object[0])) {
        @NotNull
        public MochaTestKindView createView(@NotNull Project project) {
            return new MochaSuiteView(project);
        }
    },

    TEST(JavaScriptBundle.messagePointer("rc.testRunScope.test", new Object[0])) {
        @NotNull
        public MochaTestKindView createView(@NotNull Project project) {
            return new MochaTestView(project);
        }
    };

    private final Supplier<String> myNameSupplier;

    MochaTestKind(Supplier<String> nameSupplier) {
        this.myNameSupplier = nameSupplier;
    }

    @NotNull
    @Nls
    public String getName() {
        return this.myNameSupplier.get();
    }

    @NotNull
    public abstract MochaTestKindView createView(@NotNull Project paramProject);
}
