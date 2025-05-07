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
package org.trade.ui.tables;

import org.trade.base.Table;
import org.trade.base.TableModel;
import org.trade.core.util.time.TradingCalendar;
import org.trade.core.valuetype.AccountType;
import org.trade.core.valuetype.AllocationMethod;
import org.trade.core.valuetype.Currency;
import org.trade.core.valuetype.DAOAccount;
import org.trade.core.valuetype.DAOStrategyManager;
import org.trade.core.valuetype.DataType;
import org.trade.core.valuetype.Decode;
import org.trade.core.valuetype.IndicatorSeries;
import org.trade.core.valuetype.ValueTypeException;
import org.trade.ui.widget.DateEditor;
import org.trade.ui.widget.DateField;
import org.trade.ui.widget.DateRenderer;
import org.trade.ui.widget.DecodeTableEditor;

import javax.swing.*;
import java.awt.*;
import java.io.Serial;
import java.util.Calendar;

/**
 *
 */
public class ConfigurationTable extends Table {

    @Serial
    private static final long serialVersionUID = 1132297931453070904L;

    private static final String DATETIMEFORMAT = "MM/dd/yyyy HH:mm";

    /**
     * Constructor for ConfigurationTable.
     *
     * @param model TableModel
     */
    public ConfigurationTable(TableModel model) throws ValueTypeException {
        super(model);
        DecodeTableEditor currencyEditor = new DecodeTableEditor(
                new JComboBox<>((new Currency()).getCodesDecodes()));
        DecodeTableEditor accountTypeEditor = new DecodeTableEditor(
                new JComboBox<>((new AccountType()).getCodesDecodes()));
        DateRenderer rDate = new DateRenderer(DATETIMEFORMAT);
        DateEditor eDate = new DateEditor(new DateField(DATETIMEFORMAT),
                new org.trade.core.valuetype.Date(TradingCalendar.getDateTimeNowMarketTimeZone()), DATETIMEFORMAT,
                Calendar.MINUTE);
        DecodeTableEditor dataTypeEditor = new DecodeTableEditor(
                new JComboBox<>((new DataType()).getCodesDecodes()));

        JComboBox<Decode> indicatorComboBoxEditor = new JComboBox<>(
                (new IndicatorSeries()).getCodesDecodes());
        ListCellRenderer<Object> indicatorRenderer = new DefaultListCellRenderer() {
            @Serial
            private static final long serialVersionUID = -3146015541332720784L;

            public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected,
                                                          boolean cellHasFocus) {
                if (value instanceof Decode) {
                    String indicatorName = ((Decode) value).getCode().substring(0,
                            ((Decode) value).getCode().indexOf("Series"));
                    setToolTipText(indicatorName);
                    value = ((Decode) value).getDisplayName();
                } else {
                    setToolTipText(null);
                }
                return super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
            }
        };
        indicatorComboBoxEditor.setRenderer(indicatorRenderer);
        DecodeTableEditor indicatorSeriesEditor = new DecodeTableEditor(indicatorComboBoxEditor);

        JComboBox<Decode> strategyManagerComboBox = new JComboBox<>(
                (new DAOStrategyManager()).getCodesDecodes());
        DecodeTableEditor dAOStrategyManagerEditor = new DecodeTableEditor(strategyManagerComboBox);
        JComboBox<Decode> daoAccountComboBox = new JComboBox<>(
                (new DAOAccount()).getCodesDecodes());
        DecodeTableEditor dAOAccountEditor = new DecodeTableEditor(daoAccountComboBox);
        JComboBox<Decode> allocationMethodComboBox = new JComboBox<>(
                (new AllocationMethod()).getCodesDecodes());
        DecodeTableEditor allocationMethodEditor = new DecodeTableEditor(allocationMethodComboBox);
        this.setDefaultEditor(Currency.class, currencyEditor);
        this.setDefaultEditor(AllocationMethod.class, allocationMethodEditor);
        this.setDefaultEditor(AccountType.class, accountTypeEditor);
        this.setDefaultRenderer(org.trade.core.valuetype.Date.class, rDate);
        this.setDefaultEditor(org.trade.core.valuetype.Date.class, eDate);
        this.setDefaultEditor(DataType.class, dataTypeEditor);
        this.setDefaultEditor(IndicatorSeries.class, indicatorSeriesEditor);
        this.setDefaultEditor(DAOStrategyManager.class, dAOStrategyManagerEditor);
        this.setDefaultEditor(DAOAccount.class, dAOAccountEditor);
    }
}
