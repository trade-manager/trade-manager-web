package org.trade.ui.widget;

import org.trade.core.valuetype.Percent;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.*;
import java.io.Serial;
import java.text.NumberFormat;

/**
 * @author Simon Allen
 * @version $Id: PercentRenderer.java,v 1.3 2002/01/24 01:16:08 simon Exp $
 */
public class PercentRenderer extends DefaultTableCellRenderer {
    /**
     *
     */
    @Serial
    private static final long serialVersionUID = 1531259426058273458L;
    private final NumberFormat m_formater;

    public PercentRenderer() {
        super();
        setHorizontalAlignment(SwingConstants.RIGHT);
        m_formater = NumberFormat.getPercentInstance();
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
            if (value instanceof Percent) {
                if (null == ((Percent) value).getBigDecimalValue()) {
                    setText(value.toString());
                } else {
                    setText(m_formater.format(((Percent) value).getBigDecimalValue()));
                }
            } else {
                setText(value.toString());
            }
        }
    }

    /**
     * Method getTableCellRendererComponent.
     *
     * @param table      JTable
     * @param object     Object
     * @param isSelected boolean
     * @param hasFocus   boolean
     * @param row        int
     * @param column     int
     * @return Component
     * @see javax.swing.table.TableCellRenderer#getTableCellRendererComponent(JTable,
     * Object, boolean, boolean, int, int)
     */
    public synchronized Component getTableCellRendererComponent(JTable table, Object object, boolean isSelected,
                                                                boolean hasFocus, int row, int column) {

        synchronized (object) {
            setBackground(null);
            super.getTableCellRendererComponent(table, object, isSelected, hasFocus, row, column);
            Percent percent = (Percent) object;
            if (row > -1) {
                if (!isSelected) {
                    if (percent.doubleValue() > 0) {
                        setBackground(Color.GREEN);
                    }
                    if (percent.doubleValue() < 0) {
                        setBackground(Color.RED);
                    }
                }
            }
            return this;
        }
    }
}
