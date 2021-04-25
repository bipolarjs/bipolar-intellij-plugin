/*     */ package org.bipolar.codeInsight.require;
/*     */ 
/*     */ import com.intellij.codeInsight.completion.CompletionContributor;
/*     */ import com.intellij.codeInsight.completion.CompletionParameters;
/*     */ import com.intellij.codeInsight.completion.CompletionResultSet;
/*     */ import com.intellij.codeInsight.completion.CompletionType;
/*     */ import com.intellij.codeInsight.lookup.AutoCompletionPolicy;
/*     */ import com.intellij.codeInsight.lookup.LookupElement;
/*     */ import com.intellij.codeInsight.lookup.LookupElementBuilder;
/*     */ import com.intellij.codeInsight.lookup.LookupElementPresentation;
/*     */ import com.intellij.codeInsight.lookup.LookupElementRenderer;
/*     */ import com.intellij.javascript.nodejs.NodeModuleSearchUtil;
/*     */ import com.intellij.javascript.nodejs.npm.registry.NpmRegistryService;
/*     */ import com.intellij.javascript.nodejs.packageJson.NodePackageBasicInfo;
/*     */ import com.intellij.javascript.nodejs.packageJson.codeInsight.popularPackages.PopularNodePackagesProvider;
/*     */ import com.intellij.lang.javascript.psi.JSCallExpression;
/*     */ import com.intellij.openapi.diagnostic.Logger;
/*     */ import com.intellij.openapi.keymap.KeymapUtil;
/*     */ import com.intellij.openapi.progress.ProcessCanceledException;
/*     */ import com.intellij.openapi.progress.ProgressManager;
/*     */ import com.intellij.openapi.util.text.StringUtil;
/*     */ import com.intellij.psi.PsiElement;
/*     */ import com.intellij.util.containers.ContainerUtil;
/*     */ import org.bipolar.NodeJSBundle;
/*     */ import java.io.IOException;
/*     */ import java.util.List;
/*     */ import java.util.Set;
/*     */
import org.jetbrains.annotations.NotNull;
/*     */ 
/*     */ public class NodeRemotePackagesCompletionContributor
/*     */   extends CompletionContributor {
/*  32 */   public static final LookupElementRenderer<LookupElement> RENDERER = new LookupElementRenderer<LookupElement>()
/*     */     {
/*     */       public void renderElement(LookupElement element, LookupElementPresentation presentation) {
/*  35 */         presentation.setItemText(element.getLookupString());
/*  36 */         presentation.setTypeGrayed(true);
/*  37 */         presentation.setTypeText("remote module");
/*     */       }
/*     */     };
/*  40 */   private static final Logger LOG = Logger.getInstance(NodeRemotePackagesCompletionContributor.class);
/*     */ 
/*     */   
/*     */   public void fillCompletionVariants(@NotNull CompletionParameters parameters, @NotNull CompletionResultSet result) {
/*  44 */     if (parameters == null) $$$reportNull$$$0(0);  if (result == null) $$$reportNull$$$0(1);  PsiElement position = parameters.getOriginalPosition();
/*  45 */     if (position == null)
/*  46 */       return;  String text = StringUtil.unquoteString(position.getText());
/*  47 */     if (NodeModuleSearchUtil.isFileModuleRequired(text.trim()))
/*     */       return; 
/*  49 */     if (position.getParent() instanceof com.intellij.lang.javascript.psi.JSLiteralExpression && 
/*  50 */       position.getParent().getParent() instanceof com.intellij.lang.javascript.psi.JSArgumentList && position
/*  51 */       .getParent().getParent().getParent() instanceof JSCallExpression) {
/*  52 */       JSCallExpression call = (JSCallExpression)position.getParent().getParent().getParent();
/*  53 */       if (call.isRequireCall()) {
/*  54 */         if (parameters.getInvocationCount() > 1 || CompletionType.SMART.equals(parameters.getCompletionType())) {
/*  55 */           if (text.trim().length() < 3) {
/*  56 */             advertisement(text, result);
/*     */           } else {
/*     */             
/*  59 */             collectPackages(result);
/*     */           } 
/*     */         } else {
/*  62 */           advertisement(text, result);
/*     */         } 
/*     */       }
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   private static void collectPackages(@NotNull CompletionResultSet result) {
/*  70 */     if (result == null) $$$reportNull$$$0(2);  String prefix = result.getPrefixMatcher().getPrefix();
/*  71 */     List<NodePackageBasicInfo> popularPackages = PopularNodePackagesProvider.getInstance().findByPrefix(prefix, 10, info -> true);
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*  76 */     for (NodePackageBasicInfo pkg : popularPackages) {
/*  77 */       result.consume(createElement(pkg.getName()));
/*     */     }
/*  79 */     Set<String> popularNames = ContainerUtil.map2Set(popularPackages, NodePackageBasicInfo::getName);
/*     */     try {
/*  81 */       NpmRegistryService.getInstance().findPackages(
/*  82 */           ProgressManager.getInstance().getProgressIndicator(), 
/*  83 */           NpmRegistryService.namePrefixSearch(prefix), 
/*  84 */           Math.max(0, 10 - popularPackages.size()), info -> !popularNames.contains(info.getName()), info -> result.consume(createElement(info.getName())));
/*     */ 
/*     */ 
/*     */     
/*     */     }
/*  89 */     catch (ProcessCanceledException e) {
/*  90 */       LOG.debug("Fetching '" + prefix + "*' packages from registry cancelled");
/*     */     }
/*  92 */     catch (IOException e) {
/*  93 */       LOG.info("Cannot fetch '" + prefix + "*' packages from registry", e);
/*     */     } 
/*  95 */     result.restartCompletionOnAnyPrefixChange();
/*     */   }
/*     */   
/*     */   private static void advertisement(String text, CompletionResultSet result) {
/*  99 */     if (text.trim().length() < 3) {
/* 100 */       result.addLookupAdvertisement(
/* 101 */           NodeJSBundle.message("popup.advertisement.type.at.least.symbols.press.twice.for.remote.packages", new Object[] {
/* 102 */               KeymapUtil.getFirstKeyboardShortcutText("CodeCompletion") }));
/*     */     } else {
/* 104 */       result.addLookupAdvertisement(NodeJSBundle.message("popup.advertisement.press.again.for.remote.packages", new Object[] {
/* 105 */               KeymapUtil.getFirstKeyboardShortcutText("CodeCompletion") }));
/*     */     } 
/*     */   }
/*     */   
/*     */   @NotNull
/*     */   private static LookupElement createElement(String repoPackage) {
/* 111 */     if (LookupElementBuilder.create(repoPackage)
/* 112 */       .withRenderer(RENDERER)
/* 113 */       .withAutoCompletionPolicy(AutoCompletionPolicy.NEVER_AUTOCOMPLETE) == null) $$$reportNull$$$0(3);  return LookupElementBuilder.create(repoPackage).withRenderer(RENDERER).withAutoCompletionPolicy(AutoCompletionPolicy.NEVER_AUTOCOMPLETE);
/*     */   }
/*     */ }


/* Location:              C:\Program Files\JetBrains\WebStorm 2021.1\plugins\NodeJS\lib\NodeJS.jar!\com\jetbrains\nodejs\codeInsight\require\NodeRemotePackagesCompletionContributor.class
 * Java compiler version: 11 (55.0)
 * JD-Core Version:       1.1.3
 */