/*    */ package org.bipolar.mocha.execution;
/*    */ 
/*    */ import com.intellij.javascript.testFramework.util.TestFullNameView;
/*    */ import com.intellij.lang.javascript.JavaScriptBundle;
/*    */ import com.intellij.openapi.project.Project;
/*    */ import javax.swing.JComponent;
/*    */ import javax.swing.JPanel;
/*    */ import org.jetbrains.annotations.NotNull;
/*    */ 
/*    */ public class MochaTestView extends MochaTestKindView {
/*    */   private final MochaTestFileView myTestFileView;
/*    */   private final TestFullNameView myTestFullNameView;
/*    */   private final JPanel myPanel;
/*    */   
/*    */   public MochaTestView(@NotNull Project project) {
/* 16 */     this.myTestFileView = new MochaTestFileView(project);
/* 17 */     this.myTestFullNameView = new TestFullNameView();
/* 18 */     this
/*    */       
/* 20 */       .myPanel = this.myTestFileView.getFormBuilder().addLabeledComponent(JavaScriptBundle.message("rc.testOrSuiteScope.test.label", new Object[0]), this.myTestFullNameView.getComponent()).getPanel();
/*    */   }
/*    */ 
/*    */   
/*    */   @NotNull
/*    */   public JComponent getComponent() {
/* 26 */     if (this.myPanel == null) $$$reportNull$$$0(1);  return this.myPanel;
/*    */   }
/*    */ 
/*    */   
/*    */   public void resetFrom(@NotNull MochaRunSettings settings) {
/* 31 */     if (settings == null) $$$reportNull$$$0(2);  this.myTestFileView.resetFrom(settings);
/* 32 */     this.myTestFullNameView.setNames(settings.getTestNames());
/*    */   }
/*    */ 
/*    */   
/*    */   public void applyTo(@NotNull MochaRunSettings.Builder builder) {
/* 37 */     if (builder == null) $$$reportNull$$$0(3);  this.myTestFileView.applyTo(builder);
/* 38 */     builder.setTestNames(this.myTestFullNameView.getNames());
/*    */   }
/*    */ }


/* Location:              C:\Program Files\JetBrains\WebStorm 2021.1\plugins\NodeJS\lib\NodeJS.jar!\com\jetbrains\nodejs\mocha\execution\MochaTestView.class
 * Java compiler version: 11 (55.0)
 * JD-Core Version:       1.1.3
 */