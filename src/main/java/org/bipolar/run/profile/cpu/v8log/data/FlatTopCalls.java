/*     */ package org.bipolar.run.profile.cpu.v8log.data;
/*     */ 
/*     */ import com.intellij.openapi.util.Pair;
/*     */ import com.intellij.openapi.util.text.StringUtil;
/*     */ import org.bipolar.NodeJSBundle;
/*     */ import org.bipolar.run.profile.cpu.calculation.CallTreesSerializer;
/*     */ import org.bipolar.run.profile.cpu.calculation.V8ProfileLine;
/*     */ import org.bipolar.run.profile.heap.io.RawSerializer;
/*     */ import java.io.DataInput;
/*     */ import java.io.DataOutput;
/*     */ import java.io.IOException;
/*     */ import java.util.ArrayList;
/*     */ import java.util.List;
/*     */
import org.jetbrains.annotations.NonNls;
/*     */ import org.jetbrains.annotations.NotNull;
/*     */ 
/*     */ 
/*     */ 
/*     */ public class FlatTopCalls
/*     */ {
/*     */   @NonNls
/*     */   public static final String JAVA_SCRIPT_GROUP = "JavaScript";
/*     */   private final List<V8ProfileLine> myUnknown;
/*     */   private final List<V8ProfileLine> mySharedLibraries;
/*     */   private final List<V8ProfileLine> myJavaScript;
/*     */   private final List<V8ProfileLine> myCpp;
/*     */   private final List<V8ProfileLine> myGc;
/*     */   
/*     */   public FlatTopCalls() {
/*  30 */     this.myUnknown = new ArrayList<>();
/*  31 */     this.mySharedLibraries = new ArrayList<>();
/*  32 */     this.myJavaScript = new ArrayList<>();
/*  33 */     this.myCpp = new ArrayList<>();
/*  34 */     this.myGc = new ArrayList<>();
/*     */   }
/*     */   
/*     */   public FlatTopCalls cloneMe() {
/*  38 */     return new FlatTopCalls(this.myUnknown, this.mySharedLibraries, this.myJavaScript, this.myCpp, this.myGc);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private FlatTopCalls(List<V8ProfileLine> unknown, List<V8ProfileLine> sharedLibraries, List<V8ProfileLine> javaScript, List<V8ProfileLine> cpp, List<V8ProfileLine> gc) {
/*  46 */     this.myUnknown = unknown;
/*  47 */     this.mySharedLibraries = sharedLibraries;
/*  48 */     this.myJavaScript = javaScript;
/*  49 */     this.myCpp = cpp;
/*  50 */     this.myGc = gc;
/*     */   }
/*     */   
/*     */   public List<Pair<String, List<V8ProfileLine>>> createPresentation() {
/*  54 */     List<Pair<String, List<V8ProfileLine>>> list = new ArrayList<>();
/*  55 */     addList(list, this.myUnknown, "Unknown");
/*  56 */     addList(list, this.myGc, "GC");
/*  57 */     addList(list, this.mySharedLibraries, "Shared Libraries");
/*  58 */     addList(list, this.myJavaScript, "JavaScript");
/*  59 */     addList(list, this.myCpp, "C++");
/*  60 */     return list;
/*     */   }
/*     */   
/*     */   private void addList(List<Pair<String, List<V8ProfileLine>>> list, List<V8ProfileLine> subList, String name) {
/*  64 */     if (!subList.isEmpty()) {
/*  65 */       if (subList.size() == 1) {
/*  66 */         V8ProfileLine line = subList.get(0);
/*  67 */         if (StringUtil.isEmptyOrSpaces(line.toString())) {
/*  68 */           line.setPresentation(NodeJSBundle.message("profile.cpu.line.total.text", new Object[0]));
/*  69 */           line.setIsInternal(true);
/*     */         } 
/*     */       } 
/*  72 */       list.add(Pair.create(name, subList));
/*     */     } 
/*     */   }
/*     */   
/*     */   public List<V8ProfileLine> getUnknown() {
/*  77 */     return this.myUnknown;
/*     */   }
/*     */   
/*     */   public List<V8ProfileLine> getSharedLibraries() {
/*  81 */     return this.mySharedLibraries;
/*     */   }
/*     */   
/*     */   public List<V8ProfileLine> getJavaScript() {
/*  85 */     return this.myJavaScript;
/*     */   }
/*     */   
/*     */   public List<V8ProfileLine> getCpp() {
/*  89 */     return this.myCpp;
/*     */   }
/*     */   
/*     */   public List<V8ProfileLine> getGc() {
/*  93 */     return this.myGc;
/*     */   }
/*     */   
/*     */   public static class MySerializer
/*     */     implements RawSerializer<FlatTopCalls> {
/*     */     public long getRecordSize() {
/*  99 */       return -1L;
/*     */     }
/*     */ 
/*     */     
/*     */     public void write(@NotNull DataOutput os, @NotNull FlatTopCalls calls) throws IOException {
/* 104 */       if (os == null) $$$reportNull$$$0(0);  if (calls == null) $$$reportNull$$$0(1);  writeList(os, calls.myUnknown);
/* 105 */       writeList(os, calls.mySharedLibraries);
/* 106 */       writeList(os, calls.myJavaScript);
/* 107 */       writeList(os, calls.myCpp);
/* 108 */       writeList(os, calls.myGc);
/*     */     }
/*     */     
/*     */     private void writeList(@NotNull DataOutput os, @NotNull List<V8ProfileLine> list) throws IOException {
/* 112 */       if (os == null) $$$reportNull$$$0(2);  if (list == null) $$$reportNull$$$0(3);  os.writeInt(list.size());
/* 113 */       for (V8ProfileLine line : list) {
/* 114 */         CallTreesSerializer.writeLine(os, line);
/*     */       }
/*     */     }
/*     */     
/*     */     private void readList(@NotNull DataInput is, List<V8ProfileLine> list) throws IOException {
/* 119 */       if (is == null) $$$reportNull$$$0(4);  int size = is.readInt();
/* 120 */       for (int i = 0; i < size; i++) {
/* 121 */         list.add(CallTreesSerializer.readLine(is, null));
/*     */       }
/*     */     }
/*     */ 
/*     */     
/*     */     public FlatTopCalls read(@NotNull DataInput is) throws IOException {
/* 127 */       if (is == null) $$$reportNull$$$0(5);  FlatTopCalls calls = new FlatTopCalls();
/* 128 */       readList(is, calls.myUnknown);
/* 129 */       readList(is, calls.mySharedLibraries);
/* 130 */       readList(is, calls.myJavaScript);
/* 131 */       readList(is, calls.myCpp);
/* 132 */       readList(is, calls.myGc);
/* 133 */       return calls;
/*     */     }
/*     */   }
/*     */ }


/* Location:              C:\Program Files\JetBrains\WebStorm 2021.1\plugins\NodeJS\lib\NodeJS.jar!\com\jetbrains\nodejs\run\profile\cpu\v8log\data\FlatTopCalls.class
 * Java compiler version: 11 (55.0)
 * JD-Core Version:       1.1.3
 */