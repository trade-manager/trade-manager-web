package org.trade.ui.models;

import org.trade.base.TableModel;
import org.trade.core.persistent.dao.TradelogReport;
import org.trade.core.persistent.dao.TradelogSummary;
import org.trade.core.valuetype.Decimal;
import org.trade.core.valuetype.Money;
import org.trade.core.valuetype.Percent;
import org.trade.core.valuetype.Quantity;

import javax.swing.event.TableModelEvent;
import java.io.Serial;
import java.util.ArrayList;
import java.util.List;
/**
 *
 * @author Simon Allen
 * @version $Revision: 1.0 $
 */
public class TradelogSummaryTableModel extends TableModel {
    /**
     *
     */
    @Serial
    private static final long serialVersionUID = 3087514589731145479L;

    public static final String PERIOD = "Period";
    public static final String BATTING_AVERAGE = "Batting Avg";
    public static final String SHARPE_RATIO = "Sharpe Ratio";
    public static final String GROSS_PL = "Gross P/L";
    public static final String QUANTITY = "Quantity";
    public static final String COMMISSION = "Commission";
    public static final String NET_PL = "Net P/L";
    public static final String WIN_COUNT = "Wins";
    public static final String WIN_AMOUNT = "Profit Amount";
    public static final String LOSS_COUNT = "Losses";
    public static final String LOSS_AMOUNT = "Loss Amount";
    public static final String POSITION_COUNT = "Positions";
    public static final String CONTRACT_COUNT = "Contracts";

    private static final String[] columnHeaderToolTip = {null, "% wins vs loss",
            "Simple sharpe ratio (sum $wins/#wins)/(sum $loss/#loss)", null, null, null, null, null, null, null, null,
            null, null};

    private TradelogReport data = null;

    /**
     * OrderModel() -
     */
    public TradelogSummaryTableModel() {
        super(columnHeaderToolTip);

        columnNames = new String[13];
        columnNames[0] = PERIOD;
        columnNames[1] = BATTING_AVERAGE;
        columnNames[2] = SHARPE_RATIO;
        columnNames[3] = GROSS_PL;
        columnNames[4] = QUANTITY;
        columnNames[5] = COMMISSION;
        columnNames[6] = NET_PL;
        columnNames[7] = WIN_COUNT;
        columnNames[8] = WIN_AMOUNT;
        columnNames[9] = LOSS_COUNT;
        columnNames[10] = LOSS_AMOUNT;
        columnNames[11] = POSITION_COUNT;
        columnNames[12] = CONTRACT_COUNT;
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
        return false;
    }

    /**
     * Method getData.
     *
     * @return TradelogReport
     */
    public TradelogReport getData() {
        return data;
    }

    /**
     * setData() -
     *
     * @param data TradelogReport
     */

    public void setData(TradelogReport data) {

        this.data = data;
        this.clearAll();

        if (!getData().getTradelogSummary().isEmpty()) {

            for (final TradelogSummary element : getData().getTradelogSummary()) {

                final List<Object> newRow = new ArrayList<>();
                getNewRow(newRow, element);
                rows.add(newRow);
            }
            fireTableDataChanged();
        }
    }

    /**
     * getData() -
     *
     * @param value  Object
     * @param row    int
     * @param column int
     */

    public void populateDAO(Object value, int row, int column) {
        final TradelogSummary element = getData().getTradelogSummary().get(row);

        switch (column) {

            case 1: {
                element.setPeriod((String) value);
                break;
            }
            case 2: {
                element.setBattingAverage(((Percent) value).getBigDecimalValue());
                break;
            }
            case 3: {
                element.setSimpleSharpeRatio(((Decimal) value).getBigDecimalValue());
                break;
            }
            case 4: {
                element.setGrossProfitLoss(((Money) value).getBigDecimalValue());
                break;
            }
            case 5: {
                element.setQuantity(((Quantity) value).getIntegerValue());
                break;
            }
            case 6: {
                element.setCommission(((Money) value).getBigDecimalValue());
                break;
            }
            case 7: {
                element.setNetProfitLoss(((Money) value).getBigDecimalValue());
                break;
            }
            case 8: {
                element.setWinCount(((Quantity) value).getIntegerValue());
                break;
            }
            case 9: {
                element.setProfitAmount(((Money) value).getBigDecimalValue());
                break;
            }
            case 10: {
                element.setLossCount(((Quantity) value).getIntegerValue());
                break;
            }
            case 11: {
                element.setLossAmount(((Money) value).getBigDecimalValue());
                break;
            }
            case 12: {
                element.setPositionCount(((Quantity) value).getIntegerValue());
                break;
            }
            case 13: {
                element.setTradestrategyCount(((Quantity) value).getIntegerValue());
                break;
            }
            default: {
            }
        }
    }

    /**
     * deleteRow() -
     *
     * @param selectedRow int
     */
    public void deleteRow(int selectedRow) {

        int i = 0;

        for (final TradelogSummary element : getData().getTradelogSummary()) {

            if (i == selectedRow) {

                getData().getTradelogSummary().remove(element);
                final List<Object> currRow = rows.get(selectedRow);
                rows.remove(currRow);
                this.fireTableRowsDeleted(selectedRow, selectedRow);
                break;
            }
            i++;
        }
    }

    /**
     * Method addRow.
     *
     * @param element TradelogSummary
     */
    public void addRow(TradelogSummary element) {

        getData().getTradelogSummary().add(element);
        final List<Object> newRow = new ArrayList<>();

        getNewRow(newRow, element);
        rows.add(newRow);
        // Tell the listeners a new table has arrived.
        this.fireTableRowsInserted(rows.size() - 1, rows.size() - 1);

    }

    public void addRow() {
        final TradelogSummary element = new TradelogSummary();
        getData().getTradelogSummary().add(element);
        final List<Object> newRow = new ArrayList<>();
        getNewRow(newRow, element);
        rows.add(newRow);

        // Tell the listeners a new table has arrived.
        fireTableChanged(new TableModelEvent(this));
    }

    /**
     * Method getNewRow.
     *
     * @param newRow  List<Object>
     * @param element TradelogSummary
     */
    public void getNewRow(List<Object> newRow, TradelogSummary element) {

        newRow.add(element.getPeriod());
        newRow.add(new Percent(element.getBattingAverage()));
        newRow.add(new Decimal(element.getSimpleSharpeRatio(), 2));
        newRow.add(new Money(element.getGrossProfitLoss()));
        newRow.add(new Quantity(element.getQuantity()));
        newRow.add(new Money(element.getCommission()));
        newRow.add(new Money(element.getNetProfitLoss()));
        newRow.add(new Quantity(element.getWinCount()));
        newRow.add(new Money(element.getProfitAmount()));
        newRow.add(new Quantity(element.getLossCount()));
        newRow.add(new Money(element.getLossAmount()));
        newRow.add(new Quantity(element.getPositionCount()));
        newRow.add(new Quantity(element.getTradestrategyCount()));
    }
}
