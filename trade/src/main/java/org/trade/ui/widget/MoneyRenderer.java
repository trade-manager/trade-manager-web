package org.trade.ui.widget;

import org.trade.core.valuetype.Money;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import java.io.Serial;
import java.text.NumberFormat;

/**
 * @author Simon Allen
 * @version $Id: MoneyRenderer.java,v 1.3 2002/01/24 01:16:08 simon Exp $
 */
public class MoneyRenderer extends DefaultTableCellRenderer {
    /**
     *
     */
    @Serial
    private static final long serialVersionUID = 6325763792561257469L;
    private final NumberFormat m_formater;

    public MoneyRenderer() {
        super();
        setHorizontalAlignment(SwingConstants.RIGHT);
        m_formater = NumberFormat.getCurrencyInstance();
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
            if (value instanceof Money) {

                if (null == ((Money) value).getBigDecimalValue()) {
                    setText(value.toString());
                } else {
                    setText(m_formater.format(((Money) value).getBigDecimalValue()));
                }

            } else {
                setText(value.toString());
            }
        }
    }
}
