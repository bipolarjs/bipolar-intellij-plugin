package org.bipolar.util;

import com.intellij.util.ThrowableConsumer;
import java.io.Closeable;

public interface CloseableThrowableConsumer<S, T extends Throwable> extends Closeable, ThrowableConsumer<S, T> {}


/* Location:              C:\Program Files\JetBrains\WebStorm 2021.1\plugins\NodeJS\lib\NodeJS.jar!\com\jetbrains\nodej\\util\CloseableThrowableConsumer.class
 * Java compiler version: 11 (55.0)
 * JD-Core Version:       1.1.3
 */