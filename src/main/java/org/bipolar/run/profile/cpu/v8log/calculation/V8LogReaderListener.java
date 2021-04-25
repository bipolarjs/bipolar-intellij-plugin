package org.bipolar.run.profile.cpu.v8log.calculation;

import com.intellij.util.ThrowableConsumer;
import java.io.IOException;
import java.util.List;

public interface V8LogReaderListener {
  ParserBase advanceDistortion();
  
  ParserBase processTick();
  
  ParserBase processFunctionMove();
  
  ParserBase processCodeDelete();
  
  ParserBase processCodeMove();
  
  ThrowableConsumer<List<String>, IOException> processCodeCreation();
  
  ParserBase processSharedLibrary();
}


/* Location:              C:\Program Files\JetBrains\WebStorm 2021.1\plugins\NodeJS\lib\NodeJS.jar!\com\jetbrains\nodejs\run\profile\cpu\v8log\calculation\V8LogReaderListener.class
 * Java compiler version: 11 (55.0)
 * JD-Core Version:       1.1.3
 */