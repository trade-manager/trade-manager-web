package org.trade.core.persistent.dao.indicator.series;

import java.io.Serial;
import java.io.Serializable;
import java.util.Objects;


public class DatasetGroup implements Cloneable, Serializable {
    @Serial
    private static final long serialVersionUID = -3640642179674185688L;
    private final String id;

    public DatasetGroup() {
        this.id = "NOID";
    }

    public DatasetGroup(String id) {
        //Args.nullNotPermitted(id, "id");
        this.id = id;
    }

    public String getID() {
        return this.id;
    }

    public Object clone() throws CloneNotSupportedException {
        return super.clone();
    }

    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        } else if (!(obj instanceof DatasetGroup that)) {
            return false;
        } else {
            return Objects.equals(this.id, that.id);
        }
    }

    public int hashCode() {
        int hash = 3;
        hash = 41 * hash + Objects.hashCode(this.id);
        return hash;
    }
}
