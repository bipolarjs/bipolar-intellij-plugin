/*    */ package org.bipolar.run.profile.heap.view.main;
/*    */ 
/*    */ import com.intellij.openapi.fileChooser.FileChooserDescriptor;
/*    */ import com.intellij.openapi.fileChooser.ex.FileChooserDialogImpl;
/*    */ import com.intellij.openapi.project.Project;
/*    */ import com.intellij.ui.components.JBCheckBox;
/*    */ import com.intellij.ui.components.JBPanel;
/*    */ import org.bipolar.NodeJSBundle;
/*    */ import java.awt.BorderLayout;
/*    */ import java.awt.Component;
/*    */ import javax.swing.JComponent;
/*    */
import org.jetbrains.annotations.NotNull;
/*    */ import org.jetbrains.annotations.Nullable;
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
/*    */ public class LoadSnapshotDialog
/*    */   extends FileChooserDialogImpl
/*    */ {
/*    */   private JBCheckBox myShowHidden;
/*    */   
/*    */   public LoadSnapshotDialog(@NotNull FileChooserDescriptor descriptor, @Nullable Project project) {
/* 38 */     super(descriptor, project);
/*    */   }
/*    */ 
/*    */   
/*    */   @Nullable
/*    */   protected JComponent createSouthPanel() {
/* 44 */     JComponent component = super.createSouthPanel();
/* 45 */     JBPanel panel = new JBPanel(new BorderLayout());
/* 46 */     this.myShowHidden = new JBCheckBox(NodeJSBundle.message("checkbox.show.hidden.data", new Object[0]));
/* 47 */     panel.add((Component)this.myShowHidden, "Center");
/* 48 */     panel.add(component, "South");
/* 49 */     return (JComponent)panel;
/*    */   }
/*    */   
/*    */   public boolean showHiddenData() {
/* 53 */     return this.myShowHidden.isSelected();
/*    */   }
/*    */ }


/* Location:              C:\Program Files\JetBrains\WebStorm 2021.1\plugins\NodeJS\lib\NodeJS.jar!\com\jetbrains\nodejs\run\profile\heap\view\main\LoadSnapshotDialog.class
 * Java compiler version: 11 (55.0)
 * JD-Core Version:       1.1.3
 */