/*     */ package org.bipolar.run.profile.heap.calculation;
/*     */ 
/*     */ import com.google.gson.JsonParseException;
/*     */ import com.google.gson.stream.JsonReader;
/*     */ import com.google.gson.stream.JsonToken;
/*     */ import org.bipolar.run.profile.heap.data.V8HeapEdge;
/*     */ import org.bipolar.run.profile.heap.data.V8HeapEntry;
/*     */ import org.bipolar.run.profile.heap.data.V8HeapHeader;
/*     */ import java.io.Closeable;
/*     */ import java.io.File;
/*     */ import java.io.FileInputStream;
/*     */ import java.io.FileNotFoundException;
/*     */ import java.io.IOException;
/*     */ import java.io.InputStreamReader;
/*     */ import java.nio.charset.StandardCharsets;
/*     */ import org.jetbrains.annotations.NotNull;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class HeapSnapshotReader
/*     */   implements Closeable
/*     */ {
/*     */   @NotNull
/*     */   private final File myFile;
/*     */   private final JsonReader myReader;
/*     */   private long myNodesCnt;
/*     */   private long myEdgesCnt;
/*     */   private long myStringsCnt;
/*     */   private boolean myHaveTraceData = false;
/*     */   
/*     */   public HeapSnapshotReader(@NotNull File file) throws FileNotFoundException {
/*  41 */     this.myFile = file;
/*  42 */     FileInputStream fileReader = new FileInputStream(this.myFile);
/*  43 */     InputStreamReader ir = new InputStreamReader(fileReader, StandardCharsets.UTF_8);
/*  44 */     this.myReader = new JsonReader(ir);
/*  45 */     this.myStringsCnt = 0L;
/*     */   }
/*     */   
/*     */   public V8HeapHeader readHeader() throws IOException {
/*  49 */     this.myReader.beginObject();
/*  50 */     assertName(this.myReader, "snapshot");
/*  51 */     this.myReader.beginObject();
/*     */     
/*  53 */     String name = this.myFile.getName();
/*  54 */     long traceFunctionsCnt = 0L;
/*  55 */     while (JsonToken.NAME.equals(this.myReader.peek())) {
/*  56 */       String readName = this.myReader.nextName();
/*  57 */       if ("title".equals(readName)) {
/*  58 */         name = readName;
/*  59 */         this.myReader.skipValue(); continue;
/*  60 */       }  if ("node_count".equals(readName)) {
/*  61 */         this.myNodesCnt = this.myReader.nextLong(); continue;
/*  62 */       }  if ("edge_count".equals(readName)) {
/*  63 */         this.myEdgesCnt = this.myReader.nextLong(); continue;
/*  64 */       }  if ("trace_function_count".equals(readName)) {
/*  65 */         traceFunctionsCnt = this.myReader.nextLong();
/*  66 */         this.myHaveTraceData = true; continue;
/*     */       } 
/*  68 */       this.myReader.skipValue();
/*     */     } 
/*     */     
/*  71 */     this.myReader.endObject();
/*     */     
/*  73 */     return new V8HeapHeader(name, this.myNodesCnt, this.myEdgesCnt, traceFunctionsCnt);
/*     */   }
/*     */ 
/*     */   
/*     */   public void close() throws IOException {
/*  78 */     this.myReader.close();
/*     */   }
/*     */   
/*     */   public void readWithReader(@NotNull HeapSnapshotListener listener) throws IOException {
/*  82 */     if (listener == null) $$$reportNull$$$0(1);  while (JsonToken.NAME.equals(this.myReader.peek())) {
/*  83 */       String name = this.myReader.nextName();
/*  84 */       if ("nodes".equals(name)) {
/*  85 */         readNodes(this.myReader, listener);
/*  86 */         listener.allNodesRead(); continue;
/*  87 */       }  if ("edges".equals(name)) {
/*  88 */         readEdges(this.myReader, listener);
/*  89 */         listener.allEdgesRead(); continue;
/*  90 */       }  if ("strings".equals(name)) {
/*  91 */         this.myReader.beginArray();
/*  92 */         this.myStringsCnt++;
/*  93 */         listener.stringsCount(this.myStringsCnt);
/*  94 */         while (this.myReader.hasNext()) {
/*  95 */           String string = this.myReader.nextString();
/*  96 */           listener.accept(string);
/*     */         } 
/*  98 */         this.myReader.endArray(); continue;
/*     */       } 
/* 100 */       this.myReader.skipValue();
/*     */     } 
/*     */     
/* 103 */     this.myReader.endObject();
/*     */   }
/*     */   
/*     */   private void readEdges(JsonReader reader, HeapSnapshotListener listener) throws IOException {
/* 107 */     reader.beginArray();
/* 108 */     for (int i = 0; i < this.myEdgesCnt; i++) {
/* 109 */       int typeCode = reader.nextInt();
/* 110 */       long nameId = reader.nextLong();
/* 111 */       long toIndex = reader.nextLong();
/*     */       
/* 113 */       V8HeapEdge edge = V8HeapEdge.createFromJson(typeCode, nameId, toIndex, i);
/* 114 */       if (edge.hasStringName()) {
/* 115 */         this.myStringsCnt = Math.max(this.myStringsCnt, nameId);
/*     */       }
/* 117 */       listener.accept(edge);
/*     */     } 
/* 119 */     reader.endArray();
/*     */   }
/*     */   
/*     */   private void readNodes(JsonReader reader, HeapSnapshotListener listener) throws IOException {
/* 123 */     reader.beginArray();
/* 124 */     for (int i = 0; i < this.myNodesCnt; i++) {
/* 125 */       int type = reader.nextInt();
/* 126 */       long nameId = reader.nextLong();
/* 127 */       long snapshotId = reader.nextLong();
/*     */       
/* 129 */       long size = reader.nextLong();
/* 130 */       long childrenCount = reader.nextLong();
/* 131 */       long traceId = this.myHaveTraceData ? reader.nextLong() : -1L;
/*     */       
/* 133 */       this.myStringsCnt = Math.max(this.myStringsCnt, nameId);
/*     */       
/* 135 */       listener.accept(V8HeapEntry.createFromJson(type, nameId, snapshotId, childrenCount, size, traceId, i));
/*     */     } 
/* 137 */     reader.endArray();
/*     */   }
/*     */   
/*     */   private static void assertName(@NotNull JsonReader reader, @NotNull String name) throws IOException {
/* 141 */     if (reader == null) $$$reportNull$$$0(2);  if (name == null) $$$reportNull$$$0(3);  String readName = reader.nextName();
/* 142 */     if (!name.equals(readName)) throw new JsonParseException("Wrong format"); 
/*     */   }
/*     */ }


/* Location:              C:\Program Files\JetBrains\WebStorm 2021.1\plugins\NodeJS\lib\NodeJS.jar!\com\jetbrains\nodejs\run\profile\heap\calculation\HeapSnapshotReader.class
 * Java compiler version: 11 (55.0)
 * JD-Core Version:       1.1.3
 */