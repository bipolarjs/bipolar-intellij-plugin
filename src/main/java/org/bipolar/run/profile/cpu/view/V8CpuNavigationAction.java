/*    */ package org.bipolar.run.profile.cpu.view;
/*    */ 
/*    */ import com.intellij.openapi.actionSystem.ActionGroup;
/*    */ import com.intellij.openapi.actionSystem.AnAction;
/*    */ import com.intellij.openapi.actionSystem.AnActionEvent;
/*    */ import com.intellij.openapi.actionSystem.DefaultActionGroup;
/*    */ import com.intellij.openapi.project.DumbAwareAction;
/*    */ import com.intellij.openapi.ui.popup.JBPopup;
/*    */ import com.intellij.openapi.ui.popup.JBPopupFactory;
/*    */ import com.intellij.openapi.ui.popup.ListPopup;
/*    */ import org.bipolar.NodeJSBundle;
/*    */ import org.bipolar.run.profile.V8Utils;
/*    */ import icons.NodeJSIcons;
/*    */
import org.jetbrains.annotations.NotNull;
/*    */ 
/*    */ public class V8CpuNavigationAction
/*    */   extends DumbAwareAction {
/*    */   private final DefaultActionGroup myGroup;
/*    */   
/*    */   public V8CpuNavigationAction() {
/* 21 */     super(NodeJSBundle.messagePointer("action.V8CpuNavigationAction.navigate.to.text", new Object[0]),
/* 22 */         NodeJSBundle.messagePointer("action.V8CpuNavigationAction.navigates.to.call.in.other.views.description", new Object[0]), NodeJSIcons.Navigate_inMainTree);
/*    */     
/* 24 */     this.myGroup = new DefaultActionGroup();
/*    */   }
/*    */   
/*    */   public V8CpuNavigationAction addActions(AnAction... actions) {
/* 28 */     for (AnAction anAction : actions) {
/* 29 */       this.myGroup.add(anAction);
/*    */     }
/* 31 */     return this;
/*    */   }
/*    */   
/*    */   public DefaultActionGroup getGroup() {
/* 35 */     return this.myGroup;
/*    */   }
/*    */ 
/*    */   
/*    */   public void actionPerformed(@NotNull AnActionEvent e) {
/* 40 */     if (e == null) $$$reportNull$$$0(0);  ListPopup popup = JBPopupFactory.getInstance().createActionGroupPopup(NodeJSBundle.message("popup.title.navigate.to", new Object[0]), (ActionGroup)this.myGroup, e
/* 41 */         .getDataContext(), true, false, true, null, -1, null);
/* 42 */     V8Utils.showPopup(e, (JBPopup)popup);
/*    */   }
/*    */ }


/* Location:              C:\Program Files\JetBrains\WebStorm 2021.1\plugins\NodeJS\lib\NodeJS.jar!\com\jetbrains\nodejs\run\profile\cpu\view\V8CpuNavigationAction.class
 * Java compiler version: 11 (55.0)
 * JD-Core Version:       1.1.3
 */