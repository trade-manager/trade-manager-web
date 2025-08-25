
package org.trade.core.lookup;

import java.util.Enumeration;
import java.util.Hashtable;

/**
 * Used to describe the access of dynamic key value pairs that can be used by a
 * ILookupServiceProvider to constrain the lookup returned.
 *
 * @author Simon Allen
 */
public class LookupQualifier {
    //
    // Private Attributes
    //
    private final Hashtable<String, Object> m_values = new Hashtable<>();

    //
    // Public Methods
    //

    /**
     * Default Constructor
     */
    public LookupQualifier() {
    }

    /**
     * @return An Enumeration of all of the qualifying keys.
     */
    public Enumeration<String> getKeys() {
        return (m_values.keys());
    }

    /**
     * @return An Enumeration of all of the qualifying values.
     */
    public Enumeration<Object> getValues() {
        return (m_values.elements());
    }

    /**
     * Get the specified value.
     *
     * @param key String
     * @return The value if it exists, null otherwise.
     */
    public Object getValue(String key) {
        return (m_values.get(key));
    }

    /**
     * Set the specified value.
     *
     * @param key   String
     * @param value Object
     */
    public void setValue(String key, Object value) {
        m_values.put(key, value);
    }

    /**
     * Remove the specified value from the qualifier.
     *
     * @param key String
     */
    public void removeValue(String key) {
        m_values.remove(key);
    }

    /**
     * Method toString.
     *
     * @return String
     */
    public String toString() {
        return m_values.toString();
    }

    /**
     * Method equals.
     *
     * @param objectToCompare Object
     * @return boolean
     */
    public boolean equals(Object objectToCompare) {
        if (this == objectToCompare) {
            return true;
        }
        if (objectToCompare == null) {
            return false;
        }
        if (!(objectToCompare instanceof LookupQualifier other)) {
            return false;
        }

        if (toString().equals(other.toString())) {
            return true;
        }

        return false;
    }

    /**
     * Method hashcode.
     *
     * @return int
     */
    public int hashcode() {
        return toString().hashCode();
    }
}
