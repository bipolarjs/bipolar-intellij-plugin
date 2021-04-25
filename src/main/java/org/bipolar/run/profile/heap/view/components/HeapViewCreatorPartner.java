package org.bipolar.run.profile.heap.view.components;

import com.intellij.util.Consumer;
import org.bipolar.run.profile.cpu.view.ViewCreatorPartner;
import org.bipolar.run.profile.heap.calculation.ByteArrayWrapper;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public interface HeapViewCreatorPartner<T extends com.intellij.ui.treeStructure.treetable.TreeTable> extends ViewCreatorPartner<T> {
  @Nullable
  V8MainTreeNavigator getNavigator();
  
  void reportInvolvedSnapshots(@NotNull Consumer<ByteArrayWrapper> paramConsumer);
}


/* Location:              C:\Program Files\JetBrains\WebStorm 2021.1\plugins\NodeJS\lib\NodeJS.jar!\com\jetbrains\nodejs\run\profile\heap\view\components\HeapViewCreatorPartner.class
 * Java compiler version: 11 (55.0)
 * JD-Core Version:       1.1.3
 */