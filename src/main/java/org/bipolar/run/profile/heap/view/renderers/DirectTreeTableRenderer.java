/*     */ package org.bipolar.run.profile.heap.view.renderers;
/*     */ 
/*     */ import com.intellij.openapi.editor.DefaultLanguageHighlighterColors;
/*     */ import com.intellij.openapi.editor.colors.EditorColorsManager;
/*     */ import com.intellij.openapi.editor.colors.EditorColorsScheme;
/*     */ import com.intellij.openapi.editor.markup.TextAttributes;
/*     */ import com.intellij.openapi.project.Project;
/*     */ import com.intellij.openapi.util.Pair;
/*     */ import com.intellij.openapi.util.text.StringUtil;
/*     */ import com.intellij.ui.ColoredTreeCellRenderer;
/*     */ import com.intellij.ui.ExpandableItemsHandler;
/*     */ import com.intellij.ui.JBColor;
/*     */ import com.intellij.ui.SimpleTextAttributes;
/*     */ import com.intellij.ui.TableCell;
/*     */ import com.intellij.ui.treeStructure.treetable.TreeTable;
/*     */ import com.intellij.ui.treeStructure.treetable.TreeTableTree;
/*     */ import com.intellij.util.MathUtil;
/*     */ import com.intellij.util.ui.StartupUiUtil;
/*     */ import com.intellij.util.ui.UIUtil;
/*     */ import org.bipolar.NodeJSBundle;
/*     */ import org.bipolar.run.profile.V8Utils;
/*     */ import org.bipolar.run.profile.heap.V8CachingReader;
/*     */ import org.bipolar.run.profile.heap.data.Aggregate;
/*     */ import org.bipolar.run.profile.heap.data.V8HeapEntry;
/*     */ import org.bipolar.run.profile.heap.data.V8HeapNodeType;
/*     */ import org.bipolar.run.profile.heap.view.components.ChainTreeTableModel;
/*     */ import org.bipolar.run.profile.heap.view.components.V8HeapComponent;
/*     */ import org.bipolar.run.profile.heap.view.models.V8HeapContainmentTreeTableModel;
/*     */ import org.bipolar.run.profile.heap.view.nodes.FixedRetainerNode;
/*     */ import java.awt.Color;
/*     */ import java.awt.Font;
/*     */ import java.util.ArrayList;
/*     */ import java.util.Collection;
/*     */ import java.util.List;
/*     */ import javax.swing.JTree;
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
/*     */ public class DirectTreeTableRenderer
/*     */   extends ColoredTreeCellRenderer
/*     */ {
/*     */   public static final int WINDOW_LINK_LIMIT = 60;
/*  56 */   private final SimpleTextAttributes myBlackTextAttributes = SimpleTextAttributes.REGULAR_ATTRIBUTES
/*  57 */     .derive(-1, UIUtil.getTableForeground(), null, null);
/*  58 */   private final SimpleTextAttributes mySelectionAttributes = SimpleTextAttributes.REGULAR_ATTRIBUTES
/*  59 */     .derive(-1, UIUtil.getTableSelectionForeground(true), null, null);
/*     */   private final V8HeapComponent myHeapComponent;
/*     */   @NotNull
/*     */   protected V8CachingReader myReader;
/*     */   private final SimpleTextAttributes myObjBackAtt;
/*     */   private final SimpleTextAttributes myCodeAtt;
/*     */   private final SimpleTextAttributes myClosureAtt;
/*     */   private final SimpleTextAttributes myRegExpAtt;
/*     */   private final SimpleTextAttributes mySyntheticAtt;
/*     */   private final SimpleTextAttributes myPropAtt;
/*     */   private final EditorColorsScheme myScheme;
/*     */   private boolean myReverseAsDirect;
/*  71 */   private int myMaxWidth = -1;
/*  72 */   private int myMinWidth = -1;
/*     */   private boolean myFocusState;
/*     */   
/*     */   public DirectTreeTableRenderer(@NotNull Project project, @NotNull V8CachingReader reader) {
/*  76 */     this.myReader = reader;
/*  77 */     this.myScheme = EditorColorsManager.getInstance().getGlobalScheme();
/*     */     
/*  79 */     SimpleTextAttributes keyword = convert(this.myScheme.getAttributes(DefaultLanguageHighlighterColors.KEYWORD));
/*  80 */     this.myObjBackAtt = keyword;
/*     */     
/*  82 */     this.myCodeAtt = new SimpleTextAttributes(0, null, (Color)new JBColor(new Color(45, 250, 240), new Color(45, 250, 240)));
/*  83 */     this.myClosureAtt = new SimpleTextAttributes(0, (Color)new JBColor(keyword.getFgColor(), keyword.getFgColor()));
/*  84 */     this.myRegExpAtt = new SimpleTextAttributes(0, null, (Color)new JBColor(new Color(120, 250, 190, 57), new Color(120, 250, 190, 57)));
/*  85 */     this.mySyntheticAtt = new SimpleTextAttributes(1, UIUtil.getInactiveTextColor());
/*  86 */     this.myPropAtt = convert(this.myScheme.getAttributes(DefaultLanguageHighlighterColors.INSTANCE_FIELD));
/*  87 */     this.myHeapComponent = V8HeapComponent.getInstance(project);
/*     */   }
/*     */ 
/*     */   
/*     */   protected boolean calcFocusedState() {
/*  92 */     return this.myFocusState;
/*     */   }
/*     */ 
/*     */   
/*     */   protected boolean shouldDrawBackground() {
/*  97 */     return true;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void customizeCellRenderer(@NotNull JTree tree, Object value, boolean selected, boolean expanded, boolean leaf, int row, boolean hasFocus) {
/* 108 */     if (tree == null) $$$reportNull$$$0(2);  setPaintFocusBorder(false);
/* 109 */     int width = getWidth(tree, row);
/* 110 */     boolean lineHasFocus = ((TreeTableTree)tree).getTreeTable().hasFocus();
/* 111 */     this.myFocusState = lineHasFocus;
/* 112 */     setBackground((selected && !lineHasFocus) ? UIUtil.getTreeUnfocusedSelectionBackground() : null);
/* 113 */     boolean selectedForeground = (selected && (lineHasFocus || StartupUiUtil.isUnderDarcula()));
/* 114 */     if (value instanceof ChainTreeTableModel.Node) {
/* 115 */       value = ((ChainTreeTableModel.Node)value).getT();
/*     */     }
/* 117 */     SimpleTextAttributes defaultAttrs = selectedForeground ? this.mySelectionAttributes : this.myBlackTextAttributes;
/* 118 */     List<PartRenderer> list = new ArrayList<>();
/* 119 */     if (value instanceof FixedRetainerNode) {
/* 120 */       FixedRetainerNode entry = (FixedRetainerNode)value;
/* 121 */       if (entry.isUnreachable()) {
/* 122 */         list.add(new FixedTextRenderer(NodeJSBundle.message("profile.table.unreachable_or_hidden.text", new Object[0]), defaultAttrs));
/*     */       }
/* 124 */       appendMarkRenderer((V8HeapContainmentTreeTableModel.NamedEntry)entry, list);
/* 125 */       appendDetachedRenderer(list, entry.getEntry(), selected, lineHasFocus);
/* 126 */       appendReachableFromWindowRenderer(list, entry.getEntry(), selected, lineHasFocus);
/* 127 */       appendLinkRenderer((V8HeapContainmentTreeTableModel.NamedEntry)entry, list, tree, row);
/* 128 */       if (this.myReverseAsDirect) {
/* 129 */         list.add(new FixedTextRenderer(" : : ", defaultAttrs));
/* 130 */         appendObjectTypeRenderer(entry.getEntry(), entry.getName(), list);
/* 131 */         appendObjectIdRenderer(entry.getEntry(), list);
/* 132 */       } else if (entry.getParent() != null) {
/* 133 */         list.add(new FixedTextRenderer(NodeJSBundle.message("profile.table.in.text", new Object[0]), defaultAttrs));
/* 134 */         appendObjectTypeRenderer(entry.getParent(), entry.getParentName(), list);
/* 135 */         appendObjectIdRenderer(entry.getParent(), list);
/*     */       } 
/* 137 */       adaptiveRendering(list, width, selectedForeground);
/* 138 */     } else if (value instanceof V8HeapContainmentTreeTableModel.NamedEntry) {
/* 139 */       V8HeapContainmentTreeTableModel.NamedEntry entry = (V8HeapContainmentTreeTableModel.NamedEntry)value;
/* 140 */       appendMarkRenderer(entry, list);
/* 141 */       appendDetachedRenderer(list, entry.getEntry(), selected, lineHasFocus);
/* 142 */       appendReachableFromWindowRenderer(list, entry.getEntry(), selected, lineHasFocus);
/* 143 */       if (!entry.isDoNotShowLink()) {
/* 144 */         appendLinkRenderer(entry, list, tree, row);
/* 145 */         list.add(new FixedTextRenderer(" : : ", defaultAttrs));
/*     */       } 
/* 147 */       appendObjectTypeRenderer(entry.getEntry(), entry.getName(), list);
/* 148 */       appendObjectIdRenderer(entry.getEntry(), list);
/* 149 */       adaptiveRendering(list, width, selectedForeground);
/* 150 */     } else if (value instanceof Aggregate) {
/* 151 */       appendAggregateNameRenderer((Aggregate)value, ((Aggregate)value).getPresentation(this.myReader), list, SimpleTextAttributes.REGULAR_ATTRIBUTES);
/* 152 */       adaptiveRendering(list, width, selectedForeground);
/*     */     } else {
/* 154 */       String fragment = value.toString();
/* 155 */       append(fragment, defaultAttrs);
/*     */     } 
/*     */   }
/*     */   
/* 159 */   private static final JBColor detached = new JBColor(new Color(255, 210, 210), new Color(132, 66, 66));
/* 160 */   private static final JBColor detachedUnfocusedSelected = new JBColor(new Color(234, 191, 191), new Color(84, 75, 124));
/* 161 */   private static final JBColor detachedSelected = new JBColor(new Color(120, 159, 196), new Color(84, 75, 124));
/*     */   
/* 163 */   private static final JBColor accessibleFromWindow = new JBColor(new Color(255, 254, 220), new Color(99, 98, 78));
/* 164 */   private static final JBColor accessibleFromWindowUnfocusedSelected = new JBColor(new Color(233, 232, 198), new Color(47, 77, 110));
/* 165 */   private static final JBColor accessibleFromWindowSelected = new JBColor(new Color(104, 165, 255), new Color(94, 129, 140));
/*     */   
/*     */   private void appendDetachedRenderer(List<PartRenderer> list, V8HeapEntry entry, boolean selected, boolean focused) {
/* 168 */     if (this.myReader.isDetached((int)entry.getId())) {
/* 169 */       setBackground(selected ? (focused ? (Color)detachedSelected : (Color)detachedUnfocusedSelected) : (Color)detached);
/*     */     }
/*     */   }
/*     */ 
/*     */   
/*     */   private void appendReachableFromWindowRenderer(List<PartRenderer> list, V8HeapEntry entry, boolean selected, boolean focused) {
/* 175 */     if (this.myReader.isReachableFromWindow(entry))
/* 176 */       setBackground(selected ? (focused ? (Color)accessibleFromWindowSelected : (Color)accessibleFromWindowUnfocusedSelected) : (Color)accessibleFromWindow); 
/*     */   }
/*     */   
/*     */   protected int getWidth(@NotNull JTree tree, int row) {
/*     */     int width;
/* 181 */     if (tree == null) $$$reportNull$$$0(3);  if (this.myMaxWidth < 0) {
/* 182 */       this.myMinWidth = getFontMetrics(getFont()).stringWidth("compareDocument :: compareDocument @114509");
/* 183 */       this.myMaxWidth = 2 * this.myMinWidth;
/*     */     } 
/*     */     
/* 186 */     if (tree instanceof TreeTableTree) {
/* 187 */       int rowX = V8Utils.getTableRowX(tree, row);
/* 188 */       TreeTable table = ((TreeTableTree)tree).getTreeTable();
/* 189 */       width = table.getColumnModel().getColumn(0).getWidth() - rowX;
/*     */     } else {
/* 191 */       width = tree.getWidth();
/*     */     } 
/* 193 */     return MathUtil.clamp(width, this.myMinWidth, this.myMaxWidth);
/*     */   }
/*     */   
/*     */   protected void adaptiveRendering(List<PartRenderer> renderers, int totalWidth, boolean selected) {
/* 197 */     int width = 0;
/* 198 */     int nonCutWidth = 0;
/* 199 */     int cutParts = 0;
/* 200 */     List<Pair<Integer, Integer>> textParams = new ArrayList<>();
/*     */     
/* 202 */     for (PartRenderer renderer : renderers) {
/* 203 */       String text = renderer.getFullText();
/* 204 */       SimpleTextAttributes attributes = getAttributesFromRenderer(selected, renderer);
/* 205 */       Font font = getFont().deriveFont(attributes.getFontStyle());
/* 206 */       int w = getFontMetrics(font).stringWidth(text);
/* 207 */       width += w;
/* 208 */       if (renderer.canBeCut()) {
/* 209 */         cutParts++;
/* 210 */         textParams.add(Pair.create(Integer.valueOf(text.length()), Integer.valueOf(w)));
/*     */         continue;
/*     */       } 
/* 213 */       textParams.add(Pair.create(Integer.valueOf(-1), Integer.valueOf(-1)));
/* 214 */       nonCutWidth += w;
/*     */     } 
/*     */ 
/*     */     
/* 218 */     if (width <= totalWidth || cutParts == 0) {
/* 219 */       renderFullTexts(renderers, selected);
/*     */       
/*     */       return;
/*     */     } 
/* 223 */     int partWidth = Math.max(50, totalWidth - nonCutWidth) / cutParts;
/* 224 */     int leftParts = 0;
/* 225 */     int newNonCutWidth = nonCutWidth;
/* 226 */     for (int i = 0; i < renderers.size(); i++) {
/* 227 */       PartRenderer renderer = renderers.get(i);
/* 228 */       if (renderer.canBeCut()) {
/* 229 */         if (((Integer)((Pair)textParams.get(i)).getSecond()).intValue() <= partWidth) {
/* 230 */           newNonCutWidth += ((Integer)((Pair)textParams.get(i)).getSecond()).intValue();
/* 231 */           textParams.set(i, Pair.create(Integer.valueOf(-1), Integer.valueOf(-1)));
/*     */         } else {
/* 233 */           leftParts++;
/*     */         } 
/*     */       }
/*     */     } 
/*     */     
/* 238 */     if (leftParts == 0) {
/* 239 */       renderFullTexts(renderers, selected);
/*     */       
/*     */       return;
/*     */     } 
/* 243 */     int cutWidth = 0;
/* 244 */     partWidth = Math.max(50, totalWidth - newNonCutWidth) / leftParts;
/* 245 */     for (int j = 0; j < renderers.size(); j++) {
/* 246 */       PartRenderer renderer = renderers.get(j);
/* 247 */       SimpleTextAttributes attributes = getAttributesFromRenderer(selected, renderer);
/* 248 */       if (renderer.canBeCut() && ((Integer)((Pair)textParams.get(j)).getFirst()).intValue() > 0) {
/* 249 */         Pair<Integer, Integer> params = textParams.get(j);
/* 250 */         int numSymb = (int)Math.floor(partWidth / ((Integer)params.getSecond()).intValue() / ((Integer)params.getFirst()).intValue()) - 1;
/* 251 */         numSymb = Math.max(8, numSymb);
/* 252 */         String cutText = renderer.getCutText(numSymb);
/* 253 */         append(cutText, attributes);
/*     */         
/* 255 */         Font font = getFont().deriveFont(attributes.getFontStyle());
/* 256 */         int w = getFontMetrics(font).stringWidth(cutText);
/* 257 */         cutWidth += w;
/* 258 */         leftParts--;
/* 259 */         partWidth = (leftParts == 0) ? 0 : ((totalWidth - nonCutWidth - cutWidth) / leftParts);
/*     */       } else {
/* 261 */         append(renderer.getFullText(), attributes);
/*     */       } 
/*     */     } 
/*     */   }
/*     */   
/*     */   private void renderFullTexts(List<PartRenderer> renderers, boolean selected) {
/* 267 */     for (PartRenderer renderer : renderers) {
/* 268 */       SimpleTextAttributes attributes = getAttributesFromRenderer(selected, renderer);
/* 269 */       append(renderer.getFullText(), attributes);
/*     */     } 
/*     */   }
/*     */   
/*     */   private SimpleTextAttributes getAttributesFromRenderer(boolean selected, PartRenderer renderer) {
/* 274 */     SimpleTextAttributes attributes = selected ? renderer.getSelectedAttributes() : renderer.getAttributes();
/* 275 */     if (attributes == null) {
/* 276 */       attributes = selected ? this.mySelectionAttributes : this.myBlackTextAttributes;
/*     */     }
/* 278 */     return attributes;
/*     */   }
/*     */   protected static interface PartRenderer {
/*     */     SimpleTextAttributes getAttributes();
/*     */     SimpleTextAttributes getSelectedAttributes();
/*     */     
/*     */     @Nls
/*     */     String getFullText();
/*     */     
/*     */     @Nls
/*     */     String getCutText(int param1Int);
/*     */     
/*     */     boolean canBeCut(); }
/*     */   
/*     */   private static class FixedTextRenderer implements PartRenderer { private final SimpleTextAttributes myAttrs;
/*     */     
/*     */     private FixedTextRenderer(@Nls String text, SimpleTextAttributes attrs) {
/* 295 */       this.myText = text;
/* 296 */       this.myAttrs = attrs;
/* 297 */       this.mySelectedAttrs = this.myAttrs.derive(attrs.getStyle(), UIUtil.getTreeSelectionForeground(), attrs.getBgColor(), attrs.getWaveColor());
/*     */     }
/*     */     private final SimpleTextAttributes mySelectedAttrs; @Nls
/*     */     protected final String myText;
/*     */     public SimpleTextAttributes getAttributes() {
/* 302 */       return this.myAttrs;
/*     */     }
/*     */ 
/*     */     
/*     */     public SimpleTextAttributes getSelectedAttributes() {
/* 307 */       return this.mySelectedAttrs;
/*     */     }
/*     */ 
/*     */     
/*     */     public String getFullText() {
/* 312 */       return this.myText;
/*     */     }
/*     */ 
/*     */     
/*     */     public String getCutText(int maxSymbols) {
/* 317 */       return this.myText;
/*     */     }
/*     */ 
/*     */     
/*     */     public boolean canBeCut() {
/* 322 */       return false;
/*     */     } }
/*     */ 
/*     */   
/*     */   private static class SimpleCuttableRenderer extends FixedTextRenderer {
/*     */     SimpleCuttableRenderer(@Nls String text, SimpleTextAttributes attrs) {
/* 328 */       super(text, attrs);
/*     */     }
/*     */ 
/*     */     
/*     */     public String getCutText(int maxSymbols) {
/* 333 */       if (this.myText.length() <= maxSymbols) return this.myText; 
/* 334 */       return this.myText.substring(0, maxSymbols - 3) + "...";
/*     */     }
/*     */ 
/*     */     
/*     */     public boolean canBeCut() {
/* 339 */       return true;
/*     */     }
/*     */   }
/*     */   
/*     */   private static final class MarkRenderer extends FixedTextRenderer {
/*     */     private MarkRenderer(@Nls String markText, SimpleTextAttributes attrs) {
/* 345 */       super(markText, attrs);
/*     */     }
/*     */ 
/*     */     
/*     */     public String getFullText() {
/* 350 */       return "[" + super.getFullText() + "]";
/*     */     }
/*     */ 
/*     */     
/*     */     public String getCutText(int maxSymbols) {
/* 355 */       if (this.myText.length() + 2 <= maxSymbols) return getFullText(); 
/* 356 */       return "[" + this.myText.substring(0, maxSymbols - 5) + "...]";
/*     */     }
/*     */ 
/*     */     
/*     */     public boolean canBeCut() {
/* 361 */       return true;
/*     */     }
/*     */   }
/*     */   
/*     */   private void appendMarkRenderer(V8HeapContainmentTreeTableModel.NamedEntry entry, List<PartRenderer> list) {
/* 366 */     String mark = this.myHeapComponent.getMark(this.myReader.getDigest(), entry.getEntry());
/* 367 */     if (!StringUtil.isEmptyOrSpaces(mark)) {
/* 368 */       list.add(new MarkRenderer(mark, SimpleTextAttributes.ERROR_ATTRIBUTES));
/*     */     }
/*     */   }
/*     */   
/*     */   private static void appendObjectIdRenderer(V8HeapEntry entry, List<PartRenderer> list) {
/* 373 */     if (entry.getId() == 0L) {
/* 374 */       list.add(new FixedTextRenderer(NodeJSBundle.message("profile.table.root.text", new Object[0]), SimpleTextAttributes.REGULAR_ATTRIBUTES));
/*     */     } else {
/* 376 */       list.add(new FixedTextRenderer(" @" + entry.getSnapshotObjectId(), SimpleTextAttributes.GRAYED_ATTRIBUTES));
/*     */     } 
/*     */   }
/*     */   
/*     */   private static class LinkRenderer extends SimpleCuttableRenderer {
/*     */     private final TreeTableTree myTree;
/*     */     private final int myRow;
/*     */     
/*     */     LinkRenderer(@Nls String text, SimpleTextAttributes attrs, JTree tree, int row) {
/* 385 */       super(text, attrs);
/* 386 */       this.myRow = row;
/* 387 */       this.myTree = (tree instanceof TreeTableTree) ? (TreeTableTree)tree : null;
/*     */     }
/*     */ 
/*     */     
/*     */     public boolean canBeCut() {
/* 392 */       if (this.myTree != null) {
/* 393 */         boolean isExpanded = false;
/* 394 */         TreeTable table = this.myTree.getTreeTable();
/* 395 */         ExpandableItemsHandler<TableCell> handler = table.getExpandableItemsHandler();
/* 396 */         if (handler.isEnabled() && !handler.getExpandedItems().isEmpty()) {
/* 397 */           Collection<TableCell> items = handler.getExpandedItems();
/* 398 */           for (TableCell item : items) {
/* 399 */             if (item.row == this.myRow && item.column == 0) {
/* 400 */               return false;
/*     */             }
/*     */           } 
/*     */         } 
/*     */       } 
/* 405 */       return true;
/*     */     }
/*     */   }
/*     */   
/*     */   private void appendLinkRenderer(V8HeapContainmentTreeTableModel.NamedEntry entry, List<PartRenderer> list, JTree tree, int row) {
/* 410 */     String presentation = entry.getLinkPresentation();
/*     */ 
/*     */     
/* 413 */     SimpleTextAttributes linkAtt = entry.isLinkHidden() ? SimpleTextAttributes.GRAYED_ATTRIBUTES : (entry.isProperty() ? this.myPropAtt : this.myBlackTextAttributes);
/* 414 */     list.add(new LinkRenderer(presentation, linkAtt, tree, row));
/*     */   }
/*     */   
/*     */   private void appendObjectTypeRenderer(V8HeapEntry entry, @Nls String name, List<PartRenderer> list) {
/* 418 */     SimpleTextAttributes att = getAtt(entry.getType());
/* 419 */     if (V8HeapNodeType.kClosure.equals(entry.getType())) {
/* 420 */       list.add(new FixedTextRenderer(NodeJSBundle.message("profile.table.named_function.text", new Object[] { name }), att));
/*     */       return;
/*     */     } 
/* 423 */     if (V8HeapNodeType.kArray.equals(entry.getType())) {
/* 424 */       list.add(new ArrayTypeRenderer(name, att));
/*     */       return;
/*     */     } 
/* 427 */     if (V8HeapNodeType.kObject.equals(entry.getType()) && name.startsWith("Window")) {
/* 428 */       list.add(new WindowRenderer(name, att));
/*     */       return;
/*     */     } 
/* 431 */     list.add(new SimpleCuttableRenderer(name, att));
/*     */   }
/*     */   
/*     */   protected void appendAggregateNameRenderer(Aggregate aggregate, @Nls String presentation, List<PartRenderer> list, SimpleTextAttributes att) {
/* 435 */     if (V8HeapNodeType.kObject.equals(aggregate.getType()) && presentation.startsWith("Window")) {
/* 436 */       list.add(new WindowRenderer(presentation, att));
/*     */       return;
/*     */     } 
/* 439 */     list.add(new SimpleCuttableRenderer(presentation, att));
/*     */   }
/*     */   
/*     */   private static class ArrayTypeRenderer extends SimpleCuttableRenderer {
/*     */     ArrayTypeRenderer(@Nls String text, SimpleTextAttributes attrs) {
/* 444 */       super(text, attrs);
/*     */     }
/*     */ 
/*     */     
/*     */     public String getFullText() {
/* 449 */       return super.getFullText() + "[]";
/*     */     }
/*     */ 
/*     */     
/*     */     public String getCutText(int maxSymbols) {
/* 454 */       String text = super.getCutText(maxSymbols - 2);
/* 455 */       return text + "[]";
/*     */     }
/*     */   }
/*     */   
/*     */   private static class WindowRenderer extends FixedTextRenderer {
/*     */     WindowRenderer(@Nls String text, SimpleTextAttributes attrs) {
/* 461 */       super(text, attrs);
/*     */     }
/*     */ 
/*     */     
/*     */     public String getCutText(int maxSymbols) {
/* 466 */       if (this.myText.length() <= maxSymbols) return this.myText; 
/* 467 */       int idx1 = this.myText.indexOf("/");
/* 468 */       if (idx1 <= 0) return this.myText;
/*     */       
/* 470 */       String fragment = this.myText.substring(idx1 + 1).trim();
/* 471 */       int protocolIdx = fragment.indexOf("://");
/* 472 */       if (protocolIdx > 0) {
/* 473 */         fragment = fragment.substring(protocolIdx + 3).trim();
/*     */       }
/* 475 */       if (fragment.length() > maxSymbols) {
/* 476 */         int firstPart = maxSymbols / 2;
/*     */         
/* 478 */         String cut = fragment.substring(0, firstPart) + "..." + fragment.substring(0, firstPart);
/* 479 */         fragment = cut;
/*     */       } 
/* 481 */       return NodeJSBundle.message("profile.table.window_fragment", new Object[] { fragment });
/*     */     }
/*     */ 
/*     */     
/*     */     public boolean canBeCut() {
/* 486 */       return true;
/*     */     }
/*     */   }
/*     */   
/*     */   private SimpleTextAttributes getAtt(@NotNull V8HeapNodeType type) {
/* 491 */     if (type == null) $$$reportNull$$$0(4);  switch (type) { case kHidden:
/* 492 */         return SimpleTextAttributes.GRAY_ATTRIBUTES;
/* 493 */       case kNative: return SimpleTextAttributes.GRAY_ITALIC_ATTRIBUTES;
/* 494 */       case kSynthetic: return this.mySyntheticAtt;
/*     */       case kArray:
/*     */       case kHeapNumber:
/*     */       case kSymbol:
/* 498 */         return this.myBlackTextAttributes;
/* 499 */       case kString: case kConsString: case kSlicedString: return convert(this.myScheme.getAttributes(DefaultLanguageHighlighterColors.STRING));
/* 500 */       case kObject: return this.myBlackTextAttributes;
/* 501 */       case kCode: return this.myCodeAtt;
/* 502 */       case kClosure: return this.myClosureAtt;
/* 503 */       case kRegExp: return this.myRegExpAtt; }
/* 504 */      return this.myBlackTextAttributes;
/*     */   }
/*     */ 
/*     */   
/*     */   private SimpleTextAttributes convert(TextAttributes ta) {
/* 509 */     return new SimpleTextAttributes(ta.getBackgroundColor(), ta.getForegroundColor(), ta.getEffectColor(), ta.getFontType());
/*     */   }
/*     */   
/*     */   public void setReverseAsDirect(boolean reverseAsDirect) {
/* 513 */     this.myReverseAsDirect = reverseAsDirect;
/*     */   }
/*     */ }


/* Location:              C:\Program Files\JetBrains\WebStorm 2021.1\plugins\NodeJS\lib\NodeJS.jar!\com\jetbrains\nodejs\run\profile\heap\view\renderers\DirectTreeTableRenderer.class
 * Java compiler version: 11 (55.0)
 * JD-Core Version:       1.1.3
 */