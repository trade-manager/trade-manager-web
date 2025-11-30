package org.trade.ui.models;

import org.trade.base.TableModel;
import org.trade.core.aspect.Aspect;
import org.trade.core.aspect.Aspects;
import org.trade.core.factory.ClassFactory;
import org.trade.core.persistent.codetype.CodeValue;
import org.trade.core.persistent.strategy.Strategy;
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

import static org.trade.core.persistent.strategy.series.indicator.IndicatorSeries.INDICATOR_PACKAGE;

/**
 * @author Simon Allen
 * @version $Revision: 1.0 $
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

    Strategy data = null;

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
        return data;
    }

    /**
     * Method setData.
     *
     * @param data Strategy
     */
    public void setData(Strategy data) {

        this.data = data;
        this.clearAll();
        if (!getData().getIndicatorSeries().isEmpty()) {

            for (final org.trade.core.persistent.strategy.series.indicator.IndicatorSeries element : getData().getIndicatorSeries()) {

                final List<Object> newRow = new ArrayList<>();
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
            org.trade.core.persistent.strategy.series.indicator.IndicatorSeries element = getData().getIndicatorSeries().get(row);
            return null == element.getId();
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

        org.trade.core.persistent.strategy.series.indicator.IndicatorSeries element = getData().getIndicatorSeries().get(row);

        switch (column) {
            case 0: {
                element.setStrategy((Strategy) ((DAOStrategy) value).getObject());
                break;
            }
            case 1: {
                String type = ((org.trade.core.valuetype.IndicatorSeries) value).getCode();
                String indicatorName = type.substring(0, type.indexOf("Series"));
                element = this.getIndicatorSeries(element.getStrategy(), indicatorName, type, indicatorName);
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
                for (Aspect aspect : ((Aspects) value).getAspects()) {
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
    public void replaceRow(org.trade.core.persistent.strategy.series.indicator.IndicatorSeries newElement, int selectedRow) {

        getData().getIndicatorSeries().set(selectedRow, newElement);
        final List<Object> newRow = new ArrayList<>();
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

        for (final org.trade.core.persistent.strategy.series.indicator.IndicatorSeries element : getData().getIndicatorSeries()) {

            if (CoreUtils.nullSafeComparator(element.getName(), name) == 0
                    && CoreUtils.nullSafeComparator(element.getType(), type) == 0) {

                getData().getIndicatorSeries().remove(element);
                getData().setDirty(true);
                final List<Object> currRow = rows.get(selectedRow);
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
    private org.trade.core.persistent.strategy.series.indicator.IndicatorSeries getIndicatorSeries(Strategy strategy, String name, String type, String description) {

        try {

            List<Object> params = new ArrayList<>();
            params.add(strategy);
            params.add(name);
            params.add(type);
            params.add(description);
            params.add(false);
            params.add(0);
            params.add(false);
            String className = INDICATOR_PACKAGE + type;
            return (org.trade.core.persistent.strategy.series.indicator.IndicatorSeries) ClassFactory.getCreateClass(className, params, this);
        } catch (Exception e) {

            // will only ever happen if IndicatorSeries does not exist.
        }
        return null;
    }

    public void addRow() {

        String indicatorName = IndicatorSeries.MovingAverageSeries.substring(0,
                IndicatorSeries.MovingAverageSeries.indexOf("Series"));
        org.trade.core.persistent.strategy.series.indicator.IndicatorSeries element = getIndicatorSeries(getData(), indicatorName, IndicatorSeries.MovingAverageSeries,
                indicatorName);
        getData().getIndicatorSeries().add(element);
        getData().setDirty(true);
        final List<Object> newRow = new ArrayList<>();
        getNewRow(newRow, Objects.requireNonNull(element));
        rows.add(newRow);
        // Tell the listeners a new table has arrived.
        this.fireTableRowsInserted(rows.size() - 1, rows.size() - 1);
    }

    /**
     * Method getNewRow.
     *
     * @param newRow  List<Object>
     * @param element IndicatorSeries
     */
    public void getNewRow(List<Object> newRow, org.trade.core.persistent.strategy.series.indicator.IndicatorSeries element) {

        if (null == element.getStrategy()) {
            newRow.add(DAOStrategy.newInstance());
        } else {
            newRow.add(DAOStrategy.newInstance(element.getStrategy().getName()));
        }

        if (null == element.getType()) {
            newRow.add(org.trade.core.valuetype.IndicatorSeries.newInstance());
        } else {
            newRow.add(org.trade.core.valuetype.IndicatorSeries.newInstance(element.getType()));
        }
        newRow.add(element.getName());
        newRow.add(element.getDescription());
        if (null == element.getDisplaySeries()) {
            newRow.add(YesNo.newInstance(YesNo.NO));
        } else {
            newRow.add(YesNo.newInstance(element.getDisplaySeries()));
        }
        if (null == element.getSubChart()) {
            newRow.add(YesNo.newInstance(YesNo.NO));
        } else {
            newRow.add(YesNo.newInstance(element.getSubChart()));
        }
        if (null == element.getSeriesRGBColor()) {
            newRow.add(new Color(0));
        } else {
            newRow.add(new Color(element.getSeriesRGBColor()));
        }

        if (null == element.getCodeValues()) {
            newRow.add(new Aspects());
        } else {
            Aspects aspect = new Aspects();
            for (CodeValue code : element.getCodeValues()) {
                aspect.add(code);
            }
            newRow.add(aspect);
        }
    }
}
