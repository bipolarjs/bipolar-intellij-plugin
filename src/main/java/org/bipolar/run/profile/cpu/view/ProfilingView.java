package org.bipolar.run.profile.cpu.view;

import com.intellij.openapi.actionSystem.DefaultActionGroup;
import javax.swing.JComponent;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.Nullable;

public interface ProfilingView<T extends com.intellij.ui.treeStructure.treetable.TreeTable> {
  JComponent getMainComponent();
  
  @Nls
  String getName();
  
  void addActions(DefaultActionGroup paramDefaultActionGroup);
  
  @Nls
  String getError();
  
  @Nullable
  T getTreeTable();
  
  void defaultExpand();
}


/* Location:              C:\Program Files\JetBrains\WebStorm 2021.1\plugins\NodeJS\lib\NodeJS.jar!\com\jetbrains\nodejs\run\profile\cpu\view\ProfilingView.class
 * Java compiler version: 11 (55.0)
 * JD-Core Version:       1.1.3
 */