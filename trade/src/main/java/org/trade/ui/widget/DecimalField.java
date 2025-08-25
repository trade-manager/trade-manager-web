package org.trade.ui.widget;

import org.trade.core.valuetype.Decimal;

import javax.swing.*;
import javax.swing.text.DefaultFormatterFactory;
import javax.swing.text.NumberFormatter;
import java.io.Serial;
import java.text.NumberFormat;

/**
 * @author Simon Allen
 * @version $Id: DecimalField.java,v 1.2 2001/12/28 21:14:55 simon Exp $
 */
public class DecimalField extends JFormattedTextField {
    /**
     *
     */
    @Serial
    private static final long serialVersionUID = -4264304378113205235L;
    private static int _SCALE = 2;

    public DecimalField(int scale) {
        _SCALE = scale;
        NumberFormat displayFormat = NumberFormat.getNumberInstance();
        displayFormat.setMinimumFractionDigits(_SCALE);
        NumberFormat editFormat = NumberFormat.getNumberInstance();
        editFormat.setMinimumFractionDigits(_SCALE);
        this.setFormatterFactory(new DefaultFormatterFactory(new NumberFormatter(displayFormat),
                new NumberFormatter(displayFormat), new NumberFormatter(editFormat)));
        this.setHorizontalAlignment(SwingConstants.RIGHT);
        this.setValue(0);
        this.setColumns(10);
    }

    /**
     * Method getDecimal.
     *
     * @return Decimal
     */
    public Decimal getDecimal() {
        try {
            this.setValue(this.getText());
        } catch (Exception ex) {
            // Do nothing will return the current value.
        }
        return new Decimal(((Number) this.getValue()).doubleValue(), _SCALE);
    }

    /**
     * Method setDecimal.
     *
     * @param number Decimal
     */
    public void setDecimal(Decimal number) {
        this.setValue(number.doubleValue());
    }
}
