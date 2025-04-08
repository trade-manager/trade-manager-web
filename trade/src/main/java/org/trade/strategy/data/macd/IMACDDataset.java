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
package org.trade.strategy.data.macd;

import org.jfree.data.xy.XYDataset;

/**
 * An interface that defines data for MACD
 *
 * @author Simon Allen
 * @version $Revision: 1.0 $
 */
public interface IMACDDataset extends XYDataset {

    /**
     * Returns the MACD for an item within a series.
     *
     * @param series the series index.
     * @param item   the item index.
     * @return The MACD.
     */
    public double getMACDValue(int series, int item);

    /**
     * Returns the MACD for an item within a series.
     *
     * @param series the series index.
     * @param item   the item index.
     * @return The MACD.
     */
    public Number getMACD(int series, int item);

}
