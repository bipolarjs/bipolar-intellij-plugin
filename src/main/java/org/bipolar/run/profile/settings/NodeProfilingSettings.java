/*     */ package org.bipolar.run.profile.settings;
/*     */ 
/*     */ import com.intellij.configurationStore.XmlSerializer;
/*     */ import com.intellij.execution.ExecutionException;
/*     */ import com.intellij.execution.configurations.RuntimeConfigurationError;
/*     */ import com.intellij.javascript.nodejs.NodeFileTransfer;
/*     */ import com.intellij.javascript.nodejs.NodeProfilingRuntimeSettings;
/*     */ import com.intellij.notification.NotificationGroup;
/*     */ import com.intellij.openapi.project.Project;
/*     */ import com.intellij.openapi.util.InvalidDataException;
/*     */ import com.intellij.openapi.util.WriteExternalException;
/*     */ import com.intellij.openapi.util.text.StringUtil;
/*     */ import com.intellij.util.text.DateFormatUtil;
/*     */ import com.intellij.util.xmlb.annotations.Attribute;
/*     */ import com.intellij.util.xmlb.annotations.Tag;
/*     */ import org.bipolar.NodeJSBundle;
/*     */ import org.bipolar.run.profile.heap.view.components.V8HeapComponent;
/*     */ import java.io.File;
/*     */ import java.io.IOException;
/*     */ import java.util.Date;
/*     */
import org.jdom.Element;
/*     */ import org.jetbrains.annotations.NotNull;
/*     */ import org.jetbrains.annotations.Nullable;
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
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ @Tag("profiling")
/*     */ public class NodeProfilingSettings
/*     */ {
/*     */   public static final String CPU_TOOL_WINDOW_TITLE = "V8 Profiling";
/*  51 */   public static final NotificationGroup CPU_NOTIFICATION_GROUP = NotificationGroup.toolWindowGroup("V8 CPU Profiling Messages", "V8 Profiling");
/*     */   
/*  53 */   public static final NotificationGroup HEAP_NOTIFICATION_GROUP = NotificationGroup.toolWindowGroup("V8 Heap Snapshot Analysis Messages", V8HeapComponent.TOOL_WINDOW_TITLE.get());
/*     */   
/*     */   public static final String OPEN_TAKEN_SNAPSHOT = "Node.Profiling.Open.Snapshot.After.Save";
/*     */   
/*     */   public static final int DEFAULT_PORT = 43517;
/*     */   private boolean myProfile;
/*     */   private boolean myOpenViewer;
/*     */   @Nullable
/*  61 */   private String myLogFolder = ".";
/*     */   
/*     */   private boolean myOneLogFile;
/*     */   private boolean myAllowRuntimeHeapSnapshot;
/*     */   private String myV8ProfilerPackage;
/*     */   private int myInnerPort;
/*     */   
/*     */   public NodeProfilingSettings() {
/*  69 */     this.myProfile = false;
/*  70 */     this.myOpenViewer = true;
/*  71 */     this.myAllowRuntimeHeapSnapshot = false;
/*  72 */     this.myInnerPort = 43517;
/*     */   }
/*     */   
/*     */   public NodeProfilingSettings(NodeProfilingSettings settings) {
/*  76 */     this.myProfile = settings.myProfile;
/*  77 */     this.myOpenViewer = settings.myOpenViewer;
/*  78 */     this.myLogFolder = settings.myLogFolder;
/*  79 */     this.myOneLogFile = settings.myOneLogFile;
/*  80 */     this.myAllowRuntimeHeapSnapshot = settings.myAllowRuntimeHeapSnapshot;
/*  81 */     this.myV8ProfilerPackage = settings.myV8ProfilerPackage;
/*  82 */     this.myInnerPort = settings.myInnerPort;
/*     */   }
/*     */   
/*     */   public void check(@Nullable String pathToJsFile) throws RuntimeConfigurationError {
/*  86 */     if (this.myProfile) {
/*  87 */       if (StringUtil.isEmptyOrSpaces(this.myLogFolder)) {
/*  88 */         throw new RuntimeConfigurationError(NodeJSBundle.message("dialog.message.v8.profiling.no.log.folder.defined", new Object[0]));
/*     */       }
/*  90 */       File file = new File(this.myLogFolder);
/*  91 */       if (!file.isAbsolute() && 
/*  92 */         file.exists() && !file.isDirectory()) {
/*  93 */         throw new RuntimeConfigurationError(NodeJSBundle.message("runConfiguration.nodejs.profiling.cpu.error.log.folder.isfile", new Object[0]));
/*     */       }
/*     */     } 
/*     */ 
/*     */     
/*  98 */     if (this.myAllowRuntimeHeapSnapshot && StringUtil.isEmptyOrSpaces(pathToJsFile)) {
/*  99 */       throw new RuntimeConfigurationError(NodeJSBundle.message("runConfiguration.nodejs.profiling.heap.v8profiler.error.no.app.file", new Object[0]));
/*     */     }
/*     */   }
/*     */   
/*     */   public void setProfile(boolean profile) {
/* 104 */     this.myProfile = profile;
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public NodeProfilingRuntimeSettings createRuntimeSettings(@NotNull Project project, @NotNull String workingDirectory, @NotNull NodeFileTransfer fileTransfer) throws IOException, ExecutionException {
/* 110 */     if (project == null) $$$reportNull$$$0(0);  if (workingDirectory == null) $$$reportNull$$$0(1);  if (fileTransfer == null) $$$reportNull$$$0(2);  if (!this.myProfile) return null; 
/* 111 */     String suffix = getDateTimeSuffix();
/* 112 */     File logFolder = getLocalLogFolder(project);
/* 113 */     NodeProfilingRuntimeSettingsImpl settings = new NodeProfilingRuntimeSettingsImpl(suffix, logFolder, workingDirectory, fileTransfer);
/*     */     
/* 115 */     settings.setNodeParameters(commandLineParameters(settings.getLogFileName()));
/* 116 */     return settings;
/*     */   }
/*     */   
/*     */   @NotNull
/*     */   private File getLocalLogFolder(@NotNull Project project) {
/* 121 */     if (project == null) $$$reportNull$$$0(3);  String nonNullLogFolder = StringUtil.notNullize(this.myLogFolder);
/* 122 */     if (".".equals(nonNullLogFolder) && project.getBasePath() != null) return new File(project.getBasePath()); 
/* 123 */     File logFolder = new File(nonNullLogFolder);
/* 124 */     if (!logFolder.isAbsolute()) {
/* 125 */       logFolder = new File(project.getBasePath(), nonNullLogFolder);
/*     */     }
/* 127 */     if (logFolder == null) $$$reportNull$$$0(4);  return logFolder;
/*     */   }
/*     */   
/*     */   @NotNull
/*     */   private String commandLineParameters(String logFileName) throws ExecutionException {
/* 132 */     StringBuilder sb = new StringBuilder("--prof");
/*     */ 
/*     */ 
/*     */ 
/*     */     
/* 137 */     if (StringUtil.isEmptyOrSpaces(this.myLogFolder)) {
/* 138 */       throw new ExecutionException(NodeJSBundle.message("profile.cpu.no.log.folder.dialog.message", new Object[0]));
/*     */     }
/*     */     
/* 141 */     if (this.myOneLogFile)
/*     */     {
/* 143 */       sb.append(" --nologfile_per_isolate");
/*     */     }
/*     */ 
/*     */     
/* 147 */     sb.append(" --logfile=\"").append(logFileName).append("\"");
/* 148 */     if (sb.toString() == null) $$$reportNull$$$0(5);  return sb.toString();
/*     */   }
/*     */   
/*     */   @NotNull
/*     */   private static String getDateTimeSuffix() {
/* 153 */     Date time1 = new Date();
/* 154 */     String date = DateFormatUtil.formatDate(time1);
/* 155 */     String time = DateFormatUtil.formatTimeWithSeconds(time1);
/* 156 */     if (escapeForFileName(date, '-') + "_" + escapeForFileName(date, '-') + "-" == null) $$$reportNull$$$0(6);  return escapeForFileName(date, '-') + "_" + escapeForFileName(date, '-') + "-";
/*     */   }
/*     */   
/*     */   private static String escapeForFileName(String s, char symbol) {
/* 160 */     StringBuilder result = new StringBuilder();
/* 161 */     for (int i = 0; i < s.length(); i++) {
/* 162 */       char c = s.charAt(i);
/* 163 */       if (Character.isLetterOrDigit(c)) {
/* 164 */         result.append(c);
/*     */       } else {
/* 166 */         result.append(symbol);
/*     */       } 
/*     */     } 
/* 169 */     return result.toString();
/*     */   }
/*     */   
/*     */   public void setOpenViewer(boolean openViewer) {
/* 173 */     this.myOpenViewer = openViewer;
/*     */   }
/*     */   
/*     */   public void setLogFolder(@Nullable String logFolder) {
/* 177 */     this.myLogFolder = logFolder;
/*     */   }
/*     */   
/*     */   @Deprecated
/*     */   public void setInnerPort(int innerPort) {
/* 182 */     this.myInnerPort = innerPort;
/*     */   }
/*     */   
/*     */   public void setOneLogFile(boolean oneLogFile) {
/* 186 */     this.myOneLogFile = oneLogFile;
/*     */   }
/*     */   
/*     */   @Deprecated
/*     */   @Attribute("runtime-heap-snapshots-port")
/*     */   public int getInnerPort() {
/* 192 */     return this.myInnerPort;
/*     */   }
/*     */   
/*     */   @Attribute("do-profile")
/*     */   public boolean isProfile() {
/* 197 */     return this.myProfile;
/*     */   }
/*     */   
/*     */   @Attribute("open-viewer")
/*     */   public boolean isOpenViewer() {
/* 202 */     return this.myOpenViewer;
/*     */   }
/*     */   
/*     */   @Attribute("one-log-file")
/*     */   public boolean isOneLogFile() {
/* 207 */     return this.myOneLogFile;
/*     */   }
/*     */   
/*     */   @Attribute("log-folder")
/*     */   @Nullable
/*     */   public String getLogFolder() {
/* 213 */     return this.myLogFolder;
/*     */   }
/*     */   
/*     */   public void setAllowRuntimeHeapSnapshot(boolean allowRuntimeHeapSnapshot) {
/* 217 */     this.myAllowRuntimeHeapSnapshot = allowRuntimeHeapSnapshot;
/*     */   }
/*     */   
/*     */   @Attribute("allow-runtime-heap-snapshot")
/*     */   public boolean isAllowRuntimeHeapSnapshot() {
/* 222 */     return this.myAllowRuntimeHeapSnapshot;
/*     */   }
/*     */   
/*     */   public static NodeProfilingSettings readExternal(@NotNull Element element) throws InvalidDataException {
/* 226 */     if (element == null) $$$reportNull$$$0(7);  Element state = element.getChild("profiling");
/* 227 */     NodeProfilingSettings settings = new NodeProfilingSettings();
/* 228 */     if (state != null) {
/* 229 */       XmlSerializer.deserializeInto(state, settings);
/*     */     }
/* 231 */     return settings;
/*     */   }
/*     */   
/*     */   public void writeExternal(@NotNull Element element) throws WriteExternalException {
/* 235 */     if (element == null) $$$reportNull$$$0(8);  Element state = XmlSerializer.serialize(this);
/* 236 */     if (state != null) {
/* 237 */       element.addContent(state);
/*     */     }
/*     */   }
/*     */   
/*     */   @Deprecated
/*     */   @Attribute("v8-profiler-path")
/*     */   @Nullable
/*     */   public String getV8ProfilerPackage() {
/* 245 */     return this.myV8ProfilerPackage;
/*     */   }
/*     */   
/*     */   @Deprecated
/*     */   public void setV8ProfilerPackage(@Nullable String v8ProfilerPackage) {
/* 250 */     this.myV8ProfilerPackage = StringUtil.nullize(v8ProfilerPackage);
/*     */   }
/*     */ }


/* Location:              C:\Program Files\JetBrains\WebStorm 2021.1\plugins\NodeJS\lib\NodeJS.jar!\com\jetbrains\nodejs\run\profile\settings\NodeProfilingSettings.class
 * Java compiler version: 11 (55.0)
 * JD-Core Version:       1.1.3
 */