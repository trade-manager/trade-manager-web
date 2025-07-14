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

import java.util.Enumeration;
import java.util.Iterator;

/**
 * This class is used internally to help with handling nesting of exceptions and
 * handling the associated messages.
 *
 * @author Simon Allen
 */
class Enumerator implements Iterator<Object> {

    private Iterator<?> mine;
    private Enumerator next = null;

    private Enumerator() {
    }

    /**
     * Constructor for Enumerator.
     *
     * @param enumeration Iterator<?>
     */
    Enumerator(Iterator<?> enumeration) {
        mine = enumeration;
    }

    /**
     * Method appendEnumeration.
     *
     * @param enumeration Iterator<?>
     */
    void appendEnumeration(Iterator<?> enumeration) {

        if (next == null) {

            next = new Enumerator(enumeration);
        } else {
            next.appendEnumeration(enumeration);
        }
    }

    /**
     * Method prependEnumeration.
     *
     * @param enumeration Enumeration<?>
     */
    void prependEnumeration(Iterator<?> enumeration) {

        Enumerator e = new Enumerator();
        e.mine = mine;
        e.next = next;
        next = e;
        mine = enumeration;
    }

    /**
     * Method next.
     *
     * @return Object
     * @see Enumeration#nextElement()
     */
    public Object next() {

        if (mine.hasNext()) {

            return mine.next();
        }

        if (next != null) {
            // Here we eliminate the next in the list
            mine = next.mine;
            next = next.next;

            // Recurse on this method
            return next();
        }
        return null;
    }

    /**
     * Method hasNext.
     *
     * @return boolean
     * @see Enumeration#hasMoreElements()
     */
    public boolean hasNext() {

        if (mine.hasNext()) {

            return true;
        }

        if (next != null) {

            return next.hasNext();
        }
        return false;
    }
}
