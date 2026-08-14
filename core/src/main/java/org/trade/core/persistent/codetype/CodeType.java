package org.trade.core.persistent.codetype;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.DiscriminatorColumn;
import jakarta.persistence.DiscriminatorType;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Inheritance;
import jakarta.persistence.InheritanceType;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;
import org.trade.core.aspect.Aspect;

import java.io.Serial;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Simon Allen
 * @version $Revision: 1.0 $
 */
@Entity
@Table(name = "codetype")
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "type", discriminatorType = DiscriminatorType.STRING)
@DiscriminatorValue("Decode")
public class CodeType extends Aspect implements java.io.Serializable {

    @Serial
    private static final long serialVersionUID = 2273276207080568947L;

    @Column(name = "name", nullable = false, length = 45)
    private String name;

    @Column(name = "type", length = 45, insertable = false, updatable = false, unique = true, nullable = false)
    private String type;

    @Column(name = "category", nullable = false, length = 45)
    private String category;

    @Column(name = "description", nullable = false, length = 100)
    private String description;

    @OneToMany(mappedBy = "codeType", fetch = FetchType.EAGER, orphanRemoval = true, cascade = {CascadeType.ALL})
    private List<CodeAttribute> codeAttributes = new ArrayList<>(0);

    public static final String IndicatorParameters = "IndicatorParameters";
    public static final String StrategyParameters = "StrategyParameters";
    public static final String Decode = "Decode";

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
     * @param category    String
     * @param name        String
     * @param description String
     */
    public CodeType(String category, String name, String description) {

        this.name = name;
        this.category = category;
        this.description = description;
    }

    /**
     * Constructor for CodeType.
     *
     * @param name        String
     * @param description String
     */
    public CodeType(String type, String category, String name, String description) {

        this.type = type;
        this(category, name, description);
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
     * Method getcategory
     *
     * @return String
     */
    public String getCategory() {
        return this.category;
    }

    /**
     * Method setcategory.
     *
     * @param category String
     */
    public void setCategory(String category) {
        this.category = category;
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
     * Method getCodeAttributes.
     *
     * @return List<CodeAttribute>
     */
    public List<CodeAttribute> getCodeAttributes() {
        return this.codeAttributes;
    }

    /**
     * Method setCodeAttributes.
     *
     * @param codeAttributes List<CodeAttribute>
     */
    public void setCodeAttributes(List<CodeAttribute> codeAttributes) {

        this.codeAttributes = codeAttributes;
    }

    /**
     *
     * @param codeAttribute CodeAttribute
     */
    public CodeAttribute addChild(CodeAttribute codeAttribute) {

        for (CodeAttribute codeAttributeExist : this.codeAttributes) {

            if (this.getType().equals(codeAttribute.getCodeType().getType()) && this.getCategory().equals(codeAttribute.getCodeType().getCategory()) &&
                    codeAttributeExist.getName().equals(codeAttribute.getName())) {

                return codeAttributeExist;
            }
        }

        this.codeAttributes.add(codeAttribute);
        codeAttribute.setCodeType(this);
        return codeAttribute;
    }

    /**
     * Method isDirty.
     *
     * @return boolean
     */
    @Transient
    public boolean isDirty() {

        for (CodeAttribute item : this.getCodeAttributes()) {

            if (item.isDirty()) {
                return true;
            }
        }
        return super.isDirty();
    }
}
