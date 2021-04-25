package org.bipolar.run.profile.heap.data;

import org.bipolar.run.profile.heap.V8CachingReader;
import org.jetbrains.annotations.NotNull;

public interface Presentable {
  String getPresentation(@NotNull V8CachingReader paramV8CachingReader);
}


/* Location:              C:\Program Files\JetBrains\WebStorm 2021.1\plugins\NodeJS\lib\NodeJS.jar!\com\jetbrains\nodejs\run\profile\heap\data\Presentable.class
 * Java compiler version: 11 (55.0)
 * JD-Core Version:       1.1.3
 */