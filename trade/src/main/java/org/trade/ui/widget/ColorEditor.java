package org.trade.ui.widget;

import javax.swing.*;
import javax.swing.table.TableCellEditor;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.Serial;

/**
 *
 */
public class ColorEditor extends AbstractCellEditor implements TableCellEditor, ActionListener {
    /**
     *
     */
    @Serial
    private static final long serialVersionUID = -6974362652025540325L;
    private Color currentColor;
    private final JButton button;
    private final JColorChooser colorChooser;
    private final JDialog dialog;
    protected static final String EDIT = "edit";

    public ColorEditor() {
        // Set up the editor (from the table's point of view),
        // which is a button.
        // This button brings up the color chooser dialog,
        // which is the editor from the user's point of view.
        button = new JButton();
        button.setActionCommand(EDIT);
        button.addActionListener(this);
        button.setBorderPainted(false);

        // Set up the dialog that the button brings up.
        colorChooser = new JColorChooser();
        dialog = JColorChooser.createDialog(button, "Pick a Color", true, // modal
                colorChooser, this, // OK button handler
                null); // no CANCEL button handler
    }

    /**
     * Handles events from the editor button and from the dialog's OK button.
     *
     * @param e ActionEvent
     * @see java.awt.event.ActionListener#actionPerformed(ActionEvent)
     */
    public void actionPerformed(ActionEvent e) {
        if (EDIT.equals(e.getActionCommand())) {
            // The user has clicked the cell, so
            // bring up the dialog.
            button.setBackground(currentColor);
            colorChooser.setColor(currentColor);
            dialog.setVisible(true);

            // Make the renderer reappear.
            fireEditingStopped();

        } else { // User pressed dialog's "OK" button.
            currentColor = colorChooser.getColor();
        }
    }

    // Implement the one CellEditor method that AbstractCellEditor doesn't.

    /**
     * Method getCellEditorValue.
     *
     * @return Object
     * @see javax.swing.CellEditor#getCellEditorValue()
     */
    public Object getCellEditorValue() {
        return currentColor;
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
        currentColor = (Color) value;
        return button;
    }
}
