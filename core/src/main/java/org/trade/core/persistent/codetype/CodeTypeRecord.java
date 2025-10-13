package org.trade.core.persistent.codetype;

import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Simon Allen
 * @version $Revision: 1.0 $
 */

public record CodeTypeRecord(Long id,
                             ZonedDateTime createdDate,
                             ZonedDateTime updatedDate,
                             Integer version,
                             Long domainId,
                             String name,
                             String type,
                             String description,
                             List<CodeAttributeRecord> codeAttributes) {


    /**
     * Method from note roles are LAZY loaded., hence we do not get the children.
     *
     * @param codeType       CodeType
     * @param withAttributes Boolean
     * @return CodeTypeRecord
     */
    public static CodeTypeRecord from(final CodeType codeType, Boolean withAttributes) {


        List<CodeAttributeRecord> codeAttributeRecords = new ArrayList<>();


        if (withAttributes && null != codeType.getCodeAttributes() && !codeType.getCodeAttributes().isEmpty()) {

            for (CodeAttribute codeAttribute : codeType.getCodeAttributes()) {

                codeAttributeRecords.add(CodeAttributeRecord.from(codeAttribute, false));
            }
        }

        return new CodeTypeRecord(
                codeType.getId(),
                codeType.getCreatedDate(),
                codeType.getUpdatedDate(),
                codeType.getVersion(),
                codeType.getDomainId(),
                codeType.getName(),
                codeType.getType(),
                codeType.getDescription(),
                List.copyOf(codeAttributeRecords)
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
     * Method getType.
     *
     * @return String
     */
    public String getType() {
        return this.type;
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
     * Method getCodeAttributes.
     *
     * @return List<CodeAttributeRecord>
     */
    public List<CodeAttributeRecord> getCodeAttributes() {
        return this.codeAttributes;
    }

}
