/*     */ package org.bipolar.run.profile.settings;
/*     */ 
/*     */ import com.intellij.execution.ExecutionException;
/*     */ import com.intellij.javascript.nodejs.NodeFileTransfer;
/*     */ import com.intellij.javascript.nodejs.NodeProfilingRuntimeSettings;
/*     */ import com.intellij.openapi.util.Condition;
/*     */ import com.intellij.openapi.util.io.FileUtil;
/*     */ import com.intellij.openapi.vfs.LocalFileSystem;
/*     */ import com.intellij.util.containers.ContainerUtil;
/*     */ import org.bipolar.NodeJSBundle;
/*     */ import java.io.File;
/*     */ import java.io.IOException;
/*     */ import java.util.ArrayList;
/*     */ import java.util.Collections;
/*     */ import java.util.HashSet;
/*     */ import java.util.List;
/*     */ import java.util.Random;
/*     */ import java.util.Set;
/*     */ 
/*     */ public class NodeProfilingRuntimeSettingsImpl implements NodeProfilingRuntimeSettings {
/*  21 */   private String myNodeParameters = "";
/*     */   
/*     */   public static final String REMOTE_LOG_FOLDER = "v8logs";
/*     */   
/*     */   private final String myLogFileName;
/*     */   
/*     */   private final String myFileSuffix;
/*     */   
/*     */   private final File myLocalLogFolder;
/*     */   private final String myWorkingDir;
/*     */   private final NodeFileTransfer myTransfer;
/*     */   private final Set<String> myBeforeFileNames;
/*     */   private final Condition<String> myNameCondition;
/*     */   
/*     */   public NodeProfilingRuntimeSettingsImpl(String fileSuffix, File localLogFolder, String workingDir, NodeFileTransfer transfer) throws ExecutionException, IOException {
/*  36 */     this.myFileSuffix = fileSuffix;
/*  37 */     this.myLocalLogFolder = localLogFolder;
/*  38 */     this.myWorkingDir = workingDir;
/*  39 */     this.myTransfer = transfer;
/*  40 */     this.myNameCondition = (s -> (s.contains("v8") && s.contains(this.myFileSuffix)));
/*     */     
/*  42 */     List<String> names = this.myTransfer.listDirectoryContents(this.myWorkingDir);
/*  43 */     this.myBeforeFileNames = new HashSet<>(ContainerUtil.filter(names, this.myNameCondition));
/*  44 */     this.myLogFileName = generateLogFileName();
/*     */   }
/*     */   
/*     */   private String generateLogFileName() throws ExecutionException {
/*  48 */     String prefix = "v8-" + this.myFileSuffix;
/*  49 */     String extension = ".log";
/*  50 */     if (!this.myBeforeFileNames.contains(prefix + ".log")) return prefix + ".log"; 
/*  51 */     Random random = new Random(17L);
/*  52 */     for (int i = 0; i < 1000; i++) {
/*  53 */       String name = prefix + prefix + ".log";
/*  54 */       if (!this.myBeforeFileNames.contains(name)) return name; 
/*     */     } 
/*  56 */     throw new ExecutionException(NodeJSBundle.message("profile.cpu.cannot.generate.log.file.dialog.message", new Object[0]));
/*     */   }
/*     */ 
/*     */   
/*     */   public List<File> getLogFiles() throws IOException {
/*  61 */     List<File> files = new ArrayList<>();
/*  62 */     this.myTransfer.grouped(NodeJSBundle.message("remote.interpreter.fetching_log_files.progress.title", new Object[0]), transfer -> {
/*     */           List<String> names = this.myTransfer.listDirectoryContents(this.myWorkingDir);
/*     */           List<String> createdNames = ContainerUtil.filter(names, ());
/*     */           if (createdNames.isEmpty()) {
/*     */             return;
/*     */           }
/*     */           HashSet<String> namesSet = new HashSet<>(createdNames);
/*     */           if (!this.myTransfer.isLocal()) {
/*     */             this.myTransfer.copy(this.myWorkingDir, namesSet, this.myWorkingDir + "/v8logs");
/*     */           }
/*     */           this.myTransfer.fetch(this.myWorkingDir, namesSet, this.myLocalLogFolder.getAbsolutePath());
/*     */           try {
/*     */             if (!FileUtil.pathsEqual(this.myWorkingDir, this.myTransfer.getMappingFor(this.myLocalLogFolder.getAbsolutePath()))) {
/*     */               this.myTransfer.delete(this.myWorkingDir, namesSet);
/*     */             }
/*  77 */           } catch (ExecutionException e) {
/*     */             throw new IOException(e);
/*     */           } 
/*     */           for (String name : createdNames) {
/*     */             files.add(new File(this.myLocalLogFolder, name));
/*     */           }
/*     */         });
/*  84 */     if (files.isEmpty()) return Collections.emptyList(); 
/*  85 */     LocalFileSystem.getInstance().refreshIoFiles(files);
/*  86 */     return files;
/*     */   }
/*     */ 
/*     */   
/*     */   public String getLogFileName() {
/*  91 */     return this.myLogFileName;
/*     */   }
/*     */ 
/*     */   
/*     */   public void setNodeParameters(String nodeParameters) {
/*  96 */     this.myNodeParameters = nodeParameters;
/*     */   }
/*     */ 
/*     */   
/*     */   public String getNodeParameters() {
/* 101 */     return this.myNodeParameters;
/*     */   }
/*     */ 
/*     */   
/*     */   public File getLogFolder() {
/* 106 */     return this.myLocalLogFolder;
/*     */   }
/*     */ }


/* Location:              C:\Program Files\JetBrains\WebStorm 2021.1\plugins\NodeJS\lib\NodeJS.jar!\com\jetbrains\nodejs\run\profile\settings\NodeProfilingRuntimeSettingsImpl.class
 * Java compiler version: 11 (55.0)
 * JD-Core Version:       1.1.3
 */