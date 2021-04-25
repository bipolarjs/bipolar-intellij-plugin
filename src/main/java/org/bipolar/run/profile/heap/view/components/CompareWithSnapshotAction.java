/*     */ package org.bipolar.run.profile.heap.view.components;
/*     */ 
/*     */ import com.intellij.icons.AllIcons;
/*     */ import com.intellij.openapi.actionSystem.AnActionEvent;
/*     */ import com.intellij.openapi.diagnostic.Logger;
/*     */ import com.intellij.openapi.fileChooser.FileChooserDescriptorFactory;
/*     */ import com.intellij.openapi.progress.ProgressIndicator;
/*     */ import com.intellij.openapi.progress.ProgressManager;
/*     */ import com.intellij.openapi.progress.Task;
/*     */ import com.intellij.openapi.project.DumbAwareAction;
/*     */ import com.intellij.openapi.project.Project;
/*     */ import com.intellij.openapi.ui.MessageType;
/*     */ import com.intellij.openapi.util.io.FileUtil;
/*     */ import com.intellij.openapi.vfs.VirtualFile;
/*     */ import com.intellij.util.Consumer;
/*     */ import org.bipolar.NodeJSBundle;
/*     */ import org.bipolar.run.profile.heap.TempFiles;
/*     */ import org.bipolar.run.profile.heap.V8CachingReader;
/*     */ import org.bipolar.run.profile.heap.calculation.ByteArrayWrapper;
/*     */ import org.bipolar.run.profile.heap.calculation.V8HeapProcessor;
/*     */ import org.bipolar.run.profile.heap.calculation.diff.ShowSnapshotDiffDialog;
/*     */ import org.bipolar.run.profile.heap.calculation.diff.V8DiffCachingReader;
/*     */ import org.bipolar.run.profile.heap.calculation.diff.V8HeapDiffCalculator;
/*     */ import org.bipolar.run.profile.heap.calculation.diff.V8HeapDiffComponentPartner;
/*     */ import org.bipolar.run.profile.heap.view.main.OpenSnapshotWorker;
/*     */ import org.bipolar.run.profile.settings.NodeProfilingSettings;
/*     */ import java.io.File;
/*     */ import java.io.IOException;
/*     */
import org.jetbrains.annotations.Nls;
/*     */ import org.jetbrains.annotations.NotNull;
/*     */ import org.jetbrains.annotations.Nullable;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class CompareWithSnapshotAction
/*     */   extends DumbAwareAction
/*     */ {
/*  39 */   private static final Logger LOG = Logger.getInstance(CompareWithSnapshotAction.class);
/*     */   
/*     */   private final V8CachingReader myReader;
/*     */ 
/*     */   
/*     */   public CompareWithSnapshotAction(@NotNull V8CachingReader reader, @NotNull Project project, @Nls String name) {
/*  45 */     super(NodeJSBundle.message("action.CompareWithSnapshotAction.compare.with.text", new Object[0]),
/*  46 */         NodeJSBundle.message("action.CompareWithSnapshotAction.compare.with.another.snapshot.description", new Object[0]), AllIcons.Actions.Diff);
/*  47 */     this.myReader = reader;
/*  48 */     this.myProject = project;
/*  49 */     this.myName = name;
/*     */   } @NotNull
/*     */   private final Project myProject; @Nls
/*     */   private final String myName;
/*     */   public void actionPerformed(@NotNull AnActionEvent e) {
/*  54 */     if (e == null) $$$reportNull$$$0(2);
/*     */     
/*  56 */     ShowSnapshotDiffDialog dialog = new ShowSnapshotDiffDialog(FileChooserDescriptorFactory.createSingleFileDescriptor("heapsnapshot"), this.myProject, NodeJSBundle.message("profile.CompareWithSnapshot.snapshot_was_taken.text", new Object[0]));
/*  57 */     VirtualFile[] files = dialog.choose(this.myProject, new VirtualFile[0]);
/*     */     
/*  59 */     if (files.length != 1) {
/*     */       return;
/*     */     }
/*  62 */     VirtualFile file = files[0];
/*     */     
/*  64 */     if (file != null) {
/*  65 */       File changedFile = new File(file.getPath());
/*  66 */       if (FileUtil.filesEqual(changedFile, this.myReader.getOriginalFile())) {
/*  67 */         NodeProfilingSettings.HEAP_NOTIFICATION_GROUP
/*  68 */           .createNotification(NodeJSBundle.message("profile.compare_snapshot_with_itself.action.name", new Object[0]), MessageType.WARNING)
/*  69 */           .notify(this.myProject);
/*     */         
/*     */         return;
/*     */       } 
/*  73 */       CalculateSecondAndDiff calculateSecondAndDiff = new CalculateSecondAndDiff(this.myProject, changedFile, this.myReader, this.myName, file.getNameWithoutExtension(), !dialog.isAfter());
/*  74 */       ProgressManager.getInstance().run((Task)calculateSecondAndDiff);
/*     */     } 
/*     */   }
/*     */   
/*     */   private static class CalculateSecondAndDiff
/*     */     extends Task.Backgroundable {
/*     */     @NotNull
/*     */     private final File myFile;
/*     */     @NotNull
/*     */     private final V8CachingReader myBaseReader;
/*     */     @Nls
/*     */     private final String myName;
/*     */     @Nls
/*     */     private final String myChangedName;
/*     */     private final boolean mySwitchSnapshots;
/*     */     private final Consumer<String> myNotificator;
/*     */     private V8CachingReader mySecondReader;
/*     */     private V8DiffCachingReader myDiffCachingReader;
/*     */     private V8CachingReader myChangedForDiffReader;
/*     */     private V8CachingReader myBaseForDiffReader;
/*     */     
/*     */     CalculateSecondAndDiff(@Nullable Project project, @NotNull File file, @NotNull V8CachingReader baseReader, @Nls String name, @Nls String changedName, boolean switchSnapshots) {
/*  96 */       super(project, NodeJSBundle.message("progress.title.processing.heap.snapshot.building.snapshots.diff", new Object[] { file.getName() }));
/*  97 */       this.myFile = file;
/*  98 */       this.myBaseReader = baseReader;
/*  99 */       this.myName = name;
/* 100 */       this.myChangedName = changedName;
/* 101 */       this.mySwitchSnapshots = switchSnapshots;
/* 102 */       this.myNotificator = OpenSnapshotWorker.createNotificator(this.myProject);
/*     */     }
/*     */ 
/*     */ 
/*     */     
/*     */     public void run(@NotNull ProgressIndicator indicator) {
/* 108 */       if (indicator == null) $$$reportNull$$$0(2);  try { V8HeapProcessor processor = new V8HeapProcessor(this.myNotificator, this.myFile, this.myBaseReader.isShowHidden(), indicator);
/* 109 */         this.mySecondReader = processor.getFromCacheOrProcess();
/*     */         
/* 111 */         V8HeapProcessor processorChangedForDiff = new V8HeapProcessor(this.myNotificator, this.myFile, this.myBaseReader.isShowHidden(), indicator);
/* 112 */         V8HeapProcessor baseForDiffProcessor = new V8HeapProcessor(this.myNotificator, this.myBaseReader.getOriginalFile(), this.myBaseReader.isShowHidden(), indicator);
/*     */         
/* 114 */         if (this.mySwitchSnapshots) {
/* 115 */           this.myBaseForDiffReader = processorChangedForDiff.getFromCacheOrProcess();
/* 116 */           this.myChangedForDiffReader = baseForDiffProcessor.getFromCacheOrProcess();
/*     */         } else {
/* 118 */           this.myBaseForDiffReader = baseForDiffProcessor.getFromCacheOrProcess();
/* 119 */           this.myChangedForDiffReader = processorChangedForDiff.getFromCacheOrProcess();
/*     */         } 
/*     */         
/* 122 */         V8HeapDiffCalculator diffCalculator = new V8HeapDiffCalculator(this.myBaseForDiffReader, this.myChangedForDiffReader, new TempFiles("v8"));
/* 123 */         diffCalculator.execute();
/* 124 */         this
/*     */ 
/*     */ 
/*     */ 
/*     */           
/* 129 */           .myDiffCachingReader = new V8DiffCachingReader(this.myBaseForDiffReader, this.myChangedForDiffReader, diffCalculator.getBaseSnapshotIds(), diffCalculator.getChangedSnapshotIds(), diffCalculator.getBaseSnapshotSizes(), diffCalculator.getChangedSnapshotSizes(), diffCalculator.getAggregatesViewDiff(), diffCalculator.getBiggestObjectsDiff());
/* 130 */         this.myDiffCachingReader.prepare(); }
/*     */       
/* 132 */       catch (IOException e)
/* 133 */       { CompareWithSnapshotAction.LOG.info(e);
/* 134 */         this.myNotificator.consume(e.getMessage());
/* 135 */         indicator.cancel(); }
/*     */       
/* 137 */       catch (ClassNotFoundException e)
/* 138 */       { CompareWithSnapshotAction.LOG.info(e);
/* 139 */         this.myNotificator.consume(e.getMessage());
/* 140 */         indicator.cancel(); }
/*     */     
/*     */     }
/*     */ 
/*     */     
/*     */     public void onSuccess() {
/* 146 */       ByteArrayWrapper baseDigest = this.myBaseReader.getDigest();
/* 147 */       ByteArrayWrapper changedDigest = this.mySecondReader.getDigest();
/* 148 */       byte[] bytes = new byte[(baseDigest.getData()).length + (changedDigest.getData()).length];
/* 149 */       if (this.mySwitchSnapshots) {
/* 150 */         System.arraycopy(changedDigest.getData(), 0, bytes, 0, (changedDigest.getData()).length);
/* 151 */         System.arraycopy(baseDigest.getData(), 0, bytes, (changedDigest.getData()).length, (baseDigest.getData()).length);
/*     */       } else {
/* 153 */         System.arraycopy(baseDigest.getData(), 0, bytes, 0, (baseDigest.getData()).length);
/* 154 */         System.arraycopy(changedDigest.getData(), 0, bytes, (baseDigest.getData()).length, (changedDigest.getData()).length);
/*     */       } 
/*     */       
/*     */       try {
/* 158 */         V8HeapComponent heapComponent = V8HeapComponent.getInstance(this.myProject);
/* 159 */         heapComponent.showMe(this.mySecondReader.getDigest(), new V8HeapComponentPartner(this.mySecondReader, this.myChangedName), this.myChangedName, null);
/*     */         
/* 161 */         ByteArrayWrapper digest = new ByteArrayWrapper(bytes);
/* 162 */         if (heapComponent.activateIfOpen(digest))
/*     */           return; 
/* 164 */         if (this.mySwitchSnapshots) {
/* 165 */           V8HeapDiffComponentPartner partner = new V8HeapDiffComponentPartner(this.myProject, this.myDiffCachingReader, this.myChangedName, this.myName);
/* 166 */           heapComponent.showMe(digest, (HeapViewCreatorPartner<V8HeapTreeTable>)partner, this.myChangedName + " -> " + this.myChangedName, AllIcons.Actions.Diff);
/*     */         } else {
/* 168 */           V8HeapDiffComponentPartner partner = new V8HeapDiffComponentPartner(this.myProject, this.myDiffCachingReader, this.myName, this.myChangedName);
/* 169 */           heapComponent.showMe(digest, (HeapViewCreatorPartner<V8HeapTreeTable>)partner, this.myName + " -> " + this.myName, AllIcons.Actions.Diff);
/*     */         }
/*     */       
/* 172 */       } catch (IOException e) {
/* 173 */         CompareWithSnapshotAction.LOG.info(e);
/* 174 */         this.myNotificator.consume(e.getMessage());
/*     */       } 
/*     */     }
/*     */   }
/*     */ }


/* Location:              C:\Program Files\JetBrains\WebStorm 2021.1\plugins\NodeJS\lib\NodeJS.jar!\com\jetbrains\nodejs\run\profile\heap\view\components\CompareWithSnapshotAction.class
 * Java compiler version: 11 (55.0)
 * JD-Core Version:       1.1.3
 */