package org.trade.core.exception;

import java.io.Serial;

/**
 * ExceptionCode is used as the key for retrieving an exception message.
 * <p>
 * WARNING: Do not add setters to this class because it is IMMUTABLE.
 * doing so will break code (e.g. ExceptionMessage which depend upon this
 * class not changing).
 *
 * <p>
 * Objects of this type are immutable (cannot be altered).
 *
 * @author Simon Allen
 */
public class ExceptionCode implements java.io.Serializable {

    /**
     *
     */
    @Serial
    private static final long serialVersionUID = 1429333155399564179L;

    private static final String FIELD_SEQUENCE_SEPARATOR = "_";
    private final String code;
    private String fieldRef = null;

    /**
     * Constructor for ExceptionCode.
     *
     * @param code String
     */
    public ExceptionCode(String code) {
        this.code = code;
    }

    /**
     * Constructor for ExceptionCode.
     *
     * @param code     String
     * @param fieldRef String
     */
    public ExceptionCode(String code, String fieldRef) {

        this.code = code;
        this.fieldRef = fieldRef;
    }

    /**
     * This can be used to generate a new ExceptionCode object where the field
     * reference has the specified sequence number appended to it. It may be
     * used when repeating groups of data are being validated.
     *
     * @param sequence int
     * @return ExceptionCode
     */
    public ExceptionCode createSequencedCode(int sequence) {

        if (null == fieldRef) {

            // Okay because this class is immutable.
            return this;
        } else {

            return new ExceptionCode(code, fieldRef + FIELD_SEQUENCE_SEPARATOR + sequence);
        }
    }

    /**
     * Method getCode.
     *
     * @return String
     */
    public String getCode() {
        return this.code;
    }

    /**
     * Method getFieldReference.
     *
     * @return String
     */
    public String getFieldReference() {
        return this.fieldRef;
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

        if (!(objectToCompare instanceof ExceptionCode otherExceptionCode)) {
            return false;
        }

        boolean equal = false;
        boolean codeMatches;
        boolean fieldMatches;

        if (null == this.code) {

            codeMatches = (null == otherExceptionCode.code);
        } else {
            codeMatches = (code.equals(otherExceptionCode.code));
        }

        if (null == this.fieldRef) {
            fieldMatches = (null == otherExceptionCode.fieldRef);
        } else {
            fieldMatches = (fieldRef.equals(otherExceptionCode.fieldRef));
        }

        if (codeMatches && fieldMatches) {
            equal = true;
        }

        return equal;
    }

    /**
     * Method hashCode.
     *
     * @return int
     */
    public int hashCode() {

        int hash = 1;
        hash = hash * 31 + code.hashCode();
        hash = hash * 31 + (fieldRef == null ? 0 : fieldRef.hashCode());
        return hash;
    }

    /**
     * Method toString.
     *
     * @return String
     */
    public String toString() {
        return this.code;
    }
}
