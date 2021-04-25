/*    */ package org.bipolar.run.profile.heap.view.main;
/*    */ 
/*    */ import com.intellij.ide.util.PropertiesComponent;
/*    */ import com.intellij.openapi.fileChooser.FileSaverDescriptor;
/*    */ import com.intellij.openapi.fileChooser.ex.FileSaverDialogImpl;
/*    */ import com.intellij.openapi.project.Project;
/*    */ import com.intellij.ui.components.JBCheckBox;
/*    */ import com.intellij.ui.components.JBPanel;
/*    */ import com.intellij.util.ui.DialogUtil;
/*    */ import org.bipolar.NodeJSBundle;
/*    */ import java.awt.BorderLayout;
/*    */ import java.awt.Component;
/*    */ import javax.swing.AbstractButton;
/*    */ import javax.swing.JComponent;
/*    */
import org.jetbrains.annotations.NotNull;
/*    */ import org.jetbrains.annotations.Nullable;
/*    */ 
/*    */ public class SaveSnapshotDialog
/*    */   extends FileSaverDialogImpl
/*    */ {
/*    */   @Nullable
/*    */   private final Project myProject;
/*    */   private JBCheckBox myOpenCreatedSnapshot;
/*    */   private JBCheckBox myShowHidden;
/*    */   
/*    */   public SaveSnapshotDialog(@NotNull FileSaverDescriptor descriptor, @Nullable Project project) {
/* 27 */     super(descriptor, project);
/* 28 */     setTitle(NodeJSBundle.message("node.js.v8.heap.take.snapshot.dialog.title", new Object[0]));
/* 29 */     this.myProject = project;
/*    */   }
/*    */ 
/*    */   
/*    */   @Nullable
/*    */   protected JComponent createSouthPanel() {
/* 35 */     JComponent component = super.createSouthPanel();
/* 36 */     JBPanel panel = new JBPanel(new BorderLayout());
/* 37 */     this.myOpenCreatedSnapshot = new JBCheckBox(NodeJSBundle.message("node.js.v8.heap.take.snapshot.dialog.open.snapshot", new Object[0]));
/* 38 */     DialogUtil.registerMnemonic((AbstractButton)this.myOpenCreatedSnapshot, '&');
/* 39 */     this.myOpenCreatedSnapshot.setSelected(PropertiesComponent.getInstance(this.myProject).getBoolean("Node.Profiling.Open.Snapshot.After.Save", true));
/* 40 */     panel.add((Component)this.myOpenCreatedSnapshot, "North");
/* 41 */     this.myShowHidden = new JBCheckBox(NodeJSBundle.message("node.js.v8.heap.take.snapshot.dialog.show.hidden", new Object[0]));
/* 42 */     DialogUtil.registerMnemonic((AbstractButton)this.myShowHidden, '&');
/* 43 */     panel.add((Component)this.myShowHidden, "Center");
/* 44 */     panel.add(component, "South");
/* 45 */     return (JComponent)panel;
/*    */   }
/*    */   
/*    */   public boolean showHiddenData() {
/* 49 */     return this.myShowHidden.isSelected();
/*    */   }
/*    */   
/*    */   public boolean openCreatedSnapshot() {
/* 53 */     return this.myOpenCreatedSnapshot.isSelected();
/*    */   }
/*    */ 
/*    */   
/*    */   protected void doOKAction() {
/* 58 */     PropertiesComponent.getInstance(this.myProject).setValue("Node.Profiling.Open.Snapshot.After.Save", 
/* 59 */         String.valueOf(this.myOpenCreatedSnapshot.isSelected()));
/* 60 */     super.doOKAction();
/*    */   }
/*    */ }


/* Location:              C:\Program Files\JetBrains\WebStorm 2021.1\plugins\NodeJS\lib\NodeJS.jar!\com\jetbrains\nodejs\run\profile\heap\view\main\SaveSnapshotDialog.class
 * Java compiler version: 11 (55.0)
 * JD-Core Version:       1.1.3
 */