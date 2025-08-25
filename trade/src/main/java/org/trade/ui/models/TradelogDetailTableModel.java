package org.trade.ui.models;

import org.trade.base.TableModel;
import org.trade.core.persistent.dao.TradelogDetail;
import org.trade.core.persistent.dao.TradelogReport;
import org.trade.core.valuetype.Action;
import org.trade.core.valuetype.DAOStrategy;
import org.trade.core.valuetype.Date;
import org.trade.core.valuetype.Decimal;
import org.trade.core.valuetype.MarketBar;
import org.trade.core.valuetype.Money;
import org.trade.core.valuetype.OrderStatus;
import org.trade.core.valuetype.Quantity;
import org.trade.core.valuetype.Side;
import org.trade.core.valuetype.Tier;
import org.trade.core.valuetype.TradestrategyStatus;

import javax.swing.event.TableModelEvent;
import java.io.Serial;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Simon Allen
 * @version $Revision: 1.0 $
 */
public class TradelogDetailTableModel extends TableModel {
    /**
     *
     */
    @Serial
    private static final long serialVersionUID = 3087514589731145479L;

    public static final String DATE = "   Date    ";
    public static final String SYMBOL = "Symbol";
    public static final String LONGSHORT = "Long/Short";
    public static final String TIER = "Tier";
    public static final String MARKET_BIAS = "Mkt Bias";
    public static final String MARKET_BAR = "Mkt Bar";
    public static final String STRATEGY = "   Strategy   ";
    public static final String STATUS = "    Status    ";
    public static final String SIDE = "Side";
    public static final String ACTION = "Action";
    public static final String STOP_PRICE = "Stop Price";
    public static final String ORDER_STATUS = "Order Status";
    public static final String FILLED_DATE = "Trade Time";
    public static final String QUANTITY = "Quantity";
    public static final String AVG_FILL_PRICE = "Avg Price";
    public static final String COMMISION = "Comms";
    public static final String PROFIT_LOSS = "Net P/L Amt";

    private TradelogReport data = null;

    /**
     * OrderModel() -
     */
    public TradelogDetailTableModel() {

        columnNames = new String[17];
        columnNames[0] = DATE;
        columnNames[1] = SYMBOL;
        columnNames[2] = LONGSHORT;
        columnNames[3] = TIER;
        columnNames[4] = MARKET_BIAS;
        columnNames[5] = MARKET_BAR;
        columnNames[6] = STRATEGY;
        columnNames[7] = STATUS;
        columnNames[8] = SIDE;
        columnNames[9] = ACTION;
        columnNames[10] = STOP_PRICE;
        columnNames[11] = ORDER_STATUS;
        columnNames[12] = FILLED_DATE;
        columnNames[13] = QUANTITY;
        columnNames[14] = AVG_FILL_PRICE;
        columnNames[15] = COMMISION;
        columnNames[16] = PROFIT_LOSS;
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
     * Method setData.
     *
     * @param data TradelogReport
     */
    public void setData(TradelogReport data) {

        this.data = data;
        this.clearAll();

        if (!getData().getTradelogDetail().isEmpty()) {

            for (final TradelogDetail element : getData().getTradelogDetail()) {

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
        final TradelogDetail element = getData().getTradelogDetail().get(row);

        switch (column) {

            case 1: {
                element.setOpen((String) value);
                break;
            }
            case 2: {
                element.setSymbol((String) value);
                break;
            }
            case 3: {
                element.setLongShort(((Side) value).getCode());
                break;
            }
            case 4: {
                element.setTier(((Tier) value).getCode());
                break;
            }
            case 5: {
                element.setMarketBias(((MarketBar) value).getCode());
                break;
            }
            case 6: {
                element.setMarketBar(((MarketBar) value).getCode());
                break;
            }
            case 7: {
                element.setName((String) value);
                break;
            }
            case 8: {
                element.setStatus(((TradestrategyStatus) value).getCode());
                break;
            }
            case 9: {
                element.setSide(((Side) value).getCode());
                break;
            }
            case 10: {
                element.setAction(((Action) value).getCode());
                break;
            }
            case 11: {
                element.setStopPrice(((Money) value).getBigDecimalValue());
                break;
            }
            case 12: {
                element.setOrderStatus(((OrderStatus) value).getCode());
                break;
            }
            case 13: {
                element.setFilledDate(((Date) value).getZonedDateTime());
                break;
            }
            case 14: {
                element.setQuantity(((Quantity) value).getIntegerValue());
                break;
            }
            case 15: {
                element.setAverageFilledPrice(((Decimal) value).getBigDecimalValue());
                break;
            }
            case 16: {
                element.setCommission(((Money) value).getBigDecimalValue());
                break;
            }
            case 17: {
                element.setProfitLoss(((Money) value).getBigDecimalValue());
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
        for (final TradelogDetail element : getData().getTradelogDetail()) {

            if (i == selectedRow) {

                getData().getTradelogDetail().remove(element);
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
     * @param element TradelogDetail
     */
    public void addRow(TradelogDetail element) {

        getData().getTradelogDetail().add(element);
        final List<Object> newRow = new ArrayList<>();

        getNewRow(newRow, element);
        rows.add(newRow);
        // Tell the listeners a new table has arrived.
        this.fireTableRowsInserted(rows.size() - 1, rows.size() - 1);

    }

    public void addRow() {

        final TradelogDetail element = new TradelogDetail();
        getData().getTradelogDetail().add(element);
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
     * @param element TradelogDetail
     */
    public void getNewRow(List<Object> newRow, TradelogDetail element) {

        newRow.add(element.getOpen());
        if (null == element.getSymbol()) {
            newRow.add("");
        } else {
            newRow.add(element.getSymbol());
        }
        if (null == element.getLongShort()) {
            newRow.add(new Side());
        } else {
            newRow.add(Side.newInstance(element.getLongShort()));
        }

        if (null == element.getTier()) {
            newRow.add(new Tier());
        } else {
            newRow.add(Tier.newInstance(element.getTier()));
        }
        if (null == element.getMarketBias()) {
            newRow.add(new MarketBar());
        } else {
            newRow.add(MarketBar.newInstance((element.getMarketBias())));
        }
        if (null == element.getMarketBar()) {
            newRow.add(new MarketBar());
        } else {
            newRow.add(MarketBar.newInstance((element.getMarketBar())));
        }
        if (null == element.getName()) {
            newRow.add("");
        } else {
            newRow.add(DAOStrategy.newInstance(element.getName()));
        }
        if (null == element.getStatus()) {
            newRow.add(new TradestrategyStatus());
        } else {
            newRow.add(TradestrategyStatus.newInstance((element.getStatus())));
        }
        if (null == element.getSide()) {
            newRow.add(new Side());
        } else {
            newRow.add(Side.newInstance((element.getSide())));
        }
        if (null == element.getAction()) {
            newRow.add(new Action());
        } else {
            newRow.add(Action.newInstance(element.getAction()));
        }
        if (null == element.getStopPrice()) {
            newRow.add(new Money());
        } else {
            newRow.add(new Money(element.getStopPrice()));
        }
        if (null == element.getOrderStatus()) {
            newRow.add(new OrderStatus());
        } else {
            newRow.add(OrderStatus.newInstance(element.getOrderStatus()));
        }
        if (null == element.getFilledDate()) {
            newRow.add(new Date());
        } else {
            newRow.add(new Date(element.getFilledDate()));
        }
        newRow.add(new Quantity(element.getQuantity()));

        if (null == element.getAverageFilledPrice()) {
            newRow.add(new Decimal(3));
        } else {
            newRow.add(new Decimal(element.getAverageFilledPrice(), 3));
        }
        if (null == element.getCommission()) {
            newRow.add(new Money());
        } else {
            newRow.add(new Money(element.getCommission()));
        }
        if (null == element.getProfitLoss()) {
            newRow.add(new Money());
        } else {
            newRow.add(new Money(element.getProfitLoss()));
        }
    }
}
