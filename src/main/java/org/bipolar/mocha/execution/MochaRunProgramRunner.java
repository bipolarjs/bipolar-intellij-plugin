/*    */ package org.bipolar.mocha.execution;
/*    */ 
/*    */ import com.intellij.execution.ExecutionException;
/*    */ import com.intellij.execution.ExecutionResult;
/*    */ import com.intellij.execution.configurations.RunProfile;
/*    */ import com.intellij.execution.configurations.RunProfileState;
/*    */ import com.intellij.execution.runners.ExecutionEnvironment;
/*    */ import com.intellij.execution.runners.GenericProgramRunner;
/*    */ import com.intellij.execution.runners.ProgramRunner;
/*    */ import com.intellij.execution.runners.RerunTestsAction;
/*    */ import com.intellij.execution.runners.RerunTestsNotification;
/*    */ import com.intellij.execution.runners.RunContentBuilder;
/*    */ import com.intellij.execution.ui.RunContentDescriptor;
/*    */ import com.intellij.openapi.fileEditor.FileDocumentManager;
/*    */ import org.jetbrains.annotations.NotNull;
/*    */ 
/*    */ public class MochaRunProgramRunner
/*    */   extends GenericProgramRunner {
/*    */   @NotNull
/*    */   public String getRunnerId() {
/* 21 */     return "RunnerForMochaJavaScript";
/*    */   }
/*    */ 
/*    */   
/*    */   public boolean canRun(@NotNull String executorId, @NotNull RunProfile profile) {
/* 26 */     if (executorId == null) $$$reportNull$$$0(0);  if (profile == null) $$$reportNull$$$0(1);  return ("Run".equals(executorId) && profile instanceof MochaRunConfiguration);
/*    */   }
/*    */ 
/*    */   
/*    */   protected RunContentDescriptor doExecute(@NotNull RunProfileState state, @NotNull ExecutionEnvironment environment) throws ExecutionException {
/* 31 */     if (state == null) $$$reportNull$$$0(2);  if (environment == null) $$$reportNull$$$0(3);  FileDocumentManager.getInstance().saveAllDocuments();
/* 32 */     ExecutionResult executionResult = state.execute(environment.getExecutor(), (ProgramRunner)this);
/* 33 */     if (executionResult == null) {
/* 34 */       return null;
/*    */     }
/*    */     
/* 37 */     RunContentDescriptor descriptor = (new RunContentBuilder(executionResult, environment)).showRunContent(environment.getContentToReuse());
/* 38 */     RerunTestsNotification.showRerunNotification(environment.getContentToReuse(), executionResult.getExecutionConsole());
/* 39 */     RerunTestsAction.register(descriptor);
/* 40 */     return descriptor;
/*    */   }
/*    */ }


/* Location:              C:\Program Files\JetBrains\WebStorm 2021.1\plugins\NodeJS\lib\NodeJS.jar!\com\jetbrains\nodejs\mocha\execution\MochaRunProgramRunner.class
 * Java compiler version: 11 (55.0)
 * JD-Core Version:       1.1.3
 */