package org.bipolar.mocha.execution;

import javax.swing.JComponent;
import org.jetbrains.annotations.NotNull;

public abstract class MochaTestKindView {
  @NotNull
  public abstract JComponent getComponent();
  
  public abstract void resetFrom(@NotNull MochaRunSettings paramMochaRunSettings);
  
  public abstract void applyTo(@NotNull MochaRunSettings.Builder paramBuilder);
}


/* Location:              C:\Program Files\JetBrains\WebStorm 2021.1\plugins\NodeJS\lib\NodeJS.jar!\com\jetbrains\nodejs\mocha\execution\MochaTestKindView.class
 * Java compiler version: 11 (55.0)
 * JD-Core Version:       1.1.3
 */