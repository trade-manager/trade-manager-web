package org.trade.ui.models;

import org.trade.base.TableModel;
import org.trade.core.persistent.dao.Account;
import org.trade.core.persistent.dao.Portfolio;
import org.trade.core.util.CoreUtils;
import org.trade.core.valuetype.AccountType;
import org.trade.core.valuetype.Currency;
import org.trade.core.valuetype.Date;
import org.trade.core.valuetype.Money;

import java.io.Serial;
import java.util.ArrayList;
import java.util.List;
/**
 *
 * @author Simon Allen
 * @version $Revision: 1.0 $
 */
public class AccountTableModel extends TableModel {
    /**
     *
     */
    @Serial
    private static final long serialVersionUID = 3087514589731145479L;

    private static final String NAME = "Name*";
    private static final String ACCT_NUMBER = "Acct #*";
    private static final String ACCT_TYPE = "Type*";
    private static final String ACCT_ALIAS = "Alias";
    private static final String CURRENCY = "Currency*";
    private static final String AVAILABLE_FUNDS = "Availble Funds";
    private static final String BUYING_POWER = " Buying Power";
    private static final String CASH_BALANCE = "Cash Bal";
    private static final String GROSS_POSITION_VALUE = "Gross Pos Val";
    private static final String REALIZED_PL = "Realized P/L";
    private static final String UNREALIZED_PL = "Unrealized P/L";
    private static final String LAST_UPDATED = "  Last Update  ";

    private static final String[] columnHeaderToolTip = {null, null, "Use Corp for FA accounts", null, null, null,
            null, null, null, null, null, null};

    private Portfolio data = null;

    public AccountTableModel() {
        super(columnHeaderToolTip);
        columnNames = new String[12];
        columnNames[0] = NAME;
        columnNames[1] = ACCT_NUMBER;
        columnNames[2] = ACCT_TYPE;
        columnNames[3] = ACCT_ALIAS;
        columnNames[4] = CURRENCY;
        columnNames[5] = AVAILABLE_FUNDS;
        columnNames[6] = BUYING_POWER;
        columnNames[7] = CASH_BALANCE;
        columnNames[8] = GROSS_POSITION_VALUE;
        columnNames[9] = REALIZED_PL;
        columnNames[10] = UNREALIZED_PL;
        columnNames[11] = LAST_UPDATED;
    }

    /**
     * Method getData.
     *
     * @return Aspects
     */
    public Portfolio getData() {
        return data;
    }

    /**
     * Method setData.
     *
     * @param data Aspects
     */
    public void setData(Portfolio data) {

        this.data = data;
        this.clearAll();
        if (!getData().getAccounts().isEmpty()) {

            for (final Account element : getData().getAccounts()) {

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

        final Account element = getData().getAccounts().get(row);

        switch (column) {
            case 0: {
                element.setName((String) value);
                break;
            }
            case 1: {
                element.setAccountNumber((String) value);
                break;
            }
            case 2: {
                element.setAccountType(((AccountType) value).getCode());
                break;
            }
            case 3: {
                element.setAlias((String) value);
                break;
            }
            case 4: {
                element.setCurrency(((Currency) value).getCode());
                break;
            }
            case 5: {
                element.setAvailableFunds(((Money) value).getBigDecimalValue());
                break;
            }
            case 6: {
                element.setBuyingPower(((Money) value).getBigDecimalValue());
                break;
            }
            case 7: {
                element.setCashBalance(((Money) value).getBigDecimalValue());
                break;
            }
            case 8: {
                element.setGrossPositionValue(((Money) value).getBigDecimalValue());
                break;
            }
            case 9: {
                element.setRealizedPnL(((Money) value).getBigDecimalValue());
                break;
            }
            case 10: {
                element.setUnrealizedPnL(((Money) value).getBigDecimalValue());
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

        String acctNumber = (String) this.getValueAt(selectedRow, 1);

        for (final Account element : getData().getAccounts()) {

            if (CoreUtils.nullSafeComparator(element.getAccountNumber(), acctNumber) == 0) {

                getData().getAccounts().remove(element);
                getData().setDirty(true);
                final List<Object> currRow = rows.get(selectedRow);
                rows.remove(currRow);
                this.fireTableRowsDeleted(selectedRow, selectedRow);
                break;
            }
        }
    }

    public void addRow() {

        final Account account = new Account();
        getData().getAccounts().add(account);
        final List<Object> newRow = new ArrayList<>();
        getNewRow(newRow, account);
        rows.add(newRow);
        // Tell the listeners a new table has arrived.
        this.fireTableRowsInserted(rows.size() - 1, rows.size() - 1);
    }

    /**
     * Method getNewRow.
     *
     * @param newRow  List<Object>
     * @param element Account
     */
    public void getNewRow(List<Object> newRow, Account element) {

        newRow.add(element.getName());
        newRow.add(element.getAccountNumber());

        if (null == element.getAccountType()) {

            newRow.add(new AccountType());
        } else {

            newRow.add(AccountType.newInstance(element.getAccountType()));
        }

        newRow.add(element.getAlias());
        newRow.add(Currency.newInstance(element.getCurrency()));

        if (null == element.getAvailableFunds()) {

            newRow.add(new Money(0));
        } else {

            newRow.add(new Money(element.getAvailableFunds()));
        }

        if (null == element.getBuyingPower()) {

            newRow.add(new Money(0));
        } else {

            newRow.add(new Money(element.getBuyingPower()));
        }

        if (null == element.getCashBalance()) {

            newRow.add(new Money(0));
        } else {

            newRow.add(new Money(element.getCashBalance()));
        }

        if (null == element.getGrossPositionValue()) {

            newRow.add(new Money(0));
        } else {

            newRow.add(new Money(element.getGrossPositionValue()));
        }
        if (null == element.getRealizedPnL()) {

            newRow.add(new Money(0));
        } else {

            newRow.add(new Money(element.getRealizedPnL()));
        }

        if (null == element.getUnrealizedPnL()) {

            newRow.add(new Money(0));
        } else {

            newRow.add(new Money(element.getUnrealizedPnL()));
        }

        if (null == element.getUpdatedDate()) {

            newRow.add(new Date());
        } else {

            newRow.add(new Date(element.getUpdatedDate()));
        }
    }
}
