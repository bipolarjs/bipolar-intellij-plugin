/*    */ package org.bipolar.run.profile;
/*    */ 
/*    */ import com.intellij.icons.AllIcons;
/*    */ import com.intellij.openapi.actionSystem.AnActionEvent;
/*    */ import com.intellij.openapi.project.DumbAwareAction;
/*    */ import com.intellij.ui.tabs.JBTabs;
/*    */ import com.intellij.ui.tabs.TabInfo;
/*    */ import org.bipolar.NodeJSBundle;
/*    */ import org.jetbrains.annotations.NotNull;
/*    */ 
/*    */ 
/*    */ public class CloseTabAction
/*    */   extends DumbAwareAction
/*    */ {
/*    */   private final JBTabs myPane;
/*    */   private TabInfo myInfo;
/*    */   
/*    */   public CloseTabAction(JBTabs pane) {
/* 19 */     super(NodeJSBundle.messagePointer("action.CloseTabAction.close.tab.text", new Object[0]), 
/* 20 */         NodeJSBundle.messagePointer("action.CloseTabAction.close.tab.description", new Object[0]), AllIcons.Actions.Cancel);
/* 21 */     this.myPane = pane;
/*    */   }
/*    */ 
/*    */   
/*    */   public void actionPerformed(@NotNull AnActionEvent e) {
/* 26 */     if (e == null) $$$reportNull$$$0(0);  if (this.myInfo != null) {
/* 27 */       this.myPane.removeTab(this.myInfo);
/*    */     }
/*    */   }
/*    */   
/*    */   public void setInfo(TabInfo info) {
/* 32 */     this.myInfo = info;
/*    */   }
/*    */ }


/* Location:              C:\Program Files\JetBrains\WebStorm 2021.1\plugins\NodeJS\lib\NodeJS.jar!\com\jetbrains\nodejs\run\profile\CloseTabAction.class
 * Java compiler version: 11 (55.0)
 * JD-Core Version:       1.1.3
 */