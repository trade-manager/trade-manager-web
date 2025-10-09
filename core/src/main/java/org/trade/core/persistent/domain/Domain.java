package org.trade.core.persistent.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import org.trade.core.aspect.Aspect;

import java.io.Serial;
import java.io.Serializable;

/**
 * @author Simon Allen
 * @version $Revision: 1.0 $
 */
@Entity
@Table(name = "domain")
public class Domain extends Aspect implements Serializable, Cloneable {

    @Serial
    private static final long serialVersionUID = 5691902477608387034L;

    public static final String GLOBAL = "global";

    @Column(name = "name")
    private String name;

    @Column(name = "description")
    private String description;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "parent_id")
    private Domain domain;

    private Domain() {
    }

    public Domain(String name, String description) {
        this.name = name;
        this.description = description;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return this.description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * Method getParent.
     *
     * @return Domain
     */
    public Domain getParent() {
        return this.domain;
    }

    /**
     * Method setParent.
     *
     * @param domain Domain
     */
    public void setParent(Domain domain) {
        this.domain = domain;
    }

    /**
     * Method hasParent.
     *
     * @return boolean
     */
    public boolean hasParent() {
        return null != getParent();
    }

    /**
     * Method clone.
     *
     * @return Object
     */
    public Object clone() throws CloneNotSupportedException {

        return super.clone();
    }
}
