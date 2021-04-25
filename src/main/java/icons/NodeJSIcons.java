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
    public static final Icon Bipolar = load("org/bipolar/icons/mocha.svg", -1827434342563603944L, 1);
}
