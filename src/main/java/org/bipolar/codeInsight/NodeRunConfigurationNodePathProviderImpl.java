/*    */ package org.bipolar.codeInsight;
/*    */ 
/*    */ import com.intellij.javascript.nodejs.reference.NodePathManager;
/*    */ import com.intellij.openapi.project.Project;
/*    */ import com.intellij.openapi.project.ProjectUtil;
/*    */ import com.intellij.openapi.util.Ref;
/*    */ import com.intellij.openapi.util.io.OSAgnosticPathUtil;
/*    */ import com.intellij.openapi.vfs.LocalFileSystem;
/*    */ import com.intellij.openapi.vfs.VirtualFile;
/*    */ import org.bipolar.run.NodeJsRunConfiguration;
/*    */ import java.util.Objects;
/*    */ import org.jetbrains.annotations.NotNull;
/*    */ import org.jetbrains.annotations.Nullable;
/*    */ 
/*    */ public class NodeRunConfigurationNodePathProviderImpl
/*    */   implements NodePathManager.NodeRunConfigurationNodePathProvider
/*    */ {
/*    */   private final Project myProject;
/* 19 */   private volatile Ref<NodePathManager.NodePathData> myNodePathDataRef = Ref.create();
/*    */   
/*    */   public NodeRunConfigurationNodePathProviderImpl(@NotNull Project project) {
/* 22 */     this.myProject = project;
/*    */   }
/*    */   
/*    */   @NotNull
/*    */   public static NodeRunConfigurationNodePathProviderImpl getInstance(@NotNull Project project) {
/* 27 */     if (project == null) $$$reportNull$$$0(1);  if ((NodeRunConfigurationNodePathProviderImpl)Objects.requireNonNull(
/* 28 */         (NodeRunConfigurationNodePathProviderImpl)NodePathManager.NodeRunConfigurationNodePathProvider.getInstance(project)) == null) $$$reportNull$$$0(2);  return Objects.requireNonNull((NodeRunConfigurationNodePathProviderImpl)NodePathManager.NodeRunConfigurationNodePathProvider.getInstance(project));
/*    */   }
/*    */ 
/*    */ 
/*    */   
/*    */   @Nullable
/*    */   public NodePathManager.NodePathData getNodePathData() {
/* 35 */     Ref<NodePathManager.NodePathData> nodePathDataRef = this.myNodePathDataRef;
/* 36 */     if (nodePathDataRef != null) {
/* 37 */       return (NodePathManager.NodePathData)nodePathDataRef.get();
/*    */     }
/* 39 */     NodeJsRunConfiguration defaultConfiguration = NodeJsRunConfiguration.getDefaultRunConfiguration(this.myProject);
/* 40 */     String nodePathStr = null;
/* 41 */     VirtualFile contextDir = null;
/* 42 */     if (defaultConfiguration != null) {
/* 43 */       nodePathStr = (String)defaultConfiguration.getEnvs().get("NODE_PATH");
/* 44 */       String directory = defaultConfiguration.getWorkingDirectory();
/* 45 */       if (directory != null && OSAgnosticPathUtil.isAbsolute(directory)) {
/* 46 */         contextDir = LocalFileSystem.getInstance().findFileByPath(directory);
/*    */       }
/*    */     } 
/* 49 */     if (contextDir == null) {
/* 50 */       contextDir = ProjectUtil.guessProjectDir(this.myProject);
/*    */     }
/* 52 */     NodePathManager.NodePathData nodePathData = null;
/* 53 */     if (nodePathStr != null && contextDir != null) {
/* 54 */       nodePathData = new NodePathManager.NodePathData(nodePathStr, contextDir);
/*    */     }
/* 56 */     this.myNodePathDataRef = new Ref(nodePathData);
/* 57 */     return nodePathData;
/*    */   }
/*    */   
/*    */   public void dropCache() {
/* 61 */     this.myNodePathDataRef = null;
/*    */   }
/*    */ }


/* Location:              C:\Program Files\JetBrains\WebStorm 2021.1\plugins\NodeJS\lib\NodeJS.jar!\com\jetbrains\nodejs\codeInsight\NodeRunConfigurationNodePathProviderImpl.class
 * Java compiler version: 11 (55.0)
 * JD-Core Version:       1.1.3
 */