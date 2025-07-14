/* ===========================================================
 * TradeManager : a application to trade strategies for the Java(tm) platform
 * ===========================================================
 *
 * (C) Copyright 2011-2011, by Simon Allen and Contributors.
 *
 * Project Info:  org.trade
 *
 * This library is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation; either version 2.1 of the License, or
 * (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY
 * or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public
 * License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301,
 * USA.
 *
 * [Java is a trademark or registered trademark of Oracle, Inc.
 * in the United States and other countries.]
 *
 * (C) Copyright 2011-2011, by Simon Allen and Contributors.
 *
 * Original Author:  Simon Allen;
 * Contributor(s):   -;
 *
 * Changes
 * -------
 *
 */
package org.trade.ui.models;

import org.trade.core.dao.Aspect;
import org.trade.core.dao.Aspects;
import org.trade.core.persistent.dao.Account;
import org.trade.core.persistent.dao.Portfolio;
import org.trade.core.util.CoreUtils;
import org.trade.core.valuetype.AllocationMethod;
import org.trade.core.valuetype.DAOAccount;
import org.trade.core.valuetype.Decode;
import org.trade.core.valuetype.YesNo;

import java.io.Serial;
import java.util.ArrayList;
import java.util.List;

/**
 *
 */
public class PortfolioTableModel extends AspectTableModel {
    /**
     *
     */
    @Serial
    private static final long serialVersionUID = 3087514589731145479L;

    private static final String NAME = "Name*";
    private static final String PORTFOLIO_ALIAS = "Alias";
    private static final String DESCRIPTION = "Description";
    private static final String ALLOCATION_METHOD = "Allocation Method";
    private static final String MASTER_ACCT_NUMBER = "Add Acct #*";
    private static final String IS_DEFAULT = "Default";

    private static final String[] columnHeaderToolTip = {null, null, null, "The account that is subscribed to", null};

    private Aspects data = null;

    public PortfolioTableModel() {
        super(columnHeaderToolTip);
        columnNames = new String[6];
        columnNames[0] = NAME;
        columnNames[1] = PORTFOLIO_ALIAS;
        columnNames[2] = DESCRIPTION;
        columnNames[3] = ALLOCATION_METHOD;
        columnNames[4] = MASTER_ACCT_NUMBER;
        columnNames[5] = IS_DEFAULT;
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

        if (!getData().getAspect().isEmpty()) {

            for (final Aspect element : getData().getAspect()) {

                final List<Object> newRow = new ArrayList<>();
                getNewRow(newRow, (Portfolio) element);
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

        final Portfolio element = (Portfolio) getData().getAspect().get(row);

        switch (column) {
            case 0: {
                element.setName((String) value);
                break;
            }
            case 1: {
                element.setAlias((String) value);
                break;
            }
            case 2: {
                element.setDescription((String) value);
                break;
            }
            case 3: {
                element.setAllocationMethod(((AllocationMethod) value).getCode());
                break;
            }
            case 4: {

                Account account = (Account) ((DAOAccount) value).getObject();
                boolean exists = false;

                for (Account item : element.getAccounts()) {

                    if (account.getAccountNumber().equals(item.getAccountNumber())) {

                        exists = true;
                        break;
                    }
                }

                if (!exists) {

                    element.getAccounts().add(account);
                }
                break;
            }
            case 5: {
                for (Aspect item : getData().getAspect()) {
                    Portfolio portfolio = (Portfolio) item;
                    if (!portfolio.getName().equals(element.getName()) && portfolio.getIsDefault()) {
                        portfolio.setIsDefault(false);
                        portfolio.setDirty(true);
                    }
                }
                element.setIsDefault(Boolean.valueOf(((YesNo) value).getCode()));
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

        if (getData().getAspect().size() > 1) {

            String name = (String) this.getValueAt(selectedRow, 0);

            for (final Aspect element : getData().getAspect()) {

                if (CoreUtils.nullSafeComparator(((Portfolio) element).getName(), name) == 0) {

                    getData().remove(element);
                    getData().setDirty(true);
                    final List<Object> currRow = rows.get(selectedRow);
                    rows.remove(currRow);
                    this.fireTableRowsDeleted(selectedRow, selectedRow);
                    break;
                }
            }
        }
    }

    public void addRow() {

        final Portfolio element = new Portfolio();
        getData().getAspect().add(element);

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
     * @param element Portfolio
     */
    public void getNewRow(List<Object> newRow, Portfolio element) {

        newRow.add(element.getName());
        newRow.add(element.getAlias());
        newRow.add(element.getDescription());

        if (null == element.getAllocationMethod()) {

            newRow.add(AllocationMethod.newInstance(Decode.NONE));
        } else {

            newRow.add(AllocationMethod.newInstance(element.getAllocationMethod()));
        }

        newRow.add(DAOAccount.newInstance(Decode.NONE));
        newRow.add(YesNo.newInstance(element.getIsDefault()));
    }
}
