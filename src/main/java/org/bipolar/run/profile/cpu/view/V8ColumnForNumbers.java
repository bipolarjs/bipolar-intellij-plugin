/*    */ package org.bipolar.run.profile.cpu.view;
/*    */ 
/*    */ import com.intellij.openapi.util.NlsContexts.ColumnName;
/*    */ import com.intellij.util.Processor;
/*    */ import com.intellij.util.ui.ColumnInfo;
/*    */ import org.bipolar.run.profile.cpu.calculation.V8ProfileLine;
/*    */ import org.bipolar.run.profile.heap.view.renderers.RightAlignedRenderer;
/*    */ import javax.swing.table.TableCellRenderer;
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public abstract class V8ColumnForNumbers<T>
/*    */   extends ColumnInfo<T, String>
/*    */ {
/*    */   private final Processor<? super V8ProfileLine> myLineShouldBeMarkedProcessor;
/*    */   private boolean myIsBold;
/*    */   
/* 33 */   private final TableCellRenderer myCellRenderer = (TableCellRenderer)new RightAlignedRenderer()
/*    */     {
/*    */       protected boolean isBold(Object value) {
/* 36 */         return V8ColumnForNumbers.this.myIsBold;
/*    */       }
/*    */     };
/*    */   
/*    */   public V8ColumnForNumbers(@ColumnName String name, Processor<? super V8ProfileLine> processor) {
/* 41 */     super(name);
/* 42 */     this.myLineShouldBeMarkedProcessor = processor;
/*    */   }
/*    */ 
/*    */   
/*    */   public TableCellRenderer getCustomizedRenderer(T o, TableCellRenderer renderer) {
/* 47 */     this.myIsBold = false;
/* 48 */     if (o instanceof V8ProfileLine) {
/* 49 */       V8ProfileLine line = (V8ProfileLine)o;
/* 50 */       if (this.myLineShouldBeMarkedProcessor.process(line) && V8TreeTableModel.v8LineIsAboveThresholds(line)) {
/* 51 */         this.myIsBold = true;
/*    */       }
/*    */     } 
/* 54 */     return this.myCellRenderer;
/*    */   }
/*    */ }


/* Location:              C:\Program Files\JetBrains\WebStorm 2021.1\plugins\NodeJS\lib\NodeJS.jar!\com\jetbrains\nodejs\run\profile\cpu\view\V8ColumnForNumbers.class
 * Java compiler version: 11 (55.0)
 * JD-Core Version:       1.1.3
 */