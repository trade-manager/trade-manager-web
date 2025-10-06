package org.trade.core.persistent.codetype;

import java.util.List;

/**
 * @author Simon Allen
 * @version $Revision: 1.0 $
 */
public interface CodeTypeService {

    /**
     * Method findCodeTypeByName.
     *
     * @param name String
     * @return CodeType
     */
    CodeType findCodeTypeByName(String name);

    /**
     * Method findCodeTypeByNameAndType.
     *
     * @param name String
     * @param type String
     * @return CodeType
     */
    CodeType findCodeTypeByNameAndType(String name, String type);

    /**
     * @param codeTypeName      String
     * @param codeAttributeName String
     * @return List<CodeValue>
     */
    List<CodeValue> findByAttributeName(String codeTypeName, String codeAttributeName);

    /**
     * Method saveCodeType.
     *
     * @param codeType CodeType
     * @return CodeType
     */
    CodeType saveCodeType(CodeType codeType);

    /**
     * Method deleteCodeType.
     *
     * @param codeType CodeType
     */
    void deleteCodeType(CodeType codeType);

}
