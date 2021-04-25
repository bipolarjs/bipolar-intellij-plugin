/*     */ package org.bipolar.mocha.coverage;
/*     */ 
/*     */ import com.intellij.execution.ExecutionException;
/*     */ import com.intellij.execution.configurations.GeneralCommandLine;
/*     */ import com.intellij.execution.runners.ExecutionEnvironment;
/*     */ import com.intellij.javascript.nodejs.NodeModuleDirectorySearchProcessor;
/*     */ import com.intellij.javascript.nodejs.NodeModuleSearchUtil;
/*     */ import com.intellij.javascript.nodejs.PackageJsonData;
/*     */ import com.intellij.javascript.nodejs.ResolvedModuleInfo;
/*     */ import com.intellij.javascript.nodejs.interpreter.NodeJsInterpreter;
/*     */ import com.intellij.javascript.nodejs.util.NodePackage;
/*     */ import com.intellij.lang.javascript.buildTools.npm.PackageJsonUtil;
/*     */ import com.intellij.openapi.project.Project;
/*     */ import com.intellij.openapi.roots.ProjectFileIndex;
/*     */ import com.intellij.openapi.util.NlsSafe;
/*     */ import com.intellij.openapi.util.io.FileUtil;
/*     */ import com.intellij.openapi.util.text.StringUtil;
/*     */ import com.intellij.openapi.vfs.LocalFileSystem;
/*     */ import com.intellij.openapi.vfs.VirtualFile;
/*     */ import com.intellij.util.containers.ContainerUtil;
/*     */ import com.intellij.util.execution.ParametersListUtil;
/*     */ import com.intellij.util.text.SemVer;
/*     */ import org.bipolar.NodeJSBundle;
/*     */ import org.bipolar.mocha.execution.MochaRunSettings;
/*     */ import org.bipolar.mocha.execution.MochaTestKind;
/*     */ import java.io.File;
/*     */ import java.io.IOException;
/*     */ import java.util.Collections;
/*     */ import java.util.List;
/*     */
import org.jetbrains.annotations.NotNull;
/*     */ import org.jetbrains.annotations.Nullable;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public final class MochaCoverageRunState
/*     */ {
/*     */   private static final String ISTANBUL_PACKAGE_NAME = "istanbul";
/*     */   @NlsSafe
/*     */   public static final String NYC_PACKAGE_NAME = "nyc";
/*     */   private final Project myProject;
/*     */   private final ExecutionEnvironment myEnv;
/*     */   private final NodePackage myMochaPackage;
/*     */   private final MochaRunSettings myRunSettings;
/*     */   private File myCoverageTempDir;
/*     */   
/*     */   private MochaCoverageRunState(@NotNull ExecutionEnvironment env, @NotNull NodePackage mochaPackage, @NotNull MochaRunSettings runSettings) {
/*  49 */     this.myProject = env.getProject();
/*  50 */     this.myEnv = env;
/*  51 */     this.myMochaPackage = mochaPackage;
/*  52 */     this.myRunSettings = runSettings;
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   @Nullable
/*     */   public static MochaCoverageRunState create(@NotNull ExecutionEnvironment env, @NotNull NodePackage mochaPackage, @NotNull MochaRunSettings runSettings) {
/*  59 */     if (env == null) $$$reportNull$$$0(3);  if (mochaPackage == null) $$$reportNull$$$0(4);  if (runSettings == null) $$$reportNull$$$0(5);  if (env.getExecutor().getId().equals("Coverage")) {
/*  60 */       return new MochaCoverageRunState(env, mochaPackage, runSettings);
/*     */     }
/*  62 */     return null;
/*     */   }
/*     */   
/*     */   @Nullable
/*     */   private VirtualFile getWorkingDirectory() {
/*  67 */     return LocalFileSystem.getInstance().findFileByPath(this.myRunSettings.getWorkingDir());
/*     */   }
/*     */   
/*     */   @NotNull
/*     */   private NodePackage findPackage(@NotNull NodeJsInterpreter interpreter) throws ExecutionException {
/*  72 */     if (interpreter == null) $$$reportNull$$$0(6);  VirtualFile workingDirectory = getWorkingDirectory();
/*  73 */     NodePackage pkg = findPackage("nyc", workingDirectory, interpreter);
/*  74 */     if (pkg == null || !pkg.isValid()) {
/*  75 */       pkg = findPackage("istanbul", workingDirectory, interpreter);
/*     */     }
/*  77 */     if (pkg != null && pkg.isValid()) {
/*  78 */       if (pkg == null) $$$reportNull$$$0(7);  return pkg;
/*     */     } 
/*  80 */     throw MochaCoveragePackageNotFoundException.create(this.myProject, this.myMochaPackage, this.myEnv, interpreter, this.myRunSettings
/*  81 */         .getWorkingDir());
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   @Nullable
/*     */   private NodePackage findPackage(@NotNull String packageName, @Nullable VirtualFile workingDirectory, @NotNull NodeJsInterpreter interpreter) {
/*  88 */     if (packageName == null) $$$reportNull$$$0(8);  if (interpreter == null) $$$reportNull$$$0(9);  if (workingDirectory != null) {
/*  89 */       ResolvedModuleInfo info = NodeModuleSearchUtil.resolveModuleFromNodeModulesDir(workingDirectory, packageName, NodeModuleDirectorySearchProcessor.PROCESSOR);
/*     */ 
/*     */       
/*  92 */       if (info != null && info.getModuleSourceRoot().isDirectory()) {
/*  93 */         return new NodePackage(info.getModuleSourceRoot().getPath());
/*     */       }
/*     */     } 
/*  96 */     File mochaNodeModulesDir = (new File(this.myMochaPackage.getSystemDependentPath())).getParentFile();
/*  97 */     if (mochaNodeModulesDir != null && mochaNodeModulesDir.getName().equals("node_modules")) {
/*  98 */       File pkgDir = new File(mochaNodeModulesDir, packageName);
/*  99 */       if (pkgDir.isDirectory()) {
/* 100 */         return new NodePackage(pkgDir.getAbsolutePath());
/*     */       }
/*     */     } 
/* 103 */     return NodePackage.findDefaultPackage(this.myProject, packageName, interpreter);
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean configure(@NotNull GeneralCommandLine commandLine, @NotNull NodeJsInterpreter interpreter) throws ExecutionException {
/* 108 */     if (commandLine == null) $$$reportNull$$$0(10);  if (interpreter == null) $$$reportNull$$$0(11);  NodePackage pkg = findPackage(interpreter);
/* 109 */     if (pkg.getName().equals("istanbul")) {
/* 110 */       configureIstanbul(commandLine, pkg);
/* 111 */       return true;
/*     */     } 
/* 113 */     configureNyc(commandLine, pkg);
/* 114 */     return false;
/*     */   }
/*     */   
/*     */   private static void throwFileNotFound(@NotNull File file, @NotNull String packageName) throws ExecutionException {
/* 118 */     if (file == null) $$$reportNull$$$0(12);  if (packageName == null) $$$reportNull$$$0(13);  throw new ExecutionException(
/* 119 */         NodeJSBundle.message("mocha.coverage.invalid_package.file_not_found.message", new Object[] { packageName, file.getAbsolutePath() }));
/*     */   }
/*     */   
/*     */   private void configureNyc(@NotNull GeneralCommandLine commandLine, @NotNull NodePackage nycPackage) throws ExecutionException {
/* 123 */     if (commandLine == null) $$$reportNull$$$0(14);  if (nycPackage == null) $$$reportNull$$$0(15);  File nycBinFile = new File(nycPackage.getSystemDependentPath(), "bin/nyc.js");
/* 124 */     if (!nycBinFile.isFile()) {
/* 125 */       throwFileNotFound(nycBinFile, "nyc");
/*     */     }
/* 127 */     commandLine.addParameter(nycBinFile.getAbsolutePath());
/* 128 */     commandLine.addParameter("--reporter=lcovonly");
/* 129 */     SemVer version = nycPackage.getVersion(this.myProject);
/* 130 */     if (version == null || !version.isGreaterOrEqualThan(15, 0, 0)) {
/* 131 */       addExtensionsIfNoConfigurationFound(commandLine);
/*     */     }
/* 133 */     commandLine.addParameters(new String[] { "--report-dir", createCoverageTempDir().getAbsolutePath() });
/*     */   }
/*     */   
/*     */   private void addExtensionsIfNoConfigurationFound(@NotNull GeneralCommandLine commandLine) {
/* 137 */     if (commandLine == null) $$$reportNull$$$0(16);  boolean nycConfigurationFound = (findConfig(".nycrc") != null);
/* 138 */     if (!nycConfigurationFound) {
/* 139 */       VirtualFile workingDirectory = getWorkingDirectory();
/* 140 */       if (workingDirectory != null) {
/* 141 */         VirtualFile packageJson = PackageJsonUtil.findUpPackageJson(workingDirectory);
/* 142 */         if (packageJson != null) {
/* 143 */           PackageJsonData data = PackageJsonData.getOrCreate(packageJson);
/* 144 */           nycConfigurationFound = data.getTopLevelProperties().contains("nyc");
/*     */         } 
/*     */       } 
/*     */     } 
/* 148 */     if (!nycConfigurationFound) {
/* 149 */       String[] extensions = { ".js", ".cjs", ".mjs", ".ts", ".tsx", ".jsx" };
/* 150 */       for (String ext : extensions) {
/* 151 */         commandLine.addParameter("--extension=" + ext);
/*     */       }
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   private void configureIstanbul(@NotNull GeneralCommandLine commandLine, @NotNull NodePackage istanbulPackage) throws ExecutionException {
/* 158 */     if (commandLine == null) $$$reportNull$$$0(17);  if (istanbulPackage == null) $$$reportNull$$$0(18);  File istanbulBinFile = new File(istanbulPackage.getSystemDependentPath(), "lib/cli.js");
/* 159 */     if (!istanbulBinFile.isFile()) {
/* 160 */       throwFileNotFound(istanbulBinFile, "istanbul");
/*     */     }
/* 162 */     commandLine.addParameter(istanbulBinFile.getAbsolutePath());
/* 163 */     commandLine.addParameter("cover");
/* 164 */     commandLine.addParameter("--report=lcovonly");
/* 165 */     VirtualFile istanbulConfig = findConfig(".istanbul.yml");
/* 166 */     if (istanbulConfig == null) {
/* 167 */       String[] extensions = { ".ts", ".js" };
/* 168 */       for (String ext : extensions) {
/* 169 */         commandLine.addParameter("--extension=" + ext);
/*     */       }
/* 171 */       List<String> testPatterns = getTestFilesPatterns();
/* 172 */       for (String testPattern : testPatterns) {
/* 173 */         commandLine.addParameters(new String[] { "-x", FileUtil.toSystemIndependentName(testPattern) });
/*     */       } 
/*     */     } 
/* 176 */     commandLine.addParameters(new String[] { "--dir", createCoverageTempDir().getAbsolutePath() });
/*     */   }
/*     */   
/*     */   @NotNull
/*     */   private File createCoverageTempDir() throws ExecutionException {
/*     */     try {
/* 182 */       File dir = FileUtil.createTempDirectory("mocha-intellij-coverage-", null);
/* 183 */       this.myCoverageTempDir = dir;
/* 184 */       if (dir == null) $$$reportNull$$$0(19);  return dir;
/*     */     }
/* 186 */     catch (IOException e) {
/* 187 */       throw new ExecutionException(NodeJSBundle.message("mocha.coverage.cannot_create_temporary_directory.message", new Object[0]), e);
/*     */     } 
/*     */   }
/*     */   
/*     */   @Nullable
/*     */   public File getCoverageDir() {
/* 193 */     return this.myCoverageTempDir;
/*     */   }
/*     */   
/*     */   @Nullable
/*     */   private VirtualFile findConfig(@NotNull String configName) {
/* 198 */     if (configName == null) $$$reportNull$$$0(20);  VirtualFile dir = LocalFileSystem.getInstance().findFileByPath(this.myRunSettings.getWorkingDir());
/* 199 */     ProjectFileIndex fileIndex = ProjectFileIndex.getInstance(this.myProject);
/* 200 */     while (dir != null && dir.isValid() && fileIndex.getContentRootForFile(dir, false) != null) {
/* 201 */       VirtualFile config = dir.findChild(configName);
/* 202 */       if (config != null) {
/* 203 */         return config;
/*     */       }
/* 205 */       dir = dir.getParent();
/*     */     } 
/* 207 */     return null;
/*     */   }
/*     */   
/*     */   @NotNull
/*     */   private List<String> getTestFilesPatterns() {
/* 212 */     if (this.myRunSettings.getTestKind() == MochaTestKind.DIRECTORY) {
/* 213 */       String basePattern = getPathRelativeToWorkingDir(this.myRunSettings.getTestDirPath());
/* 214 */       if (ContainerUtil.newArrayList((Object[])new String[] { basePattern + "/**/*.spec.*", basePattern + "/**/*.test.*" }) == null) $$$reportNull$$$0(21);  return ContainerUtil.newArrayList((Object[])new String[] { basePattern + "/**/*.spec.*", basePattern + "/**/*.test.*" });
/*     */     } 
/*     */     
/* 217 */     if (this.myRunSettings.getTestKind() == MochaTestKind.TEST_FILE || this.myRunSettings
/* 218 */       .getTestKind() == MochaTestKind.SUITE || this.myRunSettings
/* 219 */       .getTestKind() == MochaTestKind.TEST) {
/* 220 */       if (Collections.singletonList(getPathRelativeToWorkingDir(this.myRunSettings.getTestFilePath())) == null) $$$reportNull$$$0(22);  return Collections.singletonList(getPathRelativeToWorkingDir(this.myRunSettings.getTestFilePath()));
/*     */     } 
/* 222 */     if (this.myRunSettings.getTestKind() == MochaTestKind.PATTERN) {
/* 223 */       if (ParametersListUtil.parse(this.myRunSettings.getTestFilePattern()) == null) $$$reportNull$$$0(23);  return ParametersListUtil.parse(this.myRunSettings.getTestFilePattern());
/*     */     } 
/* 225 */     if (Collections.emptyList() == null) $$$reportNull$$$0(24);  return (List)Collections.emptyList();
/*     */   }
/*     */   
/*     */   @NotNull
/*     */   private String getPathRelativeToWorkingDir(@NotNull String path) {
/* 230 */     if (path == null) $$$reportNull$$$0(25);  String result = path;
/* 231 */     if (!this.myRunSettings.getWorkingDir().isEmpty() && path.startsWith(this.myRunSettings.getWorkingDir())) {
/* 232 */       String relative = path.substring(this.myRunSettings.getWorkingDir().length());
/* 233 */       if (relative.length() > 1) {
/* 234 */         result = StringUtil.trimStart(StringUtil.trimStart(relative, "\\"), "/");
/*     */       }
/*     */     } 
/* 237 */     if (StringUtil.trimEnd(StringUtil.trimEnd(result, "\\"), "/") == null) $$$reportNull$$$0(26);  return StringUtil.trimEnd(StringUtil.trimEnd(result, "\\"), "/");
/*     */   }
/*     */ }


/* Location:              C:\Program Files\JetBrains\WebStorm 2021.1\plugins\NodeJS\lib\NodeJS.jar!\com\jetbrains\nodejs\mocha\coverage\MochaCoverageRunState.class
 * Java compiler version: 11 (55.0)
 * JD-Core Version:       1.1.3
 */