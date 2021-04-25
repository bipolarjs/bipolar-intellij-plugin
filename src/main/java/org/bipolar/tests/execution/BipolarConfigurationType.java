package org.bipolar.tests.execution;

import com.intellij.execution.configurations.ConfigurationTypeUtil;
import com.intellij.execution.configurations.RunConfiguration;
import com.intellij.execution.configurations.RunConfigurationSingletonPolicy;
import com.intellij.execution.configurations.SimpleConfigurationType;
import com.intellij.openapi.project.DumbAware;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.NotNullLazyValue;
import icons.NodeJSIcons;
import org.jetbrains.annotations.NotNull;

public final class BipolarConfigurationType extends SimpleConfigurationType implements DumbAware {
    public BipolarConfigurationType() {
        super("bipolar-javascript-test-runner",
                "Bipolar",
                null,
                NotNullLazyValue.createValue(() -> NodeJSIcons.Bipolar)
        );
    }

    @NotNull
    public String getTag() {
        return "bipolar";
    }

    public String getHelpTopic() {
        return null;
    }

    @NotNull
    public RunConfiguration createTemplateConfiguration(@NotNull Project project) {
        return new BipolarRunConfiguration(project, this, null);
    }

    @NotNull
    public RunConfigurationSingletonPolicy getSingletonPolicy() {
        return RunConfigurationSingletonPolicy.SINGLE_INSTANCE_ONLY;
    }

    public boolean isEditableInDumbMode() {
        return true;
    }

    @NotNull
    public static BipolarConfigurationType getInstance() {
        return ConfigurationTypeUtil.findConfigurationType(BipolarConfigurationType.class);
    }
}
