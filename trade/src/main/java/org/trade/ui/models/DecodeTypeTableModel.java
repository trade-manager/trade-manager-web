package org.trade.ui.models;

import org.trade.core.aspect.Aspect;
import org.trade.core.aspect.Aspects;
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
public class DecodeTypeTableModel extends AspectTableModel {
    /**
     *
     */
    @Serial
    private static final long serialVersionUID = 3087514589731145479L;
    private static final String ID = "Id*";
    private static final String TYPE = "Type*";
    private static final String DESCRIPTION = "Description";
    Aspects data = null;

    public DecodeTypeTableModel() {

        columnNames = new String[3];
        columnNames[0] = ID;
        columnNames[1] = TYPE;
        columnNames[2] = DESCRIPTION;
    }

    /**
     * Method getData.
     *
     * @return Aspects
     */
    public Aspects getData() {
        return data;
    }

    /**
     * Method setData.
     *
     * @param data Aspects
     */
    public void setData(Aspects data) {

        this.data = data;
        this.clearAll();

        if (!getData().getAspects().isEmpty()) {

            for (final Aspect element : getData().getAspects()) {

                final List<Object> newRow = new ArrayList<>();
                getNewRow(newRow, (DecodeType) element);
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

        final DecodeType element = (DecodeType) getData().getAspects().get(row);

        switch (column) {
            case 0: {
                element.setId((Long) value);
                break;
            }
            case 1: {
                element.setType((String) value);
                break;
            }
            case 2: {
                element.setDescription((String) value);
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

        Long name = (Long) this.getValueAt(selectedRow, 0);

        for (final Aspect element : getData().getAspects()) {

            if (CoreUtils.nullSafeComparator(element.getId(), name) == 0) {
                
                getData().remove(element);
                getData().setDirty(true);
                final List<Object> currRow = rows.get(selectedRow);
                rows.remove(currRow);
                this.fireTableRowsDeleted(selectedRow, selectedRow);
                break;
            }
        }
    }

    public void addRow() {

        DecodeType selectedDecodeType = (DecodeType) this.getSelectRowValue();

        if (null != selectedDecodeType) {

            try {

                final DecodeType element = (DecodeType) selectedDecodeType.clone();
                element.setId(null);
                element.setDirty(true);
                element.setCodeValues(new ArrayList<>(0));

                for (final CodeValue codeValue : selectedDecodeType.getCodeValues()) {

                    CodeValue newCodeValue = new CodeValue(codeValue.getCodeAttribute(), element, "");
                    element.addChild(newCodeValue);
                }

                getData().add(element);
                getData().setDirty(true);
                final List<Object> newRow = new ArrayList<>();
                getNewRow(newRow, element);
                rows.add(newRow);
                // Tell the listeners a new table has arrived.
                this.fireTableRowsInserted(rows.size() - 1, rows.size() - 1);

            } catch (Exception ex) {

                throw new RuntimeException(ex);
            }
        }
    }

    /**
     * Method getNewRow.
     *
     * @param newRow  List<Object>
     * @param element DecodeType
     */
    public void getNewRow(List<Object> newRow, DecodeType element) {

        newRow.add(element.getId());
        newRow.add(element.getType());
        newRow.add(element.getDescription());
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

        if (Objects.equals(columnNames[column], ID)) {

            return false;
        }
        return true;
    }
}
