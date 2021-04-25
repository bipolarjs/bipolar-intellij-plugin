/*     */ package org.bipolar.run.profile.cpu.calculation;
/*     */ 
/*     */ import com.intellij.execution.ExecutionException;
/*     */ import com.intellij.openapi.project.Project;
/*     */ import com.intellij.openapi.util.NlsSafe;
/*     */ import com.intellij.openapi.util.Pair;
/*     */ import com.intellij.openapi.util.text.StringUtil;
/*     */ import com.intellij.pom.Navigatable;
/*     */ import com.intellij.util.Consumer;
/*     */ import com.intellij.util.Parent;
/*     */ import org.bipolar.NodeJSBundle;
/*     */ import org.bipolar.run.profile.cpu.CallHolder;
/*     */ import org.bipolar.run.profile.cpu.v8log.data.V8CpuLogCall;
/*     */ import java.util.ArrayDeque;
/*     */ import java.util.ArrayList;
/*     */ import java.util.List;
/*     */
import org.jetbrains.annotations.Nls;
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
/*     */ 
/*     */ 
/*     */ 
/*     */ public class V8ProfileLine
/*     */   implements CallHolder, Parent<V8ProfileLine>
/*     */ {
/*     */   private int myTotalTicks;
/*     */   private int myTotalTensPercent;
/*     */   private int mySelfTicks;
/*     */   private int mySelfTensPercent;
/*     */   private final V8CpuLogCall myCall;
/*     */   @Nullable
/*     */   private final V8ProfileLine myParent;
/*     */   private final List<V8ProfileLine> myChildren;
/*     */   private final int myOffset;
/*     */   private int myRecursionCount;
/*     */   private static final int INHERITANCE_STEP = 2;
/*     */   
/*     */   public V8ProfileLine(int totalTicks, int totalTensPercent, int selfTicks, int selfTensPercent, @Nullable V8ProfileLine parent, int offset, V8CpuLogCall call) {
/*  62 */     this.myTotalTicks = totalTicks;
/*  63 */     this.myTotalTensPercent = totalTensPercent;
/*  64 */     this.mySelfTicks = selfTicks;
/*  65 */     this.mySelfTensPercent = selfTensPercent;
/*  66 */     this.myParent = parent;
/*  67 */     this.myOffset = offset;
/*  68 */     this.myChildren = new ArrayList<>();
/*     */     
/*  70 */     this.myCall = call;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public V8ProfileLine(int totalTicks, int totalTensPercent, int selfTicks, int selfTensPercent, @Nullable V8ProfileLine parent, int offset, @Nls String text, long stringId) {
/*  80 */     this(totalTicks, totalTensPercent, selfTicks, selfTensPercent, parent, offset, V8CpuLogCall.create(text, stringId));
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public V8ProfileLine cloneWithoutChildren(@Nullable V8ProfileLine parent) {
/*  86 */     V8ProfileLine copy = new V8ProfileLine(getTotalTicks(), getTotalTensPercent(), getSelfTicks(), getSelfTensPercent(), parent, getOffset(), this.myCall.cloneMe());
/*  87 */     copy.setPresentation(this.myCall.getPresentation());
/*  88 */     if (parent != null) {
/*  89 */       parent.myChildren.add(copy);
/*     */     }
/*  91 */     return copy;
/*     */   }
/*     */ 
/*     */   
/*     */   public V8CpuLogCall getCall() {
/*  96 */     return this.myCall;
/*     */   }
/*     */   
/*     */   public void setTotalTicks(int totalTicks) {
/* 100 */     this.myTotalTicks = totalTicks;
/*     */   }
/*     */   
/*     */   public void setTotalTensPercent(int totalTensPercent) {
/* 104 */     this.myTotalTensPercent = totalTensPercent;
/*     */   }
/*     */   
/*     */   public void setSelfTicks(int selfTicks) {
/* 108 */     this.mySelfTicks = selfTicks;
/*     */   }
/*     */   
/*     */   public void setSelfTensPercent(int selfTensPercent) {
/* 112 */     this.mySelfTensPercent = selfTensPercent;
/*     */   }
/*     */   
/*     */   public void setIsInternal(boolean value) {
/* 116 */     this.myCall.setNotNavigatable(value);
/*     */   }
/*     */   
/*     */   public boolean isInternal() {
/* 120 */     return (this.myCall.isNative() || this.myCall.isNotNavigatable());
/*     */   }
/*     */   
/*     */   public Navigatable[] getNavigatables(Project project) {
/* 124 */     if (this.myCall.getDescriptor() == null) return null; 
/* 125 */     return this.myCall.getDescriptor().getNavigatables(project, this.myCall.getFunctionName(), this.myCall.isNative());
/*     */   }
/*     */   
/*     */   public boolean isLocalCode() {
/* 129 */     return this.myCall.isLocal();
/*     */   }
/*     */ 
/*     */   
/*     */   public String toString() {
/* 134 */     return this.myCall.getPresentation();
/*     */   }
/*     */   
/*     */   public void setPresentation(@Nls String presentation) {
/* 138 */     this.myCall.setPresentation(presentation);
/*     */   }
/*     */   
/*     */   public String getPresentation(boolean fullPath) {
/* 142 */     return this.myCall.getPresentation(fullPath);
/*     */   }
/*     */   
/*     */   public static class TopCalledParser { @NotNull
/*     */     private final Project myProject;
/*     */     final List<Pair<String, List<V8ProfileLine>>> myData;
/*     */     
/*     */     public TopCalledParser(@NotNull Project project) {
/* 150 */       this.myProject = project;
/* 151 */       this.myData = new ArrayList<>();
/*     */     }
/*     */     
/*     */     public void parseLines(@NotNull List<String> lines) throws ExecutionException {
/* 155 */       if (lines == null) $$$reportNull$$$0(1);  String currentGroup = null;
/* 156 */       List<V8ProfileLine> currentLines = new ArrayList<>();
/*     */       
/* 158 */       for (int i = 1; i < lines.size(); i++) {
/* 159 */         String string = ((String)lines.get(i)).trim();
/* 160 */         if (!string.startsWith("ticks ") && !StringUtil.isEmptyOrSpaces(string))
/*     */         {
/* 162 */           if (string.startsWith("[")) {
/* 163 */             int idx = string.indexOf("]");
/* 164 */             if (idx < 0) {
/* 165 */               throw new ExecutionException(NodeJSBundle.message("profile.cpu.parse_error.group_name.message", new Object[] { string }));
/*     */             }
/* 167 */             if (!currentLines.isEmpty() && currentGroup == null) {
/* 168 */               throw new ExecutionException(NodeJSBundle.message("profile.cpu.parse_error.no_group_before_line.message", new Object[] { string }));
/*     */             }
/* 170 */             if (currentGroup != null && !currentLines.isEmpty()) {
/* 171 */               this.myData.add(Pair.create(currentGroup, currentLines));
/* 172 */               currentLines = new ArrayList<>();
/* 173 */               currentGroup = null;
/*     */             } 
/* 175 */             currentGroup = string.substring(1, idx);
/*     */           } else {
/*     */             
/* 178 */             V8ProfileLine.Parser parser = new V8ProfileLine.Parser(string, false, true);
/* 179 */             parser.parse();
/* 180 */             if (parser.getTotalTicks() == null || parser.getTotalTensPercent() == null) {
/* 181 */               throw new ExecutionException(NodeJSBundle.message("profile.cpu.parse_error.cannot_parse_line.message", new Object[] { string }));
/*     */             }
/* 183 */             V8ProfileLine line = V8ProfileLine.createLineOld(parser, 0, null);
/* 184 */             currentLines.add(line);
/*     */           }  } 
/*     */       } 
/* 187 */       if (!currentLines.isEmpty()) {
/* 188 */         if (currentGroup == null) {
/* 189 */           throw new ExecutionException(
/* 190 */               NodeJSBundle.message("profile.cpu.parse_error.no_group_before_line.message", new Object[] { currentLines.get(0) }));
/*     */         }
/* 192 */         this.myData.add(Pair.create(currentGroup, currentLines));
/*     */       } 
/* 194 */       for (Pair<String, List<V8ProfileLine>> pair : this.myData) {
/* 195 */         if (((List)pair.getSecond()).size() == 1) {
/* 196 */           V8ProfileLine line = ((List<V8ProfileLine>)pair.getSecond()).get(0);
/* 197 */           if (StringUtil.isEmptyOrSpaces(line.toString())) {
/* 198 */             line.setPresentation(NodeJSBundle.message("profile.cpu.line.total.text", new Object[0]));
/* 199 */             line.setIsInternal(true);
/*     */           } 
/*     */         } 
/*     */       } 
/*     */     }
/*     */     
/*     */     public List<Pair<String, List<V8ProfileLine>>> getData() {
/* 206 */       return this.myData;
/*     */     } }
/*     */ 
/*     */   
/*     */   public static class TreeParser
/*     */   {
/*     */     private final V8ProfileLine myTop;
/*     */     @NotNull
/*     */     private final Project myProject;
/*     */     private V8ProfileLine myCurrentParent;
/*     */     private boolean myParseSelfTicks;
/*     */     private boolean myParseSelfPercent;
/*     */     
/*     */     public TreeParser(@NotNull Project project) {
/* 220 */       this.myProject = project;
/* 221 */       this.myTop = new V8ProfileLine(-1, -1, -1, -1, null, 0, "", -1L);
/* 222 */       this.myCurrentParent = this.myTop;
/*     */     }
/*     */     
/*     */     public void setParseSelfTicks(boolean parseSelfTicks) {
/* 226 */       this.myParseSelfTicks = parseSelfTicks;
/*     */     }
/*     */     
/*     */     public void setParseSelfPercent(boolean parseSelfPercent) {
/* 230 */       this.myParseSelfPercent = parseSelfPercent;
/*     */     }
/*     */     
/*     */     public void parseLines(@NotNull List<String> lines) throws ExecutionException {
/* 234 */       if (lines == null) $$$reportNull$$$0(1);  for (String string : lines) {
/* 235 */         if (!StringUtil.isEmptyOrSpaces(string)) {
/* 236 */           parseNextLine(string);
/*     */         }
/*     */       } 
/*     */     }
/*     */ 
/*     */     
/*     */     public void parseNextLine(@NotNull String line) throws ExecutionException {
/* 243 */       if (line == null) $$$reportNull$$$0(2);  V8ProfileLine.Parser parser = new V8ProfileLine.Parser(line, this.myParseSelfTicks, this.myParseSelfPercent);
/* 244 */       parser.parse();
/*     */       
/* 246 */       if (!parser.isValid()) {
/* 247 */         if (this.myTop.getChildren().isEmpty())
/* 248 */           return;  throw new ExecutionException(NodeJSBundle.message("profile.cpu.parse_error.cannot_parse_line.message", new Object[] { line }));
/*     */       } 
/*     */       
/* 251 */       int offset = parser.getOffset();
/* 252 */       if (offset == this.myCurrentParent.getOffset()) {
/*     */         
/* 254 */         if (this.myCurrentParent.getParent() == null) {
/* 255 */           throw new ExecutionException(NodeJSBundle.message("profile.cpu.parse_error.unknown_child_2.message", new Object[] { line }));
/*     */         }
/* 257 */         this.myCurrentParent = V8ProfileLine.createLineOld(parser, offset, this.myCurrentParent.getParent());
/*     */         return;
/*     */       } 
/* 260 */       if (offset > this.myCurrentParent.getOffset()) {
/* 261 */         if (offset - this.myCurrentParent.getOffset() > 2) {
/* 262 */           throw new ExecutionException(NodeJSBundle.message("profile.cpu.parse_error.unknown_child_3.message", new Object[] { line }));
/*     */         }
/*     */         
/* 265 */         this.myCurrentParent = V8ProfileLine.createLineOld(parser, offset, this.myCurrentParent);
/*     */         
/*     */         return;
/*     */       } 
/* 269 */       int steps = this.myCurrentParent.getOffset() - offset + 2;
/* 270 */       while (this.myCurrentParent != null && steps > 0) {
/* 271 */         this.myCurrentParent = this.myCurrentParent.getParent();
/* 272 */         steps -= 2;
/*     */       } 
/* 274 */       if (this.myCurrentParent == null) {
/* 275 */         throw new ExecutionException(NodeJSBundle.message("profile.cpu.parse_error.unknown_child_4.message", new Object[] { line }));
/*     */       }
/* 277 */       this.myCurrentParent = V8ProfileLine.createLineOld(parser, offset, this.myCurrentParent);
/*     */     }
/*     */     
/*     */     public V8ProfileLine getTop() {
/* 281 */       return this.myTop;
/*     */     }
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private static V8ProfileLine createLineOld(Parser parser, int offset, @Nullable V8ProfileLine parent) {
/* 289 */     V8ProfileLine line = new V8ProfileLine(parser.getTotalTicks().intValue(), parser.getTotalTensPercent().intValue(), parser.getSelfTicks().intValue(), parser.getSelfTensPercent().intValue(), parent, offset, parser.getCallText(), 0L);
/* 290 */     if (parent != null) {
/* 291 */       parent.myChildren.add(line);
/*     */     }
/* 293 */     return line;
/*     */   }
/*     */   
/*     */   public static V8ProfileLine createLine(@NotNull String text, @Nullable V8ProfileLine parent, long stringId) {
/* 297 */     if (text == null) $$$reportNull$$$0(0);  Parser parser = new Parser(text);
/* 298 */     parser.parse();
/* 299 */     V8ProfileLine line = new V8ProfileLine(0, 0, 0, 0, parent, 0, parser.getCallText(), stringId);
/* 300 */     if (parent != null) {
/* 301 */       parent.myChildren.add(line);
/*     */     }
/* 303 */     return line;
/*     */   }
/*     */   
/*     */   public int getTotalTicks() {
/* 307 */     return this.myTotalTicks;
/*     */   }
/*     */   
/*     */   public int getTotalTensPercent() {
/* 311 */     return this.myTotalTensPercent;
/*     */   }
/*     */   
/*     */   public int getSelfTicks() {
/* 315 */     return this.mySelfTicks;
/*     */   }
/*     */   
/*     */   public int getSelfTensPercent() {
/* 319 */     return this.mySelfTensPercent;
/*     */   }
/*     */   
/*     */   public ExecKind getExecKind() {
/* 323 */     return this.myCall.getExecKind();
/*     */   }
/*     */   
/*     */   public String getFunctionName() {
/* 327 */     return this.myCall.getFunctionName();
/*     */   }
/*     */   
/*     */   public boolean isNative() {
/* 331 */     return this.myCall.isNative();
/*     */   }
/*     */   
/*     */   @Nullable
/*     */   public V8ProfileLine getParent() {
/* 336 */     return this.myParent;
/*     */   }
/*     */ 
/*     */   
/*     */   public List<V8ProfileLine> getChildren() {
/* 341 */     return this.myChildren;
/*     */   }
/*     */   
/*     */   public int getOffset() {
/* 345 */     return this.myOffset;
/*     */   }
/*     */   
/*     */   public V8ProfileLineFileDescriptor getFileDescriptor() {
/* 349 */     return this.myCall.getDescriptor();
/*     */   }
/*     */   
/*     */   public String getNotParsedCallable() {
/* 353 */     return this.myCall.getNotParsedCallable();
/*     */   }
/*     */   
/*     */   public int getRecursionCount() {
/* 357 */     return this.myRecursionCount;
/*     */   }
/*     */   
/*     */   public void setRecursionCount(int recursionCount) {
/* 361 */     this.myRecursionCount = recursionCount;
/*     */   }
/*     */   
/*     */   public static void dfs(V8ProfileLine root, Consumer<? super V8ProfileLine> consumer) {
/* 365 */     ArrayDeque<V8ProfileLine> queue = new ArrayDeque<>();
/* 366 */     queue.addFirst(root);
/* 367 */     while (!queue.isEmpty()) {
/* 368 */       V8ProfileLine line = queue.removeFirst();
/* 369 */       consumer.consume(line);
/* 370 */       List<V8ProfileLine> children = line.getChildren();
/* 371 */       for (int i = children.size() - 1; i >= 0; i--)
/* 372 */         queue.addFirst(children.get(i)); 
/*     */     } 
/*     */   }
/*     */   
/*     */   public static class Parser
/*     */   {
/*     */     @NotNull
/*     */     final String myLine;
/*     */     private final boolean myParseSelfTicks;
/*     */     private final boolean myParseSelfPercent;
/*     */     private final boolean myOnlyLine;
/*     */     private Integer myTotalTicks;
/*     */     private Integer myTotalTensPercent;
/* 385 */     private Integer mySelfTicks = Integer.valueOf(0);
/* 386 */     private Integer mySelfTensPercent = Integer.valueOf(0);
/*     */     
/* 388 */     private int myOffset = 0; @NlsSafe
/* 389 */     private String myCallText = "";
/*     */     private int myPosition;
/*     */     private boolean myValid;
/*     */     
/*     */     public Parser(@NotNull String line) {
/* 394 */       this.myLine = line;
/* 395 */       this.myParseSelfTicks = false;
/* 396 */       this.myParseSelfPercent = false;
/* 397 */       this.myOnlyLine = true;
/*     */     }
/*     */     
/*     */     public Parser(@NotNull String line, boolean ticks, boolean percent) {
/* 401 */       this.myLine = line;
/* 402 */       this.myParseSelfTicks = ticks;
/* 403 */       this.myParseSelfPercent = percent;
/* 404 */       this.myOnlyLine = false;
/*     */     }
/*     */     
/*     */     public void parse() {
/* 408 */       if (this.myLine.isEmpty())
/* 409 */         return;  this.myPosition = 0;
/* 410 */       if (!this.myOnlyLine) {
/* 411 */         if ((this.myTotalTicks = V8ProfileLine.parseInt(word())) == null)
/* 412 */           return;  if ((this.myTotalTensPercent = V8ProfileLine.parseTensPercent(word())) == null)
/* 413 */           return;  if (this.myParseSelfTicks && (
/* 414 */           this.mySelfTicks = V8ProfileLine.parseInt(word())) == null)
/*     */           return; 
/* 416 */         if (this.myParseSelfPercent && (
/* 417 */           this.mySelfTensPercent = V8ProfileLine.parseTensPercent(word())) == null) {
/*     */           return;
/*     */         }
/*     */       } 
/* 421 */       readOffset();
/* 422 */       this.myCallText = StringUtil.notNullize(this.myLine.substring(this.myPosition));
/* 423 */       this.myValid = true;
/*     */     }
/*     */     @Nls
/*     */     public String getCallText() {
/* 427 */       return this.myCallText;
/*     */     }
/*     */     
/*     */     public Integer getTotalTicks() {
/* 431 */       return Integer.valueOf(nonNullize(this.myTotalTicks));
/*     */     }
/*     */     
/*     */     public Integer getTotalTensPercent() {
/* 435 */       return Integer.valueOf(nonNullize(this.myTotalTensPercent));
/*     */     }
/*     */     
/*     */     public Integer getSelfTicks() {
/* 439 */       return Integer.valueOf(nonNullize(this.mySelfTicks));
/*     */     }
/*     */     
/*     */     private static int nonNullize(Integer integer) {
/* 443 */       return (integer == null) ? 0 : integer.intValue();
/*     */     }
/*     */     
/*     */     public Integer getSelfTensPercent() {
/* 447 */       return Integer.valueOf(nonNullize(this.mySelfTensPercent));
/*     */     }
/*     */     
/*     */     public int getOffset() {
/* 451 */       return this.myOffset;
/*     */     }
/*     */     
/*     */     public int getPosition() {
/* 455 */       return this.myPosition;
/*     */     }
/*     */     
/*     */     public boolean isValid() {
/* 459 */       return this.myValid;
/*     */     }
/*     */     
/*     */     private void readOffset() {
/* 463 */       int cnt = 1;
/* 464 */       while (this.myPosition < this.myLine.length() && this.myLine.charAt(this.myPosition) == ' ') {
/* 465 */         this.myPosition++;
/* 466 */         cnt++;
/*     */       } 
/* 468 */       this.myOffset = cnt;
/* 469 */       this.myValid = (this.myPosition < this.myLine.length());
/*     */     }
/*     */     
/*     */     private String word() {
/* 473 */       for (; this.myPosition < this.myLine.length() && this.myLine.charAt(this.myPosition) == ' '; this.myPosition++);
/* 474 */       int idx = this.myLine.indexOf(' ', this.myPosition);
/* 475 */       if (idx == -1) {
/* 476 */         int val = this.myPosition;
/* 477 */         this.myPosition = this.myLine.length();
/* 478 */         return this.myLine.substring(val);
/*     */       } 
/* 480 */       String word = this.myLine.substring(this.myPosition, idx).trim();
/* 481 */       this.myPosition = idx + 1;
/* 482 */       return word;
/*     */     }
/*     */   }
/*     */   
/*     */   private static Integer parseInt(@NotNull String s) {
/* 487 */     if (s == null) $$$reportNull$$$0(1);  if (StringUtil.isEmptyOrSpaces(s)) return null; 
/*     */     try {
/* 489 */       return Integer.valueOf(Integer.parseInt(s));
/*     */     }
/* 491 */     catch (NumberFormatException e) {
/* 492 */       return null;
/*     */     } 
/*     */   }
/*     */   
/*     */   private static Integer parseTensPercent(@NotNull String s) {
/* 497 */     if (s == null) $$$reportNull$$$0(2);  if (StringUtil.isEmptyOrSpaces(s)) return null; 
/* 498 */     if (!s.endsWith("%")) return null; 
/* 499 */     s = s.substring(0, s.length() - 1);
/* 500 */     int dot = s.indexOf('.');
/* 501 */     if (dot < 0) {
/* 502 */       Integer integer1 = parseInt(s);
/* 503 */       return (integer1 == null) ? null : Integer.valueOf(integer1.intValue() * 10);
/*     */     } 
/* 505 */     if (dot == 0) {
/* 506 */       return getTens(s.substring(1));
/*     */     }
/*     */     
/* 509 */     Integer integer = parseInt(s.substring(0, dot));
/* 510 */     Integer tens = getTens(s.substring(dot + 1));
/* 511 */     return (integer == null || tens == null) ? null : Integer.valueOf(integer.intValue() * 10 + tens.intValue());
/*     */   }
/*     */ 
/*     */   
/*     */   private static Integer getTens(@NotNull String s) {
/* 516 */     if (s == null) $$$reportNull$$$0(3);  Integer tens = parseInt(s.substring(0, 1));
/* 517 */     if (tens == null) return null; 
/* 518 */     return tens;
/*     */   }
/*     */   
/*     */   public enum ExecKind {
/* 522 */     Function, Stub, LazyCompile, Script, Eval, unknown;
/*     */ 
/*     */     
/*     */     @NotNull
/*     */     public static ExecKind safeValueOf(@NotNull String s) {
/* 527 */       if (s == null) $$$reportNull$$$0(0);  try { if (valueOf(s) == null) $$$reportNull$$$0(1);  return valueOf(s); }
/*     */       
/* 529 */       catch (IllegalArgumentException e)
/* 530 */       { if (unknown == null) $$$reportNull$$$0(2);  return unknown; }
/*     */     
/*     */     }
/*     */   }
/*     */ }


/* Location:              C:\Program Files\JetBrains\WebStorm 2021.1\plugins\NodeJS\lib\NodeJS.jar!\com\jetbrains\nodejs\run\profile\cpu\calculation\V8ProfileLine.class
 * Java compiler version: 11 (55.0)
 * JD-Core Version:       1.1.3
 */