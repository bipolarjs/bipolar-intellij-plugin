/*    */ package org.bipolar.packageJson;
/*    */ 
/*    */ import com.intellij.lang.javascript.EmbeddedJsonSchemaFileProvider;
/*    */ import com.intellij.lang.javascript.buildTools.npm.PackageJsonUtil;
/*    */ import com.intellij.openapi.project.Project;
/*    */ import com.intellij.openapi.vfs.VirtualFile;
/*    */ import com.jetbrains.jsonSchema.extension.JsonSchemaFileProvider;
/*    */ import com.jetbrains.jsonSchema.extension.JsonSchemaProviderFactory;
/*    */ import java.util.Collections;
/*    */ import java.util.List;
/*    */ import org.jetbrains.annotations.NotNull;
/*    */ 
/*    */ public class PackageJsonSchemaProviderFactory
/*    */   implements JsonSchemaProviderFactory {
/*    */   private static final String JSON_SCHEMA_FILENAME = "packageJsonSchema.json";
/*    */   
/*    */   @NotNull
/*    */   private static JsonSchemaFileProvider createProvider() {
/* 19 */     return (JsonSchemaFileProvider)new EmbeddedJsonSchemaFileProvider("packageJsonSchema.json", "package.json", "http://json.schemastore.org/package", PackageJsonSchemaProviderFactory.class, "", new String[] { "package.json" })
/*    */       {
/*    */         public boolean isAvailable(@NotNull VirtualFile file)
/*    */         {
/* 23 */           if (file == null) $$$reportNull$$$0(0);  return PackageJsonUtil.isPackageJsonFile(file);
/*    */         }
/*    */       };
/*    */   }
/*    */ 
/*    */   
/*    */   @NotNull
/*    */   public List<JsonSchemaFileProvider> getProviders(@NotNull Project project) {
/* 31 */     if (project == null) $$$reportNull$$$0(0);  if (Collections.singletonList(createProvider()) == null) $$$reportNull$$$0(1);  return Collections.singletonList(createProvider());
/*    */   }
/*    */ }


/* Location:              C:\Program Files\JetBrains\WebStorm 2021.1\plugins\NodeJS\lib\NodeJS.jar!\com\jetbrains\nodejs\packageJson\PackageJsonSchemaProviderFactory.class
 * Java compiler version: 11 (55.0)
 * JD-Core Version:       1.1.3
 */