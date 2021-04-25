/*    */ package org.bipolar.run.profile.heap.calculation.diff;
/*    */ 
/*    */ import com.intellij.icons.AllIcons;
/*    */ import com.intellij.openapi.project.Project;
/*    */ import com.intellij.ui.SimpleTextAttributes;
/*    */ import com.intellij.ui.treeStructure.treetable.TreeTableTree;
/*    */ import com.intellij.util.BeforeAfter;
/*    */ import com.intellij.util.ui.StartupUiUtil;
/*    */ import org.bipolar.run.profile.heap.V8CachingReader;
/*    */ import org.bipolar.run.profile.heap.data.Aggregate;
/*    */ import org.bipolar.run.profile.heap.view.models.V8HeapContainmentTreeTableModel;
/*    */ import org.bipolar.run.profile.heap.view.renderers.DirectTreeTableRenderer;
/*    */ import java.util.ArrayList;
/*    */ import java.util.List;
/*    */ import javax.swing.JTree;
/*    */ import org.jetbrains.annotations.NotNull;
/*    */ 
/*    */ public class DiffCellRenderer
/*    */   extends DirectTreeTableRenderer
/*    */ {
/*    */   @NotNull
/*    */   private final V8CachingReader myBaseReader;
/*    */   @NotNull
/*    */   private final V8CachingReader myChangedReader;
/*    */   @NotNull
/*    */   private final AggregateDifferenceEmphasizerI myEmphasizer;
/*    */   private final SimpleTextAttributes mySelectedBold;
/*    */   
/*    */   public DiffCellRenderer(@NotNull Project project, @NotNull V8CachingReader baseReader, @NotNull V8CachingReader changedReader, @NotNull AggregateDifferenceEmphasizerI emphasizer) {
/* 30 */     super(project, baseReader);
/* 31 */     this.myBaseReader = baseReader;
/* 32 */     this.myChangedReader = changedReader;
/* 33 */     this.myEmphasizer = emphasizer;
/* 34 */     this.mySelectedBold = SimpleTextAttributes.SELECTED_SIMPLE_CELL_ATTRIBUTES.derive(1, null, null, null);
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public void customizeCellRenderer(@NotNull JTree tree, Object value, boolean selected, boolean expanded, boolean leaf, int row, boolean hasFocus) {
/* 45 */     if (tree == null) $$$reportNull$$$0(4);  setIcon(null);
/* 46 */     if (value instanceof AggregatesViewDiff.AggregateDifference) {
/* 47 */       setBackground(null);
/* 48 */       int width = getWidth(tree, row);
/* 49 */       AggregatesViewDiff.AggregateDifference difference = (AggregatesViewDiff.AggregateDifference)value;
/*    */       
/* 51 */       String presentation = (difference.getChanged() == null) ? difference.getBase().getPresentation(this.myBaseReader) : difference.getChanged().getPresentation(this.myChangedReader);
/*    */ 
/*    */       
/* 54 */       SimpleTextAttributes att = this.myEmphasizer.emphasize(difference) ? (selected ? this.mySelectedBold : SimpleTextAttributes.REGULAR_BOLD_ATTRIBUTES) : SimpleTextAttributes.REGULAR_ATTRIBUTES;
/* 55 */       Aggregate aggregate = (difference.getChanged() == null) ? difference.getBase() : difference.getChanged();
/*    */       
/* 57 */       List<DirectTreeTableRenderer.PartRenderer> list = new ArrayList<>();
/* 58 */       appendAggregateNameRenderer(aggregate, presentation, list, att);
/* 59 */       boolean lineHasFocus = ((TreeTableTree)tree).getTreeTable().hasFocus();
/* 60 */       boolean selectedForeground = (selected && (lineHasFocus || StartupUiUtil.isUnderDarcula()));
/* 61 */       adaptiveRendering(list, width, selectedForeground);
/* 62 */     } else if (value instanceof BeforeAfter) {
/* 63 */       BeforeAfter<V8HeapContainmentTreeTableModel.NamedEntry> beforeAfter = (BeforeAfter<V8HeapContainmentTreeTableModel.NamedEntry>)value;
/* 64 */       if (beforeAfter.getAfter() == null) {
/* 65 */         setIcon(AllIcons.General.Remove);
/* 66 */         this.myReader = this.myBaseReader;
/* 67 */         super.customizeCellRenderer(tree, beforeAfter.getBefore(), selected, expanded, leaf, row, hasFocus);
/*    */       } else {
/* 69 */         if (beforeAfter.getBefore() == null) setIcon(AllIcons.General.Add); 
/* 70 */         this.myReader = this.myChangedReader;
/* 71 */         super.customizeCellRenderer(tree, beforeAfter.getAfter(), selected, expanded, leaf, row, hasFocus);
/*    */       } 
/*    */     } else {
/* 74 */       super.customizeCellRenderer(tree, value, selected, expanded, leaf, row, hasFocus);
/*    */     } 
/*    */   }
/*    */ }


/* Location:              C:\Program Files\JetBrains\WebStorm 2021.1\plugins\NodeJS\lib\NodeJS.jar!\com\jetbrains\nodejs\run\profile\heap\calculation\diff\DiffCellRenderer.class
 * Java compiler version: 11 (55.0)
 * JD-Core Version:       1.1.3
 */