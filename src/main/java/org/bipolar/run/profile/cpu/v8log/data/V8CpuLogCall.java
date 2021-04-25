/*     */ package org.bipolar.run.profile.cpu.v8log.data;
/*     */ 
/*     */ import com.intellij.openapi.project.Project;
/*     */ import com.intellij.openapi.util.NlsSafe;
/*     */ import com.intellij.openapi.util.io.FileUtil;
/*     */ import com.intellij.openapi.util.text.StringUtil;
/*     */ import com.intellij.pom.Navigatable;
/*     */ import org.bipolar.run.profile.cpu.calculation.V8ProfileLine;
/*     */ import org.bipolar.run.profile.cpu.calculation.V8ProfileLineFileDescriptor;
/*     */ import java.util.ArrayDeque;
/*     */ import java.util.Arrays;
/*     */ import org.jetbrains.annotations.Nls;
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
/*     */ public class V8CpuLogCall
/*     */ {
/*     */   private final V8ProfileLine.ExecKind myExecKind;
/*     */   private final String myFunctionName;
/*     */   private final boolean myNative;
/*     */   private boolean myNotNavigatable;
/*     */   @Nullable
/*     */   private final V8ProfileLineFileDescriptor myDescriptor;
/*     */   @Nls
/*     */   private final String myNotParsedCallable;
/*     */   private final CodeState myCodeState;
/*     */   private final long myStringId;
/*     */   @Nls
/*     */   private String myPresentation;
/*     */   
/*     */   public V8CpuLogCall(V8ProfileLine.ExecKind execKind, String functionName, boolean aNative, boolean notNavigatable, @Nullable V8ProfileLineFileDescriptor descriptor, @NlsSafe String notParsedCallable, CodeState codeState, long stringId) {
/*  39 */     this.myExecKind = execKind;
/*  40 */     this.myFunctionName = functionName;
/*  41 */     this.myNative = aNative;
/*  42 */     this.myNotNavigatable = notNavigatable;
/*  43 */     this.myDescriptor = descriptor;
/*  44 */     this.myNotParsedCallable = notParsedCallable;
/*  45 */     this.myCodeState = codeState;
/*  46 */     this.myStringId = stringId;
/*  47 */     setPresentation(getPresentation(false));
/*     */   }
/*     */   
/*     */   public V8CpuLogCall cloneMe() {
/*  51 */     return new V8CpuLogCall(this.myExecKind, this.myFunctionName, this.myNative, this.myNotNavigatable, this.myDescriptor, this.myNotParsedCallable, this.myCodeState, this.myStringId);
/*     */   }
/*     */   
/*     */   public long getStringId() {
/*  55 */     return this.myStringId;
/*     */   }
/*     */   
/*     */   public void setNotNavigatable(boolean notNavigatable) {
/*  59 */     this.myNotNavigatable = notNavigatable;
/*     */   }
/*     */   
/*     */   public V8ProfileLine.ExecKind getExecKind() {
/*  63 */     return this.myExecKind;
/*     */   }
/*     */   
/*     */   public String getFunctionName() {
/*  67 */     return this.myFunctionName;
/*     */   }
/*     */   
/*     */   public boolean isNative() {
/*  71 */     return this.myNative;
/*     */   }
/*     */   
/*     */   public boolean isNotNavigatable() {
/*  75 */     return this.myNotNavigatable;
/*     */   }
/*     */   
/*     */   public boolean isLocal() {
/*  79 */     return (!isNative() && !isNotNavigatable() && getDescriptor() != null && getDescriptor().getPath() != null);
/*     */   }
/*     */   
/*     */   @Nullable
/*     */   public V8ProfileLineFileDescriptor getDescriptor() {
/*  84 */     return this.myDescriptor;
/*     */   }
/*     */   @Nls
/*     */   public String getNotParsedCallable() {
/*  88 */     return this.myNotParsedCallable;
/*     */   }
/*     */   
/*     */   public CodeState getCodeState() {
/*  92 */     return this.myCodeState;
/*     */   }
/*     */   
/*     */   public void setPresentation(@Nls String presentation) {
/*  96 */     this.myPresentation = presentation;
/*     */   }
/*     */   @Nls
/*     */   public String getPresentation() {
/* 100 */     return this.myPresentation;
/*     */   }
/*     */   @Nls
/*     */   public String getPresentation(boolean fullPath) {
/* 104 */     return getPresentation(fullPath, false);
/*     */   }
/*     */   @Nls
/*     */   public String getPresentation(boolean fullPath, boolean oldStyle) {
/* 108 */     V8ProfileLine.ExecKind kind = getExecKind();
/* 109 */     if (V8ProfileLine.ExecKind.unknown.equals(kind)) {
/* 110 */       return getNotParsedCallable();
/*     */     }
/* 112 */     StringBuilder sb = new StringBuilder();
/* 113 */     V8ProfileLineFileDescriptor descriptor = getDescriptor();
/* 114 */     if (descriptor != null) {
/* 115 */       if (oldStyle) {
/* 116 */         sb.append(kind.name()).append(": ");
/*     */       }
/* 118 */       sb.append(getCodeState().getPrefix());
/* 119 */       String functionName = getFunctionName();
/* 120 */       if (!StringUtil.isEmptyOrSpaces(functionName)) {
/* 121 */         sb.append(functionName).append(" ");
/*     */       }
/* 123 */       if (isNative()) {
/* 124 */         sb.append("native ");
/*     */       }
/* 126 */       sb.append(fullPath ? descriptor.getLink() : descriptor.getShortLink());
/*     */     } else {
/* 128 */       sb.append(getNotParsedCallable());
/*     */     } 
/* 130 */     String s = sb.toString();
/* 131 */     return s;
/*     */   }
/*     */   public static V8CpuLogCall create(@NotNull @NlsSafe String line, long stringId) {
/*     */     String word;
/* 135 */     if (line == null) $$$reportNull$$$0(0);  String[] split = line.split(" ");
/* 136 */     ArrayDeque<String> parts = new ArrayDeque<>(Arrays.asList(split));
/* 137 */     if (parts.size() < 2) return dumb(line, stringId); 
/* 138 */     boolean isNative = false;
/*     */     
/* 140 */     V8ProfileLine.ExecKind kind = parseKind(parts.removeFirst());
/* 141 */     if (kind == null) return dumb(line, stringId); 
/* 142 */     boolean notNavigatable = (!V8ProfileLine.ExecKind.Function.equals(kind) && !V8ProfileLine.ExecKind.LazyCompile.equals(kind));
/*     */     
/* 144 */     String functionStr = parts.getFirst();
/* 145 */     CodeState codeState = functionStr.isEmpty() ? CodeState.compiled : parseCodeState(functionStr.substring(0, 1));
/*     */ 
/*     */     
/* 148 */     String functionName = (CodeState.optimizable.equals(codeState) || CodeState.optimized.equals(codeState)) ? functionStr.substring(1) : functionStr;
/*     */     
/* 150 */     if (notNavigatable) {
/* 151 */       word = functionName;
/* 152 */       functionName = "";
/*     */     } else {
/*     */       
/* 155 */       parts.removeFirst();
/* 156 */       if (parts.isEmpty()) {
/* 157 */         return new V8CpuLogCall(kind, functionName, false, true, null, "", codeState, stringId);
/*     */       }
/* 159 */       word = parts.getFirst();
/*     */     } 
/*     */     
/* 162 */     if ("native".equals(word)) {
/* 163 */       isNative = true;
/* 164 */       if (parts.isEmpty()) return dumb(line, stringId); 
/* 165 */       parts.removeFirst();
/*     */     } else {
/* 167 */       parts.removeFirst();
/* 168 */       parts.addFirst(word);
/*     */     } 
/* 170 */     String fileStr = StringUtil.join(parts, " ");
/* 171 */     V8ProfileLineFileDescriptor descriptor = parseFileStr(fileStr, kind);
/* 172 */     if (descriptor == null) {
/* 173 */       return new V8CpuLogCall(kind, functionName, isNative, notNavigatable, null, fileStr, codeState, stringId);
/*     */     }
/* 175 */     return new V8CpuLogCall(kind, functionName, isNative, notNavigatable, descriptor, fileStr, codeState, stringId);
/*     */   }
/*     */ 
/*     */   
/*     */   private static V8ProfileLineFileDescriptor parseFileStr(String str, V8ProfileLine.ExecKind kind) {
/* 180 */     if (V8ProfileLine.ExecKind.Script.equals(kind)) {
/* 181 */       return parsePath(FileUtil.toSystemIndependentName(str), -1, -1);
/*     */     }
/* 183 */     int idxSecondColon = str.lastIndexOf(":");
/* 184 */     if (idxSecondColon <= 0) {
/* 185 */       return parsePath(FileUtil.toSystemIndependentName(str), -1, -1);
/*     */     }
/* 187 */     int idxFirstColon = str.lastIndexOf(":", idxSecondColon - 1);
/* 188 */     if (idxFirstColon <= 0) {
/* 189 */       return null;
/*     */     }
/*     */     try {
/* 192 */       int line = Integer.parseInt(str.substring(idxFirstColon + 1, idxSecondColon));
/* 193 */       int column = Integer.parseInt(str.substring(idxSecondColon + 1));
/* 194 */       String fileName = str.substring(0, idxFirstColon);
/* 195 */       fileName = FileUtil.toSystemIndependentName(fileName);
/* 196 */       return parsePath(fileName, line, column);
/* 197 */     } catch (NumberFormatException e) {
/* 198 */       return null;
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   private static V8ProfileLineFileDescriptor parsePath(@NotNull String fileName, int line, int column) {
/* 204 */     if (fileName == null) $$$reportNull$$$0(1);  int idx = fileName.lastIndexOf('/');
/* 205 */     if (idx >= 0) {
/* 206 */       String path = fileName;
/* 207 */       fileName = fileName.substring(idx + 1);
/* 208 */       return new V8ProfileLineFileDescriptor(fileName, path, line, column);
/*     */     } 
/* 210 */     return new V8ProfileLineFileDescriptor(fileName, null, line, column);
/*     */   }
/*     */   
/*     */   private static CodeState parseCodeState(String substring) {
/* 214 */     CodeState state = CodeState.fromStrState(substring);
/* 215 */     return (state == null) ? CodeState.compiled : state;
/*     */   }
/*     */   
/*     */   @NotNull
/*     */   public static V8CpuLogCall dumb(@NotNull @Nls String line, long stringId) {
/* 220 */     if (line == null) $$$reportNull$$$0(2);  return new V8CpuLogCall(V8ProfileLine.ExecKind.unknown, "", true, true, null, line, CodeState.compiled, stringId);
/*     */   }
/*     */   
/*     */   private static V8ProfileLine.ExecKind parseKind(String s) {
/* 224 */     if (!s.endsWith(":")) return null; 
/* 225 */     return V8ProfileLine.ExecKind.safeValueOf(s.substring(0, s.length() - 1));
/*     */   }
/*     */   
/*     */   public Navigatable[] getNavigatables(Project project) {
/* 229 */     return (this.myDescriptor == null) ? null : this.myDescriptor.getNavigatables(project, this.myFunctionName, this.myNative);
/*     */   }
/*     */   
/*     */   public String getShort() {
/* 233 */     if (V8ProfileLine.ExecKind.unknown.equals(getExecKind())) {
/* 234 */       return getNotParsedCallable();
/*     */     }
/* 236 */     String link = (this.myDescriptor == null) ? this.myNotParsedCallable : this.myDescriptor.getShortLink();
/* 237 */     if (StringUtil.isEmptyOrSpaces(this.myFunctionName)) {
/* 238 */       return link + " (" + link + ")";
/*     */     }
/*     */     
/* 241 */     return this.myFunctionName + " " + this.myFunctionName;
/*     */   }
/*     */ }


/* Location:              C:\Program Files\JetBrains\WebStorm 2021.1\plugins\NodeJS\lib\NodeJS.jar!\com\jetbrains\nodejs\run\profile\cpu\v8log\data\V8CpuLogCall.class
 * Java compiler version: 11 (55.0)
 * JD-Core Version:       1.1.3
 */