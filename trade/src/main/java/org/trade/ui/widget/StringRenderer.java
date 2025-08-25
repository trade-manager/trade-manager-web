package org.trade.ui.widget;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import java.io.Serial;

/**
 * @author Simon Allen
 * @version $Revision: 1.0 $
 */
public class StringRenderer extends DefaultTableCellRenderer {
    /**
     *
     */
    @Serial
    private static final long serialVersionUID = 8447756819964236715L;

    public StringRenderer() {
        super();
        setHorizontalAlignment(SwingConstants.LEFT);
    }

    /**
     * Method setValue.
     *
     * @param value Object
     */
    protected void setValue(Object value) {
        if (value == null) {
            setText("");
        } else {
            if (value instanceof String) {
                setText((String) value);
            } else {
                setText(value.toString());
            }
        }
    }
}
