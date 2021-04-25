/*     */ package org.bipolar.run.profile.cpu.v8log.reading;
/*     */ 
/*     */ import com.intellij.javascript.nodejs.NodeUIUtil;
/*     */ import com.intellij.openapi.actionSystem.AnActionEvent;
/*     */ import com.intellij.openapi.actionSystem.PlatformDataKeys;
/*     */ import com.intellij.openapi.project.DumbAwareAction;
/*     */ import com.intellij.openapi.project.Project;
/*     */ import org.bipolar.NodeJSBundle;
/*     */ import org.bipolar.run.profile.V8Utils;
/*     */ import org.bipolar.run.profile.cpu.v8log.data.V8CpuLogCall;
/*     */ import org.bipolar.run.profile.cpu.v8log.ui.FlameChartView;
/*     */ import org.bipolar.run.profile.cpu.view.SearchInV8TreeAction;
/*     */ import org.bipolar.run.profile.cpu.view.V8ProfilingCallTreeTable;
/*     */ import org.bipolar.run.profile.cpu.view.V8ProfilingMainComponent;
/*     */ import icons.NodeJSIcons;
/*     */
import org.jetbrains.annotations.Nls;
/*     */ import org.jetbrains.annotations.NotNull;
/*     */ 
/*     */ 
/*     */ 
/*     */ public class V8SwitchViewActionsFactory
/*     */ {
/*     */   private final V8ProfilingMainComponent.MyController<V8ProfilingCallTreeTable> myController;
/*     */   private CpuProfilingView myTopCalls;
/*     */   private CpuProfilingView myBottomUp;
/*     */   private CpuProfilingView myTopDown;
/*     */   private ToViewAction myToTopCalls;
/*     */   private ToViewAction myToBottomUp;
/*     */   private ToViewAction myToTopDown;
/*     */   private FlameChartView myFlameChartView;
/*     */   
/*     */   public V8SwitchViewActionsFactory(V8ProfilingMainComponent.MyController<V8ProfilingCallTreeTable> controller) {
/*  33 */     this.myController = controller;
/*     */   }
/*     */   
/*     */   public void createActions() {
/*  37 */     this.myToTopCalls = new ToViewAction(NodeJSBundle.message("profile.cpu.navigate_in_top_calls.text", new Object[0]), this.myTopCalls, this.myController);
/*  38 */     this.myToBottomUp = new ToViewAction(NodeJSBundle.message("profile.cpu.navigate_in_bottom_up.text", new Object[0]), this.myBottomUp, this.myController);
/*  39 */     this.myToTopDown = new ToViewAction(NodeJSBundle.message("profile.cpu.navigate_in_top_down.text", new Object[0]), this.myTopDown, this.myController);
/*     */   }
/*     */   
/*     */   public void setTopCalls(CpuProfilingView topCalls) {
/*  43 */     this.myTopCalls = topCalls;
/*     */   }
/*     */   
/*     */   public void setBottomUp(CpuProfilingView bottomUp) {
/*  47 */     this.myBottomUp = bottomUp;
/*     */   }
/*     */   
/*     */   public void setTopDown(CpuProfilingView topDown) {
/*  51 */     this.myTopDown = topDown;
/*     */   }
/*     */   
/*     */   public ToViewAction getToTopCalls() {
/*  55 */     return this.myToTopCalls;
/*     */   }
/*     */   
/*     */   public ToViewAction getToBottomUp() {
/*  59 */     return this.myToBottomUp;
/*     */   }
/*     */   
/*     */   public ToViewAction getToTopDown() {
/*  63 */     return this.myToTopDown;
/*     */   }
/*     */   
/*     */   public FlameChartView getFlameChartView() {
/*  67 */     return this.myFlameChartView;
/*     */   }
/*     */   
/*     */   public void setFlameChartView(FlameChartView flameChartView) {
/*  71 */     this.myFlameChartView = flameChartView;
/*     */   }
/*     */   
/*     */   private static class ToViewAction
/*     */     extends DumbAwareAction {
/*     */     @NotNull
/*     */     private final CpuProfilingView myView;
/*     */     private final V8ProfilingMainComponent.MyController<V8ProfilingCallTreeTable> myController;
/*     */     
/*     */     ToViewAction(@NotNull @Nls String name, @NotNull CpuProfilingView view, V8ProfilingMainComponent.MyController<V8ProfilingCallTreeTable> controller) {
/*  81 */       super(name, name, NodeJSIcons.Navigate_inMainTree);
/*  82 */       this.myView = view;
/*  83 */       this.myController = controller;
/*     */     }
/*     */ 
/*     */     
/*     */     public void update(@NotNull AnActionEvent e) {
/*  88 */       if (e == null) $$$reportNull$$$0(2);  e.getPresentation().setEnabled((e.getData(V8Utils.SELECTED_CALL) != null && e
/*  89 */           .getData(PlatformDataKeys.PROJECT) != null));
/*     */     }
/*     */ 
/*     */     
/*     */     public void actionPerformed(@NotNull AnActionEvent e) {
/*  94 */       if (e == null) $$$reportNull$$$0(3);  V8CpuLogCall call = (V8CpuLogCall)e.getData(V8Utils.SELECTED_CALL);
/*  95 */       Project project = (Project)e.getData(PlatformDataKeys.PROJECT);
/*  96 */       if (call == null || project == null)
/*     */         return; 
/*  98 */       SearchInV8TreeAction.Searcher searcher = (SearchInV8TreeAction.Searcher)this.myView.getSearcherFactory().create();
/*  99 */       if (!searcher.search(call.getPresentation(false), false)) {
/* 100 */         NodeUIUtil.balloonInfo(project, NodeJSBundle.message("node.js.v8.cpu.navigation.not.found.error", new Object[0]), null, null);
/*     */         return;
/*     */       } 
/* 103 */       this.myController.showTab(this.myView.getName());
/* 104 */       SearchInV8TreeAction.showCallsInTree(searcher, call, this.myView.getMasterDetails());
/*     */     }
/*     */   }
/*     */ }


/* Location:              C:\Program Files\JetBrains\WebStorm 2021.1\plugins\NodeJS\lib\NodeJS.jar!\com\jetbrains\nodejs\run\profile\cpu\v8log\reading\V8SwitchViewActionsFactory.class
 * Java compiler version: 11 (55.0)
 * JD-Core Version:       1.1.3
 */