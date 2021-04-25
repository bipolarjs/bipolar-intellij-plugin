/*    */ package org.bipolar.run;
/*    */ 
/*    */ import com.intellij.javascript.nodejs.interpreter.NodeJsInterpreterRef;
/*    */ import com.intellij.openapi.components.BaseState;
/*    */ import com.intellij.util.xmlb.annotations.Attribute;
/*    */
/*    */
/*    */ import kotlin.properties.ReadWriteProperty;
/*    */
/*    */ import org.jetbrains.annotations.Nullable;
/*    */ 
/*    */ @Metadata(mv = {1, 4, 2}, bv = {1, 0, 3}, k = 1, d1 = {"\000$\n\002\030\002\n\002\030\002\n\002\b\002\n\002\020\016\n\002\b\007\n\002\030\002\n\002\030\002\n\002\030\002\n\002\b\025\b\000\030\0002\0020\001B\005¢\006\002\020\002R/\020\005\032\004\030\0010\0042\b\020\003\032\004\030\0010\0048G@FX\002¢\006\022\n\004\b\n\020\013\032\004\b\006\020\007\"\004\b\b\020\tRS\020\021\032\0260\f¢\006\002\b\r¢\006\f\b\016\022\b\b\017\022\004\b\b(\0202\032\020\003\032\0260\f¢\006\002\b\r¢\006\f\b\016\022\b\b\017\022\004\b\b(\0208G@FX\002¢\006\022\n\004\b\026\020\013\032\004\b\022\020\023\"\004\b\024\020\025R/\020\027\032\004\030\0010\0042\b\020\003\032\004\030\0010\0048G@FX\002¢\006\022\n\004\b\032\020\013\032\004\b\030\020\007\"\004\b\031\020\tR/\020\033\032\004\030\0010\0042\b\020\003\032\004\030\0010\0048G@FX\002¢\006\022\n\004\b\036\020\013\032\004\b\034\020\007\"\004\b\035\020\tR/\020\037\032\004\030\0010\0042\b\020\003\032\004\030\0010\0048G@FX\002¢\006\022\n\004\b\"\020\013\032\004\b \020\007\"\004\b!\020\t¨\006#"}, d2 = {"Lcom/jetbrains/nodejs/run/NodeJsRunConfigurationState;", "Lcom/intellij/execution/configurations/LocatableRunConfigurationOptions;", "()V", "<set-?>", "", "applicationParameters", "getApplicationParameters", "()Ljava/lang/String;", "setApplicationParameters", "(Ljava/lang/String;)V", "applicationParameters$delegate", "Lkotlin/properties/ReadWriteProperty;", "Lcom/intellij/javascript/nodejs/interpreter/NodeJsInterpreterRef;", "Lorg/jetbrains/annotations/NotNull;", "Lkotlin/ParameterName;", "name", "value", "interpreterRef", "getInterpreterRef", "()Lcom/intellij/javascript/nodejs/interpreter/NodeJsInterpreterRef;", "setInterpreterRef", "(Lcom/intellij/javascript/nodejs/interpreter/NodeJsInterpreterRef;)V", "interpreterRef$delegate", "nodeParameters", "getNodeParameters", "setNodeParameters", "nodeParameters$delegate", "pathToJsFile", "getPathToJsFile", "setPathToJsFile", "pathToJsFile$delegate", "workingDir", "getWorkingDir", "setWorkingDir", "workingDir$delegate", "intellij.nodeJS"})
/*    */ public final class NodeJsRunConfigurationState extends LocatableRunConfigurationOptions {
/*    */   @Metadata(mv = {1, 4, 2}, bv = {1, 0, 3}, k = 3, d1 = {"\000\022\n\000\n\002\020\013\n\000\n\002\030\002\n\002\030\002\n\000\020\000\032\0020\0012\013\020\002\032\0070\003¢\006\002\b\004H\n¢\006\002\b\005"}, d2 = {"<anonymous>", "", "it", "Lcom/intellij/javascript/nodejs/interpreter/NodeJsInterpreterRef;", "Lorg/jetbrains/annotations/NotNull;", "invoke"})
/* 15 */   static final class NodeJsRunConfigurationState$interpreterRef$2 extends Lambda implements Function1<NodeJsInterpreterRef, Boolean> { public final boolean invoke(@NotNull NodeJsInterpreterRef it) { Intrinsics.checkNotNullParameter(it, "it"); return it.isProjectRef(); } public static final NodeJsRunConfigurationState$interpreterRef$2 INSTANCE = new NodeJsRunConfigurationState$interpreterRef$2(); NodeJsRunConfigurationState$interpreterRef$2() { super(1); } } @Nullable private final ReadWriteProperty workingDir$delegate = BaseState.string$default((BaseState)this, null, 1, null).provideDelegate(this, $$delegatedProperties[0]); @NotNull private final ReadWriteProperty interpreterRef$delegate = property(NodeJsInterpreterRef.createProjectRef(), NodeJsRunConfigurationState$interpreterRef$2.INSTANCE).provideDelegate(this, $$delegatedProperties[1]);
/*    */   @Nullable
/* 17 */   private final ReadWriteProperty pathToJsFile$delegate = BaseState.string$default((BaseState)this, null, 1, null).provideDelegate(this, $$delegatedProperties[2]);
/*    */   @Nullable
/* 19 */   private final ReadWriteProperty applicationParameters$delegate = BaseState.string$default((BaseState)this, null, 1, null).provideDelegate(this, $$delegatedProperties[3]);
/*    */   @Nullable
/* 21 */   private final ReadWriteProperty nodeParameters$delegate = BaseState.string$default((BaseState)this, null, 1, null).provideDelegate(this, $$delegatedProperties[4]);
/*    */   
/*    */   @Attribute("working-dir")
/*    */   @Nullable
/*    */   public final String getWorkingDir() {
/*    */     return (String)this.workingDir$delegate.getValue(this, $$delegatedProperties[0]);
/*    */   }
/*    */   
/*    */   public final void setWorkingDir(@Nullable String <set-?>) {
/*    */     this.workingDir$delegate.setValue(this, $$delegatedProperties[0], <set-?>);
/*    */   }
/*    */   
/*    */   @Attribute(value = "path-to-node", converter = NodeJsInterpreterRefConverter.class)
/*    */   @NotNull
/*    */   public final NodeJsInterpreterRef getInterpreterRef() {
/*    */     return (NodeJsInterpreterRef)this.interpreterRef$delegate.getValue(this, $$delegatedProperties[1]);
/*    */   }
/*    */   
/*    */   public final void setInterpreterRef(@NotNull NodeJsInterpreterRef <set-?>) {
/*    */     Intrinsics.checkNotNullParameter(<set-?>, "<set-?>");
/*    */     this.interpreterRef$delegate.setValue(this, $$delegatedProperties[1], <set-?>);
/*    */   }
/*    */   
/*    */   @Attribute("path-to-js-file")
/*    */   @Nullable
/*    */   public final String getPathToJsFile() {
/*    */     return (String)this.pathToJsFile$delegate.getValue(this, $$delegatedProperties[2]);
/*    */   }
/*    */   
/*    */   public final void setPathToJsFile(@Nullable String <set-?>) {
/*    */     this.pathToJsFile$delegate.setValue(this, $$delegatedProperties[2], <set-?>);
/*    */   }
/*    */   
/*    */   @Attribute("application-parameters")
/*    */   @Nullable
/*    */   public final String getApplicationParameters() {
/*    */     return (String)this.applicationParameters$delegate.getValue(this, $$delegatedProperties[3]);
/*    */   }
/*    */   
/*    */   public final void setApplicationParameters(@Nullable String <set-?>) {
/*    */     this.applicationParameters$delegate.setValue(this, $$delegatedProperties[3], <set-?>);
/*    */   }
/*    */   
/*    */   @Attribute("node-parameters")
/*    */   @Nullable
/*    */   public final String getNodeParameters() {
/*    */     return (String)this.nodeParameters$delegate.getValue(this, $$delegatedProperties[4]);
/*    */   }
/*    */   
/*    */   public final void setNodeParameters(@Nullable String <set-?>) {
/*    */     this.nodeParameters$delegate.setValue(this, $$delegatedProperties[4], <set-?>);
/*    */   }
/*    */ }


/* Location:              C:\Program Files\JetBrains\WebStorm 2021.1\plugins\NodeJS\lib\NodeJS.jar!\com\jetbrains\nodejs\run\NodeJsRunConfigurationState.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */