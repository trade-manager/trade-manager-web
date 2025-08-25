package org.trade.core.properties;

/**
 * This class provides a simple container for storing object arrays.
 *
 * @author Simon Allen
 */
public class ArrayOfValues {

    private final Object[] array;

    /**
     * Constructor for ArrayOfValues.
     *
     * @param values Object[]
     */
    public ArrayOfValues(Object[] values) {
        array = values;
    }

    /**
     * Method getValues.
     *
     * @return Object[]
     */
    public Object[] getValues() {
        return (array);
    }
}
