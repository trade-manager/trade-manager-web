package org.trade.core.persistent.domain;

import org.trade.core.dao.Aspect;

import java.io.Serial;
import java.io.Serializable;

/**
 * @author Simon Allen
 * @version $Revision: 1.0 $
 */

public class DomainDTO extends Aspect implements Serializable, Cloneable {

    @Serial
    private static final long serialVersionUID = 5691902477608387034L;

    private String name;
    private String description;
    private DomainDTO domain;

    private DomainDTO() {
    }

    public DomainDTO(String name, String description) {
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
    public DomainDTO getParent() {
        return this.domain;
    }

    /**
     * Method setParent.
     *
     * @param domain Domain
     */
    public void setParent(DomainDTO domain) {
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
