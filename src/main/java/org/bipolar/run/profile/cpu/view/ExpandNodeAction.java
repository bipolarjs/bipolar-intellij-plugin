/*    */ package org.bipolar.run.profile.cpu.view;
/*    */ 
/*    */ import com.intellij.icons.AllIcons;
/*    */ import com.intellij.openapi.actionSystem.AnActionEvent;
/*    */ import com.intellij.openapi.project.DumbAwareAction;
/*    */ import com.intellij.ui.treeStructure.treetable.TreeTableModel;
/*    */ import com.intellij.util.ArrayUtil;
/*    */ import org.bipolar.NodeJSBundle;
/*    */ import org.bipolar.run.profile.TreeTableWidthController;
/*    */ import java.util.ArrayList;
/*    */ import java.util.Arrays;
/*    */ import java.util.List;
/*    */ import javax.swing.tree.TreePath;
/*    */
import org.jetbrains.annotations.NotNull;
/*    */ 
/*    */ 
/*    */ 
/*    */ public class ExpandNodeAction
/*    */   extends DumbAwareAction
/*    */ {
/*    */   private final V8ProfilingCallTreeTable myTable;
/*    */   
/*    */   public ExpandNodeAction(V8ProfilingCallTreeTable table) {
/* 24 */     super(NodeJSBundle.messagePointer("action.ExpandNodeAction.expand.node.text", new Object[0]),
/* 25 */         NodeJSBundle.messagePointer("action.ExpandNodeAction.expand.node.recursively.description", new Object[0]), AllIcons.Actions.Expandall);
/* 26 */     this.myTable = table;
/*    */   }
/*    */ 
/*    */   
/*    */   public void update(@NotNull AnActionEvent e) {
/* 31 */     if (e == null) $$$reportNull$$$0(0);  super.update(e);
/* 32 */     e.getPresentation().setEnabled((this.myTable.getSelectedRow() >= 0));
/*    */   }
/*    */ 
/*    */   
/*    */   public void actionPerformed(@NotNull AnActionEvent e) {
/* 37 */     if (e == null) $$$reportNull$$$0(1);  int row = this.myTable.getSelectedRow();
/* 38 */     if (row >= 0) {
/* 39 */       TreeTableWidthController widthController = this.myTable.getWidthController();
/* 40 */       if (widthController != null) widthController.startBatchExpand(); 
/* 41 */       expandRowRecursively(row);
/* 42 */       if (widthController != null) widthController.stopBatchExpand(); 
/*    */     } 
/*    */   }
/*    */   
/*    */   private void expandRowRecursively(int row) {
/* 47 */     this.myTable.getTree().expandRow(row);
/* 48 */     TreePath parentPath = this.myTable.getTree().getPathForRow(row);
/* 49 */     Object component = parentPath.getLastPathComponent();
/* 50 */     TreeTableModel model = this.myTable.getTableModel();
/* 51 */     int count = model.getChildCount(component);
/* 52 */     Object[] pathComponents = parentPath.getPath();
/* 53 */     for (int i = 0; i < count; i++) {
/* 54 */       Object child = model.getChild(component, i);
/* 55 */       if (child != null) {
/* 56 */         List<Object> list = new ArrayList(Arrays.asList(pathComponents));
/* 57 */         list.add(child);
/* 58 */         int childRow = this.myTable.getTree().getRowForPath(new TreePath(ArrayUtil.toObjectArray(list)));
/* 59 */         if (childRow >= 0)
/* 60 */           expandRowRecursively(childRow); 
/*    */       } 
/*    */     } 
/*    */   }
/*    */ }


/* Location:              C:\Program Files\JetBrains\WebStorm 2021.1\plugins\NodeJS\lib\NodeJS.jar!\com\jetbrains\nodejs\run\profile\cpu\view\ExpandNodeAction.class
 * Java compiler version: 11 (55.0)
 * JD-Core Version:       1.1.3
 */