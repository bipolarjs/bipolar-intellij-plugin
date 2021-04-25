/*    */ package org.bipolar.run.profile.cpu.v8log.calculation;
/*    */ 
/*    */ import com.intellij.openapi.progress.ProgressIndicator;
/*    */ import com.intellij.openapi.project.Project;
/*    */ import org.bipolar.NodeJSBundle;
/*    */ import org.bipolar.run.profile.cpu.v8log.V8CpuIndexManager;
/*    */ import org.bipolar.run.profile.cpu.v8log.reading.V8LogCachingReader;
/*    */ import org.bipolar.run.profile.heap.CompositeCloseable;
/*    */ import org.bipolar.run.profile.heap.calculation.ByteArrayWrapper;
/*    */ import java.io.File;
/*    */ import java.io.IOException;
/*    */
import org.jetbrains.annotations.NotNull;
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public class V8CachingLogProcessor
/*    */ {
/*    */   @NotNull
/*    */   private final Project myProject;
/*    */   private final CompositeCloseable myResources;
/*    */   @NotNull
/*    */   private final File myV8log;
/*    */   private final long myNano;
/*    */   private final V8CpuIndexManager myManager;
/*    */   private final ByteArrayWrapper myDigest;
/*    */   
/*    */   public V8CachingLogProcessor(@NotNull Project project, CompositeCloseable resources, @NotNull File v8log, long distortionPerEntryNano) throws IOException {
/* 30 */     this.myProject = project;
/* 31 */     this.myResources = resources;
/* 32 */     this.myV8log = v8log;
/* 33 */     this.myNano = distortionPerEntryNano;
/* 34 */     this.myManager = new V8CpuIndexManager(v8log);
/* 35 */     this.myDigest = new ByteArrayWrapper(this.myManager.createDigest(v8log));
/*    */   }
/*    */ 
/*    */   
/*    */   public boolean willTakeFromCache() {
/* 40 */     return this.myManager.isInitialized();
/*    */   }
/*    */   
/*    */   public V8LogCachingReader getFromCacheOrProcess(@NotNull ProgressIndicator indicator) throws IOException {
/* 44 */     if (indicator == null) $$$reportNull$$$0(2);  if (this.myManager.isInitialized()) {
/*    */       try {
/* 46 */         indicator.setText(NodeJSBundle.message("progress.text.reading.indexes", new Object[0]));
/* 47 */         V8LogCachingReader v8LogCachingReader = this.myManager.initReader(this.myDigest, this.myV8log, this.myResources);
/* 48 */         if (v8LogCachingReader != null) return v8LogCachingReader; 
/* 49 */       } catch (IOException e) {
/* 50 */         this.myManager.clearRoot();
/*    */       } 
/*    */     }
/* 53 */     V8TickProcessor processor = new V8TickProcessor(this.myDigest, this.myV8log, this.myNano, this.myManager.getIndexFiles());
/* 54 */     V8LogCachingReader reader = processor.execute(this.myResources);
/* 55 */     indicator.setText(NodeJSBundle.message("progress.text.recording.indexes", new Object[0]));
/* 56 */     this.myManager.recordReader(reader);
/* 57 */     return reader;
/*    */   }
/*    */ }


/* Location:              C:\Program Files\JetBrains\WebStorm 2021.1\plugins\NodeJS\lib\NodeJS.jar!\com\jetbrains\nodejs\run\profile\cpu\v8log\calculation\V8CachingLogProcessor.class
 * Java compiler version: 11 (55.0)
 * JD-Core Version:       1.1.3
 */