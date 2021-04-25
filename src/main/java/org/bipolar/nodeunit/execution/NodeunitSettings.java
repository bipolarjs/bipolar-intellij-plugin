/*     */ package org.bipolar.nodeunit.execution;
/*     */ 
/*     */ import com.intellij.execution.configuration.EnvironmentVariablesData;
/*     */ import com.intellij.javascript.nodejs.interpreter.NodeJsInterpreterRef;
/*     */ import com.intellij.javascript.nodejs.util.NodePackage;
/*     */ import com.intellij.openapi.util.NlsSafe;
/*     */ import com.intellij.openapi.util.io.FileUtil;
/*     */ import org.jetbrains.annotations.NotNull;
/*     */ 
/*     */ public class NodeunitSettings
/*     */ {
/*     */   private final NodeJsInterpreterRef myInterpreterRef;
/*     */   private final EnvironmentVariablesData myEnvData;
/*     */   private final NodePackage myNodeunitPackage;
/*     */   private final String myWorkingDirectory;
/*     */   private final NodeunitTestType myTestType;
/*     */   private final String myDirectory;
/*     */   private final String myJsFile;
/*     */   private final String myTestName;
/*     */   
/*     */   public NodeunitSettings(@NotNull Builder builder) {
/*  22 */     this.myInterpreterRef = builder.myInterpreterRef;
/*  23 */     this.myEnvData = builder.myEnvData;
/*  24 */     this.myNodeunitPackage = builder.myNodeunitPackage;
/*  25 */     this.myWorkingDirectory = FileUtil.toSystemIndependentName(builder.myWorkingDirectory);
/*  26 */     this.myTestType = builder.myTestType;
/*  27 */     this.myDirectory = FileUtil.toSystemIndependentName(builder.myDirectory);
/*  28 */     this.myJsFile = FileUtil.toSystemIndependentName(builder.myJsFile);
/*  29 */     this.myTestName = builder.myTestName;
/*     */   }
/*     */   
/*     */   @NotNull
/*     */   public NodeJsInterpreterRef getInterpreterRef() {
/*  34 */     if (this.myInterpreterRef == null) $$$reportNull$$$0(1);  return this.myInterpreterRef;
/*     */   }
/*     */   
/*     */   @NotNull
/*     */   public EnvironmentVariablesData getEnvData() {
/*  39 */     if (this.myEnvData == null) $$$reportNull$$$0(2);  return this.myEnvData;
/*     */   }
/*     */   
/*     */   @NotNull
/*     */   public NodePackage getNodeunitPackage() {
/*  44 */     if (this.myNodeunitPackage == null) $$$reportNull$$$0(3);  return this.myNodeunitPackage;
/*     */   } @NotNull
/*     */   @NlsSafe
/*     */   public String getWorkingDirectory() {
/*  48 */     if (this.myWorkingDirectory == null) $$$reportNull$$$0(4);  return this.myWorkingDirectory;
/*     */   }
/*     */   
/*     */   @NotNull
/*     */   public NodeunitTestType getTestType() {
/*  53 */     if (this.myTestType == null) $$$reportNull$$$0(5);  return this.myTestType;
/*     */   }
/*     */   @NotNull
/*     */   @NlsSafe
/*     */   public String getDirectory() {
/*  58 */     if (this.myDirectory == null) $$$reportNull$$$0(6);  return this.myDirectory;
/*     */   }
/*     */   @NotNull
/*     */   @NlsSafe
/*     */   public String getJsFile() {
/*  63 */     if (this.myJsFile == null) $$$reportNull$$$0(7);  return this.myJsFile;
/*     */   }
/*     */   @NotNull
/*     */   @NlsSafe
/*     */   public String getTestName() {
/*  68 */     if (this.myTestName == null) $$$reportNull$$$0(8);  return this.myTestName;
/*     */   }
/*     */   
/*     */   @NotNull
/*     */   public Builder builder() {
/*  73 */     return new Builder(this);
/*     */   }
/*     */   
/*     */   public static class Builder {
/*  77 */     private NodeJsInterpreterRef myInterpreterRef = NodeJsInterpreterRef.createProjectRef();
/*  78 */     private EnvironmentVariablesData myEnvData = EnvironmentVariablesData.DEFAULT;
/*  79 */     private NodePackage myNodeunitPackage = new NodePackage("");
/*  80 */     private String myWorkingDirectory = "";
/*  81 */     private NodeunitTestType myTestType = NodeunitTestType.JS_FILE;
/*  82 */     private String myDirectory = "";
/*  83 */     private String myJsFile = "";
/*  84 */     private String myTestName = "";
/*     */     
/*     */     public Builder() {}
/*     */     
/*     */     public Builder(@NotNull NodeunitSettings settings) {
/*  89 */       this.myInterpreterRef = settings.getInterpreterRef();
/*  90 */       this.myEnvData = settings.getEnvData();
/*  91 */       this.myNodeunitPackage = settings.getNodeunitPackage();
/*  92 */       this.myWorkingDirectory = settings.getWorkingDirectory();
/*  93 */       this.myTestType = settings.getTestType();
/*  94 */       this.myDirectory = settings.getDirectory();
/*  95 */       this.myJsFile = settings.getJsFile();
/*  96 */       this.myTestName = settings.getTestName();
/*     */     }
/*     */     
/*     */     @NotNull
/*     */     public Builder setInterpreterRef(@NotNull NodeJsInterpreterRef interpreterRef) {
/* 101 */       if (interpreterRef == null) $$$reportNull$$$0(1);  this.myInterpreterRef = interpreterRef;
/* 102 */       if (this == null) $$$reportNull$$$0(2);  return this;
/*     */     }
/*     */     
/*     */     @NotNull
/*     */     public Builder setEnvData(@NotNull EnvironmentVariablesData envData) {
/* 107 */       if (envData == null) $$$reportNull$$$0(3);  this.myEnvData = envData;
/* 108 */       if (this == null) $$$reportNull$$$0(4);  return this;
/*     */     }
/*     */     
/*     */     @NotNull
/*     */     public Builder setNodeunitPackage(@NotNull NodePackage nodeunitPackage) {
/* 113 */       if (nodeunitPackage == null) $$$reportNull$$$0(5);  this.myNodeunitPackage = nodeunitPackage;
/* 114 */       if (this == null) $$$reportNull$$$0(6);  return this;
/*     */     }
/*     */     
/*     */     @NotNull
/*     */     public Builder setTestType(@NotNull NodeunitTestType testType) {
/* 119 */       if (testType == null) $$$reportNull$$$0(7);  this.myTestType = testType;
/* 120 */       if (this == null) $$$reportNull$$$0(8);  return this;
/*     */     }
/*     */     
/*     */     @NotNull
/*     */     public Builder setWorkingDirectory(@NotNull String workingDirectory) {
/* 125 */       if (workingDirectory == null) $$$reportNull$$$0(9);  this.myWorkingDirectory = workingDirectory;
/* 126 */       if (this == null) $$$reportNull$$$0(10);  return this;
/*     */     }
/*     */     
/*     */     @NotNull
/*     */     public Builder setDirectory(@NotNull @NlsSafe String directory) {
/* 131 */       if (directory == null) $$$reportNull$$$0(11);  this.myDirectory = directory;
/* 132 */       if (this == null) $$$reportNull$$$0(12);  return this;
/*     */     }
/*     */     
/*     */     @NotNull
/*     */     public Builder setJsFilePath(@NotNull @NlsSafe String jsFile) {
/* 137 */       if (jsFile == null) $$$reportNull$$$0(13);  this.myJsFile = jsFile;
/* 138 */       if (this == null) $$$reportNull$$$0(14);  return this;
/*     */     }
/*     */     
/*     */     @NotNull
/*     */     public Builder setTest(@NotNull @NlsSafe String testName) {
/* 143 */       if (testName == null) $$$reportNull$$$0(15);  this.myTestName = testName;
/* 144 */       if (this == null) $$$reportNull$$$0(16);  return this;
/*     */     }
/*     */     
/*     */     @NotNull
/*     */     public NodeunitSettings build() {
/* 149 */       return new NodeunitSettings(this);
/*     */     }
/*     */   }
/*     */ }


/* Location:              C:\Program Files\JetBrains\WebStorm 2021.1\plugins\NodeJS\lib\NodeJS.jar!\com\jetbrains\nodejs\nodeunit\execution\NodeunitSettings.class
 * Java compiler version: 11 (55.0)
 * JD-Core Version:       1.1.3
 */