package org.trade.core.exception;

import java.io.Serial;
import java.util.StringTokenizer;

/**
 * @author Simon Allen
 * @version $Revision: 1.0 $
 */
public class ExceptionMessage implements java.io.Serializable {
    /**
     *
     */
    @Serial
    private static final long serialVersionUID = -1095613543601005491L;

    private final static String charsStartString = "contains invalid characters: [";
    private final static String charsEndString = "] -- check api specification";
    private final static String lengthStartString = "length is not to exceed ";
    private final ExceptionCode code;
    private String message;

    /**
     * Copy constructor.
     *
     * @param other ExceptionMessage
     */
    public ExceptionMessage(ExceptionMessage other) {

        this.code = other.code;
        this.message = other.message;
    }

    /**
     * Constructor allowing the exception code and message to be provided.
     *
     * @param code    the code assigned to the new exception message
     * @param message the message (may contain context markup in the form of
     *                #parameters#
     */
    public ExceptionMessage(ExceptionCode code, String message) {

        this.code = code;
        this.message = message;
    }

    /**
     * Constructor that copies an existing exception message but assigns the new
     * message the provided exception code.
     *
     * @param code    the code assigned to the new exception message
     * @param message the existing exception message from which to copy any existing
     *                message content/context
     */
    public ExceptionMessage(ExceptionCode code, ExceptionMessage message) {

        this.code = code;
        this.message = message.message;
    }

    /**
     * @return The Exception Code associated with the message
     */
    public ExceptionCode getExceptionCode() {
        return this.code;
    }

    /**
     * The exception context represents a named parameter within this message.
     * <p>
     * Rules:
     * <p>
     * 1. If two contexts are added with the same parameter name, the first
     * context has precedence.<br>
     *
     * @param exceptionContext ExceptionContext
     */
    public void addExceptionContext(ExceptionContext exceptionContext) {

        if (null != message) {

            StringTokenizer tokenizer = new StringTokenizer(message, "#");
            StringBuilder buf = new StringBuilder();

            for (int i = 0; tokenizer.hasMoreTokens(); i++) {

                String token = tokenizer.nextToken();

                // This is not a parameter.
                if ((i % 2) == 0) {

                    // Append the token as is, because the token is part of the message.
                    buf.append(token);
                } else {
                    // We have a parameter.
                    // Validate the parameter name.
                    if (exceptionContext.getParameterName().equals(token)) {

                        buf.append(exceptionContext.getValue());
                    } else {

                        // We don't want to lose the unused parameter
                        buf.append('#');
                        buf.append(token);
                        buf.append('#');
                    }
                }
            }

            message = buf.toString();
        }
    }

    /**
     * @return The message that describes the exception.
     */
    public String getMessage() {

        StringBuilder buf = new StringBuilder();

        // If there are any remaining parameters not substituted we will
        // remove them. Note we do not change the real message, so parameters
        // could still be added in the future.
        if (null != message) {

            StringTokenizer tokenizer = new StringTokenizer(message, "#");

            for (int i = 0; tokenizer.hasMoreTokens(); i++) {

                String token = tokenizer.nextToken();
// This is not a parameter.
                if ((i % 2) == 0) {

                    // Append the token as is, because the token is part of the
                    // message.
                    buf.append(token);
                }
            }
        }

        return buf.toString();
    }

    /**
     * this method allows the exception message translator to look up parameters
     * in an exception message without using reflection or having this class
     * implement dictionary
     * <p>
     * this currently only implements message - same as getMessage() description
     * - everything in the message after the first colon
     *
     * @param lookup the string to use for the look-up
     * @return Object the result of the look-up, or null if nothing is found
     * (most likely a string)
     */
    // TODO: get rid of this
    public Object get(String lookup) {

        if ("description".equals(lookup)) {
            return getDescription();
        }

        if ("message".equals(lookup)) {
            return getMessage();
        }

        if ("max_length".equals(lookup)) {
            return getInvalidLength();
        }

        if ("invalid_chars".equals(lookup)) {
            return getInvalidChars();
        }

        if ("empty".equals(lookup)) {
            return getEmpty();
        }

        return null;
    }

    // TODO: get rid of this

    /**
     * Method getEmpty.
     *
     * @return String
     */
    private String getEmpty() {

        int emptyPos = getMessage().indexOf("Empty");

        if (-1 != emptyPos) {

            return "empty";
        }

        // the internal
        int manditoryPos = getMessage().indexOf("Mandatory");

        // response uses
        // a capitol M
        if (-1 != manditoryPos) {

            return "empty";
        }
        return null;
    }

    // TODO: get rid of this

    /**
     * Method getInvalidChars.
     *
     * @return String
     */
    private String getInvalidChars() {

        int startLength = getMessage().indexOf(charsStartString);

        if (startLength < 0) {

            return null; // not found
        }
        startLength = startLength + charsStartString.length();
        int endLength = getMessage().indexOf(charsEndString);

        if (endLength < 0) {

            return null; // not found
        }

        if (startLength > endLength) {

            return null;
        }

        return getMessage().substring(startLength, endLength);
    }

    // TODO: get rid of this

    /**
     * Method getInvalidLength.
     *
     * @return String
     */
    private String getInvalidLength() {

        int startLength = getMessage().indexOf(lengthStartString);

        if (-1 == startLength) {

            return null; // not found
        }
        startLength = startLength + lengthStartString.length();
        return getMessage().substring(startLength);
    }

    // TODO: get rid of this

    /**
     * Method getDescription.
     *
     * @return String
     */
    private String getDescription() {

        // the description is everything in the message after the first colon
        int firstColon = getMessage().indexOf(":");
        return getMessage().substring(firstColon + 1).trim();
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

        if (!(objectToCompare instanceof ExceptionMessage otherExceptionMessage)) {
            return false;
        }

        boolean equal = false;
        boolean codeMatches;
        boolean messageMatches;

        if (null == this.code) {

            codeMatches = (null == otherExceptionMessage.code);
        } else {
            codeMatches = (this.code.equals(otherExceptionMessage.code));
        }

        if (null == this.message) {
            messageMatches = (null == otherExceptionMessage.message);
        } else {
            messageMatches = (this.message.equals(otherExceptionMessage.message));
        }

        if (codeMatches && messageMatches) {
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
        hash = hash * 31 + (message == null ? 0 : message.hashCode());
        return hash;
    }

    /**
     * Method toString.
     *
     * @return String
     */
    public String toString() {
        return "code: [" + this.code + "] message: [" + this.message + "]";
    }
}
