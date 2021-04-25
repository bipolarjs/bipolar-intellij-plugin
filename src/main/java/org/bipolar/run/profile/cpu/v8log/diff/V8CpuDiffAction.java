/*    */ package org.bipolar.run.profile.cpu.v8log.diff;
/*    */ 
/*    */ import com.intellij.icons.AllIcons;
/*    */ import com.intellij.openapi.actionSystem.AnActionEvent;
/*    */ import com.intellij.openapi.fileChooser.FileChooserDescriptorFactory;
/*    */ import com.intellij.openapi.progress.ProgressManager;
/*    */ import com.intellij.openapi.progress.Task;
/*    */ import com.intellij.openapi.project.DumbAwareAction;
/*    */ import com.intellij.openapi.project.Project;
/*    */ import com.intellij.openapi.ui.MessageType;
/*    */ import com.intellij.openapi.util.io.FileUtil;
/*    */ import com.intellij.openapi.vfs.VirtualFile;
/*    */ import org.bipolar.NodeJSBundle;
/*    */ import org.bipolar.run.profile.cpu.v8log.reading.V8LogCachingReader;
/*    */ import org.bipolar.run.profile.heap.calculation.diff.ShowSnapshotDiffDialog;
/*    */ import org.bipolar.run.profile.settings.NodeProfilingSettings;
/*    */ import java.io.File;
/*    */
import org.jetbrains.annotations.NotNull;
/*    */ 
/*    */ 
/*    */ public class V8CpuDiffAction
/*    */   extends DumbAwareAction
/*    */ {
/*    */   private final Project myProject;
/*    */   private final V8LogCachingReader myReader;
/*    */   
/*    */   public V8CpuDiffAction(Project project, V8LogCachingReader reader) {
/* 28 */     super(NodeJSBundle.messagePointer("action.V8CpuDiffAction.compare.with.text", new Object[0]),
/* 29 */         NodeJSBundle.messagePointer("action.V8CpuDiffAction.compare.with.another.v8.log.description", new Object[0]), AllIcons.Actions.Diff);
/* 30 */     this.myProject = project;
/* 31 */     this.myReader = reader;
/*    */   }
/*    */ 
/*    */   
/*    */   public void actionPerformed(@NotNull AnActionEvent e) {
/* 36 */     if (e == null) $$$reportNull$$$0(0);
/*    */     
/* 38 */     ShowSnapshotDiffDialog dialog = new ShowSnapshotDiffDialog(FileChooserDescriptorFactory.createSingleFileDescriptor("log"), this.myProject, NodeJSBundle.message("profile.cpu.ShowSnapshotDiffDialog.title", new Object[0]));
/* 39 */     VirtualFile[] files = dialog.choose(this.myProject, new VirtualFile[0]);
/*    */     
/* 41 */     if (files.length != 1) {
/*    */       return;
/*    */     }
/* 44 */     VirtualFile file = files[0];
/* 45 */     if (file != null) {
/* 46 */       File changedFile = new File(file.getPath());
/* 47 */       if (FileUtil.filesEqual(changedFile, this.myReader.getV8LogFile())) {
/* 48 */         NodeProfilingSettings.HEAP_NOTIFICATION_GROUP
/* 49 */           .createNotification(NodeJSBundle.message("profile.compare_snapshot_with_itself.action.name", new Object[0]), MessageType.WARNING)
/* 50 */           .notify(this.myProject);
/*    */         
/*    */         return;
/*    */       } 
/* 54 */       CpuDiffCalculator calculateSecondAndDiff = new CpuDiffCalculator(this.myProject, this.myReader, changedFile, !dialog.isAfter());
/* 55 */       ProgressManager.getInstance().run((Task)calculateSecondAndDiff);
/*    */     } 
/*    */   }
/*    */ }


/* Location:              C:\Program Files\JetBrains\WebStorm 2021.1\plugins\NodeJS\lib\NodeJS.jar!\com\jetbrains\nodejs\run\profile\cpu\v8log\diff\V8CpuDiffAction.class
 * Java compiler version: 11 (55.0)
 * JD-Core Version:       1.1.3
 */