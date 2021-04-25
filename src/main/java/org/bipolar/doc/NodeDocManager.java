/*     */ package org.bipolar.doc;
/*     */ 
/*     */ import com.intellij.openapi.application.PathManager;
/*     */ import com.intellij.openapi.diagnostic.Logger;
/*     */
/*     */ import com.intellij.openapi.progress.ProgressIndicator;
/*     */ import com.intellij.openapi.progress.ProgressManager;
/*     */ import com.intellij.openapi.progress.Task;
/*     */ import com.intellij.openapi.project.Project;
/*     */ import com.intellij.openapi.util.io.FileUtil;
/*     */ import com.intellij.platform.templates.github.DownloadUtil;
/*     */ import com.intellij.util.text.SemVer;
/*     */ import com.intellij.util.ui.UIUtil;
/*     */ import org.bipolar.NodeJSBundle;
/*     */ import java.io.File;
/*     */ import java.io.IOException;
/*     */ import java.util.concurrent.Semaphore;
/*     */
import org.jetbrains.annotations.NotNull;
/*     */ import org.jetbrains.annotations.Nullable;
/*     */ 
/*     */ public class NodeDocManager
/*     */ {
/*  23 */   public static final Logger LOG = Logger.getInstance(NodeDocManager.class);
/*  24 */   private static final NodeDocManager INSTANCE = new NodeDocManager();
/*     */   
/*     */   @NotNull
/*     */   public NodeDoc getDoc(@NotNull Project project, @NotNull SemVer nodeVersion) {
/*  28 */     if (project == null) $$$reportNull$$$0(0);  if (nodeVersion == null) $$$reportNull$$$0(1);  File docDir = getDocDir(nodeVersion);
/*  29 */     downloadIfNeeded(project, nodeVersion, docDir);
/*  30 */     return new NodeDoc(nodeVersion, docDir);
/*     */   }
/*     */   
/*     */   @NotNull
/*     */   private static File getDocDir(@NotNull SemVer nodeVersion) {
/*  35 */     if (nodeVersion == null) $$$reportNull$$$0(2); 
/*  36 */     File rootDir = new File(new File(PathManager.getSystemPath(), "extLibs"), "nodejs" + File.separator + "documentation");
/*     */ 
/*     */     
/*  39 */     return new File(rootDir, nodeVersion.getParsedVersion());
/*     */   }
/*     */   
/*     */   public void downloadIfNeeded(@NotNull Project project, @NotNull SemVer nodeVersion, @NotNull File docDir) {
/*  43 */     if (project == null) $$$reportNull$$$0(3);  if (nodeVersion == null) $$$reportNull$$$0(4);  if (docDir == null) $$$reportNull$$$0(5);  File allDocJson = new File(docDir, "all.json");
/*  44 */     if (!allDocJson.isFile()) {
/*  45 */       downloadWithProgressSync(project, nodeVersion, docDir, allDocJson);
/*     */     }
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private static void downloadWithProgressSync(@NotNull Project project, @NotNull SemVer nodeVersion, @NotNull File docDir, File allDocJson) {
/*  53 */     if (project == null) $$$reportNull$$$0(6);  if (nodeVersion == null) $$$reportNull$$$0(7);  if (docDir == null) $$$reportNull$$$0(8);  Semaphore semaphore = new Semaphore(0, true);
/*  54 */     downloadWithProgressAsync(project, nodeVersion, docDir, allDocJson, semaphore);
/*     */     try {
/*  56 */       semaphore.acquire();
/*     */     }
/*  58 */     catch (InterruptedException ignored) {
/*  59 */       Thread.interrupted();
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private static void downloadWithProgressAsync(@NotNull Project project, @NotNull final SemVer nodeVersion, @NotNull final File docDir, @NotNull final File allDocJson, @Nullable final Semaphore semaphore) {
/*  68 */     if (project == null) $$$reportNull$$$0(9);  if (nodeVersion == null) $$$reportNull$$$0(10);  if (docDir == null) $$$reportNull$$$0(11);  if (allDocJson == null) $$$reportNull$$$0(12);  UIUtil.invokeLaterIfNeeded(() -> ProgressManager.getInstance().run((Task)new Task.Backgroundable(project, NodeJSBundle.message("progress.title.node.js.documentation", new Object[0]), false, null)
/*     */           {
/*     */             
/*     */             public void run(@NotNull ProgressIndicator indicator)
/*     */             {
/*  73 */               if (indicator == null) $$$reportNull$$$0(0);  try { NodeDocManager.downloadAllDocJson(nodeVersion, docDir, allDocJson);
/*  74 */                 (new NodeDocSplitter(allDocJson, docDir)).split(); }
/*     */               
/*  76 */               catch (IOException e)
/*  77 */               { NodeDocManager.LOG.warn("Cannot download Node.js " + nodeVersion.getParsedVersion() + " documentation", e); }
/*     */               finally
/*     */               
/*  80 */               { if (semaphore != null) {
/*  81 */                   semaphore.release();
/*     */                 } }
/*     */             
/*     */             }
/*     */           }));
/*     */   }
/*     */   
/*     */   private static void downloadAllDocJson(@NotNull SemVer nodeVersion, @NotNull File docDir, @NotNull File allDocJson) throws IOException {
/*  89 */     if (nodeVersion == null) $$$reportNull$$$0(13);  if (docDir == null) $$$reportNull$$$0(14);  if (allDocJson == null) $$$reportNull$$$0(15);  long startTime = System.nanoTime();
/*  90 */     FileUtil.delete(docDir);
/*  91 */     if (!FileUtil.createDirectory(docDir)) {
/*  92 */       throw new IOException("Cannot create directory for Node.js documentation " + docDir.getAbsolutePath());
/*     */     }
/*  94 */     String parsedVersion = nodeVersion.getParsedVersion();
/*  95 */     String url = "http://nodejs.org/dist/v" + parsedVersion + "/docs/api/all.json";
/*  96 */     ProgressIndicator progress = ProgressManager.getInstance().getProgressIndicator();
/*  97 */     progress.setText(
/*  98 */         NodeJSBundle.message("progress.text.getting.node.js.documentation", new Object[] { parsedVersion, "${content-length}" }));
/*  99 */     DownloadUtil.downloadAtomically(progress, url, allDocJson);
/* 100 */     long duration = System.nanoTime() - startTime;
/* 101 */     LOG.info(String.format("Node.js %s documentation %s downloaded in %.2f ms", new Object[] { parsedVersion, url, Double.valueOf(duration / 1000000.0D) }));
/*     */   }
/*     */   
/*     */   public static NodeDocManager getInstance() {
/* 105 */     return INSTANCE;
/*     */   }
/*     */ }


/* Location:              C:\Program Files\JetBrains\WebStorm 2021.1\plugins\NodeJS\lib\NodeJS.jar!\com\jetbrains\nodejs\doc\NodeDocManager.class
 * Java compiler version: 11 (55.0)
 * JD-Core Version:       1.1.3
 */