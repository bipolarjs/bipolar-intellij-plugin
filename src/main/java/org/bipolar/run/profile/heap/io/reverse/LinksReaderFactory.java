/*    */ package org.bipolar.run.profile.heap.io.reverse;
/*    */ 
/*    */ import org.bipolar.run.profile.heap.io.RawSerializer;
/*    */ import java.io.File;
/*    */ import java.io.FileNotFoundException;
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public class LinksReaderFactory<Out>
/*    */ {
/*    */   private final RawSerializer<Out> mySerializer;
/*    */   private final File myNumFile;
/*    */   private final File myFile;
/*    */   
/*    */   public LinksReaderFactory(RawSerializer<Out> serializer, File numFile, File file) {
/* 32 */     this.mySerializer = serializer;
/* 33 */     this.myNumFile = numFile;
/* 34 */     this.myFile = file;
/*    */   }
/*    */   
/*    */   public LinksReader<Out> create(boolean forSequential) throws FileNotFoundException {
/* 38 */     return new LinksReader<>(this.myNumFile, this.myFile, this.mySerializer, forSequential);
/*    */   }
/*    */   
/*    */   public File getNumFile() {
/* 42 */     return this.myNumFile;
/*    */   }
/*    */   
/*    */   public File getFile() {
/* 46 */     return this.myFile;
/*    */   }
/*    */ }


/* Location:              C:\Program Files\JetBrains\WebStorm 2021.1\plugins\NodeJS\lib\NodeJS.jar!\com\jetbrains\nodejs\run\profile\heap\io\reverse\LinksReaderFactory.class
 * Java compiler version: 11 (55.0)
 * JD-Core Version:       1.1.3
 */