/*    */ package org.bipolar.run.profile.heap.view.components;
/*    */ 
/*    */ import com.intellij.openapi.actionSystem.DataProvider;
/*    */ import com.intellij.openapi.util.Getter;
/*    */ import com.intellij.ui.components.JBPanel;
/*    */ import java.awt.BorderLayout;
/*    */ import java.awt.LayoutManager;
/*    */ import java.util.HashMap;
/*    */ import java.util.Map;
/*    */ import javax.swing.JComponent;
/*    */ import org.jetbrains.annotations.NonNls;
/*    */ import org.jetbrains.annotations.NotNull;
/*    */ import org.jetbrains.annotations.Nullable;
/*    */ 
/*    */ public class DataProviderPanel
/*    */   extends JBPanel
/*    */   implements DataProvider
/*    */ {
/*    */   private final Map<String, Getter<Object>> myMap;
/*    */   
/*    */   public DataProviderPanel(LayoutManager layout) {
/* 22 */     super(layout);
/* 23 */     this.myMap = new HashMap<>();
/*    */   }
/*    */   
/*    */   public static DataProviderPanel wrap(@NotNull JComponent component) {
/* 27 */     if (component == null) $$$reportNull$$$0(0);  DataProviderPanel panel = new DataProviderPanel(new BorderLayout());
/* 28 */     panel.add(component, "Center");
/* 29 */     return panel;
/*    */   }
/*    */   
/*    */   public void register(@NotNull String key, Getter<Object> getter) {
/* 33 */     if (key == null) $$$reportNull$$$0(1);  this.myMap.put(key, getter);
/*    */   }
/*    */ 
/*    */   
/*    */   @Nullable
/*    */   public Object getData(@NotNull @NonNls String dataId) {
/* 39 */     if (dataId == null) $$$reportNull$$$0(2);  Getter<Object> getter = this.myMap.get(dataId);
/* 40 */     return (getter == null) ? null : getter.get();
/*    */   }
/*    */ }


/* Location:              C:\Program Files\JetBrains\WebStorm 2021.1\plugins\NodeJS\lib\NodeJS.jar!\com\jetbrains\nodejs\run\profile\heap\view\components\DataProviderPanel.class
 * Java compiler version: 11 (55.0)
 * JD-Core Version:       1.1.3
 */