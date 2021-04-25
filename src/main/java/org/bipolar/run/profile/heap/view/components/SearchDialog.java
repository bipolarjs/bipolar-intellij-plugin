/*     */ package org.bipolar.run.profile.heap.view.components;
/*     */ import com.intellij.ide.util.PropertiesComponent;
/*     */ import com.intellij.notification.Notification;
/*     */ import com.intellij.openapi.actionSystem.DefaultActionGroup;
/*     */ import com.intellij.openapi.application.ApplicationManager;
/*     */ import com.intellij.openapi.diagnostic.Logger;
/*     */ import com.intellij.openapi.progress.ProcessCanceledException;
/*     */ import com.intellij.openapi.progress.ProgressIndicator;
/*     */ import com.intellij.openapi.progress.Task;
/*     */ import com.intellij.openapi.project.Project;
/*     */ import com.intellij.openapi.ui.DialogBuilder;
/*     */ import com.intellij.openapi.ui.MessageType;
/*     */ import com.intellij.openapi.util.Pair;
/*     */ import com.intellij.openapi.util.text.StringUtil;
/*     */ import com.intellij.ui.SearchTextField;
/*     */ import com.intellij.ui.components.JBCheckBox;
/*     */ import com.intellij.util.containers.MultiMap;
/*     */ import com.intellij.util.ui.DialogUtil;
/*     */ import com.intellij.util.ui.FormBuilder;
/*     */ import org.bipolar.NodeJSBundle;
/*     */ import org.bipolar.run.profile.heap.V8CachingReader;
/*     */ import org.bipolar.run.profile.heap.data.V8HeapEdge;
/*     */ import org.bipolar.run.profile.heap.data.V8HeapEntry;
/*     */ import org.bipolar.run.profile.heap.io.RawSerializer;
/*     */ import org.bipolar.run.profile.heap.io.SequentialRawReader;
/*     */ import org.bipolar.run.profile.heap.view.models.V8HeapContainmentTreeTableModel;
/*     */ import org.bipolar.run.profile.settings.NodeProfilingSettings;
/*     */ import org.bipolar.util.CloseableProcessor;
/*     */ import org.bipolar.util.CloseableThrowableConsumer;
/*     */ import java.awt.Dimension;
/*     */ import java.awt.event.ActionEvent;
/*     */ import java.awt.event.ActionListener;
/*     */ import java.io.IOException;
/*     */ import java.util.ArrayList;
/*     */ import java.util.Collection;
/*     */ import java.util.Collections;
/*     */ import java.util.HashMap;
/*     */ import java.util.HashSet;
/*     */ import java.util.Iterator;
/*     */ import java.util.List;
/*     */ import java.util.Map;
/*     */ import java.util.Set;
/*     */ import javax.swing.AbstractButton;
/*     */ import javax.swing.JComponent;
/*     */
import org.jetbrains.annotations.Nls;
/*     */ import org.jetbrains.annotations.NotNull;
/*     */ 
/*     */ public class SearchDialog {
/*  49 */   private static final Logger LOG = Logger.getInstance(SearchDialog.class); private static final int SIZE_LIMIT = 1000;
/*     */   private static final String ourBasePropName = "Node.js.profiling.heap.search.option.";
/*     */   
/*     */   private static class Names { @Nls
/*  53 */     private final String LINK_NAMES = NodeJSBundle.message("profile.search.option.link_names.text", new Object[0]); @Nls
/*  54 */     private final String CLASS_NAMES = NodeJSBundle.message("profile.search.option.class_names.text", new Object[0]); @Nls
/*  55 */     private final String TEXT_STRINGS = NodeJSBundle.message("profile.search.option.text_strings.text", new Object[0]); @Nls
/*  56 */     private final String SNAPSHOT_OBJECT_IDS = NodeJSBundle.message("profile.search.option.snapshot_object_ids.text", new Object[0]); @Nls
/*  57 */     private final String MARKS = NodeJSBundle.message("profile.search.option.marks.text", new Object[0]); @Nls
/*  58 */     private final String[] OPTIONS = new String[] { this.LINK_NAMES, this.CLASS_NAMES, this.TEXT_STRINGS, this.SNAPSHOT_OBJECT_IDS, this.MARKS }; }
/*     */   
/*  60 */   private final Names myNames = new Names(); @Nls
/*  61 */   private final String LINK_NAMES = this.myNames.LINK_NAMES; @Nls
/*  62 */   private final String CLASS_NAMES = this.myNames.CLASS_NAMES; @Nls
/*  63 */   private final String TEXT_STRINGS = this.myNames.TEXT_STRINGS; @Nls
/*  64 */   private final String SNAPSHOT_OBJECT_IDS = this.myNames.SNAPSHOT_OBJECT_IDS; @Nls
/*  65 */   private final String MARKS = this.myNames.MARKS;
/*     */   
/*     */   private final Project myProject;
/*     */   
/*     */   private final V8CachingReader myReader;
/*     */   
/*     */   private final V8MainTableWithRetainers<V8HeapContainmentTreeTableModel> myTableWithRetainers;
/*     */   private final Map<String, JBCheckBox> myOptions;
/*     */   private JBCheckBox mySearchEverywhere;
/*     */   private SearchTextField myText;
/*     */   private JBCheckBox myCaseSensitive;
/*     */   
/*     */   public SearchDialog(Project project, V8CachingReader reader, V8MainTableWithRetainers<V8HeapContainmentTreeTableModel> tableWithRetainers) {
/*  78 */     this.myProject = project;
/*  79 */     this.myReader = reader;
/*  80 */     this.myTableWithRetainers = tableWithRetainers;
/*  81 */     this.myOptions = new HashMap<>();
/*  82 */     for (String option : getOptions()) {
/*  83 */       JBCheckBox checkBox = new JBCheckBox(option);
/*  84 */       checkBox.setSelected(true);
/*  85 */       DialogUtil.registerMnemonic((AbstractButton)checkBox, '&');
/*  86 */       this.myOptions.put(option, checkBox);
/*     */     } 
/*     */   }
/*     */   
/*     */   public void search() {
/*  91 */     if (showDialog()) {
/*  92 */       String text = this.myText.getText().trim();
/*  93 */       if (text.isEmpty())
/*  94 */         return;  this.myText.addCurrentTextToHistory();
/*  95 */       storeOptions();
/*  96 */       ProgressManager.getInstance().run((Task)new Searcher(this.myProject, text, this.myReader, this.myOptions, this.myCaseSensitive.isSelected(), this.mySearchEverywhere
/*  97 */             .isSelected(), this.myTableWithRetainers));
/*     */     } 
/*     */   }
/*     */   
/*     */   private void storeOptions() {
/* 102 */     PropertiesComponent propertiesComponent = PropertiesComponent.getInstance(this.myProject);
/* 103 */     for (Map.Entry<String, JBCheckBox> entry : this.myOptions.entrySet()) {
/* 104 */       propertiesComponent.setValue("Node.js.profiling.heap.search.option." + (String)entry.getKey(), Boolean.toString(((JBCheckBox)entry.getValue()).isSelected()), "true");
/*     */     }
/* 106 */     propertiesComponent.setValue("Node.js.profiling.heap.search.option.everywhere", Boolean.toString(this.mySearchEverywhere.isSelected()), "false");
/* 107 */     propertiesComponent.setValue("Node.js.profiling.heap.search.option.caseSensitive", Boolean.toString(this.myCaseSensitive.isSelected()), "false");
/*     */   }
/*     */   
/*     */   private void readStoredOptions() {
/* 111 */     PropertiesComponent propertiesComponent = PropertiesComponent.getInstance(this.myProject);
/* 112 */     for (Map.Entry<String, JBCheckBox> entry : this.myOptions.entrySet()) {
/* 113 */       boolean value = propertiesComponent.getBoolean("Node.js.profiling.heap.search.option." + (String)entry.getKey(), true);
/* 114 */       ((JBCheckBox)entry.getValue()).setSelected(value);
/*     */     } 
/* 116 */     this.mySearchEverywhere.setSelected(propertiesComponent.getBoolean("Node.js.profiling.heap.search.option.everywhere"));
/* 117 */     this.myCaseSensitive.setSelected(propertiesComponent.getBoolean("Node.js.profiling.heap.search.option.caseSensitive"));
/*     */   }
/*     */   
/*     */   private boolean showDialog() {
/* 121 */     this.myText = new SearchTextField("Node.js.profiling.heap.searh.history")
/*     */       {
/*     */         public Dimension getMinimumSize() {
/* 124 */           Dimension size = super.getMinimumSize();
/* 125 */           return new Dimension(150, size.height);
/*     */         }
/*     */       };
/* 128 */     this.mySearchEverywhere = new JBCheckBox(NodeJSBundle.message("checkbox.everywhere", new Object[0]));
/* 129 */     DialogUtil.registerMnemonic((AbstractButton)this.mySearchEverywhere, '&');
/* 130 */     this.myCaseSensitive = new JBCheckBox(NodeJSBundle.message("checkbox.case.sensitive", new Object[0]));
/* 131 */     DialogUtil.registerMnemonic((AbstractButton)this.myCaseSensitive, '&');
/* 132 */     FormBuilder formBuilder = (new FormBuilder()).addLabeledComponent(NodeJSBundle.message("label.search", new Object[0]), (JComponent)this.myText);
/* 133 */     formBuilder.addComponent((JComponent)this.myCaseSensitive);
/* 134 */     formBuilder.addVerticalGap(5);
/* 135 */     formBuilder.addComponent((JComponent)new JBLabel(NodeJSBundle.message("label.scope", new Object[0])));
/* 136 */     formBuilder.addComponent((JComponent)this.mySearchEverywhere);
/* 137 */     formBuilder.addVerticalGap(5);
/* 138 */     for (String option : getOptions()) {
/* 139 */       formBuilder.addComponent((JComponent)this.myOptions.get(option));
/*     */     }
/* 141 */     ActionListener everyWhereListener = new ActionListener()
/*     */       {
/*     */         public void actionPerformed(ActionEvent e) {
/* 144 */           for (JBCheckBox checkBox : SearchDialog.this.myOptions.values()) {
/* 145 */             checkBox.setEnabled(!SearchDialog.this.mySearchEverywhere.isSelected());
/* 146 */             if (SearchDialog.this.mySearchEverywhere.isSelected()) {
/* 147 */               checkBox.setSelected(true);
/*     */             }
/*     */           } 
/*     */         }
/*     */       };
/* 152 */     this.mySearchEverywhere.addActionListener(everyWhereListener);
/* 153 */     readStoredOptions();
/* 154 */     everyWhereListener.actionPerformed(null);
/*     */     
/* 156 */     DialogBuilder builder = new DialogBuilder(this.myProject);
/* 157 */     builder.setTitle(NodeJSBundle.message("dialog.title.search.in.v8.heap", new Object[0]));
/* 158 */     builder.setCenterPanel(formBuilder.getPanel());
/* 159 */     builder.setPreferredFocusComponent((JComponent)this.myText);
/* 160 */     builder.setDimensionServiceKey("com.jetbrains.nodejs.run.profile.heap.view.components.SearchDialog");
/* 161 */     builder.setHelpId("reference.v8.heap.search");
/* 162 */     return builder.showAndGet();
/*     */   }
/*     */   @Nls
/*     */   public static String[] getOptions() {
/* 166 */     return (new Names()).OPTIONS;
/*     */   }
/*     */ 
/*     */   
/*     */   private class Searcher
/*     */     extends Task.Backgroundable
/*     */   {
/*     */     private final Coordinator myCoordinator;
/*     */     
/*     */     private final String myText;
/*     */     
/*     */     private final boolean myCaseSensitive;
/*     */     
/*     */     private final boolean mySearchEverywhereSelected;
/*     */     private final V8MainTableWithRetainers<V8HeapContainmentTreeTableModel> myTableWithRetainers;
/*     */     private final V8CachingReader myReader;
/*     */     private final Map<String, JBCheckBox> myOptions;
/*     */     private final Map<String, Map<String, List<Pair<V8HeapEdge, V8HeapEntry>>>> myResultsByType;
/*     */     private boolean myTooMany;
/*     */     private final String myTextLowered;
/*     */     
/*     */     Searcher(Project project, String text, V8CachingReader reader, Map<String, JBCheckBox> options, boolean caseSensitive, boolean searchEverywhereSelected, V8MainTableWithRetainers<V8HeapContainmentTreeTableModel> tableWithRetainers) {
/* 188 */       super(project, NodeJSBundle.message("progress.title.searching.in.heap.snapshot", new Object[] { text }), true);
/* 189 */       this.myText = text;
/* 190 */       this.myCaseSensitive = caseSensitive;
/* 191 */       this.mySearchEverywhereSelected = searchEverywhereSelected;
/* 192 */       this.myTableWithRetainers = tableWithRetainers;
/* 193 */       this.myTextLowered = StringUtil.toLowerCase(this.myText);
/* 194 */       this.myReader = reader;
/* 195 */       this.myOptions = options;
/* 196 */       this.myCoordinator = new Coordinator(() -> show());
/* 197 */       this.myResultsByType = Collections.synchronizedMap(new HashMap<>());
/*     */     }
/*     */ 
/*     */     
/*     */     public void run(@NotNull ProgressIndicator indicator) {
/* 202 */       if (indicator == null) $$$reportNull$$$0(0);  Coordinator.Listener start = this.myCoordinator.start();
/*     */       
/* 204 */       trySearchByObjectId(indicator, this.myCoordinator.start());
/* 205 */       trySearchByTextMark(indicator, this.myCoordinator.start());
/* 206 */       trySearchByString(indicator, this.myCoordinator.start());
/*     */       
/* 208 */       start.finished();
/*     */     }
/*     */     
/*     */     private void show() {
/* 212 */       if (!this.myReader.isShowHidden()) {
/* 213 */         Set<Map.Entry<String, Map<String, List<Pair<V8HeapEdge, V8HeapEntry>>>>> byTypeEntries = this.myResultsByType.entrySet();
/* 214 */         Iterator<Map.Entry<String, Map<String, List<Pair<V8HeapEdge, V8HeapEntry>>>>> byTypeIterator = byTypeEntries.iterator();
/* 215 */         while (byTypeIterator.hasNext()) {
/* 216 */           Map.Entry<String, Map<String, List<Pair<V8HeapEdge, V8HeapEntry>>>> entry = byTypeIterator.next();
/* 217 */           Set<Map.Entry<String, List<Pair<V8HeapEdge, V8HeapEntry>>>> entries = ((Map<String, List<Pair<V8HeapEdge, V8HeapEntry>>>)entry.getValue()).entrySet();
/* 218 */           Iterator<Map.Entry<String, List<Pair<V8HeapEdge, V8HeapEntry>>>> byStringIterator = entries.iterator();
/* 219 */           while (byStringIterator.hasNext()) {
/* 220 */             Map.Entry<String, List<Pair<V8HeapEdge, V8HeapEntry>>> listEntry = byStringIterator.next();
/* 221 */             List<Pair<V8HeapEdge, V8HeapEntry>> value = listEntry.getValue();
/* 222 */             Iterator<Pair<V8HeapEdge, V8HeapEntry>> iterator = value.iterator();
/* 223 */             while (iterator.hasNext()) {
/* 224 */               Pair<V8HeapEdge, V8HeapEntry> pair = iterator.next();
/* 225 */               if (V8HeapNodeType.kHidden.equals(((V8HeapEntry)pair.getSecond()).getType()) || (pair
/* 226 */                 .getFirst() != null && V8HeapGraphEdgeType.kHidden.equals(((V8HeapEdge)pair.getFirst()).getType()))) {
/* 227 */                 iterator.remove();
/*     */               }
/*     */             } 
/* 230 */             if (value.isEmpty()) byStringIterator.remove(); 
/*     */           } 
/* 232 */           if (entries.isEmpty()) byTypeIterator.remove();
/*     */         
/*     */         } 
/*     */       } 
/* 236 */       if (this.myResultsByType.isEmpty()) {
/*     */         
/* 238 */         Notification notification = NodeProfilingSettings.HEAP_NOTIFICATION_GROUP.createNotification(NodeJSBundle.message("profile.heap.search.nothing_found_by_text.notification.content", new Object[] { this.myText }), MessageType.INFO);
/* 239 */         notification.notify(this.myProject);
/*     */         
/*     */         return;
/*     */       } 
/*     */       
/* 244 */       SearchResultsTreeModelFactory.ChainTreeModelWithTopLevelFilter<String> simple = (new SearchResultsTreeModelFactory(this.myProject, this.myText, this.myReader, this.myResultsByType, this.mySearchEverywhereSelected)).createSimple();
/*     */       
/* 246 */       SearchResultsTreeModelFactory.ChainTreeModelWithTopLevelFilter<String> byTypes = (new SearchResultsTreeModelFactory(this.myProject, this.myText, this.myReader, this.myResultsByType, this.mySearchEverywhereSelected)).createByTypes();
/* 247 */       ApplicationManager.getApplication().invokeLater(() -> {
/*     */             FindResultsWithOneTree results = new FindResultsWithOneTree(this.myProject, this.myReader, this.myText, simple.getNumResults(), this.myTableWithRetainers.getMainTreeNavigator());
/*     */             if (this.myTooMany) {
/*     */               results.moreResultsThan(Integer.valueOf(1000));
/*     */             }
/*     */             DataProviderPanel panel = results.showMe(simple, byTypes);
/*     */             DefaultActionGroup group = new DefaultActionGroup();
/*     */             results.addActions(group);
/*     */             this.myTableWithRetainers.addTab(NodeJSBundle.message("profile.search.occurences_tab.title", new Object[] { this.myText }), (JComponent)panel, group, true);
/*     */             results.defaultExpand();
/*     */           });
/*     */     }
/*     */ 
/*     */     
/*     */     private void trySearchByString(final ProgressIndicator indicator, final Coordinator.Listener listener) {
/* 262 */       if (!((JBCheckBox)this.myOptions.get(SearchDialog.this.LINK_NAMES)).isSelected() && !((JBCheckBox)this.myOptions.get(SearchDialog.this.CLASS_NAMES)).isSelected() && 
/* 263 */         !((JBCheckBox)this.myOptions.get(SearchDialog.this.TEXT_STRINGS)).isSelected()) {
/* 264 */         listener.finished();
/*     */         
/*     */         return;
/*     */       } 
/* 268 */       this.myReader.getStringIndex().parallelIterate(new CloseableProcessor<Pair<Long, String>, IOException>() {
/* 269 */             private final Map<Long, Pair<Long, String>> myByStringIdMap = Collections.synchronizedMap(new HashMap<>());
/*     */ 
/*     */             
/*     */             public void exceptionThrown(@NotNull IOException e) {
/* 273 */               if (e == null) $$$reportNull$$$0(0);  NodeProfilingSettings.HEAP_NOTIFICATION_GROUP
/* 274 */                 .createNotification(NodeJSBundle.message("profile.heap.search.processing.error.notification.content", new Object[] { e.getMessage() }), MessageType.ERROR).notify(SearchDialog.Searcher.this.myProject);
/* 275 */               SearchDialog.LOG.info(e);
/*     */             }
/*     */ 
/*     */ 
/*     */             
/*     */             public void close() throws IOException {
/*     */               try {
/* 282 */                 for (Map.Entry<Long, Pair<Long, String>> entry : this.myByStringIdMap.entrySet()) {
/* 283 */                   List<Pair<V8HeapEdge, V8HeapEntry>> nodes = SearchDialog.Searcher.this.myReader.getNodesByNameId(((Long)entry.getKey()).longValue());
/* 284 */                   MultiMap<String, Pair<V8HeapEdge, V8HeapEntry>> byType = SearchDialog.Searcher.this.filterNodesByType(entry.getKey(), nodes);
/*     */                   
/* 286 */                   for (Map.Entry<String, Collection<Pair<V8HeapEdge, V8HeapEntry>>> pairEntry : (Iterable<Map.Entry<String, Collection<Pair<V8HeapEdge, V8HeapEntry>>>>)byType.entrySet()) {
/* 287 */                     String type = pairEntry.getKey();
/* 288 */                     Map<String, List<Pair<V8HeapEdge, V8HeapEntry>>> listMap = SearchDialog.Searcher.this.myResultsByType.get(type);
/* 289 */                     if (listMap == null) {
/* 290 */                       SearchDialog.Searcher.this.myResultsByType.put(type, listMap = new HashMap<>());
/*     */                     }
/* 292 */                     List<Pair<V8HeapEdge, V8HeapEntry>> pairs = listMap.get(((Pair)entry.getValue()).getSecond());
/* 293 */                     if (pairs == null) {
/* 294 */                       listMap.put((String)((Pair)entry.getValue()).getSecond(), pairs = new ArrayList<>());
/*     */                     }
/* 296 */                     pairs.addAll(pairEntry.getValue());
/*     */                   } 
/*     */                 } 
/*     */               } finally {
/*     */                 
/* 301 */                 listener.finished();
/*     */               } 
/*     */             }
/*     */ 
/*     */             
/*     */             public boolean process(Pair<Long, String> pair) {
/* 307 */               if (indicator.isCanceled()) {
/* 308 */                 return false;
/*     */               }
/* 310 */               if (SearchDialog.Searcher.this.myCaseSensitive)
/* 311 */               { if (!((String)pair.getSecond()).contains(SearchDialog.Searcher.this.myText)) return true;
/*     */                  }
/*     */               
/* 314 */               else if (!StringUtil.toLowerCase((String)pair.getSecond()).contains(SearchDialog.Searcher.this.myTextLowered)) { return true; }
/*     */               
/* 316 */               if (this.myByStringIdMap.size() >= 1000) {
/* 317 */                 SearchDialog.Searcher.this.myTooMany = true;
/* 318 */                 return false;
/*     */               } 
/* 320 */               this.myByStringIdMap.put((Long)pair.getFirst(), pair);
/* 321 */               return true;
/*     */             }
/*     */           });
/*     */     }
/*     */     
/*     */     private MultiMap<String, Pair<V8HeapEdge, V8HeapEntry>> filterNodesByType(Long stringId, List<Pair<V8HeapEdge, V8HeapEntry>> nodes) {
/* 327 */       if (nodes == null || nodes.isEmpty()) return new MultiMap();
/*     */       
/* 329 */       MultiMap<String, Pair<V8HeapEdge, V8HeapEntry>> map = new MultiMap();
/* 330 */       for (Pair<V8HeapEdge, V8HeapEntry> pair : nodes) {
/* 331 */         String type = null;
/* 332 */         if (((JBCheckBox)this.myOptions.get(SearchDialog.this.LINK_NAMES)).isSelected() && pair.getFirst() != null && stringId
/* 333 */           .equals(Long.valueOf(((V8HeapEdge)pair.getFirst()).getNameId()))) {
/* 334 */           type = SearchDialog.this.LINK_NAMES;
/*     */         }
/* 336 */         else if (((JBCheckBox)this.myOptions.get(SearchDialog.this.CLASS_NAMES)).isSelected() && stringId.equals(Long.valueOf(((V8HeapEntry)pair.getSecond()).getNameId())) && 
/* 337 */           !((V8HeapEntry)pair.getSecond()).getType().isStringType()) {
/* 338 */           type = SearchDialog.this.CLASS_NAMES;
/*     */         }
/* 340 */         else if (((JBCheckBox)this.myOptions.get(SearchDialog.this.TEXT_STRINGS)).isSelected() && stringId.equals(Long.valueOf(((V8HeapEntry)pair.getSecond()).getNameId())) && ((V8HeapEntry)pair
/* 341 */           .getSecond()).getType().isStringType()) {
/* 342 */           type = SearchDialog.this.TEXT_STRINGS;
/*     */         } 
/* 344 */         if (type != null) {
/* 345 */           map.putValue(type, pair);
/*     */         }
/*     */       } 
/* 348 */       return map;
/*     */     }
/*     */     
/*     */     private void trySearchByTextMark(ProgressIndicator indicator, Coordinator.Listener listener) {
/* 352 */       if (!((JBCheckBox)this.myOptions.get(SearchDialog.this.MARKS)).isSelected()) {
/* 353 */         listener.finished();
/*     */         return;
/*     */       } 
/* 356 */       ApplicationManager.getApplication().executeOnPooledThread(() -> {
/*     */             try {
/*     */               indicator.checkCanceled();
/*     */               SearchByMarksWorker worker = new SearchByMarksWorker(this.myProject, this.myReader, this.myText, this.myCaseSensitive);
/*     */               Map<String, List<Pair<V8HeapEdge, V8HeapEntry>>> search = worker.search();
/*     */               indicator.checkCanceled();
/*     */               if (search != null) {
/*     */                 this.myResultsByType.put(SearchDialog.this.MARKS, search);
/*     */                 indicator.setText(NodeJSBundle.message("progress.text.found.nodes.by.text.mark", new Object[] { Integer.valueOf(worker.getNodesCnt()) }));
/*     */               } 
/*     */             } finally {
/*     */               listener.finished();
/*     */             } 
/*     */           });
/*     */     }
/*     */     
/*     */     private class SearchByMarksWorker { @NotNull
/*     */       private final Project myProject;
/*     */       private final V8CachingReader myReader;
/*     */       private final String myText;
/*     */       private final boolean myCaseSensitive;
/*     */       private final Map<String, List<Pair<V8HeapEdge, V8HeapEntry>>> myResult;
/*     */       private int myNodesCnt;
/*     */       
/*     */       SearchByMarksWorker(@NotNull Project project, V8CachingReader reader, String text, boolean caseSensitive) {
/* 381 */         this.myProject = project;
/* 382 */         this.myReader = reader;
/* 383 */         this.myText = text;
/* 384 */         this.myCaseSensitive = caseSensitive;
/* 385 */         this.myResult = new HashMap<>();
/*     */       }
/*     */       
/*     */       public Map<String, List<Pair<V8HeapEdge, V8HeapEntry>>> search() {
/* 389 */         Map<Long, String> map = V8HeapComponent.getInstance(this.myProject).searchByMark(this.myReader.getDigest(), this.myText, this.myCaseSensitive);
/* 390 */         if (map.isEmpty()) return null; 
/* 391 */         Set<Long> existing = new HashSet<>();
/* 392 */         for (Map.Entry<Long, String> entry : map.entrySet()) {
/* 393 */           if (existing.contains(entry.getKey()))
/* 394 */             continue;  existing.add(entry.getKey());
/*     */           
/* 396 */           List<Pair<V8HeapEdge, V8HeapEntry>> list = this.myResult.get(entry.getValue());
/* 397 */           if (list == null) this.myResult.put(entry.getValue(), list = new ArrayList<>()); 
/* 398 */           list.add(Pair.create(null, this.myReader.getNode(((Long)entry.getKey()).longValue())));
/*     */         } 
/* 400 */         this.myNodesCnt = existing.size();
/* 401 */         return this.myResult;
/*     */       }
/*     */       
/*     */       public int getNodesCnt() {
/* 405 */         return this.myNodesCnt;
/*     */       } }
/*     */     
/*     */     private void trySearchByObjectId(final ProgressIndicator indicator, final Coordinator.Listener listener) {
/*     */       final long id;
/* 410 */       if (!((JBCheckBox)this.myOptions.get(SearchDialog.this.SNAPSHOT_OBJECT_IDS)).isSelected()) {
/* 411 */         listener.finished();
/*     */         
/*     */         return;
/*     */       } 
/*     */       try {
/* 416 */         String searchText = this.myText.startsWith("@") ? this.myText.substring(1) : this.myText;
/* 417 */         id = Long.parseLong(searchText);
/* 418 */       } catch (NumberFormatException e) {
/* 419 */         listener.finished();
/*     */         
/*     */         return;
/*     */       } 
/* 423 */       ApplicationManager.getApplication().executeOnPooledThread(() -> {
/*     */             try {
/*     */               SequentialRawReader<V8HeapEntry> reader = new SequentialRawReader(this.myReader.getNodeIndexFile(), (RawSerializer)V8HeapEntry.MyRawSerializer.getInstance(), this.myReader.getNodeCount());
/*     */               reader.iterate(new CloseableThrowableConsumer<V8HeapEntry, IOException>()
/*     */                   {
/*     */                     private V8HeapEntry myEntry;
/*     */ 
/*     */ 
/*     */                     
/*     */                     public void close() throws IOException {
/* 433 */                       if (this.myEntry != null) {
/* 434 */                         List<Pair> list = new ArrayList();
/* 435 */                         list.add(Pair.create(null, this.myEntry));
/* 436 */                         Map<Object, Object> map = new HashMap<>();
/* 437 */                         map.put(SearchDialog.Searcher.this.myText, list);
/* 438 */                         SearchDialog.Searcher.this.myResultsByType.put(SearchDialog.this.SNAPSHOT_OBJECT_IDS, map);
/*     */                       } 
/* 440 */                       listener.finished();
/*     */                     }
/*     */ 
/*     */                     
/*     */                     public void consume(V8HeapEntry entry) throws IOException {
/* 445 */                       indicator.checkCanceled();
/* 446 */                       if (entry.getSnapshotObjectId() == id) {
/* 447 */                         this.myEntry = entry;
/* 448 */                         indicator.setText(NodeJSBundle.message("progress.text.found.object", new Object[] { Long.valueOf(this.val$id) }));
/* 449 */                         throw new ProcessCanceledException();
/*     */                       }
/*     */                     
/*     */                     }
/*     */                   });
/* 454 */             } catch (IOException e) {
/*     */               NodeProfilingSettings.HEAP_NOTIFICATION_GROUP.createNotification(NodeJSBundle.message("profile.heap.search.by.snapshot.object.id.error.notification.content", new Object[] { e.getMessage() }), MessageType.ERROR).notify(this.myProject);
/*     */               SearchDialog.LOG.info(e);
/*     */             } 
/*     */           });
/*     */     }
/*     */   }
/*     */   
/*     */   class null implements CloseableProcessor<Pair<Long, String>, IOException> {
/*     */     private final Map<Long, Pair<Long, String>> myByStringIdMap = Collections.synchronizedMap(new HashMap<>());
/*     */     
/*     */     public void exceptionThrown(@NotNull IOException e) {
/*     */       if (e == null)
/*     */         $$$reportNull$$$0(0); 
/*     */       NodeProfilingSettings.HEAP_NOTIFICATION_GROUP.createNotification(NodeJSBundle.message("profile.heap.search.processing.error.notification.content", new Object[] { e.getMessage() }), MessageType.ERROR).notify(SearchDialog.Searcher.this.myProject);
/*     */       SearchDialog.LOG.info(e);
/*     */     }
/*     */     
/*     */     public void close() throws IOException {
/*     */       try {
/*     */         for (Map.Entry<Long, Pair<Long, String>> entry : this.myByStringIdMap.entrySet()) {
/*     */           List<Pair<V8HeapEdge, V8HeapEntry>> nodes = SearchDialog.Searcher.this.myReader.getNodesByNameId(((Long)entry.getKey()).longValue());
/*     */           MultiMap<String, Pair<V8HeapEdge, V8HeapEntry>> byType = SearchDialog.Searcher.this.filterNodesByType(entry.getKey(), nodes);
/*     */           for (Map.Entry<String, Collection<Pair<V8HeapEdge, V8HeapEntry>>> pairEntry : (Iterable<Map.Entry<String, Collection<Pair<V8HeapEdge, V8HeapEntry>>>>)byType.entrySet()) {
/*     */             String type = pairEntry.getKey();
/*     */             Map<String, List<Pair<V8HeapEdge, V8HeapEntry>>> listMap = SearchDialog.Searcher.this.myResultsByType.get(type);
/*     */             if (listMap == null)
/*     */               SearchDialog.Searcher.this.myResultsByType.put(type, listMap = new HashMap<>()); 
/*     */             List<Pair<V8HeapEdge, V8HeapEntry>> pairs = listMap.get(((Pair)entry.getValue()).getSecond());
/*     */             if (pairs == null)
/*     */               listMap.put((String)((Pair)entry.getValue()).getSecond(), pairs = new ArrayList<>()); 
/*     */             pairs.addAll(pairEntry.getValue());
/*     */           } 
/*     */         } 
/*     */       } finally {
/*     */         listener.finished();
/*     */       } 
/*     */     }
/*     */     
/*     */     public boolean process(Pair<Long, String> pair) {
/*     */       if (indicator.isCanceled())
/*     */         return false; 
/*     */       if (SearchDialog.Searcher.this.myCaseSensitive) {
/*     */         if (!((String)pair.getSecond()).contains(SearchDialog.Searcher.this.myText))
/*     */           return true; 
/*     */       } else if (!StringUtil.toLowerCase((String)pair.getSecond()).contains(SearchDialog.Searcher.this.myTextLowered)) {
/*     */         return true;
/*     */       } 
/*     */       if (this.myByStringIdMap.size() >= 1000) {
/*     */         SearchDialog.Searcher.this.myTooMany = true;
/*     */         return false;
/*     */       } 
/*     */       this.myByStringIdMap.put((Long)pair.getFirst(), pair);
/*     */       return true;
/*     */     }
/*     */   }
/*     */   
/*     */   private class SearchByMarksWorker {
/*     */     @NotNull
/*     */     private final Project myProject;
/*     */     private final V8CachingReader myReader;
/*     */     private final String myText;
/*     */     private final boolean myCaseSensitive;
/*     */     private final Map<String, List<Pair<V8HeapEdge, V8HeapEntry>>> myResult;
/*     */     private int myNodesCnt;
/*     */     
/*     */     SearchByMarksWorker(@NotNull Project project, V8CachingReader reader, String text, boolean caseSensitive) {
/*     */       this.myProject = project;
/*     */       this.myReader = reader;
/*     */       this.myText = text;
/*     */       this.myCaseSensitive = caseSensitive;
/*     */       this.myResult = new HashMap<>();
/*     */     }
/*     */     
/*     */     public Map<String, List<Pair<V8HeapEdge, V8HeapEntry>>> search() {
/*     */       Map<Long, String> map = V8HeapComponent.getInstance(this.myProject).searchByMark(this.myReader.getDigest(), this.myText, this.myCaseSensitive);
/*     */       if (map.isEmpty())
/*     */         return null; 
/*     */       Set<Long> existing = new HashSet<>();
/*     */       for (Map.Entry<Long, String> entry : map.entrySet()) {
/*     */         if (existing.contains(entry.getKey()))
/*     */           continue; 
/*     */         existing.add(entry.getKey());
/*     */         List<Pair<V8HeapEdge, V8HeapEntry>> list = this.myResult.get(entry.getValue());
/*     */         if (list == null)
/*     */           this.myResult.put(entry.getValue(), list = new ArrayList<>()); 
/*     */         list.add(Pair.create(null, this.myReader.getNode(((Long)entry.getKey()).longValue())));
/*     */       } 
/*     */       this.myNodesCnt = existing.size();
/*     */       return this.myResult;
/*     */     }
/*     */     
/*     */     public int getNodesCnt() {
/*     */       return this.myNodesCnt;
/*     */     }
/*     */   }
/*     */   
/*     */   class null implements CloseableThrowableConsumer<V8HeapEntry, IOException> {
/*     */     private V8HeapEntry myEntry;
/*     */     
/*     */     public void close() throws IOException {
/*     */       if (this.myEntry != null) {
/*     */         List<Pair> list = new ArrayList();
/*     */         list.add(Pair.create(null, this.myEntry));
/*     */         Map<Object, Object> map = new HashMap<>();
/*     */         map.put(SearchDialog.Searcher.this.myText, list);
/*     */         SearchDialog.Searcher.this.myResultsByType.put(SearchDialog.this.SNAPSHOT_OBJECT_IDS, map);
/*     */       } 
/*     */       listener.finished();
/*     */     }
/*     */     
/*     */     public void consume(V8HeapEntry entry) throws IOException {
/*     */       indicator.checkCanceled();
/*     */       if (entry.getSnapshotObjectId() == id) {
/*     */         this.myEntry = entry;
/*     */         indicator.setText(NodeJSBundle.message("progress.text.found.object", new Object[] { Long.valueOf(this.val$id) }));
/*     */         throw new ProcessCanceledException();
/*     */       } 
/*     */     }
/*     */   }
/*     */ }


/* Location:              C:\Program Files\JetBrains\WebStorm 2021.1\plugins\NodeJS\lib\NodeJS.jar!\com\jetbrains\nodejs\run\profile\heap\view\components\SearchDialog.class
 * Java compiler version: 11 (55.0)
 * JD-Core Version:       1.1.3
 */