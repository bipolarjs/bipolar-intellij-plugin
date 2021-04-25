/*     */ package org.bipolar.run.profile.heap.view.components;
/*     */ 
/*     */ import com.intellij.openapi.Disposable;
/*     */ import com.intellij.openapi.util.Conditions;
/*     */ import com.intellij.openapi.util.Disposer;
/*     */ import com.intellij.ui.ColoredTableCellRenderer;
/*     */ import com.intellij.ui.SimpleTextAttributes;
/*     */ import com.intellij.ui.treeStructure.treetable.TreeTableCellRenderer;
/*     */ import com.intellij.ui.treeStructure.treetable.TreeTableModel;
/*     */ import com.intellij.util.ui.UIUtil;
/*     */ import com.intellij.util.ui.tree.WideSelectionTreeUI;
/*     */ import org.bipolar.run.profile.TreeTableWidthController;
/*     */ import org.bipolar.run.profile.TreeTableWithTreeWidthController;
/*     */ import org.bipolar.run.profile.V8Utils;
/*     */ import org.bipolar.run.profile.cpu.view.TreeTableModelWithCustomRenderer;
/*     */ import org.bipolar.run.profile.heap.CompositeCloseable;
/*     */ import org.bipolar.run.profile.heap.view.models.V8HeapContainmentTreeTableModel;
/*     */ import java.awt.Dimension;
/*     */ import java.io.Closeable;
/*     */ import java.io.IOException;
/*     */ import javax.swing.JTable;
/*     */ import javax.swing.plaf.TreeUI;
/*     */ import javax.swing.table.TableCellRenderer;
/*     */ import javax.swing.tree.TreeCellRenderer;
/*     */ import javax.swing.tree.TreePath;
/*     */
import org.jetbrains.annotations.NotNull;
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
/*     */ 
/*     */ 
/*     */ public class V8HeapTreeTable
/*     */   extends TreeTableWithTreeWidthController
/*     */ {
/*     */   private final ColoredTableCellRenderer myDefaultTableRenderer;
/*     */   private TreeTableWidthController myWidthController;
/*     */   
/*     */   public V8HeapTreeTable(TreeTableModel treeTableModel, CompositeCloseable resources) {
/*  53 */     super(treeTableModel, createDisposable(resources));
/*  54 */     getTree().setUI((TreeUI)new WideSelectionTreeUI(true, Conditions.alwaysTrue()));
/*  55 */     smallerIndent();
/*  56 */     this.myDefaultTableRenderer = new ColoredTableCellRenderer()
/*     */       {
/*     */         protected void customizeCellRenderer(@NotNull JTable table, Object value, boolean selected, boolean hasFocus, int row, int column) {
/*  59 */           if (table == null) $$$reportNull$$$0(0);  setPaintFocusBorder(false);
/*  60 */           String fragment = value.toString();
/*  61 */           if (selected && !table.hasFocus()) {
/*  62 */             setBackground(UIUtil.getTreeUnfocusedSelectionBackground());
/*  63 */             append(fragment, SimpleTextAttributes.REGULAR_ATTRIBUTES.derive(-1, UIUtil.getTableForeground(), null, null));
/*     */           } else {
/*     */             
/*  66 */             append(fragment);
/*     */           } 
/*     */         }
/*     */ 
/*     */         
/*     */         protected SimpleTextAttributes modifyAttributes(SimpleTextAttributes attributes) {
/*  72 */           return attributes;
/*     */         }
/*     */       };
/*     */   }
/*     */   
/*     */   public void attachWidthController(CompositeCloseable resources, TreeCellRenderer renderer) {
/*  78 */     if (this.myWidthController != null) {
/*  79 */       this.myWidthController.installListener();
/*     */       return;
/*     */     } 
/*  82 */     this.myWidthController = new TreeTableWidthController(this, createDisposable(resources));
/*  83 */     this.myWidthController.setMeaningfulRenderer(renderer);
/*     */   }
/*     */ 
/*     */   
/*     */   @Nullable
/*     */   public TreeTableWidthController getWidthController() {
/*  89 */     return this.myWidthController;
/*     */   }
/*     */   
/*     */   @NotNull
/*     */   private static Disposable createDisposable(@NotNull CompositeCloseable compositeCloseable) {
/*  94 */     if (compositeCloseable == null) $$$reportNull$$$0(0);  final Disposable disposable = Disposer.newDisposable();
/*  95 */     compositeCloseable.register(new Closeable()
/*     */         {
/*     */           public void close() throws IOException {
/*  98 */             Disposer.dispose(disposable);
/*     */           }
/*     */         });
/* 101 */     if (disposable == null) $$$reportNull$$$0(1);  return disposable;
/*     */   }
/*     */ 
/*     */   
/*     */   public Dimension getPreferredSize() {
/* 106 */     Dimension size = super.getPreferredSize();
/* 107 */     int parentWidth = getParent().getWidth();
/*     */     
/* 109 */     if (this.myWidthController == null || parentWidth == 0) return size;
/*     */     
/* 111 */     int width = this.myWidthController.getWidth(parentWidth);
/* 112 */     return new Dimension(width, size.height);
/*     */   }
/*     */   
/*     */   public void smallerIndent() {
/* 116 */     V8Utils.setSmallerTreeIndent(getTree());
/*     */   }
/*     */   
/*     */   private V8HeapContainmentTreeTableModel.NamedEntry getNamedEntry(int row) {
/* 120 */     TreePath pathForRow = getTree().getPathForRow(row);
/* 121 */     if (pathForRow != null) {
/* 122 */       Object o = pathForRow.getLastPathComponent();
/* 123 */       if (o instanceof V8HeapContainmentTreeTableModel.NamedEntry) return (V8HeapContainmentTreeTableModel.NamedEntry)o; 
/*     */     } 
/* 125 */     return null;
/*     */   }
/*     */   
/*     */   private Object getEntry(int row) {
/* 129 */     TreePath pathForRow = getTree().getPathForRow(row);
/* 130 */     if (pathForRow != null) {
/* 131 */       return pathForRow.getLastPathComponent();
/*     */     }
/*     */     
/* 134 */     return null;
/*     */   }
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
/*     */   
/*     */   public TableCellRenderer getCellRenderer(int row, int column) {
/* 157 */     TableCellRenderer superRenderer = super.getCellRenderer(row, column);
/* 158 */     if (superRenderer instanceof TreeTableCellRenderer) {
/* 159 */       ((TreeTableCellRenderer)superRenderer).setCellRenderer(null);
/*     */     }
/* 161 */     TableCellRenderer renderer = ((TreeTableModelWithCustomRenderer)getTableModel()).getCustomizedRenderer(column, getEntry(row), superRenderer);
/* 162 */     return (renderer == null) ? (TableCellRenderer)this.myDefaultTableRenderer : renderer;
/*     */   }
/*     */ }


/* Location:              C:\Program Files\JetBrains\WebStorm 2021.1\plugins\NodeJS\lib\NodeJS.jar!\com\jetbrains\nodejs\run\profile\heap\view\components\V8HeapTreeTable.class
 * Java compiler version: 11 (55.0)
 * JD-Core Version:       1.1.3
 */