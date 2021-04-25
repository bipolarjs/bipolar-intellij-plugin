/*    */ package org.bipolar.util.ui;
/*    */ import com.intellij.openapi.ui.ComboBox;
/*    */ import com.intellij.openapi.util.NlsContexts.Label;
/*    */ import com.intellij.ui.SimpleListCellRenderer;
/*    */ import com.intellij.ui.components.JBLabel;
/*    */ import com.intellij.util.ui.JBUI;
/*    */ import com.intellij.util.ui.UIUtil;
/*    */ import org.bipolar.NodeJSBundle;
/*    */ import java.awt.Component;
/*    */ import java.awt.FlowLayout;
/*    */ import java.awt.GridBagConstraints;
/*    */ import java.awt.Insets;
/*    */ import java.util.Arrays;
/*    */ import javax.swing.Box;
/*    */ import javax.swing.JPanel;
/*    */ import javax.swing.ListCellRenderer;
/*    */ import org.jetbrains.annotations.Nls;
/*    */ import org.jetbrains.annotations.NotNull;
/*    */ 
/*    */ public final class UIHelper {
/*    */   @NotNull
/*    */   public static JPanel createGrowingDownPanelWithHorizontallyStretchedComponent(@NotNull Component upperComponent) {
/* 23 */     if (upperComponent == null) $$$reportNull$$$0(0);  JPanel panel = new JPanel(new GridBagLayout());
/* 24 */     panel.add(upperComponent, new GridBagConstraints(0, 0, 1, 1, 1.0D, 0.0D, 18, 2, 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */           
/* 30 */           (Insets)JBUI.emptyInsets(), 0, 0));
/*    */ 
/*    */     
/* 33 */     JPanel empty = createEmptyPanel();
/* 34 */     panel.add(empty, new GridBagConstraints(0, 1, 1, 1, 1.0D, 1.0D, 18, 1, 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */           
/* 40 */           (Insets)JBUI.emptyInsets(), 0, 0));
/*    */ 
/*    */     
/* 43 */     if (panel == null) $$$reportNull$$$0(1);  return panel;
/*    */   }
/*    */   
/*    */   @NotNull
/*    */   private static JPanel createEmptyPanel() {
/* 48 */     return new JPanel(new FlowLayout(0, 0, 0));
/*    */   }
/*    */ 
/*    */   
/*    */   @NotNull
/*    */   public static ComboBox<WithDisplayName> createCombobox(@NotNull @Nls final String prototypeDisplayValue, @NotNull WithDisplayName displayNameWhenListIsEmpty) {
/* 54 */     if (prototypeDisplayValue == null) $$$reportNull$$$0(2);  if (displayNameWhenListIsEmpty == null) $$$reportNull$$$0(3);  ComboBox<WithDisplayName> comboBox = new ComboBox();
/* 55 */     comboBox.setPrototypeDisplayValue(new WithDisplayName()
/*    */         {
/*    */           public String getDisplayName() {
/* 58 */             return prototypeDisplayValue;
/*    */           }
/*    */         });
/* 61 */     comboBox.setRenderer((ListCellRenderer)SimpleListCellRenderer.create(displayNameWhenListIsEmpty.getDisplayName(), WithDisplayName::getDisplayName));
/* 62 */     if (comboBox == null) $$$reportNull$$$0(4);  return comboBox;
/*    */   }
/*    */   
/*    */   @NotNull
/*    */   public static <E extends WithDisplayName> ComboBox<E> createCombobox(@NotNull @Label String displayNameForEmptyList) {
/* 67 */     if (displayNameForEmptyList == null) $$$reportNull$$$0(5);  ComboBox<E> comboBox = new ComboBox();
/* 68 */     comboBox.setRenderer((ListCellRenderer)SimpleListCellRenderer.create(displayNameForEmptyList, value -> {
/*    */             String text = value.getDisplayName();
/*    */             if (text.length() < displayNameForEmptyList.length()) {
/*    */               char[] a = new char[2 * (displayNameForEmptyList.length() - text.length())];
/*    */               Arrays.fill(a, ' ');
/*    */               text = text + text;
/*    */             } 
/*    */             return text;
/*    */           }));
/* 77 */     if (comboBox == null) $$$reportNull$$$0(6);  return comboBox;
/*    */   }
/*    */   
/*    */   @NotNull
/*    */   public static JPanel wrapInCenteredPanel(@NotNull @Label String text) {
/* 82 */     if (text == null) $$$reportNull$$$0(7);  JBLabel comp = new JBLabel(text);
/* 83 */     comp.setForeground(UIUtil.getInactiveTextColor());
/*    */     
/* 85 */     JPanel listPane = new JPanel();
/* 86 */     listPane.setLayout(new BoxLayout(listPane, 0));
/*    */     
/* 88 */     listPane.add(Box.createHorizontalGlue());
/* 89 */     listPane.add((Component)comp);
/* 90 */     listPane.setBackground(UIUtil.getTableBackground());
/* 91 */     listPane.add(Box.createHorizontalGlue());
/* 92 */     if (listPane == null) $$$reportNull$$$0(8);  return listPane;
/*    */   } @NotNull
/*    */   @Nls
/*    */   public static String getUnavailableText() {
/* 96 */     if (NodeJSBundle.message("node.combobox.unavailable.text", new Object[0]) == null) $$$reportNull$$$0(9);  return NodeJSBundle.message("node.combobox.unavailable.text", new Object[0]);
/*    */   }
/*    */ }


/* Location:              C:\Program Files\JetBrains\WebStorm 2021.1\plugins\NodeJS\lib\NodeJS.jar!\com\jetbrains\nodej\\uti\\ui\UIHelper.class
 * Java compiler version: 11 (55.0)
 * JD-Core Version:       1.1.3
 */