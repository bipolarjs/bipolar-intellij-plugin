/*    */ package org.bipolar.run.profile.cpu.v8log.ui;
/*    */ 
/*    */ import com.intellij.openapi.util.io.FileUtil;
/*    */ import java.io.File;
/*    */ import java.io.IOException;
/*    */ import java.util.List;
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public final class GenerateColors
/*    */ {
/*    */   public static void main(String[] args) throws IOException {
/* 14 */     File file = new File(args[0]);
/* 15 */     List<String> lines = FileUtil.loadLines(file);
/* 16 */     for (String line : lines) {
/* 17 */       String code = "0x" + line.substring(1);
/* 18 */       System.out.println("new JBColor(new Color(" + code + "), new Color(" + code + ")),");
/*    */     } 
/*    */   }
/*    */ 
/*    */   
/*    */   private static void generateGrey() {
/* 24 */     int cnt = 220;
/* 25 */     while (cnt >= 60) {
/* 26 */       String code = "" + cnt + "," + cnt + "," + cnt;
/* 27 */       System.out.println("new JBColor(new Color(" + code + "), new Color(" + code + ")),");
/* 28 */       cnt -= 10;
/*    */     } 
/*    */   }
/*    */ }


/* Location:              C:\Program Files\JetBrains\WebStorm 2021.1\plugins\NodeJS\lib\NodeJS.jar!\com\jetbrains\nodejs\run\profile\cpu\v8lo\\ui\GenerateColors.class
 * Java compiler version: 11 (55.0)
 * JD-Core Version:       1.1.3
 */