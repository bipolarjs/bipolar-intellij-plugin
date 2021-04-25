/*     */ package org.bipolar.run.profile;
/*     */ 
/*     */ import com.intellij.openapi.Disposable;
/*     */ import com.intellij.openapi.util.ZipperUpdater;
/*     */ import com.intellij.ui.SimpleColoredComponent;
/*     */ import com.intellij.ui.treeStructure.treetable.TreeTableTree;
/*     */ import com.intellij.util.Alarm;
/*     */ import java.awt.Dimension;
/*     */ import java.util.ArrayList;
/*     */ import java.util.List;
/*     */ import javax.swing.JTree;
/*     */ import javax.swing.event.TreeExpansionEvent;
/*     */ import javax.swing.event.TreeExpansionListener;
/*     */ import javax.swing.table.TableColumn;
/*     */ import javax.swing.table.TableColumnModel;
/*     */ import javax.swing.tree.TreeCellRenderer;
/*     */ import org.jetbrains.annotations.NotNull;
/*     */ 
/*     */ 
/*     */ 
/*     */ public class TreeTableWidthController
/*     */ {
/*     */   private final TreeTableWithTreeWidthController myTreeTable;
/*     */   private final List<Integer> myTailWidths;
/*     */   private int myRightMin;
/*     */   private final ZipperUpdater myUpdater;
/*     */   private int myCurrentZeroWidth;
/*     */   private TreeCellRenderer myMeaningfulRenderer;
/*     */   private TreeExpansionListener myExpansionListener;
/*  30 */   private int myCachedWidth = -1;
/*     */   private final Runnable myRecalculateRunnable;
/*     */   
/*     */   public TreeTableWidthController(TreeTableWithTreeWidthController treeTable, @NotNull Disposable disposable) {
/*  34 */     this.myTreeTable = treeTable;
/*  35 */     this.myTailWidths = new ArrayList<>();
/*  36 */     recalculateTail();
/*  37 */     this.myRecalculateRunnable = (() -> {
/*     */         this.myCachedWidth = -1;
/*     */         recalculateImpl();
/*     */       });
/*  41 */     this.myUpdater = new ZipperUpdater(100, Alarm.ThreadToUse.SWING_THREAD, disposable);
/*  42 */     recalculateZero();
/*  43 */     installListener();
/*     */   }
/*     */   
/*     */   private void recalculateTail() {
/*  47 */     TableColumnModel model = this.myTreeTable.getColumnModel();
/*  48 */     int width = 0;
/*  49 */     for (int i = 1; i < model.getColumnCount(); i++) {
/*  50 */       int tailWidth = model.getColumn(i).getMinWidth();
/*  51 */       this.myTailWidths.add(Integer.valueOf(tailWidth));
/*  52 */       width += tailWidth;
/*     */     } 
/*  54 */     this.myRightMin = width;
/*     */   }
/*     */   
/*     */   public void recalculateAll() {
/*  58 */     recalculateTail();
/*  59 */     this.myCachedWidth = -1;
/*  60 */     recalculateZero();
/*     */   }
/*     */   
/*     */   public void setMeaningfulRenderer(TreeCellRenderer meaningfulRenderer) {
/*  64 */     this.myMeaningfulRenderer = meaningfulRenderer;
/*  65 */     recalculateZero();
/*     */   }
/*     */   
/*     */   public int getWidth(int parentWidth) {
/*  69 */     if (this.myCachedWidth > 0) {
/*  70 */       return this.myCachedWidth;
/*     */     }
/*  72 */     TableColumnModel model = this.myTreeTable.getColumnModel();
/*  73 */     TableColumn zero = model.getColumn(0);
/*  74 */     if (this.myCurrentZeroWidth + this.myRightMin < parentWidth) {
/*  75 */       int width = 0;
/*  76 */       for (int j = 1; j < model.getColumnCount(); j++) {
/*     */         
/*  78 */         int colWidth = ((Integer)this.myTailWidths.get(j - 1)).intValue();
/*  79 */         width += colWidth;
/*  80 */         model.getColumn(j).setMinWidth(colWidth);
/*  81 */         model.getColumn(j).setWidth(colWidth);
/*     */       } 
/*  83 */       int zeroWidth = parentWidth - width;
/*  84 */       zero.setMinWidth(zeroWidth);
/*  85 */       zero.setWidth(zeroWidth);
/*     */       
/*  87 */       return parentWidth;
/*     */     } 
/*  89 */     zero.setMinWidth(this.myCurrentZeroWidth);
/*  90 */     zero.setWidth(this.myCurrentZeroWidth);
/*  91 */     for (int i = 1; i < model.getColumnCount(); i++) {
/*  92 */       Integer width = this.myTailWidths.get(i - 1);
/*  93 */       model.getColumn(i).setMinWidth(width.intValue());
/*  94 */       model.getColumn(i).setWidth(width.intValue());
/*     */     } 
/*  96 */     return this.myCurrentZeroWidth + this.myRightMin;
/*     */   }
/*     */ 
/*     */   
/*     */   public void startBatchExpand() {
/* 101 */     this.myCachedWidth = getWidth(this.myTreeTable.getParent().getWidth());
/* 102 */     this.myTreeTable.getTree().removeTreeExpansionListener(this.myExpansionListener);
/*     */   }
/*     */   
/*     */   public void stopBatchExpand() {
/* 106 */     this.myTreeTable.getTree().addTreeExpansionListener(this.myExpansionListener);
/* 107 */     this.myCachedWidth = -1;
/* 108 */     recalculateZero();
/*     */   }
/*     */   
/*     */   public void installListener() {
/* 112 */     if (this.myExpansionListener != null) this.myTreeTable.getTree().removeTreeExpansionListener(this.myExpansionListener); 
/* 113 */     this.myExpansionListener = new TreeExpansionListener()
/*     */       {
/*     */         public void treeExpanded(TreeExpansionEvent event) {
/* 116 */           TreeTableWidthController.this.recalculateZero();
/*     */         }
/*     */ 
/*     */         
/*     */         public void treeCollapsed(TreeExpansionEvent event) {
/* 121 */           TreeTableWidthController.this.recalculateZero();
/*     */         }
/*     */       };
/* 124 */     this.myTreeTable.getTree().addTreeExpansionListener(this.myExpansionListener);
/* 125 */     recalculateZero();
/*     */   }
/*     */   
/*     */   private void adjustColumn() {
/* 129 */     TableColumn column = this.myTreeTable.getColumnModel().getColumn(0);
/* 130 */     column.setMinWidth(this.myCurrentZeroWidth);
/* 131 */     column.setWidth(this.myCurrentZeroWidth);
/*     */   }
/*     */   
/*     */   private void recalculateZero() {
/* 135 */     this.myUpdater.queue(this.myRecalculateRunnable);
/*     */   }
/*     */   
/*     */   private void recalculateImpl() {
/* 139 */     int max = 0;
/* 140 */     int count = this.myTreeTable.getRowCount();
/* 141 */     TreeTableTree tree = this.myTreeTable.getTree();
/* 142 */     TreeCellRenderer renderer = (this.myMeaningfulRenderer != null) ? this.myMeaningfulRenderer : tree.getCellRenderer();
/* 143 */     for (int i = 0; i < count; i++) {
/* 144 */       int width = getRowWidth(renderer, i);
/* 145 */       max = Math.max(width, max);
/*     */     } 
/* 147 */     this.myCurrentZeroWidth = max;
/* 148 */     adjustColumn();
/*     */   }
/*     */   private int getRowWidth(TreeCellRenderer renderer, int i) {
/*     */     int width;
/* 152 */     TreeTableTree tree = this.myTreeTable.getTree();
/* 153 */     Object component = tree.getPathForRow(i).getLastPathComponent();
/*     */     
/* 155 */     if (renderer instanceof SimpleColoredComponent) {
/* 156 */       renderer.getTreeCellRendererComponent((JTree)tree, component, true, true, false, i, false);
/* 157 */       Dimension size = ((SimpleColoredComponent)renderer).computePreferredSize(false);
/* 158 */       int rowX = V8Utils.getTableRowX((JTree)tree, i);
/* 159 */       width = rowX + size.width;
/*     */     } else {
/* 161 */       double approxWidth = tree.getFontMetrics(tree.getFont()).stringWidth(component.toString()) * 1.1D;
/* 162 */       int rowX = V8Utils.getTableRowX((JTree)tree, i);
/* 163 */       width = rowX + (int)approxWidth;
/*     */     } 
/* 165 */     return width;
/*     */   }
/*     */ }


/* Location:              C:\Program Files\JetBrains\WebStorm 2021.1\plugins\NodeJS\lib\NodeJS.jar!\com\jetbrains\nodejs\run\profile\TreeTableWidthController.class
 * Java compiler version: 11 (55.0)
 * JD-Core Version:       1.1.3
 */