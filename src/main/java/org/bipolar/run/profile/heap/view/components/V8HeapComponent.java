/*     */ package org.bipolar.run.profile.heap.view.components;
/*     */ 
/*     */ import com.intellij.openapi.Disposable;
/*     */ import com.intellij.openapi.components.ServiceManager;
/*     */ import com.intellij.openapi.project.Project;
/*     */ import com.intellij.openapi.util.Pair;
/*     */ import com.intellij.openapi.util.text.StringUtil;
/*     */ import com.intellij.openapi.wm.ToolWindow;
/*     */ import com.intellij.ui.content.Content;
/*     */ import com.intellij.util.Consumer;
/*     */ import org.bipolar.NodeJSBundle;
/*     */ import org.bipolar.run.profile.cpu.view.V8ProfilingMainComponent;
/*     */ import org.bipolar.run.profile.heap.calculation.ByteArrayWrapper;
/*     */ import org.bipolar.run.profile.heap.data.V8HeapEntry;
/*     */ import icons.NodeJSIcons;
/*     */ import java.io.IOException;
/*     */ import java.util.Collections;
/*     */ import java.util.HashMap;
/*     */ import java.util.Map;
/*     */ import java.util.function.Supplier;
/*     */ import javax.swing.Icon;
/*     */
import org.jetbrains.annotations.Nls;
/*     */ import org.jetbrains.annotations.NotNull;
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
/*     */ public class V8HeapComponent
/*     */ {
/*  46 */   public static final Supplier<String> TOOL_WINDOW_TITLE = NodeJSBundle.messagePointer("profile.heap.tool_window.title", new Object[0]);
/*     */   
/*     */   private final Object myLock;
/*     */   
/*     */   private final Map<ByteArrayWrapper, Pair<Content, HeapViewCreatorPartner<V8HeapTreeTable>>> myOpenedTabs;
/*     */   
/*     */   public V8HeapComponent(@NotNull Project project) {
/*  53 */     this.myProject = project;
/*  54 */     this.myLock = new Object();
/*  55 */     this.myOpenedTabs = new HashMap<>();
/*  56 */     this.myMarks = new HashMap<>();
/*     */   } private final Map<ByteArrayWrapper, Map<Long, String>> myMarks; @NotNull
/*     */   private final Project myProject;
/*     */   public static V8HeapComponent getInstance(Project project) {
/*  60 */     return (V8HeapComponent)ServiceManager.getService(project, V8HeapComponent.class);
/*     */   }
/*     */   @Nls
/*     */   public String getMark(ByteArrayWrapper digest, V8HeapEntry value) {
/*  64 */     synchronized (this.myLock) {
/*  65 */       Map<Long, String> map = this.myMarks.get(digest);
/*  66 */       return (map == null) ? null : map.get(Long.valueOf(value.getId()));
/*     */     } 
/*     */   }
/*     */   
/*     */   public void markOrUnmark(ByteArrayWrapper digest, @NotNull V8HeapEntry entry, @NotNull String value) {
/*  71 */     if (entry == null) $$$reportNull$$$0(1);  if (value == null) $$$reportNull$$$0(2);  synchronized (this.myLock) {
/*  72 */       Map<Long, String> map = this.myMarks.get(digest);
/*  73 */       if (map == null) {
/*  74 */         this.myMarks.put(digest, map = new HashMap<>());
/*     */       
/*     */       }
/*  77 */       else if (map.remove(Long.valueOf(entry.getId())) != null) {
/*     */         return;
/*  79 */       }  map.put(Long.valueOf(entry.getId()), value);
/*     */     } 
/*     */   }
/*     */   
/*     */   public Map<Long, String> searchByMark(ByteArrayWrapper digest, @NotNull String substring, boolean caseSensitive) {
/*  84 */     if (substring == null) $$$reportNull$$$0(3);  synchronized (this.myLock) {
/*  85 */       Map<Long, String> marksMap = this.myMarks.get(digest);
/*  86 */       if (marksMap == null) {
/*  87 */         return Collections.emptyMap();
/*     */       }
/*  89 */       String lower = StringUtil.toLowerCase(substring);
/*  90 */       Map<Long, String> map = new HashMap<>();
/*  91 */       for (Map.Entry<Long, String> entry : marksMap.entrySet()) {
/*     */         
/*  93 */         boolean contains = caseSensitive ? ((String)entry.getValue()).contains(substring) : StringUtil.toLowerCase(entry.getValue()).contains(lower);
/*  94 */         if (contains) {
/*  95 */           map.put(entry.getKey(), entry.getValue());
/*     */         }
/*     */       } 
/*  98 */       return map.isEmpty() ? Collections.<Long, String>emptyMap() : map;
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void showMe(final ByteArrayWrapper digest, final HeapViewCreatorPartner<V8HeapTreeTable> partner, @NotNull @Nls String name, Icon contentIcon) throws IOException {
/* 108 */     if (name == null) $$$reportNull$$$0(4);  if (activateIfOpen(digest))
/*     */       return; 
/* 110 */     Disposable disposable = new Disposable()
/*     */       {
/*     */         public void dispose() {
/* 113 */           synchronized (V8HeapComponent.this.myLock) {
/* 114 */             V8HeapComponent.this.myOpenedTabs.remove(digest);
/* 115 */             boolean[] used = new boolean[1];
/* 116 */             Consumer<ByteArrayWrapper> consumer = wrapper -> used[0] = used[0] | digest.equals(wrapper);
/*     */             
/* 118 */             for (Pair<Content, HeapViewCreatorPartner<V8HeapTreeTable>> pair : V8HeapComponent.this.myOpenedTabs.values()) {
/* 119 */               ((HeapViewCreatorPartner)pair.getSecond()).reportInvolvedSnapshots(consumer);
/*     */             }
/* 121 */             if (!used[0]) {
/* 122 */               V8HeapComponent.this.myMarks.remove(digest);
/*     */             }
/*     */           } 
/* 125 */           partner.close();
/*     */         }
/*     */       };
/* 128 */     synchronized (this.myLock) {
/*     */       
/* 130 */       Content content = V8ProfilingMainComponent.showMe(this.myProject, name, TOOL_WINDOW_TITLE.get(), NodeJSIcons.OpenV8HeapSnapshot_ToolWin, 2, partner, disposable, null, contentIcon);
/* 131 */       this.myOpenedTabs.put(digest, Pair.create(content, partner));
/*     */     } 
/*     */   }
/*     */   
/*     */   public boolean activateIfOpen(ByteArrayWrapper digest) {
/* 136 */     synchronized (this.myLock) {
/* 137 */       Pair<Content, HeapViewCreatorPartner<V8HeapTreeTable>> pair = this.myOpenedTabs.get(digest);
/* 138 */       if (pair != null) {
/* 139 */         Content content = (Content)pair.getFirst();
/*     */         
/* 141 */         ToolWindow toolWindow = V8ProfilingMainComponent.getToolWindow(this.myProject, TOOL_WINDOW_TITLE.get(), NodeJSIcons.OpenV8HeapSnapshot_ToolWin);
/* 142 */         if (toolWindow.getContentManager().getIndexOfContent(content) >= 0) {
/* 143 */           toolWindow.activate(() -> toolWindow.getContentManager().setSelectedContent(content, true));
/* 144 */           return true;
/*     */         } 
/*     */       } 
/*     */     } 
/* 148 */     return false;
/*     */   }
/*     */ 
/*     */   
/*     */   public V8MainTreeNavigator getNavigator(ByteArrayWrapper digest) {
/* 153 */     synchronized (this.myLock) {
/* 154 */       Pair<Content, HeapViewCreatorPartner<V8HeapTreeTable>> pair = this.myOpenedTabs.get(digest);
/* 155 */       return (pair == null) ? null : ((HeapViewCreatorPartner)pair.getSecond()).getNavigator();
/*     */     } 
/*     */   }
/*     */ }


/* Location:              C:\Program Files\JetBrains\WebStorm 2021.1\plugins\NodeJS\lib\NodeJS.jar!\com\jetbrains\nodejs\run\profile\heap\view\components\V8HeapComponent.class
 * Java compiler version: 11 (55.0)
 * JD-Core Version:       1.1.3
 */