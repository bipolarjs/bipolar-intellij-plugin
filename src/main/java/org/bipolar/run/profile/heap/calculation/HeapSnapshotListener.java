package org.bipolar.run.profile.heap.calculation;

import org.bipolar.run.profile.heap.data.V8HeapEdge;
import org.bipolar.run.profile.heap.data.V8HeapEntry;
import java.io.IOException;
import org.jetbrains.annotations.NotNull;

public interface HeapSnapshotListener {
  void accept(@NotNull V8HeapEntry paramV8HeapEntry) throws IOException;
  
  void accept(@NotNull V8HeapEdge paramV8HeapEdge) throws IOException;
  
  void accept(@NotNull String paramString) throws IOException;
  
  void allNodesRead() throws IOException;
  
  void allEdgesRead();
  
  void stringsCount(long paramLong);
}


/* Location:              C:\Program Files\JetBrains\WebStorm 2021.1\plugins\NodeJS\lib\NodeJS.jar!\com\jetbrains\nodejs\run\profile\heap\calculation\HeapSnapshotListener.class
 * Java compiler version: 11 (55.0)
 * JD-Core Version:       1.1.3
 */