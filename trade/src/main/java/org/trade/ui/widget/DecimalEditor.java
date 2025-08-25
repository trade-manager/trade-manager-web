package org.trade.ui.widget;

import org.trade.core.valuetype.Decimal;

import javax.swing.*;
import java.io.Serial;

/**
 * @author Simon Allen
 * @version $Revision: 1.0 $
 */
public class DecimalEditor extends DefaultCellEditor {
    /**
     *
     */
    @Serial
    private static final long serialVersionUID = -3633663882477548304L;

    /**
     * Constructor for DecimalEditor.
     *
     * @param textField DecimalField
     */
    public DecimalEditor(final DecimalField textField) {
        super(textField);

        editorComponent = textField;
        this.clickCountToStart = 1;
        delegate = new EditorDelegate() {
            /**
             *
             */
            @Serial
            private static final long serialVersionUID = -5844604630045985498L;

            public void setValue(Object value) {
                textField.setDecimal((Decimal) value);
            }

            public Object getCellEditorValue() {
                return textField.getDecimal();
            }
        };

        textField.addActionListener(delegate);
    }
}
