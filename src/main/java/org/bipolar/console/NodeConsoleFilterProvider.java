/*    */ package org.bipolar.console;
/*    */ import com.intellij.execution.CommonProgramRunConfigurationParameters;
/*    */ import com.intellij.execution.filters.Filter;
/*    */ import com.intellij.execution.ui.ConsoleView;
/*    */ import com.intellij.javascript.nodejs.NodeStackTraceFilter;
/*    */ import com.intellij.openapi.actionSystem.DataProvider;
/*    */ import com.intellij.openapi.actionSystem.LangDataKeys;
/*    */ import com.intellij.openapi.project.Project;
/*    */ import com.intellij.openapi.vfs.VirtualFile;
/*    */ import com.intellij.psi.search.GlobalSearchScope;
/*    */ import kotlin.jvm.internal.Intrinsics;
/*    */ import org.jetbrains.annotations.NotNull;
/*    */ 
/*    */ @Metadata(mv = {1, 4, 2}, bv = {1, 0, 3}, k = 1, d1 = {"\000*\n\002\030\002\n\002\030\002\n\002\b\002\n\002\020\021\n\002\030\002\n\000\n\002\030\002\n\000\n\002\030\002\n\000\n\002\030\002\n\002\b\003\b\002\030\0002\0020\001B\005¢\006\002\020\002J+\020\003\032\b\022\004\022\0020\0050\0042\006\020\006\032\0020\0072\006\020\b\032\0020\t2\006\020\n\032\0020\013H\026¢\006\002\020\fJ\033\020\003\032\b\022\004\022\0020\0050\0042\006\020\b\032\0020\tH\026¢\006\002\020\r¨\006\016"}, d2 = {"Lcom/jetbrains/nodejs/console/NodeConsoleFilterProvider;", "Lcom/intellij/execution/filters/ConsoleDependentFilterProvider;", "()V", "getDefaultFilters", "", "Lcom/intellij/execution/filters/Filter;", "consoleView", "Lcom/intellij/execution/ui/ConsoleView;", "project", "Lcom/intellij/openapi/project/Project;", "scope", "Lcom/intellij/psi/search/GlobalSearchScope;", "(Lcom/intellij/execution/ui/ConsoleView;Lcom/intellij/openapi/project/Project;Lcom/intellij/psi/search/GlobalSearchScope;)[Lcom/intellij/execution/filters/Filter;", "(Lcom/intellij/openapi/project/Project;)[Lcom/intellij/execution/filters/Filter;", "intellij.nodeJS"})
/*    */ final class NodeConsoleFilterProvider extends ConsoleDependentFilterProvider {
/*    */   @NotNull
/*    */   public Filter[] getDefaultFilters(@NotNull ConsoleView consoleView, @NotNull Project project, @NotNull GlobalSearchScope scope) {
/* 18 */     Intrinsics.checkNotNullParameter(consoleView, "consoleView"); Intrinsics.checkNotNullParameter(project, "project"); Intrinsics.checkNotNullParameter(scope, "scope");
/* 19 */     if (!(LangDataKeys.RUN_PROFILE.getData((DataProvider)consoleView) instanceof CommonProgramRunConfigurationParameters)) LangDataKeys.RUN_PROFILE.getData((DataProvider)consoleView);  (CommonProgramRunConfigurationParameters)null; String workingDirectory = (consoleView instanceof DataProvider) ? (((CommonProgramRunConfigurationParameters)null != null) ? ((CommonProgramRunConfigurationParameters)null).getWorkingDirectory() : null) : 
/*    */ 
/*    */       
/* 22 */       null;
/*    */     
/* 24 */     if (workingDirectory != null) {
/* 25 */       return new Filter[] { (Filter)new NodeStackTraceFilter(project, workingDirectory) };
/*    */     }
/* 27 */     return getDefaultFilters(project);
/*    */   }
/*    */   @NotNull
/*    */   public Filter[] getDefaultFilters(@NotNull Project project) {
/* 31 */     Intrinsics.checkNotNullParameter(project, "project"); VirtualFile root = ProjectUtil.guessProjectDir(project);
/* 32 */     return new Filter[] { (Filter)new NodeStackTraceFilter(project, root), (Filter)new WebpackErrorFilter(project, root), 
/* 33 */         (Filter)new EslintErrorFilter(project, root) };
/*    */   }
/*    */ }


/* Location:              C:\Program Files\JetBrains\WebStorm 2021.1\plugins\NodeJS\lib\NodeJS.jar!\com\jetbrains\nodejs\console\NodeConsoleFilterProvider.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */