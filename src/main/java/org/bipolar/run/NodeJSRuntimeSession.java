/*    */ package org.bipolar.run;
/*    */ 
/*    */ import com.intellij.execution.ExecutionException;
/*    */ import com.intellij.execution.configurations.GeneralCommandLine;
/*    */ import com.intellij.execution.process.ProcessHandler;
/*    */ import com.intellij.javascript.debugger.CommandLineDebugConfigurator;
/*    */ import com.intellij.openapi.actionSystem.AnAction;
/*    */ import java.io.IOException;
/*    */ import java.util.Collections;
/*    */ import java.util.List;
/*    */ import org.jetbrains.annotations.NotNull;
/*    */ import org.jetbrains.annotations.Nullable;
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public interface NodeJSRuntimeSession
/*    */ {
/*    */   @NotNull
/*    */   default List<String> getNodeParameters(boolean isDebugStarted) throws IOException {
/* 21 */     if (Collections.emptyList() == null) $$$reportNull$$$0(0);  return (List)Collections.emptyList();
/*    */   }
/*    */   
/*    */   @NotNull
/*    */   default List<AnAction> getRunDebugActions() {
/* 26 */     if (Collections.emptyList() == null) $$$reportNull$$$0(1);  return (List)Collections.emptyList();
/*    */   }
/*    */ 
/*    */ 
/*    */   
/*    */   default ProcessHandler createProcessHandler(GeneralCommandLine commandLine, @Nullable CommandLineDebugConfigurator debugConfigurator, List<Integer> openPorts) throws ExecutionException {
/* 32 */     return null;
/*    */   }
/*    */   
/*    */   @Nullable
/*    */   default String getDebugHost() {
/* 37 */     return null;
/*    */   }
/*    */   
/*    */   @NotNull
/*    */   default List<Integer> getUsedPorts() {
/* 42 */     if (Collections.emptyList() == null) $$$reportNull$$$0(2);  return (List)Collections.emptyList();
/*    */   }
/*    */   
/*    */   default void onProcessStarted(ProcessHandler processHandler) {}
/*    */ }


/* Location:              C:\Program Files\JetBrains\WebStorm 2021.1\plugins\NodeJS\lib\NodeJS.jar!\com\jetbrains\nodejs\run\NodeJSRuntimeSession.class
 * Java compiler version: 11 (55.0)
 * JD-Core Version:       1.1.3
 */