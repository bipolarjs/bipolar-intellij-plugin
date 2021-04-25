/*    */ package org.bipolar.mocha.coverage;
/*    */ 
/*    */ import com.intellij.coverage.CoverageDataManager;
/*    */ import com.intellij.coverage.CoverageHelper;
/*    */ import com.intellij.coverage.CoverageRunnerData;
/*    */ import com.intellij.execution.ExecutionException;
/*    */ import com.intellij.execution.ExecutionManager;
/*    */ import com.intellij.execution.configurations.ConfigurationInfoProvider;
/*    */ import com.intellij.execution.configurations.RunConfigurationBase;
/*    */ import com.intellij.execution.configurations.RunProfile;
/*    */
/*    */ import com.intellij.execution.configurations.RunnerSettings;
/*    */ import com.intellij.execution.configurations.coverage.CoverageEnabledConfiguration;
/*    */ import com.intellij.execution.process.ProcessAdapter;
/*    */ import com.intellij.execution.process.ProcessEvent;
/*    */ import com.intellij.execution.process.ProcessHandler;
/*    */ import com.intellij.execution.process.ProcessListener;
/*    */ import com.intellij.execution.runners.DefaultProgramRunnerKt;
/*    */ import com.intellij.execution.runners.ExecutionEnvironment;
/*    */ import com.intellij.execution.runners.ProgramRunner;
/*    */ import com.intellij.execution.ui.RunContentDescriptor;
/*    */ import com.intellij.openapi.application.ApplicationManager;
/*    */ import com.intellij.openapi.diagnostic.Logger;
/*    */ import com.intellij.openapi.util.io.FileUtil;
/*    */ import org.bipolar.mocha.execution.MochaRunConfiguration;
import org.bipolar.mocha.execution.MochaRunProfileState;
/*    */ import java.io.File;
/*    */ import java.io.IOException;
/*    */ import java.util.Objects;
/*    */ import org.jetbrains.annotations.NotNull;
/*    */ 
/*    */ final class MochaCoverageProgramRunner
/*    */   implements ProgramRunner<RunnerSettings> {
/* 33 */   private static final Logger LOG = Logger.getInstance(MochaCoverageProgramRunner.class);
/* 34 */   private static final String COVERAGE_RUNNER_ID = MochaCoverageProgramRunner.class.getSimpleName();
/*    */ 
/*    */   
/*    */   @NotNull
/*    */   public String getRunnerId() {
/* 39 */     if (COVERAGE_RUNNER_ID == null) $$$reportNull$$$0(0);  return COVERAGE_RUNNER_ID;
/*    */   }
/*    */ 
/*    */   
/*    */   public boolean canRun(@NotNull String executorId, @NotNull RunProfile profile) {
/* 44 */     if (executorId == null) $$$reportNull$$$0(1);  if (profile == null) $$$reportNull$$$0(2);  return ("Coverage".equals(executorId) && profile instanceof MochaRunConfiguration);
/*    */   }
/*    */ 
/*    */   
/*    */   public RunnerSettings createConfigurationData(@NotNull ConfigurationInfoProvider settingsProvider) {
/* 49 */     if (settingsProvider == null) $$$reportNull$$$0(3);  return (RunnerSettings)new CoverageRunnerData();
/*    */   }
/*    */ 
/*    */   
/*    */   public void execute(@NotNull final ExecutionEnvironment environment) throws ExecutionException {
/* 54 */     if (environment == null) $$$reportNull$$$0(4);  ExecutionManager.getInstance(environment.getProject()).startRunProfile(environment, state -> {
/*    */           RunContentDescriptor descriptor = DefaultProgramRunnerKt.executeState(state, environment, this);
/*    */           if (descriptor != null) {
/*    */             ProcessHandler handler = Objects.<ProcessHandler>requireNonNull(descriptor.getProcessHandler());
/*    */             handler.addProcessListener((ProcessListener)new ProcessAdapter()
/*    */                 {
/*    */                   public void processTerminated(@NotNull ProcessEvent event) {
/* 61 */                     if (event == null) $$$reportNull$$$0(0);  ApplicationManager.getApplication().invokeLater(() -> MochaCoverageProgramRunner.updateCoverageView(environment, (MochaRunProfileState)state), environment
/*    */                         
/* 63 */                         .getProject().getDisposed());
/*    */                   }
/*    */                 });
/*    */           } 
/*    */           return descriptor;
/*    */         });
/*    */   }
/*    */ 
/*    */   
/*    */   private static void updateCoverageView(@NotNull ExecutionEnvironment env, @NotNull MochaRunProfileState mochaState) {
/* 73 */     if (env == null) $$$reportNull$$$0(5);  if (mochaState == null) $$$reportNull$$$0(6);  RunConfigurationBase<?> runConfiguration = (RunConfigurationBase)env.getRunProfile();
/* 74 */     CoverageEnabledConfiguration coverageEnabledConfiguration = CoverageEnabledConfiguration.getOrCreate(runConfiguration);
/* 75 */     CoverageHelper.resetCoverageSuit(runConfiguration);
/* 76 */     String coverageFilePath = coverageEnabledConfiguration.getCoverageFilePath();
/* 77 */     if (coverageFilePath == null) {
/*    */       return;
/*    */     }
/* 80 */     MochaCoverageRunState coverageRunState = Objects.<MochaCoverageRunState>requireNonNull(mochaState.getCoverageRunState());
/* 81 */     File coverageDir = Objects.<File>requireNonNull(coverageRunState.getCoverageDir());
/* 82 */     File lcovFile = new File(coverageDir, "lcov.info");
/* 83 */     if (!lcovFile.isFile()) {
/* 84 */       LOG.warn("Cannot find " + lcovFile.getAbsolutePath());
/*    */       return;
/*    */     } 
/*    */     try {
/* 88 */       FileUtil.copy(lcovFile, new File(coverageFilePath));
/*    */     }
/* 90 */     catch (IOException e) {
/* 91 */       LOG.error("Cannot copy " + lcovFile.getAbsolutePath() + " to " + coverageFilePath, e);
/*    */       return;
/*    */     } 
/* 94 */     RunnerSettings runnerSettings = env.getRunnerSettings();
/* 95 */     if (runnerSettings != null) {
/* 96 */       MochaCoverageRunner coverageRunner = MochaCoverageRunner.getInstance();
/* 97 */       coverageRunner.setWorkingDirectory(mochaState.getRunSettings().getWorkingDir());
/* 98 */       coverageRunner.setInterpreter(mochaState.getRunSettings().getInterpreterRef().resolve(env.getProject()));
/* 99 */       CoverageDataManager.getInstance(env.getProject()).processGatheredCoverage(runConfiguration, runnerSettings);
/*    */     } 
/*    */   }
/*    */ }


/* Location:              C:\Program Files\JetBrains\WebStorm 2021.1\plugins\NodeJS\lib\NodeJS.jar!\com\jetbrains\nodejs\mocha\coverage\MochaCoverageProgramRunner.class
 * Java compiler version: 11 (55.0)
 * JD-Core Version:       1.1.3
 */