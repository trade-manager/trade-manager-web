package org.trade.core.validator;

import org.trade.core.exception.ExceptionMessage;
import org.trade.core.message.IMessageFactory;
import org.trade.core.message.MessageContextFactory;
import org.trade.core.message.MessageFactory;

/**
 *
 * @author Simon Allen
 * @version $Revision: 1.0 $
 */
public class StringValidator implements IValidator {

    // Use these to indicate sets of valid characters
    public final static int NONE = 0;
    public final static int DIGITS = 1;
    public final static int SPACES = 2;
    public final static int LETTERS = 4;
    public final static int PUNCTUATION = 8;
    public final static int ANY = 16;
    public final static int ALPHANUMERIC = DIGITS + SPACES + LETTERS;
    public final static int ALPHA = LETTERS + SPACES;

    private IMessageFactory messageFactory;
    private int minLength;
    private final int maxLength;
    private final int permittedCharacterSet;
    private final boolean isMandatory;
    private final String additionalPermittedCharacters;

    /**
     * Constructor for StringValidator.
     *
     * @param messageFactory                IMessageFactory
     * @param maxLength                     int
     * @param permittedCharacterSet         int
     * @param additionalPermittedCharacters String
     * @param isMandatory                   boolean
     */
    public StringValidator(IMessageFactory messageFactory, int maxLength, int permittedCharacterSet,
                           String additionalPermittedCharacters, boolean isMandatory) {
        this.messageFactory = messageFactory;
        this.maxLength = maxLength;
        this.permittedCharacterSet = permittedCharacterSet;
        this.isMandatory = isMandatory;
        this.additionalPermittedCharacters = additionalPermittedCharacters;
    }

    /**
     * Constructor for StringValidator.
     *
     * @param messageFactory                IMessageFactory
     * @param minLength                     int
     * @param maxLength                     int
     * @param permittedCharacterSet         int
     * @param additionalPermittedCharacters String
     * @param isMandatory                   boolean
     */
    public StringValidator(IMessageFactory messageFactory, int minLength, int maxLength, int permittedCharacterSet,
                           String additionalPermittedCharacters, boolean isMandatory) {
        this(messageFactory, maxLength, permittedCharacterSet, additionalPermittedCharacters, isMandatory);
        this.minLength = minLength;
    }

    /**
     * Constructor for StringValidator.
     *
     * @param messageFactory        IMessageFactory
     * @param minLength             int
     * @param maxLength             int
     * @param permittedCharacterSet int
     * @param isMandatory           boolean
     */
    public StringValidator(IMessageFactory messageFactory, int minLength, int maxLength, int permittedCharacterSet,
                           boolean isMandatory) {
        this(messageFactory, maxLength, permittedCharacterSet, null, isMandatory);
        this.minLength = minLength;
    }

    /**
     * Constructor for StringValidator.
     *
     * @param messageFactory        IMessageFactory
     * @param maxLength             int
     * @param permittedCharacterSet int
     * @param isMandatory           boolean
     */
    public StringValidator(IMessageFactory messageFactory, int maxLength, int permittedCharacterSet,
                           boolean isMandatory) {
        this(messageFactory, maxLength, permittedCharacterSet, null, isMandatory);
    }

    /**
     * Method getMessageFactory.
     *
     * @return IMessageFactory
     */
    protected IMessageFactory getMessageFactory() {
        if (null == messageFactory) {
            messageFactory = MessageFactory.SYSTEM_ERROR;
        }

        return messageFactory;
    }

    /**
     * Method isMandatory.
     *
     * @return boolean
     */
    public boolean isMandatory() {
        return (isMandatory);
    }

    /**
     * Method isValid.
     *
     * @param value          Object
     * @param invalidValue   String
     * @param expectedFormat String
     * @param receiver       IExceptionMessageListener
     * @return boolean
     * @see IValidator#isValid(Object, String, String,
     * IExceptionMessageListener)
     */
    public boolean isValid(Object value, String invalidValue, String expectedFormat,
                           IExceptionMessageListener receiver) {
        if (null == receiver) {
            receiver = new IExceptionMessageListener() {
                public void addExceptionMessage(ExceptionMessage e) {
                }
            };
        }

        boolean valid = true;

        if (null == value) {
            value = "";
        }

        if (((String) value).isEmpty()) // Optional/mandatory check
        {
            if (isMandatory) {
                valid = false;
                receiver.addExceptionMessage(
                        getMessageFactory().create(MessageContextFactory.MANDATORY_VALUE_NOT_PROVIDED.create()));
            }
        } else if (((String) value).length() > maxLength) // Max length check
        {
            valid = false;
            receiver.addExceptionMessage(getMessageFactory().create(MessageContextFactory.MAX_LENGTH_EXCEEDED
                    .create(MessageContextFactory.MAX_LENGTH.create(maxLength))));
        } else if (((String) value).length() < minLength) // Min length check
        {
            valid = false;
            receiver.addExceptionMessage(getMessageFactory().create(MessageContextFactory.MIN_LENGTH_FAILED
                    .create(MessageContextFactory.MIN_LENGTH.create(minLength))));
        } else
        // 0 < length < max length so check valid characters
        {
            String invalidCharacters = checkForInvalidCharacters(((String) value), permittedCharacterSet,
                    additionalPermittedCharacters);

            if (null != invalidCharacters) {
                valid = false;
                receiver.addExceptionMessage(
                        getMessageFactory().create(MessageContextFactory.CONTAINS_INVALID_CHARACTERS
                                .create(MessageContextFactory.INVALID_CHARACTERS.create(invalidCharacters))));
            }
        }

        return valid;
    }

    /**
     * Checks a string for invalid characters. Any invalid characters will be
     * returned.
     *
     * @param toStrip        String
     * @param whatToKeep     int
     * @param whatElseToKeep String
     * @return String A list of each invalid character; null if all characters
     * are permitted.
     */
    public static String checkForInvalidCharacters(String toStrip, int whatToKeep, String whatElseToKeep) {
        if ((null == toStrip) || (toStrip.isEmpty())) {
            return null;
        }

        StringBuilder invalidChars = new StringBuilder();

        int toStripLength = toStrip.length();
        for (int i = 0; i < toStripLength; i++) {
            if ((!isValidChar(toStrip.charAt(i), whatToKeep)) && (!isValidChar(toStrip.charAt(i), whatElseToKeep))) {
                if (invalidChars.toString().indexOf(toStrip.charAt(i)) == -1) {
                    invalidChars.append(toStrip.charAt(i));
                }
            }
        }

        if (invalidChars.toString().isEmpty()) {
            return null;
        } else {
            return invalidChars.toString();
        }
    }

    /**
     * Method isValidChar.
     *
     * @param toCheck    char
     * @param whatToKeep int
     * @return boolean
     */
    protected static boolean isValidChar(char toCheck, int whatToKeep) {
        if ((whatToKeep & ANY) > 0) {
            return true;
        }

        boolean spaces = ((whatToKeep & SPACES) > 0);
        if (toCheck == ' ') {
            return spaces;
        }

        boolean digits = ((whatToKeep & DIGITS) > 0);
        if ((toCheck >= '0') && (toCheck <= '9')) {
            return digits;
        }

        boolean letters = ((whatToKeep & LETTERS) > 0);
        if (((toCheck >= 'a') && (toCheck <= 'z')) || ((toCheck >= 'A') && (toCheck <= 'Z'))) {
            return letters;
        }

        boolean punctuation = ((whatToKeep & PUNCTUATION) > 0);
        if (punctuation) {
            return isValidChar(toCheck, "`~!@#$%^&*()-_=+\\|]}[{;:,<.>/?\"'");
        } else {
            return false;
        }
    }

    /**
     * Method isValidChar.
     *
     * @param toCheck    char
     * @param whatToKeep String
     * @return boolean
     */
    protected static boolean isValidChar(char toCheck, String whatToKeep) {
        if (null != whatToKeep) {
            int whatToKeepLength = whatToKeep.length();
            for (int i = 0; i < whatToKeepLength; i++) {
                if (whatToKeep.charAt(i) == toCheck) {
                    return true;
                }
            }
        }

        return false;
    }

    /**
     * Method stripSpecificChars.
     *
     * @param toStrip     String
     * @param whatToStrip String
     * @return String
     */
    public static String stripSpecificChars(String toStrip, String whatToStrip) {
        if ((null == toStrip) || (toStrip.isEmpty())) {
            return toStrip;
        }

        StringBuilder bufferedResult = new StringBuilder();

        int toStripLength = toStrip.length();
        for (int i = 0; i < toStripLength; i++) {
            // if the character is valid where the valid chars come from
            // toStrip,
            // then the character is of of the chars the need to be stripped
            if (!isValidChar(toStrip.charAt(i), whatToStrip)) {
                bufferedResult.append(toStrip.charAt(i));
            }
        }

        return bufferedResult.toString();
    }
}
