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
package org.trade.core.conversion;

/**
 * This is an abstract class which is inherited by all java.lang.String to
 * java.lang.Object subclass converter classes.
 *
 * @author Simon Allen
 */
public abstract class StringToObjectConverter implements IJavaTypeConverter {
    /**
     * Default constructor.
     */
    public StringToObjectConverter() {
    }

    //
    // IJavaTypeConverter interface methods
    //

    /**
     * This method is used by the JavaTypeTranslator to convert a source object
     * of type java.lang.String to an instance of a subclass of type
     * java.lang.Object.
     * <p>
     * Subclasses must implement the getConvertedString() method for it to work
     * properly.
     *
     * @param valueToConvert the java.lang.String value to convert
     * @return Object the converted value * @exception IllegalArgumentException
     * thrown if the valueToConvert is not of type java.lang.String or
     * if the valueToConvert is not in the correct format to do the
     * conversion * @see
     * org.trade.core.conversion.IJavaTypeConverter#convert(Object)
     */
    public Object convert(Object valueToConvert) throws IllegalArgumentException {
        if (valueToConvert instanceof String) {
            return getConvertedString(((String) valueToConvert).trim());
        }

        throw new IllegalArgumentException("The source object must be of type: " + getSourceType().getName());
    }

    /**
     * This method returns the source type or class that the converter conerts
     * from. In this case java.lang.String .
     *
     * @return Class the class of the source value which will be converted
     * * @see
     * org.trade.core.conversion.IJavaTypeConverter#getSourceType()
     */
    public Class<?> getSourceType() {
        return String.class;
    }

    //
    // Methods which need to be overridden
    //

    /**
     * This method should be implemented by a subclass such that it returns the
     * converted value of the String.
     *
     * @param aString the String to be converted
     * @return Object the converted String * @exception IllegalArgumentException
     * thrown if the String to convert is not in the correct format to
     * do the conversion
     */
    protected abstract Object getConvertedString(String aString) throws IllegalArgumentException;
}
