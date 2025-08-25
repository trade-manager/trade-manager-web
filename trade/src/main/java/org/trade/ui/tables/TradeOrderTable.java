package org.trade.ui.tables;

import org.trade.base.Table;
import org.trade.base.TableModel;
import org.trade.core.util.time.TradingCalendar;
import org.trade.core.valuetype.Action;
import org.trade.core.valuetype.Decode;
import org.trade.core.valuetype.OCAType;
import org.trade.core.valuetype.OrderStatus;
import org.trade.core.valuetype.OrderType;
import org.trade.core.valuetype.OverrideConstraints;
import org.trade.core.valuetype.TimeInForce;
import org.trade.core.valuetype.TriggerMethod;
import org.trade.core.valuetype.ValueTypeException;
import org.trade.ui.widget.DateEditor;
import org.trade.ui.widget.DateField;
import org.trade.ui.widget.DateRenderer;
import org.trade.ui.widget.DecodeTableEditor;
import org.trade.ui.widget.StringEditor;
import org.trade.ui.widget.StringField;
import org.trade.ui.widget.StringRenderer;

import javax.swing.*;
import javax.swing.text.MaskFormatter;
import java.io.Serial;
import java.text.ParseException;
import java.util.Calendar;

/**
 * @author Simon Allen
 * @version $Revision: 1.0 $
 */
public class TradeOrderTable extends Table {

    @Serial
    private static final long serialVersionUID = 1132297931453070904L;

    /**
     * Constructor for TradeOrderTable.
     *
     * @param model TableModel
     */
    public TradeOrderTable(TableModel model) throws ValueTypeException, ParseException {
        super(model);

        DecodeTableEditor actionEditor = new DecodeTableEditor(
                new JComboBox<>((new Action()).getCodesDecodes().toArray(new Decode[0])));
        DecodeTableEditor oCATypeEditor = new DecodeTableEditor(
                new JComboBox<>((new OCAType()).getCodesDecodes().toArray(new Decode[0])));
        DecodeTableEditor orderTypeEditor = new DecodeTableEditor(
                new JComboBox<>((new OrderType()).getCodesDecodes().toArray(new Decode[0])));
        DecodeTableEditor overrideConstraintsEditor = new DecodeTableEditor(
                new JComboBox<>((new OverrideConstraints()).getCodesDecodes().toArray(new Decode[0])));
        DecodeTableEditor timeInForceEditor = new DecodeTableEditor(
                new JComboBox<>((new TimeInForce()).getCodesDecodes().toArray(new Decode[0])));
        DecodeTableEditor triggerMethodEditor = new DecodeTableEditor(
                new JComboBox<>((new TriggerMethod()).getCodesDecodes().toArray(new Decode[0])));
        DecodeTableEditor orderStatusEditor = new DecodeTableEditor(
                new JComboBox<>((new OrderStatus()).getCodesDecodes().toArray(new Decode[0])));

        String OCA_MASK = "AAAAAA";
        String OCA_VALIDCHARS = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
        StringEditor eString = new StringEditor(new StringField(new MaskFormatter(OCA_MASK), OCA_VALIDCHARS, null));
        StringRenderer rString = new StringRenderer();
        this.setDefaultRenderer(String.class, rString);
        this.setDefaultEditor(String.class, eString);
        this.setDefaultEditor(Action.class, actionEditor);
        this.setDefaultEditor(OCAType.class, oCATypeEditor);
        this.setDefaultEditor(OrderType.class, orderTypeEditor);
        this.setDefaultEditor(OverrideConstraints.class, overrideConstraintsEditor);
        this.setDefaultEditor(TimeInForce.class, timeInForceEditor);
        this.setDefaultEditor(TriggerMethod.class, triggerMethodEditor);
        this.setDefaultEditor(OrderStatus.class, orderStatusEditor);
        String DATETIMEFORMAT = "HH:mm:ss";
        DateRenderer rDate = new DateRenderer(DATETIMEFORMAT);
        DateEditor eDate = new DateEditor(new DateField(DATETIMEFORMAT),
                new org.trade.core.valuetype.Date(TradingCalendar.getDateTimeNowMarketTimeZone()), DATETIMEFORMAT,
                Calendar.DAY_OF_MONTH);
        this.setDefaultRenderer(org.trade.core.valuetype.Date.class, rDate);
        this.setDefaultEditor(org.trade.core.valuetype.Date.class, eDate);
        this.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
    }
}
