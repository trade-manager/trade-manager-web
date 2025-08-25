package org.trade.ui.tables;

import org.trade.base.Table;
import org.trade.base.TableModel;
import org.trade.core.util.time.TradingCalendar;
import org.trade.core.valuetype.Decode;
import org.trade.core.valuetype.MarketBar;
import org.trade.core.valuetype.ValueTypeException;
import org.trade.ui.widget.DateEditor;
import org.trade.ui.widget.DateField;
import org.trade.ui.widget.DateRenderer;
import org.trade.ui.widget.DecodeTableEditor;

import javax.swing.*;
import javax.swing.table.TableRowSorter;
import java.awt.*;
import java.io.Serial;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

/**
 * @author Simon Allen
 * @version $Revision: 1.0 $
 */
public class TradingdayTable extends Table {

    @Serial
    private static final long serialVersionUID = 1132297931453070904L;

    private static final String DATETIMEFORMAT = "MM/dd/yyyy HH:mm";

    /**
     * Constructor for TradingdayTable.
     *
     * @param model TableModel
     */
    public TradingdayTable(TableModel model) throws ValueTypeException {

        super(model);
        DecodeTableEditor marketBarEditor = new DecodeTableEditor(
                new JComboBox<>((new MarketBar()).getCodesDecodes().toArray(new Decode[0])));
        this.setDefaultEditor(MarketBar.class, marketBarEditor);
        DateRenderer rDate = new DateRenderer(DATETIMEFORMAT);
        DateEditor eDate = new DateEditor(new DateField(DATETIMEFORMAT),
                new org.trade.core.valuetype.Date(TradingCalendar.getDateTimeNowMarketTimeZone()), DATETIMEFORMAT,
                Calendar.MINUTE);
        this.setDefaultRenderer(org.trade.core.valuetype.Date.class, rDate);
        this.setDefaultEditor(org.trade.core.valuetype.Date.class, eDate);
        this.setFont(new Font("Monospaced", Font.PLAIN, 12));
        this.setPreferredScrollableViewportSize(new Dimension(250, 40));
        this.setFillsViewportHeight(true);
        this.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        TableRowSorter<TableModel> sorter = new TableRowSorter<>(model);
        this.setRowSorter(sorter);
        List<RowSorter.SortKey> sortKeys = new ArrayList<>();
        sortKeys.add(new RowSorter.SortKey(0, SortOrder.DESCENDING));
        sorter.setSortKeys(sortKeys);
    }
}
