package org.bipolar.mocha.execution;

import com.intellij.openapi.project.Project;
import org.jetbrains.annotations.NotNull;

public interface MochaTestKindViewProducer {
  @NotNull
  MochaTestKindView produce(@NotNull Project paramProject);
}


/* Location:              C:\Program Files\JetBrains\WebStorm 2021.1\plugins\NodeJS\lib\NodeJS.jar!\com\jetbrains\nodejs\mocha\execution\MochaTestKindViewProducer.class
 * Java compiler version: 11 (55.0)
 * JD-Core Version:       1.1.3
 */