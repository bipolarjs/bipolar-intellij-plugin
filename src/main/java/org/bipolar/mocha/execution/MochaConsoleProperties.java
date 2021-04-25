/*    */ package org.bipolar.mocha.execution;
/*    */ 
/*    */ import com.intellij.execution.Executor;
/*    */ import com.intellij.execution.configurations.RunConfiguration;
/*    */ import com.intellij.execution.process.ProcessHandler;
/*    */ import com.intellij.execution.testframework.TestConsoleProperties;
/*    */ import com.intellij.execution.testframework.actions.AbstractRerunFailedTestsAction;
/*    */ import com.intellij.execution.testframework.sm.runner.SMTestLocator;
/*    */ import com.intellij.execution.testframework.sm.runner.ui.SMTRunnerConsoleView;
/*    */ import com.intellij.execution.ui.ConsoleView;
/*    */ import com.intellij.javascript.testing.JsTestConsoleProperties;
/*    */
/*    */ import com.intellij.terminal.TerminalExecutionConsole;
/*    */ import com.intellij.util.config.AbstractProperty;
/*    */ import org.jetbrains.annotations.NotNull;
/*    */ import org.jetbrains.annotations.Nullable;
/*    */ 
/*    */ public class MochaConsoleProperties
/*    */   extends JsTestConsoleProperties
/*    */ {
/*    */   private static final String FRAMEWORK_NAME = "MochaJavaScriptTestRunner";
/*    */   private final SMTestLocator myLocator;
/*    */   private final boolean myWithTerminalConsole;
/*    */   
/*    */   public MochaConsoleProperties(@NotNull MochaRunConfiguration configuration, @NotNull Executor executor, @NotNull SMTestLocator locator, boolean withTerminalConsole) {
/* 26 */     super((RunConfiguration)configuration, "MochaJavaScriptTestRunner", executor);
/* 27 */     this.myLocator = locator;
/* 28 */     this.myWithTerminalConsole = withTerminalConsole;
/* 29 */     setUsePredefinedMessageFilter(false);
/* 30 */     setIfUndefined((AbstractProperty)TestConsoleProperties.HIDE_PASSED_TESTS, false);
/* 31 */     setIfUndefined((AbstractProperty)TestConsoleProperties.HIDE_IGNORED_TEST, true);
/* 32 */     setIfUndefined((AbstractProperty)TestConsoleProperties.SCROLL_TO_SOURCE, true);
/* 33 */     setIfUndefined((AbstractProperty)TestConsoleProperties.SELECT_FIRST_DEFECT, true);
/* 34 */     setIdBasedTestTree(true);
/* 35 */     setPrintTestingStartedTime(false);
/*    */   }
/*    */ 
/*    */   
/*    */   @NotNull
/*    */   public ConsoleView createConsole() {
/* 41 */     if (this.myWithTerminalConsole) {
/* 42 */       return (ConsoleView)new TerminalExecutionConsole(getProject(), null)
/*    */         {
/*    */           public void attachToProcess(@NotNull ProcessHandler processHandler) {
/* 45 */             if (processHandler == null) $$$reportNull$$$0(0);  attachToProcess(processHandler, false);
/*    */           }
/*    */         };
/*    */     }
/* 49 */     if (super.createConsole() == null) $$$reportNull$$$0(3);  return super.createConsole();
/*    */   }
/*    */ 
/*    */   
/*    */   public SMTestLocator getTestLocator() {
/* 54 */     return this.myLocator;
/*    */   }
/*    */ 
/*    */   
/*    */   @Nullable
/*    */   public AbstractRerunFailedTestsAction createRerunFailedTestsAction(ConsoleView consoleView) {
/* 60 */     return new MochaRerunFailedTestAction((SMTRunnerConsoleView)consoleView, this);
/*    */   }
/*    */ }


/* Location:              C:\Program Files\JetBrains\WebStorm 2021.1\plugins\NodeJS\lib\NodeJS.jar!\com\jetbrains\nodejs\mocha\execution\MochaConsoleProperties.class
 * Java compiler version: 11 (55.0)
 * JD-Core Version:       1.1.3
 */