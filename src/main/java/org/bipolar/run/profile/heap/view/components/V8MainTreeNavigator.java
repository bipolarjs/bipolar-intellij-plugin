package org.bipolar.run.profile.heap.view.components;

import org.bipolar.run.profile.heap.data.V8HeapEdge;
import org.bipolar.run.profile.heap.data.V8HeapEntry;
import javax.swing.tree.TreePath;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public interface V8MainTreeNavigator {
  boolean navigateTo(@NotNull TreePath paramTreePath);
  
  boolean navigateTo(@NotNull V8HeapEntry paramV8HeapEntry, @Nullable V8HeapEdge paramV8HeapEdge);
}


/* Location:              C:\Program Files\JetBrains\WebStorm 2021.1\plugins\NodeJS\lib\NodeJS.jar!\com\jetbrains\nodejs\run\profile\heap\view\components\V8MainTreeNavigator.class
 * Java compiler version: 11 (55.0)
 * JD-Core Version:       1.1.3
 */