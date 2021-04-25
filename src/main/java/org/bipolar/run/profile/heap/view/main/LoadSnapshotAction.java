/*    */ package org.bipolar.run.profile.heap.view.main;
/*    */ 
/*    */ import com.intellij.openapi.actionSystem.AnActionEvent;
/*    */ import com.intellij.openapi.fileChooser.FileChooserDescriptorFactory;
/*    */ import com.intellij.openapi.project.DumbAwareAction;
/*    */ import com.intellij.openapi.project.Project;
/*    */ import com.intellij.openapi.vfs.VirtualFile;
/*    */ import org.bipolar.NodeJSBundle;
/*    */ import icons.NodeJSIcons;
/*    */
import org.jetbrains.annotations.NotNull;
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
/*    */ public class LoadSnapshotAction
/*    */   extends DumbAwareAction
/*    */ {
/*    */   public LoadSnapshotAction() {
/* 34 */     super(NodeJSBundle.messagePointer("action.LoadSnapshotAction.analyze.v8.heap.snapshot.text", new Object[0]),
/* 35 */         NodeJSBundle.messagePointer("action.LoadSnapshotAction.analyze.v8.heap.snapshot.description", new Object[0]), NodeJSIcons.OpenV8HeapSnapshot);
/*    */   }
/*    */ 
/*    */   
/*    */   public void actionPerformed(@NotNull AnActionEvent e) {
/* 40 */     if (e == null) $$$reportNull$$$0(0);  Project project = e.getProject();
/* 41 */     if (project == null)
/*    */       return; 
/* 43 */     LoadSnapshotDialog dialog = new LoadSnapshotDialog(FileChooserDescriptorFactory.createSingleFileDescriptor("heapsnapshot"), project);
/* 44 */     VirtualFile[] files = dialog.choose(project, new VirtualFile[0]);
/*    */     
/* 46 */     if (files.length == 1)
/* 47 */       OpenSnapshotWorker.work(project, files[0], dialog.showHiddenData()); 
/*    */   }
/*    */ }


/* Location:              C:\Program Files\JetBrains\WebStorm 2021.1\plugins\NodeJS\lib\NodeJS.jar!\com\jetbrains\nodejs\run\profile\heap\view\main\LoadSnapshotAction.class
 * Java compiler version: 11 (55.0)
 * JD-Core Version:       1.1.3
 */