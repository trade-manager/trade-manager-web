package org.trade.ui.tables;

import org.trade.base.Table;
import org.trade.base.TableModel;
import org.trade.core.valuetype.ValueTypeException;

import javax.swing.*;
import java.io.Serial;
/**
 *
 * @author Simon Allen
 * @version $Revision: 1.0 $
 */
public class TradelogSummaryTable extends Table {

    @Serial
    private static final long serialVersionUID = 1132297931453070904L;

    /**
     * Constructor for TradelogSummaryTable.
     *
     * @param model TableModel
     */
    public TradelogSummaryTable(TableModel model) throws ValueTypeException {
        super(model);
        this.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
        this.enablePopupMenu(false);
    }
}
