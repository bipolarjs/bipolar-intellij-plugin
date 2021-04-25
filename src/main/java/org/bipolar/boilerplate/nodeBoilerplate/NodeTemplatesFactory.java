/*    */ package org.bipolar.boilerplate.nodeBoilerplate;
/*    */ 
/*    */ import com.intellij.ide.util.projectWizard.WizardContext;
/*    */ import com.intellij.platform.ProjectTemplate;
/*    */ import com.intellij.platform.ProjectTemplatesFactory;
/*    */ import org.bipolar.boilerplate.express.ExpressAppProjectGenerator;
/*    */ import org.bipolar.boilerplate.npmInit.NpmInitProjectGenerator;
/*    */ import icons.JavaScriptLanguageIcons;
/*    */ import javax.swing.Icon;
/*    */
import org.jetbrains.annotations.NotNull;
/*    */ import org.jetbrains.annotations.Nullable;
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public class NodeTemplatesFactory
/*    */   extends ProjectTemplatesFactory
/*    */ {
/*    */   public String[] getGroups() {
/* 21 */     (new String[1])[0] = "JavaScript"; if (new String[1] == null) $$$reportNull$$$0(0);  return new String[1];
/*    */   }
/*    */ 
/*    */   
/*    */   public Icon getGroupIcon(String group) {
/* 26 */     return JavaScriptLanguageIcons.Nodejs.Nodejs;
/*    */   }
/*    */ 
/*    */   
/*    */   @NotNull
/*    */   public ProjectTemplate[] createTemplates(@Nullable String group, WizardContext context) {
/* 32 */     (new ProjectTemplate[2])[0] = (ProjectTemplate)new NpmInitProjectGenerator(); (new ProjectTemplate[2])[1] = (ProjectTemplate)new ExpressAppProjectGenerator(); if (new ProjectTemplate[2] == null) $$$reportNull$$$0(1);  return new ProjectTemplate[2];
/*    */   }
/*    */ }


/* Location:              C:\Program Files\JetBrains\WebStorm 2021.1\plugins\NodeJS\lib\NodeJS.jar!\com\jetbrains\nodejs\boilerplate\nodeBoilerplate\NodeTemplatesFactory.class
 * Java compiler version: 11 (55.0)
 * JD-Core Version:       1.1.3
 */