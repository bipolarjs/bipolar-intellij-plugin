/*    */ package org.bipolar.mocha;
/*    */ 
/*    */ import com.intellij.ide.util.PropertiesComponent;
/*    */ import com.intellij.javascript.nodejs.util.NodePackage;
/*    */ import com.intellij.javascript.nodejs.util.NodePackageDescriptor;
/*    */ import com.intellij.javascript.testFramework.interfaces.mochaTdd.MochaTddFileStructure;
/*    */ import com.intellij.javascript.testFramework.interfaces.mochaTdd.MochaTddFileStructureBuilder;
/*    */ import com.intellij.lang.javascript.psi.JSFile;
/*    */ import com.intellij.lang.javascript.psi.JSTestFileType;
/*    */ import com.intellij.openapi.project.Project;
/*    */ import com.intellij.openapi.util.text.StringUtil;
/*    */ import com.intellij.util.containers.ContainerUtil;
/*    */ import java.util.Collections;
/*    */ import java.util.List;
/*    */ import org.jetbrains.annotations.NotNull;
/*    */ import org.jetbrains.annotations.Nullable;
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public final class MochaUtil
/*    */ {
/*    */   public static final String UI_BDD = "bdd";
/*    */   public static final String UI_TDD = "tdd";
/*    */   public static final String UI_EXPORTS = "exports";
/*    */   public static final String UI_QUNIT = "qunit";
/*    */   public static final String UI_REQUIRE = "require";
/*    */   public static final String UI_MOCHA_TYPESCRIPT = "mocha-typescript";
/*    */   public static final String NODE_PACKAGE_NAME = "mocha";
/*    */   private static final String VUE_CLI_SERVICE = "@vue/cli-service";
/*    */   public static final String ELECTRON_MOCHA_PACKAGE_NAME = "electron-mocha";
/*    */   public static final String MOCHA_WEBPACK_PACKAGE_NAME = "mocha-webpack";
/* 33 */   public static final NodePackageDescriptor PACKAGE_DESCRIPTOR = new NodePackageDescriptor(
/* 34 */       ContainerUtil.newArrayList((Object[])new String[] { "mocha-webpack", "electron-mocha", "mocha", "@vue/cli-service"
/* 35 */         }, ), Collections.singletonMap("@vue/cli-service", "@vue/cli-plugin-unit-mocha"), null);
/*    */ 
/*    */   
/*    */   private static final String MOCHA_PACKAGE_DIR__KEY = "nodejs.mocha.mocha_node_package_dir";
/*    */ 
/*    */ 
/*    */   
/*    */   @NotNull
/*    */   public static List<String> getMochaUiList() {
/* 44 */     if (ContainerUtil.newArrayList((Object[])new String[] { "bdd", "tdd", "exports", "qunit", "require", "mocha-typescript" }) == null) $$$reportNull$$$0(0);  return ContainerUtil.newArrayList((Object[])new String[] { "bdd", "tdd", "exports", "qunit", "require", "mocha-typescript" });
/*    */   }
/*    */   
/*    */   @Nullable
/*    */   public static String findUi(@NotNull JSFile file) {
/* 49 */     if (file == null) $$$reportNull$$$0(1);  JSTestFileType testFileType = file.getTestFileType();
/* 50 */     if (testFileType == JSTestFileType.JASMINE) {
/* 51 */       return "bdd";
/*    */     }
/* 53 */     if (testFileType == JSTestFileType.QUNIT) {
/* 54 */       return "qunit";
/*    */     }
/* 56 */     if (testFileType == JSTestFileType.TDD) {
/* 57 */       MochaTddFileStructure structure = (MochaTddFileStructure)MochaTddFileStructureBuilder.getInstance().fetchCachedTestFileStructure(file);
/* 58 */       if (structure.hasMochaTypeScriptDeclarations()) {
/* 59 */         return "mocha-typescript";
/*    */       }
/* 61 */       return "tdd";
/*    */     } 
/* 63 */     return null;
/*    */   }
/*    */   
/*    */   @NotNull
/*    */   public static NodePackage getMochaPackage(@NotNull Project project) {
/* 68 */     if (project == null) $$$reportNull$$$0(2);  String packageDir = PropertiesComponent.getInstance(project).getValue("nodejs.mocha.mocha_node_package_dir");
/* 69 */     if (PACKAGE_DESCRIPTOR.createPackage(StringUtil.notNullize(packageDir)) == null) $$$reportNull$$$0(3);  return PACKAGE_DESCRIPTOR.createPackage(StringUtil.notNullize(packageDir));
/*    */   }
/*    */   
/*    */   public static void setMochaPackage(@NotNull Project project, @NotNull NodePackage mochaPackage) {
/* 73 */     if (project == null) $$$reportNull$$$0(4);  if (mochaPackage == null) $$$reportNull$$$0(5);  PropertiesComponent.getInstance(project).setValue("nodejs.mocha.mocha_node_package_dir", mochaPackage.getSystemIndependentPath());
/*    */   }
/*    */   
/*    */   public static boolean isVueCliService(@NotNull NodePackage pkg) {
/* 77 */     if (pkg == null) $$$reportNull$$$0(6);  return pkg.nameMatches("@vue/cli-service");
/*    */   }
/*    */ }


/* Location:              C:\Program Files\JetBrains\WebStorm 2021.1\plugins\NodeJS\lib\NodeJS.jar!\com\jetbrains\nodejs\mocha\MochaUtil.class
 * Java compiler version: 11 (55.0)
 * JD-Core Version:       1.1.3
 */