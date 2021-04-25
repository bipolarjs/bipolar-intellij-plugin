/*     */ package org.bipolar.execution;
/*     */ import com.intellij.execution.Executor;
/*     */ import com.intellij.execution.Location;
/*     */ import com.intellij.execution.PsiLocation;
/*     */ import com.intellij.execution.RunManager;
/*     */ import com.intellij.execution.RunnerAndConfigurationSettings;
/*     */ import com.intellij.execution.actions.ConfigurationContext;
/*     */ import com.intellij.execution.actions.ConfigurationFromContext;
/*     */ import com.intellij.execution.actions.RunConfigurationProducer;
/*     */ import com.intellij.execution.runners.ExecutionEnvironmentBuilder;
/*     */ import com.intellij.ide.DataManager;
/*     */ import com.intellij.ide.actions.runAnything.items.RunAnythingHelpItem;
/*     */
/*     */ import com.intellij.javascript.nodejs.execution.NodeRunConfigurationLocationFilterKt;
/*     */ import com.intellij.lang.javascript.library.JSLibraryUtil;
/*     */ import com.intellij.openapi.actionSystem.CommonDataKeys;
/*     */ import com.intellij.openapi.actionSystem.DataContext;
/*     */ import com.intellij.openapi.actionSystem.DataProvider;
/*     */ import com.intellij.openapi.actionSystem.PlatformDataKeys;
/*     */ import com.intellij.openapi.actionSystem.impl.SimpleDataContext;
/*     */ import com.intellij.openapi.application.ReadAction;
/*     */ import com.intellij.openapi.diagnostic.Logger;
/*     */ import com.intellij.openapi.fileTypes.FileType;
/*     */ import com.intellij.openapi.project.Project;
/*     */ import com.intellij.openapi.project.ProjectUtil;
/*     */ import com.intellij.openapi.util.io.FileUtil;
/*     */ import com.intellij.openapi.util.text.StringUtil;
/*     */ import com.intellij.openapi.vfs.VfsUtilCore;
/*     */ import com.intellij.openapi.vfs.VirtualFile;
/*     */ import com.intellij.psi.PsiFile;
/*     */ import com.intellij.psi.search.FileTypeIndex;
/*     */ import com.intellij.psi.search.GlobalSearchScope;
/*     */ import com.intellij.psi.search.ProjectScope;
/*     */ import com.intellij.util.ObjectUtils;
/*     */ import org.bipolar.run.ContextInfo;
/*     */ import org.bipolar.run.NodeJsRunConfigurationProducer;
/*     */ import org.bipolar.run.NodeJsRunConfigurationProducerKt;
/*     */ import icons.JavaScriptLanguageIcons;
/*     */ import java.awt.Component;
/*     */ import java.util.ArrayList;
/*     */ import java.util.Collection;
/*     */ import java.util.Collections;
/*     */ import java.util.List;
/*     */ import java.util.Objects;
/*     */ import java.util.function.Supplier;
/*     */ import java.util.stream.Collectors;
/*     */ import javax.swing.Icon;
/*     */ import javax.swing.JComponent;
/*     */ import org.jetbrains.annotations.NotNull;
/*     */ import org.jetbrains.annotations.Nullable;
/*     */ 
/*     */ public class NodeRunAnythingProvider extends RunAnythingProviderBase<NodeRunAnythingProvider.RunInfo> {
/*  53 */   private static final Logger LOG = Logger.getInstance(NodeRunAnythingProvider.class);
/*     */   private static final String PREFIX = "node";
/*     */   
/*     */   @NotNull
/*     */   public Collection<RunInfo> getValues(@NotNull DataContext dataContext, @NotNull String pattern) {
/*  58 */     if (dataContext == null) $$$reportNull$$$0(0);  if (pattern == null) $$$reportNull$$$0(1);  Project project = (Project)CommonDataKeys.PROJECT.getData(dataContext);
/*  59 */     if (project == null || project.isDefault()) { if (Collections.emptyList() == null) $$$reportNull$$$0(2);  return (Collection)Collections.emptyList(); }
/*  60 */      if (!hasNodePrefix(pattern)) { if (Collections.emptyList() == null) $$$reportNull$$$0(3);  return (Collection)Collections.emptyList(); }
/*  61 */      VirtualFile root = ProjectUtil.guessProjectDir(project);
/*  62 */     if (root == null) { if (Collections.emptyList() == null) $$$reportNull$$$0(4);  return (Collection)Collections.emptyList(); }
/*  63 */      if ((Collection<RunInfo>)listFilesInProject(project).stream()
/*  64 */       .map(file -> VfsUtilCore.getRelativePath(file, root))
/*  65 */       .filter(Objects::nonNull)
/*  66 */       .sorted()
/*  67 */       .map(path -> new RunInfo(path, null))
/*  68 */       .collect(Collectors.toList()) == null) $$$reportNull$$$0(5);  return (Collection<RunInfo>)listFilesInProject(project).stream().map(file -> VfsUtilCore.getRelativePath(file, root)).filter(Objects::nonNull).sorted().map(path -> new RunInfo(path, null)).collect(Collectors.toList());
/*     */   }
/*     */   
/*     */   @Nullable
/*     */   public RunInfo findMatchingValue(@NotNull DataContext dataContext, @NotNull String pattern) {
/*  73 */     if (dataContext == null) $$$reportNull$$$0(6);  if (pattern == null) $$$reportNull$$$0(7);  if (!hasNodePrefix(pattern)) return null; 
/*  74 */     String pathAndArgs = pattern.substring("node".length()).trim();
/*  75 */     int pathEndInd = pathAndArgs.indexOf(' ');
/*  76 */     if (pathEndInd == -1) pathEndInd = pathAndArgs.length(); 
/*  77 */     RunInfo runInfo = new RunInfo(pathAndArgs.substring(0, pathEndInd).trim(), pathAndArgs.substring(pathEndInd).trim());
/*  78 */     Project project = (Project)CommonDataKeys.PROJECT.getData(dataContext);
/*  79 */     VirtualFile root = (project != null) ? ProjectUtil.guessProjectDir(project) : null;
/*  80 */     if (root == null) return null; 
/*  81 */     VirtualFile file = root.findFileByRelativePath(FileUtil.toSystemIndependentName(runInfo.myRelativePath));
/*  82 */     return (file != null && file.isValid() && !file.isDirectory()) ? runInfo : null;
/*     */   }
/*     */   
/*     */   private static boolean hasNodePrefix(@NotNull String pattern) {
/*  86 */     if (pattern == null) $$$reportNull$$$0(8);  return (pattern.startsWith("node") && (pattern.length() == "node".length() || pattern.charAt("node".length()) == ' '));
/*     */   }
/*     */ 
/*     */   
/*     */   public void execute(@NotNull DataContext dataContext, @NotNull RunInfo value) {
/*  91 */     if (dataContext == null) $$$reportNull$$$0(9);  if (value == null) $$$reportNull$$$0(10);  DataContext updatedDataContext = createDataContext(dataContext, value);
/*  92 */     if (updatedDataContext == null)
/*  93 */       return;  Project project = Objects.<Project>requireNonNull((Project)CommonDataKeys.PROJECT.getData(updatedDataContext));
/*  94 */     DataProvider extraDataProvider = dataId -> {
/*     */         VirtualFile workingDirectory = (VirtualFile)dataContext.getData(CommonDataKeys.VIRTUAL_FILE);
/*     */ 
/*     */         
/*     */         return NodeJsRunConfigurationProducerKt.getCONTEXT_INFO().is(dataId) ? new ContextInfo(workingDirectory, value.myAppParams) : null;
/*     */       };
/*     */     
/* 101 */     Component component = (Component)PlatformDataKeys.CONTEXT_COMPONENT.getData(dataContext);
/* 102 */     RunnerAndConfigurationSettings settings = computeWithExtraDataContext(component, extraDataProvider, () -> {
/*     */           ConfigurationContext context = ConfigurationContext.getFromContext(updatedDataContext);
/*     */           NodeJsRunConfigurationProducer producer = (NodeJsRunConfigurationProducer)RunConfigurationProducer.getInstance(NodeJsRunConfigurationProducer.class);
/*     */           return getOrCreate(project, producer, context);
/*     */         });
/* 107 */     if (settings != null) {
/* 108 */       Executor executor = (Executor)ObjectUtils.chooseNotNull(updatedDataContext.getData(RunAnythingAction.EXECUTOR_KEY), 
/* 109 */           DefaultRunExecutor.getRunExecutorInstance());
/* 110 */       ExecutionEnvironmentBuilder builder = ExecutionEnvironmentBuilder.createOrNull(executor, settings);
/* 111 */       if (builder != null) {
/* 112 */         ExecutionManager.getInstance(project).restartRunProfile(builder.build());
/*     */       }
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   @Nullable
/*     */   private static <T> T computeWithExtraDataContext(@Nullable Component component, @NotNull DataProvider extraDataProvider, @NotNull Supplier<T> supplier) {
/* 120 */     if (extraDataProvider == null) $$$reportNull$$$0(11);  if (supplier == null) $$$reportNull$$$0(12);  JComponent c = (JComponent)ObjectUtils.tryCast(component, JComponent.class);
/* 121 */     if (c == null) {
/* 122 */       LOG.info("Cannot use data component: " + component);
/* 123 */       return supplier.get();
/*     */     } 
/* 125 */     DataProvider oldProvider = DataManager.getDataProvider(c);
/*     */     try {
/* 127 */       DataManager.registerDataProvider(c, dataId -> {
/*     */             Object data = extraDataProvider.getData(dataId);
/*     */ 
/*     */             
/*     */             return (data != null) ? data : ((oldProvider != null) ? oldProvider.getData(dataId) : null);
/*     */           });
/*     */       
/* 134 */       return supplier.get();
/*     */     } finally {
/*     */       
/* 137 */       if (oldProvider != null) {
/* 138 */         DataManager.registerDataProvider(c, oldProvider);
/*     */       }
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   @Nullable
/*     */   private static RunnerAndConfigurationSettings getOrCreate(@NotNull Project project, @NotNull NodeJsRunConfigurationProducer producer, @NotNull ConfigurationContext context) {
/* 146 */     if (project == null) $$$reportNull$$$0(13);  if (producer == null) $$$reportNull$$$0(14);  if (context == null) $$$reportNull$$$0(15);  RunManager runManager = RunManager.getInstance(project);
/* 147 */     RunnerAndConfigurationSettings settings = producer.findExistingConfiguration(context);
/* 148 */     if (settings == null) {
/* 149 */       ConfigurationFromContext configurationFromContext = producer.findOrCreateConfigurationFromContext(context);
/* 150 */       if (configurationFromContext == null) return null; 
/* 151 */       settings = configurationFromContext.getConfigurationSettings();
/* 152 */       settings.setTemporary(true);
/* 153 */       runManager.setUniqueNameIfNeeded(settings);
/* 154 */       runManager.addConfiguration(settings);
/*     */     } 
/* 156 */     runManager.setSelectedConfiguration(settings);
/* 157 */     return settings;
/*     */   }
/*     */   @Nullable
/*     */   private static DataContext createDataContext(@NotNull DataContext parentDataContext, @NotNull RunInfo runInfo) {
/* 161 */     if (parentDataContext == null) $$$reportNull$$$0(16);  if (runInfo == null) $$$reportNull$$$0(17);  Project project = (Project)CommonDataKeys.PROJECT.getData(parentDataContext);
/* 162 */     VirtualFile root = (project != null) ? ProjectUtil.guessProjectDir(project) : null;
/* 163 */     if (root == null) return null; 
/* 164 */     VirtualFile file = root.findFileByRelativePath(FileUtil.toSystemIndependentName(runInfo.myRelativePath));
/* 165 */     if (file == null || file.isDirectory() || !file.isValid()) return null; 
/* 166 */     PsiFile psiFile = PsiManager.getInstance(project).findFile(file);
/* 167 */     if (psiFile == null || !psiFile.isValid()) return null; 
/* 168 */     return SimpleDataContext.getSimpleContext(Location.DATA_KEY, PsiLocation.fromPsiElement((PsiElement)psiFile), parentDataContext);
/*     */   }
/*     */   @NotNull
/*     */   private static List<VirtualFile> listFilesInProject(@NotNull Project project) {
/* 172 */     if (project == null) $$$reportNull$$$0(18);  GlobalSearchScope contentScope = ProjectScope.getContentScope(project);
/* 173 */     GlobalSearchScope scope = contentScope.intersectWith(GlobalSearchScope.notScope(ProjectScope.getLibrariesScope(project)));
/* 174 */     List<VirtualFile> result = new ArrayList<>();
/* 175 */     ReadAction.run(() -> {
/*     */           for (FileType type : NodeRunConfigurationLocationFilterKt.getAllRegisteredFileTypes()) {
/*     */             Collection<VirtualFile> files = FileTypeIndex.getFiles(type, scope);
/*     */             
/*     */             for (VirtualFile file : files) {
/*     */               if (file != null && file.isValid() && !file.isDirectory() && !JSLibraryUtil.isProbableLibraryFile(file)) {
/*     */                 result.add(file);
/*     */               }
/*     */             } 
/*     */           } 
/*     */         });
/* 186 */     if (result == null) $$$reportNull$$$0(19);  return result;
/*     */   }
/*     */   
/*     */   @NotNull
/*     */   public String getCommand(@NotNull RunInfo value) {
/* 191 */     if (value == null) $$$reportNull$$$0(20);  if ("node " + FileUtil.toSystemDependentName(value.myRelativePath) == null) $$$reportNull$$$0(21);  return "node " + FileUtil.toSystemDependentName(value.myRelativePath);
/*     */   }
/*     */   
/*     */   @Nullable
/*     */   public String getCompletionGroupTitle() {
/* 196 */     return "Node.js";
/*     */   }
/*     */   
/*     */   @Nullable
/*     */   public String getHelpGroupTitle() {
/* 201 */     return "Node.js";
/*     */   }
/*     */   
/*     */   @Nullable
/*     */   public String getHelpCommand() {
/* 206 */     return "node <script path>";
/*     */   }
/*     */   
/*     */   @Nullable
/*     */   public Icon getHelpIcon() {
/* 211 */     return JavaScriptLanguageIcons.Nodejs.Nodejs;
/*     */   }
/*     */   
/*     */   @Nullable
/*     */   public Icon getIcon(@NotNull RunInfo value) {
/* 216 */     if (value == null) $$$reportNull$$$0(22);  return JavaScriptLanguageIcons.Nodejs.Nodejs;
/*     */   }
/*     */   
/*     */   @Nullable
/*     */   public RunAnythingHelpItem getHelpItem(@NotNull DataContext dataContext) {
/* 221 */     if (dataContext == null) $$$reportNull$$$0(23);  return super.getHelpItem(dataContext);
/*     */   }
/*     */   
/*     */   static final class RunInfo
/*     */   {
/*     */     private final String myRelativePath;
/*     */     private final String myAppParams;
/*     */     
/*     */     private RunInfo(@NotNull String relativePath, @Nullable String appParams) {
/* 230 */       this.myRelativePath = relativePath;
/* 231 */       this.myAppParams = StringUtil.nullize(StringUtil.notNullize(appParams).trim());
/*     */     }
/*     */   }
/*     */ }


/* Location:              C:\Program Files\JetBrains\WebStorm 2021.1\plugins\NodeJS\lib\NodeJS.jar!\com\jetbrains\nodejs\execution\NodeRunAnythingProvider.class
 * Java compiler version: 11 (55.0)
 * JD-Core Version:       1.1.3
 */