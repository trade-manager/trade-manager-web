package org.trade.ui.widget;

import javax.swing.*;
import java.io.Serial;
/**
 *
 * @author Simon Allen
 * @version $Revision: 1.0 $
 */
public class StringEditor extends DefaultCellEditor {
    /**
     *
     */
    @Serial
    private static final long serialVersionUID = -2681004614247446255L;

    /**
     * Constructor for StringEditor.
     *
     * @param textField StringField
     */
    public StringEditor(final StringField textField) {
        super(textField);
        editorComponent = textField;
        this.clickCountToStart = 1;
        delegate = new EditorDelegate() {
            /**
             *
             */
            @Serial
            private static final long serialVersionUID = 2354896381826003264L;

            public void setValue(Object value) {
                textField.setText((String) value);
            }

            public Object getCellEditorValue() {
                return textField.getText();
            }
        };
        textField.addActionListener(delegate);
    }
}
