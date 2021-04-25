/*     */ package org.bipolar.run.profile.heap.view.actions;
/*     */ 
/*     */ import com.intellij.icons.AllIcons;
/*     */ import com.intellij.openapi.actionSystem.AnActionEvent;
/*     */ import com.intellij.openapi.actionSystem.DataKey;
/*     */ import com.intellij.openapi.project.DumbAwareToggleAction;
/*     */ import com.intellij.openapi.project.Project;
/*     */ import com.intellij.openapi.ui.Messages;
/*     */ import com.intellij.openapi.util.text.StringUtil;
/*     */ import com.intellij.openapi.wm.ToolWindow;
/*     */ import com.intellij.openapi.wm.ToolWindowManager;
/*     */ import com.intellij.ui.treeStructure.treetable.TreeTable;
/*     */ import org.bipolar.NodeJSBundle;
/*     */ import org.bipolar.run.profile.heap.V8CachingReader;
/*     */ import org.bipolar.run.profile.heap.calculation.ByteArrayWrapper;
/*     */ import org.bipolar.run.profile.heap.data.V8HeapEntry;
/*     */ import org.bipolar.run.profile.heap.view.components.V8HeapComponent;
/*     */ import java.util.function.Supplier;
/*     */ import javax.swing.JComponent;
/*     */
import org.jetbrains.annotations.NotNull;
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
/*     */ public class MarkUnmarkAction
/*     */   extends DumbAwareToggleAction
/*     */ {
/*  44 */   public static final DataKey<V8HeapEntry> SELECTED_NODE = DataKey.create("V8_SELECTED_NODE");
/*  45 */   public static final DataKey<Boolean> UNREACHABLE_NODE = DataKey.create("V8_UNREACHABLE_NODE");
/*  46 */   public static final DataKey<Long> SELECTED_LINK = DataKey.create("V8_SELECTED_LINK");
/*  47 */   public static final DataKey<Runnable> REVALIDATION = DataKey.create("V8_REPAINT_REVALIDATE");
/*  48 */   public static final Supplier<String> MARK = NodeJSBundle.messagePointer("action.MarkUnmarkAction.text", new Object[0]);
/*     */   
/*  50 */   public static final Supplier<String> MARK_OBJECT_WITH_TEXT = NodeJSBundle.messagePointer("action.MarkUnmarkAction.description", new Object[0]);
/*     */   private final V8HeapComponent myHeapComponent;
/*     */   private final ByteArrayWrapper myDigest;
/*     */   private TreeTable myTable;
/*     */   
/*     */   public MarkUnmarkAction(@NotNull Project project, @NotNull V8CachingReader reader) {
/*  56 */     super(MARK, MARK_OBJECT_WITH_TEXT, AllIcons.Actions.SetDefault);
/*  57 */     this.myDigest = reader.getDigest();
/*  58 */     this.myHeapComponent = V8HeapComponent.getInstance(project);
/*     */   }
/*     */   
/*     */   public void setTable(TreeTable table) {
/*  62 */     this.myTable = table;
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean isSelected(@NotNull AnActionEvent e) {
/*  67 */     if (e == null) $$$reportNull$$$0(2);  EntryDataGetter getter = new EntryDataGetter(e.getDataContext(), this.myTable);
/*  68 */     V8HeapEntry data = getter.getData();
/*  69 */     e.getPresentation().setEnabled((data != null && data.getId() != 0L && getter.getRunnable() != null && getter
/*  70 */         .getProject() != null));
/*     */     
/*  72 */     return (data != null && this.myHeapComponent.getMark(this.myDigest, data) != null);
/*     */   }
/*     */ 
/*     */   
/*     */   public void setSelected(@NotNull AnActionEvent e, boolean state) {
/*  77 */     if (e == null) $$$reportNull$$$0(3);  EntryDataGetter getter = new EntryDataGetter(e.getDataContext(), this.myTable);
/*  78 */     V8HeapEntry entry = getter.getData();
/*     */     
/*  80 */     Runnable runnable = getter.getRunnable();
/*  81 */     Project project = getter.getProject();
/*  82 */     if (project == null)
/*     */       return; 
/*  84 */     if (entry != null && runnable != null) {
/*  85 */       markOrUnmark(entry, project, this.myHeapComponent, this.myDigest);
/*  86 */       runnable.run();
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   public static void markOrUnmark(@NotNull V8HeapEntry entry, @NotNull Project project, @NotNull V8HeapComponent heapComponent, @NotNull ByteArrayWrapper digest) {
/*  92 */     if (entry == null) $$$reportNull$$$0(4);  if (project == null) $$$reportNull$$$0(5);  if (heapComponent == null) $$$reportNull$$$0(6);  if (digest == null) $$$reportNull$$$0(7);  if (!StringUtil.isEmptyOrSpaces(heapComponent.getMark(digest, entry))) {
/*  93 */       heapComponent.markOrUnmark(digest, entry, "");
/*     */     } else {
/*     */       
/*  96 */       String text = Messages.showInputDialog(project, 
/*  97 */           NodeJSBundle.message("dialog.message.mark.node.with.text", new Object[] { Long.valueOf(entry.getSnapshotObjectId())
/*  98 */             }), NodeJSBundle.message("dialog.title.mark.heap.object", new Object[0]), Messages.getQuestionIcon());
/*  99 */       if (!StringUtil.isEmptyOrSpaces(text)) {
/* 100 */         heapComponent.markOrUnmark(digest, entry, text);
/*     */       }
/*     */     } 
/* 103 */     ToolWindow window = ToolWindowManager.getInstance(project).getToolWindow(V8HeapComponent.TOOL_WINDOW_TITLE.get());
/* 104 */     if (window != null) {
/* 105 */       JComponent component = window.getComponent();
/* 106 */       component.revalidate();
/* 107 */       component.repaint();
/*     */     } 
/*     */   }
/*     */ }


/* Location:              C:\Program Files\JetBrains\WebStorm 2021.1\plugins\NodeJS\lib\NodeJS.jar!\com\jetbrains\nodejs\run\profile\heap\view\actions\MarkUnmarkAction.class
 * Java compiler version: 11 (55.0)
 * JD-Core Version:       1.1.3
 */