package org.bipolar.util;

import java.io.Closeable;

public interface CloseableThrowableProcessor<S, T extends Throwable> extends Closeable {
  boolean process(S paramS) throws T;
}


/* Location:              C:\Program Files\JetBrains\WebStorm 2021.1\plugins\NodeJS\lib\NodeJS.jar!\com\jetbrains\nodej\\util\CloseableThrowableProcessor.class
 * Java compiler version: 11 (55.0)
 * JD-Core Version:       1.1.3
 */