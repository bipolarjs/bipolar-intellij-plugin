/*     */ package org.bipolar.run.profile;
/*     */ import com.intellij.icons.AllIcons;
/*     */ import com.intellij.ide.IdeBundle;
/*     */ import com.intellij.ide.actions.EditSourceAction;
/*     */ import com.intellij.ide.util.DefaultPsiElementCellRenderer;
/*     */ import com.intellij.idea.ActionsBundle;
/*     */ import com.intellij.lang.javascript.psi.JSNamedElementBase;
/*     */ import com.intellij.openapi.actionSystem.ActionGroup;
/*     */ import com.intellij.openapi.actionSystem.ActionManager;
/*     */ import com.intellij.openapi.actionSystem.ActionToolbar;
/*     */ import com.intellij.openapi.actionSystem.AnAction;
/*     */ import com.intellij.openapi.actionSystem.AnActionEvent;
/*     */ import com.intellij.openapi.actionSystem.DataContext;
/*     */ import com.intellij.openapi.actionSystem.DataKey;
/*     */ import com.intellij.openapi.actionSystem.DefaultActionGroup;
/*     */ import com.intellij.openapi.actionSystem.PlatformDataKeys;
/*     */ import com.intellij.openapi.editor.Document;
/*     */ import com.intellij.openapi.fileEditor.OpenFileDescriptor;
/*     */ import com.intellij.openapi.project.DumbAwareAction;
/*     */ import com.intellij.openapi.project.Project;
/*     */ import com.intellij.openapi.ui.popup.IPopupChooserBuilder;
/*     */ import com.intellij.openapi.ui.popup.JBPopup;
/*     */ import com.intellij.openapi.ui.popup.JBPopupFactory;
/*     */ import com.intellij.openapi.util.Comparing;
/*     */ import com.intellij.openapi.util.Pair;
/*     */ import com.intellij.pom.Navigatable;
/*     */ import com.intellij.psi.PsiDocumentManager;
/*     */ import com.intellij.psi.PsiElement;
/*     */ import com.intellij.psi.PsiFile;
/*     */ import com.intellij.ui.ColoredTableCellRenderer;
/*     */ import com.intellij.ui.PopupHandler;
/*     */ import com.intellij.ui.awt.RelativePoint;
/*     */ import com.intellij.ui.treeStructure.treetable.TreeTable;
/*     */ import com.intellij.ui.treeStructure.treetable.TreeTableModel;
/*     */ import com.intellij.ui.treeStructure.treetable.TreeTableTree;
/*     */ import com.intellij.util.Consumer;
/*     */ import com.intellij.util.ui.UIUtil;
/*     */ import com.intellij.util.ui.tree.TreeUtil;
/*     */ import org.bipolar.NodeJSBundle;
/*     */ import org.bipolar.run.profile.cpu.v8log.data.V8CpuLogCall;
/*     */ import org.bipolar.run.profile.cpu.v8log.ui.TableWithFixedWidth;
/*     */ import org.bipolar.run.profile.cpu.view.TreeTableModelWithCustomRenderer;
/*     */ import org.bipolar.run.profile.heap.CompositeCloseable;
/*     */ import org.bipolar.run.profile.heap.V8CachingReader;
/*     */ import org.bipolar.run.profile.heap.view.actions.MarkUnmarkAction;
/*     */ import org.bipolar.run.profile.heap.view.actions.V8NavigateToMainTreeAction;
/*     */ import org.bipolar.run.profile.heap.view.components.V8HeapTreeTable;
/*     */ import org.bipolar.run.profile.heap.view.components.V8MainTreeNavigator;
/*     */ import org.bipolar.run.profile.settings.NodeProfilingSettings;
/*     */ import it.unimi.dsi.fastutil.ints.IntArrayList;
/*     */ import it.unimi.dsi.fastutil.ints.IntList;
/*     */ import java.awt.BorderLayout;
/*     */ import java.awt.Color;
/*     */ import java.awt.Component;
/*     */ import java.awt.Font;
/*     */ import java.awt.FontMetrics;
/*     */ import java.awt.Graphics2D;
/*     */ import java.awt.Paint;
/*     */ import java.awt.Point;
/*     */ import java.awt.Stroke;
/*     */ import java.awt.event.InputEvent;
/*     */ import java.awt.event.MouseEvent;
/*     */ import java.io.IOException;
/*     */ import java.io.ObjectInput;
/*     */ import java.io.ObjectOutput;
/*     */ import java.net.MalformedURLException;
/*     */ import java.net.URI;
/*     */ import java.net.URISyntaxException;
/*     */ import java.net.URL;
/*     */ import java.util.Arrays;
/*     */ import java.util.Comparator;
/*     */ import java.util.Formatter;
/*     */ import java.util.HashMap;
/*     */ import java.util.List;
/*     */ import java.util.Map;
/*     */ import javax.swing.JComponent;
/*     */ import javax.swing.JPanel;
/*     */ import javax.swing.JTable;
/*     */ import javax.swing.JTree;
/*     */ import javax.swing.ListCellRenderer;
/*     */ import javax.swing.plaf.TreeUI;
/*     */ import javax.swing.plaf.basic.BasicTreeUI;
/*     */ import javax.swing.table.TableCellRenderer;
/*     */ import javax.swing.table.TableColumn;
/*     */ import javax.swing.table.TableColumnModel;
/*     */ import javax.swing.table.TableModel;
/*     */ import javax.swing.tree.TreeCellRenderer;
/*     */ import javax.swing.tree.TreePath;
/*     */ import org.jetbrains.annotations.NonNls;
/*     */ import org.jetbrains.annotations.NotNull;
/*     */ import org.jetbrains.annotations.Nullable;
/*     */ 
/*     */ public final class V8Utils {
/*  94 */   public static final DataKey<Long> DETAILS_POSITION = DataKey.create("V8.CPU.DETAILS_POSITION");
/*  95 */   public static final DataKey<Boolean> IS_NAVIGATABLE = DataKey.create("V8.IS.NAVIGATABLE");
/*  96 */   public static final DataKey<Navigatable[]> NAVIGATABLE_ONLY_FOR_ACTION = DataKey.create("V8.NAVIGATABLE_ONLY_FOR_ACTION");
/*  97 */   public static final DataKey<Pair<Integer, Integer>> NAVIGATION_POSITION = DataKey.create("V8.NAVIGATION_POSITION");
/*  98 */   public static final DataKey<V8CpuLogCall> SELECTED_CALL = DataKey.create("V8.SELECTED_CALL"); @NonNls
/*     */   public static final String HEAPSNAPSHOT = "heapsnapshot";
/* 100 */   private static final Navigatable[] ourEmptyNavigatables = new Navigatable[] { new Navigatable()
/*     */       {
/*     */         public void navigate(boolean requestFocus) {}
/*     */ 
/*     */ 
/*     */         
/*     */         public boolean canNavigate() {
/* 107 */           return true;
/*     */         }
/*     */ 
/*     */         
/*     */         public boolean canNavigateToSource() {
/* 112 */           return true;
/*     */         }
/*     */       } };
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static void selectAndNavigate(AnActionEvent e, List<Navigatable> navigatables, Consumer<Navigatable> consumer, @Nullable final Map<Navigatable, String> addText) {
/* 135 */     IPopupChooserBuilder<Navigatable> popupChooserBuilder = JBPopupFactory.getInstance().createPopupChooserBuilder(navigatables).setTitle(NodeJSBundle.message("popup.title.select.file.to.navigate.to", new Object[0])).setRenderer((ListCellRenderer)new DefaultPsiElementCellRenderer() { public String getContainerText(PsiElement element, String name) { String text = super.getContainerText(element, name); String add = (addText == null) ? "" : (String)addText.get(element); return (add == null) ? text : (text + text); } }).setMovable(true).setResizable(true).setCancelKeyEnabled(true).setCancelOnWindowDeactivation(true).setCancelOnClickOutside(true).setItemChosenCallback(consumer);
/* 136 */     JBPopup popup = popupChooserBuilder.createPopup();
/* 137 */     showPopup(e, popup);
/*     */   }
/*     */   
/*     */   public static Point showPopup(AnActionEvent e, JBPopup popup) {
/* 141 */     InputEvent event = e.getInputEvent();
/* 142 */     if (event instanceof MouseEvent) {
/* 143 */       MouseEvent mouseEvent = (MouseEvent)event;
/* 144 */       if (mouseEvent.getXOnScreen() == 0 && mouseEvent.getYOnScreen() == 0) {
/* 145 */         popup.showInBestPositionFor(e.getDataContext());
/* 146 */         return popup.getLocationOnScreen();
/*     */       } 
/* 148 */       popup.show(new RelativePoint(mouseEvent));
/*     */     } else {
/* 150 */       popup.showInBestPositionFor(e.getDataContext());
/*     */     } 
/* 152 */     return popup.getLocationOnScreen();
/*     */   }
/*     */   
/*     */   public static JComponent wrapWithActions(@NotNull JComponent pane, @NotNull DefaultActionGroup group) {
/* 156 */     if (pane == null) $$$reportNull$$$0(0);  if (group == null) $$$reportNull$$$0(1);  return (new WithToolbarWrapper(pane, group)).getMainPanel();
/*     */   }
/*     */   
/*     */   public static void adjustTableColumnWidths(TableWithFixedWidth table) {
/* 160 */     TableModel model = table.getModel();
/* 161 */     table.setAutoResizeMode(0);
/* 162 */     int columnCount = table.getColumnCount();
/* 163 */     FontMetrics metrics = table.getFontMetrics(table.getTableHeader().getFont());
/* 164 */     int rowCount = model.getRowCount();
/* 165 */     int total = 0;
/* 166 */     for (int i = 0; i < columnCount; i++) {
/* 167 */       TableColumn column = table.getColumnModel().getColumn(i);
/* 168 */       int maxWidth = metrics.stringWidth(column.getHeaderValue().toString()) + 2;
/* 169 */       for (int j = 0; j < rowCount; j++) {
/* 170 */         TableCellRenderer renderer = table.getCellRenderer(j, i);
/* 171 */         renderer.getTableCellRendererComponent((JTable)table, model.getValueAt(j, i), false, false, j, i);
/* 172 */         if (renderer instanceof ColoredTableCellRenderer) {
/* 173 */           maxWidth = Math.max((((ColoredTableCellRenderer)renderer).computePreferredSize(false)).width + 2, maxWidth);
/*     */         }
/*     */       } 
/* 176 */       total += maxWidth;
/* 177 */       if (i == 0) {
/* 178 */         column.setMinWidth(maxWidth);
/*     */       } else {
/* 180 */         fixColumnWidth(maxWidth, column);
/*     */       } 
/*     */     } 
/* 183 */     table.setMinWidth(total);
/*     */   }
/*     */   
/*     */   public static class WithToolbarWrapper {
/*     */     private final JPanel myMainPanel;
/*     */     private final ActionToolbar myToolbar;
/*     */     
/*     */     public WithToolbarWrapper(@NotNull JComponent pane, @NotNull DefaultActionGroup group) {
/* 191 */       this.myMainPanel = new JPanel(new BorderLayout());
/* 192 */       this.myMainPanel.add(pane, "Center");
/* 193 */       this.myToolbar = ActionManager.getInstance().createActionToolbar("V8 profiling", (ActionGroup)group, false);
/* 194 */       this.myMainPanel.add(this.myToolbar.getComponent(), "West");
/*     */     }
/*     */     
/*     */     public JPanel getMainPanel() {
/* 198 */       return this.myMainPanel;
/*     */     }
/*     */     
/*     */     public ActionToolbar getToolbar() {
/* 202 */       return this.myToolbar;
/*     */     }
/*     */   }
/*     */   
/*     */   public static void collapseAll(TreeTable treeTable) {
/* 207 */     TreeTableTree tree = treeTable.getTree();
/* 208 */     TreePath leadSelectionPath = tree.getLeadSelectionPath();
/*     */     
/* 210 */     collapseAllBare(treeTable);
/* 211 */     Object root = tree.getModel().getRoot();
/* 212 */     tree.expandPath(new TreePath(root));
/* 213 */     if (leadSelectionPath != null && (leadSelectionPath.getPath()).length > 1) {
/* 214 */       TreeUtil.selectPath((JTree)tree, new TreePath(Arrays.copyOf(leadSelectionPath.getPath(), 2)));
/*     */     }
/*     */   }
/*     */   
/*     */   public static void collapseAllBare(TreeTable treeTable) {
/* 219 */     TreeTableTree tree = treeTable.getTree();
/* 220 */     int row = tree.getRowCount() - 1;
/* 221 */     while (row >= 0) {
/* 222 */       tree.collapseRow(row);
/* 223 */       row--;
/*     */     } 
/*     */   }
/*     */   
/*     */   public static void setSmallerTreeIndent(TreeTableTree tree) {
/* 228 */     TreeUI ui = tree.getUI();
/* 229 */     if (ui instanceof BasicTreeUI) {
/* 230 */       BasicTreeUI basicUi = (BasicTreeUI)ui;
/* 231 */       basicUi.setRightChildIndent(UIUtil.getTreeRightChildIndent() / 2);
/*     */     } 
/*     */   }
/*     */   
/*     */   @NotNull
/*     */   public static String formatPercent(int percent) {
/* 237 */     if (percent == 0) return "0%"; 
/* 238 */     if ((new Formatter()).format("%.1f%%", new Object[] { Double.valueOf(percent / 10.0D) }).toString() == null) $$$reportNull$$$0(2);  return (new Formatter()).format("%.1f%%", new Object[] { Double.valueOf(percent / 10.0D) }).toString();
/*     */   }
/*     */   
/*     */   public static int tensPercent(long ticks, long totalTicks) {
/* 242 */     return tensPercent((int)ticks, (int)totalTicks);
/*     */   }
/*     */   
/*     */   public static int tensPercent(int ticks, int totalTicks) {
/* 246 */     return (int)Math.round(ticks * 1000.0D / totalTicks);
/*     */   }
/*     */   
/*     */   public static class LightweightEditSourceAction extends EditSourceAction { private final Component myComponent;
/*     */     
/*     */     public LightweightEditSourceAction(JComponent component) {
/* 252 */       getTemplatePresentation().setIcon(AllIcons.Actions.EditSource);
/* 253 */       getTemplatePresentation().setText(ActionsBundle.actionText("EditSource").replace("_", ""));
/* 254 */       getTemplatePresentation().setDescription(ActionsBundle.actionDescription("EditSource"));
/*     */ 
/*     */ 
/*     */       
/* 258 */       this.myComponent = component;
/* 259 */       registerCustomShortcutSet(ActionManager.getInstance().getAction("EditSource").getShortcutSet(), component);
/*     */     }
/*     */ 
/*     */     
/*     */     public void update(@NotNull AnActionEvent e) {
/* 264 */       if (e == null) $$$reportNull$$$0(0);  super.update(e);
/* 265 */       Project project = (Project)e.getData(PlatformDataKeys.PROJECT);
/* 266 */       boolean enabled = (project != null && Boolean.TRUE.equals(e.getData(V8Utils.IS_NAVIGATABLE)));
/* 267 */       e.getPresentation().setEnabled(enabled);
/* 268 */       if ("V8_CPU_PROFILING_POPUP".equals(e.getPlace())) {
/* 269 */         e.getPresentation().setVisible(enabled);
/*     */       }
/*     */     }
/*     */ 
/*     */     
/*     */     protected Navigatable[] getNavigatables(DataContext dataContext) {
/* 275 */       return V8Utils.ourEmptyNavigatables;
/*     */     }
/*     */     
/*     */     public void actionPerformed(@NotNull AnActionEvent e)
/*     */     {
/* 280 */       if (e == null) $$$reportNull$$$0(1);  Project project = (Project)e.getData(PlatformDataKeys.PROJECT);
/* 281 */       if (project == null)
/* 282 */         return;  Pair<Integer, Integer> position = (Pair<Integer, Integer>)e.getData(V8Utils.NAVIGATION_POSITION);
/*     */       
/* 284 */       Consumer<Navigatable> consumer = item -> {
/*     */           if (item instanceof PsiFile && ((PsiFile)item).getVirtualFile() != null && position != null) {
/*     */             (new OpenFileDescriptor(project, ((PsiFile)item).getVirtualFile(), Math.max(0, ((Integer)position.getFirst()).intValue() - 1), Math.max(0, ((Integer)position.getSecond()).intValue() - 1))).navigate(true);
/*     */             
/*     */             return;
/*     */           } 
/*     */           
/*     */           item.navigate(true);
/*     */         };
/* 293 */       Navigatable[] navigatables = (Navigatable[])e.getData(V8Utils.NAVIGATABLE_ONLY_FOR_ACTION);
/* 294 */       if (navigatables == null || navigatables.length == 0) {
/* 295 */         NodeProfilingSettings.CPU_NOTIFICATION_GROUP
/* 296 */           .createNotification(NodeJSBundle.message("profile.GoToSourceAction.notification.not_found.content", new Object[0]), MessageType.INFO)
/* 297 */           .notify(project);
/*     */         return;
/*     */       } 
/* 300 */       if (navigatables.length == 1) {
/* 301 */         navigatables[0].navigate(true);
/*     */         return;
/*     */       } 
/* 304 */       Map<Navigatable, String> addText = (position == null) ? null : new HashMap<>();
/* 305 */       Navigatable first = null;
/* 306 */       if (position != null) {
/* 307 */         for (Navigatable navigatable : navigatables) {
/* 308 */           if (navigatable instanceof PsiFile) {
/* 309 */             addText.put(navigatable, ":" + position.getFirst() + ":" + position.getSecond());
/* 310 */           } else if (navigatable instanceof PsiElement && ((PsiElement)navigatable).getContainingFile() != null) {
/* 311 */             Document document = PsiDocumentManager.getInstance(project).getDocument(((PsiElement)navigatable).getContainingFile());
/* 312 */             int elemOffset = ((PsiElement)navigatable).getTextOffset();
/* 313 */             int lineNumber = document.getLineNumber(elemOffset);
/* 314 */             int column = elemOffset - document.getLineStartOffset(lineNumber);
/* 315 */             boolean exact = (lineNumber + 1 == ((Integer)position.getFirst()).intValue());
/* 316 */             String comment = ":" + lineNumber + ":" + column;
/* 317 */             if (exact) {
/* 318 */               comment = ":" + lineNumber + 1 + ":" + position.getSecond() + " (exact match)";
/* 319 */               first = navigatable;
/*     */             } 
/* 321 */             addText.put(navigatable, comment);
/*     */           } 
/*     */         } 
/*     */       }
/* 325 */       final Navigatable finalFirst = first;
/* 326 */       Arrays.sort(navigatables, new Comparator<Navigatable>()
/*     */           {
/*     */             public int compare(Navigatable o1, Navigatable o2) {
/* 329 */               if (o1.equals(finalFirst)) return -1; 
/* 330 */               if (o2.equals(finalFirst)) return 1; 
/* 331 */               boolean isFile1 = o1 instanceof PsiFile;
/* 332 */               boolean isFile2 = o2 instanceof PsiFile;
/* 333 */               if (isFile1 != isFile2) {
/* 334 */                 return isFile1 ? 1 : -1;
/*     */               }
/*     */               
/* 337 */               return Comparing.compare(getText(o1), getText(o2));
/*     */             }
/*     */             
/*     */             private String getText(Object o) {
/* 341 */               if (o instanceof JSNamedElementBase) {
/* 342 */                 return ((JSNamedElementBase)o).getName();
/*     */               }
/* 344 */               if (o instanceof PsiFile) {
/* 345 */                 return ((PsiFile)o).getName();
/*     */               }
/* 347 */               return o.toString();
/*     */             }
/*     */           });
/* 350 */       V8Utils.selectAndNavigate(e, Arrays.asList(navigatables), consumer, addText);
/*     */     } } class null implements Comparator<Navigatable> {
/*     */     public int compare(Navigatable o1, Navigatable o2) { if (o1.equals(finalFirst)) return -1;  if (o2.equals(finalFirst)) return 1;  boolean isFile1 = o1 instanceof PsiFile; boolean isFile2 = o2 instanceof PsiFile; if (isFile1 != isFile2) return isFile1 ? 1 : -1;  return Comparing.compare(getText(o1), getText(o2)); } private String getText(Object o) { if (o instanceof JSNamedElementBase)
/*     */         return ((JSNamedElementBase)o).getName();  if (o instanceof PsiFile)
/*     */         return ((PsiFile)o).getName();  return o.toString(); }
/* 355 */   } public static void installHeapPopupMenu(Project project, @NotNull TreeTable treeTable, @NotNull V8CachingReader reader, V8MainTreeNavigator navigator) { if (treeTable == null) $$$reportNull$$$0(3);  if (reader == null) $$$reportNull$$$0(4);  DefaultActionGroup group = new DefaultActionGroup();
/* 356 */     MarkUnmarkAction markUnmarkAction = new MarkUnmarkAction(project, reader);
/* 357 */     markUnmarkAction.setTable(treeTable);
/* 358 */     group.add((AnAction)markUnmarkAction);
/* 359 */     if (navigator != null) {
/* 360 */       V8NavigateToMainTreeAction action = new V8NavigateToMainTreeAction();
/* 361 */       action.setFixedNavigator(navigator);
/* 362 */       action.setTable(treeTable);
/* 363 */       group.add((AnAction)action);
/*     */     } 
/* 365 */     group.add((AnAction)new GoToSourceAction(reader, treeTable));
/*     */     
/* 367 */     PopupHandler.installPopupHandler((JComponent)treeTable, (ActionGroup)group, "V8_HEAP_PROFILING_POPUP", ActionManager.getInstance()); }
/*     */ 
/*     */   
/*     */   public static class ExpandAllAction
/*     */     extends DumbAwareAction
/*     */   {
/*     */     public ExpandAllAction(TreeTableWithTreeWidthController table) {
/* 374 */       super(IdeBundle.messagePointer("action.ExpandAllAction.text.expand.all", new Object[0]), 
/* 375 */           IdeBundle.messagePointer("action.ExpandAllAction.text.expand.all", new Object[0]), AllIcons.Actions.Expandall);
/* 376 */       this.myTable = table;
/* 377 */       if (this.myTable != null)
/* 378 */         registerCustomShortcutSet(ActionManager.getInstance().getAction("ExpandAll").getShortcutSet(), (JComponent)this.myTable); 
/*     */     }
/*     */     private TreeTableWithTreeWidthController myTable;
/*     */     
/*     */     public void setTable(TreeTableWithTreeWidthController table) {
/* 383 */       this.myTable = table;
/* 384 */       registerCustomShortcutSet(ActionManager.getInstance().getAction("ExpandAll").getShortcutSet(), (JComponent)this.myTable);
/*     */     }
/*     */ 
/*     */     
/*     */     public void update(@NotNull AnActionEvent e) {
/* 389 */       if (e == null) $$$reportNull$$$0(0);  super.update(e);
/* 390 */       e.getPresentation().setEnabled((this.myTable != null));
/*     */     }
/*     */ 
/*     */     
/*     */     public void actionPerformed(@NotNull AnActionEvent e) {
/* 395 */       if (e == null) $$$reportNull$$$0(1);  if (this.myTable == null)
/* 396 */         return;  TreeTableWidthController controller = this.myTable.getWidthController();
/* 397 */       if (controller != null) controller.startBatchExpand(); 
/* 398 */       TreeUtil.expandAll((JTree)this.myTable.getTree());
/* 399 */       if (controller != null) controller.stopBatchExpand(); 
/*     */     }
/*     */   }
/*     */   
/*     */   public static class CollapseAllAction extends DumbAwareAction {
/*     */     private TreeTable myTable;
/*     */     
/*     */     public CollapseAllAction(TreeTable table) {
/* 407 */       super(IdeBundle.messagePointer("action.CollapseAllAction.text.collapse.all", new Object[0]), 
/* 408 */           IdeBundle.messagePointer("action.CollapseAllAction.text.collapse.all", new Object[0]), AllIcons.Actions.Collapseall);
/* 409 */       this.myTable = table;
/* 410 */       registerCustomShortcutSet(ActionManager.getInstance().getAction("CollapseAll").getShortcutSet(), (JComponent)this.myTable);
/*     */     }
/*     */     
/*     */     public void setTable(TreeTableWithTreeWidthController table) {
/* 414 */       this.myTable = table;
/* 415 */       registerCustomShortcutSet(ActionManager.getInstance().getAction("ExpandAll").getShortcutSet(), (JComponent)this.myTable);
/*     */     }
/*     */ 
/*     */     
/*     */     public void update(@NotNull AnActionEvent e) {
/* 420 */       if (e == null) $$$reportNull$$$0(0);  super.update(e);
/* 421 */       e.getPresentation().setEnabled((this.myTable != null));
/*     */     }
/*     */ 
/*     */     
/*     */     public void actionPerformed(@NotNull AnActionEvent e) {
/* 426 */       if (e == null) $$$reportNull$$$0(1);  if (this.myTable == null)
/* 427 */         return;  V8Utils.collapseAll(this.myTable);
/*     */     }
/*     */   }
/*     */ 
/*     */   
/*     */   public static String encodeUrl(@NotNull String url) throws IOException {
/* 433 */     if (url == null) $$$reportNull$$$0(5);  try { URL urlUrl = new URL(url);
/*     */       
/* 435 */       URI uri = new URI(urlUrl.getProtocol(), urlUrl.getUserInfo(), urlUrl.getHost(), urlUrl.getPort(), urlUrl.getPath(), urlUrl.getQuery(), urlUrl.getRef());
/* 436 */       return uri.toURL().toString(); }
/*     */     
/* 438 */     catch (MalformedURLException e)
/* 439 */     { throw new IOException(e); }
/*     */     
/* 441 */     catch (URISyntaxException e)
/* 442 */     { throw new IOException(e); }
/*     */   
/*     */   }
/*     */   
/*     */   public static int getTableRowX(@NotNull JTree tree, int row) {
/* 447 */     if (tree == null) $$$reportNull$$$0(6);  return TreeUtil.getNodeRowX(tree, row);
/*     */   }
/*     */   
/*     */   public static V8HeapTreeTable createTable(Project project, TreeTableModel model, V8CachingReader reader) {
/* 451 */     V8HeapTreeTable table = new V8HeapTreeTable(model, reader.getResourses());
/* 452 */     afterModelReset(project, reader, table);
/* 453 */     return table;
/*     */   }
/*     */   
/*     */   public static void afterModelReset(Project project, V8CachingReader reader, V8HeapTreeTable table) {
/* 457 */     String sample = "" + reader.getRetainedSize(0) + " 100% a b";
/* 458 */     afterModelReset(table, sample, reader.getResourses(), (TreeCellRenderer)new DirectTreeTableRenderer(project, reader));
/*     */   }
/*     */   
/*     */   public static void afterModelReset(V8HeapTreeTable table, @Nullable String sample, CompositeCloseable resources, TreeCellRenderer renderer) {
/* 462 */     table.setAutoResizeMode(0);
/* 463 */     table.getTableHeader().setResizingAllowed(false);
/* 464 */     table.getTableHeader().setReorderingAllowed(false);
/* 465 */     table.setRootVisible(false);
/* 466 */     table.getTree().setShowsRootHandles(true);
/* 467 */     table.smallerIndent();
/* 468 */     table.setSelectionMode(0);
/* 469 */     adjustColumnWIdths((TreeTable)table, sample);
/* 470 */     table.attachWidthController(resources, renderer);
/*     */   }
/*     */   
/*     */   public static void adjustColumnWIdths(TreeTable table, String sample) {
/* 474 */     FontMetrics metrics = table.getTableHeader().getFontMetrics(table.getTableHeader().getFont());
/* 475 */     TableColumnModel model = table.getTableHeader().getColumnModel();
/* 476 */     int count = table.getTableModel().getColumnCount();
/*     */     
/* 478 */     if (sample == null && table.getTableModel() instanceof TreeTableModelWithCustomRenderer) {
/* 479 */       TreeTableModelWithCustomRenderer withRenderer = (TreeTableModelWithCustomRenderer)table.getTableModel();
/* 480 */       for (int col = 1; col < count; col++) {
/* 481 */         TableColumn column = table.getColumnModel().getColumn(col);
/* 482 */         int maxWidth = metrics.stringWidth(column.getHeaderValue().toString()) + 2;
/*     */         
/* 484 */         for (int row = 0; row < table.getRowCount(); row++) {
/* 485 */           Object value = table.getValueAt(row, col);
/*     */           
/* 487 */           ColoredTableCellRenderer renderer = (ColoredTableCellRenderer)withRenderer.getCustomizedRenderer(col, value, table.getCellRenderer(row, col));
/* 488 */           renderer.getTableCellRendererComponent((JTable)table, value, false, true, row, col);
/* 489 */           maxWidth = Math.max((renderer.computePreferredSize(false)).width + 10, maxWidth);
/*     */         } 
/* 491 */         fixColumnWidth(maxWidth, column);
/*     */       } 
/* 493 */     } else if (sample != null) {
/* 494 */       int width = Math.max(metrics.stringWidth(sample) + 3, 10);
/* 495 */       for (int i = 1; i < count; i++) {
/* 496 */         fixColumnWidth(width, model.getColumn(i));
/*     */       }
/*     */     } 
/*     */   }
/*     */   
/*     */   public static void fixColumnWidth(int width, TableColumn two) {
/* 502 */     two.setWidth(width);
/* 503 */     two.setMaxWidth(4 * width);
/* 504 */     two.setMinWidth(width);
/*     */   }
/*     */   
/*     */   public static void writeIntList(IntList list, ObjectOutput out) throws IOException {
/* 508 */     out.writeInt(list.size());
/* 509 */     for (int i = 0; i < list.size(); i++) {
/* 510 */       out.writeInt(list.getInt(i));
/*     */     }
/*     */   }
/*     */   
/*     */   public static IntList readIntList(ObjectInput in) throws IOException {
/* 515 */     int size = in.readInt();
/* 516 */     IntArrayList intArrayList = new IntArrayList(size);
/* 517 */     for (int i = 0; i < size; i++) {
/* 518 */       intArrayList.add(in.readInt());
/*     */     }
/* 520 */     return (IntList)intArrayList;
/*     */   }
/*     */   
/*     */   public static void safeDraw(@NotNull Graphics2D graphics2D, Consumer<? super Graphics2D> consumer) {
/* 524 */     if (graphics2D == null) $$$reportNull$$$0(7);  Color color = graphics2D.getColor();
/* 525 */     Stroke stroke = graphics2D.getStroke();
/* 526 */     Font font = graphics2D.getFont();
/* 527 */     Paint paint = graphics2D.getPaint();
/*     */     try {
/* 529 */       consumer.consume(graphics2D);
/*     */     } finally {
/* 531 */       graphics2D.setColor(color);
/* 532 */       graphics2D.setStroke(stroke);
/* 533 */       graphics2D.setPaint(paint);
/* 534 */       graphics2D.setFont(font);
/*     */     } 
/*     */   }
/*     */ }


/* Location:              C:\Program Files\JetBrains\WebStorm 2021.1\plugins\NodeJS\lib\NodeJS.jar!\com\jetbrains\nodejs\run\profile\V8Utils.class
 * Java compiler version: 11 (55.0)
 * JD-Core Version:       1.1.3
 */