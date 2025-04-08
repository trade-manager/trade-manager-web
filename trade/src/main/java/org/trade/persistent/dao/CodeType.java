/* ===========================================================
 * TradeManager : a application to trade strategies for the Java(tm) platform
 * ===========================================================
 *
 * (C) Copyright 2011-2011, by Simon Allen and Contributors.
 *
 * Project Info:  org.trade
 *
 * This library is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation; either version 2.1 of the License, or
 * (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY
 * or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public
 * License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301,
 * USA.
 *
 * [Java is a trademark or registered trademark of Oracle, Inc.
 * in the United States and other countries.]
 *
 * (C) Copyright 2011-2011, by Simon Allen and Contributors.
 *
 * Original Author:  Simon Allen;
 * Contributor(s):   -;
 *
 * Changes
 * -------
 *
 */
package org.trade.persistent.dao;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.DiscriminatorColumn;
import jakarta.persistence.DiscriminatorType;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Inheritance;
import jakarta.persistence.InheritanceType;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;
import jakarta.persistence.Version;
import jakarta.validation.constraints.NotNull;
import org.trade.core.dao.Aspect;

import java.util.ArrayList;
import java.util.List;

import static jakarta.persistence.GenerationType.IDENTITY;


/**
 *
 */
@Entity
@Table(name = "codetype")
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "type", discriminatorType = DiscriminatorType.STRING)
@DiscriminatorValue("CodeType")
public class CodeType extends Aspect implements java.io.Serializable {

    private static final long serialVersionUID = 2273276207080568947L;

    @NotNull
    private String name;
    @NotNull
    private String type;
    private String description;
    private List<CodeAttribute> codeAttributes = new ArrayList<CodeAttribute>(0);

    public static final String IndicatorParameters = "IndicatorParameters";
    public static final String StrategyParameters = "StrategyParameters";

    /**
     * Default constructor for CodeType.
     */

    public CodeType() {
    }

    /**
     * Constructor for CodeType.
     *
     * @param type String
     */
    public CodeType(String type) {
        this.type = type;
    }

    /**
     * Constructor for CodeType.
     *
     * @param name        String
     * @param description String
     */
    public CodeType(String name, String type, String description) {
        this.name = name;
        this.type = type;
        this.description = description;
    }

    /**
     * Method getId.
     *
     * @return Integer
     */
    @Id
    @GeneratedValue(strategy = IDENTITY)
    @Column(name = "id", unique = true, nullable = false)
    public Integer getId() {
        return this.id;
    }

    /**
     * Method setId.
     *
     * @param id Integer
     */
    public void setId(Integer id) {
        this.id = id;
    }

    /**
     * Method getName.
     *
     * @return String
     */
    @Column(name = "name", nullable = false, length = 45)
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
     * Method getType.
     *
     * @return String
     */
    @Column(name = "type", length = 45, insertable = false, updatable = false, unique = true, nullable = false)
    public String getType() {
        return this.type;
    }

    /**
     * Method setType.
     *
     * @param type String
     */
    public void setType(String type) {
        this.type = type;
    }

    /**
     * Method getDescription.
     *
     * @return String
     */
    @Column(name = "description", nullable = false, length = 100)
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
     * Method getVersion.
     *
     * @return Integer
     */
    @Version
    @Column(name = "version")
    public Integer getVersion() {
        return this.version;
    }

    /**
     * Method setVersion.
     *
     * @param version Integer
     */
    public void setVersion(Integer version) {
        this.version = version;
    }

    /**
     * Method getCodeAttribute.
     *
     * @return List<CodeAttribute>
     */
    @OneToMany(mappedBy = "codeType", fetch = FetchType.EAGER, orphanRemoval = true, cascade = {CascadeType.ALL})
    public List<CodeAttribute> getCodeAttribute() {
        return this.codeAttributes;
    }

    /**
     * Method setCodeAttribute.
     *
     * @param codeAttributes List<CodeAttribute>
     */
    public void setCodeAttribute(List<CodeAttribute> codeAttributes) {
        this.codeAttributes = codeAttributes;
    }

    /**
     * Method isDirty.
     *
     * @return boolean
     */
    @Transient
    public boolean isDirty() {
        for (CodeAttribute item : this.getCodeAttribute()) {
            if (item.isDirty())
                return true;
        }
        return super.isDirty();
    }
}
