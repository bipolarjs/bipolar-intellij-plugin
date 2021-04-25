/*     */ package org.bipolar.run.profile.heap.view.actions;
/*     */ 
/*     */ import com.intellij.icons.AllIcons;
/*     */ import com.intellij.idea.ActionsBundle;
/*     */ import com.intellij.lang.javascript.index.JavaScriptIndex;
/*     */ import com.intellij.navigation.NavigationItem;
/*     */ import com.intellij.openapi.actionSystem.AnAction;
/*     */ import com.intellij.openapi.actionSystem.AnActionEvent;
/*     */ import com.intellij.openapi.project.Project;
/*     */ import com.intellij.openapi.ui.MessageType;
/*     */ import com.intellij.pom.Navigatable;
/*     */ import com.intellij.psi.PsiDocumentManager;
/*     */ import com.intellij.ui.treeStructure.treetable.TreeTable;
/*     */ import org.bipolar.NodeJSBundle;
/*     */ import org.bipolar.run.profile.V8Utils;
/*     */ import org.bipolar.run.profile.heap.V8CachingReader;
/*     */ import org.bipolar.run.profile.heap.data.V8HeapEdge;
/*     */ import org.bipolar.run.profile.heap.data.V8HeapEntry;
/*     */ import org.bipolar.run.profile.heap.data.V8HeapGraphEdgeType;
/*     */ import org.bipolar.run.profile.heap.data.V8HeapNodeType;
/*     */ import org.bipolar.run.profile.settings.NodeProfilingSettings;
/*     */ import java.util.ArrayList;
/*     */ import java.util.Collections;
/*     */ import java.util.List;
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
/*     */ public class GoToSourceAction
/*     */   extends AnAction
/*     */ {
/*     */   @NotNull
/*     */   private final V8CachingReader myReader;
/*     */   private TreeTable myTable;
/*     */   
/*     */   public GoToSourceAction(@NotNull V8CachingReader reader, TreeTable table) {
/*  51 */     super(ActionsBundle.actionText("EditSource").replace("_", ""), ActionsBundle.actionDescription("EditSource"), AllIcons.Actions.EditSource);
/*     */     
/*  53 */     this.myReader = reader;
/*  54 */     this.myTable = table;
/*     */   }
/*     */   
/*     */   public void setTable(TreeTable table) {
/*  58 */     this.myTable = table;
/*     */   }
/*     */ 
/*     */   
/*     */   public void update(@NotNull AnActionEvent e) {
/*  63 */     if (e == null) $$$reportNull$$$0(1);  EntryDataGetter getter = new EntryDataGetter(e.getDataContext(), this.myTable);
/*  64 */     V8HeapEntry data = getter.getData();
/*  65 */     e.getPresentation().setEnabled((data != null && data.getId() != 0L && getter.getProject() != null && !getter.isUnreachable() && (V8HeapNodeType.kObject
/*  66 */         .equals(data.getType()) || V8HeapNodeType.kClosure.equals(data.getType()) || V8HeapNodeType.kString
/*  67 */         .equals(data.getType()))));
/*     */   }
/*     */   
/*     */   public void actionPerformed(@NotNull AnActionEvent e) {
/*     */     NavigationItem[] items;
/*  72 */     if (e == null) $$$reportNull$$$0(2);  EntryDataGetter getter = new EntryDataGetter(e.getDataContext(), this.myTable);
/*  73 */     Project project = getter.getProject();
/*  74 */     if (project == null)
/*  75 */       return;  V8HeapEntry entry = getter.getData();
/*  76 */     if (entry == null)
/*  77 */       return;  boolean unreachable = getter.isUnreachable();
/*  78 */     if (unreachable)
/*  79 */       return;  Long edgeId = getter.getSelectedEdgeId();
/*     */     
/*  81 */     PsiDocumentManager.getInstance(project).commitAllDocuments();
/*  82 */     JavaScriptIndex index = JavaScriptIndex.getInstance(project);
/*     */     
/*  84 */     V8HeapEdge edge = (edgeId == null) ? null : this.myReader.getEdge(edgeId.longValue());
/*  85 */     if (edge != null && (V8HeapGraphEdgeType.kProperty.equals(edge.getType()) || V8HeapGraphEdgeType.kContextVariable.equals(edge.getType()))) {
/*  86 */       items = index.getSymbolsByName(this.myReader.getString(edge.getNameId()), true);
/*     */     } else {
/*  88 */       items = index.getSymbolsByName(this.myReader.getString(entry.getNameId()), true);
/*     */     } 
/*  90 */     if (items.length == 0) {
/*  91 */       NodeProfilingSettings.HEAP_NOTIFICATION_GROUP
/*  92 */         .createNotification(NodeJSBundle.message("profile.GoToSourceAction.notification.not_found.content", new Object[0]), MessageType.INFO)
/*  93 */         .notify(project);
/*     */       return;
/*     */     } 
/*  96 */     if (items.length == 1) {
/*  97 */       items[0].navigate(true);
/*     */       
/*     */       return;
/*     */     } 
/* 101 */     List<Navigatable> list = new ArrayList<>(items.length);
/* 102 */     Collections.addAll(list, (Object[])items);
/* 103 */     V8Utils.selectAndNavigate(e, list, item -> item.navigate(true), null);
/*     */   }
/*     */ }


/* Location:              C:\Program Files\JetBrains\WebStorm 2021.1\plugins\NodeJS\lib\NodeJS.jar!\com\jetbrains\nodejs\run\profile\heap\view\actions\GoToSourceAction.class
 * Java compiler version: 11 (55.0)
 * JD-Core Version:       1.1.3
 */