package org.trade.ui.widget;

import org.trade.core.valuetype.Date;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import java.io.Serial;
import java.text.SimpleDateFormat;
import java.util.Locale;

/**
 * @author Simon Allen
 * @version $Revision: 1.0 $
 */
public class DateRenderer extends DefaultTableCellRenderer {

    @Serial
    private static final long serialVersionUID = -7703222115247216081L;
    private final SimpleDateFormat dateFormat;

    /**
     * Constructor for DateRenderer.
     *
     * @param mask String
     */
    public DateRenderer(String mask) {
        super();

        setHorizontalAlignment(SwingConstants.CENTER);
        dateFormat = new SimpleDateFormat(mask, Locale.getDefault());
        dateFormat.setLenient(false);
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
            if (value instanceof java.util.Date) {
                setText(dateFormat.format(value));
            } else if (value instanceof Date) {
                java.util.Date date = ((Date) value).getDate();
                if (null == date) {
                    setText(value.toString());
                } else {
                    setText(dateFormat.format(date));
                }
            } else {
                setText(value.toString());
            }
        }
    }
}
