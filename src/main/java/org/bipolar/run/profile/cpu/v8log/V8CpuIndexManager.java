/*     */ package org.bipolar.run.profile.cpu.v8log;
/*     */ import com.intellij.openapi.util.Pair;
/*     */ import org.bipolar.run.profile.V8IndexCatalogManager;
/*     */ import org.bipolar.run.profile.cpu.calculation.CallTreesSerializer;
/*     */ import org.bipolar.run.profile.cpu.calculation.V8ProfileLine;
/*     */ import org.bipolar.run.profile.cpu.v8log.calculation.TickIndexer;
/*     */ import org.bipolar.run.profile.cpu.v8log.calculation.V8LogIndexesWriter;
/*     */ import org.bipolar.run.profile.cpu.v8log.calculation.V8TickProcessor;
/*     */ import org.bipolar.run.profile.cpu.v8log.data.FlatTopCalls;
/*     */ import org.bipolar.run.profile.cpu.v8log.reading.V8LogCachingReader;
/*     */ import org.bipolar.run.profile.heap.CompositeCloseable;
/*     */ import org.bipolar.run.profile.heap.calculation.ByteArrayWrapper;
/*     */ import org.bipolar.run.profile.heap.io.LongRawSerializer;
/*     */ import org.bipolar.run.profile.heap.io.RawSerializer;
/*     */ import org.bipolar.run.profile.heap.io.SequentialRawReader;
/*     */ import org.bipolar.run.profile.heap.io.SequentialRawWriter;
/*     */ import org.bipolar.run.profile.heap.io.reverse.LinksReaderFactory;
/*     */ import java.io.DataInput;
/*     */ import java.io.DataOutput;
/*     */ import java.io.File;
/*     */ import java.io.IOException;
/*     */ import java.util.HashMap;
/*     */ import java.util.List;
/*     */ import java.util.Map;
/*     */
import org.jetbrains.annotations.NotNull;
/*     */ 
/*     */ public class V8CpuIndexManager extends V8IndexManager<V8LogIndexesWriter.Category> {
/*     */   public V8CpuIndexManager(@NotNull File snapshotFile) throws IOException {
/*  29 */     super(snapshotFile, (Enum[])getCategoryValues());
/*     */   }
/*     */   
/*     */   private static V8LogIndexesWriter.Category[] getCategoryValues() {
/*  33 */     List<V8LogIndexesWriter.Category> categories = new ArrayList<>(Arrays.asList(V8LogIndexesWriter.Category.values()));
/*  34 */     categories.remove(V8LogIndexesWriter.Category.durationByTick1);
/*  35 */     categories.remove(V8LogIndexesWriter.Category.numDurationByTick1);
/*  36 */     return categories.<V8LogIndexesWriter.Category>toArray(new V8LogIndexesWriter.Category[0]);
/*     */   }
/*     */ 
/*     */   
/*     */   public byte[] createDigest(@NotNull File snapshotFile) throws IOException {
/*  41 */     if (snapshotFile == null) $$$reportNull$$$0(1);  return V8IndexCatalogManager.digestFile(snapshotFile, new byte[0][]);
/*     */   }
/*     */ 
/*     */   
/*     */   protected V8LogIndexesWriter.Category getDescriptionCategory() {
/*  46 */     return V8LogIndexesWriter.Category.description;
/*     */   }
/*     */ 
/*     */   
/*     */   public V8LogCachingReader initReader(@NotNull ByteArrayWrapper digest, File v8log, @NotNull CompositeCloseable resources) throws IOException {
/*  51 */     if (digest == null) $$$reportNull$$$0(2);  if (resources == null) $$$reportNull$$$0(3);
/*     */     
/*  53 */     LinksReaderFactory<Long> stackReader = new LinksReaderFactory((RawSerializer)new LongRawSerializer(), this.myIndexFiles.getOneFile((Enum)V8LogIndexesWriter.Category.numStack), this.myIndexFiles.getOneFile((Enum)V8LogIndexesWriter.Category.stack));
/*     */ 
/*     */ 
/*     */     
/*  57 */     LinksReaderFactory<String> stringsReader = new LinksReaderFactory((RawSerializer)new StringRawSerializer(), this.myIndexFiles.getOneFile((Enum)V8LogIndexesWriter.Category.numStrings), this.myIndexFiles.getOneFile((Enum)V8LogIndexesWriter.Category.strings));
/*     */ 
/*     */     
/*  60 */     LinksReaderFactory<V8LogIndexesWriter.TimerEvent> timerEventReader = new LinksReaderFactory((RawSerializer)new V8LogIndexesWriter.TimerEvent.MyRawSerializer(), this.myIndexFiles.getOneFile((Enum)V8LogIndexesWriter.Category.numEvents), this.myIndexFiles.getOneFile((Enum)V8LogIndexesWriter.Category.events));
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*  65 */     LinksReaderFactory<Long> durationFactory = new LinksReaderFactory((RawSerializer)new LongRawSerializer(), this.myIndexFiles.getOneFile((Enum)V8LogIndexesWriter.Category.numDurationByTick2), this.myIndexFiles.getOneFile((Enum)V8LogIndexesWriter.Category.durationByTick2));
/*     */     
/*  67 */     FlatTopCalls flatTopCalls = read(V8LogIndexesWriter.Category.flatTree, (RawSerializer<FlatTopCalls>)new FlatTopCalls.MySerializer());
/*  68 */     TickIndexer eventsIndex = read(V8LogIndexesWriter.Category.eventsIndex, (RawSerializer<TickIndexer>)new TickIndexer.MySerializer());
/*  69 */     TickIndexer eventsEndsIndex = read(V8LogIndexesWriter.Category.eventsEndsIndex, (RawSerializer<TickIndexer>)new TickIndexer.MySerializer());
/*  70 */     Map<String, V8TickProcessor.CodeType> typeMap = read(V8LogIndexesWriter.Category.codeTypes, new CodeTypesSerializer());
/*     */     
/*  72 */     return new V8LogCachingReader(digest, v8log, this.myIndexFiles.getOneFile((Enum)V8LogIndexesWriter.Category.header), this.myIndexFiles
/*  73 */         .getOneFile((Enum)V8LogIndexesWriter.Category.timeForTick), this.myIndexFiles
/*  74 */         .getOneFile((Enum)V8LogIndexesWriter.Category.stackSize), stackReader, stringsReader, timerEventReader, this.myIndexFiles
/*  75 */         .getFiles((Enum)V8LogIndexesWriter.Category.overviewScale), durationFactory, 
/*  76 */         read(V8LogIndexesWriter.Category.bottomUp, (RawSerializer<V8ProfileLine>)new CallTreesSerializer()),
/*  77 */         read(V8LogIndexesWriter.Category.topDown, (RawSerializer<V8ProfileLine>)new CallTreesSerializer()), flatTopCalls, resources, eventsIndex, eventsEndsIndex, typeMap, this.myIndexFiles
/*     */         
/*  79 */         .getOneFile((Enum)V8LogIndexesWriter.Category.distribution), this.myIndexFiles
/*  80 */         .getOneFile((Enum)V8LogIndexesWriter.Category.selfDistribution));
/*     */   }
/*     */   
/*     */   public void recordReader(V8LogCachingReader reader) throws IOException {
/*  84 */     if (this.myDoNotSerialize)
/*  85 */       return;  Map<V8LogIndexesWriter.Category, List<File>> map = this.myIndexFiles.getFilesMap();
/*  86 */     map.remove(V8LogIndexesWriter.Category.durationByTick1);
/*  87 */     map.remove(V8LogIndexesWriter.Category.numDurationByTick1);
/*     */     
/*  89 */     record(V8LogIndexesWriter.Category.bottomUp, (RawSerializer<V8ProfileLine>)new CallTreesSerializer(), reader.getBottomUp());
/*  90 */     record(V8LogIndexesWriter.Category.topDown, (RawSerializer<V8ProfileLine>)new CallTreesSerializer(), reader.getTopDown());
/*  91 */     record(V8LogIndexesWriter.Category.flatTree, (RawSerializer<FlatTopCalls>)new FlatTopCalls.MySerializer(), reader.getFlat());
/*  92 */     record(V8LogIndexesWriter.Category.eventsIndex, (RawSerializer<TickIndexer>)new TickIndexer.MySerializer(), reader.getEventsTickIndexer());
/*  93 */     record(V8LogIndexesWriter.Category.eventsEndsIndex, (RawSerializer<TickIndexer>)new TickIndexer.MySerializer(), reader.getEventsEndTickIndexer());
/*  94 */     record(V8LogIndexesWriter.Category.codeTypes, new CodeTypesSerializer(), reader.getCodeTypes());
/*     */     
/*  96 */     V8IndexCatalogManager.writeDigests(map, categoryFile((Enum)V8LogIndexesWriter.Category.description));
/*     */   }
/*     */   
/*     */   private <T> void record(V8LogIndexesWriter.Category category, RawSerializer<T> serializer, T value) throws IOException {
/* 100 */     File file = this.myIndexFiles.generate((Enum)category, null);
/* 101 */     SequentialRawWriter<T> writer = new SequentialRawWriter(file, serializer);
/* 102 */     writer.write(value);
/* 103 */     writer.close();
/*     */   }
/*     */   
/*     */   private <T> T read(V8LogIndexesWriter.Category category, RawSerializer<T> serializer) throws IOException {
/* 107 */     File file = this.myIndexFiles.getOneFile((Enum)category);
/* 108 */     SequentialRawReader<T> reader = new SequentialRawReader(file, serializer);
/*     */     try {
/* 110 */       return (T)reader.read();
/*     */     } finally {
/* 112 */       reader.close();
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   protected boolean checkAllTypesArePresent(Map<V8LogIndexesWriter.Category, List<Pair<String, byte[]>>> digests) {
/* 118 */     V8LogIndexesWriter.Category[] values = getCategoryValues();
/* 119 */     if (digests.size() == values.length - 1) return true; 
/* 120 */     int len = values.length - 1;
/* 121 */     if (!digests.containsKey(V8LogIndexesWriter.Category.overviewScale)) len--; 
/* 122 */     if (!digests.containsKey(V8LogIndexesWriter.Category.eventsOverview)) len--; 
/* 123 */     if (digests.size() == len) return true; 
/* 124 */     return false;
/*     */   }
/*     */   
/*     */   private static class CodeTypesSerializer
/*     */     implements RawSerializer<Map<String, V8TickProcessor.CodeType>> {
/*     */     public long getRecordSize() {
/* 130 */       return -1L;
/*     */     }
/*     */ 
/*     */     
/*     */     public void write(@NotNull DataOutput os, @NotNull Map<String, V8TickProcessor.CodeType> map) throws IOException {
/* 135 */       if (os == null) $$$reportNull$$$0(0);  if (map == null) $$$reportNull$$$0(1);  os.writeInt(map.size());
/* 136 */       for (Map.Entry<String, V8TickProcessor.CodeType> entry : map.entrySet()) {
/* 137 */         os.writeUTF(entry.getKey());
/* 138 */         os.writeUTF(((V8TickProcessor.CodeType)entry.getValue()).name());
/*     */       } 
/*     */     }
/*     */ 
/*     */     
/*     */     public Map<String, V8TickProcessor.CodeType> read(@NotNull DataInput is) throws IOException {
/* 144 */       if (is == null) $$$reportNull$$$0(2);  Map<String, V8TickProcessor.CodeType> map = new HashMap<>();
/* 145 */       int size = is.readInt();
/* 146 */       for (int i = 0; i < size; i++) {
/* 147 */         String key = is.readUTF();
/* 148 */         V8TickProcessor.CodeType value = V8TickProcessor.CodeType.valueOf(is.readUTF());
/* 149 */         map.put(key, value);
/*     */       } 
/* 151 */       return map;
/*     */     }
/*     */   }
/*     */ }


/* Location:              C:\Program Files\JetBrains\WebStorm 2021.1\plugins\NodeJS\lib\NodeJS.jar!\com\jetbrains\nodejs\run\profile\cpu\v8log\V8CpuIndexManager.class
 * Java compiler version: 11 (55.0)
 * JD-Core Version:       1.1.3
 */