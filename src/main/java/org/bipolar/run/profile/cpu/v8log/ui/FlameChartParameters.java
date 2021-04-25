/*    */ package org.bipolar.run.profile.cpu.v8log.ui;
/*    */ 
/*    */ import com.intellij.openapi.application.ApplicationManager;
/*    */ import com.intellij.util.BeforeAfter;
/*    */ import java.util.ArrayList;
/*    */ import java.util.List;
/*    */ import org.jetbrains.annotations.NotNull;
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public class FlameChartParameters
/*    */ {
/*    */   private BeforeAfter<Long> mySelection;
/*    */   private Long myDetailsPosition;
/*    */   private Long myStringId;
/*    */   private boolean myNotInitial;
/*    */   
/*    */   public BeforeAfter<Long> getSelection() {
/* 20 */     return this.mySelection;
/*    */   }
/*    */   
/*    */   public void setSelection(BeforeAfter<Long> selection) {
/* 24 */     this.mySelection = selection;
/*    */   }
/*    */   
/*    */   public Long getDetailsPosition() {
/* 28 */     return this.myDetailsPosition;
/*    */   }
/*    */   
/*    */   public void setDetailsPosition(Long detailsPosition) {
/* 32 */     this.myDetailsPosition = detailsPosition;
/*    */   }
/*    */   
/*    */   public Long getStringId() {
/* 36 */     return this.myStringId;
/*    */   }
/*    */   
/*    */   public void setNotInitial(boolean notInitial) {
/* 40 */     this.myNotInitial = notInitial;
/*    */   }
/*    */   
/*    */   public void setStringId(Long stringId) {
/* 44 */     this.myStringId = stringId;
/*    */   }
/*    */   
/*    */   public void updateView(@NotNull FlameChartView view) {
/* 48 */     if (view == null) $$$reportNull$$$0(0);  List<Runnable> steps = new ArrayList<>();
/* 49 */     if (!this.myNotInitial || this.mySelection != null) {
/* 50 */       steps.add(() -> {
/*    */             if (this.mySelection != null) {
/*    */               view.getOverviewChart().setSelection(((Long)this.mySelection.getBefore()).longValue(), ((Long)this.mySelection.getAfter()).longValue());
/*    */             } else {
/*    */               view.getOverviewChart().initialSelection();
/*    */             } 
/*    */           });
/*    */     }
/*    */     
/* 59 */     if (this.myDetailsPosition != null) {
/* 60 */       steps.add(() -> setDetails(view));
/*    */     }
/* 62 */     if (steps.isEmpty())
/* 63 */       return;  doStep(steps, 0);
/*    */   }
/*    */   
/*    */   private void setDetails(@NotNull FlameChartView view) {
/* 67 */     if (view == null) $$$reportNull$$$0(1);  view.getViewUpdater().afterLoad(() -> {
/*    */           if (view.getFlameChart().isEmpty())
/*    */             setDetails(view); 
/*    */           view.getFlameChart().setDetailsPosition(this.myDetailsPosition, (this.myStringId == null) ? -1L : this.myStringId.longValue());
/*    */         });
/*    */   }
/*    */   private static void doStep(List<Runnable> steps, int idx) {
/* 74 */     ApplicationManager.getApplication().invokeLater(() -> {
/*    */           ((Runnable)steps.get(idx)).run();
/*    */           if (idx < steps.size() - 1)
/*    */             doStep(steps, idx + 1); 
/*    */         });
/*    */   }
/*    */ }


/* Location:              C:\Program Files\JetBrains\WebStorm 2021.1\plugins\NodeJS\lib\NodeJS.jar!\com\jetbrains\nodejs\run\profile\cpu\v8lo\\ui\FlameChartParameters.class
 * Java compiler version: 11 (55.0)
 * JD-Core Version:       1.1.3
 */