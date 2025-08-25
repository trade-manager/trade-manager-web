package org.trade.ui.widget;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import java.io.Serial;
import java.text.NumberFormat;
/**
 *
 * @author Simon Allen
 * @version $Revision: 1.0 $
 */
public class IntegerRenderer extends DefaultTableCellRenderer {
    /**
     *
     */
    @Serial
    private static final long serialVersionUID = 3568483163690718815L;
    private final NumberFormat m_formater;

    public IntegerRenderer() {
        super();
        setHorizontalAlignment(SwingConstants.RIGHT);
        m_formater = NumberFormat.getIntegerInstance();
        m_formater.setMinimumFractionDigits(0);
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
            if (value instanceof Integer) {
                setText(m_formater.format(value));
            } else {
                setText(value.toString());
            }
        }
    }
}
