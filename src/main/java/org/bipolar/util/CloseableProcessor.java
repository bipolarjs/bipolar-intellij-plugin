package org.bipolar.util;

import com.intellij.util.Processor;
import java.io.Closeable;
import org.jetbrains.annotations.NotNull;

public interface CloseableProcessor<T, E extends Throwable> extends Closeable, Processor<T> {
  void exceptionThrown(@NotNull E paramE);
}


/* Location:              C:\Program Files\JetBrains\WebStorm 2021.1\plugins\NodeJS\lib\NodeJS.jar!\com\jetbrains\nodej\\util\CloseableProcessor.class
 * Java compiler version: 11 (55.0)
 * JD-Core Version:       1.1.3
 */