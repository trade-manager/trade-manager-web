package org.trade.core.persistent.dao;

import jakarta.persistence.Transient;
import org.trade.core.dao.Aspect;

import java.io.Serial;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Simon Allen
 * @version $Revision: 1.0 $
 */

public class CodeTypeDTO extends Aspect implements java.io.Serializable {

    @Serial
    private static final long serialVersionUID = 2273276207080568947L;

    private String name;
    private String type;
    private String description;
    private List<CodeAttributeDTO> codeAttributes = new ArrayList<>(0);


    /**
     * Default constructor for CodeType.
     */

    public CodeTypeDTO() {
    }

    /**
     * Constructor for CodeType.
     *
     * @param type String
     */
    public CodeTypeDTO(String type) {
        this.type = type;
    }

    /**
     * Constructor for CodeType.
     *
     * @param name        String
     * @param description String
     */
    public CodeTypeDTO(String name, String type, String description) {

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
    public List<CodeAttributeDTO> getCodeAttribute() {
        return this.codeAttributes;
    }

    /**
     * Method setCodeAttribute.
     *
     * @param codeAttributes List<CodeAttributeDto>
     */
    public void setCodeAttribute(List<CodeAttributeDTO> codeAttributes) {
        this.codeAttributes = codeAttributes;
    }

    /**
     * Method isDirty.
     *
     * @return boolean
     */
    @Transient
    public boolean isDirty() {

        for (CodeAttributeDTO item : this.getCodeAttribute()) {

            if (item.isDirty()) {
                return true;
            }
        }
        return super.isDirty();
    }
}
