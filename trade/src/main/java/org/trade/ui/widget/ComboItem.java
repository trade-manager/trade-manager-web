package org.trade.ui.widget;

public class ComboItem {

    private final Object value;
    private final String label;

    public ComboItem(Object value, String label) {
        this.value = value;
        this.label = label;
    }

    /**
     * Method getValue
     *
     * @return Object
     */
    public Object getValue() {
        return this.value;
    }

    /**
     * Method getLabel
     *
     * @return String
     */
    public String getLabel() {
        return this.label;
    }

    /**
     * Method toString
     *
     * @return String
     */
    public String toString() {

        if (null == this.label) {
            return this.value.toString();
        }
        return this.label;
    }
}
