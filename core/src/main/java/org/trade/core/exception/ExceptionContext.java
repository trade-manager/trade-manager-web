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
package org.trade.core.exception;

import java.io.Serial;

/**
 * Describes the context in which an exception occurred. Contains the
 * information for constructing the message about a specific exception.
 *
 * @author Simon Allen
 */
public class ExceptionContext implements java.io.Serializable {
    /**
     *
     */
    @Serial
    private static final long serialVersionUID = 4837522639316151345L;

    private final static String NULL_VALUE = "";
    private final String parameterName;
    private String value;

    /**
     * Constructor.
     *
     * @param parameterName String
     * @param value         Object
     */
    public ExceptionContext(String parameterName, Object value) {

        this.parameterName = parameterName;
        setValue(value);
    }

    /**
     * Copy constructor.
     *
     * @param other ExceptionContext
     */
    public ExceptionContext(ExceptionContext other) {

        this.parameterName = other.parameterName;
        value = other.value;
    }

    /**
     * Copy constructor.
     *
     * @param other the object to be copied
     * @param value the new value for this context (overrides the value of the
     *              object being copied
     */
    public ExceptionContext(ExceptionContext other, Object value) {

        this.parameterName = other.parameterName;
        setValue(value);
    }

    /**
     * Represents the name used within exception messages to refer to this
     * context.
     *
     * @return String
     */
    public String getParameterName() {
        return this.parameterName;
    }

    /**
     * Represents the context of the exception. This value should be directly
     * substituted into a named parameter in an exception message.
     *
     * @return String
     */
    public String getValue() {
        return this.value;
    }

    /**
     * Method setValue.
     *
     * @param value Object
     */
    private void setValue(Object value) {

        if (null == value) {
            value = NULL_VALUE;
        }

        this.value = value.toString();
    }
}
