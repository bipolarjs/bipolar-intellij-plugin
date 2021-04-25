/*    */ package org.bipolar.run.profile.heap.calculation.diff;
/*    */ 
/*    */ import com.intellij.openapi.Disposable;
/*    */ import com.intellij.openapi.actionSystem.AnAction;
/*    */ import com.intellij.openapi.actionSystem.DefaultActionGroup;
/*    */ import com.intellij.openapi.application.ApplicationManager;
/*    */ import com.intellij.openapi.project.Project;
/*    */ import com.intellij.util.Consumer;
/*    */ import org.bipolar.run.profile.V8Utils;
/*    */ import org.bipolar.run.profile.cpu.view.ProfilingView;
/*    */ import org.bipolar.run.profile.cpu.view.V8ProfilingMainComponent;
/*    */ import org.bipolar.run.profile.heap.calculation.ByteArrayWrapper;
/*    */ import org.bipolar.run.profile.heap.view.components.HeapViewCreatorPartner;
/*    */ import org.bipolar.run.profile.heap.view.components.V8HeapTreeTable;
/*    */ import org.bipolar.run.profile.heap.view.components.V8MainTreeNavigator;
/*    */ import java.io.IOException;
/*    */ import java.util.List;
/*    */ import javax.swing.JComponent;
/*    */
import org.jetbrains.annotations.NotNull;
/*    */ import org.jetbrains.annotations.Nullable;
/*    */ 
/*    */ public class V8HeapDiffComponentPartner
/*    */   implements HeapViewCreatorPartner<V8HeapTreeTable>
/*    */ {
/*    */   private final Project myProject;
/*    */   @NotNull
/*    */   private final V8DiffCachingReader myReader;
/*    */   @NotNull
/*    */   private final String myBaseName;
/*    */   @NotNull
/*    */   private final String myChangedName;
/*    */   
/*    */   public V8HeapDiffComponentPartner(Project project, @NotNull V8DiffCachingReader reader, @NotNull String baseName, @NotNull String changedName) {
/* 34 */     this.myProject = project;
/* 35 */     this.myReader = reader;
/* 36 */     this.myBaseName = baseName;
/* 37 */     this.myChangedName = changedName;
/*    */   }
/*    */ 
/*    */   
/*    */   public JComponent wrapWithStandardActions(ProfilingView<V8HeapTreeTable> view, AnAction closeAction) {
/* 42 */     DefaultActionGroup group = new DefaultActionGroup();
/* 43 */     view.addActions(group);
/* 44 */     group.add(closeAction);
/* 45 */     return V8Utils.wrapWithActions(view.getMainComponent(), group);
/*    */   }
/*    */ 
/*    */   
/*    */   public void addViews(Project project, List<ProfilingView<V8HeapTreeTable>> list, Disposable disposable) {
/* 50 */     list.add(new V8HeapSummaryDiffComponent(this.myProject, this.myReader, this.myBaseName, this.myChangedName));
/* 51 */     list.add(new V8HeapBiggestObjectsDiffComponent(this.myProject, this.myReader, this.myBaseName, this.myChangedName));
/*    */   }
/*    */ 
/*    */   
/*    */   public String errorCreated() {
/* 56 */     return null;
/*    */   }
/*    */ 
/*    */ 
/*    */   
/*    */   public void announceController(V8ProfilingMainComponent.MyController<V8HeapTreeTable> controller) {}
/*    */ 
/*    */ 
/*    */   
/*    */   @Nullable
/*    */   public V8MainTreeNavigator getNavigator() {
/* 67 */     return null;
/*    */   }
/*    */ 
/*    */   
/*    */   public void reportInvolvedSnapshots(@NotNull Consumer<ByteArrayWrapper> digestConsumer) {
/* 72 */     if (digestConsumer == null) $$$reportNull$$$0(3);  digestConsumer.consume(this.myReader.getBaseReader().getDigest());
/* 73 */     digestConsumer.consume(this.myReader.getChangedReader().getDigest());
/*    */   }
/*    */ 
/*    */   
/*    */   public void close() {
/* 78 */     ApplicationManager.getApplication().executeOnPooledThread(() -> {
/*    */           
/*    */           try {
/*    */             this.myReader.close();
/* 82 */           } catch (IOException iOException) {}
/*    */         });
/*    */   }
/*    */ }


/* Location:              C:\Program Files\JetBrains\WebStorm 2021.1\plugins\NodeJS\lib\NodeJS.jar!\com\jetbrains\nodejs\run\profile\heap\calculation\diff\V8HeapDiffComponentPartner.class
 * Java compiler version: 11 (55.0)
 * JD-Core Version:       1.1.3
 */