package org.trade.core.exception;

import java.io.ByteArrayOutputStream;
import java.io.PrintWriter;
import java.io.Serial;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

/**
 * This class represents an exception capable of containing other exceptions. It
 * also supports adding multiple user-friendly messages. This is useful when you
 * want to display nice messages for the user, but you want the nested
 * (original) exception to be cached so you can log it or view it it you are a
 * developer.
 *
 * <p>
 * Another reason you may want to use this class is to isolate dependencies
 * within your application. For example, if you are executing Java on the client
 * you do not want to expose any exceptions from a third party library. Doing so
 * would force all clients of your API to have the third party library available
 * on their machine, which might not be desirable in a distributed environment.
 *
 * @author Simon Allen
 */
public class NestingException extends Exception {
    /**
     *
     */
    @Serial
    private static final long serialVersionUID = 1692132595046896286L;

    public String exception;
    public String seedClassName;
    private NestingException nestedException;
    private final List<ExceptionMessage> exceptionMessages = new ArrayList<>();
    private List<ExceptionContext> exceptionContexts = new ArrayList<>();
    private final Date timeStamp = new Date();
    private String stackTrace = null;

    /**
     * Default constructor.
     */
    public NestingException() {
        this(getNoExceptionMessage());
    }

    /**
     * Constructor that takes an error message.
     *
     * @param message A user-friendly description of the exception.
     */
    public NestingException(String message) {

        super(message);

        // init member variables
        stackTrace = captureStackTrace();
    }

    /**
     * Constructor that allows nesting of another <code>Throwable</code>.
     *
     * @param throwable The <code>Throwable</code> to be nested.
     */
    public NestingException(Throwable throwable) {
        this(throwable, getNoExceptionMessage());
    }

    /**
     * Constructor allows the developer to set the exception message
     *
     * @param throwable java.lang.Throwable
     * @param message   the exception message
     */
    public NestingException(Throwable throwable, String message) {

        this(message);

        // Initialize member variables.
        this.exception = throwable.toString();
        this.stackTrace = ExceptionUtil.nestStackTrace(this.stackTrace, throwable);

        // Store the exception itself if it is a NestingException
        if (throwable instanceof NestingException) {

            this.nestedException = (NestingException) throwable;
            assimilateContext(this.nestedException);
        } else {
            this.seedClassName = throwable.getClass().toString();
        }
    }

    /**
     * Constructor for NestingException.
     *
     * @param exceptionMessage ExceptionMessage
     */
    public NestingException(ExceptionMessage exceptionMessage) {

        this();
        addExceptionMessage(exceptionMessage);
    }

    /**
     * Constructor for NestingException.
     *
     * @param exceptionMessage ExceptionMessage
     * @param exceptionContext ExceptionContext
     */
    public NestingException(ExceptionMessage exceptionMessage, ExceptionContext exceptionContext) {

        this();
        addExceptionMessage(exceptionMessage);
        addExceptionContext(exceptionContext);
    }

    /**
     * Constructor.
     *
     * @param exceptionMessage The user friendly exception message.
     * @param message          The standard message printed in stack traces.
     */
    public NestingException(ExceptionMessage exceptionMessage, String message) {

        this(message);
        addExceptionMessage(exceptionMessage);
    }

    /**
     * Constructor for NestingException.
     *
     * @param exceptionMessage ExceptionMessage
     * @param exceptionContext ExceptionContext
     * @param message          String
     */
    public NestingException(ExceptionMessage exceptionMessage, ExceptionContext exceptionContext, String message) {

        this(message);
        addExceptionMessage(exceptionMessage);
        addExceptionContext(exceptionContext);
    }

    /**
     * Constructor for NestingException.
     *
     * @param exceptionMessage ExceptionMessage
     * @param throwable        Throwable
     */
    public NestingException(ExceptionMessage exceptionMessage, Throwable throwable) {

        this(throwable);
        addExceptionMessage(exceptionMessage);
    }

    /**
     * Constructor for NestingException.
     *
     * @param exceptionMessage ExceptionMessage
     * @param gruesomeDetails  String
     * @param throwable        Throwable
     */
    public NestingException(ExceptionMessage exceptionMessage, String gruesomeDetails, Throwable throwable) {
        this(throwable, gruesomeDetails);
        addExceptionMessage(exceptionMessage);
    }

    /**
     * Any outstanding context from the exception provided will be added to this
     * exception. The context in the exception provided will be cleared.
     *
     * @param nestingException NestingException
     */
    private void assimilateContext(NestingException nestingException) {

        // Get the contexts to be added if any.
        ListIterator<ExceptionContext> enumExeptions = nestingException.getExceptionContexts();

        if (enumExeptions.hasNext()) {

            // Add each of the contexts to this
            while (enumExeptions.hasNext()) {
                addExceptionContext(enumExeptions.next());
            }

            // Clear the context of the nesting exception.
            nestingException.clearContexts();
        }
    }

    /**
     * Method getExceptionContexts.
     *
     * @return ListIterator<ExceptionContext>
     */
    private ListIterator<ExceptionContext> getExceptionContexts() {

        return exceptionContexts.listIterator();
    }

    private void clearContexts() {

        exceptionContexts = new ArrayList<>(1);
    }

    /**
     * Adds a message to this exception. Multiple messages can be added.
     *
     * @param exceptionMessage The user-friendly exception message.
     * @see ExceptionMessage
     */
    public void addExceptionMessage(ExceptionMessage exceptionMessage) {

        this.exceptionMessages.add(exceptionMessage);

        // Here we loop through any outstanding contexts and apply them.
        ListIterator<ExceptionContext> iteration = this.exceptionContexts.listIterator();

        while (iteration.hasNext()) {
            addExceptionContext(iteration.next());
        }
    }

    /**
     * Method addExceptionMessage.
     *
     * @param exceptionMessage ExceptionMessage
     * @param exceptionContext ExceptionContext
     */
    public void addExceptionMessage(ExceptionMessage exceptionMessage, ExceptionContext exceptionContext) {

        addExceptionMessage(exceptionMessage);
        addExceptionContext(exceptionContext);
    }

    /**
     * Adds an iteration of <code>ExceptionMessages</code> to this exception.
     *
     * @param messages The iteration of <code>ExceptionMessage</code> objects.
     */
    public void addExceptionMessages(ListIterator<?> messages) {

        while (messages.hasNext()) {

            ExceptionMessage m = (ExceptionMessage) messages.next();
            exceptionMessages.add(m);
        }
    }

    /**
     * Add context to exception messages within this exception. The context is
     * effectively a named parameter used within the messages.
     * <p>
     * The rules:
     * <p>
     * 1. If this exception contains messages the context will be applied only
     * to the most recent message. 2. If this exception contains no messages the
     * context will be applied to the next exception message that is added if
     * one is ever added. 3. Regardless of the outcome of (1) and (2) above, the
     * context will be applied to ALL messages contained within any child
     * exceptions (exceptions nested within this exception).
     *
     * @param exceptionContext ExceptionContext
     */
    public void addExceptionContext(ExceptionContext exceptionContext) {

        ExceptionMessage mostRecent = this.exceptionMessages.getLast();

        if (null != mostRecent) {

            // There is at least one exception message, so we add the context
            // to the most recent message.
            mostRecent.addExceptionContext(exceptionContext);
        } else {

            // There are no exception messages so we hold onto the context
            // in case a message is added later.
            this.exceptionContexts.add(exceptionContext);
        }

        // Regardless of whether we cached the context we will apply the
        // context recursively to any nested exceptions we find.
        if (null != this.nestedException) {

            Iterator<?> iteration = this.nestedException.getAllExceptionMessages();

            ExceptionMessage exceptionMessage;

            while (iteration.hasNext()) {

                exceptionMessage = (ExceptionMessage) iteration.next();
                exceptionMessage.addExceptionContext(exceptionContext);
            }
        }
    }

    /**
     * Adds a string message to this exception. Multiple messages can be
     * contained in the exception.
     *
     * @param message The user-friendly exception message.
     * @param code    ExceptionCode
     */
    public void addExceptionMessage(ExceptionCode code, String message) {

        addExceptionMessage(new ExceptionMessage(code, message));
    }

    /**
     * Adds a string message to this exception. Multiple messages can be
     * contained in the exception.
     *
     * @param message          The user-friendly exception message.
     * @param code             ExceptionCode
     * @param nestingException NestingException
     */
    public void addExceptionMessage(ExceptionCode code, String message, NestingException nestingException) {

        this.exceptionMessages.add(new ExceptionMessage(code, message));
        assimilateContext(nestingException);
    }

    /**
     * Method addExceptionMessage.
     *
     * @param userFriendlyMessage ExceptionMessage
     * @param nestingException    NestingException
     */
    public void addExceptionMessage(ExceptionMessage userFriendlyMessage, NestingException nestingException) {

        this.exceptionMessages.add(userFriendlyMessage);
        assimilateContext(nestingException);
    }

    /**
     * Remove the specified exception message from the exception.
     *
     * @param message ExceptionMessage
     */
    public void removeExceptionMessage(ExceptionMessage message) {

        this.exceptionMessages.remove(message);
    }

    /**
     * Remove any exception messages that match the code passed.
     *
     * @param code ExceptionCode
     */
    public void removeExceptionMessages(ExceptionCode code) {

        List<ExceptionMessage> remove = new ArrayList<>();
        int i;
        int nbrMessages = this.exceptionMessages.size();

        for (i = 0; i < nbrMessages; i++) {

            ExceptionMessage msg = this.exceptionMessages.get(i);

            if (msg.getExceptionCode().equals(code)) {

                remove.add(msg);
            }
        }

        int removeSize = remove.size();

        for (i = 0; i < removeSize; i++) {

            removeExceptionMessage(remove.get(i));
        }
    }

    /**
     * @return true if the object has user friendly error messages.
     */
    public boolean hasExceptionMessages() {

        if (nestedException != null) {

            return !this.exceptionMessages.isEmpty() || this.nestedException.hasExceptionMessages();
        } else {

            return !this.exceptionMessages.isEmpty();
        }
    }

    /**
     * Obtain an <code>iteration</code> of user-friendly messages for this
     * exception. This does not includes messages from nested exceptions.
     *
     * @return <code>ListIterator</code> of <code>ExceptionMessage</code> objects
     * containing user-friendly text.
     */
    protected ListIterator<ExceptionMessage> getExceptionMessages() {

        return this.exceptionMessages.listIterator();
    }

    /**
     * Obtain an <code>iteration</code> of messages for this exception. This
     * includes messages from nested exceptions if any exist.
     *
     * @return <code>iteration</code> of <code>ExceptionMessage</code> objects
     * containing exception message text. * @see
     * #getAllUserFriendlyMessages()
     */
    public Iterator<?> getAllExceptionMessages() {


        if (nestedException != null) {

            Enumerator enumMsg = (Enumerator) this.nestedException.getAllExceptionMessages();
            enumMsg.prependEnumeration(this.exceptionMessages.listIterator());
            return enumMsg;
        } else {

            return new Enumerator(this.exceptionMessages.listIterator());
        }
    }

    /**
     * Returns the time stamp of when the exception occurred
     *
     * @return time stamp
     */
    public Date getTimeStamp() {
        return this.timeStamp;
    }

    /**
     * Prints the stack trace for this exception to the console.
     */
    public void printStackTrace() {

        this.stackTrace = ExceptionUtil.fillInExceptionMessage(this, this.stackTrace, getMessage());
        System.out.print(this.stackTrace);
    }

    /**
     * Prints the stack trace for this exception into a specified
     * <code>PrintWriter</code>.
     *
     * @param writer The <code>PrintWriter</code> to use.
     */
    public void printStackTrace(PrintWriter writer) {

        this.stackTrace = ExceptionUtil.fillInExceptionMessage(this, this.stackTrace, getMessage());
        writer.print(this.stackTrace);
    }

    /**
     * Method getNoExceptionMessage.
     *
     * @return String
     */
    static String getNoExceptionMessage() {

        return ("SEE NESTED EXCEPTION MESSAGE BELOW");
    }

    /**
     * This just places the stack trace in a string.
     *
     * @return String
     */
    private String captureStackTrace() {

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        PrintWriter writer = new PrintWriter(out);
        super.printStackTrace(writer);
        writer.flush();
        return out.toString();
    }
}
