package org.trade.core.persistent.dao;

import org.trade.core.dao.Aspect;

import java.io.Serial;
import java.io.Serializable;

/**
 * @author Simon Allen
 * @version $Revision: 1.0 $
 */
public class PortfolioDTO extends Aspect implements Serializable, Cloneable {

    @Serial
    private static final long serialVersionUID = 2273276207080568947L;

    private String name;
    private String alias;
    private String allocationMethod;
    private String description;
    private Boolean isDefault = false;

    public PortfolioDTO() {

    }

    /**
     * Constructor for Portfolio.
     *
     * @param name        String
     * @param description String
     */
    public PortfolioDTO(String name, String description) {

        this.name = name;
        this.description = description;
    }

    /**
     * Method getName.
     *
     * @return String
     */
    public String getName() {
        return this.name;
    }

    /**
     * Method setName.
     *
     * @param name String
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Method getAlias.
     *
     * @return String
     */
    public String getAlias() {
        return this.alias;
    }

    /**
     * Method setAlias.
     *
     * @param alias String
     */
    public void setAlias(String alias) {
        this.alias = alias;
    }

    /**
     * Method getAllocationMethod.
     *
     * @return String
     */
    public String getAllocationMethod() {
        return this.allocationMethod;
    }

    /**
     * Method setAllocationMethod.
     *
     * @param allocationMethod String
     */
    public void setAllocationMethod(String allocationMethod) {
        this.allocationMethod = allocationMethod;
    }

    /**
     * Method getDescription.
     *
     * @return String
     */
    public String getDescription() {
        return this.description;
    }

    /**
     * Method setDescription.
     *
     * @param description String
     */
    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * Method getIsDefault.
     *
     * @return Boolean
     */
    public Boolean getIsDefault() {
        return this.isDefault;
    }

    /**
     * Method setIsDefault.
     *
     * @param isDefault Boolean
     */
    public void setIsDefault(Boolean isDefault) {
        this.isDefault = isDefault;
    }

    /**
     * Method hashCode.
     * <p>
     * For every field tested in the equals-Method, calculate a hash code c by:
     * <p>
     * If the field f is a boolean: calculate * (f ? 0 : 1);
     * <p>
     * If the field f is a byte, char, short or int: calculate (int)f;
     * <p>
     * If the field f is a long: calculate (int)(f ^ (f >>> 32));
     * <p>
     * If the field f is a float: calculate Float.floatToIntBits(f);
     * <p>
     * If the field f is a double: calculate Double.doubleToLongBits(f) and
     * handle the return value like every long value;
     * <p>
     * If the field f is an object: Use the result of the hashCode() method or 0
     * if f == null;
     * <p>
     * If the field f is an array: See every field as separate element and
     * calculate the hash value in a recursive fashion and combine the values as
     * described next.
     *
     * @return int
     */
    public int hashCode() {
        int hash = super.hashCode();
        hash = hash + (this.getName() == null ? 0 : this.getName().hashCode());
        return hash;
    }

    /**
     * Method clone.
     *
     * @return Object
     */
    public Object clone() throws CloneNotSupportedException {

        PortfolioDTO portfolio = (PortfolioDTO) super.clone();
        return portfolio;
    }
}
