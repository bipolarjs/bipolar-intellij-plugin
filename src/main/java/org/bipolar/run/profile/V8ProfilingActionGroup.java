/*    */ package org.bipolar.run.profile;
/*    */ 
/*    */ import com.intellij.openapi.actionSystem.AnActionEvent;
/*    */ import com.intellij.openapi.actionSystem.DefaultActionGroup;
/*    */ import com.intellij.openapi.project.DumbAware;
/*    */ import org.bipolar.NodeJSBundle;
/*    */ import icons.NodeJSIcons;
/*    */ import org.jetbrains.annotations.NotNull;
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
/*    */ public class V8ProfilingActionGroup
/*    */   extends DefaultActionGroup
/*    */   implements DumbAware
/*    */ {
/*    */   public void update(@NotNull AnActionEvent e) {
/* 31 */     if (e == null) $$$reportNull$$$0(0);  super.update(e);
/* 32 */     e.getPresentation().setIcon(NodeJSIcons.V8);
/* 33 */     e.getPresentation().setText(NodeJSBundle.message("profile.action_group.v8_profiling.text", new Object[0]));
/*    */   }
/*    */ }


/* Location:              C:\Program Files\JetBrains\WebStorm 2021.1\plugins\NodeJS\lib\NodeJS.jar!\com\jetbrains\nodejs\run\profile\V8ProfilingActionGroup.class
 * Java compiler version: 11 (55.0)
 * JD-Core Version:       1.1.3
 */