
package org.trade.core.lookup;

/**
 * Interface describing a ILookup - essentially it represents a single/multi
 * comlumn table/matrix that can be accessed by column id/name and row.
 * <p>
 * Note : This API has borrowed heavily from the javax.swing.table.TableModel
 * interface so that it is easier to hook it up to a GUI.
 *
 * @author Simon Allen
 */
public interface ILookup {
    /**
     * The number of columns that exist in the ILookup.
     *
     * @return Number of columns in this ILookup. * @exception LookupException
     */
    int getColumnCount() throws LookupException;

    /**
     * The number of rows in the ILookup.
     *
     * @return Number of rows in the ILookup. * @exception LookupException
     */
    int getRowCount() throws LookupException;

    /**
     * Retrieves the object from the current row and indicated column position.
     *
     * @param colPos int
     * @return Returns the Object from the current row and column position.
     * * @exception LookupException Thrown if the column position or row
     * position is out of range.
     */
    Object getValueAt(int colPos) throws LookupException;

    /**
     * Retrieves the object from the current row and indicated column name.
     *
     * @param colName String
     * @return Returns the Object from the current row and named column.
     * * @exception LookupException Thrown if the column name does not
     * exist or the row position is out of range.
     */
    Object getValueAt(String colName) throws LookupException;

    /**
     * Retrieves the object from the indicated row and col.
     *
     * @param row int
     * @param col int
     * @return Returns the Object from the row and column. * @exception
     * LookupException Thrown if the column name does not exist or the
     * row position is out of range.
     */
    Object getValueAt(int row, int col) throws LookupException;

    /**
     * Retrieves the column name from the indicated column position.
     *
     * @param colPos int
     * @return Returns the name of the column position. * @exception
     * LookupException Thrown if the column position is out of range.
     */
    String getColumnName(int colPos) throws LookupException;

    /**
     * The current row position is set to the row with the corresponding value
     * in the named column. If the value is not found a default position will no
     * longer be set in the object - in which case a null will be returned from
     * the single parameter getValue() methods.
     *
     * @param colValue Object
     * @param colName  String
     * @return True if the value is found in the column name. * @exception
     * LookupException Thrown if the column name does not exist.
     */
    boolean setPos(Object colValue, String colName) throws LookupException;

    /**
     * The current row position is set to the row with the corresponding value
     * in the named column. If the value is not found a default position will no
     * longer be set in the object - in which case a null will be returned from
     * the single parameter getValue() methods.
     *
     * @param colName String
     * @return True if the value is found in the column name. * @exception
     * LookupException Thrown if the column name does not exist.
     */
    boolean setDefaultPos(String colName) throws LookupException;

    /**
     * The current row position is set to the row with the corresponding value
     * in the named column. If the value is not found a default position will no
     * longer be set in the object - in which case a null will be returned from
     * the single parameter getValue() methods.
     *
     * @param colValue Object
     * @param colPos   int
     * @return True if the value is found in the column position. * @exception
     * LookupException Thrown if the column position is out of range.
     */
    boolean setPos(Object colValue, int colPos) throws LookupException;

    /**
     * @return A copy of this object.
     */
    Object clone();
}
