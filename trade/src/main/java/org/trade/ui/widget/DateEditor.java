package org.trade.ui.widget;

import org.trade.core.valuetype.Date;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.io.Serial;
import java.util.EventObject;
/**
 *
 * @author Simon Allen
 * @version $Revision: 1.0 $
 */
public class DateEditor extends DefaultCellEditor {
    /**
     *
     */
    @Serial
    private static final long serialVersionUID = -8851345801047150318L;

    private final JSpinner spinner = new JSpinner();

    /**
     * Constructor for DateEditor. Initializes the spinner.
     *
     * @param date  Date
     * @param mask  String
     * @param field int
     */
    public DateEditor(final DateField dateField, Date date, String mask, int field) {
        super(dateField);
        dateField.setDate(date);
        spinner.setModel(new SpinnerDateModel(date.getDate(), null, null, field));
        final JSpinner.DateEditor editor = new JSpinner.DateEditor(spinner, mask);
        spinner.setEditor(editor);
    }

    /**
     * Method getTableCellEditorComponent. Prepares the spinner component and
     * returns it.
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
        if (null == ((Date) value).getDate()) {
            spinner.setValue(new java.util.Date());
        } else {
            spinner.setValue(((Date) value).getDate());
        }
        return spinner;
    }

    /**
     * Method isCellEditable. Enables the editor only for double-clicks.
     *
     * @param evt EventObject
     * @return boolean
     * @see javax.swing.CellEditor#isCellEditable(EventObject)
     */
    public boolean isCellEditable(EventObject evt) {
        if (evt instanceof MouseEvent) {
            return ((MouseEvent) evt).getClickCount() >= 2;
        }
        return true;
    }

    /**
     * Method getCellEditorValue. Returns the spinners current value.
     *
     * @return Object
     * @see javax.swing.CellEditor#getCellEditorValue()
     */
    public Object getCellEditorValue() {
        return new Date((java.util.Date) spinner.getValue());
    }

}