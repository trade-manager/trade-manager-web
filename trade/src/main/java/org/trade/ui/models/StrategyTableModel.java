package org.trade.ui.models;

import org.trade.core.dao.Aspect;
import org.trade.core.dao.Aspects;
import org.trade.core.persistent.dao.Strategy;
import org.trade.core.util.CoreUtils;
import org.trade.core.valuetype.DAOStrategyManager;
import org.trade.core.valuetype.Decode;
import org.trade.core.valuetype.YesNo;

import java.io.Serial;
import java.util.ArrayList;
import java.util.List;
/**
 *
 * @author Simon Allen
 * @version $Revision: 1.0 $
 */
public class StrategyTableModel extends AspectTableModel {
    /**
     *
     */
    @Serial
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

    private Aspects data = null;

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
        return data;
    }

    /**
     * Method setData.
     *
     * @param data Aspects
     */
    public void setData(Aspects data) {

        this.data = data;
        this.clearAll();

        if (!getData().getAspects().isEmpty()) {

            for (final Aspect element : getData().getAspects()) {

                final List<Object> newRow = new ArrayList<>();
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

        final Strategy element = (Strategy) getData().getAspects().get(row);

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
                element.setMarketData(Boolean.valueOf(((YesNo) value).getCode()));
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

        for (final Aspect element : getData().getAspects()) {

            if (CoreUtils.nullSafeComparator(((Strategy) element).getName(), name) == 0) {

                getData().remove(element);
                getData().setDirty(true);
                final List<Object> currRow = rows.get(selectedRow);
                rows.remove(currRow);
                this.fireTableRowsDeleted(selectedRow, selectedRow);
                break;
            }
        }
    }

    public void addRow() {

        final Strategy element = new Strategy();
        getData().getAspects().add(element);
        getData().setDirty(true);
        final List<Object> newRow = new ArrayList<>();
        getNewRow(newRow, element);
        rows.add(newRow);
        // Tell the listeners a new table has arrived.
        this.fireTableRowsInserted(rows.size() - 1, rows.size() - 1);
    }

    /**
     * Method getNewRow.
     *
     * @param newRow  List<Object>
     * @param element Strategy
     */
    public void getNewRow(List<Object> newRow, Strategy element) {

        newRow.add(element.getName());
        newRow.add(element.getDescription());
        newRow.add(element.getClassName());
        if (element.hasStrategyManager()) {
            newRow.add(DAOStrategyManager.newInstance(element.getStrategyManager().getName()));
        } else {
            newRow.add(DAOStrategyManager.newInstance(Decode.NONE));
        }
        if (null == element.getMarketData()) {
            newRow.add(new YesNo());
        } else {
            newRow.add(YesNo.newInstance(element.getMarketData()));
        }
    }
}
