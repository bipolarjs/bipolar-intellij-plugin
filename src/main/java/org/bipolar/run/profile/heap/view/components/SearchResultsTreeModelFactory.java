/*     */ package org.bipolar.run.profile.heap.view.components;
/*     */ import com.intellij.openapi.project.Project;
/*     */ import com.intellij.openapi.util.Comparing;
/*     */ import com.intellij.openapi.util.Pair;
/*     */ import com.intellij.ui.ColoredTreeCellRenderer;
/*     */ import com.intellij.ui.SimpleTextAttributes;
/*     */ import com.intellij.ui.treeStructure.treetable.TreeTableModel;
/*     */ import com.intellij.ui.treeStructure.treetable.TreeTableTree;
/*     */ import com.intellij.util.ui.ColumnInfo;
/*     */ import org.bipolar.NodeJSBundle;
/*     */ import org.bipolar.run.profile.heap.V8CachingReader;
/*     */ import org.bipolar.run.profile.heap.data.V8HeapEdge;
/*     */ import org.bipolar.run.profile.heap.data.V8HeapEntry;
/*     */ import org.bipolar.run.profile.heap.view.models.RetainersTreeModel;
/*     */ import org.bipolar.run.profile.heap.view.models.SearchDetailsTreeModel;
/*     */ import org.bipolar.run.profile.heap.view.nodes.FixedRetainerNode;
/*     */ import org.bipolar.run.profile.heap.view.renderers.DirectTreeTableRenderer;
/*     */ import java.util.ArrayList;
/*     */ import java.util.Collections;
/*     */ import java.util.Comparator;
/*     */ import java.util.HashMap;
/*     */ import java.util.HashSet;
/*     */ import java.util.List;
/*     */ import java.util.Map;
/*     */ import java.util.Set;
/*     */ import javax.swing.JTree;
/*     */
import org.jetbrains.annotations.Nls;
/*     */ import org.jetbrains.annotations.NotNull;
/*     */ 
/*     */ public class SearchResultsTreeModelFactory {
/*  31 */   private static final Object TARGETS = new Object();
/*  32 */   private static final Object FOUND = new Object();
/*  33 */   private static final Object TARGETS_CONTENTS = new Object();
/*  34 */   private static final Comparator<String> BY_TYPE_COMPARATOR = createByTypeComparator();
/*     */   private final Project myProject;
/*     */   @NotNull
/*     */   private final String myText;
/*     */   @NotNull
/*     */   private final V8CachingReader myReader;
/*     */   @NotNull
/*     */   private final Map<String, Map<String, List<Pair<V8HeapEdge, V8HeapEntry>>>> myMap;
/*     */   private final boolean mySearchEverywhereSelected;
/*     */   private final Comparator<String> myComparator;
/*     */   @Nls
/*     */   private String myTarget;
/*     */   
/*     */   public SearchResultsTreeModelFactory(Project project, @NotNull String text, @NotNull V8CachingReader reader, @NotNull Map<String, Map<String, List<Pair<V8HeapEdge, V8HeapEntry>>>> map, boolean searchEverywhereSelected) {
/*  48 */     this.myProject = project;
/*  49 */     this.myText = text;
/*  50 */     this.myReader = reader;
/*  51 */     this.myMap = new HashMap<>();
/*  52 */     for (Map.Entry<String, Map<String, List<Pair<V8HeapEdge, V8HeapEntry>>>> entry : map.entrySet()) {
/*  53 */       this.myMap.put(((String)entry.getKey()).replace("&", ""), entry.getValue());
/*     */     }
/*  55 */     this.mySearchEverywhereSelected = searchEverywhereSelected;
/*  56 */     this.myComparator = createComparator();
/*     */   }
/*     */   
/*     */   @NotNull
/*     */   private static Comparator<String> createByTypeComparator() {
/*  61 */     Map<String, Integer> rank = new HashMap<>();
/*  62 */     for (String option : SearchDialog.getOptions()) {
/*  63 */       rank.put(option.replace("&", ""), Integer.valueOf(rank.size()));
/*     */     }
/*  65 */     if (((o1, o2) -> Comparing.compare((Integer)rank.get(o1), (Integer)rank.get(o2))) == null) $$$reportNull$$$0(3);  return (o1, o2) -> Comparing.compare((Integer)rank.get(o1), (Integer)rank.get(o2));
/*     */   }
/*     */   
/*     */   @NotNull
/*     */   private Comparator<String> createComparator() {
/*     */     if (((o1, o2) -> {
/*     */         boolean exact1 = o1.equalsIgnoreCase(this.myText);
/*     */         boolean exact2 = o2.equalsIgnoreCase(this.myText);
/*  73 */         return (exact1 && exact2) ? o1.compareTo(o2) : (exact1 ? -1 : (exact2 ? 1 : o1.compareTo(o2))); }) == null) $$$reportNull$$$0(4);  return (o1, o2) -> { boolean exact1 = o1.equalsIgnoreCase(this.myText); boolean exact2 = o2.equalsIgnoreCase(this.myText); return (exact1 && exact2) ? o1.compareTo(o2) : (exact1 ? -1 : (exact2 ? 1 : o1.compareTo(o2)));
/*     */       };
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private ColumnInfo[] createColumns(V8CachingReader reader) {
/*  82 */     ColumnInfo[] columnInfos = new ColumnInfo[4];
/*  83 */     DirectTreeTableRenderer renderer = new DirectTreeTableRenderer(this.myProject, reader)
/*     */       {
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */         
/*     */         public void customizeCellRenderer(@NotNull JTree tree, Object value, boolean selected, boolean expanded, boolean leaf, int row, boolean hasFocus)
/*     */         {
/*  92 */           if (tree == null) $$$reportNull$$$0(0);  if (value instanceof ChainTreeTableModel.Node) {
/*  93 */             Object obj = ((ChainTreeTableModel.Node)value).getT();
/*  94 */             if (obj == SearchResultsTreeModelFactory.TARGETS) {
/*  95 */               setBackground(null);
/*  96 */               append(NodeJSBundle.message("profile.search_results.targets.node.name", new Object[0]), SimpleTextAttributes.REGULAR_BOLD_ATTRIBUTES);
/*  97 */             } else if (obj == SearchResultsTreeModelFactory.FOUND) {
/*  98 */               setBackground(null);
/*  99 */               append(NodeJSBundle.message("profile.search_results.found_occurrences.node.name", new Object[0]), SimpleTextAttributes.REGULAR_BOLD_ATTRIBUTES);
/* 100 */             } else if (obj == SearchResultsTreeModelFactory.TARGETS_CONTENTS) {
/* 101 */               setBackground(null);
/* 102 */               append(SearchResultsTreeModelFactory.this.myTarget, SimpleTextAttributes.REGULAR_ATTRIBUTES);
/* 103 */             } else if (obj instanceof Pair && ((Pair)obj).getFirst() instanceof String && ((Pair)obj).getSecond() instanceof SimpleTextAttributes) {
/* 104 */               setBackground(null);
/* 105 */               SimpleTextAttributes attributes = (SimpleTextAttributes)((Pair)obj).getSecond();
/* 106 */               if (selected)
/*     */               {
/* 108 */                 attributes = new SimpleTextAttributes(attributes.getBgColor(), SimpleTextAttributes.SELECTED_SIMPLE_CELL_ATTRIBUTES.getFgColor(), attributes.getWaveColor(), attributes.getStyle());
/*     */               }
/* 110 */               String fragment = ((Pair)obj).getFirst().toString();
/* 111 */               append(fragment, attributes);
/*     */             } else {
/* 113 */               super.customizeCellRenderer(tree, obj, selected, expanded, leaf, row, hasFocus);
/*     */             } 
/* 115 */             int count = ((ChainTreeTableModel.Node)value).getMeaningfulChildren();
/* 116 */             if (count > 0) {
/*     */               
/* 118 */               SimpleTextAttributes attributes = selected ? SimpleTextAttributes.REGULAR_ATTRIBUTES : SimpleTextAttributes.GRAYED_ATTRIBUTES;
/* 119 */               append(" (", attributes);
/* 120 */               append(NodeJSBundle.message("profile.search_results.found_occurrences.text", new Object[] { Integer.valueOf(count) }), attributes);
/* 121 */               append(")", attributes);
/*     */             } 
/*     */           } else {
/*     */             
/* 125 */             super.customizeCellRenderer(tree, value, selected, expanded, leaf, row, hasFocus);
/*     */           } 
/*     */         }
/*     */       };
/* 129 */     renderer.setReverseAsDirect(true);
/* 130 */     RetainersTreeModel.fillColumns(this.myProject, columnInfos, reader, (ColoredTreeCellRenderer)renderer);
/* 131 */     return columnInfos;
/*     */   }
/*     */   
/*     */   public ChainTreeModelWithTopLevelFilter<String> createByTypes() {
/* 135 */     List<String> typeKeys = new ArrayList<>(this.myMap.keySet());
/* 136 */     typeKeys.sort(BY_TYPE_COMPARATOR);
/*     */     
/* 138 */     ChainTreeModelWithTopLevelFilter<String> mainModel = new ChainTreeModelWithTopLevelFilter<>(createColumns(this.myReader));
/* 139 */     addSearchConditions(mainModel);
/*     */     
/* 141 */     ChainTreeTableModel.Node topNode = mainModel.createNode(FOUND);
/* 142 */     mainModel.addTopKey(topNode);
/*     */     
/* 144 */     int numResults = 0;
/* 145 */     for (String key : typeKeys) {
/* 146 */       ChainTreeTableModel.Node<Object> typeNode = mainModel.createNode(Pair.create(key, SimpleTextAttributes.REGULAR_ATTRIBUTES));
/* 147 */       topNode.getChildren().add(typeNode);
/* 148 */       Map<String, List<Pair<V8HeapEdge, V8HeapEntry>>> map = this.myMap.get(key);
/* 149 */       List<String> stringKeys = new ArrayList<>(map.keySet());
/* 150 */       stringKeys.sort(this.myComparator);
/* 151 */       List<Pair<V8HeapEdge, V8HeapEntry>> commonList = new ArrayList<>();
/* 152 */       for (String stringKey : stringKeys) {
/* 153 */         commonList.addAll(map.get(stringKey));
/*     */       }
/* 155 */       int numNodes = addResultsForStringKey(commonList, mainModel, typeNode.getChildren());
/* 156 */       typeNode.setMeaningfulChildren(numNodes);
/* 157 */       numResults += numNodes;
/*     */     } 
/* 159 */     topNode.setMeaningfulChildren(numResults);
/* 160 */     mainModel.setNumResults(numResults);
/* 161 */     return mainModel;
/*     */   }
/*     */   
/*     */   public ChainTreeModelWithTopLevelFilter<String> createSimple() {
/* 165 */     Map<String, List<Pair<V8HeapEdge, V8HeapEntry>>> map = zipByString();
/* 166 */     List<String> keys = new ArrayList<>(map.keySet());
/* 167 */     keys.sort(this.myComparator);
/*     */     
/* 169 */     ChainTreeModelWithTopLevelFilter<String> mainModel = new ChainTreeModelWithTopLevelFilter<>(createColumns(this.myReader));
/* 170 */     addSearchConditions(mainModel);
/*     */     
/* 172 */     ChainTreeTableModel.Node topNode = mainModel.createNode(FOUND);
/* 173 */     mainModel.addTopKey(topNode);
/* 174 */     int numResults = 0;
/* 175 */     for (String key : keys) {
/* 176 */       ChainTreeTableModel.Node<Object> keyNode = mainModel.createNode(Pair.create(key, SimpleTextAttributes.DARK_TEXT));
/* 177 */       topNode.getChildren().add(keyNode);
/* 178 */       List<ChainTreeTableModel.Node<Object>> addedNodesList = keyNode.getChildren();
/* 179 */       int numNodes = addResultsForStringKey(map.get(key), mainModel, addedNodesList);
/* 180 */       keyNode.setMeaningfulChildren(numNodes);
/* 181 */       numResults += numNodes;
/*     */     } 
/* 183 */     topNode.setMeaningfulChildren(numResults);
/* 184 */     mainModel.setNumResults(numResults);
/* 185 */     return mainModel;
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   private int addResultsForStringKey(List<Pair<V8HeapEdge, V8HeapEntry>> list, ChainTreeModelWithTopLevelFilter<String> mainModel, List<ChainTreeTableModel.Node<Object>> addedNodesList) {
/* 191 */     int numResults = 0;
/* 192 */     List<List<FixedRetainerNode>> nodes = new ArrayList<>();
/*     */     
/* 194 */     for (Pair<V8HeapEdge, V8HeapEntry> pair : list) {
/* 195 */       List<FixedRetainerNode> chain = SearchDetailsTreeModel.getChainToRoot(0, (V8HeapEntry)pair.getSecond(), (V8HeapEdge)pair.getFirst(), this.myReader);
/* 196 */       Collections.reverse(chain);
/* 197 */       if (!chain.isEmpty()) {
/* 198 */         nodes.add(chain);
/* 199 */         numResults++;
/*     */       } 
/*     */     } 
/*     */     
/* 203 */     for (List<FixedRetainerNode> nodeList : nodes) {
/* 204 */       ChainTreeTableModel.Node<Object> node = mainModel.createNode(nodeList.get(0));
/* 205 */       addedNodesList.add(node);
/* 206 */       for (int i = 1; i < nodeList.size(); i++) {
/* 207 */         ChainTreeTableModel.Node<Object> childNode = mainModel.createNode(nodeList.get(i));
/* 208 */         node.getChildren().add(childNode);
/* 209 */         node = childNode;
/*     */       } 
/*     */     } 
/* 212 */     return numResults;
/*     */   }
/*     */   
/*     */   private void addSearchConditions(ChainTreeModelWithTopLevelFilter<String> model) {
/* 216 */     StringBuilder sb = new StringBuilder("Occurrences of '" + this.myText + "' ");
/* 217 */     if (this.mySearchEverywhereSelected) {
/* 218 */       sb.append("everywhere");
/*     */     } else {
/* 220 */       List<String> list = new ArrayList<>();
/* 221 */       for (String s : this.myMap.keySet()) {
/* 222 */         list.add(s.replace("&", ""));
/*     */       }
/* 224 */       Collections.sort(list);
/* 225 */       sb.append("in ").append(StringUtil.join(list, ", "));
/*     */     } 
/*     */     
/* 228 */     ChainTreeTableModel.Node targetsNode = model.createNode(TARGETS);
/* 229 */     model.addTopKey(targetsNode);
/* 230 */     String target = sb.toString();
/* 231 */     this.myTarget = target;
/* 232 */     targetsNode.getChildren().add(model.createNode(TARGETS_CONTENTS));
/*     */   }
/*     */   
/*     */   private Map<String, List<Pair<V8HeapEdge, V8HeapEntry>>> zipByString() {
/* 236 */     Map<String, List<Pair<V8HeapEdge, V8HeapEntry>>> result = new HashMap<>();
/* 237 */     for (Map.Entry<String, Map<String, List<Pair<V8HeapEdge, V8HeapEntry>>>> entry : this.myMap.entrySet()) {
/* 238 */       Map<String, List<Pair<V8HeapEdge, V8HeapEntry>>> byKey = entry.getValue();
/* 239 */       for (Map.Entry<String, List<Pair<V8HeapEdge, V8HeapEntry>>> listEntry : byKey.entrySet()) {
/* 240 */         List<Pair<V8HeapEdge, V8HeapEntry>> list = result.get(listEntry.getKey());
/* 241 */         if (list == null) {
/* 242 */           result.put(listEntry.getKey(), list = new ArrayList<>());
/*     */         }
/* 244 */         list.addAll(listEntry.getValue());
/*     */       } 
/*     */     } 
/* 247 */     for (Map.Entry<String, List<Pair<V8HeapEdge, V8HeapEntry>>> entry : result.entrySet()) {
/* 248 */       List<Pair<V8HeapEdge, V8HeapEntry>> list = entry.getValue();
/* 249 */       List<Pair<V8HeapEdge, V8HeapEntry>> filtered = filterRepeatingNodesOut(list);
/* 250 */       if (list.size() > filtered.size()) {
/* 251 */         list.clear();
/* 252 */         list.addAll(filtered);
/*     */       } 
/*     */     } 
/* 255 */     return result;
/*     */   }
/*     */   
/*     */   private static List<Pair<V8HeapEdge, V8HeapEntry>> filterRepeatingNodesOut(List<Pair<V8HeapEdge, V8HeapEntry>> list) {
/* 259 */     if (list.size() <= 1) return list; 
/* 260 */     Set<Long> nodeIdsReferencesByLinks = new HashSet<>();
/* 261 */     for (Pair<V8HeapEdge, V8HeapEntry> pair : list) {
/* 262 */       if (pair.getFirst() != null) {
/* 263 */         nodeIdsReferencesByLinks.add(Long.valueOf(((V8HeapEntry)pair.getSecond()).getId()));
/*     */       }
/*     */     } 
/* 266 */     List<Pair<V8HeapEdge, V8HeapEntry>> result = new ArrayList<>();
/* 267 */     Set<Long> alreadyAdded = new HashSet<>();
/* 268 */     for (Pair<V8HeapEdge, V8HeapEntry> pair : list) {
/* 269 */       if ((pair.getFirst() == null && nodeIdsReferencesByLinks.contains(Long.valueOf(((V8HeapEntry)pair.getSecond()).getId()))) || 
/* 270 */         alreadyAdded.contains(Long.valueOf(((V8HeapEntry)pair.getSecond()).getId())))
/* 271 */         continue;  result.add(pair);
/* 272 */       alreadyAdded.add(Long.valueOf(((V8HeapEntry)pair.getSecond()).getId()));
/*     */     } 
/* 274 */     return result;
/*     */   }
/*     */   
/*     */   public static void expandTop(TreeTableTree tree, TreeTableModel model) {
/* 278 */     ChainTreeTableModel.Node root = (ChainTreeTableModel.Node)model.getRoot();
/* 279 */     for (Object o : root.getChildren()) {
/*     */       
/* 281 */       tree.expandPath(new TreePath(new Object[] { root, o }));
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   public static class ChainTreeModelWithTopLevelFilter<T>
/*     */     extends ChainTreeTableModel
/*     */   {
/*     */     private final Set<T> myAllowedTopLevel;
/*     */     
/*     */     private boolean myFilter;
/*     */     private int myNumResults;
/*     */     
/*     */     public ChainTreeModelWithTopLevelFilter(ColumnInfo[] columns) {
/* 295 */       super(columns);
/* 296 */       this.myAllowedTopLevel = new HashSet<>();
/* 297 */       this.myFilter = false;
/* 298 */       this.myNumResults = 0;
/*     */     }
/*     */     
/*     */     public int getNumResults() {
/* 302 */       return this.myNumResults;
/*     */     }
/*     */     
/*     */     public void setNumResults(int numResults) {
/* 306 */       this.myNumResults = numResults;
/*     */     }
/*     */     
/*     */     public void noFilter() {
/* 310 */       this.myFilter = false;
/* 311 */       this.myAllowedTopLevel.clear();
/*     */     }
/*     */     
/*     */     public void setFilter(Set<T> set) {
/* 315 */       this.myAllowedTopLevel.clear();
/* 316 */       this.myAllowedTopLevel.addAll(set);
/* 317 */       this.myFilter = true;
/*     */     }
/*     */ 
/*     */     
/*     */     protected List<ChainTreeTableModel.Node<?>> getChildren(Object object) {
/* 322 */       List<ChainTreeTableModel.Node<?>> children = super.getChildren(object);
/* 323 */       if (this.myFilter && getRoot().equals(object)) {
/* 324 */         List<ChainTreeTableModel.Node<?>> list = new ArrayList<>();
/* 325 */         for (ChainTreeTableModel.Node<?> child : children) {
/* 326 */           if (this.myAllowedTopLevel.contains(child.getT())) {
/* 327 */             list.add(child);
/*     */           }
/*     */         } 
/* 330 */         return list;
/*     */       } 
/* 332 */       return children;
/*     */     }
/*     */   }
/*     */ }


/* Location:              C:\Program Files\JetBrains\WebStorm 2021.1\plugins\NodeJS\lib\NodeJS.jar!\com\jetbrains\nodejs\run\profile\heap\view\components\SearchResultsTreeModelFactory.class
 * Java compiler version: 11 (55.0)
 * JD-Core Version:       1.1.3
 */