package org.bipolar.tests.execution;

import com.intellij.openapi.project.Project;
import org.jetbrains.annotations.NotNull;

public interface MochaTestKindViewProducer {
  @NotNull
  MochaTestKindView produce(@NotNull Project paramProject);
}
