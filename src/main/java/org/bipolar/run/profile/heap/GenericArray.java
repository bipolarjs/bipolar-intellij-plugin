package org.bipolar.run.profile.heap;

import java.io.IOException;

public interface GenericArray<T> {
  void set(long paramLong, T paramT) throws IOException;
  
  T get(long paramLong) throws IOException;
  
  long size();
}


/* Location:              C:\Program Files\JetBrains\WebStorm 2021.1\plugins\NodeJS\lib\NodeJS.jar!\com\jetbrains\nodejs\run\profile\heap\GenericArray.class
 * Java compiler version: 11 (55.0)
 * JD-Core Version:       1.1.3
 */