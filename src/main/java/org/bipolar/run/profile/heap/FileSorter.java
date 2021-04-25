/*     */ package org.bipolar.run.profile.heap;
/*     */ 
/*     */ import com.intellij.openapi.util.io.FileUtil;
/*     */ import com.intellij.util.Processor;
/*     */ import org.bipolar.run.profile.heap.io.RawSerializer;
/*     */ import org.bipolar.run.profile.heap.io.SequentialRawReader;
/*     */ import org.bipolar.run.profile.heap.io.SequentialRawWriter;
/*     */ import java.io.File;
/*     */ import java.io.IOException;
/*     */ import java.util.ArrayDeque;
/*     */ import java.util.ArrayList;
/*     */ import java.util.Comparator;
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
/*     */ 
/*     */ 
/*     */ public class FileSorter<T>
/*     */ {
/*     */   private static final long PACK_SIZE = 10000L;
/*     */   private long myPackSize;
/*     */   private final RawSerializer<T> mySerializer;
/*     */   private final File myInFile;
/*     */   @NotNull
/*     */   private final Comparator<? super T> myComparator;
/*     */   private File myOutFile;
/*     */   private final ArrayDeque<File> myTmpFiles;
/*     */   private Processor<? super T> myFilter;
/*     */   
/*     */   public FileSorter(@NotNull RawSerializer<T> serializer, @NotNull File inFile, @NotNull Comparator<? super T> comparator) throws IOException {
/*  48 */     this.mySerializer = serializer;
/*  49 */     this.myInFile = inFile;
/*  50 */     this.myComparator = comparator;
/*  51 */     this.myTmpFiles = new ArrayDeque<>();
/*  52 */     this.myPackSize = 10000L;
/*     */   }
/*     */   
/*     */   public void setFilter(Processor<? super T> filter) {
/*  56 */     this.myFilter = filter;
/*     */   }
/*     */   
/*     */   public void setPackSize(long packSize) {
/*  60 */     this.myPackSize = packSize;
/*     */   }
/*     */   
/*     */   public void sort() throws IOException {
/*  64 */     sortParts();
/*  65 */     connectParts();
/*     */   }
/*     */   
/*     */   private void connectParts() throws IOException {
/*  69 */     if (this.myTmpFiles.isEmpty()) {
/*  70 */       this.myOutFile = this.myInFile;
/*     */       return;
/*     */     } 
/*  73 */     while (this.myTmpFiles.size() > 1) {
/*  74 */       File first = this.myTmpFiles.removeFirst();
/*  75 */       File second = this.myTmpFiles.removeFirst();
/*  76 */       this.myTmpFiles.add(connectTwo(first, second));
/*     */     } 
/*  78 */     this.myOutFile = this.myTmpFiles.removeFirst();
/*  79 */     FileUtil.delete(this.myInFile);
/*  80 */     FileUtil.rename(this.myOutFile, this.myInFile);
/*  81 */     this.myOutFile = this.myInFile;
/*     */   }
/*     */   
/*     */   private File connectTwo(@NotNull File first, @NotNull File second) throws IOException {
/*  85 */     if (first == null) $$$reportNull$$$0(3);  if (second == null) $$$reportNull$$$0(4);  File tmp = FileUtil.createTempFile("sort", null, false);
/*  86 */     SequentialRawWriter<T> writer = new SequentialRawWriter(tmp, this.mySerializer);
/*     */     
/*  88 */     SequentialRawReader<T> readerOne = new SequentialRawReader(first, this.mySerializer);
/*  89 */     SequentialRawReader<T> readerTwo = new SequentialRawReader(second, this.mySerializer);
/*     */     
/*     */     try {
/*  92 */       T one = (T)readerOne.read();
/*  93 */       T two = (T)readerTwo.read();
/*     */       label35: while (true) {
/*  95 */         while (this.myComparator.compare(one, two) < 0) {
/*  96 */           writer.write(one);
/*  97 */           if (!readerOne.hasNext()) {
/*  98 */             writer.write(two);
/*     */             break label35;
/*     */           } 
/* 101 */           one = (T)readerOne.read();
/*     */         } 
/* 103 */         writer.write(two);
/* 104 */         if (!readerTwo.hasNext()) {
/* 105 */           writer.write(one);
/*     */           break;
/*     */         } 
/* 108 */         two = (T)readerTwo.read();
/*     */       } 
/*     */       
/* 111 */       while (readerOne.hasNext()) {
/* 112 */         writer.write(readerOne.read());
/*     */       }
/* 114 */       while (readerTwo.hasNext()) {
/* 115 */         writer.write(readerTwo.read());
/*     */       }
/*     */     } finally {
/* 118 */       writer.close();
/* 119 */       readerOne.close();
/* 120 */       readerTwo.close();
/* 121 */       FileUtil.delete(first);
/* 122 */       FileUtil.delete(second);
/*     */     } 
/* 124 */     return tmp;
/*     */   }
/*     */   
/*     */   public File getOutFile() {
/* 128 */     return this.myOutFile;
/*     */   }
/*     */   
/*     */   private void sortParts() throws IOException {
/* 132 */     SequentialRawReader<T> reader = new SequentialRawReader(this.myInFile, this.mySerializer);
/*     */     
/*     */     try {
/* 135 */       while (reader.hasNext()) {
/* 136 */         sortPart(reader);
/*     */       }
/*     */     } finally {
/* 139 */       reader.close();
/*     */     } 
/*     */   }
/*     */   
/*     */   private void sortPart(SequentialRawReader<T> reader) throws IOException {
/* 144 */     List<T> list = new ArrayList<>();
/* 145 */     while (list.size() <= this.myPackSize && reader.hasNext()) {
/* 146 */       T item = (T)reader.read();
/* 147 */       if (this.myFilter != null && !this.myFilter.process(item))
/* 148 */         continue;  list.add(item);
/*     */     } 
/* 150 */     list.sort(this.myComparator);
/*     */     
/* 152 */     File tmp = FileUtil.createTempFile("sort", null, false);
/* 153 */     SequentialRawWriter<T> writer = new SequentialRawWriter(tmp, this.mySerializer);
/*     */     try {
/* 155 */       for (T t : list) {
/* 156 */         writer.write(t);
/*     */       }
/* 158 */       this.myTmpFiles.add(tmp);
/*     */     } finally {
/* 160 */       writer.close();
/*     */     } 
/*     */   }
/*     */ }


/* Location:              C:\Program Files\JetBrains\WebStorm 2021.1\plugins\NodeJS\lib\NodeJS.jar!\com\jetbrains\nodejs\run\profile\heap\FileSorter.class
 * Java compiler version: 11 (55.0)
 * JD-Core Version:       1.1.3
 */