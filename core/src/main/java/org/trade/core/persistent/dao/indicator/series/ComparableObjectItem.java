package org.trade.core.persistent.dao.indicator.series;

import java.io.Serializable;
import java.util.Objects;


public class ComparableObjectItem implements Cloneable, Comparable, Serializable {
    private static final long serialVersionUID = 2751513470325494890L;
    private Comparable x;
    private Object obj;

    public ComparableObjectItem(Comparable x, Object y) {
        //  Args.nullNotPermitted(x, "x");
        this.x = x;
        this.obj = y;
    }

    protected Comparable getComparable() {
        return this.x;
    }

    protected Object getObject() {
        return this.obj;
    }

    protected void setObject(Object y) {
        this.obj = y;
    }

    public int compareTo(Object o1) {

        if (o1 instanceof ComparableObjectItem) {

            ComparableObjectItem that = (ComparableObjectItem) o1;
            return this.x.compareTo(that.x);
        } else {
            int result = 1;
            return result;
        }
    }

    public Object clone() throws CloneNotSupportedException {
        return super.clone();
    }

    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        } else if (!(obj instanceof ComparableObjectItem)) {
            return false;
        } else {
            ComparableObjectItem that = (ComparableObjectItem) obj;
            if (!this.x.equals(that.x)) {
                return false;
            } else {
                return Objects.equals(this.obj, that.obj);
            }
        }
    }

    public int hashCode() {
        int result = this.x.hashCode();
        result = 29 * result + (this.obj != null ? this.obj.hashCode() : 0);
        return result;
    }
}

