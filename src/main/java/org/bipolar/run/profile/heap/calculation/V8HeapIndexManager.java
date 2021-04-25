/*     */ package org.bipolar.run.profile.heap.calculation;
/*     */ import com.intellij.openapi.application.PathManager;
/*     */ import com.intellij.openapi.util.io.FileUtil;
/*     */ import com.intellij.util.Consumer;
/*     */ import org.bipolar.run.profile.V8IndexCatalogManager;
/*     */ import org.bipolar.run.profile.V8IndexManager;
/*     */ import org.bipolar.run.profile.heap.CompositeCloseable;
/*     */ import org.bipolar.run.profile.heap.V8CachingReader;
/*     */ import org.bipolar.run.profile.heap.data.LinkedByNameId;
/*     */ import org.bipolar.run.profile.heap.data.V8HeapEdge;
/*     */ import org.bipolar.run.profile.heap.data.V8HeapHeader;
/*     */ import org.bipolar.run.profile.heap.io.LongRawSerializer;
/*     */ import org.bipolar.run.profile.heap.io.RawSerializer;
/*     */ import org.bipolar.run.profile.heap.io.reverse.LinksReaderFactory;
/*     */ import java.io.BufferedInputStream;
/*     */ import java.io.ByteArrayOutputStream;
/*     */ import java.io.Externalizable;
/*     */ import java.io.File;
/*     */ import java.io.FileInputStream;
/*     */ import java.io.IOException;
/*     */ import java.io.ObjectInputStream;
/*     */ import java.io.ObjectOutputStream;
/*     */ import java.nio.charset.StandardCharsets;
/*     */ import java.util.List;
/*     */
import org.jetbrains.annotations.NotNull;
/*     */ import org.jetbrains.annotations.Nullable;
/*     */ 
/*     */ public class V8HeapIndexManager extends V8IndexManager<V8HeapIndexManager.Category> {
/*  29 */   public static final Integer MAX_FILE_SIZE = Integer.valueOf(Integer.getInteger("idea.javascript.profiling.v8.heap.max.file.mb", 100).intValue() * 1048576); public static final String IDEA_JAVASCRIPT_PROFILING_V8_HEAP_MAX_FILE_MB = "idea.javascript.profiling.v8.heap.max.file.mb";
/*     */   private final boolean myShowHidden;
/*     */   
/*     */   public V8HeapIndexManager(@NotNull File snapshotFile, boolean showHidden) throws IOException {
/*  33 */     super(snapshotFile, (Enum[])Category.values());
/*  34 */     this.myShowHidden = showHidden;
/*     */   }
/*     */   
/*     */   @NotNull
/*     */   public static File getSystemDir() {
/*  39 */     return new File(PathManager.getSystemPath(), ApplicationManager.getApplication().isUnitTestMode() ? "v8test" : "v8");
/*     */   }
/*     */ 
/*     */   
/*     */   protected byte[] createDigest(@NotNull File snapshotFile) throws IOException {
/*  44 */     if (snapshotFile == null) $$$reportNull$$$0(1);  return snapshotDigest(snapshotFile, this.myShowHidden);
/*     */   }
/*     */ 
/*     */   
/*     */   protected Category getDescriptionCategory() {
/*  49 */     return Category.description;
/*     */   }
/*     */ 
/*     */   
/*     */   @Nullable
/*     */   public V8CachingReader initReader(@NotNull CompositeCloseable resources, @Nullable Consumer<String> errorNotificator) throws IOException, ClassNotFoundException {
/*  55 */     if (resources == null) $$$reportNull$$$0(2);  List<File> files = this.myIndexFiles.getFiles(Category.strings);
/*  56 */     V8StringIndex stringIndex = (V8StringIndex)resources.register(new V8StringIndex(files.<File>toArray(new File[0])));
/*     */ 
/*     */     
/*  59 */     LinksReaderFactory<LinkedByNameId> stringsReverseIndexReaderFactory = new LinksReaderFactory((RawSerializer)LinkedByNameId.Serializer.getInstance(), this.myIndexFiles.getOneFile(Category.stringsNumLinks), this.myIndexFiles.getOneFile(Category.stringsLinks));
/*     */     
/*  61 */     LinksReaderFactory<V8HeapEdge> reverseIndexReaderFactory = new LinksReaderFactory((RawSerializer)V8HeapEdge.MyRawSerializer.getInstance(), this.myIndexFiles.getOneFile(Category.reverseNumLinks), this.myIndexFiles.getOneFile(Category.reverseLinks));
/*     */     
/*  63 */     V8HeapHeader header = new V8HeapHeader();
/*  64 */     V8ImportantStringsHolder holder = new V8ImportantStringsHolder();
/*     */     
/*  66 */     File headerFile = this.myIndexFiles.getOneFile(Category.header);
/*  67 */     readHeader(headerFile, header, holder);
/*     */ 
/*     */     
/*  70 */     V8CachingReader reader = new V8CachingReader(this.mySnapshotFile, this.myDigest, resources, header, errorNotificator, stringIndex, this.myIndexFiles.getOneFile(Category.nodeIdx), this.myIndexFiles.getOneFile(Category.edgeIdx), stringsReverseIndexReaderFactory, reverseIndexReaderFactory, holder);
/*     */ 
/*     */     
/*  73 */     File inMemory = this.myIndexFiles.getOneFile(Category.inMemoryIndexes);
/*  74 */     V8HeapInMemoryIndexes inMemoryIndexes = new V8HeapInMemoryIndexes();
/*  75 */     readExternalizableFromFile(inMemoryIndexes, inMemory);
/*     */ 
/*     */     
/*  78 */     LinksReaderFactory<Long> aggregatesReaderFactory = new LinksReaderFactory((RawSerializer)new LongRawSerializer(), this.myIndexFiles.getOneFile(Category.aggregateNum), this.myIndexFiles.getOneFile(Category.aggregateLinks));
/*     */ 
/*     */     
/*  81 */     reader.setInMemoryIndexes(inMemoryIndexes);
/*  82 */     reader.setAggregatesLinksReaderFactory(aggregatesReaderFactory);
/*  83 */     if (!this.myShowHidden) {
/*  84 */       reader.resetDoNotShowHidden();
/*     */     }
/*  86 */     return reader;
/*     */   }
/*     */   
/*     */   public void recordReader(@NotNull V8CachingReader reader) throws IOException {
/*  90 */     if (reader == null) $$$reportNull$$$0(3);  if (this.myDoNotSerialize)
/*  91 */       return;  writeHeader(reader, this.myIndexFiles.generate(Category.header, ".header"));
/*  92 */     writeExternalizableToFile(reader.getInMemoryIndexes(), this.myIndexFiles.generate(Category.inMemoryIndexes, ".inMemory"));
/*  93 */     File description = categoryFile(Category.description);
/*  94 */     V8IndexCatalogManager.writeDigests(this.myIndexFiles.getFilesMap(), description);
/*     */   }
/*     */   
/*     */   private void writeExternalizableToFile(@NotNull Externalizable externalizable, @NotNull File file) throws IOException {
/*  98 */     if (externalizable == null) $$$reportNull$$$0(4);  if (file == null) $$$reportNull$$$0(5);  ByteArrayOutputStream out = new ByteArrayOutputStream();
/*  99 */     ObjectOutputStream stream = new ObjectOutputStream(out);
/* 100 */     externalizable.writeExternal(stream);
/* 101 */     stream.flush();
/* 102 */     FileUtil.writeToFile(file, out.toByteArray());
/*     */   }
/*     */ 
/*     */   
/*     */   private static void readExternalizableFromFile(@NotNull Externalizable externalizable, @NotNull File file) throws IOException, ClassNotFoundException {
/* 107 */     if (externalizable == null) $$$reportNull$$$0(6);  if (file == null) $$$reportNull$$$0(7);  checkFileSize(file.length());
/* 108 */     ObjectInputStream ois = new ObjectInputStream(new BufferedInputStream(new FileInputStream(file)));
/* 109 */     externalizable.readExternal(ois);
/*     */   }
/*     */   
/*     */   private static void checkFileSize(long length) throws IOException {
/* 113 */     if (length > MAX_FILE_SIZE.intValue()) {
/* 114 */       throw new IOException("Can not load V8 Heap indexes file, file is too big.\nPlease specify a greater value in megabytes for property 'idea.javascript.profiling.v8.heap.max.file.mb' in idea.properties file.");
/*     */     }
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private void writeHeader(@NotNull V8CachingReader reader, @NotNull File file) throws IOException {
/* 123 */     if (reader == null) $$$reportNull$$$0(8);  if (file == null) $$$reportNull$$$0(9);  V8HeapHeader header = reader.getHeader();
/* 124 */     ByteArrayOutputStream out = new ByteArrayOutputStream();
/* 125 */     ObjectOutputStream stream = new ObjectOutputStream(out);
/* 126 */     header.writeExternal(stream);
/*     */     
/* 128 */     V8ImportantStringsHolder holder = reader.getImportantStringsHolder();
/* 129 */     holder.writeExternal(stream);
/* 130 */     stream.flush();
/*     */     
/* 132 */     FileUtil.writeToFile(file, out.toByteArray());
/*     */   }
/*     */ 
/*     */   
/*     */   private void readHeader(@NotNull File file, @NotNull V8HeapHeader header, @NotNull V8ImportantStringsHolder holder) throws IOException, ClassNotFoundException {
/* 137 */     if (file == null) $$$reportNull$$$0(10);  if (header == null) $$$reportNull$$$0(11);  if (holder == null) $$$reportNull$$$0(12);  ObjectInputStream ois = new ObjectInputStream(new BufferedInputStream(new FileInputStream(file)));
/* 138 */     header.readExternal(ois);
/* 139 */     holder.readExternal(ois);
/*     */   }
/*     */   
/*     */   public static byte[] snapshotDigest(@NotNull File snapshotFile, boolean showHidden) throws IOException {
/* 143 */     if (snapshotFile == null) $$$reportNull$$$0(13);  return V8IndexCatalogManager.digestFile(snapshotFile, new byte[][] { (showHidden ? "hidden" : "user").getBytes(StandardCharsets.UTF_8) });
/*     */   }
/*     */   
/*     */   public enum Category {
/* 147 */     description,
/* 148 */     nodeIdx,
/* 149 */     edgeIdx,
/* 150 */     strings,
/* 151 */     stringsNumLinks,
/* 152 */     stringsLinks,
/* 153 */     reverseNumLinks,
/* 154 */     reverseLinks,
/* 155 */     aggregateNum,
/* 156 */     aggregateLinks,
/* 157 */     header,
/* 158 */     inMemoryIndexes;
/*     */   }
/*     */ }


/* Location:              C:\Program Files\JetBrains\WebStorm 2021.1\plugins\NodeJS\lib\NodeJS.jar!\com\jetbrains\nodejs\run\profile\heap\calculation\V8HeapIndexManager.class
 * Java compiler version: 11 (55.0)
 * JD-Core Version:       1.1.3
 */