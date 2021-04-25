/*    */ package org.bipolar.run.profile.cpu.v8log.ui;
/*    */ 
/*    */ import com.intellij.openapi.actionSystem.DataProvider;
/*    */ import com.intellij.openapi.project.Project;
/*    */ import com.intellij.openapi.util.Pair;
/*    */ import org.bipolar.run.profile.V8Utils;
/*    */ import org.bipolar.run.profile.cpu.calculation.V8ProfileLineFileDescriptor;
/*    */ import org.bipolar.run.profile.cpu.v8log.data.V8CpuLogCall;
/*    */ import org.bipolar.run.profile.cpu.v8log.reading.V8StackTableModel;
/*    */ import java.io.IOException;
/*    */ import javax.swing.table.TableCellRenderer;
/*    */ import javax.swing.table.TableModel;
/*    */
import org.jetbrains.annotations.NonNls;
/*    */ import org.jetbrains.annotations.NotNull;
/*    */ import org.jetbrains.annotations.Nullable;
/*    */ 
/*    */ 
/*    */ public class StackTraceTable
/*    */   extends TableWithFixedWidth
/*    */   implements DataProvider
/*    */ {
/*    */   private final Project myProject;
/*    */   
/*    */   public StackTraceTable(Project project, V8StackTableModel model) throws IOException {
/* 25 */     super((TableModel)model);
/* 26 */     this.myProject = project;
/* 27 */     setSelectionMode(0);
/*    */   }
/*    */   
/*    */   public V8CpuLogCall getCall() {
/* 31 */     int row = getSelectedRow();
/* 32 */     if (row < 0) return null; 
/* 33 */     return ((V8StackTableModel)getModel()).getCall(row);
/*    */   }
/*    */ 
/*    */   
/*    */   @Nullable
/*    */   public Object getData(@NotNull @NonNls String dataId) {
/* 39 */     if (dataId == null) $$$reportNull$$$0(0);  V8CpuLogCall call = getCall();
/* 40 */     if (call == null) return null; 
/* 41 */     if (V8Utils.NAVIGATABLE_ONLY_FOR_ACTION.is(dataId)) {
/* 42 */       return call.getNavigatables(this.myProject);
/*    */     }
/* 44 */     if (V8Utils.NAVIGATION_POSITION.is(dataId)) {
/* 45 */       V8ProfileLineFileDescriptor descriptor = call.getDescriptor();
/* 46 */       return (descriptor == null) ? null : Pair.create(Integer.valueOf(descriptor.getRow()), Integer.valueOf(descriptor.getCol()));
/*    */     } 
/* 48 */     if (V8Utils.IS_NAVIGATABLE.is(dataId))
/* 49 */       return Boolean.valueOf((call.getDescriptor() != null)); 
/* 50 */     if (V8Utils.SELECTED_CALL.is(dataId)) {
/* 51 */       return getCall();
/*    */     }
/* 53 */     return null;
/*    */   }
/*    */ 
/*    */   
/*    */   public TableCellRenderer getCellRenderer(int row, int column) {
/* 58 */     return ((V8StackTableModel)getModel()).getCellRenderer(row, column);
/*    */   }
/*    */ }


/* Location:              C:\Program Files\JetBrains\WebStorm 2021.1\plugins\NodeJS\lib\NodeJS.jar!\com\jetbrains\nodejs\run\profile\cpu\v8lo\\ui\StackTraceTable.class
 * Java compiler version: 11 (55.0)
 * JD-Core Version:       1.1.3
 */