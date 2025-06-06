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
import org.trade.core.valuetype.Action;
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
 *
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
                new JComboBox<>((new Action()).getCodesDecodes()));
        DecodeTableEditor oCATypeEditor = new DecodeTableEditor(
                new JComboBox<>((new OCAType()).getCodesDecodes()));
        DecodeTableEditor orderTypeEditor = new DecodeTableEditor(
                new JComboBox<>((new OrderType()).getCodesDecodes()));
        DecodeTableEditor overrideConstraintsEditor = new DecodeTableEditor(
                new JComboBox<>((new OverrideConstraints()).getCodesDecodes()));
        DecodeTableEditor timeInForceEditor = new DecodeTableEditor(
                new JComboBox<>((new TimeInForce()).getCodesDecodes()));
        DecodeTableEditor triggerMethodEditor = new DecodeTableEditor(
                new JComboBox<>((new TriggerMethod()).getCodesDecodes()));
        DecodeTableEditor orderStatusEditor = new DecodeTableEditor(
                new JComboBox<>((new OrderStatus()).getCodesDecodes()));

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
