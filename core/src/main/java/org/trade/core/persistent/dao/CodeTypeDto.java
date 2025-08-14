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
package org.trade.core.persistent.dao;

import jakarta.persistence.Transient;
import org.trade.core.dao.Aspect;

import java.io.Serial;
import java.util.ArrayList;
import java.util.List;


/**
 *
 */

public class CodeTypeDto extends Aspect implements java.io.Serializable {

    @Serial
    private static final long serialVersionUID = 2273276207080568947L;

    private String name;
    private String type;
    private String description;
    private List<CodeAttributeDto> codeAttributes = new ArrayList<>(0);


    /**
     * Default constructor for CodeType.
     */

    public CodeTypeDto() {
    }

    /**
     * Constructor for CodeType.
     *
     * @param type String
     */
    public CodeTypeDto(String type) {
        this.type = type;
    }

    /**
     * Constructor for CodeType.
     *
     * @param name        String
     * @param description String
     */
    public CodeTypeDto(String name, String type, String description) {

        this.name = name;
        this.type = type;
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
     * Method getType.
     *
     * @return String
     */
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
     * Method getCodeAttribute.
     *
     * @return List<CodeAttributeDto>
     */
    public List<CodeAttributeDto> getCodeAttribute() {
        return this.codeAttributes;
    }

    /**
     * Method setCodeAttribute.
     *
     * @param codeAttributes List<CodeAttributeDto>
     */
    public void setCodeAttribute(List<CodeAttributeDto> codeAttributes) {
        this.codeAttributes = codeAttributes;
    }

    /**
     * Method isDirty.
     *
     * @return boolean
     */
    @Transient
    public boolean isDirty() {

        for (CodeAttributeDto item : this.getCodeAttribute()) {

            if (item.isDirty()) {
                return true;
            }
        }
        return super.isDirty();
    }
}
