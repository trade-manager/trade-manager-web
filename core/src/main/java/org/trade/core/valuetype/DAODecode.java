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
package org.trade.core.valuetype;

import java.util.Vector;

/**
 * This class is suppose to represent a base class for a specialized CodeDecode
 * type object e.g. US State Codes and Descriptions.
 * <p>
 * Note : This object is not intended to be used directly.
 * <p>
 * This object will use a LookupService in order to obtain a ILookup containing
 * all of the Systems CODE_DECODE values for a specific Type.
 *
 * @author Simon Allen
 */
public class DAODecode extends Decode {

    private static final long serialVersionUID = -5356057478795774210L;

    private static final String DAO_DECODE_IDENTIFIER = "DAO_DECODE";

    /**
     * Default Constructor
     */
    public DAODecode() {
    }

    /**
     * Default Constructor
     *
     * @param codeDecodeType String
     * @param columnNames    Vector<String>
     * @param values         Vector<Object>
     */
    public DAODecode(String codeDecodeType, Vector<String> columnNames, Vector<Object> values) {
        super(codeDecodeType, columnNames, values, DAO_DECODE_IDENTIFIER);
    }

    /**
     * Default Constructor
     *
     * @param codeDecodeType String
     */
    public DAODecode(String codeDecodeType) {
        this(codeDecodeType, false);
    }

    /**
     * Default Constructor
     *
     * @param codeDecodeType String
     * @param optional       boolean
     */
    public DAODecode(String codeDecodeType, boolean optional) {
        super(codeDecodeType, DAO_DECODE_IDENTIFIER, optional);
    }

    /**
     * Default Constructor
     *
     * @param codeDecodeType String
     * @param identifier     String
     * @param optional       boolean
     */
    public DAODecode(String codeDecodeType, String identifier, boolean optional) {
        super(codeDecodeType, DAO_DECODE_IDENTIFIER, optional);
    }

    /**
     * Method convertToUppercase. override this method for value types that need
     * to distinguish upper case from lowercase
     *
     * @return boolean
     */
    protected boolean convertToUppercase() {
        return false;
    }

}
