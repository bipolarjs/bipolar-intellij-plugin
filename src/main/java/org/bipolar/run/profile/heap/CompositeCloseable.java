/*    */ package org.bipolar.run.profile.heap;
/*    */ 
/*    */ import com.intellij.openapi.diagnostic.Logger;
/*    */ import java.io.Closeable;
/*    */ import java.io.IOException;
/*    */ import java.util.ArrayList;
/*    */ import java.util.List;
/*    */ import java.util.concurrent.atomic.AtomicBoolean;
/*    */ import org.jetbrains.annotations.NotNull;
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
/*    */ public class CompositeCloseable
/*    */   implements Closeable
/*    */ {
/* 31 */   private final Logger LOG = Logger.getInstance(CompositeCloseable.class);
/*    */   private final List<Closeable> myList;
/*    */   private Closeable myVeryLast;
/*    */   private final AtomicBoolean myDisposeStarted;
/*    */   
/*    */   public CompositeCloseable() {
/* 37 */     this.myList = new ArrayList<>();
/* 38 */     this.myDisposeStarted = new AtomicBoolean(false);
/*    */   }
/*    */   
/*    */   public <T extends Closeable> T register(@NotNull T closeable) {
/* 42 */     if (closeable == null) $$$reportNull$$$0(0);  this.myList.add((Closeable)closeable);
/* 43 */     return closeable;
/*    */   }
/*    */ 
/*    */   
/*    */   public void close() throws IOException {
/* 48 */     this.myDisposeStarted.set(true);
/* 49 */     for (Closeable closeable : this.myList) {
/*    */       try {
/* 51 */         closeable.close();
/* 52 */       } catch (IOException e) {
/* 53 */         this.LOG.info(e);
/*    */       } 
/*    */     } 
/* 56 */     if (this.myVeryLast != null) {
/* 57 */       this.myVeryLast.close();
/*    */     }
/*    */   }
/*    */ 
/*    */   
/*    */   public <T extends Closeable> void closeAndRemove(@NotNull T closeable) {
/* 63 */     if (closeable == null) $$$reportNull$$$0(1);  try { closeable.close(); }
/*    */     
/* 65 */     catch (IOException e)
/* 66 */     { this.LOG.info(e); }
/*    */     
/* 68 */     this.myList.remove(closeable);
/*    */   }
/*    */   
/*    */   public <T extends Closeable> T setVeryLast(T veryLast) {
/* 72 */     this.myVeryLast = (Closeable)veryLast;
/* 73 */     return veryLast;
/*    */   }
/*    */   
/*    */   public boolean isDisposeStarted() {
/* 77 */     return this.myDisposeStarted.get();
/*    */   }
/*    */ }


/* Location:              C:\Program Files\JetBrains\WebStorm 2021.1\plugins\NodeJS\lib\NodeJS.jar!\com\jetbrains\nodejs\run\profile\heap\CompositeCloseable.class
 * Java compiler version: 11 (55.0)
 * JD-Core Version:       1.1.3
 */