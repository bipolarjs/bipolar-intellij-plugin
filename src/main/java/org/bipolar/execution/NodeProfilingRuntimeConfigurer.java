/*     */ package org.bipolar.execution;
/*     */ 
/*     */ import com.intellij.execution.ExecutionListener;
/*     */ import com.intellij.execution.ExecutionManager;
/*     */ import com.intellij.execution.configurations.RunProfile;
/*     */ import com.intellij.execution.process.ProcessHandler;
/*     */ import com.intellij.execution.runners.ExecutionEnvironment;
/*     */ import com.intellij.javascript.nodejs.NodeProfilingRuntimeSettings;
/*     */ import com.intellij.openapi.Disposable;
/*     */ import com.intellij.openapi.project.Project;
/*     */ import com.intellij.openapi.ui.MessageType;
/*     */ import com.intellij.openapi.util.Disposer;
/*     */ import com.intellij.openapi.util.io.FileUtil;
/*     */ import com.intellij.openapi.util.io.FileUtilRt;
/*     */ import com.intellij.openapi.util.text.StringUtil;
/*     */ import com.intellij.util.Alarm;
/*     */ import com.intellij.util.Consumer;
/*     */ import com.intellij.util.messages.MessageBusConnection;
/*     */ import com.intellij.util.ui.UIUtil;
/*     */ import org.bipolar.NodeJSBundle;
/*     */ import org.bipolar.run.NodeJsRunConfiguration;
/*     */
/*     */ import org.bipolar.run.profile.heap.TakeHeapSnapshotAction;
/*     */ import org.bipolar.run.profile.settings.NodeProfilingSettings;
/*     */ import java.io.File;
/*     */ import java.io.IOException;
/*     */ import java.util.ArrayList;
/*     */ import java.util.List;
/*     */ import org.jetbrains.annotations.NotNull;
/*     */ import org.jetbrains.annotations.Nullable;
/*     */ 
/*     */ 
/*     */ public class NodeProfilingRuntimeConfigurer
/*     */ {
/*     */   @NotNull
/*     */   private final NodeJsRunConfiguration myConfiguration;
/*     */   @NotNull
/*     */   private final ExecutionEnvironment myEnvironment;
/*     */   @Nullable
/*     */   private NodeProfilingRuntimeSettings myRuntimeSettings;
/*     */   private final boolean myTakeHeapSnapshots;
/*     */   private final boolean myProfileCpu;
/*     */   private final ProfilingExecutionListener myListener;
/*     */   
/*     */   public NodeProfilingRuntimeConfigurer(@NotNull NodeJsRunConfiguration configuration, @NotNull ExecutionEnvironment environment) {
/*  46 */     this.myConfiguration = configuration;
/*  47 */     this.myEnvironment = environment;
/*  48 */     this.myTakeHeapSnapshots = proxyForHeapSnapshots(this.myConfiguration);
/*  49 */     this.myProfileCpu = configurationOkForProfiling(this.myConfiguration);
/*  50 */     this.myListener = new ProfilingExecutionListener(this.myConfiguration);
/*     */   }
/*     */   
/*     */   public static boolean configurationOkForProfiling(NodeJsRunConfiguration configuration) {
/*  54 */     return (configuration.getNodeProfilingSettings().isProfile() && configuration.getNodeProfilingSettings().isOpenViewer());
/*     */   }
/*     */   
/*     */   public static boolean proxyForHeapSnapshots(NodeJsRunConfiguration configuration) {
/*  58 */     return (configuration.getNodeProfilingSettings().isAllowRuntimeHeapSnapshot() && 
/*  59 */       !StringUtil.isEmptyOrSpaces(configuration.getExePath()));
/*     */   }
/*     */   
/*     */   public void setRuntimeSettings(@Nullable NodeProfilingRuntimeSettings runtimeSettings) {
/*  63 */     this.myRuntimeSettings = runtimeSettings;
/*     */   }
/*     */ 
/*     */   
/*     */   public void onCommandLineCreation() {
/*  68 */     Project project = this.myEnvironment.getProject();
/*     */     
/*  70 */     if (this.myProfileCpu) {
/*  71 */       this.myListener.addAfter(configuration -> {
/*     */             if (this.myRuntimeSettings != null) {
/*     */               showProfilingResults();
/*     */             }
/*     */           });
/*     */     }
/*     */     
/*  78 */     if (this.myProfileCpu || this.myTakeHeapSnapshots) {
/*  79 */       MessageBusConnection connection = project.getMessageBus().connect();
/*  80 */       this.myListener.setConnection(connection);
/*  81 */       connection.subscribe(ExecutionManager.EXECUTION_TOPIC, this.myListener);
/*     */     } 
/*     */   }
/*     */   
/*     */   public boolean isTakeHeapSnapshots() {
/*  86 */     return this.myTakeHeapSnapshots;
/*     */   }
/*     */   
/*     */   public TakeHeapSnapshotAction createSnapshotAction(int inspectorPort) {
/*  90 */     assert this.myTakeHeapSnapshots;
/*     */     
/*  92 */     TakeHeapSnapshotAction action = new TakeHeapSnapshotAction(this.myConfiguration.getProject(), inspectorPort, this.myConfiguration.getName());
/*  93 */     this.myListener.addBefore(configuration -> action.setDisposed());
/*  94 */     return action;
/*     */   }
/*     */ 
/*     */   
/*     */   private void showProfilingResults() {
/*  99 */     Alarm alarm = new Alarm(Alarm.ThreadToUse.POOLED_THREAD, (Disposable)this.myEnvironment);
/* 100 */     alarm.addRequest(() -> {
/*     */           Disposer.dispose((Disposable)alarm);
/*     */ 
/*     */ 
/*     */           
/*     */           try {
/*     */             logFiles = this.myRuntimeSettings.getLogFiles();
/* 107 */           } catch (IOException e) {
/*     */             NodeProfilingSettings.CPU_NOTIFICATION_GROUP.createNotification(NodeJSBundle.message("profile.cpu.fetching.log.files.failed.notification.content", new Object[] { e.getMessage() }), MessageType.ERROR).notify(this.myConfiguration.getProject());
/*     */             return;
/*     */           } 
/*     */           if (logFiles.isEmpty()) {
/*     */             NodeProfilingSettings.CPU_NOTIFICATION_GROUP.createNotification(NodeJSBundle.message("profile.cpu.no.log.files.notification.content", new Object[0]), MessageType.ERROR).notify(this.myConfiguration.getProject());
/*     */             return;
/*     */           } 
/*     */           List<File> logFiles = copyFilesToLogFolder(logFiles);
/*     */           for (File file : logFiles) {
/*     */             UIUtil.invokeLaterIfNeeded(());
/*     */           }
/*     */         }500);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private List<File> copyFilesToLogFolder(List<File> logFiles) {
/* 129 */     File logFolder = this.myRuntimeSettings.getLogFolder();
/* 130 */     if (FileUtil.filesEqual(((File)logFiles.get(0)).getParentFile(), logFolder)) {
/* 131 */       return logFiles;
/*     */     }
/* 133 */     List<File> result = new ArrayList<>(logFiles.size());
/* 134 */     if (!logFolder.exists()) {
/* 135 */       FileUtilRt.createDirectory(logFolder);
/*     */     }
/* 137 */     for (File file : logFiles) {
/* 138 */       LogCopier logCopier = new LogCopier(this.myConfiguration.getProject(), file, logFolder);
/* 139 */       if (logCopier.copy()) {
/* 140 */         result.add(logCopier.getToFile());
/*     */       }
/*     */     } 
/* 143 */     return result;
/*     */   }
/*     */   
/*     */   private static class LogCopier {
/*     */     private static final long TIMEOUT = 300L;
/*     */     private static final int NUM_ATTEMPTS = 10;
/*     */     private final Project myProject;
/*     */     private final File myFile;
/*     */     private final File myLogFolder;
/*     */     private IOException myIoException;
/*     */     private File myToFile;
/*     */     
/*     */     LogCopier(Project project, File file, File logFolder) {
/* 156 */       this.myProject = project;
/* 157 */       this.myFile = file;
/* 158 */       this.myLogFolder = logFolder;
/*     */     }
/*     */     
/*     */     public boolean copy() {
/* 162 */       String fileName = this.myFile.getName();
/* 163 */       this.myToFile = new File(this.myLogFolder, fileName);
/* 164 */       if (this.myToFile.exists()) {
/*     */         try {
/* 166 */           this.myToFile = createNewToFile(fileName);
/*     */         }
/* 168 */         catch (IOException e) {
/* 169 */           notifyException(e);
/* 170 */           return false;
/*     */         } 
/*     */       }
/*     */       
/* 174 */       for (int i = 0; i < 10; i++) {
/* 175 */         this.myIoException = null;
/*     */         try {
/* 177 */           FileUtil.copy(this.myFile, this.myToFile);
/* 178 */           FileUtil.delete(this.myFile);
/* 179 */           return true;
/* 180 */         } catch (IOException e) {
/* 181 */           this.myIoException = e;
/* 182 */           sleep();
/*     */         } 
/*     */       } 
/* 185 */       notifyException(this.myIoException);
/* 186 */       FileUtil.delete(this.myToFile);
/* 187 */       return false;
/*     */     }
/*     */     
/*     */     public File getToFile() {
/* 191 */       return this.myToFile;
/*     */     }
/*     */     
/*     */     private static void sleep() {
/*     */       try {
/* 196 */         Thread.sleep(300L);
/*     */       }
/* 198 */       catch (InterruptedException e1) {
/* 199 */         throw new RuntimeException(e1);
/*     */       } 
/*     */     }
/*     */     
/*     */     private void notifyException(IOException e) {
/* 204 */       NodeProfilingSettings.CPU_NOTIFICATION_GROUP.createNotification(
/* 205 */           NodeJSBundle.message("profile.cpu.log.file.not.copied.notification.content", new Object[] { e.getMessage() }), MessageType.ERROR)
/*     */         
/* 207 */         .notify(this.myProject);
/*     */     }
/*     */     
/*     */     @NotNull
/*     */     private File createNewToFile(String fileName) throws IOException {
/*     */       String prefix, suffix;
/* 213 */       int dotIdx = fileName.lastIndexOf('.');
/*     */ 
/*     */       
/* 216 */       if (dotIdx == -1) {
/* 217 */         prefix = fileName;
/* 218 */         suffix = "";
/*     */       } else {
/* 220 */         prefix = fileName.substring(0, dotIdx);
/* 221 */         suffix = fileName.substring(dotIdx + 1);
/*     */       } 
/* 223 */       File toFile = FileUtil.createTempFile(this.myLogFolder, prefix, suffix, true, false);
/* 224 */       FileUtil.delete(toFile);
/* 225 */       if (toFile == null) $$$reportNull$$$0(0);  return toFile;
/*     */     }
/*     */   }
/*     */   
/*     */   private static class ProfilingExecutionListener implements ExecutionListener {
/*     */     private final List<Consumer<NodeJsRunConfiguration>> myAfterTerminationActions;
/*     */     private final List<Consumer<NodeJsRunConfiguration>> myBeforeTerminationActions;
/*     */     private final NodeJsRunConfiguration myConfiguration;
/*     */     private boolean myWasStartedOk;
/*     */     private MessageBusConnection myConnection;
/*     */     
/*     */     ProfilingExecutionListener(NodeJsRunConfiguration configuration) {
/* 237 */       this.myConfiguration = configuration;
/* 238 */       this.myAfterTerminationActions = new ArrayList<>();
/* 239 */       this.myBeforeTerminationActions = new ArrayList<>();
/* 240 */       this.myWasStartedOk = false;
/*     */     }
/*     */     
/*     */     public void addBefore(Consumer<NodeJsRunConfiguration> consumer) {
/* 244 */       this.myBeforeTerminationActions.add(consumer);
/*     */     }
/*     */     
/*     */     public void addAfter(Consumer<NodeJsRunConfiguration> consumer) {
/* 248 */       this.myAfterTerminationActions.add(consumer);
/*     */     }
/*     */     
/*     */     public boolean isEmpty() {
/* 252 */       return (this.myBeforeTerminationActions.isEmpty() && this.myAfterTerminationActions.isEmpty());
/*     */     }
/*     */ 
/*     */     
/*     */     public void processStarted(@NotNull String executorId, @NotNull ExecutionEnvironment env, @NotNull ProcessHandler handler) {
/* 257 */       if (executorId == null) $$$reportNull$$$0(0);  if (env == null) $$$reportNull$$$0(1);  if (handler == null) $$$reportNull$$$0(2);  this.myWasStartedOk = true;
/*     */     }
/*     */ 
/*     */     
/*     */     public void processTerminating(@NotNull String executorId, @NotNull ExecutionEnvironment env, @NotNull ProcessHandler handler) {
/* 262 */       if (executorId == null) $$$reportNull$$$0(3);  if (env == null) $$$reportNull$$$0(4);  if (handler == null) $$$reportNull$$$0(5);  RunProfile runProfile = env.getRunProfile();
/*     */       
/* 264 */       if (this.myConfiguration != runProfile)
/* 265 */         return;  if (!this.myWasStartedOk)
/* 266 */         return;  for (Consumer<NodeJsRunConfiguration> action : this.myBeforeTerminationActions) {
/* 267 */         action.consume(runProfile);
/*     */       }
/*     */     }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     public void processTerminated(@NotNull String executorId, @NotNull ExecutionEnvironment env, @NotNull ProcessHandler handler, int exitCode) {
/* 276 */       if (executorId == null) $$$reportNull$$$0(6);  if (env == null) $$$reportNull$$$0(7);  if (handler == null) $$$reportNull$$$0(8);  RunProfile runProfile = env.getRunProfile();
/*     */       
/* 278 */       if (this.myConfiguration != runProfile)
/* 279 */         return;  this.myConnection.disconnect();
/* 280 */       if (!this.myWasStartedOk)
/* 281 */         return;  for (Consumer<NodeJsRunConfiguration> action : this.myAfterTerminationActions) {
/* 282 */         action.consume(runProfile);
/*     */       }
/*     */     }
/*     */     
/*     */     public void setConnection(MessageBusConnection connection) {
/* 287 */       this.myConnection = connection;
/*     */     }
/*     */   }
/*     */ }


/* Location:              C:\Program Files\JetBrains\WebStorm 2021.1\plugins\NodeJS\lib\NodeJS.jar!\com\jetbrains\nodejs\execution\NodeProfilingRuntimeConfigurer.class
 * Java compiler version: 11 (55.0)
 * JD-Core Version:       1.1.3
 */