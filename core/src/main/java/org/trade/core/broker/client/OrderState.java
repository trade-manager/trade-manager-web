package org.trade.core.broker.client;

import org.trade.core.util.CoreUtils;
import org.trade.core.valuetype.Money;

/**
 *
 */
public class OrderState {

    public String status;
    public String initMargin;
    public String maintMargin;
    public String equityWithLoan;
    public double commission;
    public double minCommission;
    public double maxCommission;
    public String commissionCurrency;
    public String warningText;

    public OrderState() {
        this(null, null, null, null, 0.0, 0.0, 0.0, null, null);
    }

    /**
     * Constructor for OrderState.
     *
     * @param status             String
     * @param initMargin         String
     * @param maintMargin        String
     * @param equityWithLoan     String
     * @param commission         double
     * @param minCommission      double
     * @param maxCommission      double
     * @param commissionCurrency String
     * @param warningText        String
     */
    public OrderState(String status, String initMargin, String maintMargin, String equityWithLoan, double commission,
                      double minCommission, double maxCommission, String commissionCurrency, String warningText) {

        this.initMargin = initMargin;
        this.maintMargin = maintMargin;
        this.equityWithLoan = equityWithLoan;
        this.commission = commission;
        this.minCommission = minCommission;
        this.maxCommission = maxCommission;
        this.commissionCurrency = commissionCurrency;
        this.warningText = warningText;
    }

    /**
     * Method equals.
     *
     * @param objectToCompare Object
     * @return boolean
     */
    public boolean equals(Object objectToCompare) {

        if (this == objectToCompare) {
            return true;
        }

        if (objectToCompare == null) {
            return false;
        }

        if (!(objectToCompare instanceof OrderState state)) {
            return false;
        }

        if (CoreUtils.nullSafeComparator(new Money(commission), new Money(state.commission)) != 0
                || (CoreUtils.nullSafeComparator(new Money(minCommission), new Money(state.minCommission)) != 0)
                || (CoreUtils.nullSafeComparator(new Money(maxCommission), new Money(state.maxCommission)) != 0)) {
            return false;
        }

        return (CoreUtils.nullSafeComparator(status, state.status) == 0)
                && (CoreUtils.nullSafeComparator(initMargin, state.initMargin) == 0)
                && (CoreUtils.nullSafeComparator(maintMargin, state.maintMargin) == 0)
                && (CoreUtils.nullSafeComparator(equityWithLoan, state.equityWithLoan) == 0)
                && (CoreUtils.nullSafeComparator(commissionCurrency, state.commissionCurrency) == 0);
    }
}
