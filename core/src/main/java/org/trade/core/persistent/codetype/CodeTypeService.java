package org.trade.core.persistent.codetype;

import java.util.List;

/**
 * @author Simon Allen
 * @version $Revision: 1.0 $
 */
public interface CodeTypeService {

    /**
     * Method findByName.
     *
     * @param name String
     * @return CodeType
     */
    CodeType findByName(String name);

    /**
     * Method validateAndGet.
     *
     * @param name String
     * @return CodeType
     */
    CodeType validateAndGet(String name);

    /**
     * Method findByNameAndType.
     *
     * @param name String
     * @param type String
     * @return CodeType
     */
    CodeType findByNameAndType(String name, String type);

    /**
     * Method findByNameAndType.
     *
     * @param name     String
     * @param type     String
     * @param category String
     * @return CodeType
     */
    CodeType findByNameAndTypeAndCategory(String name, String type, String category);

    /**
     * Method findByAttributeName.
     *
     * @param codeTypeName      String
     * @param codeAttributeName String
     * @return List<CodeValue>
     */
    List<CodeValue> findByAttributeName(String codeTypeName, String codeAttributeName);

    /**
     * Method save.
     *
     * @param codeType CodeType
     * @return CodeType
     */
    CodeType save(CodeType codeType);

    /**
     * Method delete.
     *
     * @param codeType CodeType
     */
    void delete(CodeType codeType);

}
