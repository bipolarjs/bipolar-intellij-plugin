/*     */ package org.bipolar.run.profile.cpu.v8log;
/*     */ 
/*     */ import com.intellij.notification.NotificationType;
/*     */ import com.intellij.openapi.actionSystem.AnActionEvent;
/*     */ import com.intellij.openapi.actionSystem.DataContext;
/*     */ import com.intellij.openapi.actionSystem.PlatformDataKeys;
/*     */ import com.intellij.openapi.fileChooser.FileChooser;
/*     */ import com.intellij.openapi.fileChooser.FileChooserDescriptorFactory;
/*     */ import com.intellij.openapi.progress.ProgressIndicator;
/*     */ import com.intellij.openapi.progress.ProgressManager;
/*     */ import com.intellij.openapi.progress.Task;
/*     */ import com.intellij.openapi.project.DumbAwareAction;
/*     */ import com.intellij.openapi.project.Project;
/*     */ import com.intellij.openapi.vfs.VirtualFile;
/*     */ import com.intellij.util.Consumer;
/*     */ import org.bipolar.NodeJSBundle;
/*     */ import org.bipolar.run.profile.cpu.v8log.calculation.V8RawLogProcessor;
/*     */ import org.bipolar.run.profile.cpu.v8log.reading.V8CpuViewCreatorPartner;
/*     */ import org.bipolar.run.profile.cpu.v8log.reading.V8LogCachingReader;
/*     */ import org.bipolar.run.profile.cpu.v8log.ui.FlameChartParameters;
/*     */ import org.bipolar.run.profile.cpu.view.V8ProfilingMainComponent;
/*     */ import org.bipolar.run.profile.cpu.view.ViewCreatorPartner;
/*     */ import org.bipolar.run.profile.heap.CompositeCloseable;
/*     */ import org.bipolar.run.profile.settings.NodeProfilingSettings;
/*     */ import icons.NodeJSIcons;
/*     */ import java.io.File;
/*     */ import java.util.function.Supplier;
/*     */
import org.jetbrains.annotations.NotNull;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class ReadV8LogRawAction
/*     */   extends DumbAwareAction
/*     */ {
/*  50 */   public static final Supplier<String> TOOL_WINDOW_TITLE = NodeJSBundle.messagePointer("profile.cpu.toolwindow.title", new Object[0]);
/*     */   
/*     */   public ReadV8LogRawAction() {
/*  53 */     super(NodeJSBundle.messagePointer("action.analyze.v8.profiling.log.text", new Object[0]), 
/*  54 */         NodeJSBundle.messagePointer("action.read.v8.profiling.log.file.show.cpu.profiling.results.description", new Object[0]), NodeJSIcons.OpenV8ProfilingLog);
/*     */   }
/*     */ 
/*     */   
/*     */   public void actionPerformed(@NotNull AnActionEvent e) {
/*  59 */     if (e == null) $$$reportNull$$$0(0);  DataContext dc = e.getDataContext();
/*  60 */     Project project = (Project)PlatformDataKeys.PROJECT.getData(dc);
/*  61 */     if (project == null)
/*     */       return; 
/*  63 */     VirtualFile vf = FileChooser.chooseFile(FileChooserDescriptorFactory.createSingleLocalFileDescriptor(), project, null);
/*  64 */     if (vf != null) {
/*     */       
/*  66 */       File file = new File(vf.getPath());
/*  67 */       openFile(project, file);
/*     */     } 
/*     */   }
/*     */   
/*     */   public static void openFile(final Project project, final File file) {
/*  72 */     ProgressManager.getInstance().run((Task)new Task.Backgroundable(project, NodeJSBundle.message("progress.title.process.v8.log", new Object[0]), true)
/*     */         {
/*     */           private CompositeCloseable myResources;
/*     */           
/*     */           private V8LogCachingReader myReader;
/*     */           
/*     */           public void run(@NotNull ProgressIndicator indicator) {
/*  79 */             if (indicator == null) $$$reportNull$$$0(0);  Consumer<String> notificator = ReadV8LogRawAction.createNotificator(this.myProject);
/*  80 */             V8RawLogProcessor processor = new V8RawLogProcessor(project, file, notificator);
/*  81 */             processor.run(indicator);
/*  82 */             this.myResources = processor.getResources();
/*  83 */             this.myReader = processor.getReader();
/*     */           }
/*     */ 
/*     */           
/*     */           public void onSuccess() {
/*  88 */             if (this.myReader != null) {
/*  89 */               String description = file.getName();
/*     */               
/*  91 */               V8CpuViewCreatorPartner partner = new V8CpuViewCreatorPartner(this.myProject, this.myResources, this.myReader, ReadV8LogRawAction.createNotificator(this.myProject), description, null, null, new FlameChartParameters());
/*     */ 
/*     */               
/*  94 */               V8ProfilingMainComponent.showMe(project, description, ReadV8LogRawAction.TOOL_WINDOW_TITLE.get(), NodeJSIcons.OpenV8ProfilingLog_ToolWin, 1, (ViewCreatorPartner)partner, null, description, null);
/*     */             } 
/*     */           }
/*     */         });
/*     */   }
/*     */ 
/*     */   
/*     */   @NotNull
/*     */   public static Consumer<String> createNotificator(@NotNull Project project) {
/* 103 */     if (project == null) $$$reportNull$$$0(1);  if ((s -> NodeProfilingSettings.CPU_NOTIFICATION_GROUP.createNotification(NodeJSBundle.message("profile.cpu.processing.log.error.notification.content", new Object[] { s }), NotificationType.ERROR).notify(project)) == null) $$$reportNull$$$0(2);  return s -> NodeProfilingSettings.CPU_NOTIFICATION_GROUP.createNotification(NodeJSBundle.message("profile.cpu.processing.log.error.notification.content", new Object[] { s }), NotificationType.ERROR).notify(project);
/*     */   }
/*     */ }


/* Location:              C:\Program Files\JetBrains\WebStorm 2021.1\plugins\NodeJS\lib\NodeJS.jar!\com\jetbrains\nodejs\run\profile\cpu\v8log\ReadV8LogRawAction.class
 * Java compiler version: 11 (55.0)
 * JD-Core Version:       1.1.3
 */