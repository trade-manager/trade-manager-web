package org.trade.core.util;

import java.util.Comparator;

/**
 * @author Simon Allen
 */
public final class Pair {

    public double x;
    public double y;
    public static final Comparator<Pair> X_VALUE_ASC = (o1, o2) -> CoreUtils.nullSafeComparator(o1.x, o2.x);

    /**
     * Constructor for Pair.
     *
     * @param x double
     * @param y double
     */
    public Pair(double x, double y) {

        this.x = x;
        this.y = y;
    }

    /**
     * Method toString.
     *
     * @return String
     */
    public String toString() {

        return x + "," + y;
    }
}
