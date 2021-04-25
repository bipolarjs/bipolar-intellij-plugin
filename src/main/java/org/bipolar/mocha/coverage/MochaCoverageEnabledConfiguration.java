/*    */ package org.bipolar.mocha.coverage;
/*    */ 
/*    */ import com.intellij.coverage.CoverageRunner;
/*    */ import com.intellij.execution.configurations.RunConfigurationBase;
/*    */ import com.intellij.execution.configurations.coverage.CoverageEnabledConfiguration;
/*    */ import org.jetbrains.annotations.NotNull;
/*    */ 
/*    */ public class MochaCoverageEnabledConfiguration extends CoverageEnabledConfiguration {
/*    */   public MochaCoverageEnabledConfiguration(@NotNull RunConfigurationBase configuration) {
/* 10 */     super(configuration);
/* 11 */     MochaCoverageRunner coverageRunner = (MochaCoverageRunner)CoverageRunner.getInstance(MochaCoverageRunner.class);
/* 12 */     setCoverageRunner(coverageRunner);
/*    */   }
/*    */ }


/* Location:              C:\Program Files\JetBrains\WebStorm 2021.1\plugins\NodeJS\lib\NodeJS.jar!\com\jetbrains\nodejs\mocha\coverage\MochaCoverageEnabledConfiguration.class
 * Java compiler version: 11 (55.0)
 * JD-Core Version:       1.1.3
 */