package org.bipolar.nodeunit.execution.ui;

import org.bipolar.nodeunit.execution.NodeunitSettings;
import javax.swing.JComponent;

import org.jetbrains.annotations.NotNull;

interface SettingsController {
  @NotNull
  JComponent getComponent();
  
  void resetFrom(@NotNull NodeunitSettings paramNodeunitSettings);
  
  void applyTo(@NotNull NodeunitSettings.Builder paramBuilder);
}


/* Location:              C:\Program Files\JetBrains\WebStorm 2021.1\plugins\NodeJS\lib\NodeJS.jar!\com\jetbrains\nodejs\nodeunit\executio\\ui\SettingsController.class
 * Java compiler version: 11 (55.0)
 * JD-Core Version:       1.1.3
 */