/*     */ package org.bipolar.mocha.execution;
/*     */ 
/*     */ import com.google.common.collect.ImmutableList;
/*     */ import com.intellij.execution.configuration.EnvironmentVariablesData;
/*     */ import com.intellij.javascript.nodejs.interpreter.NodeJsInterpreterRef;
/*     */ import com.intellij.javascript.nodejs.util.NodePackage;
/*     */ import com.intellij.openapi.util.NlsSafe;
/*     */ import com.intellij.openapi.util.io.FileUtil;
/*     */ import java.util.List;
/*     */ import org.jetbrains.annotations.NotNull;
/*     */ import org.jetbrains.annotations.Nullable;
/*     */ 
/*     */ 
/*     */ public final class MochaRunSettings
/*     */ {
/*     */   private final NodeJsInterpreterRef myInterpreterRef;
/*     */   private final String myNodeOptions;
/*     */   private final NodePackage myMochaPackage;
/*     */   private final String myWorkingDir;
/*     */   private final EnvironmentVariablesData myEnvData;
/*     */   private final String myUi;
/*     */   private final String myExtraMochaOptions;
/*     */   private final MochaTestKind myTestKind;
/*     */   private final String myTestDirPath;
/*     */   private final boolean myRecursive;
/*     */   private final String myTestFilePattern;
/*     */   private final String myTestFilePath;
/*     */   private final List<String> mySuiteNames;
/*     */   private final List<String> myTestNames;
/*     */   
/*     */   private MochaRunSettings(@NotNull Builder builder) {
/*  32 */     this.myInterpreterRef = builder.myInterpreterRef;
/*  33 */     this.myNodeOptions = builder.myNodeOptions;
/*  34 */     this.myMochaPackage = builder.myMochaPackage;
/*  35 */     this.myWorkingDir = FileUtil.toSystemIndependentName(builder.myWorkingDir);
/*  36 */     this.myEnvData = builder.myEnvData;
/*  37 */     this.myUi = builder.myUi;
/*  38 */     this.myExtraMochaOptions = builder.myExtraMochaOptions;
/*  39 */     this.myTestKind = builder.myTestKind;
/*  40 */     this.myTestDirPath = FileUtil.toSystemIndependentName(builder.myTestDirPath);
/*  41 */     this.myRecursive = builder.myRecursive;
/*  42 */     this.myTestFilePattern = builder.myTestFilePattern;
/*  43 */     this.myTestFilePath = FileUtil.toSystemIndependentName(builder.myTestFilePath);
/*  44 */     this.mySuiteNames = (List<String>)ImmutableList.copyOf(builder.mySuiteNames);
/*  45 */     this.myTestNames = (List<String>)ImmutableList.copyOf(builder.myTestNames);
/*     */   }
/*     */   
/*     */   @NotNull
/*     */   public NodeJsInterpreterRef getInterpreterRef() {
/*  50 */     if (this.myInterpreterRef == null) $$$reportNull$$$0(1);  return this.myInterpreterRef;
/*     */   }
/*     */   
/*     */   @NotNull
/*     */   public String getNodeOptions() {
/*  55 */     if (this.myNodeOptions == null) $$$reportNull$$$0(2);  return this.myNodeOptions;
/*     */   }
/*     */   
/*     */   @Nullable
/*     */   public NodePackage getMochaPackage() {
/*  60 */     return this.myMochaPackage;
/*     */   }
/*     */   
/*     */   @NotNull
/*     */   public String getWorkingDir() {
/*  65 */     if (this.myWorkingDir == null) $$$reportNull$$$0(3);  return this.myWorkingDir;
/*     */   }
/*     */   
/*     */   @NotNull
/*     */   public EnvironmentVariablesData getEnvData() {
/*  70 */     if (this.myEnvData == null) $$$reportNull$$$0(4);  return this.myEnvData;
/*     */   }
/*     */   
/*     */   @NotNull
/*     */   public String getUi() {
/*  75 */     if (this.myUi == null) $$$reportNull$$$0(5);  return this.myUi;
/*     */   }
/*     */   
/*     */   @NotNull
/*     */   public String getExtraMochaOptions() {
/*  80 */     if (this.myExtraMochaOptions == null) $$$reportNull$$$0(6);  return this.myExtraMochaOptions;
/*     */   }
/*     */   
/*     */   @NotNull
/*     */   public MochaTestKind getTestKind() {
/*  85 */     if (this.myTestKind == null) $$$reportNull$$$0(7);  return this.myTestKind;
/*     */   }
/*     */   
/*     */   @NotNull
/*     */   public String getTestDirPath() {
/*  90 */     if (this.myTestDirPath == null) $$$reportNull$$$0(8);  return this.myTestDirPath;
/*     */   }
/*     */   
/*     */   public boolean isRecursive() {
/*  94 */     return this.myRecursive;
/*     */   }
/*     */   @NotNull
/*     */   @NlsSafe
/*     */   public String getTestFilePattern() {
/*  99 */     if (this.myTestFilePattern == null) $$$reportNull$$$0(9);  return this.myTestFilePattern;
/*     */   }
/*     */   
/*     */   @NotNull
/*     */   public String getTestFilePath() {
/* 104 */     if (this.myTestFilePath == null) $$$reportNull$$$0(10);  return this.myTestFilePath;
/*     */   }
/*     */   
/*     */   @NotNull
/*     */   public List<String> getSuiteNames() {
/* 109 */     if (this.mySuiteNames == null) $$$reportNull$$$0(11);  return this.mySuiteNames;
/*     */   }
/*     */   
/*     */   @NotNull
/*     */   public List<String> getTestNames() {
/* 114 */     if (this.myTestNames == null) $$$reportNull$$$0(12);  return this.myTestNames;
/*     */   }
/*     */   
/*     */   @NotNull
/*     */   public Builder builder() {
/* 119 */     return new Builder(this);
/*     */   }
/*     */   
/*     */   @NotNull
/*     */   public static Builder builder(@NotNull MochaRunSettings runSettings) {
/* 124 */     if (runSettings == null) $$$reportNull$$$0(13);  return new Builder(runSettings);
/*     */   }
/*     */   
/*     */   public static class Builder {
/* 128 */     private NodeJsInterpreterRef myInterpreterRef = NodeJsInterpreterRef.createProjectRef();
/* 129 */     private String myNodeOptions = "";
/* 130 */     private NodePackage myMochaPackage = null;
/* 131 */     private String myWorkingDir = "";
/* 132 */     private EnvironmentVariablesData myEnvData = EnvironmentVariablesData.DEFAULT;
/* 133 */     private String myUi = "";
/* 134 */     private String myExtraMochaOptions = "";
/* 135 */     private MochaTestKind myTestKind = MochaTestKind.DIRECTORY;
/* 136 */     private String myTestDirPath = "";
/*     */     private boolean myRecursive = false;
/* 138 */     private String myTestFilePattern = "";
/* 139 */     private String myTestFilePath = "";
/* 140 */     private List<String> mySuiteNames = (List<String>)ImmutableList.of();
/* 141 */     private List<String> myTestNames = (List<String>)ImmutableList.of();
/*     */ 
/*     */     
/*     */     public Builder() {}
/*     */     
/*     */     public Builder(@NotNull MochaRunSettings runSettings) {
/* 147 */       this.myInterpreterRef = runSettings.getInterpreterRef();
/* 148 */       this.myNodeOptions = runSettings.getNodeOptions();
/* 149 */       this.myMochaPackage = runSettings.getMochaPackage();
/* 150 */       this.myWorkingDir = runSettings.getWorkingDir();
/* 151 */       this.myEnvData = runSettings.getEnvData();
/* 152 */       this.myUi = runSettings.getUi();
/* 153 */       this.myExtraMochaOptions = runSettings.getExtraMochaOptions();
/* 154 */       this.myTestKind = runSettings.getTestKind();
/* 155 */       this.myTestDirPath = runSettings.getTestDirPath();
/* 156 */       this.myRecursive = runSettings.isRecursive();
/* 157 */       this.myTestFilePattern = runSettings.getTestFilePattern();
/* 158 */       this.myTestFilePath = runSettings.getTestFilePath();
/* 159 */       this.mySuiteNames = runSettings.getSuiteNames();
/* 160 */       this.myTestNames = runSettings.getTestNames();
/*     */     }
/*     */     
/*     */     @NotNull
/*     */     public Builder setInterpreterRef(@NotNull NodeJsInterpreterRef interpreterRef) {
/* 165 */       if (interpreterRef == null) $$$reportNull$$$0(1);  this.myInterpreterRef = interpreterRef;
/* 166 */       if (this == null) $$$reportNull$$$0(2);  return this;
/*     */     }
/*     */     
/*     */     @NotNull
/*     */     public Builder setNodeOptions(@NotNull String nodeOptions) {
/* 171 */       if (nodeOptions == null) $$$reportNull$$$0(3);  this.myNodeOptions = nodeOptions;
/* 172 */       if (this == null) $$$reportNull$$$0(4);  return this;
/*     */     }
/*     */     
/*     */     @NotNull
/*     */     public Builder setMochaPackage(@Nullable NodePackage mochaPackage) {
/* 177 */       this.myMochaPackage = mochaPackage;
/* 178 */       if (this == null) $$$reportNull$$$0(5);  return this;
/*     */     }
/*     */     
/*     */     @NotNull
/*     */     public Builder setWorkingDir(@NotNull String workingDir) {
/* 183 */       if (workingDir == null) $$$reportNull$$$0(6);  this.myWorkingDir = workingDir;
/* 184 */       if (this == null) $$$reportNull$$$0(7);  return this;
/*     */     }
/*     */     
/*     */     @NotNull
/*     */     public Builder setEnvData(@NotNull EnvironmentVariablesData envData) {
/* 189 */       if (envData == null) $$$reportNull$$$0(8);  this.myEnvData = envData;
/* 190 */       if (this == null) $$$reportNull$$$0(9);  return this;
/*     */     }
/*     */     
/*     */     @NotNull
/*     */     public Builder setUi(@NotNull String ui) {
/* 195 */       if (ui == null) $$$reportNull$$$0(10);  this.myUi = ui;
/* 196 */       if (this == null) $$$reportNull$$$0(11);  return this;
/*     */     }
/*     */     
/*     */     @NotNull
/*     */     public Builder setExtraMochaOptions(@NotNull String extraMochaOptions) {
/* 201 */       if (extraMochaOptions == null) $$$reportNull$$$0(12);  this.myExtraMochaOptions = extraMochaOptions;
/* 202 */       if (this == null) $$$reportNull$$$0(13);  return this;
/*     */     }
/*     */     
/*     */     @NotNull
/*     */     public Builder setTestKind(@NotNull MochaTestKind testKind) {
/* 207 */       if (testKind == null) $$$reportNull$$$0(14);  this.myTestKind = testKind;
/* 208 */       if (this == null) $$$reportNull$$$0(15);  return this;
/*     */     }
/*     */     
/*     */     @NotNull
/*     */     public Builder setTestDirPath(@NotNull String testDirPath) {
/* 213 */       if (testDirPath == null) $$$reportNull$$$0(16);  this.myTestDirPath = testDirPath;
/* 214 */       if (this == null) $$$reportNull$$$0(17);  return this;
/*     */     }
/*     */     
/*     */     @NotNull
/*     */     public Builder setRecursive(boolean recursive) {
/* 219 */       this.myRecursive = recursive;
/* 220 */       if (this == null) $$$reportNull$$$0(18);  return this;
/*     */     }
/*     */     
/*     */     @NotNull
/*     */     public Builder setTestFilePattern(@NotNull String testFilePattern) {
/* 225 */       if (testFilePattern == null) $$$reportNull$$$0(19);  this.myTestFilePattern = testFilePattern;
/* 226 */       if (this == null) $$$reportNull$$$0(20);  return this;
/*     */     }
/*     */     
/*     */     @NotNull
/*     */     public Builder setTestFilePath(@NotNull String testFilePath) {
/* 231 */       if (testFilePath == null) $$$reportNull$$$0(21);  this.myTestFilePath = testFilePath;
/* 232 */       if (this == null) $$$reportNull$$$0(22);  return this;
/*     */     }
/*     */     
/*     */     @NotNull
/*     */     public Builder setSuiteNames(@NotNull List<String> suiteNames) {
/* 237 */       if (suiteNames == null) $$$reportNull$$$0(23);  this.mySuiteNames = suiteNames;
/* 238 */       if (this == null) $$$reportNull$$$0(24);  return this;
/*     */     }
/*     */     
/*     */     @NotNull
/*     */     public Builder setTestNames(@NotNull List<String> testNames) {
/* 243 */       if (testNames == null) $$$reportNull$$$0(25);  this.myTestNames = testNames;
/* 244 */       if (this == null) $$$reportNull$$$0(26);  return this;
/*     */     }
/*     */     
/*     */     @NotNull
/*     */     public MochaRunSettings build() {
/* 249 */       return new MochaRunSettings(this);
/*     */     }
/*     */   }
/*     */ }


/* Location:              C:\Program Files\JetBrains\WebStorm 2021.1\plugins\NodeJS\lib\NodeJS.jar!\com\jetbrains\nodejs\mocha\execution\MochaRunSettings.class
 * Java compiler version: 11 (55.0)
 * JD-Core Version:       1.1.3
 */