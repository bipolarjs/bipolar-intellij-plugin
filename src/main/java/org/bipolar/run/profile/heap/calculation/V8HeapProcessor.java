/*     */ package org.bipolar.run.profile.heap.calculation;
/*     */ 
/*     */ import com.intellij.openapi.Disposable;
/*     */ import com.intellij.openapi.diagnostic.Logger;
/*     */ import com.intellij.openapi.progress.ProgressIndicator;
/*     */ import com.intellij.util.Consumer;
/*     */ import org.bipolar.NodeJSBundle;
/*     */ import org.bipolar.run.profile.heap.CompositeCloseable;
/*     */ import org.bipolar.run.profile.heap.IndexFiles;
/*     */ import org.bipolar.run.profile.heap.TempFiles;
/*     */ import org.bipolar.run.profile.heap.TimeReporter;
/*     */ import org.bipolar.run.profile.heap.V8CachingReader;
/*     */ import org.bipolar.run.profile.heap.data.V8HeapEdge;
/*     */ import org.bipolar.run.profile.heap.data.V8HeapEntry;
/*     */ import org.bipolar.run.profile.heap.data.V8HeapHeader;
/*     */ import org.bipolar.run.profile.heap.io.RawSerializer;
/*     */ import org.bipolar.run.profile.heap.io.SequentialRawReader;
/*     */ import org.bipolar.run.profile.heap.io.SequentialRawWriter;
/*     */ import org.bipolar.util.CloseableThrowableConsumer;
/*     */ import java.io.Closeable;
/*     */ import java.io.File;
/*     */ import java.io.FileNotFoundException;
/*     */ import java.io.IOException;
/*     */
import org.jetbrains.annotations.NotNull;
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
/*     */ 
/*     */ 
/*     */ 
/*     */ public class V8HeapProcessor
/*     */   implements Disposable
/*     */ {
/*  43 */   public static final Logger LOG = Logger.getInstance(V8HeapProcessor.class);
/*     */   
/*     */   private final File myFile;
/*     */   
/*     */   private final TimeReporter myReporter;
/*     */   
/*     */   @NotNull
/*     */   private final Consumer<String> myErrorNotificator;
/*     */   
/*     */   private File myNodeIndex;
/*     */   private File myEdgeIndex;
/*     */   private V8HeapHeader myHeader;
/*     */   private ReverseIndexes myReverseIndexes;
/*     */   private V8StringIndex myStringIndex;
/*     */   private long myStringsCnt;
/*     */   private V8CachingReader myCachingReader;
/*     */   private final CompositeCloseable myResourses;
/*     */   private RetainedSizeCalculator myRetainedSizeCalculator;
/*     */   private final boolean myShowHidden;
/*     */   private V8ImportantStringsHolder myStringsHolder;
/*     */   private final V8HeapIndexManager myManager;
/*     */   
/*     */   public V8HeapProcessor(@NotNull Consumer<String> errorNotificator, File file, boolean showHidden, ProgressIndicator indicator) throws IOException {
/*  66 */     this.myErrorNotificator = errorNotificator;
/*  67 */     this.myShowHidden = showHidden;
/*  68 */     this.myReporter = new TimeReporter(NodeJSBundle.message("profile.heap.processing.action.name", new Object[0]), indicator);
/*  69 */     this.myFile = file;
/*  70 */     this.myResourses = new CompositeCloseable();
/*     */     
/*  72 */     if (this.myFile.getParentFile() == null) throw new IOException("Can not create temp index files."); 
/*  73 */     this.myManager = new V8HeapIndexManager(this.myFile, this.myShowHidden);
/*     */   }
/*     */   
/*     */   public ByteArrayWrapper getDigest() {
/*  77 */     return this.myManager.getDigest();
/*     */   }
/*     */   
/*     */   public V8CachingReader getFromCacheOrProcess() throws IOException, ClassNotFoundException {
/*  81 */     if (this.myManager.isInitialized()) {
/*  82 */       V8CachingReader v8CachingReader = this.myManager.initReader(this.myResourses, this.myErrorNotificator);
/*  83 */       if (v8CachingReader != null) {
/*  84 */         return v8CachingReader;
/*     */       }
/*     */     } 
/*     */     
/*  88 */     V8CachingReader reader = processSnapshot(this.myManager.getDigest(), true, this.myManager.getIndexFiles());
/*  89 */     this.myManager.recordReader(reader);
/*  90 */     return reader;
/*     */   }
/*     */   
/*     */   public V8CachingReader processSnapshot(@NotNull ByteArrayWrapper digest) throws IOException {
/*  94 */     if (digest == null) $$$reportNull$$$0(1);  return processSnapshot(digest, true, (IndexFiles)this.myResourses.setVeryLast((Closeable)new TempFiles(this.myFile.getName())));
/*     */   }
/*     */   
/*     */   public V8CachingReader processSnapshot(@NotNull ByteArrayWrapper digest, boolean calculateRetainedSizes) throws IOException {
/*  98 */     if (digest == null) $$$reportNull$$$0(2);  return processSnapshot(digest, calculateRetainedSizes, (IndexFiles)this.myResourses.setVeryLast((Closeable)new TempFiles(this.myFile.getName())));
/*     */   }
/*     */ 
/*     */   
/*     */   public V8CachingReader processSnapshot(@NotNull ByteArrayWrapper digest, boolean calculateRetainedSizes, IndexFiles indexFiles) throws IOException {
/* 103 */     if (digest == null) $$$reportNull$$$0(3);  try { this.myNodeIndex = indexFiles.generate(V8HeapIndexManager.Category.nodeIdx, ".node.index");
/* 104 */       this.myEdgeIndex = indexFiles.generate(V8HeapIndexManager.Category.edgeIdx, ".edge.index");
/*     */       
/* 106 */       this.myStringIndex = (V8StringIndex)this.myResourses.register(new V8StringIndex(indexFiles));
/*     */       
/* 108 */       plainRead(indexFiles);
/*     */       
/* 110 */       this.myReverseIndexes.continueCalculation(this.myEdgeIndex, this.myHeader.getEdgesCnt());
/* 111 */       this.myReporter.reportStage("Reverse indexes calculated");
/* 112 */       this.myResourses.closeAndRemove(this.myReverseIndexes);
/*     */       
/* 114 */       this
/*     */         
/* 116 */         .myCachingReader = new V8CachingReader(this.myFile, digest, this.myResourses, this.myHeader, this.myErrorNotificator, this.myStringIndex, this.myNodeIndex, this.myEdgeIndex, this.myReverseIndexes.getReverseStringIndexProcessor(), this.myReverseIndexes.getReverseLinkIndexProcessor(), this.myStringsHolder);
/*     */       
/* 118 */       if (calculateRetainedSizes) {
/* 119 */         this.myRetainedSizeCalculator = (RetainedSizeCalculator)this.myResourses.register(new RetainedSizeCalculator(this.myHeader.getNodesCnt(), this.myShowHidden));
/* 120 */         this.myRetainedSizeCalculator.execute(this.myCachingReader, this.myReverseIndexes.getReverseLinkIndexProcessor(), this.myNodeIndex, indexFiles, this.myStringsHolder);
/* 121 */         this.myCachingReader.setInMemoryIndexes(this.myRetainedSizeCalculator.getInMemoryIndexes());
/* 122 */         this.myCachingReader.setAggregatesLinksReaderFactory(this.myRetainedSizeCalculator.getAggregatesLinksReaderFactory());
/* 123 */         this.myCachingReader.resortChildren();
/*     */         
/* 125 */         this.myResourses.closeAndRemove(this.myRetainedSizeCalculator);
/* 126 */         this.myRetainedSizeCalculator = null;
/*     */       } 
/*     */       
/* 129 */       this.myReporter.reportStage("Retained sizes ready");
/* 130 */       this.myReporter.reportTotal();
/*     */       
/* 132 */       if (!this.myShowHidden) {
/* 133 */         this.myCachingReader.resetDoNotShowHidden();
/*     */       }
/*     */       
/* 136 */       return this.myCachingReader; }
/* 137 */     catch (IOException e)
/* 138 */     { this.myResourses.close();
/* 139 */       throw e; }
/* 140 */     catch (Throwable th)
/* 141 */     { this.myResourses.close();
/* 142 */       throw new RuntimeException(th); }
/*     */   
/*     */   }
/*     */   
/*     */   private void plainRead(IndexFiles indexFiles) throws IOException {
/* 147 */     HeapSnapshotReader reader = (HeapSnapshotReader)this.myResourses.register(new HeapSnapshotReader(this.myFile));
/* 148 */     this.myHeader = reader.readHeader();
/*     */     
/* 150 */     this.myReverseIndexes = (ReverseIndexes)this.myResourses.register(new ReverseIndexes(this.myHeader.getNodesCnt(), indexFiles, this.myNodeIndex));
/* 151 */     ReaderListener listener = (ReaderListener)this.myResourses.register(new ReaderListener(this.myNodeIndex, this.myEdgeIndex, this.myReverseIndexes, this.myStringIndex, this.myHeader));
/*     */     
/* 153 */     reader.readWithReader(listener);
/* 154 */     this.myResourses.closeAndRemove(listener);
/* 155 */     this.myResourses.closeAndRemove(reader);
/*     */     
/* 157 */     this.myStringsCnt = listener.getStringsCnt();
/* 158 */     this.myStringsHolder = listener.getStringsHolder();
/* 159 */     this.myStringIndex.startReading();
/* 160 */     this.myReporter.reportStage("Read file, wrote indexes");
/*     */   }
/*     */   
/*     */   public long getStringsCnt() {
/* 164 */     return this.myStringsCnt;
/*     */   }
/*     */   
/*     */   public V8ImportantStringsHolder getStringsHolder() {
/* 168 */     return this.myStringsHolder;
/*     */   }
/*     */   
/*     */   public V8HeapHeader getHeader() {
/* 172 */     return this.myHeader;
/*     */   }
/*     */ 
/*     */   
/*     */   public void dispose() {
/*     */     try {
/* 178 */       this.myResourses.close();
/*     */     }
/* 180 */     catch (IOException iOException) {}
/*     */   }
/*     */ 
/*     */   
/*     */   private static class EdgeIndexCreator
/*     */   {
/*     */     private final SequentialRawWriter<V8HeapEdge> myEdgeWriter;
/*     */     private long myEdgeIdx;
/*     */     private long myNodeIdx;
/*     */     private V8HeapEntry myNode;
/*     */     private final SequentialRawReader<V8HeapEntry> myNodesReader;
/*     */     
/*     */     EdgeIndexCreator(@NotNull File nodeIndexFile, @NotNull File edgeIndexFile, long nodesCnt) throws IOException {
/* 193 */       this.myEdgeWriter = new SequentialRawWriter(edgeIndexFile, (RawSerializer)V8HeapEdge.MyRawSerializer.getInstance());
/* 194 */       this.myNodesReader = new SequentialRawReader(nodeIndexFile, (RawSerializer)V8HeapEntry.MyRawSerializer.getInstance(), nodesCnt);
/* 195 */       this.myEdgeIdx = 0L;
/* 196 */       this.myNodeIdx = 0L;
/* 197 */       this.myNode = (V8HeapEntry)this.myNodesReader.read();
/*     */     }
/*     */     
/*     */     public void adjust(@NotNull V8HeapEdge edge) throws IOException {
/* 201 */       if (edge == null) $$$reportNull$$$0(2);  while (this.myEdgeIdx == this.myNode.getEdgesOffset() + this.myNode.getChildrenCount()) {
/* 202 */         this.myNode = (V8HeapEntry)this.myNodesReader.read();
/* 203 */         this.myNodeIdx++;
/*     */       } 
/*     */       
/* 206 */       edge.setFromIndex(this.myNodeIdx);
/*     */     }
/*     */     
/*     */     public void serialize(@NotNull V8HeapEdge edge) throws IOException {
/* 210 */       if (edge == null) $$$reportNull$$$0(3);  this.myEdgeWriter.write(edge);
/* 211 */       this.myEdgeIdx++;
/*     */     }
/*     */     
/*     */     public void close() {
/* 215 */       this.myEdgeWriter.close();
/* 216 */       this.myNodesReader.close();
/*     */     }
/*     */   }
/*     */   
/*     */   private static class ReaderListener implements HeapSnapshotListener, Closeable {
/*     */     private final V8ImportantStringsHolder myStringsHolder;
/*     */     private long myEdgesOff;
/*     */     private long myStringsCnt;
/*     */     @NotNull
/*     */     private final File myNodeIndexFile;
/*     */     @NotNull
/*     */     private final File myEdgeIndexFile;
/*     */     private final SequentialRawWriter<V8HeapEntry> myNodeWriter;
/*     */     private V8HeapProcessor.EdgeIndexCreator myEdgeIndexCreator;
/*     */     @NotNull
/*     */     private final ReverseIndexes myProcessor;
/*     */     @NotNull
/*     */     private final V8StringIndex myStringIndex;
/*     */     private final V8HeapHeader myHeader;
/*     */     private CloseableThrowableConsumer<V8HeapEdge, IOException> myEdgesFirstStageCalculator;
/*     */     private final CloseableThrowableConsumer<V8HeapEntry, IOException> myNodesFirstStageCalculator;
/*     */     
/*     */     ReaderListener(@NotNull File nodeIndexFile, @NotNull File edgeIndexFile, @NotNull ReverseIndexes processor, @NotNull V8StringIndex stringIndex, V8HeapHeader header) throws FileNotFoundException {
/* 239 */       this.myNodeIndexFile = nodeIndexFile;
/* 240 */       this.myEdgeIndexFile = edgeIndexFile;
/* 241 */       this.myProcessor = processor;
/* 242 */       this.myStringIndex = stringIndex;
/* 243 */       this.myHeader = header;
/* 244 */       this.myEdgesOff = 0L;
/*     */       
/* 246 */       this.myNodeWriter = new SequentialRawWriter(this.myNodeIndexFile, (RawSerializer)V8HeapEntry.MyRawSerializer.getInstance());
/* 247 */       this.myNodesFirstStageCalculator = this.myProcessor.getNodesFirstStageCalculator();
/* 248 */       this.myStringsHolder = new V8ImportantStringsHolder();
/*     */     }
/*     */ 
/*     */     
/*     */     public void allNodesRead() throws IOException {
/* 253 */       this.myNodeWriter.close();
/* 254 */       this.myEdgeIndexCreator = new V8HeapProcessor.EdgeIndexCreator(this.myNodeIndexFile, this.myEdgeIndexFile, this.myHeader.getNodesCnt());
/* 255 */       this.myEdgesFirstStageCalculator = this.myProcessor.getEdgesFirstStageCalculator();
/*     */     }
/*     */ 
/*     */     
/*     */     public void allEdgesRead() {
/* 260 */       this.myEdgeIndexCreator.close();
/*     */     }
/*     */ 
/*     */     
/*     */     public void stringsCount(long cnt) {
/* 265 */       this.myProcessor.correctStringsSize(cnt);
/* 266 */       this.myStringsCnt = cnt;
/*     */     }
/*     */ 
/*     */     
/*     */     public void accept(@NotNull V8HeapEntry node) throws IOException {
/* 271 */       if (node == null) $$$reportNull$$$0(4);  node.setEdgesOffset(this.myEdgesOff);
/* 272 */       this.myNodesFirstStageCalculator.consume(node);
/* 273 */       this.myNodeWriter.write(node);
/* 274 */       this.myEdgesOff += node.getChildrenCount();
/*     */     }
/*     */ 
/*     */     
/*     */     public void accept(@NotNull V8HeapEdge edge) throws IOException {
/* 279 */       if (edge == null) $$$reportNull$$$0(5);  this.myEdgeIndexCreator.adjust(edge);
/* 280 */       this.myEdgesFirstStageCalculator.consume(edge);
/* 281 */       this.myEdgeIndexCreator.serialize(edge);
/*     */     }
/*     */ 
/*     */     
/*     */     public void accept(@NotNull String name) throws IOException {
/* 286 */       if (name == null) $$$reportNull$$$0(6);  long id = this.myStringIndex.addString(name);
/* 287 */       this.myStringsHolder.accept(id, name);
/*     */     }
/*     */ 
/*     */     
/*     */     public void close() throws IOException {
/* 292 */       this.myStringsHolder.onFinish();
/* 293 */       if (this.myNodeWriter != null) {
/* 294 */         this.myNodeWriter.close();
/*     */       }
/* 296 */       if (this.myEdgeIndexCreator != null) {
/* 297 */         this.myEdgeIndexCreator.close();
/*     */       }
/* 299 */       if (this.myEdgesFirstStageCalculator != null) {
/* 300 */         this.myEdgesFirstStageCalculator.close();
/*     */       }
/* 302 */       if (this.myNodesFirstStageCalculator != null) {
/* 303 */         this.myNodesFirstStageCalculator.close();
/*     */       }
/*     */     }
/*     */     
/*     */     public long getStringsCnt() {
/* 308 */       return this.myStringsCnt;
/*     */     }
/*     */     
/*     */     public V8ImportantStringsHolder getStringsHolder() {
/* 312 */       return this.myStringsHolder;
/*     */     }
/*     */   }
/*     */ }


/* Location:              C:\Program Files\JetBrains\WebStorm 2021.1\plugins\NodeJS\lib\NodeJS.jar!\com\jetbrains\nodejs\run\profile\heap\calculation\V8HeapProcessor.class
 * Java compiler version: 11 (55.0)
 * JD-Core Version:       1.1.3
 */