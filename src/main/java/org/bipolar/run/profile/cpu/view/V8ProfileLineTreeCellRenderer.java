/*     */ package org.bipolar.run.profile.cpu.view;
/*     */ 
/*     */ import com.intellij.openapi.util.Getter;
/*     */ import com.intellij.ui.ColoredTreeCellRenderer;
/*     */ import com.intellij.ui.SimpleTextAttributes;
/*     */ import com.intellij.ui.treeStructure.treetable.TreeTableTree;
/*     */ import com.intellij.util.Processor;
/*     */ import com.intellij.util.ui.StartupUiUtil;
/*     */ import com.intellij.util.ui.UIUtil;
/*     */ import org.bipolar.NodeJSBundle;
/*     */ import org.bipolar.run.profile.cpu.calculation.V8ProfileLine;
/*     */ import org.bipolar.run.profile.cpu.v8log.data.V8CpuLogCall;
/*     */ import org.bipolar.run.profile.heap.view.components.ChainTreeTableModel;
/*     */ import javax.swing.JTree;
/*     */
import org.jetbrains.annotations.Nls;
/*     */ import org.jetbrains.annotations.NotNull;
/*     */ 
/*     */ 
/*     */ 
/*     */ public abstract class V8ProfileLineTreeCellRenderer<T>
/*     */   extends ColoredTreeCellRenderer
/*     */ {
/*     */   private final Processor<? super T> myLineShouldBeMarkedProcessor;
/*     */   private final LineColorProvider myLineColorProvider;
/*     */   private boolean myFocusState;
/*     */   private static final int MAX = 100;
/*     */   
/*     */   public V8ProfileLineTreeCellRenderer(Processor<? super T> getter, LineColorProvider lineColorProvider) {
/*  29 */     this.myLineShouldBeMarkedProcessor = getter;
/*  30 */     this.myLineColorProvider = lineColorProvider;
/*     */   }
/*     */ 
/*     */   
/*     */   protected boolean calcFocusedState() {
/*  35 */     return this.myFocusState;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected boolean shouldDrawBackground() {
/*  42 */     return true;
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
/*  53 */     if (tree == null) $$$reportNull$$$0(0);  boolean tableHasFocus = ((TreeTableTree)tree).getTreeTable().hasFocus();
/*  54 */     this.myFocusState = tableHasFocus;
/*     */     
/*  56 */     V8CpuLogCall call = getCall(value);
/*  57 */     if (call != null) {
/*  58 */       markLineWithIcon(value);
/*  59 */       boolean bold = this.myLineShouldBeMarkedProcessor.process(value);
/*  60 */       mainText(value, selected, tableHasFocus, call, bold);
/*  61 */       execKind(call, selected, tableHasFocus, bold);
/*  62 */       setPaintFocusBorder(false);
/*  63 */     } else if (value instanceof ChainTreeTableModel.Node) {
/*  64 */       Object inner = ((ChainTreeTableModel.Node)value).getT();
/*  65 */       String fragment = inner.toString();
/*  66 */       append(fragment, calculateTextAttributes(selected, tableHasFocus, false, () -> Attributes.local));
/*     */     } else {
/*  68 */       String fragment = value.toString();
/*  69 */       append(fragment, calculateTextAttributes(selected, tableHasFocus, false, () -> Attributes.local));
/*     */     } 
/*  71 */     setPaintFocusBorder(false);
/*  72 */     if (selected)
/*  73 */     { if (tableHasFocus) { setBackground(UIUtil.getTableSelectionBackground(true)); }
/*  74 */       else { setBackground(UIUtil.getListUnfocusedSelectionBackground()); }
/*     */        }
/*  76 */     else { setBackground(UIUtil.getTableBackground()); }
/*     */   
/*     */   }
/*     */   
/*     */   private void execKind(V8CpuLogCall call, boolean selected, boolean tableHasFocus, boolean bold) {
/*  81 */     SimpleTextAttributes additionalAttrs = calculateTextAttributes(selected, tableHasFocus, bold, () -> Attributes.native_);
/*     */     
/*  83 */     if (V8ProfileLine.ExecKind.LazyCompile.equals(call.getExecKind())) {
/*  84 */       append(" (" + NodeJSBundle.message("profile.cpu.record.kind.lazy.text", new Object[0]) + ")", additionalAttrs);
/*  85 */     } else if (!V8ProfileLine.ExecKind.Function.equals(call.getExecKind()) && !V8ProfileLine.ExecKind.unknown.equals(call.getExecKind())) {
/*  86 */       append("(" + call.getExecKind() + ")", additionalAttrs);
/*     */     } 
/*     */   }
/*     */   
/*     */   private void mainText(Object value, boolean selected, boolean tableHasFocus, V8CpuLogCall call, boolean bold) {
/*  91 */     String s = value.toString();
/*  92 */     String text = cutStringIfNeeded(s);
/*  93 */     SimpleTextAttributes correctedAttributes = calculateTextAttributes(selected, tableHasFocus, bold, () -> getAttributes(call.isLocal(), 
/*  94 */           (call.isNative() || call.isNotNavigatable())));
/*  95 */     append(text, correctedAttributes);
/*     */   }
/*     */ 
/*     */   
/*     */   private SimpleTextAttributes calculateTextAttributes(boolean selected, boolean tableHasFocus, boolean bold, Getter<Attributes> attrsGetter) {
/*     */     Attributes attributes;
/* 101 */     if (selected) {
/* 102 */       attributes = (tableHasFocus || StartupUiUtil.isUnderDarcula()) ? Attributes.selected : (Attributes)attrsGetter.get();
/* 103 */       setBackground(UIUtil.getTreeSelectionBackground(tableHasFocus));
/*     */     } else {
/*     */       
/* 106 */       attributes = (Attributes)attrsGetter.get();
/*     */     } 
/* 108 */     return attributes.getAttributes(bold);
/*     */   }
/*     */   
/*     */   @Nls
/*     */   private static String cutStringIfNeeded(@Nls String s) {
/* 113 */     return (s.length() <= 100) ? s : (s.substring(0, 48) + "..." + s.substring(0, 48));
/*     */   }
/*     */   
/*     */   @NotNull
/*     */   public static Attributes getAttributes(boolean isLocalCode, boolean isInternal) {
/*     */     Attributes attributes;
/* 119 */     if (isLocalCode) {
/* 120 */       attributes = Attributes.local;
/*     */     } else {
/*     */       
/* 123 */       attributes = isInternal ? Attributes.native_ : Attributes.library;
/*     */     } 
/* 125 */     if (attributes == null) $$$reportNull$$$0(1);  return attributes;
/*     */   }
/*     */   
/*     */   public enum Attributes {
/* 129 */     selected((String)new SimpleTextAttributes(0, UIUtil.getTableSelectionForeground()), true),
/* 130 */     local((String)SimpleTextAttributes.REGULAR_ATTRIBUTES.derive(-1, UIUtil.getTableForeground(), null, null), false),
/* 131 */     native_((String)SimpleTextAttributes.GRAYED_ATTRIBUTES, false),
/* 132 */     library((String)SimpleTextAttributes.DARK_TEXT, false);
/*     */     
/*     */     private final SimpleTextAttributes myAttributes;
/*     */     private final SimpleTextAttributes myBoldAttributes;
/*     */     
/*     */     Attributes(SimpleTextAttributes attributes, boolean selected) {
/* 138 */       this.myAttributes = attributes;
/* 139 */       this.myBoldAttributes = attributes.derive(1, null, null, null);
/*     */     }
/*     */     
/*     */     public SimpleTextAttributes getAttributes(boolean bold) {
/* 143 */       return bold ? this.myBoldAttributes : this.myAttributes;
/*     */     }
/*     */   }
/*     */   
/*     */   protected void markLineWithIcon(Object object) {
/* 148 */     if (object instanceof V8ProfileLine) {
/* 149 */       V8ProfileLine value = (V8ProfileLine)object;
/* 150 */       if ((!V8ProfileLine.ExecKind.Function.equals(value.getExecKind()) && !V8ProfileLine.ExecKind.LazyCompile.equals(value.getExecKind())) || value
/* 151 */         .getFileDescriptor() == null) {
/*     */         return;
/*     */       }
/*     */       
/* 155 */       setIcon(this.myLineColorProvider.getColorByStringId(value.getCall().getStringId()));
/*     */     } 
/*     */   }
/*     */   
/*     */   protected abstract V8CpuLogCall getCall(Object paramObject);
/*     */ }


/* Location:              C:\Program Files\JetBrains\WebStorm 2021.1\plugins\NodeJS\lib\NodeJS.jar!\com\jetbrains\nodejs\run\profile\cpu\view\V8ProfileLineTreeCellRenderer.class
 * Java compiler version: 11 (55.0)
 * JD-Core Version:       1.1.3
 */