/*    */ package org.bipolar.run.profile.heap.view.main;
/*    */ 
/*    */ import com.intellij.openapi.progress.ProgressIndicator;
/*    */ import com.intellij.openapi.progress.ProgressManager;
/*    */ import com.intellij.openapi.progress.Task;
/*    */ import com.intellij.openapi.project.Project;
/*    */ import com.intellij.openapi.ui.MessageType;
/*    */ import com.intellij.openapi.vfs.VirtualFile;
/*    */ import com.intellij.util.Consumer;
/*    */ import com.intellij.util.concurrency.annotations.RequiresEdt;
/*    */ import com.intellij.util.ui.UIUtil;
/*    */ import org.bipolar.NodeJSBundle;
/*    */ import org.bipolar.run.profile.heap.V8CachingReader;
/*    */ import org.bipolar.run.profile.heap.calculation.ByteArrayWrapper;
/*    */ import org.bipolar.run.profile.heap.calculation.V8HeapIndexManager;
/*    */ import org.bipolar.run.profile.heap.calculation.V8HeapProcessor;
/*    */ import org.bipolar.run.profile.heap.view.components.HeapViewCreatorPartner;
/*    */ import org.bipolar.run.profile.heap.view.components.V8HeapComponent;
/*    */ import org.bipolar.run.profile.heap.view.components.V8HeapComponentPartner;
/*    */ import org.bipolar.run.profile.settings.NodeProfilingSettings;
/*    */ import java.io.File;
/*    */ import java.io.IOException;
/*    */
import org.jetbrains.annotations.NotNull;
/*    */ 
/*    */ 
/*    */ public final class OpenSnapshotWorker
/*    */   extends Task.Backgroundable
/*    */ {
/*    */   private final VirtualFile myFile;
/*    */   private final boolean myShowHiddenData;
/*    */   private V8CachingReader myCachingReader;
/*    */   
/*    */   private OpenSnapshotWorker(Project project, VirtualFile file, boolean showHiddenData) {
/* 34 */     super(project, NodeJSBundle.message("progress.title.processing.heap.snapshot", new Object[] { file.getName() }), true, null);
/* 35 */     this.myFile = file;
/* 36 */     this.myShowHiddenData = showHiddenData;
/*    */   }
/*    */   
/*    */   @RequiresEdt
/*    */   public static void work(Project project, VirtualFile file, boolean showHiddenData) {
/*    */     try {
/* 42 */       byte[] bytes = V8HeapIndexManager.snapshotDigest(new File(file.getPath()), showHiddenData);
/* 43 */       if (bytes != null && 
/* 44 */         V8HeapComponent.getInstance(project).activateIfOpen(new ByteArrayWrapper(bytes))) {
/*    */         return;
/*    */       }
/* 47 */     } catch (IOException e) {
/* 48 */       createNotificator(project).consume(e.getMessage());
/*    */     } 
/* 50 */     ProgressManager.getInstance().run((Task)new OpenSnapshotWorker(project, file, showHiddenData));
/*    */   }
/*    */ 
/*    */ 
/*    */   
/*    */   public void run(@NotNull ProgressIndicator indicator) {
/* 56 */     if (indicator == null) $$$reportNull$$$0(0);  try { V8HeapProcessor processor = new V8HeapProcessor(createNotificator(this.myProject), new File(this.myFile.getPath()), this.myShowHiddenData, indicator);
/* 57 */       this.myCachingReader = processor.getFromCacheOrProcess(); }
/*    */     
/* 59 */     catch (IOException e1)
/* 60 */     { notifyOnException(e1); }
/*    */     
/* 62 */     catch (ClassNotFoundException e1)
/* 63 */     { notifyOnException(e1); }
/*    */   
/*    */   }
/*    */   
/*    */   @NotNull
/*    */   public static Consumer<String> createNotificator(Project project) {
/* 69 */     if ((message -> NodeProfilingSettings.HEAP_NOTIFICATION_GROUP.createNotification(NodeJSBundle.message("profile.heap.error.notification.content", new Object[] { message }), MessageType.ERROR).notify(project)) == null) $$$reportNull$$$0(1);  return message -> NodeProfilingSettings.HEAP_NOTIFICATION_GROUP.createNotification(NodeJSBundle.message("profile.heap.error.notification.content", new Object[] { message }), MessageType.ERROR).notify(project);
/*    */   }
/*    */ 
/*    */ 
/*    */   
/*    */   private void notifyOnException(Exception e1) {
/* 75 */     V8HeapProcessor.LOG.info(e1);
/* 76 */     NodeProfilingSettings.HEAP_NOTIFICATION_GROUP
/* 77 */       .createNotification(NodeJSBundle.message("profile.heap.processing.snapshot.error.notification.content", new Object[] { e1.getMessage() }), MessageType.ERROR)
/* 78 */       .notify(this.myProject);
/*    */   }
/*    */ 
/*    */   
/*    */   public void onSuccess() {
/* 83 */     if (this.myCachingReader != null)
/* 84 */       UIUtil.invokeLaterIfNeeded(() -> {
/*    */             try {
/*    */               String name = this.myFile.getName();
/*    */               
/*    */               name = name.endsWith(".heapsnapshot") ? name.substring(0, name.length() - "heapsnapshot".length() - 1) : name;
/*    */               
/*    */               V8HeapComponent.getInstance(this.myProject).showMe(this.myCachingReader.getDigest(), (HeapViewCreatorPartner)new V8HeapComponentPartner(this.myCachingReader, name), name, null);
/* 91 */             } catch (IOException e) {
/*    */               notifyOnException(e);
/*    */             } 
/*    */           }); 
/*    */   }
/*    */ }


/* Location:              C:\Program Files\JetBrains\WebStorm 2021.1\plugins\NodeJS\lib\NodeJS.jar!\com\jetbrains\nodejs\run\profile\heap\view\main\OpenSnapshotWorker.class
 * Java compiler version: 11 (55.0)
 * JD-Core Version:       1.1.3
 */