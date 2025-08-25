
package org.trade.core.conversion;

/**
 * This object is responsible for attaching a format mask to an object to be
 * converted e.g. a Date with String in the format YYMM.
 *
 * @author Simon Allen
 */
public class JavaFormatForObject {
    //
    // Private Attributes
    //
    private final String m_format;

    private final Object m_forObject;

    //
    // Public Methods
    //

    /**
     * Constructor
     *
     * @param format    String
     * @param forObject Object
     */
    public JavaFormatForObject(String format, Object forObject) {
        m_format = format;
        m_forObject = forObject;
    }

    /**
     * @return The format mask for the object.
     */
    public String getFormat() {
        return (m_format);
    }

    /**
     * @return The object that the format object is for.
     */
    public Object getForObject() {
        return (m_forObject);
    }

    /**
     * Method toString.
     *
     * @return String
     */
    public String toString() {
        if (null == getForObject()) {
            return null;
        } else {
            return getForObject().toString();
        }
    }
}
