package org.trade.ui.widget;

import javax.swing.*;
import javax.swing.text.DefaultFormatterFactory;
import javax.swing.text.NumberFormatter;
import java.io.Serial;
import java.text.NumberFormat;

/**
 * @author Simon Allen
 * @version $Id: IntegerField.java,v 1.2 2001/12/28 21:14:55 simon Exp $
 */
public class IntegerField extends JFormattedTextField {
    /**
     *
     */
    @Serial
    private static final long serialVersionUID = 3445299380677561974L;

    public IntegerField() {
        super();
        NumberFormat displayFormat = NumberFormat.getIntegerInstance();
        displayFormat.setMinimumFractionDigits(0);
        NumberFormat editFormat = NumberFormat.getNumberInstance();
        editFormat.setMinimumFractionDigits(0);
        this.setFormatterFactory(new DefaultFormatterFactory(new NumberFormatter(displayFormat),
                new NumberFormatter(displayFormat), new NumberFormatter(editFormat)));
        this.setHorizontalAlignment(SwingConstants.RIGHT);
        this.setValue(0);
        this.setColumns(10);
    }

    /**
     * Method getInteger.
     *
     * @return Integer
     */
    public Integer getInteger() {
        try {
            this.setValue(Integer.valueOf(this.getText()));
        } catch (Exception ex) {
            // Do nothing will return the current value.
        }
        return ((Number) this.getValue()).intValue();
    }

    /**
     * Method setInteger.
     *
     * @param number Integer
     */
    public void setInteger(Integer number) {
        this.setValue(number);
    }
}
