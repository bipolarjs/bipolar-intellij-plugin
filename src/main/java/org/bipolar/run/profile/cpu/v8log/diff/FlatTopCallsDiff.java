/*    */ package org.bipolar.run.profile.cpu.v8log.diff;
/*    */ 
/*    */ import com.intellij.openapi.util.Pair;
/*    */ import java.util.ArrayList;
/*    */ import java.util.List;
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public class FlatTopCallsDiff
/*    */ {
/*    */   private final List<DiffNode> myUnknown;
/*    */   private final List<DiffNode> mySharedLibraries;
/*    */   private final List<DiffNode> myJavaScript;
/*    */   private final List<DiffNode> myCpp;
/*    */   private final List<DiffNode> myGc;
/*    */   
/*    */   public FlatTopCallsDiff(List<DiffNode> unknown, List<DiffNode> sharedLibraries, List<DiffNode> javaScript, List<DiffNode> cpp, List<DiffNode> gc) {
/* 23 */     this.myUnknown = unknown;
/* 24 */     this.mySharedLibraries = sharedLibraries;
/* 25 */     this.myJavaScript = javaScript;
/* 26 */     this.myCpp = cpp;
/* 27 */     this.myGc = gc;
/*    */   }
/*    */   
/*    */   public List<Pair<String, List<DiffNode>>> createPresentation() {
/* 31 */     List<Pair<String, List<DiffNode>>> list = new ArrayList<>();
/* 32 */     addList(list, this.myUnknown, "Unknown");
/* 33 */     addList(list, this.myGc, "GC");
/* 34 */     addList(list, this.mySharedLibraries, "Shared Libraries");
/* 35 */     addList(list, this.myJavaScript, "JavaScript");
/* 36 */     addList(list, this.myCpp, "C++");
/* 37 */     return list;
/*    */   }
/*    */   
/*    */   private void addList(List<Pair<String, List<DiffNode>>> list, List<DiffNode> subList, String name) {
/* 41 */     if (!subList.isEmpty())
/* 42 */       list.add(Pair.create(name, subList)); 
/*    */   }
/*    */ }


/* Location:              C:\Program Files\JetBrains\WebStorm 2021.1\plugins\NodeJS\lib\NodeJS.jar!\com\jetbrains\nodejs\run\profile\cpu\v8log\diff\FlatTopCallsDiff.class
 * Java compiler version: 11 (55.0)
 * JD-Core Version:       1.1.3
 */