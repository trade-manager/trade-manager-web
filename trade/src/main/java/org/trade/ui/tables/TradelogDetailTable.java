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

import org.trade.core.util.TradingCalendar;
import org.trade.core.valuetype.Decode;
import org.trade.core.valuetype.ValueTypeException;
import org.trade.dictionary.valuetype.Action;
import org.trade.dictionary.valuetype.DAOStrategy;
import org.trade.dictionary.valuetype.MarketBar;
import org.trade.dictionary.valuetype.MarketBias;
import org.trade.dictionary.valuetype.Side;
import org.trade.dictionary.valuetype.Tier;
import org.trade.dictionary.valuetype.TradestrategyStatus;
import org.trade.ui.base.Table;
import org.trade.ui.base.TableModel;
import org.trade.ui.widget.DateEditor;
import org.trade.ui.widget.DateField;
import org.trade.ui.widget.DateRenderer;
import org.trade.ui.widget.DecodeTableEditor;

import javax.swing.*;
import java.util.Calendar;
import java.util.Vector;

/**
 *
 */
public class TradelogDetailTable extends Table {

    private static final long serialVersionUID = 1132297931453070904L;

    private static final String DATETIMEFORMAT = "HH:mm:ss";

    /**
     * Constructor for TradelogDetailTable.
     *
     * @param model TableModel
     * @throws ValueTypeException
     */
    public TradelogDetailTable(TableModel model) throws ValueTypeException {
        super(model);
        DecodeTableEditor sideEditor = new DecodeTableEditor(
                new JComboBox<Decode>((Vector<Decode>) (new Side()).getCodesDecodes()));
        DecodeTableEditor tierEditor = new DecodeTableEditor(
                new JComboBox<Decode>((Vector<Decode>) (new Tier()).getCodesDecodes()));
        DecodeTableEditor tradestrategyStatusEditor = new DecodeTableEditor(
                new JComboBox<Decode>((Vector<Decode>) (new TradestrategyStatus()).getCodesDecodes()));
        DecodeTableEditor strategyEditor = new DecodeTableEditor(
                new JComboBox<Decode>((Vector<Decode>) (new DAOStrategy()).getCodesDecodes()));
        DecodeTableEditor marketBiasEditor = new DecodeTableEditor(
                new JComboBox<Decode>((Vector<Decode>) (new MarketBias()).getCodesDecodes()));
        DecodeTableEditor marketBarEditor = new DecodeTableEditor(
                new JComboBox<Decode>((Vector<Decode>) (new MarketBar()).getCodesDecodes()));
        DecodeTableEditor actionEditor = new DecodeTableEditor(
                new JComboBox<Decode>((Vector<Decode>) (new Action()).getCodesDecodes()));
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
