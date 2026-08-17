package org.trade.ui.tables;

import org.trade.base.Table;
import org.trade.base.TableModel;
import org.trade.core.util.time.TradingCalendar;
import org.trade.core.valuetype.AccountType;
import org.trade.core.valuetype.AllocationMethod;
import org.trade.core.valuetype.Currency;
import org.trade.core.valuetype.DAOAccount;
import org.trade.core.valuetype.StrategyManager;
import org.trade.core.valuetype.DataType;
import org.trade.core.valuetype.Decode;
import org.trade.core.valuetype.DAOIndicatorSeries;
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
 * @author Simon Allen
 * @version $Revision: 1.0 $
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
                new JComboBox<>((new Currency()).getCodesDecodes().toArray(new Decode[0])));
        DecodeTableEditor accountTypeEditor = new DecodeTableEditor(
                new JComboBox<>((new AccountType()).getCodesDecodes().toArray(new Decode[0])));
        DateRenderer rDate = new DateRenderer(DATETIMEFORMAT);
        DateEditor eDate = new DateEditor(new DateField(DATETIMEFORMAT),
                new org.trade.core.valuetype.Date(TradingCalendar.getDateTimeNowMarketTimeZone()), DATETIMEFORMAT,
                Calendar.MINUTE);
        DecodeTableEditor dataTypeEditor = new DecodeTableEditor(
                new JComboBox<>((new DataType()).getCodesDecodes().toArray(new Decode[0])));

        JComboBox<Decode> indicatorComboBoxEditor = new JComboBox<>(
                (new DAOIndicatorSeries()).getCodesDecodes().toArray(new Decode[0]));
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
                (new StrategyManager()).getCodesDecodes().toArray(new Decode[0]));
        DecodeTableEditor dAOStrategyManagerEditor = new DecodeTableEditor(strategyManagerComboBox);
        JComboBox<Decode> daoAccountComboBox = new JComboBox<>(
                (new DAOAccount()).getCodesDecodes().toArray(new Decode[0]));
        DecodeTableEditor dAOAccountEditor = new DecodeTableEditor(daoAccountComboBox);
        JComboBox<Decode> allocationMethodComboBox = new JComboBox<>(
                (new AllocationMethod()).getCodesDecodes().toArray(new Decode[0]));
        DecodeTableEditor allocationMethodEditor = new DecodeTableEditor(allocationMethodComboBox);
        this.setDefaultEditor(Currency.class, currencyEditor);
        this.setDefaultEditor(AllocationMethod.class, allocationMethodEditor);
        this.setDefaultEditor(AccountType.class, accountTypeEditor);
        this.setDefaultRenderer(org.trade.core.valuetype.Date.class, rDate);
        this.setDefaultEditor(org.trade.core.valuetype.Date.class, eDate);
        this.setDefaultEditor(DataType.class, dataTypeEditor);
        this.setDefaultEditor(DAOIndicatorSeries.class, indicatorSeriesEditor);
        this.setDefaultEditor(StrategyManager.class, dAOStrategyManagerEditor);
        this.setDefaultEditor(DAOAccount.class, dAOAccountEditor);
    }
}
