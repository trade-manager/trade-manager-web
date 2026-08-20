package org.trade.core.valuetype;

import org.trade.core.conversion.JavaTypeTranslator;
import org.trade.core.message.IMessageFactory;
import org.trade.core.util.CoreUtils;
import org.trade.core.validator.IExceptionMessageListener;
import org.trade.core.validator.IValidator;
import org.trade.core.validator.PercentValidator;

import java.io.Serial;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.RoundingMode;
import java.util.Comparator;
import java.util.Objects;

/**
 * @author Simon Allen
 * @version $Revision: 1.0 $
 */
public class Percent extends ValueType implements Comparator<Percent>, Comparable<Percent> {
    /**
     *
     */
    @Serial
    private static final long serialVersionUID = 6356086072126179279L;

    public static final String PERCENT_POSITIVE_7_2 = "($)#(,)###(,)###(.##)";
    public static final String PERCENT_NONNEGATIVE_8_2 = "($)##(,)###(,)###(.##)";
    public static final String PERCENT_POSITIVE_10_2 = "($)#(,)###(,)###(,)###(.##)";
    public static final String PERCENT_NONNEGATIVE_11_2 = "($)##(,)###(,)###(,)###(.##)";
    public static final Percent ZERO = new Percent(0L, 0);
    protected static Boolean ascending = true;

    static {

        // Register the appropriate converters
        JavaTypeTranslator.registerDynamicTypeConverter(new ObjectToPercent());
        JavaTypeTranslator.registerDynamicTypeConverter(new PercentToObject());
    }

    private BigDecimal value = null;
    private String format = PERCENT_NONNEGATIVE_11_2;
    private String invalidValue = null; // This will be null if there were
    private static final int SCALE = 6;
    private static final String MULTIPLIER = "100";

    /**
     * Default Constructor. Create an object and initialize it to empty.
     */
    public Percent() {
    }

    /**
     * Default Constructor. Create an object and initialize it to empty.
     *
     * @param PercentString String
     */
    public Percent(String PercentString) {

        if ((null != PercentString) && (!PercentString.isEmpty())) {

            // This is necessary because Java will parse strings with multiple
            // dashes
            if (PercentString.indexOf("-") != PercentString.lastIndexOf("-")) {

                invalidValue = PercentString;
            } else {

                try {

                    setBigDecimal(new BigDecimal(PercentString));

                } catch (NumberFormatException e) {
                    invalidValue = PercentString;
                }
            }
        }
    }

    /**
     * Constructor
     *
     * @param d double
     */
    public Percent(double d) {

        setBigDecimal(new BigDecimal(d));
    }

    /**
     * Constructor
     *
     * @param d Double
     */
    public Percent(Double d) {

        setBigDecimal(BigDecimal.valueOf(d));
    }

    /**
     * Constructor
     *
     * @param bd BigDecimal
     */
    public Percent(BigDecimal bd) {

        setBigDecimal(bd);
    }

    /**
     * Constructor for Percent.
     *
     * @param Percent Percent
     */
    public Percent(Percent Percent) {

        value = Percent.value;
        format = Percent.format;
        invalidValue = Percent.invalidValue;
    }

    /**
     * Constructor
     *
     * @param nonDecimalAmount long
     * @param decimalAmount    int
     */
    public Percent(long nonDecimalAmount, int decimalAmount) {

        // Set up the default constraints for basic Percent values
        BigDecimal val = new BigDecimal((nonDecimalAmount * 100) + decimalAmount);
        setBigDecimal(val.movePointLeft(SCALE));
    }

    /**
     * Provides the format used for determining if this object is valid. The
     * format should be one of the format constants on this class. The default
     * format is NORMAL_11_2.
     *
     * @param format String
     */
    public void setFormat(String format) {

        this.format = format;
    }

    /**
     * Method getFormat.
     *
     * @return String
     */
    public String getFormat() {

        return format;
    }

    /**
     * This maximum length includes the decimal point and digits to both sides.
     *
     * @return int
     */
    public int getMaxLength() {

        return switch (getFormat()) {

            case PERCENT_NONNEGATIVE_8_2 -> 11;
            case PERCENT_POSITIVE_10_2 -> 13;
            case PERCENT_POSITIVE_7_2 -> 10;
            default -> 14;
        };
    }

    /**
     * This indicates whether zero is an acceptable value for this instance.
     * Currently this is determined by the format returned by getFormat().
     *
     * @return boolean
     */
    public boolean canBeZero() {

        boolean zero = true;

        if (getFormat().equals(PERCENT_POSITIVE_7_2)) {

            zero = false;
        } else if (getFormat().equals(PERCENT_POSITIVE_10_2)) {

            zero = false;
        }

        return zero;
    }

    /**
     * This indicates whether zero is an acceptable value for this instance.
     * Currently this is determined by the format returned by getFormat().
     *
     * @return boolean
     */
    public boolean canBeNegative() {

        // Currently all formats prohibit negative numbers.

        return false;
    }

    /**
     * Method isNegative.
     *
     * @return boolean
     */
    public boolean isNegative() {

        assertDefined();
        return value.compareTo(new BigDecimal(0)) < 0;
    }

    /**
     * Method isEmpty.
     *
     * @return boolean
     */
    public boolean isEmpty() {

        return (null == value) || (null != invalidValue);
    }

    /**
     * @return The value before the decimal point in the Percent value.
     */
    public long getNonDecimalAmount() {

        assertDefined();

        long nonDecimalAmount = 0;

        if (null != value) {

            nonDecimalAmount = value.longValue();
        }

        return nonDecimalAmount;
    }

    /**
     * See description of superclass method. Overrode functionality to return
     * the BigDecimal this object is using intrnally.
     *
     * @return Object
     */
    public Object getSQLObject() {
        return (getBigDecimalValue());
    }

    /**
     * @return The value after the decimal point in the Percent value.
     */
    public int getDecimalAmount() {

        assertDefined();
        int decimalAmount = 0;

        if (null != value) {

            BigInteger tot = (value.movePointRight(SCALE)).toBigInteger();
            BigInteger sub = value.toBigInteger();
            sub = sub.multiply(new BigInteger(MULTIPLIER));
            BigInteger res = tot.subtract(sub);
            decimalAmount = res.intValue();
        }

        return decimalAmount;
    }

    /**
     * Will throw a <code>NullPointerException</code> if this valuetype is
     * empty.
     *
     * @return A BigDecimal representing the monetary value.
     */
    public BigDecimal getBigDecimalValue() {

        assertDefined();
        return value;
    }

    /**
     * Method toString.
     *
     * @return String
     */
    public String toString() {

        if (null != value) {

            return (value.toString());
        } else {
            return Objects.requireNonNullElse(invalidValue, "");
        }
    }

    /**
     * Method setValue.
     *
     * @param value Object
     */
    public void setValue(Object value) throws ValueTypeException {

        if (value instanceof Percent) {

            setBigDecimal(((Percent) value).value);
        } else {

            try {
                setBigDecimal(((Percent) Objects.requireNonNull(JavaTypeTranslator.convert(Percent.class, value))).getBigDecimalValue());

            } catch (Exception ex) {

                throw new ValueTypeException(ex);
            }
        }
    }

    /**
     * Adds two Percent objects
     *
     * @param Percent the Percent object to be added
     * @return Percent the result
     */
    public Percent add(Percent Percent) {

        assertDefined();

        if (null == value) {

            if (null == Percent.getBigDecimalValue()) {

                return new Percent();
            } else {

                return new Percent(Percent.getBigDecimalValue());
            }
        }

        BigDecimal value = this.value.add(Percent.getBigDecimalValue());
        return new Percent(value);
    }

    /**
     * Subtracts two Percent objects
     *
     * @param Percent the Percent object to be subtracted
     * @return Percent the result
     */
    public Percent subtract(Percent Percent) {

        assertDefined();

        if (null == value) {

            return (Percent);
        }

        BigDecimal value = this.value.subtract(Percent.getBigDecimalValue());
        return new Percent(value);
    }

    /**
     * Compares two Percent objects.
     *
     * @param Percent the Percent object to compare with.
     * @return boolean result.
     */
    public boolean isLessThen(Percent Percent) {
        assertDefined();

        BigDecimal thisValue = notNull(this);
        BigDecimal parameter = notNull(Percent);

        return (thisValue.compareTo(parameter) < 0);
    }

    /**
     * Compares two Percent objects.
     *
     * @param Percent the Percent object to compare with.
     * @return boolean result.
     */
    public boolean isLessThenOrEqualTo(Percent Percent) {

        assertDefined();
        BigDecimal thisValue = notNull(this);
        BigDecimal parameter = notNull(Percent);
        return (thisValue.compareTo(parameter) <= 0);
    }

    /**
     * Compares two Percent objects.
     *
     * @param Percent the Percent object to compare with.
     * @return boolean result.
     */
    public boolean isGreaterThen(Percent Percent) {

        assertDefined();
        BigDecimal thisValue = notNull(this);
        BigDecimal parameter = notNull(Percent);
        return (thisValue.compareTo(parameter) > 0);
    }

    /**
     * Compares two Percent objects.
     *
     * @param Percent the Percent object to compare with.
     * @return boolean result.
     */
    public boolean isGreaterThenOrEqualTo(Percent Percent) {

        assertDefined();
        BigDecimal thisValue = notNull(this);
        BigDecimal parameter = notNull(Percent);
        return (thisValue.compareTo(parameter) >= 0);
    }

    /**
     * Method isValid.
     *
     * @param validator IValidator
     * @param receiver  IExceptionMessageListener
     * @return boolean
     */
    public boolean isValid(IValidator validator, IExceptionMessageListener receiver) {

        return validator.isValid(value, invalidValue, null, receiver);
    }

    /**
     * Method getDefaultValidator.
     *
     * @param messageFactory IMessageFactory
     * @param isMandatory    boolean
     * @return IValidator
     */
    public IValidator getDefaultValidator(IMessageFactory messageFactory, boolean isMandatory) {

        // This allow non-negative 11.2
        return new PercentValidator(messageFactory, false, true, 7, 5, isMandatory);
    }

    /**
     * @return boolean
     * @deprecated Use the new validator method instead.
     */
    public boolean isValid() {

        boolean valid = false;
        String error = getError();

        if (null == error) {

            valid = true;
        }

        return valid;
    }

    /**
     * @return String
     * @deprecated Use the new validator method instead.
     */
    public String getError() {

        String error = null;

        if (!isEmpty()) {

            long nonDecimalLength = Long.toString(getNonDecimalAmount()).length();

            // Note that the decimal length will be 1 for 00-09.
            long decimalLength = Long.toString(getDecimalAmount()).length();

            // Allow only 2 decimal places.
            if (decimalLength > 2) {

                error = "only two decimal places are allowed";
            }

            // Add three to account for the decimal portion and decimal point.
            if ((nonDecimalLength + 3) > getMaxLength()) {

                error = "length of digits and decimal point should not exceed " + getMaxLength();
            }

            // Disallow zero for certain formats
            if (!canBeZero() && (getBigDecimalValue().doubleValue() == 0)) {

                error = "amount cannot be zero";
            }

            // Disallow negative numbers
            if (!canBeNegative() && (getBigDecimalValue().doubleValue() < 0)) {

                error = "amount cannot be negative";
            }
        }

        return error;
    }

    /**
     * Will throw a <code>NullPointerException</code> if this valuetype is
     * empty.
     *
     * @return A double representing the monetary value.
     */

    public double doubleValue() {

        assertDefined();
        return value.doubleValue();
    }

    /**
     * Overrides Cloneable
     *
     * @return Object
     */

    public Object clone() {
        try {

            return super.clone();
        } catch (CloneNotSupportedException e) {
            // will never happen
            return null;
        }
    }

    /**
     * Method compareTo.
     *
     * @param other Percent
     * @return int
     */
    public int compareTo(final Percent other) {

        return CoreUtils.nullSafeComparator(this.getBigDecimalValue(), other.getBigDecimalValue());
    }

    /**
     * Method compare.
     *
     * @param o1 Percent
     * @param o2 Percent
     * @return int
     */
    public int compare(Percent o1, Percent o2) {

        int returnVal = CoreUtils.nullSafeComparator(o1.getBigDecimalValue(), o2.getBigDecimalValue());

        if (ascending.equals(Boolean.FALSE)) {

            returnVal = returnVal * -1;
        }
        return returnVal;
    }

    /**
     * Method equals.
     *
     * @param objectToCompare Object
     * @return boolean
     */
    public boolean equals(Object objectToCompare) {

        if (super.equals(objectToCompare)) {
            return true;
        }

        if (objectToCompare instanceof Percent) {

            return CoreUtils.nullSafeComparator(((Percent) objectToCompare).getBigDecimalValue(),
                    this.getBigDecimalValue()) == 0;
        }
        return false;
    }

    /**
     * Method setBigDecimal.
     *
     * @param value BigDecimal
     */
    private void setBigDecimal(BigDecimal value) {

        if (null == value) {

            this.value = new BigDecimal("0.0");
        } else {

            // value = value;
            this.value = value.setScale(SCALE, RoundingMode.HALF_EVEN);
        }

        // Clear any invalid values
        invalidValue = null;
    }

    /**
     * Method notNull.
     *
     * @param value Percent
     * @return BigDecimal
     */
    private BigDecimal notNull(Percent value) {

        if (null == value) {
            return (new BigDecimal("0.0"));
        } else {
            return (value.getBigDecimalValue());
        }
    }

    private void assertDefined() {

        if (null != invalidValue) {

            throw new NumberFormatException(
                    "Attempting to use a Percent that was not properly initialized.  Invalid value is: "
                            + invalidValue);
        }
    }
}
