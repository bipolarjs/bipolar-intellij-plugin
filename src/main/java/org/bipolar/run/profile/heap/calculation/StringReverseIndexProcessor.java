/*     */ package org.bipolar.run.profile.heap.calculation;
/*     */ 
/*     */ import com.intellij.util.PairConsumer;
/*     */ import org.bipolar.run.profile.heap.IndexFiles;
/*     */ import org.bipolar.run.profile.heap.data.LinkedByNameId;
/*     */ import org.bipolar.run.profile.heap.data.V8HeapEdge;
/*     */ import org.bipolar.run.profile.heap.data.V8HeapEntry;
/*     */ import org.bipolar.run.profile.heap.io.RawSerializer;
/*     */ import org.bipolar.run.profile.heap.io.SequentialRawReader;
/*     */ import org.bipolar.run.profile.heap.io.reverse.LinksReaderFactory;
/*     */ import org.bipolar.util.CloseableThrowableConsumer;
/*     */ import java.io.DataInput;
/*     */ import java.io.DataOutput;
/*     */ import java.io.File;
/*     */ import java.io.FileNotFoundException;
/*     */ import java.io.IOException;
/*     */ import java.util.HashSet;
/*     */ import java.util.Iterator;
/*     */ import java.util.Set;
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
/*     */ public class StringReverseIndexProcessor
/*     */   implements EdgeProcessor
/*     */ {
/*     */   private final ProcessInPortions<Intermediate, LinkedByNameId> myDelegate;
/*     */   @NotNull
/*     */   private final File myNodesIndexFile;
/*     */   private CloseableThrowableConsumer<Intermediate, IOException> myFirstStageCalculator;
/*     */   
/*     */   public StringReverseIndexProcessor(@NotNull IndexFiles indexFiles, @NotNull File nodesIndexFile) throws IOException {
/*  44 */     this.myNodesIndexFile = nodesIndexFile;
/*  45 */     this
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */       
/*  55 */       .myDelegate = new ProcessInPortions<>(-1L, -1L, new IntermediateRawSerializer(), (RawSerializer<LinkedByNameId>)LinkedByNameId.Serializer.getInstance(), trinity -> Long.valueOf(trinity.getNameId()), trinity -> { LinkedByNameId nameId = new LinkedByNameId(trinity.isNode() ? trinity.getNodeId() : trinity.getEdgeId(), trinity.isNode()); if (!trinity.isNode()) nameId.setSecondId(trinity.getNodeId());  return nameId; }indexFiles.generate(V8HeapIndexManager.Category.stringsNumLinks, ".num.index"), indexFiles.generate(V8HeapIndexManager.Category.stringsLinks, ".index"));
/*  56 */     this.myDelegate.setFilter(new MyFilter());
/*  57 */     this.myFirstStageCalculator = this.myDelegate.getFirstStageCalculator();
/*     */   }
/*     */   
/*     */   public void correctSize(long stringsSize) {
/*  61 */     this.myDelegate.correctSize(stringsSize);
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public void continueCalculation(@NotNull File edgeIndexFile, long numEdges, long nodesCnt) throws IOException {
/*  67 */     if (edgeIndexFile == null) $$$reportNull$$$0(2);  this.myFirstStageCalculator.close();
/*  68 */     this.myFirstStageCalculator = null;
/*     */     
/*  70 */     this.myDelegate.continueCalculation(consumer -> {
/*     */           (new SequentialRawReader(this.myNodesIndexFile, (RawSerializer)V8HeapEntry.MyRawSerializer.getInstance(), nodesCnt)).iterate(createNodeProxy(consumer));
/*     */           (new SequentialRawReader(edgeIndexFile, (RawSerializer)V8HeapEdge.MyRawSerializer.getInstance(), numEdges)).iterate(createEdgeProxy(consumer));
/*     */           consumer.close();
/*     */         });
/*     */   }
/*     */ 
/*     */   
/*     */   private static CloseableThrowableConsumer<V8HeapEntry, IOException> createNodeProxy(@NotNull final CloseableThrowableConsumer<Intermediate, IOException> consumer) {
/*  79 */     if (consumer == null) $$$reportNull$$$0(3);  return new CloseableThrowableConsumer<V8HeapEntry, IOException>()
/*     */       {
/*     */         public void close() throws IOException {}
/*     */ 
/*     */ 
/*     */         
/*     */         public void consume(V8HeapEntry entry) throws IOException {
/*  86 */           consumer.consume(new StringReverseIndexProcessor.Intermediate(entry.getId(), -1L, entry.getNameId()));
/*     */         }
/*     */       };
/*     */   }
/*     */ 
/*     */   
/*     */   private static CloseableThrowableConsumer<V8HeapEdge, IOException> createEdgeProxy(@NotNull final CloseableThrowableConsumer<Intermediate, IOException> consumer) {
/*  93 */     if (consumer == null) $$$reportNull$$$0(4);  return new CloseableThrowableConsumer<V8HeapEdge, IOException>()
/*     */       {
/*     */         public void close() throws IOException {}
/*     */ 
/*     */ 
/*     */         
/*     */         public void consume(V8HeapEdge edge) throws IOException {
/* 100 */           if (edge.hasStringName()) {
/* 101 */             consumer.consume(new StringReverseIndexProcessor.Intermediate(edge.getToIndex(), edge.getId(), edge.getNameId()));
/*     */           }
/*     */         }
/*     */       };
/*     */   }
/*     */   
/*     */   public CloseableThrowableConsumer<V8HeapEntry, IOException> getNodesFirstStageCalculator() {
/* 108 */     return createNodeProxy(this.myFirstStageCalculator);
/*     */   }
/*     */ 
/*     */   
/*     */   public CloseableThrowableConsumer<V8HeapEdge, IOException> getFirstStageCalculator() {
/* 113 */     return createEdgeProxy(this.myFirstStageCalculator);
/*     */   }
/*     */ 
/*     */   
/*     */   public void close() throws IOException {
/* 118 */     if (this.myFirstStageCalculator != null) {
/* 119 */       this.myFirstStageCalculator.close();
/*     */     }
/* 121 */     this.myDelegate.close();
/*     */   }
/*     */   
/*     */   public LinksReaderFactory<LinkedByNameId> getLinksReaderFactory() throws FileNotFoundException {
/* 125 */     return this.myDelegate.getLinksReaderFactory();
/*     */   }
/*     */   
/*     */   private static class Intermediate {
/*     */     private final long myNodeId;
/*     */     private final long myEdgeId;
/*     */     private final long myNameId;
/*     */     
/*     */     Intermediate(long nodeId, long edgeId, long nameId) {
/* 134 */       this.myNodeId = nodeId;
/* 135 */       this.myEdgeId = edgeId;
/* 136 */       this.myNameId = nameId;
/*     */     }
/*     */     
/*     */     public boolean isNode() {
/* 140 */       return (this.myEdgeId == -1L);
/*     */     }
/*     */     
/*     */     public long getNodeId() {
/* 144 */       return this.myNodeId;
/*     */     }
/*     */     
/*     */     public long getEdgeId() {
/* 148 */       return this.myEdgeId;
/*     */     }
/*     */     
/*     */     public long getNameId() {
/* 152 */       return this.myNameId;
/*     */     }
/*     */   }
/*     */   
/*     */   private static class IntermediateRawSerializer
/*     */     implements RawSerializer<Intermediate> {
/*     */     public void write(@NotNull DataOutput os, @NotNull StringReverseIndexProcessor.Intermediate intermediate) throws IOException {
/* 159 */       if (os == null) $$$reportNull$$$0(0);  if (intermediate == null) $$$reportNull$$$0(1);  RawSerializer.Helper.serializeLong(intermediate.getNodeId(), os);
/* 160 */       RawSerializer.Helper.serializeLong(intermediate.getEdgeId(), os);
/* 161 */       RawSerializer.Helper.serializeLong(intermediate.getNameId(), os);
/*     */     }
/*     */ 
/*     */     
/*     */     public long getRecordSize() {
/* 166 */       return 24L;
/*     */     }
/*     */ 
/*     */ 
/*     */     
/*     */     public StringReverseIndexProcessor.Intermediate read(@NotNull DataInput is) throws IOException {
/* 172 */       if (is == null) $$$reportNull$$$0(2);  return new StringReverseIndexProcessor.Intermediate(
/* 173 */           RawSerializer.Helper.deserializeLong(is), 
/* 174 */           RawSerializer.Helper.deserializeLong(is), 
/* 175 */           RawSerializer.Helper.deserializeLong(is));
/*     */     }
/*     */   }
/*     */   
/*     */   private static class MyFilter
/*     */     implements PairConsumer<Long, Set<LinkedByNameId>>
/*     */   {
/*     */     public void consume(Long aLong, Set<LinkedByNameId> ids) {
/* 183 */       Set<Long> nodesWithLinks = new HashSet<>();
/* 184 */       for (LinkedByNameId id : ids) {
/* 185 */         if (!id.isNode()) {
/* 186 */           nodesWithLinks.add(Long.valueOf(id.getSecondId()));
/*     */         }
/*     */       } 
/* 189 */       Iterator<LinkedByNameId> iterator = ids.iterator();
/* 190 */       while (iterator.hasNext()) {
/* 191 */         LinkedByNameId id = iterator.next();
/* 192 */         if (id.isNode() && nodesWithLinks.contains(Long.valueOf(id.getId())))
/* 193 */           iterator.remove(); 
/*     */       } 
/*     */     }
/*     */   }
/*     */ }


/* Location:              C:\Program Files\JetBrains\WebStorm 2021.1\plugins\NodeJS\lib\NodeJS.jar!\com\jetbrains\nodejs\run\profile\heap\calculation\StringReverseIndexProcessor.class
 * Java compiler version: 11 (55.0)
 * JD-Core Version:       1.1.3
 */