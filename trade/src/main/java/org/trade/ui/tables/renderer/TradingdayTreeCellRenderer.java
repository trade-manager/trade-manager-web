package org.trade.ui.tables.renderer;

import org.trade.core.persistent.dao.TradeOrder;
import org.trade.core.persistent.dao.Tradestrategy;
import org.trade.core.valuetype.Side;
import org.trade.ui.models.TradingdayTreeModel;

import javax.swing.*;
import javax.swing.tree.DefaultTreeCellRenderer;
import java.awt.*;
import java.io.Serial;

/**
 *
 */
public class TradingdayTreeCellRenderer extends DefaultTreeCellRenderer {

    @Serial
    private static final long serialVersionUID = 7664391812385841364L;
    private final Color backgroundSelectionColor;

    public TradingdayTreeCellRenderer() {
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

        Object node = ((TradingdayTreeModel) tree.getModel()).getNode(value);

        Component comp = super.getTreeCellRendererComponent(tree, value, selected, expanded, leaf, row, hasFocus);
        if (selected) {
            this.setBackgroundSelectionColor(backgroundSelectionColor);
        }
        if (node != null) {
            if ((node instanceof Tradestrategy tradestrategy) /* leaf */) {
                if (!tradestrategy.getTradeOrders().isEmpty()) {
                    TradeOrder tradeOrder = tradestrategy.getTradeOrders().getFirst();
                    if (tradeOrder.hasTradePosition()) {
                        if (Side.BOT.equals(tradeOrder.getTradePosition().getSide())) {
                            comp.setForeground(Color.GREEN);
                        } else {
                            comp.setForeground(Color.RED);
                        }
                    }

                    if (selected) {
                        this.setBackgroundSelectionColor(backgroundSelectionColor);
                    }
                }
                this.setToolTipText("Select to open chart.");
            } else if (expanded) {

            } else {

            }
        }
        return comp;
    }
}
