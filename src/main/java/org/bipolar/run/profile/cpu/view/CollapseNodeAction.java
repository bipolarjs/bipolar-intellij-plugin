/*    */ package org.bipolar.run.profile.cpu.view;
/*    */ 
/*    */ import com.intellij.icons.AllIcons;
/*    */ import com.intellij.openapi.actionSystem.AnActionEvent;
/*    */ import com.intellij.openapi.project.DumbAwareAction;
/*    */ import org.bipolar.NodeJSBundle;
/*    */
import org.jetbrains.annotations.NotNull;
/*    */ 
/*    */ 
/*    */ public class CollapseNodeAction
/*    */   extends DumbAwareAction
/*    */ {
/*    */   private final V8ProfilingCallTreeTable myTable;
/*    */   
/*    */   public CollapseNodeAction(V8ProfilingCallTreeTable table) {
/* 16 */     super(NodeJSBundle.message("action.CollapseNodeAction.collapse.node.text", new Object[0]),
/* 17 */         NodeJSBundle.message("action.CollapseNodeAction.collapse.node.description", new Object[0]), AllIcons.Actions.Collapseall);
/* 18 */     this.myTable = table;
/*    */   }
/*    */ 
/*    */   
/*    */   public void update(@NotNull AnActionEvent e) {
/* 23 */     if (e == null) $$$reportNull$$$0(0);  super.update(e);
/* 24 */     e.getPresentation().setEnabled((this.myTable.getSelectedRow() >= 0));
/*    */   }
/*    */ 
/*    */   
/*    */   public void actionPerformed(@NotNull AnActionEvent e) {
/* 29 */     if (e == null) $$$reportNull$$$0(1);  this.myTable.collapseRowRecursively();
/*    */   }
/*    */ }


/* Location:              C:\Program Files\JetBrains\WebStorm 2021.1\plugins\NodeJS\lib\NodeJS.jar!\com\jetbrains\nodejs\run\profile\cpu\view\CollapseNodeAction.class
 * Java compiler version: 11 (55.0)
 * JD-Core Version:       1.1.3
 */