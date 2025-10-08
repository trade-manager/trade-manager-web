package org.trade.core.persistent.codetype;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Simon Allen
 * @version $Revision: 1.0 $
 */

public record CodeAttributeRecord(Long id, String name, String description, String defaultValue, String className,
                                  String classEditorName, CodeTypeRecord codeType, List<CodeValueRecord> codeValues) {

    /**
     * Method from codeValue roles are LAZY loaded., hence we do not get the children.
     *
     * @param codeAttribute CodeAttributeRecord
     * @return CodeAttributeRecord
     */
    public static CodeAttributeRecord from(final CodeAttribute codeAttribute) {

        List<CodeValueRecord> codeValueRecords = new ArrayList<>();

        if (null != codeAttribute.getCodeValues() && !codeAttribute.getCodeValues().isEmpty()) {

            for (CodeValue codeValue : codeAttribute.getCodeValues()) {

                codeValueRecords.add(CodeValueRecord.from(codeValue));
            }
        }

        return new CodeAttributeRecord(
                codeAttribute.getId(),
                codeAttribute.getName(),
                codeAttribute.getDescription(),
                codeAttribute.getDefaultValue(),
                codeAttribute.getClassName(),
                codeAttribute.getEditorClassName(),
                CodeTypeRecord.from(codeAttribute.getCodeType()),
                List.copyOf(codeValueRecords)

        );
    }
}
