package org.trade.ui.tables.renderer;

import org.trade.core.persistent.rule.Rule;
import org.trade.core.persistent.dao.Strategy;
import org.trade.ui.models.StrategyTreeModel;

import javax.swing.*;
import javax.swing.tree.DefaultTreeCellRenderer;
import java.awt.*;
import java.io.Serial;

/**
 * @author Simon Allen
 * @version $Revision: 1.0 $
 */
public class StrategyTreeCellRenderer extends DefaultTreeCellRenderer {

    @Serial
    private static final long serialVersionUID = 7664391812385841364L;
    private final Color backgroundSelectionColor;

    public StrategyTreeCellRenderer() {
        super();
        backgroundSelectionColor = this.getBackgroundSelectionColor();
    }

    /**
     * Method getTreeCellRendererComponent.
     *
     * @param tree     JTree
     * @param value    Object
     * @param selected boolean
     * @param expanded boolean
     * @param leaf     boolean
     * @param row      int
     * @param hasFocus boolean
     * @return Component
     * @see javax.swing.tree.TreeCellRenderer#getTreeCellRendererComponent(JTree,
     * Object, boolean, boolean, boolean, int, boolean)
     */
    public Component getTreeCellRendererComponent(JTree tree, Object value, boolean selected, boolean expanded,
                                                  boolean leaf, int row, boolean hasFocus) {

        Object node = ((StrategyTreeModel) tree.getModel()).getNode(value);

        Component comp = super.getTreeCellRendererComponent(tree, value, selected, expanded, leaf, row, hasFocus);
        if (selected) {
            this.setBackgroundSelectionColor(backgroundSelectionColor);
        }
        if (node != null) {
            if ((node instanceof Rule) /* leaf */) {
                this.setToolTipText("Select to open rule.");
                if (((Rule) node).isDirty()) {
                    setBackgroundSelectionColor(Color.RED);
                }
            } else if ((node instanceof Strategy)) {
                this.setToolTipText("Class name: " + ((Strategy) node).getClassName());
            } else if (expanded) {

            } else {

            }
        }
        return comp;
    }
}
