package org.trade.core.persistent.codetype;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Simon Allen
 * @version $Revision: 1.0 $
 */

public record CodeTypeRecord(Long id, String name, String type, String description,
                             List<CodeAttributeRecord> codeAttributes) {


    /**
     * Method from note roles are LAZY loaded., hence we do not get the children.
     *
     * @param codeType CodeType
     * @return CodeTypeRecord
     */
    public static CodeTypeRecord from(final CodeType codeType) {


        List<CodeAttributeRecord> codeAttributeRecords = new ArrayList<>();

        if (null != codeType.getCodeAttributes() && !codeType.getCodeAttributes().isEmpty()) {

            for (CodeAttribute codeAttribute : codeType.getCodeAttributes()) {

                codeAttributeRecords.add(CodeAttributeRecord.from(codeAttribute));
            }
        }

        return new CodeTypeRecord(
                codeType.getId(),
                codeType.getName(),
                codeType.getType(),
                codeType.getDescription(),
                List.copyOf(codeAttributeRecords)
        );
    }
}
