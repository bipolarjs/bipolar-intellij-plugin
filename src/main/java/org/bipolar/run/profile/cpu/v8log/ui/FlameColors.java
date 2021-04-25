/*     */ package org.bipolar.run.profile.cpu.v8log.ui;
/*     */ 
/*     */ import com.intellij.ui.Gray;
/*     */ import com.intellij.ui.JBColor;
/*     */ import org.bipolar.run.profile.cpu.v8log.data.V8CodeScope;
/*     */ import java.awt.Color;
/*     */ import java.util.ArrayList;
/*     */ import java.util.Arrays;
/*     */ import java.util.Collections;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public final class FlameColors
/*     */ {
/*  17 */   public static final Color[] EVENTS_COLORS = new Color[] { (Color)new JBColor(new Color(16745131), new Color(16745131)), (Color)new JBColor((Color)Gray._120, (Color)Gray._120), (Color)new JBColor(new Color(12578815), new Color(12578815)), (Color)new JBColor(new Color(10157978), new Color(10157978)) };
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*  23 */   public static final JBColor[] COLORS = new JBColor[] { new JBColor(new Color(11390688), new Color(68, 137, 206)), new JBColor(new Color(16776901), new Color(159, 107, 0)), new JBColor(new Color(11661737), new Color(159, 137, 0)), new JBColor(new Color(9424842), new Color(0, 137, 137)), new JBColor(new Color(9041851), new Color(0, 107, 117)), new JBColor(new Color(12576601), new Color(98, 150, 85)), new JBColor(new Color(16757719), new Color(120, 170, 115)), new JBColor(new Color(14601469), new Color(151, 118, 169)), new JBColor(new Color(12435455), new Color(121, 98, 119)), new JBColor(new Color(16762544), new Color(200, 80, 80)), new JBColor(new Color(16743813), new Color(200, 80, 80)), new JBColor(new Color(16756094), new Color(220, 130, 70)), new JBColor(new Color(13616704), new Color(138, 138, 0)) };
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
/*  38 */   public static final JBColor OTHER_COLOR = new JBColor(new Color(197, 255, 227), new Color(197, 255, 227));
/*  39 */   public static final JBColor IDLE_COLOR = new JBColor(new Color(16777215), new Color(16777215));
/*     */   
/*  41 */   public static final JBColor[] NODE_COLORS = new JBColor[] { new JBColor(new Color(4286945), new Color(4286945)), new JBColor(new Color(4749055), new Color(4749055)), new JBColor(new Color(4419310), new Color(4419310)), new JBColor(new Color(6591981), new Color(6591981)), new JBColor(new Color(2003199), new Color(2003199)), new JBColor(new Color(1869550), new Color(1869550)), new JBColor(new Color(4620980), new Color(4620980)), new JBColor(new Color(6535423), new Color(6535423)), new JBColor(new Color(6073582), new Color(6073582)), new JBColor(new Color(5215437), new Color(5215437)), new JBColor(new Color(8900346), new Color(8900346)), new JBColor(new Color(11592447), new Color(11592447)), new JBColor(new Color(10802158), new Color(10802158)), new JBColor(new Color(8900351), new Color(8900351)), new JBColor(new Color(8306926), new Color(8306926)), new JBColor(new Color(7120589), new Color(7120589)), new JBColor(new Color(8900331), new Color(8900331)), new JBColor(new Color(49151), new Color(49151)), new JBColor(new Color(45806), new Color(45806)), new JBColor(new Color(39629), new Color(39629)), new JBColor(new Color(3383753), new Color(3383753)), new JBColor(new Color(12578815), new Color(12578815)), new JBColor(new Color(10024447), new Color(10024447)), new JBColor(new Color(9364974), new Color(9364974)), new JBColor(new Color(62975), new Color(62975)), new JBColor(new Color(12320767), new Color(12320767)), new JBColor(new Color(9961471), new Color(9961471)), new JBColor(new Color(9301742), new Color(9301742)), new JBColor(new Color(65535), new Color(65535)), new JBColor(new Color(61166), new Color(61166)), new JBColor(new Color(52685), new Color(52685)), new JBColor(new Color(4772300), new Color(4772300)), new JBColor(new Color(2142890), new Color(2142890)), new JBColor(new Color(239774), new Color(239774)), new JBColor(new Color(4251856), new Color(4251856)) };
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
/*  79 */   public static final JBColor[] V8_COLORS = new JBColor[] { new JBColor((Color)Gray._220, (Color)Gray._220), new JBColor((Color)Gray._210, (Color)Gray._210), new JBColor((Color)Gray._200, (Color)Gray._200), new JBColor((Color)Gray._190, (Color)Gray._190), new JBColor((Color)Gray._180, (Color)Gray._180), new JBColor((Color)Gray._170, (Color)Gray._170), new JBColor((Color)Gray._160, (Color)Gray._160), new JBColor((Color)Gray._150, (Color)Gray._150), new JBColor((Color)Gray._140, (Color)Gray._140), new JBColor((Color)Gray._130, (Color)Gray._130), new JBColor((Color)Gray._120, (Color)Gray._120), new JBColor((Color)Gray._110, (Color)Gray._110), new JBColor((Color)Gray._100, (Color)Gray._100) };
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
/*  95 */   public static final JBColor[] CODE_COLORS = new JBColor[] { new JBColor(new Color(16758465), new Color(16758465)), new JBColor(new Color(16756409), new Color(16756409)), new JBColor(new Color(15639213), new Color(15639213)), new JBColor(new Color(13470869), new Color(13470869)), new JBColor(new Color(16761035), new Color(16761035)), new JBColor(new Color(16758213), new Color(16758213)), new JBColor(new Color(15641016), new Color(15641016)), new JBColor(new Color(13472158), new Color(13472158)), new JBColor(new Color(14381203), new Color(14381203)), new JBColor(new Color(16745131), new Color(16745131)), new JBColor(new Color(15628703), new Color(15628703)), new JBColor(new Color(13461641), new Color(13461641)), new JBColor(new Color(16727702), new Color(16727702)), new JBColor(new Color(15612556), new Color(15612556)), new JBColor(new Color(13447800), new Color(13447800)), new JBColor(new Color(16738740), new Color(16738740)), new JBColor(new Color(15624871), new Color(15624871)), new JBColor(new Color(13459600), new Color(13459600)), new JBColor(new Color(14315734), new Color(14315734)), new JBColor(new Color(16745466), new Color(16745466)), new JBColor(new Color(15629033), new Color(15629033)), new JBColor(new Color(13461961), new Color(13461961)), new JBColor(new Color(15631086), new Color(15631086)), new JBColor(new Color(12211667), new Color(12211667)), new JBColor(new Color(14706431), new Color(14706431)), new JBColor(new Color(13721582), new Color(13721582)), new JBColor(new Color(11817677), new Color(11817677)), new JBColor(new Color(12533503), new Color(12533503)), new JBColor(new Color(11680494), new Color(11680494)), new JBColor(new Color(10105549), new Color(10105549)), new JBColor(new Color(9055202), new Color(9055202)), new JBColor(new Color(10170623), new Color(10170623)), new JBColor(new Color(9514222), new Color(9514222)), new JBColor(new Color(64154), new Color(64154)), new JBColor(new Color(65407), new Color(65407)), new JBColor(new Color(61046), new Color(61046)), new JBColor(new Color(52582), new Color(52582)), new JBColor(new Color(35653), new Color(35653)), new JBColor(new Color(3978097), new Color(3978097)), new JBColor(new Color(5570463), new Color(5570463)), new JBColor(new Color(5172884), new Color(5172884)), new JBColor(new Color(4443520), new Color(4443520)), new JBColor(new Color(3050327), new Color(3050327)), new JBColor(new Color(51543), new Color(51543)), new JBColor(new Color(4034880), new Color(4034880)), new JBColor(new Color(10025880), new Color(10025880)), new JBColor(new Color(10157978), new Color(10157978)), new JBColor(new Color(9498256), new Color(9498256)), new JBColor(new Color(8179068), new Color(8179068)), new JBColor(new Color(3329330), new Color(3329330)), new JBColor(new Color(2263842), new Color(2263842)), new JBColor(new Color(52480), new Color(52480)), new JBColor(new Color(35584), new Color(35584)), new JBColor(new Color(32768), new Color(32768)), new JBColor(new Color(3178516), new Color(3178516)), new JBColor(new Color(8190976), new Color(8190976)), new JBColor(new Color(8388352), new Color(8388352)), new JBColor(new Color(7794176), new Color(7794176)), new JBColor(new Color(6737152), new Color(6737152)), new JBColor(new Color(4557568), new Color(4557568)), new JBColor(new Color(11403055), new Color(11403055)), new JBColor(new Color(13303664), new Color(13303664)), new JBColor(new Color(12381800), new Color(12381800)), new JBColor(new Color(12648254), new Color(12648254)), new JBColor(new Color(11791930), new Color(11791930)), new JBColor(new Color(10145074), new Color(10145074)), new JBColor(new Color(16776960), new Color(16776960)), new JBColor(new Color(15658496), new Color(15658496)), new JBColor(new Color(13487360), new Color(13487360)), new JBColor(new Color(9145088), new Color(9145088)), new JBColor(new Color(8421376), new Color(8421376)), new JBColor(new Color(16774799), new Color(16774799)), new JBColor(new Color(15656581), new Color(15656581)), new JBColor(new Color(13485683), new Color(13485683)), new JBColor(new Color(15787660), new Color(15787660)), new JBColor(new Color(16772235), new Color(16772235)), new JBColor(new Color(15654018), new Color(15654018)), new JBColor(new Color(13483632), new Color(13483632)), new JBColor(new Color(14929751), new Color(14929751)), new JBColor(new Color(16766720), new Color(16766720)), new JBColor(new Color(15649024), new Color(15649024)), new JBColor(new Color(13479168), new Color(13479168)), new JBColor(new Color(9139456), new Color(9139456)), new JBColor(new Color(14329120), new Color(14329120)), new JBColor(new Color(16761125), new Color(16761125)), new JBColor(new Color(15643682), new Color(15643682)), new JBColor(new Color(13474589), new Color(13474589)), new JBColor(new Color(9136404), new Color(9136404)), new JBColor(new Color(12092939), new Color(12092939)), new JBColor(new Color(16759055), new Color(16759055)), new JBColor(new Color(15641870), new Color(15641870)), new JBColor(new Color(13473036), new Color(13473036)), new JBColor(new Color(16753920), new Color(16753920)), new JBColor(new Color(15636992), new Color(15636992)), new JBColor(new Color(13468928), new Color(13468928)), new JBColor(new Color(16750866), new Color(16750866)), new JBColor(new Color(14919785), new Color(14919785)), new JBColor(new Color(15569185), new Color(15569185)), new JBColor(new Color(16747520), new Color(16747520)), new JBColor(new Color(16744192), new Color(16744192)), new JBColor(new Color(15627776), new Color(15627776)), new JBColor(new Color(13460992), new Color(13460992)), new JBColor(new Color(16744448), new Color(16744448)), new JBColor(new Color(16753999), new Color(16753999)), new JBColor(new Color(15637065), new Color(15637065)), new JBColor(new Color(13468991), new Color(13468991)), new JBColor(new Color(16032864), new Color(16032864)), new JBColor(new Color(13066516), new Color(13066516)), new JBColor(new Color(13789470), new Color(13789470)), new JBColor(new Color(16744228), new Color(16744228)), new JBColor(new Color(15627809), new Color(15627809)), new JBColor(new Color(13461021), new Color(13461021)), new JBColor(new Color(16743744), new Color(16743744)), new JBColor(new Color(16736515), new Color(16736515)), new JBColor(new Color(16745031), new Color(16745031)), new JBColor(new Color(15628610), new Color(15628610)), new JBColor(new Color(13461561), new Color(13461561)), new JBColor(new Color(16752762), new Color(16752762)), new JBColor(new Color(15635826), new Color(15635826)), new JBColor(new Color(13468002), new Color(13468002)), new JBColor(new Color(16744272), new Color(16744272)), new JBColor(new Color(16729344), new Color(16729344)), new JBColor(new Color(15613952), new Color(15613952)), new JBColor(new Color(13448960), new Color(13448960)), new JBColor(new Color(15308410), new Color(15308410)), new JBColor(new Color(16747625), new Color(16747625)), new JBColor(new Color(15630946), new Color(15630946)), new JBColor(new Color(13463636), new Color(13463636)), new JBColor(new Color(16740950), new Color(16740950)), new JBColor(new Color(15624784), new Color(15624784)), new JBColor(new Color(13458245), new Color(13458245)), new JBColor(new Color(16737095), new Color(16737095)), new JBColor(new Color(15621186), new Color(15621186)), new JBColor(new Color(13455161), new Color(13455161)), new JBColor(new Color(16416882), new Color(16416882)), new JBColor(new Color(15761536), new Color(15761536)), new JBColor(new Color(13458524), new Color(13458524)), new JBColor(new Color(16738922), new Color(16738922)), new JBColor(new Color(15623011), new Color(15623011)), new JBColor(new Color(13456725), new Color(13456725)), new JBColor(new Color(10824234), new Color(10824234)), new JBColor(new Color(16728128), new Color(16728128)), new JBColor(new Color(15612731), new Color(15612731)), new JBColor(new Color(13447987), new Color(13447987)), new JBColor(new Color(11674146), new Color(11674146)), new JBColor(new Color(16724016), new Color(16724016)), new JBColor(new Color(15608876), new Color(15608876)), new JBColor(new Color(13444646), new Color(13444646)), new JBColor(new Color(13434880), new Color(13434880)) };
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
/*     */   static {
/* 248 */     shuffleArray(V8_COLORS);
/* 249 */     shuffleArray(NODE_COLORS);
/* 250 */     shuffleArray(CODE_COLORS);
/*     */   }
/*     */   
/*     */   private static void shuffleArray(JBColor[] arr) {
/* 254 */     ArrayList<JBColor> list = new ArrayList<>(Arrays.asList(arr));
/* 255 */     Collections.shuffle(list);
/* 256 */     for (int i = 0; i < list.size(); i++) {
/* 257 */       JBColor color = list.get(i);
/* 258 */       arr[i] = color;
/*     */     } 
/*     */   }
/*     */   
/*     */   public static JBColor getColor(long id, V8CodeScope scope) {
/*     */     JBColor[] arr;
/* 264 */     if (V8CodeScope.gc.equals(scope)) { arr = new JBColor[] { new JBColor((Color)JBColor.BLACK, (Color)JBColor.WHITE) }; }
/* 265 */     else if (V8CodeScope.stackTraceCut.equals(scope)) { arr = new JBColor[] { new JBColor((Color)JBColor.WHITE, (Color)JBColor.BLACK) }; }
/* 266 */     else if (V8CodeScope.v8.equals(scope)) { arr = V8_COLORS; }
/* 267 */     else if (V8CodeScope.node.equals(scope)) { arr = NODE_COLORS; }
/* 268 */     else { arr = CODE_COLORS; }
/*     */     
/* 270 */     long idx = id % arr.length;
/* 271 */     return arr[(int)idx];
/*     */   }
/*     */ }


/* Location:              C:\Program Files\JetBrains\WebStorm 2021.1\plugins\NodeJS\lib\NodeJS.jar!\com\jetbrains\nodejs\run\profile\cpu\v8lo\\ui\FlameColors.class
 * Java compiler version: 11 (55.0)
 * JD-Core Version:       1.1.3
 */