/*     */ package org.bipolar.run.profile.heap.io.reverse;
/*     */ 
/*     */ import com.intellij.util.Processor;
/*     */ import com.intellij.util.ThrowableConsumer;
/*     */ import org.bipolar.run.profile.heap.io.RandomRawReader;
/*     */ import org.bipolar.run.profile.heap.io.RawSerializer;
/*     */ import org.bipolar.run.profile.heap.io.SequentialRawReader;
/*     */ import org.bipolar.util.CloseableThrowableConsumer;
/*     */ import org.bipolar.util.CloseableThrowableProcessor;
/*     */ import java.io.Closeable;
/*     */ import java.io.File;
/*     */ import java.io.FileNotFoundException;
/*     */ import java.io.IOException;
/*     */ import java.util.ArrayList;
/*     */ import java.util.List;
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
/*     */ 
/*     */ 
/*     */ 
/*     */ public class LinksReader<T>
/*     */   implements Closeable
/*     */ {
/*     */   private final File myNumFile;
/*     */   private final File myLinksFile;
/*     */   private final RawSerializer<T> mySerializer;
/*     */   private final RandomRawReader<SizeOffset> myNumReader;
/*     */   private final RandomRawReader<T> myLinksReader;
/*     */   private final SequentialRawReader<SizeOffset> mySNumReader;
/*     */   private final SequentialRawReader<T> mySLinksReader;
/*     */   
/*     */   public LinksReader(File numFile, File linksFile, RawSerializer<T> serializer, boolean forSequential) throws FileNotFoundException {
/*  47 */     this.myNumFile = numFile;
/*  48 */     this.myLinksFile = linksFile;
/*  49 */     this.mySerializer = serializer;
/*  50 */     if (forSequential) {
/*  51 */       this.myNumReader = null;
/*  52 */       this.myLinksReader = null;
/*  53 */       this.mySLinksReader = new SequentialRawReader(this.myLinksFile, this.mySerializer);
/*  54 */       this.mySNumReader = new SequentialRawReader(this.myNumFile, SizeOffset.MySerializer.getInstance());
/*     */     } else {
/*  56 */       this.myNumReader = new RandomRawReader(this.myNumFile, SizeOffset.MySerializer.getInstance());
/*  57 */       this.myLinksReader = new RandomRawReader(this.myLinksFile, this.mySerializer);
/*  58 */       this.mySLinksReader = null;
/*  59 */       this.mySNumReader = null;
/*     */     } 
/*     */   }
/*     */   
/*     */   public void read(long index, @NotNull Processor<? super T> processor) throws IOException {
/*  64 */     if (SYNTHETIC_LOCAL_VARIABLE_3 == null) $$$reportNull$$$0(0);  if (this.myNumReader == null) throw new IllegalStateException(); 
/*  65 */     SizeOffset sizeOffset = (SizeOffset)this.myNumReader.read(index);
/*  66 */     this.myLinksReader.read(sizeOffset.getOffset(), sizeOffset.getSize(), (Processor)SYNTHETIC_LOCAL_VARIABLE_3);
/*     */   }
/*     */   
/*     */   public T readRandomLen(long index) throws IOException {
/*  70 */     if (this.myNumReader == null) throw new IllegalStateException(); 
/*  71 */     SizeOffset sizeOffset = (SizeOffset)this.myNumReader.read(index);
/*  72 */     return (T)this.myLinksReader.readRandomLen(sizeOffset.getOffset(), sizeOffset.getSize());
/*     */   }
/*     */   
/*     */   public void skip(long items, boolean isRandomLen) throws IOException {
/*     */     SizeOffset offset;
/*  77 */     RandomRawReader<SizeOffset> random = new RandomRawReader(this.myNumFile, SizeOffset.MySerializer.getInstance());
/*     */     try {
/*  79 */       offset = (SizeOffset)random.read(items);
/*     */     } finally {
/*  81 */       random.close();
/*     */     } 
/*  83 */     this.mySNumReader.skip(items);
/*  84 */     if (isRandomLen) {
/*  85 */       this.mySLinksReader.skipBytes((int)offset.getOffset(), offset.getSize());
/*     */     } else {
/*  87 */       this.mySLinksReader.skip(offset.getOffset());
/*     */     } 
/*     */   }
/*     */   
/*     */   public void iterateRandomLen(@NotNull final Processor<? super T> processor) throws IOException {
/*  92 */     if (processor == null) $$$reportNull$$$0(1);  this.mySNumReader.iterate(new CloseableThrowableProcessor<SizeOffset, IOException>()
/*     */         {
/*     */           public boolean process(SizeOffset offset) throws IOException {
/*  95 */             return processor.process(LinksReader.this.mySLinksReader.read());
/*     */           }
/*     */ 
/*     */           
/*     */           public void close() throws IOException {
/* 100 */             LinksReader.this.mySLinksReader.close();
/*     */           }
/*     */         });
/*     */   }
/*     */   
/*     */   public void iterate(@NotNull final Processor<? super List<T>> consumer) throws IOException {
/* 106 */     if (consumer == null) $$$reportNull$$$0(2);  this.mySNumReader.iterate(new CloseableThrowableProcessor<SizeOffset, IOException>()
/*     */         {
/*     */           public void close() throws IOException {
/* 109 */             LinksReader.this.mySLinksReader.close();
/*     */           }
/*     */ 
/*     */           
/*     */           public boolean process(SizeOffset offset) throws IOException {
/* 114 */             List<T> list = new ArrayList<>(offset.getSize());
/* 115 */             for (int i = 0; i < offset.getSize(); i++) {
/* 116 */               list.add((T)LinksReader.this.mySLinksReader.read());
/*     */             }
/* 118 */             return consumer.process(list);
/*     */           }
/*     */         });
/*     */   }
/*     */   
/*     */   public void iterateWithThrowable(@NotNull final ThrowableConsumer<List<T>, IOException> consumer) throws IOException {
/* 124 */     if (consumer == null) $$$reportNull$$$0(3);  this.mySNumReader.iterate(new CloseableThrowableConsumer<SizeOffset, IOException>()
/*     */         {
/*     */           public void close() throws IOException {
/* 127 */             LinksReader.this.mySLinksReader.close();
/*     */           }
/*     */ 
/*     */           
/*     */           public void consume(SizeOffset offset) throws IOException {
/* 132 */             List<T> list = new ArrayList<>(offset.getSize());
/* 133 */             for (int i = 0; i < offset.getSize(); i++) {
/* 134 */               list.add((T)LinksReader.this.mySLinksReader.read());
/*     */             }
/* 136 */             consumer.consume(list);
/*     */           }
/*     */         });
/*     */   }
/*     */ 
/*     */   
/*     */   public void close() throws IOException {
/* 143 */     if (this.myNumReader == null) {
/* 144 */       this.mySNumReader.close();
/* 145 */       this.mySLinksReader.close();
/*     */     } else {
/* 147 */       this.myNumReader.close();
/* 148 */       this.myLinksReader.close();
/*     */     } 
/*     */   }
/*     */ }


/* Location:              C:\Program Files\JetBrains\WebStorm 2021.1\plugins\NodeJS\lib\NodeJS.jar!\com\jetbrains\nodejs\run\profile\heap\io\reverse\LinksReader.class
 * Java compiler version: 11 (55.0)
 * JD-Core Version:       1.1.3
 */