/*    */ package org.bipolar.run.profile.settings;
/*    */ 
/*    */ import com.intellij.ide.util.PropertiesComponent;
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
/*    */ public final class ProfilingPromptSettings
/*    */ {
/*    */   public static final String SHOW_TIMING_PROMPT = "Node.Profiling.Show.Timing.Prompt";
/* 25 */   private static final ProfilingPromptSettings ourInstance = new ProfilingPromptSettings();
/*    */   
/*    */   private boolean myShowTimingPrompt = true;
/*    */   
/*    */   public static ProfilingPromptSettings getInstance() {
/* 30 */     return ourInstance;
/*    */   }
/*    */   
/*    */   private ProfilingPromptSettings() {
/* 34 */     this.myShowTimingPrompt = PropertiesComponent.getInstance().getBoolean("Node.Profiling.Show.Timing.Prompt", true);
/*    */   }
/*    */   
/*    */   public boolean isShowTimingPrompt() {
/* 38 */     return this.myShowTimingPrompt;
/*    */   }
/*    */   
/*    */   public void hide() {
/* 42 */     this.myShowTimingPrompt = false;
/* 43 */     PropertiesComponent.getInstance().setValue("Node.Profiling.Show.Timing.Prompt", String.valueOf(false));
/*    */   }
/*    */ }


/* Location:              C:\Program Files\JetBrains\WebStorm 2021.1\plugins\NodeJS\lib\NodeJS.jar!\com\jetbrains\nodejs\run\profile\settings\ProfilingPromptSettings.class
 * Java compiler version: 11 (55.0)
 * JD-Core Version:       1.1.3
 */