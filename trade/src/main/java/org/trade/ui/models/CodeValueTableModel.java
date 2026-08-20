package org.trade.ui.models;

import org.trade.base.TableModel;
import org.trade.core.persistent.codetype.CodeAttribute;
import org.trade.core.persistent.codetype.CodeType;
import org.trade.core.persistent.codetype.CodeValue;
import org.trade.core.persistent.codetype.DecodeType;
import org.trade.core.util.CoreUtils;

import java.io.Serial;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * @author Simon Allen
 * @version $Revision: 1.0 $
 */
public class CodeValueTableModel extends TableModel {
    /**
     *
     */
    @Serial
    private static final long serialVersionUID = 3087514589731145479L;
    private static final String ATTRIBUTE_NAME = "Attribute name*";
    private static final String VALUE = "Value*";
    DecodeType data = null;

    public CodeValueTableModel() {

        columnNames = new String[2];
        columnNames[0] = ATTRIBUTE_NAME;
        columnNames[1] = VALUE;
    }

    /**
     * Method getData.
     *
     * @return DecodeType
     */
    public DecodeType getData() {
        return data;
    }

    /**
     * Method setData.
     *
     * @param data DecodeType
     */
    public void setData(DecodeType data) {

        this.data = data;
        this.clearAll();

        if (!getData().getCodeValues().isEmpty()) {

            for (final CodeValue element : getData().getCodeValues()) {

                final List<Object> newRow = new ArrayList<>();
                getNewRow(newRow, element);
                rows.add(newRow);
            }

            fireTableDataChanged();
        }
    }

    /**
     * Method populateDAO.
     *
     * @param value  Object
     * @param row    int
     * @param column int
     */
    public void populateDAO(Object value, int row, int column) {

        final CodeValue element = getData().getCodeValues().get(row);

        switch (column) {
            case 0: {

                element.getCodeAttribute().setName((String) value);
                break;
            }
            case 1: {

                element.setCodeValue((String) value);
                break;
            }
            default: {
            }
        }
        element.setDirty(true);
    }

    /**
     * Method deleteRow.
     *
     * @param selectedRow int
     */
    public void deleteRow(int selectedRow) {

        String name = (String) this.getValueAt(selectedRow, 0);

        for (final CodeValue element : getData().getCodeValues()) {

            if (CoreUtils.nullSafeComparator(element.getCodeValue(), name) == 0) {

                getData().getCodeValues().remove(element);
                getData().setDirty(true);
                final List<Object> currRow = rows.get(selectedRow);
                rows.remove(currRow);
                this.fireTableRowsDeleted(selectedRow, selectedRow);
                break;
            }
        }
    }

    public void addRow() {

        final CodeValue element = new CodeValue(this.data.getCodeValues().getFirst().getCodeAttribute(), this.data,null);
        getData().getCodeValues().add(element);
        getData().setDirty(true);
        final List<Object> newRow = new ArrayList<>();
        getNewRow(newRow, element);
        rows.add(newRow);

        // Tell the listeners a new table has arrived.
        this.fireTableRowsInserted(rows.size() - 1, rows.size() - 1);
    }

    /**
     * Method getNewRow.
     *
     * @param newRow  List<Object>
     * @param element CodeAttribute
     */
    public void getNewRow(List<Object> newRow, CodeValue element) {

        newRow.add(element.getCodeAttribute().getName());
        newRow.add(element.getCodeValue());
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

        if (column == 0) {

            return false;
        }

        if (Objects.equals(columnNames[column], ATTRIBUTE_NAME)) {

            CodeValue element = getData().getCodeValues().get(row);
            return null == element.getId();
        }
        return true;
    }
}
