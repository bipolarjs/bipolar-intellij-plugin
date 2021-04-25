/*    */ package org.bipolar.run.profile.cpu.v8log.diff;
/*    */ 
/*    */ import com.intellij.openapi.Disposable;
/*    */ import com.intellij.openapi.actionSystem.ActionGroup;
/*    */ import com.intellij.openapi.actionSystem.ActionManager;
/*    */ import com.intellij.openapi.actionSystem.AnAction;
/*    */ import com.intellij.openapi.actionSystem.DefaultActionGroup;
/*    */ import com.intellij.openapi.project.Project;
/*    */ import com.intellij.openapi.util.Pair;
/*    */ import com.intellij.ui.PopupHandler;
/*    */ import com.intellij.ui.treeStructure.treetable.TreeTable;
/*    */ import com.intellij.util.Consumer;
/*    */ import org.bipolar.run.profile.V8Utils;
/*    */ import org.bipolar.run.profile.cpu.calculation.CallTreeType;
/*    */ import org.bipolar.run.profile.cpu.v8log.reading.V8LogCachingReader;
/*    */ import org.bipolar.run.profile.cpu.view.CollapseNodeAction;
/*    */ import org.bipolar.run.profile.cpu.view.CompareWithClipboard;
/*    */ import org.bipolar.run.profile.cpu.view.CopyNameAction;
/*    */ import org.bipolar.run.profile.cpu.view.ExpandNodeAction;
/*    */ import org.bipolar.run.profile.cpu.view.ProfilingView;
/*    */ import org.bipolar.run.profile.cpu.view.V8ProfilingCallTreeTable;
/*    */ import org.bipolar.run.profile.cpu.view.V8ProfilingMainComponent;
/*    */ import org.bipolar.run.profile.cpu.view.ViewCreatorPartner;
/*    */ import org.bipolar.run.profile.heap.CompositeCloseable;
/*    */ import java.io.IOException;
/*    */ import java.util.List;
/*    */ import javax.swing.JComponent;
/*    */
import org.jetbrains.annotations.NotNull;
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public class V8CpuDiffViewCreatorPartner
/*    */   implements ViewCreatorPartner<V8ProfilingCallTreeTable>
/*    */ {
/*    */   private final Project myProject;
/*    */   private final CompositeCloseable myResources;
/*    */   private final V8LogCachingReader myBaseReader;
/*    */   private final V8LogCachingReader myChangedReader;
/*    */   
/*    */   public V8CpuDiffViewCreatorPartner(Project project, CompositeCloseable resources, V8LogCachingReader baseReader, V8LogCachingReader changedReader, @NotNull Consumer<String> notificator, @NotNull DiffNode topDown, @NotNull DiffNode bottomUp, @NotNull List<Pair<String, List<DiffNode>>> flatDiff) {
/* 44 */     this.myProject = project;
/* 45 */     this.myResources = resources;
/* 46 */     this.myBaseReader = baseReader;
/* 47 */     this.myChangedReader = changedReader;
/* 48 */     this.myNotificator = notificator;
/* 49 */     this.myTopDown = topDown;
/* 50 */     this.myBottomUp = bottomUp;
/* 51 */     this.myFlatDiff = flatDiff; } @NotNull
/*    */   private final Consumer<String> myNotificator; @NotNull
/*    */   private final DiffNode myTopDown; @NotNull
/*    */   private final DiffNode myBottomUp; @NotNull
/*    */   private final List<Pair<String, List<DiffNode>>> myFlatDiff; private V8ProfilingMainComponent.MyController<V8ProfilingCallTreeTable> myViewController; public JComponent wrapWithStandardActions(ProfilingView<V8ProfilingCallTreeTable> view, AnAction closeAction) {
/* 56 */     DefaultActionGroup group = new DefaultActionGroup();
/* 57 */     view.addActions(group);
/* 58 */     DefaultActionGroup popupGroup = new DefaultActionGroup(group.getChildActionsOrStubs());
/*    */     
/* 60 */     V8ProfilingCallTreeTable table = (V8ProfilingCallTreeTable)view.getTreeTable();
/* 61 */     group.add((AnAction)new V8Utils.CollapseAllAction((TreeTable)table));
/* 62 */     group.add(closeAction);
/*    */     
/* 64 */     popupGroup.add((AnAction)new CopyNameAction(table));
/* 65 */     popupGroup.add(ActionManager.getInstance().getAction("$Copy"));
/* 66 */     popupGroup.add((AnAction)new CompareWithClipboard(table));
/* 67 */     popupGroup.add((AnAction)new ExpandNodeAction(table));
/* 68 */     popupGroup.add((AnAction)new CollapseNodeAction(table));
/* 69 */     PopupHandler.installPopupHandler((JComponent)table, (ActionGroup)popupGroup, "V8_CPU_PROFILING_POPUP", ActionManager.getInstance());
/*    */     
/* 71 */     return V8Utils.wrapWithActions(view.getMainComponent(), group);
/*    */   }
/*    */ 
/*    */   
/*    */   public void addViews(Project project, List<ProfilingView<V8ProfilingCallTreeTable>> list, Disposable disposable) {
/* 76 */     list.add(new V8CpuDiffFlatViewComponent(this.myProject, disposable, this.myBaseReader, this.myChangedReader, this.myFlatDiff));
/* 77 */     list.add(new V8CpuDiffComponent(this.myProject, this.myBottomUp, CallTreeType.bottomUp, disposable, this.myBaseReader, this.myChangedReader));
/* 78 */     list.add(new V8CpuDiffComponent(this.myProject, this.myTopDown, CallTreeType.topDown, disposable, this.myBaseReader, this.myChangedReader));
/*    */   }
/*    */ 
/*    */   
/*    */   public String errorCreated() {
/* 83 */     return null;
/*    */   }
/*    */ 
/*    */   
/*    */   public void announceController(V8ProfilingMainComponent.MyController<V8ProfilingCallTreeTable> controller) {
/* 88 */     this.myViewController = controller;
/*    */   }
/*    */ 
/*    */   
/*    */   public void close() {
/*    */     try {
/* 94 */       this.myResources.close();
/* 95 */       this.myBaseReader.getResources().close();
/* 96 */       this.myChangedReader.getResources().close();
/*    */     }
/* 98 */     catch (IOException iOException) {}
/*    */   }
/*    */ }


/* Location:              C:\Program Files\JetBrains\WebStorm 2021.1\plugins\NodeJS\lib\NodeJS.jar!\com\jetbrains\nodejs\run\profile\cpu\v8log\diff\V8CpuDiffViewCreatorPartner.class
 * Java compiler version: 11 (55.0)
 * JD-Core Version:       1.1.3
 */