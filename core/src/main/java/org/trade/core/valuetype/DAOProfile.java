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

import org.trade.core.persistent.dao.Portfolio;

import java.io.Serial;
import java.util.Vector;

/**
 *
 */
public class DAOProfile extends DAODecode {

    @Serial
    private static final long serialVersionUID = -5381026427696898592L;
    public static final String DECODE = "PROFILE_DATA";
    public static final String _TABLE = "_TABLE";
    public static final String _TABLE_ID = "_TABLE_ID";
    public static final String _COLUMN = "_COLUMN";

    public DAOProfile() {
        super(DECODE, true);
    }

    /**
     * Method getCodesDecodes.
     *
     * @return Vector<Decode>
     */

    public Vector<Decode> getCodesDecodes() throws ValueTypeException {
        final Vector<Decode> decodes = new Vector<>();
        final Vector<Decode> decodesAll = super.getCodesDecodes();
        for (final Decode decode : decodesAll) {
            final Portfolio portfolio = (Portfolio) decode.getObject();
            if (null != portfolio.getAllocationMethod()) {
                Integer value = null;
                try {
                    value = Integer.parseInt(portfolio.getAllocationMethod());
                } catch (NumberFormatException ex) {
                    // Do nothing
                }
                if (null != value) {
                    decodes.add(decode);
                }
            } else {
                if (Decode.NONE.equals(decode.getDisplayName())) {
                    decodes.add(decode);
                }
            }
        }
        return decodes;
    }

    /**
     * Method newInstance.
     *
     * @param displayName String
     * @return DAOTradeAccount
     */
    public static DAOProfile newInstance(String displayName) {
        final DAOProfile returnInstance = new DAOProfile();
        returnInstance.setDisplayName(displayName);
        return returnInstance;
    }

    /**
     * Method newInstance.
     *
     * @return DAOProfile
     */
    public static DAOProfile newInstance() {
        final DAOProfile returnInstance = new DAOProfile();
        returnInstance.setDefaultCode();
        return returnInstance;
    }

    /**
     * Method convertToUppercase.
     *
     * @return boolean
     */
    protected boolean convertToUppercase() {
        return false;
    }
}