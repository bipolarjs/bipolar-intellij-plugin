/*    */ package org.bipolar.nodeunit.execution.ui;
/*    */ 
/*    */ import com.intellij.openapi.project.Project;
/*    */ import javax.swing.JComponent;
/*    */ import javax.swing.JTextField;
/*    */ import org.jetbrains.annotations.NotNull;
/*    */ import org.jetbrains.annotations.Nullable;
/*    */ 
/*    */ 
/*    */ class Context
/*    */ {
/*    */   @NotNull
/*    */   private final Project myProject;
/*    */   
/*    */   Context(@NotNull Project project) {
/* 16 */     this.myProject = project;
/*    */   } @Nullable
/*    */   private JComponent myAnchor; @Nullable
/*    */   private JTextField myWorkingDirectory; Context(@NotNull Project project, @NotNull JComponent anchor) {
/* 20 */     this.myProject = project;
/* 21 */     this.myAnchor = anchor;
/*    */   }
/*    */   
/*    */   Context(@NotNull Project project, @NotNull JComponent anchor, @NotNull JTextField workingDirectory) {
/* 25 */     this.myProject = project;
/* 26 */     this.myAnchor = anchor;
/* 27 */     this.myWorkingDirectory = workingDirectory;
/*    */   }
/*    */   @NotNull
/*    */   public Project getProject() {
/* 31 */     if (this.myProject == null) $$$reportNull$$$0(6);  return this.myProject;
/*    */   }
/*    */   @Nullable
/*    */   public JComponent getAnchor() {
/* 35 */     return this.myAnchor;
/*    */   }
/*    */   @Nullable
/*    */   public JTextField getWorkingDirectory() {
/* 39 */     return this.myWorkingDirectory;
/*    */   }
/*    */ }


/* Location:              C:\Program Files\JetBrains\WebStorm 2021.1\plugins\NodeJS\lib\NodeJS.jar!\com\jetbrains\nodejs\nodeunit\executio\\ui\Context.class
 * Java compiler version: 11 (55.0)
 * JD-Core Version:       1.1.3
 */