package org.trade.ui.widget;

import org.trade.base.BaseButton;

import javax.swing.*;
import javax.swing.table.TableCellEditor;
import java.awt.*;
import java.io.Serial;

/**
 *
 */
public class ButtonEditor extends AbstractCellEditor implements TableCellEditor {
    /**
     *
     */
    @Serial
    private static final long serialVersionUID = -6974362652025540325L;
    private BaseButton button;

    protected static final String EDIT = "edit";

    /**
     * Constructor for ButtonEditor.
     *
     * @param button BaseButton
     */
    public ButtonEditor(BaseButton button) {
        this.button = button;
    }

    // Implement the one CellEditor method that AbstractCellEditor doesn't.

    /**
     * Method getCellEditorValue.
     *
     * @return Object
     * @see javax.swing.CellEditor#getCellEditorValue()
     */
    public Object getCellEditorValue() {
        return null;
    }

    // Implement the one method defined by TableCellEditor.

    /**
     * Method getTableCellEditorComponent.
     *
     * @param table      JTable
     * @param value      Object
     * @param isSelected boolean
     * @param row        int
     * @param column     int
     * @return Component
     * @see javax.swing.table.TableCellEditor#getTableCellEditorComponent(JTable,
     * Object, boolean, int, int)
     */
    public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column) {
        if (isSelected) {
            this.button.setBackground(table.getSelectionBackground());
            this.button.setEnabled(true);
        } else {
            this.button.setBackground(table.getBackground());
            this.button.setEnabled(false);
        }
        return button;
    }
}
