package org.trade.base;

import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableModel;
import java.io.Serial;

/**
 * @author Simon Allen
 * @version $Id: TableMap.java,v 1.1 2001/10/18 01:32:14 simon Exp $
 */
public class TableMap extends AbstractTableModel implements TableModelListener {
    /**
     *
     */
    @Serial
    private static final long serialVersionUID = 5933237472572427135L;
    protected TableModel model;

    /**
     * Method getModel.
     *
     * @return TableModel
     */
    public TableModel getModel() {
        return model;
    }

    /**
     * Method setModel.
     *
     * @param model TableModel
     */
    public void setModel(TableModel model) {
        this.model = model;

        model.addTableModelListener(this);
    }

    /**
     * Method getValueAt. By default, Implement TableModel by forwarding all
     * messages to the model.
     *
     * @param aRow    int
     * @param aColumn int
     * @return Object
     * @see javax.swing.table.TableModel#getValueAt(int, int)
     */
    public Object getValueAt(int aRow, int aColumn) {
        return model.getValueAt(aRow, aColumn);
    }

    /**
     * Method setValueAt.
     *
     * @param aValue  Object
     * @param aRow    int
     * @param aColumn int
     * @see javax.swing.table.TableModel#setValueAt(Object, int, int)
     */
    public void setValueAt(Object aValue, int aRow, int aColumn) {
        model.setValueAt(aValue, aRow, aColumn);
    }

    /**
     * Method getRowCount.
     *
     * @return int
     * @see javax.swing.table.TableModel#getRowCount()
     */
    public int getRowCount() {
        return (model == null) ? 0 : model.getRowCount();
    }

    /**
     * Method getColumnCount.
     *
     * @return int
     * @see javax.swing.table.TableModel#getColumnCount()
     */
    public int getColumnCount() {
        return (model == null) ? 0 : model.getColumnCount();
    }

    /**
     * Method getColumnName.
     *
     * @param aColumn int
     * @return String
     * @see javax.swing.table.TableModel#getColumnName(int)
     */
    public String getColumnName(int aColumn) {
        return model.getColumnName(aColumn);
    }

    /**
     * Method getColumnClass.
     *
     * @param aColumn int
     * @return Class<?>
     * @see javax.swing.table.TableModel#getColumnClass(int)
     */
    public Class<?> getColumnClass(int aColumn) {
        return model.getColumnClass(aColumn);
    }

    /**
     * Method isCellEditable.
     *
     * @param row    int
     * @param column int
     * @return boolean
     * @see javax.swing.table.TableModel#isCellEditable(int, int)
     */
    public boolean isCellEditable(int row, int column) {
        return model.isCellEditable(row, column);
    }

    /**
     * Method tableChanged. Implementation of the TableModelListener interface,
     * By default forward all events to all the listeners.
     *
     * @param e TableModelEvent
     * @see javax.swing.event.TableModelListener#tableChanged(TableModelEvent)
     */
    public void tableChanged(TableModelEvent e) {
        fireTableChanged(e);
    }
}
