/* ===========================================================
 * TradeManager : a application to trade strategies for the Java(tm) platform
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
package org.trade.core.util;

import org.trade.core.dao.Aspect;
import org.trade.core.properties.CollectionUtilities;
import org.trade.core.valuetype.Decode;

import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import javax.swing.text.SimpleAttributeSet;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 *
 */
public class CoreUtils {
    /**
     * Replace substrings of a string.
     *
     * @param replaceIn   String
     * @param toReplace   String
     * @param replaceWith String
     * @return String
     */
    public static final String replace(String replaceIn, String toReplace, String replaceWith) {
        StringBuffer buf = new StringBuffer(replaceIn);
        int replaceLength = toReplace.length();
        int replaceWithLength = replaceWith.length();
        int netLength = replaceWithLength - replaceLength;
        int index = -1;
        int count = 0;

        while ((index = replaceIn.indexOf(toReplace, index + 1)) != -1) {
            int bufIndex = index + (count * netLength);

            buf.replace(bufIndex, bufIndex + replaceLength, replaceWith);

            count++;
        }

        return buf.toString();
    }

    /**
     * Convert a string into a Java string representation. This just changes \
     * to \\ and " to \".
     *
     * @param unescaped String
     * @return String
     */
    public static final String escapeJava(String unescaped) {
        String escaped = replace(unescaped, "\\", "\\\\");

        escaped = replace(escaped, "\"", "\\\"");

        return escaped;
    }

    /**
     * Returns a Hashtable of the attribute anems and vales in alpha order
     * Returns a <code>Hashtable</code>
     *
     * @param aspect           Aspect
     * @param decodeConvertion boolean
     * @return Hashtable. * @throws InvocationTargetException
     * @throws IllegalAccessException
     * @since ICAP Version Exchange
     */

    public static Hashtable<String, Object> getAllAttribueValues(Aspect aspect, boolean decodeConvertion)
            throws InvocationTargetException, IllegalAccessException {

        final Hashtable<String, Object> attributeList = new Hashtable<String, Object>();
        final Method method[] = aspect.getClass().getDeclaredMethods();

        if (method.length > 0) {

            for (final Method element : method) {
                final String methodName = element.getName();

                if ("GET".equals(methodName.substring(0, 3).toUpperCase())) {

                    element.getReturnType();
                    final Class<?> parms[] = element.getParameterTypes();
                    final Object[] o = new Object[parms.length];

                    for (int j = 0; j < parms.length; j++) {
                        final Object obj = parms[j];
                        o[j] = obj;
                    }
                    Object returnValue = element.invoke(aspect, o);

                    if (null == returnValue) {
                        returnValue = "null";
                    }
                    if (decodeConvertion
                            && returnValue.getClass().getSuperclass().getName().equals(Decode.class.getName())) {
                        returnValue = ((Decode) returnValue).getCode();
                    }
                    attributeList.put(methodName.substring(3, methodName.length()), returnValue);
                }

            }
        }

        return attributeList;
    }

    /**
     * Format the return values from the getters as a string Returns a
     * <code>String</code>
     *
     * @param aspect Aspect
     * @return String. * @throws InvocationTargetException
     * @throws IllegalAccessException
     * @since ICAP Version Exchange
     */

    public static String toFormattedXMLString(Aspect aspect) throws InvocationTargetException, IllegalAccessException {
        Hashtable<String, Object> attributeList = null;
        StringBuffer returnStringBuf = null;
        String returnString = null;
        String attributeName = null;

        if (null != (attributeList = getAllAttribueValues(aspect, true))) {
            returnStringBuf = new StringBuffer();

            String className = aspect.getClass().getName();
            final StringTokenizer spaceTokens = new StringTokenizer(className, ".");
            while (spaceTokens.hasMoreTokens()) {
                className = spaceTokens.nextToken();
            }

            returnStringBuf.append("<" + className + "> \n");

            final String index[] = new String[attributeList.size()];
            final Enumeration<String> enumAttr = attributeList.keys();
            int i = 0;

            while (enumAttr.hasMoreElements()) {
                index[i++] = enumAttr.nextElement();
            }

            CollectionUtilities.n2sort(index, true);

            for (final String element : index) {
                attributeName = element;
                // if we get a list do toXMLString on it

                if (attributeList.get(attributeName) instanceof ArrayList) {
                    @SuppressWarnings("unchecked") final List<Aspect> list = (List<Aspect>) attributeList.get(attributeName);
                    for (Aspect aspect1 : list) {
                        returnStringBuf.append(toFormattedXMLString(aspect1));
                    }
                } else if (attributeList.get(attributeName).getClass().getSuperclass().getName()
                        .equals(Aspect.class.getName())) {
                    final Aspect aspect1 = (Aspect) attributeList.get(attributeName);
                    returnStringBuf.append(toFormattedXMLString(aspect1));
                } else {
                    returnStringBuf.append("    <" + attributeName + ">" + attributeList.get(attributeName) + "</"
                            + attributeName + "> \n");
                }
            }
            returnStringBuf.append("</" + className + "> \n");
            returnString = returnStringBuf.toString();

        }

        return returnString;
    }

    /**
     * Format the return values from the getters as a string Returns a
     * <code>String</code>
     *
     * @param aspect Aspect
     * @return String. * @throws InvocationTargetException
     * @throws IllegalAccessException
     * @since ICAP Version Exchange
     */

    public static String toFormattedString(Aspect aspect) throws InvocationTargetException, IllegalAccessException {
        Hashtable<String, Object> attributeList = null;
        StringBuffer returnStringBuf = null;
        String returnString = null;
        String attributeName = null;

        if (null != (attributeList = getAllAttribueValues(aspect, true))) {
            returnStringBuf = new StringBuffer();

            final String index[] = new String[attributeList.size()];
            final Enumeration<String> enumAttr = attributeList.keys();
            int i = 0;

            while (enumAttr.hasMoreElements()) {
                index[i++] = enumAttr.nextElement();
            }

            CollectionUtilities.n2sort(index, true);

            for (final String element : index) {
                attributeName = element;
                // if we get a list do toString on it

                if (attributeList.get(attributeName) instanceof ArrayList) {
                    @SuppressWarnings("unchecked") final List<Aspect> list = (List<Aspect>) attributeList.get(attributeName);
                    for (Aspect aspect1 : list) {
                        returnStringBuf.append(toFormattedXMLString(aspect1));
                    }
                } else if (attributeList.get(attributeName).getClass().getSuperclass().getName()
                        .equals(Aspect.class.getName())) {
                    final Aspect aspect1 = (Aspect) attributeList.get(attributeName);
                    returnStringBuf.append(toFormattedXMLString(aspect1));
                } else {
                    returnStringBuf.append(attributeName + "=\'" + attributeList.get(attributeName) + "\'\n");
                }
            }

            returnString = returnStringBuf.toString();

        }

        return returnString;
    }

    /**
     * append to the input StringBuffer the string to format an SQL load line..
     *
     * @param strBuf          a <code>StringBuffer</code> that has the toAppend a
     *                        <code>Object</code> or attributeName a <code>String</code>
     *                        appended
     * @param attributeName   String
     * @param toAppend        Object
     * @param columnNamesOnly boolean
     * @see com.inpurchase.icap.aspect.Aspect
     * @since ICAP Version Exchange
     */

    public static void appendSQLString(StringBuffer strBuf, String attributeName, Object toAppend,
                                       boolean columnNamesOnly) {

        if (toAppend instanceof String) {
            if ("null".equals(toAppend)) {
                toAppend = null;
            } else {
                toAppend = ((String) toAppend).trim();
            }
        }
        if (toAppend instanceof Date) {
            final SimpleDateFormat newDateFormat = new SimpleDateFormat("dd-MMM-yy HH:mm:ss");
            toAppend = newDateFormat.format((Date) toAppend);

        }

        if (columnNamesOnly) {
            if (0 == strBuf.length()) {
                strBuf.append(attributeName);
            } else {
                strBuf.append("," + attributeName);
            }

        } else {

            if (null == toAppend) {
                if (0 != strBuf.length()) {
                    strBuf.append("||");
                }
            } else {

                if (0 == strBuf.length()) {
                    strBuf.append("\"" + toAppend + "\"");
                } else {
                    strBuf.append("||" + "\"" + toAppend + "\"");
                }
            }
        }
    }

    /**
     * Method setDocumentText.
     *
     * @param content String
     * @param append  boolean
     * @param newLine boolean
     * @param attrSet SimpleAttributeSet
     * @throws BadLocationException
     */
    public static void setDocumentText(Document doc, String content, boolean newLine, SimpleAttributeSet attrSet)
            throws BadLocationException {
        if (null != content) {
            doc.insertString(doc.getLength(), content, attrSet);
            if (newLine)
                doc.insertString(doc.getLength(), "\n", null);
        }
    }

    /**
     * Method padRight.
     *
     * @param text String
     * @param size int
     * @return String
     */
    public static String padRight(String text, int size) {
        return String.format("%1$-" + size + "s", text);
    }

    /**
     * Method padLeft.
     *
     * @param text String
     * @param size int
     * @return String
     */
    public static String padLeft(String text, int size) {
        return String.format("%1$" + size + "s", text);
    }

    /**
     * Method nullSafeObjectComparator.
     *
     * @param one T
     * @param two T
     * @return int
     */

    public static <T extends Comparable<T>> int nullSafeComparator(T one, T two) {
        if (one == null ^ two == null) {
            return (one == null) ? -1 : 1;
        }

        if (one == null && two == null) {
            return 0;
        }
        return one.compareTo(two);
    }

    /**
     * Method isBetween. Is c between a and b
     *
     * @param a <T extends Number>
     * @param b <T extends Number>
     * @param c <T extends Number>
     * @return boolean
     */

    public static <T extends Number> boolean isBetween(T a, T b, T c) {
        return b.doubleValue() > a.doubleValue()
                ? c.doubleValue() >= a.doubleValue() && c.doubleValue() <= b.doubleValue()
                : c.doubleValue() >= b.doubleValue() && c.doubleValue() <= a.doubleValue();
    }

    /**
     * Method isNumeric.
     *
     * @param str String to check
     * @return boolean
     */
    public static boolean isNumeric(String number) {
        if (null != number)
            return number.matches("-?\\d+(\\.\\d+)?");
        return false;
    }
}
