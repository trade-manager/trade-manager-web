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
package org.trade.core.persistent.dao.series.indicator.bollingerbands;

// Generated Feb 21, 2011 12:43:33 PM by Hibernate Tools 3.4.0.CR1

import java.io.Serial;
import java.io.Serializable;
import java.math.BigDecimal;

/**
 * BollingerBands
 *
 * @author Simon Allen
 * @version $Revision: 1.0 $
 */

public class BollingerBands implements Serializable {

    /**
     *
     */
    @Serial
    private static final long serialVersionUID = 7644763985378994305L;

    private BigDecimal bollingerBands;

    public BollingerBands() {
    }

    /**
     * Constructor for BollingerBands.
     *
     * @param bollingerBands BigDecimal
     */
    public BollingerBands(BigDecimal bollingerBands) {
        this.bollingerBands = bollingerBands;
    }

    /**
     * Method getBollingerBands.
     *
     * @return BigDecimal
     */
    public BigDecimal getBollingerBands() {
        return this.bollingerBands;
    }

    /**
     * Method setBollingerBands.
     *
     * @param bollingerBands BigDecimal
     */
    public void setBollingerBands(BigDecimal bollingerBands) {
        this.bollingerBands = bollingerBands;
    }

}
