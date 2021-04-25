/*     */ package org.bipolar.run.profile.cpu.calculation;
/*     */ 
/*     */ import com.intellij.openapi.util.text.StringUtil;
/*     */ import org.bipolar.run.profile.cpu.v8log.data.CodeState;
/*     */ import org.bipolar.run.profile.cpu.v8log.data.V8CpuLogCall;
/*     */ import org.bipolar.run.profile.heap.io.RawSerializer;
/*     */ import java.io.DataInput;
/*     */ import java.io.DataOutput;
/*     */ import java.io.IOException;
/*     */ import java.util.ArrayDeque;
/*     */ import java.util.ArrayList;
/*     */ import java.util.Collections;
/*     */ import java.util.List;
/*     */
import org.jetbrains.annotations.NotNull;
/*     */ import org.jetbrains.annotations.Nullable;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class CallTreesSerializer
/*     */   implements RawSerializer<V8ProfileLine>
/*     */ {
/*     */   public long getRecordSize() {
/*  25 */     return -1L;
/*     */   }
/*     */ 
/*     */   
/*     */   public void write(@NotNull DataOutput os, @NotNull V8ProfileLine line) throws IOException {
/*  30 */     if (os == null) $$$reportNull$$$0(0);  if (line == null) $$$reportNull$$$0(1);  ArrayDeque<V8ProfileLine> queue = new ArrayDeque<>();
/*  31 */     queue.add(line);
/*  32 */     while (!queue.isEmpty()) {
/*  33 */       V8ProfileLine current = queue.removeFirst();
/*  34 */       writeLine(os, current);
/*  35 */       os.writeInt(current.getChildren().size());
/*     */       
/*  37 */       List<V8ProfileLine> children = new ArrayList<>(current.getChildren());
/*  38 */       Collections.reverse(children);
/*  39 */       for (V8ProfileLine child : children) {
/*  40 */         queue.addFirst(child);
/*     */       }
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   public V8ProfileLine read(@NotNull DataInput is) throws IOException {
/*  47 */     if (is == null) $$$reportNull$$$0(2);  V8ProfileLine root = null;
/*  48 */     ArrayDeque<V8ProfileLine> queue = new ArrayDeque<>();
/*  49 */     queue.add(new V8ProfileLine(0, 0, 0, 0, null, 0, "", -1L));
/*  50 */     while (!queue.isEmpty()) {
/*  51 */       V8ProfileLine parent = queue.removeFirst();
/*  52 */       V8ProfileLine line = readLine(is, parent);
/*  53 */       if (root == null) {
/*  54 */         root = line;
/*     */       } else {
/*  56 */         parent.getChildren().add(line);
/*     */       } 
/*  58 */       int numChildren = is.readInt();
/*  59 */       for (int i = 0; i < numChildren; i++) {
/*  60 */         queue.addFirst(line);
/*     */       }
/*     */     } 
/*  63 */     return root;
/*     */   }
/*     */   
/*     */   public static void writeLine(@NotNull DataOutput os, @NotNull V8ProfileLine line) throws IOException {
/*  67 */     if (os == null) $$$reportNull$$$0(3);  if (line == null) $$$reportNull$$$0(4);  V8CpuLogCall call = line.getCall();
/*     */     
/*  69 */     writeCall(os, call);
/*     */     
/*  71 */     os.writeInt(line.getTotalTicks());
/*  72 */     os.writeInt(line.getTotalTensPercent());
/*  73 */     os.writeInt(line.getSelfTicks());
/*  74 */     os.writeInt(line.getSelfTensPercent());
/*     */   }
/*     */   
/*     */   public static void writeCall(@NotNull DataOutput os, V8CpuLogCall call) throws IOException {
/*  78 */     if (os == null) $$$reportNull$$$0(5);  os.writeLong(call.getStringId());
/*  79 */     os.writeUTF(call.getExecKind().name());
/*  80 */     os.writeUTF(StringUtil.notNullize(call.getFunctionName()));
/*  81 */     os.writeBoolean(call.isNative());
/*  82 */     os.writeBoolean(call.isNotNavigatable());
/*     */     
/*  84 */     V8ProfileLineFileDescriptor descriptor = call.getDescriptor();
/*  85 */     os.writeBoolean((descriptor != null));
/*  86 */     if (descriptor != null) {
/*  87 */       os.writeUTF(descriptor.getName());
/*  88 */       os.writeUTF(StringUtil.notNullize(descriptor.getPath()));
/*  89 */       os.writeInt(descriptor.getRow());
/*  90 */       os.writeInt(descriptor.getCol());
/*     */     } 
/*  92 */     os.writeUTF(StringUtil.notNullize(call.getNotParsedCallable()));
/*  93 */     os.writeUTF(call.getCodeState().name());
/*     */   }
/*     */   
/*     */   public static V8ProfileLine readLine(@NotNull DataInput is, @Nullable V8ProfileLine parent) throws IOException {
/*  97 */     if (is == null) $$$reportNull$$$0(6);  V8CpuLogCall call = readCall(is);
/*     */     
/*  99 */     int totalTicks = is.readInt();
/* 100 */     int totalPercent = is.readInt();
/* 101 */     int selfTicks = is.readInt();
/* 102 */     int selfPercent = is.readInt();
/* 103 */     return new V8ProfileLine(totalTicks, totalPercent, selfTicks, selfPercent, parent, 0, call);
/*     */   }
/*     */   
/*     */   @NotNull
/*     */   public static V8CpuLogCall readCall(@NotNull DataInput is) throws IOException {
/* 108 */     if (is == null) $$$reportNull$$$0(7);  long stringId = is.readLong();
/* 109 */     V8ProfileLine.ExecKind execKind = V8ProfileLine.ExecKind.safeValueOf(is.readUTF());
/* 110 */     execKind = (execKind == null) ? V8ProfileLine.ExecKind.unknown : execKind;
/* 111 */     String functionName = readNullize(is);
/* 112 */     boolean isNative = is.readBoolean();
/* 113 */     boolean isNotNavigable = is.readBoolean();
/*     */     
/* 115 */     boolean haveDescriptor = is.readBoolean();
/* 116 */     V8ProfileLineFileDescriptor descriptor = null;
/* 117 */     if (haveDescriptor) {
/* 118 */       String fileName = is.readUTF();
/* 119 */       String path = readNullize(is);
/* 120 */       int row = is.readInt();
/* 121 */       int col = is.readInt();
/* 122 */       descriptor = new V8ProfileLineFileDescriptor(fileName, path, row, col);
/*     */     } 
/* 124 */     String nonParsedCallable = is.readUTF();
/* 125 */     String codeStateName = is.readUTF();
/* 126 */     CodeState codeState = CodeState.safeValueOf(codeStateName);
/* 127 */     if (codeState == null) throw new IOException("Wrong code state: " + codeStateName); 
/* 128 */     return new V8CpuLogCall(execKind, functionName, isNative, isNotNavigable, descriptor, nonParsedCallable, codeState, stringId);
/*     */   }
/*     */   
/*     */   private static String readNullize(@NotNull DataInput is) throws IOException {
/* 132 */     if (is == null) $$$reportNull$$$0(8);  String s = is.readUTF();
/* 133 */     return StringUtil.isEmptyOrSpaces(s) ? null : s;
/*     */   }
/*     */ }


/* Location:              C:\Program Files\JetBrains\WebStorm 2021.1\plugins\NodeJS\lib\NodeJS.jar!\com\jetbrains\nodejs\run\profile\cpu\calculation\CallTreesSerializer.class
 * Java compiler version: 11 (55.0)
 * JD-Core Version:       1.1.3
 */