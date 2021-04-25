/*    */ package org.bipolar.run.profile.cpu.v8log.calculation;
/*    */ 
/*    */ import com.intellij.openapi.progress.ProgressIndicator;
/*    */ import com.intellij.openapi.project.Project;
/*    */ import com.intellij.util.Consumer;
/*    */ import org.bipolar.run.profile.cpu.v8log.reading.V8LogCachingReader;
/*    */ import org.bipolar.run.profile.heap.CompositeCloseable;
/*    */ import java.io.File;
/*    */ import java.io.IOException;
/*    */ import org.jetbrains.annotations.NotNull;
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public class V8RawLogProcessor
/*    */ {
/*    */   @NotNull
/*    */   private final Project myProject;
/*    */   private final File myV8log;
/*    */   private final Consumer<? super String> myNotificator;
/*    */   private final CompositeCloseable myResources;
/*    */   private V8LogCachingReader myReader;
/*    */   private V8CachingLogProcessor myCachingLogProcessor;
/*    */   
/*    */   public V8RawLogProcessor(@NotNull Project project, @NotNull File v8log, @NotNull Consumer<? super String> notificator) {
/* 26 */     this.myProject = project;
/* 27 */     this.myV8log = v8log;
/* 28 */     this.myNotificator = notificator;
/* 29 */     this.myResources = new CompositeCloseable();
/*    */   }
/*    */ 
/*    */   
/*    */   public void run(@NotNull ProgressIndicator indicator) {
/* 34 */     if (indicator == null) $$$reportNull$$$0(3);  try { this.myCachingLogProcessor = new V8CachingLogProcessor(this.myProject, this.myResources, this.myV8log, 0L);
/* 35 */       this.myReader = this.myCachingLogProcessor.getFromCacheOrProcess(indicator); }
/*    */     
/* 37 */     catch (IOException e)
/* 38 */     { this.myNotificator.consume(e.getMessage()); }
/*    */   
/*    */   }
/*    */   
/*    */   public V8LogCachingReader getReader() {
/* 43 */     return this.myReader;
/*    */   }
/*    */   
/*    */   public CompositeCloseable getResources() {
/* 47 */     return this.myResources;
/*    */   }
/*    */ }


/* Location:              C:\Program Files\JetBrains\WebStorm 2021.1\plugins\NodeJS\lib\NodeJS.jar!\com\jetbrains\nodejs\run\profile\cpu\v8log\calculation\V8RawLogProcessor.class
 * Java compiler version: 11 (55.0)
 * JD-Core Version:       1.1.3
 */