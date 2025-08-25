package org.trade.core.persistent.dao;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import org.trade.core.dao.Aspect;

import java.io.Serial;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Simon Allen
 * @version $Revision: 1.0 $
 */
@Entity
@Table(name = "codeattribute")
public class CodeAttribute extends Aspect implements java.io.Serializable {

    @Serial
    private static final long serialVersionUID = 2273276207080568947L;

    @Column(name = "name", nullable = false, length = 45)
    private String name;

    @Column(name = "description", length = 100)
    private String description;

    @Column(name = "default_value", length = 45)
    private String defaultValue;

    @Column(name = "class_name", nullable = false, length = 100)
    private String className;

    @Column(name = "class_editor_name", length = 100)
    private String classEditorName;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "code_type_id", nullable = false)
    private CodeType codeType;

    @OneToMany(mappedBy = "codeAttribute", fetch = FetchType.LAZY)
    private List<CodeValue> codeValues = new ArrayList<>(0);

    public CodeAttribute() {
    }

    /**
     * Constructor for CodeAttribute.
     *
     * @param codeType        CodeType
     * @param name            String
     * @param description     String
     * @param defaultValue    String
     * @param className       String
     * @param classEditorName String
     */
    public CodeAttribute(CodeType codeType, String name, String description, String defaultValue, String className,
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
     * @return CodeType
     */

    public CodeType getCodeType() {
        return this.codeType;
    }

    /**
     * Method setCodeType.
     *
     * @param codeType CodeType
     */
    public void setCodeType(CodeType codeType) {
        this.codeType = codeType;
    }

    /**
     * Method getCodeValue.
     *
     * @return List<CodeValue>
     */
    public List<CodeValue> getCodeValue() {
        return this.codeValues;
    }

    /**
     * Method setCodeValue.
     *
     * @param codeValues List<CodeValue>
     */
    public void setCodeValue(List<CodeValue> codeValues) {
        this.codeValues = codeValues;
    }
}
