/*    */ package org.bipolar.run.profile.cpu.view;
/*    */ 
/*    */ import com.intellij.icons.AllIcons;
/*    */ import com.intellij.openapi.actionSystem.AnActionEvent;
/*    */ import com.intellij.openapi.ide.CopyPasteManager;
/*    */ import com.intellij.openapi.project.DumbAwareAction;
/*    */ import org.bipolar.NodeJSBundle;
/*    */ import java.awt.datatransfer.StringSelection;
/*    */
import org.jetbrains.annotations.NotNull;
/*    */ 
/*    */ 
/*    */ public class CopyNameAction
/*    */   extends DumbAwareAction
/*    */ {
/*    */   @NotNull
/*    */   private final V8ProfilingCallTreeTable myTable;
/*    */   
/*    */   public CopyNameAction(@NotNull V8ProfilingCallTreeTable table) {
/* 19 */     super(NodeJSBundle.messagePointer("action.CopyNameAction.copy.call.text", new Object[0]),
/* 20 */         NodeJSBundle.messagePointer("action.CopyNameAction.copy.call.text", new Object[0]), AllIcons.Actions.Copy);
/* 21 */     this.myTable = table;
/*    */   }
/*    */ 
/*    */   
/*    */   public void update(@NotNull AnActionEvent e) {
/* 26 */     if (e == null) $$$reportNull$$$0(1);  super.update(e);
/* 27 */     e.getPresentation().setEnabledAndVisible((this.myTable.getSelectedRow() >= 0 && this.myTable.getV8ProfileLine(this.myTable.getSelectedRow()) != null));
/*    */   }
/*    */ 
/*    */   
/*    */   public void actionPerformed(@NotNull AnActionEvent e) {
/* 32 */     if (e == null) $$$reportNull$$$0(2);  int row = this.myTable.getSelectedRow();
/* 33 */     if (row < 0)
/* 34 */       return;  Object at = this.myTable.getValueAt(row, 0);
/* 35 */     CopyPasteManager.getInstance().setContents(new StringSelection((at == null) ? null : at.toString()));
/*    */   }
/*    */ }


/* Location:              C:\Program Files\JetBrains\WebStorm 2021.1\plugins\NodeJS\lib\NodeJS.jar!\com\jetbrains\nodejs\run\profile\cpu\view\CopyNameAction.class
 * Java compiler version: 11 (55.0)
 * JD-Core Version:       1.1.3
 */