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

import org.trade.core.persistent.dao.Strategy;
import org.trade.core.persistent.dao.Tradestrategy;
import org.trade.core.persistent.dao.strategy.IStrategyRule;
import org.trade.core.valuetype.DAOStrategyManager;
import org.trade.ui.models.TradestrategyTableModel;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.*;
import java.io.Serial;
import java.util.concurrent.ConcurrentHashMap;

/**
 *
 */
public class DAOStrategyManagerRenderer extends DefaultTableCellRenderer {

    @Serial
    private static final long serialVersionUID = -6600633898553131547L;
    private final ConcurrentHashMap<String, IStrategyRule> strategyWorkers;

    /**
     * Constructor for DAOStrategyManagerRenderer.
     *
     * @param strategyWorkers ConcurrentHashMap<String,IStrategyRule>
     */
    public DAOStrategyManagerRenderer(ConcurrentHashMap<String, IStrategyRule> strategyWorkers) {
        this.strategyWorkers = strategyWorkers;
    }

    /**
     * Method getTableCellRendererComponent.
     *
     * @param table              JTable
     * @param dAOStrategyManager Object
     * @param isSelected         boolean
     * @param hasFocus           boolean
     * @param row                int
     * @param column             int
     * @return Component
     * @see javax.swing.table.TableCellRenderer#getTableCellRendererComponent(JTable,
     * Object, boolean, boolean, int, int)
     */
    public Component getTableCellRendererComponent(JTable table, Object dAOStrategyManager, boolean isSelected,
                                                   boolean hasFocus, int row, int column) {

        synchronized (dAOStrategyManager) {
            setBackground(null);
            super.getTableCellRendererComponent(table, dAOStrategyManager, isSelected, hasFocus, row, column);
            if (row > -1 && ((DAOStrategyManager) dAOStrategyManager).isValid()) {
                Tradestrategy transferObject = ((TradestrategyTableModel) table.getModel()).getData()
                        .getTradestrategies().get(table.convertRowIndexToModel(row));

                if (null == ((DAOStrategyManager) dAOStrategyManager).getObject())
                    return this;
                String key = ((Strategy) ((DAOStrategyManager) dAOStrategyManager).getObject()).getClassName()
                        + transferObject.getId();
                if (this.strategyWorkers.containsKey(key) && !isSelected) {
                    if (this.strategyWorkers.get(key).isDone()) {
                        setBackground(Color.YELLOW);
                        setToolTipText("Strategy complete");
                    } else if (this.strategyWorkers.get(key).isRunning()) {
                        setBackground(Color.GREEN);
                        setToolTipText("Strategy running");
                    }
                }
            }
            return this;
        }
    }
}
