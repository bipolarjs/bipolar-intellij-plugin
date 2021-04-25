/*    */ package org.bipolar.mocha.execution;
/*    */ 
/*    */ import com.intellij.lang.javascript.JavaScriptBundle;
/*    */ import com.intellij.openapi.project.Project;
/*    */ import org.bipolar.NodeJSBundle;
/*    */ import java.util.function.Supplier;
/*    */ import org.jetbrains.annotations.Nls;
/*    */ import org.jetbrains.annotations.NotNull;
/*    */ 
/*    */ public enum MochaTestKind
/*    */ {
/* 12 */   DIRECTORY(NodeJSBundle.messagePointer("rc.mocha.testRunScope.allInDirectory", new Object[0]))
/*    */   {
/*    */     @NotNull
/*    */     public MochaTestKindView createView(@NotNull Project project) {
/* 16 */       if (project == null) null.$$$reportNull$$$0(0);  return new MochaDirectoryView(project);
/*    */     }
/*    */   },
/*    */   
/* 20 */   PATTERN(NodeJSBundle.messagePointer("rc.mocha.testRunScope.filePattern", new Object[0]))
/*    */   {
/*    */     @NotNull
/*    */     public MochaTestKindView createView(@NotNull Project project) {
/* 24 */       if (project == null) null.$$$reportNull$$$0(0);  return new MochaPatternView(project);
/*    */     }
/*    */   },
/*    */   
/* 28 */   TEST_FILE(JavaScriptBundle.messagePointer("rc.testRunScope.testFile", new Object[0]))
/*    */   {
/*    */     @NotNull
/*    */     public MochaTestKindView createView(@NotNull Project project) {
/* 32 */       if (project == null) null.$$$reportNull$$$0(0);  return new MochaTestFileView(project);
/*    */     }
/*    */   },
/*    */   
/* 36 */   SUITE(JavaScriptBundle.messagePointer("rc.testRunScope.suite", new Object[0]))
/*    */   {
/*    */     @NotNull
/*    */     public MochaTestKindView createView(@NotNull Project project) {
/* 40 */       if (project == null) null.$$$reportNull$$$0(0);  return new MochaSuiteView(project);
/*    */     }
/*    */   },
/*    */   
/* 44 */   TEST(JavaScriptBundle.messagePointer("rc.testRunScope.test", new Object[0]))
/*    */   {
/*    */     @NotNull
/*    */     public MochaTestKindView createView(@NotNull Project project) {
/* 48 */       if (project == null) null.$$$reportNull$$$0(0);  return new MochaTestView(project);
/*    */     }
/*    */   };
/*    */   
/*    */   private final Supplier<String> myNameSupplier;
/*    */   
/*    */   MochaTestKind(Supplier<String> nameSupplier) {
/* 55 */     this.myNameSupplier = nameSupplier;
/*    */   }
/*    */   @NotNull
/*    */   @Nls
/*    */   public String getName() {
/* 60 */     if ((String)this.myNameSupplier.get() == null) $$$reportNull$$$0(1);  return this.myNameSupplier.get();
/*    */   }
/*    */   
/*    */   @NotNull
/*    */   public abstract MochaTestKindView createView(@NotNull Project paramProject);
/*    */ }


/* Location:              C:\Program Files\JetBrains\WebStorm 2021.1\plugins\NodeJS\lib\NodeJS.jar!\com\jetbrains\nodejs\mocha\execution\MochaTestKind.class
 * Java compiler version: 11 (55.0)
 * JD-Core Version:       1.1.3
 */