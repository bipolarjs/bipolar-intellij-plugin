/*     */ package org.bipolar.boilerplate.express;
/*     */ import com.intellij.ide.util.projectWizard.SettingsStep;
/*     */ import com.intellij.javascript.nodejs.util.NodePackage;
/*     */ import com.intellij.javascript.nodejs.util.NodePackageField;
/*     */ import com.intellij.lang.javascript.boilerplate.NpmPackageProjectGenerator;
/*     */ import com.intellij.openapi.ui.ComboBox;
/*     */ import com.intellij.openapi.ui.LabeledComponent;
/*     */ import com.intellij.openapi.ui.ValidationInfo;
/*     */ import com.intellij.openapi.util.Key;
/*     */ import com.intellij.ui.PanelWithAnchor;
/*     */ import com.intellij.util.ObjectUtils;
/*     */ import com.intellij.util.containers.ContainerUtil;
/*     */ import com.intellij.util.text.SemVer;
/*     */ import com.intellij.util.ui.JBUI;
/*     */ import com.intellij.util.ui.SwingHelper;
/*     */ import com.intellij.util.ui.UIUtil;
/*     */ import org.bipolar.NodeJSBundle;
/*     */ import org.bipolar.util.ui.UIHelper;
/*     */ import java.awt.Component;
/*     */ import java.awt.FlowLayout;
/*     */ import java.util.List;
/*     */ import java.util.Objects;
/*     */ import java.util.concurrent.ExecutionException;
/*     */ import javax.swing.Box;
/*     */ import javax.swing.JComboBox;
/*     */ import javax.swing.JComponent;
/*     */ import javax.swing.JPanel;
/*     */
import org.jetbrains.annotations.Nls;
/*     */ import org.jetbrains.annotations.NotNull;
/*     */ import org.jetbrains.annotations.Nullable;
/*     */ import org.jetbrains.concurrency.Promise;
/*     */ 
/*     */ public final class ExpressAppGeneratorPeerHelper {
/*  34 */   private static final Key<ExpressAppSettings> EXTRA_SETTINGS = Key.create(ExpressAppGeneratorPeerHelper.class.getName());
/*  35 */   public static final SemVer LATEST_VERSION = new SemVer("4.16.0", 4, 16, 0);
/*     */   
/*     */   private final ComboBox<ExpressTemplateEngine> myTemplateEngine;
/*     */   private final ComboBox<ExpressStylesheetEngine> myStylesheetEngine;
/*     */   private final JPanel myOptionsPanel;
/*     */   private SemVer myLatestVersion;
/*     */   
/*     */   ExpressAppGeneratorPeerHelper() {
/*  43 */     this.myTemplateEngine = UIHelper.createCombobox(UIHelper.getUnavailableText());
/*  44 */     this.myTemplateEngine.setPrototypeDisplayValue(ExpressTemplateEngine.NO_VIEW);
/*  45 */     this.myStylesheetEngine = UIHelper.createCombobox(UIHelper.getUnavailableText());
/*  46 */     this.myOptionsPanel = createOptionsPanel(this.myTemplateEngine, this.myStylesheetEngine);
/*  47 */     updateVersion(null);
/*     */   }
/*     */   
/*     */   @NotNull
/*     */   public JPanel getOptionsPanel() {
/*  52 */     if (this.myOptionsPanel == null) $$$reportNull$$$0(0);  return this.myOptionsPanel;
/*     */   }
/*     */   
/*     */   public void buildUI(@NotNull SettingsStep settingsStep) {
/*  56 */     if (settingsStep == null) $$$reportNull$$$0(1);  settingsStep.addSettingsComponent(this.myOptionsPanel);
/*     */   }
/*     */   
/*     */   @NotNull
/*     */   public NpmPackageProjectGenerator.Settings getSettings(@NotNull NpmPackageProjectGenerator.Settings settings) {
/*  61 */     if (settings == null) $$$reportNull$$$0(2);  SemVer version = getPackageVersion(settings);
/*  62 */     ExpressTemplateEngine templateEngine = (ExpressTemplateEngine)ObjectUtils.notNull(this.myTemplateEngine.getSelectedItem(), 
/*  63 */         getDefaultTemplateEngine(version));
/*  64 */     ExpressStylesheetEngine cssEngine = (ExpressStylesheetEngine)ObjectUtils.notNull(this.myStylesheetEngine.getSelectedItem(), ExpressStylesheetEngine.PLAIN_CSS);
/*     */     
/*  66 */     settings.putUserData(EXTRA_SETTINGS, new ExpressAppSettings(version, templateEngine, cssEngine));
/*  67 */     if (settings == null) $$$reportNull$$$0(3);  return settings;
/*     */   }
/*     */   
/*     */   public void setPackageField(@NotNull NodePackageField packageField) {
/*  71 */     if (packageField == null) $$$reportNull$$$0(4);  packageField.addSelectionListener(this::onPackageSelected);
/*  72 */     onPackageSelected(packageField.getSelected());
/*     */   }
/*     */   
/*     */   private void onPackageSelected(@Nullable NodePackage pkg) {
/*  76 */     if (pkg != null) {
/*  77 */       pkg.getVersionPromise(null).onProcessed(version -> updateVersion(version));
/*     */     
/*     */     }
/*     */     else {
/*     */       
/*  82 */       updateVersion(null);
/*     */     } 
/*     */   }
/*     */   
/*     */   private void updateVersion(@Nullable SemVer version) {
/*  87 */     if (Objects.equals(this.myLatestVersion, version))
/*  88 */       return;  this.myLatestVersion = version;
/*  89 */     if (version == null) {
/*  90 */       version = LATEST_VERSION;
/*     */     }
/*  92 */     List<ExpressTemplateEngine> templates = ContainerUtil.newArrayList((Object[])new ExpressTemplateEngine[] { ExpressTemplateEngine.JADE, ExpressTemplateEngine.EJS });
/*     */     
/*  94 */     List<ExpressStylesheetEngine> stylesheets = ContainerUtil.newArrayList((Object[])new ExpressStylesheetEngine[] { ExpressStylesheetEngine.PLAIN_CSS, ExpressStylesheetEngine.STYLUS });
/*  95 */     if (version.isGreaterOrEqualThan(3, 0, 0)) {
/*  96 */       templates.add(ExpressTemplateEngine.HBS);
/*  97 */       templates.add(ExpressTemplateEngine.HOGAN);
/*  98 */       stylesheets.add(ExpressStylesheetEngine.LESS);
/*  99 */       stylesheets.add(ExpressStylesheetEngine.COMPASS);
/*     */     } 
/* 101 */     if (version.isGreaterOrEqualThan(4, 16, 0)) {
/* 102 */       templates = ContainerUtil.newArrayList((Object[])ExpressTemplateEngine.values());
/* 103 */       templates.remove(ExpressTemplateEngine.JADE);
/*     */     }
/* 105 */     else if (version.isGreaterOrEqualThan(4, 15, 0)) {
/* 106 */       templates = ContainerUtil.newArrayList((Object[])ExpressTemplateEngine.values());
/* 107 */       templates.remove(ExpressTemplateEngine.JADE);
/* 108 */       templates.remove(ExpressTemplateEngine.NO_VIEW);
/*     */     } 
/* 110 */     if (version.isGreaterOrEqualThan(4, 13, 0)) {
/* 111 */       stylesheets.add(ExpressStylesheetEngine.SASS);
/*     */     }
/* 113 */     SwingHelper.updateItems((JComboBox)this.myTemplateEngine, templates, getDefaultTemplateEngine(version));
/* 114 */     SwingHelper.updateItems((JComboBox)this.myStylesheetEngine, stylesheets, ExpressStylesheetEngine.PLAIN_CSS);
/*     */   }
/*     */   
/*     */   @NotNull
/*     */   private static ExpressTemplateEngine getDefaultTemplateEngine(@Nullable SemVer version) {
/* 119 */     if (((version == null || version.isGreaterOrEqualThan(4, 15, 0)) ? ExpressTemplateEngine.PUG : ExpressTemplateEngine.JADE) == null) $$$reportNull$$$0(5);  return (version == null || version.isGreaterOrEqualThan(4, 15, 0)) ? ExpressTemplateEngine.PUG : ExpressTemplateEngine.JADE;
/*     */   }
/*     */   
/*     */   @NotNull
/*     */   static ExpressAppSettings getExpressSettings(@NotNull NpmPackageProjectGenerator.Settings settings) {
/* 124 */     if (settings == null) $$$reportNull$$$0(6);  if ((ExpressAppSettings)Objects.requireNonNull((ExpressAppSettings)settings.getUserData(EXTRA_SETTINGS)) == null) $$$reportNull$$$0(7);  return Objects.requireNonNull((ExpressAppSettings)settings.getUserData(EXTRA_SETTINGS));
/*     */   }
/*     */   
/*     */   @Nullable
/*     */   static SemVer getPackageVersion(@NotNull NpmPackageProjectGenerator.Settings settings) {
/* 129 */     if (settings == null) $$$reportNull$$$0(8);  Promise<SemVer> versionPromise = settings.myPackage.getVersionPromise(null);
/*     */     try {
/* 131 */       return (SemVer)versionPromise.blockingGet(5, TimeUnit.MILLISECONDS);
/*     */     }
/* 133 */     catch (TimeoutException e) {
/* 134 */       return null;
/*     */     }
/* 136 */     catch (ExecutionException e) {
/* 137 */       return null;
/*     */     } 
/*     */   }
/*     */   
/*     */   @Nullable
/*     */   public ValidationInfo validate() {
/* 143 */     ExpressTemplateEngine templateEngine = (ExpressTemplateEngine)ObjectUtils.tryCast(this.myTemplateEngine.getSelectedItem(), ExpressTemplateEngine.class);
/* 144 */     if (templateEngine == null) {
/* 145 */       return new ValidationInfo(NodeJSBundle.message("dialog.message.template.engine.unavailable", new Object[0]), (JComponent)this.myTemplateEngine);
/*     */     }
/* 147 */     ExpressStylesheetEngine stylesheetEngine = (ExpressStylesheetEngine)ObjectUtils.tryCast(this.myStylesheetEngine.getSelectedItem(), ExpressStylesheetEngine.class);
/* 148 */     if (stylesheetEngine == null) {
/* 149 */       return new ValidationInfo(NodeJSBundle.message("dialog.message.stylesheet.engine.unavailable", new Object[0]), (JComponent)this.myStylesheetEngine);
/*     */     }
/* 151 */     return null;
/*     */   }
/*     */   
/*     */   @NotNull
/*     */   private static JPanel createOptionsPanel(@NotNull ComboBox<?> templateEngine, @NotNull ComboBox<?> cssEngine) {
/* 156 */     if (templateEngine == null) $$$reportNull$$$0(9);  if (cssEngine == null) $$$reportNull$$$0(10);  LabeledComponent<JComponent> templateLabeledComponent = createLabeledComponent(
/* 157 */         NodeJSBundle.message("express.generator.view.engine.label", new Object[0]), (JComponent)templateEngine);
/* 158 */     LabeledComponent<JComponent> cssLabeledComponent = createLabeledComponent(
/* 159 */         NodeJSBundle.message("express.generator.stylesheet.engine.label", new Object[0]), (JComponent)cssEngine);
/* 160 */     UIUtil.mergeComponentsWithAnchor(new PanelWithAnchor[] { (PanelWithAnchor)templateLabeledComponent, (PanelWithAnchor)cssLabeledComponent });
/* 161 */     setMaxPreferredWidth((ComboBox<?>[])new ComboBox[] { templateEngine, cssEngine });
/*     */     
/* 163 */     JPanel optionsPanel = SwingHelper.newLeftAlignedVerticalPanel(new Component[] { (Component)templateLabeledComponent, 
/* 164 */           Box.createVerticalStrut(JBUI.scale(4)), (Component)cssLabeledComponent });
/*     */     
/* 166 */     optionsPanel.setBorder(IdeBorderFactory.createTitledBorder(NodeJSBundle.message("express.generator.options.delimiter.name", new Object[0])));
/* 167 */     if (SwingHelper.newLeftAlignedVerticalPanel(new Component[] { Box.createVerticalStrut(JBUI.scale(10)), 
/* 168 */           Box.createVerticalStrut(JBUI.scale(4)), optionsPanel }) == null) $$$reportNull$$$0(11);  return SwingHelper.newLeftAlignedVerticalPanel(new Component[] { Box.createVerticalStrut(JBUI.scale(10)), Box.createVerticalStrut(JBUI.scale(4)), optionsPanel });
/*     */   }
/*     */ 
/*     */   
/*     */   private static void setMaxPreferredWidth(ComboBox<?>... comboBoxes) {
/* 173 */     int maxWidth = 0;
/* 174 */     for (ComboBox<?> box : comboBoxes) {
/* 175 */       maxWidth = Math.max(maxWidth, (box.getPreferredSize()).width);
/*     */     }
/* 177 */     for (ComboBox<?> box : comboBoxes) {
/* 178 */       SwingHelper.setPreferredWidth((Component)box, maxWidth);
/*     */     }
/*     */   }
/*     */   
/*     */   @NotNull
/*     */   private static LabeledComponent<JComponent> createLabeledComponent(@NotNull @Nls String text, @NotNull JComponent comp) {
/* 184 */     if (text == null) $$$reportNull$$$0(12);  if (comp == null) $$$reportNull$$$0(13);  JPanel panel = new JPanel(new FlowLayout(0, 0, 0));
/* 185 */     panel.add(comp);
/* 186 */     LabeledComponent<JComponent> labeledComponent = LabeledComponent.create(panel, text);
/* 187 */     labeledComponent.setLabelLocation("West");
/* 188 */     if (labeledComponent == null) $$$reportNull$$$0(14);  return labeledComponent;
/*     */   }
/*     */ }


/* Location:              C:\Program Files\JetBrains\WebStorm 2021.1\plugins\NodeJS\lib\NodeJS.jar!\com\jetbrains\nodejs\boilerplate\express\ExpressAppGeneratorPeerHelper.class
 * Java compiler version: 11 (55.0)
 * JD-Core Version:       1.1.3
 */