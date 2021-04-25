package org.bipolar.run.profile.heap.calculation;

import org.bipolar.run.profile.heap.data.V8HeapEdge;
import org.bipolar.util.CloseableThrowableConsumer;
import java.io.Closeable;
import java.io.File;
import java.io.IOException;

import org.jetbrains.annotations.NotNull;

public interface EdgeProcessor extends Closeable {
  void continueCalculation(@NotNull File paramFile, long paramLong1, long paramLong2) throws IOException;
  
  CloseableThrowableConsumer<V8HeapEdge, IOException> getFirstStageCalculator();
  
  void close() throws IOException;
}


/* Location:              C:\Program Files\JetBrains\WebStorm 2021.1\plugins\NodeJS\lib\NodeJS.jar!\com\jetbrains\nodejs\run\profile\heap\calculation\EdgeProcessor.class
 * Java compiler version: 11 (55.0)
 * JD-Core Version:       1.1.3
 */