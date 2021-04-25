/*    */ package org.bipolar.run.profile.heap.calculation.diff;
/*    */ 
/*    */ import com.intellij.openapi.actionSystem.AnActionEvent;
/*    */ import com.intellij.openapi.project.DumbAwareAction;
/*    */ import com.intellij.openapi.project.Project;
/*    */ import com.intellij.util.BeforeAfter;
/*    */ import org.bipolar.NodeJSBundle;
/*    */ import org.bipolar.run.profile.heap.calculation.ByteArrayWrapper;
/*    */ import org.bipolar.run.profile.heap.view.components.V8HeapComponent;
/*    */ import org.bipolar.run.profile.heap.view.components.V8MainTreeNavigator;
/*    */ import org.bipolar.run.profile.heap.view.models.V8HeapContainmentTreeTableModel;
/*    */ import icons.NodeJSIcons;
/*    */
import org.jetbrains.annotations.NotNull;
/*    */ 
/*    */ 
/*    */ 
/*    */ public class NavigateInSourceSnapshotAction
/*    */   extends DumbAwareAction
/*    */ {
/*    */   private final Project myProject;
/*    */   private final String myBaseName;
/*    */   private final String myChangedName;
/*    */   private final ByteArrayWrapper myBaseDigest;
/*    */   private final ByteArrayWrapper myChangedDigest;
/*    */   
/*    */   public NavigateInSourceSnapshotAction(Project project, String baseName, String changedName, ByteArrayWrapper baseDigest, ByteArrayWrapper changedDigest) {
/* 27 */     super(NodeJSBundle.messagePointer("action.NavigateInSourceSnapshotAction.navigate.in.source.snapshot.text", new Object[0]),
/* 28 */         NodeJSBundle.messagePointer("action.NavigateInSourceSnapshotAction.navigate.in.source.snapshot.text", new Object[0]), NodeJSIcons.Navigate_inMainTree);
/* 29 */     this.myProject = project;
/* 30 */     this.myBaseName = baseName;
/* 31 */     this.myChangedName = changedName;
/* 32 */     this.myBaseDigest = baseDigest;
/* 33 */     this.myChangedDigest = changedDigest;
/*    */   }
/*    */ 
/*    */   
/*    */   public void update(@NotNull AnActionEvent e) {
/* 38 */     if (e == null) $$$reportNull$$$0(0);  BeforeAfter<V8HeapContainmentTreeTableModel.NamedEntry> beforeAfter = (BeforeAfter<V8HeapContainmentTreeTableModel.NamedEntry>)e.getData(V8HeapSummaryDiffComponent.SELECTED_PAIR);
/* 39 */     e.getPresentation().setEnabled((beforeAfter != null));
/*    */   }
/*    */ 
/*    */   
/*    */   public void actionPerformed(@NotNull AnActionEvent e) {
/* 44 */     if (e == null) $$$reportNull$$$0(1);  BeforeAfter<V8HeapContainmentTreeTableModel.NamedEntry> beforeAfter = (BeforeAfter<V8HeapContainmentTreeTableModel.NamedEntry>)e.getData(V8HeapSummaryDiffComponent.SELECTED_PAIR);
/* 45 */     V8HeapComponent instance = V8HeapComponent.getInstance(this.myProject);
/* 46 */     if (beforeAfter != null && beforeAfter.getAfter() instanceof V8HeapContainmentTreeTableModel.NamedEntry && beforeAfter
/* 47 */       .getBefore() instanceof V8HeapContainmentTreeTableModel.NamedEntry) {
/* 48 */       V8HeapSummaryDiffComponent.selectSourceProfile(this.myBaseName, this.myChangedName, e, beforeSelected -> {
/*    */ 
/*    */             
/*    */             if (Boolean.TRUE.equals(beforeSelected)) {
/*    */               navigateToBefore(beforeAfter, instance);
/*    */             } else {
/*    */               navigateToAfter(beforeAfter, instance);
/*    */             } 
/*    */           });
/* 57 */     } else if (beforeAfter != null && beforeAfter.getAfter() instanceof V8HeapContainmentTreeTableModel.NamedEntry) {
/* 58 */       navigateToAfter(beforeAfter, instance);
/*    */     }
/* 60 */     else if (beforeAfter != null && beforeAfter.getBefore() instanceof V8HeapContainmentTreeTableModel.NamedEntry) {
/* 61 */       navigateToBefore(beforeAfter, instance);
/*    */     } 
/*    */   }
/*    */   
/*    */   private void navigateToBefore(BeforeAfter<V8HeapContainmentTreeTableModel.NamedEntry> beforeAfter, V8HeapComponent instance) {
/* 66 */     V8MainTreeNavigator navigator = instance.getNavigator(this.myBaseDigest);
/* 67 */     if (navigator != null && 
/* 68 */       navigator.navigateTo(((V8HeapContainmentTreeTableModel.NamedEntry)beforeAfter.getBefore()).getEntry(), null)) {
/* 69 */       instance.activateIfOpen(this.myBaseDigest);
/*    */     }
/*    */   }
/*    */ 
/*    */   
/*    */   private void navigateToAfter(BeforeAfter<V8HeapContainmentTreeTableModel.NamedEntry> beforeAfter, V8HeapComponent instance) {
/* 75 */     V8MainTreeNavigator navigator = instance.getNavigator(this.myChangedDigest);
/* 76 */     if (navigator != null) {
/* 77 */       navigator.navigateTo(((V8HeapContainmentTreeTableModel.NamedEntry)beforeAfter.getAfter()).getEntry(), null);
/* 78 */       instance.activateIfOpen(this.myChangedDigest);
/*    */     } 
/*    */   }
/*    */ }


/* Location:              C:\Program Files\JetBrains\WebStorm 2021.1\plugins\NodeJS\lib\NodeJS.jar!\com\jetbrains\nodejs\run\profile\heap\calculation\diff\NavigateInSourceSnapshotAction.class
 * Java compiler version: 11 (55.0)
 * JD-Core Version:       1.1.3
 */