/*     */ package org.bipolar.run.profile.cpu.v8log.calculation;
/*     */ 
/*     */ import com.intellij.openapi.util.text.StringUtil;
/*     */ import com.intellij.util.containers.BidirectionalMap;
/*     */ import org.bipolar.run.profile.cpu.v8log.data.CodeState;
/*     */ import org.bipolar.run.profile.cpu.v8log.data.Counter;
/*     */ import java.math.BigInteger;
/*     */ import java.util.HashMap;
/*     */ import java.util.Iterator;
/*     */ import java.util.List;
/*     */ import java.util.Map;
/*     */ import java.util.NavigableMap;
/*     */ import java.util.TreeMap;
/*     */ import org.jetbrains.annotations.NotNull;
/*     */ 
/*     */ 
/*     */ public class CodeMap
/*     */ {
/*     */   private static final int PAGE_ALIGNMENT = 12;
/*     */   private static final int PAGE_SIZE = 4096;
/*     */   private final Map<String, Counter> myNameGeneratorMap;
/*     */   private final TreeMap<BigInteger, DynamicCodeEntry> myDynamics;
/*     */   private final TreeMap<BigInteger, CodeEntry> myStatics;
/*     */   private final TreeMap<BigInteger, CodeEntry> myLibraries;
/*     */   private final Map<BigInteger, Integer> myPages;
/*     */   private final BidirectionalMap<String, Long> myStringsMap;
/*     */   
/*     */   public CodeMap() {
/*  29 */     this.myNameGeneratorMap = new HashMap<>();
/*  30 */     this.myDynamics = new TreeMap<>();
/*  31 */     this.myStatics = new TreeMap<>();
/*  32 */     this.myLibraries = new TreeMap<>();
/*  33 */     this.myPages = new HashMap<>();
/*  34 */     this.myStringsMap = new BidirectionalMap();
/*  35 */     this.myStringsMap.put("(Garbage collection)", Long.valueOf(0L));
/*  36 */     this.myStringsMap.put("(Unknown code)", Long.valueOf(1L));
/*  37 */     this.myStringsMap.put("(Stacktrace cut)", Long.valueOf(2L));
/*     */   }
/*     */   
/*     */   public String getStringByCode(long code) {
/*  41 */     List<String> value = this.myStringsMap.getKeysByValue(Long.valueOf(code));
/*  42 */     return (value == null) ? null : value.get(0);
/*     */   }
/*     */   
/*     */   public Long getStringCode(String s) {
/*  46 */     Long code = (Long)this.myStringsMap.get(s);
/*  47 */     if (code == null) {
/*  48 */       Long value = Long.valueOf(this.myStringsMap.size());
/*  49 */       this.myStringsMap.put(s, value);
/*  50 */       return value;
/*     */     } 
/*  52 */     return code;
/*     */   }
/*     */   
/*     */   public BidirectionalMap<String, Long> getStringsMap() {
/*  56 */     return this.myStringsMap;
/*     */   }
/*     */   
/*     */   private void ensureString(CodeEntry codeEntry) {
/*  60 */     String name = codeEntry.getName();
/*  61 */     if (!this.myStringsMap.containsKey(name)) {
/*  62 */       this.myStringsMap.put(name, Long.valueOf(this.myStringsMap.size()));
/*     */     }
/*     */   }
/*     */   
/*     */   public void addCode(BigInteger start, @NotNull DynamicCodeEntry codeEntry) {
/*  67 */     if (codeEntry == null) $$$reportNull$$$0(0);  deleteFromDynamics(start, start.add(BigInteger.valueOf(codeEntry.getSize())));
/*  68 */     ensureString(codeEntry);
/*  69 */     this.myDynamics.put(start, codeEntry);
/*     */   }
/*     */   
/*     */   public boolean moveCode(BigInteger from, BigInteger to) {
/*  73 */     DynamicCodeEntry removed = this.myDynamics.remove(from);
/*  74 */     if (removed == null) {
/*  75 */       return false;
/*     */     }
/*  77 */     addCode(to, removed);
/*  78 */     return true;
/*     */   }
/*     */   
/*     */   public boolean deleteCode(BigInteger start) {
/*  82 */     return (this.myDynamics.remove(start) != null);
/*     */   }
/*     */   
/*     */   public void addLibrary(BigInteger start, @NotNull CodeEntry library) {
/*  86 */     if (library == null) $$$reportNull$$$0(1);  markPages(start, start.add(BigInteger.valueOf(library.getSize())));
/*  87 */     ensureString(library);
/*  88 */     this.myLibraries.put(start, library);
/*     */   }
/*     */   
/*     */   public void addStaticCode(BigInteger start, @NotNull CodeEntry codeEntry) {
/*  92 */     if (codeEntry == null) $$$reportNull$$$0(2);  ensureString(codeEntry);
/*  93 */     this.myStatics.put(start, codeEntry);
/*     */   }
/*     */   
/*     */   private <T extends CodeEntry> T findInTree(TreeMap<BigInteger, T> map, BigInteger addr) {
/*  97 */     if (map.isEmpty()) return null; 
/*  98 */     Map.Entry<BigInteger, T> entry = map.floorEntry(addr);
/*  99 */     if (entry == null) return null; 
/* 100 */     BigInteger key = entry.getKey();
/* 101 */     return (key.compareTo(addr) <= 0 && addr.compareTo(key.add(BigInteger.valueOf(((CodeEntry)entry.getValue()).getSize()))) < 0) ? entry.getValue() : null;
/*     */   }
/*     */   
/*     */   public CodeEntry findEntry(BigInteger addr) {
/* 105 */     BigInteger pageAddr = pageAlignmentShift(addr);
/* 106 */     if (this.myPages.containsKey(pageAddr)) {
/* 107 */       CodeEntry entry = findInTree(this.myStatics, addr);
/* 108 */       if (entry != null) return entry; 
/* 109 */       return findInTree(this.myLibraries, addr);
/*     */     } 
/* 111 */     if (this.myDynamics.isEmpty()) return null; 
/* 112 */     BigInteger min = this.myDynamics.firstKey();
/* 113 */     Map.Entry<BigInteger, DynamicCodeEntry> last = this.myDynamics.lastEntry();
/* 114 */     if (min.compareTo(addr) <= 0 && addr.compareTo(((BigInteger)last.getKey()).add(BigInteger.valueOf(((DynamicCodeEntry)last.getValue()).getSize()))) < 0) {
/* 115 */       DynamicCodeEntry entry = findInTree(this.myDynamics, addr);
/* 116 */       if (entry == null) return null;
/*     */       
/* 118 */       if (entry.getName().isEmpty()) {
/* 119 */         Counter counter = this.myNameGeneratorMap.get(entry.getName());
/* 120 */         if (counter == null) {
/* 121 */           this.myNameGeneratorMap.put(entry.getName(), new Counter());
/*     */         } else {
/* 123 */           entry.updateName(entry.getName() + " {" + entry.getName() + "}");
/*     */         } 
/*     */       } 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */       
/* 134 */       return entry;
/*     */     } 
/* 136 */     return null;
/*     */   }
/*     */   
/*     */   private BigInteger pageAlignmentShift(BigInteger addr) {
/* 140 */     return BigInteger.valueOf(addr.divide(BigInteger.valueOf(2L)).longValue() >>> 11L);
/*     */   }
/*     */   
/*     */   public DynamicCodeEntry findDynamicByStart(BigInteger addr) {
/* 144 */     return this.myDynamics.get(addr);
/*     */   }
/*     */   
/*     */   private void markPages(BigInteger start, BigInteger end) {
/* 148 */     for (BigInteger addr = start; addr.compareTo(end) <= 0; addr = addr.add(BigInteger.valueOf(4096L))) {
/* 149 */       this.myPages.put(pageAlignmentShift(addr), Integer.valueOf(1));
/*     */     }
/*     */   }
/*     */   
/*     */   private void deleteFromDynamics(BigInteger start, BigInteger end) {
/* 154 */     if (this.myDynamics.isEmpty())
/* 155 */       return;  NavigableMap<BigInteger, DynamicCodeEntry> head = (NavigableMap)this.myDynamics.headMap(end);
/* 156 */     NavigableMap<BigInteger, DynamicCodeEntry> desc = head.descendingMap();
/* 157 */     Iterator<Map.Entry<BigInteger, DynamicCodeEntry>> iterator = desc.entrySet().iterator();
/* 158 */     while (iterator.hasNext()) {
/* 159 */       Map.Entry<BigInteger, DynamicCodeEntry> entry = iterator.next();
/* 160 */       if (((BigInteger)entry.getKey()).compareTo(start) < 0)
/* 161 */         return;  iterator.remove();
/*     */     } 
/*     */   }
/*     */   
/*     */   public void editName(CodeEntry codeEntry, String newName) {
/* 166 */     codeEntry.setName(newName);
/* 167 */     ensureString(codeEntry);
/*     */   }
/*     */   
/*     */   public static class CodeEntry {
/*     */     private final int mySize;
/*     */     protected String myName;
/*     */     private boolean myNameUpdated;
/*     */     
/*     */     public CodeEntry(int size, String name) {
/* 176 */       this.mySize = size;
/* 177 */       this.myName = name;
/*     */     }
/*     */     
/*     */     public int getSize() {
/* 181 */       return this.mySize;
/*     */     }
/*     */     
/*     */     public String getName() {
/* 185 */       return this.myName;
/*     */     }
/*     */     
/*     */     public String getRawName() {
/* 189 */       return this.myName;
/*     */     }
/*     */     
/*     */     public boolean isNameUpdated() {
/* 193 */       return this.myNameUpdated;
/*     */     }
/*     */     
/*     */     public void updateName(String newName) {
/* 197 */       if (this.myNameUpdated)
/* 198 */         return;  this.myNameUpdated = true;
/* 199 */       this.myName = newName;
/*     */     }
/*     */     
/*     */     private void setName(String name) {
/* 203 */       this.myName = name;
/*     */     }
/*     */   }
/*     */   
/*     */   public static class DynamicProfileCodeEntry extends DynamicCodeEntry {
/*     */     public DynamicProfileCodeEntry(int size, String name, String type) {
/* 209 */       super(size, name, type);
/*     */     }
/*     */ 
/*     */     
/*     */     public String getName() {
/* 214 */       return getType() + ": " + getType();
/*     */     }
/*     */ 
/*     */     
/*     */     public String getRawName() {
/* 219 */       return super.getName();
/*     */     }
/*     */   }
/*     */   
/*     */   public static class DynamicFuncCodeEntry extends DynamicCodeEntry {
/*     */     private CodeState myState;
/*     */     private final CodeMap.CodeEntry myFunctionCodeEntry;
/*     */     
/*     */     public DynamicFuncCodeEntry(int size, String name, String type, CodeState state, CodeMap.CodeEntry functionEntry) {
/* 228 */       super(size, name, type);
/* 229 */       this.myState = state;
/* 230 */       this.myFunctionCodeEntry = functionEntry;
/*     */     }
/*     */     
/*     */     public void setState(CodeState state) {
/* 234 */       this.myState = state;
/*     */     }
/*     */ 
/*     */     
/*     */     public String getName() {
/* 239 */       return getType() + ": " + getType() + this.myState.getPrefix();
/*     */     }
/*     */ 
/*     */     
/*     */     public String getRawName() {
/* 244 */       return this.myFunctionCodeEntry.getName();
/*     */     }
/*     */     
/*     */     public CodeMap.CodeEntry getFunctionCodeEntry() {
/* 248 */       return this.myFunctionCodeEntry;
/*     */     }
/*     */   }
/*     */   
/*     */   public static class DynamicCodeEntry extends CodeEntry {
/*     */     private final String myType;
/*     */     
/*     */     public DynamicCodeEntry(int size, String name, String type) {
/* 256 */       super(size, name);
/* 257 */       this.myType = type;
/*     */     }
/*     */     
/*     */     public String getType() {
/* 261 */       return this.myType;
/*     */     }
/*     */ 
/*     */ 
/*     */     
/*     */     public String getName() {
/* 267 */       return StringUtil.isEmptyOrSpaces(this.myType) ? this.myName : (this.myType + ": " + this.myType);
/*     */     }
/*     */   }
/*     */   
/*     */   public static class FunctionEntry extends DynamicCodeEntry {
/*     */     public FunctionEntry(int size, String name) {
/* 273 */       super(size, name, "");
/*     */     }
/*     */ 
/*     */     
/*     */     public String getName() {
/* 278 */       if (this.myName.length() == 0) return "<anonymous>"; 
/* 279 */       if (this.myName.charAt(0) == ' ') return "<anonymous>" + this.myName; 
/* 280 */       return super.getName();
/*     */     }
/*     */   }
/*     */ }


/* Location:              C:\Program Files\JetBrains\WebStorm 2021.1\plugins\NodeJS\lib\NodeJS.jar!\com\jetbrains\nodejs\run\profile\cpu\v8log\calculation\CodeMap.class
 * Java compiler version: 11 (55.0)
 * JD-Core Version:       1.1.3
 */