/*     */ package org.bipolar.doc;
/*     */ import com.google.gson.JsonArray;
/*     */ import com.google.gson.JsonElement;
/*     */ import com.google.gson.JsonObject;
/*     */ import com.google.gson.JsonParser;
/*     */ import com.google.gson.JsonPrimitive;
/*     */ import com.intellij.openapi.diagnostic.Logger;
/*     */ import java.io.File;
/*     */ import java.io.IOException;
/*     */ import java.io.Reader;
/*     */ import java.util.ArrayList;
/*     */ import java.util.Collections;
/*     */ import java.util.Iterator;
/*     */ import java.util.List;
/*     */ import org.jetbrains.annotations.NotNull;
/*     */ import org.jetbrains.annotations.Nullable;
/*     */ 
/*     */ public final class ModuleDocParser {
/*  19 */   private static final Logger LOG = Logger.getInstance(ModuleDoc.class);
/*     */   private static final String TEXT_RAW = "textRaw";
/*     */   private static final String NAME = "name";
/*     */   private static final String STABILITY = "stability";
/*     */   private static final String STABILITY_TEXT = "stabilityText";
/*     */   private static final String DESCRIPTION = "desc";
/*     */   private static final String SIGNATURES = "signatures";
/*     */   
/*     */   @Nullable
/*     */   public static ModuleDoc parseFrom(@NotNull File jsonFile) {
/*  29 */     if (jsonFile == null) $$$reportNull$$$0(0);  if (!jsonFile.isFile()) {
/*  30 */       return null;
/*     */     }
/*     */     try {
/*  33 */       Reader reader = new InputStreamReader(new BufferedInputStream(new FileInputStream(jsonFile), 32768), StandardCharsets.UTF_8);
/*     */ 
/*     */ 
/*     */       
/*     */       try {
/*  38 */         JsonParser parser = new JsonParser();
/*  39 */         JsonElement topElement = parser.parse(reader);
/*  40 */         return convert(topElement);
/*     */       } finally {
/*     */         try {
/*  43 */           reader.close();
/*     */         }
/*  45 */         catch (IOException e) {
/*  46 */           LOG.warn("Can't close", e);
/*     */         }
/*     */       
/*     */       } 
/*  50 */     } catch (IOException e) {
/*  51 */       LOG.warn("Can't parse " + jsonFile.getAbsolutePath(), e);
/*  52 */       return null;
/*     */     } 
/*     */   }
/*     */   
/*     */   @Nullable
/*     */   private static ModuleDoc convert(@NotNull JsonElement topElement) throws IOException {
/*  58 */     if (topElement == null) $$$reportNull$$$0(1);  JsonObject topObject = topElement.getAsJsonObject();
/*  59 */     JsonArray array = topObject.getAsJsonArray("modules");
/*  60 */     Iterator<JsonElement> iterator = array.iterator(); if (iterator.hasNext()) { JsonElement element = iterator.next();
/*  61 */       return covertModuleObject(element.getAsJsonObject()); }
/*     */     
/*  63 */     return null;
/*     */   }
/*     */   
/*     */   @Nullable
/*     */   private static ModuleDoc covertModuleObject(@NotNull JsonObject moduleObject) {
/*  68 */     if (moduleObject == null) $$$reportNull$$$0(2);  String textRaw = getStringProperty(moduleObject, "textRaw");
/*  69 */     String name = getStringProperty(moduleObject, "name");
/*  70 */     Integer stability = getIntegerProperty(moduleObject, "stability");
/*  71 */     String stabilityText = getStringProperty(moduleObject, "stabilityText");
/*  72 */     String description = getStringProperty(moduleObject, "desc");
/*  73 */     List<ModulePropertyDoc> properties = parseProperties(moduleObject);
/*  74 */     List<ModuleMethodDoc> methods = parseMethods(moduleObject);
/*  75 */     return new ModuleDoc(name, textRaw, stability, stabilityText, description, properties, methods);
/*     */   }
/*     */   
/*     */   @NotNull
/*     */   private static List<ModulePropertyDoc> parseProperties(@NotNull JsonObject moduleObject) {
/*  80 */     if (moduleObject == null) $$$reportNull$$$0(3);  JsonElement propertiesElement = moduleObject.get("properties");
/*  81 */     if (propertiesElement == null) {
/*  82 */       if (Collections.emptyList() == null) $$$reportNull$$$0(4);  return (List)Collections.emptyList();
/*     */     } 
/*  84 */     JsonArray propertiesArray = propertiesElement.getAsJsonArray();
/*  85 */     List<ModulePropertyDoc> properties = new ArrayList<>();
/*  86 */     for (JsonElement propertyElement : propertiesArray) {
/*  87 */       ModulePropertyDoc property = parseProperty(propertyElement.getAsJsonObject());
/*  88 */       if (property != null) {
/*  89 */         properties.add(property);
/*     */       }
/*     */     } 
/*  92 */     if (properties == null) $$$reportNull$$$0(5);  return properties;
/*     */   }
/*     */   
/*     */   @Nullable
/*     */   private static ModulePropertyDoc parseProperty(@NotNull JsonObject propertyObject) {
/*  97 */     if (propertyObject == null) $$$reportNull$$$0(6);  String textRaw = getStringProperty(propertyObject, "textRaw");
/*  98 */     String name = getStringProperty(propertyObject, "name");
/*  99 */     String description = getStringProperty(propertyObject, "desc");
/* 100 */     if (name == null) {
/* 101 */       return null;
/*     */     }
/* 103 */     return new ModulePropertyDoc(name, textRaw, description);
/*     */   }
/*     */   
/*     */   @NotNull
/*     */   private static List<ModuleMethodDoc> parseMethods(@NotNull JsonObject moduleObject) {
/* 108 */     if (moduleObject == null) $$$reportNull$$$0(7);  JsonElement methodsElement = moduleObject.get("methods");
/* 109 */     if (methodsElement == null) {
/* 110 */       if (Collections.emptyList() == null) $$$reportNull$$$0(8);  return (List)Collections.emptyList();
/*     */     } 
/* 112 */     JsonArray methodsArray = methodsElement.getAsJsonArray();
/* 113 */     List<ModuleMethodDoc> methods = new ArrayList<>(methodsArray.size());
/* 114 */     for (JsonElement methodElement : methodsArray) {
/* 115 */       ModuleMethodDoc method = parseMethod(methodElement.getAsJsonObject());
/* 116 */       if (method != null) {
/* 117 */         methods.add(method);
/*     */       }
/*     */     } 
/* 120 */     if (methods == null) $$$reportNull$$$0(9);  return methods;
/*     */   }
/*     */   
/*     */   @Nullable
/*     */   private static ModuleMethodDoc parseMethod(@NotNull JsonObject methodObject) {
/* 125 */     if (methodObject == null) $$$reportNull$$$0(10);  String textRaw = getStringProperty(methodObject, "textRaw");
/* 126 */     String name = getStringProperty(methodObject, "name");
/* 127 */     String description = getStringProperty(methodObject, "desc");
/* 128 */     List<ModuleMethodParamDoc> params = Collections.emptyList();
/* 129 */     JsonElement signaturesElement = methodObject.get("signatures");
/* 130 */     if (signaturesElement != null) {
/* 131 */       JsonArray signaturesArray = signaturesElement.getAsJsonArray();
/* 132 */       if (signaturesArray.size() > 1) {
/* 133 */         LOG.warn("Multiple signatures discovered");
/*     */       }
/* 135 */       if (signaturesArray.size() > 0) {
/* 136 */         JsonElement signatureElement = signaturesArray.iterator().next();
/* 137 */         JsonObject signatureObject = signatureElement.getAsJsonObject();
/* 138 */         params = parseSignature(signatureObject);
/*     */       } 
/*     */     } 
/* 141 */     if (name != null) {
/* 142 */       return new ModuleMethodDoc(name, textRaw, description, params);
/*     */     }
/* 144 */     return null;
/*     */   }
/*     */   
/*     */   @NotNull
/*     */   private static List<ModuleMethodParamDoc> parseSignature(@NotNull JsonObject signatureObject) {
/* 149 */     if (signatureObject == null) $$$reportNull$$$0(11);  JsonArray paramsArray = signatureObject.getAsJsonArray("params");
/* 150 */     List<ModuleMethodParamDoc> params = new ArrayList<>(paramsArray.size());
/* 151 */     for (JsonElement paramElement : paramsArray) {
/* 152 */       JsonObject paramObject = paramElement.getAsJsonObject();
/* 153 */       String name = getStringProperty(paramObject, "name");
/* 154 */       boolean optional = getBooleanProperty(paramObject, "optional", false);
/* 155 */       if (name != null) {
/* 156 */         params.add(new ModuleMethodParamDoc(name, optional));
/*     */         continue;
/*     */       } 
/* 159 */       LOG.warn("method.signature.param.name is null");
/*     */     } 
/*     */     
/* 162 */     if (params == null) $$$reportNull$$$0(12);  return params;
/*     */   }
/*     */   
/*     */   @Nullable
/*     */   private static String getStringProperty(@NotNull JsonObject obj, @NotNull String key) {
/* 167 */     if (obj == null) $$$reportNull$$$0(13);  if (key == null) $$$reportNull$$$0(14);  JsonElement stringElement = obj.get(key);
/* 168 */     if (stringElement != null && stringElement.isJsonPrimitive()) {
/* 169 */       JsonPrimitive p = stringElement.getAsJsonPrimitive();
/* 170 */       if (p.isString()) {
/* 171 */         return p.getAsString();
/*     */       }
/*     */     } 
/* 174 */     return null;
/*     */   }
/*     */   
/*     */   @Nullable
/*     */   private static Integer getIntegerProperty(@NotNull JsonObject obj, @NotNull String key) {
/* 179 */     if (obj == null) $$$reportNull$$$0(15);  if (key == null) $$$reportNull$$$0(16);  JsonElement integerElement = obj.get(key);
/* 180 */     if (integerElement != null && integerElement.isJsonPrimitive()) {
/* 181 */       JsonPrimitive p = integerElement.getAsJsonPrimitive();
/* 182 */       if (p.isNumber()) {
/* 183 */         Number n = p.getAsNumber();
/* 184 */         return Integer.valueOf(n.intValue());
/*     */       } 
/*     */     } 
/* 187 */     return null;
/*     */   }
/*     */   
/*     */   private static boolean getBooleanProperty(@NotNull JsonObject obj, @NotNull String key, boolean defaultValue) {
/* 191 */     if (obj == null) $$$reportNull$$$0(17);  if (key == null) $$$reportNull$$$0(18);  JsonElement boolElement = obj.get(key);
/* 192 */     if (boolElement != null && boolElement.isJsonPrimitive()) {
/* 193 */       JsonPrimitive p = boolElement.getAsJsonPrimitive();
/* 194 */       if (p.isBoolean()) {
/* 195 */         return p.getAsBoolean();
/*     */       }
/*     */     } 
/* 198 */     return defaultValue;
/*     */   }
/*     */   
/*     */   public static void main(String[] args) {
/* 202 */     File file = new File("/home/segrey/work/idea-master/system/webstorm/extLibs/nodejs-v0.11.4-src/core-modules-sources/doc/api/http.json");
/* 203 */     ModuleDoc documentation = parseFrom(file);
/* 204 */     System.out.println(documentation);
/*     */   }
/*     */ }


/* Location:              C:\Program Files\JetBrains\WebStorm 2021.1\plugins\NodeJS\lib\NodeJS.jar!\com\jetbrains\nodejs\doc\ModuleDocParser.class
 * Java compiler version: 11 (55.0)
 * JD-Core Version:       1.1.3
 */