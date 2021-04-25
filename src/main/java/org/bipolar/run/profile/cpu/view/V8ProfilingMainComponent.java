/*     */ package org.bipolar.run.profile.cpu.view;
/*     */ 
/*     */ import com.intellij.execution.ui.layout.impl.JBRunnerTabs;
/*     */ import com.intellij.icons.AllIcons;
/*     */ import com.intellij.openapi.Disposable;
/*     */ import com.intellij.openapi.actionSystem.ActionGroup;
/*     */ import com.intellij.openapi.actionSystem.ActionManager;
/*     */ import com.intellij.openapi.actionSystem.ActionToolbar;
/*     */ import com.intellij.openapi.actionSystem.AnAction;
/*     */ import com.intellij.openapi.actionSystem.AnActionEvent;
/*     */ import com.intellij.openapi.actionSystem.DefaultActionGroup;
/*     */ import com.intellij.openapi.project.DumbAwareAction;
/*     */ import com.intellij.openapi.project.Project;
/*     */ import com.intellij.openapi.ui.MultiLineLabelUI;
/*     */ import com.intellij.openapi.util.Disposer;
/*     */ import com.intellij.openapi.util.Getter;
/*     */ import com.intellij.openapi.util.NlsContexts.TabTitle;
/*     */ import com.intellij.openapi.wm.ToolWindow;
/*     */ import com.intellij.openapi.wm.ToolWindowAnchor;
/*     */ import com.intellij.openapi.wm.ToolWindowManager;
/*     */ import com.intellij.ui.JBColor;
/*     */ import com.intellij.ui.components.JBLabel;
/*     */ import com.intellij.ui.components.JBScrollPane;
/*     */ import com.intellij.ui.content.Content;
/*     */ import com.intellij.ui.content.ContentManager;
/*     */ import com.intellij.ui.tabs.JBTabs;
/*     */ import com.intellij.ui.tabs.TabInfo;
/*     */ import com.intellij.ui.tabs.UiDecorator;
/*     */ import com.intellij.ui.treeStructure.treetable.TreeTable;
/*     */ import com.intellij.util.ui.FormBuilder;
/*     */ import com.intellij.util.ui.JBUI;
/*     */ import org.bipolar.NodeJSBundle;
/*     */ import java.awt.BorderLayout;
/*     */ import java.awt.Color;
/*     */ import java.awt.Insets;
/*     */ import java.util.ArrayList;
/*     */ import java.util.List;
/*     */ import java.util.Objects;
/*     */
/*     */ import javax.swing.Icon;
/*     */ import javax.swing.JComponent;
/*     */ import javax.swing.JPanel;
/*     */ import javax.swing.plaf.LabelUI;
/*     */
import org.jetbrains.annotations.Nls;
/*     */ import org.jetbrains.annotations.NotNull;
/*     */ import org.jetbrains.annotations.Nullable;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class V8ProfilingMainComponent<T extends TreeTable>
/*     */ {
/*     */   private final String myToolWindow;
/*     */   private final int myNumPinned;
/*     */   private final ViewCreatorPartner<T> myPartner;
/*     */   @NotNull
/*     */   private final Disposable myDisposable;
/*     */   @NotNull
/*     */   private final Project myProject;
/*     */   @NotNull
/*     */   private final AnAction myCloseMe;
/*     */   private final String myName;
/*     */   private JComponent myComponent;
/*     */   private JBTabs myPane;
/*     */   private final List<ProfilingView<T>> myViews;
/*     */   
/*     */   protected V8ProfilingMainComponent(String name, @NotNull AnAction closeMe, @NotNull Project project, String toolWindow, int numPinned, @NotNull ViewCreatorPartner<T> partner, @NotNull Disposable disposable) {
/*  72 */     this.myName = name;
/*  73 */     this.myCloseMe = closeMe;
/*  74 */     this.myProject = project;
/*  75 */     this.myToolWindow = toolWindow;
/*  76 */     this.myNumPinned = numPinned;
/*  77 */     this.myPartner = partner;
/*  78 */     this.myDisposable = disposable;
/*  79 */     this.myViews = new ArrayList<>(numPinned);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static <T extends TreeTable> Content showMe(Project project, @TabTitle String tabName, String toolWindow, @NotNull Icon icon, int numPinned, @NotNull final ViewCreatorPartner<T> partner, @Nullable Disposable disposable, @Nullable @Nls String description, @Nullable Icon contentIcon) {
/*  90 */     if (icon == null) $$$reportNull$$$0(4);  if (partner == null) $$$reportNull$$$0(5);  ToolWindow window = getToolWindow(project, toolWindow, icon);
/*  91 */     ContentManager cm = window.getContentManager();
/*  92 */     Content[] content = new Content[1];
/*     */     
/*  94 */     String name = findContentName(tabName, cm);
/*     */     
/*  96 */     if (disposable == null) {
/*  97 */       disposable = new Disposable()
/*     */         {
/*     */           public void dispose() {
/* 100 */             partner.close();
/*     */           }
/*     */         };
/*     */     }
/*     */     
/* 105 */     final V8ProfilingMainComponent<T> component = new V8ProfilingMainComponent<>(name, (AnAction)createCloseAction(cm, () -> content[0]), project, toolWindow, numPinned, partner, disposable);
/*     */     
/* 107 */     partner.announceController(new MyController<T>()
/*     */         {
/*     */           public void autoExpand()
/*     */           {
/* 111 */             component.autoExpand();
/*     */           }
/*     */ 
/*     */           
/*     */           public void showTab(@NotNull String name) {
/* 116 */             if (name == null) $$$reportNull$$$0(0);  component.showTabImpl(name);
/*     */           }
/*     */ 
/*     */           
/*     */           public ProfilingView<T> getView(@NotNull String name) {
/* 121 */             if (name == null) $$$reportNull$$$0(1);  return component.getView(name);
/*     */           }
/*     */         });
/* 124 */     JComponent view = component.createView(partner);
/*     */     
/* 126 */     content[0] = cm.getFactory().createContent(view, name, false);
/* 127 */     if (description != null) {
/* 128 */       content[0].setDescription(description);
/*     */     }
/* 130 */     content[0].putUserData(ToolWindow.SHOW_CONTENT_ICON, Boolean.TRUE);
/* 131 */     content[0].setIcon(contentIcon);
/*     */     
/* 133 */     cm.addContent(content[0]);
/* 134 */     Disposer.register((Disposable)content[0], disposable);
/* 135 */     window.activate(() -> { component.autoExpand(); cm.setSelectedContent(content[0], true); }true);
/*     */ 
/*     */ 
/*     */     
/* 139 */     return content[0];
/*     */   }
/*     */   
/*     */   @NotNull
/*     */   public static ToolWindow getToolWindow(Project project, String toolWindow, @NotNull Icon icon) {
/* 144 */     if (icon == null) $$$reportNull$$$0(6);  ToolWindowManager toolWindowManager = ToolWindowManager.getInstance(project);
/* 145 */     ToolWindow window = toolWindowManager.getToolWindow(toolWindow);
/* 146 */     if (window == null) {
/* 147 */       window = toolWindowManager.registerToolWindow(toolWindow, true, ToolWindowAnchor.BOTTOM);
/* 148 */       window.installWatcher(window.getContentManager());
/* 149 */       window.setIcon(icon);
/*     */     } 
/* 151 */     if (window == null) $$$reportNull$$$0(7);  return window;
/*     */   }
/*     */   
/*     */   private void autoExpand() {
/* 155 */     for (ProfilingView<T> view : this.myViews) {
/* 156 */       view.defaultExpand();
/*     */     }
/*     */   }
/*     */   
/*     */   private JComponent createView(ViewCreatorPartner<T> partner) {
/* 161 */     String error = partner.errorCreated();
/* 162 */     if (error != null) return createErrorComponent(error);
/*     */     
/* 164 */     this.myPane = (JBTabs)JBRunnerTabs.create(this.myProject, this.myDisposable);
/* 165 */     this.myPane.getPresentation().setInnerInsets(new Insets(0, 0, 0, 0)).setPaintBorder(0, 0, 0, 0).setActiveTabFillIn((Color)JBColor.GRAY)
/* 166 */       .setUiDecorator(new UiDecorator()
/*     */         {
/*     */           @NotNull
/*     */           public UiDecorator.UiDecoration getDecoration() {
/* 170 */             return new UiDecorator.UiDecoration(null, (Insets)JBUI.insets(4));
/*     */           }
/*     */         });
/* 173 */     partner.addViews(this.myProject, this.myViews, this.myDisposable);
/* 174 */     for (ProfilingView<T> view : this.myViews) {
/* 175 */       String viewError = view.getError();
/* 176 */       if (viewError != null) {
/* 177 */         this.myPane.addTab((new TabInfo(wrapWithActions((JComponent)new JBScrollPane(createError(viewError)), createOnlyCloseGroup()))).setText(view.getName())); continue;
/*     */       } 
/* 179 */       this.myPane.addTab((new TabInfo(partner.wrapWithStandardActions(view, this.myCloseMe))).setText(view.getName()));
/*     */     } 
/*     */ 
/*     */     
/* 183 */     this.myComponent = this.myPane.getComponent();
/* 184 */     return this.myComponent;
/*     */   }
/*     */   
/*     */   public ProfilingView<T> getView(@NotNull String name) {
/* 188 */     if (name == null) $$$reportNull$$$0(8);  for (ProfilingView<T> view : this.myViews) {
/* 189 */       if (name.equals(view.getName())) {
/* 190 */         return view;
/*     */       }
/*     */     } 
/* 193 */     return null;
/*     */   }
/*     */   
/*     */   public void showTabImpl(@NotNull String name) {
/* 197 */     if (name == null) $$$reportNull$$$0(9);  for (int i = 0; i < this.myPane.getTabCount(); i++) {
/* 198 */       TabInfo at = this.myPane.getTabAt(i);
/* 199 */       if (name.equals(at.getText())) {
/* 200 */         this.myPane.select(at, true);
/*     */         return;
/*     */       } 
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private static DumbAwareAction createCloseAction(final ContentManager cm, final Getter<Content> content) {
/* 213 */     return new DumbAwareAction(NodeJSBundle.messagePointer("action.DumbAware.V8ProfilingMainComponent.text.close", new Object[0]),
/* 214 */         NodeJSBundle.messagePointer("action.DumbAware.V8ProfilingMainComponent.description.close", new Object[0]), AllIcons.Actions.Cancel)
/*     */       {
/*     */         public void actionPerformed(@NotNull AnActionEvent e)
/*     */         {
/* 218 */           if (e == null) $$$reportNull$$$0(0);  cm.removeContent((Content)content.get(), true);
/*     */         }
/*     */ 
/*     */         
/*     */         public void update(@NotNull AnActionEvent e) {
/* 223 */           if (e == null) $$$reportNull$$$0(1);  e.getPresentation().setEnabled(true);
/*     */         }
/*     */       };
/*     */   }
/*     */   @TabTitle
/*     */   private static String findContentName(@TabTitle String tabName, ContentManager cm) {
/* 229 */     String name = tabName;
/* 230 */     int cnt = 1;
/* 231 */     for (Content content : cm.getContents()) {
/* 232 */       if (Objects.equals(content.getDisplayName(), name)) {
/* 233 */         name = tabName + " (" + tabName + ")";
/* 234 */         cnt++;
/*     */       } 
/*     */     } 
/* 237 */     return name;
/*     */   }
/*     */   
/*     */   protected static JComponent wrapWithActions(@NotNull JComponent pane, @NotNull DefaultActionGroup group) {
/* 241 */     if (pane == null) $$$reportNull$$$0(10);  if (group == null) $$$reportNull$$$0(11);  JPanel mainPane = new JPanel(new BorderLayout());
/* 242 */     mainPane.add(pane, "Center");
/* 243 */     ActionToolbar leftToolbar = ActionManager.getInstance().createActionToolbar("V8 profiling", (ActionGroup)group, false);
/* 244 */     mainPane.add(leftToolbar.getComponent(), "West");
/* 245 */     return mainPane;
/*     */   }
/*     */   
/*     */   protected JComponent createErrorComponent(@NotNull @Nls String text) {
/* 249 */     if (text == null) $$$reportNull$$$0(12);  return wrapWithActions(createError(text), createOnlyCloseGroup());
/*     */   }
/*     */   
/*     */   private DefaultActionGroup createOnlyCloseGroup() {
/* 253 */     DefaultActionGroup group = new DefaultActionGroup();
/* 254 */     group.add(this.myCloseMe);
/* 255 */     return group;
/*     */   }
/*     */   
/*     */   static JPanel createError(@NotNull @Nls String text) {
/* 259 */     if (text == null) $$$reportNull$$$0(13);  FormBuilder builder = new FormBuilder();
/* 260 */     JBLabel component = new JBLabel(text);
/* 261 */     component.setUI((LabelUI)new MultiLineLabelUI());
/* 262 */     builder.addComponent((JComponent)component);
/* 263 */     return builder.getPanel();
/*     */   }
/*     */   
/*     */   public static interface MyController<T extends TreeTable> {
/*     */     void autoExpand();
/*     */     
/*     */     void showTab(@NotNull String param1String);
/*     */     
/*     */     ProfilingView<T> getView(@NotNull String param1String);
/*     */   }
/*     */ }


/* Location:              C:\Program Files\JetBrains\WebStorm 2021.1\plugins\NodeJS\lib\NodeJS.jar!\com\jetbrains\nodejs\run\profile\cpu\view\V8ProfilingMainComponent.class
 * Java compiler version: 11 (55.0)
 * JD-Core Version:       1.1.3
 */