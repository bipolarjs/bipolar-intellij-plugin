/*     */ package org.bipolar.run.profile.cpu.v8log.diff;
/*     */ 
/*     */ import com.intellij.icons.AllIcons;
/*     */ import com.intellij.openapi.progress.ProgressIndicator;
/*     */ import com.intellij.openapi.progress.Task;
/*     */ import com.intellij.openapi.project.Project;
/*     */ import com.intellij.openapi.util.Pair;
/*     */ import com.intellij.openapi.util.Trinity;
/*     */ import com.intellij.util.BeforeAfter;
/*     */ import com.intellij.util.Consumer;
/*     */ import org.bipolar.NodeJSBundle;
/*     */ import org.bipolar.run.profile.cpu.calculation.V8ProfileLine;
/*     */ import org.bipolar.run.profile.cpu.v8log.ReadV8LogRawAction;
/*     */ import org.bipolar.run.profile.cpu.v8log.calculation.V8RawLogProcessor;
/*     */ import org.bipolar.run.profile.cpu.v8log.data.FlatTopCalls;
/*     */ import org.bipolar.run.profile.cpu.v8log.data.V8CpuLogCall;
/*     */ import org.bipolar.run.profile.cpu.v8log.reading.V8CpuViewCreatorPartner;
/*     */ import org.bipolar.run.profile.cpu.v8log.reading.V8LogCachingReader;
/*     */ import org.bipolar.run.profile.cpu.v8log.ui.FlameChartParameters;
/*     */ import org.bipolar.run.profile.cpu.view.V8ProfilingMainComponent;
/*     */ import org.bipolar.run.profile.cpu.view.ViewCreatorPartner;
/*     */ import org.bipolar.run.profile.heap.CompositeCloseable;
/*     */ import icons.NodeJSIcons;
/*     */ import java.io.File;
/*     */ import java.io.IOException;
/*     */ import java.util.ArrayDeque;
/*     */ import java.util.ArrayList;
/*     */ import java.util.HashMap;
/*     */ import java.util.Iterator;
/*     */ import java.util.List;
/*     */ import java.util.Map;
/*     */
import org.jetbrains.annotations.NotNull;
/*     */ 
/*     */ 
/*     */ public class CpuDiffCalculator
/*     */   extends Task.Backgroundable
/*     */ {
/*     */   private final V8LogCachingReader myBase;
/*     */   @NotNull
/*     */   private final File myChangedFile;
/*     */   private final boolean mySwitchLogs;
/*     */   private DiffNode myTopDownDiff;
/*     */   
/*     */   public CpuDiffCalculator(@NotNull Project project, V8LogCachingReader base, @NotNull File changedFile, boolean switchLogs) {
/*  45 */     super(project, NodeJSBundle.message("progress.title.calculating.v8.cpu.profiles.diff", new Object[0]), true);
/*  46 */     this.myBase = base;
/*  47 */     this.myChangedFile = changedFile;
/*  48 */     this.mySwitchLogs = switchLogs;
/*     */   }
/*     */   private DiffNode myBottomUpDiff; private V8LogCachingReader myOtherReader; private V8LogCachingReader myBaseForDiff;
/*     */   private V8LogCachingReader myChangedForDiff;
/*     */   private List<Pair<String, List<DiffNode>>> myFlatDiff;
/*     */   
/*     */   public void run(@NotNull ProgressIndicator indicator) {
/*  55 */     if (indicator == null) $$$reportNull$$$0(2);  try { V8RawLogProcessor processor = new V8RawLogProcessor(this.myProject, this.myChangedFile, s -> {
/*     */             indicator.setText(s);
/*     */             indicator.cancel();
/*     */           });
/*  59 */       processor.run(indicator);
/*     */       
/*  61 */       this.myOtherReader = processor.getReader();
/*  62 */       this.myBaseForDiff = this.mySwitchLogs ? this.myOtherReader.cloneReader() : this.myBase;
/*  63 */       this.myChangedForDiff = this.mySwitchLogs ? this.myBase : this.myOtherReader.cloneReader(); }
/*     */     
/*  65 */     catch (IOException e)
/*  66 */     { ReadV8LogRawAction.createNotificator(this.myProject).consume("Error in V8 logs diff processing: " + e.getMessage());
/*  67 */       indicator.cancel();
/*     */       
/*     */       return; }
/*     */     
/*  71 */     this.myTopDownDiff = treeDiff(this.myBaseForDiff.getTopDown(), this.myChangedForDiff.getTopDown());
/*  72 */     this.myBottomUpDiff = treeDiff(this.myBaseForDiff.getBottomUp(), this.myChangedForDiff.getBottomUp());
/*  73 */     correctBottomUpPercentOfParent();
/*  74 */     this.myFlatDiff = flatDiff(this.myBaseForDiff.getFlat(), this.myChangedForDiff.getFlat());
/*     */   }
/*     */   
/*     */   private List<Pair<String, List<DiffNode>>> flatDiff(FlatTopCalls baseFlat, FlatTopCalls changedFlat) {
/*  78 */     List<Pair<String, List<V8ProfileLine>>> baseFlatPresentation = baseFlat.createPresentation();
/*  79 */     List<Pair<String, List<V8ProfileLine>>> changedFlatPresentation = changedFlat.createPresentation();
/*     */     
/*  81 */     Map<String, BeforeAfter<List<V8ProfileLine>>> map = new HashMap<>();
/*     */     
/*  83 */     Map<String, List<V8ProfileLine>> baseMap = new HashMap<>();
/*  84 */     for (Pair<String, List<V8ProfileLine>> pair : baseFlatPresentation) {
/*  85 */       baseMap.put((String)pair.getFirst(), (List<V8ProfileLine>)pair.getSecond());
/*     */     }
/*  87 */     for (Pair<String, List<V8ProfileLine>> pair : changedFlatPresentation) {
/*  88 */       map.put((String)pair.getFirst(), new BeforeAfter(baseMap.get(pair.getFirst()), pair.getSecond()));
/*     */     }
/*  90 */     for (Pair<String, List<V8ProfileLine>> pair : baseFlatPresentation) {
/*  91 */       if (!map.containsKey(pair.getFirst())) {
/*  92 */         map.put((String)pair.getFirst(), new BeforeAfter(pair.getSecond(), null));
/*     */       }
/*     */     } 
/*     */     
/*  96 */     final Map<String, List<DiffNode>> middleMap = new HashMap<>();
/*  97 */     final Iterator<Map.Entry<String, BeforeAfter<List<V8ProfileLine>>>> iterator = map.entrySet().iterator();
/*     */     
/*  99 */     (new ToDiffNodesConvertor()
/*     */       {
/*     */         private ArrayList<DiffNode> myList;
/*     */         
/*     */         protected void currentNode() {
/* 104 */           Map.Entry<String, BeforeAfter<List<V8ProfileLine>>> entry = iterator.next();
/* 105 */           this.myList = new ArrayList<>();
/* 106 */           middleMap.put(entry.getKey(), this.myList);
/* 107 */           this.myBaseCurrentChildren = (List<V8ProfileLine>)((BeforeAfter)entry.getValue()).getBefore();
/* 108 */           this.myChangedCurrentChildren = (List<V8ProfileLine>)((BeforeAfter)entry.getValue()).getAfter();
/*     */         }
/*     */ 
/*     */         
/*     */         protected void onNode(DiffNode node, V8ProfileLine baseLine, V8ProfileLine changedForBase) {
/* 113 */           this.myList.add(node);
/*     */         }
/*     */ 
/*     */         
/*     */         protected boolean queueIsNotEmpty() {
/* 118 */           return iterator.hasNext();
/*     */         }
/* 120 */       }).execute();
/*     */     
/* 122 */     ArrayList<Pair<String, List<DiffNode>>> result = new ArrayList<>();
/* 123 */     String[] order = { "GC", "Shared Libraries", "JavaScript", "C++" };
/* 124 */     for (String key : order) {
/* 125 */       List<DiffNode> list = middleMap.get(key);
/* 126 */       if (list != null) {
/* 127 */         result.add(Pair.create(key, list));
/*     */       }
/*     */     } 
/* 130 */     return result;
/*     */   }
/*     */ 
/*     */   
/*     */   private void correctBottomUpPercentOfParent() {
/* 135 */     ArrayDeque<Pair<DiffNode, BeforeAfter<Integer>>> queue = new ArrayDeque<>();
/* 136 */     List<DiffNode> topLevel = this.myBottomUpDiff.getChildren();
/* 137 */     for (DiffNode line : topLevel) {
/* 138 */       queue.add(Pair.create(line, new BeforeAfter(Integer.valueOf((int)this.myBaseForDiff.getNumTicks()), Integer.valueOf((int)this.myChangedForDiff.getNumTicks()))));
/*     */     }
/* 140 */     while (!queue.isEmpty()) {
/* 141 */       Pair<DiffNode, BeforeAfter<Integer>> current = queue.removeFirst();
/* 142 */       DiffNode line = (DiffNode)current.getFirst();
/* 143 */       int beforeTicks = 0;
/* 144 */       int afterTicks = 0;
/* 145 */       if (line.getBefore() != null) {
/* 146 */         line.getBefore().setNumParentTicks(((Integer)((BeforeAfter)current.getSecond()).getBefore()).intValue());
/* 147 */         beforeTicks = line.getBefore().getTotal();
/*     */       } 
/* 149 */       if (line.getAfter() != null) {
/* 150 */         line.getAfter().setNumParentTicks(((Integer)((BeforeAfter)current.getSecond()).getAfter()).intValue());
/* 151 */         afterTicks = line.getAfter().getTotal();
/*     */       } 
/* 153 */       for (DiffNode childLine : line.getChildren()) {
/* 154 */         queue.add(Pair.create(childLine, new BeforeAfter(Integer.valueOf(beforeTicks), Integer.valueOf(afterTicks))));
/*     */       }
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   public void onSuccess() {
/* 161 */     Consumer<String> notificator = ReadV8LogRawAction.createNotificator(this.myProject);
/*     */ 
/*     */     
/* 164 */     V8CpuViewCreatorPartner otherPartner = new V8CpuViewCreatorPartner(this.myProject, this.myOtherReader.getResources(), this.myOtherReader, notificator, this.myOtherReader.getV8LogFile().getName(), null, null, new FlameChartParameters());
/* 165 */     V8ProfilingMainComponent.showMe(this.myProject, this.myOtherReader.getV8LogFile().getName(), ReadV8LogRawAction.TOOL_WINDOW_TITLE.get(), NodeJSIcons.OpenV8ProfilingLog_ToolWin, 1, (ViewCreatorPartner)otherPartner, null, null, null);
/*     */ 
/*     */     
/* 168 */     CompositeCloseable resources = new CompositeCloseable();
/*     */     
/* 170 */     V8CpuDiffViewCreatorPartner partner = new V8CpuDiffViewCreatorPartner(this.myProject, resources, this.myBaseForDiff, this.myChangedForDiff, notificator, this.myTopDownDiff, this.myBottomUpDiff, this.myFlatDiff);
/*     */ 
/*     */     
/* 173 */     String name = this.myBaseForDiff.getV8LogFile().getName() + "->" + this.myBaseForDiff.getV8LogFile().getName();
/*     */     
/* 175 */     V8ProfilingMainComponent.showMe(this.myProject, name, ReadV8LogRawAction.TOOL_WINDOW_TITLE.get(), NodeJSIcons.OpenV8ProfilingLog_ToolWin, 1, partner, null, null, AllIcons.Actions.Diff);
/*     */   }
/*     */   
/*     */   private DiffNode treeDiff(V8ProfileLine base, V8ProfileLine changed) {
/* 179 */     DiffNode root = new DiffNode(V8CpuLogCall.dumb("", -1L), new DiffNode.Ticks(0, 0), new DiffNode.Ticks(0, 0));
/* 180 */     final ArrayDeque<Trinity<DiffNode, V8ProfileLine, V8ProfileLine>> queue = new ArrayDeque<>();
/* 181 */     queue.add(new Trinity(root, base, changed));
/*     */     
/* 183 */     (new ToDiffNodesConvertor()
/*     */       {
/*     */         private DiffNode myTarget;
/*     */         
/*     */         protected void currentNode() {
/* 188 */           Trinity<DiffNode, V8ProfileLine, V8ProfileLine> trinity = queue.removeFirst();
/* 189 */           this.myTarget = (DiffNode)trinity.getFirst();
/* 190 */           this.myBaseCurrentChildren = (trinity.getSecond() == null) ? null : ((V8ProfileLine)trinity.getSecond()).getChildren();
/* 191 */           this.myChangedCurrentChildren = (trinity.getThird() == null) ? null : ((V8ProfileLine)trinity.getThird()).getChildren();
/*     */         }
/*     */ 
/*     */         
/*     */         protected void onNode(DiffNode node, V8ProfileLine baseLine, V8ProfileLine changedForBase) {
/* 196 */           this.myTarget.addChild(node);
/* 197 */           if (baseLine != null && changedForBase != null) {
/* 198 */             queue.add(new Trinity(node, baseLine, changedForBase));
/*     */           }
/*     */         }
/*     */ 
/*     */         
/*     */         protected boolean queueIsNotEmpty() {
/* 204 */           return !queue.isEmpty();
/*     */         }
/* 206 */       }).execute();
/* 207 */     return root;
/*     */   }
/*     */   
/*     */   private static abstract class ToDiffNodesConvertor {
/*     */     protected List<V8ProfileLine> myBaseCurrentChildren;
/*     */     protected List<V8ProfileLine> myChangedCurrentChildren;
/*     */     
/*     */     public void execute() {
/* 215 */       while (queueIsNotEmpty()) {
/* 216 */         currentNode();
/* 217 */         if (this.myChangedCurrentChildren == null) {
/* 218 */           for (V8ProfileLine baseLine : this.myBaseCurrentChildren)
/* 219 */             onNode(new DiffNode(baseLine.getCall(), new DiffNode.Ticks(baseLine.getTotalTicks(), baseLine.getSelfTicks()), null), baseLine, null); 
/*     */           continue;
/*     */         } 
/* 222 */         if (this.myBaseCurrentChildren == null) {
/* 223 */           for (V8ProfileLine changedLine : this.myChangedCurrentChildren) {
/* 224 */             onNode(new DiffNode(changedLine.getCall(), null, new DiffNode.Ticks(changedLine.getTotalTicks(), changedLine.getSelfTicks())), null, changedLine);
/*     */           }
/*     */           continue;
/*     */         } 
/* 228 */         Map<String, V8ProfileLine> map = new HashMap<>();
/* 229 */         for (V8ProfileLine changedLine : this.myChangedCurrentChildren) {
/* 230 */           map.put(changedLine.getPresentation(true), changedLine);
/*     */         }
/* 232 */         for (V8ProfileLine baseLine : this.myBaseCurrentChildren) {
/* 233 */           V8ProfileLine changedForBase = map.remove(baseLine.getPresentation(true));
/* 234 */           if (changedForBase != null) {
/*     */             
/* 236 */             DiffNode newTarget = new DiffNode(baseLine.getCall(), new DiffNode.Ticks(baseLine.getTotalTicks(), baseLine.getSelfTicks()), new DiffNode.Ticks(changedForBase.getTotalTicks(), changedForBase.getSelfTicks()));
/* 237 */             onNode(newTarget, baseLine, changedForBase); continue;
/*     */           } 
/* 239 */           onNode(new DiffNode(baseLine.getCall(), new DiffNode.Ticks(baseLine.getTotalTicks(), baseLine.getSelfTicks()), null), baseLine, null);
/*     */         } 
/*     */ 
/*     */         
/* 243 */         for (V8ProfileLine changedLine : map.values())
/* 244 */           onNode(new DiffNode(changedLine.getCall(), null, new DiffNode.Ticks(changedLine.getTotalTicks(), changedLine.getSelfTicks())), null, changedLine); 
/*     */       } 
/*     */     }
/*     */     
/*     */     protected abstract void currentNode();
/*     */     
/*     */     protected abstract void onNode(DiffNode param1DiffNode, V8ProfileLine param1V8ProfileLine1, V8ProfileLine param1V8ProfileLine2);
/*     */     
/*     */     protected abstract boolean queueIsNotEmpty();
/*     */   }
/*     */ }


/* Location:              C:\Program Files\JetBrains\WebStorm 2021.1\plugins\NodeJS\lib\NodeJS.jar!\com\jetbrains\nodejs\run\profile\cpu\v8log\diff\CpuDiffCalculator.class
 * Java compiler version: 11 (55.0)
 * JD-Core Version:       1.1.3
 */