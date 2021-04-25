/*    */ package org.bipolar.mocha.execution;
/*    */ import com.intellij.execution.Executor;
/*    */ import com.intellij.execution.configurations.RunConfigurationBase;
/*    */ import com.intellij.execution.configurations.RunProfileState;
/*    */ import com.intellij.execution.runners.ExecutionEnvironment;
/*    */ import com.intellij.execution.testframework.AbstractTestProxy;
/*    */ import com.intellij.execution.testframework.TestConsoleProperties;
/*    */ import com.intellij.execution.testframework.actions.AbstractRerunFailedTestsAction;
/*    */ import com.intellij.execution.testframework.sm.runner.ui.SMTRunnerConsoleView;
/*    */ import com.intellij.javascript.testFramework.util.EscapeUtils;
/*    */ import com.intellij.openapi.ui.ComponentContainer;
/*    */ import com.intellij.openapi.vfs.VirtualFileManager;
/*    */ import java.util.ArrayList;
/*    */ import java.util.List;
/*    */ import org.jetbrains.annotations.NotNull;
/*    */ import org.jetbrains.annotations.Nullable;
/*    */ 
/*    */ public class MochaRerunFailedTestAction extends AbstractRerunFailedTestsAction {
/*    */   public MochaRerunFailedTestAction(@NotNull SMTRunnerConsoleView consoleView, @NotNull MochaConsoleProperties consoleProperties) {
/* 20 */     super((ComponentContainer)consoleView);
/* 21 */     init((TestConsoleProperties)consoleProperties);
/* 22 */     setModel((TestFrameworkRunningModel)consoleView.getResultsViewer());
/*    */   }
/*    */ 
/*    */   
/*    */   @Nullable
/*    */   protected AbstractRerunFailedTestsAction.MyRunProfile getRunProfile(@NotNull ExecutionEnvironment environment) {
/* 28 */     if (environment == null) $$$reportNull$$$0(2);  MochaRunConfiguration configuration = (MochaRunConfiguration)this.myConsoleProperties.getConfiguration();
/*    */     
/* 30 */     final MochaRunProfileState state = new MochaRunProfileState(configuration.getProject(), configuration, environment, configuration.getMochaPackage(), configuration.getRunSettings());
/* 31 */     state.setFailedTests(convertToTestFqns(getFailedTests(configuration.getProject())));
/* 32 */     return new AbstractRerunFailedTestsAction.MyRunProfile((RunConfigurationBase)configuration)
/*    */       {
/*    */         public RunProfileState getState(@NotNull Executor executor, @NotNull ExecutionEnvironment environment) {
/* 35 */           if (executor == null) $$$reportNull$$$0(0);  if (environment == null) $$$reportNull$$$0(1);  return (RunProfileState)state;
/*    */         }
/*    */       };
/*    */   }
/*    */   
/*    */   @NotNull
/*    */   private static List<List<String>> convertToTestFqns(@NotNull List<AbstractTestProxy> tests) {
/* 42 */     if (tests == null) $$$reportNull$$$0(3);  List<List<String>> result = new ArrayList<>();
/* 43 */     for (AbstractTestProxy test : tests) {
/* 44 */       List<String> fqn = convertToTestFqn(test);
/* 45 */       if (fqn != null) {
/* 46 */         result.add(fqn);
/*    */       }
/*    */     } 
/* 49 */     if (result == null) $$$reportNull$$$0(4);  return result;
/*    */   }
/*    */   
/*    */   @Nullable
/*    */   private static List<String> convertToTestFqn(@NotNull AbstractTestProxy test) {
/* 54 */     if (test == null) $$$reportNull$$$0(5);  String url = test.getLocationUrl();
/* 55 */     if (test.isLeaf() && url != null) {
/* 56 */       List<String> testFqn = EscapeUtils.split(VirtualFileManager.extractPath(url), '.');
/* 57 */       if (!testFqn.isEmpty()) {
/* 58 */         return testFqn;
/*    */       }
/*    */     } 
/* 61 */     return null;
/*    */   }
/*    */ }


/* Location:              C:\Program Files\JetBrains\WebStorm 2021.1\plugins\NodeJS\lib\NodeJS.jar!\com\jetbrains\nodejs\mocha\execution\MochaRerunFailedTestAction.class
 * Java compiler version: 11 (55.0)
 * JD-Core Version:       1.1.3
 */