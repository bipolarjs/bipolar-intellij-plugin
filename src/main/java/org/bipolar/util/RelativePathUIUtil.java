/*     */ package org.bipolar.util;
/*     */ 
/*     */ import com.intellij.openapi.fileChooser.FileChooserDescriptor;
/*     */ import com.intellij.openapi.fileChooser.ex.FileLookup;
/*     */ import com.intellij.openapi.fileChooser.ex.FileTextFieldImpl;
/*     */ import com.intellij.openapi.fileChooser.ex.LocalFsFinder;
/*     */ import com.intellij.openapi.project.Project;
/*     */ import com.intellij.openapi.ui.TextFieldWithBrowseButton;
/*     */ import com.intellij.openapi.util.Ref;
/*     */ import com.intellij.openapi.util.io.FileUtil;
/*     */ import com.intellij.openapi.util.text.StringUtil;
/*     */ import com.intellij.ui.DocumentAdapter;
/*     */ import java.io.File;
/*     */ import javax.swing.JTextField;
/*     */ import javax.swing.event.DocumentEvent;
/*     */ import javax.swing.event.DocumentListener;
/*     */ import javax.swing.text.BadLocationException;
/*     */ import javax.swing.text.Document;
/*     */ import org.jetbrains.annotations.NotNull;
/*     */ import org.jetbrains.annotations.Nullable;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public final class RelativePathUIUtil
/*     */ {
/*     */   public static TextFieldWithBrowseButton createRelativePathTextFieldAndTrackBaseDirChanges(@NotNull Project project, @NotNull final FileChooserDescriptor descriptor, @NotNull Document baseDirDocument) {
/*  33 */     if (project == null) $$$reportNull$$$0(0);  if (descriptor == null) $$$reportNull$$$0(1);  if (baseDirDocument == null) $$$reportNull$$$0(2);  final Ref<Boolean> doneRef = Ref.create(Boolean.valueOf(false));
/*  34 */     JTextField relativePathTextField = new RelativePathTextField();
/*  35 */     TextFieldWithBrowseButton relativePath = new TextFieldWithBrowseButton(relativePathTextField)
/*     */       {
/*     */         protected void installPathCompletion(FileChooserDescriptor fileChooserDescriptor) {
/*  38 */           if (!((Boolean)doneRef.get()).booleanValue()) {
/*  39 */             super.installPathCompletion(descriptor);
/*  40 */             doneRef.set(Boolean.valueOf(true));
/*     */           } 
/*     */         }
/*     */       };
/*  44 */     trackBaseDirChanges(relativePath, baseDirDocument);
/*  45 */     return relativePath;
/*     */   }
/*     */ 
/*     */   
/*     */   private static void trackBaseDirChanges(@NotNull final TextFieldWithBrowseButton textFieldWithBrowseButton, @NotNull final Document baseDirDocument) {
/*  50 */     if (textFieldWithBrowseButton == null) $$$reportNull$$$0(3);  if (baseDirDocument == null) $$$reportNull$$$0(4);  final Ref<String> oldBaseDirRef = Ref.create(getText(baseDirDocument));
/*  51 */     updatePathCompletion(textFieldWithBrowseButton, getText(baseDirDocument));
/*  52 */     baseDirDocument.addDocumentListener((DocumentListener)new DocumentAdapter()
/*     */         {
/*     */           protected void textChanged(@NotNull DocumentEvent e) {
/*  55 */             if (e == null) $$$reportNull$$$0(0);  String oldText = StringUtil.notNullize((String)oldBaseDirRef.get());
/*  56 */             String newText = StringUtil.notNullize(RelativePathUIUtil.getText(baseDirDocument));
/*  57 */             if (!oldText.equals(newText)) {
/*  58 */               RelativePathUIUtil.updatePathCompletion(textFieldWithBrowseButton, newText);
/*  59 */               oldBaseDirRef.set(newText);
/*     */             } 
/*     */           }
/*     */         });
/*     */   }
/*     */ 
/*     */   
/*     */   @NotNull
/*     */   private static String getText(@NotNull Document document) {
/*  68 */     if (document == null) $$$reportNull$$$0(5);  try { if (document.getText(0, document.getLength()) == null) $$$reportNull$$$0(6);  return document.getText(0, document.getLength()); }
/*     */     
/*  70 */     catch (BadLocationException e)
/*  71 */     { return ""; }
/*     */   
/*     */   }
/*     */ 
/*     */   
/*     */   private static void updatePathCompletion(@NotNull TextFieldWithBrowseButton textFieldWithBrowseButton, @NotNull String baseDirPath) {
/*  77 */     if (textFieldWithBrowseButton == null) $$$reportNull$$$0(7);  if (baseDirPath == null) $$$reportNull$$$0(8);  JTextField textField = textFieldWithBrowseButton.getTextField();
/*  78 */     File baseDir = new File(baseDirPath);
/*  79 */     if (textField instanceof RelativePathTextField) {
/*  80 */       RelativePathTextField relativePathTextField = (RelativePathTextField)textField;
/*  81 */       relativePathTextField.setBaseDir(baseDir);
/*     */     } 
/*  83 */     FileTextFieldImpl fileTextField = (FileTextFieldImpl)textField.getClientProperty("fileTextField");
/*  84 */     FileLookup.Finder finder = fileTextField.getFinder();
/*  85 */     if (finder instanceof LocalFsFinder) {
/*  86 */       LocalFsFinder localFsFinder = (LocalFsFinder)finder;
/*  87 */       localFsFinder.setBaseDir(baseDir);
/*     */     } 
/*     */   }
/*     */   
/*     */   private static final class RelativePathTextField
/*     */     extends JTextField {
/*     */     private File myBaseDir;
/*     */     
/*     */     private RelativePathTextField() {
/*  96 */       super(10);
/*     */     }
/*     */     
/*     */     private void setBaseDir(@NotNull File baseDir) {
/* 100 */       if (baseDir == null) $$$reportNull$$$0(0);  this.myBaseDir = baseDir;
/*     */     }
/*     */ 
/*     */     
/*     */     public void setText(String text) {
/* 105 */       String refinedText = refineText(text);
/* 106 */       super.setText(refinedText);
/*     */     }
/*     */     
/*     */     @NotNull
/*     */     private String refineText(@Nullable String path) {
/* 111 */       if (path == null || path.trim().isEmpty()) {
/* 112 */         if (StringUtil.notNullize(path) == null) $$$reportNull$$$0(1);  return StringUtil.notNullize(path);
/*     */       } 
/* 114 */       File file = new File(path);
/* 115 */       if (file.isAbsolute() && this.myBaseDir != null && this.myBaseDir.isDirectory()) {
/* 116 */         boolean inside = FileUtil.isAncestor(this.myBaseDir, file, true);
/* 117 */         if (inside) {
/* 118 */           String relativePath = FileUtil.getRelativePath(this.myBaseDir, file);
/* 119 */           if (relativePath != null) {
/* 120 */             if (relativePath == null) $$$reportNull$$$0(2);  return relativePath;
/*     */           } 
/*     */         } 
/*     */       } 
/* 124 */       if (path == null) $$$reportNull$$$0(3);  return path;
/*     */     }
/*     */   }
/*     */ }


/* Location:              C:\Program Files\JetBrains\WebStorm 2021.1\plugins\NodeJS\lib\NodeJS.jar!\com\jetbrains\nodej\\util\RelativePathUIUtil.class
 * Java compiler version: 11 (55.0)
 * JD-Core Version:       1.1.3
 */