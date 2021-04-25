/*    */ package org.bipolar.run.profile.heap.calculation.diff;
/*    */ 
/*    */ import com.intellij.icons.AllIcons;
/*    */ import com.intellij.openapi.actionSystem.AnActionEvent;
/*    */ import com.intellij.openapi.project.DumbAwareAction;
/*    */ import com.intellij.openapi.project.Project;
/*    */ import com.intellij.util.BeforeAfter;
/*    */ import org.bipolar.run.profile.heap.calculation.ByteArrayWrapper;
/*    */ import org.bipolar.run.profile.heap.view.actions.MarkUnmarkAction;
/*    */ import org.bipolar.run.profile.heap.view.components.V8HeapComponent;
/*    */ import org.bipolar.run.profile.heap.view.models.V8HeapContainmentTreeTableModel;
/*    */ import org.jetbrains.annotations.NotNull;
/*    */ 
/*    */ 
/*    */ 
/*    */ public class MarkUnmarkDiffAction
/*    */   extends DumbAwareAction
/*    */ {
/*    */   private final V8HeapComponent myComponent;
/*    */   private final Project myProject;
/*    */   private final String myBaseName;
/*    */   private final String myChangedName;
/*    */   private final ByteArrayWrapper myBaseDigest;
/*    */   private final ByteArrayWrapper myChangedDigest;
/*    */   
/*    */   public MarkUnmarkDiffAction(Project project, String baseName, String changedName, ByteArrayWrapper baseDigest, ByteArrayWrapper changedDigest) {
/* 27 */     super(MarkUnmarkAction.MARK, MarkUnmarkAction.MARK_OBJECT_WITH_TEXT, AllIcons.Actions.SetDefault);
/* 28 */     this.myProject = project;
/* 29 */     this.myBaseName = baseName;
/* 30 */     this.myChangedName = changedName;
/* 31 */     this.myBaseDigest = baseDigest;
/* 32 */     this.myChangedDigest = changedDigest;
/* 33 */     this.myComponent = V8HeapComponent.getInstance(this.myProject);
/*    */   }
/*    */ 
/*    */   
/*    */   public void actionPerformed(@NotNull AnActionEvent e) {
/* 38 */     if (e == null) $$$reportNull$$$0(0);  BeforeAfter<V8HeapContainmentTreeTableModel.NamedEntry> beforeAfter = (BeforeAfter<V8HeapContainmentTreeTableModel.NamedEntry>)e.getData(V8HeapSummaryDiffComponent.SELECTED_PAIR);
/* 39 */     if (beforeAfter != null && beforeAfter.getAfter() instanceof V8HeapContainmentTreeTableModel.NamedEntry && beforeAfter
/* 40 */       .getBefore() instanceof V8HeapContainmentTreeTableModel.NamedEntry) {
/* 41 */       V8HeapSummaryDiffComponent.selectSourceProfile(this.myBaseName, this.myChangedName, e, beforeSelected -> {
/*    */ 
/*    */             
/*    */             if (Boolean.TRUE.equals(beforeSelected)) {
/*    */               MarkUnmarkAction.markOrUnmark(((V8HeapContainmentTreeTableModel.NamedEntry)beforeAfter.getBefore()).getEntry(), this.myProject, this.myComponent, this.myBaseDigest);
/*    */             } else {
/*    */               MarkUnmarkAction.markOrUnmark(((V8HeapContainmentTreeTableModel.NamedEntry)beforeAfter.getAfter()).getEntry(), this.myProject, this.myComponent, this.myChangedDigest);
/*    */             } 
/*    */           });
/* 50 */     } else if (beforeAfter != null && beforeAfter.getAfter() instanceof V8HeapContainmentTreeTableModel.NamedEntry) {
/* 51 */       MarkUnmarkAction.markOrUnmark(((V8HeapContainmentTreeTableModel.NamedEntry)beforeAfter.getAfter()).getEntry(), this.myProject, this.myComponent, this.myChangedDigest);
/*    */     }
/* 53 */     else if (beforeAfter != null && beforeAfter.getBefore() instanceof V8HeapContainmentTreeTableModel.NamedEntry) {
/* 54 */       MarkUnmarkAction.markOrUnmark(((V8HeapContainmentTreeTableModel.NamedEntry)beforeAfter.getBefore()).getEntry(), this.myProject, this.myComponent, this.myBaseDigest);
/*    */     } 
/*    */   }
/*    */ 
/*    */   
/*    */   public void update(@NotNull AnActionEvent e) {
/* 60 */     if (e == null) $$$reportNull$$$0(1);  BeforeAfter<V8HeapContainmentTreeTableModel.NamedEntry> beforeAfter = (BeforeAfter<V8HeapContainmentTreeTableModel.NamedEntry>)e.getData(V8HeapSummaryDiffComponent.SELECTED_PAIR);
/* 61 */     e.getPresentation().setEnabled((beforeAfter != null));
/*    */   }
/*    */ }


/* Location:              C:\Program Files\JetBrains\WebStorm 2021.1\plugins\NodeJS\lib\NodeJS.jar!\com\jetbrains\nodejs\run\profile\heap\calculation\diff\MarkUnmarkDiffAction.class
 * Java compiler version: 11 (55.0)
 * JD-Core Version:       1.1.3
 */