package org.trade.ui.widget;

import org.trade.core.valuetype.Decimal;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import java.io.Serial;
import java.math.BigDecimal;
import java.text.NumberFormat;

/**
 * @author Simon Allen
 * @version $Id: DecimalRenderer.java,v 1.2 2002/01/24 00:05:23 simon Exp $
 */
public class DecimalRenderer extends DefaultTableCellRenderer {
    /**
     *
     */
    @Serial
    private static final long serialVersionUID = 2923032656753601362L;

    private final NumberFormat m_formater;

    public DecimalRenderer() {
        super();
        setHorizontalAlignment(SwingConstants.RIGHT);
        m_formater = NumberFormat.getNumberInstance();
        m_formater.setMinimumFractionDigits(2);
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
            if (value instanceof Decimal) {

                BigDecimal bigDecimal = ((Decimal) value).getBigDecimalValue();
                if (null == bigDecimal) {
                    setText(value.toString());
                } else {
                    setText(m_formater.format(bigDecimal));
                }

            } else if (value instanceof BigDecimal) {
                setText(m_formater.format(value));
            } else {
                setText(value.toString());
            }
        }
    }
}
