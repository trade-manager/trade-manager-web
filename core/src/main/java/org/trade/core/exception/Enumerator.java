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
     * @param iterator Iterator<?>
     */
    Enumerator(Iterator<?> iterator) {
        mine = iterator;
    }

    /**
     * Method appendEnumeration.
     *
     * @param iterator Iterator<?>
     */
    void appendEnumeration(Iterator<?> iterator) {

        if (next == null) {

            next = new Enumerator(iterator);
        } else {
            next.appendEnumeration(iterator);
        }
    }

    /**
     * Method prependEnumeration.
     *
     * @param iterator Iterator<?>
     */
    void prependEnumeration(Iterator<?> iterator) {

        Enumerator e = new Enumerator();
        e.mine = mine;
        e.next = next;
        next = e;
        mine = iterator;
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
