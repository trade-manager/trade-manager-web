/* ===========================================================
 * TradeManager : a application to trade strategies for the Java(tm) platform
 * ===========================================================
 *
 * (C) Copyright 2011-2011, by Simon Allen and Contributors.
 *
 * Project Info:  org.trade
 *
 * This library is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation; either version 2.1 of the License, or
 * (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY
 * or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public
 * License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301,
 * USA.
 *
 * [Java is a trademark or registered trademark of Oracle, Inc.
 * in the United States and other countries.]
 *
 * (C) Copyright 2011-2011, by Simon Allen and Contributors.
 *
 * Original Author:  Simon Allen;
 * Contributor(s):   -;
 *
 * Changes
 * -------
 *
 */
package org.trade.ui.tables.renderer;

import org.trade.dictionary.valuetype.Side;
import org.trade.persistent.dao.TradeOrder;
import org.trade.persistent.dao.Tradestrategy;
import org.trade.ui.models.TradingdayTreeModel;

import javax.swing.*;
import javax.swing.tree.DefaultTreeCellRenderer;
import java.awt.*;

/**
 *
 */
public class TradingdayTreeCellRenderer extends DefaultTreeCellRenderer {

    private static final long serialVersionUID = 7664391812385841364L;
    private Color backgroundSelectionColor = null;

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
            if ((node instanceof Tradestrategy) /* leaf */) {
                Tradestrategy tradestrategy = (Tradestrategy) node;
                if (!tradestrategy.getTradeOrders().isEmpty()) {
                    TradeOrder tradeOrder = tradestrategy.getTradeOrders().get(0);
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
