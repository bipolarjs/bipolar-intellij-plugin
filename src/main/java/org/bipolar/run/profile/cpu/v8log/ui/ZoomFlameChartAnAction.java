/*     */ package org.bipolar.run.profile.cpu.v8log.ui;
/*     */ 
/*     */ import com.intellij.icons.AllIcons;
/*     */ import com.intellij.openapi.actionSystem.AnActionEvent;
/*     */ import com.intellij.openapi.progress.ProgressIndicator;
/*     */ import com.intellij.openapi.progress.ProgressManager;
/*     */ import com.intellij.openapi.progress.Task;
/*     */ import com.intellij.openapi.project.DumbAwareAction;
/*     */ import com.intellij.openapi.project.Project;
/*     */ import com.intellij.util.BeforeAfter;
/*     */ import com.intellij.util.Consumer;
/*     */ import org.bipolar.NodeJSBundle;
/*     */ import org.bipolar.run.profile.V8Utils;
/*     */ import org.bipolar.run.profile.cpu.v8log.ReadV8LogRawAction;
/*     */ import org.bipolar.run.profile.cpu.v8log.data.V8CpuLogCall;
/*     */ import org.bipolar.run.profile.cpu.v8log.reading.V8CpuViewCallback;
/*     */ import org.bipolar.run.profile.cpu.v8log.reading.V8CpuViewCreatorPartner;
/*     */ import org.bipolar.run.profile.cpu.v8log.reading.V8LogCachingReader;
/*     */ import org.bipolar.run.profile.cpu.v8log.reading.V8StackTableModel;
/*     */ import org.bipolar.run.profile.cpu.view.V8ProfilingMainComponent;
/*     */ import org.bipolar.run.profile.cpu.view.ViewCreatorPartner;
/*     */ import icons.NodeJSIcons;
/*     */ import java.io.IOException;
/*     */
import org.jetbrains.annotations.Nls;
/*     */ import org.jetbrains.annotations.NotNull;
/*     */ import org.jetbrains.annotations.Nullable;
/*     */ 
/*     */ 
/*     */ public class ZoomFlameChartAnAction
/*     */   extends DumbAwareAction
/*     */ {
/*     */   private final Project myProject;
/*     */   private final V8LogCachingReader myReader;
/*     */   @Nls
/*     */   private final String myDescription;
/*     */   private final Consumer<String> myNotificator;
/*     */   private final FlameChartView myFlameChartView;
/*     */   private final V8CpuViewCallback myViewCallback;
/*     */   
/*     */   public ZoomFlameChartAnAction(Project project, V8LogCachingReader reader, @Nls String description, Consumer<String> notificator, FlameChartView flameChartView, V8CpuViewCallback viewCallback) {
/*  41 */     super(NodeJSBundle.message("action.ZoomFlameChartAnAction.text", new Object[0]), NodeJSBundle.message("action.ZoomFlameChartAnAction.description", new Object[0]), AllIcons.Graph.ZoomIn);
/*     */     
/*  43 */     this.myProject = project;
/*  44 */     this.myReader = reader;
/*  45 */     this.myDescription = description;
/*  46 */     this.myNotificator = notificator;
/*  47 */     this.myFlameChartView = flameChartView;
/*  48 */     this.myViewCallback = viewCallback;
/*     */   }
/*     */   
/*     */   private BeforeAfter<Long> getInterval() {
/*  52 */     V8CpuOverviewChart overviewChart = this.myFlameChartView.getOverviewChart();
/*  53 */     if (overviewChart.isSelectionNarrow()) {
/*  54 */       return new BeforeAfter(Long.valueOf(overviewChart.getLeftBound()), Long.valueOf(overviewChart.getRightBound()));
/*     */     }
/*  56 */     return null;
/*     */   }
/*     */ 
/*     */   
/*     */   public void update(@NotNull AnActionEvent e) {
/*  61 */     if (e == null) $$$reportNull$$$0(0);  BeforeAfter<Long> interval = getInterval();
/*  62 */     e.getPresentation().setEnabled((interval != null && ((Long)interval.getBefore()).longValue() >= 0L && ((Long)interval.getAfter()).longValue() >= 0L));
/*     */   }
/*     */ 
/*     */   
/*     */   public void actionPerformed(@NotNull AnActionEvent e) {
/*  67 */     if (e == null) $$$reportNull$$$0(1);  BeforeAfter<Long> interval = getInterval();
/*  68 */     if (interval == null || ((Long)interval.getBefore()).longValue() < 0L || ((Long)interval.getAfter()).longValue() < 0L)
/*  69 */       return;  Long detailsPosition = (Long)e.getData(V8Utils.DETAILS_POSITION);
/*  70 */     V8StackTableModel model = (V8StackTableModel)this.myFlameChartView.getStackTraceTable().getModel();
/*  71 */     int row = this.myFlameChartView.getStackTraceTable().getSelectedRow();
/*  72 */     V8CpuLogCall call = (row == -1) ? null : model.getCall(row);
/*     */ 
/*     */     
/*  75 */     ZoomTask zoomTask = (new ZoomTask(this.myProject, this.myReader, this.myDescription, this.myNotificator, interval)).withDetailsPosition(detailsPosition);
/*  76 */     if (call != null) zoomTask = zoomTask.withStringId(call.getStringId()); 
/*  77 */     ProgressManager.getInstance().run((Task)zoomTask);
/*  78 */     this.myViewCallback.updateActionsAvailability();
/*     */   }
/*     */ 
/*     */   
/*     */   public static class ZoomTask
/*     */     extends Task.Backgroundable
/*     */   {
/*     */     private final Project myProject;
/*     */     
/*     */     private final V8LogCachingReader myReader;
/*     */     @Nls
/*     */     private final String myDescription;
/*     */     private final Consumer<String> myNotificator;
/*     */     private final BeforeAfter<Long> myInterval;
/*     */     private final FlameChartParameters myParameters;
/*     */     private V8LogCachingReader myCopyReader;
/*     */     
/*     */     public ZoomTask(@Nullable Project project, V8LogCachingReader reader, @Nls String description, Consumer<String> notificator, BeforeAfter<Long> interval) {
/*  96 */       super(project, NodeJSBundle.message("progress.title.zooming.v8.cpu.flamechart", new Object[0]), true);
/*  97 */       this.myProject = project;
/*  98 */       this.myReader = reader;
/*  99 */       this.myDescription = description;
/* 100 */       this.myNotificator = notificator;
/* 101 */       this.myInterval = interval;
/* 102 */       this.myParameters = new FlameChartParameters();
/* 103 */       this.myParameters.setNotInitial(true);
/*     */     }
/*     */     
/*     */     public ZoomTask withDetailsPosition(Long details) {
/* 107 */       this.myParameters.setDetailsPosition(details);
/* 108 */       return this;
/*     */     }
/*     */     
/*     */     public ZoomTask withStringId(long stringId) {
/* 112 */       this.myParameters.setStringId(Long.valueOf(stringId));
/* 113 */       return this;
/*     */     }
/*     */     
/*     */     public ZoomTask withSelection(long from, long to) {
/* 117 */       this.myParameters.setSelection(new BeforeAfter(Long.valueOf(from), Long.valueOf(to)));
/* 118 */       return this;
/*     */     }
/*     */ 
/*     */ 
/*     */     
/*     */     public void run(@NotNull ProgressIndicator indicator) {
/* 124 */       if (indicator == null) $$$reportNull$$$0(0);  try { this.myCopyReader = this.myReader.cloneReader(((Long)this.myInterval.getBefore()).longValue(), ((Long)this.myInterval.getAfter()).longValue()); }
/*     */       
/* 126 */       catch (IOException e1)
/* 127 */       { this.myNotificator.consume(e1.getMessage()); }
/*     */     
/*     */     }
/*     */ 
/*     */ 
/*     */     
/*     */     public void onSuccess() {
/* 134 */       String zoomDescription = this.myDescription + ": " + this.myDescription;
/*     */ 
/*     */       
/* 137 */       V8CpuViewCreatorPartner partner = new V8CpuViewCreatorPartner(this.myProject, this.myCopyReader.getResources(), this.myCopyReader, this.myNotificator, this.myDescription, (Long)this.myInterval.getBefore(), (Long)this.myInterval.getAfter(), this.myParameters);
/*     */ 
/*     */       
/* 140 */       V8ProfilingMainComponent.showMe(this.myProject, zoomDescription, ReadV8LogRawAction.TOOL_WINDOW_TITLE.get(), NodeJSIcons.OpenV8ProfilingLog_ToolWin, 1, (ViewCreatorPartner)partner, null, zoomDescription, AllIcons.Graph.ZoomIn);
/*     */       
/* 142 */       partner.getViewController().showTab(FlameChartView.FLAME_CHART.get());
/*     */     }
/*     */   }
/*     */ }


/* Location:              C:\Program Files\JetBrains\WebStorm 2021.1\plugins\NodeJS\lib\NodeJS.jar!\com\jetbrains\nodejs\run\profile\cpu\v8lo\\ui\ZoomFlameChartAnAction.class
 * Java compiler version: 11 (55.0)
 * JD-Core Version:       1.1.3
 */