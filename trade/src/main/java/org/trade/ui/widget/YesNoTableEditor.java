package org.trade.ui.widget;

import org.trade.core.valuetype.YesNo;

import javax.swing.*;
import java.io.Serial;

/**
 * @author Simon Allen
 * @version $Revision: 1.0 $
 */
public class YesNoTableEditor extends DefaultCellEditor {

    /**
     *
     */
    @Serial
    private static final long serialVersionUID = 2148534921779125768L;

    public YesNoTableEditor() {
        this(new JCheckBox());
    }

    /**
     * Constructs a DefaultCellEditor object that uses a check box.
     *
     * @param checkBox JCheckBox
     */
    public YesNoTableEditor(final JCheckBox checkBox) {
        super(checkBox);

        checkBox.setHorizontalAlignment(SwingConstants.CENTER);

        editorComponent = checkBox;
        delegate = new EditorDelegate() {
            /**
             *
             */
            @Serial
            private static final long serialVersionUID = 6696276657185790230L;

            public void setValue(Object value) {
                boolean selected = false;

                if (value instanceof Boolean) {
                    selected = (Boolean) value;
                } else if (value instanceof YesNo) {
                    if (((YesNo) value).isYes()) {
                        selected = true;
                    }
                } else if (value instanceof String) {
                    selected = value.equals("true");
                }

                checkBox.setSelected(selected);
            }

            public Object getCellEditorValue() {
                YesNo yesNo;
                if (checkBox.isSelected()) {
                    yesNo = YesNo.newInstance(YesNo.YES);
                } else {
                    yesNo = YesNo.newInstance(YesNo.NO);
                }
                return yesNo;
            }
        };

        checkBox.addActionListener(delegate);
    }
}
