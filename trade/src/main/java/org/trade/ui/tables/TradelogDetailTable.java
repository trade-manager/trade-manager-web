package org.trade.ui.tables;

import org.trade.base.Table;
import org.trade.base.TableModel;
import org.trade.core.util.time.TradingCalendar;
import org.trade.core.valuetype.Action;
import org.trade.core.valuetype.DAOStrategy;
import org.trade.core.valuetype.Decode;
import org.trade.core.valuetype.MarketBar;
import org.trade.core.valuetype.MarketBias;
import org.trade.core.valuetype.Side;
import org.trade.core.valuetype.Tier;
import org.trade.core.valuetype.TradestrategyStatus;
import org.trade.core.valuetype.ValueTypeException;
import org.trade.ui.widget.DateEditor;
import org.trade.ui.widget.DateField;
import org.trade.ui.widget.DateRenderer;
import org.trade.ui.widget.DecodeTableEditor;

import javax.swing.*;
import java.io.Serial;
import java.util.Calendar;

/**
 *
 */
public class TradelogDetailTable extends Table {

    @Serial
    private static final long serialVersionUID = 1132297931453070904L;

    private static final String DATETIMEFORMAT = "HH:mm:ss";

    /**
     * Constructor for TradelogDetailTable.
     *
     * @param model TableModel
     */
    public TradelogDetailTable(TableModel model) throws ValueTypeException {
        super(model);
        DecodeTableEditor sideEditor = new DecodeTableEditor(
                new JComboBox<>((new Side()).getCodesDecodes().toArray(new Decode[0])));
        DecodeTableEditor tierEditor = new DecodeTableEditor(
                new JComboBox<>((new Tier()).getCodesDecodes().toArray(new Decode[0])));
        DecodeTableEditor tradestrategyStatusEditor = new DecodeTableEditor(
                new JComboBox<>((new TradestrategyStatus()).getCodesDecodes().toArray(new Decode[0])));
        DecodeTableEditor strategyEditor = new DecodeTableEditor(
                new JComboBox<>((new DAOStrategy()).getCodesDecodes().toArray(new Decode[0])));
        DecodeTableEditor marketBiasEditor = new DecodeTableEditor(
                new JComboBox<>((new MarketBias()).getCodesDecodes().toArray(new Decode[0])));
        DecodeTableEditor marketBarEditor = new DecodeTableEditor(
                new JComboBox<>((new MarketBar()).getCodesDecodes().toArray(new Decode[0])));
        DecodeTableEditor actionEditor = new DecodeTableEditor(
                new JComboBox<>((new Action()).getCodesDecodes().toArray(new Decode[0])));
        this.setDefaultEditor(DAOStrategy.class, strategyEditor);
        this.setDefaultEditor(Side.class, sideEditor);
        this.setDefaultEditor(Tier.class, tierEditor);
        this.setDefaultEditor(TradestrategyStatus.class, tradestrategyStatusEditor);
        this.setDefaultEditor(MarketBias.class, marketBiasEditor);
        this.setDefaultEditor(MarketBar.class, marketBarEditor);
        this.setDefaultEditor(Action.class, actionEditor);

        DateRenderer rDate = new DateRenderer(DATETIMEFORMAT);
        DateEditor eDate = new DateEditor(new DateField(DATETIMEFORMAT),
                new org.trade.core.valuetype.Date(TradingCalendar.getDateTimeNowMarketTimeZone()), DATETIMEFORMAT,
                Calendar.DAY_OF_MONTH);
        this.setDefaultRenderer(org.trade.core.valuetype.Date.class, rDate);
        this.setDefaultEditor(org.trade.core.valuetype.Date.class, eDate);
        this.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
        this.enablePopupMenu(false);

    }
}
