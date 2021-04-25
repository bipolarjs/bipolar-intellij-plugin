/*    */ package org.bipolar.run;
/*    */ import com.intellij.execution.Location;
/*    */ import com.intellij.execution.actions.ConfigurationContext;
/*    */ import com.intellij.execution.actions.ConfigurationFromContext;
/*    */
/*    */ import com.intellij.execution.configurations.RunConfiguration;
/*    */ import com.intellij.execution.util.ScriptFileUtil;
/*    */ import com.intellij.ide.scratch.ScratchUtil;
/*    */ import com.intellij.openapi.fileTypes.FileType;
/*    */ import com.intellij.openapi.project.Project;
/*    */ import com.intellij.openapi.project.ProjectUtil;
/*    */ import com.intellij.openapi.util.Ref;
/*    */ import com.intellij.openapi.vfs.VirtualFile;
/*    */ import com.intellij.psi.PsiElement;
/*    */ import com.intellij.psi.PsiFile;
/*    */ import com.intellij.util.text.StringKt;
/*    */ import kotlin.jvm.internal.Intrinsics;
/*    */ import org.jetbrains.annotations.NotNull;
/*    */ 
/*    */ @Metadata(mv = {1, 4, 2}, bv = {1, 0, 3}, k = 1, d1 = {"\000L\n\002\030\002\n\002\030\002\n\002\030\002\n\002\b\002\n\002\020\013\n\002\030\002\n\002\b\002\n\002\030\002\n\000\n\002\030\002\n\002\b\002\n\002\030\002\n\002\b\002\n\002\030\002\n\002\b\005\n\002\030\002\n\002\b\003\n\002\030\002\n\002\030\002\n\000\b\000\030\0002\b\022\004\022\0020\0020\001B\005¢\006\002\020\003J,\020\b\032\004\030\0010\t2\006\020\n\032\0020\0132\006\020\f\032\0020\t2\b\020\r\032\004\030\0010\0162\006\020\017\032\0020\005H\002J\b\020\020\032\0020\021H\026J\030\020\022\032\0020\0052\006\020\023\032\0020\0022\006\020\024\032\0020\006H\026J\032\020\025\032\0020\0052\006\020\026\032\0020\0272\b\020\030\032\004\030\0010\027H\026J&\020\031\032\0020\0052\006\020\023\032\0020\0022\006\020\024\032\0020\0062\f\020\032\032\b\022\004\022\0020\0340\033H\024R\032\020\004\032\0020\005*\004\030\0010\0068BX\004¢\006\006\032\004\b\004\020\007¨\006\035"}, d2 = {"Lcom/jetbrains/nodejs/run/NodeJsRunConfigurationProducer;", "Lcom/intellij/execution/actions/LazyRunConfigurationProducer;", "Lcom/jetbrains/nodejs/run/NodeJsRunConfiguration;", "()V", "isAcceptable", "", "Lcom/intellij/execution/actions/ConfigurationContext;", "(Lcom/intellij/execution/actions/ConfigurationContext;)Z", "findWorkingDirectory", "Lcom/intellij/openapi/vfs/VirtualFile;", "project", "Lcom/intellij/openapi/project/Project;", "file", "contextInfo", "Lcom/jetbrains/nodejs/run/ContextInfo;", "isScratchFile", "getConfigurationFactory", "Lcom/jetbrains/nodejs/run/NodeJsRunConfigurationType;", "isConfigurationFromContext", "configuration", "context", "isPreferredConfiguration", "self", "Lcom/intellij/execution/actions/ConfigurationFromContext;", "other", "setupConfigurationFromContext", "sourceElement", "Lcom/intellij/openapi/util/Ref;", "Lcom/intellij/psi/PsiElement;", "intellij.nodeJS"})
/*    */ public final class NodeJsRunConfigurationProducer extends LazyRunConfigurationProducer<NodeJsRunConfiguration> {
/*    */   private final boolean isAcceptable(ConfigurationContext $this$isAcceptable) {
/* 23 */     RunConfiguration original = ($this$isAcceptable != null) ? $this$isAcceptable.getOriginalConfiguration(null) : null;
/* 24 */     return (original == null || original instanceof NodeJsRunConfiguration);
/*    */   }
/*    */   @NotNull
/* 27 */   public NodeJsRunConfigurationType getConfigurationFactory() { int $i$f$runConfigurationType = 0; return 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */       
/* 95 */       (NodeJsRunConfigurationType)ConfigurationTypeUtil.findConfigurationType(NodeJsRunConfigurationType.class); } private final VirtualFile findWorkingDirectory(Project project, VirtualFile file, ContextInfo contextInfo, boolean isScratchFile) { if (contextInfo != null && contextInfo.getWorkingDirectory() != null) {
/* 96 */       VirtualFile virtualFile1 = contextInfo.getWorkingDirectory(); boolean bool1 = false, bool2 = false; VirtualFile it = virtualFile1; int $i$a$-let-NodeJsRunConfigurationProducer$findWorkingDirectory$1 = 0;
/*    */       return it;
/*    */     } 
/*    */     contextInfo.getWorkingDirectory();
/*    */     VirtualFile parentDir = file.getParent();
/*    */     if (ProjectUtil.guessProjectDir(project) == null)
/*    */       ProjectUtil.guessProjectDir(project); 
/*    */     return (parentDir != null && parentDir.isInLocalFileSystem() && !isScratchFile) ? parentDir : parentDir; }
/*    */ 
/*    */   
/*    */   protected boolean setupConfigurationFromContext(@NotNull NodeJsRunConfiguration configuration, @NotNull ConfigurationContext context, @NotNull Ref sourceElement) {
/*    */     Intrinsics.checkNotNullParameter(configuration, "configuration");
/*    */     Intrinsics.checkNotNullParameter(context, "context");
/*    */     Intrinsics.checkNotNullParameter(sourceElement, "sourceElement");
/*    */     if (!isAcceptable(context))
/*    */       return false; 
/*    */     if ((PsiElement)sourceElement.get() != null && ((PsiElement)sourceElement.get()).getContainingFile() != null) {
/*    */       PsiFile psiFile = ((PsiElement)sourceElement.get()).getContainingFile();
/*    */       if (psiFile.getVirtualFile() != null) {
/*    */         VirtualFile virtualFile = psiFile.getVirtualFile();
/*    */         Intrinsics.checkNotNullExpressionValue(psiFile.getProject(), "psiFile.project");
/*    */         Project project = psiFile.getProject();
/*    */         Intrinsics.checkNotNullExpressionValue(psiFile.getLanguage(), "psiFile.language");
/*    */         psiFile.getLanguage().getAssociatedFileType();
/*    */         Intrinsics.checkNotNullExpressionValue((psiFile.getLanguage().getAssociatedFileType() != null) ? (FileType)psiFile.getLanguage().getAssociatedFileType() : virtualFile.getFileType(), "psiFile.language.associa…e ?: virtualFile.fileType");
/*    */         if (!NodeRunConfigurationLocationFilterKt.acceptFileType((psiFile.getLanguage().getAssociatedFileType() != null) ? (FileType)psiFile.getLanguage().getAssociatedFileType() : virtualFile.getFileType()))
/*    */           return false; 
/*    */         sourceElement.set(psiFile);
/*    */         ContextInfo contextInfo = (ContextInfo)context.getDataContext().getData(NodeJsRunConfigurationProducerKt.getCONTEXT_INFO());
/*    */         boolean isScratchFile = ScratchUtil.isScratch(virtualFile);
/*    */         if (StringUtil.isEmpty(configuration.getWorkingDirectory())) {
/*    */           VirtualFile workingDirectory = findWorkingDirectory(project, virtualFile, contextInfo, isScratchFile);
/*    */           configuration.setWorkingDirectory((workingDirectory != null) ? workingDirectory.getPath() : null);
/*    */         } 
/*    */         configuration.setInputPath(isScratchFile ? ScriptFileUtil.getScriptFilePath(virtualFile) : virtualFile.getPath());
/*    */         String appParams = (contextInfo != null) ? contextInfo.getApplicationParameters() : null;
/*    */         if (appParams != null) {
/*    */           CharSequence charSequence = appParams;
/*    */           boolean bool = false;
/*    */           if (!StringsKt.isBlank(charSequence))
/*    */             configuration.setApplicationParameters(appParams); 
/*    */         } 
/*    */         configuration.addCoffeeScriptNodeOptionIfNeeded();
/*    */         configuration.setGeneratedName();
/*    */         return true;
/*    */       } 
/*    */       psiFile.getVirtualFile();
/*    */       return false;
/*    */     } 
/*    */     ((PsiElement)sourceElement.get()).getContainingFile();
/*    */     return false;
/*    */   }
/*    */   
/*    */   public boolean isConfigurationFromContext(@NotNull NodeJsRunConfiguration configuration, @NotNull ConfigurationContext context) {
/*    */     Intrinsics.checkNotNullParameter(configuration, "configuration");
/*    */     Intrinsics.checkNotNullParameter(context, "context");
/*    */     if (!isAcceptable(context))
/*    */       return false; 
/*    */     if (context.getLocation() != null) {
/*    */       Intrinsics.checkNotNullExpressionValue(context.getLocation(), "context.location ?: return false");
/*    */       Location location = context.getLocation();
/*    */       if (location.getVirtualFile() != null) {
/*    */         Intrinsics.checkNotNullExpressionValue(location.getVirtualFile(), "location.virtualFile ?: return false");
/*    */         VirtualFile file = location.getVirtualFile();
/*    */         ContextInfo contextInfo = (ContextInfo)context.getDataContext().getData(NodeJsRunConfigurationProducerKt.getCONTEXT_INFO());
/*    */         if ((Intrinsics.areEqual(StringKt.nullize$default((contextInfo != null) ? contextInfo.getApplicationParameters() : null, false, 1, null), StringKt.nullize$default(configuration.getApplicationParameters(), false, 1, null)) ^ true) != 0)
/*    */           return false; 
/*    */         return (Intrinsics.areEqual(ScriptFileUtil.getScriptFilePath(file), configuration.getInputPath()) || Intrinsics.areEqual(file, DebuggableProcessRunConfigurationBase.findInputVirtualFile(configuration)));
/*    */       } 
/*    */       location.getVirtualFile();
/*    */       return false;
/*    */     } 
/*    */     context.getLocation();
/*    */     return false;
/*    */   }
/*    */   
/*    */   public boolean isPreferredConfiguration(@NotNull ConfigurationFromContext self, @Nullable ConfigurationFromContext other) {
/*    */     Intrinsics.checkNotNullParameter(self, "self");
/*    */     return false;
/*    */   }
/*    */ }


/* Location:              C:\Program Files\JetBrains\WebStorm 2021.1\plugins\NodeJS\lib\NodeJS.jar!\com\jetbrains\nodejs\run\NodeJsRunConfigurationProducer.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */