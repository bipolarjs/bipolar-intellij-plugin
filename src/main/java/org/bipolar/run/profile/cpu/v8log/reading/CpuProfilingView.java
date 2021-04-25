package org.bipolar.run.profile.cpu.v8log.reading;

import com.intellij.openapi.util.Factory;
import org.bipolar.run.profile.cpu.view.ProfilingView;
import org.bipolar.run.profile.cpu.view.SearchInV8TreeAction;
import org.bipolar.run.profile.cpu.view.StatisticsTreeTableWithDetails;
import org.bipolar.run.profile.cpu.view.V8ProfilingCallTreeTable;

public interface CpuProfilingView extends ProfilingView<V8ProfilingCallTreeTable> {
  StatisticsTreeTableWithDetails getMasterDetails();
  
  Factory<SearchInV8TreeAction.Searcher> getSearcherFactory();
  
  void registerItself(V8SwitchViewActionsFactory paramV8SwitchViewActionsFactory);
}


/* Location:              C:\Program Files\JetBrains\WebStorm 2021.1\plugins\NodeJS\lib\NodeJS.jar!\com\jetbrains\nodejs\run\profile\cpu\v8log\reading\CpuProfilingView.class
 * Java compiler version: 11 (55.0)
 * JD-Core Version:       1.1.3
 */