package icons;

import com.intellij.ui.IconManager;

import javax.swing.Icon;

import org.jetbrains.annotations.NotNull;

public final class NodeJSIcons {
    @NotNull
    private static Icon load(@NotNull String path, long cacheKey, int flags) {
        return IconManager.getInstance().loadRasterizedIcon(path, NodeJSIcons.class.getClassLoader(), cacheKey, flags);
    }

    @NotNull
    public static final Icon Mocha = load("org/bipolar/icons/mocha.svg", -1827434342563603944L, 1);
    @NotNull
    public static final Icon Navigate_inMainTree = load("org/bipolar/icons/Navigate_inMainTree.svg", 5641951568480195299L, 2);
    @NotNull
    public static final Icon Nodeunit = load("org/bipolar/icons/nodeunit.svg", 7814492814797491816L, 0);
    @NotNull
    public static final Icon OpenV8HeapSnapshot = load("org/bipolar/icons/OpenV8HeapSnapshot.svg", 7050082413647762914L, 2);
    @NotNull
    public static final Icon OpenV8HeapSnapshot_ToolWin = load("org/bipolar/icons/OpenV8HeapSnapshot_ToolWin.svg", 7547011968384017495L, 2);
    @NotNull
    public static final Icon OpenV8ProfilingLog = load("org/bipolar/icons/OpenV8ProfilingLog.svg", 5758168910874833400L, 2);
    @NotNull
    public static final Icon OpenV8ProfilingLog_ToolWin = load("org/bipolar/icons/OpenV8ProfilingLog_ToolWin.svg", 9142163747108453921L, 2);
    @NotNull
    public static final Icon V8 = load("org/bipolar/icons/v8.svg", 4863646977640017376L, 2);
    @NotNull
    public static final Icon V8_ToolWin = load("org/bipolar/icons/v8_ToolWin.svg", -9068928094669779538L, 2);
}
