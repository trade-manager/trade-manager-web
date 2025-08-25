package org.trade.core.persistent.dao;

import org.trade.core.dao.Aspect;

import java.io.Serial;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Simon Allen
 * @version $Revision: 1.0 $
 */

public class CodeAttributeDto extends Aspect implements java.io.Serializable {

    @Serial
    private static final long serialVersionUID = 2273276207080568947L;

    private String name;
    private String description;
    private String defaultValue;
    private String className;
    private String classEditorName;
    private CodeTypeDto codeType;
    private List<CodeValueDto> codeValues = new ArrayList<>(0);

    public CodeAttributeDto() {
    }

    /**
     * Constructor for CodeAttribute.
     *
     * @param codeType        CodeTypeDto
     * @param name            String
     * @param description     String
     * @param defaultValue    String
     * @param className       String
     * @param classEditorName String
     */
    public CodeAttributeDto(CodeTypeDto codeType, String name, String description, String defaultValue, String className,
                            String classEditorName) {

        this.name = name;
        this.description = description;
        this.defaultValue = defaultValue;
        this.className = className;
        this.classEditorName = classEditorName;
        this.codeType = codeType;
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
     * Method getDefaultValue.
     *
     * @return String
     */
    public String getDefaultValue() {
        return this.defaultValue;
    }

    /**
     * Method setDefaultValue.
     *
     * @param defaultValue String
     */
    public void setDefaultValue(String defaultValue) {
        this.defaultValue = defaultValue;
    }

    /**
     * Method getClassName.
     *
     * @return String
     */
    public String getClassName() {
        return this.className;
    }

    /**
     * Method setClassName.
     *
     * @param className String
     */
    public void setClassName(String className) {
        this.className = className;
    }

    /**
     * Method getEditorClassName.
     *
     * @return String
     */
    public String getEditorClassName() {
        return this.classEditorName;
    }

    /**
     * Method setEditorClassName.
     *
     * @param classEditorName String
     */
    public void setEditorClassName(String classEditorName) {
        this.classEditorName = classEditorName;
    }

    /**
     * Method getCodeType.
     *
     * @return CodeTypeDto
     */

    public CodeTypeDto getCodeType() {
        return this.codeType;
    }

    /**
     * Method setCodeType.
     *
     * @param codeType CodeTypeDto
     */
    public void setCodeType(CodeTypeDto codeType) {
        this.codeType = codeType;
    }

    /**
     * Method getCodeValue.
     *
     * @return List<CodeValueDto>
     */
    public List<CodeValueDto> getCodeValue() {
        return this.codeValues;
    }

    /**
     * Method setCodeValue.
     *
     * @param codeValues List<CodeValueDto>
     */
    public void setCodeValue(List<CodeValueDto> codeValues) {
        this.codeValues = codeValues;
    }
}
