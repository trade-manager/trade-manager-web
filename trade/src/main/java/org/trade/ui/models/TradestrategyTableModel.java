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
import org.trade.core.persistent.dao.Contract;
import org.trade.core.persistent.dao.Portfolio;
import org.trade.core.persistent.dao.Strategy;
import org.trade.core.persistent.dao.Tradestrategy;
import org.trade.core.persistent.dao.Tradingday;
import org.trade.core.persistent.dao.Tradingdays;
import org.trade.core.properties.ConfigProperties;
import org.trade.core.util.CoreUtils;
import org.trade.core.util.time.TradingCalendar;
import org.trade.core.valuetype.BarSize;
import org.trade.core.valuetype.ChartDays;
import org.trade.core.valuetype.Currency;
import org.trade.core.valuetype.DAOPortfolio;
import org.trade.core.valuetype.DAOStrategy;
import org.trade.core.valuetype.DAOStrategyManager;
import org.trade.core.valuetype.Date;
import org.trade.core.valuetype.Decode;
import org.trade.core.valuetype.Exchange;
import org.trade.core.valuetype.Money;
import org.trade.core.valuetype.Percent;
import org.trade.core.valuetype.SECType;
import org.trade.core.valuetype.Side;
import org.trade.core.valuetype.Tier;
import org.trade.core.valuetype.TradestrategyStatus;
import org.trade.core.valuetype.YesNo;

import javax.swing.*;
import java.io.Serial;
import java.math.BigDecimal;
import java.time.ZonedDateTime;
import java.util.Objects;
import java.util.Vector;

/**
 *
 */
public class TradestrategyTableModel extends TableModel {
    /**
     *
     */
    @Serial
    private static final long serialVersionUID = 3087514589731145479L;

    private static final String TRADE = "Trade";
    private static final String SYMBOL = "Symbol*";
    private static final String DATE = "Date*";
    private static final String SIDE = "Side";
    private static final String TIER = "Tier";
    private static final String STRATEGY = "   Strategy*   ";
    private static final String STRATEGY_MGR = " Strategy Mgr  ";
    private static final String PORTFOLIO = "Portfolio*";
    private static final String BAR_SIZE = "Bar Size*";
    private static final String CHART_DAYS = "Chart Days*";
    private static final String RISK_AMOUNT = "Risk Amt";
    private static final String PERCENTCHGFRCLOSE = "%Chg Close";
    private static final String PERCENTCHGFROPEN = "%Chg Open";
    private static final String STATUS = "     Status     ";
    private static final String CURRENCY = "Currency*";
    private static final String EXCHANGE = "Exchange*";
    private static final String PRIMARY_EXCHANGE = "Primary Exch";
    private static final String SEC_TYPE = "SEC Type*";
    private static final String EXPIRY = "Expiry";

    private static final String[] columnHeaderToolTip = {
            "<html>Tradingday<br>"
                    + "Tradestrategies are unique based on Tradingday/Contract/Portfolio/Strategy/BarSize</html>",
            "If checked the Tradestrategy will trade",
            "<html>Contract symbol<br>"
                    + "Contracts are unique based on Symbol/SECType/Exchange/Currency/Expiry date<br>"
                    + "Note the default on add is set in the config.properties (<b>trade.tradingtab.default.add</b>)</html>",
            "<html>Your dirctional bias for this contract.<br>"
                    + "Note this is only needed if your strategy uses it.</html>",
            "<html>For gaps the grade<br>" + "See gap rules for tier grading criteria.<br>"
                    + "Note this is only needed if your strategy uses it.</html>",
            "<html>The strategy to trade with<br>"
                    + "Note the default is set in the config.properties (<b>trade.strategy.default</b>)</html>",
            null, "Portfolio",
            "<html>Bar size for strategy. Note Chart Days/BarSize combinations for IB:<br/>"
                    + "Note the default is set in the config.properties (<b>trade.backfill.barsize</b>)</html>",
            "<html>Historical data to pull in i.e 2D is today + yesterday<br>"
                    + "Note the default is set in the config.properties (<b>trade.backfill.duration</b>)</html>",
            "<html>Risk amount for trade used to calculate position size<br>"
                    + "Note the default is set in the config.properties (<b>trade.risk</b>)</html>",
            "% Change from close", "% Change from open",
            "<html>Tradestrategy status<br>" + "Note this is updated by the application</html>", null, null, null,
            "<html>Expiry date for future contracts<br>" + "Format MM/YYYY</html>"};

    private Tradingday m_data = null;
    private final Timer timer;

    public TradestrategyTableModel() {
        super(columnHeaderToolTip);
        columnNames = new String[19];
        columnNames[0] = DATE;
        columnNames[1] = TRADE;
        columnNames[2] = SYMBOL;
        columnNames[3] = SIDE;
        columnNames[4] = TIER;
        columnNames[5] = STRATEGY;
        columnNames[6] = STRATEGY_MGR;
        columnNames[7] = PORTFOLIO;
        columnNames[8] = BAR_SIZE;
        columnNames[9] = CHART_DAYS;
        columnNames[10] = RISK_AMOUNT;
        columnNames[11] = PERCENTCHGFRCLOSE;
        columnNames[12] = PERCENTCHGFROPEN;
        columnNames[13] = STATUS;
        columnNames[14] = CURRENCY;
        columnNames[15] = EXCHANGE;
        columnNames[16] = PRIMARY_EXCHANGE;
        columnNames[17] = SEC_TYPE;
        columnNames[18] = EXPIRY;

        /*
         * Create a 5sec timer to refresh the data this is used for the % chg,
         * strategy and status fields.
         */
        timer = new Timer(5000, _ -> {
            for (int i = 0; i < getRowCount(); i++) {
                fireTableCellUpdated(i, 5);
                fireTableCellUpdated(i, 6);
                fireTableCellUpdated(i, 11);
                fireTableCellUpdated(i, 12);
                fireTableCellUpdated(i, 13);
            }
        });
    }

    /**
     * Method getData.
     *
     * @return Tradingday
     */
    public Tradingday getData() {
        return this.m_data;
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

        Tradestrategy element = getData().getTradestrategies().get(row);
        if (null != element) {
            if (!element.getTradeOrders().isEmpty()) {
                return false;
            }
        }

        return (!Objects.equals(columnNames[column], DATE)) && (!Objects.equals(columnNames[column], STRATEGY_MGR))
                && (!Objects.equals(columnNames[column], PERCENTCHGFRCLOSE)) && (!Objects.equals(columnNames[column], PERCENTCHGFROPEN))
                && (!Objects.equals(columnNames[column], STATUS));
    }

    /**
     * Method setValueAt.
     *
     * @param value  Object
     * @param row    int
     * @param column int
     * @see javax.swing.table.TableModel#setValueAt(Object, int, int)
     */
    public void setValueAt(Object value, int row, int column) {
        if (null != value && !value.equals(super.getValueAt(row, column))) {
            this.populateDAO(value, row, column);
            Vector<Object> dataRow = rows.get(row);
            dataRow.setElementAt(value, column);
            fireTableCellUpdated(row, column);
        }
    }

    /**
     * Method setData.
     *
     * @param data Tradingday
     */
    public void setData(Tradingday data) {
        if (timer.isRunning())
            timer.stop();
        this.m_data = data;
        this.clearAll();
        if (null != getData() && null != getData().getTradestrategies() && !getData().getTradestrategies().isEmpty()) {
            for (final Tradestrategy element : getData().getTradestrategies()) {
                final Vector<Object> newRow = new Vector<>();
                getNewRow(newRow, element);
                rows.add(newRow);
            }
            fireTableDataChanged();
        }
        timer.start();
    }

    /**
     * Method populateDAO.
     *
     * @param value  Object
     * @param row    int
     * @param column int
     */
    public void populateDAO(Object value, int row, int column) {
        Tradestrategy element = getData().getTradestrategies().get(row);

        switch (column) {
            case 0: {
                element.getTradingday().setOpen(((Date) value).getZonedDateTime());
                break;
            }
            case 1: {
                element.setTrade(Boolean.valueOf(((YesNo) value).getCode()));
                break;
            }
            case 2: {
                element.getContract().setSymbol(((String) value).trim().toUpperCase());
                break;
            }
            case 3: {
                element.setSide(((Side) value).getCode());
                break;
            }
            case 4: {
                if (!Decode.NONE.equals(((Tier) value).getDisplayName())) {
                    element.setTier(((Tier) value).getCode());
                } else {
                    element.setTier(null);
                }
                break;
            }
            case 5: {
                final Strategy strategy = (Strategy) ((DAOStrategy) value).getObject();
                element.setStrategy(strategy);

                if (strategy.hasStrategyManager()) {
                    this.setValueAt(DAOStrategyManager.newInstance(strategy.getStrategyManager().getName()), row,
                            column + 1);
                } else {
                    this.setValueAt(DAOStrategyManager.newInstance(Decode.NONE), row, column + 1);
                }
                break;
            }
            case 6: {
                element.getStrategy().setStrategyManager((Strategy) ((DAOStrategyManager) value).getObject());
                break;
            }
            case 7: {
                Portfolio portfolio = (Portfolio) ((DAOPortfolio) value).getObject();
                element.setPortfolio(portfolio);
                break;
            }
            case 8: {
                element.setBarSize(Integer.valueOf(((BarSize) value).getValue()));
                break;
            }
            case 9: {
                element.setChartDays(Integer.valueOf(((ChartDays) value).getCode()));
                break;
            }
            case 10: {
                element.setRiskAmount(((Money) value).getBigDecimalValue());
                break;
            }
            case 11: {
                break;
            }
            case 12: {
                break;
            }
            case 13: {
                element.setStatus(((TradestrategyStatus) value).getCode());
                break;
            }
            case 14: {
                element.getContract().setCurrency(((Currency) value).getCode());
                break;
            }
            case 15: {
                element.getContract().setExchange(((Exchange) value).getCode());
                break;
            }
            case 16: {
                element.getContract().setPrimaryExchange(((Exchange) value).getCode());
                break;
            }
            case 17: {
                element.getContract().setSecType(((SECType) value).getCode());
                break;
            }
            case 18: {
                ZonedDateTime zonedDateTime = ((Date) value).getZonedDateTime();
                zonedDateTime = zonedDateTime.plusMonths(1);
                zonedDateTime = zonedDateTime.minusDays(1);
                element.getContract().setExpiry(zonedDateTime);
                break;
            }
            default: {
            }
        }
        element.setLastUpdateDate(TradingCalendar.getDateTimeNowMarketTimeZone());
        element.setDirty(true);
    }

    /**
     * Method deleteRow.
     *
     * @param selectedRow int
     */
    public void deleteRow(int selectedRow) {

        String symbol = ((String) this.getValueAt(selectedRow, 2)).trim().toUpperCase();
        final Strategy strategy = (Strategy) ((DAOStrategy) this.getValueAt(selectedRow, 5)).getObject();
        Portfolio portfolio = (Portfolio) ((DAOPortfolio) this.getValueAt(selectedRow, 7)).getObject();
        int barSize = Integer.parseInt(((BarSize) this.getValueAt(selectedRow, 8)).getValue());
        String currency = ((Currency) this.getValueAt(selectedRow, 14)).getCode();
        String exchange = ((Exchange) this.getValueAt(selectedRow, 15)).getCode();
        String priaryExchange = ((Exchange) this.getValueAt(selectedRow, 16)).getCode();
        String secType = ((SECType) this.getValueAt(selectedRow, 17)).getCode();

        for (final Tradestrategy element : getData().getTradestrategies()) {

            if (barSize == 1) {

                long daySeconds = TradingCalendar.getDurationInSeconds(element.getTradingday().getOpen(),
                        element.getTradingday().getClose());
                barSize = (int) daySeconds * barSize;
            }

            if (CoreUtils.nullSafeComparator(element.getContract().getSymbol(), symbol) == 0 && element.getStrategy().getName().equals(strategy.getName()) && element.getPortfolio().getName().equals(portfolio.getName()) && element.getBarSize().equals(barSize) && element.getContract().getCurrency().equals(currency) && element.getContract().getExchange().equals(exchange) && element.getContract().getPrimaryExchange().equals(priaryExchange) && element.getContract().getSecType().equals(secType)) {

                getData().getTradestrategies().remove(element);
                getData().setDirty(true);
                final Vector<Object> currRow = rows.get(selectedRow);
                rows.remove(currRow);
                this.fireTableRowsDeleted(selectedRow, selectedRow);
                break;
            }
        }
    }

    public void addRow() {

        Tradingday tradingday = getData();
        Tradestrategy tradestrategy = null;
        String strategyName;
        Strategy strategy = (Strategy) DAOStrategy.newInstance().getObject();
        Portfolio portfolio = (Portfolio) Objects.requireNonNull(DAOPortfolio.newInstance()).getObject();
        int chartDays = ChartDays.TWO_DAYS;
        Integer barSize = BarSize.FIVE_MIN;
        int riskAmount = 0;
        if (null != tradingday) {
            try {

                chartDays = ConfigProperties.getPropAsInt("trade.backfill.duration");
                if (!ChartDays.newInstance(chartDays).isValid())
                    chartDays = 2;

                barSize = ConfigProperties.getPropAsInt("trade.backfill.barsize");
                if (!BarSize.newInstance(barSize).isValid())
                    barSize = 300;

                riskAmount = ConfigProperties.getPropAsInt("trade.risk");
                strategyName = ConfigProperties.getPropAsString("trade.strategy.default");
                if (!DAOStrategy.newInstance(strategyName).isValid())
                    strategyName = DAOStrategy.newInstance().getCode();

                if (null != strategyName) {
                    strategy = (Strategy) DAOStrategy.newInstance(strategyName).getObject();
                }
                tradestrategy = Tradingdays
                        .parseContractLine(ConfigProperties.getPropAsString("trade.tradingtab.default.add"));

            } catch (Exception e) {
                // Do nothing
            }

            if (null == tradestrategy) {
                tradestrategy = new Tradestrategy(
                        new Contract(SECType.STOCK, "", Exchange.SMART, Currency.USD, null, null), tradingday, strategy,
                        portfolio, new BigDecimal(riskAmount), null, null, true, chartDays, barSize);
            } else {
                tradestrategy.setTradingday(tradingday);
            }

            tradestrategy.setRiskAmount(new BigDecimal(riskAmount));
            tradestrategy.setBarSize(barSize);
            tradestrategy.setChartDays(chartDays);
            tradestrategy.setTrade(true);
            tradestrategy.setDirty(true);
            tradestrategy.setStrategy(strategy);
            tradestrategy.setPortfolio(portfolio);

            getData().getTradestrategies().add(tradestrategy);
            Vector<Object> newRow = new Vector<>();

            getNewRow(newRow, tradestrategy);
            rows.add(newRow);

            // Tell the listeners a new table has arrived.
            this.fireTableRowsInserted(rows.size() - 1, rows.size() - 1);
        }
    }

    /**
     * Method getNewRow.
     *
     * @param newRow  Vector<Object>
     * @param element Tradestrategy
     */
    public void getNewRow(Vector<Object> newRow, Tradestrategy element) {

        newRow.addElement(new Date(element.getTradingday().getOpen()));
        newRow.addElement(YesNo.newInstance(element.getTrade()));
        newRow.addElement(element.getContract().getSymbol());
        if (null == element.getSide()) {
            newRow.addElement(new Side());
        } else {
            newRow.addElement(Side.newInstance(element.getSide()));
        }
        if (null == element.getTier()) {
            newRow.addElement(Tier.newInstance(Decode.NONE));
        } else {
            newRow.addElement(Tier.newInstance(element.getTier()));
        }
        newRow.addElement(DAOStrategy.newInstance(element.getStrategy().getName()));
        if (element.getStrategy().hasStrategyManager()) {
            newRow.addElement(DAOStrategyManager.newInstance(element.getStrategy().getStrategyManager().getName()));
        } else {
            newRow.addElement(DAOStrategyManager.newInstance(Decode.NONE));
        }

        newRow.addElement(DAOPortfolio.newInstance(element.getPortfolio().getName()));
        newRow.addElement(BarSize.newInstance(element.getBarSize()));
        newRow.addElement(ChartDays.newInstance(element.getChartDays()));
        newRow.addElement(new Money(element.getRiskAmount()));
        /*
         * TODO If the id is null then this element has not been saved and so
         * the DatasetContainer cannot be created. This is due to an issue with
         * hibernate and Eager fetch.
         */
        if (null != element.getStrategyData()) {
            newRow.addElement(element.getStrategyData().getBaseCandleSeries().getPercentChangeFromClose());
            newRow.addElement(element.getStrategyData().getBaseCandleSeries().getPercentChangeFromOpen());
        } else {
            newRow.addElement(new Percent(0));
            newRow.addElement(new Percent(0));
        }
        newRow.addElement(element.getTradestrategyStatus());
        newRow.addElement(Currency.newInstance(element.getContract().getCurrency()));
        newRow.addElement(Exchange.newInstance(element.getContract().getExchange()));
        newRow.addElement(Exchange.newInstance(element.getContract().getPrimaryExchange()));
        newRow.addElement(SECType.newInstance(element.getContract().getSecType()));
        if (null == element.getContract().getExpiry()) {
            newRow.addElement(new Date());
        } else {
            newRow.addElement(new Date(element.getContract().getExpiry()));
        }
    }
}
