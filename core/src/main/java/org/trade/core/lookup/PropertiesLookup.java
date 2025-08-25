package org.trade.core.lookup;

import java.io.Serial;
import java.util.ArrayList;
import java.util.List;

/**
 * Implementation of the ILookup interface that uses data from the
 * ConfigProperties object for providing its ILookup information.
 *
 * @author Simon Allen
 */
public class PropertiesLookup implements ILookup, Cloneable, java.io.Serializable {
    /**
     *
     */
    @Serial
    private static final long serialVersionUID = 5263608853348477640L;

    //
    // Private Attributes
    //
    private List<?> data;
    private List<?> columnNames;
    private int currentRowPos = -1;

    /**
     * Constructor
     *
     * @param columnNames List<?>
     * @param data        List<?>
     */
    public PropertiesLookup(List<?> columnNames, List<?> data) {

        this.columnNames = columnNames;
        this.data = data;

        // A precaustion to make sure that calls to my API won't throw
        // nulls
        if (null == this.columnNames) {
            this.columnNames = new ArrayList<>();
        }

        if (null == this.data) {
            this.data = new ArrayList<>();
        }
    }

    /**
     * Method getColumnCount.
     *
     * @return int
     * @see ILookup#getColumnCount()
     */
    public int getColumnCount() {
        return (columnNames.size());
    }

    /**
     * Method getRowCount.
     *
     * @return int
     * @see ILookup#getRowCount()
     */
    public int getRowCount() throws LookupException {
        return (data.size());
    }

    /**
     * Method getValueAt.
     *
     * @param col int
     * @return Object
     * @see ILookup#getValueAt(int)
     */
    public Object getValueAt(int col) throws LookupException {
        return (doGetValue(currentRowPos, col));
    }

    /**
     * Method getValueAt.
     *
     * @param colName String
     * @return Object
     * @see ILookup#getValueAt(String)
     */
    public Object getValueAt(String colName) throws LookupException {
        return (doGetValue(currentRowPos, doGetColPos(colName)));
    }

    /**
     * Method getValueAt.
     *
     * @param row int
     * @param col int
     * @return Object
     * @see ILookup#getValueAt(int, int)
     */
    public Object getValueAt(int row, int col) throws LookupException {
        return (doGetValue(row, col));
    }

    /**
     * Method getColumnName.
     *
     * @param colPos int
     * @return String
     * @see ILookup#getColumnName(int)
     */
    public String getColumnName(int colPos) throws LookupException {
        String colName;

        try {
            colName = "" + columnNames.get(colPos);
        } catch (Throwable t) {
            throw new LookupException(t, "Not a valid column position");
        }

        return (colName);
    }

    /**
     * Method setDefaultPos.
     *
     * @param colName String
     * @return boolean
     * @see ILookup#setDefaultPos(String)
     */
    public boolean setDefaultPos(String colName) throws LookupException {
        return (doSetPos(doGetValue(0, doGetColPos(colName)), doGetColPos(colName)));
    }

    /**
     * Method setPos.
     *
     * @param colValue Object
     * @param colName  String
     * @return boolean
     * @see ILookup#setPos(Object, String)
     */
    public boolean setPos(Object colValue, String colName) throws LookupException {
        return (doSetPos(colValue, doGetColPos(colName)));
    }

    /**
     * Method setPos.
     *
     * @param colValue Object
     * @param col      int
     * @return boolean
     * @see ILookup#setPos(Object, int)
     */
    public boolean setPos(Object colValue, int col) {
        return (doSetPos(colValue, col));
    }

    /**
     * Method clone.
     *
     * @return Object
     * @see ILookup#clone()
     */
    public Object clone() {

        return (new PropertiesLookup(columnNames, data));
    }

    //
    // Private Methods
    //

    /**
     * Method doGetColPos.
     *
     * @param colName String
     * @return int
     */
    private int doGetColPos(String colName) throws LookupException {
        int pos = -1;
        int columnNamesSize = columnNames.size();

        for (int i = 0; i < columnNamesSize; i++) {
            if (columnNames.get(i).equals(colName)) {
                // Have found the position
                pos = i;
                break;
            }
        }

        if (-1 == pos) {
            throw new LookupException("Invalid Column Name");
        }

        return (pos);
    }

    /**
     * Method doGetValue.
     *
     * @param rowPos int
     * @param colPos int
     * @return Object
     */
    private Object doGetValue(int rowPos, int colPos) throws LookupException {
        Object rVal = null;

        // i.e a setPos was not performed.
        if (rowPos != -1) {
            try {

                List<?> row = (List<?>) data.get(rowPos);
                rVal = row.get(colPos);
            } catch (Throwable t) {

                throw new LookupException(t, "Out of bounds");
            }
        }
        return (rVal);
    }

    /**
     * Method doSetPos.
     *
     * @param colValue Object
     * @param col      int
     * @return boolean
     */
    private boolean doSetPos(Object colValue, int col) {
        boolean rVal = false;

        currentRowPos = -1;

        int dataSize = data.size();

        for (int i = 0; i < dataSize; i++) {
            List<?> row = (List<?>) data.get(i);

            if (row.get(col).equals(colValue)) {
                currentRowPos = i;
                rVal = true;

                break;
            }
        }
        return (rVal);
    }
}
