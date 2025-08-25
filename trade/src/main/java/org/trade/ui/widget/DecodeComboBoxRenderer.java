package org.trade.ui.widget;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.io.Serial;
/**
 *
 * @author Simon Allen
 * @version $Revision: 1.0 $
 */
public class DecodeComboBoxRenderer extends DefaultListCellRenderer {

    @Serial
    private static final long serialVersionUID = 6927205466904515527L;

    public DecodeComboBoxRenderer() {
        setOpaque(true);
        this.setBorder(new EmptyBorder(new Insets(2, 2, 2, 2)));
    }

    /**
     * Method getListCellRendererComponent.
     *
     * @param list         JList
     * @param value        Object
     * @param index        int
     * @param isSelected   boolean
     * @param cellHasFocus boolean
     * @return Component
     */
    public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected,
                                                  boolean cellHasFocus) {
        if (value != null) {
            this.setText(value.toString());
            setBackground(isSelected ? Color.red : Color.white);
            setForeground(isSelected ? Color.white : Color.black);
        }

        return this;
    }
}
