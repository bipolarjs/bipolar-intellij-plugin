/*     */ package org.bipolar.run.profile.heap;
/*     */ 
/*     */ import com.intellij.execution.ExecutionException;
/*     */ import com.intellij.icons.AllIcons;
/*     */ import com.intellij.openapi.actionSystem.AnActionEvent;
/*     */ import com.intellij.openapi.application.ApplicationManager;
/*     */ import com.intellij.openapi.diagnostic.Logger;
/*     */ import com.intellij.openapi.fileChooser.FileSaverDescriptor;
/*     */
/*     */ import com.intellij.openapi.progress.ProgressIndicator;
/*     */ import com.intellij.openapi.progress.ProgressManager;
/*     */ import com.intellij.openapi.progress.Task;
/*     */ import com.intellij.openapi.project.DumbAwareAction;
/*     */ import com.intellij.openapi.project.Project;
/*     */ import com.intellij.openapi.ui.MessageType;
/*     */ import com.intellij.openapi.util.io.FileUtil;
/*     */ import com.intellij.openapi.util.text.StringUtil;
/*     */ import com.intellij.openapi.vfs.VirtualFileWrapper;
/*     */ import com.intellij.util.ObjectUtils;
/*     */ import com.intellij.xdebugger.XDebugSession;
/*     */ import com.jetbrains.debugger.wip.WipRemoteVmConnection;
/*     */ import com.jetbrains.nodeJs.NodeChromeDebugProcess;
/*     */ import org.bipolar.NodeJSBundle;
/*     */ import org.bipolar.run.profile.heap.view.main.OpenSnapshotWorker;
/*     */ import org.bipolar.run.profile.heap.view.main.SaveSnapshotDialog;
/*     */ import org.bipolar.run.profile.settings.NodeProfilingSettings;
/*     */ import java.io.File;
/*     */ import java.io.IOException;
/*     */
import org.jetbrains.annotations.Nls;
/*     */ import org.jetbrains.annotations.NonNls;
/*     */ import org.jetbrains.annotations.NotNull;
/*     */ import org.jetbrains.annotations.Nullable;
/*     */ import org.jetbrains.debugger.connection.RemoteVmConnection;
/*     */ import org.jetbrains.debugger.connection.RemoteVmConnectionKt;
/*     */ import org.jetbrains.wip.WipV8ProfilingHelper;
/*     */ import org.jetbrains.wip.WipVm;
/*     */ 
/*     */ 
/*     */ 
/*     */ public class TakeHeapSnapshotAction
/*     */   extends DumbAwareAction
/*     */ {
/*  43 */   private static final Logger LOG = Logger.getInstance(TakeHeapSnapshotAction.class);
/*     */   
/*     */   private static final String USAGE_KEY = "node.js.v8.profiling.take.heap.snapshot";
/*     */   
/*     */   @NonNls
/*     */   public static final String SNAPSHOT = "snapshot";
/*     */   
/*     */   private final Project myProject;
/*     */   private final long myPort;
/*     */   
/*     */   public TakeHeapSnapshotAction(@NotNull Project project, long port, @Nls String configurationName) {
/*  54 */     super(NodeJSBundle.message("node.js.v8.heap.take.snapshot.action.text", new Object[0]),
/*  55 */         NodeJSBundle.message("node.js.v8.heap.take.snapshot.action.description", new Object[0]), AllIcons.Actions.StartMemoryProfile);
/*  56 */     this.myProject = project;
/*  57 */     this.myPort = port;
/*  58 */     this.myConfigurationName = (String)SYNTHETIC_LOCAL_VARIABLE_4;
/*  59 */     this.myIsDisposed = false;
/*     */   } @Nls
/*     */   private final String myConfigurationName; private volatile boolean myIsDisposed; private volatile boolean myIsInProgress; private volatile WipVm myWipVm;
/*     */   public void setDisposed() {
/*  63 */     this.myIsDisposed = true;
/*  64 */     this.myWipVm = null;
/*     */   }
/*     */ 
/*     */   
/*     */   public void update(@NotNull AnActionEvent e) {
/*  69 */     if (e == null) $$$reportNull$$$0(1);  super.update(e);
/*  70 */     e.getPresentation().setEnabled((!this.myIsDisposed && !this.myIsInProgress && (this.myPort != -1L || getNodeChromeDebugProcess(e) != null)));
/*     */   }
/*     */   
/*     */   @Nullable
/*     */   private static NodeChromeDebugProcess getNodeChromeDebugProcess(AnActionEvent e) {
/*  75 */     XDebugSession session = (XDebugSession)e.getData(XDebugSession.DATA_KEY);
/*  76 */     if (session == null) return null; 
/*  77 */     NodeChromeDebugProcess process = (NodeChromeDebugProcess)ObjectUtils.tryCast(session.getDebugProcess(), NodeChromeDebugProcess.class);
/*  78 */     return (process == null || !process.canTakeHeapSnapshot()) ? null : process;
/*     */   }
/*     */ 
/*     */   
/*     */   public void actionPerformed(@NotNull AnActionEvent e) {
/*  83 */     if (e == null) $$$reportNull$$$0(2);  NodeChromeDebugProcess process = getNodeChromeDebugProcess(e);
/*  84 */     if ((process != null && process.canTakeHeapSnapshot()) || this.myPort > 0L) {
/*  85 */       ApplicationManager.getApplication().invokeLater(() -> {
/*     */             FileSaverDescriptor descriptor = new FileSaverDescriptor(this.myConfigurationName, NodeJSBundle.message("label.select.location.to.save.v8.heap.snapshot.to", new Object[0]), new String[] { "heapsnapshot" });
/*     */             SaveSnapshotDialog dialog = new SaveSnapshotDialog(descriptor, this.myProject);
/*     */             VirtualFileWrapper wrapper = dialog.save(this.myProject.getBaseDir(), suggestFileName(this.myConfigurationName));
/*     */             if (wrapper == null) {
/*     */               return;
/*     */             }
/*     */             if (!checkFilePath(this.myProject, wrapper)) {
/*     */               return;
/*     */             }
/*     */             takeSnapshot(dialog.openCreatedSnapshot(), dialog.showHiddenData(), wrapper, process);
/*     */           });
/*     */     }
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   private void takeSnapshot(final boolean openSnapshot, final boolean showHiddenData, @NotNull final VirtualFileWrapper wrapper, @Nullable final NodeChromeDebugProcess process) {
/* 103 */     if (wrapper == null) $$$reportNull$$$0(3);  this.myIsInProgress = true;
/* 104 */     ProgressManager.getInstance().run((Task)new Task.Backgroundable(this.myProject, NodeJSBundle.message("profile.heap.taking.snapshot.progress.title", new Object[0]), true, null)
/*     */         {
/*     */           private boolean myErrorReported = false;
/*     */ 
/*     */           
/*     */           public void run(@NotNull ProgressIndicator indicator) {
/* 110 */             if (indicator == null) $$$reportNull$$$0(0);  try { initVm(process);
/* 111 */               if (TakeHeapSnapshotAction.this.myWipVm == null) {
/* 112 */                 NodeProfilingSettings.HEAP_NOTIFICATION_GROUP.createNotification(
/* 113 */                     NodeJSBundle.message("profile.heap.taking_snapshot_failed.pass_inspect_brk.notification.content", new Object[0]), MessageType.ERROR).notify(this.myProject);
/*     */                 
/*     */                 return;
/*     */               } 
/* 117 */               String error = (new WipV8ProfilingHelper(TakeHeapSnapshotAction.this.myWipVm)).takeHeapSnapShot(wrapper.getFile());
/* 118 */               if (!StringUtil.isEmptyOrSpaces(error)) {
/* 119 */                 this.myErrorReported = true;
/* 120 */                 NodeProfilingSettings.HEAP_NOTIFICATION_GROUP.createNotification(error, MessageType.ERROR).notify(this.myProject);
/*     */               }
/*     */                }
/* 123 */             catch (Exception e1)
/* 124 */             { TakeHeapSnapshotAction.LOG.info(e1);
/* 125 */               this.myErrorReported = true;
/* 126 */               NodeProfilingSettings.HEAP_NOTIFICATION_GROUP.createNotification(
/* 127 */                   NodeJSBundle.message("profile.heap.taking_snapshot_failed_common.notification.content", new Object[] { e1.getMessage() }), MessageType.ERROR).notify(this.myProject); }
/*     */           
/*     */           }
/*     */ 
/*     */           
/*     */           public void onFinished() {
/* 133 */             TakeHeapSnapshotAction.this.myIsInProgress = false;
/*     */           }
/*     */ 
/*     */           
/*     */           public void onSuccess() {
/* 138 */             if (!wrapper.getFile().exists() || wrapper.getVirtualFile() == null) {
/* 139 */               if (!this.myErrorReported) {
/* 140 */                 NodeProfilingSettings.HEAP_NOTIFICATION_GROUP.createNotification(NodeJSBundle.message("profile.heap.snapshot_not_saved.notification.content", new Object[0]), MessageType.ERROR).notify(this.myProject);
/*     */               }
/*     */               return;
/*     */             } 
/* 144 */             NodeProfilingSettings.HEAP_NOTIFICATION_GROUP.createNotification(
/* 145 */                 NodeJSBundle.message("profile.heap.snapshot_saved_to.notification.content", new Object[] { TakeHeapSnapshotAction.filePathText(this.val$wrapper) }), MessageType.INFO).notify(this.myProject);
/* 146 */             if (openSnapshot) {
/* 147 */               OpenSnapshotWorker.work(this.myProject, wrapper.getVirtualFile(), showHiddenData);
/*     */             }
/*     */           }
/*     */           
/*     */           private void initVm(@Nullable NodeChromeDebugProcess process) throws ExecutionException {
/* 152 */             if (TakeHeapSnapshotAction.this.myWipVm != null)
/*     */               return; 
/* 154 */             if (process != null) {
/* 155 */               TakeHeapSnapshotAction.this.myWipVm = process.canTakeHeapSnapshot() ? (WipVm)ObjectUtils.tryCast(process.getConnection().getVm(), WipVm.class) : null;
/* 156 */             } else if (TakeHeapSnapshotAction.this.myPort > 0L) {
/* 157 */               WipRemoteVmConnection connection = new WipRemoteVmConnection();
/* 158 */               TakeHeapSnapshotAction.this.myWipVm = (WipVm)ObjectUtils.tryCast(RemoteVmConnectionKt.initRemoteVmConnectionSync((RemoteVmConnection)connection, (int)TakeHeapSnapshotAction.this.myPort), WipVm.class);
/*     */             } 
/*     */           }
/*     */         });
/*     */   }
/*     */   
/*     */   @NotNull
/*     */   private static String filePathText(VirtualFileWrapper wrapper) {
/* 166 */     File file = wrapper.getFile();
/* 167 */     if (file.getName() + file.getName() == null) $$$reportNull$$$0(4);  return file.getName() + file.getName();
/*     */   }
/*     */   
/*     */   private static String suggestFileName(String name) {
/* 171 */     String suggested = name.replace("\\", "/");
/* 172 */     suggested = StringUtil.trimEnd(suggested, "/");
/* 173 */     if (suggested.length() == 0) return "snapshot"; 
/* 174 */     int idx = suggested.lastIndexOf('/');
/* 175 */     if (idx > 0) {
/* 176 */       suggested = suggested.substring(idx + 1);
/* 177 */       if (suggested.length() == 0) return "snapshot"; 
/*     */     } 
/* 179 */     StringBuilder sb = new StringBuilder();
/* 180 */     for (int i = 0; i < suggested.length(); i++) {
/* 181 */       char c = suggested.charAt(i);
/* 182 */       if (Character.isLetterOrDigit(c)) {
/* 183 */         sb.append(c);
/*     */       } else {
/*     */         
/* 186 */         sb.append('_');
/*     */       } 
/*     */     } 
/* 189 */     return sb.toString();
/*     */   }
/*     */   
/*     */   private static boolean checkFilePath(Project project, VirtualFileWrapper wrapper) {
/* 193 */     String message = null;
/*     */     try {
/* 195 */       if (wrapper.getFile().exists()) return true; 
/* 196 */       if (wrapper.getFile().createNewFile()) {
/* 197 */         FileUtil.delete(wrapper.getFile());
/* 198 */         return true;
/*     */       }
/*     */     
/* 201 */     } catch (IOException e1) {
/* 202 */       message = e1.getMessage();
/*     */     } 
/* 204 */     NodeProfilingSettings.HEAP_NOTIFICATION_GROUP.createNotification(
/* 205 */         NodeJSBundle.message("profile.heap.cannot_create_file.notification.content", new Object[] { wrapper.getFile().getAbsolutePath(), 
/* 206 */             StringUtil.notNullize(message)
/* 207 */           }), MessageType.ERROR).notify(project);
/* 208 */     return false;
/*     */   }
/*     */ }


/* Location:              C:\Program Files\JetBrains\WebStorm 2021.1\plugins\NodeJS\lib\NodeJS.jar!\com\jetbrains\nodejs\run\profile\heap\TakeHeapSnapshotAction.class
 * Java compiler version: 11 (55.0)
 * JD-Core Version:       1.1.3
 */