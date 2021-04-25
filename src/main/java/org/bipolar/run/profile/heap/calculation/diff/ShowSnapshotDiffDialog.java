/*    */ package org.bipolar.run.profile.heap.calculation.diff;
/*    */ 
/*    */ import com.intellij.openapi.fileChooser.FileChooserDescriptor;
/*    */ import com.intellij.openapi.fileChooser.ex.FileChooserDialogImpl;
/*    */ import com.intellij.openapi.project.Project;
/*    */ import com.intellij.ui.components.JBLabel;
/*    */ import com.intellij.ui.components.JBPanel;
/*    */ import com.intellij.ui.components.JBRadioButton;
/*    */ import com.intellij.util.ui.DialogUtil;
/*    */ import org.bipolar.NodeJSBundle;
/*    */ import java.awt.BorderLayout;
/*    */ import java.awt.Component;
/*    */ import javax.swing.AbstractButton;
/*    */ import javax.swing.ButtonGroup;
/*    */ import javax.swing.JComponent;
/*    */
import org.jetbrains.annotations.Nls;
/*    */ import org.jetbrains.annotations.NotNull;
/*    */ import org.jetbrains.annotations.Nullable;
/*    */ 
/*    */ public class ShowSnapshotDiffDialog extends FileChooserDialogImpl {
/*    */   private JBRadioButton myAfterRadio;
/*    */   private JBRadioButton myBeforeRadio;
/*    */   @Nls
/*    */   private final String myText;
/*    */   
/*    */   public ShowSnapshotDiffDialog(@NotNull FileChooserDescriptor descriptor, @Nullable Project project, @NotNull @Nls String captionText) {
/* 27 */     super(descriptor, project);
/* 28 */     this.myText = captionText;
/*    */   }
/*    */ 
/*    */   
/*    */   @Nullable
/*    */   protected JComponent createNorthPanel() {
/* 34 */     JBPanel panel = new JBPanel(new BorderLayout());
/* 35 */     this.myAfterRadio = new JBRadioButton(NodeJSBundle.message("radio.after.current", new Object[0]));
/* 36 */     this.myBeforeRadio = new JBRadioButton(NodeJSBundle.message("radio.before.current", new Object[0]));
/* 37 */     DialogUtil.registerMnemonic((AbstractButton)this.myAfterRadio, '&');
/* 38 */     DialogUtil.registerMnemonic((AbstractButton)this.myBeforeRadio, '&');
/* 39 */     ButtonGroup buttonGroup = new ButtonGroup();
/* 40 */     buttonGroup.add((AbstractButton)this.myAfterRadio);
/* 41 */     buttonGroup.add((AbstractButton)this.myBeforeRadio);
/* 42 */     this.myAfterRadio.setSelected(true);
/*    */     
/* 44 */     panel.add((Component)new JBLabel(this.myText), "North");
/* 45 */     panel.add((Component)this.myAfterRadio, "Center");
/* 46 */     panel.add((Component)this.myBeforeRadio, "South");
/*    */     
/* 48 */     return (JComponent)panel;
/*    */   }
/*    */   
/*    */   public boolean isAfter() {
/* 52 */     return this.myAfterRadio.isSelected();
/*    */   }
/*    */ }


/* Location:              C:\Program Files\JetBrains\WebStorm 2021.1\plugins\NodeJS\lib\NodeJS.jar!\com\jetbrains\nodejs\run\profile\heap\calculation\diff\ShowSnapshotDiffDialog.class
 * Java compiler version: 11 (55.0)
 * JD-Core Version:       1.1.3
 */