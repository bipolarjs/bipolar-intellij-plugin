/*     */ package org.bipolar.run.profile.heap.view.components;
/*     */ 
/*     */ import com.intellij.ide.actions.ContextHelpAction;
/*     */ import com.intellij.openapi.Disposable;
/*     */ import com.intellij.openapi.actionSystem.AnAction;
/*     */ import com.intellij.openapi.actionSystem.CommonShortcuts;
/*     */ import com.intellij.openapi.actionSystem.DefaultActionGroup;
/*     */ import com.intellij.openapi.application.ApplicationManager;
/*     */ import com.intellij.openapi.project.Project;
/*     */ import com.intellij.util.Consumer;
/*     */ import org.bipolar.run.profile.V8Utils;
/*     */ import org.bipolar.run.profile.cpu.view.ProfilingView;
/*     */ import org.bipolar.run.profile.cpu.view.V8ProfilingMainComponent;
/*     */ import org.bipolar.run.profile.heap.V8CachingReader;
/*     */ import org.bipolar.run.profile.heap.calculation.ByteArrayWrapper;
/*     */ import org.bipolar.run.profile.heap.data.V8HeapEdge;
/*     */ import org.bipolar.run.profile.heap.data.V8HeapEntry;
/*     */ import java.awt.Component;
/*     */ import java.io.IOException;
/*     */ import java.util.List;
/*     */ import javax.swing.JComponent;
/*     */ import javax.swing.tree.TreePath;
/*     */
import org.jetbrains.annotations.Nls;
/*     */ import org.jetbrains.annotations.NotNull;
/*     */ import org.jetbrains.annotations.Nullable;
/*     */ 
/*     */ 
/*     */ public class V8HeapComponentPartner
/*     */   implements HeapViewCreatorPartner<V8HeapTreeTable>
/*     */ {
/*     */   private final V8CachingReader myReader;
/*     */   @Nls
/*     */   private final String myName;
/*     */   private V8ProfilingMainComponent.MyController<V8HeapTreeTable> myController;
/*     */   private V8MainTreeNavigator myMainTreeNavigator;
/*     */   
/*     */   public V8HeapComponentPartner(V8CachingReader reader, @Nls String name) {
/*  38 */     this.myReader = reader;
/*  39 */     this.myName = name;
/*     */   }
/*     */ 
/*     */   
/*     */   public JComponent wrapWithStandardActions(ProfilingView<V8HeapTreeTable> view, AnAction closeAction) {
/*  44 */     DefaultActionGroup group = new DefaultActionGroup();
/*  45 */     view.addActions(group);
/*  46 */     ContextHelpAction helpAction = new ContextHelpAction("reference.tool.window.v8.heap");
/*  47 */     group.add((AnAction)helpAction);
/*  48 */     JComponent component = view.getMainComponent();
/*  49 */     Component[] components = component.getComponents();
/*  50 */     for (Component component1 : components) {
/*  51 */       helpAction.registerCustomShortcutSet(CommonShortcuts.getContextHelp(), (JComponent)component1);
/*     */     }
/*  53 */     group.add(closeAction);
/*  54 */     return V8Utils.wrapWithActions(view.getMainComponent(), group);
/*     */   }
/*     */ 
/*     */   
/*     */   public void addViews(Project project, List<ProfilingView<V8HeapTreeTable>> list, Disposable disposable) {
/*  59 */     final V8HeapContainmentView containmentView = new V8HeapContainmentView(project, this.myReader, this.myName, disposable);
/*  60 */     final V8MainTreeNavigator navigator = containmentView.getTableWithRetainers().getMainTreeNavigator();
/*     */     
/*  62 */     list.add(containmentView);
/*  63 */     this.myMainTreeNavigator = new V8MainTreeNavigator()
/*     */       {
/*     */         public boolean navigateTo(@NotNull TreePath path) {
/*  66 */           if (path == null) $$$reportNull$$$0(0);  if (navigator.navigateTo(path)) {
/*  67 */             V8HeapComponentPartner.this.myController.showTab(containmentView.getName());
/*  68 */             return true;
/*     */           } 
/*  70 */           return false;
/*     */         }
/*     */ 
/*     */         
/*     */         public boolean navigateTo(@NotNull V8HeapEntry node, @Nullable V8HeapEdge edgeToNode) {
/*  75 */           if (node == null) $$$reportNull$$$0(1);  if (navigator.navigateTo(node, edgeToNode)) {
/*  76 */             V8HeapComponentPartner.this.myController.showTab(containmentView.getName());
/*  77 */             return true;
/*     */           } 
/*  79 */           return false;
/*     */         }
/*     */       };
/*  82 */     list.add(new V8HeapBiggestObjectsView(project, this.myReader, this.myMainTreeNavigator, disposable));
/*  83 */     list.add(new HeapAggregatesView(project, this.myReader, this.myMainTreeNavigator, disposable));
/*     */   }
/*     */ 
/*     */   
/*     */   @Nullable
/*     */   public V8MainTreeNavigator getNavigator() {
/*  89 */     return this.myMainTreeNavigator;
/*     */   }
/*     */ 
/*     */   
/*     */   public void reportInvolvedSnapshots(@NotNull Consumer<ByteArrayWrapper> digestConsumer) {
/*  94 */     if (digestConsumer == null) $$$reportNull$$$0(0);  digestConsumer.consume(this.myReader.getDigest());
/*     */   }
/*     */ 
/*     */   
/*     */   public String errorCreated() {
/*  99 */     return null;
/*     */   }
/*     */ 
/*     */   
/*     */   public void announceController(V8ProfilingMainComponent.MyController<V8HeapTreeTable> controller) {
/* 104 */     this.myController = controller;
/*     */   }
/*     */ 
/*     */   
/*     */   public void close() {
/* 109 */     ApplicationManager.getApplication().executeOnPooledThread(() -> {
/*     */           
/*     */           try {
/*     */             this.myReader.close();
/* 113 */           } catch (IOException iOException) {}
/*     */         });
/*     */   }
/*     */ }


/* Location:              C:\Program Files\JetBrains\WebStorm 2021.1\plugins\NodeJS\lib\NodeJS.jar!\com\jetbrains\nodejs\run\profile\heap\view\components\V8HeapComponentPartner.class
 * Java compiler version: 11 (55.0)
 * JD-Core Version:       1.1.3
 */