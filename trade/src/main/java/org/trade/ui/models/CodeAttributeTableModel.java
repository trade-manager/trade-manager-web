package org.trade.ui.models;

import org.trade.base.TableModel;
import org.trade.core.persistent.dao.CodeAttribute;
import org.trade.core.persistent.dao.CodeType;
import org.trade.core.util.CoreUtils;
import org.trade.core.valuetype.DataType;

import java.io.Serial;
import java.util.ArrayList;
import java.util.List;
/**
 *
 * @author Simon Allen
 * @version $Revision: 1.0 $
 */
public class CodeAttributeTableModel extends TableModel {
    /**
     *
     */
    @Serial
    private static final long serialVersionUID = 3087514589731145479L;
    private static final String NAME = "Parm Name*";
    private static final String DESCRIPTION = "Description";
    private static final String DEFAULT_VALUE = "Default Value*";
    private static final String CLASS_NAME = "Data Type*";
    private static final String CLASS_EDITOR_NAME = "Data Type Editor";

    CodeType data = null;

    public CodeAttributeTableModel() {

        columnNames = new String[5];
        columnNames[0] = NAME;
        columnNames[1] = DESCRIPTION;
        columnNames[2] = DEFAULT_VALUE;
        columnNames[3] = CLASS_NAME;
        columnNames[4] = CLASS_EDITOR_NAME;
    }

    /**
     * Method getData.
     *
     * @return CodeType
     */
    public CodeType getData() {
        return data;
    }

    /**
     * Method setData.
     *
     * @param data CodeType
     */
    public void setData(CodeType data) {

        this.data = data;
        this.clearAll();

        if (!getData().getCodeAttribute().isEmpty()) {

            for (final CodeAttribute element : getData().getCodeAttribute()) {

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

        final CodeAttribute element = getData().getCodeAttribute().get(row);

        switch (column) {
            case 0: {
                element.setName((String) value);
                break;
            }
            case 1: {
                element.setDescription((String) value);
                break;
            }
            case 2: {
                element.setDefaultValue((String) value);
                break;
            }
            case 3: {
                element.setClassName(((DataType) value).getCode());
                break;
            }
            case 4: {
                element.setEditorClassName((String) value);
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

        for (final CodeAttribute element : getData().getCodeAttribute()) {

            if (CoreUtils.nullSafeComparator(element.getName(), name) == 0) {

                getData().getCodeAttribute().remove(element);
                getData().setDirty(true);
                final List<Object> currRow = rows.get(selectedRow);
                rows.remove(currRow);
                this.fireTableRowsDeleted(selectedRow, selectedRow);
                break;
            }
        }
    }

    public void addRow() {

        final CodeAttribute element = new CodeAttribute(this.data, "", "", null, "", null);
        getData().getCodeAttribute().add(element);
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
    public void getNewRow(List<Object> newRow, CodeAttribute element) {

        newRow.add(element.getName());
        newRow.add(element.getDescription());
        newRow.add(element.getDefaultValue());
        newRow.add(DataType.newInstance(element.getClassName()));
        newRow.add(element.getEditorClassName());
    }
}
