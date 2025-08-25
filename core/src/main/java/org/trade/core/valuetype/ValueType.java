package org.trade.core.valuetype;

import java.io.Serial;
import java.io.Serializable;

/**
 *
 */
public abstract class ValueType implements Cloneable, Serializable {
    /**
     *
     */
    @Serial
    private static final long serialVersionUID = -8054819773979644420L;

    // All well-behaved ValueTypes must implement this

    /**
     * Method isEmpty.
     *
     * @return boolean
     */
    public abstract boolean isEmpty();

    /**
     * The default implementation of this method calls the ValueTypes toString()
     * object. If a different type of object needs to be returned this method
     * should have be overridden by the specific subclass.
     *
     * @return An SQL representation of the object so that it can be stored via
     * JDBC.
     */
    public Object getSQLObject() {
        return (toString());
    }

    /**
     * Method getSQLObjectType.
     *
     * @return Class<?>
     */
    public Class<?> getSQLObjectType() {
        return String.class;
    }

    public Object clone() throws CloneNotSupportedException {
        return (super.clone());
    }
}
