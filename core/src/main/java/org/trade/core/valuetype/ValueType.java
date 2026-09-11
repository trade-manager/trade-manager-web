package org.trade.core.valuetype;

import org.trade.core.persistent.codetype.DecodeType;

import java.io.Serial;

/**
 * @author Simon Allen
 * @version $Revision: 1.0 $
 */
public abstract class ValueType extends DecodeType {
    /**
     *
     */
    @Serial
    private static final long serialVersionUID = -8054819773979644420L;
    public static final String NONE = " ";

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

}
