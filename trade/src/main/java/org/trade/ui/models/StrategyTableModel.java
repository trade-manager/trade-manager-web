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
package org.trade.ui.models;

import org.trade.core.dao.Aspect;
import org.trade.core.dao.Aspects;
import org.trade.core.util.CoreUtils;
import org.trade.core.valuetype.Decode;
import org.trade.core.valuetype.YesNo;
import org.trade.dictionary.valuetype.DAOStrategyManager;
import org.trade.persistent.dao.Strategy;

import java.util.Vector;

/**
 *
 */
public class StrategyTableModel extends AspectTableModel {
    /**
     *
     */
    private static final long serialVersionUID = 3087514589731145479L;

    private static final String NAME = "Name*";
    private static final String DESCRIPTION = "                      Description                     ";
    private static final String MARKET_DATA = "MarketData";
    private static final String CLASSNAME = "    Class Name*  ";
    private static final String STRATEGY_MANAGER_NAME = "Strategy Mgr Name";

    private static final String[] columnHeaderToolTip = {"The name of the strategy", null,
            "<html>The java class name for the strategy.<br>" + "This file is stored in the strategy dir.<br>"
                    + "Note the dir is set in the config.properties (<b>trade.strategy.default.dir</b>)</html>",
            "The strategy manager used to managed the open position",
            "<html>If checked then TWS Mkt data api will run.<br>"
                    + "This will cause the strategy to fire if last price<br>"
                    + "falls outside the currents bars H/L</html>"};

    private Aspects m_data = null;

    public StrategyTableModel() {
        super(columnHeaderToolTip);

        columnNames = new String[5];
        columnNames[0] = NAME;
        columnNames[1] = DESCRIPTION;
        columnNames[2] = CLASSNAME;
        columnNames[3] = STRATEGY_MANAGER_NAME;
        columnNames[4] = MARKET_DATA;
    }

    /**
     * Method getData.
     *
     * @return Aspects
     */
    public Aspects getData() {
        return m_data;
    }

    /**
     * Method setData.
     *
     * @param data Aspects
     */
    public void setData(Aspects data) {

        this.m_data = data;
        this.clearAll();
        if (!getData().getAspect().isEmpty()) {
            for (final Aspect element : getData().getAspect()) {
                final Vector<Object> newRow = new Vector<Object>();
                getNewRow(newRow, (Strategy) element);
                rows.add(newRow);
            }
            fireTableDataChanged();
        }
    }

    /**
     * Method populateDAO.
     *
     * @param value  Object
     * @param row    int
     * @param column int
     */
    public void populateDAO(Object value, int row, int column) {

        final Strategy element = (Strategy) getData().getAspect().get(row);

        switch (column) {
            case 0: {
                element.setName(((String) value).trim());
                break;
            }
            case 1: {
                element.setDescription((String) value);
                break;
            }
            case 2: {
                element.setClassName(((String) value).trim());
                break;
            }
            case 3: {
                if (value instanceof DAOStrategyManager) {
                    if (!Decode.NONE.equals(((DAOStrategyManager) value).getDisplayName())) {
                        element.setStrategyManager((Strategy) ((DAOStrategyManager) value).getObject());
                    } else {
                        element.setStrategyManager(null);
                    }
                }
                break;
            }
            case 4: {
                element.setMarketData(new Boolean(((YesNo) value).getCode()));
                break;
            }
            default: {
            }
        }
        element.setDirty(true);
    }

    /**
     * Method deleteRow.
     *
     * @param selectedRow int
     */
    public void deleteRow(int selectedRow) {

        String name = (String) this.getValueAt(selectedRow, 0);
        for (final Aspect element : getData().getAspect()) {
            if (CoreUtils.nullSafeComparator(((Strategy) element).getName(), name) == 0) {
                getData().remove(element);
                getData().setDirty(true);
                final Vector<Object> currRow = rows.get(selectedRow);
                rows.remove(currRow);
                this.fireTableRowsDeleted(selectedRow, selectedRow);
                break;
            }
        }
    }

    public void addRow() {

        final Strategy element = new Strategy();
        getData().getAspect().add(element);
        getData().setDirty(true);
        final Vector<Object> newRow = new Vector<Object>();
        getNewRow(newRow, element);
        rows.add(newRow);
        // Tell the listeners a new table has arrived.
        this.fireTableRowsInserted(rows.size() - 1, rows.size() - 1);
    }

    /**
     * Method getNewRow.
     *
     * @param newRow  Vector<Object>
     * @param element Strategy
     */
    public void getNewRow(Vector<Object> newRow, Strategy element) {
        newRow.addElement(element.getName());
        newRow.addElement(element.getDescription());
        newRow.addElement(element.getClassName());
        if (element.hasStrategyManager()) {
            newRow.addElement(DAOStrategyManager.newInstance(element.getStrategyManager().getName()));
        } else {
            newRow.addElement(DAOStrategyManager.newInstance(Decode.NONE));
        }
        if (null == element.getMarketData()) {
            newRow.addElement(new YesNo());
        } else {
            newRow.addElement(YesNo.newInstance(element.getMarketData()));
        }
    }
}
