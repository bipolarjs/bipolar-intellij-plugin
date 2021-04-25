/*    */ package org.bipolar.run.profile.cpu.view;
/*    */ 
/*    */ import com.intellij.icons.AllIcons;
/*    */ import com.intellij.openapi.actionSystem.AnActionEvent;
/*    */ import com.intellij.openapi.project.DumbAwareAction;
/*    */ import com.intellij.ui.treeStructure.treetable.TreeTable;
/*    */ import org.bipolar.NodeJSBundle;
/*    */ import org.bipolar.run.profile.V8Utils;
/*    */
import org.jetbrains.annotations.NotNull;
/*    */ 
/*    */ 
/*    */ 
/*    */ public class ExpandByDefaultAction
/*    */   extends DumbAwareAction
/*    */ {
/*    */   private final V8ProfilingCallTreeTable myTable;
/*    */   private final V8ProfilingMainComponent.MyController<V8ProfilingCallTreeTable> myViewController;
/*    */   
/*    */   public ExpandByDefaultAction(V8ProfilingCallTreeTable table, V8ProfilingMainComponent.MyController<V8ProfilingCallTreeTable> viewController) {
/* 20 */     super(NodeJSBundle.messagePointer("action.ExpandByDefaultAction.expand.heavy.traces.text", new Object[0]),
/* 21 */         NodeJSBundle.messagePointer("action.ExpandByDefaultAction.expand.heavy.traces.description", new Object[0]), AllIcons.Actions.Expandall);
/* 22 */     this.myTable = table;
/* 23 */     this.myViewController = viewController;
/*    */   }
/*    */ 
/*    */   
/*    */   public void update(@NotNull AnActionEvent e) {
/* 28 */     if (e == null) $$$reportNull$$$0(0);  super.update(e);
/* 29 */     e.getPresentation().setEnabledAndVisible(!"V8_CPU_PROFILING_POPUP".equals(e.getPlace()));
/*    */   }
/*    */ 
/*    */   
/*    */   public void actionPerformed(@NotNull AnActionEvent e) {
/* 34 */     if (e == null) $$$reportNull$$$0(1);  V8Utils.collapseAll((TreeTable)this.myTable);
/* 35 */     this.myViewController.autoExpand();
/*    */   }
/*    */ }


/* Location:              C:\Program Files\JetBrains\WebStorm 2021.1\plugins\NodeJS\lib\NodeJS.jar!\com\jetbrains\nodejs\run\profile\cpu\view\ExpandByDefaultAction.class
 * Java compiler version: 11 (55.0)
 * JD-Core Version:       1.1.3
 */