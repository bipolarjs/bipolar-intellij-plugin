package org.bipolar.tests.execution;

import javax.swing.JComponent;

import org.jetbrains.annotations.NotNull;

public abstract class MochaTestKindView {
    @NotNull
    public abstract JComponent getComponent();

    public abstract void resetFrom(@NotNull MochaRunSettings paramMochaRunSettings);

    public abstract void applyTo(@NotNull MochaRunSettings.Builder paramBuilder);
}
