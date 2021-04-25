/*    */ package org.bipolar.run.profile.heap.view.components;
/*    */ 
/*    */ import java.util.concurrent.atomic.AtomicInteger;
/*    */ 
/*    */ 
/*    */ 
/*    */ public class Coordinator
/*    */ {
/*    */   private final AtomicInteger myCnt;
/*    */   private final Runnable myContinuation;
/*    */   private int myOffset;
/*    */   private final Object myLock;
/*    */   
/*    */   public Coordinator(Runnable continuation) {
/* 15 */     this.myContinuation = continuation;
/* 16 */     this.myCnt = new AtomicInteger(0);
/* 17 */     this.myLock = new Object();
/* 18 */     this.myOffset = 1;
/*    */   }
/*    */   
/*    */   public Listener start() {
/*    */     final int magic;
/* 23 */     synchronized (this.myLock) {
/* 24 */       magic = this.myOffset;
/* 25 */       this.myOffset = this.myOffset * 3 + 1;
/* 26 */       this.myCnt.addAndGet(magic);
/*    */     } 
/* 28 */     return new Listener()
/*    */       {
/*    */         public void finished() {
/* 31 */           if (Coordinator.this.myCnt.addAndGet(-magic) == 0)
/* 32 */             Coordinator.this.myContinuation.run(); 
/*    */         }
/*    */       };
/*    */   }
/*    */   
/*    */   public static interface Listener {
/*    */     void finished();
/*    */   }
/*    */ }


/* Location:              C:\Program Files\JetBrains\WebStorm 2021.1\plugins\NodeJS\lib\NodeJS.jar!\com\jetbrains\nodejs\run\profile\heap\view\components\Coordinator.class
 * Java compiler version: 11 (55.0)
 * JD-Core Version:       1.1.3
 */