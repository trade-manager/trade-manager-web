package org.trade.core.message;

import org.trade.core.exception.ExceptionCode;
import org.trade.core.exception.ExceptionContext;
import org.trade.core.exception.ExceptionMessage;

/**
 * Exception context is used as the key for retrieving an exception message.
 * Each context can be either a static string from the properties file or a
 * dynamic value filled at runtime by converting an object to a string.
 *
 * @author Simon Allen
 */
public class MessageContextFactory implements IMessageContextFactory {
    // This is a special case and represents an internal error. It will be used when other messages cannot be located.
    public final static IMessageContextFactory DEFAULT = new MessageContextFactory(new ExceptionContext("", "[]"));

    // Used by RequestMap to populate XML_STRUCTURE_DOES_NOT_MATCH_REQUEST_TYPE message
    public final static IMessageContextFactory XML_STRUCTURE_MESSAGE = new MessageContextFactory(
            new ExceptionContext("description", ""));

    // Used by IntRequest, should be removed after IntRequest.getValue() goes away
    public final static IMessageContextFactory FIELD_INVALID_VALUE = new MessageContextFactory(
            new ExceptionContext("invalid_value", ""));
    public final static IMessageContextFactory FIELD_INVALID_REASON = new MessageContextFactory(
            new ExceptionContext("edit_check", ""));

    // Used by IntRequest.setHostingPartyRequestCreateTime(), possibly incorrectly
    public final static IMessageContextFactory DATE_FORMAT_INCORRECT = new MessageContextFactory(
            new ExceptionContext("edit_check", " must be formatted YYYY-MM-DDTHHmmSSZ "));

    // Used by Validators
    public final static IMessageContextFactory MANDATORY_VALUE_NOT_PROVIDED = new MessageContextFactory(
            new ExceptionContext("edit_check", "A value must be provided"));
    public final static IMessageContextFactory MIN_LENGTH_FAILED = new MessageContextFactory(
            new ExceptionContext("edit_check", "The length must be at least #min_length#"));
    public final static IMessageContextFactory MIN_LENGTH = new MessageContextFactory(
            new ExceptionContext("min_length", ""));
    public final static IMessageContextFactory MAX_LENGTH_EXCEEDED = new MessageContextFactory(
            new ExceptionContext("edit_check", "The length cannot exceed #max_length#"));
    public final static IMessageContextFactory MAX_LENGTH = new MessageContextFactory(
            new ExceptionContext("max_length", ""));
    public final static IMessageContextFactory CONTAINS_INVALID_CHARACTERS = new MessageContextFactory(
            new ExceptionContext("edit_check", "The character(s) [#invalid_characters#] are not permitted"));
    public final static IMessageContextFactory INVALID_CHARACTERS = new MessageContextFactory(
            new ExceptionContext("invalid_characters", ""));
    public final static IMessageContextFactory CODE_NOT_VALID = new MessageContextFactory(
            new ExceptionContext("edit_check", "The code [#invalid_code#] is not known"));
    public final static IMessageContextFactory INVALID_CODE = new MessageContextFactory(
            new ExceptionContext("invalid_code", ""));
    public final static IMessageContextFactory FAKE_TAX_ID = new MessageContextFactory(
            new ExceptionContext("edit_check", "The value specified is illegal"));
    public final static IMessageContextFactory EMAIL_MUST_HAVE_AT_SIGN = new MessageContextFactory(
            new ExceptionContext("edit_check", "Email addresses must contain the @ symbol"));
    public final static IMessageContextFactory EMAIL_MUST_HAVE_DOT = new MessageContextFactory(
            new ExceptionContext("edit_check", "Email addresses must contain the . symbol"));
    public final static IMessageContextFactory EMAIL_MUST_HAVE_DOT_FOLLOWING_AT = new MessageContextFactory(
            new ExceptionContext("edit_check", "Email addresses must the @ symbol preceding the . symbol"));
    public final static IMessageContextFactory EMAIL_AT_SIGN_MISPLACED = new MessageContextFactory(
            new ExceptionContext("edit_check", "The @ symbol is incorrectly located"));

    // from NumericRangeValidator
    public final static IMessageContextFactory BELOW_MIN_VALUE = new MessageContextFactory(
            new ExceptionContext("edit_check", "The value cannot be less than #min_value#"));
    public final static IMessageContextFactory MIN_VALUE = new MessageContextFactory(
            new ExceptionContext("min_value", ""));
    public final static IMessageContextFactory EXCEEDS_MAX_VALUE = new MessageContextFactory(
            new ExceptionContext("edit_check", "The value cannot be exceed #max_value#"));
    public final static IMessageContextFactory MAX_VALUE = new MessageContextFactory(
            new ExceptionContext("max_value", ""));
    public final static IMessageContextFactory COUNTRY_MUST_BE_US = new MessageContextFactory(
            new ExceptionContext("edit_check", "Only US addresses are allowed"));
    public final static IMessageContextFactory ZIP_MUST_MATCH_STATE = new MessageContextFactory(
            new ExceptionContext("edit_check", "The zip code does not match the state provided"));
    public final static IMessageContextFactory MUST_BE_STREET_ADDRESS = new MessageContextFactory(
            new ExceptionContext("edit_check", "Only street addresses are allowed"));
    public final static IMessageContextFactory AT_MOST_ONE_FIELD = new MessageContextFactory(
            new ExceptionContext("at_most_one_field", "Enter only ony value"));
    public final static IMessageContextFactory AT_LEAST_ONE_FIELD = new MessageContextFactory(
            new ExceptionContext("at_least_one_field", "Enter at least one value"));
    public final static IMessageContextFactory MONEY_RIGHT_OF_DECIMAL_TOO_LONG = new MessageContextFactory(
            new ExceptionContext("edit_check",
                    "At most #max_length# digits are allowed to the right of the decimal point"));
    public final static IMessageContextFactory MONEY_LEFT_OF_DECIMAL_TOO_LONG = new MessageContextFactory(
            new ExceptionContext("edit_check",
                    "At most #max_length# digits are allowed to the left of the decimal point"));
    public final static IMessageContextFactory MONEY_ZERO_NOT_ALLOWED = new MessageContextFactory(
            new ExceptionContext("edit_check", "Zero is not an acceptable value"));
    public final static IMessageContextFactory MONEY_NEGATIVE_NOT_ALLOWED = new MessageContextFactory(
            new ExceptionContext("edit_check", "Negative values are not allowed"));
    public final static IMessageContextFactory MONEY_DOT_WITH_NO_NUMBERS = new MessageContextFactory(
            new ExceptionContext("edit_check", "The value '.' is not permitted"));
    public final static IMessageContextFactory MONEY_MULTIPLE_DOTS = new MessageContextFactory(
            new ExceptionContext("edit_check", "Multiple decimal points are not permitted"));
    public final static IMessageContextFactory MONEY_MULTIPLE_DASHES = new MessageContextFactory(
            new ExceptionContext("edit_check", "Multiple dashes are not permitted"));
    public final static IMessageContextFactory MONEY_DASH_NOT_FIRST_CHARACTER = new MessageContextFactory(
            new ExceptionContext("edit_check", "The negative sign must be the first character"));
    public final static IMessageContextFactory PERCENT_RIGHT_OF_DECIMAL_TOO_LONG = new MessageContextFactory(
            new ExceptionContext("edit_check",
                    "At most #max_length# digits are allowed to the right of the decimal point"));
    public final static IMessageContextFactory PERCENT_LEFT_OF_DECIMAL_TOO_LONG = new MessageContextFactory(
            new ExceptionContext("edit_check",
                    "At most #max_length# digits are allowed to the left of the decimal point"));
    public final static IMessageContextFactory PERCENT_ZERO_NOT_ALLOWED = new MessageContextFactory(
            new ExceptionContext("edit_check", "Zero is not an acceptable value"));
    public final static IMessageContextFactory PERCENT_NEGATIVE_NOT_ALLOWED = new MessageContextFactory(
            new ExceptionContext("edit_check", "Negative values are not allowed"));
    public final static IMessageContextFactory PERCENT_DOT_WITH_NO_NUMBERS = new MessageContextFactory(
            new ExceptionContext("edit_check", "The value '.' is not permitted"));
    public final static IMessageContextFactory PERCENT_MULTIPLE_DOTS = new MessageContextFactory(
            new ExceptionContext("edit_check", "Multiple decimal points are not permitted"));
    public final static IMessageContextFactory PERCENT_MULTIPLE_DASHES = new MessageContextFactory(
            new ExceptionContext("edit_check", "Multiple dashes are not permitted"));
    public final static IMessageContextFactory PERCENT_DASH_NOT_FIRST_CHARACTER = new MessageContextFactory(
            new ExceptionContext("edit_check", "The negative sign must be the first character"));
    public final static IMessageContextFactory ACCT_NBR_FAILS_MOD_10_CHECK = new MessageContextFactory(
            new ExceptionContext("edit_check", "Fails mod 10 validation"));

    private final ExceptionContext exceptionContext;

    /**
     * Constructor for MessageContextFactory.
     *
     * @param exceptionContext ExceptionContext
     */
    private MessageContextFactory(ExceptionContext exceptionContext) {

        this.exceptionContext = exceptionContext;
    }

    /**
     * Method create.
     *
     * @return ExceptionContext
     * @see IMessageContextFactory#create()
     */
    public ExceptionContext create() {

        // ExceptionContext is immutable so we don't have to create a new instance.
        return exceptionContext;
    }

    /**
     * Method create.
     *
     * @param context ExceptionContext
     * @return ExceptionContext
     * @see IMessageContextFactory#create(ExceptionContext)
     */
    public ExceptionContext create(ExceptionContext context) {

        // TODO: This is a pretty back hack. I convert the context to a message
        // to use parameter substitution (too lazy to do it right).
        ExceptionMessage message = new ExceptionMessage(new ExceptionCode("XXX"), exceptionContext.getValue());
        message.addExceptionContext(context);
        return create(message.getMessage());
    }

    /**
     * Method create.
     *
     * @param dynamicValue Object
     * @return ExceptionContext
     * @see IMessageContextFactory#create(Object)
     */
    public ExceptionContext create(Object dynamicValue) {

        // Create a new ExceptionContext with the desired value filled in.
        ExceptionContext exceptionContext;
        exceptionContext = new ExceptionContext(this.exceptionContext, dynamicValue);
        return exceptionContext;
    }
}
