/*    */ package org.bipolar.run;
/*    */ 
/*    */ import com.intellij.openapi.vfs.VirtualFile;
/*    */ import kotlin.Metadata;
/*    */ import kotlin.jvm.internal.Intrinsics;
/*    */ import org.jetbrains.annotations.NotNull;
/*    */ import org.jetbrains.annotations.Nullable;
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
/*    */ @Metadata(mv = {1, 4, 2}, bv = {1, 0, 3}, k = 1, d1 = {"\000(\n\002\030\002\n\002\020\000\n\000\n\002\030\002\n\000\n\002\020\016\n\002\b\t\n\002\020\013\n\002\b\002\n\002\020\b\n\002\b\002\b\b\030\0002\0020\001B\031\022\b\020\002\032\004\030\0010\003\022\b\020\004\032\004\030\0010\005¢\006\002\020\006J\013\020\013\032\004\030\0010\003HÆ\003J\013\020\f\032\004\030\0010\005HÆ\003J!\020\r\032\0020\0002\n\b\002\020\002\032\004\030\0010\0032\n\b\002\020\004\032\004\030\0010\005HÆ\001J\023\020\016\032\0020\0172\b\020\020\032\004\030\0010\001HÖ\003J\t\020\021\032\0020\022HÖ\001J\t\020\023\032\0020\005HÖ\001R\023\020\004\032\004\030\0010\005¢\006\b\n\000\032\004\b\007\020\bR\023\020\002\032\004\030\0010\003¢\006\b\n\000\032\004\b\t\020\n¨\006\024"}, d2 = {"Lcom/jetbrains/nodejs/run/ContextInfo;", "", "workingDirectory", "Lcom/intellij/openapi/vfs/VirtualFile;", "applicationParameters", "", "(Lcom/intellij/openapi/vfs/VirtualFile;Ljava/lang/String;)V", "getApplicationParameters", "()Ljava/lang/String;", "getWorkingDirectory", "()Lcom/intellij/openapi/vfs/VirtualFile;", "component1", "component2", "copy", "equals", "", "other", "hashCode", "", "toString", "intellij.nodeJS"})
/*    */ public final class ContextInfo
/*    */ {
/*    */   @Nullable
/*    */   private final VirtualFile workingDirectory;
/*    */   @Nullable
/*    */   private final String applicationParameters;
/*    */   
/*    */   @Nullable
/*    */   public final VirtualFile getWorkingDirectory() {
/* 94 */     return this.workingDirectory; } @Nullable public final String getApplicationParameters() { return this.applicationParameters; } public ContextInfo(@Nullable VirtualFile workingDirectory, @Nullable String applicationParameters) { this.workingDirectory = workingDirectory; this.applicationParameters = applicationParameters; }
/*    */ 
/*    */   
/*    */   @Nullable
/*    */   public final VirtualFile component1() {
/*    */     return this.workingDirectory;
/*    */   }
/*    */   
/*    */   @Nullable
/*    */   public final String component2() {
/*    */     return this.applicationParameters;
/*    */   }
/*    */   
/*    */   @NotNull
/*    */   public final ContextInfo copy(@Nullable VirtualFile workingDirectory, @Nullable String applicationParameters) {
/*    */     return new ContextInfo(workingDirectory, applicationParameters);
/*    */   }
/*    */   
/*    */   @NotNull
/*    */   public String toString() {
/*    */     return "ContextInfo(workingDirectory=" + this.workingDirectory + ", applicationParameters=" + this.applicationParameters + ")";
/*    */   }
/*    */   
/*    */   public int hashCode() {
/*    */     return ((this.workingDirectory != null) ? this.workingDirectory.hashCode() : 0) * 31 + ((this.applicationParameters != null) ? this.applicationParameters.hashCode() : 0);
/*    */   }
/*    */   
/*    */   public boolean equals(@Nullable Object paramObject) {
/*    */     if (this != paramObject) {
/*    */       if (paramObject instanceof ContextInfo) {
/*    */         ContextInfo contextInfo = (ContextInfo)paramObject;
/*    */         if (Intrinsics.areEqual(this.workingDirectory, contextInfo.workingDirectory) && Intrinsics.areEqual(this.applicationParameters, contextInfo.applicationParameters))
/*    */           return true; 
/*    */       } 
/*    */     } else {
/*    */       return true;
/*    */     } 
/*    */     return false;
/*    */   }
/*    */ }


/* Location:              C:\Program Files\JetBrains\WebStorm 2021.1\plugins\NodeJS\lib\NodeJS.jar!\com\jetbrains\nodejs\run\ContextInfo.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */