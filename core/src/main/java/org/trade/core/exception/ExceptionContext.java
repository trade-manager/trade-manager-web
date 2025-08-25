package org.trade.core.exception;

import java.io.Serial;

/**
 * Describes the context in which an exception occurred. Contains the
 * information for constructing the message about a specific exception.
 *
 * @author Simon Allen
 */
public class ExceptionContext implements java.io.Serializable {
    /**
     *
     */
    @Serial
    private static final long serialVersionUID = 4837522639316151345L;

    private final static String NULL_VALUE = "";
    private final String parameterName;
    private String value;

    /**
     * Constructor.
     *
     * @param parameterName String
     * @param value         Object
     */
    public ExceptionContext(String parameterName, Object value) {

        this.parameterName = parameterName;
        setValue(value);
    }

    /**
     * Copy constructor.
     *
     * @param other ExceptionContext
     */
    public ExceptionContext(ExceptionContext other) {

        this.parameterName = other.parameterName;
        value = other.value;
    }

    /**
     * Copy constructor.
     *
     * @param other the object to be copied
     * @param value the new value for this context (overrides the value of the
     *              object being copied
     */
    public ExceptionContext(ExceptionContext other, Object value) {

        this.parameterName = other.parameterName;
        setValue(value);
    }

    /**
     * Represents the name used within exception messages to refer to this
     * context.
     *
     * @return String
     */
    public String getParameterName() {
        return this.parameterName;
    }

    /**
     * Represents the context of the exception. This value should be directly
     * substituted into a named parameter in an exception message.
     *
     * @return String
     */
    public String getValue() {
        return this.value;
    }

    /**
     * Method setValue.
     *
     * @param value Object
     */
    private void setValue(Object value) {

        if (null == value) {
            value = NULL_VALUE;
        }

        this.value = value.toString();
    }
}
