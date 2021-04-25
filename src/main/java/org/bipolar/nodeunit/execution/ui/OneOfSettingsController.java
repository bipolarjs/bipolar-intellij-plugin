/*    */ package org.bipolar.nodeunit.execution.ui;
/*    */ 
/*    */ import org.bipolar.nodeunit.execution.NodeunitSettings;
/*    */ import java.awt.CardLayout;
/*    */ import java.util.Collection;
/*    */ import java.util.HashMap;
/*    */ import java.util.Iterator;
/*    */ import java.util.Map;
/*    */ import javax.swing.JComponent;
/*    */ import javax.swing.JPanel;
/*    */
import org.jetbrains.annotations.NotNull;
/*    */ 
/*    */ class OneOfSettingsController<T extends IdProvider & SettingsControllerFactory> implements SettingsController {
/*    */   @NotNull
/*    */   private final JPanel myCardPanel;
/* 16 */   private final Map<String, SettingsController> mySectionByIdMap = new HashMap<>();
/*    */   private T mySelectedKey;
/*    */   
/*    */   OneOfSettingsController(@NotNull Context context, @NotNull Collection<T> childFactories) {
/* 20 */     this.myCardPanel = new JPanel(new CardLayout());
/* 21 */     for (IdProvider idProvider : childFactories) {
/* 22 */       SettingsController settingsController = ((SettingsControllerFactory)idProvider).createSettingsController(context);
/* 23 */       JComponent childComponent = settingsController.getComponent();
/* 24 */       this.myCardPanel.add(childComponent, idProvider.getId());
/* 25 */       this.mySectionByIdMap.put(idProvider.getId(), settingsController);
/*    */     } 
/* 27 */     Iterator<T> iterator = childFactories.iterator();
/* 28 */     if (iterator.hasNext()) {
/* 29 */       select(iterator.next());
/*    */     } else {
/* 31 */       throw new RuntimeException("No child items were found");
/*    */     } 
/*    */   }
/*    */ 
/*    */   
/*    */   public void resetFrom(@NotNull NodeunitSettings settings) {
/* 37 */     if (settings == null) $$$reportNull$$$0(2);  SettingsController settingsController = getSelectedSettingsController();
/* 38 */     settingsController.resetFrom(settings);
/*    */   }
/*    */ 
/*    */   
/*    */   public void applyTo(@NotNull NodeunitSettings.Builder settingsBuilder) {
/* 43 */     if (settingsBuilder == null) $$$reportNull$$$0(3);  SettingsController settingsController = getSelectedSettingsController();
/* 44 */     settingsController.applyTo(settingsBuilder);
/*    */   }
/*    */   
/*    */   @NotNull
/*    */   public JPanel getComponent() {
/* 49 */     if (this.myCardPanel == null) $$$reportNull$$$0(4);  return this.myCardPanel;
/*    */   }
/*    */   
/*    */   public void select(@NotNull T key) {
/* 53 */     if (key == null) $$$reportNull$$$0(5);  if (this.mySelectedKey != key) {
/* 54 */       CardLayout cardLayout = (CardLayout)this.myCardPanel.getLayout();
/* 55 */       cardLayout.show(this.myCardPanel, key.getId());
/* 56 */       this.mySelectedKey = key;
/*    */     } 
/*    */   }
/*    */   
/*    */   public T getSelectedKey() {
/* 61 */     return this.mySelectedKey;
/*    */   }
/*    */   
/*    */   private SettingsController getSelectedSettingsController() {
/* 65 */     return this.mySectionByIdMap.get(this.mySelectedKey.getId());
/*    */   }
/*    */ }


/* Location:              C:\Program Files\JetBrains\WebStorm 2021.1\plugins\NodeJS\lib\NodeJS.jar!\com\jetbrains\nodejs\nodeunit\executio\\ui\OneOfSettingsController.class
 * Java compiler version: 11 (55.0)
 * JD-Core Version:       1.1.3
 */