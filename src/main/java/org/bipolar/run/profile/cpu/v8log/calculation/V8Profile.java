/*     */ package org.bipolar.run.profile.cpu.v8log.calculation;
/*     */ 
/*     */ import com.intellij.openapi.progress.ProgressManager;
/*     */ import com.intellij.openapi.util.Pair;
/*     */ import com.intellij.util.ThrowableConvertor;
/*     */ import com.intellij.util.containers.ContainerUtil;
/*     */ import org.bipolar.NodeJSBundle;
/*     */ import org.bipolar.run.profile.V8Utils;
/*     */ import org.bipolar.run.profile.cpu.calculation.V8ProfileLine;
/*     */ import org.bipolar.run.profile.cpu.v8log.data.CodeState;
/*     */ import org.bipolar.run.profile.cpu.v8log.data.FlatTopCalls;
/*     */ import java.io.IOException;
/*     */ import java.math.BigInteger;
/*     */ import java.util.ArrayDeque;
/*     */ import java.util.ArrayList;
/*     */ import java.util.Collection;
/*     */ import java.util.Collections;
/*     */ import java.util.Comparator;
/*     */ import java.util.HashMap;
/*     */ import java.util.Iterator;
/*     */ import java.util.List;
/*     */ import java.util.Map;
/*     */
import org.jetbrains.annotations.NotNull;
/*     */ 
/*     */ 
/*     */ 
/*     */ public class V8Profile
/*     */ {
/*     */   private final CodeMap myCodeMap;
/*     */   private final CallTree myTopDownTree;
/*     */   private final CallTree myBottomUpTree;
/*     */   private final List<CallTree.CallTreeNode> myFlat;
/*     */   @NotNull
/*     */   private final V8ProfileCallback myCallback;
/*     */   private int myNumTicks;
/*     */   
/*     */   public V8Profile(@NotNull V8ProfileCallback callback) {
/*  38 */     this.myCallback = callback;
/*  39 */     this.myCodeMap = new CodeMap();
/*  40 */     this.myTopDownTree = new CallTree();
/*  41 */     this.myBottomUpTree = new CallTree();
/*  42 */     this.myFlat = new ArrayList<>();
/*  43 */     this.myNumTicks = 0;
/*     */   }
/*     */   private int myUnaccountedTicks; private int myIdleTicks; private int myGcTicks; private V8ProfileLine myBottomUpRoot; private V8ProfileLine myTopDownRoot; private FlatTopCalls myFlatTopCallsRoot; private int myMaxStackSize;
/*     */   public int getUnaccountedTicks() {
/*  47 */     return this.myUnaccountedTicks;
/*     */   }
/*     */   
/*     */   public int getIdleTicks() {
/*  51 */     return this.myIdleTicks;
/*     */   }
/*     */   
/*     */   public int getGcTicks() {
/*  55 */     return this.myGcTicks;
/*     */   }
/*     */   
/*     */   public int getNumTicks() {
/*  59 */     return this.myNumTicks;
/*     */   }
/*     */   
/*     */   public int getMaxStackSize() {
/*  63 */     return this.myMaxStackSize;
/*     */   }
/*     */   
/*     */   private void computeTotalWeights() {
/*  67 */     this.myTopDownTree.computeTotalWeight();
/*  68 */     this.myBottomUpTree.computeTotalWeight();
/*  69 */     computeFlatView();
/*     */   }
/*     */   
/*     */   private enum State {
/*  73 */     enter, exit, root;
/*     */   }
/*     */   
/*     */   private static class Data { private final V8Profile.State myState;
/*     */     private final CallTree.CallTreeNode myNode;
/*     */     
/*     */     Data(V8Profile.State state, CallTree.CallTreeNode node) {
/*  80 */       this.myState = state;
/*  81 */       this.myNode = node;
/*     */     } }
/*     */   
/*     */   private void computeFlatView() {
/*  85 */     Map<Long, Integer> currentStack = new HashMap<>();
/*  86 */     CallTree.CallTreeNode counters = new CallTree.CallTreeNode(Long.valueOf(-1L));
/*  87 */     ArrayDeque<Data> queue = new ArrayDeque<>();
/*  88 */     queue.add(new Data(State.root, this.myTopDownTree.getRoot()));
/*  89 */     while (!queue.isEmpty()) {
/*  90 */       Data data = queue.removeFirst();
/*  91 */       Long code = data.myNode.getName();
/*     */       
/*  93 */       if (State.enter.equals(data.myState)) {
/*  94 */         CallTree.CallTreeNode child = counters.findOrAddChild(code);
/*  95 */         child.getSelfWeight().add(data.myNode.getSelfWeight().getCnt());
/*  96 */         if (!currentStack.containsKey(code)) {
/*  97 */           child.getTotalWeight().add(data.myNode.getTotalWeight().getCnt());
/*     */         }
/*  99 */         Integer integer = currentStack.get(code);
/* 100 */         if (integer != null) { currentStack.put(code, Integer.valueOf(integer.intValue() + 1)); }
/* 101 */         else { currentStack.put(code, Integer.valueOf(1)); }
/*     */         
/* 103 */         queue.addFirst(new Data(State.exit, data.myNode));
/*     */         
/* 105 */         Collection<CallTree.CallTreeNode> collection = data.myNode.getChildren().values();
/* 106 */         for (CallTree.CallTreeNode value : collection)
/* 107 */           queue.addFirst(new Data(State.enter, value));  continue;
/*     */       } 
/* 109 */       if (State.exit.equals(data.myState)) {
/* 110 */         Integer integer = currentStack.get(code);
/* 111 */         if (integer != null && integer.intValue() > 0) {
/* 112 */           if (integer.intValue() == 1) {
/* 113 */             currentStack.remove(code); continue;
/*     */           } 
/* 115 */           currentStack.put(code, Integer.valueOf(integer.intValue() - 1));
/*     */         } 
/*     */         
/*     */         continue;
/*     */       } 
/* 120 */       Collection<CallTree.CallTreeNode> values = data.myNode.getChildren().values();
/* 121 */       for (CallTree.CallTreeNode value : values) {
/* 122 */         queue.add(new Data(State.enter, value));
/*     */       }
/*     */     } 
/*     */     
/* 126 */     List<CallTree.CallTreeNode> filtered = ContainerUtil.filter(counters.getChildren().values(), node -> (node.getSelfWeight().getCnt() > 0));
/*     */     
/* 128 */     this.myFlat.addAll(filtered);
/*     */     
/* 130 */     this.myFlat.sort(new FlatNodesComparator());
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void postProcess(int gcTicks, int unknownTicks, int idleTicks, Map<String, V8TickProcessor.CodeType> codeTypeMap, ThrowableConvertor<Long, String, IOException> convertor, int maxStackSize) throws IOException {
/* 137 */     this.myMaxStackSize = maxStackSize;
/* 138 */     ProgressManager.progress(NodeJSBundle.message("progress.text.calculating.total.weights.calls", new Object[0]));
/* 139 */     computeTotalWeights();
/* 140 */     CallTreeNodeComparator comparator = new CallTreeNodeComparator();
/* 141 */     this.myBottomUpRoot = createStructuredStatisticsByProfile(this.myBottomUpTree.getRoot(), convertor, comparator);
/* 142 */     correctBottomUpPercentOfParent();
/* 143 */     this.myTopDownRoot = createStructuredStatisticsByProfile(this.myTopDownTree.getRoot(), convertor, comparator);
/* 144 */     ProgressManager.progress(NodeJSBundle.message("progress.text.creating.top.calls.data", new Object[0]));
/* 145 */     this.myFlatTopCallsRoot = createFlat(gcTicks, unknownTicks, codeTypeMap, convertor);
/*     */     
/* 147 */     this.myGcTicks = gcTicks;
/* 148 */     this.myUnaccountedTicks = unknownTicks;
/* 149 */     this.myIdleTicks = idleTicks;
/*     */   }
/*     */   
/*     */   private void correctBottomUpPercentOfParent() {
/* 153 */     ArrayDeque<Pair<V8ProfileLine, Integer>> queue = new ArrayDeque<>();
/* 154 */     List<V8ProfileLine> topLevel = this.myBottomUpRoot.getChildren();
/* 155 */     for (V8ProfileLine line : topLevel) {
/* 156 */       queue.add(Pair.create(line, Integer.valueOf(this.myNumTicks)));
/*     */     }
/* 158 */     while (!queue.isEmpty()) {
/* 159 */       Pair<V8ProfileLine, Integer> current = queue.removeFirst();
/* 160 */       V8ProfileLine line = (V8ProfileLine)current.getFirst();
/* 161 */       line.setTotalTensPercent(V8Utils.tensPercent(line.getTotalTicks(), ((Integer)current.getSecond()).intValue()));
/* 162 */       for (V8ProfileLine childLine : line.getChildren()) {
/* 163 */         queue.add(Pair.create(childLine, Integer.valueOf(line.getTotalTicks())));
/*     */       }
/*     */     } 
/*     */   }
/*     */   
/*     */   public CallTree getTopDownTree() {
/* 169 */     return this.myTopDownTree;
/*     */   }
/*     */   
/*     */   public CallTree getBottomUpTree() {
/* 173 */     return this.myBottomUpTree;
/*     */   }
/*     */   
/*     */   public V8ProfileLine getBottomUpRoot() {
/* 177 */     return this.myBottomUpRoot;
/*     */   }
/*     */   
/*     */   public V8ProfileLine getTopDownRoot() {
/* 181 */     return this.myTopDownRoot;
/*     */   }
/*     */   
/*     */   public FlatTopCalls getFlatTopCallsRoot() {
/* 185 */     return this.myFlatTopCallsRoot;
/*     */   }
/*     */   
/*     */   public CodeMap.CodeEntry addLibrary(String name, BigInteger start, BigInteger end) {
/* 189 */     CodeMap.CodeEntry entry = new CodeMap.CodeEntry(end.subtract(start).intValue(), name);
/* 190 */     this.myCodeMap.addLibrary(start, entry);
/* 191 */     return entry;
/*     */   }
/*     */   
/*     */   public CodeMap.CodeEntry addStaticCode(String name, BigInteger start, BigInteger end) {
/* 195 */     CodeMap.CodeEntry entry = new CodeMap.CodeEntry(end.subtract(start).intValue(), name);
/* 196 */     this.myCodeMap.addStaticCode(start, entry);
/* 197 */     return entry;
/*     */   }
/*     */   
/*     */   public CodeMap.DynamicCodeEntry addCode(String type, String name, BigInteger start, int size) {
/* 201 */     CodeMap.DynamicCodeEntry entry = new CodeMap.DynamicCodeEntry(size, name, type);
/* 202 */     this.myCodeMap.addCode(start, entry);
/* 203 */     return entry;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public CodeMap.DynamicFuncCodeEntry addFuncCode(String type, String name, BigInteger start, int size, BigInteger funcAddress, CodeState state) {
/* 212 */     CodeMap.DynamicCodeEntry func = this.myCodeMap.findDynamicByStart(funcAddress);
/* 213 */     if (func == null) {
/* 214 */       func = new CodeMap.FunctionEntry(0, name);
/* 215 */       this.myCodeMap.addCode(funcAddress, func);
/* 216 */     } else if (!func.getName().equals(name)) {
/* 217 */       this.myCodeMap.editName(func, name);
/*     */     } 
/* 219 */     CodeMap.DynamicCodeEntry entry = this.myCodeMap.findDynamicByStart(start);
/* 220 */     if (entry instanceof CodeMap.DynamicFuncCodeEntry) {
/* 221 */       CodeMap.DynamicFuncCodeEntry funcCodeEntry = (CodeMap.DynamicFuncCodeEntry)entry;
/* 222 */       if (funcCodeEntry.getSize() == size && funcCodeEntry.getFunctionCodeEntry().getName().equals(func.getName())) {
/* 223 */         funcCodeEntry.setState(state);
/*     */       }
/*     */     } else {
/* 226 */       entry = new CodeMap.DynamicFuncCodeEntry(size, name, type, state, func);
/* 227 */       this.myCodeMap.addCode(start, entry);
/*     */     } 
/* 229 */     return (CodeMap.DynamicFuncCodeEntry)entry;
/*     */   }
/*     */   
/*     */   public void moveCode(BigInteger from, BigInteger to) {
/* 233 */     if (!this.myCodeMap.moveCode(from, to)) {
/* 234 */       this.myCallback.onUnknownMove(from);
/*     */     }
/*     */   }
/*     */   
/*     */   public void deleteCode(BigInteger from) {
/* 239 */     if (!this.myCodeMap.deleteCode(from)) {
/* 240 */       this.myCallback.onUnknownDelete(from);
/*     */     }
/*     */   }
/*     */   
/*     */   public CodeMap.CodeEntry findEntry(BigInteger addr) {
/* 245 */     return this.myCodeMap.findEntry(addr);
/*     */   }
/*     */   
/*     */   public void moveFunc(BigInteger from, BigInteger to) {
/* 249 */     if (this.myCodeMap.findDynamicByStart(from) != null) {
/* 250 */       this.myCodeMap.moveCode(from, to);
/*     */     } else {
/* 252 */       this.myCallback.onUnknownMove(from);
/*     */     } 
/*     */   }
/*     */   
/*     */   public void recordTick(List<Long> symbolicStack) {
/* 257 */     if (symbolicStack.isEmpty()) {
/* 258 */       this.myNumTicks++;
/*     */       return;
/*     */     } 
/* 261 */     Iterator<Long> iterator = symbolicStack.iterator();
/* 262 */     while (iterator.hasNext()) {
/* 263 */       Long id = iterator.next();
/* 264 */       if (id.longValue() == 0L || id.longValue() == 1L) {
/* 265 */         iterator.remove();
/*     */       }
/*     */     } 
/* 268 */     this.myBottomUpTree.addPath(symbolicStack);
/* 269 */     Collections.reverse(symbolicStack);
/* 270 */     this.myTopDownTree.addPath(symbolicStack);
/* 271 */     this.myNumTicks++;
/*     */   }
/*     */   
/*     */   public List<Long> resolveAndFilterFuncs(List<BigInteger> stack) {
/* 275 */     List<Long> result = new ArrayList<>();
/* 276 */     for (int i = 0; i < stack.size(); i++) {
/* 277 */       BigInteger stackAddr = stack.get(i);
/* 278 */       CodeMap.CodeEntry entry = this.myCodeMap.findEntry(stackAddr);
/* 279 */       if (entry != null) {
/* 280 */         String name = entry.getName();
/* 281 */         Long code = this.myCodeMap.getStringCode(name);
/* 282 */         if (this.myCallback.processFunction(name)) result.add(code); 
/* 283 */       } else if (i == stack.size() - 1 && stack.size() > 40) {
/* 284 */         result.add(Long.valueOf(2L));
/* 285 */         this.myCallback.processFunction(this.myCodeMap.getStringByCode(2L));
/*     */       } else {
/* 287 */         result.add(Long.valueOf(1L));
/* 288 */         this.myCallback.onUnknownTick(stackAddr, i);
/*     */       } 
/*     */     } 
/* 291 */     return result;
/*     */   }
/*     */   
/*     */   public CodeMap getCodeMap() {
/* 295 */     return this.myCodeMap;
/*     */   }
/*     */   
/*     */   public enum Operation {
/* 299 */     move(0), delete(1), tick(2);
/*     */     
/*     */     private final int myCode;
/*     */     
/*     */     Operation(int code) {
/* 304 */       this.myCode = code;
/*     */     }
/*     */     
/*     */     public int getCode() {
/* 308 */       return this.myCode;
/*     */     }
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   @NotNull
/*     */   private FlatTopCalls createFlat(int gcTicks, int unknownTicks, Map<String, V8TickProcessor.CodeType> codeTypeMap, ThrowableConvertor<Long, String, IOException> convertor) throws IOException {
/* 317 */     CallTree.CallTreeNode fictive = new CallTree.CallTreeNode(Long.valueOf(-1L));
/* 318 */     for (CallTree.CallTreeNode node : this.myFlat) {
/* 319 */       fictive.getChildren().put(node.getName(), node);
/*     */     }
/* 321 */     V8ProfileLine flat = createStructuredStatisticsByProfile(fictive, convertor, new FlatNodesComparator());
/* 322 */     FlatTopCalls flatTopCalls = new FlatTopCalls();
/* 323 */     for (V8ProfileLine line : flat.getChildren()) {
/* 324 */       String code = (String)convertor.convert(Long.valueOf(line.getCall().getStringId()));
/* 325 */       V8TickProcessor.CodeType codeType = (code == null) ? null : codeTypeMap.get(code);
/* 326 */       if (V8TickProcessor.CodeType.SHARED_LIB.equals(codeType)) {
/* 327 */         flatTopCalls.getSharedLibraries().add(line); continue;
/* 328 */       }  if (V8TickProcessor.CodeType.CPP.equals(codeType)) {
/* 329 */         flatTopCalls.getCpp().add(line); continue;
/*     */       } 
/* 331 */       flatTopCalls.getJavaScript().add(line);
/*     */     } 
/*     */     
/* 334 */     V8ProfileLine gcLine = V8ProfileLine.createLine("", null, -1L);
/*     */     
/* 336 */     gcLine.setTotalTicks(gcTicks);
/* 337 */     gcLine.setTotalTensPercent((int)Math.round(gcTicks * 1000.0D / this.myNumTicks));
/* 338 */     flatTopCalls.getGc().add(gcLine);
/*     */     
/* 340 */     V8ProfileLine unknownLine = V8ProfileLine.createLine("", null, -1L);
/*     */     
/* 342 */     unknownLine.setTotalTicks(unknownTicks);
/* 343 */     unknownLine.setTotalTensPercent((int)Math.round(unknownTicks * 1000.0D / this.myNumTicks));
/* 344 */     flatTopCalls.getUnknown().add(unknownLine);
/* 345 */     if (flatTopCalls == null) $$$reportNull$$$0(1);  return flatTopCalls;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public V8ProfileLine createStructuredStatisticsByProfile(@NotNull CallTree.CallTreeNode root, ThrowableConvertor<Long, String, IOException> stringConvertor, Comparator<CallTree.CallTreeNode> comparator) throws IOException {
/* 352 */     if (root == null) $$$reportNull$$$0(2);  V8ProfileLine rootLine = V8ProfileLine.createLine("", null, -1L);
/* 353 */     ArrayDeque<Pair<CallTree.CallTreeNode, V8ProfileLine>> queue = new ArrayDeque<>();
/* 354 */     queue.add(Pair.create(root, rootLine));
/* 355 */     while (!queue.isEmpty()) {
/* 356 */       Pair<CallTree.CallTreeNode, V8ProfileLine> pair = queue.removeFirst();
/* 357 */       CallTree.CallTreeNode node = (CallTree.CallTreeNode)pair.getFirst();
/* 358 */       V8ProfileLine line = (V8ProfileLine)pair.getSecond();
/* 359 */       Map<Long, CallTree.CallTreeNode> children = node.getChildren();
/* 360 */       List<CallTree.CallTreeNode> list = new ArrayList<>(children.values());
/* 361 */       list.sort(comparator);
/* 362 */       for (CallTree.CallTreeNode treeNode : list) {
/* 363 */         Long stringId = treeNode.getName();
/* 364 */         String code = (String)stringConvertor.convert(stringId);
/* 365 */         V8ProfileLine profileLine = V8ProfileLine.createLine(code, line, stringId.longValue());
/* 366 */         int total = treeNode.getTotalWeight().getCnt();
/* 367 */         int self = treeNode.getSelfWeight().getCnt();
/* 368 */         profileLine.setTotalTicks(total);
/* 369 */         profileLine.setSelfTicks(self);
/* 370 */         profileLine.setTotalTensPercent(V8Utils.tensPercent(total, this.myNumTicks));
/* 371 */         profileLine.setSelfTensPercent(V8Utils.tensPercent(self, this.myNumTicks));
/* 372 */         queue.add(Pair.create(treeNode, profileLine));
/*     */       } 
/*     */     } 
/* 375 */     return rootLine;
/*     */   }
/*     */   
/*     */   public static class CallTreeNodeComparator
/*     */     implements Comparator<CallTree.CallTreeNode> {
/*     */     public int compare(CallTree.CallTreeNode o1, CallTree.CallTreeNode o2) {
/* 381 */       int total = Integer.compare(o2.getTotalWeight().getCnt(), o1.getTotalWeight().getCnt());
/* 382 */       if (total != 0) return total; 
/* 383 */       int self = Integer.compare(o2.getSelfWeight().getCnt(), o1.getSelfWeight().getCnt());
/* 384 */       if (self != 0) return self; 
/* 385 */       return o2.getName().compareTo(o1.getName());
/*     */     }
/*     */   }
/*     */   
/*     */   private static class FlatNodesComparator
/*     */     implements Comparator<CallTree.CallTreeNode> {
/*     */     public int compare(CallTree.CallTreeNode o1, CallTree.CallTreeNode o2) {
/* 392 */       int selfCompare = Integer.compare(o2.getSelfWeight().getCnt(), o1.getSelfWeight().getCnt());
/* 393 */       if (selfCompare != 0) return selfCompare; 
/* 394 */       int totalCompare = Integer.compare(o2.getTotalWeight().getCnt(), o1.getTotalWeight().getCnt());
/* 395 */       if (totalCompare != 0) return totalCompare; 
/* 396 */       return o2.getName().compareTo(o1.getName());
/*     */     }
/*     */   }
/*     */ }


/* Location:              C:\Program Files\JetBrains\WebStorm 2021.1\plugins\NodeJS\lib\NodeJS.jar!\com\jetbrains\nodejs\run\profile\cpu\v8log\calculation\V8Profile.class
 * Java compiler version: 11 (55.0)
 * JD-Core Version:       1.1.3
 */