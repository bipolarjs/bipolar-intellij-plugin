/*     */ package org.bipolar.run.profile.heap.calculation;
/*     */ 
/*     */ import com.intellij.openapi.diagnostic.Logger;
/*     */ import com.intellij.openapi.util.text.StringUtil;
/*     */ import com.intellij.util.containers.ContainerUtil;
/*     */ import java.io.Externalizable;
/*     */ import java.io.IOException;
/*     */ import java.io.ObjectInput;
/*     */ import java.io.ObjectOutput;
/*     */ import java.util.Collections;
/*     */ import java.util.HashMap;
/*     */ import java.util.HashSet;
/*     */ import java.util.Map;
/*     */ import java.util.Set;
/*     */ 
/*     */ public class V8ImportantStringsHolder
/*     */   implements Externalizable {
/*     */   public static final String SLOPPY = "sloppy_function_map";
/*     */   public static final String SYSTEM_NATIVE = "system / NativeContext";
/*     */   public static final String MAP_DESCRIPTORS = "(map descriptors)";
/*     */   public static final String DETACHED_TREE = "(Detached DOM trees)";
/*     */   public static final String DOCUMENT_TREE = "(Document DOM trees)";
/*     */   public static final String GC_ROOTS = "(GC roots)";
/*  24 */   private static final Logger LOG = Logger.getInstance(V8ImportantStringsHolder.class);
/*     */   private final Map<String, Long> myMap;
/*     */   private final Set<Long> myWindowStrings;
/*     */   private int myCnt;
/*     */   private boolean myIsFrontend;
/*     */   
/*     */   public V8ImportantStringsHolder() {
/*  31 */     this.myMap = Collections.synchronizedMap(new HashMap<>());
/*  32 */     this.myMap.put("sloppy_function_map", Long.valueOf(-1L));
/*  33 */     this.myMap.put("system / NativeContext", Long.valueOf(-1L));
/*  34 */     this.myMap.put("(map descriptors)", Long.valueOf(-1L));
/*     */     
/*  36 */     this.myMap.put("(Detached DOM trees)", Long.valueOf(-1L));
/*  37 */     this.myMap.put("(Document DOM trees)", Long.valueOf(-1L));
/*  38 */     this.myMap.put("(GC roots)", Long.valueOf(-1L));
/*  39 */     this.myCnt = this.myMap.size();
/*     */     
/*  41 */     this.myWindowStrings = new HashSet<>();
/*     */   }
/*     */   
/*     */   public void accept(long id, String str) {
/*  45 */     if (this.myCnt > 0 && this.myMap.containsKey(str)) {
/*  46 */       this.myMap.put(str, Long.valueOf(id));
/*  47 */       this.myCnt--;
/*     */     } 
/*  49 */     if (str.startsWith("Window")) {
/*  50 */       this.myWindowStrings.add(Long.valueOf(id));
/*     */     }
/*     */   }
/*     */   
/*     */   public boolean isWindowString(long id) {
/*  55 */     return this.myWindowStrings.contains(Long.valueOf(id));
/*     */   }
/*     */   
/*     */   public long get(String str) {
/*  59 */     Long id = this.myMap.get(str);
/*  60 */     return (id == null) ? -1L : id.longValue();
/*     */   }
/*     */ 
/*     */   
/*     */   public void writeExternal(ObjectOutput out) throws IOException {
/*  65 */     out.writeInt(this.myMap.size());
/*  66 */     for (Map.Entry<String, Long> entry : this.myMap.entrySet()) {
/*  67 */       out.writeUTF(entry.getKey());
/*  68 */       out.writeLong(((Long)entry.getValue()).longValue());
/*     */     } 
/*  70 */     out.writeInt(this.myWindowStrings.size());
/*  71 */     for (Long id : this.myWindowStrings) {
/*  72 */       out.writeLong(id.longValue());
/*     */     }
/*     */   }
/*     */ 
/*     */   
/*     */   public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
/*  78 */     int size = in.readInt();
/*  79 */     for (int i = 0; i < size; i++) {
/*  80 */       this.myMap.put(in.readUTF(), Long.valueOf(in.readLong()));
/*     */     }
/*  82 */     int winSize = in.readInt();
/*  83 */     for (int j = 0; j < winSize; j++) {
/*  84 */       this.myWindowStrings.add(Long.valueOf(in.readLong()));
/*     */     }
/*     */   }
/*     */   
/*     */   public boolean isFrontend() {
/*  89 */     return this.myIsFrontend;
/*     */   }
/*     */   
/*     */   public void onFinish() {
/*  93 */     this.myIsFrontend = true;
/*  94 */     if (this.myCnt > 0)
/*  95 */       if (this.myCnt == 2 && ((Long)this.myMap.get("(Detached DOM trees)")).longValue() == -1L && ((Long)this.myMap.get("(Document DOM trees)")).longValue() == -1L) {
/*  96 */         this.myIsFrontend = false;
/*  97 */         this.myCnt = 0;
/*     */       } else {
/*  99 */         String values = StringUtil.join(ContainerUtil.map(this.myMap.entrySet(), entry -> (String)entry.getKey() + ":" + (String)entry.getKey()), ", ");
/*     */         
/* 101 */         LOG.info("V8 strings holder: some of the important strings were not found: " + values);
/*     */       }  
/*     */   }
/*     */ }


/* Location:              C:\Program Files\JetBrains\WebStorm 2021.1\plugins\NodeJS\lib\NodeJS.jar!\com\jetbrains\nodejs\run\profile\heap\calculation\V8ImportantStringsHolder.class
 * Java compiler version: 11 (55.0)
 * JD-Core Version:       1.1.3
 */