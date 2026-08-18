package org.trade.core.valuetype;

import org.trade.core.conversion.JavaTypeTranslator;
import org.trade.core.message.IMessageFactory;
import org.trade.core.util.CoreUtils;
import org.trade.core.validator.DecimalValidator;
import org.trade.core.validator.IExceptionMessageListener;
import org.trade.core.validator.IValidator;

import java.io.Serial;
import java.math.BigInteger;
import java.util.Comparator;
import java.util.Objects;

/**
 * @author Simon Allen
 * @version $Revision: 1.0 $
 */
public class Quantity extends ValueType implements Comparator<Quantity>, Comparable<Quantity> {
    /**
     *
     */
    @Serial
    private static final long serialVersionUID = 4937298768811778585L;

    public static final String QUANTITY_POSITIVE_7_0 = "#(,)###(,)###";
    public static final String QUANTITY_NONNEGATIVE_8_0 = "##(,)###(,)###";
    public static final String QUANTITY_POSITIVE_10_0 = "#(,)###(,)###(,)###";
    public static final String QUANTITY_NONNEGATIVE_11_0 = "##(,)###(,)###(,)###";
    public static final Quantity ZERO = new Quantity(0);
    protected static Boolean ascending = true;

    static {
        // Register the appropriate converters
        JavaTypeTranslator.registerDynamicTypeConverter(new ObjectToMoney());
        JavaTypeTranslator.registerDynamicTypeConverter(new MoneyToObject());
    }

    private Integer value = null;
    private String format = QUANTITY_NONNEGATIVE_11_0;
    private String invalidValue = null; // This will be null if there were

    /**
     * Default Constructor. Create an object and initialize it to empty.
     */
    public Quantity() {
    }

    /**
     * Default Constructor. Create an object and initialize it to empty.
     *
     * @param quantityString String
     */
    public Quantity(String quantityString) {

        if ((null != quantityString) && (!quantityString.isEmpty())) {

            // This is necessary because Java will parse strings with multiple
            // dashes
            if (quantityString.indexOf("-") != quantityString.lastIndexOf("-")) {

                invalidValue = quantityString;
            } else {

                try {

                    setInteger(Integer.valueOf(quantityString));
                } catch (NumberFormatException e) {
                    invalidValue = quantityString;
                }
            }
        }
    }

    /**
     * Constructor for Quantity.
     *
     * @param d int
     */
    public Quantity(int d) {

        setInteger(d);
    }

    /**
     * Constructor for Quantity.
     *
     * @param quantity Quantity
     */
    public Quantity(Quantity quantity) {

        value = quantity.value;
        format = quantity.format;
        invalidValue = quantity.invalidValue;
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
            case QUANTITY_NONNEGATIVE_8_0 -> 11;
            case QUANTITY_POSITIVE_10_0 -> 13;
            case QUANTITY_POSITIVE_7_0 -> 10;
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

        if (getFormat().equals(QUANTITY_POSITIVE_7_0)) {

            zero = false;
        } else if (getFormat().equals(QUANTITY_POSITIVE_10_0)) {

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
        return value.compareTo(0) < 0;
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
     * See description of superclass method. Overrode functionality to return
     * the BigDecimal this object is using intrnally.
     *
     * @return Object
     */
    public Object getSQLObject() {

        return (getIntegerValue());
    }

    /**
     * Will throw a <code>NullPointerException</code> if this valuetype is
     * empty.
     *
     * @return A BigDecimal representing the monetary value.
     */
    public Integer getIntegerValue() {

        assertDefined();
        return value;
    }

    /**
     * Method getBigIntegerValue.
     *
     * @return BigInteger
     */
    public BigInteger getBigIntegerValue() {

        assertDefined();

        if (null == value) {

            return null;
        }
        return new BigInteger(value.toString());
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

        if (value instanceof Quantity) {

            setInteger(((Quantity) value).value);
        } else {

            try {

                setInteger(((Quantity) Objects.requireNonNull(JavaTypeTranslator.convert(Quantity.class, value))).getIntegerValue());
            } catch (Exception ex) {

                throw new ValueTypeException(ex);
            }
        }
    }

    /**
     * Adds two Money objects
     *
     * @param quantity Quantity
     * @return Money the result
     */
    public Quantity add(Quantity quantity) {

        assertDefined();

        if (null == value) {

            if (null == quantity.getIntegerValue()) {

                return new Quantity();
            } else {

                return new Quantity(quantity.getIntegerValue());
            }
        }

        Integer value = this.value + quantity.getIntegerValue();
        return new Quantity(value);
    }

    /**
     * Subtracts two Money objects
     *
     * @param quantity Quantity
     * @return Money the result
     */
    public Quantity subtract(Quantity quantity) {

        assertDefined();

        if (null == value) {

            return (quantity);
        }
        Integer value = this.value - quantity.getIntegerValue();
        return new Quantity(value);
    }

    /**
     * Compares two Money objects.
     *
     * @param quantity Quantity
     * @return boolean result.
     */
    public boolean isLessThen(Quantity quantity) {

        assertDefined();
        Integer thisValue = notNull(this);
        Integer parameter = notNull(quantity);
        return (thisValue.compareTo(parameter) < 0);
    }

    /**
     * Compares two Money objects.
     *
     * @param quantity Quantity
     * @return boolean result.
     */
    public boolean isLessThenOrEqualTo(Quantity quantity) {

        assertDefined();
        Integer thisValue = notNull(this);
        Integer parameter = notNull(quantity);
        return (thisValue.compareTo(parameter) <= 0);
    }

    /**
     * Compares two Money objects.
     *
     * @param quantity Quantity
     * @return boolean result.
     */
    public boolean isGreaterThen(Quantity quantity) {

        assertDefined();
        Integer thisValue = notNull(this);
        Integer parameter = notNull(quantity);
        return (thisValue.compareTo(parameter) > 0);
    }

    /**
     * Compares two Money objects.
     *
     * @param quantity Quantity
     * @return boolean result.
     */
    public boolean isGreaterThenOrEqualTo(Quantity quantity) {

        assertDefined();
        Integer thisValue = notNull(this);
        Integer parameter = notNull(quantity);
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
        return new DecimalValidator(messageFactory, false, true, 11, 2, isMandatory);
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
     * @param other Quantity
     * @return int
     */
    public int compareTo(final Quantity other) {

        return CoreUtils.nullSafeComparator(this.getBigIntegerValue(), other.getBigIntegerValue());
    }

    /**
     * Method compare.
     *
     * @param o1 Quantity
     * @param o2 Quantity
     * @return int
     */
    public int compare(Quantity o1, Quantity o2) {

        int returnVal = CoreUtils.nullSafeComparator(o1.getBigIntegerValue(), o2.getBigIntegerValue());

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

        if (objectToCompare instanceof Quantity) {

            return CoreUtils.nullSafeComparator(((Quantity) objectToCompare).getBigIntegerValue(),
                    this.getBigIntegerValue()) == 0;
        }
        return false;
    }

    /**
     * Method setInteger.
     *
     * @param value Integer
     */
    private void setInteger(Integer value) {

        this.value = value;
        // Clear any invalid values
        invalidValue = null;
    }

    /**
     * Method notNull.
     *
     * @param value Quantity
     * @return Integer
     */
    private Integer notNull(Quantity value) {

        if (null == value) {

            return (0);
        } else {

            return (value.getIntegerValue());
        }
    }

    private void assertDefined() {

        if (null != invalidValue) {

            throw new NumberFormatException(
                    "Attempting to use a Quantity that was not properly initialized.  Invalid value is: "
                            + invalidValue);
        }
    }
}
