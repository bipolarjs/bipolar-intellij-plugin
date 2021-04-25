/*     */ package org.bipolar.run.profile.cpu.view;
/*     */ 
/*     */ import com.intellij.javascript.nodejs.NodeUIUtil;
/*     */ import com.intellij.openapi.actionSystem.AnAction;
/*     */ import com.intellij.openapi.actionSystem.AnActionEvent;
/*     */ import com.intellij.openapi.actionSystem.DefaultActionGroup;
/*     */ import com.intellij.openapi.actionSystem.PlatformDataKeys;
/*     */ import com.intellij.openapi.progress.ProgressManager;
/*     */ import com.intellij.openapi.progress.Task;
/*     */ import com.intellij.openapi.project.DumbAwareAction;
/*     */ import com.intellij.openapi.project.Project;
/*     */ import com.intellij.util.BeforeAfter;
/*     */ import com.intellij.util.Consumer;
/*     */ import org.bipolar.NodeJSBundle;
/*     */ import org.bipolar.run.profile.V8Utils;
/*     */ import org.bipolar.run.profile.cpu.v8log.calculation.TimeDistribution;
/*     */ import org.bipolar.run.profile.cpu.v8log.data.V8CpuLogCall;
/*     */ import org.bipolar.run.profile.cpu.v8log.reading.V8LogCachingReader;
/*     */ import org.bipolar.run.profile.cpu.v8log.ui.ZoomFlameChartAnAction;
/*     */ import icons.NodeJSIcons;
/*     */ import java.io.IOException;
/*     */
import org.jetbrains.annotations.NotNull;
/*     */ 
/*     */ 
/*     */ public class V8NavigateToFlameChartIntervalAction
/*     */   extends DumbAwareAction
/*     */ {
/*     */   @NotNull
/*     */   private final Kind myKind;
/*     */   private final V8LogCachingReader myReader;
/*     */   private final Consumer<String> myNotificator;
/*     */   
/*     */   public V8NavigateToFlameChartIntervalAction(@NotNull Kind kind, V8LogCachingReader reader, Consumer<String> notificator) {
/*  34 */     super(name(kind), name(kind), NodeJSIcons.Navigate_inMainTree);
/*  35 */     this.myKind = kind;
/*  36 */     this.myReader = reader;
/*  37 */     this.myNotificator = notificator;
/*     */   }
/*     */ 
/*     */   
/*     */   public static void addToGroup(DefaultActionGroup group, V8LogCachingReader reader, Consumer<String> notificator) {
/*  42 */     for (Kind kind : Kind.values()) {
/*  43 */       group.add((AnAction)new V8NavigateToFlameChartIntervalAction(kind, reader, notificator));
/*     */     }
/*     */   }
/*     */   
/*     */   @NotNull
/*     */   private static String name(@NotNull Kind kind) {
/*  49 */     if (kind == null) $$$reportNull$$$0(1);  if ("Navigate To " + kind.getText() + " Time" == null) $$$reportNull$$$0(2);  return "Navigate To " + kind.getText() + " Time";
/*     */   }
/*     */ 
/*     */   
/*     */   public void update(@NotNull AnActionEvent e) {
/*  54 */     if (e == null) $$$reportNull$$$0(3);  e.getPresentation().setEnabled((e.getData(V8Utils.SELECTED_CALL) != null && e
/*  55 */         .getData(PlatformDataKeys.PROJECT) != null));
/*     */   }
/*     */ 
/*     */   
/*     */   public void actionPerformed(@NotNull AnActionEvent e) {
/*  60 */     if (e == null) $$$reportNull$$$0(4);  V8CpuLogCall call = (V8CpuLogCall)e.getData(V8Utils.SELECTED_CALL);
/*  61 */     Project project = (Project)e.getData(PlatformDataKeys.PROJECT);
/*  62 */     if (call == null || project == null)
/*     */       return;  try {
/*     */       TimeDistribution distribution;
/*     */       long interval, start;
/*  66 */       if (this.myKind.mySelf) {
/*  67 */         distribution = this.myReader.getSelfTimesDistribution(call.getStringId());
/*     */       } else {
/*  69 */         distribution = this.myReader.getTimesDistribution(call.getStringId());
/*     */       } 
/*  71 */       if (distribution == null || distribution.isEmpty()) {
/*     */         
/*  73 */         String message = NodeJSBundle.message("popup.content.cannot.navigate.to.flame.chart.details", new Object[] { Integer.valueOf(this.myKind.mySelf ? 0 : 1) });
/*  74 */         NodeUIUtil.balloonInfo(project, message, null, null);
/*     */         
/*     */         return;
/*     */       } 
/*     */       
/*  79 */       boolean takeLongest = (this.myKind.myLongest || distribution.getTypicalIndex() == -1);
/*  80 */       if (takeLongest) {
/*  81 */         interval = distribution.getMax();
/*  82 */         start = distribution.getMaxStartTs();
/*     */       } else {
/*  84 */         int index = distribution.getTypicalIndex();
/*  85 */         interval = distribution.getEndTimes()[index];
/*  86 */         start = distribution.getSampleStartTs()[index];
/*     */       } 
/*  88 */       long selectionStart = (long)Math.max(0.0D, start - interval * 0.5D);
/*  89 */       long selectionEnd = start + interval * 2L;
/*  90 */       long selected = selectionEnd - selectionStart;
/*  91 */       long showStart = Math.max(0L, selectionStart - selected * 4L);
/*  92 */       long showEnd = selectionStart + selected * 5L;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */       
/*  98 */       ZoomFlameChartAnAction.ZoomTask task = (new ZoomFlameChartAnAction.ZoomTask(project, this.myReader, this.myReader.getV8LogFile().getName(), this.myNotificator, new BeforeAfter(Long.valueOf(showStart), Long.valueOf(showEnd)))).withDetailsPosition(Long.valueOf(start + interval / 4L)).withStringId(call.getStringId()).withSelection(selectionStart, selectionEnd);
/*  99 */       ProgressManager.getInstance().run((Task)task);
/*     */     }
/* 101 */     catch (IOException e1) {
/* 102 */       NodeUIUtil.balloonInfo(project, NodeJSBundle.message("popup.content.can.not.navigate.to.flame.chart", new Object[] { e1.getMessage() }), null, null);
/*     */     } 
/*     */   }
/*     */   
/*     */   public enum Kind {
/* 107 */     longestTotal("Longest", true, false),
/* 108 */     typicalTotal("Typical", false, false),
/* 109 */     longestSelf("Longest Self", true, true),
/* 110 */     typicalSelf("Typical Self", false, true);
/*     */     
/*     */     private final String myText;
/*     */     private final boolean myLongest;
/*     */     private final boolean mySelf;
/*     */     
/*     */     Kind(String text, boolean longest, boolean self) {
/* 117 */       this.myText = text;
/* 118 */       this.myLongest = longest;
/* 119 */       this.mySelf = self;
/*     */     }
/*     */     
/*     */     public String getText() {
/* 123 */       return this.myText;
/*     */     }
/*     */   }
/*     */ }


/* Location:              C:\Program Files\JetBrains\WebStorm 2021.1\plugins\NodeJS\lib\NodeJS.jar!\com\jetbrains\nodejs\run\profile\cpu\view\V8NavigateToFlameChartIntervalAction.class
 * Java compiler version: 11 (55.0)
 * JD-Core Version:       1.1.3
 */