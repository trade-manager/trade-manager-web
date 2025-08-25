package org.trade.core.exception;

import java.io.Serial;

/**
 *
 */
public class ModelException extends NestingException {
    /**
     *
     */
    @Serial
    private static final long serialVersionUID = -4931142657824969686L;

    private Integer id = 0;
    private Integer code = 0;

    public ModelException() {
        super();
    }

    /**
     * Constructor allowing a reference to another exception to be embedded.
     *
     * @param throwable The <code>Throwable</code> to be nested.
     */
    public ModelException(Throwable throwable) {

        super(throwable, throwable.getMessage());
    }

    /**
     * Constructor that allows the user to set the exception message.
     *
     * @param message The desired message text.
     * @param id      Integer
     * @param code    Integer
     */
    public ModelException(Integer id, Integer code, String message) {

        super(message);
        this.id = id;
        this.code = code;
    }

    /**
     * Constructor that allows the user to set the exception message.
     *
     * @param message The desired message text.
     */
    public ModelException(String message) {
        super(message);
    }

    /**
     * Constructor for ModelException.
     *
     * @param exceptionMessage ExceptionMessage
     * @param gruesomeDetails  String
     */
    public ModelException(ExceptionMessage exceptionMessage, String gruesomeDetails) {

        super(exceptionMessage, gruesomeDetails);
    }

    /**
     * Constructor for ModelException.
     *
     * @param exceptionMessage ExceptionMessage
     * @param gruesomeDetails  String
     * @param throwable        Throwable
     */
    public ModelException(ExceptionMessage exceptionMessage, String gruesomeDetails, Throwable throwable) {
        super(exceptionMessage, gruesomeDetails, throwable);
    }

    /**
     * Constructor for ModelException.
     *
     * @param exceptionMessage ExceptionMessage
     */
    public ModelException(ExceptionMessage exceptionMessage) {
        super(exceptionMessage);
    }

    /**
     * Constructor for ModelException.
     *
     * @param exceptionMessage ExceptionMessage
     * @param throwable        Throwable
     */
    public ModelException(ExceptionMessage exceptionMessage, Throwable throwable) {
        super(exceptionMessage, throwable);
    }

    /**
     * Method getErrorCode.
     *
     * @return Integer
     */
    public Integer getErrorCode() {
        return this.code;
    }

    /**
     * Method getErrorId.
     *
     * @return Integer
     */
    public Integer getErrorId() {
        return this.id;
    }
}
