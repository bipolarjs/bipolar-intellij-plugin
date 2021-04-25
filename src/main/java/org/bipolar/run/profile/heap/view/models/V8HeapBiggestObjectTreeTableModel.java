/*    */ package org.bipolar.run.profile.heap.view.models;
/*    */ 
/*    */ import com.intellij.openapi.project.Project;
/*    */ import com.intellij.ui.ColoredTreeCellRenderer;
/*    */ import org.bipolar.run.profile.heap.V8CachingReader;
/*    */ import org.bipolar.run.profile.heap.data.V8HeapEntry;
/*    */ import org.bipolar.run.profile.heap.view.renderers.DirectTreeTableRenderer;
/*    */
import org.jetbrains.annotations.NotNull;
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
/*    */ public class V8HeapBiggestObjectTreeTableModel
/*    */   extends V8HeapContainmentTreeTableModel
/*    */ {
/*    */   public V8HeapBiggestObjectTreeTableModel(Project project, @NotNull V8CachingReader reader) {
/* 29 */     super(project, reader);
/* 30 */     DirectTreeTableRenderer renderer = new DirectTreeTableRenderer(project, reader);
/* 31 */     setNodeRenderer((ColoredTreeCellRenderer)renderer);
/*    */   }
/*    */ 
/*    */   
/*    */   public Object getChild(Object parent, int index) {
/* 36 */     if (parent == getRoot()) {
/* 37 */       Integer biggest = this.myReader.getBiggestObjects().get(index);
/* 38 */       V8HeapEntry node = this.myReader.getNode(biggest.intValue());
/*    */       
/* 40 */       V8HeapContainmentTreeTableModel.NamedEntry entry = new V8HeapContainmentTreeTableModel.NamedEntry(node, this.myReader.getString(node.getNameId()), "", -1L);
/* 41 */       entry.setDoNotShowLink(true);
/* 42 */       return entry;
/*    */     } 
/* 44 */     return null;
/*    */   }
/*    */ 
/*    */   
/*    */   public int getChildCount(Object parent) {
/* 49 */     if (parent == getRoot()) {
/* 50 */       return this.myReader.getBiggestObjects().size();
/*    */     }
/* 52 */     return 0;
/*    */   }
/*    */ 
/*    */   
/*    */   public int getIndexOfChild(Object parent, Object child) {
/* 57 */     if (parent == getRoot() && child instanceof V8HeapContainmentTreeTableModel.NamedEntry) {
/* 58 */       V8HeapContainmentTreeTableModel.NamedEntry entry = (V8HeapContainmentTreeTableModel.NamedEntry)child;
/* 59 */       long id = entry.getEntry().getId();
/* 60 */       for (int i = 0; i < this.myReader.getBiggestObjects().size(); i++) {
/* 61 */         Integer object = this.myReader.getBiggestObjects().get(i);
/* 62 */         if (object.intValue() == id) return i; 
/*    */       } 
/*    */     } 
/* 65 */     return 0;
/*    */   }
/*    */ }


/* Location:              C:\Program Files\JetBrains\WebStorm 2021.1\plugins\NodeJS\lib\NodeJS.jar!\com\jetbrains\nodejs\run\profile\heap\view\models\V8HeapBiggestObjectTreeTableModel.class
 * Java compiler version: 11 (55.0)
 * JD-Core Version:       1.1.3
 */