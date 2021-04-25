package org.bipolar.run.profile.cpu.v8log.reading;

import java.awt.Point;
import java.util.List;
import org.jetbrains.annotations.NotNull;

public interface V8CpuViewCallback {
  void updateActionsAvailability();
  
  void navigateToTopCalls(@NotNull Long paramLong, Point paramPoint);
  
  void navigateToBottomUp(@NotNull List<Long> paramList, Point paramPoint);
  
  void navigateToTopDown(@NotNull List<Long> paramList, Point paramPoint);
}


/* Location:              C:\Program Files\JetBrains\WebStorm 2021.1\plugins\NodeJS\lib\NodeJS.jar!\com\jetbrains\nodejs\run\profile\cpu\v8log\reading\V8CpuViewCallback.class
 * Java compiler version: 11 (55.0)
 * JD-Core Version:       1.1.3
 */