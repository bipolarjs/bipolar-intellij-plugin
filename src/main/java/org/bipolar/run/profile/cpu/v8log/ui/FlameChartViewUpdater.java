/*     */ package org.bipolar.run.profile.cpu.v8log.ui;
/*     */ 
/*     */ import com.intellij.openapi.application.ApplicationManager;
/*     */ import com.intellij.openapi.util.Comparing;
/*     */ import com.intellij.openapi.util.Pair;
/*     */ import com.intellij.openapi.util.RequestsMerger;
/*     */ import com.intellij.util.BeforeAfter;
/*     */ import com.intellij.util.Consumer;
/*     */ import com.intellij.util.concurrency.annotations.RequiresBackgroundThread;
/*     */ import com.intellij.util.concurrency.annotations.RequiresEdt;
/*     */ import org.bipolar.run.profile.cpu.v8log.reading.V8CpuViewCallback;
/*     */ import org.bipolar.run.profile.cpu.v8log.reading.V8StackTableModel;
/*     */ import java.io.IOException;
/*     */ import java.util.HashMap;
/*     */ import java.util.Map;
/*     */ import java.util.concurrent.atomic.AtomicBoolean;
/*     */
import org.jetbrains.annotations.NotNull;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class FlameChartViewUpdater
/*     */ {
/*     */   private final FlameChartView myView;
/*     */   @NotNull
/*     */   private final V8CpuViewCallback myViewCallback;
/*     */   @NotNull
/*     */   private final Consumer<? super String> myNotificator;
/*     */   private FlameChartParameters myParameters;
/*     */   private final Map<LoadKind, AtomicBoolean> myNeededLoad;
/*     */   private final Map<UiUpdateKind, AtomicBoolean> myNeededUiUpdate;
/*     */   private final RequestsMerger myUiUpdater;
/*     */   private final RequestsMerger myBackgroundUpdater;
/*     */   private final Object myStackUpdateLock;
/*     */   private int myStackIdx;
/*     */   private V8StackTableModel myStackTableModel;
/*     */   
/*     */   public FlameChartViewUpdater(@NotNull FlameChartView flameChartView, @NotNull V8CpuViewCallback viewCallback, @NotNull Consumer<? super String> notificator, FlameChartParameters parameters) {
/*  41 */     this.myView = flameChartView;
/*  42 */     this.myViewCallback = viewCallback;
/*  43 */     this.myNotificator = notificator;
/*  44 */     this.myParameters = parameters;
/*  45 */     this.myStackUpdateLock = new Object();
/*  46 */     this.myStackIdx = -1;
/*  47 */     this.myStackTableModel = null;
/*     */     
/*  49 */     this.myNeededLoad = new HashMap<>();
/*  50 */     for (LoadKind kind : LoadKind.values()) {
/*  51 */       this.myNeededLoad.put(kind, new AtomicBoolean(false));
/*     */     }
/*  53 */     this.myNeededUiUpdate = new HashMap<>();
/*  54 */     for (UiUpdateKind kind : UiUpdateKind.values()) {
/*  55 */       this.myNeededUiUpdate.put(kind, new AtomicBoolean(false));
/*     */     }
/*  57 */     this.myUiUpdater = new RequestsMerger(createUiUpdate(), runnable -> ApplicationManager.getApplication().invokeLater(runnable));
/*  58 */     this.myBackgroundUpdater = new RequestsMerger(createLoadUpdate(), runnable -> ApplicationManager.getApplication().executeOnPooledThread(runnable));
/*     */   }
/*     */ 
/*     */   
/*     */   private Runnable createLoadUpdate() {
/*  63 */     return () -> {
/*     */         if (((AtomicBoolean)this.myNeededLoad.get(LoadKind.flame)).getAndSet(false)) {
/*     */           ((AtomicBoolean)this.myNeededLoad.get(LoadKind.stack)).getAndSet(false);
/*     */           updateFlameData();
/*     */           return;
/*     */         } 
/*     */         if (((AtomicBoolean)this.myNeededLoad.get(LoadKind.stack)).getAndSet(false)) {
/*     */           updateStack();
/*     */         }
/*     */       };
/*     */   }
/*     */   
/*     */   private Runnable createUiUpdate() {
/*  76 */     return () -> {
/*     */         if (((AtomicBoolean)this.myNeededUiUpdate.get(UiUpdateKind.overviewSelection)).getAndSet(false) && doOverviewSelectionChanged()) {
/*     */           return;
/*     */         }
/*     */         if (((AtomicBoolean)this.myNeededUiUpdate.get(UiUpdateKind.overviewSelectionResult)).getAndSet(false)) {
/*     */           doApplyOverviewSelectionResult();
/*     */         }
/*     */         if (((AtomicBoolean)this.myNeededUiUpdate.get(UiUpdateKind.detailsInChart)).getAndSet(false)) {
/*     */           doDetailsInChartChanged();
/*     */         }
/*     */         if (((AtomicBoolean)this.myNeededUiUpdate.get(UiUpdateKind.detailsRowInChart)).getAndSet(false)) {
/*     */           doDetailsRowInChartChanged();
/*     */         }
/*     */       };
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   @RequiresEdt
/*     */   private void reportUiChange(@NotNull UiUpdateKind kind) {
/*  96 */     if (kind == null) $$$reportNull$$$0(3);  ((AtomicBoolean)this.myNeededUiUpdate.get(kind)).set(true);
/*  97 */     this.myUiUpdater.request();
/*     */   }
/*     */   
/*     */   @RequiresEdt
/*     */   private boolean doOverviewSelectionChanged() {
/*     */     try {
/* 103 */       this.myViewCallback.updateActionsAvailability();
/* 104 */       V8CpuOverviewChart overview = this.myView.getOverviewChart();
/*     */       
/* 106 */       BeforeAfter<Long> currentSelection = this.myView.getFlameChart().getSelection();
/* 107 */       if (Comparing.equal(Long.valueOf(overview.getLeftBound()), currentSelection.getBefore()) && 
/* 108 */         Comparing.equal(Long.valueOf(overview.getRightBound()), currentSelection.getAfter())) {
/* 109 */         checkAndInit();
/* 110 */         return false;
/*     */       } 
/*     */       
/* 113 */       this.myView.getFlameChart().selection(overview.getLeftBound(), overview.getRightBound());
/* 114 */       this.myView.getEventsStripe().selection(overview.getLeftBound(), overview.getRightBound());
/*     */       
/* 116 */       ((AtomicBoolean)this.myNeededLoad.get(LoadKind.flame)).set(true);
/* 117 */       this.myBackgroundUpdater.request();
/*     */     }
/* 119 */     catch (IOException e) {
/* 120 */       this.myNotificator.consume(e.getMessage());
/*     */     } 
/* 122 */     return true;
/*     */   }
/*     */   
/*     */   private void checkAndInit() {
/* 126 */     if (this.myParameters != null) {
/* 127 */       this.myParameters.updateView(this.myView);
/* 128 */       this.myParameters = null;
/*     */     } 
/*     */   }
/*     */   
/*     */   @RequiresEdt
/*     */   private void doApplyOverviewSelectionResult() {
/* 134 */     boolean empty = !((AtomicBoolean)this.myNeededUiUpdate.get(UiUpdateKind.overviewSelection)).get();
/* 135 */     this.myView.getFlameChart().getUIUpdate().consume(Boolean.valueOf(empty));
/* 136 */     this.myView.getEventsStripe().getUIUpdate().consume(Boolean.valueOf(empty));
/* 137 */     checkAndInit();
/*     */   }
/*     */   
/*     */   @RequiresBackgroundThread
/*     */   private void updateFlameData() {
/* 142 */     this.myView.getFlameChart().updateData();
/* 143 */     this.myView.getEventsStripe().updateData();
/* 144 */     reportUiChange(UiUpdateKind.overviewSelectionResult);
/*     */   }
/*     */   
/*     */   @RequiresBackgroundThread
/*     */   private void updateStack() {
/* 149 */     Pair<Long, Integer> detailsPosition = this.myView.getFlameChart().getDetailsPosition();
/* 150 */     synchronized (this.myStackUpdateLock) {
/* 151 */       if (((Integer)detailsPosition.getSecond()).intValue() == this.myStackIdx) {
/* 152 */         reportUiChange(UiUpdateKind.detailsInChart);
/*     */         return;
/*     */       } 
/*     */     } 
/*     */     try {
/* 157 */       V8StackTableModel model = this.myView.createStackTableModel(((Integer)detailsPosition.getSecond()).intValue());
/* 158 */       synchronized (this.myStackUpdateLock) {
/* 159 */         this.myStackIdx = ((Integer)detailsPosition.getSecond()).intValue();
/* 160 */         this.myStackTableModel = model;
/*     */       } 
/* 162 */       reportUiChange(UiUpdateKind.detailsInChart);
/*     */     }
/* 164 */     catch (IOException e) {
/* 165 */       this.myNotificator.consume(e.getMessage());
/*     */     } 
/*     */   }
/*     */   
/*     */   @RequiresEdt
/*     */   private void doDetailsInChartChanged() {
/*     */     V8StackTableModel model;
/* 172 */     synchronized (this.myStackUpdateLock) {
/* 173 */       model = this.myStackTableModel;
/*     */     } 
/* 175 */     this.myView.updateStackTraceTable(model);
/*     */   }
/*     */   
/*     */   @RequiresEdt
/*     */   private void doDetailsRowInChartChanged() {
/* 180 */     this.myView.setSelectedRowInTableByChart();
/*     */   }
/*     */   
/*     */   @RequiresEdt
/*     */   public void overviewSelectionChanged() {
/* 185 */     reportUiChange(UiUpdateKind.overviewSelection);
/*     */   }
/*     */   
/*     */   @RequiresEdt
/*     */   public void detailsLineChanged() {
/* 190 */     Pair<Long, Integer> detailsPosition = this.myView.getFlameChart().getDetailsPosition();
/* 191 */     synchronized (this.myStackUpdateLock) {
/* 192 */       if (((Integer)detailsPosition.getSecond()).intValue() == this.myStackIdx) {
/* 193 */         reportUiChange(UiUpdateKind.detailsRowInChart);
/*     */       } else {
/* 195 */         ((AtomicBoolean)this.myNeededLoad.get(LoadKind.stack)).set(true);
/* 196 */         this.myBackgroundUpdater.request();
/*     */       } 
/*     */     } 
/*     */   }
/*     */   
/*     */   public void afterLoad(final Runnable runnable) {
/* 202 */     this.myBackgroundUpdater.waitRefresh(() -> this.myUiUpdater.waitRefresh(new Runnable()
/*     */           {
/*     */             public void run() {
/* 205 */               if (!FlameChartViewUpdater.this.myBackgroundUpdater.isEmpty() || !FlameChartViewUpdater.this.myUiUpdater.isEmpty() || somethingRaised()) {
/* 206 */                 ApplicationManager.getApplication().invokeLater(() -> FlameChartViewUpdater.this.afterLoad(runnable));
/*     */               } else {
/*     */                 
/* 209 */                 runnable.run();
/*     */               } 
/*     */             }
/*     */             
/*     */             private boolean somethingRaised() {
/* 214 */               boolean raised = false;
/* 215 */               for (AtomicBoolean aBoolean : FlameChartViewUpdater.this.myNeededLoad.values()) {
/* 216 */                 raised |= aBoolean.get();
/*     */               }
/* 218 */               for (AtomicBoolean aBoolean : FlameChartViewUpdater.this.myNeededUiUpdate.values()) {
/* 219 */                 raised |= aBoolean.get();
/*     */               }
/* 221 */               return raised;
/*     */             }
/*     */           }));
/*     */   }
/*     */   
/*     */   private enum LoadKind {
/* 227 */     flame, stack;
/*     */   }
/*     */   
/*     */   public enum UiUpdateKind {
/* 231 */     overviewSelection, detailsInChart, detailsRowInChart, overviewSelectionResult;
/*     */   }
/*     */ }


/* Location:              C:\Program Files\JetBrains\WebStorm 2021.1\plugins\NodeJS\lib\NodeJS.jar!\com\jetbrains\nodejs\run\profile\cpu\v8lo\\ui\FlameChartViewUpdater.class
 * Java compiler version: 11 (55.0)
 * JD-Core Version:       1.1.3
 */