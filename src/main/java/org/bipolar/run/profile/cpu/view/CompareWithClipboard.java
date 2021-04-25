/*    */ package org.bipolar.run.profile.cpu.view;
/*    */ 
/*    */ import com.intellij.diff.DiffManager;
/*    */ import com.intellij.diff.DiffRequestFactory;
/*    */ import com.intellij.diff.requests.ContentDiffRequest;
/*    */ import com.intellij.diff.requests.DiffRequest;
/*    */ import com.intellij.icons.AllIcons;
/*    */ import com.intellij.openapi.actionSystem.AnAction;
/*    */ import com.intellij.openapi.actionSystem.AnActionEvent;
/*    */ import com.intellij.openapi.actionSystem.PlatformDataKeys;
/*    */ import com.intellij.openapi.ide.CopyPasteManager;
/*    */ import com.intellij.openapi.project.Project;
/*    */ import org.bipolar.NodeJSBundle;
/*    */ import java.awt.datatransfer.Transferable;
/*    */
import org.jetbrains.annotations.NotNull;
/*    */ 
/*    */ 
/*    */ public class CompareWithClipboard
/*    */   extends AnAction
/*    */ {
/*    */   private final V8ProfilingCallTreeTable myTable;
/*    */   
/*    */   public CompareWithClipboard(V8ProfilingCallTreeTable table) {
/* 24 */     super(NodeJSBundle.messagePointer("action.CompareWithClipboard.compare.with.clipboard.text", new Object[0]),
/* 25 */         NodeJSBundle.messagePointer("action.CompareWithClipboard.compare.with.clipboard.text", new Object[0]), AllIcons.Actions.DiffWithClipboard);
/* 26 */     this.myTable = table;
/*    */   }
/*    */ 
/*    */   
/*    */   public void update(@NotNull AnActionEvent e) {
/* 31 */     if (e == null) $$$reportNull$$$0(0);  Project project = (Project)e.getData(PlatformDataKeys.PROJECT);
/* 32 */     int row = this.myTable.getSelectedRow();
/* 33 */     Transferable contents = CopyPasteManager.getInstance().getContents();
/* 34 */     e.getPresentation().setEnabled((project != null && row >= 0 && this.myTable.getValueAt(row, 0) != null && contents != null));
/*    */   }
/*    */ 
/*    */   
/*    */   public void actionPerformed(@NotNull AnActionEvent e) {
/* 39 */     if (e == null) $$$reportNull$$$0(1);  Project project = (Project)e.getData(PlatformDataKeys.PROJECT);
/* 40 */     if (project == null)
/* 41 */       return;  int row = this.myTable.getSelectedRow();
/* 42 */     if (row < 0)
/* 43 */       return;  String value = this.myTable.getValueAt(row, 0).toString();
/*    */     
/* 45 */     ContentDiffRequest contentDiffRequest = DiffRequestFactory.getInstance().createClipboardVsValue(value);
/* 46 */     DiffManager.getInstance().showDiff(project, (DiffRequest)contentDiffRequest);
/*    */   }
/*    */ }


/* Location:              C:\Program Files\JetBrains\WebStorm 2021.1\plugins\NodeJS\lib\NodeJS.jar!\com\jetbrains\nodejs\run\profile\cpu\view\CompareWithClipboard.class
 * Java compiler version: 11 (55.0)
 * JD-Core Version:       1.1.3
 */