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
import org.trade.persistent.dao.CodeType;
import org.trade.persistent.dao.IndicatorParameters;

import java.util.Vector;

/**
 *
 */
public class IndicatorParametersTableModel extends AspectTableModel {
    /**
     *
     */
    private static final long serialVersionUID = 3087514589731145479L;
    private static final String NAME = "Indicator Name*";
    private static final String DESCRIPTION = "Description";

    Aspects m_data = null;

    public IndicatorParametersTableModel() {

        columnNames = new String[2];
        columnNames[0] = NAME;
        columnNames[1] = DESCRIPTION;

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
                getNewRow(newRow, (CodeType) element);
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

        final CodeType element = (CodeType) getData().getAspect().get(row);

        switch (column) {
            case 0: {
                element.setName((String) value);
                break;
            }
            case 1: {
                element.setDescription((String) value);
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
            if (CoreUtils.nullSafeComparator(((CodeType) element).getName(), name) == 0) {
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

        final IndicatorParameters element = new IndicatorParameters("", CodeType.IndicatorParameters, "");
        getData().add(element);
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
     * @param element CodeType
     */
    public void getNewRow(Vector<Object> newRow, CodeType element) {
        newRow.addElement(element.getName());
        newRow.addElement(element.getDescription());

    }
}
