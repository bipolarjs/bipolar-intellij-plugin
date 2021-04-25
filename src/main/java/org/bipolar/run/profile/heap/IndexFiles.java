package org.bipolar.run.profile.heap;

import java.io.Closeable;
import java.io.File;
import java.io.IOException;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public interface IndexFiles<T> extends Closeable {
  File generate(@NotNull T paramT, @Nullable String paramString) throws IOException;
  
  void close() throws IOException;
}


/* Location:              C:\Program Files\JetBrains\WebStorm 2021.1\plugins\NodeJS\lib\NodeJS.jar!\com\jetbrains\nodejs\run\profile\heap\IndexFiles.class
 * Java compiler version: 11 (55.0)
 * JD-Core Version:       1.1.3
 */