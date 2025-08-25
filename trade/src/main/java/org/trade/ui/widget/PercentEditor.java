package org.trade.ui.widget;

import org.trade.core.valuetype.Percent;

import javax.swing.*;
import java.io.Serial;

/**
 *
 */
public class PercentEditor extends DefaultCellEditor {
    /**
     *
     */
    @Serial
    private static final long serialVersionUID = -373016324201689041L;

    /**
     * Constructor for PercentEditor.
     *
     * @param textField PercentField
     */
    public PercentEditor(final PercentField textField) {
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
                textField.setPercent((Percent) value);
            }

            public Object getCellEditorValue() {
                return textField.getPercent();
            }
        };

        textField.addActionListener(delegate);
    }
}
