/*     */ package org.bipolar.run.profile.cpu.view;
/*     */ 
/*     */ import com.intellij.openapi.project.Project;
/*     */ import com.intellij.ui.ColoredTableCellRenderer;
/*     */ import com.intellij.ui.JBColor;
/*     */ import com.intellij.ui.components.JBLabel;
/*     */ import com.intellij.util.containers.Convertor;
/*     */ import com.intellij.util.ui.JBUI;
/*     */ import com.intellij.util.ui.UIUtil;
/*     */ import org.bipolar.NodeJSBundle;
/*     */ import org.bipolar.run.profile.V8Utils;
/*     */ import org.bipolar.run.profile.cpu.calculation.V8ProfileLine;
/*     */ import org.bipolar.run.profile.cpu.v8log.data.FlatTopCalls;
/*     */ import org.bipolar.run.profile.cpu.v8log.data.V8CodeScope;
/*     */ import org.bipolar.run.profile.cpu.v8log.data.V8CpuLogCall;
/*     */ import org.bipolar.run.profile.cpu.v8log.reading.V8LogCachingReader;
/*     */ import org.bipolar.run.profile.cpu.v8log.ui.FlameColors;
/*     */ import org.bipolar.run.profile.heap.view.renderers.RightAlignedRenderer;
/*     */ import java.awt.BorderLayout;
/*     */ import java.awt.GridBagConstraints;
/*     */ import java.awt.GridBagLayout;
/*     */ import java.awt.Insets;
/*     */ import java.io.IOException;
/*     */ import java.util.ArrayList;
/*     */ import java.util.Comparator;
/*     */ import java.util.List;
/*     */ import javax.swing.JComponent;
/*     */ import javax.swing.JPanel;
/*     */ import javax.swing.JTable;
/*     */ import javax.swing.table.TableCellRenderer;
/*     */
import org.jetbrains.annotations.Nls;
/*     */ import org.jetbrains.annotations.NotNull;
/*     */ import org.jetbrains.annotations.Nullable;
/*     */ 
/*     */ 
/*     */ 
/*     */ public class TopCallsSelfDiagram
/*     */ {
/*     */   private static final int THRESHOLD = 10;
/*     */   @NotNull
/*     */   private final Project myProject;
/*     */   @NotNull
/*     */   private final V8LogCachingReader myReader;
/*     */   
/*     */   public TopCallsSelfDiagram(@NotNull Project project, @NotNull V8LogCachingReader reader, LineColorProvider fileColor, int height) throws IOException {
/*  46 */     this.myProject = project;
/*  47 */     this.myReader = reader;
/*  48 */     this.myFileColor = fileColor;
/*  49 */     this.myHeight = height;
/*  50 */     this.myLines = new ArrayList<>();
/*     */     
/*  52 */     fillData(reader);
/*  53 */     createUI();
/*     */   }
/*     */   private final List<Piece> myLines; private final LineColorProvider myFileColor; private final int myHeight; private JComponent myComponent;
/*     */   public JComponent getComponent() {
/*  57 */     return this.myComponent;
/*     */   }
/*     */   
/*     */   private void createUI() {
/*  61 */     JPanel panel = new JPanel(new GridBagLayout());
/*  62 */     Convertor<Piece, Integer> dataConvertor = o -> Integer.valueOf(o.getTensPercent());
/*  63 */     Convertor<Piece, JBColor> colorConvertor = o -> o.getColor();
/*  64 */     PieChartDiagram<Piece> pie = (new PieChartDiagram<>(this.myHeight, this.myLines, dataConvertor, colorConvertor)).withHoleCoeff(0.5D);
/*     */ 
/*     */     
/*  67 */     PieChartLegend<Piece> legend = new PieChartLegend<>(this.myLines, dataConvertor, colorConvertor, (TableCellRenderer)new FirstTableCellRenderer(), (TableCellRenderer)(new RightAlignedRenderer()).setDefaultBackground(UIUtil.getPanelBackground()));
/*  68 */     GridBagConstraints gb = new GridBagConstraints();
/*  69 */     gb.anchor = 18;
/*  70 */     gb.fill = 0;
/*  71 */     gb.insets = new Insets(5, 5, 5, 5);
/*  72 */     panel.add(pie, gb);
/*  73 */     gb.gridy++;
/*  74 */     panel.add(legend, gb);
/*     */     
/*  76 */     this.myComponent = new JPanel(new BorderLayout());
/*  77 */     JBLabel title = new JBLabel(NodeJSBundle.message("node.js.v8.cpu.top.calls.overview.description", new Object[0]));
/*  78 */     title.setBorder(JBUI.Borders.empty(3));
/*  79 */     JPanel titleWrapper = createNorthWrapper((JComponent)title);
/*  80 */     titleWrapper.add(createNorthWrapper(panel), "West");
/*  81 */     this.myComponent.add(titleWrapper, "West");
/*     */   }
/*     */   
/*     */   @NotNull
/*     */   private static JPanel createNorthWrapper(JComponent component) {
/*  86 */     JPanel wrapper = new JPanel(new BorderLayout());
/*  87 */     wrapper.add(component, "North");
/*  88 */     if (wrapper == null) $$$reportNull$$$0(2);  return wrapper;
/*     */   }
/*     */   
/*     */   private static class FirstTableCellRenderer
/*     */     extends ColoredTableCellRenderer {
/*     */     protected void customizeCellRenderer(@NotNull JTable table, @Nullable Object value, boolean selected, boolean hasFocus, int row, int column) {
/*  94 */       if (table == null) $$$reportNull$$$0(0);  setBackground(UIUtil.getPanelBackground());
/*  95 */       if (value instanceof TopCallsSelfDiagram.Piece) {
/*  96 */         if (((TopCallsSelfDiagram.Piece)value).getLine() != null) {
/*  97 */           V8CpuLogCall call = ((TopCallsSelfDiagram.Piece)value).getLine().getCall();
/*     */           
/*  99 */           V8ProfileLineTreeCellRenderer.Attributes attributes = V8ProfileLineTreeCellRenderer.getAttributes(call.isLocal(), (call.isNative() || call.isNotNavigatable()));
/* 100 */           append(call.getPresentation(false), attributes.getAttributes(false));
/*     */         } else {
/* 102 */           append(((TopCallsSelfDiagram.Piece)value).getPresentation());
/*     */         } 
/* 104 */       } else if (value != null) {
/* 105 */         String fragment = value.toString();
/* 106 */         append(fragment);
/*     */       } 
/*     */     }
/*     */   }
/*     */   
/*     */   private void fillData(@NotNull V8LogCachingReader reader) throws IOException {
/* 112 */     if (reader == null) $$$reportNull$$$0(3);  long total = reader.getNumTicks();
/*     */     
/* 114 */     int idlePercent = V8Utils.tensPercent(reader.getNumIdleTicks(), total);
/* 115 */     if (idlePercent >= 10) {
/* 116 */       this.myLines.add(new Piece(null, NodeJSBundle.message("profile.top_call_diagram.piece.idle.name", new Object[0]), idlePercent, FlameColors.IDLE_COLOR));
/*     */     }
/* 118 */     int gcPercent = V8Utils.tensPercent(reader.getNumGcTicks(), total);
/* 119 */     if (gcPercent >= 10) {
/* 120 */       this.myLines.add(new Piece(null, NodeJSBundle.message("profile.top_call_diagram.piece.gc.name", new Object[0]), gcPercent, 
/* 121 */             FlameColors.getColor(0L, V8CodeScope.gc)));
/*     */     }
/*     */     
/* 124 */     FlatTopCalls flat = reader.getFlat();
/* 125 */     addLines(flat.getCpp());
/* 126 */     addLines(flat.getJavaScript());
/* 127 */     addLines(flat.getSharedLibraries());
/*     */     
/* 129 */     sortAndAddOther();
/*     */   }
/*     */   
/*     */   private void sortAndAddOther() {
/* 133 */     int total = 0;
/* 134 */     for (Piece line : this.myLines) {
/* 135 */       total += line.getTensPercent();
/*     */     }
/* 137 */     total = 1000 - total;
/* 138 */     if (total > 1) {
/* 139 */       this.myLines.add(new Piece(null, NodeJSBundle.message("profile.top_call_diagram.piece.other.name", new Object[0]), total, FlameColors.OTHER_COLOR));
/*     */     }
/* 141 */     this.myLines.sort(Comparator.comparingInt(Piece::getTensPercent));
/*     */   }
/*     */   
/*     */   private void addLines(List<V8ProfileLine> list) throws IOException {
/* 145 */     for (V8ProfileLine line : list) {
/* 146 */       if (line.getSelfTensPercent() < 10)
/* 147 */         continue;  long id = line.getCall().getStringId();
/* 148 */       Piece piece = new Piece(line, null, line.getSelfTensPercent(), FlameColors.getColor(id, this.myReader.getCodeScopeByStringId(id)));
/* 149 */       this.myLines.add(piece);
/*     */     } 
/*     */   }
/*     */   
/*     */   private static class Piece { @Nullable
/*     */     private final V8ProfileLine myLine;
/*     */     @Nullable
/*     */     @Nls
/*     */     private final String myPresentation;
/*     */     private int myTensPercent;
/*     */     private final JBColor myColor;
/*     */     
/*     */     Piece(@Nullable V8ProfileLine line, @Nls String presentation, int tensPercent, JBColor color) {
/* 162 */       this.myLine = line;
/* 163 */       this.myPresentation = presentation;
/* 164 */       this.myTensPercent = tensPercent;
/* 165 */       this.myColor = color;
/*     */     }
/*     */     
/*     */     @Nullable
/*     */     public V8ProfileLine getLine() {
/* 170 */       return this.myLine;
/*     */     }
/*     */     @Nls
/*     */     public String getPresentation() {
/* 174 */       return this.myPresentation;
/*     */     }
/*     */     
/*     */     public int getTensPercent() {
/* 178 */       return this.myTensPercent;
/*     */     }
/*     */     
/*     */     public void setTensPercent(int tensPercent) {
/* 182 */       this.myTensPercent = tensPercent;
/*     */     }
/*     */     
/*     */     public JBColor getColor() {
/* 186 */       return this.myColor;
/*     */     } }
/*     */ 
/*     */ }


/* Location:              C:\Program Files\JetBrains\WebStorm 2021.1\plugins\NodeJS\lib\NodeJS.jar!\com\jetbrains\nodejs\run\profile\cpu\view\TopCallsSelfDiagram.class
 * Java compiler version: 11 (55.0)
 * JD-Core Version:       1.1.3
 */