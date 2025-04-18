package org.trade.core.persistent.dao.indicator.series;

import java.io.ObjectStreamException;
import java.io.Serializable;

public final class DomainOrder implements Serializable {

    private static final long serialVersionUID = 4902774943512072627L;
    public static final DomainOrder NONE = new DomainOrder("DomainOrder.NONE");
    public static final DomainOrder ASCENDING = new DomainOrder("DomainOrder.ASCENDING");
    public static final DomainOrder DESCENDING = new DomainOrder("DomainOrder.DESCENDING");
    private String name;

    private DomainOrder(String name) {
        this.name = name;
    }

    public String toString() {
        return this.name;
    }

    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        } else if (!(obj instanceof DomainOrder)) {
            return false;
        } else {
            DomainOrder that = (DomainOrder) obj;
            return this.name.equals(that.toString());
        }
    }

    public int hashCode() {
        return this.name.hashCode();
    }

    private Object readResolve() throws ObjectStreamException {
        if (this.equals(ASCENDING)) {
            return ASCENDING;
        } else if (this.equals(DESCENDING)) {
            return DESCENDING;
        } else {
            return this.equals(NONE) ? NONE : null;
        }
    }
}
