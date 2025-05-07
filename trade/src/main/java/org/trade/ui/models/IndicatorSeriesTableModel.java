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

import org.trade.base.TableModel;
import org.trade.core.dao.Aspect;
import org.trade.core.dao.Aspects;
import org.trade.core.factory.ClassFactory;
import org.trade.core.persistent.dao.CodeValue;
import org.trade.core.persistent.dao.Strategy;
import org.trade.core.util.CoreUtils;
import org.trade.core.valuetype.DAOStrategy;
import org.trade.core.valuetype.YesNo;
import org.trade.indicator.IndicatorSeries;

import javax.swing.event.TableModelEvent;
import java.awt.*;
import java.io.Serial;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Vector;

/**
 *
 */
public class IndicatorSeriesTableModel extends TableModel {
    /**
     *
     */
    @Serial
    private static final long serialVersionUID = 3087514589731145479L;
    private static final String STRATEGY = "  Strategy* ";
    private static final String TYPE = "  Indicator* ";
    private static final String NAME = "   Name*  ";
    private static final String DESCRIPTION = "              Description              ";
    private static final String DISPLAY_ON_CHART = "Display";
    private static final String SUBCHART_CHART = "Sub Chart";
    private static final String COLOR = "Color";
    private static final String PROPERTIES = "Properties";

    private static final String[] columnHeaderToolTip = {"The name of the strategy", null,
            "Specific name for this indicator i.e. 20-MA", null, "If checked display on the main chart",
            "If checked display this indicator as a sub-chart i.e Y-axis not $", null, null};

    Strategy m_data = null;

    public IndicatorSeriesTableModel() {
        super(columnHeaderToolTip);

        columnNames = new String[8];
        columnNames[0] = STRATEGY;
        columnNames[1] = TYPE;
        columnNames[2] = NAME;
        columnNames[3] = DESCRIPTION;
        columnNames[4] = DISPLAY_ON_CHART;
        columnNames[5] = SUBCHART_CHART;
        columnNames[6] = COLOR;
        columnNames[7] = PROPERTIES;
    }

    /**
     * Method getData.
     *
     * @return Strategy
     */
    public Strategy getData() {
        return m_data;
    }

    /**
     * Method setData.
     *
     * @param data Strategy
     */
    public void setData(Strategy data) {

        this.m_data = data;
        this.clearAll();
        if (!getData().getIndicatorSeries().isEmpty()) {

            for (final org.trade.core.persistent.dao.series.indicator.IndicatorSeries element : getData().getIndicatorSeries()) {
                final Vector<Object> newRow = new Vector<>();
                getNewRow(newRow, element);
                rows.add(newRow);
            }
            fireTableDataChanged();
        }
    }

    /**
     * Method isCellEditable.
     *
     * @param row    int
     * @param column int
     * @return boolean
     * @see javax.swing.table.TableModel#isCellEditable(int, int)
     */
    public boolean isCellEditable(int row, int column) {
        if (column == 0) {
            return false;
        }
        if (Objects.equals(columnNames[column], TYPE)) {
            org.trade.core.persistent.dao.series.indicator.IndicatorSeries element = getData().getIndicatorSeries().get(row);
            return null == element.getIdIndicatorSeries();
        }
        return true;
    }

    /**
     * Method populateDAO.
     *
     * @param value  Object
     * @param row    int
     * @param column int
     */
    public void populateDAO(Object value, int row, int column) {

        org.trade.core.persistent.dao.series.indicator.IndicatorSeries element = getData().getIndicatorSeries().get(row);

        switch (column) {
            case 0: {
                element.setStrategy((Strategy) ((DAOStrategy) value).getObject());
                break;
            }
            case 1: {
                String type = ((org.trade.core.valuetype.IndicatorSeries) value).getCode();
                String indicatorName = type.substring(0, type.indexOf("Series"));
                element = this.getIndicatorSeries(element.getStrategy(), indicatorName, type, indicatorName
                );
                this.replaceRow(element, row);
                break;
            }
            case 2: {
                element.setName(((String) value).trim());
                break;
            }
            case 3: {
                element.setDescription((String) value);
                break;
            }
            case 4: {
                element.setDisplaySeries(Boolean.valueOf(((YesNo) value).getCode()));
                break;
            }
            case 5: {
                element.setSubChart(Boolean.valueOf(((YesNo) value).getCode()));
                break;
            }
            case 6: {
                element.setSeriesRGBColor(((Color) value).getRGB());
                break;
            }
            case 7: {
                List<CodeValue> code = new ArrayList<>();
                for (Aspect aspect : ((Aspects) value).getAspect()) {
                    code.add((CodeValue) aspect);
                }
                if (!code.isEmpty())
                    element.setCodeValues(code);
                break;
            }
            default: {
            }
        }
        Objects.requireNonNull(element).setDirty(true);
    }

    /**
     * Method replaceRow.
     *
     * @param newElement  IndicatorSeries
     * @param selectedRow int
     */
    public void replaceRow(org.trade.core.persistent.dao.series.indicator.IndicatorSeries newElement, int selectedRow) {

        getData().getIndicatorSeries().set(selectedRow, newElement);
        final Vector<Object> newRow = new Vector<>();
        getNewRow(newRow, newElement);
        rows.set(selectedRow, newRow);
        // Tell the listeners a new table has arrived.
        fireTableChanged(new TableModelEvent(this));
    }

    /**
     * Method deleteRow.
     *
     * @param selectedRow int
     */
    public void deleteRow(int selectedRow) {

        String type = ((org.trade.core.valuetype.IndicatorSeries) this.getValueAt(selectedRow, 1)).getCode();
        String name = (String) this.getValueAt(selectedRow, 2);
        for (final org.trade.core.persistent.dao.series.indicator.IndicatorSeries element : getData().getIndicatorSeries()) {
            if (CoreUtils.nullSafeComparator(element.getName(), name) == 0
                    && CoreUtils.nullSafeComparator(element.getType(), type) == 0) {
                getData().getIndicatorSeries().remove(element);
                getData().setDirty(true);
                final Vector<Object> currRow = rows.get(selectedRow);
                rows.remove(currRow);
                this.fireTableRowsDeleted(selectedRow, selectedRow);
                break;
            }
        }
    }

    /**
     * Method getIndicatorSeries.
     *
     * @param strategy    Strategy
     * @param name        String
     * @param type        String
     * @param description String
     * @return IndicatorSeries
     */
    private org.trade.core.persistent.dao.series.indicator.IndicatorSeries getIndicatorSeries(Strategy strategy, String name, String type, String description) {
        try {
            Vector<Object> parm = new Vector<>();
            parm.add(strategy);
            parm.add(name);
            parm.add(type);
            parm.add(description);
            parm.add(false);
            parm.add(0);
            parm.add(false);
            String className = "org.trade.strategy.data." + type;
            return (org.trade.core.persistent.dao.series.indicator.IndicatorSeries) ClassFactory.getCreateClass(className, parm, this);
        } catch (Exception e) {
            /*
             * will only ever happen is IndicatorSeries does not exist.
             */
        }
        return null;
    }

    public void addRow() {

        String indicatorName = IndicatorSeries.MovingAverageSeries.substring(0,
                IndicatorSeries.MovingAverageSeries.indexOf("Series"));
        org.trade.core.persistent.dao.series.indicator.IndicatorSeries element = getIndicatorSeries(getData(), indicatorName, IndicatorSeries.MovingAverageSeries,
                indicatorName);
        getData().getIndicatorSeries().add(element);
        getData().setDirty(true);
        final Vector<Object> newRow = new Vector<>();
        getNewRow(newRow, Objects.requireNonNull(element));
        rows.add(newRow);
        // Tell the listeners a new table has arrived.
        this.fireTableRowsInserted(rows.size() - 1, rows.size() - 1);
    }

    /**
     * Method getNewRow.
     *
     * @param newRow  Vector<Object>
     * @param element IndicatorSeries
     */
    public void getNewRow(Vector<Object> newRow, org.trade.core.persistent.dao.series.indicator.IndicatorSeries element) {

        if (null == element.getStrategy()) {
            newRow.addElement(DAOStrategy.newInstance());
        } else {
            newRow.addElement(DAOStrategy.newInstance(element.getStrategy().getName()));
        }

        if (null == element.getType()) {
            newRow.addElement(org.trade.core.valuetype.IndicatorSeries.newInstance());
        } else {
            newRow.addElement(org.trade.core.valuetype.IndicatorSeries.newInstance(element.getType()));
        }
        newRow.addElement(element.getName());
        newRow.addElement(element.getDescription());
        if (null == element.getDisplaySeries()) {
            newRow.addElement(YesNo.newInstance(YesNo.NO));
        } else {
            newRow.addElement(YesNo.newInstance(element.getDisplaySeries()));
        }
        if (null == element.getSubChart()) {
            newRow.addElement(YesNo.newInstance(YesNo.NO));
        } else {
            newRow.addElement(YesNo.newInstance(element.getSubChart()));
        }
        if (null == element.getSeriesRGBColor()) {
            newRow.addElement(new Color(0));
        } else {
            newRow.addElement(new Color(element.getSeriesRGBColor()));
        }

        if (null == element.getCodeValues()) {
            newRow.addElement(new Aspects());
        } else {
            Aspects aspect = new Aspects();
            for (CodeValue code : element.getCodeValues()) {
                aspect.add(code);
            }
            newRow.addElement(aspect);
        }
    }
}
