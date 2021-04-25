/*     */ package org.bipolar.doc;
/*     */ import com.google.gson.stream.JsonReader;
/*     */ import com.google.gson.stream.JsonToken;
/*     */ import com.google.gson.stream.JsonWriter;
/*     */ import com.intellij.openapi.diagnostic.Logger;
/*     */ import com.intellij.openapi.util.io.FileUtil;
/*     */ import java.io.BufferedInputStream;
/*     */ import java.io.File;
/*     */ import java.io.FileInputStream;
/*     */ import java.io.FileOutputStream;
/*     */ import java.io.IOException;
/*     */ import java.io.InputStreamReader;
/*     */ import java.io.OutputStreamWriter;
/*     */ import java.nio.charset.StandardCharsets;
/*     */ import java.util.Stack;
/*     */ import org.jetbrains.annotations.NotNull;
/*     */ import org.jetbrains.annotations.Nullable;
/*     */ 
/*     */ public class NodeDocSplitter {
/*  20 */   private static final Logger LOG = Logger.getInstance(NodeDocSplitter.class);
/*     */   
/*     */   private final File myAllDocJson;
/*     */   private final File myDocDir;
/*     */   
/*     */   public NodeDocSplitter(@NotNull File allDocJson, @NotNull File docDir) throws IOException {
/*  26 */     this.myAllDocJson = allDocJson;
/*  27 */     this.myDocDir = docDir;
/*     */   }
/*     */   
/*     */   public void split() throws IOException {
/*  31 */     JsonReader jsonReader = new JsonReader(new InputStreamReader(new BufferedInputStream(new FileInputStream(this.myAllDocJson), 524288), StandardCharsets.UTF_8));
/*     */ 
/*     */ 
/*     */     
/*     */     try {
/*  36 */       jsonReader.setLenient(false);
/*  37 */       doSplit(jsonReader);
/*     */     } finally {
/*  39 */       jsonReader.close();
/*     */     } 
/*     */   }
/*     */   
/*     */   private void doSplit(@NotNull JsonReader jsonReader) throws IOException {
/*  44 */     if (jsonReader == null) $$$reportNull$$$0(2);  JsonToken peekToken = jsonReader.peek();
/*  45 */     if (peekToken != JsonToken.BEGIN_OBJECT) {
/*  46 */       LOG.warn("[Malformed structure] top level element is expected to be an object. File: " + this.myAllDocJson.getAbsolutePath());
/*     */       return;
/*     */     } 
/*  49 */     jsonReader.beginObject();
/*  50 */     while (jsonReader.hasNext()) {
/*  51 */       String name = jsonReader.nextName();
/*  52 */       if ("modules".equals(name)) {
/*  53 */         extractModules(jsonReader);
/*     */         continue;
/*     */       } 
/*  56 */       jsonReader.skipValue();
/*     */     } 
/*     */     
/*  59 */     jsonReader.endObject();
/*     */   }
/*     */   
/*     */   private void extractModules(@NotNull JsonReader reader) throws IOException {
/*  63 */     if (reader == null) $$$reportNull$$$0(3);  File tmpDir = FileUtil.createTempDirectory(this.myDocDir, "tmp-modules", null, false);
/*  64 */     if (reader.peek() != JsonToken.BEGIN_ARRAY) {
/*  65 */       LOG.warn("[Malformed structure] 'modules' is expected to be an array. File: " + this.myAllDocJson.getAbsolutePath());
/*     */       return;
/*     */     } 
/*  68 */     reader.beginArray();
/*  69 */     int moduleId = 1;
/*  70 */     while (reader.hasNext()) {
/*  71 */       File tmpFile = new File(tmpDir, "" + moduleId++ + ".json");
/*  72 */       JsonWriter jsonWriter = new JsonWriter(new OutputStreamWriter(new BufferedOutputStream(new FileOutputStream(tmpFile, false), 32768), StandardCharsets.UTF_8));
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */       
/*  78 */       jsonWriter.setIndent("  ");
/*  79 */       jsonWriter.setLenient(false);
/*     */       try {
/*  81 */         copyCurrentElement(reader, jsonWriter);
/*     */       } finally {
/*  83 */         jsonWriter.close();
/*     */       } 
/*  85 */       String moduleName = extractModuleName(tmpFile);
/*  86 */       if (moduleName == null) {
/*  87 */         LOG.warn("Cannot extract module name");
/*     */         continue;
/*     */       } 
/*  90 */       File destFile = new File(this.myDocDir, moduleName + ".json");
/*  91 */       if (destFile.exists()) {
/*  92 */         LOG.warn("File already exists " + destFile.getAbsolutePath());
/*     */         continue;
/*     */       } 
/*  95 */       FileUtil.rename(tmpFile, destFile);
/*     */     } 
/*     */ 
/*     */     
/*  99 */     reader.endArray();
/*     */   }
/*     */   
/*     */   @Nullable
/*     */   private static String extractModuleName(@NotNull File moduleFile) throws IOException {
/* 104 */     if (moduleFile == null) $$$reportNull$$$0(4);  JsonReader jsonReader = new JsonReader(new InputStreamReader(new BufferedInputStream(new FileInputStream(moduleFile), 131072), StandardCharsets.UTF_8));
/*     */ 
/*     */ 
/*     */     
/*     */     try {
/* 109 */       jsonReader.setLenient(false);
/* 110 */       return doExtractModuleName(jsonReader);
/*     */     } finally {
/* 112 */       jsonReader.close();
/*     */     } 
/*     */   }
/*     */   
/*     */   @Nullable
/*     */   private static String doExtractModuleName(@NotNull JsonReader reader) throws IOException {
/* 118 */     if (reader == null) $$$reportNull$$$0(5);  if (reader.peek() != JsonToken.BEGIN_OBJECT) {
/* 119 */       LOG.warn("[Malformed structure] module is expected to be an object.");
/* 120 */       return null;
/*     */     } 
/* 122 */     reader.beginObject();
/* 123 */     while (reader.hasNext()) {
/* 124 */       String name = reader.nextName();
/* 125 */       if ("name".equals(name)) {
/* 126 */         return reader.nextString();
/*     */       }
/* 128 */       reader.skipValue();
/*     */     } 
/* 130 */     reader.endObject();
/* 131 */     return null;
/*     */   }
/*     */   
/*     */   private static void copyCurrentElement(@NotNull JsonReader reader, @NotNull JsonWriter writer) throws IOException {
/* 135 */     if (reader == null) $$$reportNull$$$0(6);  if (writer == null) $$$reportNull$$$0(7);  Stack<JsonToken> stack = new Stack<>();
/*     */     do {
/* 137 */       JsonToken peekToken = reader.peek();
/* 138 */       if (peekToken == JsonToken.BEGIN_OBJECT) {
/* 139 */         stack.push(peekToken);
/*     */         
/* 141 */         reader.beginObject();
/* 142 */         writer.beginObject();
/*     */       }
/* 144 */       else if (peekToken == JsonToken.END_OBJECT) {
/* 145 */         JsonToken token = stack.pop();
/* 146 */         if (token != JsonToken.BEGIN_OBJECT) {
/* 147 */           throw new IOException("" + JsonToken.BEGIN_OBJECT + " was expected, but " + JsonToken.BEGIN_OBJECT + " was found");
/*     */         }
/*     */         
/* 150 */         reader.endObject();
/* 151 */         writer.endObject();
/*     */       }
/* 153 */       else if (peekToken == JsonToken.BEGIN_ARRAY) {
/* 154 */         stack.push(peekToken);
/*     */         
/* 156 */         reader.beginArray();
/* 157 */         writer.beginArray();
/*     */       }
/* 159 */       else if (peekToken == JsonToken.END_ARRAY) {
/* 160 */         JsonToken token = stack.pop();
/* 161 */         if (token != JsonToken.BEGIN_ARRAY) {
/* 162 */           throw new IOException("" + JsonToken.BEGIN_ARRAY + " was expected, but " + JsonToken.BEGIN_ARRAY + " was found");
/*     */         }
/*     */         
/* 165 */         reader.endArray();
/* 166 */         writer.endArray();
/*     */       }
/* 168 */       else if (peekToken == JsonToken.BOOLEAN) {
/* 169 */         writer.value(reader.nextBoolean());
/*     */       }
/* 171 */       else if (peekToken == JsonToken.NULL) {
/* 172 */         reader.nextNull();
/* 173 */         writer.nullValue();
/*     */       }
/* 175 */       else if (peekToken == JsonToken.NUMBER) {
/* 176 */         String str = reader.nextString();
/*     */         try {
/* 178 */           long n = Long.parseLong(str);
/* 179 */           writer.value(n);
/* 180 */         } catch (NumberFormatException ignoredLong) {
/*     */           try {
/* 182 */             double d = Double.parseDouble(str);
/* 183 */             writer.value(d);
/* 184 */           } catch (NumberFormatException ignored) {
/* 185 */             writer.value(str);
/*     */           }
/*     */         
/*     */         } 
/* 189 */       } else if (peekToken == JsonToken.NAME) {
/* 190 */         String name = reader.nextName();
/* 191 */         writer.name(name);
/*     */       }
/* 193 */       else if (peekToken == JsonToken.STRING) {
/* 194 */         String str = reader.nextString();
/* 195 */         writer.value(str);
/*     */       }
/* 197 */       else if (peekToken == JsonToken.END_DOCUMENT) {
/* 198 */         throw new IOException("Unexpected end of document");
/*     */       } 
/* 200 */     } while (!stack.isEmpty());
/*     */   }
/*     */   
/*     */   private static class TmpData {}
/*     */ }


/* Location:              C:\Program Files\JetBrains\WebStorm 2021.1\plugins\NodeJS\lib\NodeJS.jar!\com\jetbrains\nodejs\doc\NodeDocSplitter.class
 * Java compiler version: 11 (55.0)
 * JD-Core Version:       1.1.3
 */