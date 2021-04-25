/*     */ package org.bipolar.run.profile.cpu.v8log.ui;
/*     */ import com.intellij.openapi.Disposable;
/*     */ import com.intellij.openapi.actionSystem.AnAction;
/*     */ import com.intellij.openapi.actionSystem.AnActionEvent;
/*     */ import com.intellij.openapi.actionSystem.DefaultActionGroup;
/*     */ import com.intellij.openapi.project.Project;
/*     */ import com.intellij.openapi.ui.OnePixelDivider;
/*     */ import com.intellij.openapi.ui.Splitter;
/*     */ import com.intellij.openapi.util.Disposer;
/*     */ import com.intellij.openapi.util.Pair;
/*     */ import com.intellij.ui.ScrollingUtil;
/*     */ import com.intellij.ui.components.JBScrollPane;
/*     */
/*     */ import com.intellij.util.Consumer;
/*     */ import com.intellij.util.ui.JBUI;
/*     */ import com.intellij.util.ui.UIUtil;
/*     */ import com.intellij.util.ui.update.UiNotifyConnector;
/*     */ import org.bipolar.NodeJSBundle;
/*     */ import org.bipolar.run.profile.V8Utils;
/*     */ import org.bipolar.run.profile.cpu.v8log.data.V8CpuLogCall;
/*     */ import org.bipolar.run.profile.cpu.v8log.reading.V8CpuViewCallback;
/*     */ import org.bipolar.run.profile.cpu.v8log.reading.V8LogCachingReader;
/*     */ import org.bipolar.run.profile.cpu.v8log.reading.V8StackTableModel;
/*     */ import org.bipolar.run.profile.cpu.view.TopCallsV8ProfilingComponent;
/*     */ import org.bipolar.run.profile.cpu.view.V8CpuNavigationAction;
/*     */ import org.bipolar.run.profile.cpu.view.V8ProfilingCallTreeComponent;
/*     */ import org.bipolar.run.profile.cpu.view.V8ProfilingCallTreeTable;
/*     */ import org.bipolar.run.profile.heap.view.components.DataProviderPanel;
/*     */ import java.awt.BorderLayout;
/*     */ import java.awt.Component;
/*     */ import java.awt.Dimension;
/*     */
/*     */ import java.awt.Graphics;
/*     */ import java.awt.Graphics2D;
/*     */ import java.awt.Point;
/*     */ import java.io.IOException;
/*     */ import java.util.Collections;
/*     */ import java.util.List;
/*     */ import javax.swing.JComponent;
/*     */ import javax.swing.JPanel;
/*     */ import javax.swing.JTable;
/*     */ import javax.swing.event.ListSelectionEvent;
/*     */ import javax.swing.event.ListSelectionListener;
/*     */ import javax.swing.table.TableModel;
/*     */
import org.jetbrains.annotations.Nls;
/*     */ import org.jetbrains.annotations.NotNull;
/*     */ import org.jetbrains.annotations.Nullable;
/*     */ 
/*     */ public class FlameChartView implements ProfilingView<V8ProfilingCallTreeTable> {
/*  50 */   public static final Supplier<String> FLAME_CHART = NodeJSBundle.messagePointer("profile.flame.chart.title", new Object[0]);
/*     */   
/*     */   @NotNull
/*     */   private final Project myProject;
/*     */   
/*     */   private final V8LogCachingReader myReader;
/*     */   
/*     */   private final Consumer<String> myNotificator;
/*     */   
/*     */   @Nls
/*     */   private final String myDescription;
/*     */   @NotNull
/*     */   private final V8CpuViewCallback myViewCallback;
/*     */   private V8CpuOverviewChart myChart;
/*     */   private V8CpuFlameChart myFlameChart;
/*     */   private StackTraceTable myStackTraceTable;
/*     */   private JBScrollPane myFlameScroll;
/*     */   private DataProviderPanel myWrapper;
/*     */   private EventsStripe myEventsStripe;
/*     */   private ListSelectionListener myStackTraceSelectionListener;
/*     */   private final FlameChartViewUpdater myViewUpdater;
/*     */   
/*     */   public FlameChartView(@NotNull Project project, @NotNull V8LogCachingReader reader, Consumer<String> notificator, @Nullable Long leftTs, @Nullable Long rightTs, @NotNull FlameChartParameters parameters, @Nls String description, @NotNull V8CpuViewCallback viewCallback) throws IOException {
/*  73 */     this.myProject = project;
/*  74 */     this.myReader = reader;
/*  75 */     this.myNotificator = notificator;
/*  76 */     this.myDescription = description;
/*  77 */     this.myViewCallback = viewCallback;
/*  78 */     this.myViewUpdater = new FlameChartViewUpdater(this, viewCallback, notificator, parameters);
/*  79 */     Disposable disposable = () -> { 
/*  80 */       }; reader.getResources().register(() -> Disposer.dispose(disposable));
/*  81 */     createUI(leftTs, rightTs);
/*  82 */     this.myViewUpdater.overviewSelectionChanged();
/*     */   }
/*     */   
/*     */   public void updateStackTraceTable(@NotNull V8StackTableModel model) {
/*  86 */     if (model == null) $$$reportNull$$$0(4);  this.myStackTraceTable.getSelectionModel().removeListSelectionListener(this.myStackTraceSelectionListener);
/*  87 */     this.myStackTraceTable.setModel((TableModel)model);
/*  88 */     V8Utils.adjustTableColumnWidths(this.myStackTraceTable);
/*  89 */     this.myStackTraceTable.tableChanged(new TableModelEvent((TableModel)model));
/*  90 */     setSelectedRowInTableByChartImpl();
/*     */     
/*  92 */     this.myStackTraceTable.revalidate();
/*  93 */     this.myStackTraceTable.repaint();
/*     */   }
/*     */   
/*     */   public void setSelectedRowInTableByChart() {
/*  97 */     this.myStackTraceTable.getSelectionModel().removeListSelectionListener(this.myStackTraceSelectionListener);
/*  98 */     setSelectedRowInTableByChartImpl();
/*     */     
/* 100 */     this.myStackTraceTable.revalidate();
/* 101 */     this.myStackTraceTable.repaint();
/*     */   }
/*     */   
/*     */   private void setSelectedRowInTableByChartImpl() {
/* 105 */     Integer selectedInChart = this.myFlameChart.getSelectedInChartRow();
/* 106 */     if (selectedInChart != null && this.myStackTraceTable.getRowCount() > 0) {
/* 107 */       int rowInTable = this.myStackTraceTable.getRowCount() - selectedInChart.intValue() - 1;
/* 108 */       if (rowInTable >= 0 && rowInTable < this.myStackTraceTable.getRowCount()) {
/* 109 */         this.myStackTraceTable.setRowSelectionInterval(rowInTable, rowInTable);
/* 110 */         ScrollingUtil.ensureIndexIsVisible((JTable)this.myStackTraceTable, rowInTable, 1);
/*     */       } 
/*     */     } 
/* 113 */     if (this.myStackTraceTable.getSelectedRowCount() == 0 && this.myStackTraceTable.getRowCount() > 0) {
/* 114 */       this.myStackTraceTable.setRowSelectionInterval(0, 0);
/* 115 */       ScrollingUtil.ensureIndexIsVisible((JTable)this.myStackTraceTable, 0, 1);
/*     */     } 
/* 117 */     addStackTraceSelectionListener();
/*     */   }
/*     */   
/*     */   @NotNull
/*     */   public V8StackTableModel createStackTableModel(int index) throws IOException {
/* 122 */     return new V8StackTableModel(this.myReader, getStackLines(index), this.myReader.getDurationList(index));
/*     */   }
/*     */   
/*     */   private List<V8CpuLogCall> getStackLines(int index) throws IOException {
/* 126 */     if (index == -1) return Collections.emptyList(); 
/* 127 */     List<V8CpuLogCall> lines = new ArrayList<>();
/* 128 */     List<Long> stackIds = this.myReader.getStackForTsIdx(index);
/* 129 */     for (Long id : stackIds) {
/* 130 */       String line = this.myReader.getStringById(id.longValue());
/* 131 */       lines.add(V8CpuLogCall.create(StringUtil.notNullize(line), id.longValue()));
/*     */     } 
/* 133 */     return lines;
/*     */   }
/*     */   
/*     */   private void createUI(Long leftTs, Long rightTs) throws IOException {
/* 137 */     this.myChart = new V8CpuOverviewChart(0, 0, 0, 50, this.myReader, (leftTs == null) ? -1L : leftTs.longValue(), (rightTs == null) ? -1L : rightTs.longValue())
/*     */       {
/*     */         protected void selectionUpdated() {
/* 140 */           FlameChartView.this.myViewUpdater.overviewSelectionChanged();
/*     */         }
/*     */       };
/* 143 */     this.myFlameChart = new V8CpuFlameChart(0, 0, 2, this.myReader, this.myNotificator, leftTs, rightTs, this.myViewCallback)
/*     */       {
/*     */         protected void detailsChanged() {
/* 146 */           super.detailsChanged();
/* 147 */           FlameChartView.this.myStackTraceTable.getSelectionModel().removeListSelectionListener(FlameChartView.this.myStackTraceSelectionListener);
/* 148 */           FlameChartView.this.myViewUpdater.detailsLineChanged();
/* 149 */           if (FlameChartView.this.myEventsStripe != null) {
/* 150 */             FlameChartView.this.myEventsStripe.detailsChanged();
/*     */           }
/*     */         }
/*     */       };
/* 154 */     JPanel wrapper = new JPanel(new BorderLayout());
/* 155 */     JPanel innerWrapper = new JPanel(new BorderLayout());
/* 156 */     innerWrapper.add(this.myChart, "North");
/* 157 */     this.myFlameScroll = new JBScrollPane(this.myFlameChart, 22, 30);
/* 158 */     this.myEventsStripe = new EventsStripe(0, UIUtil.getScrollBarWidth(), this.myReader, this.myNotificator, this.myFlameChart);
/*     */     
/* 160 */     this.myChart.setBorder(JBUI.Borders.customLine(OnePixelDivider.BACKGROUND, 1, 1, 1, 1));
/* 161 */     this.myFlameChart.setBorder(JBUI.Borders.customLine(OnePixelDivider.BACKGROUND, 0, 1, 0, 0));
/* 162 */     this.myFlameScroll.getVerticalScrollBar().setBorder(JBUI.Borders.customLine(OnePixelDivider.BACKGROUND, 0, 0, 0, 1));
/* 163 */     this.myEventsStripe.setBorder(JBUI.Borders.customLine(OnePixelDivider.BACKGROUND, 0, 1, 0, 1));
/* 164 */     UiNotifyConnector.doWhenFirstShown((JComponent)this.myFlameScroll, () -> {
/*     */           int diff = this.myFlameScroll.getWidth() - this.myFlameChart.getWidth();
/*     */           
/*     */           this.myEventsStripe.setRight(diff);
/*     */           addStackTraceSelectionListener();
/*     */         });
/* 170 */     JPanel legend = new JPanel()
/*     */       {
/*     */         private final int myHeight;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */         
/*     */         public Dimension getPreferredSize() {
/* 181 */           Dimension dimension = super.getPreferredSize();
/* 182 */           return new Dimension(dimension.width, this.myHeight * 2);
/*     */         }
/*     */ 
/*     */         
/*     */         public void paint(Graphics g) {
/* 187 */           super.paint(g);
/* 188 */           EventsStripe.drawLegend((Graphics2D)g);
/* 189 */           paintBorder(g);
/*     */         }
/*     */       };
/* 192 */     legend.setBorder(JBUI.Borders.customLine(OnePixelDivider.BACKGROUND, 0, 1, 1, 1));
/* 193 */     innerWrapper.add(legend, "Center");
/* 194 */     innerWrapper.add(this.myEventsStripe, "South");
/*     */     
/* 196 */     wrapper.add(innerWrapper, "North");
/* 197 */     this.myFlameChart.setParentScroll(this.myFlameScroll);
/* 198 */     wrapper.add((Component)this.myFlameScroll, "Center");
/* 199 */     this.myStackTraceTable = new StackTraceTable(this.myProject, new V8StackTableModel(this.myReader, Collections.emptyList(), Collections.emptyList()));
/* 200 */     this.myStackTraceTable.getTableHeader().setReorderingAllowed(false);
/* 201 */     Splitter splitter = new Splitter(false, 0.7F);
/* 202 */     splitter.setFirstComponent(wrapper);
/* 203 */     JBScrollPane stackScroll = new JBScrollPane((Component)this.myStackTraceTable);
/* 204 */     splitter.setSecondComponent((JComponent)stackScroll);
/*     */     
/* 206 */     V8Utils.LightweightEditSourceAction editSourceAction = new V8Utils.LightweightEditSourceAction((JComponent)this.myStackTraceTable);
/* 207 */     DefaultActionGroup popupGroup = new DefaultActionGroup();
/* 208 */     popupGroup.add((AnAction)editSourceAction);
/* 209 */     addNavigation(popupGroup);
/* 210 */     popupGroup.add((AnAction)new ShowAsStackTraceAction(this.myProject, this.myStackTraceTable));
/* 211 */     PopupHandler.installPopupHandler((JComponent)this.myStackTraceTable, (ActionGroup)popupGroup, "V8_CPU_PROFILING_POPUP", ActionManager.getInstance());
/*     */     
/* 213 */     this.myWrapper = new DataProviderPanel(new BorderLayout());
/* 214 */     this.myWrapper.register(V8Utils.DETAILS_POSITION.getName(), () -> this.myFlameChart.getDetailsPosition().getFirst());
/* 215 */     this.myWrapper.add((Component)splitter, "Center");
/*     */     
/* 217 */     this.myStackTraceSelectionListener = new ListSelectionListener()
/*     */       {
/*     */         public void valueChanged(ListSelectionEvent e) {
/* 220 */           FlameChartView.this.myFlameChart.setSelectedCells(FlameChartView.this.myStackTraceTable.getModel().getRowCount(), FlameChartView.this.myStackTraceTable.getSelectedRows());
/* 221 */           FlameChartView.this.myFlameChart.revalidate();
/* 222 */           FlameChartView.this.myFlameChart.repaint();
/*     */         }
/*     */       };
/*     */   }
/*     */   
/*     */   private void addStackTraceSelectionListener() {
/* 228 */     this.myStackTraceTable.getSelectionModel().addListSelectionListener(this.myStackTraceSelectionListener);
/*     */   }
/*     */ 
/*     */   
/*     */   public JComponent getMainComponent() {
/* 233 */     return (JComponent)this.myWrapper;
/*     */   }
/*     */ 
/*     */   
/*     */   public String getName() {
/* 238 */     return FLAME_CHART.get();
/*     */   }
/*     */ 
/*     */   
/*     */   public void addActions(DefaultActionGroup group) {
/* 243 */     group.add((AnAction)new ZoomFlameChartAnAction(this.myProject, this.myReader, this.myDescription, this.myNotificator, this, this.myViewCallback));
/* 244 */     addNavigation(group);
/*     */   }
/*     */   
/*     */   private void addNavigation(DefaultActionGroup group) {
/* 248 */     V8CpuNavigationAction navigationAction = new V8CpuNavigationAction();
/* 249 */     group.add((AnAction)navigationAction);
/* 250 */     navigationAction.getGroup().add(new NavigateToStatisticsAction(this.myReader, this.myViewCallback, this.myFlameChart, this.myStackTraceTable, this.myNotificator, TopCallsV8ProfilingComponent.TOP_CALLS.get()));
/* 251 */     navigationAction.getGroup().add(new NavigateToStatisticsAction(this.myReader, this.myViewCallback, this.myFlameChart, this.myStackTraceTable, this.myNotificator, V8ProfilingCallTreeComponent.BOTTOM_UP.get()));
/* 252 */     navigationAction.getGroup().add(new NavigateToStatisticsAction(this.myReader, this.myViewCallback, this.myFlameChart, this.myStackTraceTable, this.myNotificator, V8ProfilingCallTreeComponent.TOP_DOWN.get()));
/* 253 */     V8NavigateToFlameChartIntervalAction.addToGroup(navigationAction.getGroup(), this.myReader, this.myNotificator);
/*     */   }
/*     */ 
/*     */   
/*     */   public String getError() {
/* 258 */     return null;
/*     */   }
/*     */ 
/*     */   
/*     */   @Nullable
/*     */   public V8ProfilingCallTreeTable getTreeTable() {
/* 264 */     return null;
/*     */   }
/*     */ 
/*     */   
/*     */   public void defaultExpand() {}
/*     */ 
/*     */   
/*     */   private static class NavigateToStatisticsAction
/*     */     extends AnAction
/*     */   {
/*     */     private final V8LogCachingReader myReader;
/*     */     
/*     */     private final V8CpuViewCallback myCallback;
/*     */     
/*     */     private final V8CpuFlameChart myFlameChart;
/*     */     private final StackTraceTable myStackTraceTable;
/*     */     private final Consumer<String> myNotificator;
/*     */     @NotNull
/*     */     private final String myTabName;
/*     */     
/*     */     NavigateToStatisticsAction(V8LogCachingReader reader, V8CpuViewCallback callback, V8CpuFlameChart flameChart, StackTraceTable stackTraceTable, Consumer<String> notificator, @NotNull String tabName) {
/* 285 */       super(NodeJSBundle.message("action.navigate.in.text", new Object[] { tabName }), NodeJSBundle.message("action.navigate.in.text", new Object[] { tabName }), NodeJSIcons.Navigate_inMainTree);
/*     */       
/* 287 */       this.myReader = reader;
/* 288 */       this.myCallback = callback;
/* 289 */       this.myFlameChart = flameChart;
/* 290 */       this.myStackTraceTable = stackTraceTable;
/* 291 */       this.myNotificator = notificator;
/* 292 */       this.myTabName = tabName;
/*     */     }
/*     */ 
/*     */     
/*     */     public void actionPerformed(@NotNull AnActionEvent e) {
/* 297 */       if (e == null) $$$reportNull$$$0(1);  Pair<Long, Integer> position = this.myFlameChart.getDetailsPosition();
/* 298 */       Integer stackTraceIndex = (Integer)position.getSecond();
/* 299 */       if (stackTraceIndex == null || stackTraceIndex.intValue() < 0)
/* 300 */         return;  Integer row = this.myFlameChart.getSelectedInChartRow();
/* 301 */       if (row == null || row.intValue() < 0)
/* 302 */         return;  int rowInTable = this.myStackTraceTable.getRowCount() - row.intValue() - 1;
/*     */       
/* 304 */       List<Long> stackIds = getStack(stackTraceIndex);
/* 305 */       if (stackIds == null)
/* 306 */         return;  Point onScreen = this.myStackTraceTable.getLocationOnScreen();
/* 307 */       if (((String)TopCallsV8ProfilingComponent.TOP_CALLS.get()).equals(this.myTabName)) {
/* 308 */         this.myCallback.navigateToTopCalls(stackIds.get(rowInTable), onScreen);
/* 309 */       } else if (((String)V8ProfilingCallTreeComponent.BOTTOM_UP.get()).equals(this.myTabName)) {
/* 310 */         stackIds = ContainerUtil.getFirstItems(stackIds, rowInTable + 1);
/* 311 */         this.myCallback.navigateToBottomUp(stackIds, onScreen);
/*     */       } else {
/* 313 */         stackIds = stackIds.subList(rowInTable, stackIds.size());
/* 314 */         this.myCallback.navigateToTopDown(stackIds, onScreen);
/*     */       } 
/*     */     }
/*     */     
/*     */     @Nullable
/*     */     private List<Long> getStack(Integer stackTraceIndex) {
/*     */       List<Long> stackIds;
/*     */       try {
/* 322 */         stackIds = this.myReader.getStackForTsIdx(stackTraceIndex.intValue());
/*     */       }
/* 324 */       catch (IOException e1) {
/* 325 */         this.myNotificator.consume("Can not navigate to statistics tree: " + e1.getMessage());
/* 326 */         return null;
/*     */       } 
/* 328 */       return stackIds;
/*     */     }
/*     */ 
/*     */     
/*     */     public void update(@NotNull AnActionEvent e) {
/* 333 */       if (e == null) $$$reportNull$$$0(2);  Pair<Long, Integer> position = this.myFlameChart.getDetailsPosition();
/* 334 */       Integer stackTraceIndex = (Integer)position.getSecond();
/* 335 */       Integer row = this.myFlameChart.getSelectedInChartRow();
/* 336 */       e.getPresentation().setEnabled((stackTraceIndex != null && stackTraceIndex.intValue() >= 0 && row != null && row.intValue() >= 0));
/*     */     }
/*     */   }
/*     */   
/*     */   private static class ShowAsStackTraceAction extends DumbAwareAction {
/*     */     private final Project myProject;
/*     */     private final StackTraceTable myTable;
/*     */     
/*     */     ShowAsStackTraceAction(Project project, StackTraceTable table) {
/* 345 */       super(NodeJSBundle.message("action.ShowAsStackTraceAction.show.as.stacktrace.text", new Object[0]));
/* 346 */       this.myProject = project;
/* 347 */       this.myTable = table;
/*     */     }
/*     */ 
/*     */     
/*     */     public void update(@NotNull AnActionEvent e) {
/* 352 */       if (e == null) $$$reportNull$$$0(0);  e.getPresentation().setEnabled(!this.myTable.isEmpty());
/*     */     }
/*     */ 
/*     */     
/*     */     public void actionPerformed(@NotNull AnActionEvent e) {
/* 357 */       if (e == null) $$$reportNull$$$0(1);  if (this.myTable.isEmpty())
/* 358 */         return;  V8StackTableModel model = (V8StackTableModel)this.myTable.getModel();
/* 359 */       String title = NodeJSBundle.message("profile.FlameChartView.stacktrace.tab.title", new Object[0]);
/* 360 */       StringBuilder sb = new StringBuilder(title);
/* 361 */       for (int i = 0; i < model.getRowCount(); i++) {
/* 362 */         V8CpuLogCall call = model.getCall(i);
/* 363 */         String place = (call.getDescriptor() == null) ? call.getNotParsedCallable() : call.getDescriptor().getLink();
/* 364 */         sb.append("\n\tat ").append(call.getFunctionName()).append(" (").append(place).append(")");
/*     */       } 
/* 366 */       AnalyzeStacktraceUtil.addConsole(this.myProject, null, "<" + title + ">", sb.toString());
/*     */     }
/*     */   }
/*     */   
/*     */   public V8CpuFlameChart getFlameChart() {
/* 371 */     return this.myFlameChart;
/*     */   }
/*     */   
/*     */   public V8CpuOverviewChart getOverviewChart() {
/* 375 */     return this.myChart;
/*     */   }
/*     */   
/*     */   public StackTraceTable getStackTraceTable() {
/* 379 */     return this.myStackTraceTable;
/*     */   }
/*     */   
/*     */   public EventsStripe getEventsStripe() {
/* 383 */     return this.myEventsStripe;
/*     */   }
/*     */   
/*     */   public FlameChartViewUpdater getViewUpdater() {
/* 387 */     return this.myViewUpdater;
/*     */   }
/*     */ }


/* Location:              C:\Program Files\JetBrains\WebStorm 2021.1\plugins\NodeJS\lib\NodeJS.jar!\com\jetbrains\nodejs\run\profile\cpu\v8lo\\ui\FlameChartView.class
 * Java compiler version: 11 (55.0)
 * JD-Core Version:       1.1.3
 */