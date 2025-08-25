package org.trade.ui.models;

import org.trade.core.dao.Aspect;
import org.trade.core.dao.Aspects;
import org.trade.core.persistent.dao.CodeType;
import org.trade.core.persistent.dao.IndicatorParameters;
import org.trade.core.util.CoreUtils;

import java.io.Serial;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Simon Allen
 * @version $Revision: 1.0 $
 */
public class IndicatorParametersTableModel extends AspectTableModel {
    /**
     *
     */
    @Serial
    private static final long serialVersionUID = 3087514589731145479L;
    private static final String NAME = "Indicator Name*";
    private static final String DESCRIPTION = "Description";

    Aspects data = null;

    public IndicatorParametersTableModel() {

        columnNames = new String[2];
        columnNames[0] = NAME;
        columnNames[1] = DESCRIPTION;

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
                getNewRow(newRow, (CodeType) element);
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

        final CodeType element = (CodeType) getData().getAspects().get(row);

        switch (column) {
            case 0: {
                element.setName((String) value);
                break;
            }
            case 1: {
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

        String name = (String) this.getValueAt(selectedRow, 0);

        for (final Aspect element : getData().getAspects()) {

            if (CoreUtils.nullSafeComparator(((CodeType) element).getName(), name) == 0) {

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

        final IndicatorParameters element = new IndicatorParameters("", CodeType.IndicatorParameters, "");
        getData().add(element);
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
     * @param element CodeType
     */
    public void getNewRow(List<Object> newRow, CodeType element) {

        newRow.add(element.getName());
        newRow.add(element.getDescription());
    }
}
