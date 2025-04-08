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
package org.trade.dictionary.valuetype;

import org.trade.core.valuetype.Decode;

import java.io.Serial;
import java.util.Hashtable;

/**
 *
 */
public class BarSize extends Decode {

    @Serial
    private static final long serialVersionUID = -5381026427696898592L;
    public static final String DECODE = "BAR_SIZE";
    public static final Integer FIVE_MIN = 300;
    public static final Integer HOUR_MIN = 3600;
    public static final Integer DAY = 1;

    // TODO: Fix value types to have a value
    private static final Hashtable<String, Integer> seconds = new Hashtable<>();

    public BarSize() {
        super(DECODE, true);

        seconds.put("_1_min", 60);
        seconds.put("_2_mins", 120);
        seconds.put("_5_mins", 300);
        seconds.put("_10_mins", 600);
        seconds.put("_15_mins", 900);
        seconds.put("_20_mins", 1200);
        seconds.put("_30_mins", 1800);
        seconds.put("_1_hour", 3600);
        seconds.put("_1_day", 86400);
        seconds.put("_30_secs", 30);
    }

    /**
     * Method newInstance.
     *
     * @param value Integer
     * @return BarSize
     */
    public static BarSize newInstance(Integer value) {
        final BarSize returnInstance = new BarSize();
        if (value > 3600)
            value = 1;
        returnInstance.setValue(value);
        return returnInstance;
    }

    /**
     * Method newInstance.
     *
     * @return BarSize
     */
    public static BarSize newInstance() {
        final BarSize returnInstance = new BarSize();
        returnInstance.setDefaultCode();
        return returnInstance;
    }

    /**
     * @param barSize
     * @return
     */
    public static int getSeconds(String barSize) {

        return seconds.get(barSize);
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