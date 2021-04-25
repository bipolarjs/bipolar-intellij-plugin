/*    */ package org.bipolar.library;
/*    */ 
/*    */ import com.intellij.javascript.nodejs.interpreter.NodeJsInterpreter;
/*    */ import com.intellij.javascript.nodejs.interpreter.NodeJsInterpreterManager;
/*    */ import com.intellij.javascript.nodejs.library.core.NodeCoreLibraryConfigurator;
/*    */ import com.intellij.javascript.nodejs.library.core.NodeCoreLibraryManager;
/*    */ import com.intellij.javascript.nodejs.library.core.NodeTypingsDownloadSession;
/*    */ import com.intellij.openapi.application.ApplicationManager;
/*    */ import com.intellij.openapi.application.ModalityState;
/*    */ import com.intellij.openapi.application.ReadAction;
/*    */ import com.intellij.openapi.project.Project;
/*    */ import com.intellij.openapi.startup.StartupActivity;
/*    */ import com.intellij.openapi.vfs.VirtualFile;
/*    */ import com.intellij.util.text.SemVer;
/*    */ import java.io.File;
/*    */ import java.util.List;
/*    */ import org.jetbrains.annotations.NotNull;
/*    */ import org.jetbrains.annotations.Nullable;
/*    */ 
/*    */ public class NodeJsStartupActivity
/*    */   implements StartupActivity.Background
/*    */ {
/*    */   public void runActivity(@NotNull Project project) {
/* 24 */     if (project == null) $$$reportNull$$$0(0);  reconfigureCodeLibraryIfNeeded(project);
/*    */   }
/*    */   
/*    */   private static void reconfigureCodeLibraryIfNeeded(@NotNull Project project) {
/* 28 */     if (project == null) $$$reportNull$$$0(1);  ReadAction.run(() -> {
/*    */           if (!project.isDisposed()) {
/*    */             reconfigureNodeCoreLibraryIfFilesMissing(project);
/*    */           }
/*    */         });
/*    */   }
/*    */   
/*    */   private static void reconfigureNodeCoreLibraryIfFilesMissing(@NotNull Project project) {
/* 36 */     if (project == null) $$$reportNull$$$0(2);  if (isNodeCoreReconfigurationNeeded(NodeCoreLibraryConfigurator.getConfiguredCoreLibraryVersion(project))) {
/* 37 */       List<VirtualFile> roots = NodeCoreLibraryManager.getInstance(project).getAssociatedRoots();
/* 38 */       doReconfigure(project, roots);
/*    */     } 
/*    */   }
/*    */   
/*    */   private static boolean isNodeCoreReconfigurationNeeded(@Nullable NodeCoreLibraryConfigurator.NodeLibraryVersion version) {
/* 43 */     if (version == null) {
/* 44 */       return false;
/*    */     }
/* 46 */     File coreModulesSrcDir = NodeCoreLibraryConfigurator.getCoreModulesSrcDir(version.getNodeVersion());
/* 47 */     return (!coreModulesSrcDir.isDirectory() || NodeTypingsDownloadSession.isReconfigurationNeeded(version));
/*    */   }
/*    */   
/*    */   private static void doReconfigure(@NotNull Project project, @NotNull List<VirtualFile> rootsToAssociateWith) {
/* 51 */     if (project == null) $$$reportNull$$$0(3);  if (rootsToAssociateWith == null) $$$reportNull$$$0(4);  NodeJsInterpreter interpreter = NodeJsInterpreterManager.getInstance(project).getInterpreter();
/* 52 */     if (interpreter instanceof com.intellij.javascript.nodejs.interpreter.local.NodeJsLocalInterpreter)
/* 53 */       interpreter.provideCachedVersionOrFetch(version -> {
/*    */             if (version != null)
/*    */               ApplicationManager.getApplication().invokeLater((), ModalityState.NON_MODAL, project.getDisposed()); 
/*    */           }); 
/*    */   }
/*    */ }


/* Location:              C:\Program Files\JetBrains\WebStorm 2021.1\plugins\NodeJS\lib\NodeJS.jar!\com\jetbrains\nodejs\library\NodeJsStartupActivity.class
 * Java compiler version: 11 (55.0)
 * JD-Core Version:       1.1.3
 */