/*     */ package org.bipolar.run.profile.cpu.v8log.reading;
/*     */ import com.intellij.ide.actions.ContextHelpAction;
/*     */ import com.intellij.javascript.nodejs.NodeUIUtil;
/*     */ import com.intellij.lang.javascript.ui.FileColor;
/*     */ import com.intellij.openapi.Disposable;
/*     */ import com.intellij.openapi.actionSystem.ActionManager;
/*     */ import com.intellij.openapi.actionSystem.AnAction;
/*     */ import com.intellij.openapi.actionSystem.AnActionEvent;
/*     */ import com.intellij.openapi.actionSystem.DefaultActionGroup;
/*     */ import com.intellij.openapi.fileChooser.FileSaverDescriptor;
/*     */ import com.intellij.openapi.fileChooser.FileSaverDialog;
/*     */ import com.intellij.openapi.progress.ProgressIndicator;
/*     */ import com.intellij.openapi.progress.Task;
/*     */ import com.intellij.openapi.project.Project;
/*     */ import com.intellij.openapi.ui.MessageType;
/*     */ import com.intellij.openapi.vfs.VirtualFileWrapper;
/*     */ import com.intellij.ui.JBColor;
/*     */ import com.intellij.ui.treeStructure.treetable.TreeTable;
/*     */ import com.intellij.util.Consumer;
/*     */ import org.bipolar.NodeJSBundle;
/*     */ import org.bipolar.run.profile.V8Utils;
/*     */ import org.bipolar.run.profile.cpu.calculation.CallTreeType;
/*     */ import org.bipolar.run.profile.cpu.v8log.diff.V8CpuDiffAction;
/*     */ import org.bipolar.run.profile.cpu.v8log.ui.FlameChartParameters;
/*     */ import org.bipolar.run.profile.cpu.v8log.ui.FlameChartView;
/*     */ import org.bipolar.run.profile.cpu.v8log.ui.FlameColors;
/*     */ import org.bipolar.run.profile.cpu.view.CollapseNodeAction;
/*     */ import org.bipolar.run.profile.cpu.view.LineColorProvider;
/*     */ import org.bipolar.run.profile.cpu.view.ProfilingView;
/*     */ import org.bipolar.run.profile.cpu.view.TopCallsV8ProfilingComponent;
/*     */ import org.bipolar.run.profile.cpu.view.V8ProfilingCallTreeComponent;
/*     */ import org.bipolar.run.profile.cpu.view.V8ProfilingCallTreeTable;
/*     */ import org.bipolar.run.profile.cpu.view.V8ProfilingMainComponent;
/*     */ import org.bipolar.run.profile.heap.CompositeCloseable;
/*     */ import org.bipolar.run.profile.settings.NodeProfilingSettings;
/*     */ import java.awt.Color;
/*     */ import java.awt.Point;
/*     */ import java.io.IOException;
/*     */ import java.util.ArrayList;
/*     */ import java.util.Collections;
/*     */ import java.util.List;
/*     */ import javax.swing.Icon;
/*     */ import javax.swing.JComponent;
/*     */
import org.jetbrains.annotations.Nls;
/*     */ import org.jetbrains.annotations.NotNull;
/*     */ import org.jetbrains.annotations.Nullable;
/*     */ 
/*     */ public class V8CpuViewCreatorPartner implements ViewCreatorPartner<V8ProfilingCallTreeTable> {
/*     */   private final Project myProject;
/*     */   private final CompositeCloseable myResources;
/*     */   private final V8LogCachingReader myReader;
/*     */   @NotNull
/*     */   private final Consumer<String> myNotificator;
/*     */   @Nls
/*     */   private final String myDescription;
/*     */   @Nullable
/*     */   private final Long myLeftTs;
/*     */   @Nullable
/*     */   private final Long myRightTs;
/*     */   @NotNull
/*     */   private final FlameChartParameters myParameters;
/*     */   private final LineColorProvider myLineColorProvider;
/*     */   private V8ProfilingMainComponent.MyController<V8ProfilingCallTreeTable> myViewController;
/*     */   private FlameChartView myFlameChartView;
/*     */   private ActionToolbar myActionToolbar;
/*     */   private V8ProfilingCallTreeComponent myBottomUp;
/*     */   private V8ProfilingCallTreeComponent myTopDown;
/*     */   private TopCallsV8ProfilingComponent myTopCalls;
/*     */   
/*     */   public V8CpuViewCreatorPartner(Project project, CompositeCloseable resources, @NotNull V8LogCachingReader reader, @NotNull Consumer<String> notificator, @Nls String description, @Nullable Long leftTs, @Nullable Long rightTs, @NotNull FlameChartParameters parameters) {
/*  71 */     this.myProject = project;
/*  72 */     this.myResources = resources;
/*  73 */     this.myReader = reader;
/*  74 */     this.myNotificator = notificator;
/*  75 */     this.myDescription = description;
/*  76 */     this.myLeftTs = leftTs;
/*  77 */     this.myRightTs = rightTs;
/*  78 */     this.myParameters = parameters;
/*  79 */     this.myLineColorProvider = createLineColorProvider(this.myReader, this.myNotificator);
/*     */   }
/*     */ 
/*     */   
/*     */   @NotNull
/*     */   public static LineColorProvider createLineColorProvider(@NotNull final V8LogCachingReader reader, @NotNull final Consumer<? super String> notificator) {
/*  85 */     if (reader == null) $$$reportNull$$$0(3);  if (notificator == null) $$$reportNull$$$0(4);  return new LineColorProvider()
/*     */       {
/*     */         public Icon getFileIcon(String fileName) {
/*  88 */           return getColorByStringId(0L);
/*     */         }
/*     */ 
/*     */         
/*     */         public Icon getColorByStringId(long id) {
/*  93 */           if (id == -1L) {
/*  94 */             return FileColor.createIcon(Color.gray);
/*     */           }
/*     */           try {
/*  97 */             JBColor color = FlameColors.getColor(id, reader.getCodeScopeByStringId(id));
/*  98 */             return FileColor.createIcon((Color)color);
/*     */           }
/* 100 */           catch (IOException e) {
/* 101 */             notificator.consume(e.getMessage());
/* 102 */             return FileColor.createIcon(Color.gray);
/*     */           } 
/*     */         }
/*     */       };
/*     */   }
/*     */ 
/*     */   
/*     */   public JComponent wrapWithStandardActions(ProfilingView<V8ProfilingCallTreeTable> view, AnAction closeAction) {
/* 110 */     DefaultActionGroup group = new DefaultActionGroup();
/* 111 */     view.addActions(group);
/* 112 */     if (view.getTreeTable() != null) {
/* 113 */       DefaultActionGroup popupGroup = new DefaultActionGroup(group.getChildActionsOrStubs());
/* 114 */       V8ProfilingCallTreeTable table = (V8ProfilingCallTreeTable)view.getTreeTable();
/* 115 */       group.add((AnAction)new ExpandByDefaultAction(table, this.myViewController));
/* 116 */       group.add((AnAction)new V8Utils.CollapseAllAction((TreeTable)table));
/* 117 */       group.add(new ExportStatisticsAction(this.myReader));
/* 118 */       group.add((AnAction)new V8CpuDiffAction(this.myProject, this.myReader));
/*     */       
/* 120 */       popupGroup.add((AnAction)new CopyNameAction(table));
/* 121 */       popupGroup.add(ActionManager.getInstance().getAction("$Copy"));
/* 122 */       popupGroup.add((AnAction)new CompareWithClipboard(table));
/* 123 */       popupGroup.add((AnAction)new ExpandNodeAction(table));
/* 124 */       popupGroup.add((AnAction)new CollapseNodeAction(table));
/* 125 */       PopupHandler.installPopupHandler((JComponent)view.getTreeTable(), (ActionGroup)popupGroup, "V8_CPU_PROFILING_POPUP", ActionManager.getInstance());
/*     */     } 
/* 127 */     ContextHelpAction helpAction = new ContextHelpAction("reference.tool.window.v8.profiling");
/* 128 */     group.add((AnAction)helpAction);
/* 129 */     helpAction.registerCustomShortcutSet(CommonShortcuts.getContextHelp(), view.getMainComponent());
/* 130 */     group.add(closeAction);
/* 131 */     V8Utils.WithToolbarWrapper wrapper = new V8Utils.WithToolbarWrapper(view.getMainComponent(), group);
/* 132 */     this.myActionToolbar = wrapper.getToolbar();
/* 133 */     return wrapper.getMainPanel();
/*     */   }
/*     */ 
/*     */   
/*     */   public void addViews(Project project, List<ProfilingView<V8ProfilingCallTreeTable>> list, Disposable disposable) {
/*     */     try {
/* 139 */       V8SwitchViewActionsFactory actionsFactory = new V8SwitchViewActionsFactory(this.myViewController);
/* 140 */       this.myTopCalls = new TopCallsV8ProfilingComponent(project, this.myReader, this.myReader.getFlat().createPresentation(), this.myLineColorProvider, disposable, this.myNotificator);
/* 141 */       this.myBottomUp = new V8ProfilingCallTreeComponent(project, this.myReader, CallTreeType.bottomUp, this.myLineColorProvider, disposable, this.myNotificator);
/* 142 */       this.myTopDown = new V8ProfilingCallTreeComponent(project, this.myReader, CallTreeType.topDown, this.myLineColorProvider, disposable, this.myNotificator);
/* 143 */       this
/* 144 */         .myFlameChartView = new FlameChartView(this.myProject, this.myReader, this.myNotificator, this.myLeftTs, this.myRightTs, this.myParameters, this.myDescription, createViewCallback());
/*     */       
/* 146 */       this.myTopCalls.registerItself(actionsFactory);
/* 147 */       this.myBottomUp.registerItself(actionsFactory);
/* 148 */       this.myTopDown.registerItself(actionsFactory);
/* 149 */       actionsFactory.setFlameChartView(this.myFlameChartView);
/* 150 */       actionsFactory.createActions();
/*     */       
/* 152 */       list.add(this.myTopCalls);
/* 153 */       list.add(this.myBottomUp);
/* 154 */       list.add(this.myTopDown);
/* 155 */       list.add(this.myFlameChartView);
/*     */     }
/* 157 */     catch (IOException e) {
/* 158 */       this.myNotificator.consume(e.getMessage());
/*     */     } 
/*     */   }
/*     */   
/*     */   @NotNull
/*     */   private V8CpuViewCallback createViewCallback() {
/* 164 */     return new V8CpuViewCallback()
/*     */       {
/*     */         public void updateActionsAvailability() {
/* 167 */           if (V8CpuViewCreatorPartner.this.myActionToolbar != null) {
/* 168 */             V8CpuViewCreatorPartner.this.myActionToolbar.updateActionsImmediately();
/*     */           }
/*     */         }
/*     */ 
/*     */         
/*     */         public void navigateToTopCalls(@NotNull Long stackId, Point onScreen) {
/* 174 */           if (stackId == null) $$$reportNull$$$0(0);  if (V8CpuViewCreatorPartner.this.myTopCalls.navigateByStackTrace(stackId)) {
/* 175 */             V8CpuViewCreatorPartner.this.myViewController.showTab(V8CpuViewCreatorPartner.this.myTopCalls.getName());
/*     */           } else {
/* 177 */             V8CpuViewCreatorPartner.this.canNotNavigate(onScreen);
/*     */           } 
/*     */         }
/*     */ 
/*     */         
/*     */         public void navigateToBottomUp(@NotNull List<Long> stackIds, Point onScreen) {
/* 183 */           if (stackIds == null) $$$reportNull$$$0(1);  if (V8CpuViewCreatorPartner.this.myBottomUp.navigateByStackTrace(stackIds)) {
/* 184 */             V8CpuViewCreatorPartner.this.myViewController.showTab(V8CpuViewCreatorPartner.this.myBottomUp.getName());
/*     */           } else {
/* 186 */             V8CpuViewCreatorPartner.this.canNotNavigate(onScreen);
/*     */           } 
/*     */         }
/*     */ 
/*     */         
/*     */         public void navigateToTopDown(@NotNull List<Long> stackIds, Point onScreen) {
/* 192 */           if (stackIds == null) $$$reportNull$$$0(2);  stackIds = new ArrayList<>(stackIds);
/* 193 */           Collections.reverse(stackIds);
/* 194 */           if (V8CpuViewCreatorPartner.this.myTopDown.navigateByStackTrace(stackIds)) {
/* 195 */             V8CpuViewCreatorPartner.this.myViewController.showTab(V8CpuViewCreatorPartner.this.myTopDown.getName());
/*     */           } else {
/* 197 */             V8CpuViewCreatorPartner.this.canNotNavigate(onScreen);
/*     */           } 
/*     */         }
/*     */       };
/*     */   }
/*     */   
/*     */   private void canNotNavigate(Point onScreen) {
/* 204 */     NodeUIUtil.balloonInfo(this.myProject, NodeJSBundle.message("node.js.v8.cpu.navigation.not.found.error", new Object[0]), onScreen, null);
/*     */   }
/*     */ 
/*     */   
/*     */   public String errorCreated() {
/* 209 */     return null;
/*     */   }
/*     */ 
/*     */   
/*     */   public void announceController(V8ProfilingMainComponent.MyController<V8ProfilingCallTreeTable> controller) {
/* 214 */     this.myViewController = controller;
/*     */   }
/*     */   
/*     */   public V8ProfilingMainComponent.MyController<V8ProfilingCallTreeTable> getViewController() {
/* 218 */     return this.myViewController;
/*     */   }
/*     */ 
/*     */   
/*     */   public void close() {
/*     */     try {
/* 224 */       this.myResources.close();
/*     */     }
/* 226 */     catch (IOException iOException) {}
/*     */   }
/*     */   
/*     */   private class ExportStatisticsAction
/*     */     extends AnAction
/*     */   {
/*     */     private final V8LogCachingReader myReader;
/*     */     
/*     */     ExportStatisticsAction(V8LogCachingReader reader) {
/* 235 */       super(NodeJSBundle.messagePointer("action.ExportStatisticsAction.export.profiling.statistic.text", new Object[0]), 
/* 236 */           NodeJSBundle.messagePointer("action.ExportStatisticsAction.export.profiling.statistic.description", new Object[0]), AllIcons.ToolbarDecorator.Export);
/* 237 */       this.myReader = reader;
/*     */     }
/*     */     
/*     */     public void actionPerformed(@NotNull AnActionEvent e)
/*     */     {
/* 242 */       if (e == null) $$$reportNull$$$0(0); 
/* 243 */       FileSaverDescriptor descriptor = new FileSaverDescriptor(ReadV8LogRawAction.TOOL_WINDOW_TITLE.get(), NodeJSBundle.message("label.select.path.to.save.statistics", new Object[0]), new String[] { "txt" });
/* 244 */       FileSaverDialog saverDialog = FileChooserFactory.getInstance().createSaveFileDialog(descriptor, V8CpuViewCreatorPartner.this.myProject);
/* 245 */       final VirtualFileWrapper fileWrapper = saverDialog.save(V8CpuViewCreatorPartner.this.myProject.getBaseDir(), "v8_statistics.txt");
/*     */       
/* 247 */       if (fileWrapper != null)
/* 248 */         ProgressManager.getInstance().run((Task)new Task.Backgroundable(V8CpuViewCreatorPartner.this.myProject, NodeJSBundle.message("progress.text.export.profiling.stats", new Object[0]), true)
/*     */             {
/*     */               
/*     */               public void run(@NotNull ProgressIndicator indicator)
/*     */               {
/* 253 */                 if (indicator == null) $$$reportNull$$$0(0);  try { V8CpuViewCreatorPartner.ExportStatisticsAction.this.myReader.createStatisticalReport(fileWrapper.getFile());
/*     */                   
/* 255 */                   NodeProfilingSettings.CPU_NOTIFICATION_GROUP
/* 256 */                     .createNotification(
/* 257 */                       NodeJSBundle.message("profile.cpu.statistics_saved_to_file.notification.content", new Object[] { this.val$fileWrapper.getFile().getName() }), MessageType.INFO)
/* 258 */                     .notify(this.myProject); }
/*     */                 
/* 260 */                 catch (IOException e)
/* 261 */                 { NodeProfilingSettings.CPU_NOTIFICATION_GROUP
/* 262 */                     .createNotification(
/* 263 */                       NodeJSBundle.message("profile.cpu.error_saving_statistics_to_file.notification.content", new Object[] { e.getMessage() }), MessageType.ERROR)
/* 264 */                     .notify(this.myProject); }  } });  } } class null extends Task.Backgroundable { public void run(@NotNull ProgressIndicator indicator) { if (indicator == null) $$$reportNull$$$0(0);  try { V8CpuViewCreatorPartner.ExportStatisticsAction.this.myReader.createStatisticalReport(fileWrapper.getFile()); NodeProfilingSettings.CPU_NOTIFICATION_GROUP.createNotification(NodeJSBundle.message("profile.cpu.statistics_saved_to_file.notification.content", new Object[] { this.val$fileWrapper.getFile().getName() }), MessageType.INFO).notify(this.myProject); } catch (IOException e) { NodeProfilingSettings.CPU_NOTIFICATION_GROUP.createNotification(NodeJSBundle.message("profile.cpu.error_saving_statistics_to_file.notification.content", new Object[] { e.getMessage() }), MessageType.ERROR).notify(this.myProject); }
/*     */        }
/*     */ 
/*     */     
/*     */     null(Project arg0, String arg1, boolean arg2) {
/*     */       super(arg0, arg1, arg2);
/*     */     } }
/*     */ 
/*     */ }


/* Location:              C:\Program Files\JetBrains\WebStorm 2021.1\plugins\NodeJS\lib\NodeJS.jar!\com\jetbrains\nodejs\run\profile\cpu\v8log\reading\V8CpuViewCreatorPartner.class
 * Java compiler version: 11 (55.0)
 * JD-Core Version:       1.1.3
 */