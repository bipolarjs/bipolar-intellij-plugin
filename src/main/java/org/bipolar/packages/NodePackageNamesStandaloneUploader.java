/*     */ package org.bipolar.packages;
/*     */ import com.google.gson.stream.JsonReader;
/*     */ import com.google.gson.stream.JsonWriter;
/*     */ import com.intellij.openapi.util.io.FileUtil;
/*     */ import com.intellij.util.TimeoutUtil;
/*     */ import java.io.Closeable;
/*     */ import java.io.File;
/*     */ import java.io.FileInputStream;
/*     */ import java.io.FileOutputStream;
/*     */ import java.io.IOException;
/*     */ import java.io.InputStream;
/*     */ import java.io.OutputStream;
/*     */ import java.io.OutputStreamWriter;
/*     */ import java.net.URL;
/*     */ import java.net.URLConnection;
/*     */ import java.nio.charset.StandardCharsets;
/*     */ import java.text.DateFormat;
/*     */ import java.util.ArrayList;
/*     */ import java.util.Date;
/*     */ import java.util.List;
/*     */ import java.util.Properties;
/*     */ import org.jetbrains.annotations.NotNull;
/*     */ import org.jetbrains.annotations.Nullable;
/*     */ 
/*     */ public final class NodePackageNamesStandaloneUploader {
/*     */   @NonNls
/*     */   private static final String DATE_FORMAT = "yyyy-MM-dd'T'HH:mm:ss Z";
/*     */   
/*     */   public static void main(String[] args) throws IOException {
/*  30 */     run();
/*     */   }
/*     */   private static final String NPM_REGISTRY_ALL_URL = "http://registry.npmjs.org/-/all"; private static final String NAME = "name";
/*     */   private static void run() throws IOException {
/*  34 */     File jsonFile = FileUtil.createTempFile("all-node-packages", ".json");
/*  35 */     System.out.printf("Downloading %s to %s ...\n", new Object[] { "http://registry.npmjs.org/-/all", jsonFile.getAbsoluteFile() });
/*  36 */     long startNanoTime = System.nanoTime();
/*  37 */     download("http://registry.npmjs.org/-/all", jsonFile);
/*  38 */     System.out.printf("%s has been downloaded to %s in %.1f seconds\n", new Object[] { "http://registry.npmjs.org/-/all", jsonFile
/*     */           
/*  40 */           .getAbsolutePath(), 
/*  41 */           Float.valueOf((float)TimeoutUtil.getDurationMillis(startNanoTime) / 1000.0F) });
/*     */     
/*  43 */     File namesFile = FileUtil.createTempFile("all-node-package-names-json", ".gzip");
/*  44 */     List<String> names = extractNames(jsonFile);
/*  45 */     if (names.size() < 100) {
/*  46 */       throw new RuntimeException("Too few packages " + names + ", uploading skipped");
/*     */     }
/*  48 */     Collections.sort(names);
/*  49 */     writeNames(names, namesFile);
/*  50 */     System.out.println("Package names have been written to " + namesFile.getAbsolutePath());
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*  55 */     uploadToFtp(namesFile);
/*     */   }
/*     */   
/*     */   private static void writeNames(@NotNull List<String> names, @NotNull File namesFile) throws IOException {
/*  59 */     if (names == null) $$$reportNull$$$0(0);  if (namesFile == null) $$$reportNull$$$0(1);  JsonWriter writer = new JsonWriter(new OutputStreamWriter(new GZIPOutputStream(new FileOutputStream(namesFile)), StandardCharsets.UTF_8));
/*     */     try {
/*  61 */       writer.setIndent(" ");
/*  62 */       writer.beginObject();
/*  63 */       DateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss Z");
/*  64 */       writer.name("_updated").value(df.format(new Date()));
/*  65 */       writer.name("_total_package_count").value(names.size());
/*  66 */       writer.name("package_names");
/*  67 */       writer.beginArray();
/*  68 */       for (String name : names) {
/*  69 */         writer.value(name);
/*     */       }
/*  71 */       writer.endArray();
/*  72 */       writer.endObject();
/*     */     } finally {
/*     */       
/*  75 */       writer.close();
/*     */     } 
/*     */   }
/*     */   
/*     */   private static List<String> extractNames(@NotNull File jsonFile) throws IOException {
/*  80 */     if (jsonFile == null) $$$reportNull$$$0(2);  long startNanoTime = System.nanoTime();
/*  81 */     JsonReader reader = new JsonReader(new BufferedReader(new InputStreamReader(new FileInputStream(jsonFile), StandardCharsets.UTF_8), 524288));
/*     */     
/*  83 */     List<String> names = new ArrayList<>();
/*     */     try {
/*  85 */       reader.beginObject();
/*  86 */       while (reader.hasNext()) {
/*  87 */         String key = reader.nextName();
/*  88 */         if ("_updated".equalsIgnoreCase(key)) {
/*  89 */           reader.skipValue();
/*     */           continue;
/*     */         } 
/*  92 */         String name = readPackageName(reader);
/*  93 */         if (name == null) {
/*  94 */           System.err.println("Cannot parse info for node package '" + key + "'");
/*     */           continue;
/*     */         } 
/*  97 */         names.add(name);
/*     */       } 
/*     */ 
/*     */       
/* 101 */       reader.endObject();
/* 102 */       System.out.printf("%d package names extracted in %d ms\n", new Object[] { Integer.valueOf(names.size()), Long.valueOf(TimeoutUtil.getDurationMillis(startNanoTime)) });
/*     */     } finally {
/*     */       try {
/* 105 */         reader.close();
/*     */       }
/* 107 */       catch (IOException e) {
/* 108 */         e.printStackTrace();
/*     */       } 
/*     */     } 
/* 111 */     return names;
/*     */   }
/*     */   
/*     */   @Nullable
/*     */   private static String readPackageName(@NotNull JsonReader reader) throws IOException {
/* 116 */     if (reader == null) $$$reportNull$$$0(3);  reader.beginObject();
/* 117 */     String name = null;
/* 118 */     while (reader.hasNext()) {
/* 119 */       String key = reader.nextName();
/* 120 */       if ("name".equals(key)) {
/* 121 */         name = reader.nextString();
/*     */         continue;
/*     */       } 
/* 124 */       reader.skipValue();
/*     */     } 
/*     */     
/* 127 */     reader.endObject();
/* 128 */     return name;
/*     */   }
/*     */   
/*     */   private static void download(@NotNull String location, @NotNull File outFile) throws IOException {
/* 132 */     if (location == null) $$$reportNull$$$0(4);  if (outFile == null) $$$reportNull$$$0(5);  HttpRequests.request(location).saveToFile(outFile, null);
/*     */   }
/*     */   
/*     */   private static void uploadToFtp(@NotNull File fileToUpload) throws IOException {
/* 136 */     if (fileToUpload == null) $$$reportNull$$$0(6);  String path = "/etc/intellij-nodejs-ftp-auth.properties";
/* 137 */     Properties auth = loadAuthProperties(new File(path));
/* 138 */     uploadToFtp(fileToUpload, auth
/* 139 */         .getProperty("host"), 
/* 140 */         Integer.parseInt(auth.getProperty("port")), auth
/* 141 */         .getProperty("username"), auth
/* 142 */         .getProperty("password"), auth
/* 143 */         .getProperty("uploadPath"), auth
/* 144 */         .getProperty("baseHttpUrl"));
/*     */   }
/*     */   
/*     */   @NotNull
/*     */   private static Properties loadAuthProperties(@NotNull File file) throws IOException {
/* 149 */     if (file == null) $$$reportNull$$$0(7);  InputStream inputStream = new FileInputStream(file);
/*     */     
/* 151 */     try { Properties prop = new Properties();
/* 152 */       prop.load(inputStream);
/* 153 */       Properties properties1 = prop;
/*     */ 
/*     */       
/* 156 */       inputStream.close(); return properties1; } finally { inputStream.close(); }
/*     */   
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private static void uploadToFtp(@NotNull File fileToUpload, @NotNull String host, int port, @NotNull String username, @NotNull String password, @NotNull String uploadPath, @NotNull String baseHttpUrl) throws IOException {
/* 167 */     if (fileToUpload == null) $$$reportNull$$$0(9);  if (host == null) $$$reportNull$$$0(10);  if (username == null) $$$reportNull$$$0(11);  if (password == null) $$$reportNull$$$0(12);  if (uploadPath == null) $$$reportNull$$$0(13);  if (baseHttpUrl == null) $$$reportNull$$$0(14);  String ftpUrl = "ftp://" + username + ":" + password + "@" + host + ":" + port + uploadPath;
/* 168 */     System.out.println("Uploading " + fileToUpload.getAbsolutePath() + " to " + ftpUrl + " ...");
/* 169 */     InputStream inputStream = null;
/* 170 */     OutputStream outputStream = null;
/*     */     try {
/* 172 */       URL url = new URL(ftpUrl);
/* 173 */       URLConnection conn = url.openConnection();
/* 174 */       outputStream = conn.getOutputStream();
/* 175 */       inputStream = new FileInputStream(fileToUpload);
/* 176 */       NetUtils.copyStreamContent(null, inputStream, outputStream, (int)fileToUpload.length());
/* 177 */       System.out.println("File uploaded correctly, will be available soon at " + baseHttpUrl + uploadPath);
/*     */     } finally {
/*     */       
/* 180 */       closeStream(outputStream);
/* 181 */       closeStream(inputStream);
/*     */     } 
/*     */   }
/*     */   
/*     */   private static void closeStream(@Nullable Closeable closeable) throws IOException {
/* 186 */     if (closeable != null)
/* 187 */       closeable.close(); 
/*     */   }
/*     */ }


/* Location:              C:\Program Files\JetBrains\WebStorm 2021.1\plugins\NodeJS\lib\NodeJS.jar!\com\jetbrains\nodejs\packages\NodePackageNamesStandaloneUploader.class
 * Java compiler version: 11 (55.0)
 * JD-Core Version:       1.1.3
 */