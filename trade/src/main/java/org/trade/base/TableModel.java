package org.trade.base;

import javax.swing.table.AbstractTableModel;
import java.io.Serial;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Simon Allen
 * @version $Revision: 1.0 $
 */
public abstract class TableModel extends AbstractTableModel {

    @Serial
    private static final long serialVersionUID = 7428125408630828769L;
    protected String[] columnNames = {};
    protected String[] columnHeaderToolTip = {};
    protected Class<?>[] columnTypes = {};
    protected ArrayList<List<Object>> rows = new ArrayList<>(0);

    public TableModel() {
    }

    public TableModel(String[] columnHeaderToolTip) {
        this.columnHeaderToolTip = columnHeaderToolTip;
    }

    /**
     * Method populateDAO.
     *
     * @param value  Object
     * @param row    int
     * @param column int
     */
    public abstract void populateDAO(Object value, int row, int column);

    public abstract void addRow();

    /**
     * Method deleteRow.
     *
     * @param selectedRow int
     */
    public abstract void deleteRow(int selectedRow);

    public void clearAll() {
        int rowSize = rows.size() - 1;
        if (rowSize > -1) {
            rows.forEach(List::clear);
            rows.clear();
            this.fireTableRowsDeleted(0, rowSize);
        }
    }

    /**
     * Method getColumnHeaderToolTip.
     *
     * @param column int
     * @return String
     * @see javax.swing.table.TableModel#getColumnName(int)
     */
    public String getColumnHeaderToolTip(int column) {
        if (columnHeaderToolTip.length > column) {
            return columnHeaderToolTip[column];
        } else {
            return "";
        }
    }

    /**
     * Method getColumnName.
     *
     * @param column int
     * @return String
     * @see javax.swing.table.TableModel#getColumnName(int)
     */
    public String getColumnName(int column) {
        if (columnNames[column] != null) {
            return columnNames[column];
        } else {
            return "";
        }
    }

    /**
     * Method getColumnClass.
     *
     * @param column int
     * @return Class<?>
     * @see javax.swing.table.TableModel#getColumnClass(int)
     */
    public Class<?> getColumnClass(int column) {
        Object value = getValueAt(0, column);
        if (null == value) {
            return String.class;
        } else {
            return value.getClass();
        }

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
        return true;
    }

    /**
     * Method getColumnCount.
     *
     * @return int
     * @see javax.swing.table.TableModel#getColumnCount()
     */
    public int getColumnCount() {
        return columnNames.length;
    }

    /**
     * Method getRowCount.
     *
     * @return int
     * @see javax.swing.table.TableModel#getRowCount()
     */
    public int getRowCount() {
        return rows.size();
    }

    /**
     * Method getValueAt.
     *
     * @param row    int
     * @param column int
     * @return Object
     * @see javax.swing.table.TableModel#getValueAt(int, int)
     */
    public Object getValueAt(int row, int column) {

        if (!rows.isEmpty()) {

            List<Object> value = rows.get(row);
            return value.get(column);
        }
        return null;
    }

    /**
     * Method setValueAt.
     *
     * @param value  Object
     * @param row    int
     * @param column int
     * @see javax.swing.table.TableModel#setValueAt(Object, int, int)
     */
    public void setValueAt(Object value, int row, int column) {

        Object currValue = getValueAt(row, column);

        if (null != value && !value.equals(currValue)) {

            Object newValue = getColumnDataValue(currValue, value);
            this.populateDAO(newValue, row, column);
            List<Object> dataRow = rows.get(row);
            dataRow.set(column, newValue);
            fireTableCellUpdated(row, column);
        }
    }

    /**
     * Method getColumnDataValue.
     *
     * @param currValue Object
     * @param newValue  Object
     * @return Object
     */
    public Object getColumnDataValue(Object currValue, Object newValue) {
        Object returnValue = currValue;
        if (null != newValue) {
            returnValue = newValue;
        }
        return returnValue;
    }
}
