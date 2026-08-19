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
@Table(name = "decode")
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "type", discriminatorType = DiscriminatorType.STRING)
@DiscriminatorValue("Decode")
public class Decode extends Aspect implements java.io.Serializable {

    @Serial
    private static final long serialVersionUID = 2273276207080568947L;

    @Column(name = "type", length = 45, insertable = false, updatable = false, unique = true, nullable = false)
    private String type;

    @Column(name = "description", nullable = false, length = 100)
    private String description;

    @OneToMany(mappedBy = "decode", fetch = FetchType.EAGER, orphanRemoval = true, cascade = {CascadeType.ALL})
    private List<CodeValue> codeValues = new ArrayList<>(0);

    public static final String Decode = "Decode";

    /**
     * Default constructor for CodeType.
     */

    public Decode() {
    }

    /**
     * Constructor for Decode.
     *
     * @param type String
     */
    public Decode(String type) {
        this.type = type;
    }

    /**
     * Constructor for Decode.
     *
     * @param type        String
     * @param description String
     */
    public Decode(String type, String description) {

        this.type = type;
        this.description = description;
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
     * Method getCodeValues.
     *
     * @return List<CodeValue>
     */
    public List<CodeValue> getCodeValues() {
        return this.codeValues;
    }

    /**
     * Method setCodeValues.
     *
     * @param codeCodeValues List<CodeCodeValue>
     */
    public void setCodeValues(List<CodeValue> codeCodeValues) {

        this.codeValues = codeCodeValues;
    }

    /**
     *
     * @param codeValue CodeValue
     */
    public CodeValue addChild(CodeValue codeValue) {

        for (CodeValue codeValueExist : this.codeValues) {

            if (this.getType().equals(codeValue.getDecode().getType())  &&
                    codeValueExist.getCodeAttribute().getName().equals(codeValue.getCodeAttribute().getName())) {

                return codeValueExist;
            }
        }

        this.codeValues.add(codeValue);
        codeValue.setDecode(this);
        return codeValue;
    }

    /**
     * Method isDirty.
     *
     * @return boolean
     */
    @Transient
    public boolean isDirty() {

        for (CodeValue item : this.getCodeValues()) {

            if (item.isDirty()) {
                return true;
            }
        }
        return super.isDirty();
    }
}
