package org.trade.ui.widget;

import javax.swing.*;
import java.io.Serial;

/**
 *
 */
public class IntegerEditor extends DefaultCellEditor {
    /**
     *
     */
    @Serial
    private static final long serialVersionUID = -3660193231547303276L;

    /**
     * Constructor for IntegerEditor.
     *
     * @param textField IntegerField
     */
    public IntegerEditor(final IntegerField textField) {
        super(textField);
        editorComponent = textField;
        this.clickCountToStart = 1;
        delegate = new EditorDelegate() {
            /**
             *
             */
            @Serial
            private static final long serialVersionUID = -658101805319581454L;

            public void setValue(Object value) {
                textField.setInteger((Integer) value);
            }

            public Object getCellEditorValue() {
                return textField.getInteger();
            }
        };
        textField.addActionListener(delegate);
    }
}
