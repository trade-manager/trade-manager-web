package org.trade.ui.widget;

import org.trade.core.valuetype.Quantity;

import javax.swing.*;
import javax.swing.text.DefaultFormatterFactory;
import javax.swing.text.NumberFormatter;
import java.io.Serial;
import java.text.NumberFormat;

/**
 * @author Simon Allen
 * @version $Id: IntegerField.java,v 1.2 2001/12/28 21:14:55 simon Exp $
 */
public class QuantityField extends JFormattedTextField {
    /**
     *
     */
    @Serial
    private static final long serialVersionUID = 3445299380677561974L;

    public QuantityField() {
        super();
        NumberFormat displayFormat = NumberFormat.getIntegerInstance();
        displayFormat.setMinimumFractionDigits(0);
        NumberFormat editFormat = NumberFormat.getIntegerInstance();
        editFormat.setMinimumFractionDigits(0);
        this.setFormatterFactory(new DefaultFormatterFactory(new NumberFormatter(displayFormat),
                new NumberFormatter(displayFormat), new NumberFormatter(editFormat)));
        this.setHorizontalAlignment(SwingConstants.RIGHT);
        this.setValue(0);
        this.setColumns(10);
    }

    /**
     * Method getQuantity.
     *
     * @return Quantity
     */
    public Quantity getQuantity() {
        try {
            this.setValue(Integer.valueOf(this.getText()));
        } catch (Exception ex) {
            // Do nothing will return the current value.
        }
        return new Quantity(((Number) this.getValue()).intValue());
    }

    /**
     * Method setQuantity.
     *
     * @param number Quantity
     */
    public void setQuantity(Quantity number) {
        this.setValue(number.getIntegerValue());
    }
}
