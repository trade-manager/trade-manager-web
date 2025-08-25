package org.trade.ui.widget;

import org.trade.core.valuetype.Money;

import javax.swing.*;
import javax.swing.text.DefaultFormatterFactory;
import javax.swing.text.NumberFormatter;
import java.io.Serial;
import java.text.NumberFormat;

/**
 * @author Simon Allen
 * @version $Id: MoneyField.java,v 1.2 2001/12/28 21:14:55 simon Exp $
 */
public class MoneyField extends JFormattedTextField {
    /**
     *
     */
    @Serial
    private static final long serialVersionUID = -4264304378113205235L;

    public MoneyField() {
        NumberFormat displayFormat = NumberFormat.getCurrencyInstance();
        displayFormat.setMinimumFractionDigits(2);
        NumberFormat editFormat = NumberFormat.getNumberInstance();
        editFormat.setMinimumFractionDigits(2);
        this.setFormatterFactory(new DefaultFormatterFactory(new NumberFormatter(displayFormat),
                new NumberFormatter(displayFormat), new NumberFormatter(editFormat)));
        this.setHorizontalAlignment(SwingConstants.RIGHT);
        this.setValue(0);
        this.setColumns(10);
    }

    /**
     * Method getMoney.
     *
     * @return Money
     */
    public Money getMoney() {
        try {
            this.setValue(Double.valueOf(this.getText()));
        } catch (Exception ex) {
            // Do nothing will return the current value.
        }
        return new Money(((Number) this.getValue()).doubleValue());
    }

    /**
     * Method setMoney.
     *
     * @param number Money
     */
    public void setMoney(Money number) {
        super.setValue(number.doubleValue());
    }
}
