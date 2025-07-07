/* ===========================================================
 * TradeManager : An application to trade strategies for the Java(tm) platform
 * ===========================================================
 *
 * (C) Copyright 2011-2011, by Simon Allen and Contributors.
 *
 * Project Info:  org.trade
 *
 * This library is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation; either version 2.1 of the License, or
 * (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY
 * or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public
 * License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301,
 * USA.
 *
 * [Java is a trademark or registered trademark of Oracle, Inc.
 * in the United States and other countries.]
 *
 * (C) Copyright 2011-2011, by Simon Allen and Contributors.
 *
 * Original Author:  Simon Allen;
 * Contributor(s):   -;
 *
 * Changes
 * -------
 *
 */
package org.trade.core.message;

import org.trade.core.exception.ExceptionCode;
import org.trade.core.exception.ExceptionContext;
import org.trade.core.exception.ExceptionMessage;
import org.trade.core.properties.ConfigProperties;
import org.trade.core.properties.PropertyNotFoundException;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.MessageFormat;
import java.util.Dictionary;
import java.util.Hashtable;
import java.util.MissingResourceException;
import java.util.Objects;
import java.util.PropertyResourceBundle;
import java.util.StringTokenizer;
import java.util.Vector;

/**
 * This class represnets the data and methods necessary to take exception codes
 * and parameters, and generate the proper exception message based on
 * information contained in a file.
 *
 * <p>
 * This is the file format:
 *
 * <p>
 * MY_KEY=Invalid name format: the characters[#bad_chars#] are not allowed <br>
 * MY_KEY_FIELD_REFERENCE=customer_demographic_name <br>
 * MY_KEY_CODE=NAME0802
 *
 * @author Simon Allen
 */
public class MessageTranslator {

    public static final String NAME_OF_MESSAGE_FILE_IN_PROPERTIES = "ERROR_MESSAGE_FILE_NAME";
    public static final String CODE_SUFFIX = "_CODE";
    public static final String CONTEXT_SUFFIX = "";

    public final static String PARAMETER_SUFFIX = "_PARAMETER_NAME";
    public final static String FIELD_REFERENCE_SUFFIX = "_FIELD_REFERENCE";

    private static final Hashtable<String, MessageFormat> messageFormats = new Hashtable<>();
    private static final Hashtable<String, String[]> indexesTable = new Hashtable<>();
    private static final Hashtable<String, String> fieldReferences = new Hashtable<>();
    private static final Hashtable<String, String> codes = new Hashtable<>();

    // Constants that map to the keys in the property file
    private PropertyResourceBundle props = null;
    private static final MessageTranslator theConfig = new MessageTranslator();

    // _________________ these methods are taken from the ConfigProperties class
    // ________

    /**
     * Returns a string for a key.
     *
     * @param key String
     * @return String
     */
    public static String getPropAsString(String key) throws IOException {

        String strRet;
        strRet = theConfig._getProperty(key);
        return strRet;
    }

    // read configuration properties

    /**
     * Method _getProperty.
     *
     * @param key String
     * @return String
     */
    private String _getProperty(String key) throws IOException {

        try (InputStream in = new BufferedInputStream(getClass().getResourceAsStream(getPropertyFileName()))) {

            if (null == props) {

                props = new PropertyResourceBundle(in);
            }

            return props.getString(key);
        } catch (MissingResourceException ex) {

            throw new PropertyNotFoundException("The property \"" + key + "\" was not found in the property file \""
                    + getPropertyFileName() + "\".  Check the file msg: " + ex.getMessage());
        }
    }

    /**
     * this replaces the same method in the ConfigProperties class.
     *
     * @return String
     */

    public static String getPropertyFileName() {

        try {

            return ConfigProperties.getPropAsString(NAME_OF_MESSAGE_FILE_IN_PROPERTIES);
        } catch (Exception ex) {

            // default value
            return "messages.properties";
        }
    }

// ------------------ these methods are not in the ConfigProperties class

    /**
     * This method takes an exception code and a dictionary, and it uses the
     * code to get the message, (actual) code and field reference, if it is an
     * editcheeck
     *
     * @param code   the Exception code that has the code too look up the message
     *               by
     * @param params an object that implements the dictionart interface, and
     *               provides the text to fill in the parameters in the message
     *               stored in the file
     * @return ExceptionMessage an exception message contatining the proper
     * code, field reference, and message * @throws
     * MessageTranslatorException
     */
    public static ExceptionMessage translateExceptionMessage(ExceptionCode code, Dictionary<?, ?> params)
            throws MessageTranslatorException {

        return translateExceptionMessage(code.getCode(), params);
    }

    /**
     * This is the same as the translateExceptionMessage method that takes an
     * exception code, except it takes a string representing the code to use to
     * look up the message from the file
     *
     * @param code   the string to look up the message by
     * @param params an object that implements the dictionart interface, and
     *               provides the text to fill in the parameters in the message
     *               stored in the file
     * @return ExceptionMessage an exception message contatining the proper
     * code, field reference, and message * @throws
     * MessageTranslatorException
     */
    public static ExceptionMessage translateExceptionMessage(String code, Dictionary<?, ?> params)
            throws MessageTranslatorException {

        // first look up the message + other info based on the code
        MessageFormat mf = lookupMessageFormat(code); // this can throw an
        // exception
        String[] indexNames = lookupArrayIndexNames(code);
        String fieldRef = lookupFieldReference(code);
        String newCode = lookupCodeName(code);

        // next create an object array from the dictionary
        Object[] formatParams = new Object[indexNames.length];

        for (int i = 0; i < indexNames.length; i++) {

            Object param = null;

            if (null != params) {

                param = params.get(indexNames[i]);
            }
            formatParams[i] = Objects.requireNonNullElse(param, "");
        }

        // then create the message
        String message = mf.format(formatParams);

        // return an exception message with the gathered values
        if (fieldRef == null) {

            return new ExceptionMessage(new ExceptionCode(newCode), message);
        } else {

            return new ExceptionMessage(new ExceptionCode(newCode, fieldRef), message);
        }
    }

    /**
     * Method retrieveExceptionMessage.
     *
     * @param index String
     * @return ExceptionMessage
     */
    public static ExceptionMessage retrieveExceptionMessage(String index) throws MessageTranslatorException {

        String code;
        String message;
        String field = null;

        try {

            message = getPropAsString(index);
            code = getPropAsString(index + CODE_SUFFIX);
        } catch (IOException e) {

            throw new MessageTranslatorException(e);
        }

        try {

            field = getPropAsString(index + FIELD_REFERENCE_SUFFIX);
        } catch (PropertyNotFoundException e) {
            // Ignore since the field is optional
        } catch (IOException e) {

            throw new MessageTranslatorException(e);
        }

        ExceptionMessage exceptionMessage;
        exceptionMessage = new ExceptionMessage(new ExceptionCode(code, field), message);
        return exceptionMessage;
    }

    /**
     * @param index
     * @return
     * @throws MessageTranslatorException
     */
    public static ExceptionContext retrieveExceptionContext(String index)
            throws MessageTranslatorException {

        String name;
        String context;
        try {
            name = getPropAsString(index + PARAMETER_SUFFIX);
            context =
                    getPropAsString(index + CONTEXT_SUFFIX);
        } catch (IOException e) {
            throw
                    new MessageTranslatorException(e);
        }

        ExceptionContext exceptionContext;
        exceptionContext = new
                ExceptionContext(name, context);

        return exceptionContext;
    }

    /**
     * This is the same as the translateExceptionMessage method that takes an
     * exception code, except it takes an exception message, and extracts the
     * code from that. because this method takes an exception message as a
     * parameter, is is able to return the exception message unmodified rather
     * than throwing an exception
     *
     * @param params     an object that implements the dictionart interface, and
     *                   provides the text to fill in the parameters in the message
     *                   stored in the file
     * @param oldMessage ExceptionMessage
     * @return ExceptionMessage an exception message contatining the proper
     * code, field reference, and message
     */

    public static ExceptionMessage translateExceptionMessage(ExceptionMessage oldMessage, Dictionary<?, ?> params) {

        try {

            return translateExceptionMessage(oldMessage.getExceptionCode().getCode(), params);
        } catch (MessageTranslatorException ex) {

            return oldMessage;
        }
    }

    /**
     * Method translateExceptionMessage.
     *
     * @param code String
     * @return ExceptionMessage
     */
    public static ExceptionMessage translateExceptionMessage(String code) throws MessageTranslatorException {

        return translateExceptionMessage(code, null);
    }

    /**
     * Method translateExceptionMessage.
     *
     * @param code ExceptionCode
     * @return ExceptionMessage
     */
    public static ExceptionMessage translateExceptionMessage(ExceptionCode code) throws MessageTranslatorException {

        return translateExceptionMessage(code.getCode(), null);
    }

    /**
     * Method translateMessage.
     *
     * @param code   String
     * @param params Dictionary<?,?>
     * @return String
     */
    public static String translateMessage(String code, Dictionary<?, ?> params) throws MessageTranslatorException {

        // this can throw an
        MessageFormat mf = lookupMessageFormat(code);

        // exception
        String[] indexNames = lookupArrayIndexNames(code);

        // next create an object array from the dictionary
        Object[] formatParams = new Object[indexNames.length];

        for (int i = 0; i < indexNames.length; i++) {

            Object param = null;

            if (null != params) {

                param = params.get(indexNames[i]);
            }
            formatParams[i] = Objects.requireNonNullElse(param, "");
        }

        // then return the message
        return mf.format(formatParams);
    }

    /**
     * Method translateMessage.
     *
     * @param code String
     * @return String
     */
    public static String translateMessage(String code) throws MessageTranslatorException {

        return translateMessage(code, null);
    }

    /**
     * Method lookupMessageFormat.
     * <p>
     * look up the message in the hashtable. if it isn't there the try to get all the info
     * for that code from the file if all the info can't be read, throw an exception
     *
     * @param code String
     * @return MessageFormat
     */
    private static MessageFormat lookupMessageFormat(String code) throws MessageTranslatorException {
        MessageFormat mf = messageFormats.get(code);
        if (mf == null) {
            loadMessageFormat(code); // this can throw a translator exception
            mf = messageFormats.get(code);
        }
        return mf;
    }

    /**
     * Method loadMessageFormat.
     * <p>
     * this is the method that actually carries out the loading of the info for
     * the given code
     *
     * @param code String
     */
    private static void loadMessageFormat(String code) throws MessageTranslatorException {

        try {

            // let it throw an
            String formatString = getPropAsString(code);
            // exception if not found
            MessageFormat mf = new MessageFormat(formatString);
            String[] indexes = removeNamesAndCreateIndex(mf);

            // this also strips thenamed params and replaces then with the numbers that MessageFormat uses
            messageFormats.put(code, mf);
            indexesTable.put(code, indexes);
        } catch (Exception ex) {

            throw new MessageTranslatorException(ex, ex.getMessage());
        }
    }

    /**
     * Method loadFieldReference.
     *
     * @param code String
     */
    public static void loadFieldReference(String code) {

        try {

            String fieldRef = getPropAsString(code + FIELD_REFERENCE_SUFFIX);

            // returns null if not found
            if (null != fieldRef) {
                fieldReferences.put(code, fieldRef);
            }
        } catch (Exception ex) {
            // it doesn't matter if there isn't a field reference
        }
    }

    /**
     * Method loadExceptionCode.
     *
     * @param code String
     */
    public static void loadExceptionCode(String code) throws MessageTranslatorException {
        try {
            String newCode = getPropAsString(code + CODE_SUFFIX);

            // let it throw an exception if not found
            if (null != newCode) {

                codes.put(code, newCode);
            } else {

                // there has to be an exception code, otherwise the exception is not well formed
                throw new Exception("null value for exception code " + code);
            }
        } catch (Exception x) {
            throw new MessageTranslatorException(x, "unable to load exception code for " + code);
        }
    }

    /**
     * Method lookupFieldReference.
     * <p>
     * looks up the field reference in the hashtable, returning null if the
     * value is not there
     *
     * @param code String
     * @return String
     */
    private static String lookupFieldReference(String code) {

        String fieldRef = fieldReferences.get(code);

        if (null == fieldRef) {

            loadFieldReference(code);
            fieldRef = fieldReferences.get(code);
        }
        return fieldRef;
    }

    /**
     * Method lookupArrayIndexNames.
     * <p>
     * looks up the array index names in the hashtabe for the given code,
     * returning a zero length string array if there are none
     *
     * @param code String
     * @return String[]
     */
    private static String[] lookupArrayIndexNames(String code) {

        String[] toReturn = indexesTable.get(code);

        if (null == toReturn) {

            try {

                // this can throw a translator exception
                loadMessageFormat(code);
                toReturn = indexesTable.get(code);
            } catch (Exception ex) {

                toReturn = new String[0];
            }
        }
        return toReturn;
    }

    /**
     * Method lookupCodeName.
     *
     * @param code String
     * @return String
     */
    private static String lookupCodeName(String code) throws MessageTranslatorException {

        String newCode = codes.get(code);

        if (null == newCode) {

            loadExceptionCode(code);
            newCode = codes.get(code);
        }
        return newCode;
    }

    /**
     * Method removeNamesAndCreateIndex.
     * <p>
     * this method takes a message format object that is not valid because it uses
     * #name# instead of {0}, {1}, etc. it replaces each #name# with a number in curly braces
     * it also creates a string array with all the names that had to be removed
     *
     * @param mf MessageFormat
     * @return String[]
     */
    private static String[] removeNamesAndCreateIndex(MessageFormat mf) {

        // TODO: mabe optimize this to not create a new array each time
        // this code is pasted from the database connection class
        StringTokenizer tokenizer = new StringTokenizer(mf.toPattern(), "#");

        int nbrTokens = tokenizer.countTokens();
        int counter = 0;
        Vector<String> returnVector = new Vector<>();
        StringBuilder buf = new StringBuilder();

        for (int i = 0; i < nbrTokens; i++)
        // while (nbrTokens-- > 0)
        {
            String token = tokenizer.nextToken();

            // This is not a parameter.
            if ((i % 2) == 0) {

                // no index name to add to the array, nothig to replace in the string
                buf.append(token);

            } else {
                // We have a parameter.
                returnVector.addElement(token);

                // Remove the parameter markup and replace it with a number inside curly
                // braces for the MessageFormat to replace with paramaters
                buf.append("{").append(counter).append("}");
                counter++;
            }
        }

        mf.applyPattern(buf.toString());

        if (!returnVector.isEmpty()) {

            String[] namedIndexes = new String[returnVector.size()];
            returnVector.copyInto(namedIndexes);
            return namedIndexes;
        } else {

            return new String[0];
        }
    }
}
