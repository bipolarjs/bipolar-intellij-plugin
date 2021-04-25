/*    */ package org.bipolar.nodeunit.execution;
/*    */ 
/*    */ import com.intellij.execution.configurations.RuntimeConfigurationError;
/*    */ import com.intellij.execution.configurations.RuntimeConfigurationException;
/*    */ import com.intellij.javascript.nodejs.interpreter.NodeJsInterpreter;
/*    */ import com.intellij.javascript.nodejs.interpreter.local.NodeJsLocalInterpreter;
/*    */ import com.intellij.javascript.nodejs.util.NodePackageDescriptor;
/*    */ import com.intellij.openapi.project.Project;
/*    */ import org.bipolar.NodeJSBundle;
/*    */ import java.io.File;
/*    */ import org.jetbrains.annotations.NonNls;
/*    */ import org.jetbrains.annotations.NotNull;
/*    */ 
/*    */ public final class NodeunitExecutionUtils
/*    */ {
/*    */   @NonNls
/* 17 */   public static final NodePackageDescriptor PKG = new NodePackageDescriptor("nodeunit");
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public static void checkConfiguration(@NotNull Project project, @NotNull NodeunitSettings settings) throws RuntimeConfigurationException {
/* 23 */     if (project == null) $$$reportNull$$$0(0);  if (settings == null) $$$reportNull$$$0(1);  NodeJsInterpreter interpreter = settings.getInterpreterRef().resolve(project);
/* 24 */     NodeJsLocalInterpreter.checkForRunConfiguration(interpreter);
/* 25 */     File nodeunitModuleDir = new File(settings.getNodeunitPackage().getSystemDependentPath());
/* 26 */     if (!nodeunitModuleDir.isAbsolute()) {
/* 27 */       throw new RuntimeConfigurationError(NodeJSBundle.message("nodeunit.rc.check.package.text", new Object[0]));
/*    */     }
/* 29 */     if (!nodeunitModuleDir.isDirectory()) {
/* 30 */       throw new RuntimeConfigurationError(NodeJSBundle.message("nodeunit.rc.check.package.text", new Object[0]));
/*    */     }
/* 32 */     String workingDirectoryStr = settings.getWorkingDirectory();
/* 33 */     if (workingDirectoryStr.isEmpty() || ".".equals(workingDirectoryStr)) {
/* 34 */       throw new RuntimeConfigurationError(NodeJSBundle.message("nodeunit.rc.check.working.dir.text", new Object[0]));
/*    */     }
/* 36 */     File workingDirectory = new File(workingDirectoryStr);
/* 37 */     if (!workingDirectory.isDirectory()) {
/* 38 */       throw new RuntimeConfigurationError(NodeJSBundle.message("nodeunit.rc.check.working.dir.text", new Object[0]));
/*    */     }
/* 40 */     if (settings.getTestType() == NodeunitTestType.DIRECTORY) {
/* 41 */       File testDirectory = new File(workingDirectory, settings.getDirectory());
/* 42 */       if (!testDirectory.isDirectory()) {
/* 43 */         throw new RuntimeConfigurationError(NodeJSBundle.message("nodeunit.rc.check.test.dir.text", new Object[0]));
/*    */       }
/*    */     } else {
/*    */       
/* 47 */       File jsTestFile = new File(workingDirectory, settings.getJsFile());
/* 48 */       if (!jsTestFile.isFile())
/* 49 */         throw new RuntimeConfigurationError(NodeJSBundle.message("nodeunit.rc.check.test.file.text", new Object[0])); 
/*    */     } 
/*    */   }
/*    */ }


/* Location:              C:\Program Files\JetBrains\WebStorm 2021.1\plugins\NodeJS\lib\NodeJS.jar!\com\jetbrains\nodejs\nodeunit\execution\NodeunitExecutionUtils.class
 * Java compiler version: 11 (55.0)
 * JD-Core Version:       1.1.3
 */