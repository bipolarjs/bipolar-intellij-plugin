/*     */ package org.bipolar.run.profile.cpu.view;
/*     */ 
/*     */ import com.intellij.execution.ui.layout.impl.JBRunnerTabs;
/*     */ import com.intellij.openapi.Disposable;
/*     */ import com.intellij.openapi.actionSystem.ActionGroup;
/*     */
/*     */ import com.intellij.openapi.actionSystem.AnAction;
/*     */ import com.intellij.openapi.actionSystem.AnActionEvent;
/*     */ import com.intellij.openapi.actionSystem.DefaultActionGroup;
/*     */ import com.intellij.openapi.project.DumbAwareAction;
/*     */ import com.intellij.openapi.project.Project;
/*     */ import com.intellij.openapi.ui.Splitter;
/*     */ import com.intellij.openapi.util.Disposer;
/*     */ import com.intellij.openapi.util.NlsContexts.TabTitle;
/*     */ import com.intellij.openapi.util.text.StringUtil;
/*     */ import com.intellij.openapi.wm.IdeFocusManager;
/*     */ import com.intellij.ui.TreeTableSpeedSearch;
/*     */ import com.intellij.ui.components.JBScrollPane;
/*     */ import com.intellij.ui.tabs.JBTabs;
/*     */ import com.intellij.ui.tabs.TabInfo;
/*     */ import com.intellij.ui.treeStructure.treetable.TreeTable;
/*     */ import com.intellij.util.containers.Convertor;
/*     */ import com.intellij.util.ui.UIUtil;
/*     */ import org.bipolar.run.profile.V8Utils;
/*     */ import org.bipolar.run.profile.cpu.v8log.data.V8CpuLogCall;
/*     */ import java.awt.Component;
/*     */ import java.awt.event.MouseAdapter;
/*     */ import java.awt.event.MouseEvent;
/*     */ import java.util.HashSet;
/*     */ import java.util.List;
/*     */ import java.util.Set;
/*     */ import javax.swing.JComponent;
/*     */ import javax.swing.tree.TreePath;
/*     */
import org.jetbrains.annotations.NonNls;
/*     */ import org.jetbrains.annotations.NotNull;
/*     */ import org.jetbrains.annotations.Nullable;
/*     */ 
/*     */ 
/*     */ 
/*     */ public class StatisticsTreeTableWithDetails
/*     */ {
/*     */   @NonNls
/*     */   public static final String V8_CPU_PROFILING_SEARCH_RESULTS_PLACE = "v8CpuProfilingSearchResultsTab";
/*     */   private final Project myProject;
/*     */   @NotNull
/*     */   private final Disposable myDisposable;
/*     */   private final V8ProfilingCallTreeTable myTable;
/*     */   
/*     */   public StatisticsTreeTableWithDetails(Project project, @NotNull V8ProfilingCallTreeTable table, @NotNull Convertor<TreePath, String> searchConvertor, @NotNull Disposable disposable) {
/*  50 */     this.myTable = table;
/*  51 */     this.myProject = project;
/*  52 */     this.myDisposable = disposable;
/*  53 */     this.mySplitter = new Splitter(true);
/*  54 */     this.mySplitter.setFirstComponent((JComponent)new JBScrollPane((Component)this.myTable));
/*  55 */     this.mySpeedSearch = new TreeTableSpeedSearch((TreeTable)this.myTable, searchConvertor);
/*  56 */     this.myKeepTabs = new HashSet<>();
/*     */   }
/*     */   private final TreeTableSpeedSearch mySpeedSearch; private final Splitter mySplitter; private JBTabs myRunnerTabs;
/*     */   private final Set<String> myKeepTabs;
/*     */   
/*     */   public void addDetails(@NotNull JComponent component, @Nullable List<AnAction> actions, @NotNull @TabTitle String title, boolean addRemove) {
/*     */     TabInfo info;
/*  63 */     if (component == null) $$$reportNull$$$0(3);  if (title == null) $$$reportNull$$$0(4);  if (this.mySplitter.getSecondComponent() == null) {
/*  64 */       this.myRunnerTabs = (JBTabs)JBRunnerTabs.create(this.myProject, this.myDisposable);
/*  65 */       this.myRunnerTabs.addTabMouseListener(new MouseAdapter()
/*     */           {
/*     */             public void mousePressed(@NotNull MouseEvent e) {
/*  68 */               if (e == null) $$$reportNull$$$0(0);  if (UIUtil.isCloseClick(e)) {
/*  69 */                 TabInfo tabInfo = StatisticsTreeTableWithDetails.this.myRunnerTabs.findInfo(e);
/*  70 */                 if (tabInfo != null && !StatisticsTreeTableWithDetails.this.myKeepTabs.contains(tabInfo.getText())) {
/*  71 */                   StatisticsTreeTableWithDetails.this.closeTab(tabInfo);
/*     */                 }
/*     */               } 
/*     */             }
/*     */           });
/*  76 */       this.mySplitter.setSecondComponent(this.myRunnerTabs.getComponent());
/*     */     } 
/*  78 */     JBScrollPane scroll = new JBScrollPane(component);
/*     */     
/*  80 */     if (actions != null) {
/*  81 */       DefaultActionGroup group = new DefaultActionGroup();
/*  82 */       group.addAll(actions);
/*  83 */       V8Utils.WithToolbarWrapper wrapper = new V8Utils.WithToolbarWrapper((JComponent)scroll, group);
/*  84 */       info = (new TabInfo(wrapper.getMainPanel())).setText(title);
/*     */     } else {
/*  86 */       info = (new TabInfo((JComponent)scroll)).setText(title);
/*     */     } 
/*  88 */     if (!addRemove) {
/*  89 */       this.myKeepTabs.add(info.getText());
/*     */     }
/*  91 */     this.myRunnerTabs.setPopupGroup((ActionGroup)new DefaultActionGroup(new AnAction[] { (AnAction)new DumbAwareAction()
/*     */             {
/*     */ 
/*     */ 
/*     */ 
/*     */               
/*     */               public void update(@NotNull AnActionEvent e)
/*     */               {
/*  99 */                 if (e == null) $$$reportNull$$$0(0);  TabInfo selectedInfo = StatisticsTreeTableWithDetails.this.myRunnerTabs.getSelectedInfo();
/* 100 */                 e.getPresentation().setEnabled((selectedInfo != null && !StatisticsTreeTableWithDetails.this.myKeepTabs.contains(selectedInfo.getText())));
/*     */               }
/*     */ 
/*     */               
/*     */               public void actionPerformed(@NotNull AnActionEvent e) {
/* 105 */                 if (e == null) $$$reportNull$$$0(1);  StatisticsTreeTableWithDetails.this.closeTab(StatisticsTreeTableWithDetails.this.myRunnerTabs.getSelectedInfo());
/*     */               }
/*     */             },   }, ), "v8CpuProfilingSearchResultsTab", true);
/* 108 */     this.myRunnerTabs.addTab(info);
/* 109 */     this.myRunnerTabs.select(info, true);
/*     */   }
/*     */   
/*     */   private void closeTab(TabInfo tabInfo) {
/* 113 */     this.myRunnerTabs.removeTab(tabInfo);
/* 114 */     if (this.myRunnerTabs.getTabCount() == 0) {
/* 115 */       this.mySplitter.setSecondComponent(null);
/* 116 */       Disposer.dispose((Disposable)this.myRunnerTabs);
/* 117 */       this.myRunnerTabs = null;
/*     */     } 
/*     */   }
/*     */   
/*     */   public JComponent getComponent() {
/* 122 */     return (JComponent)this.mySplitter;
/*     */   }
/*     */   
/*     */   public V8ProfilingCallTreeTable getTable() {
/* 126 */     return this.myTable;
/*     */   }
/*     */   
/*     */   public void showSpeedSearch(@NotNull V8CpuLogCall call) {
/* 130 */     if (call == null) $$$reportNull$$$0(5);  String secondWord = (call.getDescriptor() == null) ? "" : call.getDescriptor().getShortLink();
/* 131 */     String firstWord = StringUtil.notNullize(call.getFunctionName());
/*     */     
/* 133 */     String presentation = (firstWord + " " + firstWord).trim();
/*     */     
/* 135 */     if (this.mySpeedSearch.isPopupActive()) {
/* 136 */       this.mySpeedSearch.hidePopup();
/*     */     }
/* 138 */     this.mySpeedSearch.showPopup(presentation);
/* 139 */     IdeFocusManager.getGlobalInstance().doWhenFocusSettlesDown(() -> IdeFocusManager.getGlobalInstance().requestFocus((Component)this.myTable, true));
/*     */   }
/*     */ }


/* Location:              C:\Program Files\JetBrains\WebStorm 2021.1\plugins\NodeJS\lib\NodeJS.jar!\com\jetbrains\nodejs\run\profile\cpu\view\StatisticsTreeTableWithDetails.class
 * Java compiler version: 11 (55.0)
 * JD-Core Version:       1.1.3
 */