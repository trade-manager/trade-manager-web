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
package org.trade.core.properties;

import java.io.Serial;
import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;

/**
 * Exception thrown by PropertyUtils class.
 *
 * @author : Simon Allen
 */
public class MissingPropertiesException extends Exception {
    /**
     *
     */
    @Serial
    private static final long serialVersionUID = -5377864368236421685L;
    private List<String> m_missingProperties = null;

    public MissingPropertiesException() {
        super();
    }

    /**
     * Method addProperty.
     *
     * @param p String
     */
    public void addProperty(String p) {
        if (m_missingProperties == null) {
            m_missingProperties = new ArrayList<>();
        }

        m_missingProperties.add(p);
    }

    /**
     * Method getMessage.
     *
     * @return String
     */
    public String getMessage() {
        StringBuilder message = new StringBuilder("The following properties are missing: ");
        ListIterator<String> missingProperties = getMissingProperties();

        if (null == missingProperties) {

            message.append("No properties missing!");
        } else {

            boolean first = true;

            while (missingProperties.hasNext()) {

                if (first) {

                    first = false;
                } else {

                    message.append(", ");
                }

                message.append(missingProperties.next());
            }
        }

        return message.toString();
    }

    /**
     * Method getMissingProperties.
     *
     * @return ListIterator<String>
     */
    public ListIterator<String> getMissingProperties() {

        if (m_missingProperties == null) {

            m_missingProperties = new ArrayList<>();
        }

        return m_missingProperties.listIterator();
    }

    /**
     * Method toString.
     *
     * @return String
     */
    public String toString() {

        if (m_missingProperties == null) {

            return "No properties missing";
        }

        StringBuilder sb = new StringBuilder("The following [");
        sb.append(m_missingProperties.size());
        sb.append("] properties are missing: ");
        int missingPropSize = m_missingProperties.size();

        for (int ii = 0; ii < missingPropSize; ii++) {

            sb.append(m_missingProperties.get(ii));
            sb.append(", ");
        }

        sb.append('.');
        return sb.toString();
    }
}
