
package org.trade.core.exception;

import java.io.PrintWriter;
import java.io.Serial;
import java.io.Serializable;
import java.io.StringWriter;
import java.util.Arrays;
import java.util.Iterator;

/**
 * This class is used to hold generic routines for manipulating exceptions.
 *
 * @author Simon Allen
 */
public class ExceptionUtil implements Serializable {
    /**
     *
     */
    @Serial
    private static final long serialVersionUID = 1278830639842508059L;

    ExceptionUtil() {
        super();
    }

    /**
     * Method captureStackTrace.
     *
     * @param throwable Throwable
     * @return String
     */
    public static String captureStackTrace(Throwable throwable) {

        if (throwable == null) {

            return null;
        } else {

            StringWriter sw = new StringWriter();
            PrintWriter pw = new PrintWriter(sw);
            // Prints the stack trace to the PrintWriter
            throwable.printStackTrace(pw);
            // Retrieves the content as a String
            return sw.toString();
        }
    }

    /**
     * Method nestStackTrace.
     *
     * @param stackTrace String
     * @param throwable  Throwable
     * @return String
     */
    public static String nestStackTrace(String stackTrace, Throwable throwable) {


        if (throwable instanceof NestingException ex) {

            return stackTrace + Arrays.toString(ex.getStackTrace());
        } else {

            return stackTrace + captureStackTrace(throwable);
        }
    }

    /**
     * Method fillInExceptionMessage.
     *
     * @param nestingException NestingException
     * @param stackTrace       String
     * @param errorMsg         String
     * @return String
     */
    public static String fillInExceptionMessage(NestingException nestingException, String stackTrace,
                                                String errorMsg) {
        if (stackTrace != null) {

            int index = stackTrace.indexOf(':');
            String s1 = stackTrace.substring(0, index + 1);
            index = stackTrace.indexOf("\tat");
            String s2 = "";

            if (index >= 0) {

                s2 = stackTrace.substring(index);
            }

            // Construct the first line of the stack trace.
            StringBuilder buf = new StringBuilder();
            buf.append(s1);
            buf.append(' ');
            buf.append(errorMsg);
            buf.append('\n');

            // Construct the lines in the stack trace that display the
            // user-friendly messages.
            Iterator<?> iteration = nestingException.getAllExceptionMessages();

            while (iteration.hasNext()) {

                ExceptionMessage exceptionMessage;
                exceptionMessage = (ExceptionMessage) iteration.next();

                buf.append('\t');
                buf.append(exceptionMessage.getExceptionCode());
                buf.append(": ");
                buf.append(exceptionMessage.getMessage());
                buf.append('\n');
            }

            // Construct all the "at ..." lines.
            buf.append(s2);
            stackTrace = buf.toString();
        } else {
            stackTrace = "";
        }

        return stackTrace;
    }
}
