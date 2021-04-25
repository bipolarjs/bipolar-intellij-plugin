/*    */ package org.bipolar.run;
/*    */ 
/*    */ import com.intellij.ide.browsers.StartBrowserPanel;
/*    */ import com.intellij.openapi.options.ConfigurationException;
/*    */ import com.intellij.openapi.options.SettingsEditor;
/*    */ import javax.swing.JComponent;
/*    */ import org.jetbrains.annotations.NotNull;
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public class StartBrowserSettingsEditor<T extends NodeJsRunConfiguration>
/*    */   extends SettingsEditor<T>
/*    */ {
/* 17 */   private final StartBrowserPanel myPanel = new StartBrowserPanel();
/*    */ 
/*    */ 
/*    */   
/*    */   protected void resetEditorFrom(@NotNull NodeJsRunConfiguration rc) {
/* 22 */     if (rc == null) $$$reportNull$$$0(0);  this.myPanel.setFromSettings(rc.getStartBrowserSettings());
/*    */   }
/*    */ 
/*    */   
/*    */   protected void applyEditorTo(@NotNull NodeJsRunConfiguration rc) throws ConfigurationException {
/* 27 */     if (rc == null) $$$reportNull$$$0(1);  rc.setStartBrowserSettings(this.myPanel.createSettings());
/*    */   }
/*    */ 
/*    */   
/*    */   @NotNull
/*    */   protected JComponent createEditor() {
/* 33 */     if (this.myPanel.getComponent() == null) $$$reportNull$$$0(2);  return this.myPanel.getComponent();
/*    */   }
/*    */ }


/* Location:              C:\Program Files\JetBrains\WebStorm 2021.1\plugins\NodeJS\lib\NodeJS.jar!\com\jetbrains\nodejs\run\StartBrowserSettingsEditor.class
 * Java compiler version: 11 (55.0)
 * JD-Core Version:       1.1.3
 */