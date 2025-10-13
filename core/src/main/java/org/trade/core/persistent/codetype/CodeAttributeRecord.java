package org.trade.core.persistent.codetype;

import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Simon Allen
 * @version $Revision: 1.0 $
 */

public record CodeAttributeRecord(Long id,
                                  ZonedDateTime createdDate,
                                  ZonedDateTime updatedDate,
                                  Integer version,
                                  Long domainId,
                                  String name,
                                  String description,
                                  String defaultValue,
                                  String className,
                                  String classEditorName,
                                  CodeTypeRecord codeType,
                                  List<CodeValueRecord> codeValues) {

    /**
     * Method from codeValue roles are LAZY loaded., hence we do not get the children.
     *
     * @param codeAttribute CodeAttributeRecord
     * @param withValues    Boolean
     * @return CodeAttributeRecord
     */
    public static CodeAttributeRecord from(final CodeAttribute codeAttribute, Boolean withValues) {

        List<CodeValueRecord> codeValueRecords = new ArrayList<>();

        if (withValues && null != codeAttribute.getCodeValues() && !codeAttribute.getCodeValues().isEmpty()) {

            for (CodeValue codeValue : codeAttribute.getCodeValues()) {

                codeValueRecords.add(CodeValueRecord.from(codeValue, false, false));
            }
        }

        return new CodeAttributeRecord(
                codeAttribute.getId(),
                codeAttribute.getCreatedDate(),
                codeAttribute.getUpdatedDate(),
                codeAttribute.getVersion(),
                codeAttribute.getDomainId(),
                codeAttribute.getName(),
                codeAttribute.getDescription(),
                codeAttribute.getDefaultValue(),
                codeAttribute.getClassName(),
                codeAttribute.getEditorClassName(),
                CodeTypeRecord.from(codeAttribute.getCodeType(), false),
                List.copyOf(codeValueRecords)

        );
    }

    public Long getId() {
        return id;
    }

    /**
     * Method getCreatedDate.
     *
     * @return ZonedDateTime
     */
    public ZonedDateTime getCreatedDate() {
        return this.createdDate;
    }

    /**
     * Method getUpdatedDate.
     *
     * @return ZonedDateTime
     */
    public ZonedDateTime getUpdatedDate() {
        return this.updatedDate;
    }

    /**
     * Method getVersion.
     *
     * @return Integer
     */
    public Integer getVersion() {
        return version;
    }

    /**
     * Method getDomainId
     *
     * @return Long
     */
    public Long getDomainId() {

        return domainId;
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
     * Method getDescription.
     *
     * @return String
     */
    public String getDescription() {
        return this.description;
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
     * Method getClassName.
     *
     * @return String
     */
    public String getClassName() {
        return this.className;
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
     * Method getCodeType.
     *
     * @return CodeTypeRecord
     */

    public CodeTypeRecord getCodeType() {
        return this.codeType;
    }

    /**
     * Method getCodeValues.
     *
     * @return List<CodeValueRecord>
     */
    public List<CodeValueRecord> getCodeValues() {
        return this.codeValues;
    }

}
