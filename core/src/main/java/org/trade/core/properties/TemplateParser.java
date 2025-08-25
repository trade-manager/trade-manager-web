
package org.trade.core.properties;

import java.util.ArrayList;
import java.util.Dictionary;
import java.util.List;
import java.util.ListIterator;
import java.util.Properties;

/**
 * This class parses a template substituting keys inclosed in "#(" and ")#".
 * Characters '(', '#', and ')' are used as a default mark up, and can be chaged
 * with setBrackets() and setDelimiter() methods.
 * <p>
 * E.g. template (String) :
 * <font face="arial" size="2" color="#(application_form_errors_color)#"> tags
 * (Hashtable) : { "application_form_errors_color", "#0fffff" } Output:
 * <font face="arial" size="2" color="#0fffff">
 *
 * @author Simon Allen
 */
public class TemplateParser {

    private char leftBracket = '(';
    private char rightBracket = ')';
    private char delimiter = '#';
    private boolean insertMissingTags = false;
    private final String BEGIN_FOR_EACH = "BEGIN_FOR_EACH";
    private final String END_FOR_EACH = "END_FOR_EACH";
    private final String m_template;
    private final Dictionary<?, ?> m_tags;
    private int lastParsedCharPosition = 0;
    private final int lastCharPosition;
    private int iterationNumber = -1;
    private final StringBuffer errorMessages = new StringBuffer();
    private final List<String> missingKeys = new ArrayList<>();

    /**
     * @param template - the template.
     * @param tags     - the tags and values.
     */
    public TemplateParser(String template, Dictionary<?, ?> tags) {
        if ((template == null) || (tags == null)) {
            throw new IllegalArgumentException(
                    "Null argument passed to TemplateParser() constructor. template=" + template + ", tags=" + tags);
        }

        m_template = template;
        m_tags = tags;
        lastCharPosition = m_template.length() - 1;
    }

    /**
     * If 'insertUnmatchedKeys' option is true parseTemplate method will insert,
     * #(key_name)# strings found in the template, into output if value for the
     * key_name was not specified.
     * <p>
     * Otherwise #(key_name)# pattern is ignored and nothing will appear in it's
     * place in the output.
     *
     * @param option boolean
     */
    public void setInsertMissingTags(boolean option) {
        insertMissingTags = option;
    }

    /**
     * Set characters used as brackets enclosing the key. Default values are '('
     * and ')'.
     * <p>
     * E.g. #(key_name)#.
     *
     * @param left  char
     * @param right char
     */
    public void setBrackets(char left, char right) {
        leftBracket = left;
        rightBracket = right;
    }

    /**
     * Set character used as a delimiter enclosing the key. Default value is
     * '#'.
     * <p>
     * E.g. #(key_name)#.
     *
     * @param delimiter char
     */
    public void setDelimiter(char delimiter) {
        this.delimiter = delimiter;
    }

    /**
     * This method parses the template substituting keys with values supplied in
     * tags Dictionary.
     *
     * @return String
     */
    public String parseTemplate() {

        StringBuilder parsedTemplate = new StringBuilder();
        errorMessages.setLength(0);
        missingKeys.clear();

        while (true) {

            NextToken result = getNextToken();

            if (!result.finishedParsing()) {

                if (result.foundToken()) {

                    parsedTemplate.append(
                            m_template, result.getLastParsedPosition(), result.getPositionBeforeKey() + 1);

                    if (result.getMissingTag() == null) {

                        if (!result.getKey().endsWith("[]")) {

                            parsedTemplate.append(result.getValue());
                        } else {

                            try {

                                Object[] array = result.getArrayOfValues();

                                if ((iterationNumber >= 0) && (iterationNumber < array.length)) {
                                    parsedTemplate.append(array[iterationNumber].toString());
                                }
                            } catch (Exception e) {

                                missingKeys.add(result.getKey());
                                addErrorMessage("Key " + result.getKey() + " not found in the parameters. Iteration # "
                                        + iterationNumber);
                            }
                        }
                    } else {

                        if (BEGIN_FOR_EACH.equals(result.getKey())) {

                            String parsedSubtemplate = processForEachSubtemplate(result);

                            if (parsedSubtemplate == null) {

                                missingKeys.add(END_FOR_EACH);
                                addErrorMessage("Tag " + END_FOR_EACH + " not found in the template.");
                            } else {

                                parsedTemplate.append(parsedSubtemplate);
                            }
                        } else {

                            if (insertMissingTags) {
                                parsedTemplate.append(result.getMissingTag());
                            }

                            missingKeys.add(result.getKey());
                            addErrorMessage("Key " + result.getKey() + " not found in parameters.");
                        }
                    }
                } else {
                    // result.getFoundToken() == false
                    // This will happen when we find a pair of # characters
                    // which don't enclose a key #(key)#
                    parsedTemplate
                            .append(m_template, result.getLastParsedPosition(), result.getParsedPosition());
                }
            } else {
                // If finished parsing append the rest of the template
                parsedTemplate.append(m_template, result.getLastParsedPosition(), result.getParsedPosition());

                break;
            }
        }

        return parsedTemplate.toString();
    }

    /**
     * This method parses the template substituting keys with values supplied in
     * tags Dictionary.
     *
     * @return Properties
     */
    public Properties findTemplateTags() {

        Properties tags = new Properties();
        errorMessages.setLength(0);
        missingKeys.clear();

        while (true) {

            NextToken result = getNextToken();

            if (!result.finishedParsing()) {

                if (result.foundToken() && !BEGIN_FOR_EACH.equals(result.getKey())
                        && !END_FOR_EACH.equals(result.getKey())) {

                    tags.put(result.getKey(), result.getKey());
                }
            } else {

                break;
            }
        }

        return (tags);
    }

    /**
     * This method parses the template substituting keys with values supplied in
     * tags Hashtable.
     *
     * @return NextToken
     */
    public NextToken getNextToken() {

        int delimiterPosition;
        int nextDelimiterPosition = -1;
        NextToken result = new NextToken();

        delimiterPosition = m_template.indexOf(delimiter, lastParsedCharPosition);

        if (delimiterPosition != -1) {

            nextDelimiterPosition = m_template.indexOf(delimiter, delimiterPosition + 1);
        }

        if ((delimiterPosition == -1) || (nextDelimiterPosition == -1)) {

            result.setLastParsedPosition(lastParsedCharPosition);
            result.setParsedPosition(lastCharPosition + 1);
            result.setFoundToken(false);
            result.setFinishedParsing(true);
            return (result);
        } else {

            int leftBracketPosition = delimiterPosition + 1;
            int rightBracketPosition = nextDelimiterPosition - 1;

            if ((leftBracketPosition < rightBracketPosition)
                    && (m_template.charAt(leftBracketPosition) == leftBracket)
                    && (m_template.charAt(rightBracketPosition) == rightBracket)) {

                // We have correct syntax element : #(key)#
                String key = m_template.substring(leftBracketPosition + 1, rightBracketPosition);
                result.setKey(key);
                Object value = m_tags.get(key);

                if (value != null) {

                    result.setValue(value);
                }

                // If key not found #(key)# is ignored and nothing is
                // inserted in it's place in the output
                else {

                    String missingTag = String.valueOf(delimiter) +
                            leftBracket +
                            key +
                            rightBracket +
                            delimiter;
                    result.setMissingTag(missingTag);
                }

                result.setPositionAfterKey(nextDelimiterPosition + 1);
                result.setPositionBeforeKey(delimiterPosition - 1);
                result.setFoundToken(true);
            } else {

                result.setFoundToken(false);
            }

            result.setLastParsedPosition(lastParsedCharPosition);

            if (result.foundToken()) {

                lastParsedCharPosition = nextDelimiterPosition + 1;
            } else {

                lastParsedCharPosition = nextDelimiterPosition - 1;
            }

            result.setParsedPosition(lastParsedCharPosition);
            result.setFinishedParsing(false);
        }

        return result;
    }

    /**
     * Method processForEachSubtemplate.
     *
     * @param beginToken NextToken
     * @return String
     */
    private String processForEachSubtemplate(NextToken beginToken) {

        NextToken endToken;
        StringBuilder result = new StringBuilder();
        int numberOfIterations = 0;

        do {
            endToken = getNextToken();

            if ((endToken != null) && (endToken.getKey() != null) && endToken.getKey().endsWith("[]")) {

                try {

                    Object[] array = endToken.getArrayOfValues();

                    if ((array != null) && (array.length > numberOfIterations)) {

                        numberOfIterations = array.length;
                    }
                } catch (Exception _) {
                    // A class cast exception can happen
                    // here
                }
            }
        } while ((endToken != null) && !END_FOR_EACH.equals(endToken.getKey()));

        if (endToken == null) {
            return null;
        }

        String subtemplate = m_template.substring(beginToken.getPositionAfterKey(),
                endToken.getPositionBeforeKey() + 1);
        TemplateParser parser;
        String parsedSubtemplate;

        for (int i = 0; i < numberOfIterations; i++) {

            parser = new TemplateParser(subtemplate, m_tags);

            parser.setIterationNumber(i);

            parsedSubtemplate = parser.parseTemplate();

            if (parsedSubtemplate != null) {
                result.append(parsedSubtemplate);
            } else {
                break; // Something went wrong, it does not make sense to
                // continue.
            }
        }

        if ((numberOfIterations == 0) && insertMissingTags) {
            // if insert missing tags is true insert subtemplate into the output
            result.append(delimiter);
            result.append(leftBracket);
            result.append(BEGIN_FOR_EACH);
            result.append(rightBracket);
            result.append(delimiter);
            result.append(subtemplate);
            result.append(delimiter);
            result.append(leftBracket);
            result.append(END_FOR_EACH);
            result.append(rightBracket);
            result.append(delimiter);
        }

        return result.toString();
    }

    /**
     * This class holds intermediate results of parsing the template.
     *
     * @author Simon Allen
     * @version $Revision: 1.0 $
     */
    public static class NextToken {

        private String key = null;
        private Object value = null;
        private Object[] arrayOfValues = null;
        private String missingTag = null;
        private boolean foundToken = false;
        private boolean finishedParsing = false;
        private int parsedPosition = 0;
        private int lastParsedPosition = 0;
        private int positionBeforeKey = -1;
        private int positionAfterKey = -1;

        /**
         * Method setFoundToken.
         *
         * @param foundToken boolean
         */
        public void setFoundToken(boolean foundToken) {
            this.foundToken = foundToken;
        }

        /**
         * Method foundToken.
         *
         * @return boolean
         */
        public boolean foundToken() {
            return (foundToken);
        }

        /**
         * Method setFinishedParsing.
         *
         * @param finishedParsing boolean
         */
        public void setFinishedParsing(boolean finishedParsing) {
            this.finishedParsing = finishedParsing;
        }

        /**
         * Method finishedParsing.
         *
         * @return boolean
         */
        public boolean finishedParsing() {
            return (finishedParsing);
        }

        /**
         * Method getParsedPosition.
         *
         * @return int
         */
        public int getParsedPosition() {
            return (parsedPosition);
        }

        /**
         * Method setParsedPosition.
         *
         * @param position int
         */
        public void setParsedPosition(int position) {
            parsedPosition = position;
        }

        /**
         * Method getLastParsedPosition.
         *
         * @return int
         */
        public int getLastParsedPosition() {
            return (lastParsedPosition);
        }

        /**
         * Method setLastParsedPosition.
         *
         * @param position int
         */
        public void setLastParsedPosition(int position) {
            lastParsedPosition = position;
        }

        /**
         * Method getPositionBeforeKey.
         *
         * @return int
         */
        public int getPositionBeforeKey() {
            return (positionBeforeKey);
        }

        /**
         * Method setPositionBeforeKey.
         *
         * @param position int
         */
        public void setPositionBeforeKey(int position) {
            positionBeforeKey = position;
        }

        /**
         * Method getPositionAfterKey.
         *
         * @return int
         */
        public int getPositionAfterKey() {
            return (positionAfterKey);
        }

        /**
         * Method setPositionAfterKey.
         *
         * @param position int
         */
        public void setPositionAfterKey(int position) {
            positionAfterKey = position;
        }

        /**
         * Method getKey.
         *
         * @return String
         */
        public String getKey() {
            return (key);
        }

        /**
         * Method setKey.
         *
         * @param key String
         */
        public void setKey(String key) {
            this.key = key;
        }

        /**
         * Method getMissingTag.
         *
         * @return String
         */
        public String getMissingTag() {
            return (missingTag);
        }

        /**
         * Method setMissingTag.
         *
         * @param tag String
         */
        public void setMissingTag(String tag) {
            missingTag = tag;
        }

        /**
         * Method getValue.
         *
         * @return Object
         */
        public Object getValue() {
            return (value);
        }

        /**
         * Method setValue.
         *
         * @param value Object
         */
        public void setValue(Object value) {
            if (value instanceof List<?> v) {

                arrayOfValues = v.toArray();
            } else if (value instanceof ArrayOfValues) {
                arrayOfValues = ((ArrayOfValues) value).getValues();
            } else {
                this.value = value;
            }
        }

        /**
         * Method getArrayOfValues.
         *
         * @return Object[]
         */
        public Object[] getArrayOfValues() {
            return (arrayOfValues);
        }
    }

    /**
     * Method setIterationNumber.
     *
     * @param iterationNumber int
     */
    private void setIterationNumber(int iterationNumber) {
        this.iterationNumber = iterationNumber;
    }

    /**
     * Return list of error messages found while parsing last template.
     *
     * @return String with error messages
     */
    public String getErrorMessages() {
        return errorMessages.toString();
    }

    /**
     * Return list of error messages found while parsing last template.
     *
     * @param message String
     */
    private void addErrorMessage(String message) {

        if (message != null) {
            errorMessages.append(message);
        }

        errorMessages.append("\r\n");
    }

    /**
     * This method returns an <code>Enumeration</code> of key names that were
     * not found in the tags Hashtable and were not substituted while parsing
     * the template during last invokation of the parseTemplate method.
     *
     * @return Enumeration of missing parameter's names.
     */
    public ListIterator<String> getMissingParameters() {
        return missingKeys.listIterator();
    }
}
