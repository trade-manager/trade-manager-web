package org.trade.ui.widget;

import org.trade.core.valuetype.Money;

import javax.swing.*;
import java.io.Serial;
/**
 *
 * @author Simon Allen
 * @version $Revision: 1.0 $
 */
public class MoneyEditor extends DefaultCellEditor {
    /**
     *
     */
    @Serial
    private static final long serialVersionUID = 5190255283548436894L;

    /**
     * Constructor for MoneyEditor.
     *
     * @param textField MoneyField
     */
    public MoneyEditor(final MoneyField textField) {
        super(textField);
        editorComponent = textField;
        this.clickCountToStart = 1;
        delegate = new EditorDelegate() {
            /**
             *
             */
            @Serial
            private static final long serialVersionUID = 2424965279339363773L;

            public void setValue(Object value) {
                textField.setMoney((Money) value);
            }

            public Object getCellEditorValue() {
                return textField.getMoney();
            }
        };

        textField.addActionListener(delegate);
    }
}
