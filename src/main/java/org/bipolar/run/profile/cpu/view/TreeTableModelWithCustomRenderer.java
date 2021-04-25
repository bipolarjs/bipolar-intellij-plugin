package org.bipolar.run.profile.cpu.view;

import com.intellij.ui.treeStructure.treetable.TreeTableModel;
import javax.swing.table.TableCellRenderer;

public interface TreeTableModelWithCustomRenderer extends TreeTableModel {
  TableCellRenderer getCustomizedRenderer(int paramInt, Object paramObject, TableCellRenderer paramTableCellRenderer);
}


/* Location:              C:\Program Files\JetBrains\WebStorm 2021.1\plugins\NodeJS\lib\NodeJS.jar!\com\jetbrains\nodejs\run\profile\cpu\view\TreeTableModelWithCustomRenderer.class
 * Java compiler version: 11 (55.0)
 * JD-Core Version:       1.1.3
 */