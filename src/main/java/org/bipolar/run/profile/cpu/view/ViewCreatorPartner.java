package org.bipolar.run.profile.cpu.view;

import com.intellij.openapi.Disposable;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.project.Project;
import java.util.List;
import javax.swing.JComponent;
import org.jetbrains.annotations.Nls;

public interface ViewCreatorPartner<T extends com.intellij.ui.treeStructure.treetable.TreeTable> {
  JComponent wrapWithStandardActions(ProfilingView<T> paramProfilingView, AnAction paramAnAction);
  
  void addViews(Project paramProject, List<ProfilingView<T>> paramList, Disposable paramDisposable);
  
  @Nls
  String errorCreated();
  
  void announceController(V8ProfilingMainComponent.MyController<T> paramMyController);
  
  void close();
}


/* Location:              C:\Program Files\JetBrains\WebStorm 2021.1\plugins\NodeJS\lib\NodeJS.jar!\com\jetbrains\nodejs\run\profile\cpu\view\ViewCreatorPartner.class
 * Java compiler version: 11 (55.0)
 * JD-Core Version:       1.1.3
 */