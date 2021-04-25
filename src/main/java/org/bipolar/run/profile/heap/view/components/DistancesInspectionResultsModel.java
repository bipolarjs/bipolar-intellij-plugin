/*     */ package org.bipolar.run.profile.heap.view.components;
/*     */ import com.intellij.openapi.project.Project;
/*     */ import com.intellij.openapi.util.Pair;
/*     */ import com.intellij.ui.SimpleTextAttributes;
/*     */ import com.intellij.util.ui.ColumnInfo;
/*     */ import org.bipolar.NodeJSBundle;
/*     */ import org.bipolar.run.profile.heap.V8CachingReader;
/*     */ import org.bipolar.run.profile.heap.data.Aggregate;
/*     */ import org.bipolar.run.profile.heap.data.V8HeapEdge;
/*     */ import org.bipolar.run.profile.heap.data.V8HeapEntry;
/*     */ import org.bipolar.run.profile.heap.view.models.RetainersTreeModel;
/*     */ import org.bipolar.run.profile.heap.view.models.V8HeapContainmentTreeTableModel;
/*     */ import org.bipolar.run.profile.heap.view.renderers.DirectTreeTableRenderer;
/*     */ import java.util.ArrayList;
/*     */ import java.util.Iterator;
/*     */ import java.util.List;
/*     */ import java.util.Map;
/*     */ import java.util.Set;
/*     */ import java.util.TreeMap;
/*     */ import javax.swing.JTree;
/*     */
import org.jetbrains.annotations.NotNull;
/*     */ 
/*     */ public class DistancesInspectionResultsModel extends SearchResultsTreeModelFactory.ChainTreeModelWithTopLevelFilter<String> {
/*  24 */   public static final Object DESCRIPTION = new Object();
/*  25 */   public static final Object FOUND_BY_TYPE = new Object();
/*  26 */   public static final Object FOUND_BY_NAME = new Object();
/*     */   
/*     */   public DistancesInspectionResultsModel(Project project, V8CachingReader reader, V8DistancesInspection inspection) {
/*  29 */     super(createColumns(project, reader));
/*  30 */     addTopKey(createNode(DESCRIPTION));
/*     */     
/*  32 */     int numResults = byTypeSubtree(reader, inspection);
/*  33 */     numResults = byNamesSubtree(reader, inspection, numResults);
/*     */     
/*  35 */     setNumResults(numResults);
/*     */   }
/*     */   
/*     */   private int byNamesSubtree(V8CachingReader reader, V8DistancesInspection inspection, int numResults) {
/*  39 */     ChainTreeTableModel.Node namesNode = createNode(FOUND_BY_NAME);
/*  40 */     addTopKey(namesNode);
/*  41 */     ArrayList<List<Long>> byNamesList = inspection.getByNamesList();
/*  42 */     for (Iterator<List<Long>> iterator = byNamesList.iterator(); iterator.hasNext(); ) { List<Long> list = iterator.next();
/*  43 */       if (list.isEmpty())
/*  44 */         continue;  V8HeapEdge firstEdge = reader.getEdge(((Long)list.get(0)).longValue());
/*  45 */       ChainTreeTableModel.Node nameNode = createNode(Pair.create(reader.getString(firstEdge.getNameId()), SimpleTextAttributes.REGULAR_ATTRIBUTES));
/*  46 */       namesNode.getChildren().add(nameNode);
/*  47 */       numResults++;
/*     */       
/*  49 */       List<V8HeapEdge> edges = new ArrayList<>(list.size());
/*  50 */       Set<Long> nodesIds = new HashSet<>();
/*  51 */       for (Long edge : list) {
/*  52 */         V8HeapEdge heapEdge = reader.getEdge(edge.longValue());
/*  53 */         if (!nodesIds.contains(Long.valueOf(heapEdge.getToIndex()))) {
/*  54 */           edges.add(heapEdge);
/*  55 */           nodesIds.add(Long.valueOf(heapEdge.getToIndex()));
/*     */         } 
/*     */       } 
/*  58 */       edges.sort((o1, o2) -> {
/*     */             V8HeapEntry node1 = reader.getNode(o1.getToIndex());
/*     */             V8HeapEntry node2 = reader.getNode(o2.getToIndex());
/*     */             if (node1.getClassIndex() != node2.getClassIndex()) {
/*     */               String typeName1 = Aggregate.getClassNameByClassIdx(reader, node1.getClassIndex());
/*     */               String typeName2 = Aggregate.getClassNameByClassIdx(reader, node2.getClassIndex());
/*     */               return typeName1.compareTo(typeName2);
/*     */             } 
/*     */             return Integer.compare(reader.getDistance((int)o2.getToIndex()), reader.getDistance((int)o1.getToIndex()));
/*     */           });
/*  68 */       for (V8HeapEdge heapEdge : edges) {
/*     */         
/*  70 */         V8HeapContainmentTreeTableModel.NamedEntry namedEntry = V8HeapContainmentTreeTableModel.NamedEntry.create(heapEdge, reader);
/*  71 */         ChainTreeTableModel.Node node = createNode(namedEntry);
/*  72 */         nameNode.getChildren().add(node);
/*     */       } 
/*  74 */       nameNode.setMeaningfulChildren(edges.size()); }
/*     */     
/*  76 */     return numResults;
/*     */   }
/*     */   
/*     */   private int byTypeSubtree(V8CachingReader reader, V8DistancesInspection inspection) {
/*  80 */     ChainTreeTableModel.Node typesNode = createNode(FOUND_BY_TYPE);
/*  81 */     addTopKey(typesNode);
/*     */     
/*  83 */     int numResults = 0;
/*  84 */     TreeMap<Long, V8DistancesInspection.TypeData> sortedByTypes = inspection.getSortedByTypes();
/*  85 */     for (Map.Entry<Long, V8DistancesInspection.TypeData> entry : sortedByTypes.entrySet()) {
/*  86 */       String typeName = Aggregate.getClassNameByClassIdx(reader, ((Long)entry.getKey()).longValue());
/*     */       
/*  88 */       ChainTreeTableModel.Node typeNode = createNode(Pair.create(typeName, SimpleTextAttributes.REGULAR_ATTRIBUTES));
/*  89 */       typesNode.getChildren().add(typeNode);
/*  90 */       numResults++;
/*     */       
/*  92 */       TreeMap<Integer, Pair<V8HeapEntry, V8HeapEdge>> map = ((V8DistancesInspection.TypeData)entry.getValue()).getMap();
/*  93 */       Set<Integer> set = map.descendingKeySet();
/*  94 */       typeNode.setMeaningfulChildren(set.size());
/*  95 */       typeNode.setOnlyPartOfChildren(((V8DistancesInspection.TypeData)entry.getValue()).isSomethingMissing());
/*  96 */       for (Integer distance : set) {
/*  97 */         Pair<V8HeapEntry, V8HeapEdge> pair = map.get(distance);
/*     */ 
/*     */         
/* 100 */         V8HeapContainmentTreeTableModel.NamedEntry namedEntry = (pair.getSecond() != null) ? V8HeapContainmentTreeTableModel.NamedEntry.create((V8HeapEdge)pair.getSecond(), reader) : V8HeapContainmentTreeTableModel.NamedEntry.createWithoutLink(((V8HeapEntry)pair.getFirst()).getId(), reader);
/* 101 */         ChainTreeTableModel.Node node = createNode(namedEntry);
/* 102 */         typeNode.getChildren().add(node);
/*     */       } 
/*     */     } 
/* 105 */     return numResults;
/*     */   }
/*     */   
/*     */   private static ColumnInfo[] createColumns(Project project, V8CachingReader reader) {
/* 109 */     ColumnInfo[] columnInfos = new ColumnInfo[4];
/* 110 */     DirectTreeTableRenderer renderer = new DirectTreeTableRenderer(project, reader)
/*     */       {
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */         
/*     */         public void customizeCellRenderer(@NotNull JTree tree, Object value, boolean selected, boolean expanded, boolean leaf, int row, boolean hasFocus)
/*     */         {
/* 119 */           if (tree == null) $$$reportNull$$$0(0);  if (value instanceof ChainTreeTableModel.Node) {
/* 120 */             Object obj = ((ChainTreeTableModel.Node)value).getT();
/* 121 */             if (obj == DistancesInspectionResultsModel.DESCRIPTION) {
/* 122 */               setBackground(null);
/* 123 */               append(NodeJSBundle.message("profile.DistancesInspection.table.description.text", new Object[0]), SimpleTextAttributes.REGULAR_ATTRIBUTES);
/*     */             }
/* 125 */             else if (obj == DistancesInspectionResultsModel.FOUND_BY_TYPE) {
/* 126 */               setBackground(null);
/* 127 */               append(NodeJSBundle.message("profile.DistancesInspection.table.foundByType.text", new Object[0]), SimpleTextAttributes.REGULAR_BOLD_ATTRIBUTES);
/* 128 */             } else if (obj == DistancesInspectionResultsModel.FOUND_BY_NAME) {
/* 129 */               setBackground(null);
/* 130 */               append(NodeJSBundle.message("profile.DistancesInspection.table.foundByName.text", new Object[0]), SimpleTextAttributes.REGULAR_BOLD_ATTRIBUTES);
/* 131 */             } else if (obj instanceof Pair && ((Pair)obj).getFirst() instanceof String && ((Pair)obj).getSecond() instanceof SimpleTextAttributes) {
/* 132 */               setBackground(null);
/* 133 */               SimpleTextAttributes attributes = (SimpleTextAttributes)((Pair)obj).getSecond();
/* 134 */               if (selected)
/*     */               {
/* 136 */                 attributes = new SimpleTextAttributes(attributes.getBgColor(), SimpleTextAttributes.SELECTED_SIMPLE_CELL_ATTRIBUTES.getFgColor(), attributes.getWaveColor(), attributes.getStyle());
/*     */               }
/* 138 */               append(((Pair)obj).getFirst().toString(), attributes);
/*     */             } else {
/* 140 */               super.customizeCellRenderer(tree, obj, selected, expanded, leaf, row, hasFocus);
/*     */             } 
/* 142 */             int count = ((ChainTreeTableModel.Node)value).getMeaningfulChildren();
/* 143 */             boolean partly = ((ChainTreeTableModel.Node)value).isOnlyPartOfChildren();
/* 144 */             if (count > 0) {
/*     */               
/* 146 */               SimpleTextAttributes attributes = selected ? SimpleTextAttributes.REGULAR_ATTRIBUTES : SimpleTextAttributes.GRAYED_ATTRIBUTES;
/* 147 */               append(" (", attributes);
/* 148 */               if (count == 1) {
/* 149 */                 append(NodeJSBundle.message("profile.DistancesInspection.table.single_occurrence.text", new Object[0]), attributes);
/*     */               }
/* 151 */               else if (partly) {
/* 152 */                 append(NodeJSBundle.message("profile.DistancesInspection.table.part_of_found_occurrences.text", new Object[] { Integer.valueOf(count) }), attributes);
/*     */               } else {
/* 154 */                 append(NodeJSBundle.message("profile.DistancesInspection.table.multiple_occurrences.text", new Object[] { Integer.valueOf(count) }), attributes);
/*     */               } 
/*     */               
/* 157 */               append(")", attributes);
/*     */             } 
/*     */           } else {
/*     */             
/* 161 */             super.customizeCellRenderer(tree, value, selected, expanded, leaf, row, hasFocus);
/*     */           } 
/*     */         }
/*     */       };
/* 165 */     renderer.setReverseAsDirect(true);
/* 166 */     RetainersTreeModel.fillColumns(project, columnInfos, reader, (ColoredTreeCellRenderer)renderer);
/* 167 */     return columnInfos;
/*     */   }
/*     */ }


/* Location:              C:\Program Files\JetBrains\WebStorm 2021.1\plugins\NodeJS\lib\NodeJS.jar!\com\jetbrains\nodejs\run\profile\heap\view\components\DistancesInspectionResultsModel.class
 * Java compiler version: 11 (55.0)
 * JD-Core Version:       1.1.3
 */