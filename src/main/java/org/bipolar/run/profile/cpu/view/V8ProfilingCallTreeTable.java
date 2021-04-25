/*     */ package org.bipolar.run.profile.cpu.view;
/*     */ 
/*     */ import com.intellij.ide.CopyProvider;
/*     */ import com.intellij.openapi.Disposable;
/*     */ import com.intellij.openapi.actionSystem.DataContext;
/*     */ import com.intellij.openapi.actionSystem.DataProvider;
/*     */ import com.intellij.openapi.actionSystem.PlatformDataKeys;
/*     */ import com.intellij.openapi.diagnostic.Logger;
/*     */ import com.intellij.openapi.ide.CopyPasteManager;
/*     */ import com.intellij.openapi.project.Project;
/*     */ import com.intellij.openapi.util.Conditions;
/*     */ import com.intellij.openapi.util.Pair;
/*     */ import com.intellij.pom.Navigatable;
/*     */ import com.intellij.ui.ColoredTableCellRenderer;
/*     */ import com.intellij.ui.ColoredTreeCellRenderer;
/*     */ import com.intellij.ui.SimpleTextAttributes;
/*     */ import com.intellij.ui.treeStructure.treetable.TreeTableCellRenderer;
/*     */ import com.intellij.ui.treeStructure.treetable.TreeTableModel;
/*     */ import com.intellij.ui.treeStructure.treetable.TreeTableTree;
/*     */ import com.intellij.util.ui.UIUtil;
/*     */ import com.intellij.util.ui.tree.WideSelectionTreeUI;
/*     */ import org.bipolar.run.profile.TreeTableWidthController;
/*     */ import org.bipolar.run.profile.TreeTableWithTreeWidthController;
/*     */ import org.bipolar.run.profile.V8Utils;
/*     */ import org.bipolar.run.profile.cpu.CallHolder;
/*     */ import org.bipolar.run.profile.cpu.calculation.V8ProfileLineFileDescriptor;
/*     */ import java.awt.Dimension;
/*     */ import java.awt.datatransfer.StringSelection;
/*     */ import java.util.Enumeration;
/*     */ import javax.swing.JTable;
/*     */ import javax.swing.JTree;
/*     */ import javax.swing.plaf.TreeUI;
/*     */ import javax.swing.table.TableCellRenderer;
/*     */ import javax.swing.tree.TreeCellRenderer;
/*     */ import javax.swing.tree.TreePath;
/*     */
import org.jetbrains.annotations.NonNls;
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
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class V8ProfilingCallTreeTable
/*     */   extends TreeTableWithTreeWidthController
/*     */   implements DataProvider
/*     */ {
/*  59 */   private static final Logger LOG = Logger.getInstance(V8ProfilingCallTreeTable.class);
/*     */   private final CopyProvider myProvider;
/*     */   private final ColoredTableCellRenderer myDefaultTableRenderer;
/*     */   private final Project myProject;
/*     */   private final ColoredTreeCellRenderer myDefaultRenderer;
/*     */   private TreeTableWidthController myController;
/*     */   
/*     */   public V8ProfilingCallTreeTable(Project project, TreeTableModel treeTableModel, Disposable disposable) {
/*  67 */     super(treeTableModel, disposable);
/*  68 */     this.myProject = project;
/*  69 */     getTree().setUI((TreeUI)new WideSelectionTreeUI(true, Conditions.alwaysTrue()));
/*  70 */     setAutoResizeMode(0);
/*  71 */     getTableHeader().setResizingAllowed(false);
/*  72 */     this.myDefaultRenderer = new ColoredTreeCellRenderer()
/*     */       {
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */         
/*     */         public void customizeCellRenderer(@NotNull JTree tree, Object value, boolean selected, boolean expanded, boolean leaf, int row, boolean hasFocus)
/*     */         {
/*  81 */           if (tree == null) $$$reportNull$$$0(0);  boolean treeHasFocus = ((TreeTableTree)tree).getTreeTable().hasFocus();
/*  82 */           setPaintFocusBorder(false);
/*  83 */           setBackground(UIUtil.getTreeBackground(selected, treeHasFocus));
/*  84 */           String fragment = value.toString();
/*  85 */           if (selected && !treeHasFocus) {
/*  86 */             append(fragment, SimpleTextAttributes.REGULAR_ATTRIBUTES.derive(-1, UIUtil.getTableForeground(), null, null));
/*     */           } else {
/*  88 */             append(fragment);
/*     */           } 
/*     */         }
/*     */ 
/*     */         
/*     */         protected boolean shouldDrawBackground() {
/*  94 */           return true;
/*     */         }
/*     */       };
/*  97 */     this.myDefaultTableRenderer = new ColoredTableCellRenderer()
/*     */       {
/*     */         protected void customizeCellRenderer(@NotNull JTable table, Object value, boolean selected, boolean hasFocus, int row, int column) {
/* 100 */           if (table == null) $$$reportNull$$$0(0);  setPaintFocusBorder(false);
/* 101 */           setBackground(UIUtil.getTreeBackground(selected, table.hasFocus()));
/* 102 */           String fragment = value.toString();
/* 103 */           if (selected && !table.hasFocus()) {
/* 104 */             append(fragment, SimpleTextAttributes.REGULAR_ATTRIBUTES.derive(-1, UIUtil.getTableForeground(), null, null));
/*     */           } else {
/*     */             
/* 107 */             append(fragment);
/*     */           } 
/*     */         }
/*     */ 
/*     */         
/*     */         protected SimpleTextAttributes modifyAttributes(SimpleTextAttributes attributes) {
/* 113 */           return attributes;
/*     */         }
/*     */       };
/* 116 */     this.myProvider = new CopyProvider()
/*     */       {
/*     */         public void performCopy(@NotNull DataContext dataContext)
/*     */         {
/* 120 */           if (dataContext == null) $$$reportNull$$$0(0);  try { int row = V8ProfilingCallTreeTable.this.getSelectedRow();
/* 121 */             TreePath path = V8ProfilingCallTreeTable.this.getTree().getSelectionPath();
/* 122 */             if (row < 0 || path == null)
/*     */               return; 
/* 124 */             Object selectedObject = path.getLastPathComponent();
/* 125 */             TreeTableModel model = V8ProfilingCallTreeTable.this.getTableModel();
/* 126 */             StringBuilder sb = new StringBuilder();
/* 127 */             for (int i = 0; i < model.getColumnCount(); i++) {
/* 128 */               if (sb.length() > 0) sb.append(" "); 
/* 129 */               Object valueAt = model.getValueAt(selectedObject, i);
/* 130 */               if (valueAt instanceof Pair) {
/* 131 */                 Pair pair = (Pair)valueAt;
/* 132 */                 sb.append(pair.getFirst().toString()).append(" ").append(pair.getSecond().toString());
/*     */               } else {
/* 134 */                 sb.append(valueAt.toString());
/*     */               } 
/*     */             } 
/*     */             
/* 138 */             CopyPasteManager.getInstance().setContents(new StringSelection(sb.toString())); }
/*     */           
/* 140 */           catch (Exception ex)
/* 141 */           { V8ProfilingCallTreeTable.LOG.info(ex); }
/*     */         
/*     */         }
/*     */ 
/*     */         
/*     */         public boolean isCopyEnabled(@NotNull DataContext dataContext) {
/* 147 */           if (dataContext == null) $$$reportNull$$$0(1);  return (V8ProfilingCallTreeTable.this.getSelectedRowCount() > 0);
/*     */         }
/*     */ 
/*     */         
/*     */         public boolean isCopyVisible(@NotNull DataContext dataContext) {
/* 152 */           if (dataContext == null) $$$reportNull$$$0(2);  return true;
/*     */         }
/*     */       };
/* 155 */     V8Utils.setSmallerTreeIndent(getTree());
/*     */   }
/*     */ 
/*     */   
/*     */   @Nullable
/*     */   public TreeTableWidthController getWidthController() {
/* 161 */     return this.myController;
/*     */   }
/*     */   
/*     */   public void setController(TreeTableWidthController controller) {
/* 165 */     this.myController = controller;
/*     */   }
/*     */ 
/*     */   
/*     */   public Dimension getPreferredSize() {
/* 170 */     Dimension size = super.getPreferredSize();
/* 171 */     int parentWidth = getParent().getWidth();
/*     */     
/* 173 */     if (this.myController == null || parentWidth == 0) return size;
/*     */     
/* 175 */     int width = this.myController.getWidth(parentWidth);
/* 176 */     return new Dimension(width, size.height);
/*     */   }
/*     */ 
/*     */   
/*     */   @Nullable
/*     */   public Object getData(@NotNull @NonNls String dataId) {
/* 182 */     if (dataId == null) $$$reportNull$$$0(0);  if (V8Utils.NAVIGATABLE_ONLY_FOR_ACTION.is(dataId)) {
/* 183 */       Navigatable[] navigatables = getNavigatables();
/* 184 */       if (navigatables != null) {
/* 185 */         return navigatables;
/*     */       }
/* 187 */     } else if (V8Utils.NAVIGATION_POSITION.is(dataId)) {
/* 188 */       int row = getSelectedRow();
/* 189 */       if (row >= 0) {
/* 190 */         CallHolder v8ProfileLine = getV8ProfileLine(row);
/* 191 */         if (v8ProfileLine != null && v8ProfileLine.getCall().getDescriptor() != null) {
/* 192 */           V8ProfileLineFileDescriptor descriptor = v8ProfileLine.getCall().getDescriptor();
/* 193 */           return Pair.create(Integer.valueOf(descriptor.getRow()), Integer.valueOf(descriptor.getCol()));
/*     */         } 
/*     */       } 
/* 196 */     } else if (V8Utils.IS_NAVIGATABLE.is(dataId)) {
/* 197 */       int row = getSelectedRow();
/* 198 */       if (row >= 0) {
/* 199 */         CallHolder v8ProfileLine = getV8ProfileLine(row);
/* 200 */         if (v8ProfileLine != null && v8ProfileLine.getCall().getDescriptor() != null)
/* 201 */           return Boolean.valueOf(true); 
/*     */       } 
/*     */     } else {
/* 204 */       if (PlatformDataKeys.COPY_PROVIDER.is(dataId))
/* 205 */         return this.myProvider; 
/* 206 */       if (V8Utils.SELECTED_CALL.is(dataId)) {
/* 207 */         int row = getSelectedRow();
/* 208 */         if (row >= 0) {
/* 209 */           CallHolder v8ProfileLine = getV8ProfileLine(row);
/* 210 */           if (v8ProfileLine != null)
/* 211 */             return v8ProfileLine.getCall(); 
/*     */         } 
/*     */       } 
/*     */     } 
/* 215 */     return null;
/*     */   }
/*     */   
/*     */   private Navigatable[] getNavigatables() {
/* 219 */     int row = getSelectedRow();
/* 220 */     if (row >= 0) {
/* 221 */       CallHolder v8ProfileLine = getV8ProfileLine(row);
/* 222 */       if (v8ProfileLine != null && v8ProfileLine.getCall().getDescriptor() != null) {
/* 223 */         return v8ProfileLine.getCall().getNavigatables(this.myProject);
/*     */       }
/*     */     } 
/* 226 */     return null;
/*     */   }
/*     */   
/*     */   public CallHolder getV8ProfileLine(int row) {
/* 230 */     TreePath pathForRow = getTree().getPathForRow(row);
/* 231 */     if (pathForRow != null) {
/* 232 */       Object o = pathForRow.getLastPathComponent();
/* 233 */       if (o instanceof CallHolder) return (CallHolder)o; 
/*     */     } 
/* 235 */     return null;
/*     */   }
/*     */ 
/*     */   
/*     */   public TableCellRenderer getCellRenderer(int row, int column) {
/* 240 */     TableCellRenderer superRenderer = super.getCellRenderer(row, column);
/*     */     
/* 242 */     CallHolder v8ProfileLine = getV8ProfileLine(row);
/* 243 */     if (v8ProfileLine != null) {
/* 244 */       TableCellRenderer renderer = ((TreeTableModelWithCustomRenderer)getTableModel()).getCustomizedRenderer(column, v8ProfileLine, superRenderer);
/* 245 */       if (renderer != null) return renderer; 
/*     */     } 
/* 247 */     if (!(superRenderer instanceof TreeTableCellRenderer)) {
/* 248 */       return (TableCellRenderer)this.myDefaultTableRenderer;
/*     */     }
/*     */     
/* 251 */     ((TreeTableCellRenderer)superRenderer).setCellRenderer((TreeCellRenderer)this.myDefaultRenderer);
/* 252 */     return superRenderer;
/*     */   }
/*     */   
/*     */   public void collapseRowRecursively() {
/* 256 */     TreeTableTree tree = getTree();
/* 257 */     TreePath leadSelectionPath = tree.getLeadSelectionPath();
/* 258 */     if (leadSelectionPath == null)
/* 259 */       return;  Enumeration<TreePath> descendants = tree.getExpandedDescendants(leadSelectionPath);
/* 260 */     if (descendants != null) {
/* 261 */       while (descendants.hasMoreElements()) {
/* 262 */         TreePath path = descendants.nextElement();
/* 263 */         tree.collapsePath(path);
/*     */       } 
/*     */     }
/* 266 */     tree.collapsePath(leadSelectionPath);
/*     */   }
/*     */ }


/* Location:              C:\Program Files\JetBrains\WebStorm 2021.1\plugins\NodeJS\lib\NodeJS.jar!\com\jetbrains\nodejs\run\profile\cpu\view\V8ProfilingCallTreeTable.class
 * Java compiler version: 11 (55.0)
 * JD-Core Version:       1.1.3
 */